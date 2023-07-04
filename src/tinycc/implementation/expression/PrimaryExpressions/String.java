package tinycc.implementation.expression.PrimaryExpressions;

import tinycc.implementation.expression.PrimaryExpression;
import tinycc.parser.Token;

public class String extends PrimaryExpression {
    
    public String(Token token) {
        super(token);
    }

    @Override
    public java.lang.String toString() {
        return super.toString() + "\"" + getToken().getText() + "\"";
    }
}
