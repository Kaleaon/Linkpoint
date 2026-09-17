# Sector 01 — Protocol, Networking & LLSD

**Findings:** 20 | **Critical:** 4 | **High:** 8 | **Medium:** 6 | **Low:** 2

Verified against source at `Linkpoint/src/main/java/com/linkpoint/` on branch
`claude/fix-broken-code-audit-AkDJR` on 2026-09-17.

---

## PROTO-001 | 279 protocol messages declared without parser/writer support

- **File:** `Linkpoint/src/main/java/com/linkpoint/protocol/messages/MessageTemplateCatalog.kt`
- **Lines:** `declaredOnlyBacklogMessages` map
- **Category:** missing-parser / missing-writer
- **Severity:** High (aggregate — most are low-frequency sim-to-sim, but ~40 are user-facing)

**What is broken:**
The `declaredOnlyBacklogMessages` map contains 279 entries (as of this audit),
each annotated `"Declared for protocol ID parity; parser/writer support is not
implemented yet."`. These messages exist so the template catalog assigns the
correct numeric IDs (preventing ID collisions), but the viewer cannot parse or
generate any of them. User-critical entries include `AgentWearablesRequest`,
`CreateGroupRequest`, `CreateGroupReply`, `CreateInventoryItem`,
`ObjectDuplicate`, `ObjectPermissions`, `RezObject`, and `ScriptDialogReply`.

**Fix plan:**
1. Triage the 279 into three buckets:
   - **Bucket A — Active user paths** (~40): `AgentWearablesRequest`,
     `CreateGroupRequest/Reply`, `CreateInventoryItem`, `ObjectDuplicate`,
     `ObjectPermissions`, `RezObject`, `ScriptDialogReply`,
     `DetachAttachmentIntoInv`, `RezSingleAttachmentFromInv`, etc.
     Implement parsers/writers from `indra/llmessage/message_template.msg`.
   - **Bucket B — Low-frequency sim-internal** (~200): mark explicitly as
     `@DeprecatedOrSimInternal`. No parser needed.
   - **Bucket C — Deprecated** (~40): move to `deprecated_messages.kt`.
2. Add conformance fixtures in `src/test/resources/fixtures/messages/` for
   every Bucket A message.

**Acceptance test:** `./gradlew :Linkpoint:testDebugUnitTest --tests "*MessageParserConformanceFixtureTest*"`

---

## PROTO-002 | Only 23 parsers registered in MessageParserRegistry

- **File:** `Linkpoint/src/main/java/com/linkpoint/protocol/messages/MessageParserRegistry.kt`
- **Lines:** entire `init` block
- **Category:** missing-parser
- **Severity:** Critical

**What is broken:**
`MessageParserRegistry.init` registers 23 handlers. `MessageIds.kt` defines 568
message ID constants. The viewer silently drops every inbound message that lacks
a handler — no warning, no counter. High-frequency messages like
`RegionHandshake`, `AgentMovementComplete`, `AgentDataUpdate`, `AgentThrottle`,
`LogoutRequest/Reply`, `ParcelProperties`, `ParcelOverlay`, `MapBlockReply`,
`MoneyBalanceReply`, `AlertMessage`, `AgentAlertMessage`, `ScriptDialog`,
`HealthMessage`, `SimStats`, and `CoarseLocationUpdate` are all silently lost or
handled only via ad-hoc `LinkpointApp.registerHandler()` calls that bypass the
registry.

**Fix plan:**
1. Migrate the ~30 messages already routed via `LinkpointApp.registerHandler()`
   into `MessageParserRegistry` for consistency.
2. Implement parsers for the next-priority inbound set: `RegionHandshake`,
   `AgentMovementComplete`, `ParcelProperties`, `ParcelOverlay`,
   `MoneyBalanceReply`, `AlertMessage`, `ScriptDialog`, `SimStats`,
   `CoarseLocationUpdate`, `MapBlockReply`.
3. Add a catch-all counter in `MessageParserRegistry.parse()` that increments an
   `unhandledMessageCount: AtomicLong` and logs the message name at DEBUG.

**Acceptance test:** `./gradlew :Linkpoint:testDebugUnitTest --tests "*MessageParserRegistry*"`

---

## PROTO-003 | LLSD parser silently swallows parse errors

- **File:** `Linkpoint/src/main/java/com/linkpoint/protocol/llsd/LLSDParser.kt`
- **Lines:** ~72–79 (`parseBinary` catch blocks); analogous in `parseXML`, `parseNotation`
- **Category:** null-safety / broken-logic
- **Severity:** Critical

**What is broken:**
```kotlin
return try {
    parseBinaryValue(stream, state, limits)
} catch (_: LLSDParseException) {
    LLSDUndefined
} catch (_: IllegalArgumentException) {
    LLSDUndefined
}
```
Both exceptions are caught, discarded without logging, and replaced with
`LLSDUndefined`. Every downstream consumer (capability responses, event queue
payloads, LLSD-encoded object updates) receives `LLSDUndefined` for corrupted or
truncated payloads with no way to diagnose the failure.

**Fix plan:**
1. Add `Log.w(TAG, "LLSD binary parse failed", e)` in both catch blocks.
2. Introduce `LLSDParseResult` sealed class (`Success(value)` / `Error(exception)`)
   and provide `parseBinaryChecked()`, `parseXMLChecked()`, `parseNotationChecked()`
   variants that callers can opt into.
3. Reference: `github.com/Kaleaon/LLSD` test corpus for malformed-input cases.

**Acceptance test:** `./gradlew :Linkpoint:testDebugUnitTest --tests "*LLSDParser*"`

---

## PROTO-004 | IM session sync race condition

- **File:** `Linkpoint/src/main/java/com/linkpoint/chat/IMManager.kt`
- **Lines:** `handlePushWakeEvent` (~line 145)
- **Category:** race-condition
- **Severity:** Critical

**What is broken:**
```kotlin
PushEventType.IM, PushEventType.GROUP_NOTICE -> {
    pendingSyncSessions.value = pendingSyncSessions.value + sessionId
}
```
`pendingSyncSessions` is a `MutableStateFlow<Set<UUID>>`. The read-then-write on
`.value` is NOT atomic — two concurrent push events can each read the same
snapshot, add their own session ID, and write back, losing one entry.

**Fix plan:**
Replace with `pendingSyncSessions.update { it + sessionId }` — `StateFlow.update`
uses a CAS loop internally and is safe under contention.

**Acceptance test:** `./gradlew :Linkpoint:testDebugUnitTest --tests "*IMManager*"`

---

## PROTO-005 | Terrain LayerData parser returns null silently on truncation

- **File:** `Linkpoint/src/main/java/com/linkpoint/protocol/terrain/LayerDataParser.kt`
- **Lines:** 48, 60, 69, 87 (four `return null` sites)
- **Category:** placeholder-return / missing-diagnostics
- **Severity:** High

**What is broken:**
Four early-exit `return null` paths (insufficient buffer length, bad layer type,
corrupt header, truncated patch data). None log the failure reason. Callers
silently skip the patch, leaving holes in the ground plane.

**Fix plan:**
1. Add `Log.w` at each `return null` site with the specific failure reason and
   the raw packet size.
2. Fill missing patches with the region's default terrain height (from
   `RegionHandshake` data) via a `TerrainPatchFallback` helper.
3. Reference: SL viewer `indra/newview/llvlcomposition.cpp`.

**Acceptance test:** `./gradlew :Linkpoint:testDebugUnitTest --tests "*LayerData*"`

---

## PROTO-006 | Login response parser returns empty on malformed XML

- **File:** `Linkpoint/src/main/java/com/linkpoint/auth/LoginManager.kt`
- **Category:** broken-logic / missing-diagnostics
- **Severity:** High

**What is broken:**
If the XML-RPC login response is malformed or truncated (common behind
corporate proxies that inject captive-portal HTML), the parser returns an empty
map/list without reporting *why*. The user sees "Login failed" with no detail.

**Fix plan:**
1. Detect non-XML content (HTML captive portals, 302 redirects) before parsing;
   surface user-readable error ("Network login blocked by proxy or firewall").
2. Log the first 512 bytes of the response body on parse failure.
3. Reference: SL viewer `indra/newview/lllogininstance.cpp` error reporting.

**Acceptance test:** Manual — inject a malformed login response via mock server.

---

## PROTO-007 | Capability lookup returns null without diagnostic logging

- **File:** `Linkpoint/src/main/java/com/linkpoint/protocol/LinkpointTranslationLayer.kt`
- **Category:** missing-diagnostics
- **Severity:** Medium

**What is broken:**
`getCapability(name)` returns `null` when a capability is not in the seed-caps
map. Callers throughout the app check for null and silently skip work
(`TextureManager`, `OutfitManager`, `ScriptManager`). No log line records which
capability was missing.

**Fix plan:**
1. Add `Log.d(TAG, "Capability not available: $name")` in the null-return path.
2. Track an `unavailableCapabilities` set and expose it via diagnostics endpoint.

**Acceptance test:** Verify log output during a connection that omits a
capability.

---

## PROTO-008 | Chat typing indicators not wired to gesture animation

- **File:** `Linkpoint/src/main/java/com/linkpoint/chat/ChatManager.kt`
- **Category:** missing-runtime-consumer
- **Severity:** Medium

**What is broken:**
Inbound `ChatFromSimulator` messages with `chat_type == TYPING_START / TYPING_STOP`
are parsed but not forwarded to the animation or avatar render layer.

**Fix plan:**
1. Forward to `AvatarManager.setTypingState(agentId, isTyping)`.
2. Trigger typing animation on the target avatar's render instance.
3. Reference: SL viewer `indra/newview/llchatbar.cpp`.

**Acceptance test:** `./gradlew :Linkpoint:testDebugUnitTest --tests "*ChatManager*"`

---

## PROTO-009 | Requested capabilities have no registered consumer

- **File:** `Linkpoint/src/main/java/com/linkpoint/protocol/LinkpointTranslationLayer.kt`
- **Category:** missing-runtime-consumer
- **Severity:** Medium

**What is broken:**
The capability request list includes ~100+ capability names. Event-queue
responses for many (e.g. `FetchLib2`, `ReadOfflineMsgs`,
`ProvisionVoiceAccountRequest`, `ProductInfoRequest`, `SearchStatRequest`) are
never consumed by any manager.

**Fix plan:**
1. Audit every requested capability name against registered event-queue handlers.
2. For each unhandled cap: wire a consumer or remove it from the request list.
3. Priority caps to wire: `ProvisionVoiceAccountRequest`, `ReadOfflineMsgs`,
   `GetDisplayNames`.

**Acceptance test:** Static analysis — verify every
`getReferenceCapabilityNames()` entry has a matching handler.

---

## PROTO-010 | Agent identity has no lifecycle tracking

- **File:** `Linkpoint/src/main/java/com/linkpoint/core/AgentIdentity.kt`
- **Category:** missing-lifecycle
- **Severity:** Medium

**What is broken:**
`AgentIdentity` stores agent UUID, session UUID, and circuit code but does not
track state transitions (pre-login → connected → teleporting → disconnected).
Callers read these fields without knowing if they are stale after a region
crossing or forced disconnect.

**Fix plan:**
1. Add a `ConnectionPhase` enum exposed as `StateFlow<ConnectionPhase>`.
2. Update on login/teleport/disconnect.
3. Callers assert `phase == CONNECTED` before reading UUIDs.

**Acceptance test:** `./gradlew :Linkpoint:testDebugUnitTest --tests "*AgentIdentity*"`

---

## PROTO-011 | Retry-After header not parsed with backoff default

- **File:** `Linkpoint/src/main/java/com/linkpoint/network/SecondLifeProtocol.kt`
- **Category:** missing-retry-logic
- **Severity:** Medium

**What is broken:**
HTTP capability requests that receive 503 with a `Retry-After` header parse the
header value but fall back to `null` if it is absent or unparseable. No
exponential backoff default is applied; the request is abandoned.

**Fix plan:**
1. Default to 2-second initial backoff when `Retry-After` is absent or invalid.
2. Apply exponential backoff (2s, 4s, 8s) up to 3 retries.
3. Reference: SL viewer `indra/llcorehttp/`.

**Acceptance test:** Unit test that mocks 503 responses and verifies retries.

---

## PROTO-012 | `getBooleanOrFalse` loses null semantics

- **File:** `Linkpoint/src/main/java/com/linkpoint/protocol/llsd/LLSDValue.kt`
- **Category:** null-safety
- **Severity:** Low

**What is broken:**
`getBooleanOrFalse()` returns `false` for both "field is explicitly false" and
"field does not exist". Callers cannot distinguish the two cases.

**Fix plan:**
Add `getBooleanOrNull(): Boolean?` variant; migrate three-state callers.

**Acceptance test:** `./gradlew :Linkpoint:testDebugUnitTest --tests "*LLSDValue*"`

---

## PROTO-013 – PROTO-020 | Remaining findings

| ID | File (under `Linkpoint/src/main/java/com/linkpoint/`) | Category | Summary | Fix |
|---|---|---|---|---|
| PROTO-013 | `protocol/messages/MessageRouter.kt` | missing-diagnostics | Unhandled message IDs silently dropped; no counter. | Add `unhandledCount` AtomicLong; log at DEBUG. |
| PROTO-014 | `network/SecondLifeProtocol.kt:544` | incomplete-fallback | Display name lookup skipped when "capabilities not ready"; should queue for retry. | Queue and retry after caps arrive. |
| PROTO-015 | `protocol/circuit/LinkpointThreadedCircuit.kt` | incomplete-diagnostics | Circuit packet-loss counter exists but is not exposed to diagnostics UI. | Wire `packetLossRate` to DiagnosticsManager. |
| PROTO-016 | `protocol/llsd/LLSDParser.kt` (`parseXML`) | incomplete-parser | Same silent-catch-to-LLSDUndefined as PROTO-003. | Add logging in XML catch blocks. |
| PROTO-017 | `protocol/llsd/LLSDParser.kt` (`parseNotation`) | incomplete-parser | Same silent failure. | Same fix pattern as PROTO-003/016. |
| PROTO-018 | `protocol/messages/MessageTemplateCatalog.kt` | documentation-debt | `supportedParserOrWriterMessages` includes names from `LinkpointApp.parserSupportedMessageNamesForConformance`; no test verifies the set stays in sync. | Add static-analysis test. |
| PROTO-019 | `chat/ChatManager.kt` | incomplete-handler | Gesture trigger strings in chat (e.g. `/bow`) are not mapped to animation UUIDs. | Wire gesture inventory to chat input. |
| PROTO-020 | `protocol/messages/MessageIds.kt` | documentation-debt | 568 message IDs; ~130 are sim-internal constants mixed with viewer-relevant IDs. No annotation distinguishes them. | Add `@SimInternal` / `@ViewerRelevant` KDoc groupings. |
