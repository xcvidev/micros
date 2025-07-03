package com.xcvi.micros.data.weight.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weights")
data class Weight(
    val weight: Double,
    val unit: String = "kg",
    @PrimaryKey val timestamp: Long
)