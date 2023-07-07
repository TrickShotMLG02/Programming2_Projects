package tinycc.implementation.expression;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.Scope;
import tinycc.implementation.type.Type;
import tinycc.parser.Token;

public class UnaryExpression extends Expression {

    private Token token;
    private UnaryOperator operator;
    private Expression exp;

    public UnaryExpression(Token token, UnaryOperator operator, Expression exp) {
        this.token = token;
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

    @Override
    public Token getToken() {
        return token;
    }

    @Override
    public boolean isLValue() {
        return false;
    }
    
}
