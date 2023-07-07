package tinycc.implementation.type;

public class ScalarType extends ObjectType {

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

    @Override
    public boolean isComplete() {
        return true;
    }
}
