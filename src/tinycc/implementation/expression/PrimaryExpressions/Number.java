package tinycc.implementation.expression.PrimaryExpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.PrimaryExpression;
import tinycc.implementation.type.Type;
import tinycc.implementation.type.BaseTypes.Int;
import tinycc.parser.Token;

public class Number extends PrimaryExpression {
    
    public Number(Token token) {
        super(token);
    }

    @Override
    public java.lang.String toString() {
        return super.toString() + getToken().getText();
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        return new Int();
    }

    @Override
    public boolean isNullPointer() {
        return getToken().getText().equals("0");
    }
}
