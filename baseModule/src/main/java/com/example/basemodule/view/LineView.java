package com.example.basemodule.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class LineView extends View {
    private Context mContext;
    private int displayWidth, paintWidth, width;
    private int displayHeight, paintHeight, height;
    Paint mPaint;

    public LineView(Context context) {
        super(context);
        mContext = context;
        initDisplay();
        initPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 1; i < 3; i++) {
            canvas.drawLine(paintWidth * i, 0, paintWidth * i, height, mPaint);
            canvas.drawLine(0, paintHeight * i, displayWidth, paintHeight * i, mPaint);
        }
    }


    private void initDisplay() {
        displayWidth = mContext.getDisplay().getWidth();
        displayHeight = mContext.getDisplay().getWidth();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(1);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = right;
        height = bottom;
        paintWidth = (right - left) / 3;
        paintHeight = (bottom - top) / 3;
    }

}
