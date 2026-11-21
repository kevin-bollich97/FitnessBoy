# Plan: Redesign & neue Funktionen

Ziel: Die App übersichtlicher und verständlicher machen (Dashboard statt verstreuter Kennzahlen) und die offenen GitHub-Issues umsetzen. Jeder Schritt endet lauffähig, getestet (`./gradlew test`, `./gradlew lint`) und auf dem Emulator geprüft. Regeln stehen in [`CLAUDE.md`](CLAUDE.md).

Der vorherige Architektur-Umbau (Phasen 0–4) ist abgeschlossen und steht in der Git-History dieser Datei.

## Zielbild

Vier Tabs mit je einer klaren Aufgabe: **Übersicht · Gewicht · Maße · Profil**. Der bisherige BMI-Tab entfällt, seine Inhalte gehen im Dashboard auf.

| Tab | Inhalt |
| --- | --- |
| **Übersicht** (Start) | Aktuelles Gewicht mit Datum und Veränderung in Worten („↓ 3 kg in den letzten 7 Tagen“, „↓ 7 kg seit dem 07.03.“); Ziel mit Fortschrittsbalken („Noch 25 kg · 7 kg geschafft“); BMI mit Einordnung – Antippen öffnet Erklärung mit gesundem Bereich und Optimalgewicht; letzte Körpermaße – Antippen öffnet den Maße-Tab |
| **Gewicht** | Diagramm mit Achsen, darunter alle Einträge mit damaligem BMI, Löschen direkt in der Liste |
| **Maße** | Figur mit den letzten Werten, Diagramm mit Auswahl des Maßes, darunter alle Messungen mit damaligem BMI |
| **Profil** | Nur Einstellungen: Größe, Zielgewicht |

Grundregeln für das Design:
- Jede Zahl hat einen erklärenden Text – keine nackten Werte wie „7 Tage −3 kg“ oder „Spanne 32 kg“.
- Nichts doppelt anzeigen (heute z. B. BMI an vier Stellen, doppelte Überschriften, doppeltes Datum).
- Keine großen Kopfkarten mit Erklärtext; schlichte Titel, Inhalt steht oben.
- Eintragen über einen FAB unten rechts, der ein Bottom Sheet (Datum + Wert) öffnet. Auf dem Maße-Tab öffnet er die Maße-Eingabe.
- Standard-Bausteine von Material 3: `NavigationBar` (volle Breite), FAB, `ModalBottomSheet`, `LinearProgressIndicator`, Listen, `FilterChip`.
- **Große Schrift und Anzeigezoom müssen sauber umbrechen** – kein Umbruch mitten im Wort. Keine festen Reihen mit Wert-Kästchen; wo Werte nebeneinanderstehen, `FlowRow` o. Ä. Jeder Screen wird auf dem Emulator mit Schriftgröße 130 % und erhöhter Anzeigegröße geprüft.

## Schritte

### 1. Grundgerüst ✅
- [x] Tabs auf Übersicht · Gewicht · Maße · Profil umstellen, BMI-Tab entfernen (Navigation, gespeicherter Tab) (`c518659`)
- [x] Standard-`NavigationBar` über die volle Breite bis zum unteren Rand (Issue #2) (`5782576`)
- [x] Dashboard „Übersicht“ mit Gewicht, Ziel, BMI (inkl. Erklärung) und letzten Körpermaßen (`c518659`)
- [x] FAB + Bottom Sheet zum Eintragen des Gewichts; auf dem Maße-Tab öffnet der FAB vorerst den bestehenden Dialog (`63303a9`)

### 2. Gewicht-Tab
- [ ] Wiederverwendbares Diagramm: x-Achse mit Datum, y-Achse mit Werten und Hilfslinien (Issue #1)
- [ ] Antippen eines Punkts zeigt Details: Datum, Wert, damaliger BMI (Issue #1)
- [ ] Liste aller Einträge mit damaligem BMI und Löschen; eigene Verlaufsseite entfällt
- [ ] Domain: BMI zu einem Datum = Gewicht an dem Tag bzw. letzter Eintrag davor + aktuelle Größe (mit Tests)

### 3. Datenmodell neue Maße (Issue #4)
- [ ] `BodyMeasurementsEntry` um Oberarm (nur einer), Brust, Oberschenkel erweitern – alle drei optional; Bauch, Hüfte, Schultern bleiben Pflicht
- [ ] Room-Migration 2 → 3 und erster Migrationstest mit `MigrationTestHelper` (`room-testing`) auf Basis von `schemas/.../2.json`
- [ ] Validierung und Use Case `AddBodyMeasurementsEntry` erweitern (mit Tests)
- [ ] Eingabe der neuen Felder im Bottom Sheet

### 4. Maße-Tab (Issue #6)
- [ ] Diagramm (aus Schritt 2) mit `FilterChip`-Auswahl des Maßes
- [ ] Figur zeigt auch die neuen Maße
- [ ] Liste aller Messungen mit damaligem BMI und Löschen; eigene Verlaufsseite entfällt

### 5. Profil
- [ ] Profil auf Größe und Zielgewicht reduzieren, doppelte Anzeigen (BMI, Überschrift) entfernen

## GitHub-Issues

| Issue | Status |
| --- | --- |
| #1 Diagramm Skala anzeigen | Schritt 2 |
| #2 Fussleiste nutzt nicht ganze Breite | Erledigt (`5782576`) – Issue auf GitHub schließen |
| #3 Geschlecht fehlt in der BMI Rechnung | **Entfällt** – BMI-Formel und WHO-Einordnung sind für Erwachsene geschlechtsunabhängig. Issue auf GitHub schließen. |
| #4 Hinzufügen von Daten wie Oberarm | Schritt 3 |
| #5 Ausschalten der BMI-Anzeige | **Zurückgestellt** – später entscheiden, ob ein Schalter den BMI außerhalb der Übersicht ausblendet |
| #6 Visualisierung von Daten wie Oberarm | Schritt 4 |

## Unterwegs gefundene Bugs

_(noch keine)_

## Notizen

- Schema von DB-Version 1 existiert nicht mehr; die Migration 1→2 ist nicht automatisiert testbar.
- Die Größe wird ohne Verlauf gespeichert; der „damalige BMI“ nutzt immer die aktuelle Größe.
- Geprüft wird auf dem Emulator `Medium_Phone_Android_17` (API 37); er enthält dieselben Testdaten wie das API-36-Gerät (Größe 175 statt 180 cm, Gewichtseintrag 125 kg, Maße-Eintrag vom 20.11.2026).
