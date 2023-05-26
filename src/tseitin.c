#include "tseitin.h"

#include <stdio.h>

#include "err.h"
#include "util.h"

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
        VarIndex i = mkFreshVariable(vt);

        // make new clause with variable
        mkTernaryClause(vt, op_index, i, 0);

        // negate the VarIndex since negation is represented by negative index
        // of variable in vartable
        mkTernaryClause(vt, -op_index, -i, 0);

        // return varIndex of fresh variable
        return i;

    } else if (pf->kind == IMPLIES) {
        // implication
        // (P → Q) ↔ (¬P ∨ Q)
        //

        // negate first operand and store in f1
        PropFormula* f1 = mkUnaryFormula(NOT, pf->data.operands[0]);
        // store second operand in f2
        PropFormula* f2 = pf->data.operands[1];

        // create new formula for implication alternative
        PropFormula* impl = mkBinaryFormula(OR, f1, f2);

        // get varIndex for implication transformation
        VarIndex impl_index = addClauses(vt, cnf, impl);

        // return index of implication transformation
        return impl_index;

        // make transformation for implication
    } else if (pf->kind == EQUIV) {
        // Equivalenz
        // (P ↔ Q) ↔ (P ^ Q) v (¬P ^ ¬Q)
        //

        // negate first operand and store in f1
        PropFormula* f1 = pf->data.operands[0];
        // store second operand in f2
        PropFormula* f2 = pf->data.operands[1];

        // create new formula for equiv (left)
        PropFormula* equiv_l = mkBinaryFormula(AND, f1, f2);

        // negate f1 and f2
        f1 = mkUnaryFormula(NOT, f1);
        f2 = mkUnaryFormula(NOT, f2);

        // create new formula for equiv (right)
        PropFormula* equiv_r = mkBinaryFormula(AND, f1, f2);

        // create new formula for combining both

        PropFormula* equiv = mkBinaryFormula(OR, equiv_l, equiv_r);

        // get varIndex for equiv transformation
        VarIndex impl_index = addClauses(vt, cnf, equiv);

        // return index of equiv transformation
        return impl_index;

        // make transformation for equiv
    } else {
        // recursively check both operands and get resulting VarIndex
        VarIndex i1 = addClauses(vt, cnf, pf->data.operands[0]);
        VarIndex i2 = addClauses(vt, cnf, pf->data.operands[1]);

        // add binary clause with both varIndexes
        addBinaryClause(vt, cnf, i1, i2);

        // create a fresh variable to store clause in
        VarIndex i = mkFreshVariable(vt);

        // create ternary clause from binary operand indexes
        Clause* c = mkTernaryClause(vt, i1, i2, 0);

        // add clause to fresh variable parent clauses
        addParentClause(vt, i, c);

        // add created clause to cnf
        addClauseToCNF(cnf, c);

        // return varIndex of fresh variable
        return i;
    }
}

CNF* getCNF(VarTable* vt, const PropFormula* f) {
    CNF* res = mkCNF();

    VarIndex x = addClauses(vt, res, f);

    addUnaryClause(vt, res, x);

    return res;
}
