package com.entersekt.citylibrary.network

import com.dvt.forecastlibrary.network.ForecastApi
import com.dvt.forecastlibrary.network.WeatherApi
import com.dvt.forecastlibrary.network.response.ForecastResponse
import com.dvt.forecastlibrary.network.response.WeatherResponse
import com.google.gson.GsonBuilder
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

import java.util.concurrent.TimeUnit

class RetrofitForecastClient {

    private var forecastApi: ForecastApi
    private var weatherApi:WeatherApi

    private val appId = "24262fedf640971dde9ca5aed73db2b4"

    init {


        val baseUrl = "https://api.openweathermap.org/"

        val gson = GsonBuilder().setLenient().create()

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder().client(okHttpClient).baseUrl(baseUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        forecastApi = retrofit.create(ForecastApi::class.java)
        weatherApi = retrofit.create(WeatherApi::class.java)
    }

    fun fetchForecast(latitude: String, longitude: String): Single<ForecastResponse> {
        return forecastApi.get5DayForecast(latitude, longitude, appId, "metric")
    }

    fun fetchCurrentWeather(latitude: String, longitude: String): Single<WeatherResponse> {
        return weatherApi.getCurrentWeather(latitude, longitude, appId,"metric")
    }

}
