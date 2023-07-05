package tinycc.implementation.statement.Statements;

import tinycc.diagnostic.Locatable;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.statement.Statement;

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
    
}
