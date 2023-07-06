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
        
        // run checkType on exp
        Type expType = exp.checkType(d, s);

        // grab type of operator
        Type operatorType = operator.checkType(d, s);

        // check if type of exp is compatible with type of operator
        // TODO: implement me

        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'checkType'");
    }
    
}
