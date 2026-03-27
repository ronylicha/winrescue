package com.winrescue.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.winrescue.ui.components.HintImage
import com.winrescue.ui.components.KeySequencePreview
import com.winrescue.ui.components.StepIndicator
import com.winrescue.ui.components.WarningBanner
import com.winrescue.ui.navigation.Route
import com.winrescue.ui.theme.JetBrainsMono
import com.winrescue.ui.viewmodel.StepState
import com.winrescue.ui.viewmodel.WizardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WizardStepScreen(
    scriptId: String,
    stepId: Int,
    navController: NavHostController,
    viewModel: WizardViewModel = hiltViewModel()
) {
    val wizardState by viewModel.state.collectAsState()
    var showAbandonDialog by remember { mutableStateOf(false) }

    // Navigation reactive selon l'etat
    LaunchedEffect(wizardState.stepState, wizardState.currentStepIndex) {
        when (wizardState.stepState) {
            is StepState.Success -> {
                navController.navigate(Route.Success.createRoute(scriptId)) {
                    popUpTo(Route.Home.route) { inclusive = false }
                }
            }
            is StepState.Error -> {
                val step = wizardState.currentStep
                if (step != null && step.criticalStep) {
                    navController.navigate(
                        Route.Error.createRoute(scriptId, wizardState.currentStepIndex + 1)
                    ) {
                        popUpTo(Route.Home.route) { inclusive = false }
                    }
                }
            }
            else -> { /* pas de navigation automatique */ }
        }
    }

    // Dialog abandon
    if (showAbandonDialog) {
        AbandonDialog(
            onConfirm = {
                showAbandonDialog = false
                navController.popBackStack(Route.Home.route, inclusive = false)
            },
            onDismiss = { showAbandonDialog = false }
        )
    }

    val totalSteps = wizardState.totalSteps
    val currentStep = wizardState.currentStep
    val currentStepIndex = wizardState.currentStepIndex

    when (wizardState.stepState) {
        is StepState.Sending -> {
            SendingContent(
                sendingProgress = wizardState.sendingProgress,
                sendingDetail = wizardState.sendingDetail,
                onCancelSending = { viewModel.cancelSending() }
            )
        }

        else -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = if (totalSteps > 0) {
                                    "Etape ${currentStepIndex + 1}/$totalSteps"
                                } else {
                                    "Chargement..."
                                },
                                style = MaterialTheme.typography.titleMedium
                            )
                        },
                        navigationIcon = {
                            TextButton(
                                onClick = { showAbandonDialog = true },
                                modifier = Modifier.height(48.dp)
                            ) {
                                Text(
                                    text = "Abandonner",
                                    color = MaterialTheme.colorScheme.error
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
                if (currentStep == null) {
                    LoadingContent(modifier = Modifier.padding(innerPadding))
                } else {
                    when (wizardState.stepState) {
                        is StepState.WaitingConfirm -> {
                            WaitingConfirmContent(
                                step = currentStep,
                                totalSteps = totalSteps,
                                currentStepIndex = currentStepIndex,
                                userInputs = wizardState.userInputs,
                                onConfirmAndSend = { viewModel.confirmAndSend() },
                                onHelp = { /* aide contextuelle */ },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        is StepState.WaitingResult -> {
                            WaitingResultContent(
                                step = currentStep,
                                totalSteps = totalSteps,
                                currentStepIndex = currentStepIndex,
                                onConfirmSuccess = { viewModel.confirmSuccess() },
                                onRetry = { viewModel.retry() },
                                onHelp = { /* aide contextuelle */ },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }

                        is StepState.Error -> {
                            val errorState = wizardState.stepState as StepState.Error
                            if (!currentStep.criticalStep) {
                                ErrorContent(
                                    step = currentStep,
                                    totalSteps = totalSteps,
                                    currentStepIndex = currentStepIndex,
                                    errorMessage = errorState.message,
                                    onRetry = { viewModel.retry() },
                                    onAbandon = { showAbandonDialog = true },
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                        }

                        else -> {
                            LoadingContent(modifier = Modifier.padding(innerPadding))
                        }
                    }
                }
            }
        }
    }
}

// region WaitingConfirm

@Composable
private fun WaitingConfirmContent(
    step: com.winrescue.data.model.ScriptStep,
    totalSteps: Int,
    currentStepIndex: Int,
    userInputs: Map<String, String>,
    onConfirmAndSend: () -> Unit,
    onHelp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Indicateur de progression
        StepIndicator(
            totalSteps = totalSteps,
            currentStep = currentStepIndex
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Titre de la step
        Text(
            text = step.title,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Image hint
        if (step.imageHint != null) {
            HintImage(imageHint = step.imageHint)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Instruction principale
        Text(
            text = step.instruction,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Detail de l'instruction
        if (step.instructionDetail != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = step.instructionDetail,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Preview des commandes
        if (step.actions.isNotEmpty()) {
            KeySequencePreview(
                actions = step.actions,
                inputs = userInputs
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        // Question de confirmation
        if (step.confirmQuestion != null) {
            Text(
                text = step.confirmQuestion,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Bouton principal "Lancer"
        Button(
            onClick = onConfirmAndSend,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Oui, lancer !",
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Bouton aide
        OutlinedButton(
            onClick = onHelp,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Help,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Non, besoin d'aide",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

// endregion

// region Sending

@Composable
private fun SendingContent(
    sendingProgress: Float,
    sendingDetail: String,
    onCancelSending: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Keyboard,
            contentDescription = "Envoi HID",
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Envoi en cours...",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Detail de l'action en cours (police monospace)
        Text(
            text = sendingDetail,
            fontFamily = JetBrainsMono,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Barre de progression
        LinearProgressIndicator(
            progress = { sendingProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${(sendingProgress * 100).toInt()}%",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Avertissement
        WarningBanner(
            message = "Ne debranchez pas le cable !"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Bouton annulation d'urgence
        OutlinedButton(
            onClick = onCancelSending,
            modifier = Modifier.height(48.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
        ) {
            Icon(
                imageVector = Icons.Default.Cancel,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Annulation d'urgence",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

// endregion

// region WaitingResult

@Composable
private fun WaitingResultContent(
    step: com.winrescue.data.model.ScriptStep,
    totalSteps: Int,
    currentStepIndex: Int,
    onConfirmSuccess: () -> Unit,
    onRetry: () -> Unit,
    onHelp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Indicateur de progression
        StepIndicator(
            totalSteps = totalSteps,
            currentStep = currentStepIndex
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Icone succes
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Frappes envoyees",
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Frappes envoyees !",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Regardez l'ecran du PC.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = step.confirmQuestion ?: "L'operation a-t-elle reussi ?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Image hint apres envoi
        if (step.imageHint != null) {
            Spacer(modifier = Modifier.height(16.dp))
            HintImage(imageHint = step.imageHint)
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bouton succes -> etape suivante
        Button(
            onClick = onConfirmSuccess,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Oui, etape suivante",
                style = MaterialTheme.typography.labelLarge
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Bouton retry (si retryable)
        if (step.retryable) {
            OutlinedButton(
                onClick = onRetry,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Non, reessayer",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Bouton aide
        TextButton(
            onClick = onHelp,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Help,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = "Aide",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

// endregion

// region Error (non-critical)

@Composable
private fun ErrorContent(
    step: com.winrescue.data.model.ScriptStep,
    totalSteps: Int,
    currentStepIndex: Int,
    errorMessage: String,
    onRetry: () -> Unit,
    onAbandon: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        StepIndicator(
            totalSteps = totalSteps,
            currentStep = currentStepIndex
        )

        Spacer(modifier = Modifier.height(24.dp))

        Icon(
            imageVector = Icons.Default.Cancel,
            contentDescription = "Erreur",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier
                .size(48.dp)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Erreur",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (step.retryInstruction != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = step.retryInstruction,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        if (step.retryable) {
            Button(
                onClick = onRetry,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Reessayer",
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        OutlinedButton(
            onClick = onAbandon,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
        ) {
            Text(
                text = "Abandonner",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

// endregion

// region Loading

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            androidx.compose.material3.CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Chargement du script...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// endregion

// region Dialog

@Composable
private fun AbandonDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Abandonner le wizard ?",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text = "Etes-vous sur de vouloir abandonner ? " +
                    "La progression ne sera pas sauvegardee.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Oui, abandonner",
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Continuer")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

// endregion
