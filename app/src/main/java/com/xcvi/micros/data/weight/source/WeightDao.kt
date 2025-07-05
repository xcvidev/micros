package com.xcvi.micros.data.weight.source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.xcvi.micros.data.weight.model.Weight

@Dao
interface WeightDao {

    @Query("SELECT * FROM weights ORDER BY timestamp DESC LIMIT :limit")
    suspend fun get(limit: Int = 1): Weight?

    @Query("SELECT * FROM weights ORDER BY timestamp ASC")
    suspend fun get(): List<Weight>

    @Query("SELECT * FROM weights WHERE timestamp BETWEEN :start AND :end ORDER BY timestamp DESC")
    suspend fun get(start: Long, end: Long): List<Weight>

    @Upsert
    suspend fun upsert(weight: Weight)

    @Delete
    suspend fun delete(weight: Weight)


}