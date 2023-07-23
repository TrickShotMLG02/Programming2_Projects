package tinycc.implementation.expression.BinaryExpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.CompilationScope;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.BinaryExpression;
import tinycc.implementation.expression.BinaryOperator;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.type.Type;
import tinycc.implementation.type.BaseTypes.Int;
import tinycc.mipsasmgen.BranchInstruction;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.ImmediateInstruction;
import tinycc.mipsasmgen.JumpInstruction;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.mipsasmgen.RegisterInstruction;
import tinycc.mipsasmgen.TextLabel;
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
            this.type = new Int();
        }

        return this.type;
    }

    @Override
    public GPRegister generateCode(CompilationScope s, MipsAsmGen gen) {
        GPRegister leftReg = getLeft().generateCode(s, gen);
        GPRegister rightReg = getRight().generateCode(s, gen);

        GPRegister compResult1 = s.getNextFreeTempRegister();
        GPRegister compResult2 = s.getNextFreeTempRegister();

        // check if left is less than right -> 
        // or
        // left is equal to right

        // check if left is less than right -> store result in compResult1
        gen.emitInstruction(RegisterInstruction.SLT, compResult1, leftReg, rightReg);

        // check if left unequal right and store result in compResult2
        TextLabel lblTrue = gen.makeUniqueTextLabel("_LESS_EQUAL_EXPRESSION_UNEQUAL");
        TextLabel lblFalse = gen.makeUniqueTextLabel("_LESS_EQUAL_EXPRESSION_EQUAL");
        TextLabel lblExit = gen.makeUniqueTextLabel("_LESS_EQUAL_EXPRESSION_EXIT");

        gen.emitInstruction(BranchInstruction.BEQ, leftReg, rightReg, lblTrue);
        gen.emitInstruction(JumpInstruction.J, lblFalse);

        gen.emitLabel(lblTrue);
        gen.emitInstruction(ImmediateInstruction.ADDI, compResult2, GPRegister.ZERO, 1);
        gen.emitInstruction(JumpInstruction.J, lblExit);

        gen.emitLabel(lblFalse);
        gen.emitInstruction(ImmediateInstruction.ADDI, compResult2, GPRegister.ZERO, 0);
        gen.emitInstruction(JumpInstruction.J, lblExit);

        // return compResult1 and compResult2
        gen.emitLabel(lblExit);
        gen.emitInstruction(RegisterInstruction.OR, compResult1, compResult1, compResult2);

        try {
            // free up reg 2
            s.remove(compResult2);
        } catch (Exception e) {
        }

        return compResult1;
    } 
}
