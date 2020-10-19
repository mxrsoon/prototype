package dev.wazy.prototype.designer.canvas;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.LinearInterpolator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import dev.wazy.prototype.designer.tools.DesignerTool;

public class DesignerCanvas extends View {
    private Matrix manipulationMatrix = getMatrix();
    private float[] transformValues = new float[9];

    private DesignerTool[] tools;
    private DesignerTool activeTool;
    private CanvasObject selectedObject;
    private ArrayList<CanvasObject> objects = new ArrayList<>();
    private OnSelectionChangeListener onSelectionChangeListener;

    private Paint selectionPaint;
    private Paint altSelectionPaint;

    private int selectionStrokeAlpha = 96;
    private float selectionStrokeWidth = 2f;
    private float selectionStrokeScaledWidth;

    private float selectionDashLength = 8f;
    private float selectionDashOffset = 0f;
    private float selectionDashScaledOffset;
    private float[] selectionDashArray = new float[2];
    private ValueAnimator selectionDashAnimator;
    private long selectionDashAnimationSpeed = 100;

    public DesignerCanvas(Context context) {
        this(context, null);
    }

    public DesignerCanvas(Context context, AttributeSet attrs) {
        super(context, attrs);

        setupSelectionPaint();
        setupSelectionDashAnimation();
    }

    public void setOnSelectionChangeListener(OnSelectionChangeListener onSelectionChangeListener) {
        this.onSelectionChangeListener = onSelectionChangeListener;
    }

    public DesignerTool[] getTools() {
        return tools;
    }

    public void setTools(DesignerTool[] tools) {
        this.tools = tools;
    }

    public ArrayList<CanvasObject> getObjects() {
        return objects;
    }

    public void addObject(CanvasObject object) {
        objects.add(object);
        invalidate();
    }

    public boolean removeObject(CanvasObject object) {
        if (selectedObject == object) {
            setSelectedObject(null);
        }

        if (objects.remove(object)) {
            invalidate();
            return true;
        }

        return false;
    }

    public CanvasObject getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(CanvasObject selectedObject) {
        if (this.selectedObject != selectedObject) {
            this.selectedObject = selectedObject;

            if (selectedObject != null) {
                selectionDashAnimator.start();
            } else {
                selectionDashAnimator.end();
            }

            if (onSelectionChangeListener != null) {
                onSelectionChangeListener.onSelectionChange(selectedObject);
            }

            invalidate();
        }
    }

    public Matrix getManipulationMatrix() {
        return manipulationMatrix;
    }

    public float getScale() {
        float[] values = new float[9];
        getManipulationMatrix().getValues(values);
        return values[Matrix.MSCALE_X];
    }

    public void setScale(float scale) {
        float currentScale = getScale();
        getManipulationMatrix().postScale(scale / currentScale, scale / currentScale);
    }

    public DesignerTool getActiveTool() {
        return activeTool;
    }

    public void setActiveTool(@NotNull DesignerTool tool) {
        this.activeTool = tool;

        if (!tool.getButton().isActive()) {
            tool.getButton().setActive(true);
        }
    }

    public void setActiveTool(Class<? extends DesignerTool> toolClass) {
        for (DesignerTool tool : getTools()) {
            if (tool.getClass() == toolClass) {
                setActiveTool(tool);
                return;
            }
        }
    }

    public GestureDetector getGestureDetector() {
        DesignerTool tool = getActiveTool();

        if (tool != null) {
            return tool.getGestureDetector();
        }

        return null;
    }

    private void setupSelectionPaint() {
        selectionPaint = new Paint();
        altSelectionPaint = new Paint();

        selectionPaint.setStyle(Paint.Style.STROKE);
        selectionPaint.setColor(Color.BLACK);
        selectionPaint.setAlpha(selectionStrokeAlpha);

        altSelectionPaint.setStyle(Paint.Style.STROKE);
        altSelectionPaint.setColor(Color.WHITE);
        altSelectionPaint.setAlpha(selectionStrokeAlpha);
    }

    private void setupSelectionDashAnimation() {
        selectionDashAnimator = ValueAnimator.ofFloat(0, selectionDashLength * 2);

        selectionDashAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                selectionDashOffset = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });

        selectionDashAnimator.setDuration((int) (selectionDashAnimationSpeed * selectionDashLength));
        selectionDashAnimator.setInterpolator(new LinearInterpolator());
        selectionDashAnimator.setRepeatCount(ValueAnimator.INFINITE);
        selectionDashAnimator.setRepeatMode(ValueAnimator.RESTART);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getGestureDetector() != null) {
            return getGestureDetector().onTouchEvent(event);
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setMatrix(manipulationMatrix);

        for (CanvasObject object : getObjects()) {
            object.draw(canvas);
        }

        if (selectedObject != null) {
            manipulationMatrix.getValues(transformValues);
            selectionDashArray[0] = selectionDashArray[1] = selectionDashLength / transformValues[Matrix.MSCALE_X];
            selectionDashScaledOffset = selectionDashOffset / transformValues[Matrix.MSCALE_X];
            selectionStrokeScaledWidth = selectionStrokeWidth / transformValues[Matrix.MSCALE_X];

            selectionPaint.setPathEffect(new DashPathEffect(selectionDashArray, selectionDashScaledOffset));
            selectionPaint.setStrokeWidth(selectionStrokeScaledWidth);

            altSelectionPaint.setPathEffect(new DashPathEffect(selectionDashArray, selectionDashArray[0] + selectionDashScaledOffset));
            altSelectionPaint.setStrokeWidth(selectionStrokeScaledWidth);

            canvas.drawRect(selectedObject.getLeft(), selectedObject.getTop(), selectedObject.getRight(), selectedObject.getBottom(), selectionPaint);
            canvas.drawRect(selectedObject.getLeft(), selectedObject.getTop(), selectedObject.getRight(), selectedObject.getBottom(), altSelectionPaint);
        }
    }

    public static class OnScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private final float MIN_SCALE = .10f;
        private final float MAX_SCALE = 20;
        private DesignerCanvas canvas;
        private boolean handleScroll;
        private float lastFocusX;
        private float lastFocusY;

        public OnScaleGestureListener(DesignerCanvas canvas, boolean handleScroll) {
            this.canvas = canvas;
            this.handleScroll = handleScroll;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            if (handleScroll) {
                lastFocusX = detector.getFocusX();
                lastFocusY = detector.getFocusY();
            }

            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            Matrix transformationMatrix = new Matrix();
            Matrix manipulationMatrix = canvas.getManipulationMatrix();

            float focusX = detector.getFocusX();
            float focusY = detector.getFocusY();

            if (handleScroll) {
                transformationMatrix.postTranslate(focusX - lastFocusX, focusY - lastFocusY);
                lastFocusX = focusX;
                lastFocusY = focusY;
            }

            transformationMatrix.postScale(detector.getScaleFactor(), detector.getScaleFactor(), focusX, focusY);
            manipulationMatrix.postConcat(transformationMatrix);

            float scale = canvas.getScale();

            if (scale > MAX_SCALE) {
                manipulationMatrix.postScale(MAX_SCALE / scale, MAX_SCALE / scale, focusX, focusY);
            } else if (scale < MIN_SCALE) {
                manipulationMatrix.postScale(MIN_SCALE / scale, MIN_SCALE / scale, focusX, focusY);
            }

            canvas.invalidate();
            return true;
        }
    }
}