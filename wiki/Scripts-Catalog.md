# Catalogue des scripts

Les 22 scripts de recuperation Windows disponibles dans WinRescue, classes par categorie.

**Retour** : [Accueil du wiki](Home.md) | **Voir aussi** : [Demarrage rapide](Getting-Started.md) | [Protocole USB HID](USB-HID-Protocol.md)

---

## Sommaire

- [Vue d'ensemble](#vue-densemble)
- [Recovery -- Recuperation](#recovery----recuperation)
- [Admin -- Administration](#admin----administration)
- [Repair -- Reparation](#repair----reparation)
- [Security -- Securite](#security----securite)
- [Network -- Reseau](#network----reseau)
- [Distribution des scripts](#distribution-des-scripts)

---

## Vue d'ensemble

| Categorie | Nombre | Description |
|-----------|--------|-------------|
| Recovery | 7 | Reinitialisation de mot de passe, PIN, recuperation de fichiers, restauration usine |
| Admin | 4 | Creation et activation de comptes administrateur |
| Repair | 4 | Reparation du boot, des fichiers systeme, mode sans echec |
| Security | 4 | Desactivation BitLocker, suppression de malware |
| Network | 3 | Activation RDP, reinitialisation reseau |
| **Total** | **22** | |

---

## Recovery -- Recuperation

### reset_password_win10

| Champ | Valeur |
|-------|--------|
| **Nom** | Reset mot de passe Windows 10 |
| **OS** | Windows 10 |
| **Difficulte** | Medium |
| **Duree estimee** | 10 min |

Reinitialise le mot de passe d'un compte local Windows 10 en passant par l'environnement de recuperation (WinRE) et l'invite de commandes. Utilise la commande `net user <username> <password>`.

**Entrees utilisateur** : nom d'utilisateur, nouveau mot de passe.

**Deroulement** : eteindre le PC, brancher le telephone, demarrer en spammant F8, naviguer dans WinRE vers Depannage > Options avancees > Invite de commandes, executer `net user`, redemarrer.

---

### reset_password_win11

| Champ | Valeur |
|-------|--------|
| **Nom** | Reset mot de passe Windows 11 |
| **OS** | Windows 11 |
| **Difficulte** | Medium |
| **Duree estimee** | 10 min |

Identique a la version Win10 mais adapte pour Windows 11 : utilise 3 extinctions forcees au demarrage au lieu de F8 pour declencher la reparation automatique et acceder a WinRE.

**Entrees utilisateur** : nom d'utilisateur, nouveau mot de passe.

---

### reset_pin_win11

| Champ | Valeur |
|-------|--------|
| **Nom** | Reset PIN Windows Hello (Win11) |
| **OS** | Windows 11 |
| **Difficulte** | Easy |
| **Duree estimee** | 8 min |

Supprime les donnees Windows Hello (PIN, empreinte digitale, reconnaissance faciale) en supprimant le dossier NGC (`C:\Windows\ServiceProfiles\LocalService\AppData\Local\Microsoft\NGC`) apres prise de possession via `takeown` et modification des permissions via `icacls`.

Au redemarrage, Windows demande de reconfigurer le PIN.

---

### recover_files_win10

| Champ | Valeur |
|-------|--------|
| **Nom** | Recuperation de fichiers (Win10) |
| **OS** | Windows 10 |
| **Difficulte** | Medium |
| **Duree estimee** | 10 min |

Copie les dossiers Documents, Images, Bureau et Telechargements vers une cle USB branchee au PC via la commande `xcopy` dans WinRE. Necessite une cle USB supplementaire branchee au PC cible.

**Entrees utilisateur** : lettre du lecteur USB de destination.

---

### recover_files_win11

| Champ | Valeur |
|-------|--------|
| **Nom** | Recuperation de fichiers (Win11) |
| **OS** | Windows 11 |
| **Difficulte** | Medium |
| **Duree estimee** | 10 min |

Identique a la version Win10, adapte pour la navigation WinRE de Windows 11.

---

### factory_reset_win10

| Champ | Valeur |
|-------|--------|
| **Nom** | Reinitialisation usine (Win10) |
| **OS** | Windows 10 |
| **Difficulte** | Advanced |
| **Duree estimee** | 45 min |

Reinitialise completement Windows 10 a son etat d'usine via WinRE ("Reinitialiser ce PC"). **Cette operation est irreversible et supprime toutes les donnees personnelles.**

---

### factory_reset_win11

| Champ | Valeur |
|-------|--------|
| **Nom** | Reinitialisation usine (Win11) |
| **OS** | Windows 11 |
| **Difficulte** | Advanced |
| **Duree estimee** | 45 min |

Identique a la version Win10, adapte pour Windows 11.

---

## Admin -- Administration

### create_admin_win10

| Champ | Valeur |
|-------|--------|
| **Nom** | Creation compte admin (Win10) |
| **OS** | Windows 10 |
| **Difficulte** | Medium |
| **Duree estimee** | 12 min |

Cree un compte local administrateur via `net user <username> <password> /add` suivi de `net localgroup administrators <username> /add` dans WinRE.

**Entrees utilisateur** : nom du nouveau compte, mot de passe.

---

### create_admin_win11

| Champ | Valeur |
|-------|--------|
| **Nom** | Creation compte admin (Win11) |
| **OS** | Windows 11 |
| **Difficulte** | Medium |
| **Duree estimee** | 12 min |

Identique a la version Win10, adapte pour Windows 11.

---

### enable_hidden_admin_win10

| Champ | Valeur |
|-------|--------|
| **Nom** | Activation admin cache (Win10) |
| **OS** | Windows 10 |
| **Difficulte** | Medium |
| **Duree estimee** | 10 min |

Active le compte Administrateur integre de Windows (desactive par defaut) via `net user administrator /active:yes` dans WinRE. Ce compte dispose de tous les privileges sans UAC.

---

### enable_hidden_admin_win11

| Champ | Valeur |
|-------|--------|
| **Nom** | Activation admin cache (Win11) |
| **OS** | Windows 11 |
| **Difficulte** | Medium |
| **Duree estimee** | 10 min |

Identique a la version Win10, adapte pour Windows 11.

---

## Repair -- Reparation

### repair_boot_win10

| Champ | Valeur |
|-------|--------|
| **Nom** | Reparation boot (Win10) |
| **OS** | Windows 10 |
| **Difficulte** | Advanced |
| **Duree estimee** | 20 min |

Repare le demarrage de Windows 10 via les outils `bootrec` dans WinRE :
- `bootrec /fixmbr` : repare le Master Boot Record
- `bootrec /fixboot` : repare le secteur de boot
- `bootrec /scanos` : detecte les installations Windows
- `bootrec /rebuildbcd` : reconstruit la base de donnees de boot (BCD)

---

### repair_boot_win11

| Champ | Valeur |
|-------|--------|
| **Nom** | Reparation boot (Win11) |
| **OS** | Windows 11 |
| **Difficulte** | Advanced |
| **Duree estimee** | 20 min |

Repare le demarrage de Windows 11. En plus des commandes `bootrec`, utilise `bcdboot` pour recreer les fichiers de demarrage EFI avec identification de la partition EFI via `diskpart`.

---

### sfc_dism_repair

| Champ | Valeur |
|-------|--------|
| **Nom** | Reparation systeme SFC/DISM |
| **OS** | Windows 10 et 11 |
| **Difficulte** | Medium |
| **Duree estimee** | 30 min |

Repare les fichiers systeme corrompus en deux etapes :
1. `sfc /scannow` : scanne et repare les fichiers systeme proteges
2. `DISM /Online /Cleanup-Image /RestoreHealth` : repare l'image systeme elle-meme (le magasin de composants)

Le pipeline complet passe par CheckHealth, ScanHealth puis RestoreHealth.

---

### force_safe_mode

| Champ | Valeur |
|-------|--------|
| **Nom** | Mode sans echec force |
| **OS** | Windows 10 et 11 |
| **Difficulte** | Medium |
| **Duree estimee** | 10 min |

Force le demarrage en Mode Sans Echec via `bcdedit /set {default} safeboot minimal`. Utile pour desinstaller des pilotes defectueux ou supprimer des malwares. Le Mode Sans Echec charge Windows avec un minimum de pilotes et services.

Pour desactiver le Mode Sans Echec au prochain demarrage : `bcdedit /deletevalue {default} safeboot`.

---

## Security -- Securite

### disable_bitlocker_win10

| Champ | Valeur |
|-------|--------|
| **Nom** | Desactivation BitLocker (Win10) |
| **OS** | Windows 10 |
| **Difficulte** | Advanced |
| **Duree estimee** | 15 min |

Deverrouille et desactive le chiffrement BitLocker sur le disque systeme :
1. `manage-bde -unlock C: -RecoveryPassword <cle>` : deverrouille avec la cle de recuperation
2. `manage-bde -off C:` : desactive le chiffrement (dechiffrement en arriere-plan)

**Entrees utilisateur** : cle de recuperation BitLocker (48 chiffres).

**Ne contourne pas le chiffrement** : la cle de recuperation legitime est obligatoire.

---

### disable_bitlocker_win11

| Champ | Valeur |
|-------|--------|
| **Nom** | Desactivation BitLocker (Win11) |
| **OS** | Windows 11 |
| **Difficulte** | Advanced |
| **Duree estimee** | 15 min |

Identique a la version Win10, adapte pour Windows 11.

---

### remove_malware_win10

| Champ | Valeur |
|-------|--------|
| **Nom** | Suppression malware (Win10) |
| **OS** | Windows 10 |
| **Difficulte** | Advanced |
| **Duree estimee** | 10 min |

Nettoie un systeme infecte via WinRE :
- Suppression des cles de registre Run (demarrage automatique du malware)
- Suppression du fichier executable malveillant dans system32
- Reinitialisation du proxy systeme

---

### remove_malware_win11

| Champ | Valeur |
|-------|--------|
| **Nom** | Suppression malware (Win11) |
| **OS** | Windows 11 |
| **Difficulte** | Advanced |
| **Duree estimee** | 10 min |

Identique a la version Win10, adapte pour Windows 11.

---

## Network -- Reseau

### enable_rdp_win10

| Champ | Valeur |
|-------|--------|
| **Nom** | Activation RDP (Win10) |
| **OS** | Windows 10 |
| **Difficulte** | Medium |
| **Duree estimee** | 3 min |

Active le Bureau a Distance (Remote Desktop Protocol) via :
- Modification du registre : `fDenyTSConnections=0`
- Activation de la regle de pare-feu correspondante

Permet ensuite la prise de controle a distance du PC.

---

### enable_rdp_win11

| Champ | Valeur |
|-------|--------|
| **Nom** | Activation RDP (Win11) |
| **OS** | Windows 11 |
| **Difficulte** | Medium |
| **Duree estimee** | 3 min |

Identique a la version Win10, adapte pour Windows 11.

---

### reset_network

| Champ | Valeur |
|-------|--------|
| **Nom** | Reset reseau complet |
| **OS** | Windows 10 et 11 |
| **Difficulte** | Easy |
| **Duree estimee** | 5 min |

Reinitialise completement la configuration reseau via :
- `netsh winsock reset` : reinitialise le catalogue Winsock
- `netsh int ip reset` : reinitialise TCP/IP
- `ipconfig /flushdns` : vide le cache DNS
- `ipconfig /release` et `ipconfig /renew` : renouvelle l'adresse IP
- Reinitialisation du proxy systeme

---

## Distribution des scripts

### Par difficulte

| Difficulte | Nombre | Pourcentage |
|------------|--------|-------------|
| Easy | 2 | 9% |
| Medium | 12 | 55% |
| Advanced | 8 | 36% |

### Par OS

| OS cible | Nombre |
|----------|--------|
| Windows 10 uniquement | 10 |
| Windows 11 uniquement | 10 |
| Windows 10 et 11 (Both) | 3 |

Les scripts marques "Both" partagent les memes commandes Windows sur les deux versions (force_safe_mode, sfc_dism_repair, reset_network). Cela represente un seul fichier JSON pour les deux OS.

---

**Voir aussi** : [Demarrage rapide](Getting-Started.md) | [Depannage](Troubleshooting.md) | [Guide de contribution](Contributing.md)
