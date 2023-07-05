package tinycc.implementation;

import java.util.ArrayList;
import java.util.List;

public class AllowedSignatureList {
    
    private List<Signature> allowedSignatures;

    public AllowedSignatureList() {
        allowedSignatures = new ArrayList<Signature>();
    }

    /**
     * Add a signature to the list of allowed signatures
     * @param signature
     */
    public void add(Signature signature) {
        allowedSignatures.add(signature);
    }

    /**
     * Checks if the given signature is allowed
     * @param signature
     * @return
     */
    public boolean checkSignature(Signature signature) {
        boolean allowed = false;
        for (Signature allowedSignature : allowedSignatures) {
            if (allowedSignature.isAllowed(signature)) {
                allowed = true;
                break;
            }
        }
        return allowed;
    }

}
