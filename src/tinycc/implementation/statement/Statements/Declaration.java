package tinycc.implementation.statement.Statements;

import tinycc.implementation.TopLevelConstructs.ExternalDeclaration;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.statement.Statement;

public class Declaration extends Statement{
    ExternalDeclaration extDeclaration;
    Expression init;

    public Declaration(ExternalDeclaration extDeclaration, Expression init) {
        this.extDeclaration = extDeclaration;
        this.init = init;
    }

    @Override
    public String toString() {
        // initial value is handled by ExternalDeclaration toString()
        return extDeclaration.toString();
    }
    
}
