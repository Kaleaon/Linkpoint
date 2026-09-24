#!/usr/bin/env bash
set -euo pipefail
root=$(git rev-parse --show-toplevel)
contract="$root/packages/viewer-types/src/index.ts"
before=$(mktemp)
trap 'cp "$before" "$contract"; rm -f "$before"' EXIT
cp "$contract" "$before"
cargo run --quiet -p linkpoint-core --example generate_contract
cmp "$before" "$contract" || {
  echo "Generated viewer contract is stale. Run: cargo run -p linkpoint-core --example generate_contract" >&2
  exit 1
}
