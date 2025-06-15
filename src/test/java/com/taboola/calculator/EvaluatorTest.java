package com.taboola.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.taboola.calculator.Token.Type;
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
                new Token(Type.NUMBER, "3"),
                new Token(Type.NUMBER, "5"),
                new Token(Type.OPERATOR, "+")
        );
        int result = evaluator.evaluate(postfix);
        assertEquals(8, result);
    }

    @Test
    public void testVariableAddition() {
        variableStore.set("x", 10);
        variableStore.set("y", 20);
        List<Token> postfix = List.of(
                new Token(Type.VARIABLE, "x"),
                new Token(Type.VARIABLE, "y"),
                new Token(Type.OPERATOR, "+")
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
                new Token(Type.VARIABLE, "a"),
                new Token(Type.VARIABLE, "b"),
                new Token(Type.OPERATOR, "*"),
                new Token(Type.NUMBER, "2"),
                new Token(Type.OPERATOR, "+")
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

