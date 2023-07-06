package tinycc.implementation.expression.UnaryOperators;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.UnaryOperator;
import tinycc.implementation.type.Type;

public class SizeOf extends UnaryOperator{

    @Override
    public String toString() {
        return "sizeof";
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'checkType'");
    }
}
