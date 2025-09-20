#!/bin/bash

# Smart Gradle Wrapper for Linkpoint - Universal Architecture Support
# Inspired by CleverFerret - Works on ANY server architecture
# Usage: ./smart_gradlew.sh [gradle_args...]

set -e

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
CYAN='\033[0;36m'
NC='\033[0m'

# Universal architecture detection (CleverFerret inspired)
detect_universal_architecture() {
    local arch=$(uname -m)
    local detailed_arch=""
    local android_abi=""
    local needs_tools="false"
    
    case $arch in
        x86_64|amd64)
            detailed_arch="x86_64"
            android_abi="x86_64"
            needs_tools="false"  # Standard SDK works
            ;;
        aarch64|arm64)
            detailed_arch="arm64"
            android_abi="arm64-v8a"
            needs_tools="true"   # Needs android-tools
            ;;
        armv7l|armhf)
            detailed_arch="armv7"
            android_abi="armeabi-v7a"
            needs_tools="true"   # Needs android-tools
            ;;
        i386|i686)
            detailed_arch="x86"
            android_abi="x86"
            needs_tools="true"   # Needs android-tools
            ;;
        *)
            detailed_arch="x86_64"
            android_abi="x86_64"
            needs_tools="false"
            ;;
    esac
    
    export DETECTED_ARCH="$detailed_arch"
    export DETECTED_ANDROID_ABI="$android_abi"
    export NEEDS_ANDROID_TOOLS="$needs_tools"
    
    echo "$detailed_arch"
}

# Enhanced environment setup with universal support
auto_setup_environment() {
    local arch=$(detect_universal_architecture)
    
    # Enhanced Java detection for multiple architectures
    local java_paths=(
        "/usr/lib/jvm/java-17-openjdk-$DETECTED_ARCH"
        "/usr/lib/jvm/java-17-openjdk-arm64"
        "/usr/lib/jvm/java-17-openjdk-amd64"
        "/usr/lib/jvm/java-17-openjdk"
        "/usr/lib/jvm/java-1.17.0-openjdk"
        "/usr/lib/jvm/default-java"
    )
    
    for java_path in "${java_paths[@]}"; do
        if [ -d "$java_path" ]; then
            export JAVA_HOME="$java_path"
            break
        fi
    done
    
    # Enhanced Android SDK detection
    local sdk_paths=(
        "/opt/android-sdk"
        "$HOME/Android/Sdk"
        "/usr/local/android-sdk"
        "/android-sdk"
    )
    
    for sdk_path in "${sdk_paths[@]}"; do
        if [ -d "$sdk_path" ]; then
            export ANDROID_HOME="$sdk_path"
            export ANDROID_SDK_ROOT="$sdk_path"
            break
        fi
    done
    
    # Add to PATH with build tools
    if [ -n "$ANDROID_HOME" ]; then
        export PATH=$PATH:$JAVA_HOME/bin:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/build-tools/35.0.0
    fi
    
    # Set architecture-optimized Gradle options
    case $DETECTED_ARCH in
        "arm64"|"armv7")
            export GRADLE_OPTS="-Xmx3g -XX:MaxMetaspaceSize=768m -XX:+UseG1GC"
            ;;
        "x86_64")
            export GRADLE_OPTS="-Xmx4g -XX:MaxMetaspaceSize=1g -XX:+UseG1GC"
            ;;
        *)
            export GRADLE_OPTS="-Xmx2g -XX:MaxMetaspaceSize=512m -XX:+UseG1GC"
            ;;
    esac
    
    echo -e "${BLUE}🌍 Universal environment configured for $arch${NC}"
    if [ -n "$JAVA_HOME" ]; then
        echo -e "${GREEN}✅ Java: $JAVA_HOME${NC}"
    fi
    if [ -n "$ANDROID_HOME" ]; then
        echo -e "${GREEN}✅ Android SDK: $ANDROID_HOME${NC}"
    fi
    echo -e "${CYAN}⚙️ Gradle Opts: $GRADLE_OPTS${NC}"
}

# Function to check if setup is needed
check_setup_needed() {
    # Check if Java is available
    if ! command -v java >/dev/null 2>&1 && [ -z "$JAVA_HOME" ]; then
        return 0  # Setup needed
    fi
    
    # Check if Android SDK is available
    if [ ! -d "/opt/android-sdk" ] && [ -z "$ANDROID_HOME" ]; then
        return 0  # Setup needed
    fi
    
    # Check if AAPT2 issues exist
    if [ -f "./gradlew" ]; then
        local gradle_test=$(timeout 10 ./gradlew --version 2>&1 || echo "FAILED")
        if echo "$gradle_test" | grep -q "FAILED"; then
            return 0  # Setup needed
        fi
    fi
    
    return 1  # No setup needed
}

# Main execution
main() {
    cd "$(dirname "$0")"
    
    echo -e "${BLUE}🎯 Smart Gradle Wrapper for Linkpoint${NC}"
    
    # Check if full setup is needed
    if check_setup_needed; then
        echo -e "${YELLOW}⚠️  Environment setup required${NC}"
        echo "Running universal setup..."
        
        if [ -f "./universal_android_setup.sh" ]; then
            chmod +x ./universal_android_setup.sh
            ./universal_android_setup.sh
        else
            echo -e "${RED}❌ universal_android_setup.sh not found${NC}"
            echo "Please run the universal setup first"
            exit 1
        fi
    else
        echo -e "${BLUE}🔍 Auto-configuring environment...${NC}"
        auto_setup_environment
    fi
    
    # Source universal environment if available
    if [ -f "./universal_env.sh" ]; then
        echo -e "${BLUE}🌍 Loading universal environment...${NC}"
        source ./universal_env.sh >/dev/null 2>&1 || true
    fi
    
    # Verify Gradle is ready
    if [ ! -f "./gradlew" ]; then
        echo -e "${RED}❌ gradlew not found${NC}"
        exit 1
    fi
    
    chmod +x ./gradlew
    
    # Run Gradle with provided arguments
    echo -e "${GREEN}🚀 Running Gradle: ./gradlew $*${NC}"
    echo ""
    
    ./gradlew "$@"
}

# Execute main function with all arguments
main "$@"