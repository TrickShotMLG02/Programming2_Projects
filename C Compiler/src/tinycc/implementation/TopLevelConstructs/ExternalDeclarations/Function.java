package tinycc.implementation.TopLevelConstructs.ExternalDeclarations;

import java.util.List;

import tinycc.implementation.CompilationScope;
import tinycc.implementation.TopLevelConstructs.ExternalDeclaration;
import tinycc.implementation.statement.Statement;
import tinycc.implementation.type.Type;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;

public class Function extends ExternalDeclaration {
    private List<Token> parameterNames;
    private Statement body;

    public Function(Type type, Token name, List<Token> parameterNames, Statement body) {
        super(type, name, null);
        this.parameterNames = parameterNames;
        this.body = body;
    }

    @Override
    public GPRegister generateCode(CompilationScope s, MipsAsmGen gen) {
        throw new UnsupportedOperationException("Unimplemented method 'generateCode'");
    }

    @Override
    public String toString() {
        return getType().toString();
    }

    public List<Token> getParameterNames() {
        return parameterNames;
    }

    public Statement getBody() {
        return body;
    }

    @Override
    public boolean isFunction() {
        return true;
    }

    @Override
    public boolean isFunctionDeclaration() {
        return false;
    }

    @Override
    public boolean isGlobalVariable() {
        return false;
    }
    

    
}
