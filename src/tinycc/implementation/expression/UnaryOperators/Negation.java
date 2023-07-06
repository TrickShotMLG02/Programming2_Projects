package tinycc.implementation.expression.UnaryOperators;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.expression.UnaryOperator;
import tinycc.implementation.type.Type;

public class Negation extends UnaryOperator{

    public Negation(Expression applicable) {
        super(applicable);
    }

    @Override
    public String toString() {
        // TODO: Check if that is correct
        return "!";
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        // TODO Bonus task 3b
        throw new UnsupportedOperationException("Unimplemented method 'checkType'");
    }
}
