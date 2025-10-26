# Quick Start: Building Lumiya Debug APK

## TL;DR

```bash
./gradlew clean assembleDebug
```

**Output:** `app/build/outputs/apk/debug/app-debug.apk` (28.5 MB)  
**Build Time:** ~2 seconds (cached) or ~72 seconds (clean build)  
**Status:** ✅ Builds successfully with stub application

## What You Get

A **minimal working APK** that:
- ✅ Launches successfully on Android devices (API 24+)
- ✅ Shows informational UI about build status
- ❌ Does NOT include full Second Life viewer features

## Why Minimal?

The codebase has **221,328 Kotlin compilation errors** from an incomplete Java-to-Kotlin migration. See `LUMIYA_DEBUG_APK_BUILD_SUCCESS.md` and `docs/LINKPOINT_APK_BUILD_ANALYSIS.md` for details.

## Current Build Configuration

- **Kotlin:** Disabled (all .kt files excluded)
- **Java:** Only stub classes included
- **Features:** Minimal application framework only

## Install and Run

```bash
# Install APK
adb install app/build/outputs/apk/debug/app-debug.apk

# Launch app
adb shell am start -n com.lumiyaviewer.lumiya/.stub.StubCleanLoginActivity

# View logs
adb logcat | grep "Stub"
```

## Restore Full Functionality

To enable full Second Life viewer features, fix the Kotlin compilation errors following the priority order in:
- `docs/LINKPOINT_APK_BUILD_ANALYSIS.md` - Detailed error analysis
- `LUMIYA_DEBUG_APK_BUILD_SUCCESS.md` - Fix roadmap

**Estimated effort:** 40-80 hours

## Key Files

- `app/src/main/java/com/lumiyaviewer/lumiya/stub/` - Stub application
- `app/build.gradle` - Build configuration (Kotlin disabled)
- `app/src/main/AndroidManifest.xml` - Uses stub classes
- `app/build/outputs/apk/debug/app-debug.apk` - Generated APK

## Troubleshooting

**Build fails?**
- Check Java version: `java -version` (requires Java 8+)
- Clean build: `./gradlew clean`
- Check Android SDK installed at `$ANDROID_HOME`

**APK won't install?**
- Check device API level (requires API 24+)
- Enable "Unknown sources" in device settings
- Check storage space available

## Next Steps

1. ✅ Build working APK (DONE)
2. ⏳ Fix critical base classes (AsyncRequestHandler, ShaderProgram, LLSDNode)
3. ⏳ Fix protocol message system (~500 files)
4. ⏳ Fix LLSD type system (8 files)
5. ⏳ Restore full Activity classes
6. ⏳ Enable complete Second Life viewer functionality

See the detailed fix plan in `docs/LINKPOINT_APK_BUILD_ANALYSIS.md`.
