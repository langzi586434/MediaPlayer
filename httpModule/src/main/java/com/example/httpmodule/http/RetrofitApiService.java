package com.example.httpmodule.http;

import com.google.gson.JsonObject;

import retrofit2.http.GET;
import rx.Observable;
import rx.Observer;

public interface RetrofitApiService {

    String BASE_URL = "http://xxxxxxxxxx/";//服务器地址

    @GET()
    Observable<JsonObject> requestWeather();
}
