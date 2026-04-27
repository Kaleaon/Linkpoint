#!/usr/bin/env bash
set -euo pipefail

TARGETS=(
  "Linkpoint/src/main/java"
  "Linkpoint/src/main/kotlin"
)

patterns=(
  "Method not decompiled"
  "Code decompiled incorrectly"
  "TODO: Auto-generated method stub"
  "UnsupportedOperationException(\"Method not decompiled"
  "throw NotImplementedError("
)

hits=0
for target in "${TARGETS[@]}"; do
  [[ -d "$target" ]] || continue
  for pattern in "${patterns[@]}"; do
    if rg -n -F "$pattern" "$target"; then
      hits=1
    fi
  done
done

if [[ "$hits" -ne 0 ]]; then
  echo "❌ Decompilation/stub artifacts found in production sources." >&2
  exit 1
fi

echo "✅ No decompilation/stub artifacts detected in production sources."
