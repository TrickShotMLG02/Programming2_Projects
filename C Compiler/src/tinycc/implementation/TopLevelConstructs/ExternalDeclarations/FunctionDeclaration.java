package tinycc.implementation.TopLevelConstructs.ExternalDeclarations;

import tinycc.implementation.CompilationScope;
import tinycc.implementation.TopLevelConstructs.ExternalDeclaration;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.type.Type;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;

public class FunctionDeclaration extends ExternalDeclaration {

    public FunctionDeclaration(Type type, Token name) {
        this(type, name, null);
    }

    public FunctionDeclaration(Type type, Token name, Expression init) {
        super(type, name, init);
    }
    
    @Override
    public GPRegister generateCode(CompilationScope s, MipsAsmGen gen) {
        throw new UnsupportedOperationException("Unimplemented method 'generateCode'");
    }

    @Override
    public boolean isFunction() {
        return false;
    }

    @Override
    public boolean isFunctionDeclaration() {
        return true;
    }

    @Override
    public boolean isGlobalVariable() {
        return false;
    }
}
