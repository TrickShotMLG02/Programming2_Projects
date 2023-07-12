package tinycc.implementation.TopLevelConstructs.ExternalDeclarations;

import tinycc.implementation.CompilationScope;
import tinycc.implementation.TopLevelConstructs.ExternalDeclaration;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.type.Type;
import tinycc.mipsasmgen.DataLabel;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MemoryInstruction;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.mipsasmgen.TextLabel;
import tinycc.parser.Token;

public class GlobalVariable extends ExternalDeclaration {

    public GlobalVariable(Type type, Token name) {
        this(type, name, null);
    }

    public GlobalVariable(Type type, Token name, Expression init) {
        super(type, name, init);
    }
    
    @Override
    public GPRegister generateCode(CompilationScope s, MipsAsmGen gen) {
        
        // TODO: Store the data label somewhere in the scope, since i have no register for it

        try {
            // create label with name of variable
            DataLabel lbl = gen.makeDataLabel(getToken().getText());
            // add data label to scope
            s.addDataLabel(lbl);
            // emit data label with default value 0
            gen.emitWord(lbl, 0);

            // check if init expression exists
            if (getInitExpression() != null) {
                // create label for code of init expression
                TextLabel initLbl = gen.makeUniqueTextLabel();
                gen.emitLabel(initLbl);

                // grab register containing init expression
                GPRegister initReg = getInitExpression().generateCode(s, gen);

                // store value as word to variable
                gen.emitInstruction(MemoryInstruction.SW, initReg, lbl, 0, null);
        }
        } catch (Exception e) {
            // label already declared
            e.printStackTrace();
        }

        // TODO: check return value
        return null;
    }

    @Override
    public boolean isFunction() {
        return false;
    }

    @Override
    public boolean isFunctionDeclaration() {
        return false;
    }

    @Override
    public boolean isGlobalVariable() {
        return true;
    }
}
