package com.taboola.calculator;

import org.junit.jupiter.api.Test;

import java.util.List;

import static com.taboola.calculator.TokenType.*;
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
                new Token(TokenType.VARIABLE, "x"),
                new Token(TokenType.VARIABLE, "y"),
                new Token(OPERATOR, "*"),
                new Token(TokenType.VARIABLE, "z"),
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

    @Test
    void testPrefixIncrement() {
        // ++i  → postfix: i ++
        List<Token> input = List.of(
                new Token("++", true, "i"),
                new Token(TokenType.VARIABLE, "i")
        );

        List<Token> expected = List.of(
                new Token(TokenType.VARIABLE, "i"),
                new Token("++", true, "i")
        );

        List<Token> actual = ExpressionParser.infixToPostfix(input);
        assertEquals(expected, actual);
    }

    @Test
    void testPostfixIncrement() {
        // i++  → postfix: i ++
        List<Token> input = List.of(
                new Token(TokenType.VARIABLE, "i"),
                new Token("++", false, "i")
        );

        List<Token> expected = List.of(
                new Token(OPERATOR,"++",true,false,"i")
        );

        List<Token> actual = ExpressionParser.infixToPostfix(input);
        assertEquals(expected, actual);
    }

    @Test
    void testPostfixDecrement() {
        // x-- → postfix: x --
        List<Token> input = List.of(
                new Token(TokenType.VARIABLE, "x"),
                new Token("--", false, "x")
        );

        List<Token> expected = List.of(
                new Token(OPERATOR,"--",true,false,"i")
        );

        List<Token> actual = ExpressionParser.infixToPostfix(input);
        assertEquals(expected, actual);
    }
    @Test
    void testPrefixDecrement() {
        // --x → postfix: x --
        List<Token> input = List.of(
                new Token("--", true, "x"),
                new Token(TokenType.VARIABLE, "x")
        );

        List<Token> expected = List.of(
                new Token(TokenType.VARIABLE, "x"),
                new Token("--", true, "x")
        );

        List<Token> actual = ExpressionParser.infixToPostfix(input);
        assertEquals(expected, actual);
    }

    @Test
    void testExpressionWithPrefixIncrement() {
        // a + ++b → postfix: a b ++ +
        List<Token> input = List.of(
                new Token(TokenType.VARIABLE, "a"),
                new Token(TokenType.OPERATOR, "+"),
                new Token("++", true, "b"),
                new Token(TokenType.VARIABLE, "b")
        );

        List<Token> expected = List.of(
                new Token(TokenType.VARIABLE, "a"),
                new Token(TokenType.VARIABLE, "b"),
                new Token("++", true, "b"),
                new Token(TokenType.OPERATOR, "+")
        );

        List<Token> actual = ExpressionParser.infixToPostfix(input);
        assertEquals(expected, actual);
    }

    @Test
    void testExpressionWithPostfixDecrement() {
        // a * c-- → postfix: a c -- *
        List<Token> input = List.of(
                new Token(TokenType.VARIABLE, "a"),
                new Token(TokenType.OPERATOR, "*"),
                new Token(TokenType.VARIABLE, "c"),
                new Token(TokenType.OPERATOR,"--",true, false, "c")
        );

        List<Token> expected = List.of(
                new Token(TokenType.VARIABLE, "a"),
                new Token(OPERATOR,"--",true, false, "c"),
                new Token(TokenType.OPERATOR, "*")
        );

        List<Token> actual = ExpressionParser.infixToPostfix(input);
        assertEquals(expected, actual);
    }
}

