package com.xcvi.micros.ui.destinations.stats

import com.xcvi.micros.domain.FoodRepository
import com.xcvi.micros.domain.Portion
import com.xcvi.micros.domain.Weight
import com.xcvi.micros.domain.WeightRepository
import com.xcvi.micros.domain.avg
import com.xcvi.micros.domain.getEpochDate
import com.xcvi.micros.domain.getLocalDate
import com.xcvi.micros.domain.getStartTimestamp
import com.xcvi.micros.domain.getToday
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
        val weightsByWeek: Map<LocalDate, List<Weight>> = emptyMap(),
        val weightsByMonth: Map<LocalDate, List<Weight>> = emptyMap(),
        val foodsByWeek: Map<LocalDate, Portion> = emptyMap(),
        val foodsByMonth: Map<LocalDate, Portion> = emptyMap(),
    )

    fun getData() {
        val weights = weightRepository.weights
        weights.addAll(
            listOf(
                Weight(
                    timestamp = (getToday()+14).getStartTimestamp(),
                    value = 60.0
                ),
                Weight(
                    timestamp = (getToday()+7).getStartTimestamp(),
                    value = 65.0
                )
            )
        )
        val weightsByWeek = weights.groupWeightsByWeek().toMutableMap()
        val weightsByMonth = weights.groupWeightsByMonth().toMutableMap()

        val foods = foodRepository.portions
        val foodsByWeek = foods.groupFoodsByWeek().toMutableMap()
        val foodsByMonth = foods.groupFoodsByMonth().toMutableMap()


        updateData {
            copy(
                hasData = foods.isNotEmpty() && weights.isNotEmpty(),
                weightsByWeek = weightsByWeek,
                weightsByMonth = weightsByMonth,
                foodsByWeek = foodsByWeek,
                foodsByMonth = foodsByMonth,
            )
        }

    }


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
    return this.sumOf { it.value } / this.size
}


fun List<Portion>.groupFoodsByWeek(): Map<LocalDate, Portion> {
    return this.groupBy { stat ->
        val date = stat.date.getLocalDate()
        val dayOfWeek = date.dayOfWeek.isoDayNumber // Monday = 1
        date.minus(dayOfWeek - 1, DateTimeUnit.DAY) // get Monday of that week
    }.mapValues {
        it.value.avg(it.key.toEpochDays())
    }
}

fun List<Portion>.groupFoodsByMonth(): Map<LocalDate, Portion> {
    val byMonth = this.groupBy { stat ->
        val date = LocalDate.fromEpochDays(stat.date)
        Pair(date.year, date.monthNumber) // Pair: (2025, 6)
    }
    return byMonth.mapKeys {
        LocalDate(year = it.key.first, monthNumber = it.key.second, dayOfMonth = 1)
    }.mapValues {
        it.value.avg(it.key.toEpochDays())
    }
}




