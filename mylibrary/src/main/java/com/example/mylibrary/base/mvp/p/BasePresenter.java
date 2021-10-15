package com.example.mylibrary.base.mvp.p;

import android.content.Context;

import com.example.mylibrary.base.mvp.v.IView;

public abstract class BasePresenter<V extends IView> implements IPresenter {
    protected V mView;
    protected Context mContext;

    protected BasePresenter(V mView, Context context) {
        this.mView = mView;
        this.mContext = context;
    }

}

