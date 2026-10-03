#!/usr/bin/env bash
set -euo pipefail

python3 - <<'PY'
import json
import sys
from pathlib import Path

central_dir = Path("packages/design-system/themes")
community_dir = Path("ktheme-pr/themes/community")
assets_dir = Path("Linkpoint/src/main/assets/themes")

errors = []

if not central_dir.exists():
    errors.append(f"Central themes directory not found: {central_dir}")
if not community_dir.exists():
    errors.append(f"Community themes directory not found: {community_dir}")
if not assets_dir.exists():
    errors.append(f"Assets themes directory not found: {assets_dir}")

if errors:
    for err in errors:
        print(f"❌ {err}")
    sys.exit(1)

central_files = sorted(list(central_dir.glob("*.json")))
community_files = sorted(list(community_dir.glob("*.json")))
repo_files = central_files + community_files

print(f"🔍 Found {len(central_files)} central themes and {len(community_files)} community themes ({len(repo_files)} total).")

asset_file_names = {f.name for f in assets_dir.glob("*.json")}
missing_in_assets = []

for repo_file in repo_files:
    if repo_file.name not in asset_file_names:
        missing_in_assets.append(str(repo_file))

if missing_in_assets:
    print("❌ Missing theme files in Android assets:")
    for missing in missing_in_assets:
        print(f"  - {missing}")
    sys.exit(1)

# Validate json content in assets
seen_ids = set()
for asset_file in assets_dir.glob("*.json"):
    try:
        data = json.loads(asset_file.read_text(encoding="utf-8"))
        theme_id = data.get("metadata", {}).get("id")
        if not theme_id:
            print(f"❌ Missing metadata.id in asset theme: {asset_file}")
            sys.exit(1)
        if "colorScheme" not in data:
            print(f"❌ Missing colorScheme in asset theme: {asset_file}")
            sys.exit(1)
        if theme_id in seen_ids:
            print(f"❌ Duplicate theme ID found in assets: {theme_id} ({asset_file})")
            sys.exit(1)
        seen_ids.add(theme_id)
    except Exception as e:
        print(f"❌ Invalid JSON in asset theme {asset_file}: {e}")
        sys.exit(1)

print(f"✅ Theme coverage OK: All {len(repo_files)} repository theme files exist in assets and parsed validly ({len(seen_ids)} unique theme IDs).")
PY
