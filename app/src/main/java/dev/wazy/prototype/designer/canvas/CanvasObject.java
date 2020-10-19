package dev.wazy.prototype.designer.canvas;

import android.graphics.Canvas;
import android.graphics.Paint;

import dev.wazy.prototype.designer.tools.DesignerTool;

public interface CanvasObject {
    DesignerCanvas getCanvas();

    int getX();

    void setX(int x);

    int getY();

    void setY(int y);

    int getHeight();

    void setHeight(int height);

    int getWidth();

    void setWidth(int width);

    int getLeft();

    int getTop();

    int getRight();

    int getBottom();

    float getOpacity();

    void setOpacity(float opacity);

    boolean isLocked();

    void setLocked(boolean locked);

    void draw(Canvas canvas);

    Class<? extends DesignerTool> getPrimaryTool();
}
