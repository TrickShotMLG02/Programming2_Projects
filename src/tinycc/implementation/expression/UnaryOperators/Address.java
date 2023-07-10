package tinycc.implementation.expression.UnaryOperators;

import tinycc.implementation.expression.UnaryOperator;

public class Address extends UnaryOperator{

    @Override
    public String toString() {
        return "&";
    }
}
