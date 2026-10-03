# Lumiya-Redux Deep Dive Report for Linkpoint
_Date:_ 2026-04-26  
_Source analyzed:_ `https://github.com/Kaleaon/Lumiya-Redux` @ `dd4f6ff`

## 1) Executive summary

This deep dive confirms that Lumiya-Redux remains the strongest behavioral reference for **mobile-resilient SL protocol handling** and a mature **deferred GL renderer architecture**. It is not magically flawless, but it is highly opinionated in exactly the areas where Linkpoint still shows fragility in field logs:

1. **Single-threaded circuit core with explicit reliable queue semantics** (`SLCircuit` + `SLThreadingCircuit`).
2. **ACK-gated bring-up path** (`UseCircuitCode` acknowledged before movement completion + module readiness).
3. **Scene-build/render decoupling** (spatial index + draw list update cadence + adaptive responsiveness).
4. **GPU capability stratification and shader fallback policy** (`GpuCapabilities`, ES3 branch + ES2 fallback).

The repository is also explicit about strategy: Linkpoint is the ship vehicle while Lumiya-Redux is the protocol/behavior reference tree.

**Critical framing update:** Lumiya-Redux is a **decompiled corpus**, not a clean canonical source tree. Any behavior imported from it must be validated against at least one canonical reference path (Firestorm and/or Linden upstream viewer) before landing in Linkpoint.

## 2) Repository posture and scale

### 2.1 Snapshot
- HEAD: `dd4f6ff` (2026-04-26)
- Total files: ~11,234
- Code-like files (`kt/java/cpp/c/h/glsl/vert/frag`): ~4,484

### 2.2 Protocol and rendering size signals
- `slproto/messages/` classes: **477** generated message classes.
- `render/` package files: **106**.
- `render/shaders/` files: **18**.
- `render/glres/` files: **20**.

### 2.3 Heavy classes (LOC)
- `SLAgentCircuit.java`: 2,120 lines
- `WorldViewRenderer.java`: 969 lines
- `RenderContext.java`: 660 lines
- `SLCircuit.java`: 377 lines
- `SLGridConnection.java`: 379 lines

Implication: this is a behavior-rich legacy system, not a small sample app.

---

## 3) Connections deep dive (Lumiya-Redux)

### 3.1 Topology and lifecycle

Core classes:
- `SLGridConnection` orchestrates login/auth/caps/circuit creation and reconnect policy.
- `SLCircuit` owns UDP socket, selector registration, outgoing queue, unacked queue, ACK append/send handling, resend, ping watchdog.
- `SLThreadingCircuit` serializes message handling on a dedicated worker thread.
- `SLAgentCircuit` binds domain behavior (movement/chat/objects/caps events/teleport/login success transitions) on top of `SLThreadingCircuit`.

Bring-up chain:
1. XMLRPC login (`SLAuth`/`SLAuthReply`) returns sim endpoint + circuit credentials.
2. `SLGridConnection.startCircuit(...)` fetches seed capabilities, creates `SLAgentCircuit`, starts event queue, adds circuit to connection, sends `UseCircuitCode`.
3. `SLAgentCircuit.SendUseCode()` marks packet reliable and installs event listener.
4. On ACK, it notifies login success / teleport success, sends `CompleteAgentMovement`, and marks modules ready.

### 3.2 Reliability model

`SLMessage` handles:
- frequency-based message-id decode;
- ack flag, reliable flag, resent flag, zero-coding flag;
- appended ACK trailer (count + seq list);
- zero encode/decode;
- pack/unpack with explicit byte-order transitions.

`SLCircuit` has:
- `outgoingQueue` for immediate sends,
- `unackedQueue` for reliable messages in flight,
- `pendingAcks` for ACK aggregation.

Retry behavior:
- timeout window around 5s in resend checks,
- max retries around 3 for in-flight reliable messages,
- timeout callbacks when retries exhausted.

Idle ping watchdog:
- if no inbound for `NEED_PING_TIMEOUT` and interval elapsed, send `StartPingCheck`;
- after repeated unanswered pings, circuit transitions to timeout behavior.

### 3.2.1 ACK packet handling (do-not-drop checklist)

ACK correctness is load-bearing for startup and steady-state reliability. From Lumiya decompile behavior in `SLMessage.Unpack(...)` and `SLCircuit` send/receive flow:

1. When `LL_ACK_FLAG` is set in packet header, read trailing ACK count byte and ACK sequence list from end-of-packet before payload decode.
2. Remove ACK trailer bytes from effective decode payload (`limit` adjustment) so parser does not treat ACK trailer as message payload.
3. Feed each ACK sequence into reliable queue reconciliation (`ProcessReceivedAck`) against:
   - in-flight reliable queue, and
   - queued-but-not-yet-sent reliable entries (race-safe cleanup path).
4. Preserve and process standalone `PacketAck` (`0xFFFFFFFB`) messages as first-class ACK carrier.
5. Do **not** treat unknown payload message IDs as fatal to ACK handling: ACK trailer processing must still occur first.

Linkpoint import rule: ACK extraction and application must be structurally decoupled from message parser success/failure so ACKs are never silently discarded by downstream parse exceptions.

### 3.3 Threading model

`SLThreadingCircuit` uses a dedicated thread (named `SLCircuit`) polling a blocking queue. Message handling and idle work execute serially there. This significantly reduces lock-contention and race surfaces compared to multi-dispatch coroutine fan-out.

### 3.4 Capabilities and event queue integration

`SLGridConnection` initializes `SLCaps`, creates `SLCapEventQueue`, and binds cap events into `SLAgentCircuit` via a queue. `SLAgentCircuit` drains cap events in its own processing path (`capsEventQueue`), maintaining ordering relative to UDP-domain side effects.

### 3.5 Reconnect posture

`SLGridConnection` tracks:
- `userWantsConnected`
- `hadConnected`
- reconnect attempts bounded by global options
- explicit reconnect event publication

It cleanly tears down/login thread/circuit/event queue and can re-initiate from prior auth params and start location.

---

## 4) Rendering deep dive (Lumiya-Redux)

### 4.1 High-level architecture

- `WorldViewRenderer` is the frame orchestrator (GL lifecycle, draw passes, screenshot, picking, HUD, responsiveness mode).
- `RenderContext` centralizes shader programs, GPU capability decisions, backend abstraction, draw state, frame resource retention, matrix stacks.
- `SpatialObjectIndex`/`SpatialIndex` provide scene indexing and frustum extraction (`DrawList`).
- `Drawable*` classes encapsulate object, avatar, terrain, HUD and face-level render behavior.
- `GLResourceManager` + `glres/*` provide managed GL resources/load queues.

### 4.2 Frame loop and pass ordering

Within `WorldViewRenderer.onDrawFrame(...)`:
1. Optional offscreen framebuffer for FXAA.
2. Scene draw from frustum-derived `DrawList`.
3. Avatar rendering + object passes.
4. HUD/overlay conditional pass.
5. Optional FXAA resolve pass to system framebuffer.

It includes adaptive draw list refresh gates:
- time-based (`MIN_DRAW_LIST_UPDATE_INTERVAL`),
- frame-count based (`MIN_DRAW_LIST_UPDATE_FRAMES`).

### 4.3 GPU capability and shader fallback strategy

`RenderContext` chooses path from `GpuCapabilities`:
- feature tiering,
- ES3 shader compile attempt,
- runtime fallback to ES2/legacy shaders on failure,
- optional quirk gating (`quirkDisableEs3Shaders`) for problematic GPU families.

This explains practical resilience across a diverse Android device matrix.

### 4.4 Resource management and queues

`RenderContext` composes sync/async load queues with GL resource manager + drawable store, allowing asynchronous prep and render-thread-safe consume.

### 4.5 Interaction and UX hooks

`WorldViewRenderer` integrates:
- object picking by ray intersection,
- HUD touch hit-testing and object face touch relay,
- screenshot capture with `glReadPixels`,
- responsive mode toggles tied to interaction/fling state.

---

## 5) Direct relevance to current Linkpoint issues

The debug report signature observed in Linkpoint (outbound UDP packets only, zero inbound, region handshake absent) aligns with startup robustness gaps where Lumiya has stronger defensive sequencing.

Top deltas to import:

1. **Bootstrap reliability policy**
   - Lumiya treats circuit bring-up messages as reliable + ACK-driven state progression.
   - Linkpoint should enforce equivalent startup state transitions and retransmit policy before declaring stalled world load.

2. **Serial circuit execution model**
   - Lumiya’s single circuit worker thread reduces nondeterministic timing bugs.
   - Linkpoint should keep a deterministic circuit core and avoid hot-path multi-scheduler diffusion.

3. **Cap event and UDP event ordering discipline**
   - Lumiya queues cap events and integrates them in controlled processing flow.
   - Linkpoint should preserve causality between HTTP cap events and UDP state updates.

4. **Renderer adaptability**
   - Capability-tiered shader compile/fallback model in Lumiya is explicit.
   - Linkpoint’s modern renderer can keep architecture but should preserve device-specific fallback policy depth.

5. **ACK safety**
   - ACK processing in Lumiya is applied independently from high-level message semantics.
   - Linkpoint should guard against code paths where malformed/unknown payloads can accidentally bypass ACK trailer ingestion.

---

## 6) Concrete import backlog for Linkpoint

### P0 (stability first)
1. Add ACK-gated handshake machine parity for startup messages.
2. Add early startup retransmit window (before full timeout horizon).
3. Add explicit “provisional startup” diagnostics window to avoid false hard-failure logs <10s after connect.
4. Add ACK parser invariants: trailer extraction must run before payload decode and before message-class dispatch.
5. Add a negative test corpus where payload decode intentionally fails but appended ACKs must still clear reliable queue entries.

### P1 (determinism)
4. Enforce serialized circuit-side state mutations (single queue/thread discipline for UDP core).
5. Bind cap-event processing with ordering guarantees relative to core state.

### P2 (render robustness)
6. Expand GPU capability matrix + shader fallback rules based on vendor quirks.
7. Add explicit responsive-mode LOD/frame-stride policy analogous to Lumiya draw-list throttling under interaction and load.

### P3 (observability)
8. Track startup milestones as wire-level events: `UseCircuitCode sent`, `UseCircuitCode acked`, `CompleteAgentMovement sent`, `RegionHandshake recv`, `EventQueue active`.
9. Add counters for negative/underflow states in asset pipelines (e.g., pending download counters).
10. Add explicit ACK telemetry: `acks_appended_recv`, `acks_packetack_recv`, `acks_applied`, `acks_dropped_parse_path` (expected zero).

---

## 7) Linkpoint module mapping against Lumiya-Redux

| Lumiya-Redux | Linkpoint target area | Import type |
|---|---|---|
| `slproto/SLCircuit.java` | `protocol/messages/UDPConnectionFixed.kt`, `protocol/circuit/*` | behavioral parity for reliable UDP + ACK + ping timeout |
| `slproto/SLThreadingCircuit.java` | circuit execution model | deterministic serial execution model |
| `slproto/SLAgentCircuit.java` | message managers + startup handshake logic | handshake gating, module-ready sequencing |
| `slproto/SLGridConnection.java` | session/login bootstrap and reconnect | reconnect policy and startup orchestration |
| `render/WorldViewRenderer.java` | render manager + Lumiya engine path | pass ordering, draw-list refresh cadence, interaction responsiveness |
| `render/RenderContext.java` | GL context/shader subsystem | capability-tiered shader policy and fallback |
| `render/spatial/*` | scene index/culling | draw-list extraction + frustum policy |
| `render/glres/*` | render resource mgmt | resource lifecycle + load queue patterns |

---

## 8) Risk notes

1. Decompiler artifacts still exist in parts of Lumiya-Redux. Treat this repository as **behavioral reference**, not copy/paste source.
2. Some methods are stubs in decompiled output (e.g., known login gaps in historical analysis docs). Cross-check with upstream secondlife/viewer and Firestorm when uncertain.
3. Preserve Third-Party Viewer compliance constraints during behavior import.

## 8.1 Canonical reference precedence (when sources disagree)

1. Linden upstream (`secondlife/viewer`) for wire-schema and protocol contract.
2. Firestorm for production-hardened viewer behavior and practical edge handling.
3. Lumiya-Redux as Android-mobile behavioral evidence.

If Lumiya decompile behavior conflicts with Firestorm/upstream, Linkpoint should follow canonical sources and record deviation rationale.

---

## 9) Final conclusion

Lumiya-Redux is the best available Android-native reference for **connection resilience and renderer pragmatism** under real-world mobile conditions. The fastest path for Linkpoint is not transplanting Java code, but systematically importing Lumiya’s proven state-machine and fallback behaviors into Linkpoint’s modern Kotlin architecture with strict test gates.
