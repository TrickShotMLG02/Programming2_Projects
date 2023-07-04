package tinycc.implementation.type;

public abstract class ObjectType extends Type {

    /**
     * Return string consisting of "Type[ <SimpleClassName> ]"
     */
    @Override
    public String toString() {
        return "Type_" + this.getClass().getSimpleName().toLowerCase();
    }
    
}
