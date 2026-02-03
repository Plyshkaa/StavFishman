package com.example.fishingforecastappstav.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OpenMeteoResponse(
    val current: OpenMeteoCurrent?
)

data class OpenMeteoCurrent(
    @SerializedName("temperature_2m")
    val temperature2m: Float?,
    @SerializedName("wind_speed_10m")
    val windSpeed10m: Float?,
    @SerializedName("pressure_msl")
    val pressureMsl: Float?
)
