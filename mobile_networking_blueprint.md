# Linkpoint Mobile Networking Blueprint

## 1) Goals
- Deliver stable, low-latency SL/OpenSim session behavior on mobile networks.
- Preserve protocol compatibility while reducing battery and radio wake churn.
- Improve resilience across network transitions (Wi-Fi ↔ LTE/5G), NAT rebinding, and app lifecycle interruptions.
- Provide deterministic observability and recovery policies.

## 2) Design principles
1. **Stateful core, stateless edges**: centralize transport state and reliability policy in one engine.
2. **Mobile-first reliability**: bounded retries with jitter/backoff, explicit deadlines, radio-aware pacing.
3. **Protocol-agnostic adapters**: decouple message semantics from wire transport (UDP/caps/HTTP).
4. **Lifecycle safety**: foreground/background-aware behavior and fast resume.
5. **Idempotent operations**: duplicate-safe handling for reconnect and delayed retries.

## 3) Reference architecture

### 3.1 Modules
- **NetworkOrchestrator**
  - Owns global session state machine and policy selection.
  - Coordinates transport adapters and failover.
- **TransportCore**
  - Handles packet framing, sequencing, acks, retransmission queues, RTT estimation.
  - Maintains per-circuit congestion windows and watchdog timers.
- **CapsClient**
  - Capability endpoints, auth token lifecycle, HTTP retries and caching.
- **PresenceChannel**
  - Heartbeats/keepalives optimized for mobile inactivity windows.
- **LifecycleCoordinator**
  - Hooks app lifecycle (active, backgrounded, suspended, resumed).
- **TelemetryPipeline**
  - Structured metrics/events for network health and policy tuning.

### 3.2 Adapter interfaces
```kotlin
interface WireAdapter {
    suspend fun connect(endpoint: Endpoint, hints: ConnectHints): ConnectResult
    suspend fun send(frame: NetworkFrame): SendResult
    suspend fun receive(timeoutMs: Long): ReceiveResult
    suspend fun close(reason: CloseReason)
}

interface ReliabilityPolicy {
    fun nextRetry(attempt: Int, reason: RetryReason): RetryPlan
    fun shouldFailover(snapshot: LinkSnapshot): Boolean
    fun pacing(snapshot: LinkSnapshot): PacingPlan
}
```

## 4) Session state machine

### 4.1 States
- `OFFLINE`
- `BOOTSTRAPPING` (DNS, time sync, token retrieval)
- `CONNECTING_PRIMARY`
- `AUTHENTICATING`
- `ACTIVE_STABLE`
- `ACTIVE_DEGRADED`
- `RECOVERING` (short reconnect path)
- `BACKGROUND_IDLE`
- `SUSPENDED`
- `TERMINATED`

### 4.2 Transition triggers
- Connectivity change events (network type switch, captive portal, IP change).
- Packet loss/RTT thresholds exceeded.
- Auth or caps token expiry.
- App lifecycle events.

### 4.3 Key transition rules
- `ACTIVE_STABLE -> ACTIVE_DEGRADED` if loss > threshold for N windows.
- `ACTIVE_DEGRADED -> RECOVERING` on persistent ack timeout burst.
- `RECOVERING -> ACTIVE_STABLE` only after handshake + state reconciliation.
- `BACKGROUND_IDLE -> SUSPENDED` after inactivity grace period.
- `SUSPENDED -> RECOVERING` on resume (never assume old path validity).

## 5) Reliability model

### 5.1 Message classes
- **Class A (critical ordered)**: auth/session/control messages.
- **Class B (important eventually ordered)**: inventory deltas, scene-critical updates.
- **Class C (loss-tolerant realtime)**: frequent ephemeral updates.

### 5.2 Retry/backoff
- Exponential backoff with decorrelated jitter.
- Per-class retry caps and deadlines.
- Circuit reset when watchdog detects stale unacked window.

### 5.3 De-duplication and idempotency
- Global message IDs + per-session replay window.
- Safe re-issue for class A/B with idempotency keys.

## 6) Mobile-aware behavior

### 6.1 Radio and battery policy
- Batch non-urgent outbound frames in short coalescing windows.
- Reduce heartbeat cadence during low-interaction periods.
- Prefer piggyback heartbeats on natural outbound traffic.

### 6.2 Network change handling
- Detect interface swap and NAT rebinding.
- Freeze non-critical traffic, attempt fast path rebind.
- If rebind fails quickly, escalate to reconnect with snapshot restore.

### 6.3 Background constraints
- In background: keep minimal presence channel only (policy/tier dependent).
- Suspend heavy sync and media streams.
- Queue non-critical outbound updates until foreground resume.

## 7) Security and trust
- TLS for capability HTTP traffic with strict certificate validation.
- Token rotation and secure storage with explicit expiration alarms.
- Replay and downgrade protections via nonce + version negotiation checks.

## 8) Observability and SLOs

### 8.1 Core metrics
- RTT p50/p95/p99
- Effective loss % (per class)
- Reconnect frequency and median recovery time
- Battery cost proxy (wakeups/hour, bytes foreground/background)
- Message queue depth and drop counts by class

### 8.2 Health grading
- **Green**: stable RTT/loss, no reconnect storm.
- **Yellow**: degraded thresholds crossed; activate conservative pacing.
- **Red**: failover and controlled reconnect with user-visible status.

## 9) Failure scenarios and playbooks
1. **Captive portal detected**
   - Pause protocol traffic, prompt user, poll minimal endpoint.
2. **Token expired during degraded link**
   - Prioritize token refresh class A lane, pause class B/C.
3. **Reconnect storm**
   - Circuit breaker: backoff floor, alternate endpoint probing, telemetry alert.
4. **Server-side throttling**
   - Respect retry-after headers; dynamic request shaping.

## 10) Integration plan for Linkpoint

### Phase 1: Foundations (2-3 sprints)
- Add `NetworkOrchestrator`, `WireAdapter`, and `ReliabilityPolicy` abstractions.
- Define message classes and queue contracts.
- Implement telemetry schema and health states.

### Phase 2: Core transport (3-5 sprints)
- Implement TransportCore sequencing, acks, retransmit queues, watchdog.
- Implement caps HTTP client with resilient retry/token management.
- Add reconnection state reconciliation contract.

### Phase 3: Mobile hardening (2-4 sprints)
- Add lifecycle-aware throttling/background policy.
- Add interface-change fast rebind and fallback reconnect.
- Tune pacing against battery/network test matrix.

### Phase 4: Verification (continuous)
- Automated chaos tests: packet loss, latency spikes, NAT rebinding, app suspend/resume.
- SLO gates in CI with synthetic scenarios.

## 11) Test matrix (minimum)
- Wi-Fi stable, Wi-Fi weak, LTE strong/weak, 5G transition.
- Foreground continuous usage vs intermittent usage.
- Background suspend/resume at 1, 5, 15, 30 minute intervals.
- Packet loss 1/3/5/10%, RTT 50/150/300/600ms.
- Server throttle and auth expiry injections.

## 12) Implementation notes for current repo
- Existing configuration entries (`network.default.grid`, timeout, bandwidth) can seed bootstrap policy values.
- Current networking documentation should be updated to track this blueprint as execution reference.
- Initial implementation should prioritize correctness and observability before throughput optimization.
