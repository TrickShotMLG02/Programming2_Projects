package tinycc.implementation.expression.UnaryExpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.expression.UnaryExpression;
import tinycc.implementation.expression.UnaryOperator;
import tinycc.implementation.type.Type;
import tinycc.implementation.type.BaseTypes.Int;

public class SizeOfExpression extends UnaryExpression {

    public SizeOfExpression(UnaryOperator operator, Expression exp) {
        super(operator, exp);
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        // grab the type of the expression
        Type applicableType = getExpression().checkType(d, s);

        // check if the type is a Object Type or // TODO: Stringliteral
        if (applicableType.isObjectType()) {

            // return integer type sinze sizeof is always int
            return new Int();
        }
        else {
            // TODO: print error
            //d.printError(null, null, null);
            return null;
        }
    }
}
