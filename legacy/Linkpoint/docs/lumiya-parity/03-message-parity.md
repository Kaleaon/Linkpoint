# Segment 03 — UDP Message Parity

**Priority:** High. Lumiya implements 475 of 483 SL UDP messages
(98.3% coverage per Lumiya-Redux ARCHITECTURE.md). Linkpoint's parity sweep
in the original `LUMIYA_COMPARISON_WORKLIST.md` flagged seven specific
constants without handlers; this segment expands the list and clarifies the
intent for each.

Reference:
`lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/messages/`
(one Java class per message block).

---

## 1. Coverage targets

| Tier | Coverage | Required by |
|---|---|---|
| Tier 1 — Connection / agent / chat / IM / teleport / region | 100% | day-one usability |
| Tier 2 — Inventory / objects / parcels / groups / friends / money / search | 100% | feature parity |
| Tier 3 — Estate, voice, classifieds, dwell, agent-data updates | 95%+ | full parity |
| Tier 4 — Deprecated / rare (e.g. Xfer legacy) | implement-or-document | conformance |

Message template lives at
`/home/user/Linkpoint/Linkpoint/recovered/reference/message_template.msg`
in Lumiya-Redux (mirrored from `secondlife/master-message-template`). Run
`tools/protocol/run_conformance.sh` (Lumiya-Redux convention) on every
`slproto/**` change.

---

## 2. Known gaps (from existing worklist) — expanded

| MessageId | Lumiya class | Reliable? | Notes |
|---|---|---|---|
| `MOVE_INVENTORY_ITEM` | `MoveInventoryItem.java` | reliable | Reorganize folder hierarchy. Used heavily during outfit edits. |
| `UPDATE_TASK_INVENTORY` | `UpdateTaskInventory.java` | reliable | In-world object inventory edit. Pairs with `RequestTaskInventory` and `ReplyTaskInventory`. |
| `PARCEL_BUY` | `ParcelBuy.java` | reliable | Land purchase. `PriceID`, `Price`, `RemoveContribution`. Auth is server-side; client must pack agent + group. |
| `OBJECT_GRAB` / `OBJECT_DEGRAB` | `ObjectGrab.java`, `ObjectDeGrab.java` | unreliable | Touch/click. Sent fast; expect the sim to rate-limit. |
| `KICK_USER` | `KickUser.java` | reliable | Estate-owner only. Pair with `EstateOwnerMessage`. |
| `UPDATE_USER_INFO` | not present in 3.4.2 dump | n/a | Profile email update. Likely cap-only on modern SL; document as "deferred to caps". |
| `FIND_AGENT` | `FindAgent.java` | reliable | World-map "find friend" target. Reply is `FindAgent` echoed back. |

---

## 3. Reliability matrix (must-fix beyond gap list)

For every message Linkpoint sends, set `reliable` correctly. Wrong choice
silently breaks features.

### 3.1 Must be reliable

`UseCircuitCode`, `CompleteAgentMovement`, `LogoutRequest`, `AgentThrottle`,
`AgentSetAppearance`, `AgentIsNowWearing`, `RegionHandshakeReply`,
all `Teleport*Request` variants, `StartLure`, `TeleportLureRequest`,
all `Inventory*Item` (Update / UpdateCreate / Move / Copy / Remove),
`CreateInventoryFolder`, `RemoveInventoryFolder`, all `Object*` mutators
(Add / Delete / Link / Delink / Name / Description / Position / Select /
Properties — except Grab/DeGrab), `MoneyTransferRequest`, `MoneyBalanceRequest`,
`ParcelBuy`, `ParcelRelease`, `ParcelDeedToGroup`, `ParcelPropertiesUpdate`,
`ParcelAccessListUpdate`, `ChatFromViewer` (reliable on Lumiya),
`ImprovedInstantMessage`, `RetrieveInstantMessages`, `ScriptDialogReply`,
`AcceptFriendship`, `DeclineFriendship`, `FormFriendship`, `TerminateFriendship`,
`GrantUserRights`, `ChangeUserRights`, `ActivateGroup`, `LeaveGroupRequest`,
`GroupNoticeAdd`, `KickUser`, `FreezeUser`, `EstateOwnerMessage`,
`FindAgent`, `RezSingleAttachmentFromInv`, `RezObject`, `DeRezObject`,
`StartPingCheck`, `PacketAck` (technically not flagged reliable but is the
ack vehicle).

### 3.2 Must be unreliable

`AgentUpdate`, `AgentAnimation`, `ViewerEffect`, `SoundTrigger`,
`ObjectGrab`, `ObjectDeGrab` (movement-rate, lossy by design), `MultipleObjectUpdate` (per Lumiya).

### 3.3 Linkpoint audit task

Grep `sendPacket(` and `SendMessage(` call sites; cross-reference each with
the matrix above. Add a unit test: for each `MessageIds` constant, assert
the expected reliability.

---

## 4. Capability-only messages (do not implement on UDP)

Modern SL has migrated these from UDP to HTTP capabilities. Linkpoint should
**not** wire UDP handlers for them — the seed cap will return them, and the
event-queue / cap-poll will deliver replies:

| Capability | Replaces UDP message |
|---|---|
| `FetchInventory2`, `FetchInventoryDescendents2`, `FetchLib2` | `FetchInventory*`, `InventoryDescendents` UDP path (UDP path remains as fallback per protocol-conformance doc) |
| `GetTexture`, `GetMesh`, `GetMesh2` | `RequestImage`/`ImageData`/`ImagePacket`, mesh Xfer |
| `ChatSessionRequest`, `ChatterBoxSessionStartReply`, `ChatterBoxInvitation` | group-IM session bring-up |
| `EventQueueGet` | server-push channel; carries teleport finish, online/offline, chat invitations, etc. |
| `EnvironmentSettings`, `ExtEnvironment` | windlight/extended environment |
| `AgentPreferences`, `AgentState` | agent runtime prefs |
| `AvatarPickerSearch`, `GetDisplayNames` | display-name and search |
| `GroupMemberData`, `HomeLocation` | group/home-region info |

The debug report shows all 17 of these caps are present on the live
session; Linkpoint must consume them, not duplicate them on UDP.

---

## 5. Concrete work items

| ID | Item |
|---|---|
| L03-A | Generate a coverage matrix: for each entry in `message_template.msg`, list (a) Lumiya implementation status, (b) Linkpoint `MessageIds` constant, (c) whether registered in `LinkpointApp.kt` handler wiring, (d) reliability flag |
| L03-B | Add the seven gap-list messages with proper Lumiya-derived field layout |
| L03-C | Write the reliability assertion test described in §3.3 |
| L03-D | Document deliberate omissions in `tools/protocol/message_template_mismatches.txt` (Lumiya-Redux convention) |
| L03-E | Run `verify_message_template_conformance.py` in CI for every PR touching `slproto/**` |
| L03-F | Replace all hard-throw `TODO` stubs in `GeneratedParserScaffolding.kt` (worklist item #4 in original doc) with either real parsers or `error("unhandled $messageId — file an issue")` that's caught at runtime |

---

## 6. Cross-references

- Segment 01 — for the reliable-packet ACK/resend lifecycle
- Segment 04 — for the identity-block packing (every reliable mutator
  carries `AgentID`/`SessionID`)
- Segment 06 — for asset / texture transfer messages
- Segment 07 — for inventory-specific dual-path UDP+HTTP fetch
