package tinycc.implementation.expression.UnaryExpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.CompilationScope;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.expression.UnaryExpression;
import tinycc.implementation.expression.UnaryOperator;
import tinycc.implementation.type.PointerType;
import tinycc.implementation.type.Type;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MemoryInstruction;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;

public class IndirectionExpression extends UnaryExpression {

    public IndirectionExpression(Token token, UnaryOperator operator, Expression exp) {
        super(token, operator, exp);
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        // grab the type of the expression
        Type applicableType = getExpression().checkType(d, s);

        // check if it is a voidpointer
        if (applicableType.isVoidPointer()) {
            d.printError(getToken(), "Cannot dereference a void pointer");
        }

        // check if the type is a pointer
        if (applicableType.isPointerType() && applicableType.isComplete()) {
            // extract the type to which the pointer points to
            PointerType pType = (PointerType) applicableType;

            // return the type
            this.type = pType.getPointerType();
            return this.type;
        }
        else {
            d.printError(getToken(), "Not a pointer or not compete type");
            return null;
        }
    }

    @Override
    public GPRegister generateCode(CompilationScope s, MipsAsmGen gen) {
        // get offset of declaration
        Integer offset = s.lookupLocalDeclaration(getExpression().getToken().getText());

        // set offset to currentStackOffset if not found in stack
        offset = offset == null ? s.getStackOffset() : offset;

        // get register of the expression
        GPRegister reg = getExpression().generateCode(s, gen);

        // emit instruction to load from stack pointer
        gen.emitInstruction(MemoryInstruction.LW, reg, null, offset, GPRegister.SP);

        // return the reg
        return reg;
    } 

    @Override
    public boolean isLValue() {
        return true;
    }
}