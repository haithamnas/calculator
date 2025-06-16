package com.taboola.calculator;

public class Token {

    private final Type type;
    private final String value;
    // For operator type only
    private final boolean isUnary;
    private final boolean isPrefix;
    private final String associatedVariable; // Only for unary ops like ++i or i++

    public Token(Type type, String text) {
        this(type, text, false, false, null);
    }

    public Token(String operatorSymbol, boolean isPrefix, String associatedVariable) {
        this(Type.OPERATOR, operatorSymbol, true, isPrefix, associatedVariable);
    }

    private Token(Type type, String value, boolean isUnary, boolean isPrefix, String associatedVariable) {
        this.type = type;
        this.value = value;
        this.isUnary = isUnary;
        this.isPrefix = isPrefix;
        this.associatedVariable = associatedVariable;
    }

    public Type getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public boolean isVariable() {
        return type == Type.VARIABLE;
    }

    public boolean isOperator() {
        return type == Type.OPERATOR;
    }

    public boolean isUnary() {
        return isUnary;
    }
    public boolean isPrefix() {
        return isUnary && isPrefix;
    }

    public String getAssociatedVariable() {
        return associatedVariable;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Token{type=").append(type);
        sb.append(", value='").append(value).append('\'');

        if (type == Type.OPERATOR) {
            sb.append(", isUnary=").append(isUnary);
            if (isUnary) {
                sb.append(", isPrefix=").append(isPrefix);
                if (associatedVariable != null) {
                    sb.append(", associatedVariable='").append(associatedVariable).append('\'');
                }
            }
        }

        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Token other)) return false;
        return this.type == other.type && this.value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return type.hashCode() * 31 + value.hashCode();
    }
}

