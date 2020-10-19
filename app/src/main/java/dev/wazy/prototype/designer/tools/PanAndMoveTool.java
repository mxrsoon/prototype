package dev.wazy.prototype.designer.tools;

import android.graphics.Matrix;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import java.lang.reflect.InvocationTargetException;

import dev.wazy.prototype.R;
import dev.wazy.prototype.designer.canvas.CanvasObject;
import dev.wazy.prototype.designer.canvas.DesignerCanvas;

public class PanAndMoveTool extends DesignerTool {
    private boolean panning = false;
    private float startX;
    private float startY;
    private float lastX;
    private float lastY;
    private Matrix inverseMatrix;

    private CanvasObject movingObject;
    private int movingObjectOriginalX;
    private int movingObjectOriginalY;

    public PanAndMoveTool(DesignerCanvas canvas) {
        super(canvas, canvas.getContext().getDrawable(R.drawable.outline_touch_app_24));
        setGestureDetector(new PanAndMoveToolGestureDetector(canvas));
    }

    private class PanAndMoveToolGestureDetector extends GestureDetector {
        private ScaleGestureDetector scaleDetector;
        private DesignerCanvas.OnScaleGestureListener scaleListener;
        private long gestureStartTime;

        public PanAndMoveToolGestureDetector(DesignerCanvas canvas) {
            super(canvas.getContext(), new PanAndMoveToolOnGestureListener());
            scaleListener = new DesignerCanvas.OnScaleGestureListener(canvas, true);
            scaleDetector = new ScaleGestureDetector(canvas.getContext(), scaleListener);
        }

        public boolean onDown(MotionEvent ev) throws IllegalAccessException, InvocationTargetException, InstantiationException {
            if (!panning && movingObject == null) {
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
                    lastX = startX;
                    lastY = startY;
                    panning = true;
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
            } else if (panning) {
                panning = false;
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
            } else if (panning) {
                getCanvas().getManipulationMatrix().postTranslate(ev.getX() - lastX, ev.getY() - lastY);
                getCanvas().invalidate();
                lastX = ev.getX();
                lastY = ev.getY();
            }

            return super.onTouchEvent(ev);
        }

        public boolean onUp(MotionEvent ev) {
            if (panning || movingObject != null) {
                movingObject = null;
                panning = false;
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

    private class PanAndMoveToolOnGestureListener extends GestureDetector.SimpleOnGestureListener {
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
