package com.example.basemodule.view;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private Context mContext;
    private Camera mCamera;
    private Camera.Parameters parameters;
    private int width;
    private int height;
    String dir = "storage/emulated/0/DCIM/Picture/";

    private void initDisplay() {
        width = mContext.getDisplay().getWidth();
        height = mContext.getDisplay().getWidth();
    }

    public CameraPreview(Context context) {
        super(context);

        setZOrderOnTop(true);
        setZOrderMediaOverlay(true);
        mContext = context;
        holder = getHolder();
        holder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //surface第一次创建时回调
        //打开相机
        initCamera();

    }

    /**
     * 实现拍照功能
     */
    public void takePhoto() {
        //使用takePicture()方法完成拍照
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            //自动聚焦完成后拍照
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success && camera != null) {
                    mCamera.takePicture(new Camera.ShutterCallback() {
                        @Override
                        public void onShutter() {

                        }
                    }, null, new Camera.PictureCallback() {
                        //拍照回调接口
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            savaImage(data);
                            //停止预览
                            mCamera.stopPreview();
                            //重启预览
                            mCamera.startPreview();
                        }
                    });
                }
            }
        });
    }

    private void savaImage(byte[] data) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                File file = new File(dir);
                FileOutputStream fileOutputStream = null;
                //文件夹不存在，则创建它
                if (!file.exists()) {
                    file.mkdir();
                }
                try {
                    fileOutputStream = new FileOutputStream(new File(dir + "/" + System.currentTimeMillis() + ".png"));
                    fileOutputStream.write(data);
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

    }


    private void initParameters() {
        try {
            parameters = mCamera.getParameters();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setFocusMode() {
        FocusThread focusThread = new FocusThread(mCamera, parameters);
        focusThread.run();
    }

    private void setPictureSize() {

        //获取摄像头支持的各种分辨率,因为摄像头数组不确定是按降序还是升序，这里的逻辑有时不是很好找得到相应的尺寸
        //可先确定是按升还是降序排列，再进对对比吧，我这里拢统地找了个，是个不精确的...
        List<Camera.Size> list = parameters.getSupportedPictureSizes();
        int size = 0;
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i).height >= height ) {
                //完美匹配
                size = i;
                break;
            } else {
                //找不到就找个最接近的吧
                size = i;
            }
        }

        //设置照片分辨率，注意要在摄像头支持的范围内选择
        parameters.setPictureSize(list.get(size).width, list.get(size).height);
        //设置照相机参数
        mCamera.setParameters(parameters);
    }
    private void ReleaseCamera()
    {
        if(mCamera != null)
        {
            mCamera.release();
            mCamera = null;
        }
    }
    private void initCamera() {
        ReleaseCamera();
        mCamera = Camera.open(0);
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            parameters = mCamera.getParameters();
            mCamera.setDisplayOrientation(90);
        } catch (IOException e) {
            e.printStackTrace();
        }
        initDisplay();
        initParameters();
        setPictureSize();
        setFocusMode();
    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //surface变化的时候回调(格式/大小)

    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //surface销毁的时候回调
        holder.removeCallback(this);
        mCamera.setPreviewCallback(null);
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

   private Point point = new Point();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("TAG", "onTouchEvent: ");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                point.y = (int) event.getY();
                point.x = (int) event.getX();
                onFocus(point, new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean success, Camera camera) {
                        Log.d("TAG", "onAutoFocus: " + success);
                    }
                });
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }

    private boolean onFocus(Point point, Camera.AutoFocusCallback callback) {
        if (mCamera == null) {
            return false;
        }


        if (parameters.getMaxNumFocusAreas() <= 0) {
            return focus(callback);
        }


        //定点对焦
        List<Camera.Area> areas = new ArrayList<Camera.Area>();
        int left = point.x - 300;
        int top = point.y - 300;
        int right = point.x + 300;
        int bottom = point.y + 300;
        left = Math.max(left, -1000);
        top = Math.max(top, -1000);
        right = Math.min(right, 1000);
        bottom = Math.min(bottom, 1000);
        areas.add(new Camera.Area(new Rect(left, top, right, bottom), 100));
        parameters.setFocusAreas(areas);
        try {
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return false;
        }


        return focus(callback);
    }

    private boolean focus(Camera.AutoFocusCallback callback) {
        try {
            mCamera.autoFocus(callback);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    static class FocusThread extends Thread {
        Camera mCamera;
        Camera.Parameters parameters;

        public FocusThread(Camera mCamera, Camera.Parameters parameters) {
            this.mCamera = mCamera;
            this.parameters = parameters;
        }

        @Override
        public void run() {
            super.run();

        }
    }
}