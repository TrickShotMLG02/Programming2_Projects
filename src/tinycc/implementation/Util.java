package tinycc.implementation;

import tinycc.implementation.expression.BinaryExpression;
import tinycc.implementation.expression.BinaryOperator;
import tinycc.implementation.expression.Expression;
import tinycc.implementation.expression.UnaryExpression;
import tinycc.implementation.expression.UnaryOperator;
import tinycc.implementation.expression.BinaryExpressions.AddExpression;
import tinycc.implementation.expression.BinaryExpressions.AndExpression;
import tinycc.implementation.expression.BinaryExpressions.AssignExpression;
import tinycc.implementation.expression.BinaryExpressions.DivisionExpression;
import tinycc.implementation.expression.BinaryExpressions.EqualsExpression;
import tinycc.implementation.expression.BinaryExpressions.GreaterEqualExpression;
import tinycc.implementation.expression.BinaryExpressions.GreaterExpression;
import tinycc.implementation.expression.BinaryExpressions.LessEqualExpression;
import tinycc.implementation.expression.BinaryExpressions.LessExpression;
import tinycc.implementation.expression.BinaryExpressions.MultiplicationExpression;
import tinycc.implementation.expression.BinaryExpressions.OrExpression;
import tinycc.implementation.expression.BinaryExpressions.SubtractExpression;
import tinycc.implementation.expression.BinaryExpressions.UnequalExpression;
import tinycc.implementation.expression.BinaryOperators.Comp_And;
import tinycc.implementation.expression.BinaryOperators.Multiplication;
import tinycc.implementation.expression.BinaryOperators.Comp_Unequal;
import tinycc.implementation.expression.BinaryOperators.Assign;
import tinycc.implementation.expression.BinaryOperators.Comp_Equals;
import tinycc.implementation.expression.BinaryOperators.Comp_Greater;
import tinycc.implementation.expression.BinaryOperators.Comp_GreaterEqual;
import tinycc.implementation.expression.BinaryOperators.Comp_Less;
import tinycc.implementation.expression.BinaryOperators.Comp_LessEqual;
import tinycc.implementation.expression.BinaryOperators.Minus;
import tinycc.implementation.expression.BinaryOperators.Comp_Or;
import tinycc.implementation.expression.BinaryOperators.Plus;
import tinycc.implementation.expression.BinaryOperators.Division;
import tinycc.implementation.expression.PrimaryExpressions.Identifier;
import tinycc.implementation.expression.PrimaryExpressions.Number;
import tinycc.implementation.expression.UnaryExpressions.AddressExpression;
import tinycc.implementation.expression.UnaryExpressions.IndirectionExpression;
import tinycc.implementation.expression.UnaryExpressions.NegationExpression;
import tinycc.implementation.expression.UnaryExpressions.SizeOfExpression;
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
            return new Comp_And();
        else if (kind == TokenKind.ASTERISK)
            return new Multiplication();
        else if (kind == TokenKind.BANG_EQUAL)
            return new Comp_Unequal();
        else if (kind == TokenKind.EQUAL_EQUAL)
            return new Comp_Equals();
        else if (kind == TokenKind.EQUAL)
            return new Assign();
        else if (kind == TokenKind.GREATER_EQUAL)
            return new Comp_GreaterEqual();
        else if (kind == TokenKind.GREATER)
            return new Comp_Greater();
        else if (kind == TokenKind.LESS_EQUAL)
            return new Comp_LessEqual();
        else if (kind == TokenKind.LESS)
            return new Comp_Less();
        else if (kind == TokenKind.MINUS)
            return new Minus();
        else if (kind == TokenKind.PIPE_PIPE)
            return new Comp_Or();
        else if (kind == TokenKind.PLUS)
            return new Plus();
        else if (kind == TokenKind.SLASH)
            return new Division();
        else return null;
    }

    public static BinaryExpression createBinaryExpression(TokenKind kind, Expression left, Expression right) {
        BinaryOperator op = createBinaryOperator(kind);

        if (kind == TokenKind.AND_AND)
            return new AndExpression(op, left, right);
        else if (kind == TokenKind.ASTERISK)
            return new MultiplicationExpression(op, left, right);
        else if (kind == TokenKind.BANG_EQUAL)
            return new UnequalExpression(op, left, right);
        else if (kind == TokenKind.EQUAL_EQUAL)
            return new EqualsExpression(op, left, right);
        else if (kind == TokenKind.EQUAL)
            return new AssignExpression(op, left, right);
        else if (kind == TokenKind.GREATER_EQUAL)
            return new GreaterEqualExpression(op, left, right);
        else if (kind == TokenKind.GREATER)
            return new GreaterExpression(op, left, right);
        else if (kind == TokenKind.LESS_EQUAL)
            return new LessEqualExpression(op, left, right);
        else if (kind == TokenKind.LESS)
            return new LessExpression(op, left, right);
        else if (kind == TokenKind.MINUS)
            return new SubtractExpression(op, left, right);
        else if (kind == TokenKind.PIPE_PIPE)
            return new OrExpression(op, left, right);
        else if (kind == TokenKind.PLUS)
            return new AddExpression(op, left, right);
        else if (kind == TokenKind.SLASH)
            return new DivisionExpression(op, left, right);
        else return null;

    }

    public static UnaryOperator createUnaryOperator(Token token) {
        TokenKind kind = token.getKind();
        if (kind == TokenKind.ASTERISK)
            return new Indirection();
        else if (kind == TokenKind.AND)
            return new Address();
        else if (kind == TokenKind.SIZEOF)
            return new SizeOf();
        else if (kind == TokenKind.BANG)
            return new Negation();
        else
            return null;
    }

    public static UnaryExpression createUnaryExpression(Token token, Expression applicable) {
        TokenKind kind = token.getKind();
        UnaryOperator op = createUnaryOperator(token);

        if (kind == TokenKind.ASTERISK)
            return new IndirectionExpression(op, applicable);
        else if (kind == TokenKind.AND)
            return new AddressExpression(op, applicable);
        else if (kind == TokenKind.SIZEOF)
            return new SizeOfExpression(op, applicable);
        else if (kind == TokenKind.BANG)
            return new NegationExpression(op, applicable);
        else
            return null;
    }
}
