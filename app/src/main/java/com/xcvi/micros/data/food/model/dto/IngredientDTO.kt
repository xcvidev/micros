package com.xcvi.micros.data.food.model.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IngredientDTO(
    @SerialName("id")
    val id: String = "",
    @SerialName("percent_estimate")
    val percentage: Double = 0.0,
    @SerialName("ingredients")
    val subIngredients: List<IngredientDTO> = emptyList(),
)