package tinycc.implementation;

import java.util.HashMap;
import java.util.List;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.TopLevelConstructs.ExternalDeclaration;
import tinycc.implementation.TopLevelConstructs.ExternalDeclarations.Function;
import tinycc.implementation.statement.Statement;
import tinycc.parser.ASTFactory;
import tinycc.parser.ASTFactoryClass;
import tinycc.parser.Lexer;
import tinycc.parser.Parser;
import tinycc.parser.Token;
import tinycc.logic.Formula;
import tinycc.mipsasmgen.MipsAsmGen;

/**
 * The main compiler class.
 *
 * An instance of this class will handle a single translation unit (e.g. input
 * file). There will be multiple instances of your class during runtime of your
 * compiler. You can change this class but the given name and signature of
 * methods and the name of the class must not be modified.
 */
public class Compiler {

	Diagnostic diagnostic;

	/**
	 * Initializes the compiler class with the given diagnostic module
	 *
	 * @param diagnostic The diagnostic module to use
	 * @see Diagnostic
	 */
	public Compiler(final Diagnostic diagnostic) {
		this.diagnostic = diagnostic;
	}

	/**
	 * Returns the current ASTFactory which is used internally.
	 *
	 * @return The current ASTFactory which is used internally.
	 * @see ASTFactory
	 */
	public ASTFactory getASTFactory() {
		return new ASTFactoryClass();
	}

	/**
	 * Parses a single translation unit which is given by an instance of the Lexer
	 * class.
	 *
	 * @param lexer The lexer to use
	 * @see Lexer
	 * @remarks This function is invoked only once in each instance of the compiler
	 *          class.
	 */
	public void parseTranslationUnit(final Lexer lexer) {
		Parser parser = new Parser(diagnostic, lexer, this.getASTFactory());
		parser.parseTranslationUnit();
	}

	/**
	 * Checks the semantics of the input program.
	 *
	 * @see ASTFactory
	 * @remarks Use the diagnostics module to report errors. This function is
	 *          invoked only once in each instance of the compiler class.
	 */
	public void checkSemantics() {

		// we can typecast, since we know that ASTFactoyClass extends ASTFactory
		ASTFactoryClass ast = (ASTFactoryClass) getASTFactory();
		// declarations list contains our complete program which should be checked.
		List<ExternalDeclaration> declarations = ast.getExternalDeclarations();

		// create new hashmap to keep track of already present identifiers
		HashMap<String, ExternalDeclaration> uniqueDeclarations = new HashMap<String, ExternalDeclaration>();

		for (ExternalDeclaration declaration : declarations) {
			String declerationName = declaration.getName().getText();
			
			// check if it is a function
			if (declaration instanceof Function) {
				// typecast and extract statement

				// check if statement is a block, if so, recursively check for those too
			}

			// otherwise check all other things

		}

		throw new UnsupportedOperationException("TODO: implement this");
	}

	/**
	 * Performs optimizations on the input program.
	 *
	 * @remarks Bonus exercise.
	 */
	public void performOptimizations() {
		throw new UnsupportedOperationException("TODO: implement this");
	}

	/**
	 * Generates code for the input program.
	 *
	 * @param out The target output stream.
	 * @remarks This function is invoked only once in each instance of the compiler
	 *          class. Only necessary if mentioned in the project description.
	 */
	public void generateCode(final MipsAsmGen out) {
		throw new UnsupportedOperationException("TODO: implement this");
	}

	/**
	 * Generates verification conditions for the input program.
	 *
	 * @remarks This function is invoked only once in each instance of the compiler
	 *          class. Only necessary if mentioned in the project description.
	 */
	public Formula genVerificationConditions() {
		throw new UnsupportedOperationException("TODO: implement this");
	}
}
