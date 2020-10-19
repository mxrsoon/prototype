package dev.wazy.prototype.designer.canvas;

import android.graphics.Canvas;

import dev.wazy.prototype.designer.tools.DesignerTool;

public class GenericCanvasObject implements CanvasObject {
    private DesignerCanvas canvas;
    private int x;
    private int y;
    private int height;
    private int width;
    private float opacity = 1;
    private boolean locked = false;

    public GenericCanvasObject(DesignerCanvas canvas, int x, int y, int width, int height) {
        this.canvas = canvas;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    @Override
    public DesignerCanvas getCanvas() {
        return canvas;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public void setX(int x) {
        this.x = x;
        getCanvas().invalidate();
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setY(int y) {
        this.y = y;
        getCanvas().invalidate();
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
        getCanvas().invalidate();
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
        getCanvas().invalidate();
    }

    @Override
    public int getLeft() {
        return Math.min(getX(), getX() + getWidth());
    }

    @Override
    public int getTop() {
        return Math.min(getY(), getY() + getHeight());
    }

    @Override
    public int getRight() {
        return Math.max(getX(), getX() + getWidth());
    }

    @Override
    public int getBottom() {
        return Math.max(getY(), getY() + getHeight());
    }

    @Override
    public float getOpacity() {
        return opacity;
    }

    @Override
    public void setOpacity(float opacity) {
        this.opacity = Math.min(Math.max(opacity, 0), 1);
        getCanvas().invalidate();
    }

    @Override
    public boolean isLocked() {
        return locked;
    }

    @Override
    public void setLocked(boolean locked) {
        this.locked = locked;
        getCanvas().invalidate();
    }

    @Override
    public void draw(Canvas canvas) { }

    @Override
    public Class<? extends DesignerTool> getPrimaryTool() {
        return null;
    }
}

