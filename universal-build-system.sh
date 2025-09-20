#!/bin/bash

# UNIVERSAL LINKPOINT BUILD SYSTEM
# Inspired by CleverFerret - Works on ANY server architecture
# Automatically detects and configures for: ARM64, x86_64, x86, ARMv7

set -e

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Colors for beautiful output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
PURPLE='\033[0;35m'
NC='\033[0m'

log_info() { echo -e "${BLUE}[INFO]${NC} $1"; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
log_warning() { echo -e "${YELLOW}[WARNING]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }
log_header() { echo -e "${CYAN}[HEADER]${NC} $1"; }

# Universal Architecture Detection - Enhanced from CleverFerret
detect_architecture() {
    local arch=$(uname -m)
    local os_name=$(uname -s)
    local detailed_arch=""
    local android_abi=""
    local needs_tools="false"
    
    case $arch in
        x86_64|amd64)
            detailed_arch="x86_64"
            android_abi="x86_64"
            needs_tools="false"  # Standard SDK works fine
            ;;
        aarch64|arm64)
            detailed_arch="arm64"
            android_abi="arm64-v8a"
            needs_tools="true"   # Needs android-tools for native AAPT2
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
            log_warning "Unknown architecture: $arch"
            detailed_arch="x86_64"
            android_abi="x86_64"
            needs_tools="false"
            ;;
    esac
    
    export DETECTED_ARCH="$detailed_arch"
    export DETECTED_ANDROID_ABI="$android_abi"
    export NEEDS_ANDROID_TOOLS="$needs_tools"
    export HOST_OS="$os_name"
    
    echo "$detailed_arch"
}

# Print beautiful banner
print_banner() {
    echo -e "${CYAN}"
    echo "╔════════════════════════════════════════════════════════════════╗"
    echo "║                         LINKPOINT                             ║"
    echo "║                 UNIVERSAL BUILD SYSTEM                        ║"
    echo "║              Works on ANY Server Architecture                 ║"
    echo "║           ARM64 • x86_64 • x86 • ARMv7 • More                ║"
    echo "╚════════════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
}

# Enhanced system information display
show_system_info() {
    local host_arch=$(detect_architecture)
    
    log_header "🌍 UNIVERSAL SYSTEM INFORMATION"
    echo "┌─────────────────────────────────────────────────────────────┐"
    echo "│ HOST SYSTEM                                                 │"
    echo "├─────────────────────────────────────────────────────────────┤"
    echo "│ OS:           $HOST_OS $(uname -r)"
    echo "│ Architecture: $(uname -m) → $host_arch"
    echo "│ Android ABI:  $DETECTED_ANDROID_ABI"
    echo "│ Needs Tools:  $NEEDS_ANDROID_TOOLS"
    echo "└─────────────────────────────────────────────────────────────┘"
    
    # Java information
    echo "┌─────────────────────────────────────────────────────────────┐"
    echo "│ JAVA ENVIRONMENT                                            │"
    echo "├─────────────────────────────────────────────────────────────┤"
    if command -v java >/dev/null 2>&1; then
        local java_version=$(java -version 2>&1 | head -1)
        echo "│ Java:         $java_version"
        if [ -n "$JAVA_HOME" ]; then
            echo "│ JAVA_HOME:    $JAVA_HOME"
        else
            echo "│ JAVA_HOME:    Not set"
        fi
    else
        echo "│ Java:         ❌ Not installed"
    fi
    echo "└─────────────────────────────────────────────────────────────┘"
    
    # Android SDK information
    echo "┌─────────────────────────────────────────────────────────────┐"
    echo "│ ANDROID SDK                                                 │"
    echo "├─────────────────────────────────────────────────────────────┤"
    if [ -n "$ANDROID_HOME" ] && [ -d "$ANDROID_HOME" ]; then
        echo "│ SDK Path:     $ANDROID_HOME"
        if [ -d "$ANDROID_HOME/build-tools" ]; then
            local build_tools=$(ls "$ANDROID_HOME/build-tools" | tail -1)
            echo "│ Build Tools:  $build_tools"
        fi
    else
        echo "│ Android SDK:  ❌ Not found or ANDROID_HOME not set"
    fi
    echo "└─────────────────────────────────────────────────────────────┘"
    
    # Android Tools status (CleverFerret inspired)
    echo "┌─────────────────────────────────────────────────────────────┐"
    echo "│ ANDROID-TOOLS INTEGRATION                                   │"
    echo "├─────────────────────────────────────────────────────────────┤"
    if [ -d "/tmp/android-tools" ]; then
        echo "│ Repository:   ✅ Available (/tmp/android-tools)"
        echo "│ Available AAPT2 Binaries:"
        for version in "android-11.0.0_r33" "android-9.0.0_r33"; do
            if [ -d "/tmp/android-tools/build/$version" ]; then
                echo "│   📦 $version:"
                for arch in "arm64-v8a" "armeabi-v7a" "x86_64" "x86"; do
                    local aapt2_path="/tmp/android-tools/build/$version/aapt2/$arch/bin/aapt2"
                    if [ -f "$aapt2_path" ]; then
                        local status="✅"
                        local current=""
                        [ "$arch" = "$DETECTED_ANDROID_ABI" ] && current=" (current)"
                        echo "│     $status $arch$current"
                    else
                        echo "│     ❌ $arch"
                    fi
                done
            fi
        done
    else
        echo "│ Repository:   ❌ Not available"
    fi
    echo "└─────────────────────────────────────────────────────────────┘"
    
    # Build system recommendations
    echo "┌─────────────────────────────────────────────────────────────┐"
    echo "│ BUILD RECOMMENDATIONS                                       │"
    echo "├─────────────────────────────────────────────────────────────┤"
    case $DETECTED_ARCH in
        "x86_64")
            echo "│ Strategy:     Standard Android SDK (optimal)"
            echo "│ Performance:  ⭐⭐⭐⭐⭐ Excellent"
            echo "│ Compatibility: ⭐⭐⭐⭐⭐ Perfect"
            ;;
        "arm64")
            echo "│ Strategy:     Android-tools native ARM64 binaries"
            echo "│ Performance:  ⭐⭐⭐⭐⭐ Excellent (native)"
            echo "│ Compatibility: ⭐⭐⭐⭐ Very Good (with android-tools)"
            ;;
        "armv7"|"x86")
            echo "│ Strategy:     Android-tools native binaries"
            echo "│ Performance:  ⭐⭐⭐⭐ Good (native)"
            echo "│ Compatibility: ⭐⭐⭐⭐ Very Good (with android-tools)"
            ;;
    esac
    echo "└─────────────────────────────────────────────────────────────┘"
    echo ""
}

# Enhanced prerequisite verification
verify_prerequisites() {
    log_info "🔍 Verifying universal build prerequisites..."
    local errors=0
    
    # Check Java with architecture awareness
    if ! command -v java >/dev/null 2>&1; then
        log_error "Java not found. Please install OpenJDK 17+"
        errors=$((errors + 1))
    else
        local java_version=$(java -version 2>&1 | head -1 | grep -o '"[0-9]*' | grep -o '[0-9]*' | head -1)
        if [ -z "$java_version" ]; then
            java_version=$(java -version 2>&1 | head -1 | grep -o '[0-9][0-9]*\.[0-9]' | cut -d'.' -f1)
        fi
        if [ "$java_version" -lt 17 ]; then
            log_error "Java 17+ required. Found version: $java_version"
            errors=$((errors + 1))
        else
            log_success "Java $java_version detected"
        fi
    fi
    
    # Auto-detect JAVA_HOME if not set
    if [ -z "$JAVA_HOME" ]; then
        log_warning "JAVA_HOME not set, attempting auto-detection..."
        local java_paths=(
            "/usr/lib/jvm/java-17-openjdk-$DETECTED_ARCH"
            "/usr/lib/jvm/java-17-openjdk-amd64"
            "/usr/lib/jvm/java-17-openjdk-arm64"
            "/usr/lib/jvm/java-17-openjdk"
            "/usr/lib/jvm/java-1.17.0-openjdk"
        )
        
        for java_path in "${java_paths[@]}"; do
            if [ -d "$java_path" ]; then
                export JAVA_HOME="$java_path"
                log_success "Auto-detected JAVA_HOME: $JAVA_HOME"
                break
            fi
        done
        
        if [ -z "$JAVA_HOME" ]; then
            log_error "Could not auto-detect JAVA_HOME"
            errors=$((errors + 1))
        fi
    fi
    
    # Check Android SDK with flexible detection
    if [ -z "$ANDROID_HOME" ]; then
        log_warning "ANDROID_HOME not set, trying default locations..."
        for path in "/opt/android-sdk" "$HOME/Android/Sdk" "/usr/local/android-sdk" "/android-sdk"; do
            if [ -d "$path" ]; then
                export ANDROID_HOME="$path"
                log_success "Auto-detected Android SDK: $ANDROID_HOME"
                break
            fi
        done
        
        if [ -z "$ANDROID_HOME" ]; then
            log_error "Android SDK not found. Please set ANDROID_HOME or install SDK"
            errors=$((errors + 1))
        fi
    fi
    
    # Update PATH
    if [ -n "$ANDROID_HOME" ]; then
        export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/build-tools/35.0.0:$PATH"
    fi
    
    return $errors
}

# Android-tools setup inspired by CleverFerret
setup_android_tools() {
    local arch="$1"
    
    log_info "🔧 Setting up android-tools for $arch..."
    
    # Ensure android-tools repository is available
    if [ ! -d "/tmp/android-tools" ]; then
        log_info "Cloning android-tools repository..."
        cd /tmp
        git clone --depth 1 https://github.com/JonForShort/android-tools.git >/dev/null 2>&1
    fi
    
    # Find best Android version
    local android_versions=("android-11.0.0_r33" "android-9.0.0_r33")
    local selected_version=""
    
    for version in "${android_versions[@]}"; do
        if [ -d "/tmp/android-tools/build/$version" ]; then
            selected_version="$version"
            break
        fi
    done
    
    if [ -z "$selected_version" ]; then
        log_error "No compatible android-tools version found"
        return 1
    fi
    
    log_success "Using android-tools version: $selected_version"
    
    # Setup AAPT2 for the detected architecture
    local aapt2_path="/tmp/android-tools/build/$selected_version/aapt2/$arch/bin/aapt2"
    
    if [ ! -f "$aapt2_path" ]; then
        log_warning "AAPT2 not found for $arch, trying fallbacks..."
        # Try fallback architectures
        local fallback_archs=("x86_64" "arm64-v8a" "armeabi-v7a" "x86")
        for fallback_arch in "${fallback_archs[@]}"; do
            if [ "$fallback_arch" != "$arch" ]; then
                local fallback_path="/tmp/android-tools/build/$selected_version/aapt2/$fallback_arch/bin/aapt2"
                if [ -f "$fallback_path" ]; then
                    log_success "Using fallback AAPT2: $fallback_arch"
                    aapt2_path="$fallback_path"
                    arch="$fallback_arch"
                    break
                fi
            fi
        done
    fi
    
    if [ ! -f "$aapt2_path" ]; then
        log_error "No compatible AAPT2 found"
        return 1
    fi
    
    # Setup the tools
    mkdir -p /opt/android-tools
    cp "$aapt2_path" /opt/android-tools/aapt2
    chmod +x /opt/android-tools/aapt2
    
    # Test the binary
    if /opt/android-tools/aapt2 version >/dev/null 2>&1; then
        log_success "Native AAPT2 configured for $arch"
        export CUSTOM_AAPT2_PATH="/opt/android-tools/aapt2"
        return 0
    else
        log_warning "Native AAPT2 test failed, will use SDK fallback"
        return 1
    fi
}

# Universal build configuration
configure_build_environment() {
    local arch="$1"
    
    log_info "⚙️ Configuring universal build environment for $arch..."
    
    # Set optimal Gradle options based on architecture
    case $arch in
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
    
    # Configure Android SDK paths
    export ANDROID_SDK_ROOT="$ANDROID_HOME"
    
    # Setup android-tools if needed
    if [ "$NEEDS_ANDROID_TOOLS" = "true" ]; then
        if setup_android_tools "$DETECTED_ANDROID_ABI"; then
            log_success "Android-tools configured successfully"
        else
            log_warning "Android-tools setup failed, using SDK fallback"
        fi
    fi
    
    log_success "Build environment configured for $arch"
}

# Universal clean function
clean_build_universal() {
    log_info "🧹 Universal build cleanup..."
    
    cd "$PROJECT_ROOT"
    
    # Clean Gradle caches
    rm -rf .gradle/ build/ */build/
    rm -f *.log gradle-android-tools-runtime.properties
    
    # Clean architecture-specific caches
    if [ -d "$HOME/.gradle/caches" ]; then
        log_info "Cleaning Gradle user caches..."
        find "$HOME/.gradle/caches" -name "aapt2*" -delete 2>/dev/null || true
        find "$HOME/.gradle/caches" -name "*android-tools*" -delete 2>/dev/null || true
    fi
    
    # Clean android-tools setup
    rm -rf /opt/android-tools/
    
    log_success "Universal cleanup completed"
}

# Universal build function with architecture optimization
build_universal() {
    local build_type="${1:-debug}"
    local arch=$(detect_architecture)
    
    log_header "🚀 UNIVERSAL LINKPOINT BUILD ($build_type)"
    log_info "Building for architecture: $arch ($DETECTED_ANDROID_ABI)"
    
    cd "$PROJECT_ROOT"
    
    # Configure build environment
    configure_build_environment "$arch"
    
    # Determine build task
    local build_task
    case "$build_type" in
        "release")
            build_task="assembleRelease"
            ;;
        "debug"|*)
            build_task="assembleDebug"
            ;;
    esac
    
    log_info "Executing: ./smart_gradlew.sh $build_task"
    
    # Execute build with architecture-optimized settings
    if ./smart_gradlew.sh $build_task \
        --parallel \
        --build-cache \
        --no-daemon \
        --stacktrace; then
        
        log_success "🎉 Universal build completed successfully!"
        find_and_report_apks "$build_type"
        return 0
    else
        log_error "❌ Universal build failed!"
        return 1
    fi
}

# Enhanced APK finder with architecture detection
find_and_report_apks() {
    local build_type="$1"
    log_info "🔍 Searching for generated APKs..."
    
    local apk_count=0
    while IFS= read -r -d '' apk_file; do
        if [[ "$apk_file" == *"$build_type"* ]]; then
            local size=$(ls -lh "$apk_file" | awk '{print $5}')
            local arch_info=""
            
            # Determine architecture from filename or path
            case "$apk_file" in
                *arm64-v8a*) arch_info=" (ARM64)" ;;
                *armeabi-v7a*) arch_info=" (ARM32)" ;;
                *x86_64*) arch_info=" (x86_64)" ;;
                *x86*) arch_info=" (x86)" ;;
                *universal*) arch_info=" (Universal)" ;;
                *) arch_info=" (Default)" ;;
            esac
            
            log_success "📱 APK: $(basename "$apk_file") ($size)$arch_info"
            apk_count=$((apk_count + 1))
        fi
    done < <(find "$PROJECT_ROOT" -name "*.apk" -type f -print0)
    
    if [ $apk_count -eq 0 ]; then
        log_warning "No APK files found"
    else
        log_success "Generated $apk_count APK(s) for $DETECTED_ARCH architecture"
    fi
}

# Test universal build environment
test_environment_universal() {
    log_header "🧪 TESTING UNIVERSAL BUILD ENVIRONMENT"
    
    cd "$PROJECT_ROOT"
    
    log_info "Testing Gradle configuration..."
    if ./smart_gradlew.sh tasks --no-daemon | head -10; then
        log_success "Gradle configuration test passed"
    else
        log_error "Gradle configuration test failed"
        return 1
    fi
    
    log_info "Testing architecture detection..."
    detect_architecture >/dev/null
    log_success "Architecture detection: $DETECTED_ARCH → $DETECTED_ANDROID_ABI"
    
    if [ "$NEEDS_ANDROID_TOOLS" = "true" ]; then
        log_info "Testing android-tools setup..."
        if setup_android_tools "$DETECTED_ANDROID_ABI"; then
            log_success "Android-tools test passed"
        else
            log_warning "Android-tools test failed (will use fallback)"
        fi
    fi
    
    return 0
}

# Main universal function
main() {
    local action="${1:-build}"
    local build_type="${2:-debug}"
    
    print_banner
    
    case "$action" in
        "info"|"system-info")
            show_system_info
            ;;
        "clean")
            clean_build_universal
            ;;
        "test"|"test-env")
            verify_prerequisites && test_environment_universal
            ;;
        "build")
            verify_prerequisites && build_universal "$build_type"
            ;;
        "full"|"full-build")
            verify_prerequisites && clean_build_universal && build_universal "$build_type"
            ;;
        "help"|"--help"|"-h")
            show_help
            ;;
        *)
            log_error "Unknown action: $action"
            show_help
            exit 1
            ;;
    esac
}

# Enhanced help function
show_help() {
    echo "🌍 Universal Linkpoint Build System"
    echo "Works on ANY server architecture: ARM64, x86_64, x86, ARMv7"
    echo ""
    echo "Usage: $0 [action] [build_type]"
    echo ""
    echo "Actions:"
    echo "  build       - Build the application (default)"
    echo "  full-build  - Clean and build"
    echo "  clean       - Clean build environment"
    echo "  test-env    - Test build environment"
    echo "  info        - Show detailed system information"
    echo "  help        - Show this help"
    echo ""
    echo "Build Types:"
    echo "  debug       - Debug build (default)"
    echo "  release     - Release build"
    echo ""
    echo "Examples:"
    echo "  $0                    # Debug build on current architecture"
    echo "  $0 build release      # Release build"
    echo "  $0 full-build         # Clean and debug build"
    echo "  $0 info               # Show system info"
    echo "  $0 test-env           # Test environment"
    echo ""
    echo "🏗️ Architecture Support:"
    echo "  ✅ x86_64 (Intel/AMD 64-bit)    - Standard SDK"
    echo "  ✅ ARM64 (aarch64)               - Native android-tools"
    echo "  ✅ ARMv7 (armv7l)                - Native android-tools"
    echo "  ✅ x86 (i386/i686)               - Native android-tools"
    echo ""
}

# Execute main function
main "$@"