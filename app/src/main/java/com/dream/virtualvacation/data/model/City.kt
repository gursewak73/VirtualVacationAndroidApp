package com.dream.virtualvacation.data.model

import com.google.gson.annotations.SerializedName

data class City(
    @SerializedName("name")
    val name: String,
    @SerializedName("videoId")
    val videoId: String
)

data class CitiesResponse(
    @SerializedName("cities")
    val cities: List<City>
)
