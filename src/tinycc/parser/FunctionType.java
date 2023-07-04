package tinycc.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tinycc.implementation.type.Type;

public class FunctionType extends Type {

    private Type returnType;
    private List<Type> params;

    public FunctionType(Type returnType, List<Type> params) {
        this.returnType = returnType;
        this.params = params;
    }

    @Override
    public String toString() {
        // create new temporary list containing the return type and all the params
        List<Type> tmpTypes = new ArrayList<>();
        tmpTypes.add(returnType);
        tmpTypes.addAll(params);
        // return new String consisting of FunctionType[ <returnType>,<param1,...,param2> ]
        return this.getClass().getSimpleName() + Arrays.toString(tmpTypes.toArray());
    }
    
}
