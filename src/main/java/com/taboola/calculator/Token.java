package com.taboola.calculator;

public class Token {

    private final TokenType tokenType;
    private final String value;
    // For operator type only
    private final boolean isUnary;
    private final boolean isPrefix;
    private final String associatedVariable; // Only for unary ops like ++i or i++

    public Token(TokenType tokenType, String text) {
        this(tokenType, text, false, false, null);
    }

    public Token(String operatorSymbol, boolean isPrefix, String associatedVariable) {
        this(TokenType.OPERATOR, operatorSymbol, true, isPrefix, associatedVariable);
    }

    Token(TokenType tokenType, String value, boolean isUnary, boolean isPrefix, String associatedVariable) {
        this.tokenType = tokenType;
        this.value = value;
        this.isUnary = isUnary;
        this.isPrefix = isPrefix;
        this.associatedVariable = associatedVariable;
    }

    public TokenType getType() {
        return tokenType;
    }

    public String getValue() {
        return value;
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
        sb.append("Token{type=").append(tokenType);
        sb.append(", value='").append(value).append('\'');

        if (tokenType == TokenType.OPERATOR) {
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
        return this.tokenType == other.tokenType && this.value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return tokenType.hashCode() * 31 + value.hashCode();
    }
}

