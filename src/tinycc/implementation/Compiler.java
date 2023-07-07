package tinycc.implementation;

import java.util.List;

import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.TopLevelConstructs.ExternalDeclaration;
import tinycc.implementation.TopLevelConstructs.ExternalDeclarations.Function;
import tinycc.implementation.TopLevelConstructs.ExternalDeclarations.FunctionDeclaration;
import tinycc.implementation.TopLevelConstructs.ExternalDeclarations.GlobalVariable;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.statement.Statement;
import tinycc.implementation.statement.Statements.Declaration;
import tinycc.implementation.type.IntegerType;
import tinycc.implementation.type.Type;
import tinycc.parser.ASTFactory;
import tinycc.parser.ASTFactoryClass;
import tinycc.parser.Lexer;
import tinycc.parser.Parser;
import tinycc.parser.Token;
import tinycc.parser.TokenKind;
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

	private ASTFactoryClass astFactory;

	Diagnostic diagnostic;

	/**
	 * Initializes the compiler class with the given diagnostic module
	 *
	 * @param diagnostic The diagnostic module to use
	 * @see Diagnostic
	 */
	public Compiler(final Diagnostic diagnostic) {
		this.diagnostic = diagnostic;
		astFactory = new ASTFactoryClass();
	}

	/**
	 * Returns the current ASTFactory which is used internally.
	 *
	 * @return The current ASTFactory which is used internally.
	 * @see ASTFactory
	 */
	public ASTFactory getASTFactory() {
		return astFactory;
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

		Scope s = new Scope();

		List<ExternalDeclaration> delcarations;
		delcarations = astFactory.getExternalDeclarations();

		for (ExternalDeclaration decl : delcarations) {
			
			// check if declaration is a function
			if (decl.isFunction()) {
				// typecast declaration to function
				Function fun = (Function) decl;

				// create new scope for function
				Scope functionScope = s.newNestedScope();

				// grab function parameters
				List<Token> parameters = fun.getParameterNames();

				// iterate over all parameters of function
				for (Token t : parameters) {

					// store name of identifier
					String parameterName = t.getText();

					// grab kind of identifier
					TokenKind kind = t.getKind();

					// create primary expression
					Expression paramIdentifier = Util.createPrimaryExpression(t);

					// grab type of parameter
					// TODO: cant just be integer i guess
					Type paramType = new IntegerType();

					// create declaration of parameter
					Statement d = getASTFactory().createDeclarationStatement(paramType, t, null);

					try {
						// add declaration to function scope
						functionScope.add(parameterName, (Declaration) d);

					} catch (IdAlreadyDeclared e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				// grab function body
				Statement functionBody = fun.getBody();

				// checkType on function body in current function scope
				functionBody.checkType(diagnostic, functionScope);
			}
			else if (decl.isFunctionDeclaration()) {
				
				// typecast declaration to function declaration
				FunctionDeclaration fun = (FunctionDeclaration) decl;

				try {
					// Identifier not found in any scope, thus declare it
					String identifier = fun.getToken().getText();
					s.add(identifier, new Declaration(decl, decl.getInitExpression()));

					

					// TODO: maybe check if type of declaration is equal to type from scope declaration

				} catch (Exception e) {
					try {
						
						// check if function was declared in scope
						Declaration scopeDec = s.lookup(fun.getToken().getText());

						// check type of declaration
						scopeDec.checkType(diagnostic, s);

					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

					e.printStackTrace();
				}

			}
			else if (decl.isGlobalVariable()) {

				// typecast declaration to global variable
				GlobalVariable var = (GlobalVariable) decl;

				try {
					// check if variable was declared in scope
					Declaration scopeDec = s.lookup(var.getToken().getText());
					
					// check type of declaration
					scopeDec.checkType(diagnostic, s);

					// TODO: maybe check if type of declaration is equal to type from scope declaration

				} catch (IdUndeclared e) {
					
					// Identifier not found in any scope, thus declare it
					try {
						// Identifier not found in any scope, thus declare it
						String identifier = var.getToken().getText();
						s.add(identifier, new Declaration(decl, decl.getInitExpression()));

						// check if function was declared in scope
						Declaration scopeDec = s.lookup(var.getToken().getText());

						// check type of declaration
						scopeDec.checkType(diagnostic, s);
						
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
		

		//throw new UnsupportedOperationException("TODO: implement this");
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
