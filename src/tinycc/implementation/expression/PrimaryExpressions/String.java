package tinycc.implementation.expression.PrimaryExpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.PrimaryExpression;
import tinycc.implementation.type.Type;
import tinycc.parser.Token;

public class String extends PrimaryExpression {
    
    public String(Token token) {
        super(token);
    }

    @Override
    public java.lang.String toString() {
        return super.toString() + "\"" + getToken().getText() + "\"";
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'checkType'");
    }
}
