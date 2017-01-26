package com.sun.designpattern.structural.proxy;

/**
 * A proxy, in its most general form, is a class functioning as an interface to something else. The proxy could
 * interface to anything: a network connection, a large object in memory, a file, or some other resource that is
 * expensive or impossible to duplicate.
 *
 * Typically one instance of the complex object is created, and multiple proxy objects are created, all of which contain
 * a reference to the single original complex object.
 *
 * @author sunil
 */
class ProxyApp {

    public static void main(String[] args) {
        Image image1 = new ProxyImage("HiRes_10MB_Photo1");
        Image image2 = new ProxyImage("HiRes_10MB_Photo2");

        image1.displayImage(); // loading necessary
        image2.displayImage(); // loading necessary
        image1.displayImage(); // loading unnecessary
    }
}

interface Image {

    public abstract void displayImage();
}

class ProxyImage implements Image {

    private String filename;
    private RealImage image;

    public ProxyImage(String filename) {
        this.filename = filename;
    }

    public void displayImage() {
        if (image == null) {
            image = new RealImage(filename);
        }
        image.displayImage();
    }
}

class RealImage implements Image {

    private String filename;

    public RealImage(String filename) {
        this.filename = filename;
        loadImageFromDisk();
    }

    private void loadImageFromDisk() {
        System.out.println("Loading   " + filename);
    }

    public void displayImage() {
        System.out.println("Displaying " + filename);
    }
}
