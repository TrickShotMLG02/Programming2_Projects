package tinycc.implementation.expression.UnaryOperators;

import tinycc.implementation.expression.Expression;
import tinycc.implementation.expression.UnaryOperator;

public class Address extends UnaryOperator{

    public Address(Expression applicable) {
        super(applicable);
    }

    @Override
    public String toString() {
        return "&";
    }
}
