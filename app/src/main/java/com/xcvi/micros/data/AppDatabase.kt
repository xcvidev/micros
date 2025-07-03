package com.xcvi.micros.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.xcvi.micros.data.food.model.entity.Portion
import com.xcvi.micros.data.food.source.FoodDao
import com.xcvi.micros.data.weight.model.Weight
import com.xcvi.micros.data.weight.source.WeightDao

@Database(entities = [Portion::class, Weight::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase(){
    abstract fun foodDao(): FoodDao
    abstract fun weightDao(): WeightDao
}