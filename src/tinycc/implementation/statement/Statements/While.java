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

public class While extends Statement{

    Locatable loc;
    Expression condition;
    Statement body;

    public While(Locatable loc, Expression condition, Statement body) {
        this.loc = loc;
        this.condition = condition;
        this.body = body;
    }


    @Override
    public String toString() {
        return "While[" + condition + "," + body + "]";
    }


    @Override
    public void checkType(Diagnostic d, Scope scope, ExternalDeclaration f) {

        // check that type of expression is integertype
        if (!condition.checkType(d, scope).isIntegerType()) {
            // print error message since condition has invalid type
            d.printError(loc, "invalid condition type");
        }

        body.checkType(d, scope, f);
    }


    @Override
    public void generateCode(CompilationScope s, MipsAsmGen gen) {

        // create loop header label
        TextLabel loopLabel = gen.makeUniqueTextLabel("WHILE_LOOP_START");
        gen.emitLabel(loopLabel);

        // create loop exit label
        TextLabel exitLabel = gen.makeUniqueTextLabel("WHILE_LOOP_EXIT");

        // generate code for condition and save register
        TextLabel loopConditionPrepLabel = gen.makeUniqueTextLabel("WHILE_LOOP_CONDITION_PREP");
        gen.emitLabel(loopConditionPrepLabel);
        GPRegister conditionReg = condition.generateCode(s, gen);

        // create branch instruction to exit label, if condition false
        gen.emitInstruction(BranchInstruction.BEQ, conditionReg, GPRegister.ZERO, exitLabel);

        // generate code for body
        TextLabel loopBody = gen.makeUniqueTextLabel("WHILE_LOOP_BODY");
        gen.emitLabel(loopBody);
        body.generateCode(s, gen);

        // add jump instruction to loop header label
        gen.emitInstruction(JumpInstruction.J, loopLabel);

        // add exit label for loop
        gen.emitLabel(exitLabel);
    }
}
