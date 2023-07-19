package tinycc.implementation.type;

/**
 * The main type class (see project description)
 *
 * You can change this class but the given name of the class must not be
 * modified.
 */
public abstract class Type {

	/**
	 * Creates a string representation of this type.
	 *
	 * @remarks See project documentation.
	 * @see StringBuilder
	 */
	@Override
	public abstract String toString();


	public abstract boolean isFunctionType();
	
	public abstract boolean isIntegerType();

	public abstract boolean isPointerType();

	public abstract boolean isScalarType();

	public abstract boolean isVoidType();

	public abstract boolean isObjectType();

	public abstract boolean isComplete();

	public boolean isVoidPointer() {
		return false;
	}

	public int getSize() {
        return 0;
    }

	@Override
	public boolean equals(Object other) {
		return other.getClass() == this.getClass();
	}
}
