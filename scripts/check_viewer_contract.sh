#!/usr/bin/env bash
set -euo pipefail
root=$(git rev-parse --show-toplevel)
contract_ts="$root/packages/viewer-types/src/index.ts"
contract_dart="$root/packages/linkpoint_dart/lib/src/viewer_contract.g.dart"
contract_kt="$root/Linkpoint/src/main/java/com/linkpoint/protocol/ViewerContract.kt"

before_ts=$(mktemp)
before_dart=$(mktemp)
before_kt=$(mktemp)

trap 'cp "$before_ts" "$contract_ts"; cp "$before_dart" "$contract_dart"; cp "$before_kt" "$contract_kt"; rm -f "$before_ts" "$before_dart" "$before_kt"' EXIT

cp "$contract_ts" "$before_ts"
cp "$contract_dart" "$before_dart"
cp "$contract_kt" "$before_kt"

cargo run --quiet -p linkpoint-core --example generate_contract

cmp "$before_ts" "$contract_ts" || {
  echo "Generated TS viewer contract is stale. Run: cargo run -p linkpoint-core --example generate_contract" >&2
  exit 1
}

cmp "$before_dart" "$contract_dart" || {
  echo "Generated Dart viewer contract is stale. Run: cargo run -p linkpoint-core --example generate_contract" >&2
  exit 1
}

cmp "$before_kt" "$contract_kt" || {
  echo "Generated Kotlin viewer contract is stale. Run: cargo run -p linkpoint-core --example generate_contract" >&2
  exit 1
}

