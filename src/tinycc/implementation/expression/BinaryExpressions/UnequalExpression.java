package tinycc.implementation.expression.BinaryExpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.BinaryExpression;
import tinycc.implementation.expression.BinaryOperator;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.type.Type;
import tinycc.implementation.type.BaseTypes.Int;
import tinycc.parser.Token;

public class UnequalExpression extends BinaryExpression {

    public UnequalExpression(Token token, BinaryOperator operator, Expression left, Expression right) {
        super(token, operator, left, right);
    }

     @Override
    public Type checkType(Diagnostic d, Scope s) {
        Type typeLeft = getLeft().checkType(d, s);
        Type typeRight = getRight().checkType(d, s);

        if (!typeLeft.isIntegerType() && !typeLeft.isPointerType()) {
            d.printError(getLeft().getToken(), "");
        }
        
        if (!typeRight.isIntegerType() && !typeRight.isPointerType()) {
            d.printError(getRight().getToken(), "");
        }

        // check if both types are integer types
        if (typeLeft.isIntegerType() && typeRight.isIntegerType()) {
            return new Int();
        }

        // check if pointers types and they are equal
        if (typeLeft.isPointerType() && typeRight.isPointerType() && typeLeft.equals(typeRight)) {
            return new Int();
        }

        // TODO: check void pointer and null pointer constant



        // TODO: print error and return null
        d.printError(getToken(), "Invalid types");
        return null;
    }
}
