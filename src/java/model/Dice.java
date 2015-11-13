/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.Random;
import static model.GameConstants.*;

/**
 *
 * @author EyalEngel
 */
public class Dice {

    private final Random rnd;
    private final int maxNumber;
    private final int minNumber;
    private int diceResult;

    public Dice() {
        this.rnd = new Random();
        this.maxNumber = MAX_DICE_NUMBER;
        this.minNumber = MIN_DICE_NUMBER;
    }

    public int rollDice() {
        diceResult = rnd.nextInt(this.maxNumber) + this.minNumber;
        return diceResult;
    }

    public int getMaxNumber() {
        return this.maxNumber;
    }

    public int getMinNumber() {
        return this.minNumber;
    }
    
    public int getDiceResult(){
        return diceResult;
    }

}
