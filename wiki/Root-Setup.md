# Configuration root

Guide pour preparer votre smartphone Android pour WinRescue.

**Retour** : [Accueil du wiki](Home.md) | **Voir aussi** : [Demarrage rapide](Getting-Started.md) | [Depannage](Troubleshooting.md)

---

## Sommaire

- [Pourquoi le root est necessaire](#pourquoi-le-root-est-necessaire)
- [Appareils compatibles](#appareils-compatibles)
- [Magisk](#magisk)
- [KernelSU](#kernelsu)
- [Verification du root](#verification-du-root)
- [Verification du gadget HID](#verification-du-gadget-hid)
- [Desactivation SELinux](#desactivation-selinux)

---

## Pourquoi le root est necessaire

WinRescue necessite l'acces root pour trois operations :

1. **Charger le module kernel `libcomposite`** -- Ce module permet de creer des peripheriques USB gadget (dont le clavier HID). Il est present sur la plupart des kernels Android mais ne peut etre charge que par root.

2. **Configurer le gadget USB via configfs** -- La creation du peripherique HID passe par l'ecriture de fichiers dans `/sys/kernel/config/usb_gadget/`. Ces fichiers sont accessibles uniquement en root.

3. **Ecrire sur `/dev/hidg0`** -- Le peripherique HID cree est un fichier de peripherique character. L'acces en ecriture requiert des permissions root et un patch SELinux.

Sans root, aucune de ces operations n'est possible et WinRescue ne peut pas fonctionner.

---

## Appareils compatibles

### Recommandes

| Appareil | Root | USB Gadget | Notes |
|----------|------|------------|-------|
| Google Pixel (3+) | Magisk / KSU | Fonctionne | Reference de compatibilite |
| OnePlus (6+) | Magisk / KSU | Fonctionne | Bootloader facile a deverrouiller |

### Partiellement compatibles

| Appareil | Root | USB Gadget | Probleme |
|----------|------|------------|----------|
| Xiaomi | Magisk | Variable | Politique restrictive sur configfs, certains modeles bloquent l'acces |
| Motorola | Magisk | Variable | Depends du modele et de la version kernel |

### Non compatibles

| Appareil | Raison |
|----------|--------|
| Samsung | TEE (Trusted Execution Environment) whitelist USB empeche la creation de gadgets HID personnalises |
| Huawei (recent) | Bootloader verrouille, impossible de rooter |
| Appareils non-rootables | WinRescue necessite imperativement le root |

La compatibilite depend principalement de deux facteurs :
- Le **bootloader** doit etre deverrouillable
- Le **USB Device Controller (UDC)** du SoC doit supporter le mode gadget

---

## Magisk

[Magisk](https://github.com/topjohnwu/Magisk) est la solution de root la plus repandue. Elle fonctionne en patchant l'image boot du telephone.

### Installation Magisk (resume)

1. **Deverrouiller le bootloader** :
   ```bash
   # Activer le deverrouillage OEM dans Parametres > Options pour les developpeurs
   adb reboot bootloader
   fastboot flashing unlock
   ```

2. **Telecharger le firmware stock** de votre appareil et extraire `boot.img`

3. **Patcher boot.img avec Magisk** :
   - Installer l'APK Magisk sur le telephone
   - Ouvrir Magisk > Installer > Selectionner et patcher un fichier
   - Selectionner le `boot.img` extrait
   - Magisk produit un `magisk_patched_xxxxx.img`

4. **Flasher l'image patchee** :
   ```bash
   adb reboot bootloader
   fastboot flash boot magisk_patched_xxxxx.img
   fastboot reboot
   ```

5. **Verifier** : ouvrir l'app Magisk, le statut doit afficher "Installe"

### Configuration Magisk pour WinRescue

- Ouvrir Magisk > Parametres
- Verifier que **Zygisk** est active
- Au premier lancement de WinRescue, Magisk affichera une popup d'autorisation root -- accorder l'acces

---

## KernelSU

[KernelSU](https://kernelsu.org/) est une alternative a Magisk qui fonctionne au niveau du noyau. Elle est parfois preferee car elle est plus difficile a detecter par les applications bancaires.

### Installation KernelSU (resume)

1. **Verifier la compatibilite** sur [kernelsu.org/guide/installation](https://kernelsu.org/guide/installation.html)

2. **Installer via boot image** :
   ```bash
   # Telecharger le boot image KSU pour votre appareil
   adb reboot bootloader
   fastboot flash boot boot-ksud.img
   fastboot reboot
   ```

3. **Installer le manager** : telecharger l'APK KernelSU Manager

4. **Accorder l'acces root a WinRescue** dans le manager KernelSU

### Patch SELinux avec KernelSU

WinRescue utilise automatiquement `ksud` pour le patch SELinux :

```bash
ksud sepolicy patch "allow untrusted_app device chr_file { open read write ioctl }"
```

---

## Verification du root

### Depuis un terminal

```bash
# Verifier que su est disponible
su -c "id"
# Attendu : uid=0(root) gid=0(root)

# Verifier que libcomposite est chargeable
su -c "modprobe libcomposite && echo OK"
# Attendu : OK
```

### Depuis WinRescue

L'application effectue automatiquement une verification en 3 etapes au lancement :

| Etape | Verification | Etat si echec |
|-------|-------------|---------------|
| 1 | `Shell.isAppGrantedRoot()` | `NoRoot` ou `RootDenied` |
| 2 | `test -e /dev/hidg0` | `RootOnlyNoHid` (propose setup auto) |
| 3 | `test -w /dev/hidg0` | `Error` (tente patch SELinux) |

Si l'etape 2 echoue, WinRescue propose de configurer automatiquement le gadget HID via configfs (bouton "Configurer le HID").

---

## Verification du gadget HID

### Verifier manuellement

```bash
# Verifier que le peripherique existe
su -c "ls -la /dev/hidg0"
# Attendu : crw-rw-rw- 1 root root 239, 0 ... /dev/hidg0

# Verifier le UDC
su -c "ls /sys/class/udc/"
# Attendu : nom du controleur (ex: a600000.dwc3)

# Verifier le gadget configfs
su -c "cat /sys/kernel/config/usb_gadget/g1/UDC"
# Attendu : nom du UDC
```

### Creer manuellement le gadget

Si le setup automatique de WinRescue echoue, vous pouvez creer le gadget manuellement. Voir la section [Configuration du gadget via configfs](USB-HID-Protocol.md#configuration-du-gadget-via-configfs) dans la page du protocole HID.

### Supprimer le gadget

Pour nettoyer la configuration HID (utile pour deboguer) :

```bash
su -c "
cd /sys/kernel/config/usb_gadget/g1 2>/dev/null || exit 0
echo '' > UDC
rm configs/c.1/hid.usb0
rmdir configs/c.1/strings/0x409
rmdir configs/c.1
rmdir functions/hid.usb0
rmdir strings/0x409
cd .. && rmdir g1
"
```

---

## Desactivation SELinux

SELinux (Security-Enhanced Linux) est un systeme de controle d'acces obligatoire (MAC) sur Android. Il bloque par defaut l'ecriture sur `/dev/hidg0` par les applications non-systeme.

### Patch cible (recommande)

WinRescue applique un patch minimal qui n'autorise que l'operation necessaire :

```bash
# Magisk
magiskpolicy --live "allow untrusted_app device chr_file { open read write ioctl }"

# KernelSU
ksud sepolicy patch "allow untrusted_app device chr_file { open read write ioctl }"
```

Ce patch est applique "live" (en memoire) et ne survit pas a un redemarrage. WinRescue le reapplique automatiquement a chaque lancement.

### Fallback chmod

Si ni `magiskpolicy` ni `ksud` ne sont disponibles, WinRescue tente un fallback :

```bash
chmod 666 /dev/hidg0
```

Ce fallback est moins securise mais fonctionne sur la plupart des configurations.

### Verifier SELinux

```bash
# Verifier le mode SELinux
su -c "getenforce"
# Attendu : Enforcing (normal)

# Verifier les denials liees a hidg0
su -c "dmesg | grep hidg0 | grep denied"
```

---

**Voir aussi** : [Demarrage rapide](Getting-Started.md) | [Protocole USB HID](USB-HID-Protocol.md) | [Depannage](Troubleshooting.md)
