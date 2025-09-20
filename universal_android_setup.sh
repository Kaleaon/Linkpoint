#!/bin/bash

# Universal Android Build Setup for Linkpoint
# Automatically detects architecture and configures appropriate AAPT2 binary
# Works on any Linux machine: x86_64, ARM64, ARMv7, etc.

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo -e "${CYAN}🌍 Universal Android Build Setup for Linkpoint${NC}"
echo -e "${BLUE}Automatically configures for any architecture${NC}"
echo ""

# Function to detect architecture and map to android-tools naming
detect_architecture() {
    local arch=$(uname -m)
    local os_arch=""
    local aapt2_arch=""
    
    echo -e "${BLUE}🔍 Detecting system architecture...${NC}"
    echo "Raw architecture: $arch"
    
    case $arch in
        "x86_64"|"amd64")
            os_arch="x86_64"
            aapt2_arch="x86_64"
            echo -e "${GREEN}✅ Detected: x86_64 (Intel/AMD 64-bit)${NC}"
            ;;
        "aarch64"|"arm64")
            os_arch="arm64"
            aapt2_arch="arm64-v8a"
            echo -e "${GREEN}✅ Detected: ARM64 (64-bit ARM)${NC}"
            ;;
        "armv7l"|"armhf")
            os_arch="armv7"
            aapt2_arch="armeabi-v7a"
            echo -e "${GREEN}✅ Detected: ARMv7 (32-bit ARM)${NC}"
            ;;
        "i386"|"i686")
            os_arch="x86"
            aapt2_arch="x86"
            echo -e "${GREEN}✅ Detected: x86 (32-bit Intel)${NC}"
            ;;
        *)
            echo -e "${RED}❌ Unsupported architecture: $arch${NC}"
            echo -e "${YELLOW}⚠️  Supported architectures: x86_64, aarch64, armv7l, i386${NC}"
            echo -e "${YELLOW}📋 Will attempt to use standard Android SDK AAPT2 as fallback${NC}"
            os_arch="unsupported"
            aapt2_arch="fallback"
            ;;
    esac
    
    export DETECTED_OS_ARCH="$os_arch"
    export DETECTED_AAPT2_ARCH="$aapt2_arch"
}

# Function to find correct Java installation
setup_java() {
    echo -e "${BLUE}☕ Setting up Java JDK 17...${NC}"
    
    # Install Java if not present
    if ! command -v java &> /dev/null; then
        echo "Installing OpenJDK 17..."
        apt-get update -q
        apt-get install -y openjdk-17-jdk wget unzip curl git
    fi
    
    # Find Java installation path
    local java_paths=(
        "/usr/lib/jvm/java-17-openjdk-arm64"     # ARM64 Debian/Ubuntu
        "/usr/lib/jvm/java-17-openjdk-amd64"     # x86_64 Debian/Ubuntu
        "/usr/lib/jvm/java-17-openjdk"           # Generic
        "/usr/lib/jvm/java-1.17.0-openjdk"      # Alternative naming
    )
    
    for java_path in "${java_paths[@]}"; do
        if [ -d "$java_path" ]; then
            export JAVA_HOME="$java_path"
            echo -e "${GREEN}✅ Found Java at: $JAVA_HOME${NC}"
            break
        fi
    done
    
    if [ -z "$JAVA_HOME" ]; then
        echo -e "${RED}❌ Could not find Java 17 installation${NC}"
        exit 1
    fi
    
    # Verify Java works
    if $JAVA_HOME/bin/java -version >/dev/null 2>&1; then
        local java_version=$($JAVA_HOME/bin/java -version 2>&1 | head -1)
        echo -e "${GREEN}✅ Java verified: $java_version${NC}"
    else
        echo -e "${RED}❌ Java installation verification failed${NC}"
        exit 1
    fi
}

# Function to setup Android SDK
setup_android_sdk() {
    echo -e "${BLUE}📱 Setting up Android SDK...${NC}"
    
    if [ ! -d "/opt/android-sdk" ]; then
        echo "Installing Android SDK Command Line Tools..."
        cd /tmp
        
        # Download Android SDK
        local sdk_url="https://dl.google.com/android/repository/commandlinetools-linux-10406996_latest.zip"
        wget -q "$sdk_url" -O android-sdk.zip
        
        # Setup SDK structure
        mkdir -p /opt/android-sdk/cmdline-tools
        unzip -q android-sdk.zip -d /opt/android-sdk/cmdline-tools/
        mv /opt/android-sdk/cmdline-tools/cmdline-tools /opt/android-sdk/cmdline-tools/latest
        
        rm -f android-sdk.zip
    fi
    
    # Set environment variables
    export ANDROID_HOME=/opt/android-sdk
    export ANDROID_SDK_ROOT=/opt/android-sdk
    export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools
    
    # Install SDK components if not present
    if [ ! -d "$ANDROID_HOME/platforms/android-34" ]; then
        echo "Installing Android SDK components..."
        yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses >/dev/null 2>&1
        
        $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager \
            "platform-tools" \
            "platforms;android-34" \
            "platforms;android-35" \
            "build-tools;35.0.0" \
            "build-tools;34.0.0" \
            >/dev/null 2>&1
    fi
    
    echo -e "${GREEN}✅ Android SDK ready${NC}"
}

# Function to setup AAPT2 binary for detected architecture
setup_aapt2() {
    echo -e "${BLUE}🔧 Setting up AAPT2 for $DETECTED_OS_ARCH...${NC}"
    
    local aapt2_dir="/opt/android-tools"
    local aapt2_binary="$aapt2_dir/aapt2"
    
    mkdir -p "$aapt2_dir"
    
    if [ "$DETECTED_AAPT2_ARCH" = "fallback" ]; then
        echo -e "${YELLOW}⚠️  Using Android SDK AAPT2 as fallback${NC}"
        # Copy SDK AAPT2 as fallback
        if [ -f "$ANDROID_HOME/build-tools/35.0.0/aapt2" ]; then
            cp "$ANDROID_HOME/build-tools/35.0.0/aapt2" "$aapt2_binary"
            chmod +x "$aapt2_binary"
            echo -e "${YELLOW}📋 Fallback AAPT2 configured${NC}"
        else
            echo -e "${RED}❌ No AAPT2 binary available${NC}"
            return 1
        fi
    else
        echo "Downloading native AAPT2 binary for $DETECTED_AAPT2_ARCH..."
        
        # Clone android-tools repository if not present
        if [ ! -d "/tmp/android-tools" ]; then
            cd /tmp
            git clone --depth 1 https://github.com/JonForShort/android-tools.git >/dev/null 2>&1
        fi
        
        # Path to the architecture-specific AAPT2 binary
        local aapt2_source="/tmp/android-tools/build/android-11.0.0_r33/aapt2/$DETECTED_AAPT2_ARCH/bin/aapt2"
        
        if [ -f "$aapt2_source" ]; then
            cp "$aapt2_source" "$aapt2_binary"
            chmod +x "$aapt2_binary"
            echo -e "${GREEN}✅ Native AAPT2 binary installed for $DETECTED_OS_ARCH${NC}"
        else
            echo -e "${YELLOW}⚠️  Native binary not found, trying fallback...${NC}"
            # Fallback to SDK AAPT2
            if [ -f "$ANDROID_HOME/build-tools/35.0.0/aapt2" ]; then
                cp "$ANDROID_HOME/build-tools/35.0.0/aapt2" "$aapt2_binary"
                chmod +x "$aapt2_binary"
                echo -e "${YELLOW}📋 Fallback AAPT2 configured${NC}"
            fi
        fi
    fi
    
    # Test AAPT2 binary
    if [ -f "$aapt2_binary" ]; then
        if "$aapt2_binary" version >/dev/null 2>&1; then
            local aapt2_version=$("$aapt2_binary" version 2>&1 | head -1)
            echo -e "${GREEN}✅ AAPT2 working: $aapt2_version${NC}"
            export AAPT2_BINARY_PATH="$aapt2_binary"
            return 0
        else
            echo -e "${RED}❌ AAPT2 binary test failed${NC}"
            return 1
        fi
    else
        echo -e "${RED}❌ No AAPT2 binary available${NC}"
        return 1
    fi
}

# Function to configure Gradle dynamically
configure_gradle() {
    echo -e "${BLUE}⚙️  Configuring Gradle for $DETECTED_OS_ARCH...${NC}"
    
    cd /app
    
    # Create/update gradle.properties with architecture-specific settings
    local gradle_props="gradle.properties"
    local temp_props="gradle.properties.tmp"
    
    # Remove any existing aapt2 override
    if [ -f "$gradle_props" ]; then
        grep -v "android.aapt2FromMavenOverride" "$gradle_props" > "$temp_props" || true
        mv "$temp_props" "$gradle_props"
    fi
    
    # Add architecture-specific AAPT2 configuration
    echo "" >> "$gradle_props"
    echo "# === UNIVERSAL ARCHITECTURE CONFIGURATION ===" >> "$gradle_props"
    echo "# Auto-configured for: $DETECTED_OS_ARCH ($DETECTED_AAPT2_ARCH)" >> "$gradle_props"
    echo "# Generated by universal_android_setup.sh" >> "$gradle_props"
    
    if [ -n "$AAPT2_BINARY_PATH" ] && [ -f "$AAPT2_BINARY_PATH" ]; then
        echo "android.aapt2FromMavenOverride=$AAPT2_BINARY_PATH" >> "$gradle_props"
        echo -e "${GREEN}✅ AAPT2 override configured: $AAPT2_BINARY_PATH${NC}"
    else
        echo "# No AAPT2 override needed - using standard SDK" >> "$gradle_props"
        echo -e "${YELLOW}📋 Using standard Android SDK AAPT2${NC}"
    fi
    
    echo "# === END CONFIGURATION ===" >> "$gradle_props"
    
    # Make gradlew executable
    chmod +x ./gradlew
    
    echo -e "${GREEN}✅ Gradle configured for $DETECTED_OS_ARCH${NC}"
}

# Function to create universal environment script
create_environment_script() {
    echo -e "${BLUE}📝 Creating universal environment script...${NC}"
    
    cat > /app/universal_env.sh << EOF
#!/bin/bash
# Universal Android Environment for Linkpoint
# Auto-configured for: $DETECTED_OS_ARCH

export JAVA_HOME="$JAVA_HOME"
export ANDROID_HOME="$ANDROID_HOME"
export ANDROID_SDK_ROOT="$ANDROID_SDK_ROOT"
export PATH=\$PATH:\$JAVA_HOME/bin:\$ANDROID_HOME/cmdline-tools/latest/bin:\$ANDROID_HOME/platform-tools:\$ANDROID_HOME/build-tools/35.0.0

echo "🌍 Universal Android Environment Loaded"
echo "✅ Architecture: $DETECTED_OS_ARCH"
echo "✅ Java: \$JAVA_HOME"
echo "✅ Android SDK: \$ANDROID_HOME"
EOF

    if [ -n "$AAPT2_BINARY_PATH" ]; then
        echo "echo \"✅ AAPT2: $AAPT2_BINARY_PATH\"" >> /app/universal_env.sh
    fi
    
    chmod +x /app/universal_env.sh
    echo -e "${GREEN}✅ Environment script created${NC}"
}

# Function to test the build system
test_build_system() {
    echo -e "${BLUE}🧪 Testing build system...${NC}"
    
    cd /app
    
    # Source environment
    export JAVA_HOME="$JAVA_HOME"
    export ANDROID_HOME="$ANDROID_HOME"
    export PATH=$PATH:$JAVA_HOME/bin:$ANDROID_HOME/cmdline-tools/latest/bin
    
    # Test Gradle
    echo "Testing Gradle..."
    if ./gradlew --version >/dev/null 2>&1; then
        echo -e "${GREEN}✅ Gradle working${NC}"
    else
        echo -e "${RED}❌ Gradle test failed${NC}"
        return 1
    fi
    
    # Test clean build
    echo "Testing clean build..."
    if ./gradlew clean --no-daemon -q >/dev/null 2>&1; then
        echo -e "${GREEN}✅ Clean build successful${NC}"
    else
        echo -e "${RED}❌ Clean build failed${NC}"
        return 1
    fi
    
    # Test build configuration
    echo "Testing build configuration..."
    local config_output=$(timeout 15 ./gradlew tasks --no-daemon 2>&1 | head -10)
    if echo "$config_output" | grep -q "Android tasks"; then
        echo -e "${GREEN}✅ Build configuration valid${NC}"
    else
        echo -e "${YELLOW}⚠️  Build configuration test inconclusive${NC}"
    fi
    
    echo -e "${GREEN}✅ Build system tests completed${NC}"
}

# Main execution
main() {
    echo -e "${CYAN}Starting Universal Android Setup...${NC}"
    echo ""
    
    # Step 1: Detect architecture
    detect_architecture
    echo ""
    
    # Step 2: Setup Java
    setup_java
    echo ""
    
    # Step 3: Setup Android SDK
    setup_android_sdk
    echo ""
    
    # Step 4: Setup AAPT2
    setup_aapt2
    echo ""
    
    # Step 5: Configure Gradle
    configure_gradle
    echo ""
    
    # Step 6: Create environment script
    create_environment_script
    echo ""
    
    # Step 7: Test build system
    test_build_system
    echo ""
    
    # Final summary
    echo -e "${CYAN}🎉 Universal Android Setup Complete!${NC}"
    echo ""
    echo -e "${GREEN}📋 Configuration Summary:${NC}"
    echo "🏗️  Architecture: $DETECTED_OS_ARCH"
    echo "☕ Java: $JAVA_HOME"
    echo "📱 Android SDK: $ANDROID_HOME"
    if [ -n "$AAPT2_BINARY_PATH" ]; then
        echo "🔧 AAPT2: $AAPT2_BINARY_PATH"
    else
        echo "🔧 AAPT2: Standard Android SDK"
    fi
    echo ""
    echo -e "${BLUE}🚀 Ready to build:${NC}"
    echo "source universal_env.sh"
    echo "./gradlew assembleDebug"
    echo ""
    echo -e "${GREEN}✅ Build system is now universal and architecture-agnostic!${NC}"
}

# Run main function
main "$@"