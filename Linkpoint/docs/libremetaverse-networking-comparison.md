# LibreMetaverse vs Linkpoint: Connection & Networking Deep Dive (with concrete Linkpoint fixes)

## Scope

This document compares connection + networking behavior in:

- **LibreMetaverse** (`https://github.com/cinderblocks/libremetaverse`)
- **Linkpoint** (this repository)

and converts the comparison into concrete, implementation-ready fixes for Linkpoint.

---

## 1) What LibreMetaverse centralizes that Linkpoint currently splits

## LibreMetaverse model (observed)

LibreMetaverse exposes a **single networking authority** in `NetworkManager`:

- simulator connect/disconnect lifecycle (`Connect(...)`, `DisconnectSim(...)`)
- explicit connection state (`Connected`)
- simulator lifecycle events (`SimConnecting`, `SimConnected`, disconnected variants)
- capability event callback registration (`RegisterEventCallback`, `UnregisterEventCallback`)
- seeded simulator caps wiring in connect flow

At `GridClient` scope it also maintains a dedicated HTTP capability client (`HttpCapsClient`), keeping UDP simulator transport and caps/event queue in one unified network stack.

## Linkpoint model (observed)

Linkpoint networking responsibility is distributed across three places:

1. **Feature managers** (chat/profiles/snapshots/etc.) deciding UDP vs caps behavior.
2. **`UDPConnectionFixed` production path** with reconnect/watchdog parity expectations.
3. **`LLMessageSystem` + `LLCircuit`** transport primitives with sequence, resend tracking, and ACK state.

The parity target (Lumiya behavior) is documented, but ownership is fragmented.

### Why this matters

When ownership is split, correctness becomes “convention-based”: a feature manager forgetting to mark a message reliable, or diverging caps fallback policy, can break behavior without transport-layer protection.

---

## 2) Reliable delivery mechanics: where Linkpoint is strong vs fragile

## Strong pieces already present

`LLCircuit` already has useful building blocks:

- `waitingAcks` resend map
- `pendingAcks` accumulation
- sequence number generation
- packet-in liveness updates via `updateLastPacketInTime()`

`LLMessageSystem.send(...)` correctly places reliable messages in resend tracking (`queueForResend(...)`).

## Fragility points

1. **ACK semantics are overloaded in `LLCircuit.ackPacket(...)`.**
   The same method currently both queues outbound ACK intent and removes from resend wait map. These are two distinct operations and should be separated to avoid subtle coupling.
2. **No explicit transport-level reliability policy map.**
   Emission reliability is call-site dependent (`sendReliable` vs `sendUnreliable`) rather than template-driven; this can drift from protocol intent.
3. **Reconnect/watchdog policy is not represented in `LLMessageSystem`.**
   The parity document requires receive-idle driven timeout logic and reconnect ladder behavior in the production path.

---

## 3) Capability/eventing split: policy drift risk

Feature modules currently choose caps-first or UDP-first independently. This is flexible but risks inconsistent behavior and duplicated fallback logic.

LibreMetaverse’s shape suggests a better pattern: central network facade resolves capability availability and event-queue subscription, while domain managers consume normalized APIs.

---

## 4) Concrete fixes for Linkpoint (prioritized)

## P0 — make reliability semantics explicit in transport

1. Split `LLCircuit.ackPacket(...)` into:
   - `recordInboundSequenceForAck(seq)` (only pending ACK list)
   - `handleAckForOutboundSequence(seq)` (only resend-map removal)
2. Update call sites so inbound packet processing and outbound ACK processing are never conflated.

**Benefit:** removes hidden coupling and makes resend logic auditable.

## P0 — move message reliability to a central policy table

Create a transport reliability policy (template/message ID -> reliable bool), then make `send(...)` enforce it by default.

- Feature layers can still override only in exceptional, documented cases.
- Add tests that assert key protocol messages remain reliable/unreliable per parity expectations.

**Benefit:** prevents reliability regressions caused by individual call-site mistakes.

## P1 — introduce a `NetworkSessionFacade` authority

Add a single facade (or manager) owning:

- current/default simulator/session
- connection state machine (connecting/connected/reconnecting/disconnected)
- watchdog and reconnect decision logic
- caps event callback registration lifecycle

Feature managers should depend on this facade, not raw transport + raw capability clients.

**Benefit:** one source of truth for lifecycle and diagnostics.

## P1 — enforce receive-idle watchdog invariants

Codify parity requirements from `docs/lumiya-parity/01-connection-lifecycle.md` into tests/checks:

- no inbound for timeout window -> ping checks
- repeated missed pings -> single timeout transition
- timeout transition -> reconnect attempt when policy gates allow

**Benefit:** converts fragile runtime behavior into testable contract.

## P2 — unify caps fallback policy by domain contract

For each feature area, define and codify:

- primary transport (caps vs UDP)
- fallback trigger conditions
- retry/backoff/timeout behavior

Expose as reusable helpers to stop per-manager reimplementation.

**Benefit:** consistency, less duplicated error handling, cleaner telemetry.

---

## 5) Suggested implementation slices (small, shippable)

### Slice A (safe refactor, no behavior change intended)

- Refactor `LLCircuit` ACK API split (`ackPacket` decomposition).
- Add/adjust unit tests for ACK bookkeeping only.

### Slice B (behavior hardening)

- Add centralized reliability policy lookup in transport send path.
- Gate with tests for known message classes.

### Slice C (architecture convergence)

- Add `NetworkSessionFacade` wrapper around current production path.
- Migrate one module (chat) to consume facade first.

### Slice D (watchdog/reconnect confidence)

- Add integration tests around timeout->reconnect transitions.
- Assert no duplicate timeout transitions.

---

## 6) Immediate next code changes recommended

1. **Refactor `LLCircuit` ACK API split first** (low-risk, high clarity).
2. **Add reliability policy registry** near message template catalog.
3. **Add a minimal connection-state model object** used by `UDPConnectionFixed` and feature managers.

These three changes are small enough to land incrementally but directly support parity and long-term architecture cleanup.

---

## 7) Bottom line

LibreMetaverse demonstrates the payoff of a centralized network core. Linkpoint already has many needed primitives, but today they are distributed across transport classes, feature managers, and parity docs.

The best path forward is incremental centralization:

- tighten ACK/reliability semantics in transport,
- enforce reliability/watchdog rules with tests,
- then converge lifecycle + caps ownership behind one network facade.
