# Protocole USB HID

Comment WinRescue transforme un smartphone Android en clavier USB physique.

**Retour** : [Accueil du wiki](Home.md) | **Voir aussi** : [Architecture](Architecture.md) | [Configuration root](Root-Setup.md)

---

## Sommaire

- [Principe general](#principe-general)
- [USB HID en bref](#usb-hid-en-bref)
- [Le rapport clavier 8 octets](#le-rapport-clavier-8-octets)
- [Scancodes et modificateurs](#scancodes-et-modificateurs)
- [Layouts clavier (QWERTY / AZERTY)](#layouts-clavier-qwerty--azerty)
- [Configuration du gadget via configfs](#configuration-du-gadget-via-configfs)
- [HID Report Descriptor](#hid-report-descriptor)
- [Flux d'envoi dans WinRescue](#flux-denvoi-dans-winrescue)
- [Timing et delais](#timing-et-delais)

---

## Principe general

WinRescue utilise le sous-systeme USB Gadget de Linux pour creer un peripherique USB HID (Human Interface Device) sur le smartphone. Le PC cible, connecte via un cable USB-C/OTG, voit le telephone comme un **clavier USB standard** -- exactement comme un clavier physique branche en USB.

```
┌──────────────────┐         Cable USB          ┌────────────────────┐
│  Smartphone      │ ◄══════════════════════════►│  PC Windows        │
│  (peripherique)  │   /dev/hidg0 → rapports    │  (hote)            │
│                  │       HID 8 octets          │  Reconnait un      │
│  Mode USB Gadget │                             │  clavier standard  │
└──────────────────┘                             └────────────────────┘
```

L'avantage fondamental : le PC n'a besoin d'aucun pilote special, d'aucune connexion reseau et d'aucun logiciel installe. Le clavier USB fonctionne partout, y compris dans le BIOS, dans WinRE (l'environnement de recuperation Windows) et sur l'ecran de connexion.

---

## USB HID en bref

**HID** (Human Interface Device) est un protocole USB standard defini par le consortium USB-IF pour les peripheriques d'interface humaine : claviers, souris, manettes, etc.

Un clavier USB HID fonctionne par envoi de **rapports** (reports) :
- Le peripherique envoie periodiquement un rapport de 8 octets a l'hote
- Le rapport decrit l'etat actuel du clavier (quelles touches sont appuyees)
- L'hote interprete les rapports et genere les evenements clavier correspondants

Le protocole est **sans etat** : chaque rapport decrit la totalite de l'etat du clavier a un instant T. Pour simuler une frappe, il faut :
1. Envoyer un rapport avec la touche appuyee
2. Envoyer un rapport vide (toutes les touches relachees)

---

## Le rapport clavier 8 octets

Chaque rapport HID clavier fait exactement 8 octets :

```
Octet   Contenu                     Valeurs
──────  ──────────────────────────  ──────────────────────
  0     Masque des modificateurs    Bits : Ctrl, Shift, Alt, Meta (gauche et droite)
  1     Reserve                     Toujours 0x00
  2     Scancode touche 1           Code de la 1ere touche appuyee
  3     Scancode touche 2           Code de la 2eme touche (si simultanee)
  4     Scancode touche 3           Code de la 3eme touche
  5     Scancode touche 4           Code de la 4eme touche
  6     Scancode touche 5           Code de la 5eme touche
  7     Scancode touche 6           Code de la 6eme touche (max)
```

### Octet 0 : Masque des modificateurs

Chaque bit correspond a une touche de modification :

```
Bit 0 (0x01) : Left Ctrl
Bit 1 (0x02) : Left Shift
Bit 2 (0x04) : Left Alt
Bit 3 (0x08) : Left Meta (Windows)
Bit 4 (0x10) : Right Ctrl
Bit 5 (0x20) : Right Shift
Bit 6 (0x40) : Right Alt (AltGr)
Bit 7 (0x80) : Right Meta (Windows)
```

Les bits peuvent etre combines. Exemple : `Ctrl+Shift` = `0x01 | 0x02` = `0x03`.

### Octets 2-7 : Scancodes

Jusqu'a 6 touches non-modificatrices peuvent etre appuyees simultanement. Les scancodes HID sont independants du layout clavier -- ils identifient la **position physique** de la touche.

### Rapport vide

Le rapport `[0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00]` signifie "aucune touche appuyee" et correspond au relachement de toutes les touches.

---

## Scancodes et modificateurs

### Touches speciales

| Touche | Scancode | Hex |
|--------|----------|-----|
| A | 4 | 0x04 |
| B | 5 | 0x05 |
| ... | ... | ... |
| Z | 29 | 0x1D |
| 1 | 30 | 0x1E |
| 2 | 31 | 0x1F |
| ... | ... | ... |
| 0 | 39 | 0x27 |
| Enter | 40 | 0x28 |
| Escape | 41 | 0x29 |
| Backspace | 42 | 0x2A |
| Tab | 43 | 0x2B |
| Space | 44 | 0x2C |
| F1 | 58 | 0x3A |
| F2 | 59 | 0x3B |
| ... | ... | ... |
| F8 | 65 | 0x41 |
| F12 | 69 | 0x45 |
| Delete | 76 | 0x4C |
| Right Arrow | 79 | 0x4F |
| Left Arrow | 80 | 0x50 |
| Down Arrow | 81 | 0x51 |
| Up Arrow | 82 | 0x52 |

### Exemples de rapports

**Taper la lettre `a`** (minuscule, QWERTY) :
```
Appui :    [0x00, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00]
Relache :  [0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00]
```

**Taper `A`** (majuscule = Shift + a) :
```
Appui :    [0x02, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00]
Relache :  [0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00]
```

**Combinaison Ctrl+Alt+Delete** :
```
Appui :    [0x05, 0x00, 0x4C, 0x00, 0x00, 0x00, 0x00, 0x00]
             │                │
             │                └── Delete (0x4C)
             └── Ctrl (0x01) | Alt (0x04) = 0x05
Relache :  [0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00]
```

**Appuyer sur F8** :
```
Appui :    [0x00, 0x00, 0x41, 0x00, 0x00, 0x00, 0x00, 0x00]
Relache :  [0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00]
```

---

## Layouts clavier (QWERTY / AZERTY)

Les scancodes HID correspondent a des **positions physiques** de touches, pas a des caracteres. Cela signifie que le scancode 0x04 correspond toujours a la touche en position "A" sur un clavier QWERTY US, quelle que soit la configuration logicielle du PC.

### Probleme du layout

Sur un PC configure en AZERTY, la touche physique en position "A" (QWERTY) produit le caractere `q`. Pour taper `a` sur un PC AZERTY, il faut envoyer le scancode de la touche en position "Q" (QWERTY) = 0x14.

### Solution WinRescue

WinRescue maintient deux tables de mapping dans `HidKeyMap.kt` :

- **QWERTY US** : mapping direct (caractere 'a' -> scancode 0x04)
- **AZERTY FR** : mapping inverse (caractere 'a' -> scancode 0x14, position du Q physique)

Le layout doit etre configure dans les **reglages de WinRescue** pour correspondre a la configuration du PC cible.

### Particularites AZERTY

| Caractere | QWERTY | AZERTY |
|-----------|--------|--------|
| `a` | 0x04 (sans modif) | 0x14 (position Q) |
| `q` | 0x14 | 0x04 (position A) |
| `z` | 0x1D | 0x1A (position W) |
| `w` | 0x1A | 0x1D (position Z) |
| `m` | 0x10 | 0x33 (position ;) |
| `1` | 0x1E (sans modif) | 0x1E + Shift |
| `&` | 0x24 + Shift | 0x1E (sans modif) |
| `{` | 0x2F + Shift | 0x21 + AltGr |
| `@` | 0x1F + Shift | 0x27 + AltGr |

Les caracteres non-ASCII (e, e, c, a) ne sont pas supportes directement par le protocole HID basique.

---

## Configuration du gadget via configfs

Pour que le smartphone apparaisse comme un clavier USB, WinRescue utilise le sous-systeme **configfs** du noyau Linux pour creer un gadget USB HID.

### Pre-requis

- Acces root (`su`)
- Module kernel `libcomposite` disponible
- USB Device Controller (UDC) compatible mode gadget

### Procedure

```bash
# 1. Charger le module
modprobe libcomposite

# 2. Creer le gadget
cd /sys/kernel/config/usb_gadget/
mkdir -p g1 && cd g1

# 3. Identite USB
echo 0x1d6b > idVendor     # Linux Foundation
echo 0x0104 > idProduct    # Multifunction Composite Gadget

# 4. Descriptions textuelles
mkdir -p strings/0x409
echo "WinRescue" > strings/0x409/product
echo "WinRescue HID Keyboard" > strings/0x409/manufacturer

# 5. Configuration
mkdir -p configs/c.1/strings/0x409
echo "HID Config" > configs/c.1/strings/0x409/configuration
echo 120 > configs/c.1/MaxPower

# 6. Fonction HID
mkdir -p functions/hid.usb0
echo 1 > functions/hid.usb0/protocol       # 1 = Keyboard
echo 1 > functions/hid.usb0/subclass       # 1 = Boot Interface
echo 8 > functions/hid.usb0/report_length   # 8 octets par rapport

# 7. HID Report Descriptor (63 bytes)
echo -ne '\x05\x01\x09\x06\xa1\x01...' > functions/hid.usb0/report_desc

# 8. Lier la fonction a la configuration
ln -s functions/hid.usb0 configs/c.1/

# 9. Activer le gadget sur le UDC
UDC=$(ls /sys/class/udc/ | head -1)
echo "$UDC" > UDC
```

Apres l'activation, le fichier `/dev/hidg0` est cree. C'est sur ce fichier que WinRescue ecrit les rapports HID.

### Patch SELinux

Android bloque par defaut l'acces des applications a `/dev/hidg0` via SELinux. WinRescue applique un patch :

```bash
# Magisk
magiskpolicy --live "allow untrusted_app device chr_file { open read write ioctl }"

# KernelSU
ksud sepolicy patch "allow untrusted_app device chr_file { open read write ioctl }"
```

---

## HID Report Descriptor

Le Report Descriptor est un binaire de 63 octets qui decrit la structure des rapports au systeme hote. Il indique :

- C'est un clavier (Usage Page: Generic Desktop, Usage: Keyboard)
- 8 bits de modificateurs (Ctrl, Shift, Alt, Meta x2)
- 1 octet reserve
- 6 octets de scancodes
- 5 LEDs (Num Lock, Caps Lock, Scroll Lock, Compose, Kana)

Le PC lit ce descripteur une seule fois a la connexion pour savoir comment interpreter les rapports de 8 octets.

---

## Flux d'envoi dans WinRescue

### Pour un caractere

```
Caractere 'a'
     │
     ▼
HidKeyMap.charToReport('a', QWERTY_US)
     │
     ├── modifier = 0x00 (pas de Shift)
     ├── scancode = 0x04
     ▼
Rapport = [0x00, 0x00, 0x04, 0x00, 0x00, 0x00, 0x00, 0x00]
     │
     ▼
HidKeyboardManager.sendReport(rapport)
     │
     ├── FileOutputStream.write(rapport)  → /dev/hidg0
     ├── delay(50ms)
     │
     ▼
HidKeyboardManager.sendReport(EMPTY_REPORT)  → relache
     │
     ▼
delay(50ms)  → delai entre caracteres
```

### Pour une chaine de caracteres

Chaque caractere est envoye sequentiellement : `press → delay → release → delay → caractere suivant`.

### Pour un template

Les placeholders `{{variable}}` sont remplaces par les valeurs saisies par l'utilisateur dans le wizard avant l'envoi.

Exemple : `net user {{username}} {{new_password}}` avec `username=Jean` et `new_password=P@ss123` donne `net user Jean P@ss123`.

---

## Timing et delais

| Parametre | Defaut | Configurable | Description |
|-----------|--------|-------------|-------------|
| Duree d'appui | 50 ms | Non | Duree pendant laquelle la touche est maintenue |
| Duree de relache | 50 ms | Non | Duree du rapport vide apres relache |
| Delai inter-caractere | 50 ms | Oui (`charDelayMs`) | Pause entre chaque caractere d'une chaine |
| Delai entre repetitions | 100 ms | Par script | Pause entre chaque repetition (`RepeatKey`) |
| Attente avant envoi | Variable | Par etape | Pause avant de commencer l'envoi d'une etape |
| Attente apres envoi | Variable | Par etape | Pause apres avoir termine l'envoi d'une etape |

Sur les machines lentes (HDD, peu de RAM), augmenter le `charDelayMs` dans les reglages de WinRescue peut ameliorer la fiabilite de la saisie.

---

**Voir aussi** : [Architecture](Architecture.md) | [Catalogue des scripts](Scripts-Catalog.md) | [Depannage](Troubleshooting.md)
