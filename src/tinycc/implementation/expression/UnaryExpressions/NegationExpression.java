package tinycc.implementation.expression.UnaryExpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.expression.UnaryExpression;
import tinycc.implementation.expression.UnaryOperator;
import tinycc.implementation.type.Type;
import tinycc.parser.Token;

public class NegationExpression extends UnaryExpression {

    public NegationExpression(Token token, UnaryOperator operator, Expression exp) {
        super(token, operator, exp);
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        // TODO Bonus task 3b
        throw new UnsupportedOperationException("Unimplemented method 'checkType'");
    } 
}
