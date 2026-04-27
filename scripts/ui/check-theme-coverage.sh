#!/usr/bin/env bash
set -euo pipefail

THEME_FILE="Linkpoint/src/main/java/com/linkpoint/ui/theme/BuiltInThemes.kt"

if [[ ! -f "$THEME_FILE" ]]; then
  echo "❌ Theme catalog file not found: $THEME_FILE"
  exit 1
fi

python3 - "$THEME_FILE" <<'PY'
import re
import sys
from collections import Counter
from pathlib import Path

text = Path(sys.argv[1]).read_text(encoding='utf-8')

block_match = re.search(r'fun\s+getAllBuiltInThemes\s*\(\)\s*:\s*List<ThemePack>\s*=\s*listOf\((.*?)\)', text, re.S)
if not block_match:
    print('❌ Could not parse getAllBuiltInThemes() list.')
    sys.exit(1)

picker_symbols = [s.strip() for s in block_match.group(1).split(',') if s.strip()]

symbol_to_id = {}
for m in re.finditer(r'val\s+([A-Z_]+)\s*=\s*ThemePack\((.*?)\n\s*\)', text, re.S):
    symbol = m.group(1)
    body = m.group(2)
    id_match = re.search(r'id\s*=\s*"([^"]+)"', body)
    if id_match:
        symbol_to_id[symbol] = id_match.group(1)

catalog_ids = list(symbol_to_id.values())
id_counts = Counter(catalog_ids)
duplicate_ids = sorted([theme_id for theme_id, count in id_counts.items() if count > 1])

missing_in_picker = sorted([sym for sym in symbol_to_id if sym not in picker_symbols])
unknown_in_picker = sorted([sym for sym in picker_symbols if sym not in symbol_to_id])

if duplicate_ids:
    print('❌ Duplicate theme IDs found in BuiltInThemes catalog:')
    for theme_id in duplicate_ids:
        print(f'  - {theme_id}')
if missing_in_picker:
    print('❌ ThemeCatalog entries missing from getAllBuiltInThemes picker data:')
    for sym in missing_in_picker:
        print(f'  - {sym}')
if unknown_in_picker:
    print('❌ Unknown symbols in getAllBuiltInThemes picker data:')
    for sym in unknown_in_picker:
        print(f'  - {sym}')

if duplicate_ids or missing_in_picker or unknown_in_picker:
    sys.exit(1)

print(f'✅ Theme coverage OK: {len(catalog_ids)} catalog themes, all unique and represented in picker data.')
PY
