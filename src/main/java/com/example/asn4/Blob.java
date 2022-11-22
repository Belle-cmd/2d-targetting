package com.example.asn4;

public class Blob {
    double x,y;

    double r;

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
        orderCounter++;
        counter = orderCounter;
    }

    public void move(double dx, double dy) {
        x += dx;
        y += dy;
    }

    /**
     * Distance of the mouse from the blob
     */
    public boolean contains(double cx, double cy) {
        return dist(cx,cy,x,y) <= r;
    }

    private double dist(double x1,double y1,double x2, double y2) {
        return Math.sqrt(Math.pow(x2-x1,2) + Math.pow(y2-y1,2));
    }
}
