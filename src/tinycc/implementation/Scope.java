package tinycc.implementation;

import java.util.HashMap;
import java.util.Map;

import tinycc.implementation.statement.Statements.Declaration;

/**
 * Copied from Prog2 Book (https://prog2.de/book/sec-comp-tychk.html)
 */
public class Scope {
    private final Map<String, Declaration> table;
    private final Scope parent;

    /**
     * Create a new Scope with no parent (the first scope)
     */
    public Scope() {
        this(null);
    }

    /**
     * Only for internal use of newNestedScope and Scope
     * @param parent
     */
    private Scope(Scope parent) {
        this.parent = parent;
        this.table  = new HashMap<String, Declaration>();
    }

    /**
     * Create a new nested scope, which lies within the current Scope
     * @return a new nested scope
     */
    public Scope newNestedScope() {
        return new Scope(this);
    }

    /**
     * Add a new Identifier and its declaration to the current scope
     * @param id                    The identifier to add
     * @param d                     The declaration to add
     * @throws IdAlreadyDeclared    If the identifier is already declared in this scope
     */
    public void add(String id, Declaration d) throws IdAlreadyDeclared {
        if (table.containsKey(id))
            throw new IdAlreadyDeclared(id);
            table.put(id, d);
    }

    /**
     * Lookup an identifier in all parent scopes.
     * @param id                The Identifier to look up
     * @return                  The Declaration of an Identifier
     * @throws IdUndeclared     If Identifier was not found
     */
    public Declaration lookup(String id) throws IdUndeclared {

        // check if Identifier is already declared in current scope
        if (table.containsKey(id)) {
            return table.get(id);
        }

        // otherwhise check in parent scope if parent scope is not null
        if (parent != null) {
            return parent.lookup(id);
        }

        // no parent -> thus identifier was not found
        throw new IdUndeclared(id);
    }
}


/*
 * Exceptions for Scopes
 */

class IdAlreadyDeclared extends Exception {
    public IdAlreadyDeclared(String id) {
        super("Identifier "  + id + " already declared in current scope");
    }
}

class IdUndeclared extends Exception {
    public IdUndeclared(String id) {
        super("Identifier " + id +  " not yet declared");
    }
}
