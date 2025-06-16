package com.taboola.calculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LineProcessorTest {

    private VariableStore variableStore;
    private LineProcessor processor;

    @BeforeEach
    void setUp() {
        variableStore = new VariableStore();
        processor = new LineProcessor(variableStore);
    }

    @Test
    void testEmptyString() {
        assertThrows(IllegalStateException.class, () ->
                        processor.processLine(""));

    }
    @Test
    void testNull() {
        assertThrows(NullPointerException.class, () ->
                processor.processLine(null));

    }

    @Test
    void testVariableIsString() {
        processor.processLine("xxxx = 5");
        assertEquals(5, variableStore.get("xxxx"));

    }

    @Test
    void testVariableIsString2() {
        processor.processLine("xxxx=5");
        assertEquals(5, variableStore.get("xxxx"));

    }

    @Test
    void testVariableIsString3() {
        processor.processLine("xxxx    =         5");
        assertEquals(5, variableStore.get("xxxx"));

    }

    @Test
    void testVariableIsString4() {
        processor.processLine("xxxx=         5");
        assertEquals(5, variableStore.get("xxxx"));
    }

    @Test
    void testSimpleAssignment() {
        processor.processLine("x = 5");
        assertEquals(5, variableStore.get("x"));
    }

    @Test
    void testPlusAssignment() {
        //variableStore.set("x", 10);
        processor.processLine("x= 6- 3*3");
        assertEquals(-3, variableStore.get("x"));
    }
    @Test
    void testPlusAssignmentWithParenthesis() {
        //variableStore.set("x", 10);
        processor.processLine("x= (6- 3)*3");
        assertEquals(9, variableStore.get("x"));
    }

    @Test
    void testPlusAssignment2() {
        variableStore.set("x", 10);
        processor.processLine("x += 3");
        assertEquals(13, variableStore.get("x"));
    }

    @Test
    void testMinusAssignment() {
        variableStore.set("x", 10);
        processor.processLine("x -= 4");
        assertEquals(6, variableStore.get("x"));
    }

    @Test
    void testMinusAssignment2() {
        variableStore.set("x", 10);
        processor.processLine("x = x++");
        assertEquals(10, variableStore.get("x"));
    }

    @Test
    void testExpressionWithoutAssignment() {
        // Assuming "3 * 2" returns 6 and is printed
        // You can capture System.out using ByteArrayOutputStream if needed
        processor.processLine("3 * 2");
        // No assertion here; expression result is printed
    }

    @Test
    void testInvalidVariableName() {
        Exception ex = assertThrows(IllegalArgumentException.class, () ->
                processor.processLine("1x = 5")
        );
        assertTrue(ex.getMessage().contains("Invalid variable name"));
    }

    @Test
    void testUnknownOperatorIsTreatedAsExpression() {
        // "x ** 2" is not assignment, so it's treated as expression
        // Should throw during expression evaluation (depending on your parser)
        assertThrows(RuntimeException.class, () ->
                processor.processLine("x ** 2")
        );
    }
}

