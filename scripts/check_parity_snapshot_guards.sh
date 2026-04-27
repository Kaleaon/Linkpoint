#!/bin/bash
set -euo pipefail

PARITY_SNAPSHOT_SCRIPTS=(
  "scripts/generate_file_inventory.sh"
  "scripts/generate_individual_reviews.sh"
)

missing=0
for script in "${PARITY_SNAPSHOT_SCRIPTS[@]}"; do
  if [[ ! -f "$script" ]]; then
    echo "ERROR: expected parity snapshot script not found: $script" >&2
    missing=1
    continue
  fi

  if ! rg -q 'require_clean_worktree\.sh' "$script"; then
    echo "ERROR: $script does not enforce clean working tree guard." >&2
    missing=1
  fi
done

if [[ "$missing" -ne 0 ]]; then
  exit 1
fi

echo "Parity snapshot guard check passed."
