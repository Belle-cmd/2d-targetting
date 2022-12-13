package com.example.asn4;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Store the state machines for the whole application. This class acts as the 'leader' that tell model classes what
 * to store and change.
 *
 * This class also deals with 2 main list objects containing blobs: hitlist and nitPickedBlobs. hitlist involve blobs
 * from the lasso/rectangle selection. NitPickedBlobs involve blobs chosen through mouse click alone or ctrl +
 * mouse click. Both lists aren't stored in the interaction model since these are just helper objects that store blobs
 * before selection/deselection (hitlist for tool selected blobs, nitPickedBlobs for blobs picked one by one).
 */
public class BlobController {

    private BlobModel model;

    private InteractionModel iModel;

    private double prevX,prevY;

    private  double dX,dY;

    private enum State {READY,PREPARE_CREATE, DRAGGING_BLOB, DRAGGING_SELECTION}  // from interaction state model
    State currentState = State.READY;

    /** Stores the mouse position at the end of a mouse press event, just before a mouse drag event starts */
    private double beforeShiftX, beforeShiftY;

    /** list of all blobs that hold blobs from ctrl key and mouse press selection. This is different from hitlist list
     * where blobs are from the lasso/rectangle selection */
    private List<Blob> nitPickedBlobs;



    public BlobController() {

    }

    public void setModel(BlobModel newModel) {
        model = newModel;
    }

    public void setIModel(InteractionModel newIModel) {
        iModel = newIModel;
    }

    public void deleteBlobs() {
        if (iModel.getSelected() != null) {
            Blob singleBlob = iModel.getSelected();
            model.deleteBlob(singleBlob);
            iModel.unselect();
        }
    }

    public void handlePressed(MouseEvent event) {
        if (currentState == State.READY) {
            if (model.hitBlob(event.getX(), event.getY())) {
                // checks if user pressed a blob or not

                Blob b = model.whichHit(event.getX(), event.getY());
                iModel.setSelected(b);

                // Since selected area is actually a blob, add it to the nitPickedBlobs regardless if it was manually
                // selected by mouse press or ctrl key is involved
                nitPickedBlobs = new ArrayList<>();
                nitPickedBlobs.add(b);

                if (event.isControlDown()) {
                    // enable new blobs to be added one by one to selection by pressing ctrl key with mouse press
                    iModel.selectMultiple(nitPickedBlobs);
                } else {
                    if (!iModel.allSelectedBlobs(nitPickedBlobs)) {
                        iModel.clearBlobSelection();
                        iModel.selectMultiple(nitPickedBlobs);
                    }
                }

                prevX = event.getX();
                prevY = event.getY();
                beforeShiftX = prevX;  // save the current mouse position before resizing blobs
                beforeShiftY = prevY;
                currentState = State.DRAGGING_BLOB;
            } else {
                // user triggers a press event somewhere in the canvas
                if (event.isShiftDown()) {
                    // enable blob creation at shift key press (remove if-statement for multiple selections)
                    currentState = State.PREPARE_CREATE;
                }

                if (event.isControlDown()) {
                    // when mouse press occurs in canvas, saves the current mouse position
                    // for drawing the rectangle selection tool
                    iModel.setRectStartingPoint(event.getX(), event.getY());
                    handleLassoPressed(event);

                    currentState = State.DRAGGING_SELECTION;
                }
                // when the user clicks on the background, blob selection disappears
                iModel.unselect();
                iModel.clearBlobSelection();
            }
        }
    }

    public void handleDragged(MouseEvent event) {
        switch (currentState) {
            case PREPARE_CREATE -> {
                // go back to ready state since user just pressed the canvas (not a blob) and dragged somewhere
                currentState = State.READY;
            }
            case DRAGGING_BLOB -> {
                // the user will either move blob(s) or resize blob(s)

                // update the coordinates to reposition the blob
                dX = event.getX() - prevX;
                dY = event.getY() - prevY;
                prevX = event.getX();
                prevY = event.getY();

                if (event.isShiftDown()) {
                    if (beforeShiftX < prevX) {
                        iModel.getSelected().r += 1;  // at mouse drag to the right, increase blob size
                    }
                    if (beforeShiftX > prevX && iModel.getSelected().r != 5) {
                        iModel.getSelected().r -= 1;  // at mouse drag to the left, decrease blob size
                    }
                }

                model.moveBlobs(iModel.getSelectedBlobs(), dX,dY);
            }
            case DRAGGING_SELECTION -> {
                // the user will use either the lasso tool or the rectangle tool to select/unselect blobs
                iModel.setRectEndingPoint(event.getX(), event.getY());
                iModel.setLassoPoint(event.getX(), event.getY());
                handleLassoDragged(event);
            }
        }
    }

    public void handleReleased(MouseEvent event) {
        switch (currentState) {
            // user releases the mouse while holding a blob; place blob into the canvas
            // model will increase its blob which will initiate view to draw blob on canvas
            case PREPARE_CREATE -> {
                model.addBlob(event.getX(),event.getY());
                currentState = State.READY;
            }
            case DRAGGING_BLOB -> {
                currentState = State.READY;
            }
            case DRAGGING_SELECTION -> {
                currentState = State.READY;
                handleLassoReleased(event);

                // code below has to be before handleLassoReleased() bc when view is updated as a result of
                // handleLassoReleased(), BlobView sends canvas snapshot needed for the code below
                List<Blob> hitList = iModel.areaHit(model.getBlobs());  // get all the selected blobs using lasso tool
                iModel.selectMultiple(hitList);
            }
        }
    }

    /**
     * Create new points and store them to the iModel's list of points.
     * @param e mouse event
     */
    private void handleLassoPressed(MouseEvent e) {
        iModel.clearPoints();
        iModel.setLassoPathStatus(false);
        iModel.addPoints(new Point2D(e.getX(), e.getY()));
    }

    /**
     * Continuously add more points to the lasso at drag event
     * @param e mouse event
     */
    private void handleLassoDragged(MouseEvent e) {
        iModel.addPoints(new Point2D(e.getX(), e.getY()));
    }

    /**
     * End the lasso tool
     * @param e mouse event
     */
    private void handleLassoReleased(MouseEvent e) {
        iModel.setLassoPathStatus(true);
    }

    /**
     * Stores the mouse position during a dragging event, during the tool selection drag event
     * @param e mouse event
     */
    public void storeDraggingCursor(MouseEvent e) {
        iModel.setDragMouseCursorX(e.getX());
        iModel.setDragMouseCursorY(e.getY());
    }

    /**
     * Storse the mouse position during a mouse movement event on the canvas, for checking if the mouse is hovering
     * on a bitmap coordinate
     * @param e mouse event
     */
    public void storeMovingCursor(MouseEvent e) {
        iModel.setMouseCursorX(e.getX());
        iModel.setMouseCursorY(e.getY());
    }
}
