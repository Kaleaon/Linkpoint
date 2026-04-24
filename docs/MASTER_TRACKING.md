# LINKPOINT MASTER TRACKING DOCUMENT (Code-verified snapshot)

**Last verified:** 2026-04-24 (UTC)  
**Commit:** `e2011834a08d4888abd75beee10b772d7d0793a3`  
**Machine-readable tracker:** `docs/MASTER_TRACKING.json`  
**Verification scope:** `Linkpoint/src/main/java/**/*.kt`

---

## Tracker policy (single source of truth)

This file and `docs/MASTER_TRACKING.json` are the canonical tracker for modernization phase acceptance criteria.

**PR update requirement:** any PR that changes files under protocol, assets, or render paths **must** update this tracker pair (`docs/MASTER_TRACKING.md` + `docs/MASTER_TRACKING.json`) to reflect task status, dependency, and acceptance test command changes.

---

## Status legend

- `[CONFIRMED_NOW]` = confirmed in current codebase.
- `[HISTORICAL]` = prior claim/incident retained for context, not a current blocker by static inspection.
- `[OPEN_BLOCKER]` = concrete gap still visible in code.
- `NOT_STARTED | IN_PROGRESS | BLOCKED | DONE` = machine-readable status values used in `docs/MASTER_TRACKING.json`.

---

## Modernization phase acceptance tracker

| Task ID | Acceptance criterion | Owner | Status | Blocking deps | Acceptance tests / commands |
|---|---|---|---|---|---|
| `MP-001` | Script update capability flow exists end-to-end and uses task/agent update caps | `@protocol-assets` | `DONE` | none | `./gradlew :app:testDebugUnitTest --tests "*ScriptManager*"` |
| `MP-002` | Group profile capability request + runtime consumer is wired | `@protocol` | `DONE` | none | `./gradlew :app:testDebugUnitTest --tests "*ProfileManager*"` |
| `MP-003` | Scene/update routing forwards object/avatar updates to renderer queue | `@render-runtime` | `DONE` | none | `./gradlew :app:testDebugUnitTest --tests "*ObjectManager*"` |
| `MP-004` | Swap-chain recreation is invoked from Android surface lifecycle | `@render-runtime` | `DONE` | Android instrumented harness for `WorldViewActivity` | `./gradlew :app:testDebugUnitTest --tests "*RenderManager*"` |
| `MP-005` | Deferred caps are either implemented or explicitly tracked with owning backlog item | `@protocol` | `DONE` | none | `./gradlew :app:testDebugUnitTest --tests "*DeferredCapabilityConsumersTest*"` |
| `MP-006` | Renderer feature parity includes HUD pass support | `@render-runtime` | `IN_PROGRESS` | HUD scene graph + compositor pass integration | `./gradlew :app:testDebugUnitTest --tests "*LumiyaRenderer*"` |
| `MP-007` | Outfit pipeline returns real wearable resolution (no placeholder path) | `@assets-avatar` | `BLOCKED` | wearable resolution rules + baked texture mapping parity decisions | `./gradlew :app:testDebugUnitTest --tests "*OutfitManager*"` |

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

1. **Capability list cleanup**
   - Removed from requested-capability tracking gap list: `CAP_MOVE_INVENTORY_ITEM` (now implemented via notecard inventory flow request path).

2. **Renderer feature parity still partial**
   - `LumiyaRenderer` marks HUD pass as “not implemented yet”.

3. **Outfit pipeline still uses placeholder behavior**
   - `OutfitManager` contains a placeholder return path (“For now, return a placeholder”).

---

## Parity debt (vs reference docs)

**Last verified:** 2026-04-24 (UTC)  
**Commit:** `e2011834a08d4888abd75beee10b772d7d0793a3`

| Debt ID | Gap vs reference docs | Impact | Owner | Status |
|---|---|---|---|---|
| `PD-001` | Object media navigate capability exists in reference capability set but app lacks user-facing media navigation implementation. | Interactive media surfaces cannot be navigated in parity workflows. | `@protocol` | `DONE` |
| `PD-002` | Region experience capability is requested for parity but no runtime consumer path is enabled. | Experience/permission flows remain incomplete relative to reference behavior. | `@protocol` | `DONE` |
| `PD-003` | HUD render pass parity incomplete in Filament renderer path. | UI/HUD visual parity gap in scenes requiring overlay passes. | `@render-runtime` | `OPEN` |
| `PD-004` | Outfit manager still returns placeholder path instead of full wearable resolution parity. | Avatar appearance and wearable state can diverge from expected reference outcomes. | `@assets-avatar` | `OPEN` |

---

## [HISTORICAL] Older issues now reclassified

- “No object/avatar scene wiring” is **historical** (handlers and routing now present).
- “No swap chain init callback” is **historical** (callback and recreate path present).
- “Script update caps missing” is **historical** (script save/update implemented).
- “LumiyaRenderer marks HUD pass as not implemented yet” is **historical** (HUD pass + attachment routing now exist; closure waits on passing renderer tests).

---

## Next verification pass checklist

1. Keep media-navigation and region-experience request-shape tests in sync as payload schemas evolve.
2. Re-run static inventory after each capability refactor and refresh this file and `docs/MASTER_TRACKING.json` with new commit hash/date.
