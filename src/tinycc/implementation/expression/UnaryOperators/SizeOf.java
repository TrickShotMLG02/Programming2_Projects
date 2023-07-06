package tinycc.implementation.expression.UnaryOperators;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.expression.UnaryOperator;
import tinycc.implementation.type.Type;
import tinycc.implementation.type.BaseTypes.Int;

public class SizeOf extends UnaryOperator{

    public SizeOf(Expression applicable) {
        super(applicable);
    }

    @Override
    public String toString() {
        return "sizeof";
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        // grab the type of the applicable
        Type applicableType = getApplicable().checkType(d, s);

        // check if the type is a Object Type or // TODO: Stringliteral
        if (applicableType.isObjectType()) {

            // return integer type sinze sizeof is always int
            return new Int();
        }
        else {
            // TODO: print error
            d.printError(null, null, null);
            return null;
        }
    }
    
}
