package tinycc.implementation.expression.UnaryExpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.expression.UnaryExpression;
import tinycc.implementation.expression.UnaryOperator;
import tinycc.implementation.type.PointerType;
import tinycc.implementation.type.Type;

public class IndirectionExpression extends UnaryExpression {

    public IndirectionExpression(UnaryOperator operator, Expression exp) {
        super(operator, exp);
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        // grab the type of the expression
        Type applicableType = getExpression().checkType(d, s);

        // check if the type is a pointer
        if (applicableType.isPointerType()) {
            // extract the type to which the pointer points to
            PointerType pType = (PointerType) applicableType;

            // return the type
            return pType.getPointerType();
        }
        else {
            // TODO: print error
            //d.printError(null, null, null);
            return null;
        }
    }
}
