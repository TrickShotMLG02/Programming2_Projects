package tinycc.implementation.expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.CompilationScope;
import tinycc.implementation.Scope;
import tinycc.implementation.type.FunctionType;
import tinycc.implementation.type.Type;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MipsAsmGen;
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

        return calleeFunType.getReturnType();
    }

    @Override
    public GPRegister generateCode(CompilationScope s, MipsAsmGen gen) {
        throw new UnsupportedOperationException("Unimplemented method 'generateCode'");
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
