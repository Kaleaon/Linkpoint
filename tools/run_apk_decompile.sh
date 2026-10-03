#!/usr/bin/env bash
set -euo pipefail

if [[ $# -lt 1 ]]; then
  echo "Usage: $0 path/to/lumiya.apk|zip"; exit 1
fi

INPUT="$1"
MAX_HEAP="${MAX_HEAP:-4096}"

mkdir -p out/jadx out/apktool out/dex2jar out/meta

APK="lumiya.apk"
MIME=$(file -b --mime-type "$INPUT" || true)
if [[ "$MIME" == "application/zip" ]]; then
  if unzip -p "$INPUT" classes.dex >/dev/null 2>&1; then
    cp "$INPUT" "$APK"
  else
    INNER=$(unzip -l "$INPUT" | awk '{print $4}' | grep -i '\.apk$' | head -n1 || true)
    if [[ -n "$INNER" ]]; then
      unzip -p "$INPUT" "$INNER" > "$APK"
    else
      echo "ZIP does not appear to be an APK or contain one"; exit 1
    fi
  fi
else
  cp "$INPUT" "$APK"
fi

file "$APK" | tee out/meta/file_type.txt
unzip -l "$APK" | tee out/meta/apk_zip_list.txt

apktool d -f -o out/apktool "$APK"

if ! command -v jadx >/dev/null 2>&1; then
  echo "jadx not found; install it or run via CI workflow."; exit 2
fi
jadx -j2 -Xmx${MAX_HEAP}m -d out/jadx "$APK"

if command -v d2j-dex2jar >/dev/null 2>&1 && [[ -f /opt/cfr.jar ]]; then
  mkdir -p out/dex2jar/src
  d2j-dex2jar -f -o out/dex2jar/classes.jar "$APK"
  java -Xmx${MAX_HEAP}m -jar /opt/cfr.jar out/dex2jar/classes.jar --outputdir out/dex2jar/src
else
  echo "dex2jar/CFR not installed; skipping alt decompile path."
fi