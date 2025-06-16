package com.taboola.calculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EvaluatorTest {

    private VariableStore variableStore;
    private Evaluator evaluator;

    @BeforeEach
    public void setup() {
        variableStore = new VariableStore();
        evaluator = new Evaluator(variableStore);
    }

    @Test
    public void testSimpleAddition() {
        List<Token> postfix = List.of(
                new Token(TokenType.NUMBER, "3"),
                new Token(TokenType.NUMBER, "5"),
                new Token(TokenType.OPERATOR, "+")
        );
        int result = evaluator.evaluate(postfix);
        assertEquals(8, result);
    }

    @Test
    public void testVariableAddition() {
        variableStore.set("x", 10);
        variableStore.set("y", 20);
        List<Token> postfix = List.of(
                new Token(TokenType.VARIABLE, "x"),
                new Token(TokenType.VARIABLE, "y"),
                new Token(TokenType.OPERATOR, "+")
        );
        int result = evaluator.evaluate(postfix);
        assertEquals(30, result);
    }

    @Test
    public void testPrefixIncrement() {
        variableStore.set("i", 2);
        List<Token> postfix = List.of(
                new Token("++", true, "i")
        );
        int result = evaluator.evaluate(postfix);
        assertEquals(3, result);
        assertEquals(3, variableStore.get("i"));
    }

    @Test
    public void testPostfixIncrement() {
        variableStore.set("i", 2);
        List<Token> postfix = List.of(
                new Token("++", false, "i")
        );
        int result = evaluator.evaluate(postfix);
        assertEquals(2, result);                 // Old value returned
        assertEquals(3, variableStore.get("i")); // Variable updated after
    }

    @Test
    public void testComplexExpression() {
        variableStore.set("a", 4);
        variableStore.set("b", 3);
        List<Token> postfix = List.of(
                new Token(TokenType.VARIABLE, "a"),
                new Token(TokenType.VARIABLE, "b"),
                new Token(TokenType.OPERATOR, "*"),
                new Token(TokenType.NUMBER, "2"),
                new Token(TokenType.OPERATOR, "+")
        );
        int result = evaluator.evaluate(postfix); // (4 * 3) + 2 = 14
        assertEquals(14, result);
    }

    @Test
    public void testPrefixDecrement() {
        variableStore.set("x", 5);
        List<Token> postfix = List.of(
                new Token("--", true, "x")
        );
        int result = evaluator.evaluate(postfix);
        assertEquals(4, result);
        assertEquals(4, variableStore.get("x"));
    }

    @Test
    public void testPostfixDecrement() {
        variableStore.set("x", 5);
        List<Token> postfix = List.of(
                new Token("--", false, "x")
        );
        int result = evaluator.evaluate(postfix);
        assertEquals(5, result);
        assertEquals(4, variableStore.get("x"));
    }
}

