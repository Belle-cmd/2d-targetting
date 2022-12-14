package com.example.asn4.Commands;

import com.example.asn4.Blob;
import com.example.asn4.BlobModel;

public class ResizeCommand implements TargetCommand {

    /** Blob to resize/bring back to its previous size */
    private Blob blob;

    /** reference to the model, needed for the doIt() and undo() methods */
    private BlobModel model;

    /** new radius to replace the blob's current radius */
    private double newRadius;

    /** current radius of the blob before resizing occur */
    private double oldRadius;


    public ResizeCommand(BlobModel newModel, Blob newBlob) {
        this.model = newModel;
        this.blob = newBlob;
        this.newRadius = blob.r;
        this.oldRadius =  blob.initialRadius;
    }

    @Override
    public void doIt() {
        blob.r = newRadius;
        model.redrawBlobs();
    }

    @Override
    public void undo() {
        blob.r = oldRadius;
        model.redrawBlobs();
    }

    /**
     * Prints the resize command objects' data
     * @return string data of the delete command object
     */
    public String toString() {
        double startSize = Math.round(oldRadius * 100.0) / 100.0;
        double endSize = Math.round(newRadius * 100.0) / 100.0;
        return "Resize Command: blob resized from " + startSize + " to " + endSize;
    }
}
