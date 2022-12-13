package com.example.asn4;

public class RectangleSelection {
    /** Stores the mouse position at the end of a mouse press event, just before a mouse drag event occurs
     * for blob selection through lasso tool or rectangle selection tool */
    private double startingX, startingY;

    /** Stores the mouse cursor (x, y) values for the rectangle selection */
    private double endingX, endingY;

    /** dimensions of the rectangle after the user has released the mouse and rectangle selection event is over.
     * This gets initialized with a value after the BlobView's creation of the rectangle selection */
    double left, top, width, height;


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

    public void setLeft(double left) {
        this.left = left;
    }

    public void setTop(double top) {
        this.top = top;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    // operations on data

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

    /**
     * Checks if a blob is within the rectangle selection
     * @param b blob
     * @return true if a blob is within the rectangle, false otherwise
     */
    public boolean contains(Blob b) {
        return b.x >= left && b.x <= left+width && b.y >= top && b.y <= top+height;
    }
}

