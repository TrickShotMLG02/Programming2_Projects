package tinycc.implementation.expression;

public class BinaryExpression extends Expression{

    private BinaryOperator operator;
    private Expression left;
    private Expression right;

    public BinaryExpression(BinaryOperator operator, Expression left, Expression right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        // TODO: finish implementation
        return "Binary_" + operator + "[Const_42, Const_1337]";
    }
    
}
