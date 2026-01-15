# Debug Log Upload System

## Overview

The Linkpoint app now includes an automatic debug log upload system that uploads application logs and crash reports to GitHub for review by copilot. This system **only operates in debug builds** and is completely disabled in release builds.

## Features

### 🚀 Automatic Upload
- Logs are automatically uploaded every hour (for debug builds only)
- Uploads happen in the background without user intervention
- First upload occurs on app startup if more than 1 hour has passed since last upload

### 📤 Manual Upload
- **Login Screen**: Debug builds show a "📤 Upload Debug Logs" button
- **Modern Demo**: The "Export Logs" button now uploads to GitHub instead of just logging to logcat
- Both trigger immediate log upload for debugging purposes

### 💥 Crash Reporting  
- Automatic crash report upload when application initialization fails
- Includes stack trace, device information, and application status
- Helps identify and fix critical startup issues

## How It Works

### 1. Log Collection
The system collects:
- **Device Information**: Manufacturer, model, Android version, API level
- **Application Status**: Startup status, component initialization results
- **Memory Information**: Heap usage, memory pressure indicators  
- **Build Information**: Version, build type, repository information
- **System Information**: Device ID (anonymized), timestamps

### 2. GitHub Integration
- Uploads logs as **private GitHub Gists** 
- Uses GitHub API without requiring authentication tokens
- Each log file has a descriptive filename with timestamp and device ID
- Easy to find uploaded logs with the tag `LINKPOINT_DEBUG_UPLOAD`

### 3. Privacy & Security
- **Debug builds only** - completely disabled in release builds
- Private Gists (not publicly accessible)  
- Anonymized device identifiers
- No sensitive user data collected
- Automatic size limits (50KB max per upload)

## Usage

### For Developers
```bash
# Monitor log uploads in logcat
adb logcat AutoLogUploader:* LINKPOINT_DEBUG_UPLOAD:* *:S

# Look for these messages:
# I/AutoLogUploader: 🚀 Initializing automatic log upload for debug build
# I/AutoLogUploader: ✅ Log uploaded successfully!
# I/LINKPOINT_DEBUG_UPLOAD: LOG_UPLOADED: https://gist.github.com/[gist-id]
```

### For Copilot Review
1. **Automatic Uploads**: Check for Gists created by "Linkpoint-Debug-App" 
2. **Manual Uploads**: Triggered by users pressing upload buttons in the app
3. **Crash Reports**: Automatically uploaded when critical errors occur
4. **Log Format**: Structured logs with device info, app status, and system metrics

## Log Content Example

```
=== LINKPOINT DEBUG LOG UPLOAD ===
Timestamp: 2024-09-27 08:36:42 UTC
Version: 3.4.3
Build Type: DEBUG
Repository: https://github.com/Kaleaon/Linkpoint

=== DEVICE INFORMATION ===
Manufacturer: Samsung
Model: SM-G991B
Android Version: 13 (API 33)
Build ID: TP1A.220624.014
Device ID: SMG991B_a1b2c3

=== APPLICATION STATUS ===
Lumiya Application Status:
- Context: OK
- Modern Components: Safe Mode
- Running in Safe Mode - basic functionality only

=== MEMORY INFORMATION ===
Max Memory: 256 MB
Total Memory: 64 MB  
Used Memory: 32 MB
Free Memory: 32 MB
Memory Usage: 50.0%

=== LOG NOTES ===
This is an automated debug build log upload.
Recent application logs are available in Android logcat with tags:
- LumiyaApp: Application lifecycle and startup
- ModernLinkpointDemo: Modern component initialization
- CleanLoginActivity: Login screen activity
- ModernMainActivity: Main demo activity
- AutoLogUploader: This log upload system

To view recent logs: adb logcat LumiyaApp:* ModernLinkpointDemo:* AutoLogUploader:* *:S

=== END OF LOG ===
```

## Recent Debug Report Sample (2026-01-15)

```
╔══════════════════════════════════════════════════════════════════╗
║               LINKPOINT DEBUG REPORT                              ║
╚══════════════════════════════════════════════════════════════════╝

Timestamp: 2026-01-15 15:35:28.144 -0600
Report ID: 0583bec0-8b41-4592-82fb-8aa63a36abd8

┌──────────────────────────────────────────────────────────────────┐
│ USER NOTE                                                         │
└──────────────────────────────────────────────────────────────────┘

Manual capture from Settings

┌──────────────────────────────────────────────────────────────────┐
│ CONNECTION STATUS                                                 │
└──────────────────────────────────────────────────────────────────┘

Connected: true
Current Region: Unknown
Agent ID: f496d6bf-8235-4ebf-bd56-4f7f0464a27a
Avatar Name: Kaleaon Resident
Connection State: CONNECTED

┌──────────────────────────────────────────────────────────────────┐
│ NETWORK ACTIVITY & PACKET STATUS                                  │
└──────────────────────────────────────────────────────────────────┘

HTTP Requests: 2
HTTP Responses: 2
Errors: 0
Warnings: 0
Retries: 0
Timeouts: 0
Redirects: 0

No recent network errors detected

┌──────────────────────────────────────────────────────────────────┐
│ CACHE STATISTICS                                                  │
└──────────────────────────────────────────────────────────────────┘

Total Cache Size: 0.0 KB / 4.0 GB (0%)
Total Files: 0
Available Space: 364.14 GB
Low Space Warning: No

Cache Breakdown:
  Textures: 0 B (0 files)
  Meshes: 0 B (0 files)
  Sounds: 0 B (0 files)
  Animations: 0 B (0 files)
  General: 0 B (0 files)

┌──────────────────────────────────────────────────────────────────┐
│ ASSET CACHE MEMORY                                                │
└──────────────────────────────────────────────────────────────────┘

Memory Cache:
  Size: 0 B / 512.00 MB
  Hit Count: 0
  Miss Count: 0
  Hit Rate: 0.0%

Disk Cache:
  Size: 0 B
  Asset Count: 0

┌──────────────────────────────────────────────────────────────────┐
│ TEXTURE LOADING STATUS                                            │
└──────────────────────────────────────────────────────────────────┘

Pending Downloads: 0
Downloaded: 0
Downloaded Bytes: 0 B
Failed Downloads: 0
Decoded: 0
Decode Failures: 0

┌──────────────────────────────────────────────────────────────────┐
│ UDP CONNECTION STATUS (Simulator Protocol)                        │
└──────────────────────────────────────────────────────────────────┘

UDP Connected: true
Simulator IP: 18.237.183.71
Simulator Port: 13028
Circuit Code: 607348304
Socket Open: true
Receive Loop Active: true

Packet Statistics:
  Sequence Number (packets sent): 2
  Pending ACKs: 2
  Registered Handlers: 6

Registered Message Handlers:
  - AVATAR_ANIMATION
  - AGENT_MOVEMENT_COMPLETE
  - REGION_HANDSHAKE
  - CHAT_FROM_SIMULATOR
  - OBJECT_UPDATE
  - OBJECT_UPDATE_COMPRESSED

Pending Packets (awaiting ACK):
  - Seq 0: 2 retries, 162ms old
  - Seq 1: 2 retries, 162ms old

┌──────────────────────────────────────────────────────────────────┐
│ CAPABILITY STATUS (HTTP Services)                                 │
└──────────────────────────────────────────────────────────────────┘

Capabilities Ready: true
Total Capabilities: 12
Seed Capability: https://simhost-0f86877b1547cafa3.agni.secondlife....

Initialization Status:
  Completed: true
  Duration: 469ms
  Attempts: 1

Critical Capabilities:
  GetTexture: ✓ Available
  GetMesh: ✓ Available
  FetchInventory: ✓ Available
  EventQueue: ✓ Available

Event Queue:
  Active: true
  Registered Event Handlers: 3
  Event Types: ChatterBoxInvitation, ChatterBoxSessionEventReply, ChatterBoxSessionStartReply

All Available Capabilities:
  - AgentState
  - AvatarPickerSearch
  - ChatSessionRequest
  - EnvironmentSettings
  - EventQueueGet
  - ExtEnvironment
  - FetchInventory2
  - FetchInventoryDescendents2
  - FetchLib2
  - GetMesh
  - GetMesh2
  - GetTexture

┌──────────────────────────────────────────────────────────────────┐
│ NETWORK QUALITY                                                   │
└──────────────────────────────────────────────────────────────────┘

Quality Level: POOR
Network Connected: true
Network Type: WIFI
Average Latency: 6353ms
Estimated Bandwidth: 38619 kbps
Error Rate: 0.0%
Latency Samples: 1
Timeout Multiplier: 2.0x

⚠️ Poor network quality - connection issues likely!

┌──────────────────────────────────────────────────────────────────┐
│ NETWORK STATE                                                     │
└──────────────────────────────────────────────────────────────────┘

Connection Status: CONNECTED
Is Reconnecting: false
Connection Faulted: false
Reset Requested: false
Force Reconnect: false
Always Reconnect: true
Logout In Progress: false
Reconnect Count: 0
Connection Duration: 33.5s
Last Status Change: 10.2s ago
Connection Instance ID: 946d314a

┌──────────────────────────────────────────────────────────────────┐
│ OBJECT MANAGER STATUS                                             │
└──────────────────────────────────────────────────────────────────┘

Total Objects in Scene: 0
Objects by UUID: 0
Selected Objects: 0
Is Editing: false
Edit Mode: POSITION
Recently Updated (last 5s): 0
Scripted Objects: 0
Physical Objects: 0

⚠️ NO OBJECTS IN SCENE - World may not be loading!

┌──────────────────────────────────────────────────────────────────┐
│ AVATAR MANAGER STATUS                                             │
└──────────────────────────────────────────────────────────────────┘

Total Avatars in Scene: 0
My Agent ID: f496d6bf-8235-4ebf-bd56-4f7f0464a27a
My Avatar Loaded: false
Recently Updated (last 5s): 0
Flying: 0
Sitting: 0
Typing: 0

⚠️ NO AVATARS IN SCENE - Avatar data may not be loading!

┌──────────────────────────────────────────────────────────────────┐
│ INVENTORY STATUS                                                  │
└──────────────────────────────────────────────────────────────────┘

Folders Cached: 0
Items Cached: 0
Root Folder ID: Not set
System Folders: 0
Currently Loading: false
Current Folder: None

┌──────────────────────────────────────────────────────────────────┐
│ REGION DETAILS                                                    │
└──────────────────────────────────────────────────────────────────┘

Region Name: Unknown
Region Handle: 0
Position: (128, 128)
Sim IP: 18.237.183.71
Sim Port: 13028
Seed Capability: https://simhost-0f86877b1547cafa3.agni.secondlife....

⚠️ REGION NAME UNKNOWN - RegionHandshake may not have been received!

┌──────────────────────────────────────────────────────────────────┐
│ MESH MANAGER STATUS                                               │
└──────────────────────────────────────────────────────────────────┘

Pending Downloads: 0
Downloaded: 0
Downloaded Bytes: 0 B
Download Failed: 0
Parse Failed: 0
Has Mesh Capability: ✓ Yes

┌──────────────────────────────────────────────────────────────────┐
│ TEXTURE MANAGER DETAILED STATUS                                   │
└──────────────────────────────────────────────────────────────────┘

Has Texture Capability: ✓ Yes
Pending Downloads: 0
Downloaded: 0
Downloaded Bytes: 0 B
Download Failed: 0
Decoded: 0
Decode Failed: 0

Cache Status:
  Cached Textures: 0
  Pending Requests: 0
  Download Queue: 0
  Active Downloads: 0

JPEG2000 Decoding:
  Attempts: 0
  Successes: 0

┌──────────────────────────────────────────────────────────────────┐
│ RENDER MANAGER STATUS (Filament)                                  │
└──────────────────────────────────────────────────────────────────┘

Initialized: true
XR Mode: false
Viewport: 1080 x 2126
Frame Count: 334
Time Since Last Frame: 4.3s

Filament Components:
  Engine: ✓
  Renderer: ✓
  Scene: ✓
  View: ✓
  Camera: ✓
  SwapChain: ✗

Initialization Time: 2026-01-15 15:35:18.117 -0600

⚠️ NO SWAP CHAIN - Rendering not visible!

┌──────────────────────────────────────────────────────────────────┐
│ DEVICE INFORMATION                                                │
└──────────────────────────────────────────────────────────────────┘

Manufacturer: Google
Model: Pixel 10 Pro XL
Device: mustang
Android Version: 16
SDK Version: 36
Build ID: BP4A.251205.006.E1

┌──────────────────────────────────────────────────────────────────┐
│ APP INFORMATION                                                   │
└──────────────────────────────────────────────────────────────────┘

Package: com.linkpoint.debug
Version: 1.0.0-DEBUG
Version Code: 1

┌──────────────────────────────────────────────────────────────────┐
│ MEMORY USAGE                                                      │
└──────────────────────────────────────────────────────────────────┘

Total Memory: 38.43 MB
Used Memory: 16.59 MB
Free Memory: 21.84 MB
Max Memory: 512.00 MB
Memory Usage: 3%

┌──────────────────────────────────────────────────────────────────┐
│ XR STATUS                                                         │
└──────────────────────────────────────────────────────────────────┘

XR Available: false

┌──────────────────────────────────────────────────────────────────┐
│ CRASH REPORTER STATUS                                             │
└──────────────────────────────────────────────────────────────────┘

Status: INITIALIZED_WITH_EXTERNAL
Crash Logs: 0
Storage: Primary: /data/user/0/com.linkpoint.debug/files/crash_logs, External: /storage/emulated/0/Android/data/com.linkpoint.debug/files/Download/Lumiya Logs (Status: INITIALIZED_WITH_EXTERNAL)

┌──────────────────────────────────────────────────────────────────┐
│ THREAD INFORMATION                                                │
└──────────────────────────────────────────────────────────────────┘

Active Thread Count: 54
Current Thread: DefaultDispatcher-worker-4

┌──────────────────────────────────────────────────────────────────┐
│ INITIALIZATION TIMELINE                                           │
└──────────────────────────────────────────────────────────────────┘

Session Duration: 16.6s
Current Phase: UDP_CONNECTED
Total Events: 14
Warnings: 0
Errors: 0

Completed Phases:
  ✓ LOGIN_HTTP_REQUEST
  ✓ CAPABILITIES_FETCHING
  ✓ SESSION_SETUP
  ✓ UDP_CONNECTING
  ✓ LOGIN_SUCCESS

Pending Phases:
  ⏳ LOGIN_STARTING
  ⏳ CAPABILITIES_READY
  ⏳ UDP_CONNECTED

Recent Events (last 20):
[0ms] • Session tracking started
[0ms] ▶ LOGIN_STARTING: Login for Kaleaon Resident
[17ms] ▶ LOGIN_HTTP_REQUEST: Sending login request
[6372ms] ✓ LOGIN_HTTP_REQUEST: Login response received (6355ms)
[6372ms] ▶ LOGIN_SUCCESS: Processing login success
[6373ms] ▶ SESSION_SETUP: Setting up session
[6379ms] ✓ SESSION_SETUP: Session and managers initialized (6ms)
[6379ms] ▶ UDP_CONNECTING: Connecting to 18.237.183.71:13028
[6379ms] ▶ CAPABILITIES_FETCHING: Fetching capabilities from seed
[6379ms] ✓ LOGIN_SUCCESS: Login completed, waiting for world data (7ms)
[6854ms] ✓ CAPABILITIES_FETCHING: 12 capabilities loaded (475ms)
[6854ms] ▶ CAPABILITIES_READY: Capabilities available for use
[6888ms] ✓ UDP_CONNECTING: UDP connected (509ms)
[6888ms] ▶ UDP_CONNECTED: Waiting for simulator messages

┌──────────────────────────────────────────────────────────────────┐
│ RECENT NETWORK LOG (Last 30 entries)                              │
└──────────────────────────────────────────────────────────────────┘

=== Network Activity Log ===
Log Level: DEBUG
Total Entries: 9
Showing last 30 entries:

[2026-01-15 15:35:11.623] [DEBUG] [PROTOCOL]
📡 Protocol: Second Life Login - Grid: https://login.agni.lindenlab.com/cgi-bin/login.cgi, User: Kaleaon Resident, Start: last

[2026-01-15 15:35:11.638] [INFO] [AUTH]
🔑 Auth: Password Hash Generation
  originalLength: 16
  truncatedLength: 16
  hashFormat: ***REDACTED***

[2026-01-15 15:35:11.639] [INFO] [AUTH]
🔑 Auth: Login Attempt
  loginUri: https://login.agni.lindenlab.com/cgi-bin/login.cgi
  requestLength: 2483 bytes

[2026-01-15 15:35:11.645] [DEBUG] [HTTP_REQ]
→ POST https://login.agni.lindenlab.com/cgi-bin/login.cgi
Headers:
  Content-Type: text/xml
  Accept: text/xml, application/xml
  User-Agent: Linkpoint/1.0.0 (Android)
Content-Length: 2483 bytes
Content-Type: text/xml

[2026-01-15 15:35:11.646] [DEBUG] [HTTP_REQ]
→ POST https://login.agni.lindenlab.com/cgi-bin/login.cgi
Headers:
  Content-Type: text/xml
  Accept: text/xml, application/xml
  User-Agent: Linkpoint/1.0.0 (Android)
Content-Length: 2483 bytes
Content-Type: text/xml

[2026-01-15 15:35:17.139] [DEBUG] [HTTP_RESP]
← 200 OK (5493ms)
URL: https://login.agni.lindenlab.com/cgi-bin/login.cgi
Protocol: http/1.1
Headers:
  Content-Type: application/llsd+xml
  X-LL-Request-Id: aWldi1WYSzDkEDO8NIJmdQAAAcA
  X-Frame-Options: SAMEORIGIN
  X-XSS-Protection: 1; mode=block
  X-Content-Type-Options: nosniff
  Expires: Thu, 15 Jan 2026 21:35:12 GMT
  Cache-Control: max-age=0, no-cache, no-store
  Pragma: no-cache
  Date: Thu, 15 Jan 2026 21:35:12 GMT
  Transfer-Encoding: chunked
  Connection: close
  Connection: Transfer-Encoding
Content-Type: application/llsd+xml

[2026-01-15 15:35:17.633] [DEBUG] [HTTP_RESP]
← 200 OK (5986ms)
URL: https://login.agni.lindenlab.com/cgi-bin/login.cgi
Protocol: http/1.1
Headers:
  Content-Type: application/llsd+xml
  X-LL-Request-Id: aWldi1WYSzDkEDO8NIJmdQAAAcA
  X-Frame-Options: SAMEORIGIN
  X-XSS-Protection: 1; mode=block
  X-Content-Type-Options: nosniff
  Expires: Thu, 15 Jan 2026 21:35:12 GMT
  Cache-Control: max-age=0, no-cache, no-store
  Pragma: no-cache
  Date: Thu, 15 Jan 2026 21:35:12 GMT
  Transfer-Encoding: chunked
  Connection: close
  Connection: Transfer-Encoding
Content-Type: application/llsd+xml

[2026-01-15 15:35:17.995] [INFO] [AUTH]
🔑 Auth: Login Success
  agentId: f496d6bf-8235-4ebf-bd56-4f7f0464a27a
  sessionId: ***REDACTED***
  simIp: 18.237.183.71
  simPort: 13028

[2026-01-15 15:35:18.001] [DEBUG] [PROTOCOL]
📡 Protocol: Login Complete - Successfully connected to 18.237.183.71:13028

═══════════════════════════════════════════════════════════════════
End of Debug Report
═══════════════════════════════════════════════════════════════════
```

## Configuration

The system is configured in `AutoLogUploader.java`:

```java
private static final long UPLOAD_INTERVAL_MS = TimeUnit.HOURS.toMillis(1); // Upload every hour
private static final int MAX_LOG_SIZE = 50000; // 50KB limit
```

## Integration Points

### LumiyaApp.java
- Initializes AutoLogUploader on app startup
- Integrates crash reporting with application initialization errors
- Provides static methods for manual log uploads

### CleanLoginActivity.java  
- Adds debug upload button (debug builds only)
- Shows app status information
- Allows manual log upload from login screen

### ModernMainActivity.java
- "Export Logs" button now uploads to GitHub
- Integrated with existing log export functionality

## Troubleshooting

### Common Issues
1. **No uploads happening**: Check if it's a debug build (`BuildConfig.DEBUG`)
2. **Network errors**: Ensure device has internet connectivity
3. **GitHub API limits**: Gist creation may be rate-limited

### Debug Commands
```bash
# Check if auto upload is working
adb logcat | grep "AutoLogUploader"

# Look for successful uploads  
adb logcat | grep "LINKPOINT_DEBUG_UPLOAD"

# Monitor manual upload triggers
adb logcat | grep "Manual log upload requested"
```

## Benefits for Development

1. **Automatic Issue Detection**: Crashes and errors are automatically reported
2. **Remote Debugging**: Get logs from users without requiring manual log collection
3. **Performance Monitoring**: Track memory usage and component initialization
4. **Version Tracking**: Each log includes version and build information
5. **Device Compatibility**: Understand issues across different Android versions

This system provides comprehensive debugging capabilities while maintaining user privacy and only operating in debug builds.
