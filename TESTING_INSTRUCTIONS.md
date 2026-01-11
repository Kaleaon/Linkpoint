# How to Test Linkpoint Login Fix

## Prerequisites

1. **Android SDK** installed and configured
2. **Android Emulator** or physical device
3. **ADB** (Android Debug Bridge) available
4. **Java 17+** for building

## Building the App

```bash
cd /home/runner/work/Linkpoint/Linkpoint/Linkpoint

# Build debug APK
./gradlew assembleDebug

# APK will be at:
# app/build/outputs/apk/debug/app-debug.apk
```

## Installing on Device/Emulator

```bash
# List connected devices
adb devices

# Install the APK
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Or if multiple devices, specify device:
adb -s <device_id> install -r app/build/outputs/apk/debug/app-debug.apk
```

## Testing Login

### Test Credentials
- **First Name**: Kaleaon
- **Last Name**: Resident
- **Password**: SealofRassilon02
- **Grid**: Second Life (default)
- **Start Location**: Last Location (default)

### Steps
1. Launch Linkpoint app
2. Accept Terms of Service if prompted
3. Enter credentials:
   - First Name: `Kaleaon`
   - Last Name: `Resident`
   - Password: `SealofRassilon02`
4. Ensure "Second Life" grid is selected
5. Tap "Login" button

### Monitoring Logs

In a separate terminal, monitor the logs:

```bash
# Watch all Linkpoint logs
adb logcat -s SLProtocol:D CoreNetworking:D LoginActivity:D NetworkDiagnostics:D

# Or filter for specific tags
adb logcat | grep -E "SLProtocol|CoreNetworking|LoginActivity"
```

### Expected Log Output (Success)

```
D/LoginActivity: Starting login to https://login.agni.lindenlab.com/cgi-bin/login.cgi via WIFI
D/SLProtocol: Attempting login for Kaleaon Resident
D/SLProtocol: Login details - URI: https://login.agni.lindenlab.com/cgi-bin/login.cgi, 
              firstName: Kaleaon, lastName: Resident, 
              passwordLen: 16, truncatedLen: 16, startLoc: last
D/CoreNetworking: Login request to: https://login.agni.lindenlab.com/cgi-bin/login.cgi
D/CoreNetworking: Executing login request to: https://login.agni.lindenlab.com/cgi-bin/login.cgi
D/CoreNetworking: Login successful: session=509c****, agent=f496d6bf-8235-4ebf-bd56-4f7f0464a27a
I/SLProtocol: Login successful!
```

### Expected Behavior (Success)

1. **Login Screen**:
   - Progress bar appears
   - Status text shows "Logging into Second Life..."

2. **After Login**:
   - App transitions to WorldViewActivity
   - User should be connected to Second Life
   - Chat interface should load

### Troubleshooting Failed Login

If login fails, check logs for:

#### Network Issues
```
E/CoreNetworking: Login error: UnknownHostException: login.agni.lindenlab.com
```
**Solution**: Check internet connectivity

#### Invalid Credentials
```
W/CoreNetworking: Login failed: Invalid login credentials (reason: key)
```
**Solution**: Verify username and password are correct

#### Server Error
```
E/CoreNetworking: Login failed: Server returned HTTP 503
```
**Solution**: Second Life servers may be down, retry later

#### SSL/TLS Error
```
E/CoreNetworking: Login error: SSLHandshakeException
```
**Solution**: 
- Check device date/time is correct
- Ensure Android API level 21+
- Check network security configuration

#### Timeout
```
E/CoreNetworking: Login error: SocketTimeoutException
```
**Solution**:
- Check network speed
- Retry on better connection
- May indicate server issues

## Comparing with Lumiya

The reference Lumiya APK is also available for comparison testing:

```bash
# Install Lumiya for comparison
adb install /tmp/Lumiya_3.4.2.apk

# Test login with same credentials
# Should succeed if network is working
```

## Manual Verification Checklist

- [ ] App builds without errors
- [ ] APK installs successfully
- [ ] App launches without crashes
- [ ] Login screen displays correctly
- [ ] Can enter credentials
- [ ] Login button triggers login attempt
- [ ] Progress indicator appears during login
- [ ] Success: Transitions to WorldViewActivity
- [ ] Logs show "Login successful!"
- [ ] Session ID and Agent ID are obtained

## Known Issues

1. **Password Truncation**: Now fixed - passwords are correctly truncated to 16 chars
2. **Network Timeouts**: Comprehensive retry logic with exponential backoff implemented
3. **SSL Issues**: Enhanced error handling with detailed diagnostics

## Additional Testing

### Test Different Password Lengths

```
Short: "test123" (7 chars)
Medium: "mypassword123" (13 chars)
Exact: "SealofRassilon02" (16 chars)
Long: "ThisIsAVeryLongPassword123" (26 chars - will be truncated to 16)
```

All should work if credentials are valid in Second Life.

### Test Different Start Locations

- "last" - Last location
- "home" - Home location
- "uri:Region Name/128/128/23" - Specific location

### Test Different Grids

- Second Life (Main Grid)
- Second Life Beta (Aditi)
- Kitely
- Custom OpenSim grids

## Success Criteria

✅ Login succeeds with test credentials
✅ Session ID and Agent ID obtained
✅ No crashes during login process
✅ Proper error handling for network issues
✅ Logs show correct password length handling
✅ App transitions to main world view after login

## References

- **Login Analysis**: `LOGIN_CONNECTION_ANALYSIS.md`
- **Test Script**: `/tmp/test_sl_login.sh` (curl-based login test)
- **Lumiya APK**: `/tmp/Lumiya_3.4.2.apk` (reference implementation)
- **Password Tests**: `/tmp/test_password_hashing.sh`
