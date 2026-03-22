package com.winrescue.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import com.winrescue.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.winrescue.data.settings.AppSettings
import com.winrescue.ui.viewmodel.HidTestResult
import com.winrescue.ui.viewmodel.SettingsViewModel
import com.winrescue.usb.HidKeyMap.KeyboardLayout
import com.winrescue.ui.theme.WinRescueTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavHostController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState()
    val hidTestResult by viewModel.hidTestResult.collectAsState()
    val isTestingHid by viewModel.isTestingHid.collectAsState()

    SettingsScreenContent(
        settings = settings,
        hidTestResult = hidTestResult,
        isTestingHid = isTestingHid,
        onBackClick = { navController.popBackStack() },
        onLayoutSelected = { viewModel.updateKeyboardLayout(it) },
        onCharDelayChanged = { viewModel.updateCharDelay(it) },
        onStepDelayChanged = { viewModel.updateStepDelay(it) },
        onPreviewBeforeSendChanged = { viewModel.updatePreviewBeforeSend(it) },
        onDebugModeChanged = { viewModel.updateDebugMode(it) },
        onTestHidConnection = { viewModel.testHidConnection() },
        onLanguageSelected = { viewModel.updateLanguage(it) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreenContent(
    settings: AppSettings,
    hidTestResult: HidTestResult?,
    isTestingHid: Boolean,
    onBackClick: () -> Unit,
    onLayoutSelected: (KeyboardLayout) -> Unit,
    onCharDelayChanged: (Long) -> Unit,
    onStepDelayChanged: (Long) -> Unit,
    onPreviewBeforeSendChanged: (Boolean) -> Unit,
    onDebugModeChanged: (Boolean) -> Unit,
    onTestHidConnection: () -> Unit,
    onLanguageSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Parametres",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // ---- Section USB HID ----
            item {
                SectionHeader(title = "USB HID")
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Chemin peripherique",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = settings.hidDevicePath,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.Monospace
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onTestHidConnection,
                        enabled = !isTestingHid,
                        modifier = Modifier.height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        if (isTestingHid) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(text = "Tester la connexion")
                        }
                    }

                    if (hidTestResult != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (hidTestResult.success) Icons.Default.Check else Icons.Default.Close,
                                contentDescription = if (hidTestResult.success) "Succes" else "Echec",
                                tint = if (hidTestResult.success) {
                                    MaterialTheme.colorScheme.tertiary
                                } else {
                                    MaterialTheme.colorScheme.error
                                },
                                modifier = Modifier.size(20.dp)
                            )

                            Spacer(modifier = Modifier.width(4.dp))

                            Text(
                                text = hidTestResult.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (hidTestResult.success) {
                                    MaterialTheme.colorScheme.tertiary
                                } else {
                                    MaterialTheme.colorScheme.error
                                }
                            )
                        }
                    }
                }
            }

            item {
                SectionDivider()
            }

            // ---- Section Clavier cible ----
            item {
                SectionHeader(title = "Clavier cible")
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "Layout",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FilterChip(
                            selected = settings.keyboardLayout == KeyboardLayout.QWERTY_US,
                            onClick = { onLayoutSelected(KeyboardLayout.QWERTY_US) },
                            label = { Text(text = "QWERTY US") },
                            modifier = Modifier.height(48.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )

                        FilterChip(
                            selected = settings.keyboardLayout == KeyboardLayout.AZERTY_FR,
                            onClick = { onLayoutSelected(KeyboardLayout.AZERTY_FR) },
                            label = { Text(text = "AZERTY FR") },
                            modifier = Modifier.height(48.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            item {
                SectionDivider()
            }

            // ---- Section Timing ----
            item {
                SectionHeader(title = "Timing")
            }

            item {
                SliderSetting(
                    label = "Delai entre frappes",
                    value = settings.charDelayMs.toFloat(),
                    valueRange = 10f..200f,
                    steps = 18,
                    valueLabel = "${settings.charDelayMs} ms",
                    onValueChange = { onCharDelayChanged(it.toLong()) }
                )
            }

            item {
                SliderSetting(
                    label = "Delai inter-steps",
                    value = settings.stepDelayMs.toFloat(),
                    valueRange = 500f..5000f,
                    steps = 8,
                    valueLabel = "${settings.stepDelayMs} ms",
                    onValueChange = { onStepDelayChanged(it.toLong()) }
                )
            }

            item {
                SectionDivider()
            }

            // ---- Section Affichage ----
            item {
                SectionHeader(title = "Affichage")
            }

            item {
                SwitchSetting(
                    label = "Apercu commandes avant envoi",
                    checked = settings.previewBeforeSend,
                    onCheckedChange = onPreviewBeforeSendChanged
                )
            }

            item {
                SwitchSetting(
                    label = "Mode debug (log frappes)",
                    checked = settings.debugMode,
                    onCheckedChange = onDebugModeChanged
                )
            }

            item {
                SectionDivider()
            }

            // ---- Section Langue ----
            item {
                SectionHeader(title = stringResource(R.string.language_section))
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FilterChip(
                            selected = settings.language == "auto",
                            onClick = { onLanguageSelected("auto") },
                            label = { Text(stringResource(R.string.language_auto)) },
                            modifier = Modifier.height(48.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )

                        FilterChip(
                            selected = settings.language == "fr",
                            onClick = { onLanguageSelected("fr") },
                            label = { Text(stringResource(R.string.language_french)) },
                            modifier = Modifier.height(48.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )

                        FilterChip(
                            selected = settings.language == "en",
                            onClick = { onLanguageSelected("en") },
                            label = { Text(stringResource(R.string.language_english)) },
                            modifier = Modifier.height(48.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
            }

            item {
                SectionDivider()
            }

            // ---- Section A propos ----
            item {
                SectionHeader(title = stringResource(R.string.about_section))
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.version_label),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stringResource(R.string.personal_use_only),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Licence GPL-3.0",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Copyright (c) 2026 Rony Licha\nThis program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License v3.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 8.dp)
    )
}

@Composable
private fun SectionDivider(
    modifier: Modifier = Modifier
) {
    HorizontalDivider(
        modifier = modifier.padding(vertical = 8.dp),
        color = MaterialTheme.colorScheme.outline
    )
}

@Composable
private fun SliderSetting(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    valueLabel: String,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = valueLabel,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Composable
private fun SwitchSetting(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(48.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

// region Previews

@Preview(showBackground = true, backgroundColor = 0xFF0F172A, showSystemUi = true)
@Composable
private fun SettingsScreenPreview() {
    WinRescueTheme {
        SettingsScreenContent(
            settings = AppSettings(),
            hidTestResult = null,
            isTestingHid = false,
            onBackClick = {},
            onLayoutSelected = {},
            onCharDelayChanged = {},
            onStepDelayChanged = {},
            onPreviewBeforeSendChanged = {},
            onDebugModeChanged = {},
            onTestHidConnection = {},
            onLanguageSelected = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A, showSystemUi = true)
@Composable
private fun SettingsScreenTestSuccessPreview() {
    WinRescueTheme {
        SettingsScreenContent(
            settings = AppSettings(
                keyboardLayout = KeyboardLayout.AZERTY_FR,
                charDelayMs = 80L,
                stepDelayMs = 2000L,
                previewBeforeSend = false,
                debugMode = true
            ),
            hidTestResult = HidTestResult(success = true, message = "Connexion reussie"),
            isTestingHid = false,
            onBackClick = {},
            onLayoutSelected = {},
            onCharDelayChanged = {},
            onStepDelayChanged = {},
            onPreviewBeforeSendChanged = {},
            onDebugModeChanged = {},
            onTestHidConnection = {},
            onLanguageSelected = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A, showSystemUi = true)
@Composable
private fun SettingsScreenTestFailedPreview() {
    WinRescueTheme {
        SettingsScreenContent(
            settings = AppSettings(),
            hidTestResult = HidTestResult(success = false, message = "Peripherique HID non disponible"),
            isTestingHid = false,
            onBackClick = {},
            onLayoutSelected = {},
            onCharDelayChanged = {},
            onStepDelayChanged = {},
            onPreviewBeforeSendChanged = {},
            onDebugModeChanged = {},
            onTestHidConnection = {},
            onLanguageSelected = {}
        )
    }
}

// endregion
