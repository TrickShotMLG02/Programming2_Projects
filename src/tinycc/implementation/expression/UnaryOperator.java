package tinycc.implementation.expression;

import tinycc.implementation.AllowedSignatureList;
import tinycc.implementation.Signature;
import tinycc.implementation.type.ObjectType;

public abstract class UnaryOperator {

    private Expression applicable;

    protected AllowedSignatureList allowedSignatures = new AllowedSignatureList();

    public UnaryOperator(Expression applicable) {
        this.applicable = applicable;
    }

    @Override
    public String toString() {
        return "Unary_";
    }  

    public Expression getApplicable() {
        return applicable;
    }

	public boolean isSignatureAllowed() {
        ObjectType lOp = applicable.lOperand;
		return allowedSignatures.checkSignature(new Signature(lOp.getClass(), null, null));
	}
}
