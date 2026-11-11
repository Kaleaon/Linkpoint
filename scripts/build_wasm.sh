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

if ! command -v wasm-bindgen >/dev/null 2>&1; then
    echo "Warning: wasm-bindgen CLI not found; attempting cargo install wasm-bindgen-cli..."
    cargo install wasm-bindgen-cli --version 0.2.129 || true
fi

if command -v wasm-bindgen >/dev/null 2>&1; then
    wasm-bindgen target/wasm32-unknown-unknown/release/linkpoint_protocol.wasm --out-dir "${OUT_DIR}" --target bundler
else
    echo "Warning: wasm-bindgen CLI not found; copying raw wasm"
    cp target/wasm32-unknown-unknown/release/linkpoint_protocol.wasm "${OUT_DIR}/linkpoint_protocol_bg.wasm"
fi

JS_FILE="${OUT_DIR}/linkpoint_protocol.js"
if [ -f "${JS_FILE}" ]; then
    node -e '
      const fs = require("fs");
      const p = process.argv[1];
      let content = fs.readFileSync(p, "utf8");
      if (content.includes("import * as wasm from \"./linkpoint_protocol_bg.wasm\";")) {
        content = content.replace(
          /import \* as wasm from "\.\/linkpoint_protocol_bg\.wasm";/g,
          "import initWasm from \"./linkpoint_protocol_bg.wasm?init\";"
        );
        content = content.replace(
          /__wbg_set_wasm\(wasm\);/g,
          "if (typeof initWasm === \"function\") {\n  initWasm().then((exports) => {\n    __wbg_set_wasm(exports);\n  }).catch(() => {});\n}"
        );
        fs.writeFileSync(p, content);
      }
    ' "${JS_FILE}"
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
