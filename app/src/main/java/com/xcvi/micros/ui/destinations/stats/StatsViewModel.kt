package com.xcvi.micros.ui.destinations.stats

import com.xcvi.micros.data.food.FoodRepository
import com.xcvi.micros.data.food.model.entity.Portion
import com.xcvi.micros.data.weight.WeightRepository
import com.xcvi.micros.data.weight.model.WeightStats
import com.xcvi.micros.ui.BaseViewModel
import kotlinx.datetime.LocalDate

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
/*
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
        */
    }


}






