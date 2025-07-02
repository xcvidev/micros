package com.xcvi.micros.domain

import com.xcvi.micros.data.entity.AminoAcids
import com.xcvi.micros.data.entity.Macros
import com.xcvi.micros.data.entity.Minerals
import com.xcvi.micros.data.entity.Portion
import com.xcvi.micros.data.entity.Vitamins
import kotlin.random.Random

class FoodRepository {

    val portions = (0..100).map {
        val foodName = if(it % 2 == 0) "Food $it" else "Food $it very long food name that goes outside card"
        val protein = Random.nextDouble(30.0, 50.0).roundDecimals()
        val carbs = Random.nextDouble(100.0, 200.0).roundDecimals()
        val fats = Random.nextDouble(5.0,10.0).roundDecimals()
        val calories = protein * 4 + carbs * 4 + fats * 9
        Portion(
            date = getToday() - it,
            meal = it % 6 + 1,
            barcode = "$it",
            name = foodName,
            brand = "",
            amountInGrams = if(it % 2 == 0) 100.0+it else it+50.0,
            macros = Macros(
                calories = calories,
                protein = protein,
                carbohydrates = carbs,
                fats = fats,
                saturatedFats = 0.0,
                sugars = 0.0,
                salt = 0.0,
                fiber = 0.0
            ),
            minerals = Minerals(),
            vitamins = Vitamins(),
            aminoAcids = AminoAcids()
        )
    }.toMutableList()



    fun getSummary(date: Int): Portion {
        return portions.filter { it.date == date }.summary()
    }
    fun saveCustomMeal(name: String, portions: List<Portion>) = Unit

    fun generate(date: Int, meal: Int, description: String): Portion {
        return portions.random()
    }

    fun getPortion(meal: Int, date: Int, barcode: String): Portion {
        return portions.random()
    }

    fun getPortions(date: Int, meal: Int): List<Portion> {
        return portions.filter { it.date == date && it.meal == meal }.sortedBy { it.name }
    }
    fun updatePortion(portion: Portion, newAmount: Double){

    }
}





fun List<Portion>.summary(date: Int = 0): Portion {
    val summary =  this.fold(Portion(
        date = date,
        macros = Macros(
            calories = 0.0,
            protein = 0.0,
            carbohydrates = 0.0,
            fats = 0.0,
        ),
        minerals = Minerals(),
        vitamins = Vitamins(),
        aminoAcids = AminoAcids()
    )) { acc, portion ->
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
            vitamins.apply {
                vitaminA += portion.vitamins.vitaminA
                vitaminB1 += portion.vitamins.vitaminB1
                vitaminB2 += portion.vitamins.vitaminB2
                vitaminB3 += portion.vitamins.vitaminB3
                vitaminB4 += portion.vitamins.vitaminB4
                vitaminB5 += portion.vitamins.vitaminB5
                vitaminB6 += portion.vitamins.vitaminB6
                vitaminB9 += portion.vitamins.vitaminB9
                vitaminB12 += portion.vitamins.vitaminB12
                vitaminC += portion.vitamins.vitaminC
                vitaminD += portion.vitamins.vitaminD
                vitaminE += portion.vitamins.vitaminE
                vitaminK += portion.vitamins.vitaminK
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
