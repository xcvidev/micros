package com.xcvi.micros.data.food

import com.xcvi.micros.data.food.model.dto.ProductDTO
import com.xcvi.micros.data.food.model.entity.AminoAcids
import com.xcvi.micros.data.food.model.entity.Macros
import com.xcvi.micros.data.food.model.entity.Minerals
import com.xcvi.micros.data.food.model.entity.Portion
import com.xcvi.micros.data.food.model.entity.VitaminsFull
import com.xcvi.micros.data.food.model.entity.roundDecimals
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
            sugars = macros.sugars * ratio,
            /**
             *
             */
            calcium  = macros.calcium  * ratio,
            iron  = macros.iron  * ratio,
            magnesium  = macros.magnesium  * ratio,
            potassium  = macros.potassium  * ratio,
            vitaminA  = macros.vitaminA  * ratio,
            vitaminB  = macros.vitaminB  * ratio,
            vitaminC  = macros.vitaminC  * ratio,
            vitaminD  = macros.vitaminD  * ratio,
            vitaminE  = macros.vitaminE  * ratio,
            vitaminK  = macros.vitaminK  * ratio,

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
        vitaminsFull = VitaminsFull(
            vitaminA  = vitaminsFull.vitaminA  * ratio,
            vitaminB1  = vitaminsFull.vitaminB1  * ratio,
            vitaminB2  = vitaminsFull.vitaminB2  * ratio,
            vitaminB3  = vitaminsFull.vitaminB3  * ratio,
            vitaminB4  = vitaminsFull.vitaminB4  * ratio,
            vitaminB5  = vitaminsFull.vitaminB5  * ratio,
            vitaminB6  = vitaminsFull.vitaminB6  * ratio,
            vitaminB9  = vitaminsFull.vitaminB9  * ratio,
            vitaminB12  = vitaminsFull.vitaminB12  * ratio,
            vitaminC  = vitaminsFull.vitaminC  * ratio,
            vitaminD  = vitaminsFull.vitaminD  * ratio,
            vitaminE  = vitaminsFull.vitaminE  * ratio,
            vitaminK  = vitaminsFull.vitaminK  * ratio,
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

fun ProductDTO.toPortionCache(): Portion? {
    fun getValue(main: Double, fallback: Double): Double {
        return if (main >= 0) main else fallback
    }

    val n = nutriments
    val e = nutriments_estimated

    val portion = Portion(
        date = -1,
        meal = -1,
        amountInGrams = 100.0,
        isFavorite = 0,

        name = name,
        barcode = barcode,
        brand  = brands,
        novaGroup = novaGroup.toDouble(),
        ingredients = ingredientsText,
        macros = Macros(
            calories = getValue(n.kcal, e.kcal),
            protein = getValue(n.proteins_100g, e.proteins_100g),
            carbohydrates = getValue(n.carbohydrates_100g, e.carbohydrates_100g),
            fats = getValue(n.fat_100g, e.fat_100g),
            saturatedFats = getValue(n.saturated_fat_100g, e.saturated_fat_100g),
            fiber = getValue(n.fiber_100g, e.fiber_100g),
            sugars = getValue(n.sugars_100g, e.sugars_100g),
            salt = getValue(n.salt_100g, e.salt_100g),
            /**
             *
             */

            /*
            calcium = getValue(n.calcium_100g, e.calcium_100g) * 0.001,
            iron = getValue(n.iron_100g, e.iron_100g) * 0.001,
            magnesium = getValue(n.magnesium_100g, e.magnesium_100g) * 0.001,
            potassium = getValue(n.potassium_100g, e.potassium_100g) * 0.001,
            vitaminA = getValue(n.vitaminA, e.vitaminA) * 0.000001,
            vitaminB = getValue(
                n.vitaminB1 + n.vitaminB2 + n.vitaminB3 + n.vitaminB4 + n.vitaminB5 + n.vitaminB6 + n.vitaminB9 + n.vitaminB12,
                e.vitaminB1 + e.vitaminB2 + e.vitaminB3 + e.vitaminB4 + e.vitaminB5 + e.vitaminB6 + e.vitaminB9 + e.vitaminB12
            ) * 0.001,
            vitaminC = getValue(n.vitaminC, e.vitaminC) * 0.001,
            vitaminD = getValue(n.vitaminD, e.vitaminD) * 0.000001,
            vitaminE = getValue(n.vitaminE, e.vitaminE) * 0.001,
            vitaminK = getValue(n.vitaminK, e.vitaminK) * 0.000001
        ),
        minerals = Minerals(
            calcium = getValue(n.calcium_100g, e.calcium_100g),
            iron  = getValue(n.iron_100g, e.iron_100g),
            magnesium  = getValue(n.magnesium_100g, e.magnesium_100g),
            phosphorus  = getValue(n.phosphorus_100g, e.phosphorus_100g),
            potassium  = getValue(n.potassium_100g, e.potassium_100g),
            sodium  = getValue(n.sodium_100g, e.sodium_100g),
            zinc  = getValue(n.zinc_100g, e.zinc_100g),
            copper  = getValue(n.copper_100g, e.copper_100g),
            manganese  = getValue(n.manganese_100g, e.manganese_100g),
            selenium  = getValue(n.selenium_100g, e.selenium_100g),
            fluoride  = getValue(n.fluoride, e.fluoride)

        ),
        vitaminsFull = VitaminsFull(
            vitaminA  = getValue(n.vitaminA, e.vitaminA),
            vitaminB1  = getValue(n.vitaminB1, e.vitaminB1),
            vitaminB2  = getValue(n.vitaminB2, e.vitaminB2),
            vitaminB3  = getValue(n.vitaminB3, e.vitaminB3),
            vitaminB5  = getValue(n.vitaminB5, e.vitaminB5),
            vitaminB6  = getValue(n.vitaminB6, e.vitaminB6),
            vitaminB9  = getValue(n.vitaminB9, e.vitaminB9),
            vitaminB12  = getValue(n.vitaminB12, e.vitaminB12),
            vitaminC  = getValue(n.vitaminC, e.vitaminC),
            vitaminD  = getValue(n.vitaminD, e.vitaminD),
            vitaminE  = getValue(n.vitaminE, e.vitaminE),
            vitaminK  = getValue(n.vitaminK, e.vitaminK)
        ),
        aminoAcids = AminoAcids() // optional; fill if available

             */
        )
    )

    return if(portion.macros.isEmpty()) {
        null
    } else {
        portion
    }
}




fun List<Macros>.sum(): Macros {
    val total = Macros()
    forEach {
        total.calories += it.calories
        total.protein += it.protein
        total.carbohydrates += it.carbohydrates
        total.fats += it.fats
        total.saturatedFats += it.saturatedFats
        total.fiber += it.fiber
        total.sugars += it.sugars
        total.salt += it.salt
        /**
         *
         */
        total.calcium += it.calcium
        total.iron += it.iron
        total.magnesium += it.magnesium
        total.potassium += it.potassium
        total.vitaminA += it.vitaminA
        total.vitaminB += it.vitaminB
        total.vitaminC += it.vitaminC
        total.vitaminD += it.vitaminD
        total.vitaminE += it.vitaminE
        total.vitaminK += it.vitaminK
    }
    return total
}



fun List<Portion>.summary(date: Int = 0): Portion {
    val summary =  this.fold(
        Portion(
            date = date,
            macros = Macros(
                calories = 0.0,
                protein = 0.0,
                carbohydrates = 0.0,
                fats = 0.0,
            ),
            minerals = Minerals(),
            vitaminsFull = VitaminsFull(),
            aminoAcids = AminoAcids()
        )
    ) { acc, portion ->
        acc.apply {
            macros.apply {
                calories += portion.macros.calories
                protein += portion.macros.protein
                carbohydrates += portion.macros.carbohydrates
                fats += portion.macros.fats
                saturatedFats += portion.macros.saturatedFats
                sugars += portion.macros.sugars
                salt += portion.macros.salt
                fiber += portion.macros.fiber
                /**
                 *
                 */
                calcium += portion.macros.calcium
                iron += portion.macros.iron
                magnesium += portion.macros.magnesium
                potassium += portion.macros.potassium
                vitaminA += portion.macros.vitaminA
                vitaminB += portion.macros.vitaminB
                vitaminC += portion.macros.vitaminC
                vitaminD += portion.macros.vitaminD
                vitaminE += portion.macros.vitaminE
                vitaminK += portion.macros.vitaminK
            }
            minerals.apply {
                calcium += portion.minerals.calcium
                copper += portion.minerals.copper
                iron += portion.minerals.iron
                fluoride += portion.minerals.fluoride
                magnesium += portion.minerals.magnesium
                manganese += portion.minerals.manganese
                phosphorus += portion.minerals.phosphorus
                potassium += portion.minerals.potassium
                selenium += portion.minerals.selenium
                sodium += portion.minerals.sodium
                zinc += portion.minerals.zinc
            }
            vitaminsFull.apply {
                vitaminA += portion.vitaminsFull.vitaminA
                vitaminB1 += portion.vitaminsFull.vitaminB1
                vitaminB2 += portion.vitaminsFull.vitaminB2
                vitaminB3 += portion.vitaminsFull.vitaminB3
                vitaminB4 += portion.vitaminsFull.vitaminB4
                vitaminB5 += portion.vitaminsFull.vitaminB5
                vitaminB6 += portion.vitaminsFull.vitaminB6
                vitaminB9 += portion.vitaminsFull.vitaminB9
                vitaminB12 += portion.vitaminsFull.vitaminB12
                vitaminC += portion.vitaminsFull.vitaminC
                vitaminD += portion.vitaminsFull.vitaminD
                vitaminE += portion.vitaminsFull.vitaminE
                vitaminK += portion.vitaminsFull.vitaminK
            }
            aminoAcids.apply {
                alanine += portion.aminoAcids.alanine
                arginine += portion.aminoAcids.arginine
                asparticAcid += portion.aminoAcids.asparticAcid
                asparagine += portion.aminoAcids.asparagine
                cystine += portion.aminoAcids.cystine

                glutamicAcid += portion.aminoAcids.glutamicAcid
                glutamine += portion.aminoAcids.glutamine
                glycine += portion.aminoAcids.glycine
                histidine += portion.aminoAcids.histidine
                isoleucine += portion.aminoAcids.isoleucine

                leucine += portion.aminoAcids.leucine
                lysine += portion.aminoAcids.lysine
                methionine += portion.aminoAcids.methionine
                phenylalanine += portion.aminoAcids.phenylalanine
                proline += portion.aminoAcids.proline

                serine += portion.aminoAcids.serine
                threonine += portion.aminoAcids.threonine
                tryptophan += portion.aminoAcids.tryptophan
                tyrosine += portion.aminoAcids.tyrosine
                valine += portion.aminoAcids.valine
            }
        }
    }
    return summary
}

fun Portion.roundDecimals(): Portion {
    return copy(
        macros = macros.roundDecimals(),
        minerals = minerals.roundDecimals(),
        vitaminsFull = vitaminsFull.roundDecimals(),
        aminoAcids = aminoAcids.roundDecimals()
    )
}

fun List<Portion>.avg(date: Int): Portion {
    val summary = this.summary(date)
    return summary.copy(
        date = date,
        macros = summary.macros.copy(
            calories = summary.macros.calories / this.size,
            protein = summary.macros.protein / this.size,
            carbohydrates = summary.macros.carbohydrates / this.size,
            fats = summary.macros.fats / this.size
        )
    )
}



fun List<Minerals>.sum(): Minerals {
    val total = Minerals()
    forEach {
        total.calcium += it.calcium
        total.copper += it.copper
        total.fluoride += it.fluoride
        total.iron += it.iron
        total.magnesium += it.magnesium
        total.manganese += it.manganese
        total.phosphorus += it.phosphorus
        total.potassium += it.potassium
        total.selenium += it.selenium
        total.sodium += it.sodium
        total.zinc += it.zinc
    }
    return total
}

fun List<VitaminsFull>.sum(): VitaminsFull {
    val total = VitaminsFull()
    forEach {
        total.vitaminA += it.vitaminA
        total.vitaminB1 += it.vitaminB1
        total.vitaminB2 += it.vitaminB2
        total.vitaminB3 += it.vitaminB3
        total.vitaminB4 += it.vitaminB4
        total.vitaminB5 += it.vitaminB5
        total.vitaminB6 += it.vitaminB6
        total.vitaminB9 += it.vitaminB9
        total.vitaminB12 += it.vitaminB12
        total.vitaminC += it.vitaminC
        total.vitaminD += it.vitaminD
        total.vitaminE += it.vitaminE
        total.vitaminK += it.vitaminK
    }
    return total
}
fun List<AminoAcids>.sum(): AminoAcids {
    val total = AminoAcids()
    forEach {
        total.alanine += it.alanine
        total.arginine += it.arginine
        total.asparagine += it.asparagine
        total.asparticAcid += it.asparticAcid
        total.cystine += it.cystine
        total.glutamicAcid += it.glutamicAcid
        total.glutamine += it.glutamine
        total.glycine += it.glycine
        total.histidine += it.histidine
        total.isoleucine += it.isoleucine
        total.leucine += it.leucine
        total.lysine += it.lysine
        total.methionine += it.methionine
        total.phenylalanine += it.phenylalanine
        total.proline += it.proline
        total.serine += it.serine
        total.threonine += it.threonine
        total.tryptophan += it.tryptophan
        total.tyrosine += it.tyrosine
        total.valine += it.valine
    }
    return total
}