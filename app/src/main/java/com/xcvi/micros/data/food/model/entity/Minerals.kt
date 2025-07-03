package com.xcvi.micros.data.food.model.entity

import android.content.Context
import com.xcvi.micros.R
import com.xcvi.micros.domain.roundDecimals
import kotlinx.serialization.Serializable

@Serializable
data class Minerals(
    var calcium: Double = 0.0,
    var copper: Double = 0.0,
    var fluoride: Double = 0.0,
    var iron: Double = 0.0,

    var magnesium: Double = 0.0,
    var manganese: Double = 0.0,
    var phosphorus: Double = 0.0,
    var potassium: Double = 0.0,

    var selenium: Double = 0.0,
    var sodium: Double = 0.0,
    var zinc: Double = 0.0
)

fun Minerals.toLabeledPairs(context: Context): List<Pair<String, String>> {
    return listOf(
        context.getString(R.string.calcium) to "$calcium mg",
        context.getString(R.string.copper) to "$copper mg",
        context.getString(R.string.fluoride) to "$fluoride mg",
        context.getString(R.string.iron) to "$iron mg",
        context.getString(R.string.magnesium) to "$magnesium mg",
        context.getString(R.string.manganese) to "$manganese mg",
        context.getString(R.string.phosphorus) to "$phosphorus mg",
        context.getString(R.string.potassium) to "$potassium mg",
        context.getString(R.string.selenium) to "$selenium µg",
        context.getString(R.string.sodium) to "$sodium mg",
        context.getString(R.string.zinc) to "$zinc mg"
    )
}

fun Minerals.roundDecimals(): Minerals {
    return copy(
        calcium = calcium.roundDecimals(),
        copper = copper.roundDecimals(),
        fluoride = fluoride.roundDecimals(),
        iron = iron.roundDecimals(),

        magnesium = magnesium.roundDecimals(),
        manganese = manganese.roundDecimals(),
        phosphorus = phosphorus.roundDecimals(),
        potassium = potassium.roundDecimals(),

        selenium = selenium.roundDecimals(),
        sodium = sodium.roundDecimals(),
        zinc = zinc.roundDecimals()
    )
}

/*
"minerals": {
    "calcium": 0.0,      // milligrams (mg)
    "copper": 0.0,       // milligrams (mg)
    "fluoride": 0.0,     // milligrams (mg)
    "iron": 0.0,         // milligrams (mg)
    "magnesium": 0.0,    // milligrams (mg)
    "manganese": 0.0,    // milligrams (mg)
    "phosphorus": 0.0,   // milligrams (mg)
    "potassium": 0.0,    // milligrams (mg)
    "selenium": 0.0,     // micrograms (µg)
    "sodium": 0.0,       // milligrams (mg)
    "zinc": 0.0          // milligrams (mg)
}
 */