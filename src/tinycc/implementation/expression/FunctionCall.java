package tinycc.implementation.expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.CompilationScope;
import tinycc.implementation.Scope;
import tinycc.implementation.expression.PrimaryExpressions.Identifier;
import tinycc.implementation.type.FunctionType;
import tinycc.implementation.type.Type;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.ImmediateInstruction;
import tinycc.mipsasmgen.JumpInstruction;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.mipsasmgen.RegisterInstruction;
import tinycc.mipsasmgen.TextLabel;
import tinycc.parser.Token;

public class FunctionCall extends Expression {

    Token token;
    Expression callee;
    List<Expression> arguments;
    
    public FunctionCall(Token token, Expression callee, List<Expression> arguments) {
        this.token = token;
        this.callee = callee;
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        List<Expression> tmpExp = new ArrayList<>();
        tmpExp.add(callee);
        tmpExp.addAll(arguments);

        return "Call" + Arrays.toString(tmpExp.toArray());
    }

    @Override
    public Type checkType(Diagnostic d, Scope s) {

        // check if function is declared
        Type calleeType = callee.checkType(d, s);

        // check if it not is a function type
        if (!calleeType.isFunctionType()) {
            d.printError(token, "function not defined");
        }

        // typecast to function type, since we know it is a function
        FunctionType calleeFunType = (FunctionType) calleeType;
        
        // check amount of parameters
        if (arguments.size() != calleeFunType.getParams().size()) {
            d.printError(token, "unequal amount of parameters");
        }

        // iterate over all parameters
        for (int i = 0; i < arguments.size(); i++) {
            // grab argument from current call and extract type
            Expression arg = arguments.get(i);
            Type argType = arg.checkType(d, s);

            // grab type of current arg in callee args
            Type calleeArg = calleeFunType.getParams().get(i);

            // check if both types are different
            if (!argType.equals(calleeArg)) {
                d.printError(getToken(), "Invalid parameter type - expected: " + calleeArg + " but was " + argType);
            }
        }

        this.type = calleeFunType.getReturnType();
        return this.type;
    }

    @Override
    public GPRegister generateCode(CompilationScope s, MipsAsmGen gen) {

        // callee is of type identifier
        String funName = callee.getToken().getText();
        TextLabel funLabel = s.lookupFunctionLabel(funName);

        // check that function exists
        if (funLabel == null) {
            throw new IllegalArgumentException("Function " + funName + " not defined");
        }

        // save all caller save registers and return address
        s.saveCallerSaveRegisters();

        // create new scope since every function call saves new caller registers
        CompilationScope funCallScope = s.newNestedScope();

        // assign parameters of function call to a0...a3 registers
        int currParamIndex = 0;
        for (Expression param : arguments) {

            // check if param is an identifier
            // or if it is a number
            // or if it is a general expression
            if (param instanceof Identifier) {
                // grab name of the identifier passed
                String paramName = param.getToken().getText();

                // get the register containing the value of the identifier passed to the function
                GPRegister paramReg = s.lookupRegister(paramName);

                // TODO: grab the name of the corresponding function parameter from declaration
                GPRegister paramDeclReg = funCallScope.getNextFreeFunctionRegister(paramName);

                gen.emitInstruction(RegisterInstruction.ADD, paramDeclReg, GPRegister.ZERO, paramReg);
            }
            else if (param instanceof tinycc.implementation.expression.PrimaryExpressions.Number) {
                // grab the value of the number which is passed
                String paramName = param.getToken().getText();
                int paramVal = Integer.parseInt(paramName);
                
                // get nextFreeFunctionRegister for storing that value in
                // TODO: get the name of the corresponding function parameter from declaration
                GPRegister paramDeclReg = funCallScope.getNextFreeFunctionRegister("A" + currParamIndex);
                
                // generate instruction to load the value into the register
                gen.emitInstruction(ImmediateInstruction.ADDI, paramDeclReg, GPRegister.ZERO, paramVal);
            }
            else if (param instanceof Expression) {
                // generate the code for the expression and grab the resulting register
                GPRegister paramReg = param.generateCode(funCallScope, gen);

                // get next function parameter register
                GPRegister paramDeclReg = funCallScope.getNextFreeFunctionRegister("A" + currParamIndex);

                // move expression register value to param register
                gen.emitInstruction(RegisterInstruction.ADD, paramDeclReg, GPRegister.ZERO, paramReg);

                // free paramReg
                try {
                    s.remove(paramReg);
                } catch (Exception e) {
                }

            }

            // increment current register
            currParamIndex += 1;
        }

        // call function assembly code
        gen.emitInstruction(JumpInstruction.JAL, funLabel);

        // restore caller save registers
        s.restoreCallerSaveRegisters();

        return GPRegister.V0;
    }

    @Override
    public Token getToken() {
        return token;
    }

    @Override
    public boolean isLValue() {
        return false;
    }
}
