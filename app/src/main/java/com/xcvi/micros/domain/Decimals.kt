package com.xcvi.micros.domain

import android.health.connect.datatypes.units.Percentage
import java.util.Locale
import kotlin.math.roundToInt

fun List<Double?>.normalize(scale: Double = 0.0): List<Double?> {
    if(this.isEmpty()) return this
    val min = this.filterNotNull().minOf { it }
    return this.map {
        if (it == null) {
            null
        }
        else{
            it - min + min*scale
        }
    }
}

fun Double.nextAmount(): Double {
    return try{
        if (this <= 0.0) {
            0.0
        } else {
            val rounded = this.roundToInt()
            ((rounded / 50) + 1) * 50.0
        }
    } catch (e: Exception) {
        return 0.0
    }
}

fun Double.previousAmount(): Double {
    return try{
        if (this <= 50) {
            0.0
        } else {
            val rounded = this.roundToInt()
            ((rounded - 1) / 50) * 50.0
        }
    } catch (e: Exception) {
        0.0
    }
}

fun Double.formatClean(): String {
    try{
        if (this == 0.0) return "0"
        val formatted =
            String.format(Locale.getDefault(), "%.1f", this).removeSuffix(".0").removeSuffix(",0")
        return formatted
    } catch (e: Exception) {
        return "0"
    }
}
fun Double.roundDecimals(): Double {
    try{
        val rounded = (this * 10).roundToInt() / 10.0
        return rounded
    } catch (e: Exception) {
        return 0.0
    }
}












