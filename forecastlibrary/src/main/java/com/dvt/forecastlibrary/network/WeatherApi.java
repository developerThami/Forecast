package com.dvt.forecastlibrary.network;

import com.dvt.forecastlibrary.network.response.ForecastResponse;
import com.dvt.forecastlibrary.network.response.WeatherResponse;
import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {

    @GET("/data/2.5/weather")
    Single<WeatherResponse> getCurrentWeather(@Query("lat") String latitude,
                                            @Query("lon") String longitude,
                                            @Query("appid") String appId);
}
