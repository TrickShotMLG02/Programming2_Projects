package tinycc.implementation.expression;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.Scope;
import tinycc.implementation.type.Type;
import tinycc.parser.Token;

public abstract class PrimaryExpression extends Expression {

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

    @Override
    public boolean isLValue() {
        return false;
    } 
}
