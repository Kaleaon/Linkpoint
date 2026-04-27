# Segment 10 — Restrained Love Viewer (RLV)

**Priority:** Medium. Optional feature — but if we say we support it, we
need to support it correctly. RLV is a chat-channel-based protocol
(channel 0 prefixed with `@`) that lets in-world objects impose
restrictions on the viewer (e.g., disable teleport, redirect chat, lock
attachments).

References: `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/modules/rlv/`,
`res/xml/preferences_rlv.xml`. Linkpoint already has a `rlv/` package —
audit it against this spec.

---

## 1. RLV command grammar

Commands arrive as chat lines from objects on channel 0:

```
@command:option=param[,command:option=param]*
```

Examples:

```
@detach=n              ← lock all attachments
@tploc=n               ← block teleport-to-location
@redirchat:0=add        ← redirect chat to channel 0 (i.e., suppress)
@version=4711           ← reply with viewer's RLV version on channel 4711
```

Lumiya parses with a `RLVController` and per-command handler classes:
`RLVCmdRedirChat`, `RLVCmdTeleportLandmark`, `RLVCmdSendChat`,
`RLVCmdVersion`, `RLVCmdGetStatus`, etc.

---

## 2. State machine — `RLVRestrictions`

Restrictions accumulate. Each is keyed by `(restriction, sourceObject)`
so removing the source object lifts only its restrictions (not someone
else's).

| Category | Examples |
|---|---|
| Movement | `tploc`, `tplm`, `tplure`, `sittp`, `unsit`, `fly` |
| Attachments | `detach`, `addattach`, `remattach`, `addoutfit`, `remoutfit` |
| Chat | `redirchat`, `rediremote`, `sendchat`, `chatshout`, `chatwhisper` |
| IM | `recvim`, `sendim`, `startim` |
| Inventory | `showinv`, `viewnote`, `viewscript`, `viewtexture` |
| Camera | `setcam_avdistmin`, `camdrawmax`, etc. |
| World view | `showworldmap`, `showminimap`, `showloc`, `shownames` |

Each restriction has explicit "set" and "clear" operations:
`@detach=n` to set, `@detach=y` to clear. The viewer must honor every
active restriction every time the user attempts the gated action.

---

## 3. Concrete work items

| ID | Item |
|---|---|
| L10-A | RLV opt-in setting (`preferences_rlv.xml` analogue) — disabled by default |
| L10-B | Inbound chat parser: detect `@…` lines on channel 0 from objects (not avatars), route to RLV controller, **suppress display** in chat history |
| L10-C | Per-command parser dispatching to handler classes |
| L10-D | `RLVRestrictions` state keyed by `(restriction, sourceObjectUUID)` |
| L10-E | Gates on every action: teleport, attach/detach, IM send, chat send, world map open, inventory view |
| L10-F | `@version` reply: send our viewer's RLV version on the requested channel |
| L10-G | `@getstatus` reply: list active restrictions on the requested channel |
| L10-H | UI affordance to view active restrictions (so user can see what's blocking them) |
| L10-I | Auto-clear: on disconnect, clear all RLV state. On region change, retain only persistent restrictions (those with `_pers` suffix in the spec, if we choose to support them) |
| L10-J | `SLEnableRLVOfferEvent` (segment 08) flow: when an object first issues an RLV command, prompt user to enable RLV if it's off |

---

## 4. Cross-references

- Segment 08 — RLV offers arrive as a chat-event subclass
- Segment 11 — settings panel
