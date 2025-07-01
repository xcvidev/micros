package com.xcvi.micros.ui.destinations.food.dashboard

import com.xcvi.micros.domain.FoodRepository
import com.xcvi.micros.domain.Macros
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
        val meals = repository.portions.subList(0,10).groupBy { it.meal }.toMutableMap()
        meals[2] = emptyList()
        updateData {
            copy(
                meals = meals,
                summary = repository.getSummary(date).copy(
                    macros = Macros(
                        protein = 100.0,
                        carbs = 200.0,
                        fats = 10.0
                    )
                )
            )
        }
    }

    fun setDate(date: Int){
        updateData { copy(date = date) }
        getData(date)
    }
}