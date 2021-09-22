package com.example.mainmodule;


import android.util.Log;
import android.view.View;

import com.example.basemodule.base.mvp.v.HBaseActivity;

public class MainBActivity extends HBaseActivity<Presenter> implements Contract.View {
    @Override
    protected void initPresenter() {
        mPresenter = new Presenter(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        mPresenter.getData();
    }

    @Override
    protected void initView() {

    }

    @Override
    protected int setLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void dismissLoading() {

    }

    @Override
    public void onFail(Throwable ex, String code, String msg) {
        Log.d("TAG", "onFail: "+msg);
    }

    @Override
    public void onNetError() {

    }

    @Override
    public void onSuccess() {

    }
}