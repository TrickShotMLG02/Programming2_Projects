package tinycc.parser;

import java.util.ArrayList;
import java.util.List;

import tinycc.diagnostic.Locatable;
import tinycc.implementation.Util;
import tinycc.implementation.TopLevelConstructs.ExternalDeclaration;
import tinycc.implementation.expression.BinaryExpression;
import tinycc.implementation.expression.BinaryOperator;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.expression.FunctionCall;
import tinycc.implementation.expression.UnaryExpression;
import tinycc.implementation.expression.UnaryOperator;
import tinycc.implementation.statement.Statement;
import tinycc.implementation.statement.Statements.Block;
import tinycc.implementation.statement.Statements.Declaration;
import tinycc.implementation.statement.Statements.ExpressionStatement;
import tinycc.implementation.statement.Statements.If;
import tinycc.implementation.statement.Statements.Return;
import tinycc.implementation.statement.Statements.While;
import tinycc.implementation.type.FunctionType;
import tinycc.implementation.type.PointerType;
import tinycc.implementation.type.Type;

public class ASTFactoryClass implements ASTFactory {

    // TODO: Maybe use hashset or kv map for easier use and avoiding double declaration of same variable name
    private List<ExternalDeclaration> declarations = new ArrayList<ExternalDeclaration>();
    

    @Override
    public Statement createBlockStatement(Locatable loc, List<Statement> statements) {
        return new Block(loc, statements);
    }

    @Override
    public Statement createDeclarationStatement(Type type, Token name, Expression init) {
        // create declaration
        ExternalDeclaration extDeclaration = Util.createExternalDeclaration(type, name, init);
        declarations.add(extDeclaration);

        // create statement and store declaration there
        return new Declaration(extDeclaration, init);
    }

    @Override
    public Statement createExpressionStatement(Locatable loc, Expression expression) {
        return new ExpressionStatement(loc, expression);
    }

    @Override
    public Statement createIfStatement(Locatable loc, Expression condition, Statement consequence,
            Statement alternative) {
        return new If(loc, condition, consequence, alternative);
    }

    @Override
    public Statement createReturnStatement(Locatable loc, Expression expression) {
        return new Return(expression);
    }

    @Override
    public Statement createWhileStatement(Locatable loc, Expression condition, Statement body) {
        return new While(loc, condition, body);
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
        return Util.createType(kind);
    }

    @Override
    public Expression createBinaryExpression(Token operator, Expression left, Expression right) {
        BinaryOperator op = Util.createBinaryOperator(operator.getKind());
        return new BinaryExpression(op, left, right);
    }

    @Override
    public Expression createCallExpression(Token token, Expression callee, List<Expression> arguments) {
        return new FunctionCall(token, callee, arguments);
    }

    @Override
    public Expression createConditionalExpression(Token token, Expression condition, Expression consequence,
            Expression alternative) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createConditionalExpression'");
    }

    @Override
    public Expression createUnaryExpression(Token operator, boolean postfix, Expression operand) {
        UnaryOperator op = Util.createUnaryOperator(operator, operand);
        return new UnaryExpression(op, operand);
    }

    @Override
    public Expression createPrimaryExpression(Token token) {
        return Util.createPrimaryExpression(token);
    }

    @Override
    public void createExternalDeclaration(Type type, Token name) {
        ExternalDeclaration decl = Util.createExternalDeclaration(type, name, null);
        declarations.add(decl);
    }

    @Override
    public void createFunctionDefinition(Type type, Token name, List<Token> parameterNames, Statement body) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createFunctionDefinition'");
    }
    
    public List<ExternalDeclaration> getExternalDeclarations() {
        return declarations;
    }
}
