package dev.wazy.prototype.designer.canvas;

import android.graphics.Paint;

public class ShapeCanvasObject extends GenericCanvasObject {
    private Paint paint;
    private int color;

    public ShapeCanvasObject(DesignerCanvas canvas, int x, int y, int width, int height, int color) {
        super(canvas, x, y, width, height);
        setupPaint();
        setColor(color);
    }

    private void setupPaint() {
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(false);
    }

    public Paint getPaint() {
        return paint;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        paint.setColor(color);
        getCanvas().invalidate();
    }

    @Override
    public void setOpacity(float opacity) {
        super.setOpacity(opacity);
        int alpha = (int) (getOpacity() * 255);
        paint.setAlpha(alpha);
        getCanvas().invalidate();
    }
}
