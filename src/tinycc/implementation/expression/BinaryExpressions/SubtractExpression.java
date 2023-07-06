package tinycc.implementation.expression.BinaryExpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.BinaryExpression;
import tinycc.implementation.expression.BinaryOperator;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.type.IntegerType;
import tinycc.implementation.type.PointerType;
import tinycc.implementation.type.Type;
import tinycc.parser.Token;

public class SubtractExpression extends BinaryExpression{

    public SubtractExpression(Token token, BinaryOperator operator, Expression left, Expression right) {
        super(token, operator, left, right);
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        Type typeLeft = getLeft().checkType(d, s);
        Type typeRight = getRight().checkType(d, s);

        if (!typeLeft.isIntegerType() && !typeLeft.isPointerType()) {
            d.printError(getLeft().getToken(), "", null);
        }
        if (!typeRight.isIntegerType() && !typeRight.isPointerType()) {
            d.printError(getRight().getToken(), "", null);
        }
        
        // check which rule applies
        if (typeLeft.isIntegerType() && typeRight.isIntegerType()) {
            return new IntegerType();
        }

        if (typeLeft.isPointerType() && typeRight.isIntegerType()) {
            // return new pointer
            return new PointerType(getLeft().checkType(d, s));
        }

        if (typeLeft.isPointerType() && typeRight.isPointerType()) {
            PointerType pTypeLeft = (PointerType) typeLeft;
            PointerType pTypeRight = (PointerType) typeRight;
            
            // check if pointer types are identical
            if (pTypeLeft.equals(pTypeRight)) {
                return new PointerType(pTypeLeft);
            }

            // otherwise print error since types not identical
        }

        // shouldn't reach this case
        // print error here
        return null;     
    }
}
