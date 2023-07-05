package tinycc.implementation;

import tinycc.implementation.type.ObjectType;

/**
 * AllowedSignature
 * Class for representing the allowed signatures of operators
 * based on the provided tables from the Porject description
 */
public class Signature {
    public Class lOperandSuperClass;
    public Class rOperandSuperClass;
    public Class resultSuperClass;
    public String remark;

    public Signature(Class lOperand, Class rOperand, Class result) {
        this.lOperandSuperClass = lOperand;
        this.rOperandSuperClass = rOperand;
        this.resultSuperClass = result;
        this.remark = "";
    }

    public Signature(Class lOperand, Class rOperand, Class result, String remark) {
        this(lOperand, rOperand, result);
        this.remark = remark;
    }

    /**
     * Checks if the given operand is allowed for this signature
     * Note: roperand is null for unary operands, thus this function
     * only takes one operand as input
     * @param operand
     * @return if operand is allowed for this signature
     */
    public boolean isAllowed(Class operand) {
        return isAllowed(operand, null);
    }

    /**
     * Checks if the given operands are allowed for this signature
     * @param lOperand
     * @param rOperand
     * @return  if operands are allowed for this signature
     */
    public boolean isAllowed(Class lOperand, Class rOperand) {
        // TODO: make sure to implement check for unary operators, since one class is null
        return lOperandSuperClass.isAssignableFrom(lOperand) && rOperandSuperClass.isAssignableFrom(rOperand);
    }

    public boolean isAllowed(Signature signature) {
        // TODO: make sure to implement check for unary operators, since one class is null
        return isAllowed(signature.lOperandSuperClass, signature.rOperandSuperClass);
    }

    /**
     * @return the result type of this signature
     */
    public Class getResultType() {
        return resultSuperClass;
    }
}
