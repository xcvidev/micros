package com.xcvi.micros.data.weight

import com.xcvi.micros.data.weight.model.Weight
import com.xcvi.micros.data.weight.model.WeightStats
import com.xcvi.micros.domain.getEpochDate
import com.xcvi.micros.domain.getLocalDate
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus

fun List<WeightStats>.hasData(): Boolean {
    return !(this.isEmpty() || this.all { it.avg == null && it.min == null && it.max == null })
}

fun List<Weight>.groupWeightsByWeek(): Map<LocalDate, List<Weight>> {
    return this.groupBy { stat ->
        val date = stat.timestamp.getLocalDate()
        val dayOfWeek = date.dayOfWeek.isoDayNumber // Monday = 1
        date.minus(dayOfWeek - 1, DateTimeUnit.DAY) // get Monday of that week
    }
}

fun List<Weight>.groupWeightsByMonth(): Map<LocalDate, List<Weight>> {
    val byMonth = this.groupBy { stat ->
        val date = LocalDate.fromEpochDays(stat.timestamp.getEpochDate())
        Pair(date.year, date.monthNumber) // Example: (2025, 6)
    }

    return byMonth.mapKeys {
        LocalDate(year = it.key.first, monthNumber = it.key.second, dayOfMonth = 1)
    }
}

fun List<Weight>.weightAvg(): Double {
    if (this.isEmpty()) return 0.0
    return this.sumOf { it.weight } / this.size
}

/*
class WeightRepository {

    val weights = (0..100).map {
        Weight(
            timestamp = (getToday() - it).getStartTimestamp(),
            value = Random.nextDouble(60.0, 75.0).roundDecimals()
        )
    }.sortedByDescending { it.timestamp }.toMutableList()

    fun getWeights(filterType: FilterType): List<WeightStats> {
        //data from db with or without missing dates
        val data = if (filterType == FilterType.WEEK) {
            weights.groupWeightsByWeek()
        } else {
            weights.groupWeightsByMonth()
        }.toMutableMap()
        //

        val start = data.keys.minOrNull()?.toEpochDays() ?: return emptyList()
        val end = data.keys.maxOrNull()?.toEpochDays() ?: return emptyList()

        for (i in start..end) {
            val dateKey = when (filterType) {
                FilterType.WEEK -> i.getStartOfWeek().getLocalDate()
                FilterType.MONTH -> i.getStartOfMonth().getLocalDate()
            }

            if (!data.containsKey(dateKey)) {
                data[dateKey] = emptyList()
            }
        }

        val weights = data.map { mapEntry ->
            val label = if (filterType == FilterType.WEEK) {
                mapEntry.key.monthFormatted(true) + " " + mapEntry.key.dayOfMonth
            } else {
                mapEntry.key.monthFormatted(true)
            }
            if (mapEntry.value.isEmpty()) {
                WeightStats(
                    date = mapEntry.key.toEpochDays(),
                    label = label
                )
            } else {
                val avg = mapEntry.value.sumOf { it.value } / mapEntry.value.size
                val min = mapEntry.value.minOfOrNull { it.value } ?: 0.0
                val max = mapEntry.value.maxOfOrNull { it.value } ?: 0.0
                val normalized = (avg - min)
                WeightStats(
                    max = max,
                    min = min,
                    avg = avg,
                    normalized = normalized,
                    date = mapEntry.key.toEpochDays(),
                    label = label
                )
            }

        }

        return if (weights.hasData()) {
            weights.sortedBy { it.date }
        } else {
            emptyList()
        }
    }
}

 */


