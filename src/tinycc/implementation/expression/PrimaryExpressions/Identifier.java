package tinycc.implementation.expression.PrimaryExpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.CompilationScope;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.PrimaryExpression;
import tinycc.implementation.statement.Statements.Declaration;
import tinycc.implementation.type.Type;
import tinycc.mipsasmgen.DataLabel;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MemoryInstruction;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;

public class Identifier extends PrimaryExpression {

    public Identifier(Token token) {
        super(token);
    }

    @Override
    public java.lang.String toString() {
        return "Var_" + getToken().getText();
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        // check if token was declared earlier in scope
        try {
            // grab declaration from scope
            Declaration decl = s.lookup(getToken().getText());

            // return the type of the declaration
            return decl.getExternalDeclaration().getType();
        } catch (Exception e) {
            // identifier not declared
            d.printError(getToken(), "identifier not declared " + getToken().getText());
            return null;
        }
    }

    @Override
    public GPRegister generateCode(CompilationScope s, MipsAsmGen gen) {

        // name of identifier is name of variable (DataLabel)
        java.lang.String id = getToken().getText();
        Integer offset = s.lookupLocalDeclaration(id);
        DataLabel globalVar = s.lookupDataLabel(id);
        GPRegister reg = s.lookupRegister(id);

        if (offset == null) {
            // check if it is a global declaration
            if (globalVar == null) {
                if (reg == null) {
                    throw new IllegalArgumentException("identifier " + getToken().getText() + " not found");
                }
            }
        }

        // grab next free register and reserve it
        GPRegister varReg = s.getNextFreeTempRegister();

        if (offset != null) {
            // generate instruction and return register
            gen.emitInstruction(MemoryInstruction.LW, varReg, null, offset, GPRegister.SP);
        }
        else if (globalVar != null) {
            // store value from right register into addr register
            gen.emitInstruction(MemoryInstruction.LW, varReg, globalVar, 0, null);
        }
        else if (reg != null) {
            // free varReg since it is not needed
            try {
                s.remove(varReg);
            } catch (Exception e) {
            }

            // return reg
            return reg;
        }
        else {
            throw new IllegalArgumentException("Should not reach that");
        }

        return varReg;
    }

    @Override
    public boolean isLValue() {
        return true;
    } 
}
