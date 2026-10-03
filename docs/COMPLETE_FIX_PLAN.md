# Complete Fix Plan (Current-code refresh)

**Last verified:** 2026-04-09 (UTC)  
**Commit:** `a9982607`  
**Verification basis:** static inventory + targeted symbol/callsite inspection of `Linkpoint/src/main/java/**/*.kt`

---

## 1) What changed since prior plan versions

Previous plan revisions mixed historical runtime incidents with static assumptions that are no longer true in current code. This refresh separates:
- **Confirmed implemented now** (present in code)
- **Historical-only statements** (retain for context)
- **Open blockers** (still visible in current symbols/flows)

---

## 2) Confirmed implemented now

### A. Script upload/update pipeline
- Implemented in `ScriptManager.saveScript(itemId, scriptText, taskId)`.
- Selects `UpdateScriptAgent` or `UpdateScriptTask` by context.
- Handles uploader URL POST and LLSD completion/compile parsing.

### B. Group profile capability request/usage
- Seed request list includes `GroupProfile` (`LinkpointTranslationLayer.getReferenceCapabilityNames()`).
- Runtime retrieval implemented in `ProfileManager.getGroupProfile(groupId)`.

### C. Scene object/avatar routing to renderer
- `LinkpointApp.processObjectUpdate(...)` routes:
  - avatars → `AvatarManager.updateAvatar(...)` + `RenderableUpdate.AvatarUpdate`
  - objects → `ObjectManager.handleObjectUpdate(...)` + `RenderableUpdate.PrimUpdate`

### D. Swap-chain lifecycle path exists
- `WorldViewActivity.surfaceCreated(...)` calls `RenderManager.recreateSwapChain()`.
- `RenderManager.recreateSwapChain()` creates a Filament swap chain from the active surface.

---

## 3) Open blockers (confirmed in code now)

## Blocker 1 — Task notecard save flow parity gap

**Why open:** capability symbol exists but no task-inventory notecard save function path is exposed.

**Direct references:**
- `CapabilityManager.CAP_UPDATE_NOTECARD_TASK`
- `LinkpointTranslationLayer.getReferenceCapabilityNames()` includes `UpdateNotecardTaskInventory`
- `NotecardManager.saveNotecard(itemId, newText)` uses only `CAP_UPDATE_NOTECARD_AGENT`

**Planned fix:**
1. Extend notecard save API with optional `taskId`/context.
2. Select agent vs task capability based on context.
3. Add integration tests for both agent and task notecard uploads.

---

## Blocker 2 — Capability constant coverage/usage drift

**Why open:** multiple `CAP_*` declarations currently have no clear request callsites; likely incomplete feature hooks.

**Direct references:**
- `CapabilityManager` constants:
  - `CAP_SET_DISPLAY_NAME`
  - `CAP_SIMULATOR_FEATURES`
  - `CAP_AGENT_PREFERENCES`
  - `CAP_UPDATE_AGENT_LANGUAGE`
  - `CAP_RENDER_MATERIALS`
  - `CAP_OBJECT_MEDIA_NAVIGATE`
  - `CAP_COPY_INVENTORY_FROM_NOTECARD`
  - `CAP_REGION_EXPERIENCE`
  - `CAP_MOVE_INVENTORY_ITEM`

**Planned fix:**
1. For each cap: map to intended feature owner/module.
2. Either add callsite + user flow, or mark as intentionally reserved/deferred in docs.
3. Add inventory check script to fail CI on undocumented declared-but-unused caps.

---

## Blocker 3 — Literal capability strings bypassing CAP_* constants

**Why open:** some flows use raw strings, reducing static traceability and increasing typo risk.

**Direct references:**
- `ProfileManager.getGroupProfile(...)` uses `"GroupProfile"`
- `ProfileManager` also uses literals such as `"AgentProfile"`

**Planned fix:**
1. Normalize all capability callsites to `CapabilityManager.CAP_*` when available.
2. Add lint rule (or test) for new literal capability names.

---

## Blocker 4 — Remaining renderer/UI parity items

**Why open:** explicit TODO/placeholder markers remain in current code.

**Direct references:**
- `LumiyaRenderer` comment: HUD pass “not implemented yet”.
- `OutfitManager` comment: “For now, return a placeholder”.

**Planned fix:**
1. Implement HUD render pass wiring (or de-scope clearly).
2. Replace outfit placeholder path with real outfit resolution pipeline.

---

## 4) Historical items (do not treat as current blockers)

- “Script updates not implemented” → historical.
- “GroupProfile not requested from seed” → historical.
- “No swap-chain creation callback” → historical.
- “No object/avatar update routing” → historical.

---

## 5) Re-verification protocol for next update

1. Run static inventory against `Linkpoint/src/main/java/**/*.kt`.
2. Regenerate “declared capability constants vs runtime callsites” delta.
3. Update this file + `MASTER_TRACKING.md` + `capability-gap-analysis.md` with:
   - exact verification date
   - exact commit hash
   - blocker status changes (confirmed-now vs historical).

