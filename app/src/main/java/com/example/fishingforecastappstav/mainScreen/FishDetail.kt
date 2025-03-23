package com.example.fishingforecastappstav.mainScreen

import java.io.Serializable

data class FishDetail(
    val name: String,
    val iconResName: String,
    val shortDescription: String,
    val generalInfo: String,
    val baitsInfo: String,
    val seasonsInfo: String,
    val feedingInfo: String
) : Serializable
