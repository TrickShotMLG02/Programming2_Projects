package tinycc.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tinycc.mipsasmgen.DataLabel;
import tinycc.mipsasmgen.GPRegister;

public class CompilationScope {
    // lists of unused (available) registers for functions and temporaries
    private final List<GPRegister> unusedTempRegisters;
    private final List<GPRegister> unusedFunctionRegisters;

    // storing global variables and their data labels
    private final Map<String, DataLabel> dataLabels;

    // storing offsets of local declarations for stack
    private final Map<String, Integer> localDeclarationOffsets;
    private int currentStackOffset;

    // storing local variables and their registers
    private final Map<String, GPRegister> table;

    // parent scope
    private final CompilationScope parent;

    // TODO: implement dataLabels

    // constructors
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
        this.dataLabels = new HashMap<String, DataLabel>();
        this.localDeclarationOffsets = new HashMap<String, Integer>();

        if (parent != null)
            this.currentStackOffset = parent.currentStackOffset;
        else
            this.currentStackOffset = 0;
        
        unusedTempRegisters = populateUnusedTempRegisters();
        unusedFunctionRegisters = populateUnusedFunctionRegisters();
    }
    /**
     * Create a new nested scope, which lies within the current Scope
     * @return a new nested scope
     */
    public CompilationScope newNestedScope() {
        return new CompilationScope(this);
    }

    // functions to check if register (opt. for variable name) is already in use
    public boolean isUsed(GPRegister reg) {
        return table.containsValue(reg);
    }
    public boolean isUsed(String id) {
        return table.containsKey(id);
    }

    // function to add register to table
    public void addRegister(String id, GPRegister reg) throws IdAlreadyDeclared {
        if (!isUsed(id) && !isUsed(reg)) {
            unusedTempRegisters.remove(reg);
            unusedFunctionRegisters.remove(reg);
            table.put(id, reg);
        }
        else {
            throw new IdAlreadyDeclared("register " + reg.name()+ " for variable " + id + " already declared");
        }
    }
    public void addRegister(GPRegister reg) throws IdAlreadyDeclared {
        addRegister(reg.name(), reg);
    }
    public void addDataLabel(DataLabel lbl) throws IdAlreadyDeclared {
        if (!dataLabels.containsValue(lbl)) {
            dataLabels.put(lbl.toString(), lbl);
        }
        else {
            throw new IdAlreadyDeclared("data label already declared");
        }
    }
    public void addLocalDeclaration(String declarationName) throws IdAlreadyDeclared{
        if (localDeclarationOffsets.containsKey(declarationName)) {
            throw new IdAlreadyDeclared(declarationName + "already declared");
        }
        localDeclarationOffsets.put(declarationName, currentStackOffset);
        this.currentStackOffset += 4;
    }
    // function to remove register from table
    public void remove(String id) throws IdUndeclared {
        // check if it is a register
        if (isUsed(id)) {
            // set register for id as unused
            freeRegister(table.get(id));
            // remove register for id from table
            table.remove(id);
        }
        // else check if it is a local declaration
        else if (localDeclarationOffsets.containsKey(id)) {
            localDeclarationOffsets.remove(id);
        }
    }
    public void remove(GPRegister reg) throws IdUndeclared {
        if (isUsed(reg)) {
            // set register reg as unused
            freeRegister(reg);
            // remove register for regname from table
            table.remove(reg.name());
        }
        else {
            throw new IdUndeclared("Register not in use");
        }
    }
    public void remove(DataLabel lbl) throws IdUndeclared {
        if (!dataLabels.containsValue(lbl)) {
            dataLabels.remove(lbl.toString());
        }
        else {
            throw new IdUndeclared("data label not declared yet");
        }
    }

    /**
     * Function to get the next free temporary register
     * @return  the already added temporary register
     */
    public GPRegister getNextFreeTempRegister() {
        GPRegister freeReg = unusedTempRegisters.get(0);

        try {
            addRegister(freeReg);
        } catch (IdAlreadyDeclared e) {
            e.printStackTrace();
        }

        return freeReg;
    }
    /**
     * Function to get the next free function register
     * @return  the already added function register
     */
    public GPRegister getNextFreeFunctionRegister() {
        GPRegister freeReg = unusedFunctionRegisters.get(0);

        try {
            addRegister(freeReg);
        } catch (IdAlreadyDeclared e) {
            e.printStackTrace();
        }

        return freeReg;
    }

    // functions to lookup stuff
    public GPRegister lookupRegister(String id) {
        if (table.containsKey(id))
            return table.get(id);
        else
            return parent != null ? parent.lookupRegister(id) : null;
    }
    public DataLabel lookupDataLabel(String name) {
        if (dataLabels.containsKey(name))
            return dataLabels.get(name);
        else
            return parent != null ? parent.lookupDataLabel(name) : null;
    }
    public Integer lookupLocalDeclaration(String declarationName) {
        if (localDeclarationOffsets.containsKey(declarationName))
            return localDeclarationOffsets.get(declarationName);
        else
            return parent != null ? parent.lookupLocalDeclaration(declarationName) : null;
    }
    
    // functions for poulating lists
    private List<GPRegister> populateUnusedTempRegisters() {
        List<GPRegister> regs = new ArrayList<GPRegister>();

        // T regs
        for(int i = 0; i < 10; i++) {
            regs.add(GPRegister.valueOf("T" + i));
        }

        // S regs
        for(int i = 0; i < 8; i++) {
            regs.add(GPRegister.valueOf("S" + i));
        }

        return regs;
    }
    private List<GPRegister> populateUnusedFunctionRegisters() {
        List<GPRegister> regs = new ArrayList<GPRegister>();

        // A regs
        for(int i = 0; i < 4; i++) {
            regs.add(GPRegister.valueOf("A" + i));
        }

        return regs;
    }

    // function to free register
    private void freeRegister(GPRegister reg) {
        // TODO: might cause errors when list is too short
        // get number of register
        int pos = Integer.parseInt(reg.name().substring(1));

        if (populateUnusedFunctionRegisters().contains(reg)) {
            unusedFunctionRegisters.add(pos, reg);
        }
        else if (populateUnusedTempRegisters().contains(reg)) {
            unusedTempRegisters.add(pos, reg);
        }
    }

    /*
    * Exceptions for Scopes
    */
    class IdAlreadyDeclared extends Exception {
        public IdAlreadyDeclared(String message) {
            super(message);
        }
    }
    class IdUndeclared extends Exception {
        public IdUndeclared(String id) {
            super("Identifier " + id +  " not yet declared");
        }
    }

    public CompilationScope getParent() {
        return parent;
    }

    public int getStackOffset() {
        return currentStackOffset;
    }
}
