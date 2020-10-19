package dev.wazy.prototype.designer.canvas;

import android.graphics.Canvas;

import dev.wazy.prototype.designer.tools.DesignerTool;
import dev.wazy.prototype.designer.tools.EllipsisTool;

public class EllipsisCanvasObject extends ShapeCanvasObject {
    public EllipsisCanvasObject(DesignerCanvas canvas, int x, int y, int width, int height, int color) {
        super(canvas, x, y, width, height, color);
    }

    @Override
    public void draw(Canvas canvas) {
        if (getCanvas().getScale() > 2.5f) {
            getPaint().setAntiAlias(false);
        } else {
            getPaint().setAntiAlias(true);
        }

        canvas.drawOval(getLeft(), getTop(), getRight(), getBottom(), getPaint());
    }

    @Override
    public Class<? extends DesignerTool> getPrimaryTool() {
        return EllipsisTool.class;
    }
}
