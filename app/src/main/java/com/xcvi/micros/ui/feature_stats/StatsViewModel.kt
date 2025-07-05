package com.xcvi.micros.ui.feature_stats

import androidx.lifecycle.viewModelScope
import com.xcvi.micros.data.food.FoodRepository
import com.xcvi.micros.data.food.model.FoodStats
import com.xcvi.micros.data.weight.WeightRepository
import com.xcvi.micros.data.weight.model.WeightStats
import com.xcvi.micros.domain.Response
import com.xcvi.micros.ui.BaseViewModel
import kotlinx.coroutines.launch

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

        viewModelScope.launch {
            val foods = when (val res = foodRepository.stats()) {
                is Response.Error -> Pair(emptyList(), emptyList())
                is Response.Success -> res.data
            }
            val weights = when (val res = weightRepository.stats()) {
                is Response.Error -> Pair(emptyList(), emptyList())
                is Response.Success ->{
                    val byWeek = res.data.first
                    val byMonth = res.data.second

                    Pair(byWeek, byMonth)
                }
            }

            updateData {
                copy(
                    hasData = true,
                    weightsByWeek =  weights.first,
                    weightsByMonth = weights.second,
                    foodsByWeek = foods.first,
                    foodsByMonth = foods.second,
                )
            }
        }

    }


}






