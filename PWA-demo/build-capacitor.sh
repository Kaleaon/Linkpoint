#!/bin/bash

# Linkpoint PWA - Capacitor Build Script
# Syncs PWA to Capacitor and prepares builds

set -e

echo "========================================="
echo "Linkpoint PWA - Capacitor Build"
echo "========================================="

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Step 1: Sync PWA files to Capacitor
echo -e "${BLUE}Step 1: Syncing PWA files to Capacitor...${NC}"
cd /home/runner/work/Linkpoint/Linkpoint/PWA-demo

# Copy essential PWA files to Capacitor www directory
cp -v index.html capacitor-wrapper/www/
cp -rv js capacitor-wrapper/www/
cp -rv css capacitor-wrapper/www/
cp -rv assets capacitor-wrapper/www/
cp -v manifest.json capacitor-wrapper/www/
cp -v service-worker.js capacitor-wrapper/www/

echo -e "${GREEN}✓ PWA files synced${NC}"

# Step 2: Sync Capacitor platforms
echo -e "${BLUE}Step 2: Syncing Capacitor platforms...${NC}"
cd capacitor-wrapper

# Check if node_modules exists
if [ ! -d "node_modules" ]; then
    echo "Installing Capacitor dependencies..."
    npm install
fi

# Sync to Android (if platform exists)
if [ -d "android" ]; then
    echo "Syncing Android..."
    npx cap sync android
    echo -e "${GREEN}✓ Android synced${NC}"
else
    echo "Android platform not added. Run: npm run android:add"
fi

# Sync to iOS (if platform exists)
if [ -d "ios" ]; then
    echo "Syncing iOS..."
    npx cap sync ios
    echo -e "${GREEN}✓ iOS synced${NC}"
else
    echo "iOS platform not added (macOS only). Run: npm run ios:add"
fi

# Step 3: Copy build info to builds folder
echo -e "${BLUE}Step 3: Creating build info...${NC}"
cd /home/runner/work/Linkpoint/Linkpoint

# Create builds folder if it doesn't exist
mkdir -p builds/capacitor

# Create build info file
cat > builds/capacitor/BUILD_INFO.md << 'EOF'
# Linkpoint PWA - Capacitor Build

## Build Date
EOF

date >> builds/capacitor/BUILD_INFO.md

cat >> builds/capacitor/BUILD_INFO.md << 'EOF'

## Platforms Synced

### Android
- Location: `PWA-demo/capacitor-wrapper/android/`
- To build APK: `cd PWA-demo/capacitor-wrapper/android && ./gradlew assembleDebug`
- Output: `android/app/build/outputs/apk/debug/app-debug.apk`

### iOS  
- Location: `PWA-demo/capacitor-wrapper/ios/`
- To build: Open in Xcode and build
- Requires: macOS with Xcode

## Files Synced to Capacitor

- ✅ index.html
- ✅ js/ (all JavaScript files)
- ✅ css/ (all stylesheets)
- ✅ assets/ (all assets)
- ✅ manifest.json
- ✅ service-worker.js

## Features

### CORS Bypass
- Native HTTP plugin bypasses browser CORS restrictions
- Direct connection to Second Life servers (HTTPS)
- No CORS proxies needed

### Platforms
- Android app (APK/AAB for Play Store)
- iOS app (IPA for App Store)

## Next Steps

### For Android:
1. Open in Android Studio: `npm run android:open`
2. Build APK: Use Android Studio or Gradle
3. Test on device or emulator

### For iOS (macOS only):
1. Open in Xcode: `npm run ios:open`
2. Build IPA: Use Xcode
3. Test on simulator or device

## Documentation
- Main README: `PWA-demo/capacitor-wrapper/README.md`
- Capacitor config: `PWA-demo/capacitor-wrapper/capacitor.config.js`
EOF

echo -e "${GREEN}✓ Build info created in builds/capacitor/${NC}"

# Step 4: Create a status summary
cat > builds/capacitor/STATUS.txt << EOF
Capacitor Build Status
=====================

Build Date: $(date)

PWA Files Synced: YES
Android Platform: $([ -d "PWA-demo/capacitor-wrapper/android" ] && echo "YES" || echo "NO - Run: npm run android:add")
iOS Platform: $([ -d "PWA-demo/capacitor-wrapper/ios" ] && echo "YES (macOS only)" || echo "NO - Run: npm run ios:add")

Next Steps:
- Open in Android Studio: cd PWA-demo/capacitor-wrapper && npm run android:open
- Open in Xcode: cd PWA-demo/capacitor-wrapper && npm run ios:open

Build Output Locations:
- Android APK: PWA-demo/capacitor-wrapper/android/app/build/outputs/apk/
- iOS IPA: Built through Xcode

For detailed build instructions, see: builds/capacitor/BUILD_INFO.md
EOF

echo "========================================="
echo -e "${GREEN}✓ Capacitor build preparation complete!${NC}"
echo "========================================="
echo ""
echo "Build artifacts prepared in: builds/capacitor/"
echo ""
echo "To build Android APK:"
echo "  cd PWA-demo/capacitor-wrapper"
echo "  npm run android:open"
echo ""
echo "To build iOS (macOS only):"
echo "  cd PWA-demo/capacitor-wrapper"
echo "  npm run ios:open"
echo ""
echo "Build info saved to:"
echo "  - builds/capacitor/BUILD_INFO.md"
echo "  - builds/capacitor/STATUS.txt"
echo ""
