# Umbau-Plan

Ziel: FitnessBoy auf eine saubere, wartbare Android-Architektur bringen, ohne dass die App zwischendurch kaputt ist. Jede Phase endet lauffähig, getestet (`./gradlew test`) und auf dem Emulator geprüft. Regeln dazu stehen in [`CLAUDE.md`](CLAUDE.md).

## ▶ Nächste Schritte (Stand 21.11.2026)

Phasen 0–4 sind erledigt. Offen ist nur noch:

1. **Pushen** und den ersten Lauf des CI-Workflows (`.github/workflows/ci.yml`) auf GitHub prüfen.
2. ~~`targetSdk` auf 37 anheben~~ – erledigt (`4497a91`). Nur auf dem API-36-Emulator geprüft; bei Gelegenheit auf einem Android-17-Gerät/-Image durchklicken.

Hinweise:
- Emulator `Medium_Phone_API_36.1` enthält Testdaten aus den Prüfläufen (Größe 175 statt 180 cm, Gewichtseintrag 125 kg und Maße-Eintrag vom 20.11.2026) – ggf. in der App löschen bzw. Größe zurücksetzen.
- Nichts ist gepusht; alle Commits liegen lokal auf `main`.

## Phase 0 – Grundlagen ✅

- [x] `CLAUDE.md` mit Architektur und gemeinsamen Regeln (`8fd9f3e`)
- [x] Tests für `InputParser`, BMI/Gewichtsbereich, `GoalProgress` (`d17440d`)
- [x] Tests für `WeightTrend` (`562aa4a`)
- [x] Room-Schema-Export über Room-Gradle-Plugin (`d13ba74`)

## Phase 1 – Datenschicht ✅

- [x] `WeightEntry` bekommt eine `id` (`1770c63`)
- [x] Einzelnes Insert/Delete statt „Tabelle löschen und neu schreiben“ (`1770c63`)
- [x] DAOs und Repositories auf `suspend`/`Flow`; kein `allowMainThreadQueries()`, kein `runBlocking` (`1770c63`)
- [x] Legacy-Migration aus SharedPreferences in einer Transaktion (`1770c63`)
- [x] Use-Case-Tests mit Fake-Repositories (`1770c63`)
- [x] Auf Emulator geprüft: Update mit Bestandsdaten, Hinzufügen, Löschen, Neustart

## Phase 2 – State & ViewModels ✅

- [x] `AppContainer` + `FitnessBoyApplication` statt handgebauter Singletons (`83e8b14`)
- [x] Ein ViewModel pro Bereich: Main, Weight, BodyMeasurements, Profile (`83e8b14`)
- [x] UI-State reaktiv via `combine(...).stateIn(...)`, `deriveState()` entfernt (`83e8b14`)
- [x] Formular-State getrennt vom gespeicherten Zustand (`83e8b14`)
- [x] Route/Screen-Muster: Routes sammeln State, Screens sind stateless (`83e8b14`)
- [x] Durchreicher-Use-Cases entfernt (`83e8b14`)
- [x] ViewModel-Tests mit `MainDispatcherRule` (`83e8b14`)
- [x] Auf Emulator geprüft: Tab-Wiederherstellung, Validierung, Profil → BMI reaktiv, Maße-Dialog

## Phase 3 – Navigation & UI ✅

- [x] Navigation 3 statt `WeightPage`/`ProfilePage`-Enums; Back-Stack im `MainViewModel` (`4b1489a`)
  - [x] Compose-BOM auf 2026.06.01 (Navigation 3 braucht Compose UI 1.11) (`8eedef7`)
  - [x] Totes `WeightPage.HISTORY` entfernt (inkl. gespeicherter Einstellung) (`4b1489a`)
  - [x] Zurück-Taste des Systems funktioniert auf Unterseiten und Tabs (`4b1489a`)
- [x] `FitnessBoyApp.kt` (~590 Zeilen) aufteilen: ein Screen pro Datei im jeweiligen Feature-Paket, jeder Screen mit Preview (`4a1c3aa`)
- [x] `ui/components/Cards.kt` (~700 Zeilen) aufteilen; ungenutzte `SummaryCard` entfernt (`4a1c3aa`)
- [x] Domain liefert typisierte Werte statt deutscher Texte (BMI-Kategorie, Fehler, Trendperioden) (`74990f5`)
- [x] Alle UI-Texte nach `res/values/strings.xml` (`d03a1f2`)
- [x] `formatNumber` über `NumberFormat` mit Geräte-Locale, max. 1 Nachkommastelle; Einheiten aus Ressourcen (`d03a1f2`)
- [x] Bug: Maße-Tab zeigt als „Letzter Wert“ das Gewicht statt der Körpermaße; `HeadlineSection` generisch gemacht (`5830ce5`)
- [x] Bug: Zurück-Button im Gewichtsverlauf heißt „Zurück zur Gewichtsseite“, führt aber zum Profil (`4b1489a`)
- [x] Auf Emulator geprüft: Navigation inkl. System-Zurück, alle Screens, deutsche Zahlenformatierung

## Phase 4 – Aufräumen ✅

- [x] Legacy `WeightStore`/`WeightEntryCodec` samt SharedPreferences-Migration und ungenutzter DAO-Methoden entfernen (`cb53095`)
- [x] README aktualisieren (Maße, Architektur, Tests) (`6b521ae`)
- [x] CI-Workflow für Tests und Lint bei Push/PR (`88e45bf`) – erster Lauf auf GitHub steht noch aus
- [x] Bibliotheken aktualisiert, jeweils einzeln und auf dem Emulator geprüft (`399a36b`…`a7d9ec6`)
  - [x] AGP 9.5.2, Gradle 9.9.2 (`e84c5ff`)
  - [x] `compileSdk` 37.1 – von Core 1.19, Lifecycle 2.11 und Compose 1.13 verlangt; `minSdk` bleibt 36 (`1a1fbe0`, `6b94e56`)
  - [x] Kotlin 2.4.21, KSP 2.3.14; Übergangsschalter `android.disallowKotlinSourceSets` entfernt (`a7d9ec6`)
- [x] Lint-Warnungen: `UseKtx` mit `WeightStore` entfallen, `RedundantLabel` behoben (`5bc7aba`); `OldTargetApi` durch `targetSdk` 37 behoben (`4497a91`); Lint ist warnungsfrei
- [x] Template-Reste entfernt: `ExampleInstrumentedTest`, ungenutzte `colors.xml`, Instrumentation-Abhängigkeiten (`275951b`)

## Unterwegs gefundene Bugs

- [x] 7-/30-Tage-Trend verglich mit dem ältesten statt dem jüngsten Eintrag vor dem Stichtag (`562aa4a`)
- [x] `fallbackToDestructiveMigration` hätte bei fehlender Migration alle Daten gelöscht (`2a1c846`)
- [x] Absturz bei zwei Gewichtseinträgen mit gleichem Datum und Gewicht (doppelter Listen-Key) (`1770c63`)
- [x] Maße-Tab „Letzter Wert“ zeigt Gewicht (`5830ce5`)
- [x] Falsche Beschriftung des Zurück-Buttons im Gewichtsverlauf (`4b1489a`)

## Offene Punkte / Notizen

- Schema von DB-Version 1 existiert nicht mehr; die Migration 1→2 ist daher nicht automatisiert testbar.
- Gerätetests bewusst weggelassen (DAOs sind trivial). Bei der ersten Schemaänderung (2→3) einen Migrationstest mit `MigrationTestHelper` (`room-testing`) auf Basis von `schemas/.../2.json` ergänzen.
