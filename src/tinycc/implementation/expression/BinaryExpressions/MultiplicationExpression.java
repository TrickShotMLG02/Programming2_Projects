package tinycc.implementation.expression.BinaryExpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.CompilationScope;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.BinaryExpression;
import tinycc.implementation.expression.BinaryOperator;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.type.Type;
import tinycc.implementation.type.BaseTypes.Int;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.mipsasmgen.RegisterInstruction;
import tinycc.parser.Token;

public class MultiplicationExpression extends BinaryExpression {

    public MultiplicationExpression(Token token, BinaryOperator operator, Expression left, Expression right) {
        super(token, operator, left, right);
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        Type typeLeft = getLeft().checkType(d, s);
        Type typeRight = getRight().checkType(d, s);

        if (!typeLeft.isIntegerType() || !typeRight.isIntegerType()) {
            d.printError(getToken(), "invalid types");
        }

        return new Int();
    }

    @Override
    public GPRegister generateCode(CompilationScope s, MipsAsmGen gen) {
        // generate code of left expression and right expression
        GPRegister left = getLeft().generateCode(s, gen);
        GPRegister right = getRight().generateCode(s, gen);

        // generate add instruction of left and right register
        gen.emitInstruction(RegisterInstruction.MUL, left, left, right);

        try {
            // free right register
            s.remove(right);
        } catch (Exception e) {
        }

        // return register of left expr
        return left;
    } 
}
