package tinycc.implementation.expression;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.CompilationScope;
import tinycc.implementation.Scope;
import tinycc.implementation.type.Type;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.parser.Token;

/**
 * The main expression class (see project description)
 *
 * You can change this class but the given name of the class must not be
 * modified.
 */
public abstract class Expression {

	protected Type type;

	/**
	 * Creates a string representation of this expression.
	 *
	 * @remarks See project documentation.
	 * @see StringBuilder
	 */
	@Override
	public abstract String toString();

	public abstract Type checkType(Diagnostic d, Scope s);

	public abstract GPRegister generateCode(CompilationScope s, MipsAsmGen gen);

	public abstract Token getToken();

	public abstract boolean isLValue();

	public boolean isNullPointer() {
		return false;
	}

	public Type getType() {
		return this.type;
	}
}
