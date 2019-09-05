package com.dvt.forecastlibrary.network.response.model

import com.google.gson.annotations.SerializedName

class MainInformation {

    @SerializedName("pressure")
    var pressure: Float = 0f

    @SerializedName("humidity")
    var humidity: Float = 0f

    @SerializedName("temp")
    var tempurature: Float = 0f

    @SerializedName("temp_min")
    var minimumTempurature: Float = 0f

    @SerializedName("temp_max")
    var maximumTempurature : Float = 0f

}