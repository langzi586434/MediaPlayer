package com.example.httpmodule.http;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {

    private static RetrofitHelper instance = null;


    public RetrofitApiService createRetrofitHelper() {
        return new Retrofit.Builder()
                .baseUrl(RetrofitApiService.BASE_URL)//服务器地址
                .addConverterFactory(GsonConverterFactory.create())//用Gson把服务端返回的json数据解析成实体
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(getOkhttpClient())
                .build()
                .create(RetrofitApiService.class);
    }

    public static RetrofitHelper getInstance() {
        if (null == instance) {
            synchronized (RetrofitHelper.class) {
                if (null == instance) {
                    instance = new RetrofitHelper();
                }
            }
        }
        return instance;
    }

    //okhttp连接的一些设置
    private OkHttpClient getOkhttpClient() {

        HttpLoggingInterceptor level = new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)//连接超时时间
                .readTimeout(20, TimeUnit.SECONDS)//读超时时间
                .writeTimeout(20, TimeUnit.SECONDS)//写超时时间
                .retryOnConnectionFailure(false)//失败重连
                .addInterceptor(level)
                .addInterceptor(new HeadsInterceptor())         // 自定义的消息头
                .addInterceptor(new CacheInterceptor())
                .build();
    }


}