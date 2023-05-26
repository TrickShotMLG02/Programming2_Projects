#include "dpll.h"

#include "cnf.h"
#include "err.h"
#include "list.h"
#include "util.h"
#include "variables.h"

typedef enum Reason { CHOSEN, IMPLIED } Reason;

/**
 * Struct to represent an entry in the assignment stack. Should only be created
 * and freed by pushAssignment and popAssignment.
 */
typedef struct Assignment {
    VarIndex var;
    Reason reason;
} Assignment;

/**
 * Adds a new assignment to the assignment stack.
 *
 * @param stack  an assignment stack
 * @param var    the variable to assign
 * @param r      the reason for the assignment
 */
void pushAssignment(List* stack, VarIndex var, Reason r) {
    Assignment* a = (Assignment*)malloc(sizeof(Assignment));
    a->var = var;
    a->reason = r;
    push(stack, a);
}

/**
 * Removes the head element of an assignment stack and frees it.
 *
 * @param stack  an assignment stack
 */
void popAssignment(List* stack) {
    Assignment* a = (Assignment*)peek(stack);
    free(a);
    pop(stack);
}

/**
 * Function to negate a truth value
 */
TruthValue negateTruthValue(TruthValue val) {
    return (val == TRUE) ? FALSE : TRUE;
}

/**
 * Check if all clauses are satisfied
 */
int allSatisfied(CNF* cnf) {
    // create iterator to iterate over all clauses
    ListIterator clauseIterator = mkIterator(&cnf->clauses);

    // count number of clauses in stack
    int numberOfClauses = 0;

    // loop over all clauses in stack
    while (getCurr(&clauseIterator) != NULL) {
        // increase number of Clauses
        numberOfClauses++;

        // move to next clause in stack
        next(&clauseIterator);

        // check if there is not next clause
        if (clauseIterator.current == NULL) break;
    }

    // reset iterator
    clauseIterator = mkIterator(&cnf->clauses);

    // loop over all clauses in stack by number from before
    for (int i = 0; i < numberOfClauses; i++) {
        // get current clause from iterator
        Clause* c = getCurr(&clauseIterator);

        // check if clause is false
        if (c->val != TRUE) {
            // unsatisfied
            return 0;
        }

        // move to next clause in stack
        next(&clauseIterator);

        // exit loop if there is no next clause
        if (clauseIterator.current == NULL) break;
    }
    // satisfied
    return 1;
}

/**
 * Führt eine Iteration des DPLL Algorithmus aus.
 *
 * @param vt       die zugrunde liegende Variablentabelle
 * @param stack    der Zuweisungsstack
 * @param cnf      die zu prüfende Formel
 * @return         1 falls der Algorithmus mit SAT terminieren sollte,
 *                 0 falls der Algorithmus weiterlaufen sollte,
 *                 -1 falls der Algorithmus mit UNSAT terminieren sollte
 */
/**
 * Performs one iteration of the DPLL algorithm.
 *
 * @param vt       the underlying variable table
 * @param stack    an assignment stack
 * @param cnf      the formula to check
 * @return         1 if the algorithm should terminate with SAT,
 *                 0 if the algorithm should continue,
 *                -1 if the algorithm should terminate with UNSAT
 */
int iterate(VarTable* vt, List* stack, CNF* cnf) {
    int terminationCode = 0;

    // counts number of chosen values
    int numberOfCHOSEN = 0;

    // create VarIndex to hold next variable
    VarIndex nextVar = 0;

    // create iterator to iterate over stack
    ListIterator stackIterator = mkIterator(stack);

    Assignment* curAssignment;

    while (terminationCode == 0) {
        // check if all clauses are satisfied:
        if (allSatisfied(cnf)) {
            // abort with Satisfied
            return 1;
        }
        // if one clause is not satisfied
        else if (!allSatisfied(cnf)) {
            // check if there is at least one value which was chosen
            if (numberOfCHOSEN > 0) {
                // get current assignment from iterator
                curAssignment = getCurr(&stackIterator);

                // loop until a reason is CHOSEN
                while (curAssignment->reason == IMPLIED) {
                    // go back in stack
                    next(&stackIterator);

                    // remove assignment from stack
                    popAssignment(stack);
                    numberOfCHOSEN--;
                }

                // change reason from CHOSEN to IMPLIED in curAssignment
                // negate truthValue of variable and store back
                VarIndex index = curAssignment->var;
                TruthValue val = getVariableValue(vt, curAssignment->var);
                curAssignment->reason = IMPLIED;
                updateVariableValue(vt, index, negateTruthValue(val));
                numberOfCHOSEN--;
            } else {
                // abort with unsatisfied
                return -1;
            }
        }

        // if unit clause exists
        else if (getNextUndefinedVariable(vt) != 0) {
            // fulfill next clause by setting its value to true and to implied
            updateVariableValue(vt, nextVar, TRUE);
            pushAssignment(stack, nextVar, IMPLIED);
            numberOfCHOSEN--;
        }

        // select next free variable and set to true
        nextVar = getNextUndefinedVariable(vt);
        updateVariableValue(vt, nextVar, TRUE);
        // set variable as CHOSEN since it not a unit clause
        pushAssignment(stack, nextVar, CHOSEN);
        numberOfCHOSEN++;
    }
    return terminationCode;
}

char isSatisfiable(VarTable* vt, CNF* cnf) {
    List stack = mkList();

    int res;
    do {
        res = iterate(vt, &stack, cnf);
    } while (res == 0);

    while (!isEmpty(&stack)) {
        popAssignment(&stack);
    }

    return (res < 0) ? 0 : 1;
}
