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
# After the L2 nav refactor, destinations are registered by iterating the
# routeMetadata map, so each route appears as `Routes.X to RouteMetadata(...)`.
metadata_refs = set(re.findall(r'Routes\.([A-Z_]+)\s*to\s*RouteMetadata', text))

missing_destinations = sorted(route_consts - metadata_refs)
unknown_destinations = sorted(metadata_refs - route_consts)

if missing_destinations:
    print("❌ Routes without RouteMetadata entries:")
    for route in missing_destinations:
        print(f"  - {route}")
if unknown_destinations:
    print("❌ RouteMetadata entries referencing unknown Routes constants:")
    for route in unknown_destinations:
        print(f"  - {route}")

if missing_destinations or unknown_destinations:
    sys.exit(1)

print(f"✅ Route coverage OK: {len(route_consts)} route constants all have RouteMetadata entries.")
PY
