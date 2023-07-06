package tinycc.implementation.expression.UnaryOperators;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.expression.UnaryOperator;
import tinycc.implementation.type.PointerType;
import tinycc.implementation.type.Type;

public class Indirection extends UnaryOperator{

    
    public Indirection(Expression applicable) {
        super(applicable);
    }

    @Override
    public String toString() {
        return "*";
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        // grab the type of the applicable
        Type applicableType = getApplicable().checkType(d, s);

        // check if the type is a pointer
        if (applicableType.isPointerType()) {
            // extract the type to which the pointer points to
            PointerType pType = (PointerType) applicableType;

            // return the type
            return pType.getPointerType();
        }
        else {
            // TODO: print error
            d.printError(null, null, null);
            return null;
        }
    }
}
