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
 * This class uses 2 canvases, one shown to the user (myCanvas) and a hidden canvas (selectionCanvas).
 * myCanvas is where the blobs, rectangle selection and tool selection visual feedback are shown to the user.
 * selectionCanvas is a hidden canvas where the area selection created by the lasso and rectangle tool is created.
 *
 * This class also uses a snapshot of the selectionCanvas and sends that snapshot to the interaction model so that, the
 * interaction model's lasso tool can use that snapshot to check if a blob is within the lasso tool's area selection.
 */
public class BlobView extends StackPane implements BlobModelListener, IModelListener {

    /** pen abstraction for drawing blobs */
    private GraphicsContext gcBlobs;

    /** pen abstraction for the selection tools (lasso, rectangle) shown to the user */
    private GraphicsContext gc;

    /** pen abstraction for the offscreen canvas for the selection area, hidden from the user */
    private GraphicsContext selectGC;

    /** Canvas where blobs and selection tool side effects are shown to the user */
    private Canvas myCanvas;

    /** Canvas for the rectangle and lasso selection hidden to the user */
    private Canvas selectionCanvas;

    /** Model holding the main data of the application, including the blobs created */
    private BlobModel model;

    /** Interaction model that holds data for user selections */
    private InteractionModel iModel;

    /** Stores the font style used to display a blob's order number */
    private Font font;

    /** Used to retrieve the pixel data from an Image or other surface containing pixels */
    private PixelReader reader;



    public BlobView() {
        // prepare canvas
        myCanvas = new Canvas(800,800);
        gcBlobs = myCanvas.getGraphicsContext2D();
        gc = myCanvas.getGraphicsContext2D();  // gc for lasso tool and rectangle tool shown to the user

        selectionCanvas = new Canvas(800, 800);  // canvas for selected area (hidden)
        selectGC = selectionCanvas.getGraphicsContext2D();  // used to draw the area filled by the selection (hidden)

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
     * Every time this function is
     * called, the snapshot image of selectionCanvas is sent to the interaction model so that a copy of the snapshot
     * can be used by the object instance of the rectangle/lasso tool stored in the interaction model.
     */
    private void drawSelection() {
        // rectangle selection
        gc.setStroke(Color.GREEN);
        gc.strokeRect(iModel.getRectStartingX(), iModel.getRectStartingY(),
                iModel.getDragMouseCursorX() - iModel.getRectStartingX(),
                iModel.getDragMouseCursorY() - iModel.getRectStartingY());


        if (!iModel.getLassoPathStatus()) {
            // draw path during lasso selection

            gc.setFill(Color.RED);
            // for showing the user the dots of the lasso tool
            iModel.getPoints().forEach(p -> gc.fillOval(p.getX()-3,p.getY()-3,6,6));

            // for the hidden canvas where bitmaps get compared to distinguish selection (hidden from user)
            iModel.getPoints().forEach(p -> selectGC.fillOval(p.getX()-3,p.getY()-3,6,6));

        } else {
            // when path creation is completed, fill the area to indicate the lasso selection

            selectGC.setFill(Color.ORANGE);
            selectGC.beginPath();

            if (iModel.getPoints().size() != 0) {
                // moving to the specified coordinates
                selectGC.moveTo(iModel.getPoints().get(0).getX(),iModel.getPoints().get(0).getY());

                // Creates a line path element by drawing a straight line from the current coordinate to the new coordinates
                iModel.getPoints().forEach(p -> selectGC.lineTo(p.getX(),p.getY()));
            }

            selectGC.closePath();
            selectGC.fill();
        }
        // custom graphical image that is constructed from pixels
        WritableImage buffer = selectionCanvas.snapshot(null, null);

        // interface defines methods for retrieving the pixel data from an Image or other surface containing pixels
        reader = buffer.getPixelReader();
        iModel.storeCanvasSnapshot(reader);  // stores/updates the photo of the canvas where area selection is stored
        selectGC.clearRect(0,0,myCanvas.getWidth(),myCanvas.getHeight());
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
