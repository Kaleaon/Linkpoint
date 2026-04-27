# Linkpoint CLI Editor Guide

## Overview

The Linkpoint CLI Editor is a comprehensive command-line interface for building, testing, and debugging the Linkpoint Second Life Android client APK. It provides systematic tools for identifying and resolving APK loading issues.

## Quick Start

```bash
# Make CLI editor executable
chmod +x cli_editor.sh

# Check current status
./cli_editor.sh status

# Build APK
./cli_editor.sh build

# Test APK functionality
./cli_editor.sh test

# Start debugging session if issues arise
./cli_editor.sh debug
```

## Available Commands

### Build Commands

- `./cli_editor.sh clean` - Clean project build files
- `./cli_editor.sh build` - Build the debug APK
- `./cli_editor.sh quick-build` - Clean and build in one step
- `./cli_editor.sh release` - Build release APK

### Test Commands

- `./cli_editor.sh test` - Run comprehensive APK tests
- `./cli_editor.sh validate` - Quick APK structure validation
- `./cli_editor.sh analyze` - Deep APK analysis
- `./cli_editor.sh status` - Show current build status

### Debug Commands

- `./cli_editor.sh debug` - Start debugging session for APK issues
- `./cli_editor.sh inspect` - Inspect APK contents and structure
- `./cli_editor.sh dependencies` - Show APK dependencies and libraries

### Device Commands

- `./cli_editor.sh devices` - List connected Android devices
- `./cli_editor.sh install` - Install APK to connected device
- `./cli_editor.sh uninstall` - Remove app from connected device
- `./cli_editor.sh launch` - Launch app on device
- `./cli_editor.sh logcat` - Monitor Android logs for the app

### Utility Commands

- `./cli_editor.sh interactive` - Start interactive CLI session
- `./cli_editor.sh doctor` - Diagnose build environment
- `./cli_editor.sh help` - Show help message

## APK Loading Debug Process

### Step 1: Initial Validation

When an APK refuses to load, start with basic validation:

```bash
./cli_editor.sh validate
```

This checks:
- APK file integrity
- AndroidManifest.xml presence
- classes.dex availability
- Native library structure

### Step 2: Deep Analysis

If basic validation passes but loading still fails:

```bash
./cli_editor.sh analyze
```

This provides:
- Detailed APK size breakdown
- Native libraries by architecture
- Asset analysis
- Component inventory

### Step 3: Interactive Debugging

For complex issues, use the interactive debugger:

```bash
./cli_editor.sh debug
```

The debugger will:
1. Validate APK structure automatically
2. Check for connected devices
3. Provide offline or device-based debugging options
4. Offer specific solutions for detected issues

### Step 4: Device Testing

If you have an Android device connected:

```bash
# Check connected devices
./cli_editor.sh devices

# Install and test
./cli_editor.sh install
./cli_editor.sh launch

# Monitor logs for issues
./cli_editor.sh logcat
```

## Common Loading Issues and Solutions

### Issue 1: UnsatisfiedLinkError

**Symptoms:**
- App crashes immediately on launch
- Error: "Cannot load native library"

**Diagnosis:**
```bash
./cli_editor.sh dependencies
```

**Solution:**
- Ensure all required native libraries are present
- Check architecture compatibility
- Rebuild with: `./cli_editor.sh quick-build`

### Issue 2: ClassNotFoundException

**Symptoms:**
- App fails to find main activity
- Error: "MainActivity not found"

**Diagnosis:**
```bash
./cli_editor.sh inspect
# Select option 4 (classes.dex info)
```

**Solution:**
- Verify classes.dex is present and valid
- Check AndroidManifest.xml activity declarations
- Review ProGuard/R8 settings if enabled

### Issue 3: APK Corruption

**Symptoms:**
- APK fails to install
- "Package is invalid" error

**Diagnosis:**
```bash
./cli_editor.sh validate
```

**Solution:**
- Clean rebuild: `./cli_editor.sh quick-build`
- Check build environment: `./cli_editor.sh doctor`

### Issue 4: Permission Issues

**Symptoms:**
- App installs but crashes on certain actions
- SecurityException errors

**Diagnosis:**
```bash
./cli_editor.sh inspect
# Select option 2 (AndroidManifest.xml)
```

**Solution:**
- Add required permissions to AndroidManifest.xml
- Check targetSdkVersion compatibility

## Advanced Features

### Interactive Mode

Launch interactive CLI session:

```bash
./cli_editor.sh interactive
```

This provides a command prompt where you can run multiple commands without relaunching the script:

```
linkpoint-cli> status
linkpoint-cli> build
linkpoint-cli> test
linkpoint-cli> quit
```

### APK Inspector

Detailed APK exploration:

```bash
./cli_editor.sh inspect
```

Interactive options:
1. Show directory structure
2. View AndroidManifest.xml (readable format)
3. List native libraries with sizes
4. Show classes.dex information
5. Browse assets
6. Exit inspector

### Environment Doctor

Diagnose build environment issues:

```bash
./cli_editor.sh doctor
```

Checks:
- Java installation and version
- Gradle wrapper availability
- Android SDK installation
- ADB availability
- Build directory status

## Debug Demo Script

For learning and testing, use the debug demo:

```bash
chmod +x debug_apk_loading.sh
./debug_apk_loading.sh
```

This interactive demo shows:
1. How to create test scenarios (corrupted APKs)
2. Structural debugging processes
3. Runtime error scenarios and solutions
4. Successful loading validation
5. Available debugging tools overview

## Integration with Existing Tools

The CLI editor integrates with existing project scripts:

- `validate_apk.sh` - Called by `./cli_editor.sh validate`
- `test_apk_functionality.sh` - Called by `./cli_editor.sh test`
- `scripts/emulator_manager.sh` - Android emulator management

## Expected APK Behavior

### Successful Loading Indicators

When APK loads correctly:
1. ✅ APK installs without errors (~17MB size)
2. ✅ App launches and shows splash screen
3. ✅ Native libraries load (libopenjpeg, librawbuf, libbasis_transcoder)
4. ✅ Main activity displays properly
5. ✅ Core Second Life client functionality available
6. ⚠️ Voice features show "compatibility mode" (expected - non-critical)

### Architecture Support

The APK includes native libraries for:
- arm64-v8a (64-bit ARM)
- armeabi-v7a (32-bit ARM)  
- x86 (32-bit Intel)
- x86_64 (64-bit Intel)

### Key Components

Critical APK components:
- **AndroidManifest.xml** - App configuration and permissions
- **classes.dex** - Java bytecode (8.8MB)
- **libopenjpeg.so** - JPEG2000 and Basis Universal texture support
- **librawbuf.so** - DirectByteBuffer operations
- **libbasis_transcoder.so** - Texture transcoding
- **Assets** - 225 animation, shader, and texture files (6.9MB)

## Troubleshooting Tips

1. **Always start with `./cli_editor.sh status`** to see current state
2. **Use `./cli_editor.sh doctor`** if build environment issues are suspected
3. **Run `./cli_editor.sh test`** to verify comprehensive functionality
4. **Use device testing** when possible for real-world validation
5. **Monitor logs** with `./cli_editor.sh logcat` for runtime issues
6. **Clean rebuild** with `./cli_editor.sh quick-build` for persistent issues

## Support

For additional help:
- Run `./cli_editor.sh help` for command reference
- Use `./debug_apk_loading.sh` for interactive debugging tutorials
- Check project documentation in the `docs/` directory
- Review existing validation scripts for specific test details

The CLI editor provides systematic debugging for the Linkpoint Second Life client, ensuring reliable APK deployment and operation.