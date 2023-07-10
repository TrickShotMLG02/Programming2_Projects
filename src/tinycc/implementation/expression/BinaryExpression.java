package tinycc.implementation.expression;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.Scope;
import tinycc.implementation.type.Type;
import tinycc.parser.Token;

public class BinaryExpression extends Expression {

    private Token token;
    private BinaryOperator operator;
    private Expression left;
    private Expression right;

    public BinaryExpression(Token token, BinaryOperator operator, Expression left, Expression right) {
        this.token = token;
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "Binary_" + operator + "[" + left + "," + right + "]";
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        throw new UnsupportedOperationException("Unimplemented method 'checkType'");
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    public Token getToken() {
        return token;
    }

    @Override
    public boolean isLValue() {
        return false;
    } 
}
