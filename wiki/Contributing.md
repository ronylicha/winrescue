# Guide de contribution

Comment contribuer au projet WinRescue.

**Retour** : [Accueil du wiki](Home.md) | **Voir aussi** : [Architecture](Architecture.md) | [Catalogue des scripts](Scripts-Catalog.md)

---

## Sommaire

- [Environnement de developpement](#environnement-de-developpement)
- [Workflow Git](#workflow-git)
- [Conventions de code](#conventions-de-code)
- [Creer un nouveau script](#creer-un-nouveau-script)
- [Tests](#tests)
- [Pull Requests](#pull-requests)

---

## Environnement de developpement

### Pre-requis

| Outil | Version |
|-------|---------|
| JDK | 17 |
| Android Studio | Hedgehog (2023.1) ou ulterieur |
| Android SDK | API 35 |
| Gradle | 8.x (wrapper inclus) |

### Setup

```bash
# Cloner le depot
git clone https://github.com/ronylicha/winrescue.git
cd winrescue

# Build
./gradlew assembleDebug

# Installer sur un appareil connecte
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Structure du projet

```
winrescue/
├── app/
│   ├── build.gradle.kts          # Dependances et config Android
│   ├── src/main/
│   │   ├── kotlin/com/winrescue/  # Code source Kotlin
│   │   ├── assets/scripts/        # 22 scripts JSON de recuperation
│   │   └── res/                   # Ressources Android (icons, manifest)
│   ├── src/test/                  # Tests unitaires
│   └── src/androidTest/           # Tests d'instrumentation
├── docs/                          # Documentation technique
├── wiki/                          # Pages du wiki GitHub
├── build.gradle.kts               # Config Gradle racine
└── settings.gradle.kts
```

---

## Workflow Git

### Branches

| Branche | Usage |
|---------|-------|
| `main` | Production stable, protegee |
| `develop` | Integration et tests |
| `feature/<nom>` | Nouvelle fonctionnalite |
| `fix/<nom>` | Correction de bug |
| `hotfix/<nom>` | Correction urgente en production |

### Processus

1. Creer une branche depuis `develop` :
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b feature/mon-changement
   ```

2. Developper et commiter (voir conventions ci-dessous)

3. Pousser et creer une Pull Request vers `develop`

4. Apres review et merge dans `develop`, `develop` est merge dans `main` pour les releases

---

## Conventions de code

### Commits

Format [Conventional Commits](https://www.conventionalcommits.org/) :

```
<type>(<scope>): <description>
```

| Type | Usage |
|------|-------|
| `feat` | Nouvelle fonctionnalite |
| `fix` | Correction de bug |
| `refactor` | Restructuration sans changement fonctionnel |
| `docs` | Documentation uniquement |
| `test` | Ajout ou modification de tests |
| `chore` | Taches de maintenance (dependances, CI) |

**Exemples** :
```
feat(scripts): ajouter script reset_password_win11_24h2
fix(hid): corriger le delai entre les caracteres sur machines lentes
docs(wiki): ajouter la page de depannage
refactor(root): extraire la logique SELinux dans SeLinuxPatcher.kt
test(wizard): ajouter les tests du WizardViewModel
```

### Style Kotlin

- Noms de variables descriptifs (pas de `x`, `temp`, `data2`)
- Fonctions : pattern verbe-nom (`fetchScripts()`, `sendReport()`, `isHidAvailable()`)
- Fichiers : PascalCase pour les classes, camelCase pour les utilitaires
- Maximum 50 lignes par fonction, 300 lignes par fichier
- Early return plutot que nesting profond
- Immutabilite par defaut (`val` > `var`, `copy()` > mutation)

### KDoc

Les classes et methodes publiques doivent avoir un KDoc :

```kotlin
/**
 * Envoie un rapport HID de 8 octets sur le peripherique.
 *
 * @param report Le rapport a envoyer (exactement 8 octets)
 * @throws IllegalStateException si le flux n'est pas ouvert
 */
private fun sendReport(report: ByteArray) { ... }
```

---

## Creer un nouveau script

Les scripts de recuperation sont des fichiers JSON dans `app/src/main/assets/scripts/`.

### Structure d'un script

```json
{
  "id": "mon_script_win10",
  "name": "Mon script (Win10)",
  "description": "Description claire de ce que fait le script.",
  "category": "RECOVERY",
  "os": ["WIN10"],
  "difficulty": "MEDIUM",
  "estimatedMinutes": 10,
  "requiresRoot": true,
  "icon": "build",
  "warningMessage": null,
  "inputFields": [
    {
      "id": "username",
      "label": "Nom d'utilisateur",
      "type": "TEXT",
      "placeholder": "Ex: Jean",
      "required": true,
      "defaultValue": null
    }
  ],
  "steps": [
    {
      "id": 1,
      "title": "Titre de l'etape",
      "instruction": "Instruction principale en langage clair.",
      "instructionDetail": "Explication technique optionnelle.",
      "imageHint": null,
      "confirmQuestion": "Question pour valider que l'etape a reussi ?",
      "waitBeforeSendMs": 0,
      "waitAfterSendMs": 3000,
      "actions": [
        {"type": "string", "value": "commande a taper", "delayBetweenCharsMs": 40},
        {"type": "wait", "ms": 500},
        {"type": "key", "key": "ENTER", "modifier": null}
      ],
      "retryable": true,
      "retryInstruction": "Que faire si l'etape echoue.",
      "criticalStep": true
    }
  ]
}
```

### Types d'actions

| Type | Parametres | Description |
|------|-----------|-------------|
| `string` | `value`, `delayBetweenCharsMs` | Tape une chaine de caracteres |
| `key` | `key`, `modifier` | Appuie sur une touche (optionnellement avec un modificateur) |
| `combination` | `keys` (liste) | Appuie sur une combinaison de touches simultanees |
| `wait` | `ms` | Attend un delai en millisecondes |
| `repeat` | `key`, `count`, `delayBetweenMs` | Repete l'appui sur une touche N fois |
| `template` | `template`, `delayBetweenCharsMs` | Tape une chaine avec des variables `{{id}}` |

### Touches speciales disponibles

`ENTER`, `ESCAPE`, `BACKSPACE`, `TAB`, `SPACE`, `DELETE`, `INSERT`, `HOME`, `END`, `PAGE_UP`, `PAGE_DOWN`, `UP`, `DOWN`, `LEFT`, `RIGHT`, `F1`-`F12`, `CAPS_LOCK`, `PRINT_SCREEN`, `SCROLL_LOCK`, `PAUSE`, `NUM_LOCK`

### Modificateurs

`CTRL`, `SHIFT`, `ALT`, `WIN` (Meta), `ALTGR` (Right Alt), et leurs variantes gauche/droite (`LCTRL`, `RCTRL`, etc.)

### Bonnes pratiques pour les scripts

1. **Premiere etape manuelle** : toujours commencer par une etape de preparation (eteindre le PC, brancher le cable) sans actions automatisees
2. **Questions de confirmation** : chaque etape avec des actions doit avoir une `confirmQuestion` pour verifier que le resultat est correct
3. **Retryable** : marquer `retryable: true` les etapes qui peuvent echouer sans consequence grave
4. **Instructions de retry** : fournir une instruction alternative en cas d'echec
5. **Etapes critiques** : marquer `criticalStep: true` les etapes irreversibles ou bloquantes
6. **Delais genereux** : les `waitAfterSendMs` doivent laisser le temps au PC de reagir (3-15 secondes)
7. **Nommer clairement** : l'ID du script suit le pattern `action_os` (ex: `reset_password_win10`)
8. **Tester sur un vrai PC** : les scripts doivent etre valides sur un PC reel avec le bon layout

### Checklist avant soumission

- [ ] Le JSON est valide (pas d'erreur de syntaxe)
- [ ] L'ID est unique et suit la convention de nommage
- [ ] Chaque etape a une instruction claire en francais
- [ ] Les etapes automatisees ont une `confirmQuestion`
- [ ] Les etapes risquees sont marquees `criticalStep`
- [ ] Les delais sont adaptes a la vitesse d'un PC standard
- [ ] Le script a ete teste sur un PC reel

---

## Tests

### Infrastructure de tests

| Framework | Usage |
|-----------|-------|
| JUnit 4 | Tests unitaires |
| MockK | Mocking des dependances |
| Turbine | Tests de StateFlow/Flow |
| Coroutines Test | Tests de fonctions suspend |
| Espresso | Tests d'instrumentation UI |

### Lancer les tests

```bash
# Tests unitaires
./gradlew test

# Tests d'instrumentation (necessite un appareil/emulateur)
./gradlew connectedAndroidTest
```

### Ecrire un test

```kotlin
@Test
fun `returns empty list when no scripts match query`() {
    // Arrange
    val repository = ScriptRepositoryImpl(context)

    // Act
    val result = repository.searchScripts("inexistant")

    // Assert
    assertEquals(emptyList<Script>(), result)
}
```

---

## Pull Requests

### Template

```markdown
## Description
Breve description du changement et de sa motivation.

## Type de changement
- [ ] Nouvelle fonctionnalite (feat)
- [ ] Correction de bug (fix)
- [ ] Refactoring (refactor)
- [ ] Documentation (docs)
- [ ] Tests (test)

## Checklist
- [ ] Le code suit les conventions du projet
- [ ] Les tests passent (`./gradlew test`)
- [ ] Le build est fonctionnel (`./gradlew assembleDebug`)
- [ ] La documentation est a jour (si applicable)
- [ ] Le commit suit le format Conventional Commits
```

### Processus de review

1. La PR est assignee a un reviewer
2. Le reviewer verifie le code, les tests et la documentation
3. Les commentaires sont resolus par l'auteur
4. Le reviewer approuve et merge

---

**Voir aussi** : [Architecture](Architecture.md) | [Catalogue des scripts](Scripts-Catalog.md) | [Demarrage rapide](Getting-Started.md)
