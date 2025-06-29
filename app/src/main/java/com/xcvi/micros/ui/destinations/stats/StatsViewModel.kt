package com.xcvi.micros.ui.destinations.stats

import com.xcvi.micros.domain.FoodRepository
import com.xcvi.micros.domain.WeightRepository
import com.xcvi.micros.domain.getEpochDate
import com.xcvi.micros.domain.getToday
import com.xcvi.micros.domain.normalize
import com.xcvi.micros.domain.summary
import com.xcvi.micros.ui.BaseViewModel
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus

class StatsViewModel(
    private val foodRepository: FoodRepository,
    private val weightRepository: WeightRepository
) : BaseViewModel<StatsViewModel.State>(State()) {
    data class State(
        val hasData: Boolean = false,
        val weightsByWeek: List<WeightStats> = emptyList(),
        val weightsByMonth: List<WeightStats> = emptyList(),
        val foodsByWeek: List<FoodStats> = emptyList(),
        val foodsByMonth: List<FoodStats> = emptyList(),
    )

    fun getData() {
        val weights = weightRepository.weights
            .map { WeightStats(it.value, it.timestamp.getEpochDate()) }
        val weightsByWeek = weights.groupWeightsByWeek()
        val weightsByMonth = weights.groupWeightsByMonth()

        val foods = foodRepository.portions
            .groupBy { it.date }
            .map { FoodStats(it.value.summary().calories, it.key) }
        val foodsByWeek = foods.groupFoodsByWeek()
        val foodsByMonth = foods.groupFoodsByMonth()


        updateData {
            copy(
                hasData = foods.isNotEmpty() && weights.isNotEmpty(),
                weightsByWeek = weightsByWeek,
                weightsByMonth = weightsByMonth,
                foodsByWeek =foodsByWeek,
                foodsByMonth = foodsByMonth,
            )
        }


    }


}

data class WeightStats(
    val value: Double,
    val label: Int
)

data class FoodStats(
    val value: Double, // 2000.0 kcal
    val label: Int // Tuesday May, ...
)
fun List<FoodStats>.protein(): Double {
    return this.sumOf { it.value * 0.3 }
}
fun List<FoodStats>.carbs(): Double {
    return this.sumOf { it.value * 0.3 }
}
fun List<FoodStats>.fats(): Double {
    return this.sumOf { it.value * 0.3 }
}

fun List<WeightStats>.groupWeightsByWeek(): List<WeightStats> {
    return this.groupBy { stat ->
        val date = LocalDate.fromEpochDays(stat.label)
        val dayOfWeek = date.dayOfWeek.isoDayNumber // Monday = 1
        date.minus(dayOfWeek - 1, DateTimeUnit.DAY) // get Monday of that week
    }.map {
        WeightStats(
            value = it.value.weightAvg(),
            label = it.key.toEpochDays()
        )
    }
}

fun List<WeightStats>.groupWeightsByMonth(): List<WeightStats> {
    val byMonth = this.groupBy { stat ->
        val date = LocalDate.fromEpochDays(stat.label)
        Pair(date.year, date.monthNumber) // Example: (2025, 6)
    }

    return byMonth.mapKeys {
        LocalDate(year = it.key.first, monthNumber = it.key.second, dayOfMonth = 1)
    }.map {
        WeightStats(
            value = it.value.weightAvg(),
            label = it.key.toEpochDays()
        )
    }
}

fun List<WeightStats>.weightAvg(): Double {
    return this.sumOf { it.value } / this.size
}


fun List<FoodStats>.groupFoodsByWeek(): List<FoodStats> {
    return this.groupBy { stat ->
        val date = LocalDate.fromEpochDays(stat.label)
        val dayOfWeek = date.dayOfWeek.isoDayNumber // Monday = 1
        date.minus(dayOfWeek - 1, DateTimeUnit.DAY) // get Monday of that week
    }.map {
        FoodStats(
            value = it.value.avgCalories(),
            label = it.key.toEpochDays()
        )
    }
}

fun List<FoodStats>.groupFoodsByMonth(): List<FoodStats> {
    val byMonth = this.groupBy { stat ->
        val date = LocalDate.fromEpochDays(stat.label)
        Pair(date.year, date.monthNumber) // Example: (2025, 6)
    }

    return byMonth.mapKeys {
        LocalDate(year = it.key.first, monthNumber = it.key.second, dayOfMonth = 1)
    }.map {
        FoodStats(
            value = it.value.avgCalories(),
            label = it.key.toEpochDays()
        )
    }
}

fun List<FoodStats>.avgCalories(): Double {
    return this.sumOf { it.value } / this.size
}




