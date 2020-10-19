package dev.wazy.prototype.designer.tools;

import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import dev.wazy.prototype.designer.canvas.CanvasObject;
import dev.wazy.prototype.designer.canvas.DesignerCanvas;
import dev.wazy.prototype.designer.canvas.ShapeCanvasObject;

public class ShapeTool extends DesignerTool {
    private ShapeCanvasObject currentObject;
    private Matrix inverseMatrix;
    private float startX;
    private float startY;
    private Class<? extends ShapeCanvasObject> shapeClass;
    private Constructor<? extends ShapeCanvasObject> shapeConstructor;
    private CanvasObject movingObject;
    private int movingObjectOriginalX;
    private int movingObjectOriginalY;

    public ShapeTool(DesignerCanvas canvas, Class<? extends ShapeCanvasObject> shapeClass, Drawable icon) throws NoSuchMethodException {
        super(canvas, icon);

        this.shapeClass = shapeClass;
        this.shapeConstructor = shapeClass.getConstructor(DesignerCanvas.class, int.class, int.class, int.class, int.class, int.class);

        setGestureDetector(new ShapeToolGestureDetector(canvas));
    }

    public Class<? extends ShapeCanvasObject> getShapeClass() {
        return shapeClass;
    }

    private class ShapeToolGestureDetector extends GestureDetector {
        private ScaleGestureDetector scaleDetector;
        private DesignerCanvas.OnScaleGestureListener scaleListener;
        private long gestureStartTime;

        public ShapeToolGestureDetector(DesignerCanvas canvas) {
            super(canvas.getContext(), new ShapeToolOnGestureListener());
            scaleListener = new DesignerCanvas.OnScaleGestureListener(canvas, true);
            scaleDetector = new ScaleGestureDetector(canvas.getContext(), scaleListener);
        }

        public boolean onDown(MotionEvent ev) throws IllegalAccessException, InvocationTargetException, InstantiationException {
            if (currentObject == null && movingObject == null) {
                gestureStartTime = ev.getEventTime();
                startX = ev.getX();
                startY = ev.getY();

                DesignerCanvas canvas = getCanvas();
                inverseMatrix = new Matrix(canvas.getManipulationMatrix());
                inverseMatrix.invert(inverseMatrix);
                float[] point = new float[]{startX, startY};
                inverseMatrix.mapPoints(point);

                CanvasObject selectedObject = canvas.getSelectedObject();

                if (selectedObject != null
                        && point[0] >= selectedObject.getLeft() && point[0] <= selectedObject.getRight()
                        && point[1] >= selectedObject.getTop() && point[1] <= selectedObject.getBottom()) {

                    movingObject = selectedObject;
                    movingObjectOriginalX = movingObject.getX();
                    movingObjectOriginalY = movingObject.getY();
                } else {
                    currentObject = shapeConstructor.newInstance(canvas, (int) point[0], (int) point[1], 0, 0, Color.GRAY);
                    currentObject.setOpacity(.5f);
                    canvas.addObject(currentObject);
                }

                return true;
            }

            return super.onTouchEvent(ev);
        }

        public boolean onPointerDown(MotionEvent ev) {
            if (movingObject != null && ev.getEventTime() - gestureStartTime <= 350) {
                movingObject.setX(movingObjectOriginalX);
                movingObject.setY(movingObjectOriginalY);
                movingObject = null;
            } else if (currentObject != null && ev.getEventTime() - gestureStartTime <= 350) {
                getCanvas().removeObject(currentObject);
                currentObject = null;
            }

            return super.onTouchEvent(ev);
        }

        public boolean onMove(MotionEvent ev) {
            if (movingObject != null) {
                DesignerCanvas canvas = getCanvas();
                canvas.getManipulationMatrix().invert(inverseMatrix);
                float[] delta = new float[]{ev.getX(), ev.getY(), startX, startY};

                inverseMatrix.mapPoints(delta);

                movingObject.setX(movingObjectOriginalX + (int) (delta[0] - delta[2]));
                movingObject.setY(movingObjectOriginalY + (int) (delta[1] - delta[3]));

                return true;
            } else if (currentObject != null) {
                DesignerCanvas canvas = getCanvas();
                canvas.getManipulationMatrix().invert(inverseMatrix);
                float[] point = new float[]{ev.getX(), ev.getY()};

                inverseMatrix.mapPoints(point);

                currentObject.setWidth((int) point[0] - currentObject.getX());
                currentObject.setHeight((int) point[1] - currentObject.getY());

                return true;
            }

            return super.onTouchEvent(ev);
        }

        public boolean onUp(MotionEvent ev) {
            if (currentObject != null) {
                if (currentObject.getWidth() == 0 || currentObject.getHeight() == 0) {
                    getCanvas().removeObject(currentObject);
                } else {
                    currentObject.setOpacity(1);
                    getCanvas().setSelectedObject(currentObject);
                }

                currentObject = null;
                return true;
            } else if (movingObject != null) {
                movingObject = null;
                return true;
            }

            return super.onTouchEvent(ev);
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            scaleDetector.onTouchEvent(ev);
            super.onTouchEvent(ev);

            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    try {
                        return onDown(ev);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    }

                case MotionEvent.ACTION_POINTER_DOWN:
                    return onPointerDown(ev);

                case MotionEvent.ACTION_MOVE:
                    return onMove(ev);

                case MotionEvent.ACTION_UP:
                    return onUp(ev);
            }

            return true;
        }
    }

    private class ShapeToolOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            CanvasObject tappedObject = null;
            DesignerCanvas canvas = getCanvas();
            Matrix inverseMatrix = new Matrix(canvas.getManipulationMatrix());

            inverseMatrix.invert(inverseMatrix);
            float[] point = new float[]{e.getX(), e.getY()};

            inverseMatrix.mapPoints(point);

            for (CanvasObject object: getCanvas().getObjects()) {
                if (point[0] >= object.getLeft() && point[0] <= object.getRight()
                        && point[1] >= object.getTop() && point[1] <= object.getBottom()) {
                    tappedObject = object;
                }
            }

            if (tappedObject != null && !tappedObject.isLocked()) {
                getCanvas().setSelectedObject(tappedObject);
            } else {
                getCanvas().setSelectedObject(null);
            }

            return super.onSingleTapConfirmed(e);
        }
    }
}
