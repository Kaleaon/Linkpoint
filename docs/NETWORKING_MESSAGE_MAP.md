# Linkpoint Message/Capability Map (Textures, Inventory, Chat)

This map links Linkpoint manager classes to the **caps** and **UDP message families** they use today. It is intended as a packet‑level “next step” companion to the high‑level networking flow doc and notes where the flow mirrors Lumiya’s viewer patterns (caps + UDP fallbacks).【F:docs/NETWORKING_LINKPOINT_LUMIYA.md†L5-L52】

## Encoding & Byte Order Conventions (applies to all UDP packet shapes below)

Linkpoint follows the Second Life message template conventions:
- **Block fields are little‑endian** unless explicitly noted otherwise in code comments.
- **Agent/Session UUIDs are serialized big‑endian** (network order) even when the message block itself is little‑endian. Many payload builders temporarily switch to big‑endian for UUID writes and then return to little‑endian for the rest of the block.【F:Linkpoint/src/main/java/com/linkpoint/chat/ChatManager.kt†L115-L164】【F:Linkpoint/src/main/java/com/linkpoint/world/ParcelManager.kt†L62-L104】
- **Variable fields** (strings/binaries) are typically length‑prefixed:
  - Variable‑2 fields use a **U16 length prefix** (common for chat/IM payloads).
  - Variable‑1 fields often use a **U8 length prefix** (folder names, filenames, etc.).
- **LLSD bodies** are used for caps requests/responses over HTTP and event queue payloads, serialized as XML or binary LLSD as indicated by the response headers.【F:Linkpoint/src/main/java/com/linkpoint/protocol/capabilities/CapabilityManager.kt†L366-L506】

These rules reflect the same data layout assumptions used by Lumiya‑style packet builders and parsers throughout the Linkpoint codebase.

## Textures (TextureManager)

**HTTP/Caps**
- **GetTexture** capability (`CAP_GET_TEXTURE`) is required for authenticated texture fetches. The manager builds the HTTP URL from the cap and uses an OkHttp client with Akamai CDN hostname handling for `asset-cdn.glb.agni.lindenlab.com`.【F:Linkpoint/src/main/java/com/linkpoint/assets/TextureManager.kt†L29-L69】【F:Linkpoint/src/main/java/com/linkpoint/assets/TextureManager.kt†L150-L214】【F:Linkpoint/src/main/java/com/linkpoint/assets/TextureManager.kt†L400-L458】

**UDP**
- **ImageData** / **ImagePacket** (high‑frequency UDP messages) are supported for texture streaming, with handlers to assemble multi‑packet transfers and complete the texture once all packets arrive.【F:Linkpoint/src/main/java/com/linkpoint/assets/TextureManager.kt†L303-L412】【F:Linkpoint/src/main/java/com/linkpoint/protocol/messages/MessageIds.kt†L571-L585】
  - **ImageData shape**: UUID (ImageID), codec (U8), size (S32), packets (U16), followed by initial data chunk; multi‑packet transfers are buffered by texture ID.【F:Linkpoint/src/main/java/com/linkpoint/assets/TextureManager.kt†L311-L341】
  - **ImagePacket shape**: UUID (ImageID), packet number (U16), data chunk; subsequent chunks append to the active transfer buffer.【F:Linkpoint/src/main/java/com/linkpoint/assets/TextureManager.kt†L346-L372】

## Inventory (InventoryManager)

**HTTP/Caps**
- **FetchInventoryDescendents2** (`CAP_FETCH_INVENTORY_DESCENDENTS`) fetches folder contents.
- **FetchInventory2** (`CAP_FETCH_INVENTORY`) fetches specific items.
- **InventoryAPIv3** (`CAP_INVENTORY_API`) provides AISv3 mutations (move items/folders).
- **CreateInventoryCategory** (`CAP_CREATE_INVENTORY_CATEGORY`) creates folders.
- **UpdateInventoryItem** (`CAP_UPDATE_INVENTORY_ITEM`) updates item fields like name/description.【F:Linkpoint/src/main/java/com/linkpoint/inventory/InventoryManager.kt†L108-L206】【F:Linkpoint/src/main/java/com/linkpoint/inventory/InventoryManager.kt†L360-L499】【F:Linkpoint/src/main/java/com/linkpoint/inventory/InventoryManager.kt†L568-L676】
  - **LLSD shapes (caps)**: Inventory caps requests are LLSD maps with arrays of `folders`, `items`, or `categories`. Each item/folder entry is a map with UUIDs stored as strings (e.g., `"item_id"`, `"folder_id"`, `"owner_id"`). Responses return LLSD maps/arrays parsed into `InventoryFolder`/`InventoryItem` objects.【F:Linkpoint/src/main/java/com/linkpoint/inventory/InventoryManager.kt†L108-L206】【F:Linkpoint/src/main/java/com/linkpoint/inventory/InventoryManager.kt†L212-L260】

**UDP**
- **MoveInventoryItem** is used as a fallback when AISv3 is unavailable.
- **CreateInventoryFolder** is used as a fallback when folder creation caps are unavailable.
- **UpdateInventoryItem** is used as a fallback for renames/description updates when caps fail.【F:Linkpoint/src/main/java/com/linkpoint/inventory/InventoryManager.kt†L360-L499】【F:Linkpoint/src/main/java/com/linkpoint/inventory/InventoryManager.kt†L520-L676】【F:Linkpoint/src/main/java/com/linkpoint/protocol/messages/MessageIds.kt†L199-L217】【F:Linkpoint/src/main/java/com/linkpoint/protocol/messages/MessageIds.kt†L309-L318】
  - **MoveInventoryItem shape (fallback)**: AgentData (AgentID, SessionID, Stamp), InventoryData count, then ItemID + FolderID + NewName (empty) for each item moved.【F:Linkpoint/src/main/java/com/linkpoint/inventory/InventoryManager.kt†L385-L429】
  - **CreateInventoryFolder shape (fallback)**: AgentData (AgentID, SessionID), FolderData (FolderID, ParentID, Type), followed by name as variable‑length field.【F:Linkpoint/src/main/java/com/linkpoint/inventory/InventoryManager.kt†L468-L527】
  - **UpdateInventoryItem shape (fallback)**: AgentData (AgentID, SessionID, TransactionID), InventoryData count, then a full item block with IDs, permission masks, flags, and variable‑length name/description fields.【F:Linkpoint/src/main/java/com/linkpoint/inventory/InventoryManager.kt†L631-L706】

## Chat / IM (ChatManager + IMManager)

### Local & Nearby Chat (ChatManager)

**UDP**
- **ChatFromViewer** is sent for outbound chat and typing indicators.
- **ChatFromSimulator** is parsed on inbound chat and typing events.【F:Linkpoint/src/main/java/com/linkpoint/chat/ChatManager.kt†L56-L164】【F:Linkpoint/src/main/java/com/linkpoint/protocol/messages/MessageIds.kt†L78-L104】
  - **ChatFromViewer shape**: AgentData (AgentID, SessionID), ChatData with message (U16 length + bytes + null), type (U8), channel (S32).【F:Linkpoint/src/main/java/com/linkpoint/chat/ChatManager.kt†L115-L164】

### Instant Messages / Sessions (IMManager)

**HTTP/Caps**
- **ChatSessionRequest** (`CAP_CHAT_PASS`) is used for IM send, session start, conference start, and session close (LLSD request/response).【F:Linkpoint/src/main/java/com/linkpoint/chat/IMManager.kt†L276-L405】【F:Linkpoint/src/main/java/com/linkpoint/protocol/capabilities/CapabilityManager.kt†L69-L76】
  - **LLSD shapes (caps)**: IM/session operations send LLSD maps containing `"method"`, `"session-id"`, `"params"` sub‑maps, and `"message"` fields, depending on action (start/join/leave/send). Responses are LLSD maps with `"success"` and optional `"error"` fields.【F:Linkpoint/src/main/java/com/linkpoint/chat/IMManager.kt†L276-L405】

**Event Queue (Caps)**
- **ChatterBoxInvitation**, **ChatterBoxSessionEventReply**, and **ChatterBoxSessionStartReply** are processed via the EventQueue to keep IM sessions in sync (invites, join/leave, typing, etc.).【F:Linkpoint/src/main/java/com/linkpoint/chat/IMManager.kt†L96-L205】

**UDP**
- **ImprovedInstantMessage** is used for typing indicators and IM dialog packets when sending via UDP payloads.【F:Linkpoint/src/main/java/com/linkpoint/chat/IMManager.kt†L405-L519】【F:Linkpoint/src/main/java/com/linkpoint/protocol/messages/MessageIds.kt†L92-L104】
  - **ImprovedInstantMessage shape (typing)**: AgentData (AgentID, SessionID) + MessageBlock (FromGroup, ToAgentID, ParentEstateID, RegionID, Position, Offline, Dialog, SessionID, Timestamp) + variable fields (FromAgentName, Message, BinaryBucket) + EstateBlock + MetaData count. IMManager builds this for typing start/stop packets.【F:Linkpoint/src/main/java/com/linkpoint/chat/IMManager.kt†L413-L519】

## Sound (SoundManager)

Sound playback does **not** issue network calls directly; it uses cached audio data (`AssetCache`) that is fetched elsewhere, then plays back via `SoundPool`. This keeps sound playback independent of caps/UDP logic.【F:Linkpoint/src/main/java/com/linkpoint/assets/SoundManager.kt†L18-L146】

## Meshes (MeshManager)

**HTTP/Caps**
- **GetMesh2** / **GetMesh** capabilities are used to download mesh LOD data. The manager fetches the mesh via HTTP, caches raw data, and parses LLSD headers and compressed geometry payloads locally.【F:Linkpoint/src/main/java/com/linkpoint/assets/MeshManager.kt†L14-L115】

## Scripts (ScriptManager)

**HTTP/Caps**
- **GetScriptRunning**, **SetScriptRunning**, and **GetScriptTaskInfo** are used for script state and info requests (LLSD request/response).【F:Linkpoint/src/main/java/com/linkpoint/assets/ScriptManager.kt†L70-L182】
  - **LLSD shapes (caps)**: Script requests include `"object_id"`, `"item_id"`, and `"running"` boolean flags where appropriate. Script task info responses return `"scripts"` arrays of maps with `"item_id"`, `"name"`, `"running"`, `"mono"`, `"memory"`, and `"time"` fields.【F:Linkpoint/src/main/java/com/linkpoint/assets/ScriptManager.kt†L70-L182】

**UDP/Xfer/Transfer**
- Script source text is fetched via `TransferManager.fetchAsset()` (asset type 10), which uses the SL UDP Transfer protocol under the hood.【F:Linkpoint/src/main/java/com/linkpoint/assets/ScriptManager.kt†L44-L67】【F:Linkpoint/src/main/java/com/linkpoint/protocol/transfer/TransferManager.kt†L37-L120】

## Snapshots (SnapshotManager)

**HTTP/Caps**
- **NewFileAgentInventory** is used to request an upload URL for snapshot textures; the manager then uploads bytes directly to the provided uploader URL and optionally posts to **UploadAgentProfileImage** for profile sharing.【F:Linkpoint/src/main/java/com/linkpoint/snapshot/SnapshotManager.kt†L134-L248】【F:Linkpoint/src/main/java/com/linkpoint/snapshot/SnapshotManager.kt†L250-L307】
  - **LLSD shapes (caps)**: Upload request maps include `"asset_type"`, `"inventory_type"`, `"name"`, `"description"`, and `"expected_upload_cost"`. The response returns an `"uploader"` URL, followed by a second LLSD response containing `"new_asset"` and `"new_inventory_item"`.【F:Linkpoint/src/main/java/com/linkpoint/snapshot/SnapshotManager.kt†L174-L307】

## Transfers (TransferManager + XferManager)

**UDP (Transfer protocol)**
- **TransferRequest → TransferInfo → TransferPacket** flow is used for asset transfers (textures, animations, sounds, etc.), mirroring Lumiya’s transfer manager design.【F:Linkpoint/src/main/java/com/linkpoint/protocol/transfer/TransferManager.kt†L18-L49】【F:Linkpoint/src/main/java/com/linkpoint/protocol/transfer/TransferManager.kt†L146-L210】
  - **TransferRequest shape**: TransferID + ChannelType + SourceType + Priority + Params (asset/item identifiers), encoded as little‑endian blocks in `sendTransferRequest`.【F:Linkpoint/src/main/java/com/linkpoint/protocol/transfer/TransferManager.kt†L349-L372】
  - **TransferInfo shape**: TransferID, status, size, and params (parsed in `handleTransferInfo`).【F:Linkpoint/src/main/java/com/linkpoint/protocol/transfer/TransferManager.kt†L229-L272】
  - **TransferPacket shape**: TransferID, packet number, status, data chunk (parsed in `handleTransferPacket`).【F:Linkpoint/src/main/java/com/linkpoint/protocol/transfer/TransferManager.kt†L283-L333】

**UDP (Xfer protocol)**
- **RequestXfer → SendXferPacket → ConfirmXferPacket** flow handles file‑style transfers (task inventory listings, notecard/script content) using the SL Xfer protocol, matching Lumiya’s approach.【F:Linkpoint/src/main/java/com/linkpoint/protocol/transfer/XferManager.kt†L12-L32】【F:Linkpoint/src/main/java/com/linkpoint/protocol/transfer/XferManager.kt†L78-L214】
  - **RequestXfer shape**: xfer ID (U64) + filename length + filename bytes; the manager creates an Xfer record and waits for data packets.【F:Linkpoint/src/main/java/com/linkpoint/protocol/transfer/XferManager.kt†L80-L108】
  - **SendXferPacket shape**: xfer ID (U64) + packet number (S32) + data length (U16) + data; packet number has FINAL flag (0x80000000) to indicate completion.【F:Linkpoint/src/main/java/com/linkpoint/protocol/transfer/XferManager.kt†L112-L158】
  - **ConfirmXferPacket shape**: xfer ID (U64) + packet number (S32) to acknowledge receipt.【F:Linkpoint/src/main/java/com/linkpoint/protocol/transfer/XferManager.kt†L187-L199】

## Task Inventory (TaskInventoryManager)

**UDP + Xfer**
- **RequestTaskInventory** triggers **ReplyTaskInventory** with a filename, then the listing is delivered via Xfer packets. The manager registers an Xfer handler for `task_inv` files and parses the results into object inventory entries.【F:Linkpoint/src/main/java/com/linkpoint/objects/inventory/TaskInventoryManager.kt†L15-L60】【F:Linkpoint/src/main/java/com/linkpoint/objects/inventory/TaskInventoryManager.kt†L80-L158】
  - **RequestTaskInventory shape**: AgentData (AgentID), then InventoryData with object local ID (S32).【F:Linkpoint/src/main/java/com/linkpoint/objects/inventory/TaskInventoryManager.kt†L137-L163】
  - **ReplyTaskInventory shape**: local ID (S32) + filename length + filename; filename drives the Xfer lookup and final parse step.【F:Linkpoint/src/main/java/com/linkpoint/objects/inventory/TaskInventoryManager.kt†L80-L111】

## Friends (FriendsManager)

**Event Queue (Caps)**
- Friendship offers/accept/decline/terminate and online/offline notifications are handled as EventQueue events via caps registration, consistent with Lumiya’s capability‑driven friend updates.【F:Linkpoint/src/main/java/com/linkpoint/world/FriendsManager.kt†L41-L86】
  - **LLSD shapes (event queue)**: Friend events arrive as LLSD maps with fields like `"from_id"`, `"from_name"`, `"transaction_id"` and `"AgentOnline"` arrays containing maps with `"agent_id"`.【F:Linkpoint/src/main/java/com/linkpoint/world/FriendsManager.kt†L86-L150】

**UDP Fallbacks**
- Explicit UDP handlers exist for Accept/Decline/Form friendship flows when capability events are not used.【F:Linkpoint/src/main/java/com/linkpoint/world/FriendsManager.kt†L162-L214】

## Groups (GroupsManager)

**Event Queue (Caps)**
- **AgentGroupDataUpdate**, **GroupNotice**, and **GroupChat** are processed through the EventQueue.【F:Linkpoint/src/main/java/com/linkpoint/groups/GroupsManager.kt†L32-L87】
  - **LLSD shapes (event queue)**: Group events include `"GroupData"` arrays with per‑group maps, notice payloads with `"group_id"`, `"sender_id"`, `"subject"`, `"message"`, and group chat payloads with `"group_id"`, `"from_id"`, and `"message"`.【F:Linkpoint/src/main/java/com/linkpoint/groups/GroupsManager.kt†L46-L126】

**UDP**
- Active group selection, leaving groups, and group profile requests use UDP messages (e.g., `ACTIVATE_GROUP`, `LEAVE_GROUP_REQUEST`, `GROUP_PROFILE_REQUEST`).【F:Linkpoint/src/main/java/com/linkpoint/groups/GroupsManager.kt†L93-L167】【F:Linkpoint/src/main/java/com/linkpoint/groups/GroupsManager.kt†L244-L295】
  - **ActivateGroup/LeaveGroup shape**: AgentData (AgentID, SessionID) + GroupData (GroupID).【F:Linkpoint/src/main/java/com/linkpoint/groups/GroupsManager.kt†L93-L167】【F:Linkpoint/src/main/java/com/linkpoint/groups/GroupsManager.kt†L244-L283】

**Caps**
- Group chat sessions are started via **ChatSessionRequest** (caps), with fallback to group UUID as session ID if caps fail.【F:Linkpoint/src/main/java/com/linkpoint/groups/GroupsManager.kt†L180-L240】

## Economy (EconomyManager)

**Event Queue (Caps)**
- **MoneyBalanceReply** is registered as an EventQueue handler for balance changes.【F:Linkpoint/src/main/java/com/linkpoint/economy/EconomyManager.kt†L46-L74】
  - **LLSD shapes (event queue)**: Balance events supply `"MoneyBalance"` as an integer in the LLSD map payload.【F:Linkpoint/src/main/java/com/linkpoint/economy/EconomyManager.kt†L46-L74】

**UDP**
- Balance and economy data requests use UDP (`MoneyBalanceRequest`, `MoneyBalanceReply`, `EconomyDataRequest`, `EconomyData`) for L$ balance and upload/price data.【F:Linkpoint/src/main/java/com/linkpoint/economy/EconomyManager.kt†L21-L52】【F:Linkpoint/src/main/java/com/linkpoint/economy/EconomyManager.kt†L116-L210】
  - **MoneyBalanceRequest shape**: AgentData (AgentID, SessionID) + TransactionID (UUID).【F:Linkpoint/src/main/java/com/linkpoint/economy/EconomyManager.kt†L198-L223】
  - **MoneyBalanceReply shape**: MoneyData (RequesterID, TransactionID, success flag, new balance, parcel meters, description).【F:Linkpoint/src/main/java/com/linkpoint/economy/EconomyManager.kt†L76-L124】
  - **EconomyData shape**: Info block with upload price, group create price, teleport fees, and parcel pricing fields.【F:Linkpoint/src/main/java/com/linkpoint/economy/EconomyManager.kt†L128-L176】

## Teleport (TeleportManager)

**Event Queue (Caps)**
- Teleport progress lifecycle events (`TeleportProgress`, `TeleportStart`, `TeleportFinish`, etc.) are delivered by the EventQueue via capability handlers.【F:Linkpoint/src/main/java/com/linkpoint/teleport/TeleportManager.kt†L75-L128】
  - **LLSD shapes (event queue)**: Teleport events include progress/error text and may include new circuit data via `"EstablishAgentCommunication"` (handled by TeleportManager).【F:Linkpoint/src/main/java/com/linkpoint/teleport/TeleportManager.kt†L75-L128】

**Caps + UDP**
- Teleport requests prefer **SimulatorLure** cap when available, and fall back to UDP requests for location, landmark, or home teleports.【F:Linkpoint/src/main/java/com/linkpoint/teleport/TeleportManager.kt†L132-L219】
  - **TeleportLandmarkRequest shape (UDP)**: AgentData (AgentID, SessionID) + landmark UUID + teleport flags (U32).【F:Linkpoint/src/main/java/com/linkpoint/teleport/TeleportManager.kt†L193-L219】
  - **TeleportHomeRequest shape (UDP)**: AgentData (AgentID, SessionID) + teleport flags (U32).【F:Linkpoint/src/main/java/com/linkpoint/teleport/TeleportManager.kt†L221-L255】

## Parcels (ParcelManager) + Media (MediaManager)

**UDP**
- Parcel info requests are sent via `PARCEL_INFO_REQUEST`, and parcel properties are parsed from UDP replies to update `ParcelInfo` (music/media URLs, flags, etc.).【F:Linkpoint/src/main/java/com/linkpoint/world/ParcelManager.kt†L150-L224】
  - **ParcelInfoRequest shape**: AgentData placeholder (filled by UDP layer) + local position (X, Y, Z as S32).【F:Linkpoint/src/main/java/com/linkpoint/world/ParcelManager.kt†L182-L219】

**Local Media Playback**
- Parcel music/media URLs are consumed by `MediaManager` for local streaming playback (no direct caps/UDP calls). This mirrors Lumiya’s client‑side playback model once parcel data is received via UDP.【F:Linkpoint/src/main/java/com/linkpoint/media/MediaManager.kt†L18-L92】

## Search (SearchManager)

**Caps + HTTP**
- Avatar search uses **AvatarPickerSearch** capability, while other categories (places, groups, events) use HTTP search endpoints. Land search uses the **SearchLand** capability.【F:Linkpoint/src/main/java/com/linkpoint/world/SearchManager.kt†L39-L149】【F:Linkpoint/src/main/java/com/linkpoint/world/SearchManager.kt†L174-L220】
  - **LLSD shapes (caps)**: Avatar picker search requests include `"query"`, `"start"`, `"count"`; responses include `"agents"` arrays with `"id"`, `"display_name"`, `"username"`, and `"online"` fields. Land search posts LLSD maps with `"query"`, `"category"`, `"sale_type"`, and size/price filters.【F:Linkpoint/src/main/java/com/linkpoint/world/SearchManager.kt†L39-L149】【F:Linkpoint/src/main/java/com/linkpoint/world/SearchManager.kt†L174-L220】

## Profiles & Display Names (ProfileManager)

**Caps**
- Avatar profiles use **AgentProfile** capability.
- Display names use **GetDisplayNames** capability (single or batch).【F:Linkpoint/src/main/java/com/linkpoint/world/ProfileManager.kt†L39-L140】
  - **LLSD shapes (caps)**: Profile requests send `"agent_id"` and receive maps with profile fields (`"display_name"`, `"sl_about_text"`, `"sl_image_id"`, etc.). Display name requests post `"ids"` arrays and receive `"agents"` arrays with `"id"`/`"display_name"` fields.【F:Linkpoint/src/main/java/com/linkpoint/world/ProfileManager.kt†L39-L140】

## Voice (VoiceManager)

**Caps + WebRTC**
- Voice relies on **ParcelVoiceInfoRequest** and **ProvisionVoiceAccountRequest** capabilities, then uses WebRTC for session transport, matching SL’s modern voice pipeline.【F:Linkpoint/src/main/java/com/linkpoint/voice/VoiceManager.kt†L24-L112】
  - **LLSD shapes (caps)**: Voice info returns `"channel_uri"`/`"channel_credentials"` and optional `"voice_account_server_uri"`. Provisioning returns `"username"`, `"password"`, and `"voice_sip_uri_hostname"` for WebRTC/SIP setup.【F:Linkpoint/src/main/java/com/linkpoint/voice/VoiceManager.kt†L92-L128】

## Environment / EEP (EnvironmentManager)

**Caps**
- Region environment settings are fetched using **ExtEnvironment** (preferred) or **EnvironmentSettings** capability and parsed into sky/water settings.【F:Linkpoint/src/main/java/com/linkpoint/world/environment/EnvironmentManager.kt†L46-L118】
  - **LLSD shapes (caps)**: Environment maps contain `"sky"`, `"water"`, and optional `"day_cycle"` maps; fields are arrays/values for colors, rotations, and scalars that are parsed into `SkySettings`/`WaterSettings`.【F:Linkpoint/src/main/java/com/linkpoint/world/environment/EnvironmentManager.kt†L96-L174】

## Bakes on Mesh (BakesOnMeshManager)

**Caps + UDP interplay**
- BoM rebake requests use **UploadAgentBakedTexture** capability; the resulting baked textures are applied when `AgentCachedTexture` or appearance updates arrive via UDP and are passed into this manager.【F:Linkpoint/src/main/java/com/linkpoint/bom/BakesOnMeshManager.kt†L150-L222】
  - **AgentCachedTexture / appearance updates**: baked texture UUIDs are mapped to bake channels and cached per avatar for BoM resolution.【F:Linkpoint/src/main/java/com/linkpoint/bom/BakesOnMeshManager.kt†L160-L214】
  - **LLSD shapes (caps)**: Rebake request posts `"agent_id"` and expects an LLSD map response; baked texture UUIDs arrive later via UDP messages that carry texture indices/UUIDs which are routed into this manager.【F:Linkpoint/src/main/java/com/linkpoint/bom/BakesOnMeshManager.kt†L150-L222】

---

This map now covers the most network‑sensitive managers and mirrors Lumiya’s classic pattern: **caps for HTTP services, UDP for real‑time messages, with explicit UDP fallbacks when caps are missing**.【F:docs/NETWORKING_LINKPOINT_LUMIYA.md†L5-L52】
