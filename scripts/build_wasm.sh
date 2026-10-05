#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

echo "=== Building Rust Linkpoint Protocol WebAssembly module ==="
cd "${ROOT_DIR}"

rustup target add wasm32-unknown-unknown || true

cargo build --target wasm32-unknown-unknown --features wasm -p linkpoint-protocol --release

OUT_DIR="packages/linkpoint_wasm"
mkdir -p "${OUT_DIR}"

if command -v wasm-bindgen >/dev/null 2>&1; then
    wasm-bindgen target/wasm32-unknown-unknown/release/linkpoint_protocol.wasm --out-dir "${OUT_DIR}" --target bundler
else
    echo "Warning: wasm-bindgen CLI not found; copying raw wasm"
    cp target/wasm32-unknown-unknown/release/linkpoint_protocol.wasm "${OUT_DIR}/linkpoint_protocol_bg.wasm"
fi

WASM_FILE="${OUT_DIR}/linkpoint_protocol_bg.wasm"
if [ ! -f "${WASM_FILE}" ]; then
    WASM_FILE="${OUT_DIR}/linkpoint_protocol.wasm"
fi

WASM_SIZE_KB=$(du -k "${WASM_FILE}" | cut -f1)
echo "WASM Binary Size: ${WASM_SIZE_KB} KB (${WASM_FILE})"

if [ "${WASM_SIZE_KB}" -gt 500 ]; then
    echo "ERROR: WebAssembly binary size (${WASM_SIZE_KB} KB) exceeds maximum allowed size of 500 KB!"
    exit 1
fi

echo "SUCCESS: WebAssembly binary size (${WASM_SIZE_KB} KB) is within 500 KB limit."
