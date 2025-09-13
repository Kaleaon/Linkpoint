# Building the APK – Step by Step (Beginner Friendly)

This guide walks you through building the Linkpoint (Lumiya Viewer) Android APK from scratch.
It assumes zero prior Android build experience and explains each step.

---

## 0) What we’re building first

Phase 1 target is a Java-only APK (native/JNI disabled temporarily). This unblocks the build while we fix AAPT2 and Gradle toolchain issues. After we can build and install on a device, we’ll re-enable native libraries (OpenJPEG, zero-decode, etc.).

---

## 1) Prerequisites

- Android Studio latest stable (Jellyfish/Koala or newer)
- JDK 17 (Android Studio comes with a compatible JDK)
- Android SDK Platform 35
- Android Build-Tools 35.0.x
- Platform Tools (adb)

Tip: If you use Android Studio, it will prompt to install missing SDKs on first sync.

---

## 2) Clone and open the project

```bash
# Clone repository
# git clone https://github.com/your-org/Linkpoint.git
cd Linkpoint/app

# Optional: verify Gradle wrapper version
./gradlew --version
```

Expected (or similar):
- Gradle 8.7
- Android Gradle Plugin 8.6.0
- Java 17

---

## 3) Configure SDK (local.properties)

Android Studio will automatically create `local.properties` pointing to your SDK.
If you use CLI only, create it manually:

- macOS:
```
sdk.dir=/Users/<you>/Library/Android/sdk
```
- Linux:
```
sdk.dir=/home/<you>/Android/Sdk
```
- Windows:
```
sdk.dir=C:\\Users\\<you>\\AppData\\Local\\Android\\Sdk
```

Create the file: `app/local.properties` (that is: /app/app/local.properties)

---

## 4) Build a debug APK

```bash
# Clean then build
./gradlew clean :app:assembleDebug
```

Where to find the APK:
```
app/app/build/outputs/apk/debug/app-debug.apk
```

Install on device (USB debugging enabled):
```bash
adb install -r app/app/build/outputs/apk/debug/app-debug.apk
```

---

## 5) If you hit AAPT2 errors on ARM64

Recent AGP includes arm64 aapt2 in Build-Tools 35.0.x. Ensure Build-Tools 35 is installed.
If your environment still shows aapt2 architecture errors, use the override:

1) Locate aapt2 shipped with Build-Tools:
```
$ANDROID_HOME/build-tools/35.0.x/aapt2
```

2) Add this line to `gradle.properties` (absolute path):
```
android.aapt2FromMavenOverride=/absolute/path/to/your/Android/sdk/build-tools/35.0.x/aapt2
```

More details: docs/AAPT2_ARM64_TROUBLESHOOTING.md

---

## 6) Next steps after first APK builds

- Test app launches and LoginActivity shows
- Verify permissions prompts (if any)
- Move to Phase 2: Re-enable native libraries (OpenJPEG, zero-decode) and validate JNI loads

We keep changelogs and rationales directly in the Gradle files (heavily commented) for quick onboarding.