#include "parser.h"

#include <string.h>

#include "err.h"
#include "lexer.h"
#include "list.h"
#include "util.h"

/**
 * Assigns symbols to strings.
 *
 * Aborts the program with an error message if an invalid input is detected.
 *
 * @param str  a string to translate
 * @return     the resulting symbol
 */
FormulaKind toKind(const char* str) {
    // str can contain &&, ||, ->, <->, !, or a variable name

    if (!strcmp(str, "&&"))
        return AND;
    else if (!strcmp(str, "||"))
        return OR;
    else if (!strcmp(str, "->"))
        return IMPLIES;
    else if (!strcmp(str, "<->"))
        return EQUIV;
    else if (!strcmp(str, "!"))
        return NOT;
    else {
        // should be a variable. check for correct name ([a-zA-Z0-9]+)
        int index = 0;
        while (str[index] != '\0') {
            // check if char is correct format else return error
            char c = str[index];
            if ((c >= 48 && c <= 57) || (c >= 65 && c <= 90) ||
                (c >= 97 && c <= 122)) {
                index++;
            } else {
                err("invalid char");
            }
        }
        return VAR;
    }
}

PropFormula* parseFormula(FILE* input, VarTable* vt) {
    // TODO Implement me!

    // grab token string from formula
    char* tokenName = nextToken(input);
    VarIndex varCount = 0;
    // loop over all tokens until NULL since NULL is end of formula
    while (tokenName != NULL) {
        FormulaKind kind = toKind(tokenName);
        // index of last variable
        // VarIndex vi = getNextUndefinedVariable(vt);
        // switch on kind
        switch (kind) {
            case VAR:
                // create variable and store in vartable
                mkVariable(vt, tokenName);
                varCount++;
                break;
            case NOT:
                // kind is unary operator and uses last variable from stack?
                PropFormula* f =
                    mkVarFormula(vt, getVariableName(vt, varCount));
                mkUnaryFormula(kind, f);

            default:
                // kind is any other binary operand and uses last two variables
                // from stack?
                mkBinaryFormula(
                    kind, mkVarFormula(vt, getVariableName(vt, varCount)),
                    mkVarFormula(vt, getVariableName(vt, varCount - 1)));
                break;
        }

        tokenName = nextToken(input);
    }

    // NOT_IMPLEMENTED;
    // UNUSED(input);
    // UNUSED(vt);
}
