# Testing Instructions: UI and Login Functionality

## Quick Start Testing Guide

### **Option 1: Core Authentication Test (No APK needed)**

Create a standalone test to verify the login system:

```bash
# Navigate to project directory
cd /app

# Create test directory
mkdir -p testing

# Create simple authentication test
cat > testing/AuthTest.java << 'EOF'
import com.lumiyaviewer.lumiya.slproto.auth.SLAuth;

public class AuthTest {
    public static void main(String[] args) {
        System.out.println("Testing SL Authentication...");
        
        // Test password hashing
        String testPassword = "password123";
        String hash = SLAuth.getPasswordHash(testPassword);
        System.out.println("Password: " + testPassword);
        System.out.println("Hash: " + hash);
        System.out.println("Expected format: $1$[32-char MD5 hash]");
        
        // Verify hash format
        if (hash.startsWith("$1$") && hash.length() == 35) {
            System.out.println("✅ Password hashing: WORKING");
        } else {
            System.out.println("❌ Password hashing: FAILED");
        }
    }
}
EOF
```

### **Option 2: Build System Fix**

Try alternative build approach:

```bash
# Clean previous build attempts
cd /app
./gradlew clean

# Try with different Gradle options
./gradlew assembleDebug --no-daemon --max-workers 1 --info

# If still failing, try older build tools
# (Would need to modify build.gradle to use older versions)
```

### **Option 3: Docker Build Environment**

Create proper Android build environment:

```bash
# Create Dockerfile for Android build
cat > Dockerfile << 'EOF'
FROM openjdk:17-jdk-slim

# Install Android SDK
RUN apt-get update && apt-get install -y wget unzip
RUN mkdir -p /opt/android-sdk
WORKDIR /opt/android-sdk
RUN wget -q https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip
RUN unzip commandlinetools-linux-9477386_latest.zip
RUN mkdir -p cmdline-tools && mv cmdline-tools latest && mv latest cmdline-tools/

# Set environment variables
ENV ANDROID_HOME=/opt/android-sdk
ENV PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin

# Accept licenses and install components
RUN yes | sdkmanager --licenses
RUN sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

WORKDIR /app
EOF

# Build and run
docker build -t android-build .
docker run -v $(pwd):/app android-build ./gradlew assembleDebug
```

## **Manual Testing Steps**

### **1. Authentication Logic Test**

**Purpose**: Verify that password hashing and login request formatting work correctly.

**Steps**:
1. Extract `SLAuth.getPasswordHash()` method
2. Test with sample password: `"test123"`
3. Expected result: `$1$` + 32-character MD5 hash
4. Verify format matches Second Life requirements

**Expected Output**:
```
Password: test123
Hash: $1$098f6bcd4621d373cade4e832627b4f6
✅ Hash format correct
```

### **2. Network Connectivity Test**

**Purpose**: Verify connection to Second Life login servers.

**Test Command**:
```bash
curl -X POST https://login.secondlife.com/cgi-bin/login.cgi \
  -H "Content-Type: text/xml" \
  -d '<?xml version="1.0"?>
<methodCall>
  <methodName>login_to_simulator</methodName>
  <params>
    <param>
      <value>
        <struct>
          <member>
            <name>first</name>
            <value><string>Test</string></value>
          </member>
          <member>
            <name>last</name>
            <value><string>User</string></value>
          </member>
          <member>
            <name>passwd</name>
            <value><string>$1$invalidhash</string></value>
          </member>
        </struct>
      </value>
    </param>
  </params>
</methodCall>'
```

**Expected Response**: XML error message (confirms server is reachable)

### **3. APK Testing (Once Built Successfully)**

**Prerequisites**:
- Android device or emulator
- USB debugging enabled
- APK built successfully

**Testing Steps**:

#### **A. Installation Test**
```bash
# Install APK
adb install app/build/outputs/apk/debug/app-debug.apk

# Launch application
adb shell am start -n com.lumiyaviewer.lumiya/.ui.login.LoginActivity

# Check for crashes
adb logcat | grep -E "lumiya|OpenJPEG|SLAuth"
```

#### **B. UI Display Test**
1. **Launch app**
2. **Verify UI elements display**:
   - Username/password fields
   - Grid selection dropdown
   - Login button
   - "What's New" text
3. **Check for texture loading errors** in logs

#### **C. Grid Selection Test**
1. **Tap grid dropdown**
2. **Verify grids appear**:
   - Second Life (main grid)
   - Second Life Beta
   - Any custom grids
3. **Select different grids**
4. **Verify selection changes**

#### **D. Login Flow Test**
1. **Enter test credentials**:
   - Username: `firstname lastname`
   - Password: `password123`
   - Grid: `Second Life`
2. **Tap Login button**
3. **Verify behavior**:
   - Progress indicator shows
   - Status text updates
   - Network request made
4. **Check expected failure** (unless using real credentials)
5. **Verify error dialog appears** with appropriate message

#### **E. Texture Loading Test**
1. **Watch logcat during UI display**:
   ```bash
   adb logcat | grep OpenJPEG
   ```
2. **Expected logs**:
   ```
   OpenJPEG: Initializing OpenJPEG decoder (basic implementation)
   OpenJPEG: Successfully decoded J2K to RGB: 256x256 (196608 bytes)
   ```
3. **Verify no texture loading crashes**

### **4. Authentication Integration Test**

**Purpose**: Test complete login flow with real credentials (optional).

**Prerequisites**: 
- Valid Second Life account
- Account credentials

**Steps**:
1. **Enter real SL credentials**
2. **Attempt login**
3. **Expected outcomes**:
   - **Success**: Progress to main chat interface
   - **Failure**: Appropriate error message
4. **Log analysis**:
   ```bash
   adb logcat | grep -E "Auth|Login|SLAuth"
   ```

## **Success Criteria**

### **Minimum Viable Success**:
- ✅ APK builds without errors
- ✅ App launches and displays login UI
- ✅ No immediate crashes
- ✅ UI elements are visible and functional

### **Full Functionality Success**:
- ✅ Grid selection works
- ✅ Login attempt processes correctly
- ✅ Appropriate error handling
- ✅ Texture loading functional (no placeholder patterns)
- ✅ Network communication with SL servers

### **Stretch Goal Success**:
- ✅ Successful login to Second Life
- ✅ Transition to main application interface
- ✅ Basic world rendering begins

## **Troubleshooting Common Issues**

### **Build Failures**
```
Error: AAPT2 daemon startup failed
```
**Solution**: Try different Android SDK version or build environment

### **App Crashes on Launch**
```
java.lang.UnsatisfiedLinkError: dlopen failed
```
**Solution**: Native library (OpenJPEG) not properly included
- Check CMakeLists.txt configuration
- Verify native library builds correctly

### **Login Timeouts**
```
java.net.SocketTimeoutException
```
**Solution**: 
- Check network connectivity
- Verify Second Life server status
- Test with different grid

### **Texture Loading Issues**
```
OpenJPEG: Failed to decode J2K image
```
**Solution**: 
- Current implementation uses placeholder textures
- Logs should show pattern generation, not actual J2K decoding
- This is expected behavior for now

## **Next Steps After Testing**

Based on test results:

1. **If basic UI works**: Focus on improving OpenJPEG integration
2. **If login flow works**: Begin simulator connection implementation  
3. **If textures load**: Start world rendering components
4. **If everything works**: Move to Phase 2 enhancements

Remember: The goal is **basic functionality first**, then enhancement!