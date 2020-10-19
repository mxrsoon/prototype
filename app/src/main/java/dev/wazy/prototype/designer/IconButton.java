package dev.wazy.prototype.designer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import dev.wazy.prototype.R;

public class IconButton extends androidx.appcompat.widget.AppCompatImageButton implements View.OnClickListener {
    public IconButton(Context context) {
        this(context, null);
    }

    public IconButton(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.iconButtonStyle);
    }

    public IconButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {}
}