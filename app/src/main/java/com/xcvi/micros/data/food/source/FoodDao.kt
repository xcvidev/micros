package com.xcvi.micros.data.food.source

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.RawQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.xcvi.micros.data.food.model.FoodStats
import com.xcvi.micros.data.food.model.entity.AminoAcids
import com.xcvi.micros.data.food.model.entity.Macros
import com.xcvi.micros.data.food.model.entity.Minerals
import com.xcvi.micros.data.food.model.entity.Portion
import com.xcvi.micros.data.food.model.entity.Vitamins

@Dao
interface FoodDao {

    /**
     * Summary
     */
    // --- Macros ---
    @Query(sumMacros)
    suspend fun sumMacros(): List<FoodStats>

    @Query(sumMacrosByDate)
    suspend fun sumMacros(date: Int): Macros?

    @Query(sumMacrosByDateAndMeal)
    suspend fun sumMacros(date: Int, meal: Int): Macros?

    // --- Minerals ---
    @Query(sumMineralsByDate)
    suspend fun sumMinerals(date: Int): Minerals?
    @Query(sumMineralsByDateAndMeal)
    suspend fun sumMinerals(date: Int, meal: Int): Minerals?

    // --- Vitamins ---
    @Query(sumVitaminsByDate)
    suspend fun sumVitamins(date: Int): Vitamins?
    @Query(sumVitaminsByDateAndMeal)
    suspend fun sumVitamins(date: Int, meal: Int): Vitamins?

    // --- Amino Acids ---
    @Query(sumAminoacidsByDate)
    suspend fun sumAminoacids(date: Int): AminoAcids?
    @Query(sumAminoacidsByDateAndMeal)
    suspend fun sumAminoacids(date: Int, meal: Int): AminoAcids?

    @Delete
    suspend fun deletePortion(portion: Portion)


    /**
     * Get
     */
    @RawQuery
    suspend fun search(query: SupportSQLiteQuery): List<Portion>

    @Query(getRecents)
    suspend fun getPortions(): List<Portion>

    @Query("SELECT * FROM portions WHERE barcode IN (:barcodes) GROUP BY barcode")
    suspend fun getPortions(barcodes: List<String>): List<Portion>

    @Query("SELECT * FROM portions WHERE date = :date")
    suspend fun getPortions(date: Int): List<Portion>

    @Query("SELECT * FROM portions WHERE date = :date and meal = :meal")
    suspend fun getPortions(date: Int, meal: Int): List<Portion>

    @Query("SELECT * FROM portions WHERE barcode = :barcode LIMIT 1")
    suspend fun getPortion(barcode: String): Portion?

    @Query("SELECT * FROM portions WHERE barcode = :barcode AND date = :date AND meal = :mealNumber")
    suspend fun getPortion(barcode: String, date: Int, mealNumber: Int): Portion?

}