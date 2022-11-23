package com.example.asn4;

import java.util.ArrayList;
import java.util.List;

public class InteractionModel {

    List<IModelListener> subscribers;

    Blob selected;

    /** stores the dynamically changing viewport based on user's resizing */
    private double viewWidth;


    /**
     * Prepare subscribers that will listen to the interaction model (only the view for this lab)
     */
    public InteractionModel() {
        subscribers = new ArrayList<>();
    }



    public void addSubscriber(IModelListener sub) {
        subscribers.add(sub);
    }

    private void notifySubscribers() {
        subscribers.forEach(s -> s.iModelChanged());
    }

    public void setSelected(Blob b) {
        selected = b;
        notifySubscribers();
    }



    public void unselect() {
        selected = null;
    }

    public Blob getSelected() {
        return selected;
    }

    /**
     * Sets the new view width
     * @param w view width to save
     */
    public void setViewWidth(double w) {
        viewWidth = w;
    }
}

