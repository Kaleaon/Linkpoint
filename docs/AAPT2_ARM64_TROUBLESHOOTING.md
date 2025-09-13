# AAPT2 on ARM64 – Troubleshooting Guide

AAPT2 (Android Asset Packaging Tool v2) compiles and links resources. On some ARM64 environments, older toolchains bundled x86_64-only binaries causing build failures.

This guide explains how we fix it with modern Build-Tools and, if needed, a manual override.

---

## Quick Fix Checklist

1) Use AGP 8.6.0 and Gradle 8.7 (done in this repo)
2) Use JDK 17
3) Install Android Build-Tools 35.0.x (includes universal/arm64 aapt2)
4) Ensure compileSdk/targetSdk are 35 (done)
5) Remove `<uses-sdk>` from AndroidManifest (SDK is defined in Gradle) (done)

If you still have issues: use `android.aapt2FromMavenOverride`.

---

## Manual override

- Find your aapt2 path:
```
$ANDROID_HOME/build-tools/35.0.x/aapt2
```
- Edit `/app/gradle.properties` and set:
```
android.aapt2FromMavenOverride=/absolute/path/to/Android/sdk/build-tools/35.0.x/aapt2
```

Gradle will use the provided aapt2 binary instead of the one downloaded from Maven.

---

## Common errors and fixes

- Error: `java.io.IOException: error=8, Exec format error`
  - Cause: Wrong architecture binary
  - Fix: Install Build-Tools 35 and/or set `android.aapt2FromMavenOverride` to a native binary for your platform

- Error: `AAPT2 aapt2-*.jar not executable`
  - Cause: Corrupt cache
  - Fix: `./gradlew --stop && rm -rf ~/.gradle/caches` then rebuild

- Error: `package android.support.* not found`
  - Cause: Mixed support libraries and AndroidX
  - Fix: This repo enforces AndroidX; do not add legacy support v7 dependencies

---

## Verification

After applying the override:
```bash
./gradlew :app:assembleDebug -i
```
You should see `Aapt2 from: /absolute/path/.../aapt2` log lines and build should proceed.