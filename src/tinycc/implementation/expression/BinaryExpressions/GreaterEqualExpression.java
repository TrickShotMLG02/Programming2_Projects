package tinycc.implementation.expression.BinaryExpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.BinaryExpression;
import tinycc.implementation.expression.BinaryOperator;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.type.IntegerType;
import tinycc.implementation.type.Type;

public class GreaterEqualExpression extends BinaryExpression {

    public GreaterEqualExpression(BinaryOperator operator, Expression left, Expression right) {
        super(operator, left, right);
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        Type typeLeft = getLeft().checkType(d, s);
        Type typeRight = getRight().checkType(d, s);

         if (!typeLeft.isIntegerType() && !typeLeft.isPointerType()) {
            // TODO: print error
            //d.printError(this, "...");
        }
        if (!typeRight.isIntegerType() && !typeRight.isPointerType()) {
            // TODO: print error
            //d.printError(this, "...");
        }

        // check for type equality (pointer, pointer or int, int)
        if (typeLeft.equals(typeRight)) {
            return new IntegerType();
        }

        // otherwise print error since types not identical

        return null;
    }
}
