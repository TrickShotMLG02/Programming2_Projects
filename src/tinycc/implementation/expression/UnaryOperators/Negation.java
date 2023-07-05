package tinycc.implementation.expression.UnaryOperators;

import tinycc.implementation.expression.Expression;
import tinycc.implementation.expression.UnaryOperator;

public class Negation extends UnaryOperator{

    public Negation(Expression applicable) {
        super(applicable);
    }

    @Override
    public String toString() {
        // TODO: Check if that is correct
        return "!";
    }
}
