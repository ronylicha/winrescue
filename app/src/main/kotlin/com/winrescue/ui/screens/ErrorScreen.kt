package com.winrescue.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.winrescue.ui.navigation.Route
import com.winrescue.ui.theme.WinRescueTheme

private data class StepErrorInfo(
    val stepTitle: String,
    val possibleCauses: List<String>,
    val solutions: List<String>
)

private fun getStepErrorInfo(scriptId: String, stepId: Int): StepErrorInfo {
    val stepTitle = when (scriptId) {
        "reset_password" -> when (stepId) {
            0 -> "Ouvrir l'invite de commandes"
            1 -> "Acceder au registre SAM"
            2 -> "Lister les utilisateurs"
            3 -> "Modifier le mot de passe"
            4 -> "Confirmer les modifications"
            else -> "Etape $stepId"
        }
        "enable_admin" -> when (stepId) {
            0 -> "Ouvrir l'invite de commandes"
            1 -> "Activer le compte administrateur"
            2 -> "Confirmer l'activation"
            else -> "Etape $stepId"
        }
        "safe_mode" -> when (stepId) {
            0 -> "Ouvrir l'invite de commandes"
            1 -> "Configurer le mode sans echec"
            2 -> "Confirmer la configuration"
            else -> "Etape $stepId"
        }
        else -> "Etape $stepId"
    }

    val possibleCauses = when {
        stepId == 0 -> listOf(
            "Le PC cible n'est pas sur l'ecran attendu",
            "Le cable USB n'est pas correctement branche",
            "Le peripherique HID n'est pas reconnu par le PC cible",
            "Le PC cible est en veille ou eteint"
        )
        else -> listOf(
            "Delai trop court entre les frappes",
            "Le PC cible n'a pas repondu a temps",
            "La commande precedente n'a pas abouti",
            "Le layout clavier ne correspond pas au PC cible",
            "Permissions insuffisantes sur le PC cible"
        )
    }

    val solutions = when {
        stepId == 0 -> listOf(
            "Verifiez que le cable USB est branche des deux cotes",
            "Assurez-vous que le PC cible est allume et sur l'ecran attendu",
            "Allez dans Parametres > USB HID et testez la connexion",
            "Augmentez le delai entre frappes dans les parametres"
        )
        else -> listOf(
            "Reessayez cette etape avec un delai plus long",
            "Verifiez visuellement l'etat du PC cible avant de continuer",
            "Changez le layout clavier si les caracteres envoyes sont incorrects",
            "Revenez a l'etape precedente et reprenez depuis la"
        )
    }

    return StepErrorInfo(stepTitle, possibleCauses, solutions)
}

@Composable
fun ErrorScreen(
    scriptId: String,
    stepId: Int,
    errorMessage: String?,
    navController: NavHostController
) {
    val errorInfo = remember(scriptId, stepId) { getStepErrorInfo(scriptId, stepId) }

    ErrorScreenContent(
        stepId = stepId,
        stepTitle = errorInfo.stepTitle,
        errorMessage = errorMessage,
        possibleCauses = errorInfo.possibleCauses,
        solutions = errorInfo.solutions,
        canGoBack = stepId > 0,
        onRetryStep = {
            navController.popBackStack()
            navController.navigate(Route.Wizard.createRoute(scriptId, stepId))
        },
        onPreviousStep = {
            if (stepId > 0) {
                navController.popBackStack()
                navController.navigate(Route.Wizard.createRoute(scriptId, stepId - 1))
            }
        },
        onAbandon = {
            navController.popBackStack(Route.Home.route, inclusive = false)
        }
    )
}

@Composable
private fun ErrorScreenContent(
    stepId: Int,
    stepTitle: String,
    errorMessage: String?,
    possibleCauses: List<String>,
    solutions: List<String>,
    canGoBack: Boolean,
    onRetryStep: () -> Unit,
    onPreviousStep: () -> Unit,
    onAbandon: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Icone erreur
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "Erreur",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(96.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Titre
            Text(
                text = "Etape echouee",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Step identifiant
            Text(
                text = "Step ${stepId + 1} - $stepTitle",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )

            // Message d'erreur
            if (!errorMessage.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Causes possibles
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Causes possibles",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    possibleCauses.forEach { cause ->
                        BulletItem(text = cause)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Solutions
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Solutions",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    solutions.forEach { solution ->
                        BulletItem(text = solution)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Boutons d'action
            Button(
                onClick = onRetryStep,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(text = "Reessayer cette etape")
            }

            if (canGoBack) {
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onPreviousStep,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(text = "Etape precedente")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onAbandon,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Cancel,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(text = "Abandonner")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BulletItem(
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.FiberManualRecord,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .size(8.dp)
                .padding(top = 2.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// region Previews

@Preview(showBackground = true, backgroundColor = 0xFF080C18, showSystemUi = true)
@Composable
private fun ErrorScreenStep0Preview() {
    WinRescueTheme {
        ErrorScreenContent(
            stepId = 0,
            stepTitle = "Ouvrir l'invite de commandes",
            errorMessage = "Timeout : le PC cible n'a pas repondu dans le delai imparti (10s).",
            possibleCauses = listOf(
                "Le PC cible n'est pas sur l'ecran attendu",
                "Le cable USB n'est pas correctement branche",
                "Le peripherique HID n'est pas reconnu par le PC cible",
                "Le PC cible est en veille ou eteint"
            ),
            solutions = listOf(
                "Verifiez que le cable USB est branche des deux cotes",
                "Assurez-vous que le PC cible est allume et sur l'ecran attendu",
                "Allez dans Parametres > USB HID et testez la connexion",
                "Augmentez le delai entre frappes dans les parametres"
            ),
            canGoBack = false,
            onRetryStep = {},
            onPreviousStep = {},
            onAbandon = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080C18, showSystemUi = true)
@Composable
private fun ErrorScreenStep2Preview() {
    WinRescueTheme {
        ErrorScreenContent(
            stepId = 2,
            stepTitle = "Modifier le mot de passe",
            errorMessage = null,
            possibleCauses = listOf(
                "Delai trop court entre les frappes",
                "Le PC cible n'a pas repondu a temps",
                "La commande precedente n'a pas abouti",
                "Le layout clavier ne correspond pas au PC cible"
            ),
            solutions = listOf(
                "Reessayez cette etape avec un delai plus long",
                "Verifiez visuellement l'etat du PC cible avant de continuer",
                "Changez le layout clavier si les caracteres envoyes sont incorrects",
                "Revenez a l'etape precedente et reprenez depuis la"
            ),
            canGoBack = true,
            onRetryStep = {},
            onPreviousStep = {},
            onAbandon = {}
        )
    }
}

// endregion
