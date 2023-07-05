package tinycc.implementation.TopLevelConstructs.ExternalDeclarations;

import tinycc.implementation.TopLevelConstructs.ExternalDeclaration;
import tinycc.implementation.type.Type;
import tinycc.parser.Token;

public class Function extends ExternalDeclaration {

    public Function(Type type, Token name) {
        super(type, name);
    }
    
}
