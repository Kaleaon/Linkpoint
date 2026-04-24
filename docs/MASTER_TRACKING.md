# LINKPOINT MASTER TRACKING DOCUMENT (Code-verified snapshot)

**Last verified:** 2026-04-24 (UTC)  
**Commit:** `3da3de10f9fb5aa90d70c8fc30ea99eaebdb902b`  
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
| `MP-005` | Deferred caps are either implemented or explicitly tracked with owning backlog item | `@protocol` | `BLOCKED` | media browser/nav stack for `CAP_OBJECT_MEDIA_NAVIGATE`; region-experience consumer for `CAP_REGION_EXPERIENCE` | `./gradlew :app:testDebugUnitTest --tests "*Capability*"` |
| `MP-006` | Renderer feature parity includes HUD pass support | `@render-runtime` | `IN_PROGRESS` | HUD scene graph + compositor pass integration | `./gradlew :app:testDebugUnitTest --tests "*LumiyaRenderer*"` |
| `MP-007` | Outfit pipeline returns real wearable resolution (no placeholder path) | `@assets-avatar` | `DONE` | none | `./gradlew :app:testDebugUnitTest --tests "*OutfitManager*"` |

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

3. **Renderer feature parity still partial**
   - `LumiyaRenderer` marks HUD pass as “not implemented yet”.

4. **Outfit pipeline parity evidence is now codified**
   - `OutfitManager` now records typed fallback reasons (`MISSING_FETCHER`, `MISSING_ASSET_BYTES`, `CORRUPT_ASSET_PAYLOAD`, `FETCH_EXCEPTION`) and increments telemetry counters instead of generic fallback logging.
   - `OutfitManagerTest` enforces a wearable parser corpus success threshold of **>= 95%** and verifies expected bake-channel mappings by wearable type.

---

## Parity debt (vs reference docs)

**Last verified:** 2026-04-24 (UTC)  
**Commit:** `3da3de10f9fb5aa90d70c8fc30ea99eaebdb902b`

| Debt ID | Gap vs reference docs | Impact | Owner | Status |
|---|---|---|---|---|
| `PD-001` | Object media navigate capability exists in reference capability set but app lacks user-facing media navigation implementation. | Interactive media surfaces cannot be navigated in parity workflows. | `@protocol` | `OPEN` |
| `PD-002` | Region experience capability is requested for parity but no runtime consumer path is enabled. | Experience/permission flows remain incomplete relative to reference behavior. | `@protocol` | `OPEN` |
| `PD-003` | HUD render pass parity incomplete in Filament renderer path. | UI/HUD visual parity gap in scenes requiring overlay passes. | `@render-runtime` | `OPEN` |
| `PD-004` | Outfit manager fallback path now uses typed failure reasons + telemetry counters; parser corpus threshold and bake-channel mapping tests enforce parity behavior. | Remaining risk is isolated to real-world asset fetch reliability rather than unresolved placeholder logic. | `@assets-avatar` | `CLOSED` |

---

## [HISTORICAL] Older issues now reclassified

- “No object/avatar scene wiring” is **historical** (handlers and routing now present).
- “No swap chain init callback” is **historical** (callback and recreate path present).
- “Script update caps missing” is **historical** (script save/update implemented).

---

## Next verification pass checklist

1. Continue deferral review for `CAP_OBJECT_MEDIA_NAVIGATE` and `CAP_REGION_EXPERIENCE` against product priorities.
2. Keep new managers covered by request-shape unit tests when payload schemas evolve.
3. Re-run static inventory after each capability refactor and refresh this file and `docs/MASTER_TRACKING.json` with new commit hash/date.
