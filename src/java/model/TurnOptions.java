/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author shaiyahleba
 */
public enum TurnOptions {
    
    ROLL("R"), SAVE("S"), SAVE_AS("SA"), QUIT("Q");

    private final String value;

    private TurnOptions(String str) {
        this.value = str;
    }

    public String strValue() {
        return this.value;
    }

    public static TurnOptions fromStringToEnum(String option) {
        TurnOptions retVal = null;
        
        for (TurnOptions opt : TurnOptions.values()) {
            if (opt.strValue().equals(option)) {
                retVal = opt;
                break;
            }
        }

        return retVal;
    }
}
