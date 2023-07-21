package tinycc.implementation.statement.Statements;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.CompilationScope;
import tinycc.implementation.Scope;
import tinycc.implementation.TopLevelConstructs.ExternalDeclaration;
import tinycc.implementation.TopLevelConstructs.ExternalDeclarations.GlobalVariable;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.statement.Statement;
import tinycc.implementation.type.Type;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.ImmediateInstruction;
import tinycc.mipsasmgen.MemoryInstruction;
import tinycc.mipsasmgen.MipsAsmGen;

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
    public void checkType(Diagnostic d, Scope s, ExternalDeclaration f) {
        // check that expression type and type oof external declaration are equal
        Type initType = null;
        if (init != null) {
            initType = init.checkType(d, s);
        }

        Type extDeclType = extDeclaration.getType();

        if (init != null) {
            // check if both are pointers
            if (initType.isPointerType() && extDeclType.isPointerType()) {
                // check if they are equal or init is void pointer
                if (!initType.equals(extDeclType) && !initType.isVoidPointer()) {
                    d.printError(extDeclaration.getToken(), "pointer assignment invalid");
                }
            }
            // check if 
            else if (!initType.equals(extDeclType)) {
                // print error, since init type and declaration type are not equal
                d.printError(extDeclaration.getToken(), "invalid init type " + extDeclType + " expected, but was " + initType);
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

    @Override
    public void generateCode(CompilationScope s, MipsAsmGen gen) {
        // check if it is a variable -> it is a local declaration
        //since it is stored inside a declaration
        if (getExternalDeclaration().isGlobalVariable()) {
            GlobalVariable var = (GlobalVariable) getExternalDeclaration();
            String declarationName = var.getToken().getText();

            try {
                s.addLocalDeclaration(declarationName);
                int offset = s.lookupLocalDeclaration(declarationName);

                GPRegister val;
                // check if init expression exists
                if (var.getInitExpression() != null) {
                    // generate code of the init expression and store its register
                    val = var.getInitExpression().generateCode(s, gen);
                }
                else {
                    // get new register to store result in
                    val = s.getNextFreeTempRegister();

                    // load immediate 0 into register val
                    gen.emitInstruction(ImmediateInstruction.ADDI, val, GPRegister.ZERO, 0);
                }
                
                // generate instruction with stack
                gen.emitInstruction(MemoryInstruction.SW, val, null, offset, GPRegister.SP);
                
                // free val register again
                s.remove(val);

            } catch (Exception e) {
            }
        }
        else if (getExternalDeclaration().isFunctionDeclaration()) {
            throw new UnsupportedOperationException("Unimplemented method 'generateCode'");
        }
        else if (getExternalDeclaration().isFunction()) {
            throw new UnsupportedOperationException("Unimplemented method 'generateCode'");
        }
    }

    public ExternalDeclaration getExternalDeclaration() {
        return extDeclaration;
    }
}
