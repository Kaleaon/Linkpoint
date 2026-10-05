#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

echo "=== Cross-compiling Linkpoint Protocol Native Libraries ==="
cd "${ROOT_DIR}"

OUTPUT_DIR="builds/native"
mkdir -p "${OUTPUT_DIR}"

TARGETS=(
    "x86_64-unknown-linux-gnu"
)

# Optional cross targets if toolchain targets exist
OPTIONAL_TARGETS=(
    "x86_64-pc-windows-gnu"
    "aarch64-linux-android"
    "x86_64-linux-android"
    "aarch64-apple-ios"
    "aarch64-apple-darwin"
    "x86_64-apple-darwin"
)

for target in "${TARGETS[@]}"; do
    echo "Building for mandatory target: ${target}"
    rustup target add "${target}" || true
    cargo build -p linkpoint-protocol --release --target "${target}"
done

for target in "${OPTIONAL_TARGETS[@]}"; do
    if rustup target list | grep -q "${target} (installed)"; then
        echo "Building for installed target: ${target}"
        cargo build -p linkpoint-protocol --release --target "${target}" || echo "Warning: build for ${target} skipped"
    else
        echo "Skipping target ${target} (not installed in current toolchain environment)"
    fi
done

# Copy primary Linux target artifact
if [ -f "target/release/liblinkpoint_protocol.so" ]; then
    cp "target/release/liblinkpoint_protocol.so" "${OUTPUT_DIR}/liblinkpoint_protocol.so"
elif [ -f "target/x86_64-unknown-linux-gnu/release/liblinkpoint_protocol.so" ]; then
    cp "target/x86_64-unknown-linux-gnu/release/liblinkpoint_protocol.so" "${OUTPUT_DIR}/liblinkpoint_protocol.so"
fi

echo "Native libraries packaged in ${OUTPUT_DIR}"
