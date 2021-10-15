package com.example.mylibrary.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;

public class LineView extends View {
    private Context mContext;
    private int displayWidth, paintWidth, width;
    private int displayHeight, paintHeight, height;
    private boolean isVisableHide = true;
    private boolean isFourVisableHide = true;
    private Point point = new Point();
    private Paint paint = new Paint();
    private CanvasThread canvasThread;

    public LineView(Context context) {
        super(context);
        mContext = context;
        initDisplay();
        initPaint();
    }

    @Override
    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isVisableHide) {
            for (int i = 1; i < 3; i++) {
                canvas.drawLine(paintWidth * i, 0, paintWidth * i, height, paint);
                canvas.drawLine(0, paintHeight * i, displayWidth, paintHeight * i, paint);
            }
        }
        if (isFourVisableHide) {
            int left = point.x - 100;
            int top = point.y - 100;
            int right = point.x + 100;
            int bottom = point.y + 100;
            Rect rect = new Rect(left, top, right, bottom);
            RectF rectF = new RectF(left + 40, top + 40, right - 40, bottom - 40);

            paint.setStrokeWidth(2);
            paint.setColor(Color.WHITE);
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(rect, paint);
            canvas.drawArc(rectF, 0, 360, false, paint);

            canvasThread = new CanvasThread();
            canvasThread.start();
        }
    }

    private void initDisplay() {
        displayWidth = mContext.getDisplay().getWidth();
        displayHeight = mContext.getDisplay().getWidth();
    }

    private void initPaint() {
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(1);

    }

    class CanvasThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                Thread.sleep(1000);
                isFourVisableHide = false;
                invalidate();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = right;
        height = bottom;
        point.x = width / 2;
        point.y = height / 2;
        paintWidth = (right - left) / 3;
        paintHeight = (bottom - top) / 3;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP) {
            point.x = (int) event.getX();
            point.y = (int) event.getY();
            isFourVisableHide = true;
            invalidate();
        }
        return super.onTouchEvent(event);
    }
}
