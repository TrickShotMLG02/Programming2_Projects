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
import tinycc.parser.Token;

public class LessEqualExpression extends BinaryExpression {

    public LessEqualExpression(Token token, BinaryOperator operator, Expression left, Expression right) {
        super(token, operator, left, right);
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        Type typeLeft = getLeft().checkType(d, s);
        Type typeRight = getRight().checkType(d, s);

         if (!typeLeft.isIntegerType() && !typeLeft.isPointerType()) {
            d.printError(getToken(), "tLessEqual - ype left invalid" + typeLeft + ", " + typeRight);
        }
        if (!typeRight.isIntegerType() && !typeRight.isPointerType()) {
            d.printError(getToken(), "LessEqual - type right invalid" + typeLeft + ", " + typeRight);
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
        throw new UnsupportedOperationException("Unimplemented method 'generateCode'");
    } 
}
