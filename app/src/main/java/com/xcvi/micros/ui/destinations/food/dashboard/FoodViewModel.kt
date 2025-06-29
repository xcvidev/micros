package com.xcvi.micros.ui.destinations.food.dashboard

import com.xcvi.micros.domain.FoodRepository
import com.xcvi.micros.domain.Portion
import com.xcvi.micros.domain.getToday
import com.xcvi.micros.ui.BaseViewModel

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
        meals[4] = emptyList()
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