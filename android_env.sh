#!/bin/bash
# Android Build Environment Setup
# Source this file to set up the environment for building Linkpoint

export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-arm64
export ANDROID_HOME=/opt/android-sdk
export ANDROID_SDK_ROOT=/opt/android-sdk
export PATH=$PATH:$JAVA_HOME/bin:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/build-tools/35.0.0

echo "🚀 Android Build Environment Configured!"
echo "✅ JAVA_HOME: $JAVA_HOME"
echo "✅ ANDROID_HOME: $ANDROID_HOME"
echo "✅ AAPT2 Override: /opt/android-tools/aapt2"
echo "✅ Java Version: $(java -version 2>&1 | head -1)"
echo "✅ AAPT2 Version: $(/opt/android-tools/aapt2 version)"