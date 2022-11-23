package com.example.asn4;

import javafx.beans.Observable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class BlobView extends StackPane implements BlobModelListener, IModelListener {
    GraphicsContext gc;

    Canvas myCanvas;

    BlobModel model;

    InteractionModel iModel;

    /** Stores the font style used to display a blob's order number */
    private Font font;

    /** Stores the width of the canvas */
    private double viewWidth;

    public BlobView(double vWidth) {
        // prepare canvas
        myCanvas = new Canvas(vWidth,1080);
        gc = myCanvas.getGraphicsContext2D();

        font = new Font(15);
        gc.setFont(font);

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
        draw();
    }

    private void draw() {
        // Clears a portion of the canvas with a transparent color value
        gc.clearRect(0,0,myCanvas.getWidth(),myCanvas.getHeight());

        model.getBlobs().forEach(b -> {
            if (b == iModel.getSelected()) {
                gc.setFill(Color.PINK);
            } else {
                gc.setFill(Color.STEELBLUE);
            }
            gc.fillOval(b.x-b.r,b.y-b.r,b.r*2,b.r*2);
            gc.setFill(Color.BLACK);
            gc.fillText(String.valueOf(b.counter), b.x-3,b.y+3);
        });
    }

    public void setModel(BlobModel newModel) {
        model = newModel;
    }

    public void setIModel(InteractionModel newIModel) {
        iModel = newIModel;
    }

    @Override
    public void modelChanged() {
        draw();
    }

    @Override
    public void iModelChanged() {
        draw();
    }

    public void setController(BlobController controller) {
        myCanvas.setOnMousePressed(controller::handlePressed);
        myCanvas.setOnMouseDragged(controller::handleDragged);
        myCanvas.setOnMouseReleased(controller::handleReleased);
    }
}
