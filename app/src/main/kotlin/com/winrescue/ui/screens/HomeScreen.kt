package com.winrescue.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.winrescue.data.model.Difficulty
import com.winrescue.data.model.OsTarget
import com.winrescue.data.model.Script
import com.winrescue.data.model.ScriptCategory
import com.winrescue.data.root.RootState
import com.winrescue.ui.components.ScriptCard
import com.winrescue.ui.components.UsbStatusBar
import com.winrescue.ui.components.WarningBanner
import com.winrescue.ui.navigation.Route
import com.winrescue.ui.theme.DifficultyAdvanced
import com.winrescue.ui.theme.DifficultyEasy
import com.winrescue.ui.theme.DifficultyMedium
import com.winrescue.ui.viewmodel.HomeViewModel
import com.winrescue.usb.UsbConnectionState

data class HomeUiState(
    val selectedOs: OsTarget = OsTarget.WIN10,
    val scripts: List<Script> = emptyList(),
    val usbState: UsbConnectionState = UsbConnectionState.DISCONNECTED,
    val rootState: RootState = RootState.Unchecked,
    val isLoading: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val showDisclaimer by viewModel.showDisclaimer.collectAsState()

    if (showDisclaimer) {
        DisclaimerDialog(onAccept = { viewModel.acceptDisclaimer() })
    }

    HomeScreenContent(
        uiState = uiState,
        onOsSelected = { viewModel.selectOs(it) },
        onScriptClick = { scriptId ->
            navController.navigate(Route.ScriptDetail.createRoute(scriptId))
        },
        onSettingsClick = { navController.navigate(Route.Settings.route) }
    )
}

@Composable
private fun DisclaimerDialog(onAccept: () -> Unit) {
    AlertDialog(
        onDismissRequest = { },
        confirmButton = {
            Button(onClick = onAccept) { Text("J'accepte") }
        },
        icon = {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
        },
        title = { Text("Usage legal uniquement", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column {
                Text(
                    "WinRescue est concu pour la recuperation de vos propres systemes " +
                    "ou de systemes dont vous avez l'autorisation explicite d'intervenir.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "L'utilisation de cet outil pour acceder a des systemes sans " +
                    "autorisation est illegale et punissable par la loi.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "L'auteur decline toute responsabilite pour tout usage illicite.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onOsSelected: (OsTarget) -> Unit,
    onScriptClick: (String) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Dashboard", "Windows 10", "Windows 11", "Android", "Linux")

    val filteredScripts = uiState.scripts.filter { script ->
        if (searchQuery.isBlank()) true
        else {
            script.name.contains(searchQuery, ignoreCase = true) ||
            script.description.contains(searchQuery, ignoreCase = true) ||
            script.category.name.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("WinRescue", style = MaterialTheme.typography.titleLarge) },
                    actions = {
                        IconButton(onClick = onSettingsClick, modifier = Modifier.size(48.dp)) {
                            Icon(Icons.Default.Settings, "Parametres", tint = MaterialTheme.colorScheme.onSurface)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
                UsbStatusBar(connectionState = uiState.usbState)
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Root warnings
            AnimatedVisibility(
                visible = uiState.rootState is RootState.NoRoot || uiState.rootState is RootState.RootDenied,
                enter = fadeIn(), exit = fadeOut()
            ) {
                WarningBanner("Root non disponible. Les fonctions HID sont desactivees.", Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
            }
            AnimatedVisibility(
                visible = uiState.rootState is RootState.RootOnlyNoHid,
                enter = fadeIn(), exit = fadeOut()
            ) {
                WarningBanner("Root OK mais /dev/hidg0 non trouve. Configurez le gadget USB HID.", Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
            }

            // Tabs: Dashboard | Win 10 | Win 11 | Linux
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    if (selectedTabIndex < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    val isLinux = index == 4
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            if (!isLinux) {
                                selectedTabIndex = index
                                when (index) {
                                    1 -> onOsSelected(OsTarget.WIN10)
                                    2 -> onOsSelected(OsTarget.WIN11)
                                    3 -> onOsSelected(OsTarget.ANDROID)
                                }
                            }
                        },
                        enabled = !isLinux,
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    title,
                                    fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isLinux) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                                            else if (selectedTabIndex == index) MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (isLinux) {
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        "Soon",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .background(
                                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    )
                }
            }

            // Content based on selected tab
            when (selectedTabIndex) {
                0 -> {
                    // Dashboard tab
                    DashboardContent(
                        allScripts = uiState.scripts,
                        rootState = uiState.rootState,
                        usbState = uiState.usbState
                    )
                }
                1, 2, 3 -> {
                    // Win10 / Win11 / Android script list
                    ScriptListContent(
                        scripts = filteredScripts,
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it },
                        onScriptClick = onScriptClick,
                        isLoading = uiState.isLoading
                    )
                }
            }
        }
    }
}

@Composable
private fun DashboardContent(
    allScripts: List<Script>,
    rootState: RootState,
    usbState: UsbConnectionState
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Tableau de bord", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)
        }

        // KPI row
        item {
            KpiDashboard(allScripts = allScripts, rootState = rootState, usbState = usbState)
        }

        // Category breakdown
        item {
            Text("Par categorie", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
        }
        val categories = allScripts.groupBy { it.category }
        items(categories.entries.toList()) { (category, scripts) ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = when (category) {
                                ScriptCategory.RECOVERY -> "Recuperation"
                                ScriptCategory.ADMIN -> "Administration"
                                ScriptCategory.REPAIR -> "Reparation"
                                ScriptCategory.SECURITY -> "Securite"
                                ScriptCategory.NETWORK -> "Reseau"
                                ScriptCategory.DIAGNOSTIC -> "Diagnostic"
                            },
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            "${scripts.size} script${if (scripts.size > 1) "s" else ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    // Difficulty distribution
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        val easy = scripts.count { it.difficulty == Difficulty.EASY }
                        val medium = scripts.count { it.difficulty == Difficulty.MEDIUM }
                        val advanced = scripts.count { it.difficulty == Difficulty.ADVANCED }
                        if (easy > 0) DifficultyDot(easy, DifficultyEasy)
                        if (medium > 0) DifficultyDot(medium, DifficultyMedium)
                        if (advanced > 0) DifficultyDot(advanced, DifficultyAdvanced)
                    }
                }
            }
        }

        // OS distribution
        item {
            Spacer(Modifier.height(8.dp))
            Text("Par systeme", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onBackground)
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val win10Count = allScripts.count { it.os.contains(OsTarget.WIN10) || it.os.contains(OsTarget.BOTH) }
                val win11Count = allScripts.count { it.os.contains(OsTarget.WIN11) || it.os.contains(OsTarget.BOTH) }
                KpiCard(
                    title = "Windows 10",
                    value = "$win10Count",
                    icon = Icons.Default.Storage,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                KpiCard(
                    title = "Windows 11",
                    value = "$win11Count",
                    icon = Icons.Default.Storage,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DifficultyDot(count: Int, color: androidx.compose.ui.graphics.Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("$count", style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ScriptListContent(
    scripts: List<Script>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onScriptClick: (String) -> Unit,
    isLoading: Boolean
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Rechercher un script...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Effacer")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // Script count
        Text(
            text = "${scripts.size} script${if (scripts.size > 1) "s" else ""} disponible${if (scripts.size > 1) "s" else ""}",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        when {
            isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, modifier = Modifier.size(48.dp))
                }
            }
            scripts.isEmpty() -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (searchQuery.isNotEmpty()) "Aucun script ne correspond a \"$searchQuery\""
                               else "Aucun script disponible pour cet OS.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(32.dp)
                    )
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = scripts, key = { it.id }) { script ->
                        ScriptCard(script = script, onClick = { onScriptClick(script.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun KpiDashboard(
    allScripts: List<Script>,
    rootState: RootState,
    usbState: UsbConnectionState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        KpiCard(
            title = "Scripts",
            value = "${allScripts.size}",
            icon = Icons.Default.Storage,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
        KpiCard(
            title = "Root",
            value = when (rootState) {
                is RootState.Ready -> "OK"
                is RootState.RootOnlyNoHid -> "Partiel"
                is RootState.Checking -> "..."
                else -> "Non"
            },
            icon = Icons.Default.Security,
            color = when (rootState) {
                is RootState.Ready -> DifficultyEasy
                is RootState.RootOnlyNoHid -> DifficultyMedium
                else -> DifficultyAdvanced
            },
            modifier = Modifier.weight(1f)
        )
        KpiCard(
            title = "USB",
            value = when (usbState) {
                UsbConnectionState.CONNECTED_HID_READY -> "Pret"
                UsbConnectionState.CONNECTED_NO_HID -> "Partiel"
                UsbConnectionState.DISCONNECTED -> "Non"
            },
            icon = Icons.Default.Wifi,
            color = when (usbState) {
                UsbConnectionState.CONNECTED_HID_READY -> DifficultyEasy
                UsbConnectionState.CONNECTED_NO_HID -> DifficultyMedium
                UsbConnectionState.DISCONNECTED -> DifficultyAdvanced
            },
            modifier = Modifier.weight(1f)
        )
        KpiCard(
            title = "Categories",
            value = "${allScripts.map { it.category }.distinct().size}",
            icon = Icons.Default.Build,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
            Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
