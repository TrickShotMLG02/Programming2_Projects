package tinycc.implementation;

import tinycc.implementation.TopLevelConstructs.ExternalDeclaration;
import tinycc.implementation.expression.BinaryOperator;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.expression.UnaryOperator;
import tinycc.implementation.expression.BinaryOperators.And_And;
import tinycc.implementation.expression.BinaryOperators.Asterisk;
import tinycc.implementation.expression.BinaryOperators.Bang_Equal;
import tinycc.implementation.expression.BinaryOperators.Equal;
import tinycc.implementation.expression.BinaryOperators.Equal_Equal;
import tinycc.implementation.expression.BinaryOperators.Greater;
import tinycc.implementation.expression.BinaryOperators.Greater_Equal;
import tinycc.implementation.expression.BinaryOperators.Less;
import tinycc.implementation.expression.BinaryOperators.Less_Equal;
import tinycc.implementation.expression.BinaryOperators.Minus;
import tinycc.implementation.expression.BinaryOperators.Pipe_Pipe;
import tinycc.implementation.expression.BinaryOperators.Plus;
import tinycc.implementation.expression.BinaryOperators.Slash;
import tinycc.implementation.expression.PrimaryExpressions.Identifier;
import tinycc.implementation.expression.PrimaryExpressions.Number;
import tinycc.implementation.expression.UnaryOperators.Address;
import tinycc.implementation.expression.UnaryOperators.Indirection;
import tinycc.implementation.expression.UnaryOperators.Negation;
import tinycc.implementation.expression.UnaryOperators.SizeOf;
import tinycc.implementation.type.Type;
import tinycc.implementation.type.BaseTypes.Char;
import tinycc.implementation.type.BaseTypes.Int;
import tinycc.implementation.type.BaseTypes.Void;
import tinycc.parser.Token;
import tinycc.parser.TokenKind;

public class Util {

    public static String capitalizeEnumName(String enumName) {
        char[] chrs = enumName.toCharArray();
        String formattedName = "";

        boolean nextCap = true;
        for (char currChar : chrs) {
            if (currChar == '_') {
                formattedName += currChar;
                nextCap = true;
            } else {
                formattedName += nextCap ? Character.toUpperCase(currChar) : Character.toLowerCase(currChar);
                nextCap = false;
            }
        }
        return formattedName;
    }

    /**
     * Function to create Object of type extracted from TokenKind
     * @param <T> return type specified by superclassType
     * @param kind TokenKind which should be converted
     * @param superclassType specifies the return type
     * @return new Object
     */
    public static Type createType(TokenKind kind) {
        if (kind == TokenKind.INT)
            return new Int();
        else if (kind == TokenKind.CHAR)
            return new Char();
        else if (kind == TokenKind.VOID)
            return new Void();
        else
            return null;
    }

    public static Expression createPrimaryExpression(Token token) {
        TokenKind kind = token.getKind();
        if (kind == TokenKind.CHARACTER)
            return new tinycc.implementation.expression.PrimaryExpressions.Character(token);
        else if (kind == TokenKind.IDENTIFIER)
            return new Identifier(token);
        else if (kind == TokenKind.NUMBER)
            return new Number(token);
        else if (kind == TokenKind.STRING)
            return new tinycc.implementation.expression.PrimaryExpressions.String(token);
        else
            return null;
    }

    public static BinaryOperator createBinaryOperator(TokenKind kind) {
        if (kind == TokenKind.AND_AND)
            return new And_And();
        else if (kind == TokenKind.ASTERISK)
            return new Asterisk();
        else if (kind == TokenKind.BANG_EQUAL)
            return new Bang_Equal();
        else if (kind == TokenKind.EQUAL_EQUAL)
            return new Equal_Equal();
        else if (kind == TokenKind.EQUAL)
            return new Equal();
        else if (kind == TokenKind.GREATER_EQUAL)
            return new Greater_Equal();
        else if (kind == TokenKind.GREATER)
            return new Greater();
        else if (kind == TokenKind.LESS_EQUAL)
            return new Less_Equal();
        else if (kind == TokenKind.LESS)
            return new Less();
        else if (kind == TokenKind.MINUS)
            return new Minus();
        else if (kind == TokenKind.PIPE_PIPE)
            return new Pipe_Pipe();
        else if (kind == TokenKind.PLUS)
            return new Plus();
        else if (kind == TokenKind.SLASH)
            return new Slash();
        else return null;
    }

    public static UnaryOperator createUnaryOperator(Token token, Expression applicable) {
        TokenKind kind = token.getKind();
        if (kind == TokenKind.ASTERISK)
            return new Indirection(applicable);
        else if (kind == TokenKind.AND)
            return new Address(applicable);
        else if (kind == TokenKind.SIZEOF)
            return new SizeOf(applicable);
        else if (kind == TokenKind.BANG)
            return new Negation(applicable);
        else
            return null;
    }

    public static ExternalDeclaration createExternalDeclaration(Type type, Token name, Expression init) {
        return new ExternalDeclaration(type, name, init);
    }
}
