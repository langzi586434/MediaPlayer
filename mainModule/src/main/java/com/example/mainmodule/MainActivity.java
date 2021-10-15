package com.example.mainmodule;

import android.content.Intent;
import android.view.View;
import android.widget.TableLayout;

import androidx.viewpager.widget.ViewPager;

import com.example.mylibrary.base.BaseActivity;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private ViewPager mViewPager;
    private TableLayout mBottomBar;

    @Override
    protected void initView() {
        mBottomBar = (TableLayout) findViewById(R.id.bottom_bar);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);

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