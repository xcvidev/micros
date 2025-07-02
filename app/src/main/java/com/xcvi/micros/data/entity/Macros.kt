package com.xcvi.micros.data.entity

import android.content.Context
import com.xcvi.micros.R
import com.xcvi.micros.domain.roundDecimals
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

@Serializable
data class Macros(
    var calories: Double = 0.0,
    var protein: Double = 0.0,
    var carbohydrates: Double = 0.0,
    var fats: Double = 0.0,
    var saturatedFats: Double = 0.0,
    var fiber: Double = 0.0,
    var sugars: Double = 0.0,
    var salt: Double = 0.0
) {
    fun isEmpty(): Boolean {
        return calories == 0.0 || (protein == 0.0 && carbohydrates == 0.0 && fats == 0.0)
    }
}

fun Macros.toLabeledPairs(context: Context): List<Pair<String, String>> {
    return listOf(
        context.getString(R.string.calories) to "${calories.roundToInt()} kcal",
        context.getString(R.string.protein) to "${protein.roundDecimals()} g",
        context.getString(R.string.carbs) to "${carbohydrates.roundDecimals()} g",
        context.getString(R.string.fats) to "${fats.roundDecimals()} g",
        context.getString(R.string.saturated_fats) to "${saturatedFats.roundDecimals()} g",
        context.getString(R.string.fiber) to "${fiber.roundDecimals()} g",
        context.getString(R.string.sugars) to "${sugars.roundDecimals()} g",
        context.getString(R.string.salt) to "${salt.roundDecimals()} g"
    )
}

