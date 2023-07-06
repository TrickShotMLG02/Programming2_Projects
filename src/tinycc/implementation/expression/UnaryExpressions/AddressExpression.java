package tinycc.implementation.expression.UnaryExpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.expression.UnaryExpression;
import tinycc.implementation.expression.UnaryOperator;
import tinycc.implementation.type.Type;

public class AddressExpression extends UnaryExpression {

    public AddressExpression(UnaryOperator operator, Expression exp) {
        super(operator, exp);
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
       // grab the type of the applicable expression
        Type applicableType = getExpression().checkType(d, s);

        // type can be anything for address generation

        // thus return pointer of resulting type of expression
        return applicableType;
    }
}
