package tinycc.implementation.expression.BinaryExpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.CompilationScope;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.BinaryExpression;
import tinycc.implementation.expression.BinaryOperator;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.type.Type;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MemoryInstruction;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;

public class AssignExpression extends BinaryExpression {

    public AssignExpression(Token token, BinaryOperator operator, Expression left, Expression right) {
        super(token, operator, left, right);
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        Type typeLeft = getLeft().checkType(d, s);
        Type typeRight = getRight().checkType(d, s);

        if (!typeRight.isScalarType()) {
            d.printError(getToken(), "Not a scalar type");
        }

        if (!typeLeft.isScalarType()) {
            d.printError(getToken(), "Not a scalar type");
        }

        // conditions from project description (page 6) assignments
        boolean identical = typeLeft.equals(typeRight);
        boolean intergerTypes = typeLeft.isIntegerType() && typeRight.isIntegerType();
        boolean bothPointersMinOneVoid = (typeLeft.isPointerType() && typeRight.isPointerType()) && (typeLeft.isVoidPointer() || typeRight.isVoidPointer());
        boolean leftPointerRightNullPointer = typeLeft.isPointerType() && getRight().isNullPointer();

        // left type must be lValue
        if (!getLeft().isLValue()) {
            d.printError(getToken(), "Left type not a L-Value");
        }

        // check if any condition is satisfied
        if (!identical && !intergerTypes && !bothPointersMinOneVoid && !leftPointerRightNullPointer) {
            d.printError(getToken(), "invalid assignment");
        }

        // check if any condition is true -> return always left type
        if (identical ||intergerTypes || bothPointersMinOneVoid || leftPointerRightNullPointer) {
            return typeLeft;
        }

        // should not reach this code
        d.printError(getToken(), "This should not be reached since it is covered from above statements");
        return null;
    }

    @Override
    public GPRegister generateCode(CompilationScope s, MipsAsmGen gen) {
        // TODO: fix me, since there is no case distinction for expressions or not
        // TODO: for that, store type from checkSemantics insode the expressions or something like that
        // since i want to be able to determine, if i have to generate the code for the right side
        // or if i can lookup an identifier
        // or if i have a variable

        // left expression can be an identifier, thus look it up and extract stack offset
        Integer offset = s.lookupLocalDeclaration(getLeft().getToken().getText());

        if (offset == null)
            throw new IllegalArgumentException("identifier " + getLeft().getToken().getText() + " not found in scope");
        
        // grab right expression and add/load to/from scope
        GPRegister right = getRight().generateCode(s, gen);

        // generate sw instruction of right register into stack
        gen.emitInstruction(MemoryInstruction.SW, right, null, offset, GPRegister.SP);

        try {
            // free register
            s.remove(right);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    } 
}
