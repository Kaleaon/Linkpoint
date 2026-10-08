#!/usr/bin/env bash
set -euo pipefail

THEME_FILE="legacy/Linkpoint/src/main/java/com/linkpoint/ui/theme/BuiltInThemes.kt"
if [[ ! -f "$THEME_FILE" ]]; then
  THEME_FILE="Linkpoint/src/main/java/com/linkpoint/ui/theme/BuiltInThemes.kt"
fi

if [[ ! -f "$THEME_FILE" ]]; then
  echo "❌ Theme catalog file not found: $THEME_FILE"
  exit 1
fi

python3 - "$THEME_FILE" <<'PY'
import glob
import json
import re
import sys
from pathlib import Path

theme_file = sys.argv[1]
text = Path(theme_file).read_text(encoding='utf-8')

central_dir = Path("packages/design-system/themes")
community_dir = Path("legacy/ktheme-pr/themes/community") if Path("legacy/ktheme-pr/themes/community").exists() else Path("ktheme-pr/themes/community")
assets_dir = Path("legacy/Linkpoint/src/main/assets/themes") if Path("legacy/Linkpoint/src/main/assets/themes").exists() else Path("Linkpoint/src/main/assets/themes")

errors = []

# 1. Parse BuiltInThemes.kt symbols and colors
symbol_to_id = {}
symbol_to_colors = {}
for m in re.finditer(r'val\s+([A-Z0-9_]+)\s*=\s*ThemePack\((.*?)\n\s*\)', text, re.S):
    symbol = m.group(1)
    body = m.group(2)
    id_match = re.search(r'id\s*=\s*"([^"]+)"', body)
    if id_match:
        theme_id = id_match.group(1)
        symbol_to_id[symbol] = theme_id

        colors = {}
        for key in ['colorPrimary', 'colorPrimaryDark', 'colorOnPrimary', 'colorSecondary', 'colorOnSecondary', 'colorBackground', 'colorSurface', 'colorOnSurface', 'colorOnSurfaceVariant', 'colorSurfaceVariant', 'colorError']:
            cm = re.search(fr'{key}\s*=\s*"([^"]+)"', body)
            if cm:
                colors[key] = cm.group(1)
        symbol_to_colors[symbol] = colors

catalog_ids = list(symbol_to_id.values())
duplicate_ids = [tid for tid in set(catalog_ids) if catalog_ids.count(tid) > 1]

errors = []

if duplicate_ids:
    print('❌ Duplicate theme IDs found in BuiltInThemes catalog:')
    for theme_id in duplicate_ids:
        print(f'  - {theme_id}')
    errors.append('Duplicate theme IDs in BuiltInThemes.kt')


def parse_hex_color(hex_str, context=""):
    if not isinstance(hex_str, str):
        print(f'❌ Invalid color type in {context}: expected string, got {type(hex_str).__name__}')
        return None
    cleaned = hex_str.strip().lstrip('#')
    if not re.fullmatch(r'[0-9a-fA-F]{3}|[0-9a-fA-F]{6}|[0-9a-fA-F]{8}', cleaned):
        print(f"❌ Invalid hex color format in {context}: '{hex_str}'")
        return None
    if len(cleaned) == 3:
        r = int(cleaned[0] * 2, 16) / 255.0
        g = int(cleaned[1] * 2, 16) / 255.0
        b = int(cleaned[2] * 2, 16) / 255.0
    elif len(cleaned) == 6:
        r = int(cleaned[0:2], 16) / 255.0
        g = int(cleaned[2:4], 16) / 255.0
        b = int(cleaned[4:6], 16) / 255.0
    elif len(cleaned) == 8:
        r = int(cleaned[2:4], 16) / 255.0
        g = int(cleaned[4:6], 16) / 255.0
        b = int(cleaned[6:8], 16) / 255.0
    return r, g, b

def relative_luminance(r, g, b):
    def channel_lum(c):
        return c / 12.92 if c <= 0.04045 else ((c + 0.055) / 1.055) ** 2.4
    return 0.2126 * channel_lum(r) + 0.7152 * channel_lum(g) + 0.0722 * channel_lum(b)

def contrast_ratio(hex1, hex2, context=""):
    c1 = parse_hex_color(hex1, context)
    c2 = parse_hex_color(hex2, context)
    if c1 is None or c2 is None:
        return None
    l1 = relative_luminance(*c1)
    l2 = relative_luminance(*c2)
    return (max(l1, l2) + 0.05) / (min(l1, l2) + 0.05)

# 2. Check WCAG AA contrast in BuiltInThemes.kt
kt_contrast_failures = []
for symbol, theme_id in symbol_to_id.items():
    colors = symbol_to_colors.get(symbol, {})
    pairs = [
        ('onSurface/surface', colors.get('colorOnSurface'), colors.get('colorSurface')),
        ('onPrimary/primary', colors.get('colorOnPrimary'), colors.get('colorPrimary')),
        ('onSecondary/secondary', colors.get('colorOnSecondary'), colors.get('colorSecondary')),
        ('onBackground/background', colors.get('colorOnSurface'), colors.get('colorBackground')),
    ]
    for pair_name, fg, bg in pairs:
        if fg and bg:
            cr = contrast_ratio(fg, bg, f"Kotlin theme '{theme_id}' ({symbol}) pair {pair_name}")
            if cr is None:
                errors.append(f"Invalid color format in Kotlin theme '{theme_id}' pair {pair_name}")
            elif cr < 4.5:
                kt_contrast_failures.append((theme_id, symbol, pair_name, fg, bg, cr))

if kt_contrast_failures:
    print('❌ WCAG AA contrast check failed for Kotlin BuiltInThemes:')
    for theme_id, symbol, pair_name, fg, bg, cr in kt_contrast_failures:
        print(f"  - Theme '{theme_id}' ({symbol}) {pair_name}: fg={fg}, bg={bg}, ratio={cr:.2f} < 4.5")
    errors.append('WCAG AA contrast failure in BuiltInThemes.kt')

# 3. JSON Theme Schema, Token Integrity & Contrast Validation
json_patterns = [
    'packages/design-system/themes/*.json',
    'packages/design-system/themes/community/*.json',
    'ktheme-pr/themes/community/*.json',
    'legacy/ktheme-pr/themes/community/*.json',
]
json_files = []
for pat in json_patterns:
    json_files.extend(glob.glob(pat))

json_themes = {}
json_schema_errors = []
json_contrast_failures = []

for filepath in sorted(json_files):
    try:
        content = Path(filepath).read_text(encoding='utf-8')
        data = json.loads(content)
    except Exception as e:
        print(f"❌ Failed to parse JSON file {filepath}: {e}")
        json_schema_errors.append(f"JSON syntax error in {filepath}")
        continue

    metadata = data.get("metadata")
    if not isinstance(metadata, dict):
        print(f"❌ Missing or invalid 'metadata' object in {filepath}")
        json_schema_errors.append(f"Missing metadata in {filepath}")
        continue

    theme_id = metadata.get("id")
    theme_name = metadata.get("name")
    version = metadata.get("version")
    if not theme_id or not isinstance(theme_id, str):
        print(f"❌ Missing or invalid metadata 'id' in {filepath}")
        json_schema_errors.append(f"Missing metadata id in {filepath}")
        continue
    if not theme_name or not isinstance(theme_name, str):
        print(f"❌ Missing or invalid metadata 'name' in {filepath} (theme '{theme_id}')")
        json_schema_errors.append(f"Missing metadata name in {filepath}")

    color_scheme = data.get("colorScheme")
    if not isinstance(color_scheme, dict):
        print(f"❌ Missing or invalid 'colorScheme' object in {filepath} (theme '{theme_id}')")
        json_schema_errors.append(f"Missing colorScheme in {filepath}")
        continue

    # Validate hex format for all color tokens in colorScheme
    hex_format_ok = True
    for token_key, token_val in color_scheme.items():
        if isinstance(token_val, str) and token_val.startswith('#'):
            if parse_hex_color(token_val, f"JSON theme '{theme_id}' ({filepath}) token '{token_key}'") is None:
                hex_format_ok = False

    if not hex_format_ok:
        json_schema_errors.append(f"Invalid color hex format in {filepath}")

    # Contrast check on JSON colorScheme
    pairs = [
        ('onSurface/surface', color_scheme.get('onSurface'), color_scheme.get('surface')),
        ('onPrimary/primary', color_scheme.get('onPrimary'), color_scheme.get('primary')),
        ('onSecondary/secondary', color_scheme.get('onSecondary'), color_scheme.get('secondary')),
        ('onBackground/background', color_scheme.get('onBackground', color_scheme.get('onSurface')), color_scheme.get('background')),
    ]
    for pair_name, fg, bg in pairs:
        if fg and bg:
            cr = contrast_ratio(fg, bg, f"JSON theme '{theme_id}' ({filepath}) pair {pair_name}")
            if cr is not None and cr < 4.5:
                json_contrast_failures.append((theme_id, filepath, pair_name, fg, bg, cr))

    json_themes[theme_id] = (filepath, color_scheme)

if json_schema_errors:
    errors.extend(json_schema_errors)

if json_contrast_failures:
    print('❌ WCAG AA contrast check failed for JSON themes:')
    for theme_id, filepath, pair_name, fg, bg, cr in json_contrast_failures:
        print(f"  - Theme '{theme_id}' ({filepath}) {pair_name}: fg={fg}, bg={bg}, ratio={cr:.2f} < 4.5")
    errors.append('WCAG AA contrast failure in JSON themes')

# 4. Kotlin and JSON Theme Artifact Synchronization
kt_by_id = {tid: (sym, symbol_to_colors[sym]) for sym, tid in symbol_to_id.items()}

sync_errors = []

# Compare tokens
key_mapping = {
    'colorPrimary': 'primary',
    'colorOnPrimary': 'onPrimary',
    'colorSecondary': 'secondary',
    'colorOnSecondary': 'onSecondary',
    'colorBackground': 'background',
    'colorSurface': 'surface',
    'colorOnSurface': 'onSurface',
    'colorOnSurfaceVariant': 'onSurfaceVariant'
}

missing_in_json = set(kt_by_id.keys()) - set(json_themes.keys())
missing_in_kt = set(json_themes.keys()) - set(kt_by_id.keys())

if missing_in_json:
    print('❌ Themes defined in BuiltInThemes.kt but missing JSON artifacts:')
    for tid in sorted(missing_in_json):
        print(f'  - {tid}')
    sync_errors.append('Missing JSON artifacts for Kotlin themes')

if missing_in_kt:
    print('❌ JSON theme artifacts missing corresponding Kotlin definitions in BuiltInThemes.kt:')
    for tid in sorted(missing_in_kt):
        print(f'  - {tid}')
    sync_errors.append('Missing Kotlin definitions for JSON themes')

for tid in sorted(set(kt_by_id.keys()) & set(json_themes.keys())):
    sym, kt_colors = kt_by_id[tid]
    filepath, json_colors = json_themes[tid]

    token_diffs = []
    for kt_key, json_key in key_mapping.items():
        if kt_key in kt_colors:
            kt_val = kt_colors[kt_key].upper()
            json_val = json_colors.get(json_key, '').upper()
            if kt_val != json_val:
                token_diffs.append((kt_key, json_key, kt_val, json_val))

    if token_diffs:
        print(f"❌ Token mismatch between BuiltInThemes.kt ({sym}) and JSON ({filepath}) for theme '{tid}':")
        for kt_k, j_k, kt_v, j_v in token_diffs:
            print(f"  - {kt_k} ({j_k}): Kotlin={kt_v} vs JSON={j_v}")
        sync_errors.append(f"Token mismatch for theme '{tid}'")

if sync_errors:
    errors.extend(sync_errors)

# 5. Asset coverage check (if assets directory exists)
if assets_dir.exists():
    central_files = sorted(list(central_dir.glob("*.json"))) if central_dir.exists() else []
    community_files = sorted(list(community_dir.glob("*.json"))) if community_dir.exists() else []
    repo_files = central_files + community_files
    asset_file_names = {f.name for f in assets_dir.glob("*.json")}
    missing_in_assets = [str(rf) for rf in repo_files if rf.name not in asset_file_names]
    if missing_in_assets:
        print("❌ Missing theme files in Android assets:")
        for missing in missing_in_assets:
            print(f"  - {missing}")
        errors.append("Missing theme files in Android assets")

if errors:
    print(f"\n❌ Validation failed with {len(errors)} error category/categories.")
    sys.exit(1)

print(f"✅ Theme validation passed: {len(catalog_ids)} catalog themes, {len(json_themes)} JSON themes, all WCAG AA compliant and synchronized.")
PY
