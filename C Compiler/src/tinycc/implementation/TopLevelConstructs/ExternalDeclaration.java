package tinycc.implementation.TopLevelConstructs;

import tinycc.implementation.CompilationScope;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.type.Type;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;

public abstract class ExternalDeclaration {
    
    Type type;
    Token name;
    Expression init;

    public ExternalDeclaration(Type type, Token name, Expression init) {
        this.type = type;
        this.name = name;
        this.init = init;
    }

    public abstract GPRegister generateCode(CompilationScope s, MipsAsmGen gen);

    @Override
    public String toString() {
        if (init == null)
            return "Declaration_" + name.getText() + "[" + type + "]";
        else
            return "Declaration_" + name.getText() + "[" + type + "," + init + "]";
    }

    public Type getType() {
        return type;
    }

    public Token getToken() {
        return name;
    }

    public Expression getInitExpression() {
        return init;
    }

    public boolean isFunction() {
        return false;
    }

    public boolean isFunctionDeclaration() {
        return false;
    }

    public boolean isGlobalVariable() {
        return false;
    }

}
