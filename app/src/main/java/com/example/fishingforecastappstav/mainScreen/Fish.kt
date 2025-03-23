package com.example.fishingforecastappstav.mainScreen

import java.io.Serializable

data class Fish(
    val name: String,
    val iconResId: Int,
    val shortDescription: String
) : Serializable