package tinycc.parser;

import java.util.List;

import tinycc.diagnostic.Locatable;
import tinycc.implementation.Util;
import tinycc.implementation.expression.BinaryExpression;
import tinycc.implementation.expression.BinaryOperator;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.expression.PrimaryExpression;
import tinycc.implementation.statement.Statement;
import tinycc.implementation.type.FunctionType;
import tinycc.implementation.type.ObjectType;
import tinycc.implementation.type.PointerType;
import tinycc.implementation.type.Type;

public class ASTFactoryClass implements ASTFactory {

    @Override
    public Statement createBlockStatement(Locatable loc, List<Statement> statements) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createBlockStatement'");
    }

    @Override
    public Statement createDeclarationStatement(Type type, Token name, Expression init) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createDeclarationStatement'");
    }

    @Override
    public Statement createExpressionStatement(Locatable loc, Expression expression) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createExpressionStatement'");
    }

    @Override
    public Statement createIfStatement(Locatable loc, Expression condition, Statement consequence,
            Statement alternative) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createIfStatement'");
    }

    @Override
    public Statement createReturnStatement(Locatable loc, Expression expression) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createReturnStatement'");
    }

    @Override
    public Statement createWhileStatement(Locatable loc, Expression condition, Statement body) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createWhileStatement'");
    }

    @Override
    public Type createFunctionType(Type returnType, List<Type> parameters) {
        return new FunctionType(returnType, parameters);
    }

    @Override
    public Type createPointerType(Type pointsTo) {
        return new PointerType(pointsTo);
    }

    @Override
    public Type createBaseType(TokenKind kind) {
        return Util.create(kind, ObjectType.class, Util.CREATE_BASE_TYPE);
    }

    @Override
    public Expression createBinaryExpression(Token operator, Expression left, Expression right) {
        // create Binary Operator of Type BinaryOperator
        BinaryOperator op = Util.create(operator.getKind(), BinaryOperator.class, Util.CREATE_BINARY_OPERATOR);
        return new BinaryExpression(op, left, right);
    }

    @Override
    public Expression createCallExpression(Token token, Expression callee, List<Expression> arguments) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createCallExpression'");
    }

    @Override
    public Expression createConditionalExpression(Token token, Expression condition, Expression consequence,
            Expression alternative) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createConditionalExpression'");
    }

    @Override
    public Expression createUnaryExpression(Token operator, boolean postfix, Expression operand) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createUnaryExpression'");
    }

    @Override
    public Expression createPrimaryExpression(Token token) {
        return Util.create(token, PrimaryExpression.class);
    }

    @Override
    public void createExternalDeclaration(Type type, Token name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createExternalDeclaration'");
    }

    @Override
    public void createFunctionDefinition(Type type, Token name, List<Token> parameterNames, Statement body) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createFunctionDefinition'");
    }
    
}
