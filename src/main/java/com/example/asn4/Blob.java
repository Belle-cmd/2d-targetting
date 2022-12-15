package com.example.asn4;

public class Blob {
    public double x,y;

    public double r;

    /** The radius before changes in its size occur */
    public double initialRadius;

    /** Stands for the order number of blobs created */
    private static int orderCounter = 0;

    int counter = 0;


    /**
     * Constructor methods
     * @param nx mouseX position
     * @param ny mouseY position
     */
    public Blob(double nx, double ny) {
        x = nx;
        y = ny;
        r = 50;
        initialRadius = r;
        orderCounter++;
        counter = orderCounter;
    }

    public void move(double dx, double dy) {
        x += dx;
        y += dy;
    }

    public boolean contains(double cx, double cy) {
        return dist(cx,cy,x,y) <= r;
    }

    private double dist(double x1,double y1,double x2, double y2) {
        return Math.sqrt(Math.pow(x2-x1,2) + Math.pow(y2-y1,2));
    }

    /**
     * Create a deep copy of the blob object
     * @return copy of the blob
     */
    public Blob duplicate() {
        Blob newBlob = new Blob(x, y);
        newBlob.r = this.r;
        newBlob.initialRadius = initialRadius;
        newBlob.counter = counter;
        return newBlob;
    }

    @Override
    public String toString() {
        double roundOffX = Math.round(x * 100.0) / 100.0;
        double roundOffY = Math.round(y * 100.0) / 100.0;
        return "Blob " + counter + " at (" + roundOffX + ", " + roundOffY + ")\n";
    }
}
