package tinycc.implementation.statement.Statements;

import tinycc.diagnostic.Diagnostic;
import tinycc.diagnostic.Locatable;
import tinycc.implementation.CompilationScope;
import tinycc.implementation.Scope;
import tinycc.implementation.TopLevelConstructs.ExternalDeclaration;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.statement.Statement;
import tinycc.implementation.type.Type;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.ImmediateInstruction;
import tinycc.mipsasmgen.JumpRegisterInstruction;
import tinycc.mipsasmgen.MipsAsmGen;

public class Return extends Statement{

    Locatable loc;
    Expression exp;

    public Return(Locatable loc, Expression expression) {
        this.loc = loc;
        this.exp = expression;
    }

    @Override
    public String toString() {
        if (exp == null)
            return "Return[]";
        else
            return "Return[" + exp + "]"; 
    }

    @Override
    public void checkType(Diagnostic d, Scope s, ExternalDeclaration f) {
        
        // check type of expression if it exists
        if (exp != null) {
            // grab types for comparison
            Type returnExpType = exp.checkType(d, s);
            Type funType = f.getType();

            // check if types are not equal and type of exp is not compatible with function type
            if (!returnExpType.equals(funType) || (!returnExpType.isIntegerType() && !funType.isIntegerType())) {
                d.printError(exp.getToken(), "invalid return type");
            }

        }
        else {
            // grab type of function
            Type funType = f.getType();

            // check if nothing is returned, even if function is not of type void
            if (!funType.isVoidType()) {
                d.printError(loc, "There should be nothing to return");
            }
        }
    }
    
    @Override
    public void generateCode(CompilationScope s, MipsAsmGen gen) {

        // TODO: maybe add register as used or so

        // check if there is a return value
        if (exp != null) {
            // grab register of expression code generation
            GPRegister resReg = exp.generateCode(s, gen);

            // copy value from old register to new one
            gen.emitInstruction(ImmediateInstruction.ADDIU, GPRegister.V0, resReg, 0);
        }

        // restore callee save registers
        s.restoreCalleeSaveRegisters();

        // jump to return address
        gen.emitInstruction(JumpRegisterInstruction.JR , GPRegister.RA);
    }
}
