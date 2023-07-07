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
            // check if integer type was assigned
            if (typeRight.isIntegerType()) {
                // return type of left expression, since we assign a value from right to var of left and thus the type of the left side is important
                return typeLeft;
            } else {
                // no integer type was assigned, thus return scalar type
                return new ScalarType();
            }
        }
        else {
            d.printError(getLeft().getToken(), "Not L-Value");
            return null;
        }
    }
}
