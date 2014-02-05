package com.thenathanjones.ibeaconlibrary;

public class Vector2 {

    public double x;
    public double y;

    private Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static Vector2 of(double x, double y) {
        return new Vector2(x, y);
    }

    public double dotProduct(Vector2 otherVector) {
        return x * otherVector.x + y * otherVector.y;
    }

    public double modulus() {
        return Math.sqrt(dotProduct(this));
    }

    public Vector2 subtract(Vector2 toSubtract) {
        return Vector2.of(x - toSubtract.x, y - toSubtract.y);
    }

    public double distanceFrom(Vector2 otherVector) {
        double sum = Math.pow(x - otherVector.x, 2);
        sum += Math.pow(y - otherVector.y, 2);

        return Math.sqrt(sum);
    }

    public Vector2 multiply(double toMultiply) {
        return Vector2.of(x * toMultiply,y * toMultiply);
    }

    public Vector2 multiply(Vector2 toMultiply) {
        return Vector2.of(x * toMultiply.x, y * toMultiply.y);
    }

    public Vector2 add(Vector2 toAdd) {
        return Vector2.of(x + toAdd.x, y + toAdd.y);
    }

    public Vector2 toUnitVector() {
        return Vector2.of(x / modulus(), y / modulus());
    }
}
