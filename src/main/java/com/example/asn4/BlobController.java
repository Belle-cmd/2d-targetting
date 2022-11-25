package com.example.asn4;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;

public class BlobController {
    BlobModel model;
    InteractionModel iModel;
    double prevX,prevY;
    double dX,dY;

    enum State {READY,PREPARE_CREATE, DRAGGING}  // from interaction state model
    State currentState = State.READY;

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

//                    // multiple blob selection
//                    ArrayList<Blob> hitList = model.hitArea(event.getX(), event.getY(), iModel.getCursorRadius());
//                    if (hitList.size() > 0) {
//                        if (event.isControlDown()) {
//                            iModel.select(hitList);
//                        } else {
//                            if (!iModel.allSelectedBlobs(hitList)) {
//                                iModel.clearBlobList();
//                                iModel.select(hitList);
//                            }
//                        }
//                    }

                    prevX = event.getX();
                    prevY = event.getY();
                    beforeShiftX = prevX;
                    beforeShiftY = prevY;
                    currentState = State.DRAGGING;
                } else {
                    if (event.isShiftDown()) {
                        // enable blob creation at shift key press (remove if-statement for multiple selections)
                        currentState = State.PREPARE_CREATE;
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
            case DRAGGING -> {
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
            // user releases the mouse when it isn't holding a blob, just go back to ready state
            case DRAGGING -> {
                currentState = State.READY;
            }
        }
    }
}
