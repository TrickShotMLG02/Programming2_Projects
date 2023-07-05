package tinycc.implementation.expression;

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
    
}
