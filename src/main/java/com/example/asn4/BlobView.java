package com.example.asn4;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * This class uses 3 canvases, one shown to the user (myCanvas) and 2 hidden canvas (lassoCanvas, rectCanvas).
 * myCanvas is where the blobs, rectangle selection and tool selection visual feedback are shown to the user.
 * lassoCanvas is a hidden canvas where the area selection created by the lasso tool is created. rectCanvas stores
 * the area selection of the rectangle tool.
 *
 * This class also uses a snapshot of the lassoCanvas and sends that snapshot to the interaction model so that, the
 * interaction model's lasso tool can use that snapshot to check if a blob is within the lasso tool's area selection.
 */
public class BlobView extends StackPane implements BlobModelListener, IModelListener {

    /** pen abstraction for drawing blobs */
    private GraphicsContext gcBlobs;

    /** pen abstraction for the selection tools (lasso, rectangle) shown to the user */
    private GraphicsContext gc;

    /** pen abstraction for the lasso selection area's canvas, hidden from the user */
    private GraphicsContext lassoGC;

    /** pen abstraction for the rectangle selection area's canvas, hidden from the user */
    private GraphicsContext rectGC;

    /** Canvas where blobs and selection tool side effects are shown to the user */
    private Canvas myCanvas;

    /** Canvas for the lasso selection hidden to the user */
    private Canvas lassoCanvas;

    /** Canvas for the rectangle selection hidden to the user */
    private Canvas rectCanvas;

    /** Model holding the main data of the application, including the blobs created */
    private BlobModel model;

    /** Interaction model that holds data for user selections */
    private InteractionModel iModel;

    /** Stores the font style used to display a blob's order number */
    private Font font;




    public BlobView() {
        // prepare canvas
        myCanvas = new Canvas(800,800);
        gcBlobs = myCanvas.getGraphicsContext2D();
        gc = myCanvas.getGraphicsContext2D();  // gc for lasso tool and rectangle tool shown to the user

        lassoCanvas = new Canvas(800, 800);  // canvas for lasso selection area (hidden)
        lassoGC = lassoCanvas.getGraphicsContext2D();  // used to draw the area filled by the selection (hidden)
        rectCanvas = new Canvas(800,800);
        rectGC = rectCanvas.getGraphicsContext2D();

        font = new Font(15);
        gcBlobs.setFont(font);

        this.setStyle("-fx-background-color: #b5e8e3;");  // set color of the background
        this.getChildren().add(myCanvas);
    }


    private void drawBlobs() {
        // Clears a portion of the canvas with a transparent color value
        gcBlobs.clearRect(0,0,myCanvas.getWidth(),myCanvas.getHeight());

        model.getBlobs().forEach(b -> {
            if (iModel.isSelected(b)) {
                gcBlobs.setFill(Color.ORCHID);
            } else {
                gcBlobs.setFill(Color.STEELBLUE);
            }

            gcBlobs.fillOval(b.x-b.r,b.y-b.r,b.r*2,b.r*2);
            gcBlobs.setFill(Color.BLACK);
            gcBlobs.fillText(String.valueOf(b.counter), b.x-3,b.y+3);
        });
    }


    /**
     * Draws a rectangle and lasso selection tools to the canvas myCanvas where users can see them. The selection area
     * of the rectangle and lasso tool are hidden to the user and drawn in selectionCanvas.
     *
     * Every time this function is called, the snapshot image of lassoCanvas is sent to the interaction model so that a
     * copy of the snapshot can be used by the object instance of the lasso tool stored in the interaction model. The
     * rectangle selection doesn't need a canvas snapshot. It only needs to send the dimensions of the selection to
     * the iModel.
     */
    private void drawSelection() {
        // drawing rectangle selection to the canvas shown to the user
        gc.setStroke(Color.GREEN);

        double boxLeft, boxTop, boxWidth, boxHeight;
        boxLeft = iModel.getRectStartingX();
        boxTop = iModel.getRectStartingY();
        boxWidth = iModel.getDragMouseCursorX() - iModel.getRectStartingX();
        boxHeight = iModel.getDragMouseCursorY() - iModel.getRectStartingY();

        gc.strokeRect(boxLeft, boxTop, boxWidth, boxHeight);  // draw rectangle selection border for the user

        // create the selection area of the rectangle tool hidden to the user
        rectGC.fillRect(boxLeft, boxTop, boxWidth, boxHeight);

        // store the new dimensions of the rectangle selection to the iModel so that controller can use it to find blobs
        iModel.storeRectSelectionDimension(boxLeft, boxTop, boxWidth, boxHeight);


        // drawing lasso selection to the canvas shown to the user
        if (!iModel.getLassoPathStatus()) {
            // draw path during lasso selection

            gc.setFill(Color.RED);
            // for showing the user the dots of the lasso tool
            iModel.getPoints().forEach(p -> gc.fillOval(p.getX()-3,p.getY()-3,6,6));

            // for the hidden canvas where bitmaps get compared to distinguish selection (hidden from user)
            iModel.getPoints().forEach(p -> lassoGC.fillOval(p.getX()-3,p.getY()-3,6,6));

        } else {
            // when path creation is completed, fill the area to indicate the lasso selection

            lassoGC.setFill(Color.ORANGE);
            lassoGC.beginPath();

            if (iModel.getPoints().size() != 0) {
                // moving to the specified coordinates
                lassoGC.moveTo(iModel.getPoints().get(0).getX(),iModel.getPoints().get(0).getY());

                // Creates a line path element by drawing a straight line from the current coordinate to the new coordinates
                iModel.getPoints().forEach(p -> lassoGC.lineTo(p.getX(),p.getY()));
            }

            lassoGC.closePath();
            lassoGC.fill();
        }

        WritableImage lassoBuffer = lassoCanvas.snapshot(null, null);
        PixelReader lassoReader = lassoBuffer.getPixelReader();
        iModel.storeLassoCanvasSnapshot(lassoReader);  // stores/updates the photo of the canvas where area selection is stored
        lassoGC.clearRect(0,0,myCanvas.getWidth(),myCanvas.getHeight());
    }


    public void setModel(BlobModel newModel) {
        model = newModel;
    }


    public void setIModel(InteractionModel newIModel) {
        iModel = newIModel;
    }


    @Override
    public void modelChanged() {
        drawBlobs();
    }


    @Override
    public void iModelChanged() {
        drawBlobs();
    }


    @Override
    public void iModelChangedSelection() {
        drawBlobs();
        drawSelection();
    }


    public void setController(BlobController controller) {
        myCanvas.setFocusTraversable(true);
        myCanvas.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DELETE) {
                controller.deleteBlobs();
            }
            if (e.isControlDown() && e.getCode() == KeyCode.Z) {
                System.out.println("ctrl key + Z");
                controller.handleUndo();
            }
            if (e.isControlDown() && e.getCode() == KeyCode.R) {
                System.out.println("ctrl key + R");
            }
        });
        myCanvas.setOnMousePressed(controller::handlePressed);
        myCanvas.setOnMouseDragged(e -> {
            controller.handleDragged(e);
            controller.storeDraggingCursor(e);
        });
        myCanvas.setOnMouseReleased(controller::handleReleased);
        myCanvas.setOnMouseMoved(controller::storeMovingCursor);
    }
}
