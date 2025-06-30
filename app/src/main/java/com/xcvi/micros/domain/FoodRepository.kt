package com.xcvi.micros.domain

import kotlin.random.Random

class FoodRepository {

    val portions = (0..100).map {
        val foodName = if(it % 2 == 0) "Food $it" else "Food $it very long food name that goes outside card"
        val protein = Random.nextDouble(130.0, 180.0).roundDecimals()
        val carbs = if(it % 4 == 0) 200.0 else 500.0
        val fats = if(it % 2 == 0) 20.0 else 100.0
        val calories = protein * 4 + carbs * 4 + fats * 9
        Portion(
            date = getToday() - it,
            meal = it % 6 + 1,
            barcode = "$it",
            name = foodName,
            brand = "",
            calories = calories,
            amount = if(it % 2 == 0) 100.0 else it+50.0,
            macros = Macros(
                protein = protein,
                carbs = carbs,
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

    fun generate(date: Int, meal: Int, description: String): Portion?{
        return Portion(
            date = date,
            meal = meal,
            barcode = "",
            name = description,
            brand = "",
            calories = 0.0,
            amount = 0.0,
            macros = Macros(
                protein = 30.0,
                carbs = 20.0,
                fats = 50.0,
                saturatedFats = 0.0,
                sugars = 0.0,
                salt = 0.0,
                fiber = 0.0
            ),
            minerals = Minerals(),
            vitamins = Vitamins(),
            aminoAcids = AminoAcids()
        )
    }

    fun getPortion(meal: Int, date: Int, barcode: String): Portion? {
        return portions.find { it.date == date && it.meal == meal && it.barcode == barcode }
    }

    fun getPortions(date: Int, meal: Int): List<Portion> {
        return portions.filter { it.date == date && it.meal == meal }.sortedBy { it.name }
    }
    fun updatePortion(portion: Portion, newAmount: Double){
        if(newAmount == 0.0) {
            portions.remove(portion)
            return
        }
        portions.remove(portion)
        portions.add(portion.copy(amount = newAmount, calories = portion.calories * newAmount / portion.amount))
    }
}


data class Portion(
    var  date: Int,
    var  meal: Int,
    var  barcode: String,
    var  name: String,
    var  brand: String,
    var  calories: Double,
    var  amount: Double,
    var  macros: Macros,
    var  minerals: Minerals,
    var  vitamins: Vitamins,
    var  aminoAcids: AminoAcids
)

data class Macros(
    var  protein: Double,
    var  carbs: Double,
    var  fats: Double,
    var  saturatedFats: Double,
    var  sugars: Double,
    var  salt: Double,
    var  fiber: Double
)

class Minerals
class Vitamins
class AminoAcids

fun List<Portion>.summary(date: Int = 0): Portion {
    val summary =  this.fold(Portion(
        date = date,
        meal = 0,
        barcode = "",
        name = "",
        brand = "",
        calories = 0.0,
        amount = 0.0,
        macros = Macros(
            protein = 0.0,
            carbs = 0.0,
            fats = 0.0,
            saturatedFats = 0.0,
            sugars = 0.0,
            salt = 0.0,
            fiber = 0.0
        ),
        minerals = Minerals(),
        vitamins = Vitamins(),
        aminoAcids = AminoAcids()
    )) { acc, portion ->
        acc.apply {
            calories += portion.calories
            macros.apply {
                protein += portion.macros.protein
                carbs += portion.macros.carbs
                fats += portion.macros.fats
                saturatedFats += portion.macros.saturatedFats
                sugars += portion.macros.sugars
                salt += portion.macros.salt
                fiber += portion.macros.fiber
            }
        }
    }
    return summary
}

fun List<Portion>.avg(date: Int): Portion {
    val summary = this.summary(date)
    return summary.copy(
        date = date,
        calories = summary.calories / this.size,
        macros = summary.macros.copy(
            protein = summary.macros.protein / this.size,
            carbs = summary.macros.carbs / this.size,
            fats = summary.macros.fats / this.size
        )
    )
}
