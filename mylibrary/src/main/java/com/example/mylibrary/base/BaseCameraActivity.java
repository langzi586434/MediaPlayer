package com.example.mylibrary.base;

import android.Manifest;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.example.mylibrary.view.CameraPreview;
import com.example.mylibrary.view.LineView;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.view.KeyEvent.KEYCODE_VOLUME_DOWN;
import static android.view.KeyEvent.KEYCODE_VOLUME_UP;

public abstract class BaseCameraActivity extends BaseActivity {
    private CameraPreview preview;
    protected FrameLayout mFrameLayout;

    private void initCamera() {
        preview = new CameraPreview(this);
        mFrameLayout.addView(preview);
        mFrameLayout.addView(new LineView(this));
    }

    private void desCamera() {
        preview = null;
        mFrameLayout.removeAllViews();
    }

    @Override
    protected void initData() {
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
        initCamera();
    }

    protected void clickPhone(){
        preview.takePhoto();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        desCamera();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KEYCODE_VOLUME_UP || keyCode == KEYCODE_VOLUME_DOWN) {
            preview.takePhoto();
        } else {
            return super.onKeyDown(keyCode, event);
        }
        return true;
    }
}
