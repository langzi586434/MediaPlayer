package com.example.basemodule.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.basemodule.R;

public class TopBarView extends LinearLayout {

    private TextView tvTitel;

    private String title;
    private int titleColor;//  字体的color不能是drawable
    private float titleSize;// title 属性

    private String rightText;
    private Drawable rightBackground;
    private int rightTitleColor;  //右边菜单属性

    private String leftText;
    private Drawable leftBackground;
    private int leftTitleColor;// 左菜单属性

    public TopBarView(Context context) {
        super(context);
    }

    public TopBarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);



    }

    public TopBarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TopBarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

}
