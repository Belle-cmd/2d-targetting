package com.example.asn4;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        System.out.println("Blob deleted in the model");
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

    public ArrayList<Blob> hitArea(double x, double y, double cursorRadius) {
        return (ArrayList<Blob>) blobs.stream().filter(b -> b.contains(x, y, cursorRadius)).collect(Collectors.toList());
    }
}
