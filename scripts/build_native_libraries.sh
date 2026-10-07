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
        if [[ "${target}" == *"windows-gnu"* ]] && [ "$(uname -s)" = "Linux" ]; then
            if ! command -v x86_64-w64-mingw32-dlltool >/dev/null 2>&1 && ! command -v x86_64-w64-mingw32-gcc >/dev/null 2>&1; then
                echo "Skipping target ${target} (mingw toolchain x86_64-w64-mingw32-dlltool not installed)"
                continue
            fi
        fi
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

echo "=== Compiling Texture Decoder Native Library ==="
C_SRC="${ROOT_DIR}/texture_decoder_native.c"
if [ -f "${C_SRC}" ]; then
    CC_CMD=""
    if command -v gcc >/dev/null 2>&1; then
        CC_CMD="gcc"
    elif command -v clang >/dev/null 2>&1; then
        CC_CMD="clang"
    elif command -v cc >/dev/null 2>&1; then
        CC_CMD="cc"
    fi

    if [ -n "${CC_CMD}" ]; then
        echo "Compiling ${C_SRC} with ${CC_CMD}..."
        ${CC_CMD} -O3 -fopenmp -fPIC -shared "${C_SRC}" -o "${OUTPUT_DIR}/libtexture_decoder_native.so" 2>/dev/null || \
        ${CC_CMD} -O3 -fPIC -shared "${C_SRC}" -o "${OUTPUT_DIR}/libtexture_decoder_native.so"
        cp "${OUTPUT_DIR}/libtexture_decoder_native.so" "${ROOT_DIR}/libtexture_decoder_native.so" 2>/dev/null || true
    else
        echo "Warning: No C compiler found to compile texture_decoder_native.c"
    fi
fi

echo "Native libraries packaged in ${OUTPUT_DIR}"
