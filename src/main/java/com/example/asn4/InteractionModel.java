package com.example.asn4;

import java.util.ArrayList;
import java.util.List;

public class InteractionModel {

    List<IModelListener> subscribers;

    /** Stores multiple selected blobs */
    ArrayList<Blob> selectedBlobs;

    /** stores the dynamically changing viewport based on user's resizing */
    private double viewWidth;

    /** radius of the circle following the mouse around */
    private double areaRadius = 75;

    /** stores a singly selected blob */
    private Blob selected;




    /**
     * Prepare subscribers that will listen to the interaction model (only the view for this lab)
     */
    public InteractionModel() {
        subscribers = new ArrayList<>();
        selectedBlobs = new ArrayList<>();
    }



    public void addSubscriber(IModelListener sub) {
        subscribers.add(sub);
    }

    private void notifySubscribers() {
        subscribers.forEach(s -> s.iModelChanged());
    }



    // getter and setter methods

    /**
     * Sets the new view width
     * @param w view width to save
     */
    public void setViewWidth(double w) {
        viewWidth = w;
    }

    public double getCursorRadius() {
        return areaRadius;
    }

    public ArrayList<Blob> getSelectedBlobs() {
        return selectedBlobs;
    }





    // methods for single blob selection

    public Blob getSelected() {
        return selected;
    }

    public void setSelected(Blob b) {
        selected = b;
        notifySubscribers();
    }

    public void unselect() {
        selected = null;
        notifySubscribers();
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
        notifySubscribers();
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
        notifySubscribers();
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

