# Capability gap analysis (Lumiya vs Linkpoint)

This document highlights capability coverage mismatches and likely failure points
based on the Lumiya decompiled sources and the Linkpoint implementation.

## 1) Capabilities present in Lumiya but not fully implemented in Linkpoint

### Notecard update flow
- **Lumiya** uses `UpdateNotecardAgentInventory` / `UpdateNotecardTaskInventory` caps for notecard uploads via `SLInventory.UploadNotecardContents`.
  - Source: `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/inventory/SLInventory.java:400-413`
- **Linkpoint** has a `NotecardManager.saveNotecard` method that logs the capability requirement but does not issue the cap request.
  - Source: `Linkpoint/src/main/java/com/linkpoint/inventory/notecard/NotecardManager.kt:405-417`
- **Risk**: Notecard save operations will not reach the server, leaving edits unsaved.

### Script update caps
- **Lumiya** also uses `UpdateScriptAgent` / `UpdateScriptTask` (same upload path as notecards).
  - Source: `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/inventory/SLInventory.java:400-413`
- **Linkpoint** does not currently implement any script upload or update path tied to those caps.
  - Source: `Linkpoint/src/main/java/com/linkpoint/protocol/translation/LumiyaTranslationLayer.kt:276-280` (caps requested) but no handler exists.
- **Risk**: Script edits/updates will not be functional.

## 2) Capability requests vs runtime usage in Linkpoint

### Group profile capability not requested from seed
- **Linkpoint** makes `GroupProfile` capability requests in `ProfileManager` using a string literal.
  - Source: `Linkpoint/src/main/java/com/linkpoint/world/ProfileManager.kt:224-238`
- **Linkpoint** capability request lists (standard + Lumiya list) do not include `GroupProfile`.
  - Source: `Linkpoint/src/main/java/com/linkpoint/protocol/translation/LumiyaTranslationLayer.kt:265-301`
- **Risk**: `GroupProfile` capability URLs may never be resolved, causing group profile requests to fail.

## 3) Capabilities defined but no direct usage found in Linkpoint

The following are declared in `CapabilityManager` but do not appear in runtime usage paths:
- FetchLib2
- ViewerStats
- AgentState
- UpdateAgentInformation
- ObjectMediaNavigate
- CopyInventoryFromNotecard
- RegionExperiences
- VoiceModeration
- MoveItemsToTrash
- SetDisplayName
- SimulatorFeatures
- AgentPreferences
- UpdateAgentLanguage
- RenderMaterials
- SearchStatRequest

These are all defined in the capability constants list but lack a direct usage site.
- Source: `Linkpoint/src/main/java/com/linkpoint/protocol/capabilities/CapabilityManager.kt:45-90`

**Risk**: Missing UI flows or server requests may be required to make these functional.

## 4) Caps declared in Lumiya but not referenced in Lumiya modules

The decompiled Lumiya tree declares `NewFileAgentInventory` and `GetMesh` in the capability list,
but no direct usage was found in the module code.
- Source: `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/caps/SLCaps.java:27-45`

**Note**: These may be legacy or optional capabilities not used in Lumiya’s runtime modules.
