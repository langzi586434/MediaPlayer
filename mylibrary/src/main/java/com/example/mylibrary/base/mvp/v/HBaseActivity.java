package com.example.mylibrary.base.mvp.v;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.mylibrary.base.BaseActivity;
import com.example.mylibrary.base.mvp.p.BasePresenter;
import com.example.utilemodule.IBroadcastReceiver;


public abstract class HBaseActivity<P extends BasePresenter> extends BaseActivity implements IView, IBroadcastReceiver.onConnect{

    protected P mPresenter;
    private IBroadcastReceiver iBroadcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        iBroadcastReceiver = new IBroadcastReceiver();
        iBroadcastReceiver.setOnConnect(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(iBroadcastReceiver, intentFilter);
    }

    @Override
    protected void initData() {
        initPresenter();
    }

    protected abstract void initPresenter();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mPresenter) {
            mPresenter = null;
        }
        if (iBroadcastReceiver!=null){
            unregisterReceiver(iBroadcastReceiver);
            iBroadcastReceiver = null;
        }
    }
}

