package tinycc.implementation.expression;

import tinycc.parser.TokenKind;

public class BinaryOperator {
    @Override
    public String toString() {
        // grab class name and convert it to Enum Naming Convention
        String enumElemName = this.getClass().getSimpleName().toUpperCase();
        // grab element from enum with name
        TokenKind enumElem = TokenKind.valueOf(enumElemName);
        return enumElem.getText();
    }   
}
