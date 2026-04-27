# Linkpoint PWA - Capacitor Mobile App

## 🎯 What This Is

Wraps the Linkpoint PWA as a **native iOS and Android app** using Capacitor. Bypasses CORS restrictions using native HTTP client.

## ✅ What Works

- ✅ **Real SL connectivity** - Native HTTP bypasses CORS
- ✅ **iOS App Store** - Can publish to App Store
- ✅ **Google Play** - Can publish to Play Store
- ✅ **Native features** - Camera, notifications, etc.
- ✅ **Offline support** - Full PWA capabilities
- ✅ **Touch optimized** - Mobile-first design

## 🚀 Quick Start

### Prerequisites

**For Android:**
- Android Studio
- Java JDK 11+
- Android SDK

**For iOS (macOS only):**
- Xcode 14+
- CocoaPods
- iOS 13+ device or simulator

### Initial Setup

```bash
cd capacitor-wrapper

# Install dependencies
npm install

# Add platforms
npm run android:add
npm run ios:add
```

### Android Development

```bash
# Sync PWA to Android
npm run android:sync

# Open in Android Studio
npm run android:open

# Or run directly
npm run android:run
```

### iOS Development (macOS only)

```bash
# Sync PWA to iOS
npm run ios:sync

# Open in Xcode
npm run ios:open

# Or run directly
npm run ios:run
```

## 📱 Building for Distribution

### Android APK/AAB

```bash
# Debug APK (for testing)
cd android
./gradlew assembleDebug
# Output: android/app/build/outputs/apk/debug/app-debug.apk

# Release AAB (for Play Store)
./gradlew bundleRelease
# Output: android/app/build/outputs/bundle/release/app-release.aab
```

### iOS IPA

```bash
# Open Xcode
npm run ios:open

# In Xcode:
# 1. Select "Any iOS Device" as target
# 2. Product → Archive
# 3. Distribute App → App Store Connect
```

## 🔧 CORS Bypass

Capacitor bypasses CORS using native HTTP plugin:

```javascript
// Modified sl-xmlrpc.js for Capacitor
import { CapacitorHttp } from '@capacitor/core';

static async sendRequest(url, xmlRequest) {
  if (window.Capacitor) {
    // Use native HTTP (bypasses CORS)
    const response = await CapacitorHttp.post({
      url: url,
      headers: {
        'Content-Type': 'text/xml'
      },
      data: xmlRequest
    });
    return this.parseLoginResponse(response.data);
  } else {
    // Fall back to fetch
    const response = await fetch(url, {
      method: 'POST',
      body: xmlRequest
    });
    return this.parseLoginResponse(await response.text());
  }
}
```

## 📦 App Structure

```
capacitor-wrapper/
├── capacitor.config.ts      # Main config
├── package.json             # Dependencies
├── android/                 # Android project
│   ├── app/
│   │   ├── build.gradle     # Android build config
│   │   └── src/main/
│   │       ├── AndroidManifest.xml
│   │       └── res/
│   │           ├── mipmap-*/  # App icons
│   │           └── xml/
│   │               └── network_security_config.xml
│   └── build.gradle
└── ios/                     # iOS project
    └── App/
        ├── App.xcodeproj
        ├── App.xcworkspace
        └── App/
            ├── Info.plist
            └── Assets.xcassets/  # App icons
```

## 🎨 App Icons & Splash

### Generate Icons

```bash
# Install capacitor-assets
npm install -g @capacitor/assets

# Put 1024x1024 icon at: resources/icon.png
# Put splash at: resources/splash.png

# Generate all sizes
npx capacitor-assets generate
```

### Manual Icon Sizes

**Android** (`android/app/src/main/res/`):
- `mipmap-mdpi/` - 48x48
- `mipmap-hdpi/` - 72x72
- `mipmap-xhdpi/` - 96x96
- `mipmap-xxhdpi/` - 144x144
- `mipmap-xxxhdpi/` - 192x192

**iOS** (`ios/App/App/Assets.xcassets/AppIcon.appiconset/`):
- Multiple sizes from 20x20 to 1024x1024

## 🔒 Permissions

### Android (`android/app/src/main/AndroidManifest.xml`)

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

### iOS (`ios/App/App/Info.plist`)

```xml
<key>NSCameraUsageDescription</key>
<string>Upload photos to Second Life</string>
<key>NSMicrophoneUsageDescription</key>
<string>Voice chat in Second Life</string>
<key>NSPhotoLibraryUsageDescription</key>
<string>Upload images to Second Life</string>
```

## 📊 App Store Submission

### Google Play Store

1. **Create keystore:**
```bash
keytool -genkey -v -keystore linkpoint-release.keystore \
  -alias linkpoint -keyalg RSA -keysize 2048 -validity 10000
```

2. **Configure signing:**
Create `android/key.properties`:
```properties
storePassword=your_store_password
keyPassword=your_key_password
keyAlias=linkpoint
storeFile=../linkpoint-release.keystore
```

3. **Build signed AAB:**
```bash
cd android
./gradlew bundleRelease
```

4. **Upload to Play Console:**
- https://play.google.com/console

### Apple App Store

1. **Create App ID:**
- https://developer.apple.com/account/

2. **Configure in Xcode:**
- Signing & Capabilities
- Set Team, Bundle ID

3. **Archive & Upload:**
- Product → Archive
- Distribute → App Store Connect

4. **Submit via App Store Connect:**
- https://appstoreconnect.apple.com/

## 🐛 Troubleshooting

**Android: Build fails**
```bash
# Clean build
cd android
./gradlew clean
./gradlew assembleDebug
```

**iOS: CocoaPods issues**
```bash
cd ios/App
pod deintegrate
pod install
```

**CORS still blocked:**
- Check `capacitor.config.ts` has `allowNavigation`
- Verify using `CapacitorHttp` not `fetch`
- Check network_security_config.xml on Android

**App won't install:**
- Check signing configuration
- Verify minimum SDK versions
- Check permissions in manifest

## 📱 Testing

### Android
```bash
# Install on device
adb install android/app/build/outputs/apk/debug/app-debug.apk

# View logs
adb logcat | grep Capacitor
```

### iOS
```bash
# View device logs
xcrun simctl spawn booted log stream --predicate 'processImagePath contains "Linkpoint"'
```

## 🔄 Updating PWA

After changing PWA code:

```bash
# Copy changes and sync
npm run sync

# Or per platform
npm run android:sync
npm run ios:sync
```

## 📈 Performance

- App size: ~20MB (includes WebView)
- Startup: ~1-2 seconds
- Memory: ~100-150MB
- Native performance for UI

## 🌟 Features

- ✅ Native navigation
- ✅ Hardware back button (Android)
- ✅ Status bar styling
- ✅ Splash screen
- ✅ App icons
- ✅ Push notifications (via plugin)
- ✅ Camera access
- ✅ File system access
- ✅ Biometric auth (via plugin)

---

**Result:** Native iOS/Android apps with full SL connectivity! 📱
