package com.xcvi.micros.data.food.source

import com.xcvi.micros.data.food.model.entity.Portion
import com.xcvi.micros.data.food.model.dto.ScanDTO
import com.xcvi.micros.data.food.model.dto.SearchDTO
import com.xcvi.micros.data.food.toPortionCache
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

    private val jsonFormat = Json {
        ignoreUnknownKeys = true      // just in case
        explicitNulls = false
        isLenient = true
    }

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

    suspend fun enhance(portion: Portion?, userDesc: String): Portion? {
        if (portion == null) return null

        val prompt = getEnhancementPrompt(portion, userDesc)
        val rawReply = queryOpenAi(prompt) ?: return null

        // Strip any Markdown fencing the model might add
        val cleanJson = rawReply.trim()
            .removePrefix("```json").removePrefix("```")
            .removeSuffix("```").trim()


        val vitamins = jsonFormat.decodeFromString<Vitamins>(cleanJson)
        println("MyLog:  Vitamins: $vitamins")
        if(vitamins.isEmpty()){
            return null
        }
        val updated = portion.copy(
            macros = portion.macros.copy(
                vitaminA = vitamins.vitaminA,
                vitaminB = vitamins.vitaminB,
                vitaminC = vitamins.vitaminC,
                vitaminD = vitamins.vitaminD,
                vitaminE = vitamins.vitaminE,
                vitaminK = vitamins.vitaminK
            )
        )
        return updated
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

