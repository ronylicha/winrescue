# Depannage

FAQ et solutions aux problemes courants avec WinRescue.

**Retour** : [Accueil du wiki](Home.md) | **Voir aussi** : [Configuration root](Root-Setup.md) | [Protocole USB HID](USB-HID-Protocol.md)

---

## Sommaire

- [Root et HID](#root-et-hid)
- [Connexion USB](#connexion-usb)
- [Execution des scripts](#execution-des-scripts)
- [Layout clavier](#layout-clavier)
- [Compatibilite appareils](#compatibilite-appareils)
- [WinRE et navigation Windows](#winre-et-navigation-windows)
- [Problemes generaux](#problemes-generaux)

---

## Root et HID

### WinRescue affiche "NoRoot" alors que mon telephone est roote

**Causes possibles** :

1. **La popup d'autorisation root a ete refusee** : ouvrez l'app Magisk (ou KernelSU Manager), verifiez dans la liste des applications autorisees que WinRescue a bien l'acces root. Supprimez l'entree et relancez WinRescue pour que la popup reapparaisse.

2. **Le shell root n'est pas initialise** : WinRescue utilise libsu qui appelle `Shell.getShell()` au demarrage. Si cette initialisation echoue silencieusement, le root est considere comme absent. Relancez l'application.

3. **Magisk/KSU n'est pas correctement installe** : verifiez dans un terminal :
   ```bash
   su -c "id"
   ```
   Si la commande echoue, votre root n'est pas fonctionnel. Reinstallez Magisk ou KernelSU.

---

### WinRescue affiche "RootOnlyNoHid" : le root fonctionne mais /dev/hidg0 n'existe pas

**Solution** :

1. Appuyez sur le bouton "Configurer le HID" propose par WinRescue. Le setup automatique va :
   - Charger le module `libcomposite`
   - Creer le gadget USB via configfs
   - Activer le gadget sur le UDC

2. Si le setup automatique echoue avec "HID_NO_UDC" :
   - Verifiez que le cable USB est branche
   - Verifiez que votre appareil a un USB Device Controller compatible :
     ```bash
     su -c "ls /sys/class/udc/"
     ```
   - Si le dossier est vide, votre appareil ne supporte pas le mode USB gadget

3. Si le module `libcomposite` ne se charge pas :
   ```bash
   su -c "modprobe libcomposite"
   su -c "dmesg | tail -10"
   ```
   Verifiez dans les logs kernel si le module est disponible. Certains kernels custom ne l'incluent pas.

---

### "HID existe mais non accessible en ecriture meme apres patch SELinux"

**Diagnostic** :

```bash
# Verifier les permissions
su -c "ls -la /dev/hidg0"

# Verifier les denials SELinux
su -c "dmesg | grep denied | grep hidg"
```

**Solutions** :

1. Reappliquer le patch SELinux manuellement :
   ```bash
   su -c "magiskpolicy --live 'allow untrusted_app device chr_file { open read write ioctl }'"
   ```

2. Forcer les permissions :
   ```bash
   su -c "chmod 666 /dev/hidg0"
   ```

3. Si aucune solution ne fonctionne, votre kernel ou votre version SELinux peut avoir des restrictions supplementaires. Verifiez les logs avec `dmesg`.

---

## Connexion USB

### La barre de statut USB affiche "Deconnecte" alors que le cable est branche

**Verifications** :

1. **Cable de donnees** : les cables de charge seule ne transmettent pas les donnees USB. Testez avec un cable certifie USB-IF ou le cable d'origine de votre telephone.

2. **Port USB du PC** : essayez un autre port USB. Les ports USB en facade sont parfois instables.

3. **Le gadget HID est-il actif ?** :
   ```bash
   su -c "cat /sys/kernel/config/usb_gadget/g1/UDC"
   ```
   Si vide, le gadget n'est pas lie au controleur. Relancez le setup HID.

4. **Mode USB Android** : verifiez dans les parametres Android que le mode USB n'est pas force sur "Charge seule". Certains appareils desactivent le mode gadget quand le mode charge est selectionne.

---

### Le PC ne detecte pas le clavier USB

**Diagnostic cote PC** :

1. Ouvrez le Gestionnaire de peripheriques Windows
2. Cherchez dans "Claviers" un nouveau peripherique
3. Si absent, verifiez dans "Controleurs de bus USB" si le peripherique apparait avec un avertissement

**Diagnostic cote telephone** :

```bash
su -c "cat /sys/kernel/config/usb_gadget/g1/UDC"
```

Si le fichier UDC contient le nom du controleur, le gadget est actif. Le probleme est cote cable ou PC.

---

## Execution des scripts

### L'envoi des touches semble decale ou les caracteres sont manques

**Cause** : la machine cible est trop lente pour traiter les frappes au rythme de WinRescue.

**Solution** : augmentez le delai entre les caracteres dans les reglages :
- Allez dans Reglages > Delai entre les caracteres
- Augmentez de 50 ms a 80 ms ou 100 ms
- Testez a nouveau

---

### Le script echoue a une etape de navigation WinRE

**Cause** : la disposition des menus WinRE peut varier selon la version de Windows, la langue et les mises a jour installees. Les sequences de touches (fleches, Tab, Entree) peuvent ne pas correspondre exactement.

**Solutions** :

1. Utilisez l'option **retry** : chaque etape avec des actions automatisees est retryable. L'instruction de retry vous guide pour naviguer manuellement.

2. Naviguez manuellement sur le PC jusqu'a l'ecran attendu, puis confirmez l'etape dans WinRescue pour poursuivre.

3. Si un dialogue inattendu apparait (selection de compte admin, saisie de mot de passe), gerez-le manuellement puis reprenez le wizard.

---

### "Impossible de se connecter au peripherique HID"

Cette erreur apparait quand `HidKeyboardManager.connect()` echoue a ouvrir `/dev/hidg0`.

**Verifications** :
1. Le cable USB est-il branche ?
2. Le gadget HID est-il configure ?
3. Les permissions sont-elles correctes ?

```bash
su -c "test -w /dev/hidg0 && echo OK || echo NOK"
```

Si "NOK", relancez le setup HID depuis l'ecran principal de WinRescue.

---

### Le script affiche "La commande s'est terminee correctement" mais ca n'a pas fonctionne

**Cause** : la commande `net user` peut reussir techniquement mais ne pas produire l'effet attendu si le nom d'utilisateur est incorrect.

**Solution** :
1. Verifiez le nom d'utilisateur exact avec la commande `net user` seule (sans parametres) pour lister les comptes
2. Attention aux majuscules/minuscules et aux espaces dans le nom d'utilisateur
3. Relancez le script avec le bon nom

---

## Layout clavier

### Les caracteres envoyes sont incorrects (a au lieu de q, etc.)

**Cause** : le layout configure dans WinRescue ne correspond pas au layout du PC cible.

**Solution** :
1. Allez dans Reglages > Layout clavier
2. Si le PC cible utilise un clavier AZERTY francais, selectionnez "AZERTY FR"
3. Si le PC utilise un clavier QWERTY US/international, selectionnez "QWERTY US"

Le layout a selectionner est celui du **PC cible** (pas du telephone).

---

### Les caracteres speciaux ne sont pas tapes correctement

**Cause possible** : certains caracteres speciaux sont geres differemment selon les layouts.

Les caracteres non-ASCII (e, e, c, a, u) ne sont **pas supportes** par le protocole HID basique. Les scripts WinRescue utilisent uniquement des caracteres ASCII dans les commandes Windows.

Si vous devez saisir un mot de passe contenant des caracteres accentues, utilisez un mot de passe ASCII uniquement.

---

## Compatibilite appareils

### Samsung : le gadget HID ne se cree pas

**Cause** : Samsung utilise un TEE (Trusted Execution Environment) avec une whitelist USB qui bloque la creation de gadgets HID personnalises. Cette restriction est au niveau hardware/firmware et ne peut pas etre contournee par le root.

**Solution** : il n'existe pas de contournement connu. Utilisez un appareil Google Pixel ou OnePlus.

---

### Xiaomi : "modprobe: FATAL: Module libcomposite not found"

**Cause** : certains kernels Xiaomi ne compilent pas le module `libcomposite`.

**Solutions** :
1. Verifiez si le module existe :
   ```bash
   su -c "find /lib/modules/ -name 'libcomposite*'"
   ```
2. Si absent, vous devez flasher un kernel custom qui inclut `libcomposite`
3. Verifiez le support configfs :
   ```bash
   su -c "mount | grep configfs"
   ```

---

## WinRE et navigation Windows

### F8 ne fonctionne pas pour acceder a WinRE

**Cause** : sur Windows 10/11, le demarrage rapide (Fast Startup) et l'UEFI moderne rendent le timing de F8 tres serr. De plus, certaines cartes meres ignorent le clavier USB pendant le POST.

**Solutions** :

1. **Methode des 3 extinctions forcees** (plus fiable) :
   - Allumez le PC
   - Quand le logo Windows apparait, maintenez le bouton power pendant 10 secondes
   - Repetez 3 fois
   - Au 4eme demarrage, Windows lance automatiquement la reparation automatique (WinRE)

2. **Methode Shift+Redemarrer** (si Windows demarre) :
   - Sur l'ecran de connexion Windows, cliquez sur le bouton d'alimentation
   - Maintenez Shift et cliquez sur "Redemarrer"

---

### "Reparation automatique n'a pas pu reparer votre PC"

Cet ecran est normal. Il propose "Options avancees" qui est exactement le point d'entree dont WinRescue a besoin. Confirmez l'etape dans WinRescue et continuez.

---

### WinRE demande un mot de passe admin avant d'ouvrir l'invite de commandes

**Cause** : certaines versions de Windows demandent de selectionner un compte admin et d'entrer son mot de passe pour acceder a l'invite de commandes dans WinRE.

**Solutions** :
1. Si vous connaissez le mot de passe admin, saisissez-le
2. Si c'est le mot de passe que vous essayez de reinitialiser, c'est un probleme circulaire : utilisez le script `enable_hidden_admin` qui ne necessite pas de mot de passe dans WinRE (le compte Administrateur cache n'a souvent pas de mot de passe)

---

## Problemes generaux

### L'application crash au demarrage

**Verifications** :
1. Version Android >= 8.0 (API 26) ?
2. L'application a-t-elle les permissions root ?
3. Effacez les donnees de l'application et relancez

```bash
adb shell pm clear com.winrescue
```

---

### Comment reinitialiser les parametres de WinRescue ?

```bash
adb shell pm clear com.winrescue
```

Cela efface toutes les preferences (y compris l'acceptation du disclaimer).

---

### Comment voir les logs de debug ?

1. Activez le mode debug dans Reglages > Mode debug
2. Les logs sont affiches dans logcat avec le tag "HidKeyboardManager" :

```bash
adb logcat -s HidKeyboardManager:* RootManager:*
```

---

**Voir aussi** : [Configuration root](Root-Setup.md) | [Protocole USB HID](USB-HID-Protocol.md) | [Demarrage rapide](Getting-Started.md)
