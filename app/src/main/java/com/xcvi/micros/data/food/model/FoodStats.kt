package com.xcvi.micros.data.food.model

import com.xcvi.micros.domain.generateMonthsBetween
import com.xcvi.micros.domain.generateWeeksBetween
import com.xcvi.micros.domain.getStartOfMonth
import com.xcvi.micros.domain.getStartOfWeek

data class FoodStats(
    val date: Int = 0,
    val calories: Double = 0.0,
    val protein: Double = 0.0,
    val carbohydrates: Double = 0.0,
    val fats: Double = 0.0
)

fun List<FoodStats>.groupByWeek(): List<FoodStats> {
    if (this.isEmpty()) return emptyList()
    val grouped = this.groupByPeriod { it.date.getStartOfWeek() }
    val minDate = this.minOf { it.date }
    val maxDate = this.maxOf { it.date }
    val allWeeks = generateWeeksBetween(minDate, maxDate)

    return allWeeks.map { weekStart ->
        grouped[weekStart] ?: FoodStats(date = weekStart)
    }
}

fun List<FoodStats>.groupByMonth(): List<FoodStats> {
    if (this.isEmpty()) return emptyList()
    val grouped = this.groupByPeriod { it.date.getStartOfMonth() }
    val minDate = this.minOf { it.date }
    val maxDate = this.maxOf { it.date }
    val allMonths = generateMonthsBetween(minDate, maxDate)

    return allMonths.map { monthStart ->
        grouped[monthStart] ?: FoodStats(date = monthStart)
    }
}


private fun List<FoodStats>.groupByPeriod(
    keySelector: (FoodStats) -> Int
): Map<Int, FoodStats> {
    return this.groupBy(keySelector).mapValues { (date, group) ->
        val count = group.size.toDouble()
        FoodStats(
            date = date,
            calories = group.sumOf { it.calories } / count,
            protein = group.sumOf { it.protein } / count,
            carbohydrates = group.sumOf { it.carbohydrates } / count,
            fats = group.sumOf { it.fats } / count
        )
    }
}
