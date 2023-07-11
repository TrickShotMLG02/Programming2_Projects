package tinycc.implementation;

import java.util.HashMap;
import java.util.Map;

import tinycc.mipsasmgen.GPRegister;

public class CompilationScope {
    private final Map<String, GPRegister> table;
    private final CompilationScope parent;

     /**
     * Create a new Scope with no parent (the first scope)
     */
    public CompilationScope() {
        this(null);
    }

    /**
     * Only for internal use of newNestedScope and Scope
     * @param parent
     */
    private CompilationScope(CompilationScope parent) {
        this.parent = parent;
        this.table  = new HashMap<String, GPRegister>();
    }

    /**
     * Create a new nested scope, which lies within the current Scope
     * @return a new nested scope
     */
    public CompilationScope newNestedScope() {
        return new CompilationScope(this);
    }

    /**
     * Add a new Identifier and its respective GPRegister to the current scope
     * @param id                    The identifier to add
     * @param d                     The declaration to add
     * @throws IdAlreadyDeclared    If the identifier is already declared in this scope
     */
    public void add(String id, GPRegister d) throws IdAlreadyDeclared {
        if (table.containsKey(id)) {
            // update already existing value
            table.put(id, d);
            throw new IdAlreadyDeclared(id);
        }
        table.put(id, d);
    }

    /**
     * Removes a specific identifier and its register from the map
     * @param id
     * @throws IdUndeclared
     */
    public void remove(String id) throws IdUndeclared {
        // check if identifier is declared
        if (table.containsKey(id)) {
            // remove identifier from table
            table.remove(id);
        }
        else {
            throw new IdUndeclared(id);
        }
    }

    public boolean inUse(String id) {
        return table.containsKey(id);
    }

    public GPRegister getNextFreeTempRegister() {
        for (int i = 0; i < 10; i++) {
            GPRegister tmpReg = GPRegister.valueOf("$t" + i);

            // check if register is not in use
            if (!inUse(tmpReg.name())) {
                return tmpReg;
            }  
        }

        return null;
    }

    /**
     * Lookup an identifier in all parent scopes.
     * @param id                The Identifier to look up
     * @return                  The respective GPRegister of an Identifier
     * @throws IdUndeclared     If Identifier was not found
     */
    public GPRegister lookup(String id) throws IdUndeclared {

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
}
