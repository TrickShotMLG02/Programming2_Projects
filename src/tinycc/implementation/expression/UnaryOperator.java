package tinycc.implementation.expression;

public class UnaryOperator {

    Expression applicable;

    public UnaryOperator(Expression applicable) {
        this.applicable = applicable;
    }

    @Override
    public String toString() {
        return "Unary_";
    }  

    public Expression getApplicable() {
        return applicable;
    }
}
