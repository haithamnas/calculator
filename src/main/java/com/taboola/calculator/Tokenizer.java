package com.taboola.calculator;

import java.util.*;
import java.util.regex.*;


public class Tokenizer {
    public static final String VARIABLE_NAME_PATTERN = "[a-zA-Z]\\w*";
    public static final String NUMBER = "\\d+";
    public static final String UNARY_OPS = "\\+\\+|--";
    public static final String BINARY_OPS = "[+\\-*/]";
    public static final String PARENTHESIS = "[()]";
    public static final String WHITESPACE_PATTERN  = "\\s+";

    private static final Pattern TOKEN_PATTERN = Pattern.compile(
            String.format("(%s|%s|%s|%s|%s)", VARIABLE_NAME_PATTERN, NUMBER, UNARY_OPS, BINARY_OPS, PARENTHESIS)
    );


    public static List<Token> tokenize(String expr) {
        expr = expr.replaceAll(WHITESPACE_PATTERN , "");
        List<Token> tokens = new ArrayList<>();
        Matcher matcher = TOKEN_PATTERN.matcher(expr);
        int lastIndex = 0;
        List<String> rawTokens = new ArrayList<>();

        while (matcher.find()) {
            if (matcher.start() != lastIndex) {
                // There's an unmatched part in the expression
                throw new IllegalArgumentException("Unknown token: " + expr.substring(lastIndex, matcher.start()));
            }
            rawTokens.add(matcher.group());
            //String match = matcher.group();
            //Token.Type type = determineType(match);

            //tokens.add(new Token(type, match));
            lastIndex = matcher.end() ;
        }

        if (lastIndex != expr.length()) {
            throw new IllegalArgumentException("Unknown token: " + expr.substring(lastIndex));
        }

        for (int i = 0; i < rawTokens.size(); i++) {
            String current = rawTokens.get(i);

            // Handle prefix unary (++x)
            if (current.matches(UNARY_OPS) && i + 1 < rawTokens.size() && rawTokens.get(i + 1).matches(VARIABLE_NAME_PATTERN)) {
                String var = rawTokens.get(i + 1);
                tokens.add(new Token(current, true, var));
                i++; // Skip the next token (already processed)
            }

            // Handle postfix unary (x++)
            else if (current.matches(VARIABLE_NAME_PATTERN) && i + 1 < rawTokens.size() && rawTokens.get(i + 1).matches(UNARY_OPS)) {
                tokens.add(new Token(Token.Type.VARIABLE, current)); // Add variable
                tokens.add(new Token(rawTokens.get(i + 1), false, current)); // Add postfix operator
                i++; // Skip the next token (already processed)
            }

            // Fallback: default behavior
            else {
                Token.Type type = determineType(current);
                tokens.add(new Token(type, current));
            }
        }
        return tokens;
    }

    private static Token.Type determineType(String token) {
        if (token.matches(NUMBER)) return Token.Type.NUMBER;
      //  if (token.matches(UNARY_OPS)) return Token.Type.OPERATOR;
        if (token.matches(VARIABLE_NAME_PATTERN)) return Token.Type.VARIABLE;
        if (Operator.isOperator(token)) return Token.Type.OPERATOR;
        if (token.matches(PARENTHESIS)) return Token.Type.PARENTHESIS;
        throw new IllegalArgumentException("Unknown token: " + token);
    }
}
