# Umbau-Plan

Ziel: FitnessBoy auf eine saubere, wartbare Android-Architektur bringen, ohne dass die App zwischendurch kaputt ist. Jede Phase endet lauffähig, getestet (`./gradlew test`) und auf dem Emulator geprüft. Regeln dazu stehen in [`CLAUDE.md`](CLAUDE.md).

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

## Phase 4 – Aufräumen

- [ ] Legacy `WeightStore`/`WeightEntryCodec` entfernen (wenn keine Altdaten mehr zu erwarten sind)
- [ ] README aktualisieren (Maße, Architektur, Tests)
- [ ] CI-Workflow für Tests und Lint bei Push/PR
- [ ] Lint-Warnungen abarbeiten (Library-Updates, ungenutzte Template-Farben in `colors.xml`)
- [ ] Template-Test `ExampleInstrumentedTest` entfernen oder durch echten Room-/Migrationstest ersetzen

## Unterwegs gefundene Bugs

- [x] 7-/30-Tage-Trend verglich mit dem ältesten statt dem jüngsten Eintrag vor dem Stichtag (`562aa4a`)
- [x] `fallbackToDestructiveMigration` hätte bei fehlender Migration alle Daten gelöscht (`2a1c846`)
- [x] Absturz bei zwei Gewichtseinträgen mit gleichem Datum und Gewicht (doppelter Listen-Key) (`1770c63`)
- [x] Maße-Tab „Letzter Wert“ zeigt Gewicht (`5830ce5`)
- [x] Falsche Beschriftung des Zurück-Buttons im Gewichtsverlauf (`4b1489a`)

## Offene Punkte / Notizen

- Schema von DB-Version 1 existiert nicht mehr; die Migration 1→2 ist daher nicht automatisiert testbar.
