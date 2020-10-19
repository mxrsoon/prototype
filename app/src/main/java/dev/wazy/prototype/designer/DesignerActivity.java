package dev.wazy.prototype.designer;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import dev.wazy.prototype.R;
import dev.wazy.prototype.designer.canvas.CanvasObject;
import dev.wazy.prototype.designer.canvas.DesignerCanvas;
import dev.wazy.prototype.designer.canvas.GenericCanvasObject;
import dev.wazy.prototype.designer.canvas.OnSelectionChangeListener;
import dev.wazy.prototype.designer.canvas.RectangleCanvasObject;
import dev.wazy.prototype.designer.canvas.ShapeCanvasObject;
import dev.wazy.prototype.designer.colorpicker.ColorSaturationAndVibrancyPicker;
import dev.wazy.prototype.designer.colorpicker.OnColorChangeListener;
import dev.wazy.prototype.designer.tools.DesignerTool;
import dev.wazy.prototype.designer.tools.EllipsisTool;
import dev.wazy.prototype.designer.tools.PanAndMoveTool;
import dev.wazy.prototype.designer.tools.RectangleTool;
import dev.wazy.prototype.designer.tools.TextTool;
import dev.wazy.prototype.designer.tools.ToolButton;

public class DesignerActivity extends AppCompatActivity {
    private DesignerTool[] tools;
    private DesignerCanvas canvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.designer_layout);
        
        canvas = findViewById(R.id.designer_canvas);

        try {
            setupDeleteButton();
            setupTools();
            setupPanels();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        CanvasObject background = new RectangleCanvasObject(canvas, 0, 0, 600, 1000, Color.WHITE);
        background.setLocked(true);
        canvas.addObject(background);
        canvas.addObject(new RectangleCanvasObject(canvas, 100, 100, 150, 300, getColor(R.color.accent)));
    }

    private void setupDeleteButton() {
        findViewById(R.id.designer_delete_object).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CanvasObject selectedObject = canvas.getSelectedObject();

                if (selectedObject != null) {
                    canvas.removeObject(selectedObject);
                }
            }
        });
    }

    private void setupTools() throws NoSuchMethodException {
        tools = new DesignerTool[]{
                new PanAndMoveTool(canvas),
                new RectangleTool(canvas),
                new EllipsisTool(canvas),
                new TextTool(canvas)
        };

        populateToolBar();
        canvas.setTools(tools);
        canvas.setActiveTool(tools[0]);
    }

    private void setupPanels() {
        final View panel = findViewById(R.id.designer_panel);
        LinearLayout panelButtons = findViewById(R.id.designer_panels);

        panel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        for (int i = 0; i < panelButtons.getChildCount(); i++) {
            final ExclusiveToggleButton button = (ExclusiveToggleButton) panelButtons.getChildAt(i);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    button.setActive(!button.isActive());

                    if (button.isActive()) {
                        panel.setVisibility(View.VISIBLE);
                    } else {
                        panel.setVisibility(View.GONE);
                    }
                }
            });
        }

        final ColorSaturationAndVibrancyPicker colorPicker = panel.findViewById(R.id.designer_colorpicker);
        colorPicker.setOnColorChangeListener(new OnColorChangeListener() {
            @Override
            public void onColorChange(int newColor) {
                CanvasObject selectedObject = canvas.getSelectedObject();

                if (selectedObject instanceof ShapeCanvasObject) {
                    ((ShapeCanvasObject) selectedObject).setColor(newColor);
                }
            }
        });

        canvas.setOnSelectionChangeListener(new OnSelectionChangeListener() {
            @Override
            public void onSelectionChange(CanvasObject selectedObject) {
                if (selectedObject instanceof ShapeCanvasObject) {
                    colorPicker.setColor(((ShapeCanvasObject) selectedObject).getColor());
                }
            }
        });
    }

    private void populateToolBar() {
        ViewGroup toolsWrapper = findViewById(R.id.designer_tools);

        for (DesignerTool tool : tools) {
            ToolButton button = (ToolButton) getLayoutInflater().inflate(R.layout.designer_toolbutton, null);
            button.setTool(tool);
            button.setOnClickListener(new ToolButtonOnClickListener(button));
            tool.setButton(button);
            toolsWrapper.addView(button);
        }
    }

    private class ToolButtonOnClickListener implements View.OnClickListener {
        private ToolButton button;

        public ToolButtonOnClickListener(ToolButton button) {
            this.button = button;
        }

        @Override
        public void onClick(View view) {
            canvas.setActiveTool(button.getTool());
        }
    }
}

