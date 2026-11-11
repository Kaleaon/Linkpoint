# Segment 08 — Chat & Event Hierarchy

**Priority:** Medium-High. Chat is *not* just text — in SL it's the
delivery vehicle for friendship offers, item offers, group invites,
teleport lures, script dialogs, payment receipts, and RLV commands.
A flat "chat message list" model is wrong.

References: `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/chat/`,
`slproto/users/chatsrc/`, `ui/chat/`.

---

## 1. The Lumiya chat-event hierarchy

`SLChatEvent` is the polymorphic base. Concrete subclasses:

| Event | Source | UI affordance |
|---|---|---|
| `SLChatTextEvent` | regular local/regional/shout | render text |
| `SLChatSystemMessageEvent` | system | render with system styling |
| `SLChatTextBoxDialog` | `ScriptDialog` text input | input field + submit |
| `SLChatScriptDialog` | `ScriptDialog` with buttons | button list |
| `SLChatFriendshipOfferedEvent` | `ImprovedInstantMessage` (dialog 38) | accept / decline |
| `SLChatFriendshipResultEvent` | `ImprovedInstantMessage` (dialog 39/40) | toast |
| `SLChatGroupInvitationEvent` | `ImprovedInstantMessage` (dialog 22) | accept / decline; pays L$ join fee |
| `SLChatLureEvent` | `ImprovedInstantMessage` (dialog 22 lure) | accept teleport / decline |
| `SLChatLureRequestEvent` | request *from* friend to teleport us | inverse direction |
| `SLChatInventoryItemOfferedEvent` | `ImprovedInstantMessage` (dialog 4/5) | accept (place in folder) / decline |
| `SLChatInventoryItemOfferedByGroupNoticeEvent` | group notice with attachment | similar |
| `SLChatBalanceChangedEvent` | money tx receipt | toast / balance update |
| `SLChatOnlineOfflineEvent` | friend online/offline | toast / radar update |
| `SLChatPermissionRequestEvent` | `ScriptQuestion` | grant / deny |
| `SLEnableRLVOfferEvent` | RLV restriction request | accept / decline |
| `SLMissedVoiceCallEvent` | voice plugin | toast |
| `SLVoiceUpgradeEvent` | voice plugin requirement | install offer |

### 1.1 Chat constants (mirror `SLChatEvent`)

```
CHAT_TYPE_NORMAL    = 1
CHAT_TYPE_SHOUT     = 2
CHAT_TYPE_DEBUG_MSG = 6
CHAT_TYPE_REGION    = 7
CHAT_SOURCE_AGENT   = 1
CHAT_SOURCE_OBJECT  = 2
CHAT_SOURCE_SYSTEM  = 0
CHAT_AUDIBLE_FULLY  = 1
CHAT_AUDIBLE_BARELY = 0
CHAT_AUDIBLE_NOT    = -1
```

---

## 2. ImprovedInstantMessage dialog codes

`ImprovedInstantMessage.Dialog` (uint8) is overloaded across many event
types. Linkpoint must dispatch on this byte, not parse it as raw text.

Common codes (from LL message template + Lumiya handler):

| Code | Meaning |
|---|---|
| 0 | regular IM |
| 1 | message-from-agent (notification) |
| 4 | inventory offered |
| 5 | inventory accepted (echo) |
| 9 | message-from-object (object-owned chat) |
| 17 | start typing |
| 18 | stop typing |
| 22 | friendship/group invite/lure (sub-encoded by binary bucket) |
| 32 | session add (group IM session bring-up) |
| 38 | friendship offered |
| 39 | friendship accepted |
| 40 | friendship declined |

---

## 3. Group IM session lifecycle

Group chat is **not** sent as `ImprovedInstantMessage` over UDP — it's
session-based via the `ChatSessionRequest` cap and event-queue messages
`ChatterBoxSessionStartReply` / `ChatterBoxInvitation` /
`ChatterBoxSessionEventReply`.

| Step | Mechanism |
|---|---|
| Discover invites | EventQueue: `ChatterBoxInvitation` |
| Join session | HTTP cap: `ChatSessionRequest` with `method = accept` |
| Send message | HTTP cap: `ChatSessionRequest` with `method = sendchat` |
| Receive messages | EventQueue: `ChatterBoxSessionEventReply` |
| Leave | HTTP cap: `ChatSessionRequest` with `method = mute update` etc. |

Linkpoint's debug report shows all four `ChatterBox*` event handlers
registered — verify they route to the correct UI surface.

---

## 4. Auto-response / busy mode

Lumiya `SLGridConnection.setAutoresponseInfo(enabled, text)` and
`getAutoresponse()` (lines 135-140, 156-159).

| ID | Item |
|---|---|
| L08-A | Settings entry for "Auto-respond when away" with a static text field |
| L08-B | When enabled, every inbound IM (dialog 0) triggers an auto-reply |
| L08-C | Auto-reply rate-limited per sender (one reply per N minutes) to avoid loops with other auto-responders |

---

## 5. Typing indicators

Bidirectional, fragile, optional. Lumiya implements per-IM-window typing
via dialog codes 17/18.

| ID | Item |
|---|---|
| L08-D | When user types in IM, send `ImprovedInstantMessage(dialog=17)` debounced (start typing) |
| L08-E | When user stops, send `ImprovedInstantMessage(dialog=18)` (stop typing) |
| L08-F | On inbound 17/18, show/hide typing indicator |

---

## 6. Concrete work items

| ID | Item |
|---|---|
| L08-G | Implement the full event hierarchy as a sealed class / sealed interface in Kotlin |
| L08-H | Each event subclass has its own UI affordance — don't render them all as plain text |
| L08-I | Inbound `ImprovedInstantMessage` dispatcher: decode `Dialog` byte, decode binary bucket, construct correct subclass |
| L08-J | Outbound: convenience builders for each event type that pack the right `Dialog` and binary bucket |
| L08-K | Chat history persistence (`ChatMessage` DAO) keyed by chatter UUID; export via `ExportChatHistoryTask` analogue |
| L08-L | Notification: per-event-type sound / LED / vibration (Lumiya has these in settings) |

---

## 7. Cross-references

- Segment 03 — message parity (`ChatFromViewer`, `ImprovedInstantMessage`,
  `RetrieveInstantMessages`, `ScriptDialogReply` reliability)
- Segment 10 — RLV (`SLEnableRLVOfferEvent` + restriction parsing)
- Segment 11 — UI (chat fragment, IM tabs)
- Segment 12 — persistence (chat history DAO)
