# Lumiya Comparison Worklist (2026-03-31)

This worklist captures the highest-priority items still blocking parity with the decompiled Lumiya reference implementation.

## Scope checked

- `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/messages/*.java`
- `Linkpoint/src/main/java/com/linkpoint/protocol/messages/MessageIds.kt`
- `Linkpoint/src/main/java/com/linkpoint/LinkpointApp.kt`
- Core runtime classes where placeholders/stubs still exist

## What still needs work

## 1) Replace placeholder agent/session/group IDs in object & parcel operations (Critical)

Multiple outbound payload builders still write zeroed placeholder identity blocks instead of real `AgentID`, `SessionID`, and related IDs. These operations often appear to "send" but may be ignored or rejected server-side.

- `ObjectManager` still writes placeholder `AgentData` in many object operations.
- `ParcelManager` still writes placeholder IDs/strings/transaction values.

**Why this matters vs Lumiya:** Lumiya message classes always pack concrete identity/auth fields before submit; zeroing these values breaks authoritative simulator actions.

## 2) Close UDP message parity gaps for request/reply pairs used by inventory/economy/object edit flows (High)

Quick parity sweep shows a small set of `MessageIds` constants that are present but not currently registered in `LinkpointApp` handler wiring. Some are request-only, but several are part of operational request/reply flows that should be explicitly validated and either handled or documented as intentionally outbound-only.

Candidates requiring explicit implementation decision:

- `MOVE_INVENTORY_ITEM`
- `UPDATE_TASK_INVENTORY`
- `PARCEL_BUY`
- `OBJECT_GRAB` / `OBJECT_DEGRAB`
- `KICK_USER`
- `UPDATE_USER_INFO`
- `FIND_AGENT`

**Why this matters vs Lumiya:** the decompiled Lumiya message set includes these domains as first-class message classes, so parity requires either handling or explicit intentional omission.

## 3) Finish JPEG2000 decode path for texture fidelity (High)

Current texture path can fall back to placeholders when JPEG2000 decode is unavailable, which directly impacts world usability and visual parity.

**Why this matters vs Lumiya:** texture decode is table-stakes for practical in-world navigation.

## 4) Replace remaining generated parser scaffolding TODOs or remove dead scaffolding (Medium)

`GeneratedParserScaffolding.kt` still contains hard TODO throw sites for core packets (`ObjectUpdate`, `AvatarAnimation`, `TeleportFinish`, `InventoryDescendents`).

Even if runtime currently routes through concrete parsers elsewhere, these TODO stubs are dangerous drift points and can regress if referenced accidentally.

## 5) Finish renderer parity gaps (HUD pass) (Medium)

Lumiya-based renderer still marks HUD pass as not implemented.

**Why this matters vs Lumiya:** attachment/HUD interaction is core viewer behavior.

## 6) Replace remaining temporary placeholder returns in user-facing managers (Medium)

`OutfitManager` still has a placeholder return path. This causes partial UX behavior where UI appears functional but does not reflect authoritative simulator state.

---

## Suggested execution order

1. **Protocol correctness first:** identity fields in outbound Object/Parcel flows.
2. **Message flow parity:** validate/register missing request/reply packet handling.
3. **Visual fidelity:** JPEG2000 stability + HUD pass.
4. **Hardening:** remove/replace generated parser TODO scaffolding and leftover placeholders.

---

## Repro commands used for this sweep

```bash
python - <<'PY'
import re, pathlib
root=pathlib.Path('/workspace/Linkpoint')
msgs=list((root/'lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/messages').glob('*.java'))
print('lumiya_messages',len(msgs))
mid=(root/'Linkpoint/src/main/java/com/linkpoint/protocol/messages/MessageIds.kt').read_text(errors='ignore')
print('linkpoint_message_ids',len(re.findall(r'const val\\s+([A-Z0-9_]+)\\s*=\\s*',mid)))
app=(root/'Linkpoint/src/main/java/com/linkpoint/LinkpointApp.kt').read_text(errors='ignore')
ids=re.findall(r'register(?:Parsed)?Handler\\(\\s*com\\.linkpoint\\.protocol\\.messages\\.MessageIds\\.([A-Z0-9_]+)',app)
print('registered_message_ids',len(set(ids)))
PY
```
