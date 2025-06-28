package com.xcvi.micros.ui.destination.food

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column{
                Text(
                    text = "Protein: ${protein.formatClean()} g",
                    style = MaterialTheme.typography.titleSmall,
                )

                Text(
                    text = "Carbs: ${carbs.formatClean()} g",
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    text = "Fats: ${fats.formatClean()} g",
                    style = MaterialTheme.typography.titleSmall,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$calories kcal",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary

            )
        }
    }
}