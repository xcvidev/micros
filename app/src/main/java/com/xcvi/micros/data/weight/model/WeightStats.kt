package com.xcvi.micros.data.weight.model

data class WeightStats(
    val min: Double? = null,
    val max: Double? = null,
    val avg: Double? = null,
    val normalized: Double? = null,
    val label: String = "",
    val date: Int = 0
)