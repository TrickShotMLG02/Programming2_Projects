package tinycc.implementation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tinycc.mipsasmgen.DataLabel;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.ImmediateInstruction;
import tinycc.mipsasmgen.MemoryInstruction;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.mipsasmgen.TextLabel;

public class CompilationScope {
    // lists of unused (available) registers for functions and temporaries
    private final List<GPRegister> unusedTempRegisters;
    private final List<GPRegister> unusedFunctionRegisters;

    // storing global variables and their data labels
    private final Map<String, DataLabel> dataLabels;

    //storing function names and their labels
    private final Map<String, TextLabel> functionLabels;

    // storing offsets of local declarations for stack
    private final Map<String, Integer> localDeclarationOffsets;
    private int currentStackOffset;

    // storing local variables and their registers
    private final Map<String, GPRegister> table;

    // parent scope
    private final CompilationScope parent;

    // MipsGenerator used for code generation in save/restore functions
    private MipsAsmGen gen;

    // constructors
    /**
     * Create a new Scope with no parent (the first scope)
     */
    public CompilationScope(MipsAsmGen gen) {
        this(null, gen);
    }
    /**
     * Only for internal use of newNestedScope and Scope
     * @param parent
     */
    private CompilationScope(CompilationScope parent, MipsAsmGen gen) {
        this.parent = parent;
        this.table  = new HashMap<String, GPRegister>();
        this.dataLabels = new HashMap<String, DataLabel>();
        this.localDeclarationOffsets = new HashMap<String, Integer>();
        this.functionLabels = new HashMap<String, TextLabel>();
        this.gen = gen;

        if (parent != null)
            //this.currentStackOffset = parent.currentStackOffset;
            this.currentStackOffset = 0;
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
        return new CompilationScope(this, gen);
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
    public void addFunctionLabel(TextLabel lbl) throws IdAlreadyDeclared {
        if (!functionLabels.containsValue(lbl)) {
            functionLabels.put(lbl.toString(), lbl);
        }
        else {
            throw new IdAlreadyDeclared("function label already declared");
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
            currentStackOffset -= 4;
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
    public GPRegister getNextFreeFunctionRegister(String paramName) {
        GPRegister freeReg = unusedFunctionRegisters.get(0);

        try {
            addRegister(paramName, freeReg);
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
    public TextLabel lookupFunctionLabel(String name) {
        if (functionLabels.containsKey(name))
            return functionLabels.get(name);
        else
            return parent != null ? parent.lookupFunctionLabel(name) : null;
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

    // TODO: IMPLEMENT SAVE/RESTORE FUNCTIONS
    public void saveCallerSaveRegisters() {
        // move stack pointer down by current offset
        gen.emitInstruction(ImmediateInstruction.ADDI, GPRegister.SP, -currentStackOffset);

        // move stack pointer down by amount of registers to save * 4
        gen.emitInstruction(ImmediateInstruction.ADDI, GPRegister.SP, -60);

        // save all registers which are caller save (a0 - a3, v0 - v1, t0 - t9, ra)

        int localOffset = 0;
        // save all function parameter registers (a0 - a3)
        for (int i = 0; i < 4; i++) {
            GPRegister saveReg = GPRegister.valueOf("A" + i);
            gen.emitInstruction(MemoryInstruction.SW, saveReg, null, 4 * localOffset, GPRegister.SP);
            localOffset += 1;
        }

        // save all temporary registers (t0 - t9)
        for (int i = 0; i < 10; i++) {
            GPRegister saveReg = GPRegister.valueOf("T" + i);
            gen.emitInstruction(MemoryInstruction.SW, saveReg, null, 4 * localOffset, GPRegister.SP);
            localOffset += 1;
        }

        // save return address (ra)
        for (int i = 0; i < 1; i++) {
            GPRegister saveReg = GPRegister.valueOf("RA");
            gen.emitInstruction(MemoryInstruction.SW, saveReg, null, 4 * localOffset, GPRegister.SP);
            localOffset += 1;
        }
        
        currentStackOffset += localOffset;
    }

    public void saveCalleeSaveRegisters() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void restoreCallerSaveRegisters() {
        // restore all registers which are caller save

        int localOffset = 0;

         // restore all function parameter registers (a0 - a3)
        for (int i = 0; i < 4; i++) {
            GPRegister saveReg = GPRegister.valueOf("A" + i);
            gen.emitInstruction(MemoryInstruction.LW, saveReg, null, 4 * localOffset, GPRegister.SP);
            localOffset += 1;
        }

        // restore all temporary registers (t0 - t9)
        for (int i = 0; i < 10; i++) {
            GPRegister saveReg = GPRegister.valueOf("T" + i);
            gen.emitInstruction(MemoryInstruction.LW, saveReg, null, 4 * localOffset, GPRegister.SP);
            localOffset += 1;
        }

        // restore return address (ra)
        for (int i = 0; i < 1; i++) {
            GPRegister saveReg = GPRegister.valueOf("RA");
            gen.emitInstruction(MemoryInstruction.LW, saveReg, null, 4 * localOffset, GPRegister.SP);
            localOffset += 1;
        }

        // move stackpinter up by (amount of registers restored * 4)
        gen.emitInstruction(ImmediateInstruction.ADDI, GPRegister.SP, 4 * localOffset);

        // remove local offset from stack
        currentStackOffset -= localOffset;

        // move stack pointer up by current offset
        gen.emitInstruction(ImmediateInstruction.ADDI, GPRegister.SP, currentStackOffset);
    }

    public void restoreCalleeSaveRegisters() {
        throw new UnsupportedOperationException("Not yet implemented");
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
