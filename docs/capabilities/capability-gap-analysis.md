# Capability gap analysis (Lumiya vs Linkpoint)

**Last verified:** 2026-04-09 (UTC)  
**Commit:** `a9982607`  
**Static inventory scope:** `Linkpoint/src/main/java/**/*.kt`

---

## 1) Static inventory re-run (current Kotlin paths)

Inventory was re-run against current Kotlin sources using symbol and callsite scans (`rg` + targeted file review) under `Linkpoint/src/main/java`.

### Current capability symbol source of truth
- `CapabilityManager` capability constants: `com.linkpoint.protocol.capabilities.CapabilityManager`.
- Reference capability seed-request list: `com.linkpoint.protocol.translation.LinkpointTranslationLayer.getReferenceCapabilityNames()`.

---

## 2) Reclassified findings

## Confirmed in code now (previously reported as gaps)

1. **Script upload/update flow is implemented.**  
   - `ScriptManager.saveScript(itemId, scriptText, taskId)` now uses `UpdateScriptAgent`/`UpdateScriptTask` and executes uploader POST + LLSD completion handling.

2. **Group profile capability is requested from seed list and consumed.**  
   - `LinkpointTranslationLayer.getReferenceCapabilityNames()` includes `"GroupProfile"`.
   - `ProfileManager.getGroupProfile(groupId)` calls the capability.

3. **Swap-chain creation path exists (not a missing callback anymore).**  
   - `WorldViewActivity.surfaceCreated(...)` calls `renderManager.recreateSwapChain()`.
   - `RenderManager.recreateSwapChain()` calls `engine.createSwapChain(surface)`.

4. **Object/avatar update wiring exists.**  
   - `LinkpointApp.processObjectUpdate(...)` routes prim updates to `ObjectManager.handleObjectUpdate(...)` and renderer queue, and routes avatar updates to `AvatarManager.updateAvatar(...)` and renderer queue.

## Open blockers (confirmed in code now)

1. **Task-notecard save path is still missing.**  
   - `CapabilityManager` defines `CAP_UPDATE_NOTECARD_TASK`.
   - `LinkpointTranslationLayer.getReferenceCapabilityNames()` requests `"UpdateNotecardTaskInventory"`.
   - `NotecardManager.saveNotecard(itemId, newText)` only uses `CAP_UPDATE_NOTECARD_AGENT` and has no task/object variant.

2. **Capability symbol drift (literal strings instead of constants) remains in profile flows.**  
   - `ProfileManager.getGroupProfile(...)` calls `capabilityManager.request("GroupProfile", ...)` instead of `CapabilityManager.CAP_GROUP_PROFILE`.
   - Similar literal usage exists for `"AgentProfile"`.
   - This is not a hard runtime failure today, but it is a maintenance/parity blocker for inventory-based static verification.

3. **Declared capability constants with no runtime request callsites (likely incomplete flows).**  
   - `CAP_SET_DISPLAY_NAME`, `CAP_SIMULATOR_FEATURES`, `CAP_AGENT_PREFERENCES`, `CAP_UPDATE_AGENT_LANGUAGE`, `CAP_RENDER_MATERIALS`, `CAP_OBJECT_MEDIA_NAVIGATE`, `CAP_COPY_INVENTORY_FROM_NOTECARD`, `CAP_REGION_EXPERIENCE`, `CAP_MOVE_INVENTORY_ITEM`.
   - These are currently declarations without clear feature-path usage through `capabilityManager.request(...)` callsites.

---

## 3) Historical-only claims (kept for traceability)

These claims were present in prior comparisons but are now historical and should not be treated as current blockers:

- “Script upload caps are missing.” → **Historical** (now implemented in `ScriptManager.saveScript`).
- “GroupProfile is not requested from seed capabilities.” → **Historical** (present in `getReferenceCapabilityNames()`).
- “No swap chain initialization path exists.” → **Historical** (exists in `WorldViewActivity` + `RenderManager`).

