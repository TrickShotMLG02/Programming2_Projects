package tinycc.implementation.expression.UnaryOperators;

import tinycc.implementation.expression.Expression;
import tinycc.implementation.expression.UnaryOperator;

public class SizeOf extends UnaryOperator{

    public SizeOf(Expression applicable) {
        super(applicable);
    }

    @Override
    public String toString() {
        return "sizeof";
    }
    
}
