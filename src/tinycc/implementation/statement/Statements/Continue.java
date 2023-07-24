package tinycc.implementation.statement.Statements;

import tinycc.diagnostic.Diagnostic;
import tinycc.diagnostic.Locatable;
import tinycc.implementation.CompilationScope;
import tinycc.implementation.Scope;
import tinycc.implementation.TopLevelConstructs.ExternalDeclaration;
import tinycc.implementation.statement.Statement;
import tinycc.mipsasmgen.MipsAsmGen;

public class Continue extends Statement {

    Locatable loc;

    public Continue(Locatable loc) {
        this.loc = loc;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'toString'");
    }

    @Override
    public void checkType(Diagnostic d, Scope s, ExternalDeclaration f) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'checkType'");
    }

    @Override
    public void generateCode(CompilationScope s, MipsAsmGen gen) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateCode'");
    }
    
}
