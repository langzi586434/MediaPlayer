package com.example.httpmodule.http;

import com.google.gson.JsonObject;

import retrofit2.http.GET;
import rx.Observable;
import rx.Observer;

public interface RetrofitApiService {

    String BASE_URL = "https://www.6tennis.com/";//服务器地址

    @GET("api/getSchedule?type=0&pageNumber=1&pageSize=20")
    Observable<JsonObject> requestWeather();
}
