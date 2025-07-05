package com.xcvi.micros.data.food

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.xcvi.micros.data.food.model.entity.AminoAcids
import com.xcvi.micros.data.food.model.entity.Macros
import com.xcvi.micros.data.food.model.entity.Minerals
import com.xcvi.micros.data.food.model.entity.Portion
import com.xcvi.micros.data.food.model.entity.Vitamins
import com.xcvi.micros.data.food.source.FoodDao
import com.xcvi.micros.domain.Failure
import com.xcvi.micros.domain.Response
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

data class FoodStats(
    val date: Int,
    val calories: Double,
    val protein: Double,
    val carbohydrates: Double,
    val fats: Double
)

private fun List<FoodStats>.groupByPeriod(
    keySelector: (FoodStats) -> Int
): Map<Int, FoodStats> {
    return this.groupBy(keySelector).mapValues { (_, group) ->
        val count = group.size.toDouble()
        FoodStats(
            date = group.first().date, // will override later
            calories = group.sumOf { it.calories } / count,
            protein = group.sumOf { it.protein } / count,
            carbohydrates = group.sumOf { it.carbohydrates } / count,
            fats = group.sumOf { it.fats } / count
        )
    }
}
fun generateWeeksBetween(startDate: Int, endDate: Int): List<Int> {
    val weeks = mutableListOf<Int>()
    var current = startDate.getStartOfWeek()
    val end = endDate.getStartOfWeek()
    while (current <= end) {
        weeks.add(current)
        current += 7
    }
    return weeks
}
fun generateMonthsBetween(startDate: Int, endDate: Int): List<Int> {
    val months = mutableListOf<Int>()
    var current = startDate.getStartOfMonth()
    val lastMonth = endDate.getStartOfMonth()

    while (current <= lastMonth) {
        months.add(current)
        val localDate = LocalDate.fromEpochDays(current)
        val nextMonth = LocalDate(localDate.year, localDate.month.ordinal + 1, 1)
        current = nextMonth.toEpochDays()
    }

    return months
}
fun List<FoodStats>.groupByWeekComplete(): List<FoodStats> {
    if (this.isEmpty()) return emptyList()
    val grouped = this.groupByPeriod { it.date.getStartOfWeek() }
    val minDate = this.minOf { it.date }
    val maxDate = this.maxOf { it.date }
    val allWeeks = generateWeeksBetween(minDate, maxDate)

    return allWeeks.map { weekStart ->
        grouped[weekStart]?.copy(date = weekStart)
            ?: FoodStats(date = weekStart, calories = 0.0, protein = 0.0, carbohydrates = 0.0, fats = 0.0)
    }
}

fun List<FoodStats>.groupByMonthComplete(): List<FoodStats> {
    if (this.isEmpty()) return emptyList()
    val grouped = this.groupByPeriod { it.date.getStartOfMonth() }
    val minDate = this.minOf { it.date }
    val maxDate = this.maxOf { it.date }
    val allMonths = generateMonthsBetween(minDate, maxDate)

    return allMonths.map { monthStart ->
        grouped[monthStart]?.copy(date = monthStart)
            ?: FoodStats(date = monthStart, calories = 0.0, protein = 0.0, carbohydrates = 0.0, fats = 0.0)
    }
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
            }
            else {
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
            }
            else {
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

