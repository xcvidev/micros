package com.xcvi.micros.ui.feature_food.dashoard

import androidx.lifecycle.viewModelScope
import com.xcvi.micros.data.food.model.entity.Portion
import com.xcvi.micros.data.food.FoodRepository
import com.xcvi.micros.data.food.model.MealCard
import com.xcvi.micros.domain.Response
import com.xcvi.micros.domain.getToday
import com.xcvi.micros.domain.nextAmount
import com.xcvi.micros.domain.previousAmount
import com.xcvi.micros.ui.BaseViewModel
import kotlinx.coroutines.launch

class FoodViewModel(
    private val repository: FoodRepository
) : BaseViewModel<FoodViewModel.State>(State()) {

    data class State(
        val currentDate: Int = getToday(),
        val totalSummary: Portion? = null,
        val deletePortion: Portion? = null,
        val meals: List<MealCard> = (1..6).map { MealCard(it, Portion(), emptyList()) }
    )


    fun getData() {
        viewModelScope.launch {
            when (val res = repository.getFoodData(state.currentDate)) {
                is Response.Success -> updateData {
                    copy(
                        meals = res.data.second,
                        totalSummary = res.data.first,
                    )
                }

                is Response.Error -> {}
            }
        }
    }


    fun setDate(date: Int) {
        updateData { copy(currentDate = date) }
        getData()
    }

    fun increasePortion(portion: Portion, onError: () -> Unit) {
        viewModelScope.launch {
            val res = repository.updatePortion(portion, portion.amountInGrams.nextAmount())
            if (res.isError) {
                onError()
            } else {
                getData()
            }
        }
    }

    fun decreasePortion(portion: Portion, onError: () -> Unit, onDelete: () -> Unit) {
        viewModelScope.launch {
            val newAmount = portion.amountInGrams.previousAmount()
            if (newAmount <= 0) {
                setDeletePortion(portion)
                onDelete()
                return@launch
            }
            val res = repository.updatePortion(portion, newAmount)
            if (res.isError) {
                onError()
            } else {
                getData()
            }
        }
    }

    fun setDeletePortion(portion: Portion) = updateData {
        copy(deletePortion = portion)
    }

    fun delete(onError: () -> Unit) {
        viewModelScope.launch {
            val portion = state.deletePortion ?: return@launch
            val res = repository.delete(portion)
            if (res.isError) {
                onError()
            } else {
                updateData {
                    copy(deletePortion = null)
                }
                getData()
            }
        }
    }

    fun saveCustomMeal(
        name: String,
        portions: List<Portion>,
        onError: () -> Unit,
        onSuccess: () -> Unit
    ) {
        if (name.isBlank() || portions.isEmpty()) {
            onError()
            return
        }
        viewModelScope.launch {
            val res = repository.updatePortion(portions, name)
            if (res.isError) {
                onError()
            } else {
                onSuccess()
            }
        }
    }
}