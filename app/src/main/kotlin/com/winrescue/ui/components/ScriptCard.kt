package com.winrescue.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.winrescue.data.model.Difficulty
import com.winrescue.data.model.OsTarget
import com.winrescue.data.model.Script
import com.winrescue.data.model.ScriptCategory
import com.winrescue.ui.theme.WinRescueTheme

@Composable
fun ScriptCard(
    script: Script,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = getIconForScript(script.icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = script.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = script.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DifficultyBadge(difficulty = script.difficulty)

                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )

                    Text(
                        text = "${script.estimatedMinutes} min",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

private fun getIconForScript(iconName: String): ImageVector = when (iconName) {
    "lock_reset" -> Icons.Default.LockReset
    "person_add" -> Icons.Default.PersonAdd
    "admin_panel_settings" -> Icons.Default.AdminPanelSettings
    "build" -> Icons.Default.Build
    "folder_copy" -> Icons.Default.FileCopy
    "lock_open" -> Icons.Default.LockOpen
    "desktop_windows" -> Icons.Default.DesktopWindows
    "security" -> Icons.Default.Security
    "pin" -> Icons.Default.Pin
    "restart_alt" -> Icons.Default.RestartAlt
    "healing" -> Icons.Default.Healing
    "dns" -> Icons.Default.Dns
    "wifi" -> Icons.Default.Wifi
    "bug_report" -> Icons.Default.BugReport
    "description" -> Icons.Default.Description
    "memory" -> Icons.Default.Memory
    "people" -> Icons.Default.People
    "person" -> Icons.Default.Person
    "shield" -> Icons.Default.Shield
    "storage" -> Icons.Default.Storage
    else -> Icons.Default.Code
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun ScriptCardPreview() {
    WinRescueTheme {
        ScriptCard(
            script = Script(
                id = "reset_password",
                name = "Reset mot de passe",
                description = "R\u00e9initialise le mot de passe d'un compte utilisateur Windows local via le registre SAM.",
                category = ScriptCategory.RECOVERY,
                os = listOf(OsTarget.WIN10, OsTarget.WIN11),
                difficulty = Difficulty.MEDIUM,
                estimatedMinutes = 5,
                requiresRoot = true,
                icon = "lock_reset",
                warningMessage = null,
                inputFields = emptyList(),
                steps = emptyList()
            ),
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun ScriptCardAdvancedPreview() {
    WinRescueTheme {
        ScriptCard(
            script = Script(
                id = "enable_admin",
                name = "Activer compte Admin",
                description = "Active le compte administrateur int\u00e9gr\u00e9 de Windows qui est d\u00e9sactiv\u00e9 par d\u00e9faut.",
                category = ScriptCategory.ADMIN,
                os = listOf(OsTarget.WIN10),
                difficulty = Difficulty.ADVANCED,
                estimatedMinutes = 8,
                requiresRoot = true,
                icon = "admin_panel_settings",
                warningMessage = "Op\u00e9ration sensible",
                inputFields = emptyList(),
                steps = emptyList()
            ),
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
