package com.xcvi.micros.data.food.model.entity

import android.content.Context
import com.xcvi.micros.R
import com.xcvi.micros.domain.roundDecimals
import kotlinx.serialization.Serializable

@Serializable
data class Vitamins(
    var vitaminA: Double = 0.0,
    var vitaminB1: Double = 0.0,
    var vitaminB2: Double = 0.0,
    var vitaminB3: Double = 0.0,
    var vitaminB4: Double = 0.0,
    var vitaminB5: Double = 0.0,
    var vitaminB6: Double = 0.0,
    var vitaminB9: Double = 0.0,
    var vitaminB12: Double = 0.0,
    var vitaminC: Double = 0.0,
    var vitaminD: Double = 0.0,
    var vitaminE: Double = 0.0,
    var vitaminK: Double = 0.0
)
fun Vitamins.toLabeledPairs(context: Context): List<Pair<String, String>> {
    return listOf(
        context.getString(R.string.vitaminA) to "${vitaminA} µg",
        context.getString(R.string.vitaminB1) to "${vitaminB1} mg",
        context.getString(R.string.vitaminB2) to "${vitaminB2} mg",
        context.getString(R.string.vitaminB3) to "${vitaminB3} mg",
        context.getString(R.string.vitaminB4) to "${vitaminB4} mg",
        context.getString(R.string.vitaminB5) to "${vitaminB5} mg",
        context.getString(R.string.vitaminB6) to "${vitaminB6} mg",
        context.getString(R.string.vitaminB9) to "${vitaminB9} µg",
        context.getString(R.string.vitaminB12) to "${vitaminB12} µg",
        context.getString(R.string.vitaminC) to "${vitaminC} mg",
        context.getString(R.string.vitaminD) to "${vitaminD} µg",
        context.getString(R.string.vitaminE) to "${vitaminE} mg",
        context.getString(R.string.vitaminK) to "${vitaminK} µg"
    )
}

fun Vitamins.roundDecimals(): Vitamins {
    return copy(
        vitaminA = vitaminA.roundDecimals(),
        vitaminB1 = vitaminB1.roundDecimals(),
        vitaminB2 = vitaminB2.roundDecimals(),
        vitaminB3 = vitaminB3.roundDecimals(),
        vitaminB4 = vitaminB4.roundDecimals(),
        vitaminB5 = vitaminB5.roundDecimals(),
        vitaminB6 = vitaminB6.roundDecimals(),
        vitaminB9 = vitaminB9.roundDecimals(),
        vitaminB12 = vitaminB12.roundDecimals(),
        vitaminC = vitaminC.roundDecimals(),
        vitaminD = vitaminD.roundDecimals(),
        vitaminE = vitaminE.roundDecimals(),
        vitaminK = vitaminK.roundDecimals()
    )
}

/*
"vitamins": {
    "vitaminA": 0.0,     // micrograms (µg) of retinol activity equivalents (RAE)
    "vitaminB1": 0.0,    // milligrams (mg) (thiamine)
    "vitaminB2": 0.0,    // milligrams (mg) (riboflavin)
    "vitaminB3": 0.0,    // milligrams (mg) (niacin)
    "vitaminB4": 0.0,    // milligrams (mg) (choline) — sometimes reported as vitamin B4
    "vitaminB5": 0.0,    // milligrams (mg) (pantothenic acid)
    "vitaminB6": 0.0,    // milligrams (mg) (pyridoxine)
    "vitaminB9": 0.0,    // micrograms (µg) (folate)
    "vitaminB12": 0.0,   // micrograms (µg) (cobalamin)
    "vitaminC": 0.0,     // milligrams (mg) (ascorbic acid)
    "vitaminD": 0.0,     // micrograms (µg)
    "vitaminE": 0.0,     // milligrams (mg)
    "vitaminK": 0.0      // micrograms (µg)
}
*/