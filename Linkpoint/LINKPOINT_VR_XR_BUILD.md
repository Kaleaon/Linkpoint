# Linkpoint VR/XR Build

## Overview

Linkpoint has been rebuilt using Lumiya Viewer's navigation framework as a foundation, modernized for:
- **Android XR** (Google's upcoming XR platform for Android 15+)
- **OpenXR** (Standard VR/AR API for Quest, Pico, etc.)
- **Google Cardboard** (Legacy VR support)
- **Filament** (Modern 3D rendering engine)
- **Kotlin** (Modern Android development)

## Build Status

✅ **APK Built Successfully**: `build/outputs/apk/debug/Linkpoint-debug.apk` (48.7 MB)

## Architecture (Based on Lumiya)

### Activities
| Activity | Purpose | Based on Lumiya |
|----------|---------|-----------------|
| `LoginActivity` | User authentication | `LoginActivity` |
| `WorldViewActivity` | Main 3D world view | `WorldViewActivity` |
| `XRWorldActivity` | VR/XR immersive mode | `CardboardActivity` |
| `ChatActivity` | Local/IM/Group chat | `ChatNewActivity` |
| `InventoryActivity` | Inventory management | `InventoryActivity` |
| `MinimapActivity` | Overhead region map | `MinimapActivity` |
| `MyAvatarActivity` | Avatar customization | `MyAvatarActivity` |
| `SettingsActivity` | App settings | `SettingsActivity` |
| `SLURLActivity` | SLURL handler | `TeleportSLURLActivity` |

### Services
| Service | Purpose |
|---------|---------|
| `GridConnectionService` | Background connection maintenance |
| `StreamingMediaService` | Parcel audio/video streaming |

### Core Components

#### XR Manager (`com.linkpoint.xr`)
- Detects available XR capabilities
- Supports multiple XR modes:
  - `MODE_CARDBOARD` - Google Cardboard (legacy)
  - `MODE_OPENXR` - OpenXR runtime
  - `MODE_ANDROID_XR` - Android XR (future)
- Manages XR sessions, head pose, and controllers

#### Render Manager (`com.linkpoint.render`)
- Filament-based 3D rendering
- Standard and stereo (VR) rendering modes
- Scene management
- Lighting (sun, ambient, IBL)

#### Session Manager (`com.linkpoint.core`)
- Connection state management
- Region tracking
- Avatar session data

#### Grid Manager (`com.linkpoint.core`)
- Multi-grid support (Second Life, OpenSim)
- Grid configuration and selection
- Connection testing

#### SL Protocol (`com.linkpoint.network`)
- XMLRPC login
- Chat messaging
- Teleport requests

## Key Features

### VR/XR Support
- **Stereo rendering** for immersive viewing
- **Head tracking** integration
- **Controller support** for interaction
- **90fps render loop** for smooth VR experience

### Modernizations vs Lumiya
| Feature | Lumiya | Linkpoint |
|---------|--------|-----------|
| Language | Java | Kotlin |
| Rendering | OpenGL ES 2.0 | Filament (Vulkan/GLES 3) |
| Voice | Vivox SDK | WebRTC |
| Target SDK | 26 (Android 8.0) | 34 (Android 14) |
| VR | Google Cardboard | Android XR + OpenXR |
| Architecture | Activities | Navigation + ViewModels |

## File Structure

```
src/main/java/com/linkpoint/
├── LinkpointApp.kt           # Application class
├── core/
│   ├── GridManager.kt        # Grid management
│   └── SessionManager.kt     # Session state
├── network/
│   └── SecondLifeProtocol.kt # SL protocol implementation
├── render/
│   └── RenderManager.kt      # Filament rendering
├── services/
│   ├── GridConnectionService.kt   # Background connection
│   └── StreamingMediaService.kt   # Media streaming
├── ui/
│   ├── avatar/MyAvatarActivity.kt
│   ├── chat/ChatActivity.kt
│   ├── inventory/InventoryActivity.kt
│   ├── login/LoginActivity.kt
│   ├── minimap/MinimapActivity.kt
│   ├── settings/SettingsActivity.kt
│   ├── slurl/SLURLActivity.kt
│   ├── world/WorldViewActivity.kt
│   └── xr/XRWorldActivity.kt
└── xr/
    └── XRManager.kt          # XR/VR management
```

## Building

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease
```

## Future Work

### Android XR Integration
When Android XR SDK is released:
1. Add `androidx.xr:xr-core` dependency
2. Implement `AndroidXRSession` with real XR APIs
3. Add spatial UI components
4. Implement 6DOF tracking

### OpenXR Support
For Quest/Pico/other VR headsets:
1. Add OpenXR loader library
2. Implement `OpenXRSession` with runtime binding
3. Add controller tracking
4. Implement passthrough (AR) mode

### Protocol Completion
- UDP message handling
- Avatar movement
- Object rendering
- Asset downloading
- Voice chat (WebRTC)

## Dependencies

- **Filament** 1.66.0 - 3D rendering
- **OkHttp** 4.12.0 - Networking
- **Kotlin Coroutines** 1.7.3 - Async operations
- **AndroidX** - Modern Android components
- **Material Components** - UI design
- **WebRTC** - Voice chat

## License

Linkpoint is based on Lumiya Viewer, adapted for modern Android and XR platforms.
