package tinycc.implementation.statement.Statements;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.Scope;
import tinycc.implementation.TopLevelConstructs.ExternalDeclaration;
import tinycc.implementation.TopLevelConstructs.ExternalDeclarations.GlobalVariable;
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
        Type initType = null;
        if (init != null) {
            initType = init.checkType(d, s);
        }

        Type extDeclType = extDeclaration.getType();

        if (init != null) {
            if (!initType.equals(extDeclType)) {
                // print error, since init type and declaration type are not equal
                d.printError(extDeclaration.getToken(), "invalid init type");
            }
        }

        // check if declaration is of type void which is not allowed
        if (extDeclaration.isGlobalVariable() && extDeclType.isVoidType()) {
            GlobalVariable var = (GlobalVariable) extDeclaration;
            d.printError(var.getToken(), "declaration cannot be of type void");
        }
        
        try {
            // extract identifier name
            String id = getExternalDeclaration().getToken().getText();

            // create new declaration
            Declaration decl = new Declaration(extDeclaration, init);

            // add it to scope if it isn't already added
            s.add(id, decl);
        } catch (Exception e) {
        }
    }

    public ExternalDeclaration getExternalDeclaration() {
        return extDeclaration;
    }
}
