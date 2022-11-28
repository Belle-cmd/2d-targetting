package com.example.asn4;

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

    /** Stores the dimensions of the rectangle selection */
    private  double boxLft, boxTop, boxWidth, boxHeight;

    /** Stores the mouse position at the end of a mouse press event, just before a mouse drag event occurs
     * for blob selection through lasso tool or rectangle selection tool */
    private double beforeLassoRectX, getBeforeLassoRectY;

    /** Stores the mouse cursor (x, y) values */
    private double cursorX, cursorY;



    /**
     * Prepare subscribers that will listen to the interaction model (only the view for this lab)
     */
    public InteractionModel() {
        blobSubscribers = new ArrayList<>();
        selectionSubscribers = new ArrayList<>();
        selectedBlobs = new ArrayList<>();
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

    public double getBeforeLassoRectX() {
        return beforeLassoRectX;
    }

    public void setBeforeLassoRectX(double beforeLassoRectX) {
        this.beforeLassoRectX = beforeLassoRectX;
    }

    public double getGetBeforeLassoRectY() {
        return getBeforeLassoRectY;
    }

    public void setGetBeforeLassoRectY(double getBeforeLassoRectY) {
        this.getBeforeLassoRectY = getBeforeLassoRectY;
    }

    public double getCursorX() {
        return cursorX;
    }

    public double getCursorY() {
        return cursorY;
    }

    public void setCursorRedraw(double x, double y) {
        cursorX = x;
        cursorY = y;
        notifySelectionSubscribers();
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
}

