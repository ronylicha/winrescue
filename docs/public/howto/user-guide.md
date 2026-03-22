# Guide utilisateur complet

Ce guide couvre l'ensemble des fonctionnalites de WinRescue. Si vous n'avez jamais utilise l'application, commencez par le tutoriel [Premier usage en 5 minutes](../tutorials/getting-started.md).

![Fonctionnalites WinRescue](../../assets/winrescue-features.png)

---

## Dashboard et navigation

### Ecran d'accueil

L'ecran d'accueil est le point d'entree de WinRescue. Il se compose de trois zones :

1. **Barre superieure** : affiche le nom de l'application et un bouton d'acces aux parametres (icone d'engrenage).
2. **Barre d'etat USB** : situee juste en dessous, elle indique en temps reel si le telephone est branche a un PC et si le peripherique clavier virtuel (HID) est operationnel.
3. **Zone principale** : contient les onglets de navigation et la liste des scripts.

### Avertissements systeme

Deux bannieres d'avertissement peuvent apparaitre sous la barre d'etat :

| Banniere | Signification | Action |
|----------|---------------|--------|
| "Root non disponible" | Le telephone n'est pas roote, ou WinRescue n'a pas recu l'autorisation root. | Ouvrez Magisk ou KernelSU et accordez l'acces root a WinRescue. |
| "Root OK mais /dev/hidg0 non trouve" | Le root fonctionne, mais le peripherique clavier virtuel USB n'est pas configure. | Verifiez que le module `libcomposite` est charge et que le gadget USB est configure. Consultez la [FAQ](../reference/faq.md#le-root-fonctionne-mais-hidg0-nest-pas-trouve). |

---

## Onglets de navigation

L'application propose quatre onglets :

### Dashboard

Affiche tous les scripts disponibles, independamment du systeme d'exploitation. C'est la vue par defaut.

Utilisez cet onglet quand :
- Vous voulez voir l'ensemble du catalogue d'un coup d'oeil
- Vous ne savez pas si le PC est sous Windows 10 ou 11
- Vous cherchez un script par mot-cle via la barre de recherche

### Windows 10

Affiche uniquement les scripts compatibles avec Windows 10. Cela inclut les scripts dedies Windows 10 et les scripts universels (compatibles les deux versions).

### Windows 11

Affiche uniquement les scripts compatibles avec Windows 11. Meme principe que l'onglet Windows 10.

### Linux

Cet onglet est grise avec la mention "Soon". Le support Linux est prevu dans une future version.

---

## Recherche de scripts

La barre de recherche est accessible depuis n'importe quel onglet. Elle filtre les scripts en temps reel en cherchant dans :

- Le **nom** du script (ex: "mot de passe", "boot", "malware")
- La **description** du script
- La **categorie** (RECOVERY, ADMIN, REPAIR, SECURITY, NETWORK)

Pour effacer la recherche, appuyez sur la croix a droite du champ de texte.

### Exemples de recherches utiles

| Vous cherchez... | Tapez... |
|-----------------|----------|
| Un script de reset mot de passe | `mot de passe` ou `password` |
| Tout ce qui concerne le demarrage | `boot` ou `demarrage` |
| Les scripts de securite | `malware` ou `bitlocker` |
| Les scripts reseau | `reseau` ou `rdp` |

---

## Catalogue des scripts

WinRescue embarque 22 scripts organises en cinq categories :

### Recuperation (8 scripts)

| Script | OS | Difficulte | Duree | Ce qu'il fait |
|--------|-----|-----------|-------|---------------|
| Reset mot de passe Win10 | Win10 | Moyen | 10 min | Reinitialise le mot de passe d'un compte local via l'environnement de recuperation. |
| Reset mot de passe Win11 | Win11 | Moyen | 10 min | Meme operation adaptee a Windows 11 (methode des 3 extinctions forcees). |
| Reset PIN Win11 | Win11 | Facile | 8 min | Supprime les donnees Windows Hello (code PIN, empreinte, reconnaissance faciale). |
| Recuperation fichiers Win10 | Win10 | Moyen | 10 min | Copie vos documents, images et telechargements vers une cle USB depuis l'environnement de recuperation. |
| Recuperation fichiers Win11 | Win11 | Moyen | 10 min | Meme operation adaptee a Windows 11. |
| Reinitialisation usine Win10 | Win10 | Avance | 45 min | Remet le PC dans son etat d'origine. Attention : toutes les donnees sont effacees. |
| Reinitialisation usine Win11 | Win11 | Avance | 45 min | Meme operation adaptee a Windows 11. |

### Administration (4 scripts)

| Script | OS | Difficulte | Duree | Ce qu'il fait |
|--------|-----|-----------|-------|---------------|
| Creation compte admin Win10 | Win10 | Moyen | 12 min | Cree un nouveau compte utilisateur avec les droits administrateur. |
| Creation compte admin Win11 | Win11 | Moyen | 12 min | Meme operation adaptee a Windows 11. |
| Activation admin cache Win10 | Win10 | Moyen | 10 min | Active le compte Administrateur integre a Windows, normalement masque. |
| Activation admin cache Win11 | Win11 | Moyen | 10 min | Meme operation adaptee a Windows 11. |

### Reparation (4 scripts)

| Script | OS | Difficulte | Duree | Ce qu'il fait |
|--------|-----|-----------|-------|---------------|
| Reparation demarrage Win10 | Win10 | Avance | 20 min | Repare les fichiers de demarrage du PC quand Windows refuse de se lancer. |
| Reparation demarrage Win11 | Win11 | Avance | 20 min | Meme operation adaptee a Windows 11, avec gestion du demarrage UEFI. |
| Reparation fichiers systeme | Win10/11 | Moyen | 30 min | Detecte et repare les fichiers systeme endommages ou manquants. |
| Forcer le mode sans echec | Win10/11 | Moyen | 10 min | Force le PC a demarrer en mode sans echec au prochain allumage. |

### Securite (4 scripts)

| Script | OS | Difficulte | Duree | Ce qu'il fait |
|--------|-----|-----------|-------|---------------|
| Desactivation BitLocker Win10 | Win10 | Avance | 15 min | Deverrouille et desactive le chiffrement BitLocker. Necessite la cle de recuperation a 48 chiffres. |
| Desactivation BitLocker Win11 | Win11 | Avance | 15 min | Meme operation adaptee a Windows 11. |
| Suppression malware Win10 | Win10 | Avance | 10 min | Supprime les programmes malveillants qui se lancent au demarrage. |
| Suppression malware Win11 | Win11 | Avance | 10 min | Meme operation adaptee a Windows 11. |

### Reseau (4 scripts)

| Script | OS | Difficulte | Duree | Ce qu'il fait |
|--------|-----|-----------|-------|---------------|
| Activation RDP Win10 | Win10 | Moyen | 3 min | Active le bureau a distance pour permettre le controle du PC depuis un autre appareil. |
| Activation RDP Win11 | Win11 | Moyen | 3 min | Meme operation adaptee a Windows 11. |
| Reset reseau | Win10/11 | Facile | 5 min | Remet a zero toute la configuration reseau (DNS, proxy, pare-feu). Corrige la majorite des problemes de connexion. |

---

## Detail d'un script et parametres

Quand vous appuyez sur un script dans la liste, l'ecran de detail s'ouvre. Il affiche :

### Informations generales

- **Nom du script** : en titre
- **Description** : explication en langage courant de ce que fait le script
- **Categorie** : icone et libelle (Recuperation, Administration, Reparation, Securite, Reseau)
- **Difficulte** : badge de couleur (vert pour Facile, orange pour Moyen, rouge pour Avance)
- **Duree estimee** : temps moyen pour completer le script
- **Avertissement** : message d'alerte pour les scripts qui effacent des donnees ou modifient le systeme en profondeur (ex: reinitialisation usine)

### Champs de saisie

Certains scripts demandent des informations avant de pouvoir demarrer :

| Champ | Type | Exemple |
|-------|------|---------|
| Nom d'utilisateur | Texte | Le nom du compte Windows (ex: "Jean") |
| Nouveau mot de passe | Mot de passe | Le mot de passe souhaite (masque a la saisie) |
| Cle de recuperation BitLocker | Texte | Les 48 chiffres fournis par Microsoft |

Ces valeurs sont injectees automatiquement dans les commandes envoyees au PC. Vous n'avez pas a les retaper manuellement.

### Bouton de lancement

Appuyez sur **"Demarrer le wizard"** pour lancer le guide pas a pas. Ce bouton n'est actif que lorsque tous les champs obligatoires sont remplis.

---

## Wizard pas a pas

Le wizard est le coeur de WinRescue. Il decompose chaque script en etapes simples et vous accompagne du debut a la fin.

### Structure d'une etape

Chaque etape du wizard se compose de :

1. **Barre de progression** : en haut de l'ecran, elle indique l'etape courante et le nombre total d'etapes.
2. **Titre de l'etape** : resume en une phrase ce qu'il faut faire.
3. **Instruction principale** : explication detaillee de l'action attendue.
4. **Detail technique** (optionnel) : pour les curieux, explique ce qui se passe sous le capot.
5. **Apercu des frappes** : quand le telephone s'apprete a envoyer des commandes, un encadre affiche en avance les touches qui seront envoyees.
6. **Question de confirmation** : le wizard vous pose une question pour verifier que l'etape s'est deroulee correctement (ex: "Voyez-vous l'ecran bleu 'Choisir une option' ?").

### Actions automatiques

A certaines etapes, le telephone envoie des frappes clavier au PC de maniere autonome. Vous le voyez dans l'apercu avant envoi. Les types d'actions possibles sont :

- **Touche unique** : une pression sur une touche (Entree, Fleche bas, Tab, F8...)
- **Combinaison de touches** : une touche avec un modificateur (Ctrl+C, Alt+F4, Win+R...)
- **Texte** : une commande tapee caractere par caractere (ex: `net user Jean NouveauMotDePasse`)
- **Touche repetee** : une touche envoyee plusieurs fois rapidement (ex: F8 envoye 50 fois au demarrage)
- **Attente** : une pause entre deux actions pour laisser le PC reagir

### Navigation dans le wizard

| Bouton | Action |
|--------|--------|
| **Confirmer** | Valide l'etape et passe a la suivante. |
| **Reessayer** | Relance l'etape si elle a echoue. Disponible uniquement sur les etapes marquees "retryable". |
| **Abandonner** | Quitte le wizard. Une confirmation est demandee pour eviter un abandon accidentel. |

### Etapes critiques

Certaines etapes sont marquees comme critiques. Cela signifie que si elles echouent, le wizard vous empeche de continuer. C'est un filet de securite : pas question de lancer une commande de reset de mot de passe si l'invite de commandes n'est pas ouverte.

---

## Gestion des erreurs et reessai

### Quand une etape echoue

Si le resultat attendu n'apparait pas sur l'ecran du PC :

1. **Lisez l'instruction de reessai** : le wizard affiche une methode alternative (ex: "Si le menu n'est pas apparu, eteignez le PC en maintenant le bouton power 10 secondes et recommencez").
2. **Appuyez sur Reessayer** : le wizard relance les actions automatiques de l'etape.
3. **Si l'echec persiste** : verifiez les points suivants :
   - Le cable USB est bien branche des deux cotes
   - La barre d'etat USB affiche "Connecte"
   - Le PC est bien sur l'ecran attendu par le wizard

### Erreurs courantes et solutions

| Probleme | Cause probable | Solution |
|----------|----------------|----------|
| Les touches ne s'envoient pas | Cable debranche ou HID non configure | Rebranchez le cable. Verifiez la barre d'etat USB. |
| Le menu de recuperation n'apparait pas | Timing de F8 rate ou F8 desactive | Utilisez la methode des 3 extinctions forcees. |
| "net user" renvoie une erreur | Nom d'utilisateur incorrect | Tapez `net user` seul pour lister les comptes. |
| Le PC ne redemarre pas apres le script | Commande incomplete | Redemarrez manuellement en maintenant le bouton power. |
| Les caracteres tapes sont incorrects | Mauvaise disposition clavier | Changez le layout dans les parametres (QWERTY/AZERTY). |

---

## Parametres et configuration

L'ecran des parametres est accessible via l'icone d'engrenage en haut a droite de l'ecran d'accueil.

### Disposition clavier

Choisissez la disposition qui correspond au clavier configure sur le PC cible :

- **QWERTY US** : disposition americaine (par defaut). A utiliser si le PC est configure en anglais.
- **AZERTY FR** : disposition francaise. A utiliser si le PC est configure en francais.

Un mauvais choix de disposition provoque des caracteres incorrects dans les commandes envoyees (les lettres A et Q sont inversees, entre autres).

### Delai entre les caracteres

Regle le temps d'attente entre chaque caractere envoye (en millisecondes). Valeur par defaut : 50 ms.

- **Baissez la valeur** (30-40 ms) si le PC est rapide et que les commandes s'affichent correctement.
- **Augmentez la valeur** (60-100 ms) si le PC est lent et que des caracteres sont sautes ou en double.

### Delai entre les etapes

Regle le temps d'attente entre deux etapes du wizard (en millisecondes). Valeur par defaut : 1000 ms.

- **Augmentez la valeur** si le PC est lent a reagir entre les actions.

### Apercu avant envoi

Active ou desactive l'apercu des commandes avant leur envoi au PC. Active par defaut.

- **Activez-le** pour verifier les commandes avant qu'elles soient envoyees.
- **Desactivez-le** si vous etes a l'aise et souhaitez gagner du temps.

### Mode debug

Active l'affichage d'informations techniques supplementaires (chemin du peripherique HID, codes des touches envoyees, etc.). Desactive par defaut. Utile pour le diagnostic en cas de probleme.

### Test de connexion HID

Un bouton permet de tester la connexion entre le telephone et le PC. Le test verifie que le peripherique `/dev/hidg0` est accessible en ecriture et qu'un rapport HID peut etre envoye.

---

## Conseils pratiques

### Avant une intervention

- Chargez votre telephone. Un script peut durer jusqu'a 45 minutes.
- Ayez un cable USB-C/OTG fiable. Un faux contact pendant l'envoi des commandes peut corrompre l'operation.
- Notez le nom exact du compte Windows concerne (respectez les majuscules et minuscules).
- Pour les scripts BitLocker, recuperez la cle de recuperation (48 chiffres) depuis le compte Microsoft du proprietaire.

### Pendant une intervention

- Ne debranchez jamais le cable pendant que le telephone envoie des commandes.
- Lisez chaque instruction du wizard sans sauter d'etape.
- En cas de doute, choisissez "Reessayer" plutot que de continuer.

### Apres une intervention

- Debranchez le cable USB.
- Verifiez que le resultat est celui attendu (connexion avec nouveau mot de passe, demarrage normal, etc.).
- Redemarrez le PC une derniere fois pour confirmer que tout fonctionne.

---

[Retour a l'index](../index.md) | [FAQ](../reference/faq.md) | [Glossaire](../reference/glossary.md)
