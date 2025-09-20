#!/bin/bash

# ==============================================================================
# Linkpoint AI-Assisted Build Script
# ==============================================================================
#
# PURPOSE:
# This script provides a safe and reliable way to build the Linkpoint project.
# It checks for dependencies and required configuration, provides clear
# instructions, and then runs the build.
#
# USAGE:
# ./ai-tools/setup-and-build.sh
#
# ==============================================================================

set -e

# --- Color Codes for Logging ---
readonly RED='\033[0;31m'
readonly GREEN='\033[0;32m'
readonly YELLOW='\033[1;33m'
readonly BLUE='\033[0;34m'
readonly NC='\033[0m'

log_info() { echo -e "${BLUE}[INFO]${NC} $1"; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} ✅ $1"; }
log_warning() { echo -e "${YELLOW}[WARNING]${NC} ⚠️  $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} ❌ $1"; }

# --- Step 1: Verify Prerequisites ---

log_info "Step 1: Verifying prerequisites..."
prereq_ok=true

# Check for Java 17
if ! java -version 2>&1 | grep -q 'version "17'; then
    log_error "Java 17 is not installed. Please install Java 17 and set it as your default."
    prereq_ok=false
else
    log_success "Java 17 is installed."
fi

# Check for ANDROID_HOME
if [ -z "$ANDROID_HOME" ]; then
    log_error "ANDROID_HOME environment variable is not set. Please set it to the location of your Android SDK."
    prereq_ok=false
else
    log_success "ANDROID_HOME is set to: $ANDROID_HOME"
fi

if [ "$prereq_ok" = false ]; then
    log_error "Prerequisite check failed. Please fix the issues above and run the script again."
    exit 1
fi

# --- Step 2: Verify Project Configuration ---

log_info "Step 2: Verifying project configuration..."
config_ok=true

# Check for local.properties
if [ ! -f "local.properties" ]; then
    log_warning "local.properties file not found. Creating a default one."
    echo "sdk.dir=$ANDROID_HOME" > local.properties
    log_success "Created local.properties. Please review this file."
elif ! grep -q "sdk.dir" "local.properties"; then
    log_error "local.properties exists but does not contain 'sdk.dir'. Please add 'sdk.dir=/path/to/your/sdk' to the file."
    config_ok=false
else
    log_success "local.properties is configured correctly."
fi

if [ "$config_ok" = false ]; then
    log_error "Configuration check failed. Please fix the issues above and run the script again."
    exit 1
fi

# --- Step 3: Run the Build ---

log_info "Step 3: Starting the Gradle build..."

# Make gradlew executable
chmod +x ./gradlew

# Run the build
if ./gradlew --no-daemon --parallel --build-cache --stacktrace assembleDebug; then
    log_success "Build completed successfully!"
else
    log_error "Build failed. Please check the output above for errors."
    exit 1
fi

# --- Step 4: Locate the APK ---

log_info "Step 4: Locating the generated APK..."

apk_path="app/build/outputs/apk/debug/app-debug.apk"

if [ -f "$apk_path" ]; then
    log_success "APK generated at: $apk_path"
    ls -lh "$apk_path"
else
    log_error "Could not find the generated APK. The build may have failed silently."
    exit 1
fi

echo -e "\n🎉 Linkpoint build process finished successfully!"
