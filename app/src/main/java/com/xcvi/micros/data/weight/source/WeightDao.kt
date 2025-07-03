package com.xcvi.micros.data.weight.source

import androidx.room.Dao
import androidx.room.Query
import com.xcvi.micros.data.weight.model.Weight

@Dao
interface WeightDao {

    @Query("SELECT * FROM weights ORDER BY timestamp DESC LIMIT 1")
    suspend fun getWeight(): Weight?

}