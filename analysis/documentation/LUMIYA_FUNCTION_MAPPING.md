# Lumiya APK Function Mapping for Linkpoint

## Overview
This document maps all functions from Lumiya 3.4.2 APK to Linkpoint implementation.

## Package Structure: com.lumiyaviewer.lumiya

### Core Protocol Layers

#### 1. Authentication (slproto/auth)
```
SLAuth.java - Password hashing and login
SLAuthManager.java - Session management
LoginRequest.java - XML-RPC login request builder
LoginResponse.java - XML-RPC login response parser
```

#### 2. Network Communication (slproto/https)
```
SLNetwork.java - HTTP client for login server
SLHttpsClient.java - HTTPS connection wrapper
SSLHelper.java - SSL/TLS configuration
```

#### 3. UDP Protocol (slproto/)
```
SLAgentCircuit.java - UDP circuit manager
SLPacketCodec.java - Packet encoding/decoding
SLMessageHandler.java - Message dispatch system
SLProtocol.java - Main protocol coordinator
```

#### 4. Capabilities (slproto/caps)
```
SLCaps.java - Capability manager
SLCapsHTTP.java - HTTP-based capability requests
SLCapsManager.java - Capability lifecycle
```

### Module Mapping

#### A. Inventory System (slproto/inventory)
```
InventoryManager.java - Main inventory controller
InventoryFolder.java - Folder representation
InventoryItem.java - Item representation
InventoryFetcher.java - Fetch inventory from server
InventoryDownloadManager.java - Download inventory data
```

#### B. Object System (slproto/objects)
```
ObjectManager.java - Main object controller
ObjectUpdate.java - Process object updates
ObjectPrim.java - Primitive object representation
ObjectMesh.java - Mesh object representation
ObjectTexture.java - Texture management
```

#### C. Avatar System (slproto/avatar)
```
AvatarManager.java - Main avatar controller
AvatarAppearance.java - Avatar appearance data
AvatarBaker.java - Texture baking for avatars
AvatarAnimation.java - Animation playback
AvatarMovement.java - Movement logic
```

#### D. Chat & IM (slproto/chat)
```
ChatManager.java - Chat controller
ChatMessage.java - Message representation
ChatHistory.java - Message history
InstantMessage.java - IM handler
IMSession.java - IM session management
```

#### E. Rendering (render/)
```
Renderer.java - Main renderer
RendererGL.java - OpenGL renderer
RendererCore.java - Core rendering functions
TerrainRenderer.java - Terrain rendering
MeshRenderer.java - Mesh rendering
AvatarRenderer.java - Avatar rendering
TextureManager.java - Texture management
ShaderManager.java - Shader compilation
```

#### F. Media (media/)
```
MediaPlayer.java - Media playback
MediaManager.java - Media controller
StreamPlayer.java - Stream handling
VoicePlayer.java - Voice playback
```

### Special Modules

#### G. RLV (slproto/modules/rlv)
```
RLVManager.java - RLV restriction manager
RLVCommand.java - Command parser
RLVCommands/*.java - Individual command handlers
RLVRestriction.java - Restriction state
```

#### H. Voice (slproto/modules/voice)
```
VoiceManager.java - Voice chat manager
VoiceSession.java - Voice session
VoiceCodec.java - Codec handling
VoiceConnector.java - SIP connection
```

#### I. Finance (slproto/modules/finance)
```
FinanceManager.java - L$ balance management
TransactionManager.java - Transaction history
```

#### J. Groups (slproto/modules/groups)
```
GroupManager.java - Group membership
GroupInfo.java - Group details
```

### UI Components

#### K. User Interface (ui/)
```
MainActivity.java - Main activity
LoginActivity.java - Login screen
WorldViewActivity.java - 3D world view
SettingsActivity.java - Settings
ChatActivity.java - Chat interface
InventoryActivity.java - Inventory UI
ProfileActivity.java - Profile UI
```

## Linkpoint Implementation Status

### ✅ Fully Implemented
- [x] Authentication (slproto/auth)
- [x] Network Communication (slproto/https)
- [x] UDP Protocol (slproto/)
- [x] Capabilities (slproto/caps)
- [x] Basic Inventory (slproto/inventory)
- [x] Basic Objects (slproto/objects)
- [x] Basic Avatar (slproto/avatar)
- [x] Chat & IM (slproto/chat)

### 🔄 Partially Implemented
- [ ] Rendering (render/) - Filament integration in progress
- [ ] Media (media/) - Basic streaming
- [ ] Voice (slproto/modules/voice) - SIP integration needed

### ❌ Not Yet Implemented
- [ ] RLV (slproto/modules/rlv)
- [ ] Finance (slproto/modules/finance)
- [ ] Groups (slproto/modules/groups)
- [ ] Search (slproto/modules/search)
- [ ] Outfits (ui/outfits)

### 🚧 Needs Enhancement
- [ ] Texture fetching (slproto/modules/texfetcher)
- [ ] Mesh handling (slproto/mesh)
- [ ] Terrain rendering (slproto/terrain)
- [ ] Windlight (slproto/windlight)

## Key Files to Analyze

### Critical Protocol Files
1. `SLAgentCircuit.java` - UDP connection and packet handling
2. `SLAuth.java` - Login authentication
3. `SLCaps.java` - Capability management
4. `SLPacketCodec.java` - Packet encoding/decoding

### Rendering Files
1. `RendererGL.java` - OpenGL rendering
2. `TextureManager.java` - Texture loading
3. `ShaderManager.java` - Shader management
4. `TerrainRenderer.java` - Terrain rendering

### UI Files
1. `MainActivity.java` - Main activity lifecycle
2. `LoginActivity.java` - Login flow
3. `WorldViewActivity.java` - 3D view management

## Next Steps

1. Extract smali code for critical files
2. Compare with Linkpoint implementation
3. Identify missing functions
4. Document differences
5. Create implementation plan for missing features

## References

- Second Life Protocol: https://wiki.secondlife.com/wiki/Protocol
- LLSD Format: https://wiki.secondlife.com/wiki/LLSD
- RLV Protocol: https://wiki.secondlife.com/wiki/Restrained_Life_API
- Firestorm Viewer: https://github.com/FirestormViewer/firestorm