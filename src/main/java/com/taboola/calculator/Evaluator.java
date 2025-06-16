package com.taboola.calculator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Objects;

public class Evaluator {
    private static final Logger logger = LoggerFactory.getLogger(Evaluator.class);
    private final VariableStore variableStore;

    public Evaluator(VariableStore variableStore) {
        this.variableStore = variableStore;
    }

    public int evaluate(List<Token> postfixTokens) {
        Deque<Integer> evaluationStack = new ArrayDeque<>();
        logger.info("Starting evaluation");
        for (Token token : postfixTokens) {
            switch (token.getType()) {
                case NUMBER -> evaluationStack.push(Integer.parseInt(token.getValue()));
                case VARIABLE -> evaluationStack.push(variableStore.get(token.getValue()));
                case OPERATOR -> {
                    Operator op = Objects.requireNonNull(Operator.fromSymbol(token.getValue()));
                    if (op.isUnary()) {
                        String varName = token.getAssociatedVariable();
                        int oldVal = variableStore.get(varName);
                        int newVal;
                        if (token.isPrefix()) {
                            // ++i or --i: increment first, then push new value
                            newVal = op.applyUnary(oldVal); // only 'a' param used for unary
                            variableStore.set(varName, newVal);
                            evaluationStack.push(newVal);
                        } else {
                            // i++ or i--: push old value, then increment variable
                            evaluationStack.push(oldVal);
                            newVal = op.applyUnary(oldVal);
                            variableStore.set(varName, newVal);
                        }
                        logger.debug("Unary {} on '{}': {} -> {}", op.getSymbol(), varName, oldVal, newVal);

                    } else {
                        int b = evaluationStack.pop();
                        int a = evaluationStack.pop();
                        int result = op.apply(a, b);
                        evaluationStack.push(result);
                        logger.debug("Applied {} to {} and {}, result: {}", op.getSymbol(), a, b, result);
                    }
                }
            }
        }

        if (evaluationStack .size() != 1) {
            throw new IllegalStateException("Invalid expression");
        }
        int finalResult = evaluationStack.pop();
        logger.info("Evaluation completed. Result: {}", finalResult);
        return finalResult;
    }
}