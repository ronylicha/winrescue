# Demarrage rapide

Ce guide vous permet d'installer WinRescue et d'executer votre premier script de recuperation en 5 minutes.

**Retour** : [Accueil du wiki](Home.md)

---

## Sommaire

- [Pre-requis](#pre-requis)
- [Installation](#installation)
- [Premier lancement](#premier-lancement)
- [Executer un script](#executer-un-script)
- [Prochaines etapes](#prochaines-etapes)

---

## Pre-requis

Avant de commencer, verifiez que vous disposez de :

| Element | Requis | Detail |
|---------|--------|--------|
| Smartphone Android | 8.0+ (API 26) | Google Pixel ou OnePlus recommande |
| Root | Magisk ou KernelSU | Voir [Configuration root](Root-Setup.md) |
| Cable USB | USB-C / OTG | Cable de **donnees** (pas charge seule) |
| PC cible | Windows 10 ou 11 | Avec port USB fonctionnel |

### Verifier le root

Ouvrez un terminal sur votre telephone (Termux ou similaire) et executez :

```bash
su -c "id"
```

Si la commande retourne `uid=0(root)`, votre telephone est roote. Sinon, suivez le [guide de configuration root](Root-Setup.md).

### Verifier le cable

Branchez votre telephone a un PC fonctionnel. Si le PC detecte un peripherique USB (notification "appareil USB connecte"), le cable supporte les donnees. Les cables fournis avec les chargeurs rapides sont souvent charge-only et ne fonctionneront pas.

---

## Installation

### Option A : Build depuis les sources

```bash
# Cloner le depot
git clone https://github.com/ronylicha/winrescue.git
cd winrescue

# Build debug
./gradlew assembleDebug

# Installer via ADB
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Option B : APK pre-compile

Telechargez le dernier APK depuis la page [Releases](https://github.com/ronylicha/winrescue/releases) et installez-le :

```bash
adb install winrescue-1.0.0.apk
```

Ou activez "Sources inconnues" dans les parametres Android et ouvrez l'APK directement.

---

## Premier lancement

### 1. Accepter le disclaimer

Au premier lancement, WinRescue affiche un avertissement legal obligatoire. Lisez-le attentivement et acceptez-le pour continuer. Cette acceptation est enregistree avec un timestamp dans les preferences de l'application.

### 2. Autoriser le root

WinRescue demande l'acces root via libsu. Une popup Magisk ou KernelSU apparait :
- **Magisk** : appuyez sur "Autoriser"
- **KernelSU** : appuyez sur "Accorder"

### 3. Verification automatique

L'application effectue une verification en 3 etapes :

1. **Root disponible ?** -- Verifie que `su` est accessible et autorise
2. **`/dev/hidg0` existe ?** -- Verifie que le peripherique HID est cree
3. **`/dev/hidg0` accessible en ecriture ?** -- Verifie les permissions et le patch SELinux

Si le peripherique HID n'existe pas, WinRescue propose de le configurer automatiquement via configfs. Acceptez pour creer le gadget USB HID.

### 4. Dashboard

Une fois la verification reussie, le dashboard s'affiche avec :
- La barre de statut USB en haut (vert = connecte, rouge = deconnecte)
- Les KPI (nombre de scripts, categories)
- La liste des 22 scripts, filtrable par OS et categorie
- Une barre de recherche

---

## Executer un script

Prenons l'exemple du script "Reset reseau" (le plus rapide et le moins risque).

### Etape 1 : Preparer la connexion

1. Branchez votre telephone au PC cible via le cable USB-C/OTG
2. Verifiez que la barre de statut USB affiche "Connecte" (vert)

### Etape 2 : Selectionner le script

1. Sur le dashboard, cherchez "Reset reseau" ou filtrez par categorie "Network"
2. Appuyez sur la carte du script
3. Lisez la description et les avertissements eventuels

### Etape 3 : Lancer le wizard

1. Appuyez sur "Demarrer"
2. Suivez chaque etape :
   - Lisez l'instruction affichee
   - Effectuez l'action demandee sur le PC (si manuelle)
   - Appuyez sur "Confirmer et envoyer" pour les etapes automatisees
   - Repondez a la question de confirmation ("Voyez-vous... ?")
3. Si une etape echoue, suivez l'instruction de retry affichee

### Etape 4 : Fin

L'ecran de succes s'affiche quand toutes les etapes sont terminees. Debranchez le cable USB.

---

## Prochaines etapes

- **[Catalogue des scripts](Scripts-Catalog.md)** -- Decouvrez les 22 scripts disponibles et leurs cas d'usage
- **[Protocole USB HID](USB-HID-Protocol.md)** -- Comprenez comment WinRescue simule un clavier
- **[Depannage](Troubleshooting.md)** -- Si quelque chose ne fonctionne pas
- **[Architecture](Architecture.md)** -- Plongez dans le code et l'architecture technique
