package tinycc.implementation.expression;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.Scope;
import tinycc.implementation.type.Type;

public class UnaryExpression extends Expression {

    private UnaryOperator operator;
    private Expression exp;

    public UnaryExpression(UnaryOperator operator, Expression exp) {
        this.operator = operator;
        this.exp = exp;
    }

    @Override
    public String toString() {
        return "Unary_" + operator + "[" + exp + "]";
    }

    public Expression getExpression() {
        return exp;
    }

    public UnaryOperator getOperator() {
        return operator;
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        throw new UnsupportedOperationException("Unimplemented method 'checkType'");
    }
    
}
