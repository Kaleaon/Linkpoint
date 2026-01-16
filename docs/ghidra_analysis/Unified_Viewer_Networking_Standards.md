# Unified Second Life Viewer Networking Standards

## Overview

This document establishes unified networking standards for Second Life viewers, derived from analysis of:

1. **Official Second Life APK** (decompiled via Ghidra)
2. **Lumiya Viewer** (mobile reference implementation)
3. **Firestorm Viewer** (popular third-party viewer)
4. **LibreMetaverse** (C# protocol library)
5. **OpenSimulator** (compatible server implementation)

These standards provide a comprehensive reference for Linkpoint development and validation.

---

## Table of Contents

1. [Protocol Standards](#1-protocol-standards)
2. [Authentication Standards](#2-authentication-standards)
3. [LLUDP Protocol](#3-lludp-protocol)
4. [HTTP Capabilities (CAPS)](#4-http-capabilities-caps)
5. [Asset Protocol](#5-asset-protocol)
6. [Inventory Protocol](#6-inventory-protocol)
7. [Chat and Messaging](#7-chat-and-messaging)
8. [Mobile-Specific Standards](#8-mobile-specific-standards)
9. [Security Standards](#9-security-standards)
10. [Implementation Checklist](#10-implementation-checklist)

---

## 1. Protocol Standards

### 1.1 Core Protocol Layers

```
┌─────────────────────────────────────────────────────┐
│                 Application Layer                    │
│  (Chat, Inventory, Avatar, Objects, Terrain, etc.)   │
├─────────────────────────────────────────────────────┤
│              Capability Layer (CAPS)                 │
│    (HTTP-based feature negotiation and requests)     │
├─────────────────────────────────────────────────────┤
│               Transport Layer                        │
│   ┌──────────────────┐  ┌──────────────────────────┐│
│   │   LLUDP (UDP)    │  │    HTTP/HTTPS           ││
│   │ Real-time data   │  │  Assets, CAPS, Login    ││
│   └──────────────────┘  └──────────────────────────┘│
├─────────────────────────────────────────────────────┤
│              Security Layer                          │
│      (TLS 1.2+, Token Auth, Session Management)      │
└─────────────────────────────────────────────────────┘
```

### 1.2 Protocol Version Compatibility

| Viewer | Protocol Version | LLUDP | HTTP CAPS | LLSD |
|--------|-----------------|-------|-----------|------|
| Second Life Official | Current | ✅ | ✅ | ✅ |
| Firestorm | Current | ✅ | ✅ | ✅ |
| Lumiya | 3.4.2 | ✅ | ✅ | ✅ |
| Linkpoint | Target | ✅ | ✅ | ✅ |

---

## 2. Authentication Standards

### 2.1 Login Protocol

**Endpoint**: `https://login.agni.lindenlab.com/cgi-bin/login.cgi` (Main Grid)

**Method**: XML-RPC `login_to_simulator`

#### Required Parameters
```xml
<methodCall>
  <methodName>login_to_simulator</methodName>
  <params>
    <param>
      <value><struct>
        <member><name>first</name><value><string>FirstName</string></value></member>
        <member><name>last</name><value><string>LastName</string></value></member>
        <member><name>passwd</name><value><string>$1$md5hash</string></value></member>
        <member><name>start</name><value><string>last|home|uri:Region/x/y/z</string></value></member>
        <member><name>channel</name><value><string>ViewerName</string></value></member>
        <member><name>version</name><value><string>ViewerName X.Y.Z</string></value></member>
        <member><name>platform</name><value><string>Android</string></value></member>
        <member><name>mac</name><value><string>XX:XX:XX:XX:XX:XX</string></value></member>
        <member><name>id0</name><value><string>UUID</string></value></member>
        <member><name>agree_to_tos</name><value><string>true</string></value></member>
        <member><name>read_critical</name><value><string>true</string></value></member>
        <member><name>viewer_digest</name><value><string>UUID</string></value></member>
        <!-- MFA Support (Second Life 2024+) -->
        <member><name>token</name><value><string></string></value></member>
        <member><name>mfa_hash</name><value><string></string></value></member>
        <!-- Standard options -->
        <member><name>options</name><value><array><data>
          <value><string>inventory-root</string></value>
          <value><string>inventory-skeleton</string></value>
          <value><string>inventory-lib-root</string></value>
          <value><string>inventory-lib-owner</string></value>
          <value><string>inventory-skel-lib</string></value>
          <value><string>initial-outfit</string></value>
          <value><string>gestures</string></value>
          <value><string>buddy-list</string></value>
          <value><string>global-textures</string></value>
        </data></array></value></member>
      </struct></value>
    </param>
  </params>
</methodCall>
```

#### Login Response Structure
```
Key Response Fields:
├── agent_id (UUID)
├── session_id (UUID)
├── secure_session_id (UUID)
├── circuit_code (integer)
├── sim_ip (string)
├── sim_port (integer)
├── seed_capability (URL)
├── first_name (string)
├── last_name (string)
├── look_at (vector)
├── region_x (global coordinate)
├── region_y (global coordinate)
├── inventory-root
├── inventory-skeleton
├── buddy-list
└── mfa_hash (optional, for future logins)
```

### 2.2 Password Hashing

**Standard**: MD5 with `$1$` prefix (Second Life convention)

```kotlin
fun createPasswordHash(password: String): String {
    // CRITICAL: Truncate to 16 characters (SL protocol requirement)
    val truncated = password.trim().take(16)
    val md5 = MessageDigest.getInstance("MD5")
    val hash = md5.digest(truncated.toByteArray())
        .joinToString("") { "%02x".format(it) }
    return "\$1\$$hash"
}
```

### 2.3 Multi-Factor Authentication (MFA)

**Standard**: TOTP-based (RFC 6238)

```kotlin
// MFA Flow
sealed class LoginResult {
    data class Success(val agentId: UUID, val sessionId: String, val mfaHash: String?)
    data class MFARequired(val message: String, val agentId: String?)
    data class Failure(val message: String, val errorCode: String?)
}

// When MFARequired is returned:
// 1. Prompt user for TOTP code from authenticator app
// 2. Retry login with token parameter set
// 3. Store mfa_hash for future logins to skip MFA
```

---

## 3. LLUDP Protocol

### 3.1 Packet Structure

```
┌────────────────────────────────────────────────────┐
│                    LLUDP Packet                     │
├────────┬────────────────────────────────────────────┤
│ Flags  │ 1 byte                                     │
│ SeqNum │ 4 bytes (big-endian)                       │
│ Extra  │ 1 byte (optional)                          │
│ ID     │ 1-4 bytes (depends on frequency)           │
│ Data   │ Variable length (message specific)         │
├────────┴────────────────────────────────────────────┤
│ ACK Appendix (if APPENDED_ACKS flag set)            │
│  - ACK count: 1 byte                                │
│  - ACK list: 4 bytes each                           │
└────────────────────────────────────────────────────┘
```

### 3.2 Packet Flags
```kotlin
object PacketFlags {
    const val ZEROCODED     = 0x80  // Data is zero-run-length encoded
    const val RELIABLE      = 0x40  // Requires acknowledgment
    const val RESENT        = 0x20  // This is a resend
    const val APPENDED_ACKS = 0x10  // ACKs appended to end
}
```

### 3.3 Message Frequencies
```kotlin
enum class MessageFrequency(val idBytes: Int) {
    HIGH(1),      // Single byte: 0x01-0xFF
    MEDIUM(2),    // Two bytes: 0xFF + 0x01-0xFF  
    LOW(4),       // Four bytes: 0xFF + 0xFF + 0xXXXX
    FIXED(4)      // Same as low
}
```

### 3.4 Essential Messages

| Message | Direction | Purpose |
|---------|-----------|---------|
| UseCircuitCode | C→S | Establish circuit after login |
| CompleteAgentMovement | C→S | Complete agent entry to region |
| AgentUpdate | C→S | Avatar position/rotation updates |
| ChatFromViewer | C→S | Send chat message |
| ChatFromSimulator | S→C | Receive chat message |
| ImprovedTerseObjectUpdate | S→C | Object position updates |
| ObjectUpdate | S→C | Full object data |
| PacketAck | C↔S | Acknowledge reliable packets |
| StartPingCheck | S→C | Ping request |
| CompletePingCheck | C→S | Ping response |

---

## 4. HTTP Capabilities (CAPS)

### 4.1 Seed Capability Flow

```
1. Login returns seed_capability URL
2. POST to seed_capability with requested capability names
3. Receive map of capability name → unique URL
4. Use capability URLs for feature-specific requests
```

### 4.2 Essential Capabilities

```kotlin
val ESSENTIAL_CAPS = listOf(
    // Textures
    "GetTexture",
    "ViewerAsset",
    
    // Mesh
    "GetMesh",
    "GetMesh2",
    
    // Inventory
    "FetchInventory2",
    "FetchInventoryDescendents2",
    "CreateInventoryCategory",
    "CopyInventoryFromNotecard",
    
    // Avatar
    "AgentPreferences",
    "UpdateAgentInformation",
    "UpdateAgentLanguage",
    
    // Objects
    "ObjectAdd",
    "ObjectSelect",
    "ObjectMedia",
    
    // Chat/Groups
    "ChatSessionRequest",
    "GroupMemberData",
    
    // Voice
    "ProvisionVoiceAccountRequest",
    "ParcelVoiceInfoRequest",
    
    // Events
    "EventQueueGet",  // Long-poll for server events
    
    // Region
    "SimulatorFeatures",
    "SimConsoleAsync"
)
```

### 4.3 EventQueueGet Long-Poll

```kotlin
// Standard event queue polling pattern
suspend fun pollEventQueue(eventQueueUrl: String) {
    while (isConnected) {
        try {
            val response = httpClient.post(eventQueueUrl) {
                contentType(ContentType.Application.LlsdXml)
                body = "<llsd><map><key>ack</key><integer>$lastAck</integer><key>done</key><boolean>false</boolean></map></llsd>"
            }
            
            processEvents(response.events)
            lastAck = response.id
        } catch (e: TimeoutException) {
            // Normal - reconnect and continue
        }
    }
}
```

---

## 5. Asset Protocol

### 5.1 Texture Fetching

**Primary Method**: GetTexture capability (HTTP)

```kotlin
// Standard texture fetch
suspend fun fetchTexture(textureId: UUID): ByteArray {
    val textureUrl = capabilities["GetTexture"]
    val response = httpClient.get("$textureUrl?texture_id=$textureId")
    return response.body()  // JPEG2000 data
}
```

**Texture Formats**:
- **Standard**: JPEG2000 (J2K)
- **Modern**: Basis Universal (for mobile optimization)
- **Progressive**: Discard levels for LOD

### 5.2 Mesh Fetching

```kotlin
suspend fun fetchMesh(meshId: UUID): ByteArray {
    val meshUrl = capabilities["GetMesh2"] ?: capabilities["GetMesh"]
    return httpClient.get("$meshUrl?mesh_id=$meshId").body()
}
```

### 5.3 Asset Types
```kotlin
enum class AssetType(val value: Int) {
    TEXTURE(0),
    SOUND(1),
    CALLINGCARD(2),
    LANDMARK(3),
    SCRIPT(4),
    CLOTHING(5),
    OBJECT(6),
    NOTECARD(7),
    CATEGORY(8),
    LSL_TEXT(10),
    LSL_BYTECODE(11),
    TEXTURE_TGA(12),
    BODYPART(13),
    SOUND_WAV(17),
    IMAGE_JPEG(19),
    ANIMATION(20),
    GESTURE(21),
    SIMSTATE(22),
    LINK(24),
    LINK_FOLDER(25),
    MESH(49),
    SETTINGS(56),
    MATERIAL(57)
}
```

---

## 6. Inventory Protocol

### 6.1 Inventory Structure

```
Inventory Root
├── My Inventory
│   ├── Animations
│   ├── Body Parts
│   ├── Calling Cards
│   ├── Clothing
│   ├── Current Outfit
│   ├── Favorites
│   ├── Gestures
│   ├── Landmarks
│   ├── Lost And Found
│   ├── Notecards
│   ├── Objects
│   ├── Outfits
│   ├── Photo Album
│   ├── Scripts
│   ├── Settings
│   ├── Sounds
│   ├── Textures
│   └── Trash
└── Library (shared)
```

### 6.2 Fetch Inventory

```kotlin
// CAPS-based inventory fetch
suspend fun fetchInventoryFolder(folderId: UUID): InventoryFolder {
    val url = capabilities["FetchInventoryDescendents2"]
    val response = httpClient.post(url) {
        contentType(ContentType.Application.LlsdXml)
        body = buildFetchRequest(folderId)
    }
    return parseInventoryResponse(response)
}
```

---

## 7. Chat and Messaging

### 7.1 Local Chat

**Outgoing**: `ChatFromViewer` UDP message
```kotlin
data class ChatFromViewerMessage(
    val agentData: AgentData,
    val chatData: ChatData
) {
    data class ChatData(
        val message: String,      // UTF-8, max 1023 bytes
        val type: ChatType,       // WHISPER=0, NORMAL=1, SHOUT=2
        val channel: Int          // 0 = public, negative = script-only
    )
}
```

### 7.2 Instant Messages

**Outgoing**: `ImprovedInstantMessage` UDP message
**Incoming**: Via EventQueueGet or UDP

```kotlin
enum class IMType(val value: Int) {
    NORMAL(0),
    GROUP_NOTICE(6),
    GROUP_INVITATION(7),
    INVENTORY_OFFER(4),
    TELEPORT_LURE(22),
    TELEPORT_REQUEST(23),
    FRIENDSHIP_OFFER(38),
    FRIENDSHIP_ACCEPT(39),
    FRIENDSHIP_DECLINE(40)
}
```

---

## 8. Mobile-Specific Standards

### 8.1 Lumiya Mobile Patterns

From Lumiya decompilation:
- Optimized texture LOD for mobile bandwidth
- Aggressive asset caching
- Background connection management
- Battery-aware rendering

### 8.2 Recommended Mobile Optimizations

```kotlin
// Battery-aware connection management
class MobileConnectionManager {
    fun adjustForBattery(level: Int, isCharging: Boolean) {
        when {
            level < 15 && !isCharging -> {
                reducePollInterval()
                disableBackgroundFetch()
            }
            level < 30 && !isCharging -> {
                reducePollInterval()
            }
            else -> {
                normalPollInterval()
            }
        }
    }
}
```

### 8.3 Texture Quality Presets

```kotlin
enum class TextureQuality(val maxResolution: Int, val jpegQuality: Int) {
    LOW(256, 60),
    MEDIUM(512, 75),
    HIGH(1024, 85),
    ULTRA(2048, 95)
}
```

---

## 9. Security Standards

### 9.1 Transport Security
- **Minimum TLS Version**: 1.2
- **Certificate Validation**: Required
- **Certificate Pinning**: Recommended for production

### 9.2 Session Security
- Session ID validation on all requests
- Secure session ID for sensitive operations
- Regular session refresh

### 9.3 Data Protection
- Credentials never stored in plain text
- Token-based authentication preferred
- Secure enclave for key storage (Android Keystore)

---

## 10. Implementation Checklist

### Core Protocol
- [ ] XML-RPC login implementation
- [ ] Password hashing ($1$MD5)
- [ ] MFA support (TOTP)
- [ ] Session management

### LLUDP
- [ ] Packet structure parsing
- [ ] Reliable packet ACKs
- [ ] Zerocoding support
- [ ] Circuit establishment
- [ ] Ping/keepalive handling

### CAPS
- [ ] Seed capability request
- [ ] Essential capability mapping
- [ ] EventQueueGet long-poll
- [ ] Capability-based asset fetching

### Assets
- [ ] Texture fetching (GetTexture)
- [ ] JPEG2000 decoding
- [ ] Mesh fetching (GetMesh2)
- [ ] Asset caching

### Inventory
- [ ] Inventory skeleton parsing
- [ ] Folder fetching
- [ ] Item operations

### Chat
- [ ] Local chat (UDP)
- [ ] Instant messages
- [ ] Group chat support

### Mobile Optimization
- [ ] Battery-aware operation
- [ ] Texture LOD management
- [ ] Connection pooling
- [ ] Aggressive caching

---

## References

### Official Documentation
- [Second Life Wiki - Protocol](https://wiki.secondlife.com/wiki/Protocol)
- [Second Life Wiki - Message](https://wiki.secondlife.com/wiki/Message)
- [Second Life Wiki - Capabilities](https://wiki.secondlife.com/wiki/Capabilities)
- [Current Login Protocols](https://wiki.secondlife.com/wiki/Current_login_protocols)

### Open Source Viewers
- [Second Life Viewer](https://github.com/secondlife/viewer)
- [Firestorm Viewer](https://github.com/VIRTLANTIS/Firestorm-Viewer)
- [LibreMetaverse](https://github.com/cinderblocks/libremetaverse)

### Analysis Tools
- [NSA Ghidra](https://github.com/NationalSecurityAgency/ghidra)
- [JADX Decompiler](https://github.com/skylot/jadx)
- [Wireshark LLUDP Dissector](https://github.com/Neopallium/lludp_dissector)

---

*Document generated from comprehensive analysis of Second Life ecosystem viewers and protocols.*
*For Linkpoint mobile viewer development reference.*
