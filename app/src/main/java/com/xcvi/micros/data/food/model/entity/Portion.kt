package com.xcvi.micros.data.food.model.entity

import androidx.room.Embedded
import androidx.room.Entity
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "portions",
    primaryKeys = ["date", "meal", "barcode"]
)
data class Portion(
    val date: Int = 0,
    val meal: Int = 0,

    val name: String = "",
    val brand: String = "",
    val barcode: String = "",
    val novaGroup: Double = 1.0,
    val isFavorite: Int = 0,
    val amountInGrams: Double = 100.0,
    val ingredients: String = "",

    @Embedded(prefix = "macro_")
    val macros: Macros = Macros(),

    @Embedded(prefix = "mineral_")
    val minerals: Minerals = Minerals(),

    @Embedded(prefix = "vitamin_")
    val vitamins: Vitamins = Vitamins(),

    @Embedded(prefix = "amino_")
    val aminoAcids: AminoAcids = AminoAcids()
)
