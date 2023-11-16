package tinycc.implementation.expression.PrimaryExpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.CompilationScope;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.PrimaryExpression;
import tinycc.implementation.type.PointerType;
import tinycc.implementation.type.Type;
import tinycc.implementation.type.BaseTypes.Char;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;

public class String extends PrimaryExpression {
    
    public String(Token token) {
        super(token);
    }

    @Override
    public java.lang.String toString() {
        return super.toString() + "\"" + getToken().getText() + "\"";
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        this.type = new PointerType(new Char());
        return this.type;
    }

    @Override
    public GPRegister generateCode(CompilationScope s, MipsAsmGen gen) {
        throw new UnsupportedOperationException("Unimplemented method 'generateCode'");
    }
}