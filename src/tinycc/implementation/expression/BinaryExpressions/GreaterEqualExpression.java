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

public class GreaterEqualExpression extends BinaryExpression {

    public GreaterEqualExpression(Token token, BinaryOperator operator, Expression left, Expression right) {
        super(token, operator, left, right);
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        Type typeLeft = getLeft().checkType(d, s);
        Type typeRight = getRight().checkType(d, s);

         if (!typeLeft.isIntegerType() && !typeLeft.isPointerType()) {
            d.printError(getToken(), "GreaterEqual - type left invalid: " + typeLeft + ", " + typeRight);
        }
        if (!typeRight.isIntegerType() && !typeRight.isPointerType()) {
            d.printError(getToken(), "GreaterEqual - type right invalid: " + typeLeft + ", " + typeRight);
        }

        // check for type equality (pointer, pointer or int, int)
        if (typeLeft.equals(typeRight)) {
            return new Int();
        }

        // otherwise print error since types not identical
        d.printError(getToken(), "will be null");
        return null;
    }
    
    @Override
    public GPRegister generateCode(CompilationScope s, MipsAsmGen gen) {
        GPRegister leftReg = getLeft().generateCode(s, gen);
        GPRegister rightReg = getRight().generateCode(s, gen);

        GPRegister compResult = s.getNextFreeTempRegister();

        // check if right is smaller than left, since then left is greater/equal to right
        gen.emitInstruction(RegisterInstruction.SLT, compResult, rightReg, leftReg);

        return compResult;
    }
}
