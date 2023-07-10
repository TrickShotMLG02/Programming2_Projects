package tinycc.implementation.statement.Statements;

import tinycc.diagnostic.Diagnostic;
import tinycc.diagnostic.Locatable;
import tinycc.implementation.Scope;
import tinycc.implementation.TopLevelConstructs.ExternalDeclaration;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.statement.Statement;

public class ExpressionStatement extends Statement {

    Locatable loc;
    Expression expression;

    public ExpressionStatement(Locatable loc, Expression expression) {
        this.loc = loc;
        this.expression = expression;
    }

    @Override
    public String toString() {
        return expression.toString();
    }

    @Override
    public void checkType(Diagnostic d, Scope s, ExternalDeclaration f) {
        expression.checkType(d, s);
    }
    
}
