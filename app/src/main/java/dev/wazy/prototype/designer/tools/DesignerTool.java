package dev.wazy.prototype.designer.tools;

import android.graphics.drawable.Drawable;
import android.view.GestureDetector;

import dev.wazy.prototype.designer.canvas.DesignerCanvas;

public class DesignerTool {
    private DesignerCanvas canvas;
    private Drawable icon;
    private GestureDetector gestureDetector;
    private ToolButton button;

    public DesignerTool(DesignerCanvas canvas, Drawable icon) {
        this(canvas, icon, null);
    }

    public DesignerTool(DesignerCanvas canvas, Drawable icon, GestureDetector gestureDetector) {
        this.canvas = canvas;
        setIcon(icon);
        setGestureDetector(gestureDetector);
    }

    public DesignerCanvas getCanvas() {
        return canvas;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public GestureDetector getGestureDetector() {
        return gestureDetector;
    }

    public void setGestureDetector(GestureDetector gestureDetector) {
        this.gestureDetector = gestureDetector;
    }

    public ToolButton getButton() {
        return button;
    }

    public void setButton(ToolButton button) {
        this.button = button;
    }
}