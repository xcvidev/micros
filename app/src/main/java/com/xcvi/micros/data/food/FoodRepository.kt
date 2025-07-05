package com.xcvi.micros.data.food

import com.xcvi.micros.data.food.model.FoodStats
import com.xcvi.micros.data.food.model.MealCard
import com.xcvi.micros.data.food.model.entity.Portion
import com.xcvi.micros.data.food.model.groupByMonth
import com.xcvi.micros.data.food.model.groupByWeek
import com.xcvi.micros.data.food.source.FoodApi
import com.xcvi.micros.data.food.source.FoodDao
import com.xcvi.micros.domain.Failure
import com.xcvi.micros.domain.Response
import com.xcvi.micros.domain.getNow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class FoodRepository(
    private val dao: FoodDao,
    private val api: FoodApi,
    private val upsert: suspend (List<Portion>) -> Unit
) {

    /**
     * Enhance Details TODO
     */
    /*
    suspend fun enhance(
        meal: Int,
        date: Int,
        barcode: String,
        amount: Int
    ): Response<Portion> = apiCacheFetch(
        apiCall = {
            val portion = getPortion(barcode = barcode, date = date, meal = meal, amount = amount)
            api.enhance(portion)
        },
        cacheCall = { upsert(listOf(it)) },
        dbCall = { dao.getPortion(it.barcode) },
        fallbackRequest = barcode,
        fallbackDbCall = {
            getPortion(barcode = it, date = date, meal = meal, amount = amount)
        }
    )

     */

    suspend fun getPortion(
        meal: Int,
        date: Int,
        barcode: String,
        amount: Int
    ): Response<Portion> {
        try {
            val exactPortion = withContext(Dispatchers.IO) {
                dao.getPortion(barcode = barcode, date = date, mealNumber = meal)
            }?.scaledTo(amount.toDouble())

            if (exactPortion != null) {
                return Response.Success(exactPortion)
            }

            val cachedPortion = withContext(Dispatchers.IO) {
                dao.getPortion(barcode = barcode)
            }?.scaledTo(amount.toDouble())?.copy(date = date, meal = meal)
            if (cachedPortion != null) {
                return Response.Success(cachedPortion)
            }

            return Response.Error(Failure.Unknown)
        } catch (e: Exception) {
            println("MyLog: Error ${e.message}")
            return Response.Error(Failure.Database)
        }
    }

    /**
     * STATS
     */
    suspend fun stats(): Response<Pair<List<FoodStats>, List<FoodStats>>> {
        try {
            val foodStats = withContext(Dispatchers.IO) {
                dao.sumMacros()
            }
            val byWeek = foodStats.groupByWeek()
            val byMonth = foodStats.groupByMonth()
            return Response.Success(Pair(byWeek, byMonth))
        } catch (e: Exception) {
            return Response.Error(Failure.Database)
        }

    }


    /**
     * SEARCH
     */
    suspend fun generate(description: String): Response<Portion> = apiCacheFetch(
        apiCall = { api.generate(description) },
        cacheCall = { upsert(listOf(it)) },
        dbCall = { dao.getPortion(it.barcode) },
        fallbackRequest = description,
        fallbackDbCall = { null }
    )

    suspend fun scan(barcode: String): Response<Portion> = apiCacheFetch(
        apiCall = { api.scan(barcode) },
        cacheCall = { upsert(listOf(it)) },
        dbCall = { dao.getPortion(it.barcode) },
        fallbackRequest = barcode,
        fallbackDbCall = { dao.getPortion(barcode) }
    )

    suspend fun search(searchTerm: String): Response<List<Portion>> = apiCacheFetch(
        apiCall = { api.search(searchTerm) },
        cacheCall = { upsert(it) },
        dbCall = {
            val query = withContext(Dispatchers.Default) {
                buildMultiWordQuery(searchTerm, LIMIT, OFFSET)
            }
            dao.search(query)
        },
        fallbackRequest = searchTerm,
        fallbackDbCall = { fallbackRequest ->
            val query = withContext(Dispatchers.Default) {
                buildMultiWordQuery(fallbackRequest, LIMIT, OFFSET)
            }
            dao.search(query)
        }
    )


    suspend fun getRecents(): Response<List<Portion>> {
        try {
            val recents = withContext(Dispatchers.IO) {
                dao.getPortions()
            }
            return withContext(Dispatchers.IO) {
                val res = recents.distinctBy { it.barcode }
                Response.Success(res)
            }
        } catch (e: Exception) {
            return Response.Error(Failure.Database)
        }
    }


    /**
     * Dashboard
     */
    suspend fun delete(portion: Portion): Response<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                dao.deletePortion(portion)
            }
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(Failure.Database)
        }
    }

    suspend fun updatePortion(portion: Portion, amount: Double): Response<Unit> {
        return try {
            val updatedPortion =
                withContext(Dispatchers.Default) { portion.scaledTo(amount).roundDecimals() }
            withContext(Dispatchers.IO) { upsert(listOf(updatedPortion)) }
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(Failure.Database)
        }
    }

    suspend fun updatePortion(portions: List<Portion>, name: String): Response<Unit> {
        return try {
            if (portions.isEmpty()) {
                return Response.Success(Unit)
            }
            val updatedPortion = withContext(Dispatchers.Default) {
                portions.summary().roundDecimals()
            }.copy(
                barcode = "custom_${name}_${getNow()}",
                name = name,
                date = -1,
                meal = -1,
                isFavorite = 1,
                amountInGrams = 1.0
            )
            withContext(Dispatchers.IO) { upsert(listOf(updatedPortion)) }
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(Failure.Database)
        }
    }

    suspend fun getFoodData(date: Int): Response<Pair<Portion, List<MealCard>>> {
        try {
            val portions = withContext(Dispatchers.IO) {
                dao.getPortions(date = date)
            }

            val meals = withContext(Dispatchers.Default) {
                portions.map { it.roundDecimals() }.groupBy { it.meal }
            }
            val summary = withContext(Dispatchers.Default) {
                getSummary(date, dao).roundDecimals()
            }

            val mealCards = withContext(Dispatchers.Default) {
                (1..6).map { mealNumber ->
                    val mealPortions = meals[mealNumber].orEmpty()
                    MealCard(
                        meal = mealNumber,
                        summary = mealPortions.summary(date).roundDecimals(),
                        portions = mealPortions
                    )
                }
            }
            return Response.Success(Pair(summary, mealCards))
        } catch (e: Exception) {
            return Response.Error(Failure.Database)
        }
    }


}

/*

 */