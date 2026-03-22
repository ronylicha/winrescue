# Glossaire

Tous les termes techniques utilises dans WinRescue, expliques en langage simple.

---

### AZERTY

Disposition des touches sur les claviers francais. Le nom vient des six premieres lettres de la rangee superieure. WinRescue supporte cette disposition dans ses parametres pour que les commandes envoyees au PC soient correctement interpretees.

### BCD (Boot Configuration Data)

Fichier cache de Windows qui contient toutes les informations necessaires au demarrage du systeme. Si ce fichier est corrompu, le PC refuse de demarrer. Le script de reparation du demarrage le reconstruit.

### bcdedit

Outil en ligne de commande integre a Windows qui permet de modifier les parametres de demarrage. WinRescue l'utilise notamment pour forcer le demarrage en mode sans echec.

### BitLocker

Systeme de chiffrement de disque integre a certaines editions de Windows. Il protege les donnees en rendant le disque illisible sans la cle. WinRescue peut le desactiver, mais uniquement si vous disposez de la cle de recuperation a 48 chiffres.

### bootrec

Outil de reparation du demarrage disponible dans l'environnement de recuperation Windows. Il peut reparer le secteur de demarrage, detecter les installations Windows et reconstruire la base de demarrage. C'est l'outil principal utilise par les scripts de reparation du demarrage.

### Cable OTG (On-The-Go)

Cable USB special qui permet a un telephone de se comporter comme un peripherique (clavier, souris) lorsqu'il est branche a un PC. C'est le lien physique indispensable entre le telephone WinRescue et le PC a reparer. Cout : moins de 5 EUR.

### Clavier virtuel HID

Le "faux clavier" cree par WinRescue. Quand le telephone est branche au PC via un cable OTG, le PC croit qu'un vrai clavier vient d'etre branche. WinRescue peut alors envoyer des frappes comme si un humain tapait.

### Commande CMD

Instruction textuelle tapee dans l'invite de commandes de Windows (la fenetre noire). Exemples : `net user`, `bootrec`, `sfc /scannow`. WinRescue tape ces commandes automatiquement via le clavier virtuel.

### configfs

Systeme utilise par le noyau Linux (la base d'Android) pour creer des peripheriques USB virtuels. WinRescue s'en sert pour transformer le telephone en clavier USB. Necessite le root pour y acceder.

### DISM (Deployment Image Servicing and Management)

Outil Windows de niveau avance qui repare l'image systeme elle-meme. Si les fichiers de reference de Windows sont corrompus, SFC ne peut pas les reparer seul : DISM intervient en amont pour restaurer ces references. Le script de reparation des fichiers systeme utilise les deux.

### Disposition clavier

Configuration qui determine quel caractere correspond a quelle touche physique. Les deux dispositions les plus courantes sont QWERTY (anglais) et AZERTY (francais). WinRescue doit connaitre la disposition du PC cible pour envoyer les bons caracteres.

### HID (Human Interface Device)

Protocole USB standard utilise par les claviers, souris et manettes de jeu pour communiquer avec un ordinateur. WinRescue utilise ce protocole pour que le telephone soit reconnu comme un clavier par le PC.

### HID Report (rapport HID)

Petit paquet de donnees (8 octets) que le telephone envoie au PC pour simuler une frappe clavier. Chaque rapport decrit quelles touches sont appuyees a un instant donne. Un rapport "vide" signifie "toutes les touches sont relachees".

### Invite de commandes

Fenetre noire de Windows qui permet de taper des commandes textuelles. Dans l'environnement de recuperation (WinRE), elle s'execute avec des droits d'administration complets, ce qui permet de modifier les comptes utilisateurs et les parametres systeme.

### KernelSU

Solution de root pour Android, alternative a Magisk. Fonctionne au niveau du noyau Linux. WinRescue detecte automatiquement si KernelSU est installe et l'utilise pour obtenir les permissions necessaires.

### Layout clavier

Voir [Disposition clavier](#disposition-clavier).

### libcomposite

Module du noyau Linux necessaire pour creer un peripherique USB virtuel. WinRescue le charge automatiquement au demarrage. Si le module n'est pas disponible dans le noyau du telephone, le clavier virtuel ne peut pas etre cree.

### libsu

Bibliotheque logicielle utilisee par WinRescue pour communiquer avec le systeme de root (Magisk ou KernelSU). Elle permet d'executer des commandes avec les permissions administrateur sur le telephone.

### Magisk

Solution de root la plus populaire pour Android. Permet d'obtenir les droits administrateur sur le telephone tout en cachant le root a certaines applications. WinRescue necessite Magisk ou KernelSU pour fonctionner.

### MBR (Master Boot Record)

Tout premier secteur du disque dur, lu par le PC au demarrage. Il contient le code qui lance Windows. S'il est corrompu, le PC ne demarre plus. Le script de reparation du demarrage peut le reparer.

### Mode sans echec (Safe Mode)

Mode de demarrage special de Windows qui charge uniquement les composants essentiels. Utile pour desinstaller un pilote defectueux ou supprimer un virus. WinRescue peut forcer le demarrage en mode sans echec via la commande `bcdedit`.

### net user

Commande Windows qui permet de gerer les comptes utilisateurs locaux : creer un compte, changer un mot de passe, activer ou desactiver un compte. C'est la commande principale utilisee par les scripts de recuperation de mot de passe.

### NGC (Next Generation Credentials)

Dossier cache de Windows qui stocke les donnees d'identification Windows Hello : code PIN, empreinte digitale, reconnaissance faciale. Le script de reset PIN supprime ce dossier pour forcer Windows a redemander la configuration d'un nouveau PIN.

### QWERTY

Disposition des touches sur les claviers anglais et americains. Le nom vient des six premieres lettres de la rangee superieure. C'est la disposition par defaut dans WinRescue.

### Root (Android)

Acces administrateur complet au systeme Android. Par defaut, les applications Android fonctionnent dans un environnement restreint qui leur interdit de modifier le systeme. Le root leve cette restriction. Il est obligatoire pour WinRescue.

### Scancode

Code numerique attribue a chaque touche physique d'un clavier. Par exemple, la touche "A" a le scancode 0x04, la touche "Entree" a le code 0x28. Les scancodes sont independants de la disposition clavier : la touche physique a la meme position a toujours le meme scancode, que le clavier soit AZERTY ou QWERTY.

### SELinux

Systeme de securite integre a Android qui controle finement ce que chaque application a le droit de faire. Par defaut, il interdit l'acces au peripherique USB virtuel. WinRescue modifie temporairement cette politique pour s'autoriser l'acces.

### SFC (System File Checker)

Outil Windows qui verifie l'integrite de tous les fichiers systeme et remplace ceux qui sont corrompus par une copie saine. C'est la premiere etape du script de reparation des fichiers systeme.

### UDC (USB Device Controller)

Composant materiel du telephone qui gere la communication USB. Tous les telephones ont un UDC, mais tous ne supportent pas le mode "gadget" (peripherique). Si le UDC du telephone ne supporte pas ce mode, WinRescue ne peut pas creer le clavier virtuel.

### UEFI (Unified Extensible Firmware Interface)

Systeme qui a remplace le BIOS sur les PC modernes. Il gere le tout premier demarrage du PC, avant que Windows ne se lance. Les scripts de reparation du demarrage pour Windows 11 prennent en compte les specificites de l'UEFI.

### USB gadget

Mode de fonctionnement USB dans lequel un appareil (le telephone) se presente comme un peripherique (clavier, stockage, etc.) aupres d'un hote (le PC). C'est le mode utilise par WinRescue.

### Winsock

Composant reseau de Windows qui gere toutes les connexions internet. Le script de reset reseau reinitialise Winsock a son etat par defaut, ce qui corrige la majorite des problemes de connexion causes par des malwares ou des configurations corrompues.

### WinRE (Windows Recovery Environment)

Environnement de recuperation integre a Windows. C'est un ecran bleu fonce avec des options comme "Depannage", "Options avancees" et "Invite de commandes". Il est accessible apres 3 echecs de demarrage consecutifs ou via la touche F8 (Windows 10). La majorite des scripts WinRescue commencent par y acceder.

### Wizard

Le guide pas a pas de WinRescue. Il decompose chaque script en etapes simples avec des instructions claires, des confirmations a chaque etape et des options de reessai en cas de probleme. C'est le coeur de l'experience utilisateur de WinRescue.

---

[Retour a l'index](../index.md) | [FAQ](../reference/faq.md) | [Guide utilisateur](../howto/user-guide.md)
