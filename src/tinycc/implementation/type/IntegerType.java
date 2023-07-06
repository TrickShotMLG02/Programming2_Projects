package tinycc.implementation.type;

public class IntegerType extends ScalarType{

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
    public boolean isVoidType() {
        return false;
    }

    @Override
    public boolean isObjectType() {
        return true;
    }
    
}

