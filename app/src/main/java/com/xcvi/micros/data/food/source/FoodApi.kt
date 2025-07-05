package com.xcvi.micros.data.food.source

import com.xcvi.micros.data.food.model.entity.Portion
import com.xcvi.micros.data.food.model.dto.ScanDTO
import com.xcvi.micros.data.food.model.dto.SearchDTO
import com.xcvi.micros.data.food.toPortionCache
import com.xcvi.micros.domain.Failure
import com.xcvi.micros.domain.Response
import com.xcvi.micros.domain.getNow
import com.xcvi.micros.domain.roundDecimals
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.url
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.addJsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray

class FoodApi(
    private val aiClient: HttpClient,
    private val scanClient: HttpClient
) {

    suspend fun generate(description: String): Portion? {
        val cleanedDesc = description.trim().lowercase().replaceFirstChar { it.uppercase() }
        val prompt = getEstimatePrompt(cleanedDesc)
        val json = queryOpenAi(prompt)
        println("Portion: $json")
        if (json == null) {
            return null
        } else {
            val jsonClean = json
                .trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()
            val dto = Json.decodeFromString<Portion>(jsonClean)
            val portion = dto.copy(
                name = cleanedDesc,
                barcode = "AI_${cleanedDesc}}",
                macros = dto.macros.copy(
                    calories = (dto.macros.protein * 4 + dto.macros.carbohydrates * 4 + dto.macros.fats * 9).roundDecimals()
                )
            )
            if (portion.macros.isEmpty()) {
                return null
            }
            return portion
        }
    }

    suspend fun enhance(portion: Portion?): Portion? {
        if (portion == null) {
            return null
        }
        val prompt = getEnhancePrompt(portion)
        val json = queryOpenAi(prompt) ?: return null
        val jsonClean = json.trim()
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()
        val dto = Json.decodeFromString<Portion>(jsonClean)
        return dto
    }

    private suspend fun queryOpenAi(prompt: String): String? {
        val url = "https://api.openai.com/v1/chat/completions"

        val requestBody = buildJsonObject {
            put("model", "gpt-3.5-turbo") // "gpt-4" or "gpt-3.5-turbo"
            putJsonArray("messages") {
                addJsonObject {
                    put("role", "system")
                    put(
                        "content",
                        "You are a nutrition assistant that outputs JSON formatted food estimations compatible with PortionEntity."
                    )
                }
                addJsonObject {
                    put("role", "user")
                    put("content", prompt)
                }
            }
            put("temperature", 0.7)
        }

        val response: JsonObject = withContext(Dispatchers.IO) {
            aiClient.post(url) {
                body = requestBody
            }
        }

        return response["choices"]?.jsonArray?.get(0)
            ?.jsonObject?.get("message")
            ?.jsonObject?.get("content")
            ?.jsonPrimitive?.contentOrNull
    }


    private fun getEnhancePrompt(portion: Portion): String {
        val partialJson = Json.encodeToString(Portion.serializer(), portion)
        val prompt = """
        You are a nutrition assistant. Based on the partially filled Portion JSON below, enhance any missing or incomplete fields by estimating appropriate values.

        When enhancing:
        - Only fill numeric fields that are 0 if they are likely missing values.
        - Do not overwrite values that are legitimately zero.
        - Fill empty strings with meaningful information.
        - All values must be in grams.
        - If unsure, assume missing and estimate.

        Partial Portion JSON:
        $partialJson

        Return a fully completed Portion JSON object.
    """.trimIndent()
        return prompt
    } //(use lowercase field names with underscores as shown below)

    private fun getEstimatePrompt(userInput: String): String {
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

    suspend fun scan(barcode: String): Portion? {
        val url = "https://world.openfoodfacts.org/api/v3/product/$barcode"

        val res: ScanDTO = withContext(Dispatchers.IO) {
            scanClient.get {
                url(url)
            }
        }
        return res.product?.toPortionCache()

    }

    suspend fun search(query: String, pageSize: Int = 50, page: Int = 1): List<Portion> {
        val url = "https://world.openfoodfacts.org/cgi/search.pl"

        val res: SearchDTO = withContext(Dispatchers.IO) {
            scanClient.get {
                url(url)
                parameter("search_terms", query)
                parameter("page", page)
                parameter("page_size", pageSize)
                parameter("search_simple", 1)
                parameter("action", "process")
                parameter("json", 1)

            }
        }

        return withContext(Dispatchers.Default) {
            res.products.mapNotNull { it.toPortionCache() }
        }
    }
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