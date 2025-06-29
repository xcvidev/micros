package com.xcvi.micros.domain

import kotlin.random.Random

class WeightRepository {

    val weights = (0..100).map {
        Weight(
            timestamp = (getToday()-it).getStartTimestamp(),
            value = Random.nextDouble(60.0, 80.0).roundDecimals()
        )
    }.sortedByDescending{ it.timestamp }.toMutableList()


}

data class Weight(
    val timestamp: Long,
    val value: Double,
    val unit: WeightUnit = WeightUnit.kg
)

enum class WeightUnit{
    kg,
    lbs
}