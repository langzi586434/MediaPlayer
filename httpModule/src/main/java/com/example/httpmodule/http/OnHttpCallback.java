package com.example.httpmodule.http;

public interface OnHttpCallback<T> {

    // 成功的回调；
    void onSucceed(T t);

    // 失败的回调；
    void onFailed(String exception);
}
