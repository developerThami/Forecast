package com.dvt.forecastlibrary.network.response.model

import com.google.gson.annotations.SerializedName

class Sys {

    @SerializedName("message")
    var message: Float = 0.0F

    @SerializedName("country")
    var country : String = ""

    @SerializedName("sunrise")
    var sunrise: Float = 0f

    @SerializedName("sunset")
    var sunset : Float = 0f

}