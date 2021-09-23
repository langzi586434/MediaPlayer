package com.example.basemodule.base.mvp.p;

import android.content.Context;

import com.example.basemodule.base.mvp.v.IView;

import java.lang.ref.WeakReference;

public abstract class BasePresenter<V extends IView> implements IPresenter {
    protected V mView;
    protected Context mContext;

    protected BasePresenter(V mView, Context context) {
        this.mView = mView;
        this.mContext = context;
    }

}

