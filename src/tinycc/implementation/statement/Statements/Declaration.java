package tinycc.implementation.statement.Statements;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.Scope;
import tinycc.implementation.TopLevelConstructs.ExternalDeclaration;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.statement.Statement;
import tinycc.implementation.type.Type;

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

    @Override
    public void checkType(Diagnostic d, Scope s) {
        // check that expression type and type oof external declaration are equal

        Type initType = init.checkType(d, s);
        Type extDeclType = extDeclaration.getType();

        if (!initType.equals(extDeclType)) {
            // print error, since init type and declaration type are not equal
        }
    }
    
}
