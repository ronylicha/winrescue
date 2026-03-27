package com.winrescue.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.winrescue.ui.theme.JetBrainsMono
import com.winrescue.ui.theme.WinRescueTheme

@Composable
fun CountdownTimer(
    totalMs: Long,
    remainingMs: Long,
    label: String,
    modifier: Modifier = Modifier
) {
    val progress = if (totalMs > 0) remainingMs.toFloat() / totalMs.toFloat() else 0f
    val remainingSeconds = (remainingMs / 1000).coerceAtLeast(0)

    val displayText = if (remainingSeconds >= 60) {
        val minutes = remainingSeconds / 60
        val seconds = remainingSeconds % 60
        "%d:%02d".format(minutes, seconds)
    } else {
        "${remainingSeconds}s"
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(120.dp)
        ) {
            CircularProgressIndicator(
                progress = { 1f },
                modifier = Modifier.size(120.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                strokeWidth = 6.dp,
                strokeCap = StrokeCap.Round
            )

            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(120.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 6.dp,
                strokeCap = StrokeCap.Round
            )

            Text(
                text = displayText,
                fontFamily = JetBrainsMono,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080C18)
@Composable
private fun CountdownTimerFullPreview() {
    WinRescueTheme {
        CountdownTimer(
            totalMs = 10000,
            remainingMs = 10000,
            label = "Attente du red\u00e9marrage",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080C18)
@Composable
private fun CountdownTimerHalfPreview() {
    WinRescueTheme {
        CountdownTimer(
            totalMs = 10000,
            remainingMs = 5000,
            label = "Chargement en cours",
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080C18)
@Composable
private fun CountdownTimerLongPreview() {
    WinRescueTheme {
        CountdownTimer(
            totalMs = 120000,
            remainingMs = 75000,
            label = "D\u00e9marrage Windows",
            modifier = Modifier.padding(16.dp)
        )
    }
}
