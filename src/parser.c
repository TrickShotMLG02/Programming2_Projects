#include "parser.h"

#include <stdlib.h>
#include <string.h>

#include "err.h"
#include "lexer.h"
#include "list.h"
#include "propformula.h"
#include "util.h"
#include "variables.h"

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
    else if (!strcmp(str, "<=>"))
        return EQUIV;
    else if (!strcmp(str, "=>"))
        return IMPLIES;
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

    // implement a stack for formula storing
    List fStack = mkList();

    // grab token string from formula
    char* tokenName = nextToken(input);

    // create PropFormula to store result

    // loop over all tokens until NULL since NULL is end of formula
    while (tokenName != NULL) {
        FormulaKind kind = toKind(tokenName);
        // index of last variable
        // VarIndex vi = getNextUndefinedVariable(vt);

        // switch on kind
        if (kind == VAR) {
            // create variable formula
            PropFormula* i = mkVarFormula(vt, tokenName);
            i->kind = kind;
            // put variable formula on stack
            push(&fStack, i);

            // free(i);

        } else if (kind == NOT) {
            // kind is unary operator and uses last variable from stack?
            PropFormula* op = mkUnaryFormula(kind, peek(&fStack));
            op->kind = kind;
            // remove top most formula since it was processed
            pop(&fStack);

            if (op == NULL) {
                err("missing operand");
            }

            // push formula stored in f to stack
            push(&fStack, op);

        } else if (kind == AND || kind == OR || kind == IMPLIES ||
                   kind == EQUIV) {
            // grab last formula from stack
            PropFormula* r_op = peek(&fStack);
            // remove last formula from stack
            pop(&fStack);

            // grab last formula from stack
            PropFormula* l_op = peek(&fStack);
            // remove last formula from stack
            pop(&fStack);

            if (l_op == NULL || r_op == NULL) {
                err("missing operand 1 or 2");
            }

            PropFormula* f = mkBinaryFormula(kind, l_op, r_op);
            f->kind = kind;
            push(&fStack, f);
        } else {
            err(tokenName);
        }
        tokenName = nextToken(input);
    }
    PropFormula* res = (peek(&fStack));
    pop(&fStack);

    if (!isEmpty(&fStack)) {
        err("stack");
    }

    clearList(&fStack);

    return res;
}
