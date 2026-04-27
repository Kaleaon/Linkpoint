# Linkpoint Builds

This folder contains build artifacts and build preparation for different platforms.

## Capacitor (Mobile Apps)

**Location**: `builds/capacitor/`

The Capacitor build prepares the Linkpoint PWA for deployment as native iOS and Android apps.

### What's Included

- ✅ **BUILD_INFO.md** - Comprehensive build documentation
- ✅ **STATUS.txt** - Quick status reference
- ✅ PWA files synced to `PWA-demo/capacitor-wrapper/www/`
- ✅ Android platform ready to build
- ⚠️ iOS platform (requires macOS)

### Quick Start

#### Android
```bash
cd PWA-demo/capacitor-wrapper
npm run android:open
# Build APK in Android Studio
```

#### iOS (macOS only)
```bash
cd PWA-demo/capacitor-wrapper
npm run ios:open
# Build in Xcode
```

### Features

The Capacitor build includes:
- 🚀 Direct HTTPS to Second Life (no CORS proxies needed)
- 📱 Native mobile UI with PWA features
- 🔒 Secure native HTTP client
- 📴 Offline support
- 🔔 Push notifications ready

### Documentation

- [Build Info](./capacitor/BUILD_INFO.md) - Detailed build guide
- [Status](./capacitor/STATUS.txt) - Current build status
- [Capacitor README](../PWA-demo/capacitor-wrapper/README.md) - Wrapper documentation

## Other Platforms

### Vercel (Web)
The PWA is deployed to Vercel for web browser access:
```bash
cd PWA-demo
vercel --prod
```

### Electron (Desktop)
Desktop app wrapper:
```bash
cd PWA-demo/electron-wrapper
npm run build
```

### Tauri (Desktop)
Rust-based desktop wrapper:
```bash
cd PWA-demo/tauri-wrapper
npm run build
```

## Platform Comparison

| Platform | CORS Handling | Build Output | Notes |
|----------|---------------|--------------|-------|
| **Web (Vercel)** | CORS proxies | Website | Browser access |
| **Capacitor** | Native HTTP | APK/IPA | Mobile apps |
| **Electron** | Local proxy | EXE/DMG/AppImage | Desktop |
| **Tauri** | Local proxy | EXE/DMG/AppImage | Desktop (smaller) |

## Next Steps

1. **For Android**: Follow instructions in `builds/capacitor/BUILD_INFO.md`
2. **For iOS**: Transfer to macOS and follow iOS instructions
3. **For Web**: Deploy to Vercel (already configured)

---

**Last Updated**: October 29, 2025
