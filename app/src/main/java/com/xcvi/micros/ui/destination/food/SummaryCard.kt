package com.xcvi.micros.ui.destination.food

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.xcvi.micros.domain.Portion
import com.xcvi.micros.ui.core.formatClean

@Composable
fun SummaryCard(
    calories: Int,
    protein: Double,
    carbs: Double,
    fats: Double,
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = {
            Column {
                Text(text = "Protein: ${protein.formatClean()} g")
                Text(text = "Carbs: ${carbs.formatClean()} g")
                Text(text = "Fats: ${fats.formatClean()} g")
            }

        },
        trailingContent = {
            Text(
                text = "$calories kcal",
                fontSize = MaterialTheme.typography.titleLarge.fontSize
            )
        }
    )
}