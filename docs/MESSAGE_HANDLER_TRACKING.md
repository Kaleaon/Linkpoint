# Second Life Protocol Message Handler Tracking

> **Generated:** January 24, 2026  
> **Updated:** January 24, 2026 - Added Phase 3 (100 more handlers)
> **Purpose:** Track implementation status of all SL protocol message handlers  
> **Source:** Lumiya decompiled source (`lumiya_decompiled_source/`)

This document tracks all 481 message handlers from the Lumiya viewer and their implementation status in Linkpoint.

---

## Message ID Encoding

Second Life uses three message frequency levels encoded differently:

| Frequency | Wire Format | Internal ID | Example |
|-----------|-------------|-------------|---------|
| High | 1 byte (0x01-0xFE) | Same as byte | 12 = ObjectUpdate |
| Medium | 0xFF + 1 byte | byte \| 65280 | 65289 = ObjectProperties |
| Low | 0xFF 0xFF + 2 bytes | short \| -65536 | -65388 = RegionHandshake |
| Special | 0xFB | -5 | PacketAck |

---

## Implementation Status Summary

| Status | Count | Description |
|--------|-------|-------------|
| ✅ Implemented | 181 | Fully implemented with parser and handler |
| 🔲 ID Defined | ~50 | Message ID constant exists, no handler |
| ❌ Not Started | ~250 | Not yet ported from Lumiya |

**Phase 1 (9 handlers):**
- ✅ TeleportFinish, TeleportFailed, TeleportProgress, TeleportStart
- ✅ AlertMessage, AgentAlertMessage
- ✅ EnableSimulator, CrossedRegion, ParcelProperties

**Phase 2 (50 handlers):**
- ✅ Script/Dialog: ScriptDialog, ScriptQuestion, LoadURL
- ✅ Economy: MoneyBalanceReply, EconomyData
- ✅ Inventory: InventoryDescendents, FetchInventoryReply, BulkUpdateInventory, etc.
- ✅ Avatar: AvatarAppearance, AgentWearablesUpdate, AvatarPropertiesReply, etc.
- ✅ Groups: GroupProfileReply, GroupMembersReply, GroupRoleDataReply, etc.
- ✅ Friends: AcceptFriendship, DeclineFriendship, FormFriendship
- ✅ Map/Search: MapBlockReply, MapItemReply, DirPlacesReply, etc.
- ✅ Region: RegionInfo, SimStats, EstateCovenantReply
- ✅ Parcel: ParcelInfoReply, ParcelAccessListReply, ParcelDwellReply
- ✅ Objects: ObjectPropertiesFamily, ObjectAdd
- ✅ Sound: AttachedSound, AttachedSoundGainChange, PreloadSound
- ✅ Effects: ViewerEffect
- ✅ Transfer: TransferInfo, TransferPacket, AbortXfer, ImageNotInDatabase
- ✅ Misc: MeanCollisionAlert, AvatarSitResponse, CameraConstraint, etc.

**Phase 3 (100 handlers - NEW):**
- ✅ High Freq: NeighborList, RequestImage, ImageData, ImagePacket, EdgeDataPacket, etc.
- ✅ Agent: AgentPause, AgentResume, AgentDropGroup, AgentWearablesRequest
- ✅ Avatar: AvatarPickerReply, AvatarNotesReply, AvatarPicksReply, AvatarClassifiedReply
- ✅ Classified: ClassifiedInfoReply
- ✅ Pick/Event: PickInfoReply, EventInfoReply
- ✅ Groups Extended: GroupRoleMembersReply, GroupNoticesListReply, CreateGroupReply, etc.
- ✅ Calling Cards: OfferCallingCard, AcceptCallingCard, DeclineCallingCard
- ✅ Inventory Extended: FetchInventory, FetchInventoryDescendents, UpdateInventoryFolder, etc.
- ✅ Task Inventory: RequestTaskInventory, ReplyTaskInventory
- ✅ Objects Extended: ObjectDuplicate, ObjectScale, ObjectRotation, ObjectPosition, etc.
- ✅ Land: ModifyLand, UndoLand
- ✅ Parcel Extended: ParcelPropertiesRequest, ParcelMediaCommandMessage, etc.
- ✅ Economy Extended: MoneyTransferRequest, RoutedMoneyBalanceReply, PayPriceReply
- ✅ Scripts Extended: ScriptRunningReply, ScriptReset, ScriptSensorReply, etc.
- ✅ Transfer: TransferRequest, TransferAbort, RequestXfer, AssetUploadRequest/Complete
- ✅ Region: RequestRegionInfo, SimulatorViewerTimeMessage, TeleportLocal, SimCrashed
- ✅ Map Extended: MapBlockRequest, MapNameRequest, MapLayerRequest, MapItemRequest
- ✅ Mute: MuteListRequest, UpdateMuteListEntry, RemoveMuteListEntry, etc.
- ✅ User Info: UserInfoRequest, UserInfoReply
- ✅ System: GenericMessage, SystemMessage, ErrorMessage, FeatureDisabled, etc.
- ✅ Attachments: RezMultipleAttachmentsFromInv, DetachAttachmentIntoInv, etc.
- ✅ Rez/DeRez: RezObjectFromNotecard, RezRestoreToWorld, RezScript, DeRezAck
- ✅ Misc: Undo, Redo, SetAlwaysRun, InitiateDownload, PlacesReply, etc.

---

## HIGH FREQUENCY MESSAGES (1-29)

These are the most common messages, sent frequently during normal operation.

| ID | Message | Linkpoint Status | Priority | Notes |
|----|---------|------------------|----------|-------|
| 1 | StartPingCheck | ✅ Implemented | Critical | Network health |
| 2 | CompletePingCheck | 🔲 ID Only | Critical | Response to ping |
| 3 | NeighborList | ✅ Implemented | Low | Neighboring sims |
| 4 | AgentUpdate | 🔲 ID Only | Critical | Viewer->Sim position |
| 5 | AgentAnimation | 🔲 ID Only | Medium | Start/stop anims |
| 6 | AgentRequestSit | 🔲 ID Only | Medium | Sit request |
| 7 | AgentSit | 🔲 ID Only | Medium | Sit confirm |
| 8 | RequestImage | ✅ Implemented | High | Texture request |
| 9 | ImageData | ✅ Implemented | High | Texture header |
| 10 | ImagePacket | ✅ Implemented | High | Texture data |
| 11 | LayerData | ✅ Implemented | Critical | Terrain data |
| 12 | ObjectUpdate | ✅ Implemented | Critical | Full object data |
| 13 | ObjectUpdateCompressed | ✅ Implemented | Critical | Compressed objects |
| 14 | ObjectUpdateCached | ✅ Implemented | Critical | Cache references |
| 15 | ImprovedTerseObjectUpdate | ✅ Implemented | Critical | Position updates |
| 16 | KillObject | ✅ Implemented | Critical | Object removed |
| 17 | TransferPacket | ❌ Not Started | High | Asset transfer |
| 18 | SendXferPacket | ❌ Not Started | High | Xfer data |
| 19 | ConfirmXferPacket | ❌ Not Started | High | Xfer ACK |
| 20 | AvatarAnimation | ✅ Implemented | High | Avatar anim state |
| 21 | AvatarSitResponse | ❌ Not Started | Medium | Sit response |
| 22 | CameraConstraint | ❌ Not Started | Low | Camera limits |
| 23 | ParcelProperties | ❌ Not Started | High | Parcel info |
| 24 | EdgeDataPacket | ❌ Not Started | Low | Region edge |
| 25 | ChildAgentUpdate | ❌ Not Started | Low | Child sim |
| 26 | ChildAgentAlive | ❌ Not Started | Low | Child keepalive |
| 27 | ChildAgentPositionUpdate | ❌ Not Started | Low | Child position |
| 28 | AtomicPassObject | ❌ Not Started | Low | Cross-sim pass |
| 29 | SoundTrigger | ✅ Implemented | Medium | Script sounds |

---

## MEDIUM FREQUENCY MESSAGES (65281-65297)

| ID | Message | Linkpoint Status | Priority | Notes |
|----|---------|------------------|----------|-------|
| 65281 | ObjectAdd | ❌ Not Started | Medium | Create object |
| 65282 | MultipleObjectUpdate | 🔲 ID Only | Medium | Batch updates |
| 65283 | RequestMultipleObjects | 🔲 ID Only | High | Cache miss |
| 65284 | ObjectPosition | ❌ Not Started | Medium | Position only |
| 65285 | RequestObjectPropertiesFamily | ❌ Not Started | Medium | Quick props |
| 65286 | CoarseLocationUpdate | ✅ Implemented | High | Minimap dots |
| 65287 | CrossedRegion | ❌ Not Started | Critical | Region crossing |
| 65288 | ConfirmEnableSimulator | ❌ Not Started | High | Neighbor confirm |
| 65289 | ObjectProperties | ✅ Implemented | High | Full object props |
| 65290 | ObjectPropertiesFamily | ❌ Not Started | Medium | Family props |
| 65291 | ParcelPropertiesRequest | ❌ Not Started | Medium | Request parcel |
| 65292 | SimStatus | ❌ Not Started | Low | Sim health |
| 65293 | AttachedSound | ❌ Not Started | Medium | Attached sounds |
| 65294 | AttachedSoundGainChange | ❌ Not Started | Low | Sound volume |
| 65295 | PreloadSound | ❌ Not Started | Low | Sound preload |
| 65296 | InternalScriptMail | ❌ Not Started | Low | Internal mail |
| 65297 | ViewerEffect | ❌ Not Started | Medium | Effects (beams) |

---

## LOW FREQUENCY MESSAGES (Negative IDs)

### Critical Messages (Must Have)

| ID | Message | Linkpoint Status | Priority | Notes |
|----|---------|------------------|----------|-------|
| -65533 | UseCircuitCode | 🔲 ID Only | Critical | Circuit establish |
| -65388 | RegionHandshake | ✅ Implemented | Critical | Region info |
| -65387 | RegionHandshakeReply | 🔲 ID Only | Critical | Reply |
| -65397 | ChatFromSimulator | ✅ Implemented | Critical | Chat recv |
| -65456 | ChatFromViewer | 🔲 ID Only | Critical | Chat send |
| -65287 | CompleteAgentMovement | 🔲 ID Only | Critical | Enter region |
| -65286 | AgentMovementComplete | ✅ Implemented | Critical | Entry confirmed |
| -65282 | ImprovedInstantMessage | 🔲 ID Only | Critical | IMs/Notices |
| -65222 | MoneyBalanceReply | ❌ Not Started | High | L$ balance |
| -65467 | TeleportFinish | ❌ Not Started | Critical | TP complete |
| -65462 | TeleportFailed | ❌ Not Started | Critical | TP error |
| -65385 | EnableSimulator | ❌ Not Started | Critical | Neighbor sim |
| -65384 | DisableSimulator | ❌ Not Started | High | Close neighbor |

### Agent & Avatar Messages

| ID | Message | Linkpoint Status | Priority | Notes |
|----|---------|------------------|----------|-------|
| -65149 | AgentDataUpdate | ✅ Implemented | High | Groups/title |
| -65147 | AgentGroupDataUpdate | ❌ Not Started | Medium | Group list |
| -65154 | AgentWearablesUpdate | ❌ Not Started | High | Outfit |
| -65152 | AgentCachedTexture | ❌ Not Started | High | Baked cache |
| -65151 | AgentCachedTextureResponse | ❌ Not Started | High | Cache response |
| -65378 | AvatarAppearance | ❌ Not Started | High | Full appearance |
| -65365 | AvatarPropertiesReply | ❌ Not Started | Medium | Profile |

### Friends & Social

| ID | Message | Linkpoint Status | Priority | Notes |
|----|---------|------------------|----------|-------|
| -65214 | OnlineNotification | ✅ Implemented | High | Friend online |
| -65213 | OfflineNotification | ✅ Implemented | High | Friend offline |
| -65215 | ChangeUserRights | ✅ Implemented | Medium | Friend perms |
| -65216 | GrantUserRights | 🔲 ID Only | Medium | Grant perms |
| -65236 | TerminateFriendship | 🔲 ID Only | Low | Unfriend |
| -65239 | AcceptFriendship | ❌ Not Started | Medium | Accept friend |

### Groups

| ID | Message | Linkpoint Status | Priority | Notes |
|----|---------|------------------|----------|-------|
| -65168 | ActivateGroup | 🔲 ID Only | Medium | Set active |
| -65185 | GroupProfileRequest | 🔲 ID Only | Medium | Request info |
| -65184 | GroupProfileReply | ❌ Not Started | Medium | Group info |
| -65169 | GroupMembersReply | ❌ Not Started | Medium | Members |
| -65164 | GroupRoleDataReply | ❌ Not Started | Low | Roles |
| -65475 | GroupNoticeAdd | ❌ Not Started | Medium | New notice |

### Inventory

| ID | Message | Linkpoint Status | Priority | Notes |
|----|---------|------------------|----------|-------|
| -65258 | InventoryDescendents | ❌ Not Started | High | Folder contents |
| -65256 | FetchInventoryReply | ❌ Not Started | High | Item details |
| -65255 | BulkUpdateInventory | ❌ Not Started | High | Batch update |
| -65269 | UpdateCreateInventoryItem | ❌ Not Started | High | Item created |
| -65270 | UpdateInventoryItem | 🔲 ID Only | Medium | Item modified |
| -65246 | ReplyTaskInventory | ❌ Not Started | Medium | Object contents |

### Objects

| ID | Message | Linkpoint Status | Priority | Notes |
|----|---------|------------------|----------|-------|
| -65426 | ObjectSelect | 🔲 ID Only | High | Select object |
| -65347 | ScriptControlChange | ✅ Implemented | Medium | Script controls |
| -65346 | ScriptDialog | ❌ Not Started | High | Dialog box |
| -65348 | ScriptQuestion | ❌ Not Started | High | Permission req |
| -65342 | LoadURL | ❌ Not Started | Medium | Open URL |

### Parcels & Land

| ID | Message | Linkpoint Status | Priority | Notes |
|----|---------|------------------|----------|-------|
| -65340 | ParcelOverlay | ✅ Implemented | High | Parcel borders |
| -65481 | ParcelInfoReply | ❌ Not Started | Medium | Parcel details |
| -65320 | ParcelAccessListReply | ❌ Not Started | Low | Access list |
| -65317 | ParcelDwellReply | ❌ Not Started | Low | Traffic |

### Teleport

| ID | Message | Linkpoint Status | Priority | Notes |
|----|---------|------------------|----------|-------|
| -65473 | TeleportLocationRequest | 🔲 ID Only | High | TP request |
| -65471 | TeleportLandmarkRequest | 🔲 ID Only | Medium | LM teleport |
| -65470 | TeleportProgress | ❌ Not Started | High | TP status |
| -65466 | StartLure | 🔲 ID Only | Medium | Send TP offer |
| -65465 | TeleportLureRequest | 🔲 ID Only | Medium | Accept lure |

### Economy

| ID | Message | Linkpoint Status | Priority | Notes |
|----|---------|------------------|----------|-------|
| -65511 | EconomyData | ❌ Not Started | Medium | Region economy |
| -65225 | MoneyTransferRequest | ❌ Not Started | High | Pay L$ |

### Health & Status

| ID | Message | Linkpoint Status | Priority | Notes |
|----|---------|------------------|----------|-------|
| -65398 | HealthMessage | ✅ Implemented | Medium | Agent health |
| -65401 | AgentAlertMessage | ❌ Not Started | High | Alert popup |
| -65402 | AlertMessage | ❌ Not Started | High | System alert |
| -65400 | MeanCollisionAlert | ❌ Not Started | Low | Collision warn |

### Map & Search

| ID | Message | Linkpoint Status | Priority | Notes |
|----|---------|------------------|----------|-------|
| -65127 | MapBlockReply | ❌ Not Started | Medium | Map tiles |
| -65125 | MapItemReply | ❌ Not Started | Medium | Map markers |
| -65501 | DirPlacesReply | ❌ Not Started | Low | Place search |
| -65500 | DirPeopleReply | ❌ Not Started | Low | People search |
| -65498 | DirGroupsReply | ❌ Not Started | Low | Group search |

### Estate & Region

| ID | Message | Linkpoint Status | Priority | Notes |
|----|---------|------------------|----------|-------|
| -65276 | EstateOwnerMessage | 🔲 ID Only | Medium | Estate commands |
| -65332 | EstateCovenantReply | ❌ Not Started | Low | Covenant text |
| -65394 | RegionInfo | ❌ Not Started | Medium | Region settings |
| -65396 | SimStats | ❌ Not Started | Low | FPS/agents |

### Assets & Transfers

| ID | Message | Linkpoint Status | Priority | Notes |
|----|---------|------------------|----------|-------|
| -65382 | TransferInfo | ❌ Not Started | High | Transfer start |
| -65379 | AbortXfer | ❌ Not Started | Medium | Cancel xfer |
| -65450 | ImageNotInDatabase | ❌ Not Started | Medium | Texture missing |

---

## Implementation Priority Queue

### Phase 1: Core Functionality (CRITICAL)
These handlers are required for basic viewer operation:

1. ❌ `TeleportFinish` (-65467) - Complete teleports
2. ❌ `TeleportFailed` (-65462) - Handle TP errors
3. ❌ `TeleportProgress` (-65470) - Show TP status
4. ❌ `EnableSimulator` (-65385) - Neighbor regions
5. ❌ `AgentAlertMessage` (-65401) - Important alerts
6. ❌ `AlertMessage` (-65402) - System messages
7. ❌ `CrossedRegion` (65287) - Region crossings
8. ❌ `ParcelProperties` (23) - Parcel info on arrival

### Phase 2: Social Features (HIGH)
Required for communication and social interaction:

1. ❌ `ScriptDialog` (-65346) - Dialog popups
2. ❌ `ScriptQuestion` (-65348) - Permissions
3. ❌ `MoneyBalanceReply` (-65222) - L$ balance
4. ❌ `InventoryDescendents` (-65258) - Inventory
5. ❌ `BulkUpdateInventory` (-65255) - Inventory updates
6. ❌ `AvatarAppearance` (-65378) - Other avatars

### Phase 3: Polish (MEDIUM)
Nice to have for complete experience:

1. ❌ `GroupProfileReply` (-65184) - Group info
2. ❌ `MapBlockReply` (-65127) - World map
3. ❌ `ViewerEffect` (65297) - Particle effects
4. ❌ `AttachedSound` (65293) - Object sounds
5. ❌ `EconomyData` (-65511) - Region economy

---

## Quick Reference: Message ID Conversion

To convert Lumiya's internal IDs to wire format:

```kotlin
// High frequency (1-254)
wireFormat = id.toByte()  // Single byte

// Medium frequency (65281-65535)  
wireFormat = byteArrayOf(0xFF.toByte(), (id - 65280).toByte())

// Low frequency (negative values)
val shortValue = (id and 0xFFFF).toShort()
wireFormat = byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 
    (shortValue shr 8).toByte(), shortValue.toByte())
```

---

## Handler Implementation Template

When adding a new handler:

```kotlin
// 1. Add ID to MessageIds.kt
const val NEW_MESSAGE = -65XXX  // Wire: FF FF XX XX

// 2. Add parser to MessageParser.kt
data class NewMessageData(...)
fun MessageParser.parseNewMessage(data: ByteArray): NewMessageData?

// 3. Register handler in LinkpointApp.kt
udpConnection.registerHandler(MessageIds.NEW_MESSAGE) { _, rawPacket ->
    val payload = MessageParser.extractPayload(rawPacket) ?: return@registerHandler
    val data = MessageParser.parseNewMessage(payload)
    // Process data...
}
```

---

## Files to Modify

| File | Purpose |
|------|---------|
| `Linkpoint/src/main/java/com/linkpoint/protocol/messages/MessageIds.kt` | Message ID constants |
| `Linkpoint/src/main/java/com/linkpoint/protocol/messages/MessageParser.kt` | Parsers |
| `Linkpoint/src/main/java/com/linkpoint/LinkpointApp.kt` | Handler registration |
| `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/messages/*.java` | Reference implementations |

---

## Contribution Guidelines

When implementing handlers:

1. **Check Lumiya first** - The decompiled source has working implementations
2. **Match the ID exactly** - Use the same internal representation
3. **Handle zero-coding** - Many messages are zero-coded
4. **Parse all fields** - Even if we don't use them yet
5. **Update this document** - Mark status as ✅ when done
