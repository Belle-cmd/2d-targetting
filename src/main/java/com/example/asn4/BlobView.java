package com.example.asn4;

import javafx.beans.Observable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class BlobView extends StackPane implements BlobModelListener, IModelListener {

    /** pen abstraction for drawing blobs */
    private GraphicsContext gcBlobs;

    /** pen abstraction for the selection tools (lasso, rectangle) */
    private GraphicsContext gcSelection;

    /** pen abstraction for the offscreen bitmap */
    private GraphicsContext checkGC;

    private Canvas myCanvas;

    /** Canvas for the offscreen bitmap */
    private Canvas checkCanvas;

    private BlobModel model;

    private InteractionModel iModel;

    /** Stores the font style used to display a blob's order number */
    private Font font;

    /** Stores the width of the canvas */
    private double viewWidth;

    /** Used to retrieve the pixel data from an Image or other surface containing pixels */
    private PixelReader reader;



    public BlobView() {
        // prepare canvas
        myCanvas = new Canvas(800,800);
        gcBlobs = myCanvas.getGraphicsContext2D();
        gcSelection = myCanvas.getGraphicsContext2D();  // gc for lasso tool and rectangle tool

        setupOffscreen();  // create bitmap canvas

        font = new Font(15);
        gcBlobs.setFont(font);

//        this.widthProperty().addListener(this::setCanvasSize);
        this.setStyle("-fx-background-color: #b5e8e3;");  // set color of the background
        this.getChildren().add(myCanvas);
    }


    private void setupOffscreen() {
        // offscreen bitmap for checking 'contains'
        checkCanvas = new Canvas(800, 800);  // for the offscreen bitmap
        checkGC = checkCanvas.getGraphicsContext2D();
        checkGC.beginPath();

        checkGC.moveTo(0,0);
        checkGC.lineTo(800, 0);
        checkGC.lineTo(800, 800);
        checkGC.lineTo(0,800);

        checkGC.closePath();
        checkGC.setFill(Color.ORANGE);
        checkGC.fill();


    }

    private void drawBlobs() {
        // Clears a portion of the canvas with a transparent color value
        gcBlobs.clearRect(0,0,myCanvas.getWidth(),myCanvas.getHeight());

        model.getBlobs().forEach(b -> {
            // for single blob selection
            if (b == iModel.getSelected()) {
                gcBlobs.setFill(Color.ORCHID);
            } else {
                gcBlobs.setFill(Color.STEELBLUE);
            }

            gcBlobs.fillOval(b.x-b.r,b.y-b.r,b.r*2,b.r*2);
            gcBlobs.setFill(Color.BLACK);
            gcBlobs.fillText(String.valueOf(b.counter), b.x-3,b.y+3);
        });
    }

    private void checkSelection() {
        if (reader!=null) {
            if (reader.getColor((int) iModel.getMouseCursorX(), (int) iModel.getMouseCursorY()).equals(Color.ORANGE)) {
                System.out.println("MOUSE ON SELECTION");
            }
        }
    }

    private void drawSelection() {
        // draw polygon, with colour depending on the mouse location
        // rectangle selection
        gcSelection.setStroke(Color.GREEN);
        gcSelection.strokeRect(iModel.getRectStartingX(), iModel.getRectStartingY(),
                iModel.getDragMouseCursorX() - iModel.getRectStartingX(),
                iModel.getDragMouseCursorY() - iModel.getRectStartingY());

        // draw path during lasso selection (fill the path when finished)
        if (!iModel.getLassoPathStatus()) {
            gcSelection.setFill(Color.RED);
            iModel.getPoints().forEach(p -> gcSelection.fillOval(p.getX()-3,p.getY()-3,6,6));
        } else {
            gcSelection.setFill(Color.ORANGE);
            gcSelection.beginPath();

            if (iModel.getPoints().size() != 0) {
                // moving to the specified coordinates
                gcSelection.moveTo(iModel.getPoints().get(0).getX(),iModel.getPoints().get(0).getY());

                // Creates a line path element by drawing a straight line from the current coordinate to the new coordinates
                iModel.getPoints().forEach(p -> gcSelection.lineTo(p.getX(),p.getY()));
            }

            gcSelection.closePath();
            gcSelection.fill();
        }
        // custom graphical image that is constructed from pixels
        WritableImage buffer = myCanvas.snapshot(null, null);

        // interface defines methods for retrieving the pixel data from an Image or other surface containing pixels
        reader = buffer.getPixelReader();
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
        myCanvas.setOnMouseMoved(e -> {
            controller.storeMovingCursor(e);
            checkSelection();
        });
    }
}
