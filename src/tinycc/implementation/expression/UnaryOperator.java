package tinycc.implementation.expression;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.Scope;
import tinycc.implementation.type.Type;

public abstract class UnaryOperator {

    @Override
    public String toString() {
        return "Unary_";
    }  

    /*
     * Compute the resulting type based on the given expression
     */
    public abstract Type checkType(Diagnostic d, Scope s);
}
