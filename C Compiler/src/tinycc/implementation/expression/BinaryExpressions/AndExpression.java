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

public class AndExpression extends BinaryExpression {

    /**
     * Bonus task
     * @param operator
     * @param left
     * @param right
     */
    public AndExpression(Token token, BinaryOperator operator, Expression left, Expression right) {
        super(token, operator, left, right);
    }   

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        Type typeLeft = getLeft().checkType(d, s);
        Type typeRight = getRight().checkType(d, s);

        if (!typeLeft.isScalarType()) {
            d.printError(getLeft().getToken(), "type left not a scalar type");
        }
        
        if (!typeRight.isScalarType()) {
            d.printError(getRight().getToken(), "type right not a scalar type");
        }
        
        this.type = new Int();
        return this.type;
    }

    @Override
    public GPRegister generateCode(CompilationScope s, MipsAsmGen gen) {
        // generate code of left expression and right expression
        GPRegister left = getLeft().generateCode(s, gen);
        GPRegister right = getRight().generateCode(s, gen);

        // generate and instruction of left and right register
        gen.emitInstruction(RegisterInstruction.AND, left, left, right);

        try {
            // free right register
            s.remove(right);
        } catch (Exception e) {
        }

        // return register of left expr
        return left;
    } 
}