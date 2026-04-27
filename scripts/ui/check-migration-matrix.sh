#!/usr/bin/env bash
set -euo pipefail

NAV_FILE="Linkpoint/src/main/java/com/linkpoint/ui/navigation/Navigation.kt"
MATRIX_FILE="scripts/ui/route-migration-matrix.csv"

if [[ ! -f "$NAV_FILE" ]]; then
  echo "❌ Navigation file not found: $NAV_FILE"
  exit 1
fi
if [[ ! -f "$MATRIX_FILE" ]]; then
  echo "❌ Route migration matrix not found: $MATRIX_FILE"
  exit 1
fi

python3 - "$NAV_FILE" "$MATRIX_FILE" <<'PY'
import csv
import re
import sys
from pathlib import Path

nav_text = Path(sys.argv[1]).read_text(encoding='utf-8')
route_consts = sorted(set(re.findall(r'const\s+val\s+([A-Z_]+)\s*=\s*"[^"]+"', nav_text)))

with Path(sys.argv[2]).open(newline='', encoding='utf-8') as f:
    rows = list(csv.DictReader(f))

matrix_consts = [row.get('route_const', '').strip() for row in rows]
matrix_const_set = set(matrix_consts)

missing_matrix_entries = sorted([r for r in route_consts if r not in matrix_const_set])
unknown_matrix_entries = sorted([r for r in matrix_const_set if r and r not in route_consts])

incomplete_rows = []
for row in rows:
    const = row.get('route_const', '').strip()
    route_pattern = row.get('route_pattern', '').strip()
    destination_type = row.get('destination_type', '').strip()
    if const and (not route_pattern or not destination_type):
        incomplete_rows.append(const)

if missing_matrix_entries:
    print('❌ Routes missing from migration matrix:')
    for r in missing_matrix_entries:
        print(f'  - {r}')
if unknown_matrix_entries:
    print('❌ Migration matrix entries that do not map to any Routes constant:')
    for r in unknown_matrix_entries:
        print(f'  - {r}')
if incomplete_rows:
    print('❌ Migration matrix entries missing required metadata:')
    for r in sorted(set(incomplete_rows)):
        print(f'  - {r}')

if missing_matrix_entries or unknown_matrix_entries or incomplete_rows:
    sys.exit(1)

print(f'✅ Route migration matrix OK: {len(route_consts)} routes mapped with required metadata.')
PY
