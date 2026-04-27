#!/usr/bin/env bash
set -euo pipefail

NAV_FILE="Linkpoint/src/main/java/com/linkpoint/ui/navigation/Navigation.kt"

if [[ ! -f "$NAV_FILE" ]]; then
  echo "❌ Navigation file not found: $NAV_FILE"
  exit 1
fi

python3 - "$NAV_FILE" <<'PY'
import re
import sys
from pathlib import Path

text = Path(sys.argv[1]).read_text(encoding='utf-8')

route_consts = set(re.findall(r'const\s+val\s+([A-Z_]+)\s*=\s*"[^"]+"', text))
composable_refs = set(re.findall(r'composable\(Routes\.([A-Z_]+)\)', text))

missing_destinations = sorted(route_consts - composable_refs)
unknown_destinations = sorted(composable_refs - route_consts)

if missing_destinations:
    print("❌ Routes without composable destinations:")
    for route in missing_destinations:
        print(f"  - {route}")
if unknown_destinations:
    print("❌ composable destinations referencing unknown Routes constants:")
    for route in unknown_destinations:
        print(f"  - {route}")

if missing_destinations or unknown_destinations:
    sys.exit(1)

print(f"✅ Route coverage OK: {len(route_consts)} route constants match {len(composable_refs)} composable destinations.")
PY
