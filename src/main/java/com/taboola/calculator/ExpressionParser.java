package com.taboola.calculator;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
//TODO add test
public class ExpressionParser {

    public static List<Token> infixToPostfix(List<Token> tokens) {
        List<Token> output = new ArrayList<>();
        Deque<Token> ops = new ArrayDeque<>();

        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);
            switch (token.getType()) {
                case NUMBER -> output.add(token);
                case VARIABLE -> {
                    // Only add variable if it's not already handled as part of a postfix unary op
                    boolean isPostfixUnary = (
                            i + 1 < tokens.size() &&
                                    tokens.get(i + 1).getType() == Type.OPERATOR &&
                                    tokens.get(i + 1).isUnary() &&
                                    !tokens.get(i + 1).isPrefix() &&
                                    tokens.get(i + 1).getAssociatedVariable().equals(token.getValue())
                    );

                    if (!isPostfixUnary) {
                        output.add(token);
                    }
                }
                case OPERATOR -> {
                    Operator currOp = Operator.fromSymbol(token.getValue());
                    while (!ops.isEmpty() && ops.peek().getType() == Type.OPERATOR) {
                        Operator topOp = Operator.fromSymbol(ops.peek().getValue());
                        if (topOp.getPrecedence() >= currOp.getPrecedence()) {
                            output.add(ops.pop());
                        } else break;
                    }
                    ops.push(token);
                }
                case PARENTHESIS -> {
                    if ("(".equals(token.getValue())) {
                        ops.push(token);
                    } else if (")".equals(token.getValue())) {
                        while (!ops.isEmpty() && !ops.peek().getValue().equals("(")) {
                            output.add(ops.pop());
                        }
                        if (!ops.isEmpty() && ops.peek().getValue().equals("(")) {
                            ops.pop();
                        } else {
                            throw new IllegalArgumentException("Mismatched parentheses");
                        }
                    }
                }
            }
        }

        while (!ops.isEmpty()) {
            Token op = ops.pop();
            if ("(".equals(op.getValue()) || ")".equals(op.getValue()))
                throw new IllegalArgumentException("Mismatched parentheses");
            output.add(op);
        }

        return output;
    }
}
