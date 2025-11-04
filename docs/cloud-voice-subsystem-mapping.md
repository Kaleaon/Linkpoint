## Cloud Sync & Voice Subsystem Mapping

### Google Drive Cloud Sync
- **Service entry point**: `DriveSyncService` exposes an Android `Service` that accepts `Messenger` messages from UI components via `CloudSyncMessenger`. It negotiates a `GoogleApiClient`, handles error resolution through `ConnectionResolutionActivity`, and keeps a list of requestors (`syncRequestSources`) so status updates can be fanned back out.
- **Message contract**: The service recognises `MessageType` values such as `LogSyncStart`, `LogMessageBatch`, `LogFlushMessages`, and `LogSyncStatus`. Payloads live in `cloud/common` data classes (`LogMessageBatch`, `LogFlushMessages`, etc.) that implement a `Bundleable` contract for transport via `Messenger`.
- **Connection orchestration**: `AgentSyncConnections` (legacy Java) keeps the per-agent Google Drive folder state; `ErrorResolutionTracker` manages in-flight API failures and pacing retry attempts.
- **File synchronisation**: Once connected, `DriveSynchronizer` requests or creates the `Lumiya/ChatLogs` Drive hierarchy, caches `DriveChatLogFolder` instances per agent, and feeds lined chat text through `DriveTextFile` wrappers. Flush cadence is governed by `LogWriteTracker`, which records open handles and pending writes so periodic flushes (`periodicSync` runnable) only run while work remains.
- **Known gaps**: The decompiled Java contains unresolved fragments (e.g. synthetic constructors showing as `fun DriveConnectibleFolder(): new`). These sections are archived in `legacy-java` for reference but need manual reconstruction using the APK + upstream viewers for accuracy.
- **Rebuild direction**:
  - Replace the bespoke `Handler` scheduling with `WorkManager` or coroutines so sync runs reliably in the background.
  - Keep the Messenger contract (so existing UI bindings still function) but back it with a Kotlin implementation that calls the Google Drive REST API via `Drive` v3 rather than the deprecated `GoogleApiClient` surface.
  - Mirror the batching model (`LogMessageBatch` → `MessageSyncBatch`) so chat export remains line-identical to the APK, and write unit tests that diff exported text against captures from `legacy-java`.

### Voice (Vivox → WebRTC)
- **Existing Vivox service**: `voice_backup/voice/VoiceService` is the historic plugin shim. It binds to the main process via `Messenger`, validates permission state, proxies Vivox login/connect/position messages, and monitors system audio (Bluetooth SCO, stream volume) so it can adjust in-call gain.
- **Message schema**: Requests are `VoicePluginMessageType` values wrapping data classes such as `VoiceInitialize`, `VoiceLogin`, `VoiceConnectChannel`, `VoiceSet3DPosition`, etc. These mirror the protocol Lumiya originally sent to the embedded Vivox binaries.
- **Modern rewrite**: `WebRTCVoiceManager` (Kotlin) replaces Vivox entirely. It uses the Stream WebRTC Android bindings + vanilla `org.webrtc` to stand up peer connections, manage audio tracks, and expose reactive state (`StateFlow` for mute and gain). Bridging back into the legacy interface happens through `SecondLifeWebRTCBridge` and `LinkpointVoiceManager`, which translate Vivox-style messages into WebRTC sessions.
- **Bridging plan**:
  - Keep the `VoiceService` messenger API so UI code continues to talk to a single endpoint. Internally, route those messages to a Kotlin orchestrator that selects either WebRTC or (if present) the legacy Vivox compatibility layer.
  - Map each Vivox call to a WebRTC equivalent: e.g. `VoiceConnectChannel` → `WebRTCVoiceManager.joinChannel`, `VoiceSet3DPosition` → spatial audio parameters on `JavaAudioDeviceModule`, etc.
  - Leverage `Libremetaverse` for SIP/signalling payload formats so the mobile client matches the current Second Life voice stack nuances.

### Next Steps
1. Extract a clean specification of every `MessageType` / `VoicePluginMessageType` payload (field names, ranges) and store it alongside this document, ready for Kotlin data-class reimplementation.
2. Prototype a coroutine-friendly replacement for `DriveSynchronizer.flushOpenFiles` that targets the Drive v3 API and compare output with the APK’s exported logs.
3. Build a thin adapter around `WebRTCVoiceManager` that can satisfy the `VoicePluginMessenger` contract in-process, enabling incremental replacement of the Vivox plumbing without breaking UI clients.
