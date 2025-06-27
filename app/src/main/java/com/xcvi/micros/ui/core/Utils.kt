package com.xcvi.micros.ui.core

import android.content.Context
import java.util.Locale
import kotlin.math.roundToInt

fun getDisplayName(name: String, brands: String): String {
    return if (brands.isEmpty() || brands.isBlank()) name else "$name ($brands)"
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











