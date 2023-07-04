package tinycc.implementation.expression.PrimaryExpressions;

import tinycc.implementation.expression.PrimaryExpression;
import tinycc.parser.Token;

public class Identifier extends PrimaryExpression {

    public Identifier(Token token) {
        super(token);
    }

    @Override
    public java.lang.String toString() {
        //TODO: IMPLEMENT ME
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
