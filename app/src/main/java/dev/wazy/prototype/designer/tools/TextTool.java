package dev.wazy.prototype.designer.tools;

import dev.wazy.prototype.R;
import dev.wazy.prototype.designer.canvas.DesignerCanvas;

public class TextTool extends DesignerTool {
    public TextTool(DesignerCanvas canvas) {
        super(canvas, canvas.getContext().getDrawable(R.drawable.baseline_format_shapes_24));
    }
}
