package tinycc.implementation.expression.PrimaryExpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.PrimaryExpression;
import tinycc.implementation.statement.Statements.Declaration;
import tinycc.implementation.type.Type;
import tinycc.parser.Token;

public class Identifier extends PrimaryExpression {

    public Identifier(Token token) {
        super(token);
    }

    @Override
    public java.lang.String toString() {
        return "Var_" + getToken().getText();
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        // check if token was declared earlier in scope
        try {
            // grab declaration from scope
            Declaration decl = s.lookup(getToken().getText());

            // return the type of the declaration
            return decl.getExternalDeclaration().getType();
        } catch (Exception e) {
            // identifier not declared
            d.printError(getToken(), "identifier not declared " + getToken().getText());
            return null;
        }
    }

    @Override
    public boolean isLValue() {
        return true;
    } 
}
