package tinycc.implementation.expression.UnaryOperators;

import tinycc.implementation.Signature;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.expression.UnaryOperator;
import tinycc.implementation.type.ObjectType;
import tinycc.implementation.type.PointerType;

public class Indirection extends UnaryOperator{

    
    public Indirection(Expression applicable) {
        super(applicable);

        // set valid signatures and add them to list
        Signature sig1 = new Signature(PointerType.class, null, ObjectType.class);
        allowedSignatures.add(sig1);
    }

    @Override
    public String toString() {
        return "*";
    }
}
