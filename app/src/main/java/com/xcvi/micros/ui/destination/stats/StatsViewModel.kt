package com.xcvi.micros.ui.destination.stats

import com.xcvi.micros.domain.FoodRepository
import com.xcvi.micros.domain.Weight
import com.xcvi.micros.domain.WeightRepository
import com.xcvi.micros.ui.BaseViewModel
import com.xcvi.micros.ui.destination.FoodGraph

class StatsViewModel(
    private val foodRepository: FoodRepository,
    private val weightRepository: WeightRepository
): BaseViewModel<StatsViewModel.State>(State()) {
    data class State(
        val weights: List<Weight> = emptyList(),
        val foods: List<FoodGraph> = emptyList()
    )

    fun getData(){

    }
}