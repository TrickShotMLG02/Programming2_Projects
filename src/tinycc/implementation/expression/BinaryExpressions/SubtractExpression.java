package tinycc.implementation.expression.BinaryExpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.CompilationScope;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.BinaryExpression;
import tinycc.implementation.expression.BinaryOperator;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.type.PointerType;
import tinycc.implementation.type.Type;
import tinycc.implementation.type.BaseTypes.Int;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.mipsasmgen.RegisterInstruction;
import tinycc.parser.Token;

public class SubtractExpression extends BinaryExpression{

    public SubtractExpression(Token token, BinaryOperator operator, Expression left, Expression right) {
        super(token, operator, left, right);
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        Type typeLeft = getLeft().checkType(d, s);
        Type typeRight = getRight().checkType(d, s);

        if (!typeLeft.isIntegerType() && !typeLeft.isPointerType()) {
            d.printError(getToken(), "Subtract - type left invalid: " + typeLeft + ", " + typeRight);
        }
        if (!typeRight.isIntegerType() && !typeRight.isPointerType()) {
            d.printError(getToken(), "Subtract - type right invalid: " + typeLeft + ", " + typeRight);
        }
        
        // check which rule applies
        if (typeLeft.isIntegerType() && typeRight.isIntegerType()) {
            return new Int();
        }

        if (typeLeft.isPointerType() && typeRight.isIntegerType()) {
            // return new pointer
            return new PointerType(getLeft().checkType(d, s));
        }

        if (typeLeft.isPointerType() && typeRight.isPointerType()) {
            PointerType pTypeLeft = (PointerType) typeLeft;
            PointerType pTypeRight = (PointerType) typeRight;
            
            // check if pointer types are identical and they point to complete type
            if (pTypeLeft.equals(pTypeRight) && pTypeLeft.isComplete()) {
                return new Int();
            }

            // otherwise print error since types not identical
            d.printError(getToken(), "Pointers not complete or not equal");
        }

        // shouldn't reach this case
        // print error here
        d.printError(getToken(), "will be null");
        return null;     
    }

    @Override
    public GPRegister generateCode(CompilationScope s, MipsAsmGen gen) {
        // generate code of left expression and right expression
        GPRegister left = getLeft().generateCode(s, gen);
        GPRegister right = getRight().generateCode(s, gen);

        // generate add instruction of left and right register
        gen.emitInstruction(RegisterInstruction.SUB, left, left, right);

        try {
            // free right register
            s.remove(right);
        } catch (Exception e) {
        }

        // return register of left expr
        return left;
    } 
}
