package com.xcvi.micros.data.food.model.dto

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductDTO(
    @SerialName("id")
    val barcode: String = "",
    @SerialName("product_name")
    val name: String = "",
    val brands: String = "",
    @SerialName("ingredients_text_en")
    val ingredientsText: String = "",
    @SerialName("nova_group")
    val novaGroup: Int = 1,

    val ingredients: List<IngredientDTO> = emptyList(),
    val nutriments: NutrimentsDTO = NutrimentsDTO(),
    val nutriments_estimated: NutrimentsEstimatedDTO = NutrimentsEstimatedDTO(),
)


suspend fun ProductDTO.getIngredients(): List<IngredientDTO> {
    return withContext(Dispatchers.Default) {
        buildList {
            addAll(this@getIngredients.ingredients)
            this@getIngredients.ingredients.forEach { addAll(it.subIngredients) }
        }
    }
}