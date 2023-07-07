package tinycc.implementation.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    @Override
    public boolean isFunctionType() {
        return true;
    }

    @Override
    public boolean isIntegerType() {
        return false;
    }

    @Override
    public boolean isPointerType() {
        return false;
    }

    @Override
    public boolean isScalarType() {
        return false;
    }

    @Override
    public boolean isObjectType() {
        return false;
    }
    
    @Override
    public boolean isVoidType() {
        return false;
    }

    @Override
    public boolean isComplete() {
        return false;
    }
}
