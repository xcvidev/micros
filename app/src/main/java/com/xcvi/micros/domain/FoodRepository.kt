package com.xcvi.micros.domain

import com.xcvi.micros.ui.core.getToday

class FoodRepository {

    val portions = (0..100).map {
        Portion(
            date = getToday() - (it % 7),
            meal = it % 6 + 1,
            barcode = "$it",
            name = "Food $it",
            brand = "",
            calories = if(it % 2 == 0) 100.0+it else 0.0,
            amount = if(it % 2 == 0) 100.0 else it+50.0,
            macros = Macros(
                protein = if(it % 2 == 0) 120.0 else 50.0,
                carbs = if(it % 2 == 0) 20.0 else 50.0,
                fats = if(it % 2 == 0) 144.0 else 50.0,
                saturatedFats = if(it % 2 == 0) 100.0 else 50.0,
                sugars =if(it % 2 == 0) 110.0 else 50.0,
                salt = if(it % 2 == 0) 16.0 else 50.0,
                fiber = if(it % 2 == 0) 109.0 else 50.0
            ),
            minerals = Minerals(),
            vitamins = Vitamins(),
            aminoAcids = AminoAcids()
        )
    }.toMutableList()

    fun getSummary(date: Int): Portion {
        return portions.filter { it.date == date }.summary(date)
    }
    fun saveCustomMeal(name: String, portions: List<Portion>) = Unit

    fun generate(date: Int, meal: Int, description: String): Portion?{
        return portions.find { it.date == date && it.meal == meal }?.copy(name = description, date = date, meal = meal)
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

fun List<Portion>.summary(date: Int): Portion {
    return this.fold(Portion(
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
            amount += portion.amount
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
}
