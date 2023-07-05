package tinycc.implementation.statement.Statements;

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
    
}
