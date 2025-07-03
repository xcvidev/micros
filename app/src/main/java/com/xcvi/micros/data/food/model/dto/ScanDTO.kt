package com.xcvi.micros.data.food.model.dto

import com.xcvi.micros.data.food.model.dto.ProductDTO
import kotlinx.serialization.Serializable

@Serializable
data class ScanDTO(
    val product: ProductDTO? = null,
    val code: String = "",
    val status: String = "",
)

