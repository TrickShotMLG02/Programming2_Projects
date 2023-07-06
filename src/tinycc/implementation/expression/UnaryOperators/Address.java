package tinycc.implementation.expression.UnaryOperators;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.expression.UnaryOperator;
import tinycc.implementation.type.Type;

public class Address extends UnaryOperator{

    public Address(Expression applicable) {
        super(applicable);
    }

    @Override
    public String toString() {
        return "&";
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {

        // grab the type of the applicable expression
        Type applicableType = getApplicable().checkType(d, s);

        // type can be anything for address generation

        // thus return pointer of resulting type of expression
        return applicableType;
    }
}
