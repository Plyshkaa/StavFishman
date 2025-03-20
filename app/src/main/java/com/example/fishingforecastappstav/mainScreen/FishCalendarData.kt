package com.example.fishingforecastappstav.mainScreen

data class FishCalendarData(
    val fishName: String,
    val monthlyActivity: List<Int>
)
private val fishCalendarList = listOf(
    FishCalendarData("Карп", listOf(1,2,3,4,5,5,4,4,3,2,1,1)),
    FishCalendarData("Белый амур", listOf(1,1,2,3,4,5,5,4,3,2,1,1)),
    FishCalendarData("Карась", listOf(1,2,4,5,5,5,5,5,4,3,2,1)),
    FishCalendarData("Лещ", listOf(0,1,2,3,4,5,4,4,3,2,1,0)),
    // и т.д.
)