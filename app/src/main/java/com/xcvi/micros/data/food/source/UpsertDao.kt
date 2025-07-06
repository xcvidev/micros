package com.xcvi.micros.data.food.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Upsert
import com.xcvi.micros.data.food.model.entity.Portion

@Dao
interface UpsertDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(portion: List<Portion>)
}