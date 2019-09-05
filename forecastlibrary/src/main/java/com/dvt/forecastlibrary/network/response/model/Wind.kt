package com.dvt.forecastlibrary.network.response.model

import com.google.gson.annotations.SerializedName

class Wind {

    @SerializedName("speed")
    var speed: Float = 0.0F

    @SerializedName("deg")
    var direction: Float = 0.0F

}