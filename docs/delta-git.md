# Delta Git - WinRescue

## Analyse du 2026-03-22

### Informations generales

| Champ | Valeur |
|-------|--------|
| Projet | WinRescue |
| Package | `com.winrescue` |
| Version | 1.0.0 (versionCode 1) |
| Dernier tag | Pas de tag release - projet initial |
| Nombre de commits | 1 |
| Branche principale | main/master (unique) |
| Date premier commit | 2026-03-22 01:40:06 +0100 |
| Auteur | Rony Licha (rony@ronylicha.net) |

### Commit unique

```
3d96ab1 feat: initial WinRescue Android app
```

Type : `feat` (conventional commit) - Commit initial contenant l'integralite du projet.

### Statistiques fichiers

| Type | Nombre | Description |
|------|--------|-------------|
| `.kt` | 41 | Sources Kotlin (app + tests placeholders) |
| `.json` | 22 | Scripts de recovery (assets) |
| `.xml` | 7 | Manifest, layouts, resources Android |
| `.ttf` | 3 | Polices JetBrains Mono |
| `.kts` | 3 | Gradle build scripts |
| `.gitkeep` | 3 | Placeholders repertoires vides |
| `.properties` | 2 | Gradle properties |
| `.pro` | 1 | ProGuard rules |
| `.bat` | 1 | Gradle wrapper Windows |
| `.gitignore` | 1 | Exclusions Git |
| `gradlew` | 1 | Gradle wrapper Unix |
| **Total** | **85 fichiers** | **9 762 insertions** |

### Categorisation conventional commits

| Type | Nombre | Details |
|------|--------|---------|
| `feat` | 1 | Commit initial : app Android complete |
| `fix` | 0 | - |
| `refactor` | 0 | - |
| `docs` | 0 | - |
| `test` | 0 | - |
| `chore` | 0 | - |

### Stack technique

| Composant | Version / Detail |
|-----------|-----------------|
| Kotlin | JVM target 17 |
| Android SDK | compileSdk 35, minSdk 26, targetSdk 35 |
| Jetpack Compose | BOM 2024.12.01 |
| Material 3 | Via Compose BOM |
| Navigation Compose | 2.7.7 |
| Hilt (DI) | 2.51.1 |
| libsu (root) | 6.0.0 |
| DataStore | 1.1.1 |
| kotlinx-serialization | 1.7.1 |
| kotlinx-coroutines | 1.8.1 |
| JUnit | 4.13.2 |
| MockK | 1.13.13 |
| Turbine | 1.2.0 |
| Espresso | 3.6.1 |

### Architecture de l'app

```
com.winrescue/
  MainActivity.kt          # Activity Compose + Hilt
  WinRescueApp.kt          # Application class (Hilt entry point)
  data/
    model/                  # Enums, KeyAction, Script, ScriptStep
    repository/             # ScriptRepository (interface + impl)
    root/                   # RootManager, RootState, DisclaimerState
    settings/               # AppSettings, SettingsRepository
  di/                       # Hilt RepositoryModule
  ui/
    components/             # 8 composants reutilisables
    navigation/             # NavGraph + Routes (sealed class)
    screens/                # 5 ecrans (Home, ScriptDetail, Wizard, Success, Error)
    theme/                  # Color, Theme, Type (dark Material 3)
    viewmodel/              # 3 ViewModels (Home, Settings, Wizard)
  usb/                      # HidKeyMap, HidKeyboardManager, UsbConnectionState, UsbStateReceiver
```

### Scripts de recovery (22)

| Script | OS | Categorie |
|--------|-----|-----------|
| create_admin | Win10, Win11 | ADMIN |
| disable_bitlocker | Win10, Win11 | SECURITY |
| enable_hidden_admin | Win10, Win11 | ADMIN |
| enable_rdp | Win10, Win11 | NETWORK |
| factory_reset | Win10, Win11 | RECOVERY |
| force_safe_mode | Win10/11 (commun) | DIAGNOSTIC |
| recover_files | Win10, Win11 | RECOVERY |
| remove_malware | Win10, Win11 | SECURITY |
| repair_boot | Win10, Win11 | REPAIR |
| reset_network | Win10/11 (commun) | NETWORK |
| reset_password | Win10, Win11 | RECOVERY |
| reset_pin | Win11 uniquement | RECOVERY |
| sfc_dism_repair | Win10/11 (commun) | REPAIR |

### Documentation desynchronisee

| Constat | Detail |
|---------|--------|
| Pas de README.md | Aucun fichier README a la racine du projet |
| Dossiers docs vides | `concepts/`, `howto/`, `marketing/`, `reference/`, `tutorials/`, `public/` sont tous vides |
| Seul asset doc | `docs/assets/winrescue-logo.png` |
| Pas de CHANGELOG.md | Aucun changelog existant |
| Pas de CONTRIBUTING.md | - |
| Pas de LICENSE | - |
| Pas de documentation API/architecture | Les `.prompts/` contiennent des specs de recherche/plan mais pas de doc formelle |
| Tests absents | Repertoires `test/` et `androidTest/` ne contiennent que des `.gitkeep` |
