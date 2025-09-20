#!/bin/bash
# Universal Android Environment for Linkpoint
# Auto-configured for: arm64

export JAVA_HOME="/usr/lib/jvm/java-17-openjdk-arm64"
export ANDROID_HOME="/opt/android-sdk"
export ANDROID_SDK_ROOT="/opt/android-sdk"
export PATH=$PATH:$JAVA_HOME/bin:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/build-tools/35.0.0

echo "🌍 Universal Android Environment Loaded"
echo "✅ Architecture: arm64"
echo "✅ Java: $JAVA_HOME"
echo "✅ Android SDK: $ANDROID_HOME"
echo "✅ AAPT2: /opt/android-tools/aapt2"
