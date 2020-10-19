package dev.wazy.prototype.designer.tools;

import dev.wazy.prototype.R;
import dev.wazy.prototype.designer.canvas.DesignerCanvas;
import dev.wazy.prototype.designer.canvas.RectangleCanvasObject;

public class RectangleTool extends ShapeTool {
    public RectangleTool(DesignerCanvas canvas) throws NoSuchMethodException {
        super(canvas, RectangleCanvasObject.class, canvas.getContext().getDrawable(R.drawable.sharp_check_box_outline_blank_24));
    }
}