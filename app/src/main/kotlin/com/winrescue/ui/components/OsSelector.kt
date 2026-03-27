package com.winrescue.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.winrescue.data.model.OsTarget
import com.winrescue.ui.theme.WinRescueTheme

@Composable
fun OsSelector(
    selectedOs: OsTarget,
    onOsSelected: (OsTarget) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OsFilterChip(
            label = "Windows 10",
            selected = selectedOs == OsTarget.WIN10,
            onClick = { onOsSelected(OsTarget.WIN10) }
        )

        OsFilterChip(
            label = "Windows 11",
            selected = selectedOs == OsTarget.WIN11,
            onClick = { onOsSelected(OsTarget.WIN11) }
        )
    }
}

@Composable
private fun OsFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.DesktopWindows,
                contentDescription = null
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            iconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = MaterialTheme.colorScheme.outline,
            selectedBorderColor = MaterialTheme.colorScheme.primary,
            enabled = true,
            selected = selected
        ),
        modifier = Modifier.height(48.dp)
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF080C18)
@Composable
private fun OsSelectorWin10Preview() {
    WinRescueTheme {
        OsSelector(
            selectedOs = OsTarget.WIN10,
            onOsSelected = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080C18)
@Composable
private fun OsSelectorWin11Preview() {
    WinRescueTheme {
        OsSelector(
            selectedOs = OsTarget.WIN11,
            onOsSelected = {}
        )
    }
}
