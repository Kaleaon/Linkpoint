#!/bin/bash

# Comprehensive Android Build Setup Script
# Fixes SDK and AAPT2 issues using JonForShort/android-tools

set -e

echo "🚀 Setting up Android Build Environment for Linkpoint..."

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Step 1: Install Java JDK 17 (required for AGP 8.6.0)
echo -e "${BLUE}📦 Installing Java JDK 17...${NC}"
apt-get update -q
apt-get install -y openjdk-17-jdk wget unzip curl

# Set JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
echo "export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64" >> ~/.bashrc

# Verify Java installation
echo -e "${GREEN}✅ Java Version:${NC}"
$JAVA_HOME/bin/java -version

# Step 2: Install Android SDK
echo -e "${BLUE}📱 Installing Android SDK...${NC}"
cd /tmp

# Download Android SDK Command Line Tools
SDK_VERSION="10406996_latest"
SDK_URL="https://dl.google.com/android/repository/commandlinetools-linux-${SDK_VERSION}.zip"

echo "Downloading Android SDK Command Line Tools..."
wget -q "$SDK_URL" -O android-sdk.zip

# Create SDK directory structure
mkdir -p /opt/android-sdk/cmdline-tools
unzip -q android-sdk.zip -d /opt/android-sdk/cmdline-tools/
mv /opt/android-sdk/cmdline-tools/cmdline-tools /opt/android-sdk/cmdline-tools/latest

# Set Android SDK environment variables
export ANDROID_HOME=/opt/android-sdk
export ANDROID_SDK_ROOT=/opt/android-sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools

echo "export ANDROID_HOME=/opt/android-sdk" >> ~/.bashrc
echo "export ANDROID_SDK_ROOT=/opt/android-sdk" >> ~/.bashrc
echo "export PATH=\$PATH:\$ANDROID_HOME/cmdline-tools/latest/bin:\$ANDROID_HOME/platform-tools" >> ~/.bashrc

# Step 3: Install Android SDK components
echo -e "${BLUE}🛠️  Installing Android SDK components...${NC}"

# Accept licenses automatically
yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses >/dev/null 2>&1

# Install required SDK components for the project
$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager \
    "platform-tools" \
    "platforms;android-34" \
    "platforms;android-35" \
    "build-tools;35.0.0" \
    "build-tools;34.0.0"

echo -e "${GREEN}✅ Android SDK installed successfully${NC}"

# Step 4: Fix AAPT2 issues using JonForShort/android-tools
echo -e "${BLUE}🔧 Fixing AAPT2 issues...${NC}"

# Detect architecture
ARCH=$(uname -m)
echo "Detected architecture: $ARCH"

# Determine the correct AAPT2 binary to download
case $ARCH in
    "aarch64"|"arm64")
        AAPT2_ARCH="arm64-v8a"
        echo -e "${YELLOW}ARM64 detected - downloading ARM64 AAPT2 binary${NC}"
        ;;
    "x86_64"|"amd64")
        AAPT2_ARCH="x86_64"
        echo -e "${YELLOW}x86_64 detected - downloading x86_64 AAPT2 binary${NC}"
        ;;
    "armv7l")
        AAPT2_ARCH="armeabi-v7a"
        echo -e "${YELLOW}ARMv7 detected - downloading ARMv7 AAPT2 binary${NC}"
        ;;
    *)
        echo -e "${RED}❌ Unsupported architecture: $ARCH${NC}"
        echo "Supported architectures: aarch64, x86_64, armv7l"
        exit 1
        ;;
esac

# Create directory for AAPT2 override
mkdir -p /opt/android-tools

# Download AAPT2 binary from JonForShort/android-tools
AAPT2_URL="https://github.com/JonForShort/android-tools/raw/master/build/android-11.0.0_r33/aapt2/${AAPT2_ARCH}/aapt2"
echo "Downloading AAPT2 binary from: $AAPT2_URL"

wget -q "$AAPT2_URL" -O /opt/android-tools/aapt2

# Make it executable
chmod +x /opt/android-tools/aapt2

# Verify AAPT2 works
echo -e "${BLUE}🧪 Testing AAPT2 binary...${NC}"
if /opt/android-tools/aapt2 version >/dev/null 2>&1; then
    echo -e "${GREEN}✅ AAPT2 binary works correctly${NC}"
    AAPT2_VERSION=$(/opt/android-tools/aapt2 version 2>&1 | head -1)
    echo "AAPT2 Version: $AAPT2_VERSION"
else
    echo -e "${RED}❌ AAPT2 binary test failed${NC}"
    echo "Fallback: Using standard Android SDK AAPT2"
    AAPT2_PATH="$ANDROID_HOME/build-tools/35.0.0/aapt2"
fi

# Step 5: Configure Gradle to use the correct AAPT2
echo -e "${BLUE}⚙️  Configuring Gradle...${NC}"

cd /app

# Update gradle.properties with AAPT2 override
if [ -f "/opt/android-tools/aapt2" ]; then
    echo "" >> gradle.properties
    echo "# AAPT2 override for ${ARCH} architecture (from JonForShort/android-tools)" >> gradle.properties
    echo "android.aapt2FromMavenOverride=/opt/android-tools/aapt2" >> gradle.properties
    echo -e "${GREEN}✅ Configured AAPT2 override in gradle.properties${NC}"
else
    echo -e "${YELLOW}⚠️  Using standard Android SDK AAPT2${NC}"
fi

# Set executable permissions on gradlew
chmod +x ./gradlew

# Step 6: Clean and test build
echo -e "${BLUE}🧹 Cleaning previous builds...${NC}"
./gradlew clean --no-daemon

echo -e "${BLUE}🔨 Testing build configuration...${NC}"
./gradlew tasks --no-daemon | head -10

echo -e "${GREEN}🎉 Android Build Environment Setup Complete!${NC}"
echo ""
echo -e "${BLUE}📋 Summary:${NC}"
echo "✅ Java JDK 17 installed"
echo "✅ Android SDK installed with build-tools 35.0.0"
echo "✅ AAPT2 configured for $ARCH architecture"
echo "✅ Gradle configured and ready"
echo ""
echo -e "${YELLOW}Next steps:${NC}"
echo "1. Run: ./gradlew assembleDebug"
echo "2. APK will be generated in: app/build/outputs/apk/debug/"
echo ""
echo -e "${BLUE}Environment Variables:${NC}"
echo "JAVA_HOME: $JAVA_HOME"
echo "ANDROID_HOME: $ANDROID_HOME"
echo "AAPT2 Binary: $([ -f /opt/android-tools/aapt2 ] && echo '/opt/android-tools/aapt2' || echo 'Standard SDK')"