package dev.wazy.prototype.designer.canvas;

import android.graphics.Canvas;

import dev.wazy.prototype.designer.tools.DesignerTool;
import dev.wazy.prototype.designer.tools.RectangleTool;

public class RectangleCanvasObject extends ShapeCanvasObject {
    public RectangleCanvasObject(DesignerCanvas canvas, int x, int y, int width, int height, int color) {
        super(canvas, x, y, width, height, color);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(getLeft(), getTop(), getRight(), getBottom(), getPaint());
    }

    @Override
    public Class<? extends DesignerTool> getPrimaryTool() {
        return RectangleTool.class;
    }
}
