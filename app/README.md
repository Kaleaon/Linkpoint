# Lumiya - Legacy Second Life Viewer

## Overview
Lumiya is the legacy Second Life viewer for Android, now in maintenance mode. This is the original codebase that Linkpoint is built upon, preserved for compatibility and reference.

## Status
🟡 **Maintenance Mode** - Bug fixes only, no new features

## Project Information

### Package Details
- **Package ID:** `com.lumiyaviewer.lumiya`
- **Version:** 3.4.3 (versionCode 67)
- **Min SDK:** 24 (Android 7.0)
- **Target SDK:** 34 (Android 14)

### Technology Stack
- **Language:** Kotlin/Java hybrid (transitioning to Kotlin)
- **Build System:** Gradle 8.2.2 (Groovy DSL)
- **Architecture:** Legacy Android patterns

## Building

### Prerequisites
- JDK 17 or higher
- Android SDK with API 34
- Build Tools 34.0.0

### Build Commands

From repository root:

```bash
# Debug build
./gradlew :app:assembleDebug

# Release build
./gradlew :app:assembleRelease

# Clean build
./gradlew :app:clean
```

### Output Locations
- **Debug APK:** `app/build/outputs/apk/debug/app-debug.apk`
- **Release APK:** `app/build/outputs/apk/release/app-release.apk`

## CI/CD

### Workflow
This project is built automatically via GitHub Actions:
- **Workflow:** `.github/workflows/build-release.yml`
- **Triggers:** Push to main, Pull Requests, Manual dispatch
- **Artifacts:** Debug and Release APKs

### Build Status
Check the [Actions tab](../../actions/workflows/build-release.yml) for build status.

## Features

### Supported Features
- ✅ Basic Second Life viewer functionality
- ✅ Chat and IM
- ✅ Inventory management
- ✅ Teleportation
- ✅ Avatar movement
- ✅ Basic rendering
- ✅ Voice chat (via plugin)
- ✅ Cloud storage (via plugin)

### Legacy Features
- ⚠️ OpenGL ES 2.0 rendering (older)
- ⚠️ Mixed Java/Kotlin codebase
- ⚠️ Legacy Android APIs

## Migration to Linkpoint

This codebase is being superseded by **Linkpoint**, the modern Kotlin rewrite:

### Why Migrate?
- 🚀 100% Kotlin codebase
- 🎨 Modern rendering (Filament engine)
- 🔧 AndroidX libraries
- 🎯 Better performance
- 🛡️ Improved security
- 📱 Modern Android features

### Migration Path
Users should transition to Linkpoint for:
- New features (Animesh, PBR, EEP)
- Better performance
- Active development
- Long-term support

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/           # Legacy Java code
│   │   ├── kotlin/         # Kotlin code
│   │   ├── res/            # Resources
│   │   └── AndroidManifest.xml
│   ├── rebuild/            # Rebuild utilities
│   └── test/               # Tests
├── build.gradle            # Build configuration
└── proguard-rules.pro      # ProGuard rules
```

## Configuration

### Build Configuration
The build is configured in `app/build.gradle`:
- Namespace: `com.lumiyaviewer.lumiya`
- Compile SDK: 34
- Build Tools: 34.0.0
- Min SDK: 24
- Target SDK: 34

### Signing
For release builds, create `keystore.properties` in the root:
```properties
storeFile=/path/to/keystore.jks
storePassword=your_store_password
keyAlias=your_key_alias
keyPassword=your_key_password
```

## Dependencies

### Root Dependencies
This module depends on the root `build.gradle` for:
- Android Gradle Plugin version
- Kotlin version
- Repository configuration

### Key Libraries
- AndroidX AppCompat
- AndroidX Core KTX
- Material Design Components
- Kotlin Standard Library
- Google Play Services

## Testing

### Current Status
⚠️ Limited automated testing

### Running Tests
```bash
# Unit tests
./gradlew :app:testDebugUnitTest

# Instrumented tests
./gradlew :app:connectedDebugAndroidTest
```

## Known Issues

### Current Limitations
1. Mixed Java/Kotlin codebase
2. Legacy rendering engine
3. Limited modern Android features
4. No Animesh support
5. No PBR materials
6. No Enhanced Environment (EEP)

### Workarounds
For modern features, use **Linkpoint** instead.

## Maintenance

### Support Policy
- 🐛 Critical bug fixes: Yes
- 🔒 Security updates: Yes
- ✨ New features: No (use Linkpoint)
- 📱 OS updates: Compatibility only

### Update Frequency
- As needed for critical issues
- Security patches
- OS compatibility updates

## Related Projects

### Linkpoint (Modern Replacement)
- **Location:** `Linkpoint/`
- **Status:** ✅ Active Development
- **Recommendation:** Use for new installations

### PWA Demo
- **Location:** `PWA-demo/`
- **Status:** 🔬 Experimental
- **Purpose:** Cross-platform web viewer

### Reference APKs
- **Location:** `Lumiya/`
- **Purpose:** Historical reference
- **Files:** Original Lumiya APKs and plugins

## Contributing

### Current Policy
- Bug fixes: Accepted
- New features: Redirect to Linkpoint
- Code cleanup: Accepted
- Documentation: Accepted

### Pull Requests
Submit PRs targeting the `main` branch with:
- Clear description
- Bug fix justification
- Testing evidence

## Resources

### Documentation
- [Root README](../../README.md) - Repository overview
- [Linkpoint README](../Linkpoint/README.md) - Modern app
- [APK Organization](../APK_ORGANIZATION_PLAN.md) - Project structure

### Support
- GitHub Issues: Bug reports only
- Discussions: General questions
- Wiki: Documentation (if available)

## License
See [LICENSE](../../LICENSE) file in repository root.

## Acknowledgments
- Original Lumiya developers
- Second Life community
- Open source contributors

---

**Status:** 🟡 Maintenance Mode  
**Successor:** [Linkpoint](../Linkpoint/) (Recommended)  
**Last Updated:** November 15, 2024