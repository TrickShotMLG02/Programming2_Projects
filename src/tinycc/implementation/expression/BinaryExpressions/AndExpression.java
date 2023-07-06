package tinycc.implementation.expression.BinaryExpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.BinaryExpression;
import tinycc.implementation.expression.BinaryOperator;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.type.IntegerType;
import tinycc.implementation.type.Type;
import tinycc.parser.Token;

public class AndExpression extends BinaryExpression {

    /**
     * Bonus task
     * @param operator
     * @param left
     * @param right
     */
    public AndExpression(Token token, BinaryOperator operator, Expression left, Expression right) {
        super(token, operator, left, right);
    }   

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        Type typeLeft = getLeft().checkType(d, s);
        Type typeRight = getRight().checkType(d, s);

        if (!typeLeft.isScalarType()) {
            //d.printError(this, "...");
        }
        
        if (!typeRight.isScalarType()) {
            //d.printError(this, "...");
        }

        return new IntegerType();
    }
}
