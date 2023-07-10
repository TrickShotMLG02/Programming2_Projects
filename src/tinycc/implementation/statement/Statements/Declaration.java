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

            // check if init type is a function type
            if (initType.isFunctionType()) {
                d.printError(init.getToken(), "Cannot initialize with function type");
            }
        }

        // check if declaration is of type void which is not allowed
        if (extDeclaration.isGlobalVariable() && extDeclType.isVoidType()) {
            GlobalVariable var = (GlobalVariable) extDeclaration;
            d.printError(var.getToken(), "declaration cannot be of type void");
        }


        // create new declaration of current variable
        Declaration decl = new Declaration(extDeclaration, init);

        // extract identifier name
        String id = getExternalDeclaration().getToken().getText();

        // Declaration of variable in scope
        Declaration scopeDecl = null;

        try {
            // check if it was already declared in any scope
            // if so, store it in scopeDecl
            scopeDecl = s.lookup(id);
        } catch (Exception e) {
            // not previously defined
        }

        // check if it was already declared in a scope
        if (scopeDecl != null) {

            // check if previous declaration has different type as new declaration
            if (scopeDecl.getExternalDeclaration().getType() != extDeclType) {
                d.printError(extDeclaration.getToken(), "Previously declared with different type");
            }
        }

        try {
            // add new declaration to scope
            s.add(id, decl);
        }
        catch (Exception e) {
            // should have updated the existing value, since it throws an error that is was already declared previously
        }
    }

    public ExternalDeclaration getExternalDeclaration() {
        return extDeclaration;
    }
}
