package com.xcvi.micros.data.food.model

import com.xcvi.micros.data.food.model.entity.Portion

data class MealCard(
    val meal: Int,
    val summary: Portion,
    val portions: List<Portion>,
)