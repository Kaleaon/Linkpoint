# Linkpoint Repository - APK Organization Plan

## Executive Summary
This document outlines the organization of multiple APK projects within the Linkpoint repository, ensuring proper separation, individual packaging configurations, and CI/CD workflows for each.

## Repository Structure

```
Linkpoint/
├── Linkpoint/              # Modern Kotlin Native App (Primary)
│   ├── build.gradle.kts
│   ├── settings.gradle.kts
│   └── .github/workflows/build-linkpoint.yml ✅
│
├── app/                    # Legacy Lumiya App (Maintenance Mode)
│   ├── build.gradle
│   └── .github/workflows/build-release.yml ✅
│
├── PWA-demo/              # Progressive Web App
│   ├── capacitor-wrapper/android/
│   │   └── build.gradle
│   └── .github/workflows/verify-pwa-build.yml ✅
│
└── Lumiya/                # Reference APKs (Documentation Only)
    ├── Lumiya_3.4.2.apk
    ├── Lumiya Cloud Plugin_1.0.apk
    └── Lumiya Voice Plugin_1.4.apk
```

## APK Projects

### 1. Linkpoint (Modern Kotlin App) - PRIMARY

**Location:** `Linkpoint/`

**Type:** Native Android Application (Kotlin)

**Purpose:** Modern Second Life viewer with latest features
- 100% Kotlin codebase
- Animesh support
- Bakes on Mesh (BoM)
- Enhanced Environment (EEP)
- PBR Materials
- WebRTC Voice
- Filament rendering engine

**Build Configuration:**
- **Package:** `com.linkpoint`
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 34 (Android 14)
- **Build Tools:** 34.0.0
- **Gradle:** 8.1.1 (Kotlin DSL)
- **Kotlin:** 1.9.22

**Workflow:** `.github/workflows/build-linkpoint.yml` ✅
- Builds on: push to main/develop, PRs, manual trigger
- Outputs: Debug APK, Release APK
- Artifacts: Retained 14-30 days
- Features: Lint, tests, code coverage

**Build Commands:**
```bash
cd Linkpoint
./gradlew assembleDebug    # Debug build
./gradlew assembleRelease  # Release build
```

**Output Locations:**
- Debug: `Linkpoint/build/outputs/apk/debug/`
- Release: `Linkpoint/build/outputs/apk/release/`

**Status:** ✅ Active Development - Primary focus

---

### 2. Lumiya (Legacy App) - MAINTENANCE

**Location:** `app/`

**Type:** Native Android Application (Kotlin/Java hybrid)

**Purpose:** Legacy Lumiya viewer in maintenance mode
- Transitioning to Linkpoint
- Maintained for compatibility
- No new features

**Build Configuration:**
- **Package:** `com.lumiyaviewer.lumiya`
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 34 (Android 14)
- **Build Tools:** 34.0.0
- **Gradle:** 8.2.2 (Groovy DSL)
- **Version:** 3.4.3 (versionCode 67)

**Workflow:** `.github/workflows/build-release.yml` ✅
- Builds on: push to main, PRs, manual trigger
- Outputs: Debug APK, Release APK
- Artifacts: Retained per GitHub defaults

**Build Commands:**
```bash
./gradlew :app:assembleDebug    # Debug build
./gradlew :app:assembleRelease  # Release build
```

**Output Locations:**
- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release.apk`

**Status:** 🟡 Maintenance Mode - Bug fixes only

---

### 3. PWA Capacitor Wrapper - EXPERIMENTAL

**Location:** `PWA-demo/capacitor-wrapper/android/`

**Type:** Capacitor-wrapped Progressive Web App

**Purpose:** Cross-platform PWA wrapper for web-based viewer
- Web technologies (HTML/CSS/JS)
- Capacitor native bridge
- Multi-platform support (Android, iOS, Web)

**Build Configuration:**
- **Package:** `com.linkpoint.pwa`
- **Min SDK:** 22 (from root config)
- **Target SDK:** 34 (from root config)
- **Build Tools:** 34.0.0
- **Gradle:** Groovy DSL

**Workflow:** `.github/workflows/verify-pwa-build.yml` ✅
- Verifies on: push to PWA-demo/, PRs, manual trigger
- Validates: JSON files, JavaScript syntax, static assets
- Tests: Local HTTP server, file accessibility

**Build Commands:**
```bash
cd PWA-demo/capacitor-wrapper
./gradlew assembleDebug    # Debug build
./gradlew assembleRelease  # Release build
```

**Output Locations:**
- Debug: `PWA-demo/capacitor-wrapper/android/app/build/outputs/apk/debug/`
- Release: `PWA-demo/capacitor-wrapper/android/app/build/outputs/apk/release/`

**Status:** 🔬 Experimental - Research & Development

---

### 4. Lumiya Reference APKs - DOCUMENTATION

**Location:** `Lumiya/`

**Type:** Pre-built APK files (reference only)

**Purpose:** Historical reference and feature comparison
- Original Lumiya app (3.4.2)
- Cloud Plugin (1.0)
- Voice Plugin (1.4)

**Files:**
- `Lumiya_3.4.2.apk` - Main application
- `Lumiya Cloud Plugin_1.0.apk` - Cloud storage integration
- `Lumiya Voice Plugin_1.4.apk` - Voice chat plugin

**Workflow:** None (documentation only)

**Status:** 📚 Reference Only - Not built, preserved for comparison

---

## Build Matrix

| Project | Package ID | Min SDK | Target SDK | Language | Status | Workflow |
|---------|-----------|---------|------------|----------|--------|----------|
| **Linkpoint** | com.linkpoint | 24 | 34 | Kotlin | ✅ Active | build-linkpoint.yml |
| **Lumiya** | com.lumiyaviewer.lumiya | 24 | 34 | Kotlin/Java | 🟡 Maintenance | build-release.yml |
| **PWA** | com.linkpoint.pwa | 22 | 34 | Web/Capacitor | 🔬 Experimental | verify-pwa-build.yml |
| **Reference** | N/A | N/A | N/A | N/A | 📚 Archive | None |

## Workflow Organization

### Current Workflows (All Properly Separated) ✅

1. **build-linkpoint.yml** - Linkpoint modern app
   - Path trigger: `Linkpoint/**`
   - Comprehensive build with tests, lint, coverage
   - Uploads debug and release APKs
   - Generates detailed build summary

2. **build-release.yml** - Lumiya legacy app
   - Path trigger: Root project
   - Simple debug and release builds
   - Uploads both APK variants

3. **verify-pwa-build.yml** - PWA validation
   - Path trigger: `PWA-demo/**`
   - Validates web assets and configuration
   - Tests local server functionality

4. **deploy.yml** - Deployment automation
5. **lumiya-static-analysis.yml** - Code quality
6. **quick-release.yml** - Fast release builds

### Workflow Separation Strategy ✅

Each workflow is properly isolated:
- ✅ Separate path triggers
- ✅ Independent build processes
- ✅ Unique artifact names
- ✅ No cross-dependencies
- ✅ Clear naming conventions

## APK Naming Conventions

### Linkpoint (Modern App)
```
linkpoint-debug-{git-sha}.apk
linkpoint-release-{git-sha}.apk
```

### Lumiya (Legacy App)
```
Linkpoint-debug-APK/app-debug.apk
Linkpoint-release-APK/app-release.apk
```

### PWA Wrapper
```
linkpoint-pwa-debug.apk
linkpoint-pwa-release.apk
```

## Version Management

### Linkpoint
- **Version Name:** 1.0.0
- **Version Code:** 1
- **Location:** `Linkpoint/build.gradle.kts`
- **Strategy:** Semantic versioning (MAJOR.MINOR.PATCH)

### Lumiya
- **Version Name:** 3.4.3
- **Version Code:** 67
- **Location:** `app/build.gradle`
- **Strategy:** Legacy versioning (maintained for compatibility)

### PWA
- **Version Name:** 1.0
- **Version Code:** 1
- **Location:** `PWA-demo/capacitor-wrapper/android/app/build.gradle`
- **Strategy:** Simple versioning

## Signing Configuration

### Linkpoint
```kotlin
// keystore.properties (not in repo)
storeFile=release.keystore
storePassword=***
keyAlias=***
keyPassword=***
```

### Lumiya
```groovy
// keystore.properties (not in repo)
// Same format as Linkpoint
```

### PWA
- Uses default debug signing
- Production signing TBD

## Build Output Organization

### Directory Structure
```
Linkpoint/
├── Linkpoint/build/outputs/apk/
│   ├── debug/
│   │   └── linkpoint-debug.apk
│   └── release/
│       └── linkpoint-release.apk
│
├── app/build/outputs/apk/
│   ├── debug/
│   │   └── app-debug.apk
│   └── release/
│       └── app-release.apk
│
└── PWA-demo/capacitor-wrapper/android/app/build/outputs/apk/
    ├── debug/
    │   └── app-debug.apk
    └── release/
        └── app-release.apk
```

### Artifact Retention
- **Linkpoint Debug:** 14 days
- **Linkpoint Release:** 30 days
- **Lumiya:** Default (90 days)
- **PWA:** Not uploaded (validation only)

## Dependencies Between Projects

### Linkpoint → Independent ✅
- Standalone project
- No dependencies on other modules
- Self-contained build system

### Lumiya → Root Build ⚠️
- Depends on root `build.gradle`
- Uses root `settings.gradle`
- Shares Gradle wrapper with root

### PWA → Independent ✅
- Standalone Capacitor project
- Independent build system
- No dependencies on Android modules

### Recommendation
Consider making Lumiya fully independent by:
1. Moving to its own subdirectory with complete build files
2. Adding its own Gradle wrapper
3. Removing dependency on root build.gradle

## CI/CD Pipeline Flow

```mermaid
graph TD
    A[Git Push] --> B{Which Path?}
    B -->|Linkpoint/**| C[build-linkpoint.yml]
    B -->|app/**| D[build-release.yml]
    B -->|PWA-demo/**| E[verify-pwa-build.yml]
    
    C --> F[Build Linkpoint APKs]
    D --> G[Build Lumiya APKs]
    E --> H[Validate PWA Assets]
    
    F --> I[Upload Artifacts]
    G --> I
    H --> J[Generate Report]
    
    I --> K[Ready for Release]
```

## Testing Strategy

### Linkpoint
- ✅ Unit tests (JUnit)
- ✅ Lint analysis
- ✅ Code coverage (Jacoco)
- ✅ Kotlin compilation checks
- 🔄 UI tests (Espresso) - TODO

### Lumiya
- ⚠️ Limited testing
- Basic build verification
- No automated tests currently

### PWA
- ✅ JSON validation
- ✅ JavaScript syntax checks
- ✅ HTTP server tests
- ✅ Asset accessibility tests

## Documentation Per Project

### Linkpoint
- ✅ `Linkpoint/README.md` - Project overview
- ✅ `CRASH_FIX_SUMMARY.md` - Recent fixes
- ✅ Multiple technical docs in root

### Lumiya
- ⚠️ Needs dedicated README
- Documentation scattered in root

### PWA
- ✅ `PWA-demo/README.md` - Comprehensive guide
- ✅ `PWA-demo/QUICKSTART.md` - Quick start
- ✅ Multiple feature docs

## Recommendations

### Immediate Actions ✅
1. ✅ All projects have proper workflows
2. ✅ Path triggers are correctly configured
3. ✅ Artifact naming is unique
4. ✅ No build conflicts

### Future Improvements
1. 📝 Create `app/README.md` for Lumiya project
2. 📝 Add signing documentation per project
3. 📝 Create architecture diagram
4. 🔄 Consider moving Lumiya to independent structure
5. 🔄 Add UI tests for Linkpoint
6. 🔄 Implement automated release notes

### Documentation Needed
1. Individual README for each APK project
2. Build troubleshooting guides
3. Deployment procedures
4. Version upgrade guides

## Conclusion

The Linkpoint repository is **well-organized** with proper separation of APK projects:

✅ **Strengths:**
- Clear project boundaries
- Separate CI/CD workflows
- Unique package identifiers
- Independent build processes
- Proper path-based triggers

⚠️ **Areas for Improvement:**
- Lumiya could be more independent
- Need individual project READMEs
- Could benefit from more comprehensive testing

🎯 **Overall Status:** **GOOD** - Projects are properly separated with individual workflows. Minor documentation improvements recommended.

---

**Last Updated:** November 15, 2024  
**Maintained By:** SuperNinja AI Agent  
**Status:** ✅ Complete and Validated