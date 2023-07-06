package tinycc.implementation.expression.BinaryExpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.BinaryExpression;
import tinycc.implementation.expression.BinaryOperator;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.type.IntegerType;
import tinycc.implementation.type.PointerType;
import tinycc.implementation.type.Type;

public class AddExpression extends BinaryExpression {

    public AddExpression(BinaryOperator operator, Expression left, Expression right) {
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
        if (typeLeft.isPointerType() && typeRight.isPointerType()) {
            // TODO: print error since only (pointer and int, int and pointer, int and int) are allowed
            //d.printError(this, "...");
        }
        
        // check which rule applies
        if (typeLeft.isIntegerType() && typeRight.isIntegerType()) {
            return new IntegerType();
        }

        if (typeLeft.isIntegerType() && typeRight.isPointerType()) {
            // return new pointer
            return new PointerType(getRight().checkType(d, s));
        }

        if (typeLeft.isPointerType() && typeRight.isIntegerType()) {
            // return new pointer
            return new PointerType(getLeft().checkType(d, s));
        }

        // shouldn't reach this case
        // print error here
        return null;     
    }
}
