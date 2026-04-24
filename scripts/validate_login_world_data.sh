#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
APP_DIR="$ROOT_DIR/Linkpoint"
APK_PATH="$APP_DIR/build/outputs/apk/stable/debug/Linkpoint-stable-debug.apk"
ADB="/usr/lib/android-sdk/platform-tools/adb"

USERNAME_DEFAULT="Firstname Lastname"
PASSWORD_DEFAULT="Password"
LINKPOINT_USERNAME="${LINKPOINT_USERNAME:-$USERNAME_DEFAULT}"
LINKPOINT_PASSWORD="${LINKPOINT_PASSWORD:-$PASSWORD_DEFAULT}"

run_unit_checks() {
  echo "[1/3] Running login + world-data unit tests"
  (cd "$APP_DIR" && ./gradlew testStableDebugUnitTest \
    --tests "com.linkpoint.connection.ConnectionIntegrationTest" \
    --tests "com.linkpoint.protocol.messages.RegionHandshakeParserTest" \
    --tests "com.linkpoint.protocol.messages.MessageRouterTest")
}

build_apk() {
  echo "[2/3] Building stable debug APK"
  (cd "$APP_DIR" && ./gradlew assembleStableDebug)
  echo "APK: $APK_PATH"
}

device_smoke() {
  echo "[3/3] Device smoke preflight"
  if ! command -v "$ADB" >/dev/null 2>&1; then
    echo "WARN: adb not found at $ADB. Skipping device smoke."
    return 0
  fi

  local connected
  connected="$($ADB devices | awk 'NR>1 && $2=="device" {print $1}' | head -n1)"
  if [[ -z "$connected" ]]; then
    echo "WARN: no running emulator/device detected."
    echo "      To run device smoke: boot an emulator, then rerun this script."
    return 0
  fi

  echo "Installing APK on $connected"
  "$ADB" -s "$connected" install -r "$APK_PATH"

  cat <<MSG
Device smoke prep complete.
Credentials available for manual login entry:
  username: $LINKPOINT_USERNAME
  password: $LINKPOINT_PASSWORD

After opening Linkpoint, verify these in logcat:
  - successful login response
  - UDP circuit established
  - RegionHandshake received
  - object/avatar updates flowing
MSG
}

run_unit_checks
build_apk
device_smoke

echo "Validation script completed."
