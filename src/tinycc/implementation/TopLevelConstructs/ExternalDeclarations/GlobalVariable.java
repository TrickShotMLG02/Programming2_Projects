package tinycc.implementation.TopLevelConstructs.ExternalDeclarations;

import tinycc.implementation.TopLevelConstructs.ExternalDeclaration;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.type.Type;
import tinycc.parser.Token;

public class GlobalVariable extends ExternalDeclaration {

    public GlobalVariable(Type type, Token name) {
        this(type, name, null);
    }

    public GlobalVariable(Type type, Token name, Expression init) {
        super(type, name, init);
    }
    
    @Override
    public boolean isFunction() {
        return false;
    }

    @Override
    public boolean isFunctionDeclaration() {
        return false;
    }

    @Override
    public boolean isGlobalVariable() {
        return true;
    }
}
