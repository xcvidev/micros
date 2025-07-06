package com.xcvi.micros.data.food.model.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import com.xcvi.micros.domain.toAscii
import kotlinx.serialization.Serializable

@Serializable
@Entity(
    tableName = "portions",
    primaryKeys = ["date", "meal", "barcode"],
    indices = [Index("tag")]
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

    val tag: String = "",
    val tagwordcount: Int = 0,

    @Embedded(prefix = "macro_")
    val macros: Macros = Macros(),

    @Embedded(prefix = "mineral_")
    val minerals: Minerals = Minerals(),

    @Embedded(prefix = "vitamin_")
    val vitaminsFull: VitaminsFull = VitaminsFull(),

    @Embedded(prefix = "amino_")
    val aminoAcids: AminoAcids = AminoAcids()
)


fun Portion.tag(): Portion {
    val tag = this.name.toAscii() + " " + this.brand.toAscii()
    val count = tag.split("\\s+".toRegex()).filter { it.isNotEmpty() }.size
    return this.copy(tag = tag, tagwordcount = count)
}
fun List<Portion>.tag(): List<Portion> {
    return this.map { it.tag() }
}

fun Portion.displayName(): String {
    if (this.brand.isBlank()) {
        return this.name
    }
    return "${this.name} (${this.brand})"
}


