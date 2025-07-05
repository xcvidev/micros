package com.xcvi.micros.data.food

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.xcvi.micros.data.food.model.FoodStats
import com.xcvi.micros.data.food.model.entity.AminoAcids
import com.xcvi.micros.data.food.model.entity.Macros
import com.xcvi.micros.data.food.model.entity.Minerals
import com.xcvi.micros.data.food.model.entity.Portion
import com.xcvi.micros.data.food.model.entity.Vitamins
import com.xcvi.micros.data.food.source.FoodDao
import com.xcvi.micros.domain.Failure
import com.xcvi.micros.domain.Response
import com.xcvi.micros.domain.generateMonthsBetween
import com.xcvi.micros.domain.generateWeeksBetween
import com.xcvi.micros.domain.getLocalDate
import com.xcvi.micros.domain.getStartOfMonth
import com.xcvi.micros.domain.getStartOfWeek
import com.xcvi.micros.domain.normalizeToWordSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate

const val LIMIT = 100
const val PAGE_SIZE = 50
const val OFFSET = 0
const val PAGE = 1




suspend fun getPortion(
    meal: Int,
    date: Int,
    barcode: String,
    amount: Int,
    dao: FoodDao
): Portion? {
    val exactPortion = withContext(Dispatchers.IO) {
        dao.getPortion(barcode = barcode, date = date, mealNumber = meal)
    }?.scaledTo(amount.toDouble())

    if (exactPortion != null) {
        return exactPortion
    }
    val cachedPortion = withContext(Dispatchers.IO) {
        dao.getPortion(barcode = barcode)
    }?.scaledTo(amount.toDouble())?.copy(date = date, meal = meal)

    return cachedPortion
}




suspend fun <FallbackRequest, ApiResult, DbResult> apiCacheFetch(
    fallbackRequest: FallbackRequest,
    apiCall: suspend () -> ApiResult?,
    cacheCall: suspend (ApiResult) -> Unit,
    dbCall: suspend (ApiResult) -> DbResult?,
    fallbackDbCall: suspend (FallbackRequest) -> DbResult?,
    emptyResultFailure: Failure = Failure.EmptyResult,
    networkFailure: Failure = Failure.Network,
    databaseFailure: Failure = Failure.Database,
): Response<DbResult> = withContext(Dispatchers.IO) {
    val apiResponse: Response<ApiResult> = try {
        val result = apiCall()
        if (result != null) {
            Response.Success(result)
        } else {
            Response.Error(emptyResultFailure)
        }
    } catch (e: Exception) {
        println("MyLog: API failed: ${e.message}")
        Response.Error(networkFailure)
    }

    when (apiResponse) {
        is Response.Success -> {
            val dbResult = try {
                cacheCall(apiResponse.data)
                dbCall(apiResponse.data)
            } catch (e: Exception) {
                println("MyLog: DB fetch after success failed: ${e.message}")
                null
            }

            if (dbResult != null) {
                Response.Success(dbResult)
            } else {
                Response.Error(databaseFailure)
            }
        }

        is Response.Error -> {
            val fallbackResult = try {
                fallbackDbCall(fallbackRequest)
            } catch (e: Exception) {
                println("MyLog: Fallback DB fetch failed: ${e.message}")
                null
            }

            if (fallbackResult != null) {
                Response.Success(fallbackResult)
            } else {
                Response.Error(apiResponse.error)
            }
        }
    }
}


fun buildMultiWordQuery(
    rawSearch: String,
    limit: Int,
    offset: Int,
): SupportSQLiteQuery {
    val words = rawSearch.normalizeToWordSet()
    val base = StringBuilder("SELECT * FROM portions WHERE ")
    val args = mutableListOf<String>()

    words.forEachIndexed { index, word ->
        if (index > 0) base.append(" AND ")
        base.append("(' ' || tag || ' ' LIKE ?)")
        val param = "% $word %"
        args.add(param)
    }

    // Number of search words used for ordering
    val searchWordCount = words.size
    base.append(" ORDER BY (CASE WHEN brand = '' THEN 1 ELSE 0 END) ASC, ABS(tagwordcount - ?) ASC, name ASC ")
    args.add(searchWordCount.toString())

    base.append(" LIMIT ? OFFSET ?")
    args.add(limit.toString())
    args.add(offset.toString())
    println("MyLog: query: " + SimpleSQLiteQuery(base.toString(), args.toTypedArray()).sql)
    return SimpleSQLiteQuery(base.toString(), args.toTypedArray())
}


suspend fun getSummary(date: Int, dao: FoodDao): Portion =
    withContext(Dispatchers.Default) {
        val macroDeferred = async { dao.sumMacros(date) }
        val mineralDeferred = async { dao.sumMinerals(date) }
        val vitaminDeferred = async { dao.sumVitamins(date) }
        val aminoDeferred = async { dao.sumAminoacids(date) }

        val macros = macroDeferred.await() ?: Macros()
        val minerals = mineralDeferred.await() ?: Minerals()
        val vitamins = vitaminDeferred.await() ?: Vitamins()
        val aminoAcids = aminoDeferred.await() ?: AminoAcids()

        Portion(
            date = date,
            meal = -1,
            name = "Daily Total",
            brand = "",
            barcode = "summary_$date",
            novaGroup = 0.0,
            isFavorite = 0,
            amountInGrams = 0.0,
            ingredients = "",
            macros = macros,
            minerals = minerals,
            vitamins = vitamins,
            aminoAcids = aminoAcids
        )
    }

