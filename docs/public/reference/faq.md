# Foire aux questions (FAQ)

Retrouvez ici les reponses aux questions les plus frequentes sur WinRescue.

---

## Root et compatibilite Android

### Pourquoi le root est-il obligatoire ?

WinRescue a besoin du root pour trois operations que le systeme Android interdit aux applications classiques :

1. **Charger le module noyau `libcomposite`** : ce module permet de creer un peripherique USB virtuel (le clavier).
2. **Ecrire sur `/dev/hidg0`** : c'est le fichier par lequel le telephone envoie les frappes clavier au PC.
3. **Modifier la politique SELinux** : Android bloque par defaut l'acces aux peripheriques USB. WinRescue doit ajouter une regle pour l'autoriser.

Sans root, aucune de ces operations n'est possible et l'application ne peut pas fonctionner.

### Quels telephones sont compatibles ?

Les telephones **Google Pixel** et **OnePlus** sont recommandes car ils supportent nativement le mode USB gadget necessaire au fonctionnement de WinRescue.

Les telephones suivants posent des problemes connus :

| Marque | Probleme |
|--------|----------|
| **Samsung** | La "TEE whitelist" bloque la creation du peripherique USB virtuel. |
| **Xiaomi** | Politique restrictive sur le systeme de fichiers configfs. |
| **Huawei** | Bootloader verrouille rendant le root impossible sur la majorite des modeles. |

### Magisk ou KernelSU ?

WinRescue detecte et supporte les deux solutions de root :

- **Magisk** : la solution la plus repandue. WinRescue utilise `magiskpolicy` pour ajuster les regles de securite.
- **KernelSU** : alternative plus recente. WinRescue utilise `ksud` pour le meme objectif.

L'application detecte automatiquement laquelle est installee. Aucune configuration manuelle n'est necessaire.

### Le root va-t-il endommager mon telephone ?

Le root en lui-meme n'endommage pas le telephone. Cependant :

- Il annule generalement la garantie constructeur.
- Certaines applications bancaires ou de streaming refusent de fonctionner sur un telephone roote.
- Une mauvaise manipulation pendant le rooting peut bloquer le telephone (risque faible avec les outils modernes comme Magisk).

---

## Connexion USB et cable

### Quel cable utiliser ?

Vous avez besoin d'un cable **USB-C/OTG** (On-The-Go). Ce cable relie le port USB-C de votre telephone au port USB-A du PC.

Points a verifier :
- Le cable doit supporter le transfert de donnees (pas seulement la charge).
- Un cable court (30 cm) est plus fiable qu'un cable long.
- Cout : moins de 5 EUR dans la plupart des magasins.

### Comment savoir si la connexion USB fonctionne ?

La barre d'etat en haut de l'ecran d'accueil indique l'etat de la connexion :

- **Deconnecte** : le cable n'est pas branche ou le PC est eteint.
- **Connecte** : le telephone est branche et le clavier virtuel est operationnel.

Vous pouvez egalement lancer un **test de connexion HID** depuis les parametres de l'application.

### Le root fonctionne mais hidg0 n'est pas trouve

Ce message signifie que le root est operationnel, mais que le peripherique clavier virtuel USB n'a pas ete cree. Causes possibles :

1. **Le module `libcomposite` n'est pas charge** : WinRescue tente de le charger automatiquement, mais certains noyaux Android ne l'incluent pas.
2. **Le controleur USB du telephone ne supporte pas le mode gadget** : c'est le cas de certains modeles Samsung et Xiaomi.
3. **Le cable n'est pas un cable OTG** : un cable de charge simple ne permet pas la communication USB.

Solution : verifiez que votre telephone figure dans la liste des appareils compatibles et que vous utilisez un cable OTG.

### Que se passe-t-il si je debranche le cable pendant un script ?

Le wizard detecte la deconnexion et affiche un avertissement. Les commandes en cours d'envoi sont interrompues. Vous pouvez rebrancher le cable et reessayer l'etape en cours. Aucune donnee n'est perdue sur le PC du fait de la deconnexion.

---

## Scripts et utilisation

### Comment choisir le bon script ?

Partez du probleme que vous rencontrez :

| Probleme | Script recommande |
|----------|-------------------|
| Mot de passe oublie (compte local) | Reset mot de passe Win10/Win11 |
| Code PIN oublie (Windows Hello) | Reset PIN Win11 |
| PC bloque au demarrage | Reparation demarrage Win10/Win11 |
| Fichiers a recuperer avant un format | Recuperation fichiers Win10/Win11 |
| PC infeste de virus | Suppression malware Win10/Win11 |
| Internet ne fonctionne plus | Reset reseau |
| Besoin d'un acces admin | Creation compte admin Win10/Win11 |
| PC lent a cause d'un pilote | Forcer le mode sans echec |
| BitLocker bloque l'acces au disque | Desactivation BitLocker Win10/Win11 |
| Acces distant necessaire (RDP) | Activation RDP Win10/Win11 |
| Fichiers systeme corrompus | Reparation fichiers systeme |
| PC a remettre a zero | Reinitialisation usine Win10/Win11 |

### Les scripts fonctionnent-ils avec un compte Microsoft (en ligne) ?

Les scripts de reset de mot de passe ne fonctionnent qu'avec les **comptes locaux** Windows. Si le compte est lie a un compte Microsoft, le mot de passe doit etre reinitialise depuis le site account.microsoft.com.

Le script de creation de compte admin cree un compte local, ce qui permet de reprendre le controle du PC meme si le compte principal est un compte Microsoft.

### Combien de temps dure un script ?

Cela depend du script :

- **Les plus rapides** : activation RDP (3 minutes), reset reseau (5 minutes).
- **Les plus longs** : reinitialisation usine (45 minutes), reparation fichiers systeme (30 minutes).

Les durees indiquees sont des estimations. Le temps reel depend de la vitesse du PC et de votre rapidite a valider les etapes.

### Puis-je annuler un script en cours ?

Oui. Appuyez sur **Abandonner** dans le wizard. L'application vous demande confirmation avant de quitter. Les commandes deja envoyees au PC ne sont pas annulees -- si une commande a ete executee, son effet persiste.

### Les scripts peuvent-ils endommager le PC ?

Les scripts utilisent uniquement les outils integres a Windows (`net user`, `bootrec`, `bcdedit`, `sfc`, etc.). Neanmoins, certaines operations sont par nature risquees :

- **Reinitialisation usine** : efface toutes les donnees. Irreversible.
- **Reparation du demarrage** : en cas d'erreur, le PC peut devenir totalement inaccessible.
- **Suppression de malware** : un faux positif peut supprimer un composant systeme legitime.

Les scripts avances affichent un avertissement et les etapes critiques necessitent votre confirmation avant de s'executer.

---

## Clavier et disposition

### Pourquoi les caracteres tapes sont-ils incorrects ?

Le telephone envoie des codes de touches physiques (scancodes), pas des caracteres. Si la disposition clavier configuree dans WinRescue ne correspond pas a celle du PC, les caracteres seront interpretes differemment.

**Solution** : allez dans les parametres de WinRescue et selectionnez la disposition correspondant au PC cible (QWERTY US ou AZERTY FR).

### Mon PC est en QWERTY, lequel choisir ?

- PC configure en **anglais (US/UK)** : choisissez **QWERTY US**.
- PC configure en **francais** : choisissez **AZERTY FR**.

En cas de doute, lancez un test de connexion HID depuis les parametres. Le telephone envoie un texte de test au PC : si les caracteres affiches correspondent, la disposition est correcte.

### D'autres dispositions clavier sont-elles prevues ?

Pour le moment, WinRescue supporte QWERTY US et AZERTY FR. D'autres dispositions (QWERTZ allemand, AZERTY belge, etc.) pourront etre ajoutees dans de futures versions.

---

## Securite et legalite

### WinRescue contourne-t-il le chiffrement ?

Non. WinRescue ne contourne aucun chiffrement. Le script de desactivation BitLocker necessite la **cle de recuperation legitime** (48 chiffres) fournie par Microsoft lors de l'activation de BitLocker. Sans cette cle, le script ne peut rien faire.

### WinRescue est-il legal ?

WinRescue est legal lorsqu'il est utilise pour recuperer vos propres systemes ou des systemes dont vous avez l'autorisation explicite d'intervenir.

L'utiliser pour acceder a un ordinateur sans l'autorisation de son proprietaire constitue un delit d'acces frauduleux a un systeme informatique (article 323-1 du Code penal francais), passible de 3 ans d'emprisonnement et 100 000 EUR d'amende.

### Mes donnees sont-elles collectees ?

WinRescue ne collecte aucune donnee personnelle. L'application fonctionne entierement hors ligne. Les informations saisies (noms d'utilisateur, mots de passe) restent sur le telephone et ne sont transmises qu'au PC cible via le cable USB. Aucune donnee n'est envoyee sur internet.

### Le PC cible peut-il detecter WinRescue ?

Le PC reconnait le telephone comme un clavier USB standard. Il n'y a aucune trace logicielle specifique a WinRescue sur le PC. Les commandes executees sont les memes que si un humain les avait tapees au clavier.

---

## Problemes courants

### Le menu de recuperation Windows (WinRE) n'apparait pas

Deux methodes existent :

1. **Touche F8** (Windows 10) : WinRescue envoie F8 en rafale au demarrage. Cette methode ne fonctionne pas toujours.
2. **3 extinctions forcees** (fiable sur Win10 et Win11) : allumez le PC, et quand le logo Windows apparait, maintenez le bouton power 10 secondes pour forcer l'arret. Repetez 3 fois. Au 4eme demarrage, Windows lance automatiquement la reparation automatique qui donne acces a WinRE.

### Le script de reparation du demarrage a empire la situation

Si le PC ne demarre plus du tout apres un script de reparation du demarrage, vous pouvez :

1. Relancer le script pour tenter une seconde reparation.
2. Utiliser le script de reinitialisation usine (attention : perte de donnees).
3. Utiliser un support d'installation Windows pour acceder aux outils de reparation avances.

### L'application demande le root mais je l'ai deja

Verifiez dans Magisk ou KernelSU que WinRescue apparait dans la liste des applications autorisees. Si l'application a ete refusee, supprimez l'entree et relancez WinRescue pour que la popup d'autorisation reapparaisse.

---

[Retour a l'index](../index.md) | [Guide utilisateur](../howto/user-guide.md) | [Glossaire](../reference/glossary.md)
