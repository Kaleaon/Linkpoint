#!/bin/bash

# Linkpoint CLI Editor and APK Management Tool
# Comprehensive command-line interface for building, testing, and debugging the Linkpoint APK

set -e

# Script configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$SCRIPT_DIR"
APK_PATH="$PROJECT_ROOT/app/build/outputs/apk/debug/app-debug.apk"

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Configuration
ANDROID_HOME="${ANDROID_HOME:-/usr/local/lib/android/sdk}"
ADB="$ANDROID_HOME/platform-tools/adb"

# Welcome banner
show_banner() {
    echo -e "${CYAN}╔══════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${CYAN}║                    Linkpoint CLI Editor                      ║${NC}"
    echo -e "${CYAN}║              APK Management and Debugging Tool              ║${NC}"
    echo -e "${CYAN}╚══════════════════════════════════════════════════════════════╝${NC}"
    echo -e "${GREEN}Second Life Android Client - Build, Test & Debug System${NC}"
    echo ""
}

# Function to show usage information
show_usage() {
    echo -e "${GREEN}Usage: $0 [COMMAND] [OPTIONS]${NC}"
    echo ""
    echo -e "${YELLOW}BUILD COMMANDS:${NC}"
    echo "  clean               Clean project build files"
    echo "  build               Build the debug APK"
    echo "  quick-build         Clean and build in one step"
    echo "  release             Build release APK"
    echo ""
    echo -e "${YELLOW}TEST COMMANDS:${NC}"
    echo "  test                Run comprehensive APK tests"
    echo "  validate            Quick APK structure validation"
    echo "  analyze             Deep APK analysis"
    echo "  status              Show current build status"
    echo ""
    echo -e "${YELLOW}DEBUG COMMANDS:${NC}"
    echo "  debug               Start debugging session for APK issues"
    echo "  logcat              Monitor Android logs for the app"
    echo "  inspect             Inspect APK contents and structure"
    echo "  dependencies        Show APK dependencies and libraries"
    echo ""
    echo -e "${YELLOW}DEVICE COMMANDS:${NC}"
    echo "  devices             List connected Android devices"
    echo "  install             Install APK to connected device"
    echo "  uninstall           Remove app from connected device"
    echo "  launch              Launch app on device"
    echo ""
    echo -e "${YELLOW}UTILITY COMMANDS:${NC}"
    echo "  interactive         Start interactive CLI session"
    echo "  doctor              Diagnose build environment"
    echo "  help                Show this help message"
    echo ""
    echo -e "${YELLOW}Examples:${NC}"
    echo "  $0 quick-build      # Clean and build APK"
    echo "  $0 test             # Run all tests"
    echo "  $0 debug            # Start debugging session"
    echo "  $0 interactive      # Interactive mode"
}

# Function to check build environment
check_environment() {
    echo -e "${BLUE}🔍 Checking Build Environment...${NC}"
    
    local issues=0
    
    # Check Java
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | head -n 1)
        echo -e "✅ Java: $JAVA_VERSION"
    else
        echo -e "${RED}❌ Java not found${NC}"
        issues=$((issues + 1))
    fi
    
    # Check Gradle
    if [ -f "$PROJECT_ROOT/gradlew" ]; then
        echo -e "✅ Gradle: Wrapper found"
    else
        echo -e "${RED}❌ Gradle wrapper not found${NC}"
        issues=$((issues + 1))
    fi
    
    # Check Android SDK
    if [ -d "$ANDROID_HOME" ]; then
        echo -e "✅ Android SDK: $ANDROID_HOME"
    else
        echo -e "${RED}❌ Android SDK not found${NC}"
        issues=$((issues + 1))
    fi
    
    # Check ADB
    if [ -f "$ADB" ]; then
        echo -e "✅ ADB: Available"
    else
        echo -e "${YELLOW}⚠️ ADB not found at expected location${NC}"
    fi
    
    return $issues
}

# Function to show current status
show_status() {
    echo -e "${BLUE}📊 Current Project Status${NC}"
    echo "=========================="
    
    # Check if APK exists
    if [ -f "$APK_PATH" ]; then
        APK_SIZE=$(ls -lh "$APK_PATH" | awk '{print $5}')
        APK_DATE=$(ls -l "$APK_PATH" | awk '{print $6, $7, $8}')
        echo -e "${GREEN}✅ APK Status: Built (${APK_SIZE}, ${APK_DATE})${NC}"
    else
        echo -e "${YELLOW}⚠️ APK Status: Not built${NC}"
    fi
    
    # Check build directory
    BUILD_DIR="$PROJECT_ROOT/app/build"
    if [ -d "$BUILD_DIR" ]; then
        BUILD_SIZE=$(du -sh "$BUILD_DIR" 2>/dev/null | awk '{print $1}')
        echo -e "📁 Build directory: $BUILD_SIZE"
    else
        echo -e "📁 Build directory: Clean"
    fi
    
    # Check connected devices
    if [ -f "$ADB" ]; then
        DEVICE_COUNT=$("$ADB" devices | grep -c "device$" || echo "0")
        echo -e "📱 Connected devices: $DEVICE_COUNT"
    fi
    
    echo ""
}

# Function to clean project
clean_project() {
    echo -e "${YELLOW}🧹 Cleaning project...${NC}"
    
    cd "$PROJECT_ROOT"
    if ./gradlew clean; then
        echo -e "${GREEN}✅ Project cleaned successfully${NC}"
    else
        echo -e "${RED}❌ Clean failed${NC}"
        return 1
    fi
}

# Function to build APK
build_apk() {
    echo -e "${YELLOW}🔨 Building APK...${NC}"
    echo "This may take 2-5 minutes including native compilation..."
    echo ""
    
    cd "$PROJECT_ROOT"
    
    local start_time=$(date +%s)
    
    if ./gradlew assembleDebug; then
        local end_time=$(date +%s)
        local duration=$((end_time - start_time))
        echo ""
        echo -e "${GREEN}✅ APK built successfully in ${duration}s${NC}"
        
        if [ -f "$APK_PATH" ]; then
            APK_SIZE=$(ls -lh "$APK_PATH" | awk '{print $5}')
            echo -e "${GREEN}📦 APK location: $APK_PATH (${APK_SIZE})${NC}"
        fi
        return 0
    else
        echo -e "${RED}❌ Build failed${NC}"
        return 1
    fi
}

# Function to run tests
run_tests() {
    echo -e "${YELLOW}🧪 Running comprehensive APK tests...${NC}"
    echo ""
    
    if [ ! -f "$APK_PATH" ]; then
        echo -e "${RED}❌ APK not found. Build first.${NC}"
        return 1
    fi
    
    # Run validation script
    if [ -f "$PROJECT_ROOT/validate_apk.sh" ]; then
        echo -e "${BLUE}Running APK validation...${NC}"
        if "$PROJECT_ROOT/validate_apk.sh"; then
            echo -e "${GREEN}✅ Validation passed${NC}"
        else
            echo -e "${RED}❌ Validation failed${NC}"
            return 1
        fi
        echo ""
    fi
    
    # Run functionality test
    if [ -f "$PROJECT_ROOT/test_apk_functionality.sh" ]; then
        echo -e "${BLUE}Running functionality test...${NC}"
        if "$PROJECT_ROOT/test_apk_functionality.sh"; then
            echo -e "${GREEN}✅ Functionality test passed${NC}"
        else
            echo -e "${RED}❌ Functionality test failed${NC}"
            return 1
        fi
    fi
}

# Function to analyze APK
analyze_apk() {
    echo -e "${YELLOW}🔍 Deep APK Analysis${NC}"
    echo "==================="
    
    if [ ! -f "$APK_PATH" ]; then
        echo -e "${RED}❌ APK not found. Build first.${NC}"
        return 1
    fi
    
    # APK basic info
    APK_SIZE=$(ls -lh "$APK_PATH" | awk '{print $5}')
    echo -e "${GREEN}APK Size: $APK_SIZE${NC}"
    
    # Extract and analyze components
    echo ""
    echo -e "${BLUE}📋 APK Contents:${NC}"
    
    # List major components with sizes
    unzip -l "$APK_PATH" | grep -E "(classes\.dex|AndroidManifest\.xml|lib/|assets/)" | head -20
    
    echo ""
    echo -e "${BLUE}🔧 Native Libraries by Architecture:${NC}"
    
    # Group libraries by architecture
    for arch in arm64-v8a armeabi-v7a x86 x86_64; do
        if unzip -l "$APK_PATH" | grep -q "lib/$arch/"; then
            LIB_COUNT=$(unzip -l "$APK_PATH" | grep "lib/$arch/" | wc -l)
            TOTAL_SIZE=$(unzip -l "$APK_PATH" | grep "lib/$arch/" | awk '{sum += $1} END {printf "%.1f", sum/1024/1024}')
            echo -e "  ${arch}: ${LIB_COUNT} libraries (${TOTAL_SIZE}MB)"
        fi
    done
    
    echo ""
    echo -e "${BLUE}📊 Assets Analysis:${NC}"
    ASSET_COUNT=$(unzip -l "$APK_PATH" | grep "assets/" | wc -l)
    if [ "$ASSET_COUNT" -gt 0 ]; then
        ASSET_SIZE=$(unzip -l "$APK_PATH" | grep "assets/" | awk '{sum += $1} END {printf "%.1f", sum/1024/1024}')
        echo -e "  Total assets: ${ASSET_COUNT} files (${ASSET_SIZE}MB)"
        
        # Show asset categories
        echo "  Asset categories:"
        unzip -l "$APK_PATH" | grep "assets/" | awk -F'/' '{print $2}' | sort | uniq -c | head -10
    else
        echo -e "  No assets found"
    fi
}

# Function to inspect APK contents
inspect_apk() {
    echo -e "${YELLOW}🔍 APK Inspector${NC}"
    echo "==============="
    
    if [ ! -f "$APK_PATH" ]; then
        echo -e "${RED}❌ APK not found. Build first.${NC}"
        return 1
    fi
    
    # Create temporary directory for extraction
    TEMP_DIR="/tmp/linkpoint_inspect_$$"
    mkdir -p "$TEMP_DIR"
    
    echo -e "${BLUE}Extracting APK to temporary directory...${NC}"
    unzip -q "$APK_PATH" -d "$TEMP_DIR"
    
    echo -e "${GREEN}APK extracted to: $TEMP_DIR${NC}"
    echo ""
    
    # Interactive exploration
    while true; do
        echo -e "${YELLOW}APK Inspector Options:${NC}"
        echo "1. Show directory structure"
        echo "2. View AndroidManifest.xml (readable)"
        echo "3. List native libraries"
        echo "4. Show classes.dex info"
        echo "5. Browse assets"
        echo "6. Exit inspector"
        echo ""
        read -p "Select option (1-6): " choice
        
        case $choice in
            1)
                echo -e "${BLUE}Directory structure:${NC}"
                find "$TEMP_DIR" -type f | head -30 | sort
                echo ""
                ;;
            2)
                if [ -f "$TEMP_DIR/AndroidManifest.xml" ]; then
                    echo -e "${BLUE}AndroidManifest.xml (binary to text):${NC}"
                    strings "$TEMP_DIR/AndroidManifest.xml" | grep -E "(activity|service|permission|package)" | head -20
                else
                    echo -e "${RED}AndroidManifest.xml not found${NC}"
                fi
                echo ""
                ;;
            3)
                echo -e "${BLUE}Native libraries:${NC}"
                find "$TEMP_DIR/lib" -name "*.so" -exec ls -lh {} \; 2>/dev/null | sort
                echo ""
                ;;
            4)
                if [ -f "$TEMP_DIR/classes.dex" ]; then
                    DEX_SIZE=$(ls -lh "$TEMP_DIR/classes.dex" | awk '{print $5}')
                    echo -e "${BLUE}classes.dex: $DEX_SIZE${NC}"
                    echo "Sample class names:"
                    strings "$TEMP_DIR/classes.dex" | grep -E "^L.*;" | head -10
                else
                    echo -e "${RED}classes.dex not found${NC}"
                fi
                echo ""
                ;;
            5)
                if [ -d "$TEMP_DIR/assets" ]; then
                    echo -e "${BLUE}Assets (first 20):${NC}"
                    find "$TEMP_DIR/assets" -type f | head -20
                else
                    echo -e "${RED}No assets directory${NC}"
                fi
                echo ""
                ;;
            6)
                break
                ;;
            *)
                echo -e "${RED}Invalid option${NC}"
                ;;
        esac
    done
    
    # Cleanup
    rm -rf "$TEMP_DIR"
    echo -e "${GREEN}Temporary files cleaned up${NC}"
}

# Function to show dependencies
show_dependencies() {
    echo -e "${YELLOW}📦 APK Dependencies Analysis${NC}"
    echo "============================="
    
    if [ ! -f "$APK_PATH" ]; then
        echo -e "${RED}❌ APK not found. Build first.${NC}"
        return 1
    fi
    
    echo -e "${BLUE}Native Library Dependencies:${NC}"
    
    # Show critical libraries
    CRITICAL_LIBS=("libopenjpeg.so" "librawbuf.so" "libbasis_transcoder.so" "libvivoxsdk.so")
    
    for lib in "${CRITICAL_LIBS[@]}"; do
        if unzip -l "$APK_PATH" | grep -q "$lib"; then
            SIZE=$(unzip -l "$APK_PATH" | grep "$lib" | head -1 | awk '{print $1}')
            SIZE_KB=$((SIZE / 1024))
            echo -e "  ✅ $lib (${SIZE_KB}KB)"
        else
            echo -e "  ❌ $lib (missing)"
        fi
    done
    
    echo ""
    echo -e "${BLUE}Voice Libraries (Vivox):${NC}"
    VOICE_LIBS=("libVxClient.so" "liboRTP.so" "libsndfile.so" "libvxaudio-jni.so")
    
    for lib in "${VOICE_LIBS[@]}"; do
        if unzip -l "$APK_PATH" | grep -q "$lib"; then
            SIZE=$(unzip -l "$APK_PATH" | grep "$lib" | head -1 | awk '{print $1}')
            SIZE_KB=$((SIZE / 1024))
            echo -e "  ✅ $lib (${SIZE_KB}KB)"
        else
            echo -e "  ❌ $lib (missing)"
        fi
    done
    
    echo ""
    echo -e "${BLUE}Gradle Dependencies:${NC}"
    cd "$PROJECT_ROOT"
    ./gradlew :app:dependencies --configuration debugRuntimeClasspath | head -30
}

# Function to start debugging session
start_debugging() {
    echo -e "${YELLOW}🐛 APK Debugging Session${NC}"
    echo "========================="
    
    if [ ! -f "$APK_PATH" ]; then
        echo -e "${RED}❌ APK not found. Building first...${NC}"
        if ! build_apk; then
            echo -e "${RED}❌ Cannot debug without APK${NC}"
            return 1
        fi
    fi
    
    # Check if APK loads correctly
    echo -e "${BLUE}Step 1: Validating APK structure...${NC}"
    if ! "$PROJECT_ROOT/validate_apk.sh"; then
        echo -e "${RED}❌ APK validation failed - starting structural debugging${NC}"
        debug_apk_structure
        return 1
    fi
    
    echo -e "${GREEN}✅ APK structure is valid${NC}"
    
    # Check for connected devices
    echo -e "${BLUE}Step 2: Checking connected devices...${NC}"
    if [ -f "$ADB" ]; then
        DEVICES=$("$ADB" devices | grep "device$" | wc -l)
        if [ "$DEVICES" -eq 0 ]; then
            echo -e "${YELLOW}⚠️ No devices connected. Debug options limited.${NC}"
            debug_offline
        else
            echo -e "${GREEN}✅ Found $DEVICES connected device(s)${NC}"
            debug_with_device
        fi
    else
        echo -e "${YELLOW}⚠️ ADB not available. Running offline debug.${NC}"
        debug_offline
    fi
}

# Function to debug APK structure issues
debug_apk_structure() {
    echo -e "${YELLOW}🔧 APK Structure Debugging${NC}"
    echo "=========================="
    
    # Check for missing components
    echo -e "${BLUE}Checking required components...${NC}"
    
    REQUIRED_COMPONENTS=("AndroidManifest.xml" "classes.dex")
    MISSING_COMPONENTS=0
    
    for component in "${REQUIRED_COMPONENTS[@]}"; do
        if unzip -l "$APK_PATH" | grep -q "$component"; then
            echo -e "  ✅ $component"
        else
            echo -e "  ❌ $component (CRITICAL)"
            MISSING_COMPONENTS=$((MISSING_COMPONENTS + 1))
        fi
    done
    
    # Check native libraries
    echo ""
    echo -e "${BLUE}Checking native library requirements...${NC}"
    
    REQUIRED_NATIVE=("libopenjpeg.so" "librawbuf.so" "libbasis_transcoder.so")
    MISSING_NATIVE=0
    
    for lib in "${REQUIRED_NATIVE[@]}"; do
        if unzip -l "$APK_PATH" | grep -q "$lib"; then
            echo -e "  ✅ $lib"
        else
            echo -e "  ❌ $lib (CRITICAL)"
            MISSING_NATIVE=$((MISSING_NATIVE + 1))
        fi
    done
    
    # Provide debugging suggestions
    echo ""
    echo -e "${YELLOW}📋 Debugging Suggestions:${NC}"
    
    if [ "$MISSING_COMPONENTS" -gt 0 ]; then
        echo -e "${RED}Critical APK components are missing:${NC}"
        echo "  1. Try rebuilding from clean state: ./cli_editor.sh quick-build"
        echo "  2. Check build.gradle for packaging issues"
        echo "  3. Verify Android SDK installation"
    fi
    
    if [ "$MISSING_NATIVE" -gt 0 ]; then
        echo -e "${RED}Critical native libraries are missing:${NC}"
        echo "  1. Check CMakeLists.txt configuration"
        echo "  2. Verify NDK installation and version"
        echo "  3. Review native build logs for compilation errors"
    fi
    
    if [ "$MISSING_COMPONENTS" -eq 0 ] && [ "$MISSING_NATIVE" -eq 0 ]; then
        echo -e "${GREEN}APK structure appears correct. Issue may be runtime-related.${NC}"
        echo "  1. Try installing on a device to test actual loading"
        echo "  2. Check logcat for runtime errors"
        echo "  3. Verify app permissions in AndroidManifest.xml"
    fi
}

# Function for offline debugging
debug_offline() {
    echo -e "${YELLOW}🔍 Offline APK Debugging${NC}"
    echo "========================"
    
    echo "Available offline debugging options:"
    echo "1. Analyze APK structure"
    echo "2. Check build configuration"
    echo "3. Review error patterns"
    echo "4. Validate dependencies"
    echo "5. Exit"
    
    read -p "Select option (1-5): " choice
    
    case $choice in
        1)
            analyze_apk
            ;;
        2)
            echo -e "${BLUE}Build Configuration:${NC}"
            cat "$PROJECT_ROOT/app/build.gradle" | grep -A 5 -B 5 "applicationId\|compileSdkVersion\|minSdkVersion\|targetSdkVersion"
            ;;
        3)
            echo -e "${BLUE}Common error patterns to check:${NC}"
            echo "- Missing permissions in AndroidManifest.xml"
            echo "- Incorrect package name"
            echo "- Missing native libraries"
            echo "- Proguard/R8 obfuscation issues"
            ;;
        4)
            show_dependencies
            ;;
        5)
            return 0
            ;;
        *)
            echo -e "${RED}Invalid option${NC}"
            ;;
    esac
}

# Function for debugging with connected device
debug_with_device() {
    echo -e "${YELLOW}📱 Device-based APK Debugging${NC}"
    echo "============================="
    
    local package_name="com.lumiyaviewer.lumiya"
    
    echo "Available device debugging options:"
    echo "1. Install and test APK"
    echo "2. Monitor app logs (logcat)"
    echo "3. Check app installation status"
    echo "4. Clear app data and reinstall"
    echo "5. Exit"
    
    read -p "Select option (1-5): " choice
    
    case $choice in
        1)
            install_and_test_apk "$package_name"
            ;;
        2)
            monitor_logs "$package_name"
            ;;
        3)
            check_installation_status "$package_name"
            ;;
        4)
            clear_and_reinstall "$package_name"
            ;;
        5)
            return 0
            ;;
        *)
            echo -e "${RED}Invalid option${NC}"
            ;;
    esac
}

# Function to install and test APK
install_and_test_apk() {
    local package_name="$1"
    
    echo -e "${BLUE}Installing APK...${NC}"
    if "$ADB" install -r "$APK_PATH"; then
        echo -e "${GREEN}✅ APK installed successfully${NC}"
        
        echo -e "${BLUE}Attempting to launch app...${NC}"
        if "$ADB" shell monkey -p "$package_name" -c android.intent.category.LAUNCHER 1; then
            echo -e "${GREEN}✅ App launched successfully${NC}"
            echo -e "${YELLOW}Monitor device for app behavior and check logcat for details${NC}"
        else
            echo -e "${RED}❌ App launch failed${NC}"
            echo -e "${YELLOW}Checking logcat for error messages...${NC}"
            "$ADB" logcat -d | tail -50 | grep -E "(Error|Exception|FATAL)"
        fi
    else
        echo -e "${RED}❌ APK installation failed${NC}"
        echo -e "${YELLOW}Common installation issues:${NC}"
        echo "  - Device storage full"
        echo "  - Package conflicts"
        echo "  - Invalid APK signature"
        echo "  - Insufficient permissions"
    fi
}

# Function to monitor logs
monitor_logs() {
    local package_name="$1"
    
    echo -e "${BLUE}Monitoring logs for $package_name...${NC}"
    echo -e "${YELLOW}Press Ctrl+C to stop monitoring${NC}"
    echo ""
    
    # Clear old logs and start monitoring
    "$ADB" logcat -c
    "$ADB" logcat | grep -E "($package_name|Lumiya|FATAL|AndroidRuntime)"
}

# Function to check installation status
check_installation_status() {
    local package_name="$1"
    
    echo -e "${BLUE}Checking installation status...${NC}"
    
    if "$ADB" shell pm list packages | grep -q "$package_name"; then
        echo -e "${GREEN}✅ App is installed${NC}"
        
        # Get app info
        echo -e "${BLUE}App information:${NC}"
        "$ADB" shell dumpsys package "$package_name" | grep -E "(versionName|versionCode|signatures|targetSdk)"
        
        # Check if app is running
        if "$ADB" shell ps | grep -q "$package_name"; then
            echo -e "${GREEN}✅ App is currently running${NC}"
        else
            echo -e "${YELLOW}⚠️ App is not currently running${NC}"
        fi
    else
        echo -e "${RED}❌ App is not installed${NC}"
    fi
}

# Function to clear and reinstall
clear_and_reinstall() {
    local package_name="$1"
    
    echo -e "${BLUE}Clearing app data and reinstalling...${NC}"
    
    # Uninstall if exists
    "$ADB" uninstall "$package_name" 2>/dev/null || true
    
    # Reinstall
    if "$ADB" install "$APK_PATH"; then
        echo -e "${GREEN}✅ Clean installation completed${NC}"
    else
        echo -e "${RED}❌ Reinstallation failed${NC}"
    fi
}

# Interactive CLI mode
interactive_mode() {
    echo -e "${CYAN}🚀 Interactive CLI Mode${NC}"
    echo "======================="
    echo "Type 'help' for available commands, 'quit' to exit"
    echo ""
    
    while true; do
        read -p "linkpoint-cli> " cmd args
        
        case $cmd in
            "help")
                show_usage
                ;;
            "clean")
                clean_project
                ;;
            "build")
                build_apk
                ;;
            "quick-build")
                clean_project && build_apk
                ;;
            "test")
                run_tests
                ;;
            "validate")
                "$PROJECT_ROOT/validate_apk.sh"
                ;;
            "analyze")
                analyze_apk
                ;;
            "inspect")
                inspect_apk
                ;;
            "debug")
                start_debugging
                ;;
            "status")
                show_status
                ;;
            "dependencies")
                show_dependencies
                ;;
            "devices")
                if [ -f "$ADB" ]; then
                    "$ADB" devices
                else
                    echo -e "${RED}ADB not available${NC}"
                fi
                ;;
            "install")
                if [ -f "$APK_PATH" ] && [ -f "$ADB" ]; then
                    "$ADB" install -r "$APK_PATH"
                else
                    echo -e "${RED}APK or ADB not available${NC}"
                fi
                ;;
            "logcat")
                if [ -f "$ADB" ]; then
                    monitor_logs "com.lumiyaviewer.lumiya"
                else
                    echo -e "${RED}ADB not available${NC}"
                fi
                ;;
            "quit"|"exit")
                echo -e "${GREEN}Goodbye!${NC}"
                break
                ;;
            "")
                # Empty command, continue
                ;;
            *)
                echo -e "${RED}Unknown command: $cmd${NC}"
                echo "Type 'help' for available commands"
                ;;
        esac
        echo ""
    done
}

# Environment doctor
run_doctor() {
    echo -e "${YELLOW}🩺 Environment Doctor${NC}"
    echo "==================="
    
    echo -e "${BLUE}Diagnosing build environment...${NC}"
    echo ""
    
    if check_environment; then
        echo ""
        echo -e "${GREEN}🎉 Environment is healthy!${NC}"
    else
        echo ""
        echo -e "${RED}❌ Environment issues detected${NC}"
        
        echo ""
        echo -e "${YELLOW}💡 Suggested fixes:${NC}"
        echo "1. Install or update Java SDK (Java 8+)"
        echo "2. Ensure ANDROID_HOME is set correctly"
        echo "3. Install Android SDK command-line tools"
        echo "4. Add Android SDK to PATH"
        echo "5. Install Android NDK for native compilation"
    fi
    
    echo ""
    show_status
}

# Main script logic
main() {
    show_banner
    
    local command="${1:-help}"
    
    case $command in
        "clean")
            clean_project
            ;;
        "build")
            build_apk
            ;;
        "quick-build")
            clean_project && build_apk
            ;;
        "release")
            echo -e "${YELLOW}Building release APK...${NC}"
            cd "$PROJECT_ROOT"
            ./gradlew assembleRelease
            ;;
        "test")
            run_tests
            ;;
        "validate")
            "$PROJECT_ROOT/validate_apk.sh"
            ;;
        "analyze")
            analyze_apk
            ;;
        "inspect")
            inspect_apk
            ;;
        "dependencies")
            show_dependencies
            ;;
        "debug")
            start_debugging
            ;;
        "logcat")
            if [ -f "$ADB" ]; then
                monitor_logs "com.lumiyaviewer.lumiya"
            else
                echo -e "${RED}ADB not available${NC}"
            fi
            ;;
        "status")
            show_status
            ;;
        "devices")
            if [ -f "$ADB" ]; then
                "$ADB" devices
            else
                echo -e "${RED}ADB not available${NC}"
            fi
            ;;
        "install")
            if [ -f "$APK_PATH" ] && [ -f "$ADB" ]; then
                "$ADB" install -r "$APK_PATH"
            else
                echo -e "${RED}APK or ADB not available${NC}"
            fi
            ;;
        "uninstall")
            if [ -f "$ADB" ]; then
                "$ADB" uninstall com.lumiyaviewer.lumiya
            else
                echo -e "${RED}ADB not available${NC}"
            fi
            ;;
        "launch")
            if [ -f "$ADB" ]; then
                "$ADB" shell monkey -p com.lumiyaviewer.lumiya -c android.intent.category.LAUNCHER 1
            else
                echo -e "${RED}ADB not available${NC}"
            fi
            ;;
        "interactive"|"cli")
            interactive_mode
            ;;
        "doctor")
            run_doctor
            ;;
        "help"|"--help"|"-h")
            show_usage
            ;;
        *)
            echo -e "${RED}Unknown command: $command${NC}"
            echo ""
            show_usage
            exit 1
            ;;
    esac
}

# Execute main function with all arguments
main "$@"