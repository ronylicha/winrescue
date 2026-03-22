# Architecture technique

Vue d'ensemble de l'architecture logicielle de WinRescue.

**Retour** : [Accueil du wiki](Home.md) | **Voir aussi** : [Protocole USB HID](USB-HID-Protocol.md) | [Architecture detaillee](../docs/concepts/architecture.md)

---

## Sommaire

- [Vue d'ensemble](#vue-densemble)
- [Couches logicielles](#couches-logicielles)
- [Diagramme des packages](#diagramme-des-packages)
- [Patterns architecturaux](#patterns-architecturaux)
- [Flux de donnees](#flux-de-donnees)
- [Injection de dependances](#injection-de-dependances)
- [Stack technique](#stack-technique)

---

## Vue d'ensemble

WinRescue suit une architecture **MVVM** (Model-View-ViewModel) avec injection de dependances Hilt et navigation Compose. L'application est organisee en 4 couches principales :

```
┌─────────────────────────────────────────────┐
│                  UI Layer                    │
│  Screens + Components + Theme (Compose)     │
├─────────────────────────────────────────────┤
│               ViewModel Layer               │
│  HomeVM + WizardVM + SettingsVM             │
├─────────────────────────────────────────────┤
│                Data Layer                    │
│  Repositories + Models + Settings           │
├─────────────────────────────────────────────┤
│              Platform Layer                  │
│  USB HID (hidg0) + Root (libsu) + configfs  │
└─────────────────────────────────────────────┘
```

---

## Couches logicielles

### UI Layer (`ui/`)

La couche de presentation utilise Jetpack Compose avec Material 3 (theme dark). Elle comprend :

- **5 ecrans** : Home (dashboard), ScriptDetail, WizardStep, Success, Error
- **8 composants reutilisables** : UsbStatusBar, ScriptCard, DifficultyBadge, OsSelector, WarningBanner, StepIndicator, CountdownTimer, KeySequencePreview
- **Navigation** : sealed class `Route` avec routes typees et `NavGraph` Compose
- **Theme** : palette bleue/slate, police JetBrains Mono

### ViewModel Layer (`ui/viewmodel/`)

Trois ViewModels Hilt gerent la logique metier :

| ViewModel | Responsabilite |
|-----------|---------------|
| `HomeViewModel` | Chargement des scripts, filtrage par OS/categorie, recherche, KPI |
| `WizardViewModel` | Orchestration du wizard step-by-step, envoi des actions HID, gestion des etats |
| `SettingsViewModel` | Lecture/ecriture des preferences (layout, delais, debug) |

### Data Layer (`data/`)

- **Models** : `Script`, `ScriptStep`, `KeyAction` (sealed class polymorphe), enums (`OsTarget`, `ScriptCategory`, `Difficulty`, `InputType`)
- **Repositories** : `ScriptRepository` (lecture des JSON depuis assets), `SettingsRepository` (DataStore Preferences)
- **Root** : `RootManager` (detection root, setup configfs, patch SELinux), `RootState` (machine a 6 etats)

### Platform Layer (`usb/`)

- **HidKeyboardManager** : singleton qui ouvre `/dev/hidg0` en ecriture et envoie des rapports HID de 8 octets
- **HidKeyMap** : mapping caractere -> scancode HID avec support QWERTY US et AZERTY FR
- **UsbStateReceiver** : BroadcastReceiver pour detecter connexion/deconnexion USB

---

## Diagramme des packages

```
com.winrescue
│
├── MainActivity.kt
├── WinRescueApp.kt
│
├── data
│   ├── model
│   │   ├── Script           # id, name, description, steps, inputFields
│   │   ├── ScriptStep        # instruction, actions, confirmQuestion, criticalStep
│   │   ├── KeyAction         # sealed: TypeString | PressKey | KeyCombination | Wait | RepeatKey | TemplateString
│   │   └── Enums             # OsTarget, ScriptCategory, Difficulty, InputType
│   │
│   ├── repository
│   │   ├── ScriptRepository      # interface
│   │   └── ScriptRepositoryImpl   # charge les JSON depuis assets/scripts/
│   │
│   ├── root
│   │   ├── RootManager       # detection root 3 etapes, setup configfs, patch SELinux
│   │   ├── RootState          # sealed: Unchecked | Checking | Ready | RootOnlyNoHid | NoRoot | RootDenied | Error
│   │   └── DisclaimerState    # gestion du disclaimer legal
│   │
│   └── settings
│       ├── AppSettings       # data class des preferences
│       ├── SettingsRepository     # interface
│       └── SettingsRepositoryImpl # DataStore Preferences
│
├── di
│   └── RepositoryModule      # @Module Hilt : bindings interface -> impl
│
├── ui
│   ├── screens/              # 5 ecrans Compose
│   ├── components/           # 8 composants reutilisables
│   ├── viewmodel/            # 3 ViewModels @HiltViewModel
│   ├── navigation/           # Routes (sealed class) + NavGraph
│   └── theme/                # Color + Theme + Type (Material 3 dark)
│
└── usb
    ├── HidKeyboardManager    # @Singleton : envoi rapports HID sur /dev/hidg0
    ├── HidKeyMap              # mapping char -> scancode (QWERTY/AZERTY)
    ├── UsbConnectionState    # enum de connexion
    └── UsbStateReceiver       # BroadcastReceiver USB
```

---

## Patterns architecturaux

### MVVM + Repository

```
Screen (Compose) ──observe──> ViewModel (StateFlow) ──appelle──> Repository ──lit──> Source
```

- Les Screens observent des `StateFlow` exposes par les ViewModels
- Les ViewModels appellent les Repositories via des coroutines
- Les Repositories encapsulent l'acces aux sources de donnees (JSON assets, DataStore, root shell)

### Sealed Classes pour les etats

L'application utilise des sealed classes pour modeliser les etats de maniere exhaustive :

- **`RootState`** : 7 etats (Unchecked, Checking, Ready, RootOnlyNoHid, NoRoot, RootDenied, Error)
- **`StepState`** : 5 etats (WaitingConfirm, Sending, WaitingResult, Error, Success)
- **`KeyAction`** : 6 types (TypeString, PressKey, KeyCombination, Wait, RepeatKey, TemplateString)
- **`Route`** : 6 destinations (Home, Settings, ScriptDetail, Wizard, Success, Error)

### Serialisation polymorphe

Les `KeyAction` utilisent `@SerialName` pour le polymorphisme JSON :

```json
{"type": "key", "key": "ENTER"}
{"type": "string", "value": "net user admin P@ssw0rd"}
{"type": "template", "template": "net user {{username}} {{new_password}}"}
{"type": "wait", "ms": 3000}
{"type": "repeat", "key": "F8", "count": 50, "delayBetweenMs": 150}
{"type": "combination", "keys": ["CTRL", "ALT", "DELETE"]}
```

---

## Flux de donnees

### Chargement d'un script

```
assets/scripts/*.json
       │
       ▼
ScriptRepositoryImpl ──deserialize (kotlinx-serialization)──> Script + List<ScriptStep> + List<KeyAction>
       │
       ▼
HomeViewModel ──expose via StateFlow──> HomeScreen (liste filtree)
       │
       ▼
Navigation ──scriptId──> ScriptDetailScreen
       │
       ▼
Navigation ──scriptId + stepId──> WizardStepScreen
```

### Execution d'une etape HID

```
WizardStepScreen
       │ "Confirmer et envoyer"
       ▼
WizardViewModel.confirmAndSend()
       │
       ├── Lire les settings (layout, delais)
       ├── Connecter HidKeyboardManager.connect()
       │        └── chmod 666 /dev/hidg0 (via root)
       │        └── ouvrir FileOutputStream(/dev/hidg0)
       │
       ├── Pour chaque action de l'etape :
       │   ├── TypeString   → typeString() → charToReport() → sendReport()
       │   ├── PressKey     → pressKey() → resolveKeyCode() → sendReport()
       │   ├── Combination  → pressKeyCombination() → sendReport()
       │   ├── Wait         → delay(ms)
       │   ├── RepeatKey    → repeatKey() → N x pressKey()
       │   └── Template     → resolveTemplate() → typeString()
       │
       ├── Deconnecter HidKeyboardManager.disconnect()
       └── Passer a StepState.WaitingResult (si confirmQuestion) ou nextStep()
```

---

## Injection de dependances

WinRescue utilise Hilt (Dagger) pour l'injection de dependances :

```
@HiltAndroidApp
WinRescueApp

@AndroidEntryPoint
MainActivity

@Module @InstallIn(SingletonComponent)
RepositoryModule
  ├── ScriptRepository  ←bind─  ScriptRepositoryImpl
  └── SettingsRepository ←bind─ SettingsRepositoryImpl

@Singleton
HidKeyboardManager (auto-injecte via @Inject constructor)

@Singleton
RootManager (auto-injecte via @Inject constructor)

@HiltViewModel
HomeViewModel, WizardViewModel, SettingsViewModel
```

---

## Stack technique

| Composant | Version | Role |
|-----------|---------|------|
| Kotlin | JVM 17 | Langage principal |
| Jetpack Compose | BOM 2024.12.01 | UI declarative |
| Material 3 | via Compose BOM | Design system |
| Navigation Compose | 2.7.7 | Navigation typee entre ecrans |
| Hilt | 2.51.1 | Injection de dependances |
| libsu | 6.0.0 | Acces root (Shell.cmd, isAppGrantedRoot) |
| DataStore Preferences | 1.1.1 | Persistence des parametres |
| kotlinx-serialization | 1.7.1 | Deserialization des scripts JSON |
| kotlinx-coroutines | 1.8.1 | Operations asynchrones |

---

**Voir aussi** : [Architecture detaillee avec machines a etats](../docs/concepts/architecture.md) | [Protocole USB HID](USB-HID-Protocol.md) | [Guide de contribution](Contributing.md)
