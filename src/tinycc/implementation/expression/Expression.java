package tinycc.implementation.expression;

import tinycc.implementation.type.ObjectType;

/**
 * The main expression class (see project description)
 *
 * You can change this class but the given name of the class must not be
 * modified.
 */
public abstract class Expression {

	public ObjectType lOperand;
	public ObjectType rOperand;
	public ObjectType resultOperand;

	/**
	 * Creates a string representation of this expression.
	 *
	 * @remarks See project documentation.
	 * @see StringBuilder
	 */
	@Override
	public abstract String toString();

}
