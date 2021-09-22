package com.example.mainmodule;


import android.util.Log;

import com.example.basemodule.base.mvp.p.BasePresenter;
import com.example.httpmodule.http.BaseObserver;
import com.example.httpmodule.http.RetrofitHelper;
import com.google.gson.JsonObject;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class Presenter extends BasePresenter<Contract.View> implements Contract.Presenter {


    public Presenter(Contract.View mView) {
        super(mView);
    }

    @Override
    public void getData() {
        Subscription subscribe = RetrofitHelper.getInstance()
                .createRetrofitHelper()
                .requestWeather()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new BaseObserver<JsonObject>() {
                    @Override
                    protected void onSuccess(JsonObject jsonObject) {

                    }

                    @Override
                    protected void onFiled(Throwable throwable) {
                        Log.d("TAG", "onFiled    : "+throwable.getMessage());
                    }

                });
    }
}