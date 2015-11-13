/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author EyalEngel
 */
public class Token {

    private int currentSquareNum;
    private final String color;

    public Token(String playerColor, int currentSquareNum) {
        this.currentSquareNum = currentSquareNum;
        this.color = playerColor;
    }

    public void setCurrentSquareNum(int currentSquare) {
        this.currentSquareNum = currentSquare;
    }

    public int getCurrentSquareNum() {
        return currentSquareNum;
    }

    public String getColor() {
        return color;
    }
}
