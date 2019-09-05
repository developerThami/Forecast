package com.dvt.forecastlibrary.network.response.model

import com.google.gson.annotations.SerializedName

class City {

    @SerializedName("id")
    var id: Float = 0f

    @SerializedName("country")
    var country: String = ""

    @SerializedName("name")
    var name: String = ""

    @SerializedName("coord")
    var coordinates : Coordinates =
        Coordinates()

}
