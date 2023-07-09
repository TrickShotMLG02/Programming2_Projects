package tinycc.implementation.type;

public class PointerType extends ScalarType {

    private Type pointsTo;

    private boolean isVoidPointer = false;
    private boolean isNullPointer = false;

    public PointerType(Type pointsTo) {
        this.pointsTo = pointsTo;

        if (pointsTo.isVoidType()) {
            isVoidPointer = true;
        }

        // TODO: check for nullpointer
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

    @Override
    public boolean isComplete() {
        return true;
    }

    @Override
    public boolean isVoidPointer() {
        return isVoidPointer;
    }

    @Override
    public boolean isNullPointer() {
        return isNullPointer;
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() == this.getClass()) {
            // check if pointer types are equal
            PointerType ptrTypeOther = (PointerType) other;
            return this.getPointerType().getClass() == ptrTypeOther.getPointerType().getClass();
        }
        return false;
    }
}
