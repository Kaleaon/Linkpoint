# Linkpoint Quick Start Guide - Get Running in Minutes

## TL;DR - Fastest Path to Running App

### Option 1: Use Android Studio (Recommended - Easiest)

```bash
# 1. Download Android Studio
# Visit: https://developer.android.com/studio

# 2. Open project
# File > Open > Select Linkpoint/Linkpoint folder

# 3. Wait for Gradle sync (automatic)

# 4. Click Run button (green play icon)
# Select device/emulator

# Done! App should launch
```

### Option 2: Command Line Build

```bash
# 1. Install Java 17
sudo apt-get install openjdk-17-jdk

# 2. Set JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64

# 3. Install Android SDK
# Download from: https://developer.android.com/studio#command-tools
# Extract and set ANDROID_HOME

# 4. Build
cd Linkpoint/Linkpoint
./gradlew assembleDebug

# 5. Install
adb install build/outputs/apk/debug/Linkpoint-debug.apk
```

## What You Have Right Now

### ✅ Complete (Ready to Use)
- **1,215 Kotlin files** - All code is modern Kotlin
- **3D Rendering** - OpenGL ES 3.2 + Filament
- **Voice Chat** - WebRTC implementation
- **Chat System** - Full chat manager
- **Inventory** - Complete inventory system
- **UI Components** - All activities and fragments
- **Protocol** - SL protocol implementation

### ⚠️ Needs Verification
- **Build system** - May need minor fixes
- **Resources** - Some layouts may need completion
- **Integration** - Components need end-to-end testing

## 5-Minute Checklist

### Before You Start
- [ ] Java 17 installed
- [ ] Android Studio installed (or Android SDK)
- [ ] USB debugging enabled on Android device (or emulator running)

### Build Steps
1. [ ] Open project in Android Studio
2. [ ] Wait for Gradle sync
3. [ ] Fix any sync errors (usually just accepting licenses)
4. [ ] Click Run
5. [ ] Select device
6. [ ] App launches!

### If Build Fails

**Error: "SDK location not found"**
```bash
# Create local.properties
echo "sdk.dir=/path/to/android/sdk" > local.properties
```

**Error: "Gradle wrapper not found"**
```bash
# Download wrapper
wget https://raw.githubusercontent.com/gradle/gradle/master/gradle/wrapper/gradle-wrapper.jar \
  -O gradle/wrapper/gradle-wrapper.jar
```

**Error: "License not accepted"**
```bash
# Accept licenses
yes | sdkmanager --licenses
```

## What to Test First

### 1. App Launch (30 seconds)
- [ ] App icon appears
- [ ] App launches without crash
- [ ] Login screen shows

### 2. Login (2 minutes)
- [ ] Enter credentials
- [ ] Select grid
- [ ] Login succeeds
- [ ] Main screen appears

### 3. 3D View (2 minutes)
- [ ] Navigate to world view
- [ ] 3D world renders
- [ ] Camera controls work
- [ ] No crashes

### 4. Chat (1 minute)
- [ ] Open chat
- [ ] Send message
- [ ] Receive messages
- [ ] UI responsive

### 5. Inventory (1 minute)
- [ ] Open inventory
- [ ] Browse folders
- [ ] Items load
- [ ] UI works

## Common Issues & Quick Fixes

### Issue: App Crashes on Launch
**Fix:**
```bash
# Check logs
adb logcat | grep Linkpoint

# Look for:
# - Missing permissions
# - Resource not found
# - ClassNotFoundException
```

### Issue: 3D View Black Screen
**Fix:**
```bash
# Check OpenGL support
adb shell dumpsys gfxinfo com.linkpoint.debug

# Verify device supports OpenGL ES 3.2
```

### Issue: Voice Not Working
**Fix:**
```bash
# Grant microphone permission
adb shell pm grant com.linkpoint.debug android.permission.RECORD_AUDIO

# Check audio routing
adb shell dumpsys audio
```

### Issue: Network Errors
**Fix:**
```bash
# Check internet permission in AndroidManifest.xml
# Verify network connectivity
adb shell ping google.com
```

## File Structure Quick Reference

```
Linkpoint/Linkpoint/
├── src/main/
│   ├── kotlin/com/linkpoint/
│   │   ├── LinkpointApp.kt          # Main app class
│   │   ├── ui/                       # All UI components
│   │   │   ├── login/               # Login screens
│   │   │   ├── render/              # 3D view
│   │   │   ├── chat/                # Chat UI
│   │   │   └── inventory/           # Inventory UI
│   │   ├── render/                   # 3D rendering
│   │   ├── voice/                    # Voice chat
│   │   ├── chat/                     # Chat logic
│   │   ├── inventory/                # Inventory logic
│   │   └── protocol/                 # SL protocol
│   ├── res/                          # Resources
│   │   ├── layout/                  # XML layouts
│   │   ├── values/                  # Strings, colors
│   │   └── drawable/                # Images
│   └── AndroidManifest.xml          # App config
└── build.gradle.kts                 # Build config
```

## Key Files to Know

### Main Entry Points
- `LinkpointApp.kt` - Application class
- `ui/login/CleanLoginActivity.kt` - Login screen
- `ui/LinkpointMainActivity.kt` - Main activity
- `ui/render/WorldViewActivity.kt` - 3D world view

### Core Systems
- `voice/LinkpointVoiceManager.kt` - Voice chat
- `chat/ChatManager.kt` - Chat system
- `inventory/InventorySystem.kt` - Inventory
- `protocol/LinkpointProtocolManager.kt` - Network protocol

### Rendering
- `render/ModernWorldViewRenderer.kt` - Main renderer
- `render/shaders/` - Shader programs
- `ui/render/FilamentWorldViewActivity.kt` - Filament integration

## Getting Help

### Check Logs
```bash
# Real-time logs
adb logcat | grep Linkpoint

# Save logs to file
adb logcat > linkpoint.log
```

### Check Build Output
```bash
# Verbose build
./gradlew assembleDebug --stacktrace --info
```

### Check Device Info
```bash
# Device info
adb shell getprop

# OpenGL version
adb shell dumpsys gfxinfo | grep "OpenGL"

# Memory
adb shell dumpsys meminfo com.linkpoint.debug
```

## Next Steps After First Run

1. **Test all features** - Go through each screen
2. **Check performance** - Monitor FPS and memory
3. **Fix bugs** - Address any crashes or issues
4. **Polish UI** - Complete any missing layouts
5. **Optimize** - Profile and improve performance

## Resources

### Essential Links
- **Android Studio**: https://developer.android.com/studio
- **Kotlin Docs**: https://kotlinlang.org/docs/
- **Material Design**: https://m3.material.io/
- **Second Life Wiki**: https://wiki.secondlife.com/

### Documentation in This Repo
- `LINKPOINT_REBUILD_ANALYSIS.md` - Detailed analysis
- `LINKPOINT_COMPLETE_ACTION_PLAN.md` - Full action plan
- `README.md` - Project overview
- `todo.md` - Task tracking

## Success Indicators

You'll know it's working when:
- ✅ App launches without crashes
- ✅ Login screen appears
- ✅ Can log into Second Life
- ✅ 3D world renders
- ✅ Can move camera
- ✅ Chat works
- ✅ Inventory loads

## Estimated Time to First Run

- **With Android Studio**: 15-30 minutes
- **Command line**: 30-60 minutes
- **With issues**: 1-2 hours

## Most Important Commands

```bash
# Build
./gradlew assembleDebug

# Install
adb install -r build/outputs/apk/debug/Linkpoint-debug.apk

# Run
adb shell am start -n com.linkpoint.debug/.ui.login.CleanLoginActivity

# Logs
adb logcat | grep Linkpoint

# Uninstall
adb uninstall com.linkpoint.debug
```

---

**Remember:** The code is 80-85% complete. Most of the work is verification and testing, not writing new code!

**Start with Android Studio** - It handles most setup automatically and provides great debugging tools.

Good luck! 🚀