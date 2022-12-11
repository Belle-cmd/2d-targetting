package com.example.asn4;

public class RectangleSelection {
    /** Stores the mouse position at the end of a mouse press event, just before a mouse drag event occurs
     * for blob selection through lasso tool or rectangle selection tool */
    private double startingX, startingY;

    /** Stores the mouse cursor (x, y) values for the rectangle selection */
    private double endingX, endingY;


    public RectangleSelection() {}



    // getter and setter methods

    public double getStartingX() {
        return startingX;
    }

    public double getStartingY() {
        return startingY;
    }

    public void setStartingCursor(double newStartingX, double newStartingY) {
        this.startingX = newStartingX;
        this.startingY = newStartingY;
    }

    public double getEndingX() {
        return endingX;
    }

    public double getEndingY() {
        return endingY;
    }

    public void setEndingCursor(double newEndingX, double newEndingY) {
        this.endingX = newEndingX;
        this.endingY = newEndingY;
    }
}

