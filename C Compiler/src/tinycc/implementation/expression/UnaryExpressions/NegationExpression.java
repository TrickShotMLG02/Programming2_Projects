package tinycc.implementation.expression.UnaryExpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.CompilationScope;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.expression.UnaryExpression;
import tinycc.implementation.expression.UnaryOperator;
import tinycc.implementation.type.Type;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;

public class NegationExpression extends UnaryExpression {

    public NegationExpression(Token token, UnaryOperator operator, Expression exp) {
        super(token, operator, exp);
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        // TODO Bonus task 3b
        throw new UnsupportedOperationException("Unimplemented method 'checkType'");
    }

    @Override
    public GPRegister generateCode(CompilationScope s, MipsAsmGen gen) {
        throw new UnsupportedOperationException("Unimplemented method 'generateCode'");
    } 
}
