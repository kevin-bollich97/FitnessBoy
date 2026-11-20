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
- `ui/` – Compose-Screens, ViewModels, UI-State; `ui/components/` wiederverwendbare Komponenten

Abhängigkeitsrichtung: `ui → domain ← data`. `domain` kennt weder `ui` noch `data`.

Dependency Injection: manuell über einen `AppContainer` in der `Application`-Klasse. Keine DI-Library (kein Hilt/Koin).

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
- ViewModels halten State als `StateFlow`; Composables sind möglichst stateless (State rein, Events raus).

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

0. Grundlagen: Tests für `GoalProgress`, `WeightTrend`, BMI, `InputParser`; Room-Schema-Export
1. Datenschicht: `WeightEntry` mit `id`, einzelne insert/delete statt Tabelle neu schreiben; DAOs/Repositories auf `suspend`/`Flow`
2. State: reaktiver UI-State via `combine`, ViewModel pro Bereich, Formular-State getrennt vom persistierten State, `AppContainer`
3. Navigation & UI: Navigation Compose statt `WeightPage`/`ProfilePage`-Enums; große Dateien aufteilen; Strings in Ressourcen
4. Aufräumen: Legacy `WeightStore`/`WeightEntryCodec` entfernen, README aktualisieren, CI für Tests/Lint
