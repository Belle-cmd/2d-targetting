package com.example.asn4;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

public class BlobView extends StackPane implements BlobModelListener, IModelListener {
    GraphicsContext gc;
    Canvas myCanvas;
    BlobModel model;
    InteractionModel iModel;

    public BlobView() {
        // prepare canvas
        myCanvas = new Canvas(800,800);
        gc = myCanvas.getGraphicsContext2D();
        gc.setFill(Color.ORANGE);
        gc.fillRect(100,100,200,200);

        this.getChildren().add(myCanvas);
    }

    private void draw() {
        // Clears a portion of the canvas with a transparent color value
        gc.clearRect(0,0,myCanvas.getWidth(),myCanvas.getHeight());

        model.getBlobs().forEach(b -> {
            if (b == iModel.getSelected()) {
                gc.setFill(Color.TOMATO);
            } else {
                gc.setFill(Color.BEIGE);
            }
            gc.fillOval(b.x-b.r,b.y-b.r,b.r*2,b.r*2);
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
        // everytime the mouse presses, releases, or drags in the canvas, the controller fires up,
        // regardless if it's on a blob or not!
        myCanvas.setOnMousePressed(controller::handlePressed);
        myCanvas.setOnMouseDragged(controller::handleDragged);
        myCanvas.setOnMouseReleased(controller::handleReleased);
    }
}
