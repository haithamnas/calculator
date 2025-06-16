package com.taboola.calculator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import static com.taboola.calculator.Tokenizer.VARIABLE_NAME_PATTERN;

public class LineProcessor {
    private final VariableStore variableStore;
    private final Evaluator evaluator;
    private static final Logger log = LoggerFactory.getLogger(LineProcessor.class);
    private record ParsedLine(String variable, AssignmentOperator operator, String expression) {}

    public LineProcessor(VariableStore variableStore) {
        this.variableStore = variableStore;
        this.evaluator = new Evaluator(variableStore);
    }

    public void processLine(String input) {
        input = input.trim();

        ParsedLine parsedLine = parseLine(input);
        if (parsedLine == null) {
            int result = evaluateExpression(input);
            System.out.println(result);
            return;
        }

        validateVariableName(parsedLine.variable());

        int expressionValue = evaluateExpression(parsedLine.expression());
        int currentVariableValue = variableStore.getOrDefault(parsedLine.variable(), 0);

        int newVariableValue = parsedLine.operator().apply(currentVariableValue, expressionValue);
        variableStore.set(parsedLine.variable(), newVariableValue);
    }


    private ParsedLine parseLine(String input) {
        return Arrays.stream(AssignmentOperator.values())
                .sorted((a, b) -> Integer.compare(b.getSymbol().length(), a.getSymbol().length())) // longest first("+=","=+" and then =)
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
            throw new IllegalArgumentException("Invalid variable name: " + variable);
        }
    }

    private int evaluateExpression(String expr) {
        List<Token> tokens = Tokenizer.tokenize(expr);
        List<Token> postfix = ExpressionParser.infixToPostfix(tokens);
        return evaluator.evaluate(postfix);
    }
}
