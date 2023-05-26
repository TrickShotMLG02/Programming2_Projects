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
 * Check if there are any unit clauses and fill them with the corresponding
 * values
 */
int fulfillAllUnitClauses(VarTable* vt, List* stack, CNF* cnf) {
    ListIterator clauseIterator = mkIterator(&cnf->clauses);

    // create temporary literal storage
    Literal cVar;
    // create counter to check if there was at least one change
    int Count = 0;

    // loop over all clauses
    while (clauseIterator.current != NULL) {
        // get clause from current iteration
        Clause* c = getCurr(&clauseIterator);

        // try to get UnitLiteral from clause
        cVar = getUnitLiteral(vt, c);

        // check if literal existed
        if (cVar != 0) {
            // convert negated variable literal to positive varIndex
            cVar = abs(cVar);

            // set variable to True
            updateVariableValue(vt, cVar, TRUE);
            // check if clause is now satisfied else set cVAR to FALSE
            if (c->val != TRUE) {
                updateVariableValue(vt, cVar, FALSE);
            }
            // set Variable to IMPLIED
            pushAssignment(stack, cVar, IMPLIED);

            // increment counter since variable was changed
            Count++;
        }
        // move clauseIterator
        next(&clauseIterator);
    }

    // return 0 if nothing was changed, else 1
    return (Count == 0) ? 0 : 1;
}

/**
 * check if any values are marked as chosen in stack
 */
int checkForAnyChosenValues(List* stack) {
    // create new iterator for stack
    ListIterator stackIterator = mkIterator(stack);

    // check if current clause is a unit clause
    while (stackIterator.current != NULL) {
        // get data of current stack iterator position
        Assignment* a = getCurr(&stackIterator);

        // check if variable is chosen
        if (a->reason == CHOSEN) {
            // return 1 since at least one variable is marked as chosen
            return 1;
        }
    }

    // return 0 since no variable is marked as chosen
    return 0;
}

/**
 * Check if there exists any clause which has truthvalue FALSE
 */
int existsClauseFalse(CNF* cnf) {
    // create iterator for all clauses
    ListIterator clauseIterator = mkIterator(&cnf->clauses);

    // iterate over all clauses
    while (clauseIterator.current != NULL) {
        // check if current clause is FALSE and return 1
        if (((Clause*)clauseIterator.current)->val == FALSE) {
            return 1;
        }
        // move clauseIterator
        next(&clauseIterator);
    }
    // return 0 since there was no clause which was FALSE
    return 0;
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
    // track if changes were made to variables
    int isDirty = 1;

    // create VarIndex to hold next variable
    VarIndex nextVar = 0;

    // infinite loop to process whole formula
    while (1) {
        // set flag to 0 since no changes were made yet
        isDirty = 0;

        // check if all clauses are fulfilled
        if (allSatisfied(cnf)) {
            return 1;
        }

        // check if one clause is false (NOT TRUE OR UNDEFINED)
        if (existsClauseFalse(cnf)) {
            // check if reset possible
            if (checkForAnyChosenValues(stack)) {
                // reset
                // loop until stack is empty
                while (!isEmpty(stack)) {
                    Assignment* temp = peek(stack);

                    // check if variable is chosen
                    if (temp->reason == CHOSEN) {
                        TruthValue tv = getVariableValue(vt, temp->var);
                        updateVariableValue(vt, temp->var,
                                            negateTruthValue(tv));
                        // change reason to IMPLIED, since it didn't work with
                        // previous variable
                        temp->reason = IMPLIED;

                        // changed some variable values in the current iteration
                        isDirty = 1;

                        // exit while loop
                        break;
                    } else {
                        // Variable was IMPLIED, set it to UNDEFINED
                        updateVariableValue(vt, temp->var, UNDEFINED);

                        // remove variable from stack
                        pop(stack);

                        // changed some variable values in the current iteration
                        isDirty = 1;
                    }
                }
            } else {
                // exit while loop
                break;
            }
        }

        // check if there are unit clauses and fulfill all
        if (fulfillAllUnitClauses(vt, stack, cnf)) {
            // changed some variable values in the current iteration
            isDirty = 1;
        } else {
            // select next free variable and set to true
            nextVar = getNextUndefinedVariable(vt);
            if (nextVar != 0) {
                // set variable to TRUE
                updateVariableValue(vt, nextVar, TRUE);
                // set variable as CHOSEN since it not a unit clause
                pushAssignment(stack, nextVar, CHOSEN);
                // changed some variable values in the current iteration
                isDirty = 1;
            } else {
                // exit while loop
                break;
            }
        }
    }

    // check if some variables have changed since beginning of current iteration
    if (isDirty == 0) {
        // terminate since fomrula is unsatisfiable
        return -1;
    } else {
        // end iteration
        return 0;
    }
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
