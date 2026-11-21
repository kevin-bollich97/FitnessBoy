# Plan

Jeder Schritt endet lauffähig, getestet (`./gradlew test`, `./gradlew lint`) und auf dem Emulator geprüft – auch mit großer Schrift. Regeln stehen in [`CLAUDE.md`](CLAUDE.md).

## Stand (21.11.2026)

Architektur-Umbau (Phasen 0–4) und Redesign des Aufbaus (Schritte 1–6) sind abgeschlossen; Details stehen in der Git-History dieser Datei. Die App hat:

- Tabs **Übersicht · Gewicht · Maße · Profil**, Eintragen über FAB + Bottom Sheet
- Diagramme mit Achsen, Zeitraum-Chips und automatischer Gruppierung (Tag/Woche/Monat, Durchschnitt); Ziellinie nur, wenn sie den Verlauf nicht staucht
- Listen, die dem Zeitraum folgen, nach Monaten gegliedert sind (mitlaufende Überschriften) und zugeklappt die 3 neuesten Einträge zeigen
- Optionale Maße Oberarm, Brust, Oberschenkel (DB-Version 3, Migrationstest auf Robolectric)

**Nicht gepusht:** Zuletzt gepusht ist `c1c0661`; alle Commits danach liegen nur lokal auf `main`.

## ▶ Nächste Schritte

### 1. Visuelles Design der App überarbeiten
Der Aufbau steht, jetzt soll sich das Aussehen ändern. Zu Beginn klären: Was genau gefällt nicht (Farben, Schrift, Karten, Abstände, Gesamteindruck)? Gibt es Vorbilder? Dann Entwürfe für einen Screen zeigen und erst danach umsetzen.

### 2. Diagramm-Filter als Auswahlfeld statt Chips
Die Filter-Chips über dem Diagramm brauchen zu viel Platz. Stattdessen ein Auswahlfeld (Material 3 `ExposedDropdownMenuBox`).
- [ ] Zeitraum (1 Monat · 3 Monate · 1 Jahr · Alles) als Auswahlfeld – Gewicht und Maße
- [ ] Klären: Soll auch die Auswahl des Maßes (Bauch, Hüfte, …) im Maße-Diagramm ein Auswahlfeld werden?
- [ ] Mit großer Schrift prüfen

### 3. Aufgeklappte Liste auch oben wieder zuklappen
Ist die Liste aufgeklappt, soll „Weniger anzeigen“ auch direkt bei der Überschrift „Einträge“/„Messungen“ stehen, nicht nur am Ende der Liste.
- [ ] Gewicht und Maße

### Außerdem offen
- [ ] Pushen und den CI-Lauf prüfen (er führt erstmals den Robolectric-Migrationstest aus)
- [ ] GitHub-Issues #1, #3 und #4 schließen (#2 und #6 schließen sich beim Push)

## GitHub-Issues

| Issue | Status |
| --- | --- |
| #1 Diagramm Skala anzeigen | Erledigt – auf GitHub schließen |
| #2 Fussleiste nutzt nicht ganze Breite | Erledigt – schließt sich beim Push |
| #3 Geschlecht fehlt in der BMI Rechnung | Entfällt (BMI-Formel geschlechtsunabhängig) – auf GitHub schließen |
| #4 Hinzufügen von Daten wie Oberarm | Erledigt – auf GitHub schließen |
| #5 Ausschalten der BMI-Anzeige | **Zurückgestellt** – später entscheiden, ob ein Schalter den BMI außerhalb der Übersicht ausblendet |
| #6 Visualisierung von Daten wie Oberarm | Erledigt – schließt sich beim Push |

## Entscheidungen

- Nicht gemessene optionale Maße zeigen in „Letzte Messung“ „–“, nicht den letzten bekannten Wert.
- Lange Listen: kein Paging, sondern Zeitraum-Filter + Monatsgliederung.
- Diagramm-Zeitraum startet immer mit „Alles“ (wird nicht gespeichert).

## Unterwegs gefundene Bugs

_(noch keine)_

## Notizen

- Schema von DB-Version 1 existiert nicht mehr; die Migration 1→2 ist nicht automatisiert testbar.
- Migrationstests laufen auf Robolectric; dafür liegen die Room-Schemas in den Debug-Assets (nicht im Release). Jede künftige Migration bekommt einen Test in `FitnessBoyDatabaseMigrationTest`.
- Die Größe wird ohne Verlauf gespeichert; der „damalige BMI“ nutzt immer die aktuelle Größe.
- Geprüft wird auf dem Emulator `Medium_Phone_Android_17` (API 37). Er enthält **zusätzliche Testdaten** (20 Gewichtseinträge April–November, 10 Messungen) neben den ursprünglichen Testdaten (Größe 175 cm, Ziel 100 kg, Messung vom 27.03. mit überall 100 cm). Die Sicherung des Stands davor lag nur im temporären Session-Verzeichnis und ist nächstes Mal vermutlich weg – Testdaten ggf. in der App löschen.
