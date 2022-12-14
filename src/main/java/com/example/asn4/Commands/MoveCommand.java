package com.example.asn4.Commands;

import com.example.asn4.Blob;
import com.example.asn4.BlobModel;
import java.util.ArrayList;
import java.util.Iterator;

public class MoveCommand implements TargetCommand {

    /** list of selected blobs */
    private Blob blob;

    /** reference to the model, needed for the doIt() and undo() methods */
    private BlobModel model;

    /** x and y coordinate of the blob */
    private double x, y;


    public MoveCommand(BlobModel newModel, Blob newBlob, double newX, double newY) {
        this.model = newModel;
        this.blob = newBlob;
        this.x = newX;
        this.y = newY;
    }


    @Override
    public void doIt() {
        model.moveBlob(blob, x, y);
    }

    @Override
    public void undo() {
        model.moveBlob(blob, x * -1, y * -1);
    }

    /**
     * Print to see the MoveCommand object and its new data (good for debugging)
     * @return string data of the move command object
     */
    public String toString() {
        double roundOffX = Math.round(x * 100.0) / 100.0;
        double roundOffY = Math.round(y * 100.0) / 100.0;
        return "Move Command: move blob(s) at " + roundOffX + " , " + roundOffY;
    }
}
