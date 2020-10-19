package dev.wazy.prototype.designer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import dev.wazy.prototype.R;

public class ToggleButton extends IconButton {
    private final static int[] ActiveStateSet = new int[]{android.R.attr.state_active};

    public ToggleButton(Context context) {
        this(context, null);
    }

    public ToggleButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.toggleButtonStyle);
    }

    public ToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private boolean Active;

    public boolean isActive() {
        return Active;
    }

    public void setActive(boolean active) {
        Active = active;
        refreshDrawableState();
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + ActiveStateSet.length);

        if (isActive()) {
            mergeDrawableStates(drawableState, ActiveStateSet);
        }

        return drawableState;
    }

    @Override
    public void onClick(View view) {
        setActive(!isActive());
    }
}