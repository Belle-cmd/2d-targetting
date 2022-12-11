package com.example.asn4;

import javafx.geometry.Point2D;
import java.util.ArrayList;
import java.util.List;

public class LassoSelection {
    /** Store points of the lasso tool */
    private List<Point2D> points;

    /** Stores the mouse position at the end of a mouse press event, just before a mouse drag event occurs
     * for blob selection through lasso tool */
    private double cursorX, cursorY;

    /** Indicates when drawing circles is currently occurring or not */
    private boolean pathComplete;


    public LassoSelection() {
        points = new ArrayList<>();
    }




    // getter and setter method

    public List<Point2D> getPoints() {
        return points;
    }

    public double getCursorX() {
        return cursorX;
    }

    public double getCursorY() {
        return cursorY;
    }

    public boolean isPathComplete() {
        return pathComplete;
    }

    public void setPathComplete(boolean pathComplete) {
        this.pathComplete = pathComplete;
    }

    public void setCursor(double newStartingX, double newStartingY) {
        this.cursorX = newStartingX;
        this.cursorY = newStartingY;
    }


    public void addPoint(Point2D p) {
        points.add(p);
    }

    /**
     * Removes all points of the lasso tool
     */
    public void clearPoints() {
        points.clear();
    }
}
