package com.taboola.calculator;

import java.util.*;
import java.util.regex.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tokenizer {

    private static final Logger logger = LoggerFactory.getLogger(Tokenizer.class);

    public static final String VARIABLE_NAME_PATTERN = "[a-zA-Z]\\w*";
    public static final String NUMBER = "\\d+";
    public static final String UNARY_OPS = "\\+\\+|--";
    public static final String BINARY_OPS = "[+\\-*/]";
    public static final String PARENTHESIS = "[()]";
    public static final String WHITESPACE_PATTERN = "\\s+";
    public static final String COMMA = ",";

    private static final Pattern TOKEN_PATTERN = Pattern.compile(
        String.format("(%s|%s|%s|%s|%s|%s)",
            Pattern.quote(COMMA),
            VARIABLE_NAME_PATTERN,
            NUMBER,
            UNARY_OPS,
            BINARY_OPS,
            PARENTHESIS
        )
    );

    public static List<Token> tokenize(String expr) {
        logger.debug("Tokenizing expression: '{}'", expr);

        expr = expr.replaceAll(WHITESPACE_PATTERN, COMMA);
        List<Token> tokens = new ArrayList<>();
        Matcher matcher = TOKEN_PATTERN.matcher(expr);
        int lastIndex = 0;
        List<String> rawTokens = new ArrayList<>();

        while (matcher.find()) {
            if (matcher.start() != lastIndex) {
                String unknown = expr.substring(lastIndex, matcher.start());
                logger.error("Unknown token: '{}'", unknown);
                throw new IllegalArgumentException("Unknown token: " + unknown);
            }

            String token = matcher.group();
            if (COMMA.equals(token)) {
                lastIndex = matcher.end();
                continue; // skip commas
            }

            rawTokens.add(token);
            lastIndex = matcher.end();
        }

        if (lastIndex != expr.length()) {
            String unknown = expr.substring(lastIndex);
            logger.error("Unknown trailing token: '{}'", unknown);
            throw new IllegalArgumentException("Unknown token: " + unknown);
        }

        for (int i = 0; i < rawTokens.size(); i++) {
            String rawToken = rawTokens.get(i);

            // Handle prefix unary (++x or --x)
            if (rawToken.matches(UNARY_OPS) &&
                i + 1 < rawTokens.size() &&
                rawTokens.get(i + 1).matches(VARIABLE_NAME_PATTERN)) {

                String var = rawTokens.get(i + 1);
                tokens.add(new Token(rawToken, true, var));
                i++; // Skip variable (already used)
            }

            // Handle postfix unary (x++ or x--)
            else if (rawToken.matches(VARIABLE_NAME_PATTERN) &&
                     i + 1 < rawTokens.size() &&
                     rawTokens.get(i + 1).matches(UNARY_OPS)) {

                tokens.add(new Token(TokenType.VARIABLE, rawToken));
                tokens.add(new Token(rawTokens.get(i + 1), false, rawToken));
                i++; // Skip postfix operator
            }

            // Normal token
            else {
                TokenType tokenType = determineType(rawToken);
                tokens.add(new Token(tokenType, rawToken));
            }
        }

        logger.debug("Tokenization complete. Tokens: {}", tokens);
        return tokens;
    }

    private static TokenType determineType(String token) {
        if (token.matches(NUMBER)) return TokenType.NUMBER;
        if (token.matches(VARIABLE_NAME_PATTERN)) return TokenType.VARIABLE;
        if (Operator.isOperator(token)) return TokenType.OPERATOR;
        if (token.matches(PARENTHESIS)) return TokenType.PARENTHESIS;
        logger.error("Unable to classify token: '{}'", token);
        throw new IllegalArgumentException("Unknown token: " + token);
    }
}
