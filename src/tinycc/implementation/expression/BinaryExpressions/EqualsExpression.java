package tinycc.implementation.expression.BinaryExpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.CompilationScope;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.BinaryExpression;
import tinycc.implementation.expression.BinaryOperator;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.type.Type;
import tinycc.implementation.type.BaseTypes.Int;
import tinycc.mipsasmgen.MipsAsmGen;
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
            d.printError(getLeft().getToken(), "type left invalid");
        }
        
        if (!typeRight.isIntegerType() && !typeRight.isPointerType()) {
            d.printError(getRight().getToken(), "type right invalid");
        }

        // check if both types are integer types
        if (typeLeft.isIntegerType() && typeRight.isIntegerType()) {
            return new Int();
        }

        // check if pointers types and they are equal
        if (typeLeft.isPointerType() && typeRight.isPointerType() && typeLeft.equals(typeRight)) {
            return new Int();
        }

        // check for one void pointer and a pointer
        if (typeLeft.isVoidPointer() && typeRight.isPointerType() || typeLeft.isPointerType() && typeRight.isVoidPointer()) {
            return new Int();
        }

        // check for one null pointer and a pointer
        if (getLeft().isNullPointer() && typeRight.isPointerType() || typeLeft.isPointerType() && getRight().isNullPointer()) {
            return new Int();
        }

        // print error
        d.printError(getToken(), "Invalid types");
        return null;
    }

    @Override
    public void generateCode(CompilationScope s, MipsAsmGen gen) {
        throw new UnsupportedOperationException("Unimplemented method 'generateCode'");
    } 
}
