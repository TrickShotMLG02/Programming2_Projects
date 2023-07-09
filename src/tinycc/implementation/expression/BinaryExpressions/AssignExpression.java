package tinycc.implementation.expression.BinaryExpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.BinaryExpression;
import tinycc.implementation.expression.BinaryOperator;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.type.Type;
import tinycc.parser.Token;

public class AssignExpression extends BinaryExpression {

    public AssignExpression(Token token, BinaryOperator operator, Expression left, Expression right) {
        super(token, operator, left, right);
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        Type typeLeft = getLeft().checkType(d, s);
        Type typeRight = getRight().checkType(d, s);

        if (!typeLeft.isScalarType()) {
            d.printError(getLeft().getToken(), "Not a scalar type");
        }
        
        if (!typeRight.isScalarType()) {
            d.printError(getRight().getToken(), "Not a scalar type");
        }

        // conditions from project description (page 6) assignments
        boolean identical = typeLeft.equals(typeRight);
        boolean intergerTypes = typeLeft.isIntegerType() && typeRight.isIntegerType();
        boolean bothPointersMinOneVoid = (typeLeft.isPointerType() && typeRight.isPointerType()) && (typeLeft.isVoidPointer() || typeRight.isVoidPointer());
        boolean leftPointerRightNullPointer = typeLeft.isPointerType() && getRight().isNullPointer();

        // left type must be lValue
        if (!getLeft().isLValue()) {
            d.printError(getToken(), "Left type not a L-Value");
        }

        // check if any condition is satisfied
        if (!identical && !intergerTypes && !bothPointersMinOneVoid && !leftPointerRightNullPointer) {
            d.printError(getToken(), "invalid assignment");
        }

        // check if any condition is true -> return always left type
        if (identical ||intergerTypes || bothPointersMinOneVoid || leftPointerRightNullPointer) {
            return typeLeft;
        }

        // should not reach this code
        d.printError(getToken(), "This should not be reached since it is covered from above statements");
        return null;
    }
}
