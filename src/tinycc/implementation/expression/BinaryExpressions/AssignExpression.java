package tinycc.implementation.expression.BinaryExpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.BinaryExpression;
import tinycc.implementation.expression.BinaryOperator;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.type.ScalarType;
import tinycc.implementation.type.Type;
import tinycc.parser.Token;

public class AssignExpression extends BinaryExpression {

    public AssignExpression(Token token, BinaryOperator operator, Expression left, Expression right) {
        super(token, operator, left, right);
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        // TODO: find out why type left is null
        Type typeLeft = getLeft().checkType(d, s);
        Type typeRight = getRight().checkType(d, s);

        if (!typeLeft.isScalarType()) {
            d.printError(getLeft().getToken(), "Not a scalar type");
        }
        
        if (!typeRight.isScalarType()) {
            d.printError(getRight().getToken(), "Not a scalar type");
        }

        if (getLeft().isLValue()) {
            return new ScalarType();
        }
        else {
            d.printError(getLeft().getToken(), "Not L-Value");
            return null;
        }
    }
}
