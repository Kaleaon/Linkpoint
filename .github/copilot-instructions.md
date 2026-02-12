# Linkpoint - Second Life Viewer for Android

Linkpoint is a modern Android application for connecting to Second Life virtual worlds. It is a 100% Kotlin codebase using Material 3, Filament rendering, and WebRTC voice.

**ALWAYS reference these instructions first and fallback to search or bash commands only when you encounter unexpected information that does not match the info here.**

## Working Effectively

### Build System - CRITICAL
**The Linkpoint build WORKS. Always verify your changes compile before submitting.**

The active application module is `Linkpoint/` (not `app/`). Build from the repository root using the root Gradle wrapper:

```bash
# ✅ CORRECT - Build from repository root with :Linkpoint: prefix
./gradlew :Linkpoint:assembleDebug --stacktrace

# ✅ ALSO CORRECT - Build from within Linkpoint/ directory
cd Linkpoint && ./gradlew assembleDebug --stacktrace

# ❌ WRONG - Do NOT use app/ module commands (legacy, broken)
# ./gradlew :app:assembleDebug  # OLD MODULE - DO NOT USE
```

**You MUST verify compilation after making code changes:**
```bash
./gradlew :Linkpoint:compileDebugKotlin --stacktrace
```

### Environment Setup
- **Java**: JDK 17 (Temurin distribution)
- **Android SDK**: compileSdk 35, targetSdk 34, minSdk 24
- **NDK**: 25.2.9519653
- **Gradle**: 8.7 (via wrapper)
- **Kotlin**: 1.9.22
- **Build Tools**: 35.0.0

### Build System Commands
```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew :Linkpoint:assembleDebug --stacktrace

# Build release APK
./gradlew :Linkpoint:assembleRelease --stacktrace

# Run Kotlin compilation check
./gradlew :Linkpoint:compileDebugKotlin --stacktrace

# Run unit tests
./gradlew :Linkpoint:testDebugUnitTest --stacktrace

# Run lint analysis
./gradlew :Linkpoint:lintDebug --stacktrace

# View dependencies
./gradlew :Linkpoint:dependencies --configuration debugRuntimeClasspath
```

### Development Structure
```
Linkpoint/                           # Main application module
├── build.gradle.kts                 # Kotlin DSL build script
├── settings.gradle.kts              # Project settings
├── gradlew                          # Gradle wrapper (also usable)
└── src/main/java/com/linkpoint/
    ├── LinkpointApplication.kt      # Application entry point
    ├── assets/                      # Asset management (textures, meshes)
    ├── network/                     # Network and HTTP layer
    │   └── core/                    # Grid connection, throttling
    ├── protocol/                    # Second Life protocol
    │   ├── capabilities/            # Capability management (HTTP endpoints)
    │   ├── llsd/                    # LLSD data format
    │   ├── messages/                # UDP message handling
    │   └── translation/             # Protocol translation layer
    ├── rendering/                   # 3D rendering (Filament)
    ├── ui/                          # User interface (Material 3)
    └── voice/                       # WebRTC voice system
```

### Repository Structure
The repository root contains multiple modules, but only `Linkpoint/` is active:

| Module | Location | Status |
|--------|----------|--------|
| **Linkpoint** | `Linkpoint/` | ✅ Active - 100% Kotlin |
| PWA Demo | `PWA-demo/` | Web demo |
| Gauss | `Gauss/` | 3D graphics research |
| LLSD-Kotlin | `LLSD-KOTLIN/` | LLSD library |

The root `settings.gradle` includes Linkpoint via `includeBuild('Linkpoint')`.

## Validation & Testing

### ALWAYS Verify Compilation
After making code changes, **always** verify they compile:

```bash
# Quick compilation check (~1-2 minutes)
./gradlew :Linkpoint:compileDebugKotlin --stacktrace

# Full debug build (~5-10 minutes)
./gradlew :Linkpoint:assembleDebug --stacktrace
```

### Running Tests
```bash
# Unit tests
./gradlew :Linkpoint:testDebugUnitTest --stacktrace --continue

# Lint analysis
./gradlew :Linkpoint:lintDebug --stacktrace
```

### Code Exploration
```bash
# Find Kotlin source files
find Linkpoint/src -name "*.kt" | head -20

# Search for specific classes
grep -r "class.*Manager" Linkpoint/src/main/java/

# Find protocol handlers
find Linkpoint/src/main/java -name "*Capability*"

# Check dependency configuration
grep -A 5 "implementation" Linkpoint/build.gradle.kts
```

## Common Tasks & Patterns

### Adding New Features
1. **Find the relevant package** in `Linkpoint/src/main/java/com/linkpoint/`
2. **Check existing patterns** by examining similar classes
3. **Write Kotlin** - the codebase is 100% Kotlin, do not add Java files
4. **Verify compilation** with `./gradlew :Linkpoint:compileDebugKotlin`
5. **Follow coroutines patterns** - async code uses `kotlinx.coroutines`

### Protocol and Network Changes
- **Capabilities**: `protocol/capabilities/CapabilityManager.kt`
- **UDP Messages**: `protocol/messages/`
- **Grid Connection**: `network/core/GridConnection.kt`
- **LLSD Format**: `protocol/llsd/`

### Key Classes
- `CapabilityManager` - HTTP capability endpoint management
- `GridConnection` - Main grid connection handler
- `TextureManager` / `MeshManager` - Asset loading
- `UDPConnectionFixed` - UDP protocol handler
- `LinkpointTranslationLayer` - Protocol translation

## Key Files Reference

### Build Configuration
- `settings.gradle` - Root settings (includes Linkpoint)
- `Linkpoint/build.gradle.kts` - App build configuration
- `Linkpoint/settings.gradle.kts` - App settings
- `Linkpoint/gradle.properties` - Gradle properties

### CI/CD Workflows
- `.github/workflows/build-linkpoint.yml` - Primary build (builds from root)
- `.github/workflows/build-release.yml` - Debug + Release builds
- `.github/workflows/quick-release.yml` - Quick release build
- `.github/workflows/deploy.yml` - Deployment pipeline

### Documentation
- `.github/BUILD_LINKPOINT_WORKFLOW.md` - Workflow documentation
- `.github/BUILD_PIPELINE.md` - Build pipeline details
- `docs/` - Additional project documentation

## Critical Reminders

- **ALWAYS verify compilation** after code changes
- **Build from root** using `./gradlew :Linkpoint:assembleDebug` or from `Linkpoint/` using its own `./gradlew`
- **Do NOT reference `app/` module** - it is a legacy module that does not build
- **100% Kotlin** - never add Java files
- **One companion object per class** - Kotlin only allows one companion object
- **Check CI** - the build-linkpoint.yml workflow runs on push and PR
