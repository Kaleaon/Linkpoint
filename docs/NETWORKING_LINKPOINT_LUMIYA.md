# Linkpoint/Lumiya Second Life Networking Flow (High-Level)

This note documents how Linkpoint’s networking stack mirrors Lumiya’s approach for Second Life connections, using the current codebase as the source of truth. The focus is on the HTTP login → UDP circuit → capabilities/event queue sequence and how each layer hands off state.

## 1) Login and session bootstrap (HTTP XML-RPC)

Linkpoint’s grid connection orchestration starts in `GridConnection.performConnection()`, which performs the login step before any UDP work begins. The login step uses `CoreNetworkingService.login()` (not shown here) and builds the XML-RPC payload via `buildLoginXml()` to send `login_to_simulator` with the viewer/channel/device identifiers, MFA fields, and the standard “options” array expected by the grid. This mirrors the Lumiya-style handshake by capturing `agentId`, `sessionId`, `simIp`, `simPort`, and `circuitCode` in an `AuthReply` when the login succeeds.【F:Linkpoint/src/main/java/com/linkpoint/network/core/GridConnection.kt†L155-L268】【F:Linkpoint/src/main/java/com/linkpoint/network/core/GridConnection.kt†L324-L416】

Key outputs from login:
- **Session identifiers** (`agentId`, `sessionId`), **sim endpoint** (`simIp`, `simPort`), and **circuit code** are stored in `AuthReply` and used to establish the UDP circuit.【F:Linkpoint/src/main/java/com/linkpoint/network/core/GridConnection.kt†L207-L268】

## 2) UDP circuit establishment (UseCircuitCode → CompleteAgentMovement)

Once login data is available, `GridConnection` creates an `AgentCircuit`, which encapsulates the Lumiya-style circuit establishment state machine:
1. Connect UDP.
2. Send **UseCircuitCode**.
3. On ACK, send **CompleteAgentMovement**.
4. On ACK, mark the circuit ready and begin sending periodic **AgentUpdate** messages.

This sequence is explicitly documented in `AgentCircuit`’s class comment, and the step-by-step ACK handlers are implemented in `sendUseCircuitCode()` and `sendCompleteAgentMovement()` to advance circuit state and start agent updates on success.【F:Linkpoint/src/main/java/com/linkpoint/network/core/AgentCircuit.kt†L18-L154】【F:Linkpoint/src/main/java/com/linkpoint/network/core/AgentCircuit.kt†L155-L210】

Under the hood, the UDP transport is implemented by `UDPConnectionFixed`, a Lumiya-inspired, NIO-based connection that:
- Resets the sequence number to **0** for new circuits (required for `UseCircuitCode` to be accepted).
- Opens a `DatagramChannel`, registers a selector, and starts receive/ACK/timeout loops.
- Sends `UseCircuitCode` immediately after connecting, with `CompleteAgentMovement` deferred to post-handshake sequencing (coordinated by `AgentCircuit`).【F:Linkpoint/src/main/java/com/linkpoint/protocol/messages/UDPConnectionFixed.kt†L545-L691】【F:Linkpoint/src/main/java/com/linkpoint/protocol/messages/UDPConnectionFixed.kt†L1239-L1334】

## 3) Capabilities (seed caps → EventQueue long-poll)

After the UDP circuit is established, `GridConnection` initializes the **Capabilities** system if a seed capability URL was provided at login. The `CapabilityManager`:
- Sends an LLSD **array** of capability names to the seed URL and expects an LLSD **map** response of name → URL (per the SL spec).
- Applies Lumiya-style URL repair when a login URL is provided (to normalize grid-specific hostnames).
- Starts `EventQueueGet` long-polling when the EventQueue capability is present.

This is the HTTP side of the SL protocol in Linkpoint; it complements UDP by handling inventory, textures, and event queue updates via caps URLs.【F:Linkpoint/src/main/java/com/linkpoint/network/core/GridConnection.kt†L274-L305】【F:Linkpoint/src/main/java/com/linkpoint/protocol/capabilities/CapabilityManager.kt†L149-L356】

Event queue behavior mirrors Firestorm/Lumiya patterns:
- Long-polling is normal; a 502 response is treated as a timeout and retried immediately.
- Other retryable HTTP failures use exponential backoff with `Retry-After` support.
- Each event is parsed from LLSD and dispatched to registered handlers by message name.【F:Linkpoint/src/main/java/com/linkpoint/protocol/capabilities/CapabilityManager.kt†L652-L781】

## 4) Continuous updates and scene data routing

Once the circuit is ready, `AgentCircuit` starts periodic **AgentUpdate** messages at a 100ms interval (10 updates/sec), balancing responsiveness and battery use. Scene data (terrain/object updates) is routed by registering handlers on the `MessageRouter`, which is fed by the UDP receive loop inside `UDPConnectionFixed`.【F:Linkpoint/src/main/java/com/linkpoint/network/core/AgentCircuit.kt†L207-L228】【F:Linkpoint/src/main/java/com/linkpoint/protocol/messages/UDPConnectionFixed.kt†L660-L760】

## 5) Lumiya compatibility hooks

Two explicit Lumiya compatibility hooks are worth calling out:
- **Capability URL repair**: If `loginUrl` is provided, `CapabilityManager` runs seed and per-capability URLs through `LumiyaTranslationLayer` to ensure Agni-style URL compatibility before using them.【F:Linkpoint/src/main/java/com/linkpoint/protocol/capabilities/CapabilityManager.kt†L171-L246】【F:Linkpoint/src/main/java/com/linkpoint/protocol/capabilities/CapabilityManager.kt†L324-L343】
- **Circuit establishment semantics**: `AgentCircuit` uses Lumiya’s proven UseCircuitCode → CompleteAgentMovement ACK gating to consider the circuit ready, rather than assuming readiness immediately after UDP connect.【F:Linkpoint/src/main/java/com/linkpoint/network/core/AgentCircuit.kt†L18-L210】

---

If you want deeper, packet-level details for a specific message family (textures, inventory, chat), the next step would be to map manager classes (e.g., `TextureManager`, `IMManager`, `Inventory*`) back to their caps/UDP usage and add a per-feature message/capability map.
