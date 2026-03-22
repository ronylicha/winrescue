# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added

- Initial WinRescue Android app (Kotlin, Jetpack Compose, Material 3)
- 22 recovery scripts couvrant Windows 10 et Windows 11 :
  - `create_admin` (Win10/11) : creation d'un compte administrateur
  - `disable_bitlocker` (Win10/11) : desactivation du chiffrement BitLocker
  - `enable_hidden_admin` (Win10/11) : activation du compte administrateur cache
  - `enable_rdp` (Win10/11) : activation du bureau a distance
  - `factory_reset` (Win10/11) : reinitialisation d'usine
  - `force_safe_mode` (commun) : forcer le demarrage en mode sans echec
  - `recover_files` (Win10/11) : recuperation de fichiers
  - `remove_malware` (Win10/11) : suppression de malware
  - `repair_boot` (Win10/11) : reparation du boot manager
  - `reset_network` (commun) : reinitialisation reseau complete
  - `reset_password` (Win10/11) : reinitialisation du mot de passe
  - `reset_pin` (Win11) : reinitialisation du code PIN
  - `sfc_dism_repair` (commun) : reparation systeme via SFC/DISM
- Moteur USB HID clavier avec support des layouts QWERTY US et AZERTY FR
- Configuration USB gadget via ConfigFS (root requis, /dev/hidg0)
- Detection et gestion root via libsu 6.0.0
- Ecran disclaimer obligatoire avant utilisation root
- Theme Material 3 dark personnalise (palette bleue/slate)
- Police JetBrains Mono (Regular, Medium, Bold)
- Dashboard Home avec KPIs (nombre de scripts, categories)
- Filtrage des scripts par OS (Win10, Win11, les deux)
- Recherche de scripts par nom/description
- Filtrage par categorie (Recovery, Admin, Repair, Security, Network, Diagnostic)
- Indicateur de difficulte par script (Easy, Medium, Advanced)
- Barre de statut USB en temps reel (connecte/deconnecte)
- Wizard d'execution pas-a-pas avec indicateur de progression
- Preview des sequences de touches HID avant execution
- Timer de compte a rebours entre les etapes
- Saisie utilisateur dans le wizard (texte, mot de passe, lettre, chiffre)
- Ecran de succes avec resume de l'execution
- Ecran d'erreur avec message detaille et possibilite de retenter
- Page de reglages (choix layout clavier, vitesse de frappe, delais)
- Persistance des parametres via DataStore Preferences
- Injection de dependances via Hilt (Dagger)
- Navigation Compose avec routes typees (sealed class)
- Architecture MVVM (3 ViewModels : Home, Settings, Wizard)
- Repository pattern (ScriptRepository, SettingsRepository)
- Serialisation JSON des scripts via kotlinx-serialization
- Support Android API 26+ (Android 8.0 Oreo et superieur)
- Configuration ProGuard pour le build release (minification activee)
- Infrastructure de tests preparee (JUnit 4, MockK, Turbine, Espresso)
- Icone d'application personnalisee (adaptive icon)
