package com.dut2.memorise.game.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.graphics.ColorUtils;
import com.dut2.memorise.R;

public class Block extends AppCompatImageButton {
    private int ARGBcolor;
    private GradientDrawable shape;
    public Block(@NonNull Context context) {
        super(context);
    }

    public Block(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public Block(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Block, defStyleAttr, 0);
        this.setImageResource(R.drawable.block);
        LayerDrawable layerDrawable = (LayerDrawable) this.getDrawable();
        shape = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.btn_filled);
        assert shape != null;
        shape.mutate();
        this.ARGBcolor = a.getColor(R.styleable.Block_color,0);
        shape.setColor(this.ARGBcolor);
        this.setBackground(null);
        this.setScaleType(ScaleType.CENTER_INSIDE);
        a.recycle();
    }

    public void setColor(int resId){
        this.ARGBcolor = getContext().getColor(resId);
        shape.setColor(this.ARGBcolor);
    }

    public void setARGBColor(int ARGBcolor){
        this.ARGBcolor = ARGBcolor;
        shape.setColor(ARGBcolor);
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        float[] array = new float[3];
        if(pressed){
            ColorUtils.colorToHSL(this.ARGBcolor,array);
            array[2] += 0.10f;
            this.shape.setColor(ColorUtils.HSLToColor(array));
        } else {
            ColorUtils.colorToHSL(this.ARGBcolor,array);
            array[2] -= 0.10f;
            this.shape.setColor(ColorUtils.HSLToColor(array));
        }
    }
}
