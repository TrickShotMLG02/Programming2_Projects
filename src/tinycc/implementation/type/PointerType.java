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
}
