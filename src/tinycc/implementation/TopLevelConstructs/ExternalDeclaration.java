package tinycc.implementation.TopLevelConstructs;

import tinycc.implementation.type.Type;
import tinycc.parser.Token;

public class ExternalDeclaration {
    
    Type type;
    Token name;

    public ExternalDeclaration(Type type, Token name) {
        this.type = type;
        this.name = name;
    }

    @Override
    public String toString() {
        return "Declaration_" + name.getText() + "[" + type + "]";
    }

    public Type getType() {
        return type;
    }

    public Token getName() {
        return name;
    }

}
