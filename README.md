# FitnessBoy

FitnessBoy ist eine Android-Fitness-App zum lokalen Tracken von Gewicht, BMI und persönlichen Körperdaten.

Der aktuelle Stand der App bietet:

- Gewichtseinträge mit frei wählbarem Datum
- Verlauf als Chart
- eigene Verlaufsseite mit Löschfunktion für Einträge
- Profil mit Größe und Zielgewicht
- BMI-Bereich mit Einordnung, Zielgewicht und gesundem Gewichtsbereich
- GitHub-Release-Flow für signierte APKs per Tag wie `v0.1.0`

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- Gradle Kotlin DSL
- GitHub Actions für Release-Builds

## Projektstruktur

Die wichtigsten Bereiche:

- `app/src/main/java/de/bollich/fitnessboy/ui`
  Compose-Screens und Navigation
- `app/src/main/java/de/bollich/fitnessboy/ui/components`
  Wiederverwendbare UI-Komponenten
- `app/src/main/java/de/bollich/fitnessboy/domain`
  Berechnungen und fachliche Logik
- `app/src/main/java/de/bollich/fitnessboy/data`
  Lokale Speicherung und Codec
- `app/src/main/java/de/bollich/fitnessboy/model`
  Datenmodelle

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

Kompilierung prüfen:

```bash
./gradlew compileDebugKotlin
```

Unit-Tests ausführen:

```bash
./gradlew testDebugUnitTest
```

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

Eine genauere Anleitung steht in [RELEASES.md](/home/bollich/workspace/github/FitnessBoy/RELEASES.md).

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
- Ziele und Fortschrittsanzeigen
- Datenexport
- ViewModel-basierte State-Verwaltung

## Lizenz

Aktuell ist keine Lizenzdatei hinterlegt. Wenn du das Projekt öffentlich verteilen willst, solltest du noch eine passende Lizenz ergänzen.
