package com.winrescue.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.winrescue.ui.theme.Warning as WarningColor
import com.winrescue.ui.theme.WinRescueTheme

@Composable
fun WarningBanner(
    message: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = WarningColor.copy(alpha = 0.10f)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Avertissement",
                tint = WarningColor,
                modifier = Modifier.size(24.dp)
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = WarningColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun WarningBannerPreview() {
    WinRescueTheme {
        WarningBanner(
            message = "Cette op\u00e9ration va modifier le registre Windows. " +
                "Assurez-vous d'avoir une sauvegarde avant de continuer.",
            modifier = Modifier.padding(16.dp)
        )
    }
}
