# validplate-demo

Quarkus-basierter Microservice zur Validierung deutscher KFZ-Kennzeichen gemäß Fahrzeug-Zulassungsverordnung (FZV).

---

## Implementierungsstand

### ✅ Implementiert

#### 1. Struktur- und Formatprüfung (Basis-Syntax)

Der Eingabe-String wird normalisiert (trim, collapse whitespace, uppercase) und gegen folgendes Regex-Pattern geprüft:

```
^([A-ZÄÖÜ]{1,3})-?([A-Z]{1,3})(\d{1,4})$
```

- Unterscheidungszeichen (UKZ): 1–3 Großbuchstaben (inkl. Umlaute)
- Optionaler Bindestrich als Trennzeichen
- Buchstabenteil der Erkennungsnummer: 1–3 Großbuchstaben
- Ziffernteil: 1–4 Ziffern

#### 2. Geografische Validierung — Unterscheidungszeichen

Abgleich des UKZ gegen zwei CSV-Datenquellen - wird das UKZ in keiner der beiden Quellen gefunden, ist das Kennzeichen **ungültig**:

| Quelle | Inhalt |
|---|---|
| [`kennzeichen.csv`](https://github.com/openpotato/kfz-kennzeichen/blob/main/src/de/kennzeichen.csv) | Alle aktiven und auslaufenden Kreis-/Stadtbezirkskennzeichen (KBA-Liste) |
| [`sonderkennzeichen.csv`](https://github.com/openpotato/kfz-kennzeichen/blob/main/src/de/sonderkennzeichen.csv) | Bundes- und Länderbehörden-Kennzeichen (THW, BP, Y, BWL, NRW usw.) |

siehe repo https://github.com/openpotato/kfz-kennzeichen/


#### 3. Kanonische Ausgabe

Valide Kennzeichen werden in die Normalform überführt:

```
[UKZ]-[BUCHSTABEN][ZIFFERN]   →   z.B. LI-IT100, THW-AB26, B-XY1234
```

#### REST-Endpunkt

```
GET /validplate/validate/{plate}

200 OK                    → kanonisches Kennzeichen (z.B. "B-AB1234")
422 Unprocessable Content → "error" (strukturell ungültig)
404 Not Found             → "error" (geografisch ungültig oder uneindeutig)
```


---

### ❌ Noch nicht implementiert

#### 3. Spezielle Kennzeichen-Typen (Suffix) Elektro und Oldtimer

| Typ | Suffix | Beispiel |
|---|---|---|
| Elektrofahrzeug (E-Kennzeichen) | `E` | `B XY123E` |
| Oldtimer (H-Kennzeichen) | `H` | `B XY123H` |

#### 4. Inhaltsvalidierung — Sperrliste

Prüfung der Erkennungsnummer gegen verbotene Kombinationen:

| Typ | Beispiele |
|---|---|
| Bundesweit verboten | `HJ`, `KZ`, `NS`, `SA`, `SS` |
| Regional verboten | `88`, `18`, `28` (in Kombination mit `AH`, `HH`) |

#### 5. Saisonkennzeichen (Suffix) 

Format `[NORMAL] MM/MM` — z.B. `B XY123 04/10` (April–Oktober).  
Prüflogik: Monatswerte 01–12, Mindestdauer 2 Monate, Maximaldauer 11 Monate.

#### 6. Weitere spezielle Kennzeichen-Typen (Suffix)

| Typ | Suffix | Beispiel |
|---|---|---|
| Händler (Rotes Kennzeichen) | `06`/`07` im Ziffernteil | `B 06 1234` |
| Kurzzeitkennzeichen | Ablaufdatum `TT.MM.JJ` | `B XY123 15.03.26` |

<!-- #### 6. Physikalische Kennzeichenvalidierung (DIN 74069 / FE-Schrift)

OCR-basierte Validierung von Schriftart, Abmessungen und Euro-Balken.  -->


---

## Architektur

```
GET /validplate/validate/{plate}
        │
        ▼
 PlateService.digest()
        │
        ├─ normalisieren (trim / uppercase)
        ├─ Regex-Match
        │        └─ kein Match → PlateModel.Invalid
        │
        └─ DistinctIdService.find(ukz)
                 ├─ DistrictRepo   (kennzeichen.csv)
                 ├─ SpecialRepo    (sonderkennzeichen.csv)
                 └─ nicht gefunden → PlateModel.Invalid

PlateModel.Valid  → canonical()  →  "UKZ-BUCHSTABEN+ZIFFERN"
PlateModel.Invalid → canonical() →  "error"
```

---

## Tests

Parametrisierte Tests lesen Testfälle aus CSV-Dateien unter `src/test/resources/aufgabenstellung/`:

| Datei | Inhalt |
|---|---|
| `original.csv` | Ursprüngliche Aufgabenstellung (PlateServiceTest) |
| `custom_district.csv` | Erweiterte Testfälle für ("district") Kreiskennzeichen |
| `custom_sonder.csv` | Erweiterte Testfälle für ("special") Sonderkennzeichen |

---

## Build & Run

Prerequisite: Java 25 SKD in path (for gradle wrapper is used)

```bash
# Build
./gradlew build

# Entwicklung (Hot Reload)
./gradlew :mservice:quarkusDev

# Container Image lokal bauen
./gradlew :mservice:build \
  -Dquarkus.container-image.build=true \
  -Dquarkus.container-image.push=false
```

Swagger UI (auch im Prod-Profil): http://localhost:9040/swagger-ui/

---

## Glossar

| Begriff | Erklärung |
|---|---|
| **Kennzeichen** | Alphanumerische Kombination zur eindeutigen Identifizierung eines Kraftfahrzeugs. Umgangssprachlich „Nummernschild". |
| **UKZ** | **Unterscheidungszeichen** — die ersten 1–3 Buchstaben des Kennzeichens, die den Zulassungsbezirk kennzeichnen (z.B. `B` für Berlin, `WOB` für Wolfsburg). |
| **Erkennungsnummer** | Individueller Teil nach dem UKZ: **1–3 Buchstaben** + **1–4 Ziffern** (z.B. `AB 123`). Innerhalb eines Zulassungsbezirks einmalig. |
| **Sonderkennzeichen** | Kennzeichen für Bundes- und Länderbehörden (z.B. `THW`, `BP`, `Y`, `NRW`). Kein Zulassungsbezirk, sondern Behörde als Zuordnung. |
| **Suffix `E`** | **E-Kennzeichen** für Elektro- und Brennstoffzellenfahrzeuge |
| **Suffix `H`** | **H-Kennzeichen** Oldtimer-Kennzeichen für Fahrzeuge ≥ 30 Jahre. |
| **Saisonkennzeichen** | **Suffix nach `E`\|`H`** - Kennzeichen mit zeitlich begrenzter Zulassung (2–11 Monate/Jahr), erkennbar am Monatsbereich am rechten Rand (z.B. `04/10`). |
| **Sperrliste** | Verzeichnis verbotener Buchstaben-/Zahlenkombinationen in der Erkennungsnummer (z.B. `SS`, `HJ`, `88`). |
| **Zulassungsbezirk/-stelle** | Geografisches Gebiet (Stadt oder Landkreis), das durch ein UKZ repräsentiert wird. Zulassungsstelle als behördliche Einrichtung, die Kraftfahrzeuge in einem Zulassungsbezirk zulässt.|
<!-- | **Kanonische Form** | Normalisierte Darstellung eines validen Kennzeichens: `UKZ-BUCHSTABEN+ZIFFERN` (z.B. `LI-IT100`). |
| **FZV** | Fahrzeug-Zulassungsverordnung — die rechtliche Grundlage für Kennzeichenstruktur und -vergabe in Deutschland. |
| **KBA** | Kraftfahrt-Bundesamt — führt die offizielle Liste der Unterscheidungszeichen. |
| **FE-Schrift** | Fälschungserschwerende Schrift — seit 2000 vorgeschriebene Schriftart auf Kennzeichenschildern. |
| **DIN 74069** | Technische Norm, die physische Eigenschaften von Kennzeichenschildern definiert (Abmessungen, Material, Reflexion). | -->