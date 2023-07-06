package tinycc.implementation.expression;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.Scope;
import tinycc.implementation.type.Type;

public abstract class UnaryOperator {

    private Expression applicable;

    public UnaryOperator(Expression applicable) {
        this.applicable = applicable;
    }

    @Override
    public String toString() {
        return "Unary_";
    }  

    public Expression getApplicable() {
        return applicable;
    }

    /*
     * Compute the resulting type based on the given expression
     */
    public abstract Type checkType(Diagnostic d, Scope s);
}
