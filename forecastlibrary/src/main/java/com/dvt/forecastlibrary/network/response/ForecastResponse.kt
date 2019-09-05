package com.dvt.forecastlibrary.network.response

import com.dvt.forecastlibrary.network.response.model.City
import com.dvt.forecastlibrary.network.response.model.Forecast
import com.google.gson.annotations.SerializedName

class ForecastResponse {

    @SerializedName("cod")
    var cod : String = ""

    @SerializedName("message")
    var message : Float = 0f

    @SerializedName("cnt")
    var cnt : Float = 0f

    @SerializedName("list")
    var forecast : List<Forecast> = emptyList()

    @SerializedName("city")
    var city : City =
        City()
}