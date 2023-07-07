package tinycc.implementation.expression.UnaryExpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.expression.UnaryExpression;
import tinycc.implementation.expression.UnaryOperator;
import tinycc.implementation.type.PointerType;
import tinycc.implementation.type.Type;
import tinycc.parser.Token;

public class AddressExpression extends UnaryExpression {

    public AddressExpression(Token token, UnaryOperator operator, Expression exp) {
        super(token, operator, exp);
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
       // grab the type of the applicable expression
        Type applicableType = getExpression().checkType(d, s);

        // check if type is complete
        if (applicableType.isComplete()) {
            // thus return pointer of resulting type of expression
            return new PointerType(applicableType);
        }
        else {
            d.printError(getToken(), "Type not complete", null);
            return null;
        }
    }
}
