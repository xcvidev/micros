package com.xcvi.micros.ui.destination.food.dashboard

import com.xcvi.micros.domain.FoodRepository
import com.xcvi.micros.domain.Portion
import com.xcvi.micros.ui.BaseViewModel
import com.xcvi.micros.ui.core.getToday

class FoodViewModel(
    private val repository: FoodRepository
): BaseViewModel<FoodViewModel.State>(State()){

    data class State(
        val date: Int = getToday(),
        val summary: Portion? = null,
        val meals: Map<Int,List<Portion>> = (1..6).associateWith { emptyList() }
    )

    fun getData(date: Int){
        val meals = (1..6).associateWith { mealNumber ->
            repository.portions.filter { it.date == date && it.meal == mealNumber }
        }.toMutableMap()
        updateData {
            copy(
                summary = repository.getSummary(date),
                meals = meals
            )
        }
    }

    fun setDate(date: Int){
        updateData { copy(date = date) }
        getData(date)
    }
}