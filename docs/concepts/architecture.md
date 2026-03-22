# Architecture technique detaillee -- WinRescue

Document de reference pour l'architecture logicielle de WinRescue.

---

## Sommaire

- [Vue d'ensemble](#vue-densemble)
- [Diagramme des packages](#diagramme-des-packages)
- [Couches logicielles](#couches-logicielles)
- [Flux de donnees complet](#flux-de-donnees-complet)
- [Machine a etats Root (6 etats)](#machine-a-etats-root-6-etats)
- [Machine a etats Wizard (5 etats)](#machine-a-etats-wizard-5-etats)
- [Modeles de donnees](#modeles-de-donnees)
- [Injection de dependances](#injection-de-dependances)
- [Navigation](#navigation)

---

## Vue d'ensemble

WinRescue est une application Android native ecrite en Kotlin utilisant Jetpack Compose pour l'interface utilisateur. Elle suit le pattern **MVVM** (Model-View-ViewModel) avec injection de dependances Hilt, persistance DataStore et acces root via libsu.

L'application se decompose en 4 couches principales, chacune avec une responsabilite definie :

```
┌─────────────────────────────────────────────────────────────────┐
│                        UI LAYER                                  │
│   Screens (Compose) + Components + Theme + Navigation            │
│   - Declaratif, sans logique metier                              │
│   - Observe les StateFlow des ViewModels                         │
├──────────────────────────────────────────────────────────────────┤
│                     VIEWMODEL LAYER                              │
│   HomeVM + WizardVM + SettingsVM                                 │
│   - Orchestre la logique metier                                  │
│   - Expose des StateFlow<State> immutables                       │
│   - Gere le cycle de vie via viewModelScope                      │
├──────────────────────────────────────────────────────────────────┤
│                       DATA LAYER                                 │
│   Repositories + Models + Settings + Root                        │
│   - Acces aux sources de donnees (JSON, DataStore, root shell)   │
│   - Modeles immutables (data class, sealed class)                │
│   - Interface/Implementation pour testabilite                    │
├──────────────────────────────────────────────────────────────────┤
│                     PLATFORM LAYER                               │
│   USB HID (HidKeyboardManager, HidKeyMap)                        │
│   Root (RootManager, libsu Shell)                                │
│   Android (UsbStateReceiver, configfs)                           │
│   - Interactions avec le systeme et le hardware                  │
│   - Necessite root pour /dev/hidg0 et configfs                   │
└──────────────────────────────────────────────────────────────────┘
```

---

## Diagramme des packages

```
com.winrescue
│
├── MainActivity.kt ─────────────── @AndroidEntryPoint, Activity Compose
├── WinRescueApp.kt ─────────────── @HiltAndroidApp, Application class
│
├── data ─────────────────────────── Couche donnees
│   │
│   ├── model ────────────────────── Modeles immutables
│   │   ├── Script.kt                 Script (id, name, description, steps, inputFields)
│   │   ├── ScriptStep.kt             Etape (instruction, actions, confirm, retry)
│   │   ├── KeyAction.kt              sealed class : 6 types d'actions HID
│   │   └── Enums.kt                  OsTarget, ScriptCategory, Difficulty, InputType
│   │
│   ├── repository ───────────────── Acces aux scripts JSON
│   │   ├── ScriptRepository.kt       Interface
│   │   └── ScriptRepositoryImpl.kt   Charge et deserialise les JSON depuis assets/
│   │
│   ├── root ─────────────────────── Gestion root et gadget HID
│   │   ├── RootManager.kt            Detection root, setup configfs, patch SELinux
│   │   ├── RootState.kt              sealed class (7 etats)
│   │   └── DisclaimerState.kt        Etat d'acceptation du disclaimer legal
│   │
│   └── settings ─────────────────── Preferences utilisateur
│       ├── AppSettings.kt            Data class (layout, delays, debug, disclaimer)
│       ├── SettingsRepository.kt      Interface
│       └── SettingsRepositoryImpl.kt  Persistence via DataStore Preferences
│
├── di ───────────────────────────── Injection de dependances
│   └── RepositoryModule.kt          @Module Hilt : binds interface -> implementation
│
├── ui ───────────────────────────── Interface utilisateur
│   │
│   ├── screens ──────────────────── 5 ecrans principaux
│   │   ├── HomeScreen.kt             Dashboard : KPIs, filtres, recherche, liste scripts
│   │   ├── ScriptDetailScreen.kt     Detail d'un script, saisie des inputs
│   │   ├── WizardStepScreen.kt       Execution pas-a-pas, envoi HID, progress
│   │   ├── SettingsScreen.kt         Parametres : layout, delais, debug
│   │   ├── SuccessScreen.kt          Fin de script reussie
│   │   └── ErrorScreen.kt            Erreur avec retry
│   │
│   ├── components ───────────────── 8 composants reutilisables
│   │   ├── UsbStatusBar.kt           Indicateur USB connecte/deconnecte
│   │   ├── ScriptCard.kt             Carte de script (nom, difficulte, OS, categorie)
│   │   ├── DifficultyBadge.kt        Badge colore Easy/Medium/Advanced
│   │   ├── OsSelector.kt             Filtre segmented Win10/Win11/Both
│   │   ├── WarningBanner.kt          Banniere d'avertissement jaune
│   │   ├── StepIndicator.kt          Barre de progression numerotee
│   │   ├── CountdownTimer.kt         Timer de compte a rebours entre etapes
│   │   └── KeySequencePreview.kt     Preview des touches HID avant envoi
│   │
│   ├── viewmodel ────────────────── 3 ViewModels MVVM
│   │   ├── HomeViewModel.kt          Filtrage, recherche, KPI, chargement scripts
│   │   ├── WizardViewModel.kt        Orchestration wizard, envoi HID, gestion etats
│   │   └── SettingsViewModel.kt      Lecture/ecriture preferences
│   │
│   ├── navigation ───────────────── Navigation Compose
│   │   ├── Routes.kt                 sealed class Route avec 6 destinations
│   │   └── NavGraph.kt               Graphe de navigation
│   │
│   └── theme ────────────────────── Material 3 dark theme
│       ├── Color.kt                   Palette bleue/slate
│       ├── Theme.kt                   Theme Material 3 dark
│       └── Type.kt                    Typographie JetBrains Mono
│
└── usb ──────────────────────────── Couche USB HID
    ├── HidKeyboardManager.kt         @Singleton : envoi rapports 8 octets sur /dev/hidg0
    ├── HidKeyMap.kt                   Mapping char -> scancode (QWERTY US + AZERTY FR)
    ├── UsbConnectionState.kt          Enum : Connected, Disconnected, Error
    └── UsbStateReceiver.kt            BroadcastReceiver pour evenements USB
```

---

## Couches logicielles

### UI Layer

**Responsabilite** : affichage, interactions utilisateur, navigation.

- **Compose** : UI declarative sans logique metier. Les ecrans sont des fonctions `@Composable` qui observent les `StateFlow` des ViewModels via `collectAsStateWithLifecycle()`.
- **Components** : 8 composants reutilisables, chacun parametre par des parametres fonctionnels (callbacks `onClick`, `onConfirm`, etc.).
- **Theme** : Material 3 dark avec palette bleue/slate et police JetBrains Mono (Regular, Medium, Bold).
- **Navigation** : sealed class `Route` avec des routes typees. Le `NavGraph` definit les transitions entre les 6 destinations.

### ViewModel Layer

**Responsabilite** : logique metier, transformation des donnees, gestion des etats.

| ViewModel | StateFlow | Dependances injectees |
|-----------|-----------|----------------------|
| `HomeViewModel` | Liste filtree de scripts, filtres actifs, query de recherche | `ScriptRepository` |
| `WizardViewModel` | `WizardState` (script, etape courante, etat d'envoi, inputs, erreur) | `ScriptRepository`, `HidKeyboardManager`, `SettingsRepository` |
| `SettingsViewModel` | `AppSettings` courant | `SettingsRepository` |

Le `WizardViewModel` est le plus complexe : il orchestre le chargement du script, la progression des etapes, l'envoi des actions HID et la gestion des erreurs/retry.

### Data Layer

**Responsabilite** : acces aux sources de donnees, modeles immutables.

- **ScriptRepository** : charge les 22 fichiers JSON depuis `assets/scripts/`, les deserialise via kotlinx-serialization en objets `Script` avec leurs `ScriptStep` et `KeyAction`.
- **SettingsRepository** : lit et ecrit les preferences utilisateur dans DataStore Preferences (layout clavier, delais, mode debug, acceptation du disclaimer).
- **RootManager** : detection root en 3 etapes, setup configfs du gadget HID, patch SELinux via `magiskpolicy` ou `ksud`.
- **Models** : `Script`, `ScriptStep`, `KeyAction` (sealed class polymorphe), `InputField`, enums (`OsTarget`, `ScriptCategory`, `Difficulty`, `InputType`).

### Platform Layer

**Responsabilite** : interactions directes avec le systeme Android et le hardware USB.

- **HidKeyboardManager** : ouvre `/dev/hidg0` en ecriture (via root + chmod), envoie des rapports HID de 8 octets pour simuler des frappes clavier. Supporte 6 types d'actions (string, key, combination, wait, repeat, template).
- **HidKeyMap** : mapping bidirectionnel caractere -> scancode HID pour QWERTY US et AZERTY FR. Gere les modificateurs (Shift, Ctrl, Alt, AltGr, Win) et les touches speciales (F1-F12, fleches, Enter, etc.).
- **UsbStateReceiver** : BroadcastReceiver Android qui detecte les evenements `USB_DEVICE_ATTACHED` et `USB_DEVICE_DETACHED` pour mettre a jour la barre de statut USB en temps reel.

---

## Flux de donnees complet

### 1. Demarrage de l'application

```
Application.onCreate()
     │
     ▼
Hilt initialise les singletons :
  ├── ScriptRepositoryImpl    (charge les 22 JSON depuis assets)
  ├── SettingsRepositoryImpl  (lit les preferences DataStore)
  ├── RootManager             (pret pour la verification root)
  └── HidKeyboardManager      (pret pour la connexion HID)
     │
     ▼
MainActivity.onCreate()
     │
     ├── Verifier DisclaimerState
     │     ├── Non accepte → Afficher ecran Disclaimer
     │     └── Accepte → Continuer
     │
     ├── RootManager.checkRootStatus()
     │     ├── Shell.getShell()          → popup Magisk/KSU
     │     ├── Shell.isAppGrantedRoot()  → true/false/null
     │     ├── test -e /dev/hidg0        → existe ?
     │     └── test -w /dev/hidg0        → accessible ?
     │
     └── Naviguer vers HomeScreen
```

### 2. Selection et lancement d'un script

```
HomeScreen
     │ Utilisateur selectionne un script
     ▼
ScriptDetailScreen
     │ Utilisateur remplit les inputs (username, password, etc.)
     │ Utilisateur appuie sur "Demarrer"
     ▼
WizardStepScreen (etape 1)
     │
     ├── Afficher instruction + detail
     ├── Afficher preview des touches (si previewBeforeSend=true)
     │
     │ Utilisateur appuie sur "Confirmer et envoyer"
     ▼
WizardViewModel.confirmAndSend()
     │
     ├── Lire settings (layout, delais)
     ├── HidKeyboardManager.connect()
     │     └── chmod 666 /dev/hidg0
     │     └── FileOutputStream(/dev/hidg0)
     │
     ├── waitBeforeSendMs (si > 0)
     │
     ├── Pour chaque action :
     │   ├── Mettre a jour sendingProgress
     │   ├── sendKeyAction(action, inputs, layout)
     │   │     ├── TypeString    → typeString() → charToReport() → sendReport()
     │   │     ├── PressKey      → pressKey() → resolveKeyCode() → sendReport()
     │   │     ├── Combination   → pressKeyCombination() → sendReport()
     │   │     ├── Wait          → delay(ms)
     │   │     ├── RepeatKey     → N x pressKey()
     │   │     └── Template      → resolveTemplate() → typeString()
     │   └── Chaque rapport : 8 octets ecrits sur /dev/hidg0
     │
     ├── waitAfterSendMs (si > 0)
     │
     ├── HidKeyboardManager.disconnect()
     │
     └── confirmQuestion != null ?
           ├── Oui → StepState.WaitingResult (afficher la question)
           │         ├── Utilisateur confirme → nextStep()
           │         └── Utilisateur retry → StepState.WaitingConfirm (reprendre)
           └── Non → nextStep() directement
                       ├── Pas derniere etape → Afficher etape suivante
                       └── Derniere etape → StepState.Success → SuccessScreen
```

### 3. Gestion des erreurs

```
Erreur pendant l'envoi
     │
     ├── HidKeyboardManager.disconnect() (cleanup)
     │
     └── StepState.Error(message)
           │
           ├── Afficher ErrorScreen avec le message
           ├── Si retryable → bouton "Reessayer" → retry()
           │                                         └── StepState.WaitingConfirm
           └── Si criticalStep && echec → bloquer la progression
```

---

## Machine a etats Root (6 etats)

La detection root est modelisee par une sealed class `RootState` avec 7 variantes dont 6 etats terminaux :

```
                    ┌──────────────┐
                    │  Unchecked   │  Etat initial au demarrage
                    └──────┬───────┘
                           │ checkRootStatus()
                           ▼
                    ┌──────────────┐
                    │  Checking    │  Verification en cours
                    └──────┬───────┘
                           │
              ┌────────────┼────────────┬──────────────┐
              ▼            ▼            ▼              ▼
      ┌──────────┐  ┌───────────┐  ┌──────────┐  ┌────────┐
      │  NoRoot  │  │RootDenied │  │RootOnly  │  │  Ready │
      │          │  │           │  │NoHid     │  │        │
      └──────────┘  └───────────┘  └────┬─────┘  └────────┘
                                        │
                                        │ setupHidGadget()
                                        ▼
                                   ┌─────────┐
                              ┌────│ Setup   │────┐
                              │    │ Result  │    │
                              ▼    └─────────┘    ▼
                         ┌────────┐          ┌────────┐
                         │  Ready │          │  Error │
                         └────────┘          └────────┘
```

| Etat | Condition | Action possible |
|------|-----------|----------------|
| `Unchecked` | Verification pas encore lancee | Appeler `checkRootStatus()` |
| `Checking` | Verification en cours | Attendre |
| `NoRoot` | `su` absent ou `Shell.getShell()` echoue | Afficher message "Root requis" |
| `RootDenied` | Utilisateur a refuse la popup Magisk/KSU | Proposer de relancer |
| `RootOnlyNoHid` | Root OK mais `/dev/hidg0` n'existe pas | Proposer `setupHidGadget()` |
| `Ready` | Root OK + `/dev/hidg0` accessible en ecriture | App fonctionnelle |
| `Error` | Erreur pendant la verification ou le setup | Afficher le message d'erreur |

### Proprietes derivees

- `isHidReady` : `true` uniquement si `Ready`
- `hasRoot` : `true` si `Ready` ou `RootOnlyNoHid`

---

## Machine a etats Wizard (5 etats)

L'execution d'une etape du wizard est modelisee par une sealed class `StepState` :

```
                    ┌────────────────┐
                    │ WaitingConfirm │  Attente de confirmation utilisateur
                    └───────┬────────┘
                            │ confirmAndSend()
                            ▼
                    ┌────────────────┐
                    │    Sending     │  Envoi des actions HID en cours
                    └───────┬────────┘
                            │
                   ┌────────┼──────────┐
                   │        │          │
                   ▼        ▼          ▼
            ┌──────────┐ ┌──────┐ ┌─────────────┐
            │WaitingRes│ │Error │ │(CancellationExc)
            │ult       │ │      │ │  → WaitingConfirm
            └────┬─────┘ └──┬───┘ └─────────────┘
                 │          │
        ┌────────┤          │ retry()
        │        │          ▼
        │        │   ┌────────────────┐
        │        │   │ WaitingConfirm │
        │        │   └────────────────┘
        │        │
        │  confirmSuccess()
        ▼        ▼
  ┌──────────┐ ┌──────────┐
  │ nextStep │ │ Success  │  (si derniere etape)
  │→ Waiting │ └──────────┘
  │ Confirm  │
  └──────────┘
```

| Etat | Description | Transitions possibles |
|------|-------------|----------------------|
| `WaitingConfirm` | En attente de l'action utilisateur "Confirmer et envoyer" | → `Sending` |
| `Sending` | Envoi des actions HID en cours, progress bar active | → `WaitingResult`, `Error`, `WaitingConfirm` (cancel) |
| `WaitingResult` | Envoi termine, question de confirmation affichee | → `WaitingConfirm` (etape suivante), `Success` (si derniere) |
| `Error` | Erreur pendant l'envoi | → `WaitingConfirm` (retry) |
| `Success` | Script termine avec succes | Terminal (navigation vers SuccessScreen) |

### WizardState complet

```kotlin
data class WizardState(
    val script: Script?,           // Script en cours
    val currentStepIndex: Int,     // Index de l'etape courante (0-based)
    val userInputs: Map<String, String>, // Variables saisies par l'utilisateur
    val stepState: StepState,      // Etat de l'etape courante
    val sendingProgress: Float,    // 0.0 → 1.0
    val sendingDetail: String,     // Description de l'action en cours
    val errorMessage: String?,     // Message d'erreur (si applicable)
    val retryCount: Int            // Nombre de retry sur cette etape
)
```

---

## Modeles de donnees

### Script

```
Script
├── id: String                    # Identifiant unique (ex: "reset_password_win10")
├── name: String                  # Nom affiche (ex: "Reset mot de passe Windows 10")
├── description: String           # Description longue
├── category: ScriptCategory      # RECOVERY | ADMIN | REPAIR | SECURITY | NETWORK | DIAGNOSTIC
├── os: List<OsTarget>            # [WIN10] | [WIN11] | [BOTH]
├── difficulty: Difficulty        # EASY | MEDIUM | ADVANCED
├── estimatedMinutes: Int         # Duree estimee
├── requiresRoot: Boolean         # Toujours true actuellement
├── icon: String                  # Nom de l'icone Material
├── warningMessage: String?       # Avertissement optionnel
├── inputFields: List<InputField> # Champs de saisie utilisateur
└── steps: List<ScriptStep>       # Etapes du wizard
```

### ScriptStep

```
ScriptStep
├── id: Int                       # Numero de l'etape (1-based)
├── title: String                 # Titre court
├── instruction: String           # Instruction principale en langage clair
├── instructionDetail: String?    # Explication technique optionnelle
├── imageHint: String?            # Reference vers une image d'aide
├── confirmQuestion: String?      # Question pour valider le resultat
├── waitBeforeSendMs: Long        # Delai avant envoi (ms)
├── waitAfterSendMs: Long         # Delai apres envoi (ms)
├── actions: List<KeyAction>      # Sequence d'actions HID
├── retryable: Boolean            # L'etape peut-elle etre retentee ?
├── retryInstruction: String?     # Instruction en cas d'echec
└── criticalStep: Boolean         # Etape critique (bloque si echec)
```

### KeyAction (sealed class polymorphe)

```
KeyAction
├── TypeString(value, delayBetweenCharsMs)              # Taper une chaine
├── PressKey(key, modifier?)                            # Appuyer sur une touche
├── KeyCombination(keys: List)                          # Combinaison simultanee
├── Wait(ms)                                            # Attendre
├── RepeatKey(key, count, delayBetweenMs)               # Repeter une touche
└── TemplateString(template, delayBetweenCharsMs)       # Chaine avec variables {{}}
```

La serialisation utilise un discriminateur `type` avec `@SerialName` :
- `"string"` → `TypeString`
- `"key"` → `PressKey`
- `"combination"` → `KeyCombination`
- `"wait"` → `Wait`
- `"repeat"` → `RepeatKey`
- `"template"` → `TemplateString`

---

## Injection de dependances

WinRescue utilise Hilt (Dagger 2) pour l'injection de dependances.

### Points d'entree

```
@HiltAndroidApp
class WinRescueApp : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity()
```

### Module Hilt

```
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton
    ScriptRepository ←── ScriptRepositoryImpl

    @Binds @Singleton
    SettingsRepository ←── SettingsRepositoryImpl
}
```

### Singletons auto-injectes

```
@Singleton
HidKeyboardManager(@ApplicationContext context)

@Singleton
RootManager(@ApplicationContext context)
```

### ViewModels

```
@HiltViewModel
HomeViewModel(scriptRepository)

@HiltViewModel
WizardViewModel(savedStateHandle, scriptRepository, hidKeyboardManager, settingsRepository)

@HiltViewModel
SettingsViewModel(settingsRepository)
```

---

## Navigation

### Routes

```
sealed class Route(val route: String)
├── Home          → "home"
├── Settings      → "settings"
├── ScriptDetail  → "script/{scriptId}"
├── Wizard        → "wizard/{scriptId}/step/{stepId}"
├── Success       → "success/{scriptId}"
└── Error         → "error/{scriptId}/step/{stepId}?errorMessage={errorMessage}"
```

### Flux de navigation

```
Home ──select──> ScriptDetail ──start──> Wizard (step 1)
                                           │
                                           ├── step 2, 3, ... N
                                           │
                                           ├── Succes → SuccessScreen ──> Home
                                           │
                                           └── Erreur → ErrorScreen
                                                          ├── Retry → Wizard (meme step)
                                                          └── Abandonner → Home

Home ──settings──> SettingsScreen ──back──> Home
```

---

*Document genere le 2026-03-22 a partir de l'analyse du code source WinRescue v1.0.0.*
