# Lumiya capability inventory (decompiled)

This document enumerates every capability in Lumiya's `SLCaps.SLCapability` enum and
links to the primary code paths that request or consume the capability.

## Capability list (source of truth)
The canonical list is the `SLCaps.SLCapability` enum.
- Source: `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/caps/SLCaps.java:27-45`

## Capability usage map

### EventQueueGet
- **Where**: `SLGridConnection.startCircuit` starts the caps event queue.
- **How/why**: Lumiya starts the event queue immediately after caps are resolved so it can receive LLSD event queue messages (presence, chat invites, etc.).
- **Source**: `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/SLGridConnection.java:175-183`

### GetTexture
- **Where**: `SLTextureFetcher` stores the GetTexture cap URL during construction.
- **How/why**: The texture fetcher uses the capability URL (when available) to fetch textures over HTTP; UDP is also used for transfers.
- **Source**: `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/modules/texfetcher/SLTextureFetcher.java:30-40`

### UploadBakedTexture
- **Where**: `SLTextureUploader` initializes the baked texture upload executor when the cap is present.
- **How/why**: Used to upload baked avatar textures to the simulator via HTTP.
- **Source**: `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/modules/texuploader/SLTextureUploader.java:13-26`

### FetchInventoryDescendents2
- **Where**: `SLInventory` caches the FetchInventoryDescendents2 cap URL.
- **How/why**: Used to request inventory folder contents via caps.
- **Source**: `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/inventory/SLInventory.java:210-214`

### GetDisplayNames
- **Where**: `SLDisplayNameFetcher` checks and stores the GetDisplayNames cap URL.
- **How/why**: Enables batch display-name resolution over HTTP when available.
- **Source**: `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/dispnames/SLDisplayNameFetcher.java:87-101`

### UpdateNotecardAgentInventory
- **Where**: `SLInventory.UploadNotecardContents` chooses the correct notecard upload cap.
- **How/why**: Used to upload updated notecards in agent inventory when not embedded in a task object.
- **Source**: `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/inventory/SLInventory.java:400-413`

### NewFileAgentInventory
- **Where**: Declared in `SLCaps.SLCapability`, but no direct usage was located in the decompiled tree.
- **How/why**: Likely intended for agent inventory file uploads; no runtime wiring found in decompiled sources.
- **Source**: `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/caps/SLCaps.java:27-45`

### CopyInventoryFromNotecard
- **Where**: `SLInventory` calls the cap to copy notecard contents to inventory.
- **How/why**: This is how Lumiya copies embedded notecard inventory to a destination folder.
- **Source**: `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/inventory/SLInventory.java:1261-1266`

### UpdateAvatarAppearance
- **Where**: `SLAvatarAppearance` checks for the cap and uses it during appearance updates.
- **How/why**: Triggers server-side appearance/bake updates when the capability is present.
- **Source**: `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/modules/SLAvatarAppearance.java:270-302`

### GetMesh
- **Where**: Declared in `SLCaps.SLCapability`, but no direct usage was located in the decompiled tree.
- **How/why**: Likely intended for mesh asset downloads; no runtime wiring found in decompiled sources.
- **Source**: `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/caps/SLCaps.java:27-45`

### UpdateNotecardTaskInventory
- **Where**: `SLInventory.UploadNotecardContents` selects this cap for task-embedded notecards.
- **How/why**: Used for notecards stored on in-world objects (task inventory).
- **Source**: `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/inventory/SLInventory.java:400-413`

### UpdateScriptTask
- **Where**: `SLInventory.UploadNotecardContents` selects script update caps when updating scripts in task inventory.
- **How/why**: Required to update scripts inside object inventories.
- **Source**: `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/inventory/SLInventory.java:400-413`

### UpdateScriptAgent
- **Where**: `SLInventory.UploadNotecardContents` selects script update caps when updating agent inventory scripts.
- **How/why**: Required to update scripts owned by the agent.
- **Source**: `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/inventory/SLInventory.java:400-413`

### GroupMemberData
- **Where**: `SLGroupManager` caches the GroupMemberData cap URL.
- **How/why**: Used for group member list and membership operations.
- **Source**: `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/modules/groups/SLGroupManager.java:210-214`

### HomeLocation
- **Where**: `SLUserProfiles` caches the HomeLocation cap URL.
- **How/why**: Used for home location updates (set-home operations).
- **Source**: `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/modules/SLUserProfiles.java:113-115`

### ProvisionVoiceAccountRequest
- **Where**: `SLVoice` caches the voice provisioning cap URL.
- **How/why**: Used to request voice credentials for Vivox/voice login.
- **Source**: `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/modules/voice/SLVoice.java:99-107`

### ParcelVoiceInfoRequest
- **Where**: `SLVoice` caches the parcel voice info cap URL.
- **How/why**: Used to retrieve voice channel info for the current parcel.
- **Source**: `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/modules/voice/SLVoice.java:99-103`

### ChatSessionRequest
- **Where**: `SLVoice` caches the chat session request cap URL.
- **How/why**: Used for chat/voice session requests, such as joining voice sessions.
- **Source**: `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/modules/voice/SLVoice.java:99-103`
