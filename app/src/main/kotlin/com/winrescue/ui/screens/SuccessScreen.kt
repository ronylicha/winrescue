package com.winrescue.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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

private data class PostScriptInfo(
    val scriptName: String,
    val nextStepInstruction: String
)

private fun getPostScriptInfo(scriptId: String): PostScriptInfo {
    val name = when (scriptId) {
        "reset_password" -> "Reset mot de passe"
        "enable_admin" -> "Activer compte Admin"
        "disable_admin" -> "Desactiver compte Admin"
        "safe_mode" -> "Mode sans echec"
        "add_user" -> "Ajouter un utilisateur"
        "enable_rdp" -> "Activer Bureau a distance"
        "disable_firewall" -> "Desactiver le pare-feu"
        "enable_networking" -> "Activer le reseau"
        "fix_mbr" -> "Reparer le MBR"
        "sfc_scan" -> "Verifier les fichiers systeme"
        "bitlocker_off" -> "Desactiver BitLocker"
        else -> scriptId
    }

    val instruction = when (scriptId) {
        "reset_password" -> "Redemarrez le PC et connectez-vous avec le nouveau mot de passe."
        "enable_admin" -> "Redemarrez le PC et selectionnez le compte Administrateur sur l'ecran de connexion."
        "disable_admin" -> "Le compte administrateur integre est desormais desactive."
        "safe_mode" -> "Redemarrez le PC. Il demarrera automatiquement en mode sans echec."
        "add_user" -> "Redemarrez le PC et connectez-vous avec le nouveau compte utilisateur."
        "enable_rdp" -> "Redemarrez le PC. Le bureau a distance sera accessible sur le port 3389."
        "disable_firewall" -> "Le pare-feu Windows est desactive. Pensez a le reactiver apres diagnostic."
        "enable_networking" -> "Redemarrez le PC pour appliquer la configuration reseau."
        "fix_mbr" -> "Retirez la cle USB et redemarrez le PC normalement."
        "sfc_scan" -> "Redemarrez le PC. Les fichiers systeme corrompus ont ete repares."
        "bitlocker_off" -> "Le dechiffrement BitLocker est en cours. Le PC reste utilisable pendant l'operation."
        else -> "Operation terminee. Verifiez le resultat sur le PC cible."
    }

    return PostScriptInfo(name, instruction)
}

@Composable
fun SuccessScreen(
    scriptId: String,
    navController: NavHostController
) {
    val info = remember(scriptId) { getPostScriptInfo(scriptId) }

    SuccessScreenContent(
        scriptName = info.scriptName,
        nextStepInstruction = info.nextStepInstruction,
        onReturnHome = {
            navController.popBackStack(Route.Home.route, inclusive = false)
        },
        onRelaunchScript = {
            navController.popBackStack(Route.Home.route, inclusive = false)
            navController.navigate(Route.ScriptDetail.createRoute(scriptId))
        }
    )
}

@Composable
private fun SuccessScreenContent(
    scriptName: String,
    nextStepInstruction: String,
    onReturnHome: () -> Unit,
    onRelaunchScript: () -> Unit,
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Icone succes
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Succes",
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(96.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Titre
            Text(
                text = "Termine avec succes !",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Nom du script
            Text(
                text = scriptName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Prochaine etape
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Prochaine etape :",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = nextStepInstruction,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Boutons
            Button(
                onClick = onReturnHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(text = "Retour a l'accueil")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onRelaunchScript,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Replay,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(text = "Relancer ce script")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// region Previews

@Preview(showBackground = true, backgroundColor = 0xFF080C18, showSystemUi = true)
@Composable
private fun SuccessScreenPreview() {
    WinRescueTheme {
        SuccessScreenContent(
            scriptName = "Reset mot de passe",
            nextStepInstruction = "Redemarrez le PC et connectez-vous avec le nouveau mot de passe.",
            onReturnHome = {},
            onRelaunchScript = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080C18, showSystemUi = true)
@Composable
private fun SuccessScreenGenericPreview() {
    WinRescueTheme {
        SuccessScreenContent(
            scriptName = "Activer compte Admin",
            nextStepInstruction = "Redemarrez le PC et selectionnez le compte Administrateur sur l'ecran de connexion.",
            onReturnHome = {},
            onRelaunchScript = {}
        )
    }
}

// endregion
