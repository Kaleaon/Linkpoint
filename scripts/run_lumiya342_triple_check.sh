#!/usr/bin/env bash
set -euo pipefail

APK_INPUT="${1:-https://tmpfiles.org/dl/34983673/lumiya3.4.2.apk}"
WORK_DIR="${2:-/tmp/lumiya_task}"
APK_PATH="$WORK_DIR/lumiya3.4.2.apk"
JADX_OUT="$WORK_DIR/jadx_out"
DEX_DIR="$WORK_DIR/dex"
SMALI_OUT="$WORK_DIR/smali_out"
NATIVE_DIR="$WORK_DIR/native_libs"
EXTRA_CODE_DIR="$WORK_DIR/extra_code"
ROUNDTRIP_DIR="$WORK_DIR/roundtrip"
GHIDRA_HOME="${GHIDRA_HOME:-/tmp/ghidra/ghidra_12.0.4_PUBLIC}"
GHIDRA_PROJECT_DIR="$WORK_DIR/ghidra_proj"
GHIDRA_PROJECT_NAME="Lumiya342"

mkdir -p "$WORK_DIR" "$JADX_OUT" "$DEX_DIR" "$SMALI_OUT" "$NATIVE_DIR" "$EXTRA_CODE_DIR" "$ROUNDTRIP_DIR"

if [ -f "$APK_INPUT" ]; then
  cp "$APK_INPUT" "$APK_PATH"
elif [ -f "$APK_PATH" ]; then
  echo "Using cached APK at $APK_PATH"
else
  curl -L --fail -o "$APK_PATH" "$APK_INPUT"
fi

# Basic container integrity check.
if unzip -tqq "$APK_PATH"; then
  ZIP_INTEGRITY_OK=true
else
  ZIP_INTEGRITY_OK=false
fi

APKSIGNER_STATUS="not_run"
if command -v apksigner >/dev/null 2>&1; then
  if apksigner verify --verbose "$APK_PATH" >/dev/null 2>&1; then
    APKSIGNER_STATUS="verified"
  else
    APKSIGNER_STATUS="failed"
  fi
fi

ZIPALIGN_STATUS="not_run"
if command -v zipalign >/dev/null 2>&1; then
  if zipalign -c -p 4 "$APK_PATH" >/dev/null 2>&1; then
    ZIPALIGN_STATUS="verified"
  else
    ZIPALIGN_STATUS="failed"
  fi
fi

set +e
jadx -d "$JADX_OUT" "$APK_PATH"
JADX_EXIT=$?
set -e

echo "jadx exit code: $JADX_EXIT"

# Extract all DEX files so we can process multi-dex APKs consistently.
unzip -q -o "$APK_PATH" 'classes*.dex' -d "$DEX_DIR"
mapfile -t DEX_FILES < <(find "$DEX_DIR" -maxdepth 1 -type f -name 'classes*.dex' | sort)
if [ "${#DEX_FILES[@]}" -eq 0 ]; then
  echo "error: no classes*.dex entries found in APK" >&2
  exit 1
fi

# Extract potential secondary code containers sometimes loaded dynamically.
unzip -q -o "$APK_PATH" 'assets/*.jar' 'assets/*.zip' 'assets/*.dex' -d "$EXTRA_CODE_DIR" || true

# Extract native libraries for radare2/rabin2 inspection.
unzip -q -o "$APK_PATH" 'lib/*' -d "$NATIVE_DIR" || true

# Ghidra workflow adapted from remyhax guide: operate on DEX inputs directly
# and process all classes*.dex units instead of a single top-level APK import.
if [ -x "$GHIDRA_HOME/support/analyzeHeadless" ]; then
  mkdir -p "$GHIDRA_PROJECT_DIR"

  first_dex="${DEX_FILES[0]}"
  JAVA_HOME=${JAVA_HOME:-/usr/lib/jvm/java-21-openjdk-amd64} \
  "$GHIDRA_HOME/support/analyzeHeadless" \
    "$GHIDRA_PROJECT_DIR" "$GHIDRA_PROJECT_NAME" \
    -import "$first_dex" -overwrite -analysisTimeoutPerFile 900 -max-cpu 4 || true

  for dex_file in "${DEX_FILES[@]:1}"; do
    JAVA_HOME=${JAVA_HOME:-/usr/lib/jvm/java-21-openjdk-amd64} \
    "$GHIDRA_HOME/support/analyzeHeadless" \
      "$GHIDRA_PROJECT_DIR" "$GHIDRA_PROJECT_NAME" \
      -process "*" -import "$dex_file" -analysisTimeoutPerFile 900 -max-cpu 4 || true
  done
else
  echo "warning: ghidra not found at $GHIDRA_HOME; skipping ghidra step"
fi

for dex_file in "${DEX_FILES[@]}"; do
  dex_name="$(basename "$dex_file" .dex)"
  baksmali disassemble "$dex_file" -o "$SMALI_OUT/$dex_name"

  # Round-trip sanity: reassemble smali and check generated dex exists.
  smali assemble "$SMALI_OUT/$dex_name" -o "$ROUNDTRIP_DIR/${dex_name}.roundtrip.dex" || true
done

WORK_DIR="$WORK_DIR" ZIP_INTEGRITY_OK="$ZIP_INTEGRITY_OK" APKSIGNER_STATUS="$APKSIGNER_STATUS" ZIPALIGN_STATUS="$ZIPALIGN_STATUS" python3 - <<'PY'
from pathlib import Path
import hashlib
import json
import os
import re
import subprocess

base = Path(os.environ["WORK_DIR"])
java_root = base / "jadx_out" / "sources"
smali_root = base / "smali_out"
dex_root = base / "dex"
native_root = base / "native_libs"
extra_code_root = base / "extra_code"
roundtrip_root = base / "roundtrip"

java_set = {
    str(p.relative_to(java_root).with_suffix("")) .replace("/", ".")
    for p in java_root.rglob("*.java")
}

smali_set = set()
for p in smali_root.rglob("*.smali"):
    for line in p.read_text(errors="ignore").splitlines():
        if line.startswith(".class "):
            m = re.search(r"L([^;]+);", line)
            if m:
                smali_set.add(m.group(1).replace("/", "."))
            break

ghidra_set = set()
dex_integrity = []
for dex in sorted(dex_root.glob("classes*.dex")):
    dex_bytes = dex.read_bytes()
    magic = dex_bytes[:8]
    valid_magic = magic.startswith(b"dex\n")
    strings = subprocess.check_output(["strings", str(dex)], text=True, errors="ignore")
    ghidra_set.update(
        m.group(1).replace("/", ".")
        for m in re.finditer(r"L([^;\n]+);", strings)
        if "/" in m.group(1)
    )
    dex_integrity.append(
        {
            "file": dex.name,
            "size": dex.stat().st_size,
            "sha256": hashlib.sha256(dex_bytes).hexdigest(),
            "magic": magic.decode(errors="replace"),
            "valid_dex_magic": valid_magic,
        }
    )

roundtrip_status = []
for rdex in sorted(roundtrip_root.glob("*.roundtrip.dex")):
    data = rdex.read_bytes()
    roundtrip_status.append(
        {
            "file": rdex.name,
            "size": rdex.stat().st_size,
            "valid_dex_magic": data[:8].startswith(b"dex\n"),
        }
    )

extra_code_artifacts = sorted(
    str(p.relative_to(extra_code_root))
    for p in extra_code_root.rglob("*")
    if p.is_file() and p.suffix.lower() in {".jar", ".zip", ".dex"}
)

# Appknox-guided radare2 lens:
# - inspect native ELF metadata with rabin2 -I
# - inspect strings with rabin2 -z and flag potential sensitive tokens
native_lib_info = []
native_sensitive_hits = []
keywords = re.compile(r"(password|secret|token|api[_-]?key|private[_-]?key)", re.IGNORECASE)

for so_path in sorted(native_root.rglob("*.so")):
    arch = "unknown"
    lang = ""
    bits = ""
    try:
        info = subprocess.check_output(["rabin2", "-I", str(so_path)], text=True, errors="ignore")
        for line in info.splitlines():
            if line.startswith("arch"):
                arch = line.split(maxsplit=1)[1].strip()
            elif line.startswith("lang"):
                lang = line.split(maxsplit=1)[1].strip()
            elif line.startswith("bits"):
                bits = line.split(maxsplit=1)[1].strip()
    except (FileNotFoundError, subprocess.CalledProcessError):
        info = ""

    native_lib_info.append({
        "path": str(so_path.relative_to(native_root)),
        "arch": arch,
        "bits": bits,
        "lang": lang,
        "sha256": hashlib.sha256(so_path.read_bytes()).hexdigest(),
    })

    try:
        strings_out = subprocess.check_output(["rabin2", "-z", str(so_path)], text=True, errors="ignore")
    except (FileNotFoundError, subprocess.CalledProcessError):
        strings_out = ""

    for line in strings_out.splitlines():
        if keywords.search(line):
            native_sensitive_hits.append({
                "library": str(so_path.relative_to(native_root)),
                "match": line.strip()[:240],
            })
            if len(native_sensitive_hits) >= 50:
                break

lumiya_java = sorted(c for c in java_set if c.startswith("com.lumiyaviewer"))
missing_java_in_smali = sorted(java_set - smali_set)
missing_lumiya_in_smali = sorted(c for c in lumiya_java if c not in smali_set)
missing_lumiya_in_ghidra = sorted(c for c in lumiya_java if c not in ghidra_set)

summary = {
    "apk_integrity": {
        "zip_structure_ok": os.environ.get("ZIP_INTEGRITY_OK") == "true",
        "apksigner_status": os.environ.get("APKSIGNER_STATUS"),
        "zipalign_status": os.environ.get("ZIPALIGN_STATUS"),
    },
    "dex_files_processed": sorted(p.name for p in dex_root.glob("classes*.dex")),
    "dex_integrity": dex_integrity,
    "roundtrip_dex_status": roundtrip_status,
    "extra_code_artifacts": extra_code_artifacts,
    "java_total": len(java_set),
    "smali_total": len(smali_set),
    "ghidra_like_total": len(ghidra_set),
    "missing_java_in_smali_count": len(missing_java_in_smali),
    "missing_java_in_smali_sample": missing_java_in_smali[:25],
    "lumiya_java_total": len(lumiya_java),
    "lumiya_missing_in_smali_count": len(missing_lumiya_in_smali),
    "lumiya_missing_in_smali_sample": missing_lumiya_in_smali[:25],
    "lumiya_missing_in_ghidra_strings_count": len(missing_lumiya_in_ghidra),
    "lumiya_missing_in_ghidra_strings_sample": missing_lumiya_in_ghidra[:25],
    "radare2_native_library_count": len(native_lib_info),
    "radare2_native_library_sample": native_lib_info[:15],
    "radare2_sensitive_string_hit_count": len(native_sensitive_hits),
    "radare2_sensitive_string_hit_sample": native_sensitive_hits[:25],
}

triple_out = Path("/workspace/Linkpoint/docs/apk_analysis/lumiya3.4.2_triple_check_summary.json")
triple_out.write_text(json.dumps(summary, indent=2) + "\n")

radare2_out = Path("/workspace/Linkpoint/docs/apk_analysis/lumiya3.4.2_radare2_summary.json")
radare2_out.write_text(
    json.dumps(
        {
            "native_library_inventory": native_lib_info,
            "sensitive_string_hits": native_sensitive_hits,
        },
        indent=2,
    )
    + "\n"
)

print(json.dumps(summary, indent=2))
PY
