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
                withTextFromSystemIn("i = 0", "j = ++i", "x = i++ + 5","y = 5 + 3 * 10","i += y")
                //withTextFromSystemIn("i = 0", "j = ++i","")
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
                withTextFromSystemIn("i = 0", "j = ++i", "x = i++ + ++j","y = 5 + 3 * 10","i += y")
                        //withTextFromSystemIn("i = 0", "j = ++i","")
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
                withTextFromSystemIn("(1 + 2) * 3", "")      // feed two lines: the expression and then blank to quit
                        .execute(() -> Main.main(new String[0]))
        );

        assertTrue(output.contains("9"), "Expected (1+2)*3 to evaluate to 9");
        assertTrue(output.contains("{}"), "No variables should be set");
    }

    @Test
    void mismatchedParenthesesThrowsError() throws Exception {
        String errorOutput = tapSystemOutNormalized(() ->
                withTextFromSystemIn("(1 + 2", "")           // unmatched parenthesis, then blank line
                        .execute(() -> Main.main(new String[0]))
        );

        assertTrue(errorOutput.contains("Mismatched parentheses"));
    }
}
