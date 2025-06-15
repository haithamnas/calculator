package com.taboola.calculator;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.taboola.calculator.Token.Type.*;
import static org.junit.jupiter.api.Assertions.*;

class ExpressionParserTest {

    @Test
    void testSimpleAddition() {
        List<Token> input = List.of(
                new Token(NUMBER, "2"),
                new Token(OPERATOR, "+"),
                new Token(NUMBER, "3")
        );

        List<Token> output = ExpressionParser.infixToPostfix(input);

        assertEquals(List.of(
                new Token(NUMBER, "2"),
                new Token(NUMBER, "3"),
                new Token(OPERATOR, "+")
        ), output);
    }

    @Test
    void testAdditionAndMultiplicationPrecedence() {
        List<Token> input = List.of(
                new Token(NUMBER, "2"),
                new Token(OPERATOR, "+"),
                new Token(NUMBER, "3"),
                new Token(OPERATOR, "*"),
                new Token(NUMBER, "4")
        );

        List<Token> output = ExpressionParser.infixToPostfix(input);

        assertEquals(List.of(
                new Token(NUMBER, "2"),
                new Token(NUMBER, "3"),
                new Token(NUMBER, "4"),
                new Token(OPERATOR, "*"),
                new Token(OPERATOR, "+")
        ), output);
    }

    @Test
    void testWithParentheses() {
        List<Token> input = List.of(
                new Token(PARENTHESIS, "("),
                new Token(NUMBER, "2"),
                new Token(OPERATOR, "+"),
                new Token(NUMBER, "3"),
                new Token(PARENTHESIS, ")"),
                new Token(OPERATOR, "*"),
                new Token(NUMBER, "4")
        );

        List<Token> output = ExpressionParser.infixToPostfix(input);

        assertEquals(List.of(
                new Token(NUMBER, "2"),
                new Token(NUMBER, "3"),
                new Token(OPERATOR, "+"),
                new Token(NUMBER, "4"),
                new Token(OPERATOR, "*")
        ), output);
    }

    @Test
    void testVariablesAndOperators() {
        List<Token> input = List.of(
                new Token(VARIABLE, "x"),
                new Token(OPERATOR, "*"),
                new Token(VARIABLE, "y"),
                new Token(OPERATOR, "+"),
                new Token(VARIABLE, "z")
        );

        List<Token> output = ExpressionParser.infixToPostfix(input);

        assertEquals(List.of(
                new Token(Token.Type.VARIABLE, "x"),
                new Token(Token.Type.VARIABLE, "y"),
                new Token(OPERATOR, "*"),
                new Token(Token.Type.VARIABLE, "z"),
                new Token(OPERATOR, "+")
        ), output);
    }

    @Test
    void testNestedParentheses() {
        List<Token> input = List.of(
                new Token(PARENTHESIS, "("),
                new Token(NUMBER, "1"),
                new Token(OPERATOR, "+"),
                new Token(PARENTHESIS, "("),
                new Token(NUMBER, "2"),
                new Token(OPERATOR, "*"),
                new Token(NUMBER, "3"),
                new Token(PARENTHESIS, ")"),
                new Token(PARENTHESIS, ")")
        );

        List<Token> output = ExpressionParser.infixToPostfix(input);

        assertEquals(List.of(
                new Token(NUMBER, "1"),
                new Token(NUMBER, "2"),
                new Token(NUMBER, "3"),
                new Token(OPERATOR, "*"),
                new Token(OPERATOR, "+")
        ), output);
    }

    @Test
    void testMismatchedParenthesesThrowsException() {
        List<Token> input = List.of(
                new Token(NUMBER, "1"),
                new Token(OPERATOR, "+"),
                new Token(PARENTHESIS, "("),
                new Token(NUMBER, "2")
                // חסר סוגר סוגריים
        );

        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.infixToPostfix(input));
    }

    @Test
    void testExtraClosingParenthesisThrowsException() {
        List<Token> input = List.of(
                new Token(NUMBER, "1"),
                new Token(OPERATOR, "+"),
                new Token(NUMBER, "2"),
                new Token(PARENTHESIS, ")") // סוגריים מיותרות
        );

        assertThrows(IllegalArgumentException.class, () -> ExpressionParser.infixToPostfix(input));
    }
}

