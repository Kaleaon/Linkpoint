#!/usr/bin/env bash
set -euo pipefail

if ! command -v cargo >/dev/null 2>&1; then
  echo "[viewer-contract] cargo is not installed; skipping viewer contract check."
  exit 0
fi

root=$(git rev-parse --show-toplevel)
contract_ts="$root/packages/viewer-types/src/index.ts"
contract_dart="$root/packages/linkpoint_dart/lib/src/viewer_contract.g.dart"
contract_kt="$root/legacy/Linkpoint/src/main/java/com/linkpoint/protocol/ViewerContract.kt"
contract_swift="$root/platforms/iOS/Sources/LLSD/ViewerContract.swift"
contract_cs="$root/sdks/csharp/src/ViewerContract.cs"

before_ts=$(mktemp)
before_dart=$(mktemp)
before_kt=$(mktemp)
before_swift=$(mktemp)
before_cs=$(mktemp)

trap 'cp "$before_ts" "$contract_ts"; cp "$before_dart" "$contract_dart"; cp "$before_kt" "$contract_kt"; cp "$before_swift" "$contract_swift"; cp "$before_cs" "$contract_cs"; rm -f "$before_ts" "$before_dart" "$before_kt" "$before_swift" "$before_cs"' EXIT

cp "$contract_ts" "$before_ts"
cp "$contract_dart" "$before_dart"
cp "$contract_kt" "$before_kt"
touch "$contract_swift" && cp "$contract_swift" "$before_swift"
touch "$contract_cs" && cp "$contract_cs" "$before_cs"

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

cmp "$before_swift" "$contract_swift" || {
  echo "Generated Swift viewer contract is stale. Run: cargo run -p linkpoint-core --example generate_contract" >&2
  exit 1
}

cmp "$before_cs" "$contract_cs" || {
  echo "Generated C# viewer contract is stale. Run: cargo run -p linkpoint-core --example generate_contract" >&2
  exit 1
}
