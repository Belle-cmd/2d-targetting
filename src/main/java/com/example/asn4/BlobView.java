package com.example.asn4;

import javafx.beans.Observable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class BlobView extends StackPane implements BlobModelListener, IModelListener {

    /** pen abstraction for drawing blobs */
    private GraphicsContext gcBlobs;

    /** pen abstraction for the selection tools (lasso, rectangle) */
    private GraphicsContext gcSelection;

    private Canvas myCanvas;

    private BlobModel model;

    private InteractionModel iModel;

    /** Stores the font style used to display a blob's order number */
    private Font font;

    /** Stores the width of the canvas */
    private double viewWidth;

    public BlobView(double vWidth) {
        // prepare canvas
        myCanvas = new Canvas(vWidth,1080);
        gcBlobs = myCanvas.getGraphicsContext2D();
        gcSelection = myCanvas.getGraphicsContext2D();  // gc for lasso tool and rectangle tool

        font = new Font(15);
        gcBlobs.setFont(font);

        this.widthProperty().addListener(this::setCanvasSize);
        this.setStyle("-fx-background-color: #b5e8e3;");  // set color of the background
        this.getChildren().add(myCanvas);
    }

    /**
     * Adjust canvas size based on resizing done by the user
     */
    private void setCanvasSize(Observable observable, Number oldVal, Number newVal) {
        viewWidth = newVal.doubleValue();
        myCanvas.setWidth(viewWidth);
        iModel.setViewWidth(viewWidth);
        drawBlobs();
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

    private void drawSelection() {
        gcSelection.setStroke(Color.GREEN);  // for rectangle
        gcSelection.strokeRect(iModel.getBeforeLassoRectX(), iModel.getGetBeforeLassoRectY(),
                iModel.getCursorX() - iModel.getBeforeLassoRectX(),
                iModel.getCursorY() - iModel.getGetBeforeLassoRectY());
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
        myCanvas.setOnMouseDragged(controller::handleDragged);
        myCanvas.setOnMouseReleased(controller::handleReleased);
    }
}
