package tinycc.implementation.expression.UnaryExpressions;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.CompilationScope;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.expression.UnaryExpression;
import tinycc.implementation.expression.UnaryOperator;
import tinycc.implementation.type.PointerType;
import tinycc.implementation.type.Type;
import tinycc.implementation.type.BaseTypes.Char;
import tinycc.implementation.type.BaseTypes.Int;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;

public class SizeOfExpression extends UnaryExpression {

    public SizeOfExpression(Token token, UnaryOperator operator, Expression exp) {
        super(token, operator, exp);
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {
        // grab the type of the expression
        Type applicableType = getExpression().checkType(d, s);

        // check if the type is a Object Type and complete
        if (applicableType.isObjectType() && applicableType.isComplete()) {

            // check if type is a pointer
            if (applicableType.isPointerType()) {
                // typecast expression to pointer and check its type
                PointerType pType = (PointerType) applicableType;

                // check if it is a pointer to char, print error
                if (pType.getPointerType() instanceof Char) {
                    d.printError(getToken(), "Type was string literal");
                }
            }

            // return integer type sinze sizeof is always int
            return new Int();
        }
        // check if it is a string literal (a pointer to a char)
        else if (applicableType.isPointerType()) {
            // typecast expression to pointer and check its type
            PointerType pType = (PointerType) applicableType;

            // check if it is a pointer to char, print error
            if (pType.getPointerType() instanceof Char) {
                return new Int();
            }

            d.printError(getToken(), "not a string literal");
            return null;
        } else {
            d.printError(getToken(), "Invalid type of expression");
            return null;
        }
    }

    @Override
    public void generateCode(CompilationScope s, MipsAsmGen gen) {
        throw new UnsupportedOperationException("Unimplemented method 'generateCode'");
    }
}
