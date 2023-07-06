package tinycc.implementation.TopLevelConstructs;

import tinycc.implementation.expression.Expression;
import tinycc.implementation.type.Type;
import tinycc.parser.Token;

public class ExternalDeclaration {
    
    Type type;
    Token name;
    Expression init;

    public ExternalDeclaration(Type type, Token name, Expression init) {
        this.type = type;
        this.name = name;
        this.init = init;
    }

    @Override
    public String toString() {
        if (init == null)
            return "Declaration_" + name.getText() + "[" + type + "]";
        else
            return "Declaration_" + name.getText() + "[" + type + "," + init + "]";
    }

    public Type getType() {
        return type;
    }

    public Token getName() {
        return name;
    }

    public Expression getInitExpression() {
        return init;
    }

}
