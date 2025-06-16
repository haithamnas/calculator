package com.taboola.calculator;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExpressionParser {

    private static final Logger logger = LoggerFactory.getLogger(ExpressionParser.class);

    public static final String LEFT_PAREN = "(";
    public static final String RIGHT_PAREN = ")";

    public static List<Token> infixToPostfix(List<Token> tokens) {
        List<Token> postfixOutput = new ArrayList<>();
        Deque<Token> operatorStack = new ArrayDeque<>();

        logger.debug("Starting infix to postfix conversion");

        for (int i = 0; i < tokens.size(); i++) {
            Token currentToken = tokens.get(i);

            switch (currentToken.getType()) {
                case NUMBER -> {
                    postfixOutput.add(currentToken);
                    logger.debug("Added number to output: {}", currentToken.getValue());
                }

                case VARIABLE -> {
                    // Only add variable if it's not already handled as part of a postfix unary op
                    boolean isPostfixUnary = (
                        i + 1 < tokens.size() &&
                        tokens.get(i + 1).getType() == TokenType.OPERATOR &&
                        tokens.get(i + 1).isUnary() &&
                        !tokens.get(i + 1).isPrefix() &&
                        tokens.get(i + 1).getAssociatedVariable().equals(currentToken.getValue())
                    );

                    if (!isPostfixUnary) {
                        postfixOutput.add(currentToken);
                        logger.debug("Added variable to output: {}", currentToken.getValue());
                    }
                }

                case OPERATOR -> {
                    Operator currentOperator = Operator.fromSymbol(currentToken.getValue());
                    while (!operatorStack.isEmpty() && operatorStack.peek().getType() == TokenType.OPERATOR) {
                        Operator topOperator = Operator.fromSymbol(operatorStack.peek().getValue());
                        if (topOperator.getPrecedence() >= currentOperator.getPrecedence()) {
                            Token popped = operatorStack.pop();
                            postfixOutput.add(popped);
                            logger.debug("Popped operator from stack to output: {}", popped.getValue());
                        } else {
                            break;
                        }
                    }
                    operatorStack.push(currentToken);
                    logger.debug("Pushed operator to stack: {}", currentToken.getValue());
                }

                case PARENTHESIS -> {
                    if (LEFT_PAREN.equals(currentToken.getValue())) {
                        operatorStack.push(currentToken);
                        logger.debug("Pushed left parenthesis to stack");
                    } else if (RIGHT_PAREN.equals(currentToken.getValue())) {
                        while (!operatorStack.isEmpty() && !operatorStack.peek().getValue().equals(LEFT_PAREN)) {
                            Token popped = operatorStack.pop();
                            postfixOutput.add(popped);
                            logger.debug("Popped from stack to output inside parentheses: {}", popped.getValue());
                        }

                        if (!operatorStack.isEmpty() && operatorStack.peek().getValue().equals(LEFT_PAREN)) {
                            operatorStack.pop(); // Remove LEFT_PAREN
                            logger.debug("Popped left parenthesis from stack");
                        } else {
                            logger.error("Mismatched parentheses");
                            throw new IllegalArgumentException("Mismatched parentheses");
                        }
                    }
                }

                default -> {
                    logger.error("Unknown token type: {}", currentToken);
                    throw new IllegalArgumentException("Unknown token type: " + currentToken);
                }
            }
        }

        while (!operatorStack.isEmpty()) {
            Token remaining = operatorStack.pop();
            if (LEFT_PAREN.equals(remaining.getValue()) || RIGHT_PAREN.equals(remaining.getValue())) {
                logger.error("Mismatched parentheses in remaining stack");
                throw new IllegalArgumentException("Mismatched parentheses");
            }
            postfixOutput.add(remaining);
            logger.debug("Moved remaining operator to output: {}", remaining.getValue());
        }

        logger.info("Infix to postfix conversion completed. Output size: {}", postfixOutput.size());
        return postfixOutput;
    }
}
