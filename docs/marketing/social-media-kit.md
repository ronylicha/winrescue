# Kit reseaux sociaux -- WinRescue

Ce document contient des publications pretes a publier sur LinkedIn et X (Twitter), ainsi que les hashtags recommandes.

---

## Posts LinkedIn

### Post 1 : Lancement / Annonce

**Votre telephone peut reparer Windows.**

On a tous vecu ce moment : un PC qui ne demarre plus, un mot de passe oublie, et la cle USB bootable restee au bureau.

J'ai cree WinRescue, une application Android qui transforme votre smartphone en clavier USB physique. Branche par cable au PC, le telephone envoie automatiquement les commandes de recuperation, guide par un wizard etape par etape.

22 scripts de recuperation. Windows 10 et 11. Zero materiel a transporter.

Reset mot de passe, reparation du demarrage, suppression de malware, reinitialisation usine... Tout ce qu'un technicien IT fait au quotidien, mais sans cle USB et sans risque de faute de frappe.

L'outil demande un telephone Android roote (Pixel ou OnePlus recommandes) et un cable USB-C/OTG a moins de 5 EUR.

Le projet est open source. Lien dans les commentaires.

---

### Post 2 : Probleme / Solution

**5 minutes au lieu de 30.**

Un collaborateur a oublie son mot de passe Windows. Avant, je devais :
1. Retourner au bureau chercher ma cle USB
2. Preparer un ISO si elle n'etait pas a jour
3. Modifier le BIOS pour booter dessus
4. Taper les commandes sans me tromper

Maintenant : je branche mon telephone, je choisis le script, je suis le guide.

WinRescue transforme un smartphone Android en clavier USB de recuperation. Le PC le reconnait comme un vrai clavier, meme quand Windows ne demarre pas.

Le gain de temps est reel. Le gain de nerfs aussi.

---

### Post 3 : Feature technique

**Les commandes les plus longues, tapees sans erreur.**

`takeown /f C:\Windows\ServiceProfiles\LocalService\AppData\Local\Microsoft\NGC /r /d y`

Essayez de taper ca dans une invite de commandes, sur un clavier inconnu, avec un client qui attend derriere vous.

WinRescue envoie chaque caractere avec un delai de 40 ms, sans inversion de slash, sans coquille. Les variables (nom d'utilisateur, mot de passe) sont injectees automatiquement dans les commandes.

Un detail technique qui change tout en situation reelle.

---

### Post 4 : Cas d'usage MSP

**Pour les MSP : un outil dans la poche, pas dans la camionnette.**

Quand vous gerez des dizaines de clients avec des centaines de postes, le temps passe sur chaque intervention compte.

WinRescue me permet d'intervenir sans materiel dedie : pas de cle USB a preparer, pas de BIOS a modifier, pas de commandes a memoriser.

22 scripts couvrent les cas les plus frequents : reset mot de passe, reparation boot, desactivation BitLocker (avec la cle de recup, evidemment), activation RDP, suppression malware...

Compatible Secure Boot. Fonctionne meme quand Windows ne demarre pas. Prend en charge les dispositions QWERTY et AZERTY.

Un smartphone roote + un cable a 5 EUR = un kit de depannage complet.

---

### Post 5 : Coulisses techniques

**Comment un telephone se fait passer pour un clavier ?**

Sous le capot, WinRescue utilise le protocole USB HID (Human Interface Device). Le telephone cree un peripherique USB virtuel via le systeme configfs de Linux, le noyau d'Android.

Le PC ne voit aucune difference avec un clavier physique. Il recoit des rapports HID de 8 octets : 1 octet pour les touches de modification (Ctrl, Shift, Alt), 1 octet reserve, et 6 octets pour les touches appuyees.

Resultat : le telephone peut "taper" dans n'importe quel contexte -- ecran de connexion, BIOS, environnement de recuperation, invite de commandes. Partout ou un clavier fonctionne.

Ce mecanisme necessite un telephone roote (pour acceder a configfs et /dev/hidg0) et un cable USB-C/OTG.

Le code est open source. Les curieux sont invites a regarder.

---

## Posts X (Twitter)

### Tweet 1

Votre telephone peut reparer Windows.

WinRescue transforme un smartphone Android roote en clavier USB physique. 22 scripts de recuperation, un wizard pas a pas, zero cle USB.

Reset mot de passe, reparation boot, suppression malware... Branche par cable, le telephone fait le reste.

### Tweet 2

5 min pour reset un mot de passe Windows au lieu de 30.

Pas de cle USB. Pas de BIOS a modifier. Pas de commande a memoriser.

Un telephone Android + un cable a 5 EUR. WinRescue envoie les commandes automatiquement.

### Tweet 3

La commande la plus longue que j'ai tapee sans me tromper :

takeown /f C:\Windows\ServiceProfiles\LocalService\AppData\Local\Microsoft\NGC /r /d y

Spoiler : c'est mon telephone qui l'a tapee.

WinRescue = clavier USB virtuel + wizard interactif.

### Tweet 4

WinRescue fonctionne meme quand Windows ne demarre pas.

Le telephone est reconnu comme un clavier USB physique par l'environnement de recuperation (WinRE). Pas besoin de modifier le BIOS ni de desactiver Secure Boot.

### Tweet 5

22 scripts de recuperation Windows dans un telephone :

- Reset mot de passe
- Reparation demarrage
- Suppression malware
- Reinitialisation usine
- Activation RDP
- Reset reseau
- ...

Windows 10 + 11. Wizard etape par etape. Open source.

---

## Hashtags recommandes

### Hashtags principaux (a utiliser sur chaque publication)

```
#WinRescue #WindowsRecovery #SysAdmin
```

### Hashtags secondaires (a varier selon le sujet)

```
#IT #TechIT #AdminSys #MSP #HelpDesk
#USB #Android #OpenSource
#Windows10 #Windows11 #WinRE
#CyberSecurity #ITTools #TechTips
#PasswordReset #BootRepair #Malware
```

### Combinaisons recommandees par plateforme

**LinkedIn** (5-7 hashtags max) :
```
#WinRescue #WindowsRecovery #SysAdmin #IT #OpenSource #MSP #TechIT
```

**X / Twitter** (2-3 hashtags max) :
```
#WinRescue #SysAdmin #WindowsRecovery
```
