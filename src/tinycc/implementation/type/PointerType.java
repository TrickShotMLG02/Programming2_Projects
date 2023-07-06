package tinycc.implementation.type;

public class PointerType extends ScalarType {

    private Type pointsTo;

    public PointerType(Type pointsTo) {
        this.pointsTo = pointsTo;
    }

    /**
     * Return string consisting of "Pointer[ <Type> ]"
     */
    @Override
    public String toString() {
        return "Pointer[" + pointsTo.toString() + "]";
    }

    public Type getPointerType() {
        return pointsTo;
    }

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
        return true;
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
    
}
