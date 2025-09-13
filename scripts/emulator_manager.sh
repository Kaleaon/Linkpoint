#!/bin/bash

# Android Emulator Management Tool
# Based on CLI-first approach for Android emulator management
# Provides comprehensive emulator management functionality for Linkpoint development

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$SCRIPT_DIR/.."

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Android SDK paths
if [ -z "$ANDROID_HOME" ]; then
    ANDROID_HOME="/usr/local/lib/android/sdk"
fi

AVDMANAGER="$ANDROID_HOME/cmdline-tools/latest/bin/avdmanager"
SDKMANAGER="$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager"
EMULATOR="$ANDROID_HOME/emulator/emulator"

# Check if emulator directory exists, fallback to platform-tools
if [ ! -d "$ANDROID_HOME/emulator" ]; then
    EMULATOR="$ANDROID_HOME/platform-tools/emulator"
fi

# Default configuration
DEFAULT_DEVICE="pixel_2"
DEFAULT_API="34"
DEFAULT_ABI="x86_64"
DEFAULT_TAG="google_apis"

echo -e "${CYAN}Android Emulator Management Tool${NC}"
echo -e "${CYAN}================================${NC}"

# Function to check if required tools are available
check_android_tools() {
    local tools_available=true
    
    if [ ! -f "$AVDMANAGER" ]; then
        echo -e "${RED}Error: avdmanager not found at $AVDMANAGER${NC}"
        tools_available=false
    fi
    
    if [ ! -f "$SDKMANAGER" ]; then
        echo -e "${RED}Error: sdkmanager not found at $SDKMANAGER${NC}"
        tools_available=false
    fi
    
    if [ ! -f "$EMULATOR" ] && [ ! -f "$ANDROID_HOME/platform-tools/emulator" ]; then
        echo -e "${YELLOW}Warning: emulator not found. You may need to install emulator package${NC}"
    fi
    
    if [ "$tools_available" = false ]; then
        echo -e "${RED}Please ensure Android SDK command-line tools are properly installed${NC}"
        return 1
    fi
    
    return 0
}

# Function to show usage information
show_usage() {
    echo -e "${GREEN}Usage: $0 [COMMAND] [OPTIONS]${NC}"
    echo ""
    echo -e "${YELLOW}Commands:${NC}"
    echo "  list-avds           List all available AVDs"
    echo "  list-devices        List all available device profiles"
    echo "  list-images         List all available system images"
    echo "  create [name]       Create a new AVD"
    echo "  start [name]        Start an AVD"
    echo "  stop [name]         Stop a running AVD"
    echo "  delete [name]       Delete an AVD"
    echo "  install-image       Install a system image"
    echo "  status              Show running emulators"
    echo "  help                Show this help message"
    echo ""
    echo -e "${YELLOW}Options for create:${NC}"
    echo "  --device DEVICE     Device profile (default: $DEFAULT_DEVICE)"
    echo "  --api API_LEVEL     API level (default: $DEFAULT_API)"
    echo "  --abi ABI           ABI type (default: $DEFAULT_ABI)"
    echo "  --tag TAG           System image tag (default: $DEFAULT_TAG)"
    echo ""
    echo -e "${YELLOW}Examples:${NC}"
    echo "  $0 create linkpoint-dev --api 34 --abi x86_64"
    echo "  $0 start linkpoint-dev"
    echo "  $0 list-avds"
    echo "  $0 install-image"
}

# Function to list all AVDs
list_avds() {
    echo -e "${GREEN}Available Android Virtual Devices:${NC}"
    echo "====================================="
    
    if $AVDMANAGER list avd | grep -q "Available Android Virtual Devices:"; then
        $AVDMANAGER list avd | sed -n '/Available Android Virtual Devices:/,/^$/p' | tail -n +2
    else
        echo -e "${YELLOW}No AVDs found. Use 'create' command to create one.${NC}"
    fi
}

# Function to list device profiles
list_devices() {
    echo -e "${GREEN}Available Device Profiles:${NC}"
    echo "=========================="
    $AVDMANAGER list device | grep -E "(id:|Name:)" | head -20
}

# Function to list system images
list_images() {
    echo -e "${GREEN}Available System Images:${NC}"
    echo "======================="
    $SDKMANAGER --list | grep "system-images" | head -20
}

# Function to install system image
install_image() {
    local api_level="${1:-$DEFAULT_API}"
    local abi="${2:-$DEFAULT_ABI}"
    local tag="${3:-$DEFAULT_TAG}"
    local image_package="system-images;android-$api_level;$tag;$abi"
    
    echo -e "${YELLOW}Installing system image: $image_package${NC}"
    
    if $SDKMANAGER "$image_package"; then
        echo -e "${GREEN}✓ System image installed successfully${NC}"
    else
        echo -e "${RED}✗ Failed to install system image${NC}"
        return 1
    fi
}

# Function to create new AVD
create_avd() {
    local avd_name="$1"
    local device="$DEFAULT_DEVICE"
    local api="$DEFAULT_API"
    local abi="$DEFAULT_ABI"
    local tag="$DEFAULT_TAG"
    
    if [ -z "$avd_name" ]; then
        echo -e "${RED}Error: AVD name is required${NC}"
        return 1
    fi
    
    # Parse additional options
    shift
    while [[ $# -gt 0 ]]; do
        case $1 in
            --device)
                device="$2"
                shift 2
                ;;
            --api)
                api="$2"
                shift 2
                ;;
            --abi)
                abi="$2"
                shift 2
                ;;
            --tag)
                tag="$2"
                shift 2
                ;;
            *)
                echo -e "${YELLOW}Warning: Unknown option $1${NC}"
                shift
                ;;
        esac
    done
    
    local system_image="system-images;android-$api;$tag;$abi"
    
    echo -e "${YELLOW}Creating AVD '$avd_name'...${NC}"
    echo "Device: $device"
    echo "API Level: $api"
    echo "ABI: $abi"
    echo "Tag: $tag"
    echo "System Image: $system_image"
    
    # Check if system image is available
    if ! $SDKMANAGER --list | grep -q "$system_image"; then
        echo -e "${YELLOW}System image not found. Installing...${NC}"
        if ! install_image "$api" "$abi" "$tag"; then
            return 1
        fi
    fi
    
    # Create the AVD
    echo "no" | $AVDMANAGER create avd \
        --name "$avd_name" \
        --package "$system_image" \
        --device "$device" \
        --force
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✓ AVD '$avd_name' created successfully${NC}"
        
        # Configure AVD for better performance
        local avd_config="$HOME/.android/avd/${avd_name}.avd/config.ini"
        if [ -f "$avd_config" ]; then
            echo -e "${YELLOW}Configuring AVD for optimal performance...${NC}"
            # Add performance optimizations
            echo "hw.gpu.enabled=yes" >> "$avd_config"
            echo "hw.gpu.mode=host" >> "$avd_config"
            echo "hw.ramSize=4096" >> "$avd_config"
            echo "vm.heapSize=512" >> "$avd_config"
            echo "disk.dataPartition.size=6442450944" >> "$avd_config"
        fi
    else
        echo -e "${RED}✗ Failed to create AVD '$avd_name'${NC}"
        return 1
    fi
}

# Function to start AVD
start_avd() {
    local avd_name="$1"
    
    if [ -z "$avd_name" ]; then
        echo -e "${RED}Error: AVD name is required${NC}"
        return 1
    fi
    
    # Check if AVD exists
    if ! $AVDMANAGER list avd | grep -q "Name: $avd_name"; then
        echo -e "${RED}Error: AVD '$avd_name' not found${NC}"
        return 1
    fi
    
    echo -e "${YELLOW}Starting AVD '$avd_name'...${NC}"
    
    # Start emulator with optimal parameters
    nohup $EMULATOR -avd "$avd_name" \
        -gpu host \
        -memory 4096 \
        -cores 4 \
        -partition-size 6144 \
        -no-boot-anim \
        -no-audio \
        -netdelay none \
        -netspeed full \
        > "$SCRIPT_DIR/emulator_${avd_name}.log" 2>&1 &
    
    local emulator_pid=$!
    echo "Emulator PID: $emulator_pid"
    echo "$emulator_pid" > "$SCRIPT_DIR/emulator_${avd_name}.pid"
    
    echo -e "${GREEN}✓ AVD '$avd_name' is starting...${NC}"
    echo -e "${CYAN}Log file: $SCRIPT_DIR/emulator_${avd_name}.log${NC}"
    echo -e "${CYAN}Use 'adb devices' to check when device is ready${NC}"
}

# Function to stop AVD
stop_avd() {
    local avd_name="$1"
    
    if [ -z "$avd_name" ]; then
        echo -e "${RED}Error: AVD name is required${NC}"
        return 1
    fi
    
    local pid_file="$SCRIPT_DIR/emulator_${avd_name}.pid"
    
    if [ -f "$pid_file" ]; then
        local emulator_pid=$(cat "$pid_file")
        if kill "$emulator_pid" 2>/dev/null; then
            echo -e "${GREEN}✓ AVD '$avd_name' stopped (PID: $emulator_pid)${NC}"
            rm -f "$pid_file"
        else
            echo -e "${YELLOW}Warning: Could not stop process $emulator_pid (already stopped?)${NC}"
            rm -f "$pid_file"
        fi
    else
        echo -e "${YELLOW}Warning: PID file not found. Attempting to find and stop emulator...${NC}"
        pkill -f "emulator.*$avd_name" && echo -e "${GREEN}✓ Emulator stopped${NC}" || echo -e "${YELLOW}No running emulator found${NC}"
    fi
}

# Function to delete AVD
delete_avd() {
    local avd_name="$1"
    
    if [ -z "$avd_name" ]; then
        echo -e "${RED}Error: AVD name is required${NC}"
        return 1
    fi
    
    echo -e "${YELLOW}Deleting AVD '$avd_name'...${NC}"
    
    if $AVDMANAGER delete avd --name "$avd_name"; then
        echo -e "${GREEN}✓ AVD '$avd_name' deleted successfully${NC}"
        
        # Clean up any leftover files
        rm -f "$SCRIPT_DIR/emulator_${avd_name}.pid"
        rm -f "$SCRIPT_DIR/emulator_${avd_name}.log"
    else
        echo -e "${RED}✗ Failed to delete AVD '$avd_name'${NC}"
        return 1
    fi
}

# Function to show emulator status
show_status() {
    echo -e "${GREEN}Emulator Status:${NC}"
    echo "================"
    
    # Show running emulators
    local running_emulators=$(pgrep -f "emulator" || true)
    if [ -n "$running_emulators" ]; then
        echo -e "${GREEN}Running emulators:${NC}"
        ps aux | grep "[e]mulator" | awk '{print $2, $11, $12, $13, $14, $15}'
    else
        echo -e "${YELLOW}No emulators currently running${NC}"
    fi
    
    echo ""
    echo -e "${GREEN}ADB Devices:${NC}"
    if command -v adb &> /dev/null; then
        adb devices
    else
        echo -e "${YELLOW}ADB not found in PATH${NC}"
    fi
}

# Main script logic
main() {
    # Check if Android tools are available
    if ! check_android_tools; then
        exit 1
    fi
    
    local command="${1:-help}"
    
    case $command in
        list-avds|avds)
            list_avds
            ;;
        list-devices|devices)
            list_devices
            ;;
        list-images|images)
            list_images
            ;;
        install-image)
            install_image "${2:-$DEFAULT_API}" "${3:-$DEFAULT_ABI}" "${4:-$DEFAULT_TAG}"
            ;;
        create)
            shift
            create_avd "$@"
            ;;
        start)
            start_avd "$2"
            ;;
        stop)
            stop_avd "$2"
            ;;
        delete)
            delete_avd "$2"
            ;;
        status)
            show_status
            ;;
        help|--help|-h)
            show_usage
            ;;
        *)
            echo -e "${RED}Error: Unknown command '$command'${NC}"
            echo ""
            show_usage
            exit 1
            ;;
    esac
}

# Execute main function with all arguments
main "$@"