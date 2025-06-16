package com.taboola.calculator;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.taboola.calculator.Tokenizer.VARIABLE_NAME_PATTERN;

public class LineProcessor {
    private static final Logger logger = LoggerFactory.getLogger(LineProcessor.class);

    private final VariableStore variableStore;
    private final Evaluator evaluator;

    private record ParsedLine(String variable, AssignmentOperator operator, String expression) {}

    public LineProcessor(VariableStore variableStore) {
        this.variableStore = variableStore;
        this.evaluator = new Evaluator(variableStore);
    }

    public void processLine(String input) {
        input = input.trim();
        logger.debug("Processing line: '{}'", input);

        ParsedLine parsedLine = parseLine(input);

        if (parsedLine == null) {
            int result = evaluateExpression(input);
            logger.info("Evaluated expression '{}'. Result: {}", input, result);
            System.out.println(result);  // Optional: replace with consumer if needed
            return;
        }

        validateVariableName(parsedLine.variable());

        int expressionValue = evaluateExpression(parsedLine.expression());
        int currentValue = variableStore.getOrDefault(parsedLine.variable(), 0);
        int newValue = parsedLine.operator().apply(currentValue, expressionValue);

        variableStore.set(parsedLine.variable(), newValue);
        logger.info("Assigned variable '{}': {} {} {} = {}",
                    parsedLine.variable(), currentValue,
                    parsedLine.operator().getSymbol(), expressionValue, newValue);
    }

    private ParsedLine parseLine(String input) {
        return Arrays.stream(AssignmentOperator.values())
            .sorted((a, b) -> Integer.compare(b.getSymbol().length(), a.getSymbol().length())) // longest symbols first
            .map(op -> {
                int index = input.indexOf(op.getSymbol());
                if (index > 0) {
                    String variable = input.substring(0, index).trim();
                    String expression = input.substring(index + op.getSymbol().length()).trim();
                    return new ParsedLine(variable, op, expression);
                }
                return null;
            })
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(null);
    }

    private void validateVariableName(String variable) {
        if (!variable.matches(VARIABLE_NAME_PATTERN)) {
            logger.error("Invalid variable name: {}", variable);
            throw new IllegalArgumentException("Invalid variable name: " + variable);
        }
    }

    private int evaluateExpression(String expr) {
        List<Token> tokens = Tokenizer.tokenize(expr);
        List<Token> postfix = ExpressionParser.infixToPostfix(tokens);
        return evaluator.evaluate(postfix);
    }
}
