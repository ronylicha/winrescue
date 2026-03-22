package com.winrescue.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Build
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
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.winrescue.data.model.Difficulty
import com.winrescue.data.model.InputField
import com.winrescue.data.model.InputType
import com.winrescue.data.model.OsTarget
import com.winrescue.data.model.Script
import com.winrescue.data.model.ScriptCategory
import com.winrescue.data.model.ScriptStep
import com.winrescue.data.root.RootState
import com.winrescue.ui.components.DifficultyBadge
import com.winrescue.ui.components.WarningBanner
import com.winrescue.ui.navigation.Route
import com.winrescue.ui.theme.Primary
import com.winrescue.ui.theme.WinRescueTheme
import com.winrescue.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScriptDetailScreen(
    scriptId: String,
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val inputValues by viewModel.inputValues.collectAsState()
    val script = viewModel.getScriptById(scriptId)

    if (script == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        }
        return
    }

    ScriptDetailContent(
        script = script,
        rootState = uiState.rootState,
        inputValues = inputValues,
        onInputChanged = { fieldId, value -> viewModel.updateInput(fieldId, value) },
        onBackClick = { navController.popBackStack() },
        onLaunchClick = {
            navController.navigate(Route.Wizard.createRoute(scriptId, 1))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScriptDetailContent(
    script: Script,
    rootState: RootState,
    inputValues: Map<String, String>,
    onInputChanged: (String, String) -> Unit,
    onBackClick: () -> Unit,
    onLaunchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allRequiredFieldsFilled = script.inputFields
        .filter { it.required }
        .all { field ->
            val value = inputValues[field.id] ?: field.defaultValue ?: ""
            value.isNotBlank()
        }

    val canLaunch = allRequiredFieldsFilled && rootState.isHidReady

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = script.name,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 3.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Button(
                    onClick = onLaunchClick,
                    enabled = canLaunch,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.RocketLaunch,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Lancer le wizard",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header avec icone + metadata
            item(key = "header") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = getIconForScript(script.icon),
                        contentDescription = null,
                        tint = Primary,
                        modifier = Modifier.size(48.dp)
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = script.name,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Text(
                            text = script.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Windows ${script.os.joinToString("/") { osLabel(it) }}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Timer,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = "~${script.estimatedMinutes} min",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            DifficultyBadge(difficulty = script.difficulty)
                        }
                    }
                }
            }

            // Warning du script
            if (script.warningMessage != null) {
                item(key = "warning_script") {
                    WarningBanner(message = script.warningMessage)
                }
            }

            // Warning root/HID non pret
            if (!rootState.isHidReady) {
                item(key = "warning_root") {
                    WarningBanner(
                        message = "Root/HID non disponible. Vous ne pouvez pas executer ce script."
                    )
                }
            }

            // Prerequis
            item(key = "prerequisites") {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Prerequis",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    BulletPoint("Cable USB data (pas charge uniquement)")
                    BulletPoint("Android avec acces root")
                    BulletPoint("Gadget HID configure (/dev/hidg0)")

                    if (script.os.contains(OsTarget.WIN10) || script.os.contains(OsTarget.BOTH)) {
                        BulletPoint("Windows 10")
                    }
                    if (script.os.contains(OsTarget.WIN11) || script.os.contains(OsTarget.BOTH)) {
                        BulletPoint("Windows 11")
                    }
                }
            }

            // Apercu des etapes
            if (script.steps.isNotEmpty()) {
                item(key = "steps_preview") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Apercu des etapes",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        script.steps.forEachIndexed { index, step ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 2.dp)
                            ) {
                                StepNumber(index + 1)

                                Text(
                                    text = step.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            // Champs de saisie
            if (script.inputFields.isNotEmpty()) {
                item(key = "parameters") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Parametres",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        script.inputFields.forEach { field ->
                            ParameterField(
                                field = field,
                                value = inputValues[field.id] ?: field.defaultValue ?: "",
                                onValueChange = { onInputChanged(field.id, it) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ParameterField(
    field: InputField,
    value: String,
    onValueChange: (String) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    val isPassword = field.inputType == InputType.PASSWORD

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = if (field.required) "${field.label} *" else field.label
            )
        },
        placeholder = { Text(text = field.placeholder) },
        visualTransformation = if (isPassword && !passwordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) {
                            Icons.Default.VisibilityOff
                        } else {
                            Icons.Default.Visibility
                        },
                        contentDescription = if (passwordVisible) {
                            "Masquer le mot de passe"
                        } else {
                            "Afficher le mot de passe"
                        }
                    )
                }
            }
        } else {
            null
        },
        keyboardOptions = when (field.inputType) {
            InputType.NUMBER -> KeyboardOptions(keyboardType = KeyboardType.Number)
            InputType.LETTER -> KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Characters
            )
            InputType.PASSWORD -> KeyboardOptions(keyboardType = KeyboardType.Password)
            InputType.TEXT -> KeyboardOptions.Default
        },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}

@Composable
private fun BulletPoint(text: String) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "\u2022",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StepNumber(number: Int) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.size(28.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = "$number",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun osLabel(os: OsTarget): String = when (os) {
    OsTarget.WIN10 -> "10"
    OsTarget.WIN11 -> "11"
    OsTarget.ANDROID -> "Android"
    OsTarget.BOTH -> "10/11"
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

// region Previews

private val previewSteps = listOf(
    ScriptStep(
        id = 1,
        title = "Ouvrir l'invite de commandes",
        instruction = "Attendez que l'ecran de connexion Windows apparaisse"
    ),
    ScriptStep(
        id = 2,
        title = "Acceder au registre SAM",
        instruction = "Le script va ouvrir regedit et naviguer vers SAM"
    ),
    ScriptStep(
        id = 3,
        title = "Modifier le mot de passe",
        instruction = "Saisissez le nouveau mot de passe quand demande"
    ),
    ScriptStep(
        id = 4,
        title = "Redemarrer le PC",
        instruction = "Le PC va redemarrer avec le nouveau mot de passe"
    )
)

private val previewInputFields = listOf(
    InputField(
        id = "username",
        label = "Nom d'utilisateur",
        placeholder = "Ex: Administrateur",
        required = true,
        inputType = InputType.TEXT
    ),
    InputField(
        id = "new_password",
        label = "Nouveau mot de passe",
        placeholder = "Saisissez le nouveau mot de passe",
        required = true,
        inputType = InputType.PASSWORD
    ),
    InputField(
        id = "drive_letter",
        label = "Lettre du disque",
        placeholder = "C",
        defaultValue = "C",
        required = false,
        inputType = InputType.LETTER
    )
)

private val previewScript = Script(
    id = "reset_password",
    name = "Reset mot de passe",
    description = "Reinitialise le mot de passe d'un compte utilisateur Windows local via le registre SAM.",
    category = ScriptCategory.RECOVERY,
    os = listOf(OsTarget.WIN10, OsTarget.WIN11),
    difficulty = Difficulty.MEDIUM,
    estimatedMinutes = 5,
    requiresRoot = true,
    icon = "lock_reset",
    warningMessage = "Cette operation va modifier le registre Windows. Assurez-vous d'avoir une sauvegarde.",
    inputFields = previewInputFields,
    steps = previewSteps
)

@Preview(showBackground = true, backgroundColor = 0xFF0F172A, showSystemUi = true)
@Composable
private fun ScriptDetailReadyPreview() {
    WinRescueTheme {
        ScriptDetailContent(
            script = previewScript,
            rootState = RootState.Ready(),
            inputValues = mapOf(
                "username" to "Admin",
                "new_password" to "secret123"
            ),
            onInputChanged = { _, _ -> },
            onBackClick = {},
            onLaunchClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A, showSystemUi = true)
@Composable
private fun ScriptDetailNoRootPreview() {
    WinRescueTheme {
        ScriptDetailContent(
            script = previewScript,
            rootState = RootState.NoRoot,
            inputValues = emptyMap(),
            onInputChanged = { _, _ -> },
            onBackClick = {},
            onLaunchClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A, showSystemUi = true)
@Composable
private fun ScriptDetailNoFieldsPreview() {
    WinRescueTheme {
        ScriptDetailContent(
            script = previewScript.copy(
                inputFields = emptyList(),
                warningMessage = null
            ),
            rootState = RootState.Ready(),
            inputValues = emptyMap(),
            onInputChanged = { _, _ -> },
            onBackClick = {},
            onLaunchClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A, showSystemUi = true)
@Composable
private fun ScriptDetailEmptyFieldsPreview() {
    WinRescueTheme {
        ScriptDetailContent(
            script = previewScript,
            rootState = RootState.Ready(),
            inputValues = emptyMap(),
            onInputChanged = { _, _ -> },
            onBackClick = {},
            onLaunchClick = {}
        )
    }
}

// endregion
