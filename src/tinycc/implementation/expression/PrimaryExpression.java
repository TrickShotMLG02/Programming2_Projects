package tinycc.implementation.expression;

import tinycc.parser.Token;

public class PrimaryExpression extends Expression {

    private Token token;

    public PrimaryExpression(Token token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "Const_";
    }

    public Token getToken() {
        return token;
    }
    
}
