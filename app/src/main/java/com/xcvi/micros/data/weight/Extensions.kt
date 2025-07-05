package com.xcvi.micros.data.weight

import com.xcvi.micros.data.weight.model.Weight
import com.xcvi.micros.data.weight.model.WeightStats
import com.xcvi.micros.domain.getEpochDate
import com.xcvi.micros.domain.getLocalDate
import com.xcvi.micros.domain.getStartOfMonth
import com.xcvi.micros.domain.getStartOfWeek
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus


fun List<Weight>.groupByWeek(): Map<Int, List<Weight>> {
    if (this.isEmpty()) return emptyMap()

    val grouped = this.groupBy { stat ->
        val date = stat.timestamp.getLocalDate() // LocalDate from your timestamp
        val dayOfWeek = date.dayOfWeek.isoDayNumber // Monday = 1
        date.minus(dayOfWeek - 1, DateTimeUnit.DAY).toEpochDays() // Monday epoch days
    }

    val minEpoch = this.minOf { it.timestamp.getEpochDate() }.getStartOfWeek()
    val maxEpoch = this.maxOf { it.timestamp.getEpochDate() }.getStartOfWeek()

    val allMondays = generateMondaysBetweenEpoch(minEpoch, maxEpoch)

    return allMondays.associateWith { monday ->
        grouped[monday] ?: emptyList()
    }
}

private fun generateMondaysBetweenEpoch(startMonday: Int, endMonday: Int): List<Int> {
    val mondays = mutableListOf<Int>()
    var current = startMonday
    while (current <= endMonday) {
        mondays.add(current)
        current += 7 // add 7 days in epoch days
    }
    return mondays
}

fun List<Weight>.groupByMonth(): Map<Int, List<Weight>> {
    if (this.isEmpty()) return emptyMap()

    val grouped = this.groupBy { stat ->
        stat.timestamp.getEpochDate().getStartOfMonth()
    }

    val minEpochMonth = this.minOf { it.timestamp.getEpochDate() }.getStartOfMonth()
    val maxEpochMonth = this.maxOf { it.timestamp.getEpochDate() }.getStartOfMonth()

    val allMonths = generateMonthsBetweenEpoch(minEpochMonth, maxEpochMonth)

    return allMonths.associateWith { monthStart ->
        grouped[monthStart] ?: emptyList()
    }
}

private fun generateMonthsBetweenEpoch(startMonth: Int, endMonth: Int): List<Int> {
    val months = mutableListOf<Int>()
    var current = startMonth
    while (current <= endMonth) {
        months.add(current)
        current = (LocalDate.fromEpochDays(current).plus(1, DateTimeUnit.MONTH)).toEpochDays()
    }
    return months
}




