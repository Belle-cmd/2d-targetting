package com.example.asn4;

import com.example.asn4.Commands.CreateCommand;
import com.example.asn4.Commands.TargetCommand;
import javafx.geometry.Point2D;
import javafx.scene.image.PixelReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class InteractionModel {

    private List<IModelListener> blobSubscribers;

    /** class subscribers that listen to interaction model, for the lasso and rectangle selection tool */
    private List<IModelListener> selectionSubscribers;

    /** Stores multiple selected blobs */
    private ArrayList<Blob> selectedBlobs;

    /** Stores the mouse cursor (x, y) at drag during a tool selection event */
    private double dragMouseCursorX, dragMouseCursorY;

    /** Stores the mouse cursor (x, y) per mouse movement in the canvas */
    private double mouseCursorX, mouseCursorY;

    /** lasso instance */
    private LassoSelection lassoSelection;

    /** rectangle tool instance */
    private RectangleSelection rectSelection;

    /** Everytime the lasso tool is used to create a selection area, the canvas holding its graphics content gets a
     * snapshot image stored in this var. This is done so that blobs within the selection can be distinguished */
    private PixelReader lassoCanvasSnapshot;

    /** Stores the command objects for undo */
    private Stack<TargetCommand> undoStack;

    /** Stores the command objects for redo */
    private Stack<TargetCommand> redoStack;



    public InteractionModel() {
        blobSubscribers = new ArrayList<>();
        selectionSubscribers = new ArrayList<>();
        selectedBlobs = new ArrayList<>();

        lassoSelection = new LassoSelection();
        rectSelection = new RectangleSelection();
        lassoCanvasSnapshot = null;

        undoStack = new Stack<>();
        redoStack = new Stack<>();
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
        selectedBlobs.clear();
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
     * Stores the canvas snapshot of the lasso tool by pixels
     * @param reader canvas snapshot from the BlobView, from the hidden canvas containing the selection area of the
     * lasso/rectangle tool
     */
    public void storeLassoCanvasSnapshot(PixelReader reader) {
        this.lassoCanvasSnapshot = reader;
    }

    /**
     * Store the dimensions of the rectangle selection after it is drawn in the view
     * @param newLeft new left dimension
     * @param newTop new top dimension
     * @param newWidth new width dimension
     * @param newHeight new height dimension
     */
    public void storeRectSelectionDimension(double newLeft, double newTop, double newWidth, double newHeight) {
        rectSelection.setLeft(newLeft);
        rectSelection.setTop(newTop);
        rectSelection.setWidth(newWidth);
        rectSelection.setHeight(newHeight);
    }

    /**
     * Uses the canvas snapshot (stored in canvasSnapshot var) of the hidden canvas where the lasso selection
     * is drawn, to see if a blob is within the tools' selection area. The list of all blobs in the application is
     * iterated through to see if a blob is within the area selection.
     * @param blobs list of all created blobs in the application so far
     * @return list of all blobs WITHIN the selection area of the lasso tool
     */
    public List<Blob> lassoAreaHit(List<Blob> blobs) {
        List<Blob> hitList = new ArrayList<>();
        blobs.forEach(b -> {
            if (checkLassoContains(b, lassoCanvasSnapshot)) {
                hitList.add(b);
            }
        });
        return hitList;
    }

    /**
     * Uses the canvas snapshot of the hidden canvas where the rectangle selection is drawn, to see if a blob is within
     * the tools' selection area. The list of all blobs in the application is iterated through to see if a blob is
     * within the area selection.
     * @param blobs list of all created blobs in the application so far
     * @return list of all blobs WITHIN the selection area of the rectangle tool
     */
    public List<Blob> rectAreaHit(List<Blob> blobs) {
        List<Blob> hitList = new ArrayList<>();
        blobs.forEach(b -> {
            if (rectSelection.contains(b)) {
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
    private boolean checkLassoContains(Blob b, PixelReader reader) {
        return lassoSelection.contains(b, reader);
    }



    // METHODS FOR UNDO/REDO STACKS

    public void handleUndo() {
        if (undoStack.empty()) {
            System.out.println("Nothing more to undo!");
        } else {
            TargetCommand tc = undoStack.pop();
            tc.undo();
            redoStack.push(tc);
            notifyBlobSubscribers();
        }
    }

    public void handleRedo() {
        if (redoStack.empty()) {
            System.out.println("Nothing more to redo!");
        } else {
            TargetCommand tc = redoStack.pop();
            tc.doIt();
            undoStack.push(tc);
            notifyBlobSubscribers();
        }
    }

    /**
     * Add a new task command to the undo stack
     * @param targetCommand new task command object to be pushed
     */
    public void addToUndoStack(TargetCommand targetCommand) {
        undoStack.push(targetCommand);
        notifyBlobSubscribers();
    }

    /**
     * Prints all the current items in the undo stack
     */
    public void printUndoStack() {
        System.out.println("UNDO STACK:");
        undoStack.forEach(item -> {
            System.out.println("    " + item.toString());
        });
        System.out.println();
    }

    /**
     * Prints all the current items in the redo stack
     */
    private void printRedoStack() {
        System.out.println("REDO STACK:");
        redoStack.forEach(item -> {
            System.out.println("    " + item.toString());
        });
        System.out.println();
    }
}

