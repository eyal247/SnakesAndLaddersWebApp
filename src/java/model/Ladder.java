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
public class Ladder {

    private int topLocation;
    private int bottomLocation;

    public Ladder(int bottomLocation, int topLocation) {
        this.topLocation = topLocation;
        this.bottomLocation = bottomLocation;
    }

    public int getTop() {
        return topLocation;
    }

    public int getBottom() {
        return bottomLocation;
    }

    public void setTop(int topLocation) {
        this.topLocation = topLocation;
    }

    public void setBottom(int bottomLocation) {
        this.bottomLocation = bottomLocation;
    }
}
