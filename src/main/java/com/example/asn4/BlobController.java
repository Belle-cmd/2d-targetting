package com.example.asn4;

import javafx.scene.input.MouseEvent;

public class BlobController {

    private BlobModel model;

    private InteractionModel iModel;

    private double prevX,prevY;

    private  double dX,dY;

    private enum State {READY,PREPARE_CREATE, DRAGGING_BLOB, DRAGGING_TOOL}  // from interaction state model
    State currentState = State.READY;

    /** Stores the mouse position at the end of a mouse press event, just before a mouse drag event starts */
    private double beforeShiftX, beforeShiftY;






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
        switch (currentState) {
            case READY -> {
                // checks if user pressed a blob or not
                if (model.hitBlob(event.getX(),event.getY())) {
                    Blob b = model.whichHit(event.getX(),event.getY());
                    iModel.setSelected(b);

                    prevX = event.getX();
                    prevY = event.getY();

                    beforeShiftX = prevX;  // save the current mouse position before resizing blobs
                    beforeShiftY = prevY;

                    currentState = State.DRAGGING_BLOB;
                } else {
                    if (event.isShiftDown()) {
                        // enable blob creation at shift key press (remove if-statement for multiple selections)
                        currentState = State.PREPARE_CREATE;
                    }
                    if (event.isControlDown()) {
                        // when mouse press occurs in canvas, saves the current mouse position
                        // for drawing the rectangle selection tool
                        iModel.setBeforeLassoRectX(event.getX());
                        iModel.setGetBeforeLassoRectY(event.getY());

                        currentState = State.DRAGGING_TOOL;
                    }
                    // when the user clicks on the background, blob selection disappears
                    iModel.unselect();
                }
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

                model.moveBlob(iModel.getSelected(), dX,dY);
//                model.moveBlobs(iModel.getSelectedBlobs(), dX,dY);
            }
            case DRAGGING_TOOL -> {
                // the user will use either the lasso tool or the rectangle tool to select/unselect blobs
                iModel.setCursorRedraw(event.getX(), event.getY());
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
            case DRAGGING_BLOB, DRAGGING_TOOL -> {
                currentState = State.READY;
            }
        }
    }
}
