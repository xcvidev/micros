package com.xcvi.micros.ui.destinations.food

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.xcvi.micros.domain.formatClean

@Composable
fun SummaryCard(
    calories: Int,
    protein: Double,
    carbs: Double,
    fats: Double,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,
    proteinLabel: String = "Protein",
    carbsLabel: String = "Carbohydrates",
    fatsLabel: String = "Fats"
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.Bottom

                ) {
                    Text(
                        text = proteinLabel,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                    Text(
                        text = " ${protein.formatClean()} g",
                        //style = MaterialTheme.typography.titleSmall,
                    )
                }
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.Bottom

                ) {
                    Text(
                        text = carbsLabel,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)

                    )
                    Text(
                        text = " ${carbs.formatClean()} g",
                        //style = MaterialTheme.typography.titleSmall,
                    )
                }
                Row(
                    verticalAlignment = androidx.compose.ui.Alignment.Bottom
                ) {
                    Text(
                        text = fatsLabel,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)

                    )
                    Text(
                        text = " ${fats.formatClean()} g",
                        //style = MaterialTheme.typography.titleSmall,
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.Bottom
            ){
                Text(
                    text = "$calories",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = " kcal",
                    //style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary

                )
            }
        }
    }
}