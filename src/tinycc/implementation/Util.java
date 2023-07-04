package tinycc.implementation;

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
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> T create(TokenKind kind, Class superclassType) {
        // extract text of kind
        String strKind = kind.getText();

        // capitalize first letter of string and rest lowercase
        String formattedKind = Util.capitalizeEnumName(strKind);

        // the package path of the base types
        String path = "tinycc.implementation.type.BaseTypes.";

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
}
