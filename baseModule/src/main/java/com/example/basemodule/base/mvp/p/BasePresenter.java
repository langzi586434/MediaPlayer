package com.example.basemodule.base.mvp.p;

import com.example.basemodule.base.mvp.v.IView;

import java.lang.ref.WeakReference;

public abstract class BasePresenter<V extends IView> implements IPresenter {
    protected V mView;

    protected BasePresenter(V mView) {
        this.mView = mView;
    }

}

