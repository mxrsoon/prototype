package dev.wazy.prototype.designer.colorpicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import dev.wazy.prototype.R;

public class ColorSaturationAndVibrancyPicker extends View {
    private float hue = 0;
    private float saturation = .8f;
    private float vibrancy = 1;
    private Paint backgroundPaint = new Paint();
    private Paint strokePaint = new Paint();
    private Paint selectedColorPaint = new Paint();
    private Paint gradientPaint = new Paint();
    private LinearGradient saturationGradient;
    private LinearGradient vibrancyGradient;
    private OnColorChangeListener onColorChangeListener;

    public ColorSaturationAndVibrancyPicker(Context context) {
        this(context, null);
    }

    public ColorSaturationAndVibrancyPicker(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.iconButtonStyle);
    }

    public ColorSaturationAndVibrancyPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        updateColor();
        setupNeedle();
    }

    public void setOnColorChangeListener(OnColorChangeListener onColorChangeListener) {
        this.onColorChangeListener = onColorChangeListener;
    }

    public float getHue() {
        return hue;
    }

    public void setHue(float hue) {
        if (hue != this.hue) {
            this.hue = hue;

            if (onColorChangeListener != null) {
                onColorChangeListener.onColorChange(getColor());
            }

            updateColor();
        }
    }

    public float getSaturation() {
        return saturation;
    }

    public void setSaturation(float saturation) {
        if (saturation != this.saturation) {
            this.saturation = saturation;

            if (onColorChangeListener != null) {
                onColorChangeListener.onColorChange(getColor());
            }

            updateColor();
        }
    }

    public float getVibrancy() {
        return vibrancy;
    }

    public void setVibrancy(float vibrancy) {
        if (vibrancy != this.vibrancy) {
            this.vibrancy = vibrancy;

            if (onColorChangeListener != null) {
                onColorChangeListener.onColorChange(getColor());
            }

            updateColor();
        }
    }

    public int getColor() {
        return Color.HSVToColor(new float[]{getHue(), getSaturation(), getVibrancy()});
    }

    public void setColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);

        if (this.hue != hsv[0] || this.saturation != hsv[1] || this.vibrancy != hsv[2]) {
            this.hue = hsv[0];
            this.saturation = hsv[1];
            this.vibrancy = hsv[2];

            if (onColorChangeListener != null) {
                onColorChangeListener.onColorChange(getColor());
            }

            updateColor();
        }
    }

    private void setupNeedle() {
        backgroundPaint.setColor(getContext().getColor(R.color.background));
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setAntiAlias(true);

        strokePaint.setColor(getContext().getColor(R.color.designerBorder));
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        strokePaint.setAntiAlias(true);

        selectedColorPaint.setColor(getColor());
        selectedColorPaint.setStyle(Paint.Style.FILL);
        selectedColorPaint.setAntiAlias(true);
    }

    private void updateColor() {
        saturationGradient = new LinearGradient(0, 0, getWidth(), 0, Color.HSVToColor(new float[]{getHue(), 0, 1}), Color.HSVToColor(new float[]{getHue(), 1, 1}), Shader.TileMode.CLAMP);
        vibrancyGradient = new LinearGradient(0, 0, 0, getHeight(), Color.HSVToColor(0, new float[]{getHue(), 0, 0}), Color.HSVToColor(new float[]{getHue(), 0, 0}), Shader.TileMode.CLAMP);
        selectedColorPaint.setColor(getColor());
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateColor();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        gradientPaint.setShader(saturationGradient);
        canvas.drawRect(0, 0, getWidth(), getHeight(), gradientPaint);
        gradientPaint.setShader(vibrancyGradient);
        canvas.drawRect(0, 0, getWidth(), getHeight(), gradientPaint);
        canvas.drawRect(0, 0, getWidth(), getHeight(), strokePaint);

        canvas.drawCircle(getSaturation() * getWidth(), (1 - getVibrancy()) * getHeight(), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()), backgroundPaint);
        canvas.drawCircle(getSaturation() * getWidth(), (1 - getVibrancy()) * getHeight(), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()), selectedColorPaint);
        canvas.drawCircle(getSaturation() * getWidth(), (1 - getVibrancy()) * getHeight(), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()), strokePaint);
    }

    private boolean handleTouch(MotionEvent event) {
        float saturation = Math.max(Math.min(event.getX() / getWidth(), 1), 0);
        float vibrancy = 1 - Math.max(Math.min(event.getY() / getHeight(), 1), 0);

        setColor(Color.HSVToColor(new float[]{getHue(), saturation, vibrancy}));

        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                return handleTouch(event);
        }

        return super.onTouchEvent(event);
    }
}