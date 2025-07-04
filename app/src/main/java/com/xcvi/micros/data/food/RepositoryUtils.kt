package com.xcvi.micros.data.food

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.xcvi.micros.data.food.model.entity.AminoAcids
import com.xcvi.micros.data.food.model.entity.Macros
import com.xcvi.micros.data.food.model.entity.Minerals
import com.xcvi.micros.data.food.model.entity.Portion
import com.xcvi.micros.data.food.model.entity.Vitamins
import com.xcvi.micros.data.food.source.FoodApi
import com.xcvi.micros.data.food.source.FoodDao
import com.xcvi.micros.domain.Response
import com.xcvi.micros.domain.normalizeToWordSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

 const val LIMIT = 100
 const val PAGE_SIZE = 50
 const val OFFSET = 0
 const val PAGE = 1


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

