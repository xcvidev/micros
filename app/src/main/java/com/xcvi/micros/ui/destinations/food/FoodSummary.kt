package com.xcvi.micros.ui.destinations.food

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xcvi.micros.domain.formatClean
import kotlin.math.roundToInt


@Composable
fun FoodSummary(
    calories: Int,
    protein: Double,
    carbs: Double,
    fats: Double,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f),
    proteinLabel: String ,
    carbsLabel: String ,
    fatsLabel: String
) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.background(backgroundColor)
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                MacroLabel(
                    color = MaterialTheme.colorScheme.primary,
                    label = proteinLabel,
                    amount = protein
                )
                MacroLabel(
                    color = MaterialTheme.colorScheme.secondary,
                    label = carbsLabel,
                    amount = carbs
                )
                MacroLabel(
                    color = MaterialTheme.colorScheme.tertiary,
                    label = fatsLabel,
                    amount = fats
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "$calories",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "kcal",
                    color = MaterialTheme.colorScheme.primary

                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        MacroBar(
            proteinColor = MaterialTheme.colorScheme.primary,
            carbsColor = MaterialTheme.colorScheme.secondary,
            fatsColor = MaterialTheme.colorScheme.tertiary,
            protein = protein,
            carbs = carbs,
            fats = fats,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun MacroBar(
    modifier: Modifier = Modifier,
    protein: Double,
    carbs: Double,
    fats: Double,
    proteinColor: Color,
    carbsColor: Color,
    fatsColor: Color,
    barHeight: Dp = 8.dp,
) {

    val total = protein + carbs + fats
    val safeTotal = if (total <= 0f) 1f else total.toFloat()

    val proteinPercent = protein.toFloat() / safeTotal
    val carbsPercent = carbs.toFloat() / safeTotal
    val fatsPercent = fats.toFloat() / safeTotal

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Bar
        Row(
            modifier = Modifier
                .fillMaxWidth().padding(horizontal = 8.dp)
                .height(barHeight)
                .clip(RoundedCornerShape(50))
        ) {
            if (proteinPercent > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(proteinPercent)
                        .background(proteinColor)
                )
            }
            if (carbsPercent > 0) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(carbsPercent)
                        .background(carbsColor)
                )
            }
            if (fatsPercent > 0) {

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(fatsPercent)
                        .background(fatsColor)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).height(barHeight+20.dp),
        ) {
            if(proteinPercent > 0){
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(proteinPercent)
                ) {
                    Text(
                        text = "${(proteinPercent * 100).roundToInt()}%",
                        fontSize = 12.sp,
                        color = proteinColor,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }
            if(carbsPercent>0){
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(carbsPercent)
                ) {
                    Text(
                        text = "${(carbsPercent * 100).roundToInt()}%",
                        fontSize = 12.sp,
                        color = carbsColor,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }
            if(fatsPercent>0){
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(fatsPercent)
                ) {
                    Text(
                        text = "${(fatsPercent * 100).roundToInt()}%",
                        fontSize = 12.sp,
                        color = fatsColor,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,)
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
    }
}


@Composable
fun FoodSummaryCard(
    calories: Int,
    protein: Double,
    carbs: Double,
    fats: Double,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Transparent,
    proteinLabel: String ,
    carbsLabel: String ,
    fatsLabel: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                MacroLabel(
                    color = MaterialTheme.colorScheme.primary,
                    label = proteinLabel,
                    amount = protein
                )
                MacroLabel(
                    color = MaterialTheme.colorScheme.secondary,
                    label = carbsLabel,
                    amount = carbs
                )
                MacroLabel(
                    color = MaterialTheme.colorScheme.tertiary,
                    label = fatsLabel,
                    amount = fats
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "$calories",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "kcal",
                    color = MaterialTheme.colorScheme.primary

                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        MacroBar(
            proteinColor = MaterialTheme.colorScheme.primary,
            carbsColor = MaterialTheme.colorScheme.secondary,
            fatsColor = MaterialTheme.colorScheme.tertiary,
            protein = protein,
            carbs = carbs,
            fats = fats,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Composable
fun MacroLabel(
    color: Color,
    label: String,
    amount: Double,
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(6.dp)
                    .background(color)

            )
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
        Text(
            text = "${amount.formatClean()} g",
            fontWeight = FontWeight.SemiBold
        )
    }
}


/*
Row(
    modifier = Modifier.width(barWidth),
    horizontalArrangement = Arrangement.SpaceBetween
) {
    Text(
        text = "${(proteinPercent * 100).roundToInt()}% $proteinLabel",
        fontSize = 12.sp,
        color = proteinColor
    )
    Text(
        text = "${(carbsPercent * 100).roundToInt()}% $carbsLabel",
        fontSize = 12.sp,
        color = carbsColor
    )
    Text(
        text = "${(fatsPercent * 100).roundToInt()}% $fatsLabel",
        fontSize = 12.sp,
        color = fatsColor
    )
}

 */











