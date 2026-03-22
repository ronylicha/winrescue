![WinRescue](docs/assets/winrescue-logo-dark.png)

# WinRescue

**Transformez votre smartphone Android roote en outil de recuperation universel.** WinRescue utilise le protocole USB HID pour simuler un clavier physique sur un PC Windows, et execute des commandes shell locales pour la maintenance Android -- le tout guide par un wizard interactif step-by-step, sans cle USB bootable ni connaissances avancees.

![Dashboard](docs/assets/winrescue-hero.png)

---

## Fonctionnalites

### Windows Recovery (22 scripts via USB HID)

- **Reset mot de passe** (Win10 / Win11) -- Reinitialise le mot de passe d'un compte local via WinRE
- **Reset PIN Windows Hello** (Win11) -- Supprime les donnees NGC
- **Recuperation de fichiers** (Win10 / Win11) -- Copie Documents/Images/Bureau vers USB via WinRE
- **Reinitialisation usine** (Win10 / Win11) -- Restaure Windows a son etat d'origine
- **Creation compte admin** (Win10 / Win11) -- Cree un compte local administrateur
- **Activation admin cache** (Win10 / Win11) -- Active le compte Administrateur integre
- **Reparation boot** (Win10 / Win11) -- Repare MBR/BCD via `bootrec` et `bcdboot`
- **Reparation SFC/DISM** (Win10 + Win11) -- Restaure les fichiers systeme corrompus
- **Mode sans echec** (Win10 + Win11) -- Force le demarrage en Safe Mode
- **Desactivation BitLocker** (Win10 / Win11) -- Deverrouille et desactive le chiffrement
- **Suppression malware** (Win10 / Win11) -- Nettoie registre Run et fichiers malveillants
- **Activation RDP** (Win10 / Win11) -- Active le Bureau a Distance
- **Reset reseau** (Win10 + Win11) -- Reinitialise Winsock, TCP/IP, DNS

### Android (8 scripts via shell root)

- **Vider le cache** -- Supprime le cache de toutes les applications (`pm trim-caches`)
- **Reset WiFi/Bluetooth** -- Reinitialise les parametres reseau Android
- **Forcer l'arret d'une app** -- Arrete et vide le cache d'un package
- **Recalibrer la batterie** -- Reset des statistiques batterie
- **Desactiver une app systeme** -- Desactive le bloatware sans suppression
- **Changer les DNS** -- Configure DNS prive (Cloudflare/Google/Quad9)
- **Modifier le DPI** -- Change la densite d'affichage
- **Diagnostic systeme** -- Genere un rapport complet (CPU/RAM/stockage/batterie)

![Features](docs/assets/winrescue-features.png)

---

## Pre-requis

| Element | Detail |
|---------|--------|
| **Smartphone** | Android 8.0+ (API 26) |
| **Root** | Magisk ou KernelSU |
| **Kernel** | `CONFIG_USB_CONFIGFS_F_HID=y` (compile ou module `libcomposite`) |
| **Cable** | USB-C / OTG (cable de donnees, pas charge seule) |
| **Appareils testes** | Unihertz Titan 2 (auto-setup), Google Pixel, OnePlus |
| **Appareils non compatibles** | Samsung (TEE whitelist), Xiaomi (configfs restrictif), Huawei (bootloader verrouille) |

> **Note** : Le root est obligatoire pour creer le peripherique USB HID virtuel (`/dev/hidg0`) via configfs et ecrire les rapports HID.

---

## Installation

### Depuis les sources

```bash
# Cloner le depot
git clone https://github.com/ronylicha/winrescue.git
cd winrescue

# Build debug
./gradlew assembleDebug

# Installer sur l'appareil connecte
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Build release

```bash
# Build release (necessite la configuration de la cle de signature)
./gradlew assembleRelease
```

### Configuration minimale de build

- JDK 17
- Android SDK 35
- Gradle 8.x (wrapper inclus)

---

## Architecture

### Structure du projet

```
com.winrescue/
├── MainActivity.kt              # Activity Compose + point d'entree Hilt
├── WinRescueApp.kt              # Application class (Hilt)
├── data/
│   ├── model/                   # Modeles de donnees
│   │   ├── Script.kt            # Script de recuperation (id, steps, inputs)
│   │   ├── ScriptStep.kt        # Etape du wizard (instruction, actions, confirm)
│   │   ├── KeyAction.kt         # Action HID (string, key, combination, wait, repeat, template, shell)
│   │   └── Enums.kt             # OsTarget (WIN10/WIN11/ANDROID/BOTH), ScriptCategory, Difficulty
│   ├── repository/              # Acces aux scripts JSON (assets)
│   │   ├── ScriptRepository.kt  # Interface
│   │   └── ScriptRepositoryImpl.kt
│   ├── root/                    # Gestion root et HID
│   │   ├── RootManager.kt       # Detection root, setup configfs, patch SELinux
│   │   ├── RootState.kt         # Machine a etats (6 etats)
│   │   └── DisclaimerState.kt   # Etat du disclaimer legal
│   └── settings/                # Preferences utilisateur
│       ├── AppSettings.kt       # Data class (layout, delays, debug)
│       ├── SettingsRepository.kt
│       └── SettingsRepositoryImpl.kt
├── di/
│   └── RepositoryModule.kt      # Hilt bindings
├── ui/
│   ├── components/              # Composants reutilisables
│   │   ├── UsbStatusBar.kt      # Indicateur USB temps reel
│   │   ├── ScriptCard.kt        # Carte de script sur le dashboard
│   │   ├── DifficultyBadge.kt   # Badge Easy/Medium/Advanced
│   │   ├── OsSelector.kt        # Filtre Win10/Win11/Both
│   │   ├── WarningBanner.kt     # Banniere d'avertissement
│   │   ├── StepIndicator.kt     # Progression du wizard
│   │   ├── CountdownTimer.kt    # Timer entre les etapes
│   │   ├── HintImage.kt         # Chargement images hint depuis assets/hints/
│   │   └── KeySequencePreview.kt # Preview des touches HID + shell
│   ├── screens/                 # Ecrans principaux
│   │   ├── HomeScreen.kt        # Dashboard + recherche + filtres
│   │   ├── ScriptDetailScreen.kt # Detail d'un script + inputs
│   │   ├── WizardStepScreen.kt  # Execution step-by-step
│   │   ├── SettingsScreen.kt    # Reglages
│   │   ├── SuccessScreen.kt     # Fin de script reussie
│   │   └── ErrorScreen.kt       # Gestion d'erreur + retry
│   ├── viewmodel/               # MVVM ViewModels
│   │   ├── HomeViewModel.kt     # Filtrage, recherche, KPI
│   │   ├── WizardViewModel.kt   # Execution des etapes + envoi HID
│   │   └── SettingsViewModel.kt # Persistence des preferences
│   ├── navigation/              # Navigation Compose
│   │   ├── Routes.kt            # Sealed class de routes typees
│   │   └── NavGraph.kt          # Graphe de navigation
│   └── theme/                   # Material 3 dark theme
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
└── usb/                         # Couche USB HID
    ├── HidKeyboardManager.kt    # Envoi des rapports HID sur /dev/hidg0
    ├── HidKeyMap.kt             # Mapping caractere -> scancode (QWERTY/AZERTY)
    ├── UsbConnectionState.kt    # Etat de connexion USB
    └── UsbStateReceiver.kt      # BroadcastReceiver USB
```

### Flux HID complet

```
┌──────────────────────────────────────────────────────────┐
│                    SMARTPHONE ANDROID                     │
│                                                          │
│  ┌──────────────┐    ┌───────────────┐    ┌───────────┐ │
│  │ WizardScreen │───>│ WizardVM      │───>│ HidKeyboard│ │
│  │ (UI Compose) │    │ (orchestrate) │    │ Manager    │ │
│  └──────────────┘    └───────────────┘    └─────┬─────┘ │
│                                                  │       │
│  ┌──────────────┐    ┌───────────────┐    ┌─────▼─────┐ │
│  │ ScriptJSON   │───>│ ScriptRepo    │    │ HidKeyMap  │ │
│  │ (30 scripts) │    │ (deserialize) │    │ (scancode) │ │
│  └──────────────┘    └───────────────┘    └─────┬─────┘ │
│                                                  │       │
│  ┌──────────────┐    ┌───────────────┐    ┌─────▼─────┐ │
│  │ RootManager  │───>│ configfs      │    │/dev/hidg0 │ │
│  │ (libsu 6.0)  │    │ gadget setup  │    │(8-byte HID│ │
│  └──────────────┘    └───────────────┘    │ reports)  │ │
│                                            └─────┬─────┘ │
└──────────────────────────────────────────────────┼───────┘
                                                   │
                                          Cable USB-C/OTG
                                                   │
┌──────────────────────────────────────────────────┼───────┐
│                     PC WINDOWS                    │       │
│                                                  │       │
│  ┌────────────────────────┐    ┌─────────────────▼─────┐ │
│  │ WinRE / CMD / Desktop  │<───│ Pilote clavier USB    │ │
│  │ (recoit les frappes)   │    │ (reconnait HID std)   │ │
│  └────────────────────────┘    └───────────────────────┘ │
└──────────────────────────────────────────────────────────┘
```

![Flux HID](docs/assets/winrescue-hid-flow.png)

---

## Scripts disponibles

| ID | Nom | OS | Categorie | Difficulte | Duree |
|----|-----|----|-----------|------------|-------|
| `reset_password_win10` | Reset mot de passe | Win10 | Recovery | Medium | 10 min |
| `reset_password_win11` | Reset mot de passe | Win11 | Recovery | Medium | 10 min |
| `reset_pin_win11` | Reset PIN Windows Hello | Win11 | Recovery | Easy | 8 min |
| `recover_files_win10` | Recuperation fichiers | Win10 | Recovery | Medium | 10 min |
| `recover_files_win11` | Recuperation fichiers | Win11 | Recovery | Medium | 10 min |
| `factory_reset_win10` | Reinitialisation usine | Win10 | Recovery | Advanced | 45 min |
| `factory_reset_win11` | Reinitialisation usine | Win11 | Recovery | Advanced | 45 min |
| `create_admin_win10` | Creation compte admin | Win10 | Admin | Medium | 12 min |
| `create_admin_win11` | Creation compte admin | Win11 | Admin | Medium | 12 min |
| `enable_hidden_admin_win10` | Activation admin cache | Win10 | Admin | Medium | 10 min |
| `enable_hidden_admin_win11` | Activation admin cache | Win11 | Admin | Medium | 10 min |
| `repair_boot_win10` | Reparation boot | Win10 | Repair | Advanced | 20 min |
| `repair_boot_win11` | Reparation boot | Win11 | Repair | Advanced | 20 min |
| `sfc_dism_repair` | Reparation SFC/DISM | Both | Repair | Medium | 30 min |
| `force_safe_mode` | Mode sans echec | Both | Diagnostic | Medium | 10 min |
| `disable_bitlocker_win10` | Desactivation BitLocker | Win10 | Security | Advanced | 15 min |
| `disable_bitlocker_win11` | Desactivation BitLocker | Win11 | Security | Advanced | 15 min |
| `remove_malware_win10` | Suppression malware | Win10 | Security | Advanced | 10 min |
| `remove_malware_win11` | Suppression malware | Win11 | Security | Advanced | 10 min |
| `enable_rdp_win10` | Activation RDP | Win10 | Network | Medium | 3 min |
| `enable_rdp_win11` | Activation RDP | Win11 | Network | Medium | 3 min |
| `reset_network` | Reset reseau complet | Both | Network | Easy | 5 min |
| `android_clear_app_cache` | Vider le cache | Android | Repair | Easy | 1 min |
| `android_reset_wifi` | Reset WiFi/Bluetooth | Android | Network | Medium | 2 min |
| `android_force_stop_app` | Forcer arret app | Android | Repair | Easy | 1 min |
| `android_battery_stats_reset` | Recalibrer batterie | Android | Diagnostic | Easy | 1 min |
| `android_disable_bloatware` | Desactiver app systeme | Android | Admin | Medium | 1 min |
| `android_dns_change` | Changer les DNS | Android | Network | Easy | 1 min |
| `android_screen_density` | Modifier le DPI | Android | Admin | Easy | 1 min |
| `android_system_info` | Diagnostic complet | Android | Diagnostic | Easy | 2 min |

---

## Configuration

### Parametres disponibles

| Parametre | Defaut | Description |
|-----------|--------|-------------|
| `keyboardLayout` | `QWERTY_US` | Layout clavier du PC cible (`QWERTY_US`, `AZERTY_FR`) |
| `charDelayMs` | `50` | Delai entre chaque caractere envoye (ms) |
| `stepDelayMs` | `1000` | Delai entre les etapes du wizard (ms) |
| `previewBeforeSend` | `true` | Afficher la preview des touches avant envoi |
| `debugMode` | `false` | Activer les logs detailles |
| `hidDevicePath` | `/dev/hidg0` | Chemin du peripherique HID |
| `language` | `auto` | Langue de l'interface (`auto`, `fr`, `en`) |

### Layout clavier

Le layout doit correspondre a la configuration clavier du PC cible (pas du telephone). Si le PC cible utilise un clavier AZERTY francais, selectionnez `AZERTY_FR` dans les reglages de WinRescue. Le mapping gere les inversions de touches (`a`/`q`, `z`/`w`, `m` en position `;`) et les symboles AltGr.

---

## USB HID Setup

WinRescue configure automatiquement le gadget USB HID au lancement. Le processus interne est le suivant :

### 1. Chargement du module kernel

```bash
modprobe libcomposite
```

### 2. Creation du gadget via configfs

```bash
cd /sys/kernel/config/usb_gadget/
mkdir -p g1 && cd g1

# Identite du peripherique
echo 0x1d6b > idVendor
echo 0x0104 > idProduct

# Description
mkdir -p strings/0x409
echo "WinRescue" > strings/0x409/product
echo "WinRescue HID Keyboard" > strings/0x409/manufacturer

# Configuration
mkdir -p configs/c.1/strings/0x409
echo "HID Config" > configs/c.1/strings/0x409/configuration
echo 120 > configs/c.1/MaxPower

# Fonction HID (clavier standard)
mkdir -p functions/hid.usb0
echo 1 > functions/hid.usb0/protocol    # Keyboard
echo 1 > functions/hid.usb0/subclass    # Boot Interface
echo 8 > functions/hid.usb0/report_length

# HID Report Descriptor (63 bytes, standard keyboard)
echo -ne '\x05\x01\x09\x06\xa1\x01...' > functions/hid.usb0/report_desc

# Lier et activer
ln -s functions/hid.usb0 configs/c.1/
UDC=$(ls /sys/class/udc/ | head -1)
echo "$UDC" > UDC
```

### 3. Patch SELinux

```bash
# Magisk
magiskpolicy --live "allow untrusted_app device chr_file { open read write ioctl }"

# KernelSU
ksud sepolicy patch "allow untrusted_app device chr_file { open read write ioctl }"
```

### 4. Envoi de rapports HID

Chaque frappe clavier est un rapport de 8 octets ecrit sur `/dev/hidg0` :

```
Octet 0 : Masque modificateurs (Ctrl=0x01, Shift=0x02, Alt=0x04, Win=0x08)
Octet 1 : Reserve (0x00)
Octets 2-7 : Scancodes des touches appuyees (max 6 simultanees)
```

Exemple : taper `A` (Shift + scancode 0x04) :

```
[0x02, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00]  # Appui
[0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00]  # Relache
```

---

## Stack technique

| Composant | Version |
|-----------|---------|
| Kotlin | JVM 17 |
| Android SDK | compile 35 / min 26 / target 35 |
| Jetpack Compose | BOM 2024.12.01 |
| Material 3 | via Compose BOM |
| Navigation Compose | 2.7.7 |
| Hilt (DI) | 2.51.1 |
| libsu (root) | 6.0.0 |
| DataStore Preferences | 1.1.1 |
| kotlinx-serialization | 1.7.1 |
| kotlinx-coroutines | 1.8.1 |

---

## Contribution

### Branches

| Branche | Usage |
|---------|-------|
| `main` | Production stable, protegee |
| `develop` | Integration |
| `feature/xxx` | Nouvelles fonctionnalites |
| `fix/xxx` | Corrections de bugs |

### Conventions de commit

Format [Conventional Commits](https://www.conventionalcommits.org/) :

```
feat(scripts): ajouter script reset_password_win11_24h2
fix(hid): corriger le timing sur les machines lentes
docs(readme): mettre a jour la matrice de compatibilite
refactor(root): extraire la logique SELinux dans une classe dediee
```

### Ajouter un script Windows (USB HID)

1. Creer un fichier JSON dans `app/src/main/assets/scripts/`
2. Suivre la structure existante (voir `reset_password_win10.json` comme reference)
3. Chaque etape doit avoir une `instruction` claire, une `confirmQuestion` et etre `retryable` si applicable
4. Marquer les etapes irreversibles avec `criticalStep: true`
5. Tester sur un PC reel avec le bon layout clavier

### Ajouter un script Android (shell root)

1. Creer un fichier JSON dans `app/src/main/assets/scripts/` (prefixe `android_`)
2. Utiliser `"os": ["ANDROID"]` et `"type": "shell"` dans les actions
3. Chaque action shell doit avoir `"root": true` et une `"description"` claire
4. Les templates `{{input_id}}` sont supportes dans les commandes shell
5. Tester sur un appareil roote reel

### Pull Requests

- Description claire avec le contexte du changement
- Tests passes
- Review obligatoire avant merge sur `main`

---

## Licence

Ce projet est distribue sous la [licence GPL-3.0](LICENSE).

---

## Avertissement legal

> **WinRescue est concu exclusivement pour la recuperation de vos propres systemes ou de systemes dont vous avez l'autorisation explicite d'intervenir.**
>
> L'utilisation de cet outil pour acceder a des systemes informatiques sans autorisation est **illegale** et punissable par la loi (Article 323-1 du Code penal francais : 3 ans d'emprisonnement et 100 000 EUR d'amende).
>
> Les auteurs de WinRescue declinent toute responsabilite en cas d'usage abusif ou illicite de cet outil. En installant et en utilisant WinRescue, vous acceptez d'en assumer l'entiere responsabilite legale.
>
> WinRescue ne contourne aucun mecanisme de chiffrement. Les scripts BitLocker necessitent la cle de recuperation legitime. Toutes les operations utilisent les outils natifs de Windows (`net user`, `bootrec`, `bcdedit`, `sfc`, `DISM`).
