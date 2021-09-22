package com.example.httpmodule.http;

import rx.Observable;
import rx.Observer;

public abstract class BaseObserver<T> implements Observer<T> {


    @Override
    public void onCompleted() {

    }

    @Override
    public void onError(Throwable e) {
        onFiled(e);
    }

    @Override
    public void onNext(T t) {
        onSuccess(t);
    }

    protected abstract void onSuccess(T t);

    protected abstract void onFiled(Throwable throwable);
}
