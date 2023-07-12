package tinycc.implementation.expression.PrimaryExpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.CompilationScope;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.PrimaryExpression;
import tinycc.implementation.type.Type;
import tinycc.implementation.type.BaseTypes.Int;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.ImmediateInstruction;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;

public class Number extends PrimaryExpression {
    
    public Number(Token token) {
        super(token);
    }

    @Override
    public java.lang.String toString() {
        return super.toString() + getToken().getText();
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        return new Int();
    }

    @Override
    public boolean isNullPointer() {
        return getToken().getText().equals("0");
    }

    @Override
    public GPRegister generateCode(CompilationScope s, MipsAsmGen gen) {
        // get next free register and reserve it
        GPRegister tmp = s.getNextFreeTempRegister();

        int num = Integer.parseInt(getToken().getText());
        gen.emitInstruction(ImmediateInstruction.ADDIU, tmp, GPRegister.ZERO, num);
        
        return tmp;
    }
}
