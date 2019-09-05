package com.dvt.forecastlibrary.network.response.model

import com.google.gson.annotations.SerializedName

class Forecast {

    @SerializedName("cod")
    var cod: Int = 0

    @SerializedName("id")
    var id: Int = 0

    @SerializedName("name")
    var name: String = ""

    @SerializedName("main")
    var mainInformation = MainInformation()

    @SerializedName("sys")
    var sys: Sys = Sys()

    @SerializedName("dt")
    var dt: Long = 0L

    @SerializedName("clouds")
    var cloud: Cloud =
        Cloud()

    @SerializedName("wind")
    var wind: Wind =
        Wind()

    @SerializedName("weather")
    var weather: List<Weather> = emptyList()

    @SerializedName("coord")
    var coordinates : Coordinates =
        Coordinates()

    @SerializedName("base")
    var base: String = ""

}
