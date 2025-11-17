# Linkpoint Build Workflow Documentation

## Overview

The `build-linkpoint.yml` workflow builds the Linkpoint Android application (the Material 3 successor with Filament rendering) and uploads the APK to GitHub as an artifact.

## Workflow Details

### Triggers

The workflow runs on:
- **Push** to branches: `main`, `develop`, `cursor/**`, `copilot/**`
- **Pull requests** to: `main`, `develop`
- **Manual trigger** via workflow_dispatch
- **Path filters**: Only runs when files in `Linkpoint/` or the workflow file itself change

### Build Environment

- **Runner**: `ubuntu-latest`
- **Timeout**: 45 minutes
- **Java**: OpenJDK 17 (Temurin distribution)
- **Android SDK**: API 34 (Android 14)
- **Build Tools**: 34.0.0
- **NDK**: 25.2.9519653
- **Kotlin**: 1.9.22
- **Gradle**: 8.5 (via wrapper)

### Build Process

The workflow uses the **root Gradle wrapper** and builds Linkpoint as a composite build:

```bash
./gradlew :Linkpoint:assembleDebug
```

This is different from trying to build from the Linkpoint subdirectory, which would fail because the Linkpoint/gradlew wrapper is missing gradle-wrapper.jar.

### Jobs

#### 1. Build Job

**Main build steps:**
1. Checkout code with full history
2. Setup JDK 17 with Gradle caching
3. Setup Android SDK with required components
4. Cache Gradle packages and Kotlin compiler
5. Validate Gradle wrapper
6. Check Kotlin source structure (27 Kotlin files, 0 Java files)
7. Clean build directory
8. Build Linkpoint debug APK
9. Build Linkpoint release APK (continues on error)
10. Run Kotlin compilation checks (continues on error)
11. Run unit tests (continues on error)
12. Run lint analysis (continues on error)
13. Analyze APK size
14. Upload artifacts

**Artifacts uploaded:**
- **Debug APK**: `linkpoint-debug-{sha}` (14 days retention, 9.7 MB)
  - Path: `Linkpoint/build/outputs/apk/debug/*.apk`
- **Release APK**: `linkpoint-release-{sha}` (30 days retention)
  - Path: `Linkpoint/build/outputs/apk/release/*.apk`
- **Build reports**: `build-reports-{sha}` (7 days retention)
  - Includes lint results, test results, logs
- **Lint results**: `lint-results-{sha}` (7 days retention)

**Build summary includes:**
- Build status
- APK size and details
- Kotlin/Java file counts
- Feature list (Material 3, Filament, WebRTC, etc.)
- Build configuration
- Code quality metrics

#### 2. Code Quality Job

Runs after successful build:
- Downloads build reports
- Analyzes Kotlin code metrics
- Generates code statistics

#### 3. Release Job

Runs only on push to `main` or when tags are created:
- Downloads debug and release APKs
- Creates GitHub release for tagged commits
- Attaches APKs to release
- Generates release notes

## Repository Structure

The repository contains **three Android variants**:

| Variant | Location | Description |
|---------|----------|-------------|
| **Modern Lumiya** | `app/` | Kotlin-first rebuild with minimal login prototype |
| **Legacy Lumiya** | `lumiya/` | Original Java/Kotlin with historical assets |
| **Linkpoint** | `Linkpoint/` | Material 3 with Filament + WebRTC (this workflow) |

Linkpoint is configured as a composite build via `includeBuild('Linkpoint')` in the root `settings.gradle`.

## APK Details

**Built APK:**
- **Name**: `Linkpoint-debug.apk`
- **Size**: ~9.7 MB
- **Package**: `com.linkpoint.debug`
- **Version**: 1.0.0-DEBUG
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Architecture**: ARM64 (arm64-v8a)

**Features:**
- ✅ Material 3 design system
- ✅ Filament rendering engine for advanced 3D graphics
- ✅ WebRTC integration for voice chat
- ✅ 100% Kotlin codebase (27 Kotlin files, 0 Java files)
- ✅ Coroutines for async programming
- ✅ AndroidX modern libraries

## Downloading Built APKs

### From GitHub Actions

1. Navigate to the repository on GitHub
2. Click on "Actions" tab
3. Click on a workflow run
4. Scroll down to "Artifacts" section
5. Download `linkpoint-debug-{sha}.zip`
6. Extract the APK file

### From Releases

For tagged commits, APKs are automatically attached to the GitHub release.

## Local Testing

To test the build locally:

```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew :Linkpoint:assembleDebug

# Find the APK
ls -lh Linkpoint/build/outputs/apk/debug/Linkpoint-debug.apk

# Install on device
adb install Linkpoint/build/outputs/apk/debug/Linkpoint-debug.apk
```

## Troubleshooting

### Build fails with "Could not find or load main class"

This happens if you try to run `./gradlew` from the `Linkpoint/` subdirectory. Always build from the repository root using `:Linkpoint:` prefix:

```bash
# ✅ Correct
./gradlew :Linkpoint:assembleDebug

# ❌ Wrong
cd Linkpoint && ./gradlew assembleDebug
```

### APK not found

The APK path is `Linkpoint/build/outputs/apk/debug/Linkpoint-debug.apk` (with capital L), not `linkpoint/` or `app/`.

### Workflow doesn't trigger

Check that:
1. Changes are in the `Linkpoint/` directory
2. Branch matches the trigger patterns
3. Workflow file is in `.github/workflows/`

## CI/CD Pipeline

```
┌─────────────┐
│   Push to   │
│   Branch    │
└──────┬──────┘
       │
       ▼
┌─────────────────────┐
│  Workflow Triggers  │
│  (build-linkpoint)  │
└──────┬──────────────┘
       │
       ▼
┌─────────────────────┐
│   Build Job         │
│  - Setup SDK        │
│  - Build Debug APK  │
│  - Run Tests        │
│  - Upload Artifact  │
└──────┬──────────────┘
       │
       ▼
┌─────────────────────┐
│  Code Quality Job   │
│  - Analyze Metrics  │
└──────┬──────────────┘
       │
       ▼
┌─────────────────────┐
│   Release Job       │
│  (if main or tag)   │
│  - Create Release   │
│  - Attach APKs      │
└─────────────────────┘
```

## Related Files

- **Workflow**: `.github/workflows/build-linkpoint.yml`
- **Build config**: `Linkpoint/build.gradle.kts`
- **Settings**: `Linkpoint/settings.gradle.kts`
- **Root settings**: `settings.gradle`
- **App versions**: `APP_VERSIONS.md`

## Notes

- The workflow is optimized for the Linkpoint variant specifically
- Modern Lumiya (`app/`) has its own workflow: `build-release.yml`
- Legacy Lumiya (`lumiya/`) can be built with `./gradlew :lumiya:assembleDebug`
- All builds use the root Gradle wrapper at `./gradlew`
