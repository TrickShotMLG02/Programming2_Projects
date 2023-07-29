package tinycc.implementation.type.BaseTypes;

import tinycc.implementation.type.IntegerType;

public class Char extends IntegerType {

    @Override
    public boolean isFunctionType() {
        return false;
    }

    @Override
    public boolean isIntegerType() {
        return true;
    }

    @Override
    public boolean isPointerType() {
        return false;
    }

    @Override
    public boolean isScalarType() {
        return true;
    }

    @Override
    public boolean isObjectType() {
        return true;
    }

    @Override
    public boolean isVoidType() {
        return false;
    }

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public int getSize() {
        return 2;
    }
}
