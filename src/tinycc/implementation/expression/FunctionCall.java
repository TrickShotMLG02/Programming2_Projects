package tinycc.implementation.expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tinycc.parser.Token;

public class FunctionCall extends Expression {

    Token token;
    Expression callee;
    List<Expression> arguments;
    
    public FunctionCall(Token token, Expression callee, List<Expression> arguments) {
        this.token = token;
        this.callee = callee;
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        List<Expression> tmpExp = new ArrayList<>();
        tmpExp.add(callee);
        tmpExp.addAll(arguments);

        return "Call" + Arrays.toString(tmpExp.toArray());
    }
    
}