package com.example.mainmodule;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.example.basemodule.base.BaseActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener {


    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {


    }

    @Override
    protected int setLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void onClick(View view) {
        startActivity(new Intent(this, MainBActivity.class));
//        super.onClick(view);

    }
}