package com.xcvi.micros.ui.destinations.food.dashboard

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.xcvi.micros.data.entity.AminoAcids
import com.xcvi.micros.data.entity.Macros
import com.xcvi.micros.data.entity.Minerals
import com.xcvi.micros.data.entity.Vitamins
import com.xcvi.micros.data.entity.toLabeledPairs


@Composable
fun MicrosSection(
    modifier: Modifier = Modifier,
    macros: Macros = Macros(),
    minerals: Minerals = Minerals(),
    vitamins: Vitamins = Vitamins(),
    aminoAcids: AminoAcids = AminoAcids(),
    aminoTitle: String ,
    macroTitle: String ,
    mineralTitle: String,
    vitaminTitle: String ,
) {
    val context = LocalContext.current
    val macrosLabeled = macros.toLabeledPairs(context).drop(1)
    val mineralsLabeled = minerals.toLabeledPairs(context)
    val vitaminsLabeled = vitamins.toLabeledPairs(context)
    val aminoAcidsLabeled = aminoAcids.toLabeledPairs(context)

    Column(
        modifier = modifier
    ) {
        Text(
            text = macroTitle,
            fontSize = MaterialTheme.typography.bodySmall.fontSize,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.padding(bottom = 6.dp, top = 24.dp)
        )
        macrosLabeled.forEach{  label ->
            TextListItem(
                text = label.first,
                value = label.second,
            )
        }


        Text(
            text = mineralTitle,
            fontSize = MaterialTheme.typography.bodySmall.fontSize,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.padding(bottom = 6.dp, top = 24.dp)
        )
        mineralsLabeled.forEach { mineral ->
            TextListItem(
                text = mineral.first,
                value = mineral.second,
            )
        }

        Text(
            text = vitaminTitle,
            fontSize = MaterialTheme.typography.bodySmall.fontSize,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.padding(bottom = 6.dp, top = 24.dp)
        )
        vitaminsLabeled.forEach { vitamin ->
            TextListItem(
                text = vitamin.first,
                value = vitamin.second,
            )
        }

        Text(
            text = aminoTitle,
            fontSize = MaterialTheme.typography.bodySmall.fontSize,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            modifier = Modifier.padding(bottom = 6.dp, top = 24.dp)
        )
        aminoAcidsLabeled.forEach { aminoAcid ->
            TextListItem(
                text = aminoAcid.first,
                value = aminoAcid.second,
            )
        }

    }
}

@Composable
fun TextListItem(
    text: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ){
        Row(
            modifier =Modifier.padding(vertical = 4.dp)
        ) {
            Text(
                text = text,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = value,
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontWeight = MaterialTheme.typography.bodyLarge.fontWeight,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
        }
        HorizontalDivider(thickness = 0.3.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f))
    }
}