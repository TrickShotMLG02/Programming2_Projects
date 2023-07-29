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
import tinycc.mipsasmgen.TextLabel;
import tinycc.parser.Token;

public class EqualsExpression extends BinaryExpression {

    public EqualsExpression(Token token, BinaryOperator operator, Expression left, Expression right) {
        super(token, operator, left, right);
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        Type typeLeft = getLeft().checkType(d, s);
        Type typeRight = getRight().checkType(d, s);

        if (!typeLeft.isIntegerType() && !typeLeft.isPointerType()) {
            d.printError(getToken(), "Equal - type left invalid: " + typeLeft + ", " + typeRight);
        }
        
        if (!typeRight.isIntegerType() && !typeRight.isPointerType()) {
            d.printError(getToken(), "Equal - type right invalid: " + typeLeft + ", " + typeRight);
        }

        // check if both types are integer types
        if (typeLeft.isIntegerType() && typeRight.isIntegerType()) {
            this.type = new Int();
        }

        // check if pointers types and they are equal
        else if (typeLeft.isPointerType() && typeRight.isPointerType() && typeLeft.equals(typeRight)) {
            this.type = new Int();
        }

        // check for one void pointer and a pointer
        else if (typeLeft.isVoidPointer() && typeRight.isPointerType() || typeLeft.isPointerType() && typeRight.isVoidPointer()) {
            this.type = new Int();
        }

        // check for one null pointer and a pointer
        else if (getLeft().isNullPointer() && typeRight.isPointerType() || typeLeft.isPointerType() && getRight().isNullPointer()) {
            this.type = new Int();
        }

        return this.type;
    }

    @Override
    public GPRegister generateCode(CompilationScope s, MipsAsmGen gen) {
        GPRegister leftReg = getLeft().generateCode(s, gen);
        GPRegister rightReg = getRight().generateCode(s, gen);

        GPRegister compResult = s.getNextFreeTempRegister();

        // create labels for true and false and exit
        TextLabel lblTrue = gen.makeUniqueTextLabel("_EQUALS_TRUE");
        TextLabel lblFalse = gen.makeUniqueTextLabel("_EQUALS_FALSE");
        TextLabel lblExit = gen.makeUniqueTextLabel("_EQUALS_EXIT");

        // check if left and right are equal
        gen.emitInstruction(BranchInstruction.BEQ, leftReg, rightReg, lblTrue);
        gen.emitInstruction(JumpInstruction.J, lblFalse);

        // create code for true
        gen.emitLabel(lblTrue);
        gen.emitInstruction(ImmediateInstruction.ADDIU, compResult, GPRegister.ZERO, 1);
        gen.emitInstruction(JumpInstruction.J, lblExit);

        // create code for false
        gen.emitLabel(lblFalse);
        gen.emitInstruction(ImmediateInstruction.ADDIU, compResult, GPRegister.ZERO, 0);
        gen.emitInstruction(JumpInstruction.J, lblExit);

        // emit label for exit
        gen.emitLabel(lblExit);

        return compResult;
    } 
}
