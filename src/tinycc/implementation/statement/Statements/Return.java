package tinycc.implementation.statement.Statements;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.Scope;
import tinycc.implementation.TopLevelConstructs.ExternalDeclaration;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.statement.Statement;

public class Return extends Statement{

    Expression exp;

    public Return(Expression expression) {
        this.exp = expression;
    }

    @Override
    public String toString() {
        if (exp == null)
            return "Return[]";
        else
            return "Return[" + exp + "]"; 
    }

    @Override
    public void checkType(Diagnostic d, Scope s, ExternalDeclaration f) {
        
        // check type of expression if it exists
        if (exp != null)
            exp.checkType(d, s);
    }
    
}
