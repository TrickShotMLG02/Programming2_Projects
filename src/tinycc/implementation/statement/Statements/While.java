package tinycc.implementation.statement.Statements;

import tinycc.diagnostic.Diagnostic;
import tinycc.diagnostic.Locatable;
import tinycc.implementation.Scope;
import tinycc.implementation.TopLevelConstructs.ExternalDeclaration;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.statement.Statement;
import tinycc.mipsasmgen.MipsAsmGen;

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
    public void generateCode(Scope s, MipsAsmGen gen) {
        throw new UnsupportedOperationException("Unimplemented method 'generateCode'");
    }
}
