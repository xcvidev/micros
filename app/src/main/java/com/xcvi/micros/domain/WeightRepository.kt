package com.xcvi.micros.domain

import android.icu.util.TimeZone.SystemTimeZoneType
import com.xcvi.micros.ui.core.getLocalDate
import com.xcvi.micros.ui.core.getToday
import com.xcvi.micros.ui.core.roundDecimals
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlin.random.Random

class WeightRepository {

    val weights = (0..100).map {
        Weight(
            timestamp = getLocalDate((getToday() - (it % 7))).atStartOfDayIn(TimeZone.currentSystemDefault()).epochSeconds,
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