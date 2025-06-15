package com.taboola.calculator;

import java.util.function.BiFunction;
/**
 * Represents assignment operators and their behavior on integer operands.
 */
public enum AssignmentOperator {

    /**
     * Simple assignment: x = y ⇒ assigns the right-hand value to the left-hand variable.
     */
    ASSIGN("=", (a, b) -> b),

    /**
     * Addition assignment: x += y ⇒ adds y to x and assigns the result to x.
     */
    PLUS_ASSIGN("+=", (a, b) -> a + b),

    /**
     * Subtraction assignment: x -= y ⇒ subtracts y from x and assigns the result to x.
     */
    MINUS_ASSIGN("-=", (a, b) -> a - b);

    private final String symbol;
    private final BiFunction<Integer, Integer, Integer> operation;

    AssignmentOperator(String symbol, BiFunction<Integer, Integer, Integer> operation) {
        this.symbol = symbol;
        this.operation = operation;
    }

    /**
     * Applies the operator's logic to the given operands.
     *
     * @param a the current value of the variable (left-hand side)
     * @param b the value on the right-hand side of the assignment
     * @return the result after applying the operation
     */
    public int apply(int a, int b) {
        return operation.apply(a, b);
    }

    /**
     * @return the symbol representing the operator (e.g. "+=", "=")
     */
    public String getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
