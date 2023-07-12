package tinycc.implementation;

import java.util.ArrayList;
import java.util.List;
import tinycc.diagnostic.Diagnostic;
import tinycc.implementation.TopLevelConstructs.ExternalDeclaration;
import tinycc.implementation.TopLevelConstructs.ExternalDeclarations.Function;
import tinycc.implementation.TopLevelConstructs.ExternalDeclarations.FunctionDeclaration;
import tinycc.implementation.TopLevelConstructs.ExternalDeclarations.GlobalVariable;
import tinycc.implementation.statement.Statement;
import tinycc.implementation.statement.Statements.Block;
import tinycc.implementation.statement.Statements.Declaration;
import tinycc.implementation.type.FunctionType;
import tinycc.implementation.type.Type;
import tinycc.parser.ASTFactory;
import tinycc.parser.ASTFactoryClass;
import tinycc.parser.Lexer;
import tinycc.parser.Parser;
import tinycc.parser.Token;
import tinycc.logic.Formula;
import tinycc.mipsasmgen.DataLabel;
import tinycc.mipsasmgen.GPRegister;
import tinycc.mipsasmgen.MipsAsmGen;
import tinycc.mipsasmgen.TextLabel;

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

		List<ExternalDeclaration> delcarations = new ArrayList<>(astFactory.getExternalDeclarations());

		for (ExternalDeclaration decl : delcarations) {
			
			// check if declaration is a function
			if (decl.isFunction()) {
				// typecast declaration to function
				Function fun = (Function) decl;

				// create new scope for function
				Scope functionScope = s.newNestedScope();

				// grab function parameters
				List<Token> parameters = fun.getParameterNames();

				int paramIdx = 0;

				// iterate over all parameters of function
				for (Token t : parameters) {

					// store name of parameter
					String parameterName = t.getText();

					// grab the type of the function as FunctionType (since it is a function)
					FunctionType funType = (FunctionType) fun.getType();

					// grab the list of all function parameter types
					List<Type> funParamTypes = funType.getParams();

					//grab the respective type for current parameter index
					Type paramType = funParamTypes.get(paramIdx);

					// create declaration of parameter with given type
					Statement d = getASTFactory().createDeclarationStatement(paramType, t, null);

					try {
						// add declaration to function scope
						functionScope.add(parameterName, (Declaration) d);
					} catch (IdAlreadyDeclared e) {
						//e.printStackTrace();
					}

					// move to next parameter
					paramIdx++;
				}

				// create declaration for function and add it to parent scope
				Statement f = getASTFactory().createDeclarationStatement(fun.getType(), fun.getToken(), null);
				try {
					s.add(fun.getToken().getText(), (Declaration) f);
				} catch (IdAlreadyDeclared e) {
					//e.printStackTrace();
				}

				// grab function body
				Statement functionBody = fun.getBody();

				// checkType on function body in current function scope
				functionBody.checkType(diagnostic, functionScope, fun);
			}
			else if (decl.isFunctionDeclaration()) {
				
				// typecast declaration to function declaration
				FunctionDeclaration fun = (FunctionDeclaration) decl;

				try {
					// Identifier not found in any scope, thus declare it
					String identifier = fun.getToken().getText();
					s.add(identifier, new Declaration(decl, decl.getInitExpression()));

				} catch (Exception e) {
					try {
						
						// check if function was declared in scope
						Declaration scopeDec = s.lookup(fun.getToken().getText());

						// check type of declaration
						scopeDec.checkType(diagnostic, s, fun);

					} catch (Exception e1) {
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
					scopeDec.checkType(diagnostic, s, var);

				} catch (IdUndeclared e) {
					
					// Identifier not found in any scope, thus declare it
					try {
						// Identifier not found in any scope, thus declare it
						String identifier = var.getToken().getText();
						s.add(identifier, new Declaration(decl, decl.getInitExpression()));

						// check if function was declared in scope
						Declaration scopeDec = s.lookup(var.getToken().getText());

						// check type of declaration
						scopeDec.checkType(diagnostic, s, var);
						
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		}
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
		// grab all declarations (the current program)
		List<ExternalDeclaration> delcarations = new ArrayList<>(astFactory.getExternalDeclarations());

		CompilationScope scope = new CompilationScope();

		// iterate over all declarations
		for (ExternalDeclaration decl : delcarations) {
			if (decl.isGlobalVariable()) {
				// typecast declaration to global variable
				GlobalVariable var = (GlobalVariable) decl;

				// create datalabel
				DataLabel dataLbl = out.makeDataLabel(var.getToken().getText());
				out.emitWord(dataLbl, 0);

				try {
					scope.addDataLabel(dataLbl);
				} catch (tinycc.implementation.CompilationScope.IdAlreadyDeclared e) {
					e.printStackTrace();
				}
			}
			else if (decl.isFunctionDeclaration()) {
				// typecast declaration to function declaration
				FunctionDeclaration fun = (FunctionDeclaration) decl;

				// create textlabel
				TextLabel funLbl = out.makeTextLabel(fun.getToken().getText());
				out.emitLabel(funLbl);
			}
			else if (decl.isFunction()) {

				// create new scope for function
				CompilationScope funScope = scope.newNestedScope();

				// typecast declaration to function
				Function fun = (Function) decl;

				// create textlabel
				TextLabel funLbl = out.makeTextLabel(fun.getToken().getText());
				out.emitLabel(funLbl);

				// grab function body as Block, since all functions consist of a Block
				Block body = (Block) fun.getBody();

				// get list of statements (body)
				List<Statement> bodyContents = body.getBody();

				// iterate over all statements in function body
				for (Statement stmnt : bodyContents) {

					// generate code for current statement
					stmnt.generateCode(funScope, out);

					// TODO: Bonus task
				}
			}
		}

		//throw new UnsupportedOperationException("TODO: implement this");
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
