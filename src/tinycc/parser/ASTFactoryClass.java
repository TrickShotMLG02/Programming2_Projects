package tinycc.parser;

import java.util.List;

import tinycc.diagnostic.Locatable;
import tinycc.implementation.expression.Expression;
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

        // extract text of kind
        String strKind = kind.getText();

        // capitalize first letter of string and rest lowercase
        String formattedKind = strKind.substring(0, 1).toUpperCase() + strKind.substring(1).toLowerCase();

        // the package path of the base types
        String path = "tinycc.implementation.type.BaseTypes.";

        // concat package path and kind
        String fullClassName = path + formattedKind;

        try {
            // create class with name from kind
            Class<?> cls = Class.forName(fullClassName);

            // return new Object of type cls
            return (ObjectType) cls.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            // TODO: what should i do on invalid input?
            return null;
        }
    }

    @Override
    public Expression createBinaryExpression(Token operator, Expression left, Expression right) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createBinaryExpression'");
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createPrimaryExpression'");
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
