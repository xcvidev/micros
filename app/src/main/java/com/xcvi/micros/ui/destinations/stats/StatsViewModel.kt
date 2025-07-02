package com.xcvi.micros.ui.destinations.stats

import com.xcvi.micros.data.entity.Portion
import com.xcvi.micros.domain.FilterType
import com.xcvi.micros.domain.FoodRepository
import com.xcvi.micros.domain.WeightRepository
import com.xcvi.micros.domain.WeightStats
import com.xcvi.micros.domain.avg
import com.xcvi.micros.domain.getLocalDate
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
        val foodsByWeek: Map<LocalDate, Portion> = emptyMap(),
        val foodsByMonth: Map<LocalDate, Portion> = emptyMap(),
    )

    fun getData() {

        val foods = foodRepository.portions.sortedBy { it.date }
        val foodsByWeek = foods.groupFoodsByWeek().toMutableMap()
        val foodsByMonth = foods.groupFoodsByMonth().toMutableMap()

        foodsByWeek.replace(LocalDate(2025, 6, 16), Portion())

        updateData {
            copy(
                hasData = true,
                weightsByWeek = weightRepository.getWeights(FilterType.WEEK),
                weightsByMonth = weightRepository.getWeights(FilterType.MONTH),
                foodsByWeek = foodsByWeek,
                foodsByMonth = foodsByMonth,
            )
        }

    }


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




