package com.example.asn4;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class InteractionModel {

    private List<IModelListener> blobSubscribers;

    /** class subscribers that listen to interaction model, for the lasso and rectangle selection tool */
    private List<IModelListener> selectionSubscribers;

    /** Stores multiple selected blobs */
    private ArrayList<Blob> selectedBlobs;

    /** stores the dynamically changing viewport based on user's resizing */
    private double viewWidth;

    /** radius of the circle following the mouse around */
    private double areaRadius = 75;

    /** stores a singly selected blob */
    private Blob selected;



    /** Stores the mouse cursor (x, y) */
    private double mouseCursorX, mouseCursorY;

    /** lasso instance */
    private LassoSelection lassoSelection;

    private RectangleSelection rectSelection;



    /**
     * Prepare subscribers that will listen to the interaction model (only the view for this lab)
     */
    public InteractionModel() {
        blobSubscribers = new ArrayList<>();
        selectionSubscribers = new ArrayList<>();
        selectedBlobs = new ArrayList<>();

        lassoSelection = new LassoSelection();
        rectSelection = new RectangleSelection();
    }



    public void addBlobSubscriber(IModelListener sub) {
        blobSubscribers.add(sub);
    }

    public void addSelectionSubscriber(IModelListener sub) {selectionSubscribers.add(sub);}

    private void notifyBlobSubscribers() {
        blobSubscribers.forEach(IModelListener::iModelChanged);
    }

    private void notifySelectionSubscribers() {
        selectionSubscribers.forEach(IModelListener::iModelChangedSelection);
    }


    // getter and setter methods

    public void setViewWidth(double w) {
        viewWidth = w;
    }

    public double getCursorRadius() {
        return areaRadius;
    }

    public ArrayList<Blob> getSelectedBlobs() {
        return selectedBlobs;
    }

    public double getMouseCursorX() {
        return mouseCursorX;
    }

    public void setMouseCursorX(double mouseCursorX) {
        this.mouseCursorX = mouseCursorX;
    }

    public double getMouseCursorY() {
        return mouseCursorY;
    }

    public void setMouseCursorY(double mouseCursorY) {
        this.mouseCursorY = mouseCursorY;
    }





    // methods for single blob selection

    public Blob getSelected() {
        return selected;
    }

    public void setSelected(Blob b) {
        selected = b;
        notifyBlobSubscribers();
    }

    public void unselect() {
        selected = null;
        notifyBlobSubscribers();
    }





    // methods for multiple blob selection

    /**
     * Checks if a given blob is part of iModel's selected blobs list
     * @param b blob
     * @return true if a blob exists in iModel's list, false otherwise
     */
    public boolean isSelected(Blob b) {
        return selectedBlobs.contains(b);
    }

    /**
     * The selected blobs in the controller are stored into the iModel's list of selected blobs, so that more
     * selected blobs can be stored
     * @param hitList selected blobs created in the controller
     */
    public void select(ArrayList<Blob> hitList) {
        hitList.forEach(this::updateSelected);
        notifyBlobSubscribers();
    }

    /**
     * Clears all the selected blobs stored in the iModel
     */
    public void clearBlobList() {
        selectedBlobs.clear();
    }

    /**
     * If blob is already selected, unselect (vice versa)
     * @param b selected blob
     */
    private void updateSelected(Blob b) {
        if (selectedBlobs.contains(b)) {
            selectedBlobs.remove(b);
        } else {
            selectedBlobs.add(b);
        }
        notifyBlobSubscribers();
    }

    /**
     * Checks
     * @param hitList list of selected blobs at the controller
     * @return true if all selected blobs at controller is contained in the iModel, false otherwise
     */
    public boolean allSelectedBlobs(ArrayList<Blob> hitList) {
        return selectedBlobs.containsAll(hitList);
    }



    // METHODS FOR SELECTION

    public double getRectStartingX() {
        return rectSelection.getStartingX();
    }

    public double getRectStartingY() {
        return rectSelection.getStartingY();
    }

    public double getRectEndingX() {
        return rectSelection.getEndingX();
    }

    public double getRectEndingY() {
        return rectSelection.getEndingY();
    }

    public void setRectStartingPoint(double newX, double newY) {
        rectSelection.setStartingCursor(newX, newY);
        notifySelectionSubscribers();
    }

    public void setRectEndingPoint(double newX, double newY) {
        rectSelection.setEndingCursor(newX, newY);
        notifySelectionSubscribers();
    }

    public double getLassoXCoordinate() {
        return lassoSelection.getCursorX();
    }

    public double getLassoYCoordinate() {
        return lassoSelection.getCursorY();
    }

    public boolean getLassoPathStatus() {
        return lassoSelection.isPathComplete();
    }

    public void setLassoPathStatus(boolean newStatus) {
        lassoSelection.setPathComplete(newStatus);
        notifySelectionSubscribers();
    }

    public void setLassoPoint(double newX, double newY) {
        lassoSelection.setCursor(newX, newY);
        notifySelectionSubscribers();
    }

    /**
     * Add a new point to the list of points that make up the lasso tool
     * @param point2D point
     */
    public void addPoints(Point2D point2D) {
        lassoSelection.addPoint(point2D);
        notifySelectionSubscribers();
    }

    public void clearPoints() {
        lassoSelection.clearPoints();
        notifySelectionSubscribers();
    }

    public List<Point2D> getPoints() {
       return lassoSelection.getPoints();
    }
}

