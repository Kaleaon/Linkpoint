# LINKPOINT MASTER TRACKING DOCUMENT (Code-verified snapshot)

**Last verified:** 2026-04-09 (UTC)  
**Commit:** `a9982607`  
**Verification scope:** `Linkpoint/src/main/java/**/*.kt`

---

## Status legend

- `[CONFIRMED_NOW]` = confirmed in current codebase.
- `[HISTORICAL]` = prior claim/incident retained for context, not a current blocker by static inspection.
- `[OPEN_BLOCKER]` = concrete gap still visible in code.

---

## [CONFIRMED_NOW] Fixed / implemented paths

1. **Script update capability flow exists**
   - `com.linkpoint.assets.ScriptManager.saveScript(...)`
   - Uses `CAP_UPDATE_SCRIPT_AGENT` / `CAP_UPDATE_SCRIPT_TASK` and uploader completion handling.

2. **Group profile capability path exists end-to-end**
   - Capability request list includes `GroupProfile`: `LinkpointTranslationLayer.getReferenceCapabilityNames()`.
   - Runtime callsite: `ProfileManager.getGroupProfile(...)`.

3. **Scene/update routing is wired**
   - `LinkpointApp.processObjectUpdate(...)` forwards object and avatar updates to managers and renderer queue.
   - Object manager sink: `ObjectManager.handleObjectUpdate(...)`.

4. **Swap chain lifecycle wiring exists**
   - `WorldViewActivity.surfaceCreated(...)` invokes `RenderManager.recreateSwapChain()`.
   - `RenderManager.recreateSwapChain()` creates swap chain via Filament engine.

---

## [OPEN_BLOCKER] Current blockers requiring follow-up

1. **Task notecard save capability path is incomplete**
   - Present symbols/caps: `CapabilityManager.CAP_UPDATE_NOTECARD_TASK`, translation list includes `UpdateNotecardTaskInventory`.
   - Missing flow: `NotecardManager.saveNotecard(...)` currently only targets `CAP_UPDATE_NOTECARD_AGENT` and has no task/object variant.

2. **Capability callsite coverage still incomplete for some declared caps**
   - Declared constants without clear `capabilityManager.request(...)` usage paths:
     - `CAP_SET_DISPLAY_NAME`
     - `CAP_SIMULATOR_FEATURES`
     - `CAP_AGENT_PREFERENCES`
     - `CAP_UPDATE_AGENT_LANGUAGE`
     - `CAP_RENDER_MATERIALS`
     - `CAP_OBJECT_MEDIA_NAVIGATE`
     - `CAP_COPY_INVENTORY_FROM_NOTECARD`
     - `CAP_REGION_EXPERIENCE`
     - `CAP_MOVE_INVENTORY_ITEM`

3. **Symbol consistency gap (literals vs constants)**
   - Example: `ProfileManager.getGroupProfile(...)` uses literal `"GroupProfile"` instead of `CapabilityManager.CAP_GROUP_PROFILE`.
   - This complicates static verification and can hide capability drift during refactors.

4. **Renderer feature parity still partial**
   - `LumiyaRenderer` marks HUD pass as “not implemented yet”.

5. **Outfit pipeline still uses placeholder behavior**
   - `OutfitManager` contains a placeholder return path (“For now, return a placeholder”).

---

## [HISTORICAL] Older issues now reclassified

- “No object/avatar scene wiring” is **historical** (handlers and routing now present).
- “No swap chain init callback” is **historical** (callback and recreate path present).
- “Script update caps missing” is **historical** (script save/update implemented).

---

## Next verification pass checklist

1. Add task-inventory variant to notecard save API and wire `UpdateNotecardTaskInventory`.
2. Replace capability string literals with `CapabilityManager.CAP_*` constants in profile/world managers.
3. Either implement or formally de-scope currently uncalled capability constants.
4. Re-run static inventory after each change and refresh this file with new commit hash/date.

