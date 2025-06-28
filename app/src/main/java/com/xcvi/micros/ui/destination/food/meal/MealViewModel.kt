package com.xcvi.micros.ui.destination.food.meal

import com.xcvi.micros.domain.FoodRepository
import com.xcvi.micros.domain.Portion
import com.xcvi.micros.domain.summary
import com.xcvi.micros.ui.BaseViewModel
import com.xcvi.micros.ui.core.nextAmount
import com.xcvi.micros.ui.core.previousAmount

class MealViewModel(
    private val repository: FoodRepository
): BaseViewModel<MealViewModel.State>(State()){

    data class State(
        val summary: Portion? = null,
        val mealLabel:String = "",
        val portions: List<Portion> = emptyList()
    )

    fun getData(date: Int, meal: Int){
        val portions = repository.getPortions(date, meal)
        updateData {
            copy(
                summary = portions.summary(),
                portions = repository.getPortions(date, meal),
            )
        }
    }

    fun increasePortion(portion: Portion){
        repository.updatePortion(portion, portion.amount.nextAmount())
        getData(portion.date, portion.meal)
    }

    fun decreasePortion(portion: Portion){
        repository.updatePortion(portion, portion.amount.previousAmount())
        getData(portion.date, portion.meal)
    }

    fun saveCustomMeal(name: String){
        repository.saveCustomMeal(name, state.portions)
    }
}