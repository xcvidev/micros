package com.xcvi.micros.data.food.source

import com.xcvi.micros.data.food.model.entity.Portion
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Vitamins(
    val vitaminA: Double = 0.0, // mcg
    val vitaminB: Double = 0.0, // mg
    val vitaminC: Double = 0.0, // mg
    val vitaminD: Double = 0.0, // mcg
    val vitaminE: Double = 0.0, // mg
    val vitaminK: Double = 0.0  // mcg
){
    fun isEmpty(): Boolean {
        return vitaminA == 0.0 && vitaminB == 0.0 && vitaminC == 0.0 && vitaminD == 0.0 && vitaminE == 0.0 && vitaminK == 0.0
    }
}

fun getEnhancementPrompt(portion: Portion, userDesc: String): String {
    return buildString {
        appendLine("You are a nutrition expert assistant.")
        appendLine()
        appendLine("Estimate the amount of each vitamin per 100 grams of a food product.")
        appendLine("Use the user-provided description as the most accurate and reliable source.")
        appendLine("Only use the product name or ingredients for additional context if needed.")
        appendLine()
        appendLine("Return the result in **this exact JSON format** (and nothing else):")
        appendLine("""
            {
              "vitaminA": [value in mcg],
              "vitaminB": [value in mg],
              "vitaminC": [value in mg],
              "vitaminD": [value in mcg],
              "vitaminE": [value in mg],
              "vitaminK": [value in mcg]
            }
        """.trimIndent())
        appendLine()
        appendLine("User description: \"$userDesc\"")
        appendLine("Product name: \"${portion.name}\"")
        if (portion.ingredients.isNotBlank()) {
            appendLine("Ingredients (if helpful): \"${portion.ingredients}\"")
        }
        appendLine()
        appendLine("Round all values to 1 decimal place.")
        appendLine("Output only the JSON object.")
    }.trim()
}



fun getEstimatePrompt(userInput: String): String {
    val prompt = """
        You are a nutrition assistant.

        Given a food description, return a **single JSON object** matching the structure below:

        {
            "name": "",
            "brand": "",
            "amountInGrams": 0.0,
            "ingredients": "",
            "macros": {
                "calories": 0.0,
                "protein": 0.0,
                "carbohydrates": 0.0,
                "fats": 0.0,
                "saturatedFats": 0.0,
                "fiber": 0.0,
                "sugars": 0.0,
                "salt": 0.0,
                "potassium": 0.0, // milligrams (mg)
                "calcium": 0.0,  // milligrams (mg)
                "magnesium": 0.0,  // milligrams (mg)
                "iron": 0.0,  // milligrams (mg)
                "vitaminA": 0.0, // micrograms (µg)
                "vitaminB": 0.0, // milligrams (mg)
                "vitaminC": 0.0, // milligrams (mg)
                "vitaminD": 0.0, // micrograms (µg)
                "vitaminE": 0.0  // milligrams (mg)
                "vitaminK": 0.0  // micrograms (µg)
            }
        }

        - If a food is vaguely described (e.g., “a plate of pasta”), assume a common recipe and average portion size.
        - If ingredients or brands are not specified, assume a standard version (e.g., plain cooked pasta, white rice, grilled chicken).
        - Estimate total weight and nutritional values accordingly.
        - If the description includes multiple foods, combine them into a single JSON object by summing their values.
        - If the description contains amounts like "100g of pasta and 100g of broccoli", combine the amounts into a single JSON that has amountInGrams = 200.
        - Use 0 or an empty string for unknown values.
        - Return **only** a valid JSON object. Do not include any explanations or extra text.

        Food description:
        "$userInput"
    """.trimIndent()

    return prompt
}

private fun getFullEstimatePrompt(userInput: String): String {
    val prompt = """
        You are a nutrition assistant.

        Given a food description, return a **single JSON object** matching the structure below:

        {
            "name": "",
            "brand": "",
            "amountInGrams": 0.0,
            "ingredients": "",
            "macros": {
                "calories": 0.0,
                "protein": 0.0,
                "carbohydrates": 0.0,
                "fats": 0.0,
                "saturatedFats": 0.0,
                "fiber": 0.0,
                "sugars": 0.0
            },
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
            "aminoAcids": {
                // all values in grams
                "alanine": 0.0,
                "arginine": 0.0,
                "asparticAcid": 0.0,
                "cystine": 0.0,
                "glutamicAcid": 0.0,
                "glycine": 0.0,
                "histidine": 0.0,
                "isoleucine": 0.0,
                "leucine": 0.0,
                "lysine": 0.0,
                "methionine": 0.0,
                "phenylalanine": 0.0,
                "proline": 0.0,
                "serine": 0.0,
                "threonine": 0.0,
                "tryptophan": 0.0,
                "tyrosine": 0.0,
                "valine": 0.0
            }
        }

        - If a food is vaguely described (e.g., “a plate of pasta”), assume a common recipe and average portion size.
        - If ingredients or brands are not specified, assume a standard version (e.g., plain cooked pasta, white rice, grilled chicken).
        - Estimate total weight and nutritional values accordingly.
        - If the description includes multiple foods, combine them into a single JSON object by summing their values.
        - If the description contains amounts like "100g of pasta and 100g of broccoli", combine the amounts into a single JSON that has amountsInGrams = 200.
        - Use 0 or an empty string for unknown values.
        - Return **only** a valid JSON object. Do not include any explanations or extra text.

        Food description:
        "$userInput"
    """.trimIndent()

    return prompt
}