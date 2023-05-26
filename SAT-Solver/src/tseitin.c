#include "tseitin.h"

#include <stdio.h>

#include "cnf.h"
#include "err.h"
#include "propformula.h"
#include "util.h"
#include "variables.h"

/**
 * Inserts a clause with one literal into the CNF.
 *
 * @param vt   the underlying variable table
 * @param cnf  a formula
 * @param a    a literal
 */
void addUnaryClause(VarTable* vt, CNF* cnf, Literal a) {
    Clause* clause = mkTernaryClause(vt, a, 0, 0);
    addClauseToCNF(cnf, clause);
}

/**
 * Inserts a clause with two literals into the CNF.
 *
 * @param vt   the underlying variable table
 * @param cnf  a formula
 * @param a    the first literal
 * @param b    the second literal
 */
void addBinaryClause(VarTable* vt, CNF* cnf, Literal a, Literal b) {
    Clause* clause = mkTernaryClause(vt, a, b, 0);
    addClauseToCNF(cnf, clause);
}

/**
 * Inserts a clause with three literals into the CNF.
 *
 * @param vt   the underlying variable table
 * @param cnf  a formula
 * @param a    the first literal
 * @param b    the second literal
 * @param c    the third literal
 */
void addTernaryClause(VarTable* vt, CNF* cnf, Literal a, Literal b, Literal c) {
    Clause* clause = mkTernaryClause(vt, a, b, c);
    addClauseToCNF(cnf, clause);
}

/**
 * Adds clauses for a propositional formula to a CNF.
 *
 * For a propositional formula pf, clauses that are added that are equivalent to
 *
 *     x <=> pf
 *
 * where x is usually a fresh variable. This variable is also returned.
 *
 * @param vt   the underlying variable table
 * @param cnf  a formula
 * @param pf   a propositional formula
 * @return     the variable x, as described above
 */
VarIndex addClauses(VarTable* vt, CNF* cnf, const PropFormula* pf) {
    // if formula is just a variable (literal) addUnaryClause and return the
    // varIndex of the variable
    if (pf->kind == VAR) {
        // return varIndex of variable
        return pf->data.var;

    } else if (pf->kind == NOT) {
        // recursively check sub formula of not and get VarIndex
        VarIndex op_index = addClauses(vt, cnf, pf->data.single_op);

        // create a fresh variable to store clause in
        VarIndex newVar = mkFreshVariable(vt);

        //
        // implement binary clauses for Transformation of NOT
        // x1 <=> ¬a = (¬x1 v ¬a) ^ (a v x1)
        //
        addBinaryClause(vt, cnf, -op_index, -newVar);
        addBinaryClause(vt, cnf, op_index, newVar);

        // return varIndex of new variable
        return newVar;
    } else if (pf->kind == AND) {
        // recursively check sub formulas of AND and get VarIndexes
        VarIndex l_var = addClauses(vt, cnf, pf->data.operands[0]);
        VarIndex r_var = addClauses(vt, cnf, pf->data.operands[1]);

        // create new Variable
        VarIndex newVar = mkFreshVariable(vt);

        //
        // implement binary clauses for Transformation of AND
        // x1 <=> (a ^ b) = (¬x1 v a) ^ (¬x1 v b) ^ (¬a v ¬b v x1)
        //
        addBinaryClause(vt, cnf, -newVar, l_var);
        addBinaryClause(vt, cnf, -newVar, r_var);
        addTernaryClause(vt, cnf, -l_var, -r_var, newVar);

        // return varIndex of new variable
        return newVar;

    } else if (pf->kind == OR) {
        // recursively check sub formulas of OR and get VarIndexes
        VarIndex l_var = addClauses(vt, cnf, pf->data.operands[0]);
        VarIndex r_var = addClauses(vt, cnf, pf->data.operands[1]);

        // create new Variable
        VarIndex newVar = mkFreshVariable(vt);

        //
        // implement binary clauses for Transformation of OR
        // x1 <=> (a v b) = (¬x1 v a v b) ^ (¬a v x1) ^ (¬b v x1)
        //
        addTernaryClause(vt, cnf, -newVar, l_var, r_var);
        addBinaryClause(vt, cnf, -l_var, newVar);
        addBinaryClause(vt, cnf, -r_var, newVar);

        // return varIndex of new variable
        return newVar;
    } else if (pf->kind == EQUIV) {
        // recursively check sub formulas of EQUIV and get VarIndexes
        VarIndex l_var = addClauses(vt, cnf, pf->data.operands[0]);
        VarIndex r_var = addClauses(vt, cnf, pf->data.operands[1]);

        // create new Variable
        VarIndex newVar = mkFreshVariable(vt);

        //
        // implement binary clauses for Transformation of EQUIVALENT
        // x1 <=> (a <=> b) = (¬x1 v ¬a v b) ^ (¬x1 v ¬b v a) ^ (x1 v ¬a v ¬b) ^
        // (x1 v a v b)
        //
        addTernaryClause(vt, cnf, -newVar, -l_var, r_var);
        addTernaryClause(vt, cnf, -newVar, -r_var, l_var);
        addTernaryClause(vt, cnf, newVar, -l_var, -r_var);
        addTernaryClause(vt, cnf, newVar, l_var, r_var);

        // return varIndex of new variable
        return newVar;
    }
    // else if (pf->kind == IMPLIES) {
    else {
        // recursively check sub formulas of IMPLIES and get VarIndexes
        VarIndex l_var = addClauses(vt, cnf, pf->data.operands[0]);
        VarIndex r_var = addClauses(vt, cnf, pf->data.operands[1]);

        // create new Variable
        VarIndex newVar = mkFreshVariable(vt);

        //
        // implement binary clauses for Transformation of IMPLICATION
        // x1 <=> (a => b) = (¬x1 v ¬a v  b) ^ (a v x1) ^ (¬b v x1)
        //
        addTernaryClause(vt, cnf, -newVar, -l_var, r_var);
        addBinaryClause(vt, cnf, l_var, newVar);
        addBinaryClause(vt, cnf, -r_var, newVar);

        // return varIndex of new variable
        return newVar;
    }
}

CNF* getCNF(VarTable* vt, const PropFormula* f) {
    CNF* res = mkCNF();

    VarIndex x = addClauses(vt, res, f);

    addUnaryClause(vt, res, x);

    return res;
}
