package com.xcvi.micros.data.food.model.entity

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
    var salt: Double = 0.0,

    /**
     * Temporarily added
     */
    var potassium: Double = 0.0,
    var calcium: Double = 0.0,
    var magnesium: Double = 0.0,
    var iron: Double = 0.0,

    var vitaminA: Double = 0.0,
    var vitaminB: Double = 0.0,
    var vitaminC: Double = 0.0,
    var vitaminD: Double = 0.0,
    var vitaminE: Double= 0.0,
    var vitaminK: Double= 0.0,

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
        context.getString(R.string.salt) to "${salt.roundDecimals()} g",
        /**
         *
         */
        context.getString(R.string.potassium) to "${potassium.roundDecimals()} mg",
        context.getString(R.string.calcium) to "${calcium.roundDecimals()} mg",
        context.getString(R.string.magnesium) to "${magnesium.roundDecimals()} mg",
        context.getString(R.string.iron) to "${iron.roundDecimals()} mg",
        context.getString(R.string.vitaminA) to "${vitaminA.roundDecimals()} mg",
        context.getString(R.string.vitaminB) to "${vitaminB.roundDecimals()} mg",
        context.getString(R.string.vitaminC) to "${vitaminC.roundDecimals()} mg",
        context.getString(R.string.vitaminD) to "${vitaminD.roundDecimals()} mg",
        context.getString(R.string.vitaminE) to "${vitaminE.roundDecimals()} mg",
        context.getString(R.string.vitaminK) to "${vitaminK.roundDecimals()} mg",


    )
}

fun Macros.roundDecimals(): Macros {
    return copy(
        calories = calories.roundDecimals(),
        protein = protein.roundDecimals(),
        carbohydrates = carbohydrates.roundDecimals(),
        fats = fats.roundDecimals(),
        saturatedFats = saturatedFats.roundDecimals(),
        fiber = fiber.roundDecimals(),
        sugars = sugars.roundDecimals(),
        salt = salt.roundDecimals()
    )
}
