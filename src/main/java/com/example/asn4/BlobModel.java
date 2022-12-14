package com.example.asn4;

import java.util.ArrayList;
import java.util.List;

public class BlobModel {
    private List<BlobModelListener> subscribers;
    private List<Blob> blobs;

    public BlobModel() {
        subscribers = new ArrayList<>();
        blobs = new ArrayList<>();
    }



    public void addSubscriber(BlobModelListener sub) {
        subscribers.add(sub);
    }

    private void notifySubscribers() {
        subscribers.forEach(BlobModelListener::modelChanged);
    }



    // getter and setter methods

    public List<Blob> getBlobs() {
        return blobs;
    }



    // methods for manipulating data

    /**
     * Used by CreateCommand to create a new blob
     * @param newX new blob x coordinate
     * @param newY new blob y coordinate
     * @return newly created blob
     */
    public Blob createBlob(double newX, double newY) {
        Blob b = new Blob(newX, newY);
        blobs.add(b);
        notifySubscribers();
        return b;
    }

    /**
     * Adds a new blob to the list of all blobs in the application, requiring the blob object as an argument
     * @param newBlob blob object to be stored
     */
    public void addBlob(Blob newBlob) {
        blobs.add(newBlob);
        notifySubscribers();
    }

    /**
     * Adds a new blob to the list of all blobs in the application, requiring coordinates as an argument
     * @param x
     * @param y
     */
    public void addBlob(double x, double y) {
        blobs.add(new Blob(x,y));
        notifySubscribers();
    }

    public void moveBlob(Blob b, double dx, double dy) {
        b.move(dx,dy);
        notifySubscribers();
    }

    public void moveBlobs(ArrayList<Blob> blobs, double dX, double dY) {
        blobs.forEach(b -> b.move(dX, dY));
        notifySubscribers();
    }

    public void deleteBlob(Blob blob) {
        blobs.remove(blob);
        notifySubscribers();
    }

    /**
     * Checks if the mouse is within the blob hitbox
     */
    public boolean hitBlob(double x, double y) {
        for (Blob b : blobs) {
            if (b.contains(x,y)) return true;
        }
        return false;
    }

    /**
     * Distinguish which blob was pressed
     */
    public Blob whichHit(double x, double y) {
        for (Blob b : blobs) {
            if (b.contains(x,y)) return b;
        }
        return null;
    }
}
