package com.dvt.forecastlibrary.network;

import com.dvt.forecastlibrary.network.response.ForecastResponse;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ForecastApi {

    @GET("/data/2.5/forecast")
    Single<ForecastResponse> get5DayForecast(@Query("lat") String latitude,
                                             @Query("lon") String longitude,
                                             @Query("appid") String appId,
                                             @Query("units") String units);
}
