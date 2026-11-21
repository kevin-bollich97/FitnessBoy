# FitnessBoy

FitnessBoy ist eine Android-App zum lokalen Tracken von Gewicht, BMI und Körpermaßen. Alle Daten bleiben auf dem Gerät – die App macht keine Netzwerkzugriffe.

## Funktionen

- **Übersicht:** aktuelles Gewicht mit Veränderung, Fortschritt zum Zielgewicht, BMI mit Einordnung und Erklärung (WHO-Kategorien, gesunder Bereich, Optimalgewicht), letzte Körpermaße
- **Gewicht:** Einträge mit frei wählbarem Datum, Diagramm mit Achsen und Ziellinie, Liste aller Einträge mit BMI und Löschfunktion
- **Körpermaße:** Bauch, Hüfte und Schultern sowie optional Oberarm, Brust und Oberschenkel; Figur mit allen Messstellen, Diagramm pro Maß, Liste mit BMI zum Messdatum
- **Profil:** Körpergröße und Zielgewicht
- Eintragen über einen Button unten rechts; Layouts funktionieren auch mit großer Schrift
- Zahlen im Format der Geräte-Sprache
- Signierte Release-APKs per Git-Tag über GitHub Actions

## Tech Stack

- Kotlin, Jetpack Compose, Material 3
- Navigation 3
- Room (Einträge) und DataStore (Profil, Einstellungen)
- ViewModel + `StateFlow`, Coroutines
- Gradle Kotlin DSL mit Version Catalog (`gradle/libs.versions.toml`)
- GitHub Actions für Release-Builds

## Architektur

Package `de.bollich.fitnessboy` unter `app/src/main/java/`:

| Paket | Inhalt |
| --- | --- |
| `model/` | Reine Datenklassen |
| `domain/` | Berechnungen (BMI, Trends, Zielfortschritt), Eingabe-Validierung, Use Cases, Repository-Interfaces – reines Kotlin ohne Android-Abhängigkeiten |
| `data/` | Repository-Implementierungen: Room-Datenbank (`data/local/`) und DataStore |
| `format/` | Zahlen- und Einheitenformatierung |
| `ui/` | Compose-Screens und ViewModels, ein Unterpaket pro Bereich (`overview/`, `weight/`, `measurements/`, `profile/`), dazu `navigation/`, `components/` (inkl. Diagramm) und `theme/` |

Abhängigkeitsrichtung: `ui → domain ← data`. Die Abhängigkeiten werden manuell über einen `AppContainer` in der `Application`-Klasse erzeugt, ohne DI-Library. Jeder Bereich hat ein ViewModel, das seinen UI-State reaktiv aus den Repository-Flows baut.

Die verbindlichen Architektur- und Code-Regeln stehen in [`CLAUDE.md`](CLAUDE.md), die laufende Planung steht in [`PLAN.md`](PLAN.md).

## Voraussetzungen

- Android Studio
- JDK 21
- Android SDK passend zur im Projekt gesetzten SDK-Version

## App lokal starten

Debug-Build:

```bash
./gradlew assembleDebug
```

APK liegt danach typischerweise unter:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Tests und Checks

```bash
./gradlew test    # Unit-Tests (JVM)
./gradlew lint    # Android Lint
```

Die Unit-Tests decken die Domain-Logik (Berechnungen, Validierung, Use Cases), die Formatierung und die ViewModels ab. Die ViewModel-Tests nutzen Fake-Repositories aus `app/src/test/.../testing/`.

## Release-Build lokal

Beispiel:

```bash
./gradlew assembleRelease -PreleaseVersionName=0.1.0 -PreleaseVersionCode=1
```

Ohne konfigurierten Release-Keystore wird lokal trotzdem ein Release-Artefakt gebaut. Für echte Updates und GitHub-Releases solltest du einen festen eigenen Signing-Key verwenden.

## GitHub Releases per Tag

Das Projekt enthält einen Workflow, der bei Tags wie `v0.1.0` automatisch eine Release-APK baut und an ein GitHub Release anhängt.

Workflow:

1. Änderungen committen
2. Tag erstellen
3. Tag pushen

Beispiel:

```bash
git tag v0.1.0
git push origin v0.1.0
```

Danach erstellt GitHub Actions:

- eine Release-APK
- eine SHA256-Datei
- ein GitHub Release mit den Assets

## Signing / Keystore

Für signierte Releases per GitHub Actions brauchst du diese Repository Secrets:

- `ANDROID_KEYSTORE_BASE64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

Eine genauere Anleitung steht in [RELEASES.md](RELEASES.md).

Kurzfassung:

1. lokalen Keystore erzeugen
2. Keystore in Base64 umwandeln
3. Werte als GitHub Repository Secrets speichern

## Installation auf dem Handy

Es gibt zwei Wege:

- Debug-APK lokal bauen und direkt installieren
- Release-APK aus GitHub Releases herunterladen und installieren

Wenn Android fragt, muss die Installation aus Browser oder Dateimanager erlaubt werden.

## Roadmap-Ideen

- weitere Fitnessmetriken
- bessere Verlaufsanalyse
- Datenexport

## Lizenz

Aktuell ist keine Lizenzdatei hinterlegt. Wenn du das Projekt öffentlich verteilen willst, solltest du noch eine passende Lizenz ergänzen.
