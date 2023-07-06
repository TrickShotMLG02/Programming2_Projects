package tinycc.implementation.statement.Statements;

import java.util.Arrays;
import java.util.List;

import tinycc.diagnostic.Diagnostic;
import tinycc.diagnostic.Locatable;
import tinycc.implementation.Scope;
import tinycc.implementation.statement.Statement;

public class Block extends Statement{
    
    Locatable loc;
    List<Statement> body;

    public Block(Locatable loc, List<Statement> statements) {
        this.loc = loc;
        this.body = statements;
    }

    @Override
    public String toString() {
        return "Block" + Arrays.toString(body.toArray());
    }

    @Override
    public void checkType(Diagnostic d, Scope parent) {
        Scope scope = parent.newNestedScope();
        // local variables are added by declaration statements
        // in the statement list that constitutes the block's body
        for (Statement s : body)
            s.checkType(d, scope);
    }
}
