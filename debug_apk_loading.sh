#!/bin/bash

# APK Loading Debug Demo Script
# Demonstrates debugging process when APK refuses to load

set -e

# Script configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$SCRIPT_DIR"
APK_PATH="$PROJECT_ROOT/app/build/outputs/apk/debug/app-debug.apk"

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

echo -e "${CYAN}╔══════════════════════════════════════════════════════════════╗${NC}"
echo -e "${CYAN}║                 APK Loading Debug Demo                       ║${NC}"
echo -e "${CYAN}║         Simulating and Resolving Loading Issues             ║${NC}"
echo -e "${CYAN}╚══════════════════════════════════════════════════════════════╝${NC}"
echo ""

# Function to simulate a corrupted APK
create_corrupted_apk() {
    local corrupted_path="/tmp/corrupted-app-debug.apk"
    echo -e "${YELLOW}📝 Creating simulated corrupted APK for debugging demo...${NC}"
    
    # Create a truncated/corrupted version
    if [ -f "$APK_PATH" ]; then
        head -c 1000 "$APK_PATH" > "$corrupted_path"
        echo -e "${RED}❌ Created corrupted APK: $corrupted_path${NC}"
        echo "Original size: $(ls -lh "$APK_PATH" | awk '{print $5}')"
        echo "Corrupted size: $(ls -lh "$corrupted_path" | awk '{print $5}')"
        return 0
    else
        echo -e "${RED}❌ Original APK not found${NC}"
        return 1
    fi
}

# Function to demonstrate structural debugging
demonstrate_structural_debug() {
    echo -e "${BLUE}🔍 DEMO: APK Structure Debugging Process${NC}"
    echo "========================================="
    
    local corrupted_path="/tmp/corrupted-app-debug.apk"
    
    if [ ! -f "$corrupted_path" ]; then
        echo -e "${RED}❌ Need to create corrupted APK first${NC}"
        return 1
    fi
    
    echo -e "${YELLOW}Step 1: Initial APK validation attempt...${NC}"
    echo ""
    
    # Try to validate corrupted APK
    echo "Running validation on corrupted APK..."
    if unzip -t "$corrupted_path" >/dev/null 2>&1; then
        echo -e "${GREEN}✅ APK file integrity check passed${NC}"
    else
        echo -e "${RED}❌ APK file corruption detected!${NC}"
        echo ""
        echo -e "${YELLOW}🔧 DEBUGGING STEP: File integrity check failed${NC}"
        echo "This indicates the APK file is corrupted or incomplete."
        echo ""
        
        echo -e "${BLUE}Comparing file sizes:${NC}"
        echo "Original APK: $(ls -lh "$APK_PATH" | awk '{print $5}')"
        echo "Corrupted APK: $(ls -lh "$corrupted_path" | awk '{print $5}')"
        echo ""
        
        echo -e "${GREEN}💡 SOLUTION: Rebuild APK from clean state${NC}"
        echo "Commands to fix:"
        echo "  1. ./cli_editor.sh clean"
        echo "  2. ./cli_editor.sh build"
        echo ""
    fi
    
    echo -e "${YELLOW}Step 2: Component verification...${NC}"
    
    # Check for required components
    REQUIRED_COMPONENTS=("AndroidManifest.xml" "classes.dex")
    
    for component in "${REQUIRED_COMPONENTS[@]}"; do
        if unzip -l "$corrupted_path" 2>/dev/null | grep -q "$component"; then
            echo -e "  ✅ $component present"
        else
            echo -e "  ❌ $component missing (CRITICAL ISSUE)"
            echo -e "${YELLOW}    🔧 DEBUG: Missing $component indicates severe APK corruption${NC}"
            echo -e "${GREEN}    💡 SOLUTION: Full rebuild required${NC}"
        fi
    done
    
    echo ""
    echo -e "${YELLOW}Step 3: Native library verification...${NC}"
    
    CRITICAL_LIBS=("libopenjpeg.so" "librawbuf.so" "libbasis_transcoder.so")
    
    for lib in "${CRITICAL_LIBS[@]}"; do
        if unzip -l "$corrupted_path" 2>/dev/null | grep -q "$lib"; then
            echo -e "  ✅ $lib present"
        else
            echo -e "  ❌ $lib missing (CRITICAL ISSUE)"
            echo -e "${YELLOW}    🔧 DEBUG: Missing native libraries cause UnsatisfiedLinkError${NC}"
            echo -e "${GREEN}    💡 SOLUTION: Check NDK configuration and rebuild${NC}"
        fi
    done
}

# Function to demonstrate runtime debugging
demonstrate_runtime_debug() {
    echo -e "${BLUE}🔍 DEMO: Runtime Loading Debug Process${NC}"
    echo "======================================="
    
    echo -e "${YELLOW}Simulating common runtime loading issues...${NC}"
    echo ""
    
    echo -e "${RED}SCENARIO 1: UnsatisfiedLinkError${NC}"
    echo "Error: java.lang.UnsatisfiedLinkError: Cannot load native library"
    echo ""
    echo -e "${YELLOW}🔧 DEBUGGING STEPS:${NC}"
    echo "1. Check if native libraries are present in APK"
    echo "2. Verify architecture compatibility"
    echo "3. Check library dependencies"
    echo ""
    echo -e "${GREEN}💡 DIAGNOSTIC COMMANDS:${NC}"
    echo "  ./cli_editor.sh dependencies    # Check library status"
    echo "  ./cli_editor.sh analyze        # Deep APK analysis"
    echo "  ./cli_editor.sh inspect        # Interactive inspection"
    echo ""
    
    echo -e "${RED}SCENARIO 2: ClassNotFoundException${NC}"
    echo "Error: java.lang.ClassNotFoundException: MainActivity not found"
    echo ""
    echo -e "${YELLOW}🔧 DEBUGGING STEPS:${NC}"
    echo "1. Verify classes.dex is present and valid"
    echo "2. Check AndroidManifest.xml for correct activity declarations"
    echo "3. Examine ProGuard/R8 obfuscation settings"
    echo ""
    echo -e "${GREEN}💡 DIAGNOSTIC COMMANDS:${NC}"
    echo "  ./cli_editor.sh inspect        # Browse APK contents"
    echo "  ./cli_editor.sh validate       # Structure validation"
    echo ""
    
    echo -e "${RED}SCENARIO 3: Permission Denied${NC}"
    echo "Error: SecurityException: Permission denied to access functionality"
    echo ""
    echo -e "${YELLOW}🔧 DEBUGGING STEPS:${NC}"
    echo "1. Check AndroidManifest.xml for required permissions"
    echo "2. Verify targetSdkVersion compatibility"
    echo "3. Test on different Android versions"
    echo ""
    echo -e "${GREEN}💡 DIAGNOSTIC COMMANDS:${NC}"
    echo "  ./cli_editor.sh inspect        # View manifest"
    echo "  ./cli_editor.sh debug          # Full debug session"
    echo ""
}

# Function to demonstrate successful debugging resolution
demonstrate_success_case() {
    echo -e "${BLUE}🎉 DEMO: Successful APK Loading${NC}"
    echo "================================"
    
    echo -e "${GREEN}Testing with working APK...${NC}"
    echo ""
    
    # Run validation on working APK
    if [ -f "$APK_PATH" ]; then
        echo -e "${YELLOW}Running complete validation suite...${NC}"
        
        # Quick structure check
        if unzip -t "$APK_PATH" >/dev/null 2>&1; then
            echo -e "${GREEN}✅ APK file integrity verified${NC}"
        else
            echo -e "${RED}❌ APK file integrity failed${NC}"
        fi
        
        # Component check
        REQUIRED_COMPONENTS=("AndroidManifest.xml" "classes.dex")
        ALL_COMPONENTS=true
        
        for component in "${REQUIRED_COMPONENTS[@]}"; do
            if unzip -l "$APK_PATH" | grep -q "$component"; then
                echo -e "${GREEN}✅ $component present${NC}"
            else
                echo -e "${RED}❌ $component missing${NC}"
                ALL_COMPONENTS=false
            fi
        done
        
        # Native library check
        CRITICAL_LIBS=("libopenjpeg.so" "librawbuf.so" "libbasis_transcoder.so")
        ALL_LIBS=true
        
        for lib in "${CRITICAL_LIBS[@]}"; do
            if unzip -l "$APK_PATH" | grep -q "$lib"; then
                SIZE=$(unzip -l "$APK_PATH" | grep "$lib" | head -1 | awk '{print $1}')
                SIZE_KB=$((SIZE / 1024))
                echo -e "${GREEN}✅ $lib present (${SIZE_KB}KB)${NC}"
            else
                echo -e "${RED}❌ $lib missing${NC}"
                ALL_LIBS=false
            fi
        done
        
        echo ""
        if [ "$ALL_COMPONENTS" = true ] && [ "$ALL_LIBS" = true ]; then
            echo -e "${GREEN}🎉 APK LOADING SHOULD SUCCEED!${NC}"
            echo ""
            echo -e "${BLUE}Expected successful loading sequence:${NC}"
            echo "1. APK installs without errors"
            echo "2. App launches and shows splash screen"
            echo "3. Native libraries load successfully"
            echo "4. Main activity displays properly"
            echo "5. All core functionality available"
            echo ""
            echo -e "${GREEN}✅ DEBUG RESOLUTION: No issues found${NC}"
        else
            echo -e "${RED}❌ APK LOADING WOULD FAIL${NC}"
            echo -e "${YELLOW}Issues need to be resolved before deployment${NC}"
        fi
    else
        echo -e "${RED}❌ Working APK not found - need to build first${NC}"
    fi
}

# Function to show debugging tools overview
show_debugging_tools() {
    echo -e "${BLUE}🛠️ Available Debugging Tools${NC}"
    echo "============================="
    echo ""
    
    echo -e "${YELLOW}CLI Editor Commands for Debugging:${NC}"
    echo ""
    echo -e "${GREEN}./cli_editor.sh debug${NC}         - Start interactive debugging session"
    echo -e "${GREEN}./cli_editor.sh test${NC}          - Run comprehensive APK tests"
    echo -e "${GREEN}./cli_editor.sh validate${NC}      - Quick structure validation"
    echo -e "${GREEN}./cli_editor.sh analyze${NC}       - Deep APK analysis"
    echo -e "${GREEN}./cli_editor.sh inspect${NC}       - Interactive APK inspector"
    echo -e "${GREEN}./cli_editor.sh dependencies${NC}  - Show library dependencies"
    echo -e "${GREEN}./cli_editor.sh status${NC}        - Show current build status"
    echo -e "${GREEN}./cli_editor.sh doctor${NC}        - Diagnose build environment"
    echo ""
    
    echo -e "${YELLOW}Device-specific Debugging:${NC}"
    echo ""
    echo -e "${GREEN}./cli_editor.sh devices${NC}       - List connected devices"
    echo -e "${GREEN}./cli_editor.sh install${NC}       - Install APK to device"
    echo -e "${GREEN}./cli_editor.sh launch${NC}        - Launch app on device"
    echo -e "${GREEN}./cli_editor.sh logcat${NC}        - Monitor device logs"
    echo ""
    
    echo -e "${YELLOW}Manual Debugging Utilities:${NC}"
    echo ""
    echo -e "${GREEN}./validate_apk.sh${NC}             - APK structure validation"
    echo -e "${GREEN}./test_apk_functionality.sh${NC}   - Comprehensive functionality test"
    echo -e "${GREEN}./scripts/emulator_manager.sh${NC} - Android emulator management"
    echo ""
}

# Interactive demo menu
interactive_debug_demo() {
    while true; do
        echo -e "${CYAN}🎯 APK Loading Debug Demo Menu${NC}"
        echo "==============================="
        echo ""
        echo "1. Create corrupted APK (for testing)"
        echo "2. Demonstrate structural debugging"
        echo "3. Demonstrate runtime debugging scenarios"
        echo "4. Test successful APK loading"
        echo "5. Show available debugging tools"
        echo "6. Run live debugging session with CLI editor"
        echo "7. Exit demo"
        echo ""
        read -p "Select option (1-7): " choice
        
        case $choice in
            1)
                create_corrupted_apk
                echo ""
                ;;
            2)
                demonstrate_structural_debug
                echo ""
                ;;
            3)
                demonstrate_runtime_debug
                echo ""
                ;;
            4)
                demonstrate_success_case
                echo ""
                ;;
            5)
                show_debugging_tools
                echo ""
                ;;
            6)
                echo -e "${YELLOW}Launching CLI editor debugging session...${NC}"
                "$PROJECT_ROOT/cli_editor.sh" debug
                echo ""
                ;;
            7)
                echo -e "${GREEN}Debug demo completed!${NC}"
                break
                ;;
            *)
                echo -e "${RED}Invalid option. Please select 1-7.${NC}"
                echo ""
                ;;
        esac
        
        read -p "Press Enter to continue..." 
        echo ""
    done
}

# Cleanup function
cleanup() {
    echo -e "${YELLOW}Cleaning up demo files...${NC}"
    rm -f /tmp/corrupted-app-debug.apk
    echo -e "${GREEN}Cleanup completed${NC}"
}

# Main execution
main() {
    echo -e "${GREEN}Starting APK Loading Debug Demo...${NC}"
    echo ""
    
    # Check if APK exists
    if [ ! -f "$APK_PATH" ]; then
        echo -e "${YELLOW}⚠️ APK not found. Building first...${NC}"
        cd "$PROJECT_ROOT"
        if ./cli_editor.sh build; then
            echo -e "${GREEN}✅ APK built successfully${NC}"
        else
            echo -e "${RED}❌ APK build failed. Cannot run demo.${NC}"
            exit 1
        fi
        echo ""
    fi
    
    # Run interactive demo
    interactive_debug_demo
    
    # Cleanup
    cleanup
    
    echo -e "${GREEN}🎉 APK Loading Debug Demo completed successfully!${NC}"
    echo ""
    echo -e "${BLUE}Key takeaways:${NC}"
    echo "1. Always validate APK structure first"
    echo "2. Check native libraries for UnsatisfiedLinkError issues"
    echo "3. Use CLI editor tools for systematic debugging"
    echo "4. Test on actual devices when possible"
    echo "5. Monitor logs for runtime error details"
    echo ""
    echo -e "${YELLOW}For production debugging, use:${NC}"
    echo "  ./cli_editor.sh debug"
}

# Handle cleanup on exit
trap cleanup EXIT

# Run main function
main "$@"