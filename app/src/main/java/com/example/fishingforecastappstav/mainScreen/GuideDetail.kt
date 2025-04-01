package com.example.fishingforecastappstav.mainScreen


data class GuideDetail(
    val title: String,
    val sections: List<GuideBlock>
)

data class GuideBlock(
    val title: String,
    val content: List<GuideContentItem>
)

data class GuideContentItem(
    val type: String,
    val value: String,
    val label: String? = null
)
