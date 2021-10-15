package com.example.mylibrary.base;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.basemodule.R;
import com.example.utilemodule.AppManager;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    protected TextView mTitleText;
    protected TextView mLeftTextOnclick;
    protected ImageView mBackOnclick;
    protected ImageView mRightImageOnclick;
    protected ImageView mLeftImageOnclick;
    protected TextView mRightTextOnclick;
    private long time;


    @SuppressLint("HandlerLeak")
    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
            }
        }
    };

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back_onclick) {
            finish();
        } else {
//            throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayout());
        AppManager.getInstance().addActivity(this);


        initView();
        initData();
    }

    protected void initTopBar() {
        mTitleText = (TextView) findViewById(R.id.title_text);
        mLeftTextOnclick = (TextView) findViewById(R.id.left_text_onclick);
        mBackOnclick = (ImageView) findViewById(R.id.back_onclick);
        mRightImageOnclick = (ImageView) findViewById(R.id.right_image_onclick);
        mLeftImageOnclick = (ImageView) findViewById(R.id.left_image_onclick);
        mRightTextOnclick = (TextView) findViewById(R.id.right_text_onclick);
    }

    protected abstract void initView();

    protected abstract void initData();

    protected abstract int setLayout();

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getInstance().finishActivity(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            if (AppManager.getInstance().getActivityCount() > 1) {
                finish();
            } else {
                if (time != 0 & System.currentTimeMillis() - time < 2000) {
                    AppManager.getInstance().appExit();
                } else {
                    Toast.makeText(this, "再次点击，退出应用", Toast.LENGTH_SHORT).show();
                    time = System.currentTimeMillis();
                }
            }
        }
        return false;
    }

}
