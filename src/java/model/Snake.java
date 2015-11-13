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
public class Snake {

    private int headLocation;
    private int tailLocation;

    public Snake(int headLocation, int tailLocation) {
        this.headLocation = headLocation;
        this.tailLocation = tailLocation;
    }

    public int getHeadLocation() {
        return headLocation;
    }

    public int getTailLocation() {
        return tailLocation;
    }

    public void setHeadLocation(int headLocation) {
        this.headLocation = headLocation;
    }

    public void setTailLocation(int tailLocation) {
        this.tailLocation = tailLocation;
    }

}
