# Premier usage en 5 minutes

Ce tutoriel vous guide pas a pas de l'installation de WinRescue jusqu'a l'execution de votre premier script de recuperation.

---

## Ce dont vous avez besoin

Avant de commencer, verifiez que vous disposez de :

| Element | Detail |
|---------|--------|
| **Telephone Android roote** | Magisk ou KernelSU installe et fonctionnel. Les marques Google Pixel et OnePlus sont recommandees. |
| **Cable USB-C/OTG** | Un cable qui relie le port USB-C de votre telephone au port USB du PC. Cout : moins de 5 EUR. |
| **Le PC Windows a reparer** | Windows 10 ou Windows 11, eteint ou bloque. |
| **L'APK WinRescue** | Telechargee depuis la page de releases du projet. |

> **Votre telephone n'est pas roote ?** Le root est obligatoire pour que le telephone puisse se faire passer pour un clavier USB. Sans root, l'application ne peut pas fonctionner. Consultez la [FAQ](../reference/faq.md#pourquoi-le-root-est-il-obligatoire) pour en savoir plus.

---

## Etape 1 : Installer l'APK

1. Transferez le fichier `.apk` sur votre telephone (par cable, Bluetooth, ou telechargement direct).
2. Ouvrez le fichier APK depuis un gestionnaire de fichiers.
3. Si Android vous demande d'autoriser les installations depuis des sources inconnues, acceptez.
4. Appuyez sur **Installer**, puis sur **Ouvrir** une fois l'installation terminee.

---

## Etape 2 : Premier lancement et disclaimer

Au premier lancement, WinRescue affiche un ecran d'avertissement legal :

> *"WinRescue est concu pour la recuperation de vos propres systemes ou de systemes dont vous avez l'autorisation explicite d'intervenir. L'utilisation de cet outil pour acceder a des systemes sans autorisation est illegale et punissable par la loi."*

Lisez attentivement ce message, puis appuyez sur **J'accepte** pour acceder a l'application.

Ce disclaimer ne s'affiche qu'une seule fois. Votre acceptation est enregistree avec un horodatage.

---

## Etape 3 : Verifier l'etat du systeme

L'ecran d'accueil affiche une barre d'etat en haut qui vous indique :

- **Etat du root** : un avertissement s'affiche si le root n'est pas detecte ou si l'autorisation root a ete refusee. Dans ce cas, ouvrez votre application Magisk ou KernelSU et accordez l'acces root a WinRescue.
- **Connexion USB** : indique si le telephone est branche a un PC et si le clavier virtuel (HID) est pret. Tant que vous n'avez pas branche le cable, cette zone indique "Deconnecte".

> **Astuce** : l'icone d'engrenage en haut a droite ouvre les parametres ou vous pouvez choisir la disposition clavier (QWERTY US ou AZERTY FR) et ajuster les delais de frappe.

---

## Etape 4 : Selectionner le systeme d'exploitation

L'ecran d'accueil propose quatre onglets :

| Onglet | Contenu |
|--------|---------|
| **Dashboard** | Vue d'ensemble de tous les scripts disponibles |
| **Windows 10** | Scripts specifiques a Windows 10 |
| **Windows 11** | Scripts specifiques a Windows 11 |
| **Linux** | A venir (grise pour le moment) |

Appuyez sur l'onglet correspondant au systeme du PC a reparer. Si vous ne connaissez pas la version, choisissez **Dashboard** pour voir tous les scripts.

---

## Etape 5 : Choisir et lancer un script

1. **Parcourez la liste des scripts.** Chaque carte affiche le nom du script, sa categorie (Recuperation, Administration, Reparation, Securite, Reseau), son niveau de difficulte et la duree estimee.

2. **Appuyez sur le script qui correspond a votre probleme.** Par exemple, si vous avez oublie le mot de passe d'un compte local Windows 10, choisissez **"Reset mot de passe Windows 10"**.

3. **Lisez la description detaillee.** L'ecran du script affiche :
   - Ce que fait le script, en langage clair
   - Le niveau de difficulte (Facile, Moyen, Avance)
   - La duree estimee
   - Les champs a remplir (nom d'utilisateur, nouveau mot de passe, etc.)

4. **Remplissez les champs demandes** (si le script en a). Par exemple, pour un reset de mot de passe, entrez le nom du compte Windows et le nouveau mot de passe souhaite.

5. **Appuyez sur "Demarrer le wizard"** pour lancer le guide pas a pas.

---

## Etape 6 : Suivre le wizard etape par etape

Le wizard vous guide a travers chaque etape du script. Pour chacune :

1. **Lisez l'instruction** affichee en gros. Elle vous dit exactement quoi faire (brancher le cable, allumer le PC, confirmer un ecran, etc.).
2. **Regardez le detail** pour comprendre ce qui se passe techniquement (facultatif, mais utile en cas de doute).
3. **Effectuez l'action demandee** sur le PC ou attendez que le telephone envoie les commandes automatiquement.
4. **Repondez a la question de confirmation** : le wizard vous demande si l'ecran attendu est bien visible sur le PC. Appuyez sur **Confirmer** pour passer a l'etape suivante.

> **Si une etape echoue** : certaines etapes proposent un bouton **Reessayer** avec des instructions alternatives. Par exemple, si la touche F8 ne fait pas apparaitre le menu de recuperation, le wizard vous explique comment forcer 3 extinctions consecutives a la place.

La barre de progression en haut indique votre avancement dans le script.

---

## Etape 7 : Verifier le resultat

Une fois toutes les etapes completees, le wizard affiche un ecran de succes.

Pour verifier que l'operation a fonctionne :

- **Reset de mot de passe** : redemarrez le PC et connectez-vous avec le nouveau mot de passe.
- **Reparation du demarrage** : le PC devrait demarrer normalement au prochain allumage.
- **Reset reseau** : ouvrez un navigateur et verifiez que vous avez acces a internet.

Debranchez le cable USB une fois l'intervention terminee.

---

## Et ensuite ?

- Decouvrez toutes les fonctionnalites dans le [guide utilisateur complet](../howto/user-guide.md).
- Consultez la [FAQ](../reference/faq.md) si vous rencontrez un probleme.
- Consultez le [glossaire](../reference/glossary.md) si un terme technique vous semble obscur.

[Retour a l'index](../index.md)
