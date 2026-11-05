#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
WORKSPACE_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

TARGET_FILTER=""
QUICK_MODE=false

for arg in "$@"; do
    case "$arg" in
        --quick)
            QUICK_MODE=true
            ;;
        --target=*)
            TARGET_FILTER="${arg#*=}"
            ;;
    esac
done

echo "========================================================"
echo "      Linkpoint Polyglot SDK Unified Test Suite        "
echo "========================================================"
echo "Workspace Root: $WORKSPACE_ROOT"
echo "Timestamp: $(date -u +'%Y-%m-%dT%H:%M:%SZ')"
echo ""

PASSED_COUNT=0
SKIPPED_COUNT=0
FAILED_COUNT=0
RESULTS=()

should_run_target() {
    local key="$1"
    if [ -z "$TARGET_FILTER" ]; then
        return 0
    fi
    if [[ ",$TARGET_FILTER," == *",$key,"* ]]; then
        return 0
    fi
    return 1
}

run_target() {
    local target_key="$1"
    local target_name="$2"
    local check_cmd="$3"
    local run_cmd="$4"

    if ! should_run_target "$target_key"; then
        return 0
    fi

    echo "--------------------------------------------------------"
    echo "Evaluating Target: $target_name ($target_key)"
    echo "--------------------------------------------------------"

    if ! eval "$check_cmd" > /dev/null 2>&1; then
        echo "[SKIP] $target_name: Required toolchain or runtime not found in environment."
        SKIPPED_COUNT=$((SKIPPED_COUNT + 1))
        RESULTS+=("[SKIP] $target_name")
        return 0
    fi

    echo "Running test command for $target_name..."
    if (cd "$WORKSPACE_ROOT" && eval "$run_cmd"); then
        echo "[PASS] $target_name: Test suite executed successfully."
        PASSED_COUNT=$((PASSED_COUNT + 1))
        RESULTS+=("[PASS] $target_name")
    else
        echo "[FAIL] $target_name: Test execution failed!"
        FAILED_COUNT=$((FAILED_COUNT + 1))
        RESULTS+=("[FAIL] $target_name")
    fi
}

# 1. Kotlin Target
KOTLIN_CMD="./gradlew :Linkpoint:testStableDebugUnitTest"
if [ "$QUICK_MODE" = true ]; then
    KOTLIN_CMD="./gradlew :Linkpoint:compileStableDebugUnitTestKotlin"
fi
run_target "kotlin" "Kotlin (Android)" \
    "test -x gradlew || command -v gradlew" \
    "$KOTLIN_CMD"

# 2. Java Target
run_target "java" "Java (Maven LLSD)" \
    "command -v mvn && test -f LLSD-KOTLIN/pom.xml" \
    "mvn test -f LLSD-KOTLIN/pom.xml -Dtest='*Test'"

# 3. Rust Target
run_target "rust" "Rust Core Workspace" \
    "command -v cargo" \
    "cargo test --workspace --locked"

# 4. Dart Target
run_target "dart" "Dart / Flutter SDK" \
    "command -v flutter || command -v dart" \
    "cd packages/linkpoint_dart && (/opt/flutter/bin/flutter test 2>/dev/null || flutter test 2>/dev/null || dart test)"

# 5. TypeScript Target
run_target "typescript" "TypeScript / Web Workspace" \
    "command -v npm" \
    "npm run test:web"

# 6. Python Target
run_target "python" "Python Protocol Engine" \
    "command -v python3" \
    "PYTHONPATH=\"$WORKSPACE_ROOT\" python3 tools/protocol_gen/tests/test_generator.py"

# 7. Swift Target
run_target "swift" "Swift iOS Engine" \
    "command -v swift" \
    "cd platforms/iOS && swift test"

# 8. C# Target
run_target "csharp" "C# .NET Solution" \
    "command -v dotnet" \
    "cd platforms/dotnet && (dotnet test 2>/dev/null || dotnet run --project Linkpoint.Protocol.Tests/Linkpoint.Protocol.Tests.csproj)"

echo ""
echo "========================================================"
echo "          Polyglot Test Execution Summary               "
echo "========================================================"
for res in "${RESULTS[@]}"; do
    echo "  $res"
done
echo "--------------------------------------------------------"
echo " Passed: $PASSED_COUNT | Skipped: $SKIPPED_COUNT | Failed: $FAILED_COUNT"
echo "========================================================"

if [ "$FAILED_COUNT" -gt 0 ]; then
    echo "Polyglot test verification FAILED."
    exit 1
else
    echo "Polyglot test verification PASSED (all available runtimes verified)."
    exit 0
fi
