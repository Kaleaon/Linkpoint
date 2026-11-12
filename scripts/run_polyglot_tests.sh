#!/usr/bin/env bash
set -euo pipefail

# Dynamic root directory resolution
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "${SCRIPT_DIR}/.." && pwd)"
cd "${REPO_ROOT}"

echo "========================================================"
echo " Starting Linkpoint Polyglot Conformance Test Suite"
echo " Repository root: ${REPO_ROOT}"
echo "========================================================"

# Step 1: Mandatory FFI Native Library Build
echo "=== [1/6] Building Mandatory FFI Shared Library (linkpoint-protocol) ==="
NATIVE_BUILD_DIR="${REPO_ROOT}/builds/native"
mkdir -p "${NATIVE_BUILD_DIR}"

if ! cargo build -p linkpoint-protocol --release; then
    echo "[ERROR] FFI Native shared library build failed! Stopping orchestrator immediately."
    exit 1
fi

# Copy library outputs into builds/native/
if [ -f "${REPO_ROOT}/target/release/liblinkpoint_protocol.so" ]; then
    cp "${REPO_ROOT}/target/release/liblinkpoint_protocol.so" "${NATIVE_BUILD_DIR}/liblinkpoint_protocol.so"
fi
if [ -f "${REPO_ROOT}/target/release/liblinkpoint_protocol.dylib" ]; then
    cp "${REPO_ROOT}/target/release/liblinkpoint_protocol.dylib" "${NATIVE_BUILD_DIR}/liblinkpoint_protocol.dylib"
fi
if [ -f "${REPO_ROOT}/target/release/linkpoint_protocol.dll" ]; then
    cp "${REPO_ROOT}/target/release/linkpoint_protocol.dll" "${NATIVE_BUILD_DIR}/linkpoint_protocol.dll"
fi

if [ ! -f "${NATIVE_BUILD_DIR}/liblinkpoint_protocol.so" ] && \
   [ ! -f "${NATIVE_BUILD_DIR}/liblinkpoint_protocol.dylib" ] && \
   [ ! -f "${NATIVE_BUILD_DIR}/linkpoint_protocol.dll" ]; then
    echo "[ERROR] Native shared library output missing in ${NATIVE_BUILD_DIR} after build!"
    exit 1
fi
echo "✅ Mandatory FFI shared libraries ready in ${NATIVE_BUILD_DIR}"

# Step 2: Rust Protocol Test Suite
echo "=== [2/6] Running Rust Core & Protocol Conformance Suite ==="
cargo test -p linkpoint-protocol --locked

# Step 3: TypeScript / WASM Test Suite
echo "=== [3/6] Running TypeScript / WASM Vector Conformance Suite ==="
if [ ! -d "${REPO_ROOT}/node_modules" ]; then
    npm ci --no-audit --no-fund
fi
npx vitest run apps/linkpoint/src/lib/llsd_31_vectors.test.ts apps/linkpoint/src/lib/llsd_wasm_31_vectors.test.ts

# Step 4: Dart / Flutter FFI Conformance Suite
echo "=== [4/6] Running Dart / Flutter FFI Conformance Suite ==="
(
    cd "${REPO_ROOT}/packages/linkpoint_dart"
    flutter pub get
    flutter test test/llsd_31_vectors_ffi_test.dart
)

# Step 5: C++20 Native Test Suite
echo "=== [5/6] Compiling and Running C++20 Native Test Harness ==="
CXX_CMD=""
if command -v g++ >/dev/null 2>&1; then
    CXX_CMD="g++"
elif command -v clang++ >/dev/null 2>&1; then
    CXX_CMD="clang++"
elif command -v cl.exe >/dev/null 2>&1; then
    CXX_CMD="cl.exe"
elif [ -x "/usr/bin/g++" ]; then
    CXX_CMD="/usr/bin/g++"
elif [ -x "/usr/bin/clang++" ]; then
    CXX_CMD="/usr/bin/clang++"
fi

if [ -z "${CXX_CMD}" ]; then
    echo "[ERROR] No C++ compiler (g++, clang++, cl.exe) found!"
    exit 1
fi

echo "Found C++ compiler: ${CXX_CMD}"
CPP_BIN="${NATIVE_BUILD_DIR}/test_llsd_native"
if [[ "${CXX_CMD}" == "cl.exe" ]]; then
    cl.exe /std:c++20 /I "${REPO_ROOT}/legacy/include" "${REPO_ROOT}/legacy/tests/cpp/test_llsd_native.cpp" /Fe:"${CPP_BIN}.exe"
    "${CPP_BIN}.exe"
else
    ${CXX_CMD} -std=c++20 -I "${REPO_ROOT}/legacy/include" "${REPO_ROOT}/legacy/tests/cpp/test_llsd_native.cpp" -o "${CPP_BIN}"
    "${CPP_BIN}"
fi

# Step 6: Kotlin LLSD Conformance Suite
echo "=== [6/6] Running Kotlin LLSD Conformance Suite ==="
(
    cd "${REPO_ROOT}/legacy"
    ./gradlew :llsd-core:test --no-daemon
)

# Optional Swift execution if toolchain exists
if command -v swift >/dev/null 2>&1; then
    echo "=== Running Swift iOS LLSD Conformance Suite ==="
    (
        cd "${REPO_ROOT}/platforms/iOS"
        swift test
    )
fi

echo "========================================================"
echo "🎉 ALL POLYGLOT TEST SUITES PASSED SUCCESSFULLY!"
echo "========================================================"
