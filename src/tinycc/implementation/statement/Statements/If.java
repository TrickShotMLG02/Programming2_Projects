package tinycc.implementation.statement.Statements;

import tinycc.diagnostic.Diagnostic;
import tinycc.diagnostic.Locatable;
import tinycc.implementation.CompilationScope;
import tinycc.implementation.Scope;
import tinycc.implementation.TopLevelConstructs.ExternalDeclaration;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.statement.Statement;
import tinycc.mipsasmgen.BranchInstruction;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.JumpInstruction;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.mipsasmgen.TextLabel;

public class If extends Statement{

    Locatable loc;
    Expression condition;
    Statement consequence;
    Statement alternative;

    public If(Locatable loc, Expression condition, Statement consequence, Statement alternative) {
        this.loc = loc;
        this.condition = condition;
        this.consequence = consequence;
        this.alternative = alternative;
    }

    @Override
    public String toString() {
        if (alternative == null)
            return "If[" + condition + "," + consequence + "]";
        else
            return "If[" + condition + "," + consequence + "," + alternative +"]";
    }

    @Override
    public void checkType(Diagnostic d, Scope s, ExternalDeclaration f) {

        // check that condition is integertype
        if (!condition.checkType(d, s).isIntegerType()) {
            // print error since invalid condition type
            d.printError(loc, "invalid condition");
        }

        consequence.checkType(d, s, f);

        // check if alternative exists
        if (alternative != null)
            alternative.checkType(d, s, f);
    }
    
    @Override
    public void generateCode(CompilationScope s, MipsAsmGen gen) {

        // generate code for condition and save the register
        GPRegister conditionReg = condition.generateCode(s, gen);

        // create new label for consequence code
        TextLabel consequenceLabel = gen.makeUniqueTextLabel();
        gen.emitLabel(consequenceLabel);

        // generate code of consequence branch under that label
        consequence.generateCode(s, gen);

        // create branch instruction to check if condition is not equal to 0 (thus equal to 1)
        gen.emitInstruction(BranchInstruction.BNE, conditionReg, GPRegister.ZERO, consequenceLabel);

        // check if alternative exists
        if (alternative != null) {
            // create new label for alternative code
            TextLabel alternativeLabel = gen.makeUniqueTextLabel();
            gen.emitLabel(alternativeLabel);
            
            // generate code of alternative branch under that label
            alternative.generateCode(s, gen);

            // create code to jump to the alternative label
            gen.emitInstruction(JumpInstruction.J, alternativeLabel);
        }
    }
}
