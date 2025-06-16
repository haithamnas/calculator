package com.taboola.calculator;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TokenizerTest {

    @Test
    void testTokenizeSingleNumber() {
        List<Token> tokens = Tokenizer.tokenize("42");
        assertEquals(1, tokens.size());
        assertEquals(TokenType.NUMBER, tokens.get(0).getType());
        assertEquals("42", tokens.get(0).getValue());
    }

    @Test
    void testTokenizeVariable() {
        List<Token> tokens = Tokenizer.tokenize("myVar");
        assertEquals(1, tokens.size());
        assertEquals(TokenType.VARIABLE, tokens.get(0).getType());
        assertEquals("myVar", tokens.get(0).getValue());
    }

    @Test
    void testTokenizeSimpleExpression() {
        List<Token> tokens = Tokenizer.tokenize("x + 5");
        assertEquals(3, tokens.size());
        assertEquals(TokenType.VARIABLE, tokens.get(0).getType());
        assertEquals(TokenType.OPERATOR, tokens.get(1).getType());
        assertEquals(TokenType.NUMBER, tokens.get(2).getType());
    }

    @Test
    void testTokenizeParentheses() {
        List<Token> tokens = Tokenizer.tokenize("(x + 2)");
        assertEquals(5, tokens.size());
        assertEquals(TokenType.PARENTHESIS, tokens.get(0).getType());
        assertEquals("(", tokens.get(0).getValue());
        assertEquals(TokenType.PARENTHESIS, tokens.get(4).getType());
        assertEquals(")", tokens.get(4).getValue());
    }

    @Test
    void testTokenizeUnaryOperators1() {
        List<Token> tokens = Tokenizer.tokenize("i++");
        assertEquals(2, tokens.size());
        assertEquals("i", tokens.get(0).getValue());
        assertEquals("++", tokens.get(1).getValue());
        assertEquals("i", tokens.get(1).getAssociatedVariable());
        assertEquals(TokenType.OPERATOR, tokens.get(1).getType());

    }

    @Test
    void testTokenizeUnaryOperators() {
        List<Token> tokens = Tokenizer.tokenize("++i --j");
        assertEquals(2, tokens.size());
        assertEquals("++", tokens.get(0).getValue());
        assertEquals(TokenType.OPERATOR, tokens.get(0).getType());
        assertEquals("i", tokens.get(0).getAssociatedVariable());
        assertEquals("j", tokens.get(1).getAssociatedVariable());
    }

    @Test
    void testTokenizeMixedExpression() {
        List<Token> tokens = Tokenizer.tokenize("(a + b) * (c - 3)");
        String[] expected = {"(", "a", "+", "b", ")", "*", "(", "c", "-", "3", ")"};
        assertEquals(expected.length, tokens.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], tokens.get(i).getValue());
        }
    }

    @Test
    void testInvalidToken() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> Tokenizer.tokenize("x # y"));
        assertTrue(exception.getMessage().contains("Unknown token"));
    }
}

