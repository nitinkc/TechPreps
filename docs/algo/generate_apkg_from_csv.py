#!/usr/bin/env python3
"""
Robust CSV -> Anki .apkg generator.
- Reads `flashcards_db_concepts.csv` in the same folder.
- Sanitizes out common wrappers (``` fences, // comments, empty lines).
- Parses CSV rows and creates Basic (Front/Back) Anki notes.
- Escapes HTML in fields to avoid genanki warnings.
- Writes `db_concepts.apkg` in the same folder and prints counts.
"""
import os
import io
import csv
import random
import html

try:
    import genanki
except Exception:
    print('genanki not installed. Install it with: pip install genanki')
    raise

BASE_DIR = os.path.dirname(__file__)
CSV_PATH = os.path.join(BASE_DIR, 'flashcards_db_concepts.csv')
OUTPUT_APKG = os.path.join(BASE_DIR, 'db_concepts.apkg')

if not os.path.exists(CSV_PATH):
    print('CSV file not found:', CSV_PATH)
    raise SystemExit(2)

# Read and sanitize file
with open(CSV_PATH, 'r', encoding='utf-8', errors='ignore') as f:
    raw = f.read()

lines = raw.splitlines()
clean_lines = []
for line in lines:
    s = line.strip()
    # skip code fence lines or comment markers
    if s.startswith('```') or s.startswith('//') or s == '':
        continue
    clean_lines.append(line)

if not clean_lines:
    print('No CSV content found after sanitization')
    # Write an empty apkg for consistency
    model_id = random.randrange(1 << 30, 1 << 62)
    model = genanki.Model(model_id, 'EmptyModel', fields=[{'name':'Question'},{'name':'Answer'}], templates=[{'name':'Card1','qfmt':'{{Question}}','afmt':'{{Answer}}'}])
    deck = genanki.Deck(random.randrange(1 << 30, 1 << 62), 'DB Concepts - Partitioning/Sharding/Clustering')
    genanki.Package(deck).write_to_file(OUTPUT_APKG)
    print('Wrote', OUTPUT_APKG, 'with 0 cards')
    raise SystemExit(0)

# Parse CSV from sanitized lines
csv_text = '\n'.join(clean_lines)
reader = csv.reader(io.StringIO(csv_text))
rows = [r for r in reader if r]
if not rows:
    print('No rows parsed from CSV')
    raise SystemExit(1)

# Detect header
header = rows[0]
start_idx = 1 if header and len(header) >= 2 and header[0].strip().lower().startswith('question') else 0
parsed = []
for r in rows[start_idx:]:
    if not r:
        continue
    q = r[0].strip() if len(r) > 0 else ''
    a = r[1].strip() if len(r) > 1 else ''
    if len(r) > 2:
        extra = ','.join(c.strip() for c in r[2:])
        if extra:
            a = a + '\n' + extra if a else extra
    if q:
        parsed.append((q,a))

print('Parsed cards from CSV:', len(parsed))

# Build Anki deck
model_id = random.randrange(1 << 30, 1 << 62)
model = genanki.Model(
    model_id,
    'SimpleModelCSV',
    fields=[{'name':'Question'},{'name':'Answer'}],
    templates=[{
        'name':'Card 1',
        'qfmt':'{{Question}}',
        'afmt':'{{FrontSide}}\n\n<hr id="answer">\n\n{{Answer}}',
    }]
)

deck = genanki.Deck(random.randrange(1 << 30, 1 << 62), 'DB Concepts - Partitioning/Sharding/Clustering')
count = 0
for q,a in parsed:
    q_safe = html.escape(q)
    a_safe = html.escape(a)
    note = genanki.Note(model=model, fields=[q_safe, a_safe])
    deck.add_note(note)
    count += 1

package = genanki.Package(deck)
package.write_to_file(OUTPUT_APKG)
print('Wrote', OUTPUT_APKG, 'with', count, 'cards')

