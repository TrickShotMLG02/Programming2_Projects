package tinycc.implementation.type.BaseTypes;

import tinycc.implementation.type.ObjectType;

public class Void extends ObjectType {

    @Override
    public boolean isFunctionType() {
        return false;
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
        return true;
    }
    
    @Override
    public boolean isVoidType() {
        return true;
    }
}
