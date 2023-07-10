package tinycc.implementation.expression.PrimaryExpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.PrimaryExpression;
import tinycc.implementation.type.Type;
import tinycc.implementation.type.BaseTypes.Char;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;

public class Character extends PrimaryExpression {
    public Character(Token token) {
        super(token);
    }

    @Override
    public java.lang.String toString() {
        return super.toString() + "'" + getToken().getText() + "'";
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        return new Char();
    }

    @Override
    public void generateCode(Scope s, MipsAsmGen gen) {
        throw new UnsupportedOperationException("Unimplemented method 'generateCode'");
    }    
}
