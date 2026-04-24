# LINKPOINT MASTER TRACKING DOCUMENT (Code-verified snapshot)

**Last verified:** 2026-04-23 (UTC)  
**Commit:** `TBD`  
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

5. **Former declaration-only caps now have concrete manager request paths**
   - `CAP_SET_DISPLAY_NAME` via `DisplayNameManager.setDisplayName(...)`.
   - `CAP_SIMULATOR_FEATURES` via `SimulatorFeaturesManager.fetchSimulatorFeatures(...)`.
   - `CAP_AGENT_PREFERENCES` / `CAP_UPDATE_AGENT_LANGUAGE` via `AgentPreferencesManager`.
   - `CAP_RENDER_MATERIALS` via `RenderMaterialsManager.fetchRenderMaterials(...)`.
   - `CAP_COPY_INVENTORY_FROM_NOTECARD` and `CAP_MOVE_INVENTORY_ITEM` via `NotecardManager`.

6. **Task notecard update capability path is now wired**
   - `NotecardManager.saveNotecard(itemId, newText, taskId)` routes to `CAP_UPDATE_NOTECARD_TASK`.

---

## [OPEN_BLOCKER] Current blockers requiring follow-up

1. **Deferred capability integrations remain intentionally out-of-scope**
   - `CAP_OBJECT_MEDIA_NAVIGATE` deferred (2026-04-23): media browser/navigation stack not yet wired in app UI.
   - `CAP_REGION_EXPERIENCE` deferred (2026-04-23): no active runtime consumer yet, kept for handshake parity.

2. **Capability list cleanup**
   - Removed from requested-capability tracking gap list: `CAP_MOVE_INVENTORY_ITEM` (now implemented via notecard inventory flow request path).

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

1. Continue deferral review for `CAP_OBJECT_MEDIA_NAVIGATE` and `CAP_REGION_EXPERIENCE` against product priorities.
2. Keep new managers covered by request-shape unit tests when payload schemas evolve.
3. Re-run static inventory after each capability refactor and refresh this file with new commit hash/date.
