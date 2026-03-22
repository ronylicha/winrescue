# Analyse Business & Produit -- WinRescue

## 1. Proposition de valeur

### Probleme resolu

Quand un PC Windows ne demarre plus, qu'un mot de passe est oublie ou qu'un malware bloque le systeme, la procedure standard exige :

- Une cle USB bootable avec un outil specialise (Hiren's Boot, Windows PE, Kon-Boot)
- OU un support d'installation Windows pour acceder a WinRE
- OU l'intervention d'un technicien avec son kit materiel

Ces solutions requierent du materiel supplementaire, une preparation prealable, et des connaissances techniques pour naviguer dans des interfaces en ligne de commande sans guidage.

### Ce que WinRescue apporte

WinRescue transforme un smartphone Android roote en clavier USB HID physique, capable d'envoyer automatiquement des sequences de touches et des commandes CMD a un PC Windows, guide par un wizard step-by-step.

Le telephone, connecte par cable USB-C/OTG au PC cible, est reconnu comme un clavier materiel standard. Il peut donc injecter des frappes clavier meme quand Windows ne demarre pas (ecran WinRE, invite de commandes pre-boot, menu BIOS).

### Pourquoi c'est unique

| Critere | Cle USB bootable | Technicien sur site | WinRescue |
|---------|------------------|---------------------|-----------|
| Materiel necessaire | Cle USB + ISO preparee | Kit complet + deplacements | Smartphone + cable USB |
| Preparation | Telecharger ISO, flasher cle | Planifier RDV | Installer l'app |
| Niveau requis | Intermediaire a expert | Expert | Debutant guide |
| Portabilite | Faible (cle dediee) | Faible (kit lourd) | Toujours dans la poche |
| Guidage | Aucun | Humain | Wizard interactif |
| Fonctionne sans ecran PC | Non | Oui | Oui (frappes aveugles) |

L'avantage fondamental : **tout professionnel IT ou utilisateur avance a deja un smartphone dans sa poche**. WinRescue elimine la dependance a du materiel dedie et transforme ce smartphone en outil de recuperation universel.

---

## 2. Personas

### Persona 1 : Technicien IT en entreprise (PME/ETI)

**Profil** : Responsable du parc informatique de 50 a 500 postes. Intervient quotidiennement sur des problemes de poste de travail. Possede un smartphone Android roote (Pixel ou OnePlus).

**Douleurs** :
- Doit se deplacer avec un kit de cles USB bootables, souvent perimees
- Les utilisateurs oublient regulierement leurs mots de passe locaux (comptes non-Azure AD)
- Les postes qui ne demarrent plus immobilisent les collaborateurs
- Pression pour resoudre rapidement, surtout en production

**Utilisation de WinRescue** :
- Reset de mot de passe local (script `reset_password_win10/11`)
- Creation de compte admin temporaire pour deblocage (script `create_admin_win10/11`)
- Reparation boot apres une mise a jour ratee (script `repair_boot_win10/11`)
- Activation du Mode Sans Echec pour diagnostic (script `force_safe_mode`)

**Valeur percue** : Gain de temps significatif (5-10 min au lieu de 30-60 min), plus besoin de preparer de cle USB, intervention possible partout immediatement.

### Persona 2 : Utilisateur avance / Power User

**Profil** : Particulier technophile, 25-45 ans, a l'aise avec le rooting Android et les commandes Windows. Aide regulierement famille et amis avec leurs problemes informatiques. "L'ami qui s'y connait en informatique."

**Douleurs** :
- Appele en urgence quand quelqu'un est bloque hors de son PC
- N'a pas toujours ses outils sous la main lors de ces interventions imprevues
- Doit expliquer des procedures complexes par telephone quand il ne peut pas se deplacer
- Risque de faire des erreurs de frappe dans les commandes CMD longues

**Utilisation de WinRescue** :
- Reset de mot de passe quand un proche est bloque (script `reset_password_win10/11`)
- Recuperation de fichiers importants avant un format (script `recover_files_win10/11`)
- Reinitialisation usine quand le PC est trop infecte (script `factory_reset_win10/11`)
- Reset reseau quand internet ne fonctionne plus (script `reset_network`)

**Valeur percue** : Fiabilite des commandes (pas de fautes de frappe), guidage clair meme sous stress, intervention rapide sans materiel dedie.

### Persona 3 : Administrateur systeme / MSP (Managed Service Provider)

**Profil** : Gere des dizaines de clients avec des centaines de postes. Se deplace entre les sites ou intervient a distance. Doit documenter chaque intervention.

**Douleurs** :
- Intervient sur des postes Windows heterogenes (Win10, Win11, versions variees)
- Doit gerer BitLocker qui bloque l'acces apres certaines operations
- Les malwares persistants resistent aux outils standards
- Besoin de solutions rapides pour minimiser le temps d'intervention facture

**Utilisation de WinRescue** :
- Desactivation BitLocker avec cle de recuperation (script `disable_bitlocker_win10/11`)
- Suppression de malware au demarrage (script `remove_malware_win10/11`)
- Activation RDP pour prise de controle a distance (script `enable_rdp_win10/11`)
- Reparation fichiers systeme (script `sfc_dism_repair`)
- Activation du compte Administrateur cache (script `enable_hidden_admin_win10/11`)

**Valeur percue** : Couverture complete des scenarios de recuperation, differentiation commerciale par rapport aux concurrents MSP, reduction du temps d'intervention.

---

## 3. Catalogue de scripts (22 scripts)

### Par categorie

#### RECOVERY (8 scripts)

| Script | OS | Difficulte | Duree | Description |
|--------|----|-----------:|------:|-------------|
| `reset_password_win10` | Win10 | Medium | 10 min | Reinitialise le mot de passe d'un compte local via WinRE + `net user` |
| `reset_password_win11` | Win11 | Medium | 10 min | Idem adapte Win11 (3 extinctions forcees au lieu de F8) |
| `reset_pin_win11` | Win11 | Easy | 8 min | Supprime les donnees Windows Hello (PIN/empreinte/visage) via suppression du dossier NGC |
| `recover_files_win10` | Win10 | Medium | 10 min | Copie Documents/Images/Bureau/Telechargements vers cle USB via `xcopy` dans WinRE |
| `recover_files_win11` | Win11 | Medium | 10 min | Idem adapte Win11 |
| `factory_reset_win10` | Win10 | Advanced | 45 min | Reinitialisation usine via WinRE ("Reinitialiser ce PC") |
| `factory_reset_win11` | Win11 | Advanced | 45 min | Idem adapte Win11 |
| *(Sous-total)* | | | | *Resout les cas d'urgence les plus frequents* |

#### ADMIN (4 scripts)

| Script | OS | Difficulte | Duree | Description |
|--------|----|-----------:|------:|-------------|
| `create_admin_win10` | Win10 | Medium | 12 min | Cree un compte local admin via `net user /add` + `net localgroup administrators /add` |
| `create_admin_win11` | Win11 | Medium | 12 min | Idem adapte Win11 |
| `enable_hidden_admin_win10` | Win10 | Medium | 10 min | Active le compte Administrateur integre (`net user administrator /active:yes`) |
| `enable_hidden_admin_win11` | Win11 | Medium | 10 min | Idem adapte Win11 |

#### REPAIR (4 scripts)

| Script | OS | Difficulte | Duree | Description |
|--------|----|-----------:|------:|-------------|
| `repair_boot_win10` | Win10 | Advanced | 20 min | Repare MBR/BCD via `bootrec /fixmbr`, `/fixboot`, `/scanos`, `/rebuildbcd` |
| `repair_boot_win11` | Win11 | Advanced | 20 min | Idem + `bcdboot` pour EFI + identification partition EFI via `diskpart` |
| `sfc_dism_repair` | Both | Medium | 30 min | Repare fichiers systeme via `sfc /scannow` + `DISM /RestoreHealth` |
| `force_safe_mode` | Both | Medium | 10 min | Force le demarrage en Mode Sans Echec via `bcdedit /set safeboot minimal` |

#### SECURITY (4 scripts)

| Script | OS | Difficulte | Duree | Description |
|--------|----|-----------:|------:|-------------|
| `disable_bitlocker_win10` | Win10 | Advanced | 15 min | Deverrouille et desactive BitLocker via `manage-bde -unlock` + `manage-bde -off` |
| `disable_bitlocker_win11` | Win11 | Advanced | 15 min | Idem adapte Win11 |
| `remove_malware_win10` | Win10 | Advanced | 10 min | Supprime malware : cle registre Run + fichier exe + reset proxy |
| `remove_malware_win11` | Win11 | Advanced | 10 min | Idem adapte Win11 |

#### NETWORK (4 scripts)

| Script | OS | Difficulte | Duree | Description |
|--------|----|-----------:|------:|-------------|
| `enable_rdp_win10` | Win10 | Medium | 3 min | Active RDP via registre (`fDenyTSConnections=0`) + regle pare-feu |
| `enable_rdp_win11` | Win11 | Medium | 3 min | Idem adapte Win11 |
| `reset_network` | Both | Easy | 5 min | Reset Winsock + TCP/IP + DNS + proxy via `netsh` et `ipconfig` |
| *(Sous-total)* | | | | *Restaure la connectivite reseau et l'acces distant* |

### Distribution de la difficulte

- **Easy** : 2 scripts (9%) -- accessibles sans experience prealable
- **Medium** : 12 scripts (55%) -- guidage wizard suffisant pour un utilisateur attentif
- **Advanced** : 8 scripts (36%) -- operations a risque necessitant une comprehension des consequences

### Couverture OS

- Scripts Win10 uniquement : 10
- Scripts Win11 uniquement : 10
- Scripts Win10 + Win11 ("Both") : 2 (force_safe_mode, sfc_dism_repair, reset_network -- 3 au total dans les JSON)

---

## 4. Avantages concurrentiels

### 4.1 Portabilite radicale : le smartphone comme outil de recuperation

L'outil de recuperation est le telephone que le technicien porte deja sur lui. Zero materiel supplementaire a transporter, preparer ou maintenir. Un cable USB-C/OTG suffit (cout < 5 EUR, taille d'un porte-cle).

Comparaison :
- Kit Hiren's Boot : cle USB dediee, ISO a mettre a jour, parfois incompatible UEFI Secure Boot
- Kon-Boot : licence payante (85-385 USD), incompatible Win11 recent
- Windows PE : necessite un build personnalise, lourd a preparer

### 4.2 Wizard step-by-step : democratisation de l'expertise

Chaque script est decompose en etapes atomiques avec :
- **Instruction principale** en langage clair
- **Detail explicatif** pour comprendre ce qui se passe techniquement
- **Image hint** (placeholder pour illustrations visuelles futures)
- **Question de confirmation** a chaque etape critique ("Voyez-vous l'ecran bleu ?")
- **Instructions de retry** en cas d'echec
- **Marquage des etapes critiques** (criticalStep) pour empecher la progression si echec

Ce pattern wizard transforme une operation de niveau expert (taper `bootrec /rebuildbcd` dans un terminal) en une experience guidee accessible a un technicien junior.

### 4.3 Automatisation des frappes : zero faute de frappe

Les commandes sont tapees caractere par caractere par le systeme HID avec un delai calibre (30-50ms entre chaque caractere). Avantages :
- Pas d'erreur de frappe sur des commandes longues et complexes (ex: `takeown /f C:\Windows\ServiceProfiles\LocalService\AppData\Local\Microsoft\NGC /r /d y`)
- Pas de confusion entre `/` et `\`
- Les variables utilisateur (nom de compte, mot de passe) sont injectees proprement via le systeme de templates (`{{username}}`, `{{new_password}}`)

### 4.4 Pas de cle USB bootable necessaire

WinRescue n'a pas besoin de booter depuis un support externe. Il utilise l'environnement de recuperation natif de Windows (WinRE), deja integre au PC. Le telephone agit comme un clavier physique, pas comme un disque de demarrage. Cela signifie :
- Fonctionne meme avec Secure Boot active
- Pas de modification du BIOS/UEFI necessaire
- Compatible avec toutes les marques de PC

### 4.5 Support multi-layout clavier

Le mapping HID supporte QWERTY US et AZERTY FR avec un systeme de scan codes physiques. Le layout AZERTY gere correctement les inversions de touches (a/q, z/w, m position), les symboles AltGr et la rangee des chiffres avec Shift. Configurable dans les parametres de l'application.

---

## 5. Points de friction et risques

### 5.1 Prerequis : Android roote

**Impact** : Reduit drastiquement l'audience potentielle.

Le root Android est obligatoire pour :
- Charger le module kernel `libcomposite` (creation du gadget USB HID)
- Ecrire sur `/dev/hidg0` (envoi des rapports HID)
- Configurer le sous-systeme configfs (creation du peripherique virtuel)

Solutions root supportees : Magisk et KernelSU (detection via `Shell.isAppGrantedRoot()` de libsu).

**Consequences** :
- Exclut les utilisateurs avec un smartphone non-roote (~95% du marche)
- Necessite un appareil compatible root (Pixel, OnePlus principalement)
- Le root annule generalement la garantie constructeur
- Les applications bancaires et certains services refusent de fonctionner sur un telephone roote

### 5.2 Compatibilite materielle limitee

**Appareils supportes** : Principalement Google Pixel et OnePlus.

**Appareils problematiques** : Samsung (TEE whitelist USB empeche la creation de gadget HID), Xiaomi (politique restrictive sur configfs), Huawei (bootloader verrouille).

Le systeme implemente une detection en 3 etapes (root -> hidg0 existe -> hidg0 accessible en ecriture) avec setup automatique du gadget configfs et patch SELinux (via `magiskpolicy` ou `ksud`). Mais la couche materielle sous-jacente (UDC -- USB Device Controller) doit supporter le mode gadget, ce qui n'est pas garanti.

### 5.3 Risque legal

WinRescue embarque un disclaimer legal obligatoire au premier lancement :

> "WinRescue est concu pour la recuperation de vos propres systemes ou de systemes dont vous avez l'autorisation explicite d'intervenir. L'utilisation de cet outil pour acceder a des systemes sans autorisation est illegale et punissable par la loi."

**Risques juridiques** :
- Acces non autorise a un systeme informatique (Article 323-1 du Code penal francais : 3 ans d'emprisonnement et 100 000 EUR d'amende)
- Utilisation comme outil de compromission par des acteurs malveillants
- Responsabilite de l'editeur si l'outil est utilise a des fins illicites
- Retrait potentiel du Google Play Store pour violation des politiques sur les outils de hacking

**Mesures d'attenuation en place** :
- Disclaimer obligatoire avec acceptation tracee (timestamp enregistre dans DataStore)
- Aucun contournement de chiffrement (BitLocker necessite la cle de recuperation legitime)
- Toutes les operations utilisent les outils natifs de Windows (net user, bootrec, bcdedit)

### 5.4 Fiabilite de la navigation WinRE

Les scripts utilisent des sequences de touches (fleches, Tab, Entree) pour naviguer dans l'interface WinRE. Cette navigation est fragile :
- La disposition des options peut varier selon la version de Windows, la langue et les mises a jour
- Le timing entre les frappes peut etre insuffisant sur des machines lentes
- Les dialogues inattendus (selection de compte admin, saisie de mot de passe) peuvent casser la sequence

**Attenuation** : Chaque etape avec des actions automatisees est marquee `retryable: true` avec une instruction de retry manuelle. La confirmation humaine entre chaque etape sert de filet de securite.

### 5.5 Risque d'endommagement du systeme cible

Certains scripts manipulent des composants critiques :
- `repair_boot` : reecriture du MBR/BCD, risque de rendre le PC totalement non-bootable
- `remove_malware` : suppression dans le registre et system32, risque de supprimer un composant legitime
- `factory_reset` : perte irreversible des donnees
- `disable_bitlocker` : exposition des donnees si le chiffrement etait la pour proteger des donnees sensibles

**Attenuation** : Niveaux de difficulte affiches, warnings explicites sur les scripts destructifs, marquage `criticalStep` pour les operations irreversibles.

### 5.6 Dependance a un cable physique

Le telephone doit etre physiquement connecte au PC cible par USB. Cela implique :
- Besoin d'un cable USB-C/OTG compatible
- Le port USB du PC doit etre fonctionnel
- Le cable ne doit pas etre debranche pendant l'envoi (warning affiche dans l'UI)

---

## 6. Terminologie metier

### Termes techniques fondamentaux

| Terme | Definition dans le contexte WinRescue |
|-------|---------------------------------------|
| **WinRE** (Windows Recovery Environment) | Environnement de recuperation integre a Windows. Interface bleu fonce avec les options Depannage, Options avancees, Invite de commandes. Accessible via 3 extinctions forcees au demarrage ou via F8 (Win10). C'est le point d'entree principal de la majorite des scripts WinRescue. |
| **USB HID** (Human Interface Device) | Protocole USB standard pour les peripheriques d'interface humaine (clavier, souris). WinRescue fait apparaitre le smartphone comme un clavier USB physique en envoyant des rapports HID de 8 octets sur `/dev/hidg0`. Le PC cible ne fait aucune distinction avec un vrai clavier. |
| **configfs** | Systeme de fichiers virtuel Linux (/sys/kernel/config/) permettant de configurer dynamiquement les peripheriques USB gadget. WinRescue l'utilise pour creer un gadget USB HID (clavier virtuel) avec les bons descripteurs (VendorID 0x1d6b, ProductID 0x0104, HID Report Descriptor standard keyboard). |
| **HID Report** | Paquet de 8 octets envoye sur `/dev/hidg0`. Octet 0 = masque des modificateurs (Ctrl, Shift, Alt, Win), octet 1 = reserve, octets 2-7 = scancodes des touches appuyees simultanement (max 6). Un rapport "vide" (8 zeros) signifie "toutes les touches relachees". |
| **Scancode HID** | Code numerique assignee a chaque touche physique du clavier (independant du layout). Ex: 'a' = 0x04, Entree = 0x28, F8 = 0x41, fleche bas = 0x51. WinRescue maintient un mapping complet dans `HidKeyMap.kt`. |
| **Safe Mode** (Mode Sans Echec) | Mode de demarrage Windows charge avec un minimum de pilotes et services. Active par WinRescue via `bcdedit /set {default} safeboot minimal`. Utile pour desinstaller des pilotes defectueux ou supprimer des malwares. |
| **bootrec** | Outil en ligne de commande Windows disponible dans WinRE pour reparer le demarrage. Sous-commandes : `/fixmbr` (repare le MBR), `/fixboot` (repare le secteur de boot de la partition), `/scanos` (detecte les installations Windows), `/rebuildbcd` (reconstruit la base de donnees de boot). |
| **bcdboot** | Outil Windows pour recreer les fichiers de demarrage EFI. Utilise par le script `repair_boot_win11` avec la syntaxe `bcdboot C:\Windows /s S: /f UEFI` ou S: est la partition EFI montee via diskpart. |
| **BCD** (Boot Configuration Data) | Base de donnees binaire contenant les parametres de demarrage de Windows (equivalent du boot.ini des anciennes versions). Stockee dans la partition EFI. Corrompue = PC ne demarre plus. |
| **MBR** (Master Boot Record) | Premier secteur du disque (512 octets) contenant le code de demarrage et la table des partitions. Remplace par GPT/UEFI sur les systemes modernes mais `bootrec /fixmbr` reste fonctionnel. |
| **UDC** (USB Device Controller) | Controleur materiel dans le SoC du smartphone qui gere la communication USB cote peripherique. Le script configfs ecrit le nom du UDC (ex: `a600000.dwc3`) dans le fichier `UDC` du gadget pour l'activer. Si absent, le gadget HID ne peut pas fonctionner. |
| **OTG** (On-The-Go) | Specification USB permettant a un appareil (smartphone) d'agir comme hote ou peripherique. Le cable USB-C/OTG est le lien physique entre le telephone WinRescue et le PC cible. |
| **libsu** | Bibliotheque Java/Kotlin de topjohnwu (auteur de Magisk) pour les operations root sur Android. WinRescue utilise `Shell.cmd()` pour executer des commandes root et `Shell.isAppGrantedRoot()` pour verifier l'acces root. |
| **Magisk / KernelSU** | Solutions de rooting Android. WinRescue supporte les deux : detection via `magiskpolicy` ou `ksud` pour le patch SELinux, `Shell.getShell()` pour initialiser la session root (declenche la popup d'autorisation). |
| **SELinux** | Systeme de securite Linux (Mandatory Access Control) present sur Android. Bloque par defaut l'acces des apps a `/dev/hidg0`. WinRescue contourne via `magiskpolicy --live` (Magisk) ou `ksud sepolicy patch` (KernelSU) pour ajouter une regle d'acces. |
| **net user** | Commande Windows pour gerer les comptes utilisateurs locaux. Syntaxe principale utilisee par WinRescue : `net user <username> <password>` (reset mdp), `net user <username> <password> /add` (creation), `net user administrator /active:yes` (activation du compte cache). |
| **bcdedit** | Editeur de la base de donnees de configuration de demarrage Windows (BCD). WinRescue l'utilise pour forcer le Mode Sans Echec (`bcdedit /set {default} safeboot minimal`) et pour le desactiver (`bcdedit /deletevalue {default} safeboot`). |
| **SFC** (System File Checker) | Outil Windows qui scanne et repare les fichiers systeme proteges. Commande : `sfc /scannow`. Compare chaque fichier systeme avec sa copie de reference et remplace les fichiers corrompus. |
| **DISM** (Deployment Image Servicing and Management) | Outil Windows de niveau superieur a SFC. Repare l'image systeme elle-meme (le "magasin de composants"). Pipeline WinRescue : CheckHealth -> ScanHealth -> RestoreHealth (ce dernier necessite internet ou un support d'installation). |
| **BitLocker** | Chiffrement de disque integre a Windows. WinRescue permet de le deverrouiller avec la cle de recuperation legitime (48 chiffres) via `manage-bde -unlock` puis de le desactiver via `manage-bde -off`. Ne contourne pas le chiffrement. |
| **NGC** (Next Generation Credentials) | Dossier Windows (`C:\Windows\ServiceProfiles\LocalService\AppData\Local\Microsoft\NGC`) stockant les donnees Windows Hello (PIN, empreintes, reconnaissance faciale). Le script `reset_pin_win11` le supprime apres prise de possession (`takeown`) et modification des permissions (`icacls`). |
| **Winsock** | Interface de programmation reseau Windows. `netsh winsock reset` reinitialise le catalogue a son etat par defaut, corrigeant les problemes causes par des malwares ou des configurations corrompues. |
| **KeyboardLayout** | Configuration du mapping entre les caracteres et les scancodes HID. WinRescue supporte QWERTY_US (defaut) et AZERTY_FR. Le layout est selectionnable dans les parametres et affecte l'envoi de tous les caracteres. |

### Architecture technique resumee

```
Smartphone Android (roote)
    |
    |-- [libsu] Shell root --> modprobe libcomposite
    |                      --> configfs gadget HID setup
    |                      --> SELinux policy patch
    |
    |-- [HidKeyboardManager] --> FileOutputStream(/dev/hidg0)
    |                         --> envoi rapports HID 8 octets
    |                         --> mapping char -> scancode (HidKeyMap)
    |
    |-- [WizardViewModel] --> charge Script JSON
    |                     --> execute ScriptStep sequentiellement
    |                     --> gere confirm/retry/error
    |
    === Cable USB-C/OTG ===
    |
PC Windows (cible)
    |-- Reconnait un clavier USB standard
    |-- Recoit les frappes comme si un humain tapait
    |-- Execute les commandes dans CMD/WinRE
```

---

*Document genere le 2026-03-22 a partir de l'analyse du code source WinRescue.*
