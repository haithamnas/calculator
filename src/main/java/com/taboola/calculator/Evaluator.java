package com.taboola.calculator;

import java.util.Deque;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Objects;

public class Evaluator {
    private final VariableStore variableStore;

    public Evaluator(VariableStore variableStore) {
        this.variableStore = variableStore;
    }

    public int evaluate(List<Token> postfixTokens) {
        Deque<Integer> stack = new ArrayDeque<>();

        for (Token token : postfixTokens) {
            switch (token.getType()) {
                case NUMBER -> stack.push(Integer.parseInt(token.getValue()));
                case VARIABLE -> stack.push(variableStore.get(token.getValue()));
                case OPERATOR -> {
                    Operator op = Objects.requireNonNull(Operator.fromSymbol(token.getValue()));
                    if (op.isUnary()) {
                        String varName = token.getAssociatedVariable();
                        int oldVal = variableStore.get(varName);

                        if (token.isPrefix()) {
                            // ++i or --i: increment first, then push new value
                            int newVal = op.applyUnary(oldVal); // only 'a' param used for unary
                            variableStore.set(varName, newVal);
                            stack.push(newVal);
                        } else {
                            // i++ or i--: push old value, then increment variable
                            stack.push(oldVal);
                            int newVal = op.applyUnary(oldVal);
                            variableStore.set(varName, newVal);
                        }

                    } else {
                        int b = stack.pop();
                        int a = stack.pop();
                        int result = op.apply(a, b);
                        stack.push(result);
                    }
                }
            }
        }

        if (stack.size() != 1) {
            throw new IllegalStateException("Invalid expression");
        }
        return stack.pop();
    }
}