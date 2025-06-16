package com.taboola.calculator;

import org.junit.jupiter.api.Test;

import static com.github.stefanbirkner.systemlambda.SystemLambda.*;
import static org.junit.jupiter.api.Assertions.*;

class MainE2EStubsTest {

    @Test
    void runsAndPrintsVariables() throws Exception {
        String output = tapSystemOut(() ->
                withTextFromSystemIn("a = 1  ", "")
                        .execute(() -> Main.main(new String[0]))
        );

        assertTrue(output.contains("Enter expressions or assignments"));
        assertTrue(output.contains("a=1"));
    }

    @Test
    void printsExpressionResultAndEmptyStore() throws Exception {
        String output = tapSystemOut(() ->
                withTextFromSystemIn("3 * 4", "")
                        .execute(() -> Main.main(new String[0]))
        );
        assertTrue(output.contains("12"));
        assertTrue(output.contains("{}"));
    }

    @Test
    void multipleAssignmentsAndUpdates() throws Exception {
        String output = tapSystemOut(() ->
                withTextFromSystemIn("i = 0", "j = ++i", "x = i++ + 5", "y = 5 + 3 * 10", "i += y")
                        .execute(() -> Main.main(new String[0]))
        );

        assertTrue(output.contains("j=1"), "Should contain initial assignment");
        assertTrue(output.contains("i=37"), "Should contain second assignment");
        assertTrue(output.contains("x=6"), "Should contain updated value");
        assertTrue(output.contains("y=35"), "Should contain updated value");
    }

    @Test
    void multipleAssignmentsAndUpdates2() throws Exception {
        String output = tapSystemOut(() ->
                withTextFromSystemIn("i = 0", "j = ++i", "x = i++ + ++j", "y = 5 + 3 * 10", "i += y")
                        .execute(() -> Main.main(new String[0]))
        );

        assertTrue(output.contains("j=2"), "Should contain initial assignment");
        assertTrue(output.contains("i=37"), "Should contain second assignment");
        assertTrue(output.contains("x=3"), "Should contain updated value");
        assertTrue(output.contains("y=35"), "Should contain updated value");
    }

    @Test
    void multipleAssignmentsAndUpdates3() throws Exception {
        String output = tapSystemOut(() ->
                withTextFromSystemIn("x = 2", "y = 5", "x += y", "")
                        .execute(() -> Main.main(new String[0]))
        );

        assertTrue(output.contains("x=2"), "Should contain initial assignment");
        assertTrue(output.contains("y=5"), "Should contain second assignment");
        assertTrue(output.contains("x=7"), "Should contain updated value");
    }

    @Test
    void invalidTokenOutputsError() throws Exception {
        String logs = tapSystemOutNormalized(() ->
                withTextFromSystemIn("x # 1", "")
                        .execute(() -> Main.main(new String[0]))
        );

        assertTrue(logs.contains("ERROR"),                            "Should include log level ERROR");
        assertTrue(logs.contains("Error processing 'x # 1':"),        "Should log the input that failed");
        assertTrue(logs.contains("Unknown token"),                    "Should mention 'Unknown token'");

    }

    @Test
    void invalidTokenOutputsError2() throws Exception {
        String output = tapSystemOutNormalized(() ->
                withTextFromSystemIn("x x = 1", "")
                        .execute(() -> Main.main(new String[0]))
        );
        assertTrue(output.contains("Invalid variable name: x x"), "Should contain initial assignment");
    }

    @Test
    void handlesParenthesesExpression() throws Exception {
        String output = tapSystemOutNormalized(() ->
                withTextFromSystemIn("(1 + 2) * 3", "")
                        .execute(() -> Main.main(new String[0]))
        );

        assertTrue(output.contains("9"), "Expected (1+2)*3 to evaluate to 9");
        assertTrue(output.contains("{}"), "No variables should be set");
    }

    @Test
    void mismatchedParenthesesThrowsError() throws Exception {
        String errorOutput = tapSystemOutNormalized(() ->
                withTextFromSystemIn("(1 + 2", "")
                        .execute(() -> Main.main(new String[0]))
        );

        assertTrue(errorOutput.contains("Mismatched parentheses"));
    }

    @Test
    void expressionWithPrefixIncrement() throws Exception {
        String output = tapSystemOut(() ->
                withTextFromSystemIn("a = 1", "b = ++a", "")
                        .execute(() -> Main.main(new String[0]))
        );

        assertTrue(output.contains("a=2"));
        assertTrue(output.contains("b=2"));
    }

    @Test
    void expressionWithPostfixDecrement() throws Exception {
        String output = tapSystemOut(() ->
                withTextFromSystemIn("x = 5", "y = x-- + 1", "")
                        .execute(() -> Main.main(new String[0]))
        );

        assertTrue(output.contains("x=4"));
        assertTrue(output.contains("y=6"));
    }

    @Test
    void expressionWithMixedUnaryAndBinaryOperators() throws Exception {
        String output = tapSystemOut(() ->
                withTextFromSystemIn("x = 1", "y = 2", "z = ++x * y-- + 5", "")
                        .execute(() -> Main.main(new String[0]))
        );

        assertTrue(output.contains("x=2"));
        assertTrue(output.contains("y=1"));
        assertTrue(output.contains("z=9"));
    }

    @Test
    void longExpressionWithParenthesesAndUnaryOperators() throws Exception {
        String output = tapSystemOut(() ->
                withTextFromSystemIn("a = 1", "b = 2", "c = 3", "d = ++a + (b-- * c) - 4", "")
                        .execute(() -> Main.main(new String[0]))
        );

        assertTrue(output.contains("a=2"));
        assertTrue(output.contains("b=1"));
        assertTrue(output.contains("c=3"));
        assertTrue(output.contains("d=4"));
    }

    @Test
    void postfixDecrementInMultiplication() throws Exception {
        String output = tapSystemOut(() ->
                withTextFromSystemIn("a = 5", "c = 3", "result = a * c--", "")
                        .execute(() -> Main.main(new String[0]))
        );
        assertTrue(output.contains("result=15"), "Expected 5 * 3");
        assertTrue(output.contains("c=2"), "Expected c to be decremented after use");
    }

    @Test
    void prefixAndPostfixInComplexExpression() throws Exception {
        String output = tapSystemOut(() ->
                withTextFromSystemIn("x = 2", "y = 4", "z = 10", "result = ++x * y-- + z", "")
                        .execute(() -> Main.main(new String[0]))
        );
        // ++x -> 3, y-- -> 4, then y becomes 3
        // 3 * 4 + 10 = 22
        assertTrue(output.contains("result=22"), "Expected ++x * y-- + z to be 22");
        assertTrue(output.contains("x=3"), "Expected x to be incremented");
        assertTrue(output.contains("y=3"), "Expected y to be decremented");
    }

    @Test
    void nestedExpressionWithUnaryOperatorsAndParentheses() throws Exception {
        String output = tapSystemOut(() ->
                withTextFromSystemIn("a = 1", "b = 2", "c = 5", "d = 3", "result = (a + ++b) * (c-- - d)", "")
                        .execute(() -> Main.main(new String[0]))
        );
        // ++b -> 3, a + 3 = 4
        // c-- -> 5, then c becomes 4
        // 5 - 3 = 2
        // 4 * 2 = 8
        assertTrue(output.contains("result=8"), "Expected expression to evaluate to 8");
        assertTrue(output.contains("b=3"), "Expected b to be incremented");
        assertTrue(output.contains("c=4"), "Expected c to be decremented");
    }

    @Test
    void expressionUsingZeroShouldSucceed() throws Exception {
        String output = tapSystemOut(() ->
                withTextFromSystemIn("x = 0", "y = 5", "z = x + y", "")
                        .execute(() -> Main.main(new String[0]))
        );

        assertTrue(output.contains("x=0"));
        assertTrue(output.contains("y=5"));
        assertTrue(output.contains("z=5"));
    }

    @Test
    void expressionMultiplyingByZeroShouldSucceed() throws Exception {
        String output = tapSystemOut(() ->
                withTextFromSystemIn("x = 7", "z = x * 0", "")
                        .execute(() -> Main.main(new String[0]))
        );

        assertTrue(output.contains("x=7"));
        assertTrue(output.contains("z=0"));
    }

    @Test
    void divisionByZeroShouldFail() throws Exception {
        String output = tapSystemOut(() ->
                withTextFromSystemIn("x = 0", "z = 5 / x", "")
                        .execute(() -> {
                            try {
                                Main.main(new String[0]);
                            } catch (ArithmeticException e) {
                                System.out.println("ERROR: Division by zero");
                            }
                        })
        );

        assertTrue(output.contains("Error processing 'z = 5 / x': / by zero"));
    }
    }
