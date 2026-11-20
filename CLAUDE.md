# FitnessBoy

Android-App zum lokalen Tracken von Gewicht, BMI und Körpermaßen. Kotlin, Jetpack Compose, Material 3, Room, DataStore.

## Befehle

```bash
./gradlew assembleDebug        # Debug-Build
./gradlew test                 # Unit-Tests (JVM)
./gradlew lint                 # Android Lint
```

Releases: Tag `v*` pushen → GitHub Actions baut signierte APK (siehe `RELEASES.md`).

## Architektur

Package `de.bollich.fitnessboy`:

- `model/` – reine Datenklassen, keine Android-Abhängigkeiten
- `domain/` – Berechnungen, Use Cases, Repository-Interfaces. Reines Kotlin, keine Android-Imports.
- `data/` – Repository-Implementierungen (Room für Einträge, DataStore für Profil/Einstellungen)
- `ui/` – Compose-Screens, ViewModels, UI-State; ein Unterpaket pro Bereich (`weight/`, `measurements/`, `profile/`); `ui/components/` wiederverwendbare Komponenten
- `AppContainer` / `FitnessBoyApplication` – erzeugen Datenbank, DataStores und Repositories genau einmal

Abhängigkeitsrichtung: `ui → domain ← data`. `domain` kennt weder `ui` noch `data`.

Dependency Injection: manuell über einen `AppContainer` in der `Application`-Klasse. Keine DI-Library (kein Hilt/Koin). ViewModels bekommen ihre Abhängigkeiten über eine `Factory` im `companion object` (`appContainer()` aus `CreationExtras`).

Use Cases nur, wo sie Logik enthalten (Validierung, Berechnung, z. B. `AddWeightEntry`, `SaveProfile`, `GetHealthOverview`). Reine Durchreicher zum Repository gibt es nicht – ViewModels lesen und löschen dann direkt über das Repository.

`minSdk = 36` ist bewusst so gewählt – nicht senken.

## Regeln

### Allgemein
- An gängige Android- und Kotlin-Standards halten ([Android Architecture Guide](https://developer.android.com/topic/architecture), [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)). Keine Eigenbau-Lösungen, wo es etablierte Jetpack-Bibliotheken gibt.
- Wartbarer Code: kleine, klar benannte Funktionen und Dateien; eine Verantwortung pro Klasse; keine toten Code-Pfade stehen lassen.
- Neue Abhängigkeiten nur über `gradle/libs.versions.toml` und nur wenn nötig.

### Tests
- Domain-Logik muss durch Unit-Tests abgedeckt sein, **bevor** sie umgebaut wird.
- Neue Domain-Logik und Use Cases bekommen Tests.
- `./gradlew test` muss vor jedem Commit grün sein.

### Threading & Daten
- Keine I/O auf dem Main-Thread: Datenzugriff nur über `suspend`-Funktionen und `Flow`. Kein `allowMainThreadQueries()`, kein `runBlocking`.
- Room-Schema wird exportiert; jede Schemaänderung braucht eine Migration (kein destruktiver Fallback).

### UI
- Keine hartcodierten UI-Texte in Kotlin – alles in `res/values/strings.xml`.
- Ein ViewModel pro Bereich. UI-State wird reaktiv gebaut: `combine(Repository-Flows, Formular-State).stateIn(viewModelScope, WhileSubscribed(5_000), …)`. Kein manuelles Nachrechnen abgeleiteter Werte.
- Formular-Eingaben (`…FormState`) sind getrennt vom gespeicherten Zustand und werden nie von Repository-Updates überschrieben.
- Route/Screen-Muster: `XRoute(viewModel)` sammelt den State, `XScreen(uiState, callbacks)` ist stateless und hat eine Preview.
- ViewModel-Tests nutzen `MainDispatcherRule` und Fakes aus `src/test/.../testing/`; ViewModels in Tests `by lazy` erzeugen (erst nach dem Setzen des Main-Dispatchers).

### Sicherheit
- Keine Secrets, Keystores oder Passwörter im Repo; Signing nur über Umgebungsvariablen/GitHub Secrets.
- Nutzereingaben immer validieren (Parsing, Wertebereiche) bevor sie gespeichert werden.
- Gesundheitsdaten bleiben lokal: keine Netzwerkzugriffe, kein Logging personenbezogener Werte, Backup-Regeln (`res/xml/`) bewusst halten.
- `exported`-Komponenten im Manifest nur, wenn zwingend nötig.

### Git
- Direkt auf `main`, kleine in sich lauffähige Commits.
- Conventional Commits: `feat:`, `fix:`, `refactor:`, `test:`, `docs:`, `chore:`.

## Umbau-Plan

Laufende Verbesserung in Phasen (jeweils lauffähig abschließen):

0. ✅ Grundlagen: Tests für `GoalProgress`, `WeightTrend`, BMI, `InputParser`; Room-Schema-Export
1. ✅ Datenschicht: `WeightEntry` mit `id`, einzelne insert/delete statt Tabelle neu schreiben; DAOs/Repositories auf `suspend`/`Flow`
2. ✅ State: reaktiver UI-State via `combine`, ViewModel pro Bereich, Formular-State getrennt vom persistierten State, `AppContainer`
3. Navigation & UI: Navigation Compose statt `WeightPage`/`ProfilePage`-Enums (`WeightPage.HISTORY` ist toter Code); große Dateien aufteilen; Strings in Ressourcen; `formatNumber` locale-unabhängig/konsistent machen; Maße-Tab zeigt als „Letzter Wert“ das Gewicht; Zurück-Button im Gewichtsverlauf heißt „Zurück zur Gewichtsseite“, führt aber zum Profil
4. Aufräumen: Legacy `WeightStore`/`WeightEntryCodec` entfernen, README aktualisieren, CI für Tests/Lint
