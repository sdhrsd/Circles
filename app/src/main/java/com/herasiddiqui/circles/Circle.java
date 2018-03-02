package com.herasiddiqui.circles;


public class Circle {

    private float centerX;
    private float centerY;
    private float radius;
    private float velocityX;
    private float velocityY;

    Circle() {}

    public Circle(float x, float y, float radius) {
        this.centerX = x;
        this.centerY = y;
        this.radius = radius;
    }

    public Circle(float x, float y, float radius, float velX, float velY) {
        this.centerX = x;
        this.centerY = y;
        this.radius = radius;
        this.velocityX = velX;
        this.velocityY = velY;
    }

    public float getX() {
        return centerX;
    }

    public float getY() {
        return centerY;
    }

    public float getRadius() {
        return radius;
    }

    public void setX(float x) {
        centerX=x;
    }

    public void setY(float y) {
        centerY=y;
    }

    public void setRadius(float radiusSent) {

        radius = radiusSent + radius;
    }

    public void resetRadius(float radiusSent) {

        radius = radiusSent;
    }

    public void setVelocityX(float velX) {
        velocityX = velX;
    }

    public void setVelocityY(float velY) {
        velocityY=velY;
    }

    public float getVelocityX() {
        return velocityX;
    }

    public float getVelocityY() {
        return velocityY;
    }
}
