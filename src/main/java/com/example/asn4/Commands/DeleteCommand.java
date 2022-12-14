package com.example.asn4.Commands;

import com.example.asn4.Blob;
import com.example.asn4.BlobModel;

public class DeleteCommand implements TargetCommand {

    /** Blob to delete/bring back */
    private Blob blob;

    /** reference to the model, needed for the doIt() and undo() methods */
    private BlobModel model;


    public DeleteCommand(BlobModel newModel, Blob newBlob) {
        this.model = newModel;
        this.blob = newBlob;
    }

    @Override
    public void doIt() {
        model.deleteBlob(blob);
    }

    @Override
    public void undo() {
        model.addBlob(blob);
    }

    /**
     * Prints the delete command objects' data
     * @return string data of the delete command object
     */
    public String toString() {
        double roundOffX = Math.round(blob.x * 100.0) / 100.0;
        double roundOffY = Math.round(blob.y * 100.0) / 100.0;
        return "Delete Command: blob deleted at " + roundOffX + " , " + roundOffY;
    }
}
