package com.example.asn4;

import com.example.asn4.Commands.CreateCommand;
import com.example.asn4.Commands.DeleteCommand;
import com.example.asn4.Commands.MoveCommand;
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
    private double beforeDragX, beforeDragY;

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

    /**
     * Delete selected blob(s)
     */
    public void deleteBlobs() {
        if (!iModel.getSelectedBlobs().isEmpty()) {
            iModel.getSelectedBlobs().forEach(b -> {
//                model.deleteBlob(b)
                DeleteCommand dc = new DeleteCommand(model, b);
                dc.doIt();
                iModel.addToUndoStack(dc);
            });
            iModel.clearBlobSelection();  // unselect ALL selected blobs in the iModel
        }
    }

    public void handlePressed(MouseEvent event) {
        if (currentState == State.READY) {
            if (model.hitBlob(event.getX(), event.getY())) {
                // checks if user pressed a blob or not

                Blob b = model.whichHit(event.getX(), event.getY());

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
                beforeDragX = prevX;  // save the current mouse position before resizing blobs
                beforeDragY = prevY;
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
                    if (beforeDragX < prevX) {
                        iModel.getSelectedBlobs().forEach(b -> {
                            b.r += 1;  // at mouse drag to the right, increase blob size
                        });
                    }
                    iModel.getSelectedBlobs().forEach(b -> {
                        if (beforeDragX > prevX && b.r != 5) {
                            b.r -= 1;  // at mouse drag to the left, decrease blob size
                        }
                    });
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
//                model.addBlob(event.getX(),event.getY());
                CreateCommand cc = new CreateCommand(model, event.getX(), event.getY());
                cc.doIt();  // also creates a new blob and adds it to model
                iModel.addToUndoStack(cc);

                currentState = State.READY;
            }
            case DRAGGING_BLOB -> {
                double xChange = event.getX() - beforeDragX;
                double yChange = event.getY() - beforeDragY;

                iModel.getSelectedBlobs().forEach(b -> {
                    MoveCommand mc = new MoveCommand(model, b, xChange, yChange);
                    iModel.addToUndoStack(mc);
                });


                currentState = State.READY;
            }
            case DRAGGING_SELECTION -> {
                currentState = State.READY;
                handleLassoReleased();


                // get all the selected blobs using lasso tool
                List<Blob> lassoHitList = iModel.lassoAreaHit(model.getBlobs());
                List<Blob> rectHitList = iModel.rectAreaHit(model.getBlobs());

                // choose the selection that got the bigger amount of blobs
                if (lassoHitList.size() > rectHitList.size()) {
                    iModel.selectMultiple(lassoHitList);
                } else if (lassoHitList.size() < rectHitList.size()) {
                    iModel.selectMultiple(rectHitList);
                }

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
     */
    private void handleLassoReleased() {
        iModel.setLassoPathStatus(true);
    }



    // METHODS THAT CALL INTERACTION MODEL TO STORE DATA NEEDED BY THE CONTROLLER

    /**
     * Stores the mouse position during a dragging event, during the tool selection drag event
     * @param e mouse event
     */
    public void storeDraggingCursor(MouseEvent e) {
        iModel.setDragMouseCursorX(e.getX());
        iModel.setDragMouseCursorY(e.getY());
    }

    /**
     * Store the mouse position during a mouse movement event on the canvas, for checking if the mouse is hovering
     * on a bitmap coordinate
     * @param e mouse event
     */
    public void storeMovingCursor(MouseEvent e) {
        iModel.setMouseCursorX(e.getX());
        iModel.setMouseCursorY(e.getY());
    }

    /**
     * Perform undo operations
     */
    public void handleUndo() {
        iModel.handleUndo();
    }

    /**
     * Perform redo operations
     */
    public void handleRedo() {
        iModel.handleRedo();
    }
}
