package com.xcvi.micros.data

import com.xcvi.micros.data.entity.AminoAcids
import com.xcvi.micros.data.entity.Macros
import com.xcvi.micros.data.entity.Minerals
import com.xcvi.micros.data.entity.Portion
import com.xcvi.micros.data.entity.Vitamins
import kotlin.math.roundToInt

fun Portion.scaledCalories(newAmount: Int): Int {
    if (newAmount.toDouble() == amountInGrams) return this.macros.calories.roundToInt()
    val ratio = newAmount / amountInGrams
    val newCalories = macros.calories * ratio
    return newCalories.roundToInt()
}


fun Portion.scaledTo(newAmount: Double): Portion {
    if (newAmount == amountInGrams) return this

    val ratio = newAmount / amountInGrams

    return copy(
        amountInGrams = newAmount,
        macros = Macros(
            calories = (macros.calories * ratio),
            protein = macros.protein * ratio,
            carbohydrates = macros.carbohydrates * ratio,
            fats = macros.fats * ratio,
            saturatedFats = macros.saturatedFats * ratio,
            fiber = macros.fiber * ratio,
            sugars = macros.sugars * ratio
        ),
        minerals = Minerals(
            calcium  = minerals.calcium  * ratio,
            copper  = minerals.copper  * ratio,
            iron  = minerals.iron  * ratio,
            magnesium  = minerals.magnesium  * ratio,
            manganese  = minerals.manganese  * ratio,
            phosphorus  = minerals.phosphorus  * ratio,
            potassium  = minerals.potassium  * ratio,
            selenium  = minerals.selenium  * ratio,
            sodium  = minerals.sodium  * ratio,
            zinc  = minerals.zinc  * ratio
        ),
        vitamins = Vitamins(
            vitaminA  = vitamins.vitaminA  * ratio,
            vitaminB1  = vitamins.vitaminB1  * ratio,
            vitaminB2  = vitamins.vitaminB2  * ratio,
            vitaminB3  = vitamins.vitaminB3  * ratio,
            vitaminB4  = vitamins.vitaminB4  * ratio,
            vitaminB5  = vitamins.vitaminB5  * ratio,
            vitaminB6  = vitamins.vitaminB6  * ratio,
            vitaminB9  = vitamins.vitaminB9  * ratio,
            vitaminB12  = vitamins.vitaminB12  * ratio,
            vitaminC  = vitamins.vitaminC  * ratio,
            vitaminD  = vitamins.vitaminD  * ratio,
            vitaminE  = vitamins.vitaminE  * ratio,
            vitaminK  = vitamins.vitaminK  * ratio,
        ),
        aminoAcids = AminoAcids(
            alanine = aminoAcids.alanine * ratio,
            arginine = aminoAcids.arginine * ratio,
            asparticAcid = aminoAcids.asparticAcid * ratio,
            asparagine = aminoAcids.asparagine * ratio,
            cystine = aminoAcids.cystine * ratio,
            glutamicAcid = aminoAcids.glutamicAcid * ratio,
            glutamine = aminoAcids.glutamine * ratio,
            glycine = aminoAcids.glycine * ratio,
            histidine = aminoAcids.histidine * ratio,
            isoleucine = aminoAcids.isoleucine * ratio,
            leucine = aminoAcids.leucine * ratio,
            lysine = aminoAcids.lysine * ratio,
            methionine = aminoAcids.methionine * ratio,
            phenylalanine = aminoAcids.phenylalanine * ratio,
            proline = aminoAcids.proline * ratio,
            serine = aminoAcids.serine * ratio,
            threonine = aminoAcids.threonine * ratio,
            tryptophan = aminoAcids.tryptophan * ratio,
            tyrosine = aminoAcids.tyrosine * ratio,
            valine = aminoAcids.valine * ratio
        )
    )
}

