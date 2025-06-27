package com.xcvi.micros.ui.destination.food.dashboard

import androidx.room.util.copy
import com.xcvi.micros.domain.FoodRepository
import com.xcvi.micros.domain.Portion
import com.xcvi.micros.ui.BaseViewModel
import com.xcvi.micros.ui.core.getToday
import com.xcvi.micros.ui.destination.Food

class FoodViewModel(
    private val repository: FoodRepository
): BaseViewModel<FoodViewModel.State>(State()){

    data class State(
        val date: Int = getToday(),
        val summary: Portion? = null
    )

    fun getData(date: Int){
        updateData {
            copy(summary = repository.getSummary(date))

        }
    }

    fun setDate(date: Int){
        updateData { copy(date = date) }
        getData(date)
    }
}