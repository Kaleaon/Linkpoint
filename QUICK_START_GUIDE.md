# Linkpoint Quick Start Guide

## Build & Run

### Prerequisites
- Java 17 JDK
- Android SDK (API 35)
- Android device with USB debugging OR emulator

### Build
```bash
cd /path/to/Linkpoint
./gradlew assembleDebug
```

### Install
```bash
adb install -r Linkpoint/build/outputs/apk/debug/Linkpoint-debug.apk
```

### Run
```bash
adb shell am start -n com.linkpoint.debug/.ui.login.CleanLoginActivity
```

---

## Verify Installation

### Check App Launches
- [ ] App icon visible in launcher
- [ ] Login screen displays

### Check Login Works
- [ ] Enter SL credentials
- [ ] Select grid (Second Life)
- [ ] Login succeeds
- [ ] Main screen appears

### Check Connection
Use Settings → Generate Debug Report to verify:
- [ ] `UDP Connected: true`
- [ ] `Capabilities Ready: true`
- [ ] `Event Queue Active: true`

---

## Troubleshooting

### Build Fails - SDK Location
```bash
echo "sdk.dir=/path/to/android/sdk" > local.properties
```

### Build Fails - License
```bash
yes | sdkmanager --licenses
```

### App Crashes
```bash
adb logcat | grep -E "Linkpoint|FATAL|Exception"
```

### No 3D Rendering
Check device supports OpenGL ES 3.2:
```bash
adb shell dumpsys gfxinfo com.linkpoint.debug
```

---

## Debug Commands

```bash
# Real-time logs
adb logcat | grep Linkpoint

# Verbose build
./gradlew assembleDebug --stacktrace

# Memory usage
adb shell dumpsys meminfo com.linkpoint.debug

# Force stop
adb shell am force-stop com.linkpoint.debug

# Uninstall
adb uninstall com.linkpoint.debug
```

---

## Key Documentation

| Document | Content |
|----------|---------|
| [docs/FIXES_AND_STATUS.md](docs/FIXES_AND_STATUS.md) | Fix history, known issues |
| [todo.md](todo.md) | Current tasks |
| [README.md](README.md) | Project overview |