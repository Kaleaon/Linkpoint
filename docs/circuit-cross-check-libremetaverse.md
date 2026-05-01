# Linkpoint UDP circuit vs `cinderblocks/libremetaverse` cross-check

Comparison of `Linkpoint/src/main/java/com/linkpoint/protocol/messages/UDPConnectionFixed.kt`
against the canonical reference implementation in
[`cinderblocks/libremetaverse`](https://github.com/cinderblocks/libremetaverse)
(C#, .NET 6/7, BSD-3, v2.6.6 / 2026-04-12, 5 154 commits) — specifically:

- `LibreMetaverse/Simulator.cs` — per-region UDP circuit, 1 916 lines
- `LibreMetaverse/NetworkManager.cs` — connection lifecycle + login, 1 545 lines
- `LibreMetaverse/UDPBase.cs` — raw socket + read/write loops, 316 lines
- `LibreMetaverse/Settings.cs` — timing constants

This is the working notes for the circuit cross-check. One real bug
fixed on this branch; the rest are noted in case any become regressions
later.

## Constants — what each side uses

| Constant | libremetaverse | Linkpoint Wi-Fi | Linkpoint cellular |
|---|---|---|---|
| Ping interval | `PING_INTERVAL = 2200 ms` | `5 000 ms` | `20 000 ms` (NAT keepalive) |
| Resend timeout | `RESEND_TIMEOUT = 4 000 ms` | `MESSAGE_TIMEOUT_MS = 5 000 ms` | (same) |
| Max resends before drop | `MAX_RESEND_COUNT = 3` | `MESSAGE_MAX_RETRIES = 3` | (same) |
| Pending ACKs before flush | `MAX_PENDING_ACKS = 10` | flushes on interval *or* `ACK_FLUSH_QUEUE_THRESHOLD` | (same) |
| Network tick | `NETWORK_TICK_INTERVAL = 500 ms` | I/O thread + selector wakeup | (same) |
| Unanswered pings → tear down | **none — pings forever** | `UNANSWERED_PINGS_DISCONNECT = 3` | `12` |
| Inbound dedup ring size | `IncomingPacketIDCollection` capacity `1024` | `TRACK_HANDLED_PACKETS = 1024` | (same) |

**Verdict:** our cellular thresholds sit between libremetaverse's
"never give up" and our own Wi-Fi default. We ping ~2.3× less often
(5 s vs 2.2 s) but bundle keepalive into the ping path; the steady-
state outbound rate is roughly comparable. No tuning change wanted.

## What's the same (no change needed)

- **FLAG_RESENT semantics on resend.** Both impls: clear the
  per-packet `tickCount`, OR `0x20` into the wire flag byte, requeue
  the bytes for the I/O thread, increment the resend counter, drop
  on the third miss. (Linkpoint: `checkMessageTimeouts` at
  `UDPConnectionFixed.kt:1893`. libremetaverse: `ResendUnacked` at
  `Simulator.cs:1623`.)

- **PacketAck handling.** Both decode the inbound `PacketAck`
  high-frequency message into a list of acked sequence numbers and
  remove each from the inflight queue. Both notice when a
  `UseCircuitCode` ACK arrives and use it to gate
  `CompleteAgentMovement` (libremetaverse: `GotUseCircuitCodeAck`
  semaphore; Linkpoint: `completeAgentMovementSent.compareAndSet`).

- **Reliable-packet ACK send-out.** Both append received ACKs to a
  pending-acks queue and flush either on interval or on a queue-
  size threshold (libremetaverse `MAX_PENDING_ACKS = 10`,
  Linkpoint `ACK_FLUSH_QUEUE_THRESHOLD`).

- **Trailing-ACK piggyback on outbound packets.** Both use the
  `FLAG_ACK = 0x10` bit + a trailing ACK list when there are
  pending ACKs to bundle.

- **Zero-coding (FLAG_ZEROCODED = 0x80).** Both decode in-place
  from the wire bytes BEFORE handler dispatch.

- **Sequence number wrap.** Neither impl special-cases wrap; the
  field is a 32-bit unsigned integer and both rely on monotonic
  ascent within a session. (A single SL session can't exceed
  4 billion packets in any practical timeframe.)

## Real bugs fixed on this branch

### 1. Inbound duplicate-sequence detection was missing

`Simulator.cs:1539` runs every newly-decoded reliable packet
through `PacketArchive.TryEnqueue(sequence)`. If the sequence was
already processed, the packet is dropped before any handler fires
— the upstream's resend was triggered because *its* ACK was lost,
not because we never got the original. ACKing the duplicate
clears the upstream's NeedAck queue without re-firing chat / IM /
money / inventory handlers a second time.

**Linkpoint had this gap in the production path.** A separate
`LinkpointThreadedCircuit.kt` had the dedup logic but is intentionally
not used (see `LinkpointCircuitIntegration.kt:131` — "IMPORTANT: Do
NOT create a separate LinkpointThreadedCircuit here"). The
production I/O thread in `UDPConnectionFixed.kt` dispatched every
inbound reliable packet to its handler unconditionally. Symptoms
in real use: chat messages appearing twice on a flaky link,
double-counted L$ transfers in lossy regions, IMs duplicated on
reconnect.

**Fix on this branch:** new `InboundSequenceArchive` ring buffer
(direct port of libremetaverse's `IncomingPacketIDCollection`,
same 1024-entry capacity) checked just before
`dispatchMessageDirect`. ACKs for duplicates are still sent so
the sender's NeedAck queue clears; only the handler dispatch is
skipped. `FLAG_RESENT` duplicates log at DEBUG (expected),
non-resent duplicates log at WARN (sim-side glitch worth
noticing).

## Things worth a follow-up but not landed here

These are differences that didn't surface a concrete bug in the
debug captures so far. Filed for the next time someone touches
this code.

### Per-message frequency-band stats

libremetaverse's `Stats.IncrementSentPings()` /
`Stats.GetRecvBytes()` etc. are exposed via per-frequency-band
counters (high / medium / low / fixed). Linkpoint tracks total
counts but not by band. Useful for diagnosing throttle-band
saturation; not urgent.

### `FLAG_ZEROCODED` outbound is decided by message type, not size

libremetaverse picks zerocoding per outgoing message based on the
message-template `Trusted` / `Encoded` flags (`PacketDecoder.cs`).
Linkpoint currently zero-codes opportunistically based on
compressibility. This is sometimes incorrect: e.g.
`AgentSetAppearance` is required to NOT be zerocoded (LL bug
documented elsewhere). Worth auditing message-type-by-message-
type before the next appearance-baking refactor.

### Throttle-band send rates

libremetaverse's `AgentThrottle` setter scales each band by a
configurable multiplier. Linkpoint sends a fixed band split
(see `LinkpointApp.applyAdaptiveAgentThrottle`). On extremely
constrained links the band split should be cellular-aware too —
shrink texture/asset bands more aggressively than chat/IM. Out of
scope for now.

### `Simulator.SendPing` carries a 32-bit `OldestUnacked`

libremetaverse's `SendPing` sends a `StartPingCheck` with the
`OldestUnacked` field set to the lowest sequence number still in
`NeedAck`. The simulator uses this to short-circuit its own
inflight-tracking. Linkpoint sends `OldestUnacked = 0` always
(see the packet hex dumps in the debug reports — pings are 12
bytes with the four-byte trailer = 0). It works, but means the
sim wastes a small amount of state. Not a real correctness bug.

## Sources for the comparison

Pulled directly via `curl https://raw.githubusercontent.com/cinderblocks/libremetaverse/master/...`
on 2026-05-01 against `master` (commit `a30a40a` at time of
writing — see `git log` of cinderblocks/libremetaverse for exact
SHA correspondence). The C# files are in `LibreMetaverse/`:

- [Simulator.cs](https://github.com/cinderblocks/libremetaverse/blob/master/LibreMetaverse/Simulator.cs)
- [NetworkManager.cs](https://github.com/cinderblocks/libremetaverse/blob/master/LibreMetaverse/NetworkManager.cs)
- [UDPBase.cs](https://github.com/cinderblocks/libremetaverse/blob/master/LibreMetaverse/UDPBase.cs)
- [Settings.cs](https://github.com/cinderblocks/libremetaverse/blob/master/LibreMetaverse/Settings.cs)
