package com.xcvi.micros.data.food.source

import androidx.room.Dao
import androidx.room.Upsert
import com.xcvi.micros.data.food.model.entity.Portion

@Dao
interface UpsertDao {
    @Upsert
    suspend fun upsert(portion: List<Portion>)
}