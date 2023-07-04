package tinycc.implementation.expression.PrimaryExpressions;

import tinycc.implementation.expression.PrimaryExpression;
import tinycc.parser.Token;

public class Number extends PrimaryExpression {
    
    public Number(Token token) {
        super(token);
    }

    @Override
    public java.lang.String toString() {
        return super.toString() + getToken().getText();
    }
}
