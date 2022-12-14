package com.example.asn4.Commands;

import com.example.asn4.Blob;
import com.example.asn4.BlobModel;

import java.text.DecimalFormat;

public class CreateCommand implements TargetCommand {

    /** newly created blob */
    private Blob blob;

    /** reference to the model, needed for the doIt() and undo() methods */
    private BlobModel model;

    /** x and y coordinate of the blob */
    private double x, y;



    public CreateCommand(BlobModel newModel, double newX, double newY) {
        this.x = newX;
        this.y = newY;
        this.model = newModel;
        this.blob = null;
    }

    @Override
    public void doIt() {
        if (blob == null) {
            blob = model.createBlob(x, y);
        } else {
            model.addBlob(blob);
        }
    }

    @Override
    public void undo() {
        model.deleteBlob(blob);
    }

    /**
     * Used mainly for debugging to see the CreateCommand object
     * @return string data of the create command object
     */
    public String toString() {
        double roundOffX = Math.round(x * 100.0) / 100.0;
        double roundOffY = Math.round(y * 100.0) / 100.0;
        return "Create Command: blob at " + roundOffX + " , " + roundOffY;
    }
}
