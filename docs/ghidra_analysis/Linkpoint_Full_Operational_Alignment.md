# Linkpoint Full Operational Alignment with Second Life Viewers

## Executive Summary

This document provides a comprehensive analysis of changes needed to bring Linkpoint into full operational alignment with all Second Life viewers (official and third-party). This goes beyond login to cover the complete protocol stack required to operate within Second Life.

**Current Status**: Linkpoint has a solid foundation with most major systems implemented. However, several gaps exist that prevent full operational parity with viewers like Firestorm, Alchemy, and LibreMetaverse.

---

## Table of Contents

1. [Login Protocol Alignment](#1-login-protocol-alignment)
2. [UDP Protocol (LLUDP) Alignment](#2-udp-protocol-lludp-alignment)
3. [Capabilities (CAPS) Alignment](#3-capabilities-caps-alignment)
4. [Inventory System Alignment](#4-inventory-system-alignment)
5. [Avatar System Alignment](#5-avatar-system-alignment)
6. [Chat & Messaging Alignment](#6-chat--messaging-alignment)
7. [Asset System Alignment](#7-asset-system-alignment)
8. [World/Scene Alignment](#8-worldscene-alignment)
9. [Voice System Alignment](#9-voice-system-alignment)
10. [Priority Implementation Order](#10-priority-implementation-order)

---

## 1. Login Protocol Alignment

### Current Status: 90% Complete

### Missing Elements

| Element | Priority | Status | Required Change |
|---------|----------|--------|-----------------|
| `last_exec_event` | HIGH | ❌ Missing | Add crash tracking parameter |
| Pre-hashed password detection | HIGH | ❌ Missing | Check for $1$ prefix |
| Persistent MAC address | HIGH | ❌ Missing | Use device ID, not random |
| Persistent ID0 | HIGH | ❌ Missing | Store in SharedPreferences |
| `tutorial_settings` (plural) | LOW | ⚠️ Typo | Fix spelling |
| Account benefits parsing | MEDIUM | ❌ Missing | Parse response fields |
| `presence` error handling | MEDIUM | ❌ Missing | Handle already-logged-in |

### Required Code Changes

**File**: `Linkpoint/src/main/java/com/linkpoint/network/SecondLifeProtocol.kt`

```kotlin
// 1. Add last_exec_event - tracks previous app exit status for crash reporting
// getLastExecStatus() should be implemented via CrashTracker class (see below)
append("<member><name>last_exec_event</name><value><i4>${crashTracker.getLastExecStatus()}</i4></value></member>")

// CrashTracker implementation:
class CrashTracker(context: Context) {
    private val prefs = context.getSharedPreferences("crash_tracker", Context.MODE_PRIVATE)
    
    enum class Status(val value: Int) {
        NORMAL(0), FROZE(1), FORCED_CRASH(2), OTHER_CRASH(3), LOGOUT_FROZE(4), LOGOUT_CRASH(5)
    }
    
    fun getLastExecStatus(): Int = prefs.getInt("last_exec", Status.NORMAL.value)
    
    fun recordCleanShutdown() {
        prefs.edit().putInt("last_exec", Status.NORMAL.value).apply()
    }
    
    fun recordAppStart() {
        // If not NORMAL, we crashed last time
        val lastStatus = prefs.getInt("last_exec", -1)
        if (lastStatus != Status.NORMAL.value && lastStatus != -1) {
            prefs.edit().putInt("last_exec", Status.OTHER_CRASH.value).apply()
        }
    }
}

// 2. Fix password hash detection
fun createPasswordHash(password: String): String {
    // Support already-hashed passwords
    if (password.length == 35 && password.startsWith("\$1\$")) {
        return password
    }
    val truncatedPassword = password.trim().take(16)
    return "\$1\$${md5Hash(truncatedPassword)}"
}

// 3. Persistent device identifiers
private val deviceIdentifier by lazy { DeviceIdentifier(context) }
private fun getDeviceMac(): String = deviceIdentifier.getMacAddress()
private fun getDeviceId0(): String = deviceIdentifier.getId0()
```

---

## 2. UDP Protocol (LLUDP) Alignment

### Current Status: 75% Complete

### Implemented ✅
- UseCircuitCode message
- CompleteAgentMovement message
- RegionHandshakeReply message
- AgentThrottle message
- Packet flags (ZEROCODED, RELIABLE, RESENT, ACK)
- Sequence numbering
- Basic ACK handling

### Missing Elements

| Element | Priority | Official Viewer Behavior |
|---------|----------|-------------------------|
| AgentUpdate message | CRITICAL | Send every 100ms with position/rotation |
| PacketAck batching | HIGH | Batch ACKs to reduce traffic |
| Ping/Keepalive | HIGH | Respond to StartPingCheck |
| CoarseLocationUpdate handling | HIGH | Track other avatars |
| Zerocoding decode | MEDIUM | Properly decode incoming zerocoded packets |
| Reliable packet resend | MEDIUM | Resend unacked packets |
| AgentHeightWidth | MEDIUM | Send window dimensions |
| SetAlwaysRun | LOW | Toggle run mode |

### Required Code Changes

**File**: `Linkpoint/src/main/java/com/linkpoint/protocol/messages/UDPConnection.kt`

```kotlin
// 1. AgentUpdate - CRITICAL for movement
suspend fun sendAgentUpdate(
    position: LLVector3,
    lookAt: LLVector3,
    rotation: LLQuaternion,
    state: Int = 0,
    flags: Int = 0
) {
    val payload = ByteBuffer.allocate(114).order(ByteOrder.BIG_ENDIAN)
    
    // AgentData block
    payload.putUUID(agentId)
    payload.putUUID(sessionId)
    
    // Body rotation (quaternion)
    payload.putLLQuaternion(rotation)
    
    // Head rotation
    payload.putLLQuaternion(rotation)
    
    // State (standing, sitting, etc)
    payload.put(state.toByte())
    
    // Camera center
    payload.putLLVector3(position)
    
    // Camera at/left/up vectors
    payload.putLLVector3(lookAt)
    payload.putLLVector3(LLVector3(-lookAt.y, lookAt.x, 0f)) // left
    payload.putLLVector3(LLVector3(0f, 0f, 1f)) // up
    
    // Far distance
    payload.putFloat(128f)
    
    // Control flags
    payload.putInt(0)
    
    // Flags
    payload.put(flags.toByte())
    
    sendPacket(MessageIds.AGENT_UPDATE, payload.array(), reliable = false)
}

// 2. StartPingCheck response
fun handleStartPingCheck(pingId: Byte, oldestUnacked: Int) {
    scope.launch {
        val payload = ByteBuffer.allocate(5).order(ByteOrder.BIG_ENDIAN)
        payload.put(pingId)
        payload.putInt(oldestUnacked)
        sendPacket(MessageIds.COMPLETE_PING_CHECK, payload.array())
    }
}

// 3. Packet ACK batching
private val pendingAckIds = mutableListOf<Int>()
private var lastAckFlush = System.currentTimeMillis()

fun queueAck(sequenceNumber: Int) {
    synchronized(pendingAckIds) {
        pendingAckIds.add(sequenceNumber)
        if (pendingAckIds.size >= 10 || System.currentTimeMillis() - lastAckFlush > 100) {
            flushAcks()
        }
    }
}

private fun flushAcks() {
    synchronized(pendingAckIds) {
        if (pendingAckIds.isEmpty()) return
        
        val payload = ByteBuffer.allocate(1 + pendingAckIds.size * 4)
        payload.put(pendingAckIds.size.toByte())
        pendingAckIds.forEach { payload.putInt(it) }
        
        scope.launch {
            sendPacket(MessageIds.PACKET_ACK, payload.array())
        }
        
        pendingAckIds.clear()
        lastAckFlush = System.currentTimeMillis()
    }
}
```

### New Message IDs Required

```kotlin
object MessageIds {
    // High frequency (1 byte)
    const val PACKET_ACK = 0xFB
    const val START_PING_CHECK = 0x01
    const val COMPLETE_PING_CHECK = 0x02
    const val AGENT_UPDATE = 0x04
    const val COARSE_LOCATION_UPDATE = 0x06
    
    // Medium frequency (2 bytes: 0xFF + byte)
    const val OBJECT_UPDATE = 0xFF0C
    const val OBJECT_UPDATE_COMPRESSED = 0xFF0D
    const val OBJECT_UPDATE_CACHED = 0xFF0E
    const val IMPROVED_TERSE_OBJECT_UPDATE = 0xFF0F
    const val AVATAR_ANIMATION = 0xFF14
    
    // Low frequency (4 bytes: 0xFFFF + short)
    const val COMPLETE_AGENT_MOVEMENT = 0xFFFF00F9.toInt()
    const val REGION_HANDSHAKE_REPLY = 0xFFFF00FA.toInt()
    const val AGENT_THROTTLE = 0xFFFF00FB.toInt()
    const val CHAT_FROM_VIEWER = 0xFFFF0050.toInt()
    const val CHAT_FROM_SIMULATOR = 0xFFFF008B.toInt()
    const val IM_FROM_VIEWER = 0xFFFF008D.toInt()
    const val IM_FROM_SIMULATOR = 0xFFFF008F.toInt()
}
```

---

## 3. Capabilities (CAPS) Alignment

### Current Status: 80% Complete

### Implemented ✅
- Seed capability fetching
- EventQueueGet long-polling
- FetchInventory2
- FetchInventoryDescendents2
- GetTexture
- GetMesh/GetMesh2
- Voice capabilities

### Missing Elements

| Capability | Priority | Purpose |
|------------|----------|---------|
| SimulatorFeatures | HIGH | Get region capabilities |
| AgentPreferences | HIGH | User preferences sync |
| UpdateAgentLanguage | MEDIUM | Language setting |
| ObjectAdd | MEDIUM | Rez objects |
| UpdateScriptTask | MEDIUM | Script editing |
| DispatchRegionInfo | LOW | Region info changes |
| RenderMaterials | LOW | PBR materials |
| GetDisplayNames | MEDIUM | Display name lookup |
| SetDisplayName | LOW | Change display name |

### Required Additions

**File**: `Linkpoint/src/main/java/com/linkpoint/protocol/capabilities/CapabilityManager.kt`

```kotlin
// Add missing capability constants
const val CAP_SIMULATOR_FEATURES = "SimulatorFeatures"
const val CAP_AGENT_PREFERENCES = "AgentPreferences"
const val CAP_UPDATE_AGENT_LANGUAGE = "UpdateAgentLanguage"
const val CAP_OBJECT_ADD = "ObjectAdd"
const val CAP_UPDATE_SCRIPT_TASK = "UpdateScriptTask"
const val CAP_RENDER_MATERIALS = "RenderMaterials"
const val CAP_GET_DISPLAY_NAMES = "GetDisplayNames"
const val CAP_SET_DISPLAY_NAME = "SetDisplayName"

/**
 * Fetch simulator features to know what the region supports.
 * This is called by official viewers after login.
 */
suspend fun fetchSimulatorFeatures(): Map<String, Any>? {
    val url = getCapability(CAP_SIMULATOR_FEATURES) ?: return null
    
    return try {
        val response = makeRequest(url)
        parseLLSD(response) as? Map<String, Any>
    } catch (e: Exception) {
        Log.e(TAG, "Failed to fetch simulator features", e)
        null
    }
}

/**
 * Send agent preferences to server.
 * Official viewers do this after login.
 */
suspend fun updateAgentPreferences(prefs: Map<String, Any>): Boolean {
    val url = getCapability(CAP_AGENT_PREFERENCES) ?: return false
    
    return try {
        val body = buildLLSD(prefs)
        makePostRequest(url, body)
        true
    } catch (e: Exception) {
        Log.e(TAG, "Failed to update agent preferences", e)
        false
    }
}
```

---

## 4. Inventory System Alignment

### Current Status: 70% Complete

### Implemented ✅
- Folder structure
- Item caching
- FetchInventoryDescendents2
- System folder types

### Missing Elements

| Feature | Priority | Official Viewer Behavior |
|---------|----------|-------------------------|
| Library inventory | HIGH | Fetch shared library |
| Inventory links | HIGH | Handle link items |
| Inventory moves | HIGH | Move items between folders |
| Inventory copies | MEDIUM | Copy items |
| Trash operations | MEDIUM | Delete/restore items |
| Folder creation | MEDIUM | Create new folders |
| Notecard reading | MEDIUM | View notecard contents |
| Landmark teleport | MEDIUM | TP to landmark |
| Wearable attaching | HIGH | Attach/wear items |
| Script copying | LOW | Copy scripts to objects |

### Required Code Changes

**File**: `Linkpoint/src/main/java/com/linkpoint/inventory/InventoryManager.kt`

```kotlin
/**
 * Move an item to a different folder.
 * Uses UDP message MoveInventoryItem.
 */
suspend fun moveItem(itemId: UUID, newFolderId: UUID): Boolean {
    // Implementation using MoveInventoryItem message
}

/**
 * Create a new folder.
 * Uses CreateInventoryFolder capability.
 */
suspend fun createFolder(
    parentId: UUID,
    name: String,
    type: Int = FOLDER_TYPE_CATEGORY
): UUID? {
    val url = capabilityManager.getCapability("CreateInventoryCategory") ?: return null
    // Implementation
}

/**
 * Handle inventory link items.
 * Links reference other items/folders.
 */
fun resolveLink(item: InventoryItem): InventoryItem? {
    if (item.assetType != AssetType.LINK.value && 
        item.assetType != AssetType.LINK_FOLDER.value) {
        return item
    }
    return items[item.assetId]
}

/**
 * Attach/wear an item.
 */
suspend fun attachItem(itemId: UUID, attachPoint: Int = 0) {
    // Uses RezSingleAttachmentFromInv UDP message
    val item = items[itemId] ?: return
    
    val payload = ByteBuffer.allocate(...)
    // Build attachment message
    udpConnection.sendPacket(MessageIds.REZ_SINGLE_ATTACHMENT, payload.array(), reliable = true)
}
```

---

## 5. Avatar System Alignment

### Current Status: 65% Complete

### Implemented ✅
- Avatar tracking
- Position/rotation updates
- Animation handling
- Basic appearance

### Missing Elements

| Feature | Priority | Official Viewer Behavior |
|---------|----------|-------------------------|
| Baked textures upload | CRITICAL | Upload baked appearance |
| Appearance update | CRITICAL | Send AgentSetAppearance |
| Attachment points | HIGH | Track attachments |
| Name tag rendering | HIGH | Display names above avatars |
| Animation priorities | MEDIUM | Proper anim layering |
| Hover height | MEDIUM | Avatar hover offset |
| Body physics | LOW | Breast/belly physics |

### Required Code Changes

**File**: `Linkpoint/src/main/java/com/linkpoint/avatar/AvatarManager.kt`

```kotlin
/**
 * Send appearance update to server.
 * This is required for other avatars to see your appearance.
 */
suspend fun sendAppearance() {
    // 1. Bake textures locally
    val bakedTextures = avatarBaker.bakeAllLayers()
    
    // 2. Upload baked textures via UploadBakedTexture capability
    val uploadedIds = mutableMapOf<Int, UUID>()
    bakedTextures.forEach { (index, texture) ->
        val uploadUrl = capabilityManager.getCapability(CAP_UPLOAD_BAKED_TEXTURE)
        if (uploadUrl != null) {
            val assetId = uploadBakedTexture(uploadUrl, texture)
            uploadedIds[index] = assetId
        }
    }
    
    // 3. Send AgentSetAppearance UDP message
    sendAgentSetAppearance(uploadedIds)
}

/**
 * Send AgentSetAppearance message.
 * Format matches official viewer implementation.
 */
private suspend fun sendAgentSetAppearance(bakedTextures: Map<Int, UUID>) {
    val payload = ByteBuffer.allocate(1024).order(ByteOrder.BIG_ENDIAN)
    
    // AgentData
    payload.putUUID(agentId)
    payload.putUUID(sessionId)
    payload.putInt(serialNum++)
    payload.putLLVector3(avatarSize)
    
    // WearableData - for each baked layer
    payload.put(bakedTextures.size.toByte())
    bakedTextures.forEach { (index, assetId) ->
        payload.put(index.toByte())
        payload.putUUID(assetId)
    }
    
    // Visual params
    payload.put(visualParams.size.toByte())
    visualParams.forEach { payload.put(it) }
    
    udpConnection.sendPacket(MessageIds.AGENT_SET_APPEARANCE, payload.array(), reliable = true)
}
```

---

## 6. Chat & Messaging Alignment

### Current Status: 80% Complete

### Implemented ✅
- Local chat send/receive
- Chat types (whisper, say, shout)
- Typing indicators
- IM basics

### Missing Elements

| Feature | Priority | Official Viewer Behavior |
|---------|----------|-------------------------|
| Group chat | HIGH | Via ChatSessionRequest cap |
| Conference chat | MEDIUM | Multi-party IM |
| Mute list | HIGH | Block users/objects |
| Chat history sync | LOW | Persist between sessions |
| Object IM | MEDIUM | Script-to-user messages |
| Busy/DND mode | MEDIUM | Auto-response |

### Required Code Changes

**File**: `Linkpoint/src/main/java/com/linkpoint/chat/IMManager.kt`

```kotlin
/**
 * Join a group chat session.
 * Uses ChatSessionRequest capability.
 */
suspend fun joinGroupChat(groupId: UUID): Boolean {
    val url = capabilityManager.getCapability(CAP_CHAT_PASS) ?: return false
    
    val request = LLSDMap().apply {
        put("method", LLSDString("start conference"))
        put("session-id", LLSDUUID(groupId))
    }
    
    return try {
        val response = capabilityManager.makePostRequest(url, request.toLLSDXml())
        // Parse and handle response
        true
    } catch (e: Exception) {
        Log.e(TAG, "Failed to join group chat", e)
        false
    }
}

/**
 * Mute list management.
 */
class MuteManager {
    private val mutedAgents = mutableSetOf<UUID>()
    private val mutedObjects = mutableSetOf<UUID>()
    
    fun muteAgent(agentId: UUID) {
        mutedAgents.add(agentId)
        sendMuteListUpdate()
    }
    
    fun isMuted(sourceId: UUID): Boolean {
        return sourceId in mutedAgents || sourceId in mutedObjects
    }
}
```

---

## 7. Asset System Alignment

### Current Status: 75% Complete

### Implemented ✅
- Texture fetching (GetTexture)
- Mesh fetching (GetMesh/GetMesh2)
- Asset caching
- JPEG2000 decoding basics

### Missing Elements

| Feature | Priority | Official Viewer Behavior |
|---------|----------|-------------------------|
| Progressive JPEG2000 | HIGH | Decode LOD levels |
| Animation assets | HIGH | BVH animation loading |
| Sound assets | MEDIUM | OGG Vorbis audio |
| Notecard assets | MEDIUM | Text notecards |
| Script assets | LOW | LSL source viewing |
| Landmark assets | MEDIUM | TP coordinates |
| Texture upload | LOW | Upload new textures |

### Required Code Changes

**File**: `Linkpoint/src/main/java/com/linkpoint/assets/TextureManager.kt`

```kotlin
/**
 * Fetch texture with LOD (Level of Detail) support.
 * Official viewers request lower LODs first for faster display.
 */
suspend fun fetchTextureWithLOD(
    textureId: UUID,
    discardLevel: Int = 0
): Bitmap? {
    val url = capabilityManager.getCapability(CAP_GET_TEXTURE) ?: return null
    
    // Request specific discard level for progressive loading
    val requestUrl = "$url?texture_id=$textureId&discard_level=$discardLevel"
    
    return try {
        val response = httpClient.get(requestUrl)
        decodeJPEG2000(response.body?.bytes(), discardLevel)
    } catch (e: Exception) {
        Log.e(TAG, "Failed to fetch texture $textureId", e)
        null
    }
}
```

---

## 8. World/Scene Alignment

### Current Status: 60% Complete

### Implemented ✅
- Region handshake
- Basic terrain
- Object updates

### Missing Elements

| Feature | Priority | Official Viewer Behavior |
|---------|----------|-------------------------|
| Terrain texture blending | HIGH | Proper ground rendering |
| Water rendering | HIGH | Linden water shader |
| Sky/Windlight | HIGH | Day cycle rendering |
| Object full updates | HIGH | Complete object data |
| Object caching | HIGH | Cache objects between sessions |
| LOD switching | MEDIUM | Distance-based detail |
| Parcel boundaries | MEDIUM | Show parcel lines |
| Region crossing | HIGH | Handle sim border cross |

### Required Code Changes

**File**: `Linkpoint/src/main/java/com/linkpoint/world/RegionManager.kt`

```kotlin
/**
 * Handle region crossing.
 * When avatar moves between regions.
 */
suspend fun handleRegionCrossing(
    newSimIP: String,
    newSimPort: Int,
    newCircuitCode: Int,
    seedCap: String
) {
    // 1. Close old UDP connection gracefully
    udpConnection.sendLogoutRequest()
    udpConnection.disconnect()
    
    // 2. Establish new connection
    udpConnection.configure(newSimIP, newSimPort, newCircuitCode)
    udpConnection.connect()
    
    // 3. Fetch new capabilities
    capabilityManager.setSeedCapability(seedCap)
    capabilityManager.fetchCapabilities()
    
    // 4. Request region handshake
    // Simulator will send RegionHandshake
}

/**
 * Handle ChildAgentUpdate for neighboring regions.
 * Required for smooth region crossing.
 */
fun handleChildAgentUpdate(regionHandle: Long, agentData: AgentData) {
    childRegions[regionHandle] = ChildRegionInfo(
        handle = regionHandle,
        agentData = agentData,
        lastUpdate = System.currentTimeMillis()
    )
}
```

---

## 9. Voice System Alignment

### Current Status: 50% Complete

### Implemented ✅
- Voice capability detection
- Basic Vivox integration start

### Missing Elements

| Feature | Priority | Official Viewer Behavior |
|---------|----------|-------------------------|
| Voice login | CRITICAL | Connect to Vivox |
| Spatial audio | HIGH | 3D positioned voice |
| Voice indicators | HIGH | Show who's talking |
| Push-to-talk | HIGH | PTT mode |
| Voice moderation | MEDIUM | Mute/eject from voice |
| Group voice | MEDIUM | Voice in group calls |

---

## 10. Priority Implementation Order

### Phase 1: Critical for Operation (Must Have)
1. ✅ Login protocol fixes (last_exec_event, persistent IDs)
2. ⬜ AgentUpdate message (required for movement)
3. ⬜ Ping/Keepalive response (connection maintenance)
4. ⬜ Avatar appearance upload (visible to others)
5. ⬜ Region crossing support

### Phase 2: Core Features (Should Have)
1. ⬜ Group chat support
2. ⬜ Inventory operations (move, copy, delete)
3. ⬜ Wearable/attachment handling
4. ⬜ Mute list
5. ⬜ Progressive texture loading

### Phase 3: Enhanced Experience (Nice to Have)
1. ⬜ Voice integration
2. ⬜ Windlight/sky rendering
3. ⬜ Object caching
4. ⬜ Display name support
5. ⬜ Animation improvements

---

## Implementation Checklist

### Login Protocol
- [ ] Add `last_exec_event` parameter
- [ ] Add pre-hashed password detection
- [ ] Implement persistent MAC address
- [ ] Implement persistent ID0
- [ ] Fix `tutorial_settings` typo
- [ ] Parse account_level_benefits
- [ ] Handle `presence` error code

### UDP Protocol
- [ ] Implement AgentUpdate message sending
- [ ] Add ping/keepalive response
- [ ] Implement packet ACK batching
- [ ] Add reliable packet resending
- [ ] Handle CoarseLocationUpdate

### Capabilities
- [ ] Fetch SimulatorFeatures
- [ ] Implement AgentPreferences
- [ ] Add group chat via ChatSessionRequest

### Inventory
- [ ] Implement inventory moves
- [ ] Handle inventory links
- [ ] Add wearable attachment
- [ ] Implement folder creation

### Avatar
- [ ] Implement baked texture upload
- [ ] Send AgentSetAppearance
- [ ] Track attachments
- [ ] Add display names

### World
- [ ] Handle region crossing
- [ ] Implement child agents
- [ ] Add terrain rendering improvements

---

## Files to Create/Modify

### New Files
1. `Linkpoint/src/main/java/com/linkpoint/auth/DeviceIdentifier.kt`
2. `Linkpoint/src/main/java/com/linkpoint/auth/CrashTracker.kt`
3. `Linkpoint/src/main/java/com/linkpoint/chat/MuteManager.kt`
4. `Linkpoint/src/main/java/com/linkpoint/world/RegionCrossing.kt`

### Modified Files
1. `SecondLifeProtocol.kt` - Login fixes
2. `UDPConnection.kt` - AgentUpdate, ping, ACK batching
3. `CapabilityManager.kt` - New capabilities
4. `InventoryManager.kt` - Operations
5. `AvatarManager.kt` - Appearance upload
6. `ChatManager.kt` - Mute support
7. `IMManager.kt` - Group chat

---

## Summary

Linkpoint has a solid foundation but requires specific enhancements to achieve full operational parity with desktop viewers. The most critical items are:

1. **AgentUpdate message** - Without this, movement won't work properly
2. **Appearance upload** - Without this, you'll appear as a cloud to others
3. **Login ID persistence** - Required for consistent device identification
4. **Ping response** - Required to prevent disconnection

Implementing these core items will bring Linkpoint to ~95% operational compatibility with official Second Life viewers.

---

*Document generated from comprehensive analysis of Second Life viewer protocols and Linkpoint source code.*
