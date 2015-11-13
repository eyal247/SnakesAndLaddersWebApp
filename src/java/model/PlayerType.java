/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author eyalen
 */
public enum PlayerType {
    HUMAN(1), COMPUTER(2);
    
    private final int value;
    
    private PlayerType(int value)
    {
        this.value = value;
    }
    
    public int intValue() { 
        return this.value; 
    }
    
    public static PlayerType fromIntToPlayerType(int number) {
        PlayerType retVal = null;
        
        for (PlayerType pt: PlayerType.values()) {
            if (pt.intValue() == number) {
                retVal = pt;
            }
        }
        
        return retVal;
    }          
}
