package com.example.asn4;

import java.util.ArrayList;

/**
 *
 */
public class TargetClipboard {
    /** list of currently selected blobs */
    private ArrayList<Blob> copiedSelection;



    public TargetClipboard() {
        copiedSelection = new ArrayList<>();
    }

    public ArrayList<Blob> getCopiedSelection() {
        return copiedSelection;
    }

    /**
     * Replaces the list of selected blob saved into a new list
     * @param newSelection the new selection list to replace the previously saved list of selected blobs
     */
    public void setCopiedSelection(ArrayList<Blob> newSelection) {
        if (!newSelection.isEmpty())
            // perform deep copy
            newSelection.forEach(blob -> copiedSelection.add(blob.duplicate()));
    }

    public void clearSelection() {
        this.copiedSelection.clear();
    }

    public String toString() {
        String string = "\nTargetClipboard";
        for (Blob blob: copiedSelection) {
            double roundOffX = Math.round(blob.x * 100.0) / 100.0;
            double roundOffY = Math.round(blob.y * 100.0) / 100.0;
            string += "\nBlob " + blob.counter + " at (" + roundOffX + ", " + roundOffY + ")";
        }
        return string;
    }
}
