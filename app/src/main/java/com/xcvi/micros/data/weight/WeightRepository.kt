package com.xcvi.micros.data.weight

import com.xcvi.micros.data.weight.model.Weight
import com.xcvi.micros.data.weight.model.WeightStats
import com.xcvi.micros.data.weight.model.groupByMonth
import com.xcvi.micros.data.weight.model.groupByWeek
import com.xcvi.micros.data.weight.source.WeightDao
import com.xcvi.micros.domain.Failure
import com.xcvi.micros.domain.Response
import com.xcvi.micros.domain.getEndOfMonth
import com.xcvi.micros.domain.getLocalDate
import com.xcvi.micros.domain.getLocalDateTime
import com.xcvi.micros.domain.getNow
import com.xcvi.micros.domain.getStartOfMonth
import com.xcvi.micros.domain.getStartOfWeek
import com.xcvi.micros.domain.getTimestamp
import com.xcvi.micros.domain.monthFormatted
import com.xcvi.micros.domain.roundDecimals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WeightRepository(
    private val dao: WeightDao
) {

    suspend fun stats(): Response<Pair<List<WeightStats>, List<WeightStats>>> {
        return try {
            val weights = withContext(Dispatchers.IO) { dao.get() }
            val normalizer = weights.minOfOrNull { it.weight } ?: 0.0
            withContext(Dispatchers.Default) {
                val byWeek = weights.groupByWeek().map { entry ->
                    val currentWeek = entry.value.map { it.weight }
                    val min = currentWeek.minOrNull()
                    val max = currentWeek.maxOrNull()
                    val avg = if (currentWeek.isNotEmpty()) {
                        currentWeek.average()
                    } else {
                        null
                    }
                    val date = entry.key.getLocalDate()

                    val normalized = avg?.minus(normalizer)
                    WeightStats(
                        min = min,
                        max = max,
                        avg = avg,
                        date = entry.key,
                        label = date.monthFormatted(true) + " " + date.dayOfMonth,
                        normalized = avg?.minus(normalizer)?.plus(normalizer.times(0.05))
                    )
                }

                val byMonth = weights.groupByMonth().map { entry ->
                    val currentMonth = entry.value.map { it.weight }
                    val min = currentMonth.minOrNull() ?: 0.0
                    val max = currentMonth.maxOrNull() ?: 0.0
                    val avg = if (currentMonth.isNotEmpty()) {
                        currentMonth.average()
                    } else {
                        null
                    }
                    val date = entry.key.getLocalDate()

                    WeightStats(
                        min = min,
                        max = max,
                        avg = avg,
                        date = date.toEpochDays(),
                        label = date.monthFormatted(true).uppercase(),
                        normalized = avg?.minus(normalizer)?.plus(normalizer.times(0.05))
                    )

                }

                Response.Success(Pair(byWeek, byMonth))
            }
        } catch (e: Exception) {
            Response.Error(Failure.Database)
        }
    }

    suspend fun get(): Response<Weight> {
        return try {
            val weight = dao.get(limit = 1) ?: return Response.Error(Failure.EmptyResult)
            Response.Success(weight)
        } catch (e: Exception) {
            Response.Error(Failure.Database)
        }
    }

    suspend fun getWeek(date: Int): Response<List<Weight>> {
        return try {
            val monday = date.getStartOfWeek()
            val start = monday.getTimestamp(0, 0, 0)
            val end = (monday + 6).getTimestamp(23, 59, 59)
            val weight = dao.get(start = start, end = end)
            Response.Success(weight)
        } catch (e: Exception) {
            Response.Error(Failure.Database)
        }
    }

    suspend fun save(value: Double, date: Int): Response<Unit> {
        return try {
            val time = getNow().getLocalDateTime()
            val weight = Weight(
                weight = value.roundDecimals(),
                timestamp = date.getTimestamp(time.hour, time.minute, time.second)
            )
            dao.upsert(weight)
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(Failure.Database)
        }
    }

    suspend fun delete(weight: Weight): Response<Unit> {
        return try {
            dao.delete(weight)
            Response.Success(Unit)
        } catch (e: Exception) {
            Response.Error(Failure.Database)
        }
    }

}


/*

-- Normalized exactly: There is always a zero in graph --

suspend fun stats(): Response<Pair<List<WeightStats>, List<WeightStats>>> {
        return try {
            val weights = withContext(Dispatchers.IO) { dao.get() }
            var normalizer = Double.MAX_VALUE
            withContext(Dispatchers.Default) {
                val byWeek = weights.groupByWeek().map { entry ->
                    val currentWeek = entry.value.map { it.weight }
                    val min = currentWeek.minOrNull()
                    val max = currentWeek.maxOrNull()
                    val avg = if (currentWeek.isNotEmpty()) {
                        val avg = currentWeek.average()
                        if (avg > 0.0 && avg < normalizer) {
                            normalizer = avg
                        }
                        avg
                    } else {
                        null
                    }
                    val date = entry.key.getLocalDate()

                    WeightStats(
                        min = min,
                        max = max,
                        avg = avg,
                        date = entry.key,
                        label = date.monthFormatted(true) + " " + date.dayOfMonth,
                    )
                }
                normalizer = if(normalizer == Double.MAX_VALUE) 1.0 else normalizer
                val byWeekNormalized = byWeek.map {
                    it.copy(normalized = it.avg?.minus(normalizer))
                }


                normalizer = Double.MAX_VALUE
                val byMonth = weights.groupByMonth().map { entry ->
                    val currentMonth = entry.value.map { it.weight }
                    val min = currentMonth.minOrNull() ?: 0.0
                    val max = currentMonth.maxOrNull() ?: 0.0
                    val avg = if (currentMonth.isNotEmpty()) {
                        val avg = currentMonth.average()
                        if (avg > 0.0 && avg < normalizer) {
                            normalizer = avg
                        }
                        avg
                    } else {
                        null
                    }
                    val date = entry.key.getLocalDate()
                    WeightStats(
                        min = min,
                        max = max,
                        avg = avg,
                        date = date.toEpochDays(),
                        label = date.monthFormatted(true).uppercase(),
                    )

                }
                normalizer = if(normalizer == Double.MAX_VALUE) 1.0 else normalizer
                val byMonthNormalized = byMonth.map {
                    it.copy(normalized = it.avg?.minus(normalizer))
                }
                Response.Success(Pair(byWeekNormalized, byMonthNormalized))
            }
        } catch (e: Exception) {
            Response.Error(Failure.Database)
        }
    }
 */