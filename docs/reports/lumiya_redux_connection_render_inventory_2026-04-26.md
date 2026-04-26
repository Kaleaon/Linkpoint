# Lumiya-Redux Connection + Rendering Inventory (for Linkpoint engineering)

> Important: Lumiya-Redux is decompiled output. Treat all behavior as candidate reference that requires validation against Firestorm and/or Linden upstream before production adoption.

## A. Connection subsystem inventory

### A.1 Entry and orchestration
- `app/src/main/java/com/lumiyaviewer/lumiya/slproto/SLGridConnection.java`
  - Handles login thread spawn, connection state machine (`Idle/Connecting/Connected`), auto-reconnect attempt bounds, temp-circuit handoff during teleport, capability fetch bootstrap, event queue creation, and circuit creation.
  - Key behavior: sends `UseCircuitCode` immediately after circuit add + cap initialization.

### A.2 UDP circuit base
- `app/src/main/java/com/lumiyaviewer/lumiya/slproto/SLCircuit.java`
  - Datagram channel + selector integration.
  - Queues: outgoing, unacked, pending ACKs.
  - Reliability loop: resend aging + timeout callback.
  - Ping watchdog with unanswered ping cap.
  - ACK coalescing and standalone PacketAck path.
  - Message routing fallback through `SLMessageRouter`.

### A.3 Serial execution adapter
- `app/src/main/java/com/lumiyaviewer/lumiya/slproto/SLThreadingCircuit.java`
  - Dedicated worker thread and blocking runnable queue.
  - Circuit messages handled in sequence.

### A.4 Domain-heavy circuit
- `app/src/main/java/com/lumiyaviewer/lumiya/slproto/SLAgentCircuit.java`
  - Very large domain coordinator for chat, movement, object updates, caps events, teleport flow, pause/resume, and module orchestration.
  - `SendUseCode()` listener is a critical milestone gate (on ACK -> login success/teleport success + complete movement + module ready).

### A.5 Message packing/unpacking core
- `app/src/main/java/com/lumiyaviewer/lumiya/slproto/SLMessage.java`
  - LLUDP flags and decode/encode logic.
  - Zero coding and appended ACK handling.
  - Variable UTF/OEM string helpers.

### A.5.1 ACK handling verification checklist (Linkpoint parity target)
1. Parse appended ACK trailer whenever `LL_ACK_FLAG` is set, before payload decode.
2. Trim trailer bytes from payload decode limit to avoid parser contamination.
3. Apply ACKs even when payload message type is unknown or payload decode fails.
4. Support standalone `PacketAck` message path and ensure it converges with appended ACK processing.
5. Add tests for ACK-only, ACK+known-payload, ACK+unknown-payload, ACK+malformed-payload.

### A.6 Event queue and capabilities
- `app/src/main/java/com/lumiyaviewer/lumiya/slproto/caps/SLCaps.java`
- `app/src/main/java/com/lumiyaviewer/lumiya/slproto/caps/SLCapEventQueue.java`
  - Cap table fetch and long-poll event flow; events injected into circuit-domain handling.

---

## B. Rendering subsystem inventory

### B.1 Renderer orchestrator
- `app/src/main/java/com/lumiyaviewer/lumiya/render/WorldViewRenderer.java`
  - GL lifecycle (`GLSurfaceView.Renderer` + custom EGL context factory).
  - Pass orchestration: world draw + HUD + optional FXAA resolve.
  - Picking, HUD touch, screenshot, draw list refresh gating.
  - Responsive mode controls and interaction-aware behavior.

### B.2 Render context and shader policy
- `app/src/main/java/com/lumiyaviewer/lumiya/render/RenderContext.java`
  - Program compilation and fallback across GPU caps.
  - Backend abstraction via `render/backend/*`.
  - Matrix stack and draw-state ownership.
  - Resource retention for frame-lifetime objects.

### B.3 Spatial indexing and culling
- `app/src/main/java/com/lumiyaviewer/lumiya/render/spatial/*`
  - `SpatialObjectIndex`, `DrawList`, frustum structures.
  - Scene extraction into draw-ready batches.

### B.4 GL resource system
- `app/src/main/java/com/lumiyaviewer/lumiya/render/glres/*`
  - Resource manager and load queues.
  - Buffer/texture wrappers.

### B.5 Shader and material stack
- `app/src/main/java/com/lumiyaviewer/lumiya/render/shaders/*`
  - Prim, rigged mesh, avatar, sky, water, FXAA, quad, bounding box shader programs.

---

## C. Portability scorecard for Linkpoint

### C.1 High-value behavior to port (not raw code)
1. ACK-gated startup state transitions.
2. Circuit serial processing invariants.
3. Reliable resend/timeout semantics and ping watchdog behavior.
4. Draw list update cadence and interaction-aware rendering throttle.
5. GPU tier/fallback and shader compile diagnostics.

### C.2 Keep native Linkpoint implementations
1. UI stack (Compose / modern Android architecture).
2. Filament renderer framework.
3. Kotlin test/fuzz harness and CI integration.
4. Modernized LLSD parser hardening.

### C.3 Verification gates
1. Live-grid startup milestones observed in order.
2. No-inbound startup stall recovery test on high-latency cellular profiles.
3. Render fallback matrix across representative GPUs.
4. Packet parity tests against golden fixtures.
5. ACK safety suite proving no ACK loss across parser-failure and unknown-message scenarios.

## D. Reference precedence

When behavioral differences are found:
1. Linden upstream viewer protocol contract (primary truth),
2. Firestorm implementation behavior (production hardening reference),
3. Lumiya-Redux decompile behavior (mobile evidence and heuristics).
