package dev.wazy.prototype.designer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import dev.wazy.prototype.R;

public class ExclusiveToggleButton extends ToggleButton {
    public ExclusiveToggleButton(Context context) {
        super(context);
    }

    public ExclusiveToggleButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExclusiveToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);

        ViewParent parent = getParent();

        if (active && parent instanceof ViewGroup) {
            ViewGroup parentViewGroup = (ViewGroup) parent;

            for (int i = 0; i < parentViewGroup.getChildCount(); i++) {
                View child = parentViewGroup.getChildAt(i);

                if (this != child && child instanceof ExclusiveToggleButton) {
                    ((ToggleButton) child).setActive(false);
                }
            }
        }
    }
}