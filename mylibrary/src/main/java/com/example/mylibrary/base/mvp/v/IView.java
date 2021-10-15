package com.example.mylibrary.base.mvp.v;

public interface IView {
    /**
     * 显示加载框
     */
    void showLoading();

    /**
     * 隐藏加载框
     */
    void dismissLoading();

    /**
     * 网络请求失败
     *
     * @param ex   异常信息
     * @param code 错误码
     * @param msg  错误信息
     */
    void onFail(Throwable ex, String code, String msg);

    /**
     * 网络错误
     */
    void onNetError();

}
