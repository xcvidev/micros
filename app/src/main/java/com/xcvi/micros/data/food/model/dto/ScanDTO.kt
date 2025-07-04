package com.xcvi.micros.data.food.model.dto

import com.xcvi.micros.data.food.model.dto.ProductDTO
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScanDTO(
    val product: ProductDTO? = null,
    val code: String = "",
    val status: String = "",
)


@Serializable
data class SearchDTO(
    val count: Int = 0,
    val page: Int = 0,
    @SerialName("page_count")
    val pageCount: Int = 0,
    @SerialName("page_size")
    val pageSize: Int = 0,
    val products: List<ProductDTO> = emptyList()
)

