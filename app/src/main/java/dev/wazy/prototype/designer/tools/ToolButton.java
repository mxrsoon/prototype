package dev.wazy.prototype.designer.tools;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import dev.wazy.prototype.R;
import dev.wazy.prototype.designer.ExclusiveToggleButton;

public class ToolButton extends ExclusiveToggleButton {
    public ToolButton(Context context) {
        this(context, null);
    }

    public ToolButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.toolButtonStyle);
    }

    public ToolButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private DesignerTool tool;

    public DesignerTool getTool() {
        return this.tool;
    }

    public void setTool(DesignerTool tool) {
        this.tool = tool;

        if (tool.getIcon() != null) {
            setImageDrawable(tool.getIcon());
        }
    }

    @Override
    public void onClick(View view) {
        setActive(true);
    }
}