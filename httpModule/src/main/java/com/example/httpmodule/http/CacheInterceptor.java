package com.example.httpmodule.http;

import android.text.TextUtils;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class CacheInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originResponse = chain.proceed(chain.request());

        String cacheSetting  = originResponse.cacheControl().toString();

        if(TextUtils.isEmpty(cacheSetting)){
            cacheSetting = "public,max-age=60";
        }

        return originResponse.newBuilder()
                .addHeader("Cache-Control",cacheSetting)
                .removeHeader("Pragma")
                .build();
    }
}