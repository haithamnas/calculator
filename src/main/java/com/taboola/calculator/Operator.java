package com.taboola.calculator;
/**
 * Represents a math operator (+, -, *, /)
 * Each operator has:
 * - A symbol (like "+")
 * - A precedence (e.g., * and / have higher precedence than + and -)
 * - A specific implementation for applying the operation
 */
public enum Operator {
    PLUS("+", 1, false) {
        @Override
        public int apply(int a, int b) {
            return a + b;
        }
    },
    MINUS("-", 1, false) {
        @Override
        public int apply(int a, int b) {
            return a - b;
        }
    },
    MULTIPLY("*", 2, false) {
        @Override
        public int apply(int a, int b) {
            return a * b;
        }
    },
    DIV("/", 2, false) {
        @Override
        public int apply(int a, int b) {
            return a / b;
        }
    },
    INCREMENT("++", 3, true) {
        @Override
        public int applyUnary(int a) {
            return a + 1;
        }
    },
    DECREMENT("--", 3, true) {
        @Override
        public int applyUnary(int a) {
            return a - 1;
        }
    };

    private final String symbol;
    private final int precedence;
    private final boolean unary;

    Operator(String symbol, int precedence, boolean unary) {
        this.symbol = symbol;
        this.precedence = precedence;
        this.unary = unary;
    }

    /**
     * Applies a binary operation. Only valid for binary operators.
     *
     * @param a left operand
     * @param b right operand
     * @return result
     * @throws UnsupportedOperationException if operator is unary
     */
    public int apply(int a, int b) {
        throw new UnsupportedOperationException("Operator " + symbol + " does not support binary apply()");
    }

    /**
     * Applies a unary operation. Only valid for unary operators.
     *
     * @param a operand
     * @return result
     * @throws UnsupportedOperationException if operator is binary
     */
    public int applyUnary(int a) {
        throw new UnsupportedOperationException("Operator " + symbol + " does not support unary apply()");
    }

    public int getPrecedence() {
        return precedence;
    }

    public boolean isUnary() {
        return unary;
    }

    public static Operator fromSymbol(String symbol) {
        for (Operator op : values()) {
            if (op.symbol.equals(symbol)) {
                return op;
            }
        }
        return null;
    }

    public static boolean isOperator(String token) {
        return fromSymbol(token) != null;
    }
}
