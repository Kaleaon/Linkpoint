# Linkpoint capability inventory

This document enumerates every capability declared in Linkpoint and links to
code paths that request or consume them.

## Capability list (source of truth)
The canonical list is in `CapabilityManager` constants.
- Source: `Linkpoint/src/main/java/com/linkpoint/protocol/capabilities/CapabilityManager.kt:45-90`

## Capability usage map

### Seed
- **Capability**: `Seed`
- **How/why**: Used as the initial capability URL to request the simulator’s capability map.
- **Source**: `Linkpoint/src/main/java/com/linkpoint/protocol/capabilities/CapabilityManager.kt:368-375`

### EventQueueGet
- **How/why**: Starts the event queue once capabilities resolve so the client receives server events.
- **Source**: `Linkpoint/src/main/java/com/linkpoint/protocol/capabilities/CapabilityManager.kt:342-348`

### FetchInventory2
- **How/why**: Fetches specific inventory items by ID.
- **Source**: `Linkpoint/src/main/java/com/linkpoint/inventory/InventoryManager.kt:171-193`

### FetchLib2
- **How/why**: Declared in `CapabilityManager`, but no direct usage was located in the runtime code paths.
- **Source**: `Linkpoint/src/main/java/com/linkpoint/protocol/capabilities/CapabilityManager.kt:48-50`

### FetchInventoryDescendents2
- **How/why**: Fetches folder contents (categories + items) for inventory browsing.
- **Source**: `Linkpoint/src/main/java/com/linkpoint/inventory/InventoryManager.kt:120-136`

### GetTexture
- **How/why**: Used by `TextureManager` to build and request HTTP texture URLs.
- **Source**: `Linkpoint/src/main/java/com/linkpoint/assets/TextureManager.kt:34-41`

### GetMesh / GetMesh2
- **How/why**: `MeshManager` prefers `GetMesh2` and falls back to `GetMesh` for mesh asset downloads.
- **Source**: `Linkpoint/src/main/java/com/linkpoint/assets/MeshManager.kt:70-82`

### ViewerStats
- **How/why**: Declared in `CapabilityManager` and requested in the standard caps list, but no direct usage was located.
- **Source**: `Linkpoint/src/main/java/com/linkpoint/protocol/translation/LumiyaProtocolBridge.kt:279-299`

### AgentState
- **How/why**: Declared in `CapabilityManager` and requested in the standard caps list, but no direct usage was located.
- **Source**: `Linkpoint/src/main/java/com/linkpoint/protocol/translation/LumiyaProtocolBridge.kt:279-299`

### UpdateAgentInformation
- **How/why**: Declared in `CapabilityManager` and requested in the standard caps list, but no direct usage was located.
- **Source**: `Linkpoint/src/main/java/com/linkpoint/protocol/translation/LumiyaProtocolBridge.kt:279-299`

### UploadBakedTexture
- **How/why**: `AvatarBaker` uploads baked textures through the cap to update avatar appearance.
- **Source**: `Linkpoint/src/main/java/com/linkpoint/avatar/AvatarBaker.kt:347-364`

### ObjectMedia
- **How/why**: Media manager supports MOAP (Media-on-a-Prim) data handling; capability is declared but message sending is stubbed.
- **Source**: `Linkpoint/src/main/java/com/linkpoint/media/MediaManager.kt:248-264`

### ObjectMediaNavigate
- **How/why**: Declared in `CapabilityManager`, but no direct usage was located.
- **Source**: `Linkpoint/src/main/java/com/linkpoint/protocol/capabilities/CapabilityManager.kt:58-60`

### ParcelVoiceInfoRequest
- **How/why**: `VoiceManager` requests parcel voice info to join the current voice channel.
- **Source**: `Linkpoint/src/main/java/com/linkpoint/voice/VoiceManager.kt:120-140`

### ProvisionVoiceAccountRequest
- **How/why**: `VoiceManager` provisions voice credentials for WebRTC sessions.
- **Source**: `Linkpoint/src/main/java/com/linkpoint/voice/VoiceManager.kt:135-146`

### ChatSessionRequest
- **How/why**: `IMManager` sends IMs via the chat-session capability (caps-based IM send).
- **Source**: `Linkpoint/src/main/java/com/linkpoint/chat/IMManager.kt:273-286`

### CopyInventoryFromNotecard
- **How/why**: Declared in `CapabilityManager`, but no direct usage was located.
- **Source**: `Linkpoint/src/main/java/com/linkpoint/protocol/capabilities/CapabilityManager.kt:61-64`

### EnvironmentSettings / ExtEnvironment
- **How/why**: `EnvironmentManager` fetches region environment data using ExtEnvironment with a fallback to EnvironmentSettings.
- **Source**: `Linkpoint/src/main/java/com/linkpoint/world/environment/EnvironmentManager.kt:86-101`

### RegionExperiences
- **How/why**: Declared in `CapabilityManager`, but no direct usage was located.
- **Source**: `Linkpoint/src/main/java/com/linkpoint/protocol/capabilities/CapabilityManager.kt:64-67`

### SimulatorLure
- **How/why**: `TeleportManager` uses the SimulatorLure cap when available, falling back to UDP teleport requests.
- **Source**: `Linkpoint/src/main/java/com/linkpoint/teleport/TeleportManager.kt:117-134`

### AvatarPickerSearch
- **How/why**: `SearchManager` uses the avatar picker cap to search for people.
- **Source**: `Linkpoint/src/main/java/com/linkpoint/world/SearchManager.kt:41-56`

### SearchStatRequest
- **How/why**: Declared in `CapabilityManager` and requested in the standard caps list, but no direct usage was located.
- **Source**: `Linkpoint/src/main/java/com/linkpoint/protocol/translation/LumiyaProtocolBridge.kt:279-299`

### VoiceModeration
- **How/why**: Declared in `CapabilityManager`, but no direct usage was located.
- **Source**: `Linkpoint/src/main/java/com/linkpoint/protocol/capabilities/CapabilityManager.kt:71-73`

### CreateInventoryCategory
- **How/why**: Inventory folder creation uses this capability when available.
- **Source**: `Linkpoint/src/main/java/com/linkpoint/inventory/InventoryManager.kt:465-487`

### MoveItemsToTrash
- **How/why**: Declared in `CapabilityManager`, but no direct usage was located (AISv3 uses InventoryAPIv3 instead).
- **Source**: `Linkpoint/src/main/java/com/linkpoint/protocol/capabilities/CapabilityManager.kt:74-78`

### UpdateInventoryItem
- **How/why**: Inventory updates use UpdateInventoryItem when available.
- **Source**: `Linkpoint/src/main/java/com/linkpoint/inventory/InventoryManager.kt:580-588`

### InventoryAPIv3
- **How/why**: AISv3 inventory move operations use InventoryAPIv3 (items + folders).
- **Source**: `Linkpoint/src/main/java/com/linkpoint/inventory/InventoryManager.kt:371-388`

### GetDisplayNames
- **How/why**: `ProfileManager` resolves display names in batch using the cap.
- **Source**: `Linkpoint/src/main/java/com/linkpoint/world/ProfileManager.kt:134-155`

### SetDisplayName
- **How/why**: Declared in `CapabilityManager`, but no direct usage was located.
- **Source**: `Linkpoint/src/main/java/com/linkpoint/protocol/capabilities/CapabilityManager.kt:80-82`

### SimulatorFeatures
- **How/why**: Declared in `CapabilityManager`, but no direct usage was located.
- **Source**: `Linkpoint/src/main/java/com/linkpoint/protocol/capabilities/CapabilityManager.kt:84-86`

### AgentPreferences
- **How/why**: Declared in `CapabilityManager`, but no direct usage was located.
- **Source**: `Linkpoint/src/main/java/com/linkpoint/protocol/capabilities/CapabilityManager.kt:84-87`

### UpdateAgentLanguage
- **How/why**: Declared in `CapabilityManager`, but no direct usage was located.
- **Source**: `Linkpoint/src/main/java/com/linkpoint/protocol/capabilities/CapabilityManager.kt:85-88`

### RenderMaterials
- **How/why**: Declared in `CapabilityManager`, but no direct usage was located.
- **Source**: `Linkpoint/src/main/java/com/linkpoint/protocol/capabilities/CapabilityManager.kt:89-90`
