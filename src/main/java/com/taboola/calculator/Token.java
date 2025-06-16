package com.taboola.calculator;

public class Token {

    private final Type type;
    private final String value;
    // For operator type only
    private final boolean isUnary;
    private final boolean isPrefix;
    private String associatedVariable; // Only for unary ops like ++i or i++

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
    public Token(String value, boolean isUnary, boolean isPrefix, String associatedVariable){
        this.type = Type.OPERATOR;
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

    public boolean isNumber() {
        return type == Type.NUMBER;
    }

    public boolean isVariable() {
        return type == Type.VARIABLE;
    }

    public boolean isOperator() {
        return type == Type.OPERATOR;
    }

    public boolean isParenthesis() {
        return type == Type.PARENTHESIS;
    }

    public boolean isUnary() {
        return isUnary;
    }
    public boolean isPrefix() {
        return isUnary && isPrefix;
    }

    public boolean isPostfix() {
        return isUnary && !isPrefix;
    }
    public void setAssociatedVariable(String varName) {
        this.associatedVariable = varName;
    }

    public String getAssociatedVariable() {
        return associatedVariable;
    }
    public boolean isLeftParenthesis() {
        return isParenthesis() && "(".equals(value);
    }

    public boolean isRightParenthesis() {
        return isParenthesis() && ")".equals(value);
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
        if (!(obj instanceof Token)) return false;
        Token other = (Token) obj;
        return this.type == other.type && this.value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return type.hashCode() * 31 + value.hashCode();
    }
}

