#!/bin/bash
set -e

echo "=== Linkpoint Test & Diagnostic Script ==="
echo "Running theme contrast & sync validation..."
./gradlew :Linkpoint:checkThemeContrastAndSync --info "$@"

echo "Running unit tests for Linkpoint module..."
./gradlew :Linkpoint:testStableDebugUnitTest --info "$@"

echo "Unit tests completed successfully!"
