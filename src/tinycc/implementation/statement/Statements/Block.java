package tinycc.implementation.statement.Statements;

import java.util.Arrays;
import java.util.List;

import tinycc.diagnostic.Locatable;
import tinycc.implementation.statement.Statement;

public class Block extends Statement{
    
    Locatable loc;
    List<Statement> statements;

    public Block(Locatable loc, List<Statement> statements) {
        this.loc = loc;
        this.statements = statements;
    }

    @Override
    public String toString() {
        return "Block" + Arrays.toString(statements.toArray());
    }
}
