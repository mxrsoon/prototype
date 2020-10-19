package dev.wazy.prototype.designer.tools;

import dev.wazy.prototype.R;
import dev.wazy.prototype.designer.canvas.DesignerCanvas;
import dev.wazy.prototype.designer.canvas.EllipsisCanvasObject;

public class EllipsisTool extends ShapeTool {
    public EllipsisTool(DesignerCanvas canvas) throws NoSuchMethodException {
        super(canvas, EllipsisCanvasObject.class, canvas.getContext().getDrawable(R.drawable.baseline_radio_button_unchecked_24));
    }
}