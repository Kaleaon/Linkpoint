#!/bin/bash
set -e

echo "=== Linkpoint Test & Diagnostic Script ==="
echo "Running unit tests for Linkpoint module..."

./gradlew :Linkpoint:testStableDebugUnitTest --info "$@"

echo "Unit tests completed successfully!"
