#!/bin/bash
set -euo pipefail

PARITY_SNAPSHOT_SCRIPTS=(
  "scripts/generate_file_inventory.sh"
  "scripts/generate_individual_reviews.sh"
)

missing=0

# 1. Parity snapshot worktree guards
for script in "${PARITY_SNAPSHOT_SCRIPTS[@]}"; do
  if [[ ! -f "$script" ]]; then
    echo "ERROR: expected parity snapshot script not found: $script" >&2
    missing=1
    continue
  fi

  if ! grep -q 'require_clean_worktree\.sh' "$script"; then
    echo "ERROR: $script does not enforce clean working tree guard." >&2
    missing=1
  fi
done

# 2. Canonical test vectors files & schemas presence
REQUIRED_VECTOR_FILES=(
  "test-vectors/math/quaternion_matrix_transform_vectors.json"
  "test-vectors/mesh/llmesh_decompress_vectors.json"
  "test-vectors/textures/j2k_texture_decoder_vectors.json"
  "test-vectors/schemas/math_vectors_schema.json"
  "test-vectors/schemas/mesh_vectors_schema.json"
  "test-vectors/schemas/texture_vectors_schema.json"
)

for vfile in "${REQUIRED_VECTOR_FILES[@]}"; do
  if [[ ! -f "$vfile" ]]; then
    echo "ERROR: canonical test vector file missing: $vfile" >&2
    missing=1
  fi
done

# 3. Test vector loader suite presence across platforms
REQUIRED_LOADER_SUITES=(
  "legacy/Linkpoint/src/test/kotlin/com/linkpoint/vectors/SharedTestVectorSuiteTest.kt"
  "legacy/LLSD-KOTLIN/src/test/kotlin/lindenlab/llsd/vectors/SharedTestVectorSuiteTest.kt"
  "crates/linkpoint-scene/tests/vector_tests.rs"
  "apps/linkpoint/src/linkpoint/__tests__/test-vectors.test.ts"
)

for suite in "${REQUIRED_LOADER_SUITES[@]}"; do
  if [[ ! -f "$suite" ]]; then
    echo "ERROR: required test vector loader suite missing: $suite" >&2
    missing=1
  fi
done

if [[ "$missing" -ne 0 ]]; then
  echo "Parity snapshot guard check FAILED." >&2
  exit 1
fi

# 4. Enforce valid execution of vector assertion suites
echo "Executing vector assertion suites..."
cargo test -p linkpoint-scene --test vector_tests
npx vitest run apps/linkpoint/src/linkpoint/__tests__/test-vectors.test.ts

echo "Parity snapshot guard check passed."
