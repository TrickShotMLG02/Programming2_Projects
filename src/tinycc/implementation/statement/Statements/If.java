package tinycc.implementation.statement.Statements;

import tinycc.diagnostic.Diagnostic;
import tinycc.diagnostic.Locatable;
import tinycc.implementation.CompilationScope;
import tinycc.implementation.Scope;
import tinycc.implementation.TopLevelConstructs.ExternalDeclaration;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.statement.Statement;
import tinycc.mipsasmgen.MipsAsmGen;

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
        throw new UnsupportedOperationException("Unimplemented method 'generateCode'");
    }
}
