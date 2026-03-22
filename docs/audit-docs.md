# Audit Documentation -- WinRescue

**Date** : 2026-03-22
**Projet** : WinRescue (Android Kotlin/Compose -- outil de recuperation Windows via USB HID)
**Codebase** : 41 fichiers Kotlin, 22 scripts JSON, ~6200 lignes de code

---

## 1. Inventaire des fichiers documentaires

### 1.1 Fichiers doc standards

| Fichier | Present | Contenu |
|---------|---------|---------|
| README.md | ABSENT | Aucun README a la racine |
| CHANGELOG.md | ABSENT | Aucun changelog |
| CONTRIBUTING.md | ABSENT | Aucun guide de contribution |
| LICENSE | ABSENT | Aucune licence declaree |
| CLAUDE.md (projet) | ABSENT | Pas de CLAUDE.md local au projet |

### 1.2 Arborescence docs/

La structure `docs/` existe avec des sous-dossiers organises selon le framework Diataxis, mais **tous les dossiers sont vides** :

```
docs/
  assets/          (vide)
  concepts/        (vide)
  howto/           (vide)
  marketing/       (vide)
  reference/       (vide)
  tutorials/       (vide)
  public/
    concepts/      (vide)
    howto/          (vide)
    reference/     (vide)
    tutorials/     (vide)
  audit-docs.md    (ce fichier)
```

### 1.3 Dossier wiki/

Existe mais **vide**.

### 1.4 Fichiers .prompts/ (meta-documentation de dev)

Exclus du git (via `.gitignore`), mais presents localement :

| Fichier | Contenu |
|---------|---------|
| `.prompts/1-winrescue-research/winrescue-research.md` | Recherche technique complete (~16000 tokens) : libsu, USB HID configfs, scripts Windows |
| `.prompts/1-winrescue-research/SUMMARY.md` | Resume de la recherche |
| `.prompts/2-winrescue-plan/winrescue-plan.md` | Plan d'implementation 9 phases, architecture, design system |
| `.prompts/2-winrescue-plan/SUMMARY.md` | Resume du plan |

**Verdict** : Ces fichiers contiennent une documentation architecturale riche (decisions techniques, choix de stack, compatibilite appareils, commandes Windows) mais sont :
- Exclu du depot git
- Au format XML de prompts, pas lisibles pour des humains externes
- Non structures comme de la documentation projet

---

## 2. Commentaires dans le code Kotlin

### 2.1 Statistiques

| Metrique | Valeur |
|----------|--------|
| Fichiers Kotlin (source) | 41 |
| Lignes totales | ~6200 |
| Fichiers avec KDoc (`/**`) | 5 / 41 (12%) |
| Blocs KDoc | 32 |
| Lignes de commentaires (KDoc + inline) | 180 |
| Ratio commentaires/code | ~3% |

### 2.2 Repartition des KDoc

| Fichier | Blocs KDoc | Qualite |
|---------|------------|---------|
| `usb/HidKeyboardManager.kt` | 13 | Excellente : chaque methode documentee, parametres decrits |
| `data/root/RootState.kt` | 9 | Bonne : chaque etat de la sealed class documente |
| `usb/HidKeyMap.kt` | 6 | Bonne : classe et methodes publiques documentees |
| `data/root/RootManager.kt` | 3 | Correcte : methodes principales documentees |
| `data/root/DisclaimerState.kt` | 1 | Minimale : 1 KDoc sur la classe |

### 2.3 Fichiers sans aucun KDoc (36 fichiers)

Tous les fichiers UI (screens, components, viewmodels, theme, navigation), les modeles de donnees (`Script.kt`, `ScriptStep.kt`, `KeyAction.kt`, `Enums.kt`), les repositories et le module DI sont **totalement non documentes** en KDoc.

Les commentaires inline dans les screens/components sont des commentaires de section (`// Section header`) et non des explications fonctionnelles.

### 2.4 Verdict commentaires

- **Couche USB/Root** : bien documentee (meilleure pratique KDoc)
- **Couche Data/Model** : aucun KDoc (les data classes sont lisibles mais les enums et sealed classes gagneraient a etre documentees)
- **Couche UI** : aucun KDoc (composants, screens, viewmodels, navigation)
- **DI** : aucun KDoc

---

## 3. Assets visuels

| Type | Present | Detail |
|------|---------|--------|
| Screenshots app | ABSENT | Aucune capture d'ecran |
| Diagrammes architecture | ABSENT | Aucun diagramme (architecture, flux, state machine) |
| Logo / icone | PRESENT | `ic_launcher_foreground.xml` (icone vectorielle Android standard) |
| Images d'aide (hints) | ABSENT | Les scripts JSON referencent des `imageHint` (ex: `hint_winre_menu`, `hint_power_off_and_usb`) mais aucune image correspondante n'existe |
| Drawio/PlantUML | ABSENT | Aucun fichier de diagramme |

**Verdict** : Aucun asset visuel de documentation. Les references `imageHint` dans les scripts JSON pointent vers des ressources inexistantes (probablement des placeholders pour une version future).

---

## 4. Scripts JSON -- Auto-documentation

### 4.1 Inventaire

22 scripts JSON dans `app/src/main/assets/scripts/` :

| Categorie | Scripts |
|-----------|---------|
| RECOVERY | reset_password_win10/11, recover_files_win10/11, reset_pin_win11, factory_reset_win10/11 |
| ADMIN | create_admin_win10/11, enable_hidden_admin_win10/11 |
| SECURITY | disable_bitlocker_win10/11, remove_malware_win10/11 |
| NETWORK | enable_rdp_win10/11, reset_network |
| REPAIR | force_safe_mode, sfc_dism_repair, repair_boot_win10/11 |

### 4.2 Niveau d'auto-documentation

Chaque script JSON est **bien auto-documente** avec :

- `id`, `name`, `description` : identification claire
- `category`, `os`, `difficulty`, `estimatedMinutes` : metadata utilisable
- `warningMessage` : avertissements de securite
- `inputFields` avec `label`, `placeholder`, `type` : champs saisis documentes
- `steps[].instruction` + `instructionDetail` : instructions en langage naturel (FR)
- `steps[].confirmQuestion` : questions de validation utilisateur
- `steps[].retryInstruction` : instructions en cas d'echec
- `steps[].actions` : commandes HID avec types explicites (`key`, `string`, `template`, `wait`, `repeat`)

**Verdict** : Les scripts JSON constituent la meilleure documentation du projet. Ils sont lisibles, structures, et auto-documentes. Un schema JSON serait utile pour formaliser le contrat.

### 4.3 Lacunes

- Pas de schema JSON (`scripts.schema.json`) pour valider la structure
- Pas de documentation sur comment creer un nouveau script
- Les `imageHint` referencent des images inexistantes

---

## 5. Evaluation Diataxis

### 5.1 Tutorial (apprentissage guide)

**ABSENT**

Aucun tutoriel pour :
- Demarrer avec le projet (build, run)
- Premier script de test avec un telephone root
- Configuration USB HID sur un appareil specifique

### 5.2 How-To (resolution de probleme)

**ABSENT**

Aucun guide pour :
- "Comment rooter mon telephone pour WinRescue"
- "Comment creer un nouveau script de recuperation"
- "Comment debugger les problemes HID"
- "Comment contribuer au projet"
- Troubleshooting : Samsung vs Pixel, SELinux, cable OTG

### 5.3 Reference (information exhaustive)

**PARTIELLEMENT PRESENT** (dans le code uniquement)

- Les scripts JSON servent de reference factuelle (commandes, etapes)
- Les KDoc de `HidKeyboardManager` et `RootState` documentent l'API interne
- **Manque** : reference API complete, schema JSON, liste des keycodes supportes, matrice de compatibilite appareils

### 5.4 Explanation (comprehension conceptuelle)

**ABSENT** (dans la documentation officielle)

Les fichiers `.prompts/` contiennent des explications riches (architecture libsu, fonctionnement configfs, state machine root) mais ne sont pas dans la documentation du projet.

Manque :
- Architecture de l'app (MVVM, layers, DI)
- Fonctionnement du protocole HID USB sur Android
- Machine a etats root (les 6 etats et transitions)
- Design decisions et trade-offs

---

## 6. Gaps par audience

### 6.1 Developpeurs

| Doc manquante | Priorite | Impact |
|---------------|----------|--------|
| README.md (setup, build, architecture) | CRITIQUE | Impossible d'onboarder un contributeur |
| Guide "Creer un script JSON" | HAUTE | Bloque l'extension du catalogue |
| Schema JSON des scripts | HAUTE | Pas de validation, risque d'erreurs |
| Diagramme architecture (MVVM layers) | MOYENNE | Comprehension globale difficile |
| Diagramme state machine Root | MOYENNE | 6 etats non documentes visuellement |
| KDoc sur modeles (Script, ScriptStep, KeyAction, Enums) | MOYENNE | Types non documentes |
| KDoc sur UI (screens, viewmodels, components) | BASSE | Compose est relativement lisible |
| Guide contribution (CONTRIBUTING.md) | BASSE | Projet en phase initiale |
| Tests (0 fichiers test) | CRITIQUE | Aucun test existant, 0% coverage |

### 6.2 Utilisateurs finaux

| Doc manquante | Priorite | Impact |
|---------------|----------|--------|
| Guide d'installation (sideload APK) | CRITIQUE | L'utilisateur ne peut pas installer |
| Guide rapide "Debloquer mon PC Windows" | CRITIQUE | Cas d'usage principal non documente |
| Pre-requis (root, cable OTG, modele phone) | HAUTE | Utilisateur perdu sans pre-requis |
| FAQ / Troubleshooting | HAUTE | Pas d'aide en cas de probleme |
| Screenshots de l'app | MOYENNE | L'utilisateur ne sait pas a quoi s'attendre |
| Images d'aide (hint_winre_menu, etc.) | HAUTE | Les references imageHint pointent vers du vide |
| Matrice de compatibilite appareils | MOYENNE | Samsung vs Pixel non documente |

### 6.3 Marketing / Vitrine

| Doc manquante | Priorite | Impact |
|---------------|----------|--------|
| One-pager / pitch deck | HAUTE | Pas de presentation du projet |
| Liste des fonctionnalites (feature list) | HAUTE | Pas de vitrine des 22 scripts |
| Screenshots / demo GIF | MOYENNE | Pas de visuel pour promouvoir |
| Comparaison avec alternatives | BASSE | Pas de positionnement |
| Licence open-source | HAUTE | Statut legal non defini |

---

## 7. Score global

| Dimension | Score | Detail |
|-----------|-------|--------|
| Documentation fichiers (.md) | 0/10 | Aucun fichier doc present |
| Commentaires code (KDoc) | 3/10 | 12% des fichiers, couverture partielle (USB/Root uniquement) |
| Auto-documentation scripts JSON | 8/10 | Bien structures, lisibles, metadata riche |
| Assets visuels | 1/10 | Icone app uniquement, aucun screenshot/diagramme |
| Diataxis - Tutorial | 0/4 | Absent |
| Diataxis - How-To | 0/4 | Absent |
| Diataxis - Reference | 1/4 | Partiel (dans le code) |
| Diataxis - Explanation | 0/4 | Absent (present dans .prompts/ non commites) |

**Score composite : 13/50 -- Documentation largement insuffisante**

---

## 8. Recommandations prioritaires

### Immediate (bloquant)

1. **README.md** : description du projet, pre-requis (root, OTG), instructions build (`./gradlew assembleDebug`), architecture resumee
2. **LICENSE** : choisir et declarer une licence (MIT, GPL, Apache 2.0)
3. **docs/reference/script-schema.json** : schema JSON pour valider les scripts

### Court terme (semaine)

4. **docs/tutorials/getting-started.md** : tutoriel d'installation et premier usage
5. **docs/howto/create-script.md** : guide pour creer un nouveau script JSON
6. **docs/concepts/architecture.md** : migrer les explications de `.prompts/` vers une doc lisible (MVVM, HID, root state machine)
7. **KDoc sur les modeles** : `Script.kt`, `ScriptStep.kt`, `KeyAction.kt`, `Enums.kt`

### Moyen terme (mois)

8. **docs/howto/troubleshooting.md** : FAQ et depannage (Samsung, SELinux, cable OTG)
9. **docs/reference/device-compatibility.md** : matrice appareils testes
10. **docs/assets/** : diagramme architecture, state machine root, screenshots app
11. **docs/marketing/one-pager.md** : presentation vitrine du projet
12. **Images d'aide (hints)** : creer les images referencees dans les scripts JSON ou les remplacer par des icones Material

---

## 9. Points positifs

- L'arborescence `docs/` est deja preparee avec une structure Diataxis (tutorials, howto, reference, concepts) et une separation public/interne
- Les scripts JSON sont le meilleur atout documentaire du projet : complets, lisibles, bilingues
- Les fichiers critiques (HidKeyboardManager, RootState, RootManager) sont bien commentes en KDoc
- Les fichiers `.prompts/` contiennent une base solide de contenu reutilisable pour la documentation
