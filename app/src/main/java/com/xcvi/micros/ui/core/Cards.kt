package com.xcvi.micros.ui.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay


@Composable
fun StreamingTextCard(
    title: String,
    subtitle: String,
    body: String,
    modifier: Modifier = Modifier,
    charDelayMillis: Long = 30L,
    onClick: () -> Unit,
    onFinished: (() -> Unit)? = null,
) {
    var visibleHeadline by remember { mutableStateOf("") }
    var visibleSubhead by remember { mutableStateOf("") }
    var visibleBody by remember { mutableStateOf("") }

    LaunchedEffect(title, subtitle, body) {
        visibleHeadline = ""
        for (i in title.indices) {
            visibleHeadline += title[i]
            delay(charDelayMillis)
        }
        visibleSubhead = ""
        for (i in subtitle.indices) {
            visibleSubhead += subtitle[i]
            delay(charDelayMillis)
        }
        visibleBody = ""
        for (i in body.indices) {
            visibleBody += body[i]
            delay(charDelayMillis)
        }
        onFinished?.invoke()
    }

    OutlinedCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .padding(top = 12.dp, start = 18.dp, end = 18.dp, bottom = 24.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = visibleHeadline,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = visibleSubhead,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = visibleBody,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}



@Composable
fun M3CardWithMedia(
    modifier: Modifier = Modifier,
    headline: String = "Display small",
    subhead: String = "Subhead",
    supportingText: String = "Explain more about the topic in the display and subhead through supporting text.",
    onActionClick: () -> Unit = {}
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = headline,
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subhead,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = supportingText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Placeholder for media
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Action Button
            Button(
                onClick = onActionClick,
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Action")
            }
        }
    }
}

