package tinycc.implementation.TopLevelConstructs.ExternalDeclarations;

import java.util.List;

import tinycc.implementation.TopLevelConstructs.ExternalDeclaration;
import tinycc.implementation.statement.Statement;
import tinycc.implementation.type.Type;
import tinycc.parser.Token;

public class Function extends ExternalDeclaration {
    private List<Token> parameterNames;
    private Statement body;

    public Function(Type type, Token name, List<Token> parameterNames, Statement body) {
        super(type, name, null);
        this.parameterNames = parameterNames;
        this.body = body;
    }

    @Override
    public String toString() {
        // TODO: Implement toString method
        return getType().toString();
    }

    public List<Token> getParameterNames() {
        return parameterNames;
    }

    public Statement getBody() {
        return body;
    }
    
}
