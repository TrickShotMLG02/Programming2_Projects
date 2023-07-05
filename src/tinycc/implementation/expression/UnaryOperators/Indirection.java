package tinycc.implementation.expression.UnaryOperators;

import tinycc.implementation.expression.Expression;
import tinycc.implementation.expression.UnaryOperator;

public class Indirection extends UnaryOperator{

    
    public Indirection(Expression applicable) {
        super(applicable);
    }

    @Override
    public String toString() {
        return "*";
    }
}
