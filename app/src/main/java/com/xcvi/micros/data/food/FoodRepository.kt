package com.xcvi.micros.data.food

import com.xcvi.micros.data.food.model.MealCard
import com.xcvi.micros.data.food.model.entity.Portion
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
     * SEARCH
     */
    suspend fun searchLocal(searchTerm: String): Response<List<Portion>> {
        try {
            val query = withContext(Dispatchers.Default) {
                buildMultiWordQuery(searchTerm, LIMIT, OFFSET)
            }
            val products = withContext(Dispatchers.IO) {
                dao.search(query)
            }
            return Response.Success(products)
        } catch (e: Exception) {
            println("MyLog: Error ${e.message}")
            return Response.Error(e)
        }
    }

    suspend fun searchRemote(description: String): Response<List<Portion>> {
        return try {
            val res = withContext(Dispatchers.IO) {
                api.search(query = description, pageSize = PAGE_SIZE, page = PAGE)
            }
            when (res) {
                is Response.Error -> return Response.Error(res.error)
                is Response.Success -> {
                    withContext(Dispatchers.IO) {
                        res.data.forEach { portion ->
                            upsert(listOf(portion))
                        }
                    }
                    return Response.Success(res.data)
                }
            }
        } catch (e: Exception) {
            println("MyLog: Error ${e.message}")
            Response.Error(e)
        }
    }



    suspend fun scan(barcode: String): Response<Unit> {
        try {
            val res = withContext(Dispatchers.IO) {
                api.scan(barcode)
            }
            return when (res) {
                is Response.Error -> Response.Error(res.error)
                is Response.Success -> {
                    withContext(Dispatchers.IO) {
                        upsert(listOf(res.data))
                    }
                    Response.Success(Unit)
                }
            }
        } catch (e: Exception) {
            return Response.Error(e)
        }
    }

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
            return Response.Error(e)
        }
    }


    /**
     * Portion Details
     */
    suspend fun getPortion(meal: Int, date: Int, barcode: String, amount: Int): Response<Portion> {
        try {
            val exactPortion = withContext(Dispatchers.IO) {
                dao.getPortion(barcode = barcode, date = date, mealNumber = meal)
            }
            if (exactPortion != null) {
                return Response.Success(exactPortion.scaledTo(amount.toDouble()))
            }
            val cachedPortion = withContext(Dispatchers.IO) {
                dao.getPortion(barcode = barcode)
            }
            if (cachedPortion != null) {
                val res = cachedPortion.scaledTo(amount.toDouble()).copy(date = date, meal = meal)
                return Response.Success(res)
            }
            return Response.Error(Failure.Unknown)
        } catch (e: Exception) {
            println("MyLog: Error ${e.message}")
            return Response.Error(e)
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
            Response.Error(e)
        }
    }

    suspend fun updatePortion(portion: Portion, amount: Double): Response<Unit> {
        return try {
            val updatedPortion =
                withContext(Dispatchers.Default) { portion.scaledTo(amount).roundDecimals() }
            withContext(Dispatchers.IO) { upsert(listOf(updatedPortion)) }
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(e)
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
            Response.Error(e)
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
            return Response.Error(e)
        }
    }


}


/*

suspend fun search(query: String): Response<SearchResult> {
        if (query.isBlank()) return Response.Error(Failure.InvalidInput)

        var networkError = false

        // 1. AI generate
        val generated = when (val genResult = generate(query, api, upsert)) {
            is Response.Success -> genResult.data
            is Response.Error -> {
                networkError = true
                null
            }
        }

        // 2. API search
        val apiItems = when (
            val apiResult = searchApi(
                query = query,
                api = api,
                upsert = { portions ->
                    portions.forEach { portion ->
                        withContext(Dispatchers.IO) { upsert(portion) }
                    }
                }
            )
        ) {
            is Response.Success -> apiResult.data
            is Response.Error -> {
                networkError = true
                emptyList()
            }
        }

        // 3. Local search
        val localItems = when (
            val localResult = searchLocal(query, limit = 100, offset = 0, dao = dao)
        ) {
            is Response.Success -> {
                localResult.data
            }
            is Response.Error -> {// local should never trigger networkError
                emptyList()
            }
        }

        // 4. Filter out duplicates
        val apiBarcodes = apiItems.map { it.barcode }.toSet()
        val extraLocalItems = localItems.filter { it.barcode !in apiBarcodes }
        val combinedItems = extraLocalItems + apiItems

        // 6. Merge
        val searchResult = SearchResult(combinedItems, generated)

        println(println("MyLog: generated: " + searchResult.generated?.name + " portions found: " + searchResult.portions.size))

        // 6. Return based on content and errors
        return if (combinedItems.isEmpty()){
            if (networkError) {
                Response.Error(Failure.Network)
            } else {
                Response.Error(Failure.EmptyResult)
            }
        } else {
            Response.Success(searchResult)
        }
    }



private suspend fun getSummary(date: Int, meal: Int): Portion =
        withContext(Dispatchers.Default) {
            val macroDeferred = async { dao.sumMacros(date = date, meal = meal) }
            val mineralDeferred = async { dao.sumMinerals(date = date, meal = meal) }
            val vitaminDeferred = async { dao.sumVitamins(date = date, meal = meal) }
            val aminoDeferred = async { dao.sumAminoacids(date = date, meal = meal) }

            val macros = macroDeferred.await() ?: Macros()
            val minerals = mineralDeferred.await() ?: Minerals()
            val vitamins = vitaminDeferred.await() ?: Vitamins()
            val aminoAcids = aminoDeferred.await() ?: AminoAcids()

            Portion(
                date = date,
                meal = meal,
                name = "Daily Total",
                brand = "",
                barcode = "summary_$date",
                novaGroup = 0.0,
                isFavorite = 0,
                amountInGrams = 0.0,
                ingredients = "",
                macros = macros.roundDecimals(),
                minerals = minerals.roundDecimals(),
                vitamins = vitamins.roundDecimals(),
                aminoAcids = aminoAcids.roundDecimals()
            )
        }

    suspend fun insertPortion() {
        (1..10).forEach {
            val protein = Random.nextInt(30,40)
            val carbohydrates = Random.nextInt(50,100)
            val fats = Random.nextInt(0,5)
            val portion = Portion(
                date = getToday() - (it % 3),
                meal = Random.nextInt(1, 6),
                name = "Food $it",
                barcode = "barcode $it",
                amountInGrams = Random.nextDouble(50.0, 200.0).roundDecimals(),
                macros = Macros(
                    calories = (protein * 4.0 + carbohydrates * 4.0 + fats * 9.0).roundDecimals(),
                    protein = protein * 1.0,
                    carbohydrates = carbohydrates* 1.0,
                    fats = fats* 1.0
                ),
                minerals = Minerals(calcium = 150.0),
                vitamins = Vitamins(vitaminA = 100.0),
                aminoAcids = AminoAcids(leucine = protein * 0.1),
            )

            dao.upsertPortion(portion)

        }
    }
 */
