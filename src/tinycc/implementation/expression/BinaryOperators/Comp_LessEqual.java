package tinycc.implementation.expression.BinaryOperators;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.BinaryOperator;
import tinycc.implementation.type.Type;

public class Comp_LessEqual extends BinaryOperator {
    @Override
    public String toString() {
        return "<=";
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'checkType'");
    }
}
