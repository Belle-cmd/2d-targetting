package com.example.asn4;

import javafx.geometry.Point2D;
import javafx.scene.image.PixelReader;

import java.util.ArrayList;
import java.util.List;

public class InteractionModel {

    private List<IModelListener> blobSubscribers;

    /** class subscribers that listen to interaction model, for the lasso and rectangle selection tool */
    private List<IModelListener> selectionSubscribers;

    /** Stores multiple selected blobs */
    private ArrayList<Blob> selectedBlobs;

    /** radius of the circle following the mouse around */
    private double areaRadius = 75;

    /** stores a singly selected blob */
    private Blob selected;

    /** Stores the mouse cursor (x, y) at drag during a tool selection event */
    private double dragMouseCursorX, dragMouseCursorY;

    /** Stores the mouse cursor (x, y) per mouse movement in the canvas */
    private double mouseCursorX, mouseCursorY;

    /** lasso instance */
    private LassoSelection lassoSelection;

    /** rectangle tool instance */
    private RectangleSelection rectSelection;

    /** Everytime the lasso/rectangle tool is used to create a selection area, the canvas holding that gets a snapshot
     * image. That snapshot image from the BlobView is stored here, so that it can be used by the lasso/rect tool */
    private PixelReader canvasSnapshot;



    public InteractionModel() {
        blobSubscribers = new ArrayList<>();
        selectionSubscribers = new ArrayList<>();
        selectedBlobs = new ArrayList<>();

        lassoSelection = new LassoSelection();
        rectSelection = new RectangleSelection();
        canvasSnapshot = null;
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





    // GETTER AND SETTER METHODS FOR DATA STORED IN INTERACTION MODEL

    public ArrayList<Blob> getSelectedBlobs() {
        return selectedBlobs;
    }

    /**
     * Used to retrieve the mouse cursor during a drag event for a rectangle/lasso tool selection
     * @return x mouse coordinate
     */
    public double getDragMouseCursorX() {
        return dragMouseCursorX;
    }

    /**
     * Used to set a new mouse cursor value during a drag event for a rectangle/lasso tool selection
     */
    public void setDragMouseCursorX(double dragMouseCursorX) {
        this.dragMouseCursorX = dragMouseCursorX;
    }

    /**
     * Used to retrieve the mouse cursor during a drag event for a rectangle/lasso tool selection
     * @return Y mouse coordinate
     */
    public double getDragMouseCursorY() {
        return dragMouseCursorY;
    }

    /**
     * Used to set a new mouse cursor value during a drag event for a rectangle/lasso tool selection
     */
    public void setDragMouseCursorY(double dragMouseCursorY) {
        this.dragMouseCursorY = dragMouseCursorY;
    }

    /**
     * Used to store the coordinates of the mouse when the selection tool is being used and a drag event is occurring
     * @param mouseCursorX x mouse coordinate
     */
    public void setMouseCursorX(double mouseCursorX) {
        this.mouseCursorX = mouseCursorX;
    }

    /**
     * Used to store the coordinates of the mouse when the selection tool is being used and a drag event is occurring
     * @param mouseCursorY y mouse coordinate
     */
    public void setMouseCursorY(double mouseCursorY) {
        this.mouseCursorY = mouseCursorY;
    }



    // METHODS FOR SINGLE BLOB SELECTION

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





    // METHODS FOR MULTIPLE BLOB SELECTION

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
     * @param blobList selected blobs created in the controller
     */
    public void selectMultiple(List<Blob> blobList) {
        blobList.forEach(this::updateSelected);
        notifyBlobSubscribers();
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
     * Clears all the selected blobs stored in the iModel
     */
    public void clearBlobSelection() {
        selectedBlobs.removeAll(selectedBlobs);
        notifyBlobSubscribers();
    }


    /**
     * Checks
     * @param hitList list of selected blobs at the controller
     * @return true if all selected blobs at controller is contained in the iModel, false otherwise
     */
    public boolean allSelectedBlobs(List<Blob> hitList) {
        return selectedBlobs.containsAll(hitList);
    }



    // METHODS FOR TOOL SELECTION

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


    /**
     * Stores the canvas snapshot by pixels
     * @param reader canvas snapshot from the BlobView, from the hidden canvas containing the selection area of the
     * lasso/rectangle tool
     */
    public void storeCanvasSnapshot(PixelReader reader) {
        this.canvasSnapshot = reader;
    }

    /**
     * Uses the canvas snapshot (stored in canvasSnapshot var) of the hidden canvas where the lasso selection
     * is drawn, to see if a blob is within the tools' selection area. The list of all blobs in the application is
     * iterated through to see if a blob is within the area selection.
     * @param blobs list of all created blobs in the application so far
     * @return list of all blobs WITHIN the selection area of the lasso tool
     */
    public List<Blob> areaHit(List<Blob> blobs) {
        List<Blob> hitList = new ArrayList<>();
        blobs.forEach(b -> {
            if (checkLassoContains(b, canvasSnapshot)) {
                hitList.add(b);
            }
        });
        return hitList;
    }

    /**
     * Calls the lasso selection object to see if a given blob and coordinate is within the lasso tool's
     * selection area
     * @return true if a blob is within the area selection, false otherwise
     */
    public boolean checkLassoContains(Blob b, PixelReader reader) {
        return lassoSelection.contains(b, reader);
    }

}

