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

    Literal cVar;
    int Count = 0;

    // set all unit clauses to IMPLIED
    while (clauseIterator.current != NULL) {
        // check if current clause is a unit clause

        Clause* c = getCurr(&clauseIterator);

        cVar = getUnitLiteral(vt, c);

        if (cVar != 0) {
            // set cVar variable to TRUE

            cVar = abs(cVar);

            updateVariableValue(vt, cVar, TRUE);
            // check if clause is true else set cVAR to FALSE
            if (c->val != TRUE) {
                updateVariableValue(vt, cVar, FALSE);
            }
            // set Variable to IMPLIED
            pushAssignment(stack, cVar, IMPLIED);

            // move clauseIterator

            Count++;
        }
        next(&clauseIterator);
        // err("sas");
    }

    return (Count == 0) ? 0 : 1;
}

/**
 * Check if there exists any clause which has truthvalue FALSE
 */
int existsClauseFalse(CNF* cnf) {
    ListIterator clauseIterator = mkIterator(&cnf->clauses);

    while (clauseIterator.current != NULL) {
        // check if current clause is FALSE and return 1
        if (((Clause*)clauseIterator.current)->val == FALSE) {
            return 1;
        }
        // move clauseIterator
        next(&clauseIterator);
    }
    // return 0 since there was no clause which was false
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
    // counts number of chosen values
    int numberOfCHOSEN = 0;

    // create VarIndex to hold next variable
    VarIndex nextVar = 0;

    // create iterator to iterate over stack
    ListIterator stackIterator = mkIterator(stack);

    Assignment* curAssignment;

    while (1) {
        // check if all clauses are fulfilled
        if (allSatisfied(cnf)) {
            return 1;
        }

        // check if one clause is false (NOT TRUE OR UNDEFINED)
        if (existsClauseFalse(cnf)) {
            // check if reset possible
            //
            //
            //
            // check if there is at least one value which was chosen
            if (numberOfCHOSEN > 0) {
                // get current assignment from iterator
                curAssignment = getCurr(&stackIterator);

                // loop until a reason is CHOSEN
                while (curAssignment->reason == IMPLIED) {
                    // go back in stack
                    next(&stackIterator);
                    curAssignment = getCurr(&stackIterator);
                    // remove assignment from stack
                    popAssignment(stack);

                    // end iteration
                    // return 0;
                }

                // change reason from CHOSEN to IMPLIED in curAssignment
                // negate truthValue of variable and store back
                VarIndex index = curAssignment->var;
                if (index != 0) {
                    TruthValue val = getVariableValue(vt, curAssignment->var);
                    curAssignment->reason = IMPLIED;
                    updateVariableValue(vt, index, negateTruthValue(val));
                    numberOfCHOSEN--;
                }

            } else {
                // abort with unsatisfied
                return -1;
            }
        }

        // check if there are unit clauses and fulfill all
        if (fulfillAllUnitClauses(vt, stack, cnf)) {
            //  return 0 to end current iteration
            return 0;
        }

        // select next free variable and set to true
        nextVar = getNextUndefinedVariable(vt);
        if (nextVar != 0) {
            updateVariableValue(vt, nextVar, TRUE);
            // set variable as CHOSEN since it not a unit clause
            pushAssignment(stack, nextVar, CHOSEN);
            numberOfCHOSEN++;
        }
    }
    return -1;
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
