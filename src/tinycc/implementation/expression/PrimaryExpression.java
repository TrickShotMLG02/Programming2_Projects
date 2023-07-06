package tinycc.implementation.expression;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.Scope;
import tinycc.implementation.Util;
import tinycc.implementation.statement.Statements.Declaration;
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
    public Type checkType(Diagnostic d, Scope s) {
        // lookup token in scope to check if it was declared previously
        // TODO: make sure this works
        try {
            Declaration decl = s.lookup(token.getInputName());
            return Util.createType(token.getKind());
        } catch (Exception e) {
            d.printError(token, "", null);
            return null;
        }

    }
    
}
