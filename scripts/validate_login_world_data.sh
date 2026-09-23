#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
APP_DIR="$ROOT_DIR/Linkpoint"
APK_PATH="$APP_DIR/build/outputs/apk/stable/debug/Linkpoint-stable-debug.apk"
SDK_ROOT="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-$ROOT_DIR/android-sdk}}"
ADB="$SDK_ROOT/platform-tools/adb"
AAPT="$SDK_ROOT/build-tools/35.0.0/aapt"

USERNAME_DEFAULT="Firstname Lastname"
PASSWORD_DEFAULT="Password"
LINKPOINT_USERNAME="${LINKPOINT_USERNAME:-$USERNAME_DEFAULT}"
LINKPOINT_PASSWORD="${LINKPOINT_PASSWORD:-$PASSWORD_DEFAULT}"

run_unit_checks() {
  echo "[1/4] Running the complete stable unit suite"
  # This covers login/circuit bootstrap, LLUDP parsing and reliability,
  # world/object/avatar state, assets, and both rendering backends. The live
  # grid tests run when test-credentials.properties is present and otherwise
  # skip themselves explicitly.
  (cd "$APP_DIR" && ./gradlew testStableDebugUnitTest)
}

build_apk() {
  echo "[2/4] Building stable debug APK (Kotlin, D8, native render codecs, packaging)"
  (cd "$APP_DIR" && ./gradlew assembleStableDebug)
  echo "APK: $APK_PATH"
}

verify_apk() {
  echo "[3/4] Verifying installable APK contents"
  test -s "$APK_PATH"

  if [[ -x "$AAPT" ]]; then
    local badging
    badging="$("$AAPT" dump badging "$APK_PATH")"
    grep -q "package: name='com.linkpoint.debug'" <<<"$badging"
    grep -q "launchable-activity:" <<<"$badging"
  else
    echo "WARN: aapt not found at $AAPT; package metadata check skipped."
  fi

  # Rendering and texture decode depend on packaged native libraries for every
  # ABI declared in defaultConfig. Fail before device install if one is absent.
  local apk_entries
  apk_entries="$(mktemp)"
  unzip -Z1 "$APK_PATH" >"$apk_entries"
  for abi in arm64-v8a armeabi-v7a x86 x86_64; do
    grep -q "^lib/$abi/liblinkpoint-j2k.so$" "$apk_entries"
  done
  rm -f "$apk_entries"
}

device_smoke() {
  echo "[4/4] Device install and launch smoke"
  if [[ ! -x "$ADB" ]]; then
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
  "$ADB" -s "$connected" shell monkey -p com.linkpoint.debug 1 >/dev/null
  sleep 2
  if ! "$ADB" -s "$connected" shell pidof com.linkpoint.debug | grep -q '[0-9]'; then
    echo "ERROR: Linkpoint did not remain running after launch." >&2
    return 1
  fi

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
verify_apk
device_smoke

echo "Validation script completed."
