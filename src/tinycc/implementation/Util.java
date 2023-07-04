package tinycc.implementation;

import java.lang.reflect.Constructor;

import tinycc.parser.Token;
import tinycc.parser.TokenKind;

public class Util {

    public static final String CREATE_BASE_TYPE = "tinycc.implementation.type.BaseTypes.";
    public static final String CREATE_BINARY_OPERATOR = "tinycc.implementation.expression.BinaryOperators.";
    public static final String CREATE_PRIMARY_EXPRESSION = "tinycc.implementation.expression.PrimaryExpressions.";

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
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> T create(TokenKind kind, Class superclassType, String typePackage) {
        // extract text of kind
        String strKind = kind.name().toLowerCase();

        // capitalize first letter of string and rest lowercase
        String formattedKind = Util.capitalizeEnumName(strKind);

        // the package path of the base types
        String path = typePackage;

        // concat package path and kind
        String fullClassName = path + formattedKind;
        
        try {
            // create class with name from kind
            Class<?> cls = Class.forName(fullClassName);

            // check if cls is a sub class of superClassType (since only then typecasting should be possible)
            if (superclassType.isAssignableFrom(cls)) {
                // return new Object of type cls
                return (T) cls.getDeclaredConstructor().newInstance();
            }
        } catch (Exception e) {
            // TODO: what should i do on invalid input?
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> T create(Token token, Class superclassType) {
        // extract text of kind
        String strKind = token.getKind().name().toLowerCase();

        // capitalize first letter of string and rest lowercase
        String formattedKind = Util.capitalizeEnumName(strKind);

        // formatted kind contains if it is number or string...

        // the package path of the base types
        String path = CREATE_PRIMARY_EXPRESSION;

        // concat package path and kind
        String fullClassName = path + formattedKind;
        
        try {
            // create class with name from kind
            Class<?> cls = Class.forName(fullClassName);

            // check if cls is a sub class of superClassType (since only then typecasting should be possible)
            if (superclassType.isAssignableFrom(cls)) {
                // grab constructor since we need to pass a parameter to it
                Constructor<T> constructor = (Constructor<T>) cls.getDeclaredConstructor(token.getClass());
                // call constructor with parameter
                return constructor.newInstance(token);
            }
        } catch (Exception e) {
            // TODO: what should i do on invalid input?
        }
        return null;
    }
}
