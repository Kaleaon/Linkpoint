# Segment 01 — UDP Circuit & Connection Lifecycle

**Priority:** Critical. Directly maps to the live failure mode captured in the
2026-04-25 debug report (282 packets sent, 33 received, 0 ACKs received,
4 unanswered pings, `Reconnect Count: 0` despite `Always Reconnect: true`).

Reference implementation:
`lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/SLCircuit.java`,
`SLAgentCircuit.java`, `SLGridConnection.java`.

---

## 1. Lumiya circuit constants (the working baseline)

`SLCircuit.java:32-39`:

| Constant | Value | Purpose |
|---|---|---|
| `DEFAULT_IDLE_INTERVAL` | 1000 ms | Wakeup cadence when nothing pending |
| `FAST_IDLE_INTERVAL` | 100 ms | Wakeup cadence when packets queued |
| `MESSAGE_TIMEOUT_MILLIS` | 5000 ms | Reliable-packet retransmit timeout |
| `MESSAGE_MAX_RETRIES` | 3 | Reliable-packet retry budget |
| `NEED_PING_TIMEOUT` | 10000 ms | Idle gap before forcing a ping |
| `PING_INTERVAL` | 5000 ms | Min spacing between consecutive pings |
| `UNANSWERED_PINGS` | 3 | Trigger `ProcessTimeout()` when reached |
| `TRACK_HANDLED_PACKETS` | 1024 | Dedup recent inbound seqnums |

Linkpoint must adopt these (or document why it deviates).

---

## 2. The `TryProcessIdle()` watchdog (`SLCircuit.java:311-338`)

This is the **whole watchdog** in Lumiya — five lines of logic that we are not
matching:

```java
public void TryProcessIdle() {
    long now = SystemClock.elapsedRealtime();
    if (now < lastReceivedPacketMillis + NEED_PING_TIMEOUT
        || now < lastPingSent + PING_INTERVAL) return;
    if (pingSentCount >= UNANSWERED_PINGS) {
        if (timedOut) return;
        timedOut = true;
        ProcessTimeout();   // ← drops connection, triggers reconnect
        return;
    }
    StartPingCheck ping = new StartPingCheck();
    ping.PingID_Field.PingID = lastPingID++;
    ping.PingID_Field.OldestUnacked =
        unackedQueue.peek() != null ? unackedQueue.peek().seqNum : lastSeqNum.get();
    SendMessage(ping);          // SendMessage() routes through reliable queue
    pingSentCount++;
    lastPingSent = now;
}
```

Three rules to copy:

1. The **trigger is "no inbound packets for 10 s"**, not "I sent N pings". A
   silent receive path is the *symptom* the watchdog must catch — Lumiya keys
   off `lastReceivedPacketMillis`, not unanswered-ping count.
2. **`pingSentCount` resets to 0 on any inbound packet** (see
   `ProcessReceive()` body — every successful receive calls
   `lastReceivedPacketMillis = now; pingSentCount = 0;`). This keeps a healthy
   circuit from drifting toward timeout.
3. After 3 unacked pings (≈15 s of silence), `ProcessTimeout()` runs once
   (`timedOut` flag prevents double-fire), which propagates to
   `SLAgentCircuit.ProcessTimeout()` →
   `gridConn.processDisconnect(false, "Connection has timed out.")` →
   `Reconnect()`.

### Linkpoint mismatch (known)

`UDPConnectionFixed.kt:1089` (`checkPingHealth`) only fires reconnect when
`unansweredPings >= UNANSWERED_PINGS_DISCONNECT`. But `unansweredPings`
increments only when `sendStartPingCheck()` runs; on cellular NAT loss the
selector may stop selecting writable, the ping send is skipped, and the
counter never reaches the threshold. The watchdog has to be **time-based on
the receive side**, not state-based on the send side.

---

## 3. Reliable-vs-unreliable matrix

`SLCircuit.java:311-338` calls `SendMessage(startPingCheck)`. `SendMessage`
does **not** auto-set the reliable flag — it just enqueues. `StartPingCheck`
inherits `SLMessage.isReliable = false` and stays unreliable. The "reliability"
of pings comes from a parallel mechanism: the watchdog itself sends a fresh
ping every `PING_INTERVAL` (5 s) until either a `CompletePingCheck` arrives
(implicitly via the receive path resetting `pingSentCount`) or 3 unanswered
pings trip `ProcessTimeout()`. Pings are **not** retransmitted via the
`unackedQueue`.

The `OldestUnacked` field packed into outbound `StartPingCheck` is purely
informational — it tells the sim our oldest pending sequence number so it can
retransmit anything below that. Loss of the outbound ping itself is benign
because the watchdog will send another in 5 s.

**Do not** mark `StartPingCheck` reliable: doing so would put it on the
unacked queue, where it would be retransmitted by the resend lifecycle as
well as by the watchdog — duplicate work and no benefit. (An earlier draft
of this segment recommended otherwise; that was wrong.)

Reliability matrix to apply to the rest of the message set:

- **Must be reliable** — `UseCircuitCode`, `CompleteAgentMovement`,
  `LogoutRequest`, `TeleportLocationRequest`, `TeleportLandmarkRequest`,
  `TeleportLureRequest`, `RegionHandshakeReply`, `AgentThrottle`,
  `AgentSetAppearance`, `Inventory*Item`, `*Friendship`, `Object*`
  (modify/delete/select), and `MoneyTransferRequest`. Lumiya sets each of
  these explicitly — search `isReliable = true` across `slproto/`.
- **Must be unreliable** — `AgentUpdate`, `AgentAnimation`, `ObjectGrab*`,
  `ChatFromViewer`, `StartPingCheck`. The protocol expects them to be lossy.

---

## 4. The reliable ACK / resend lifecycle (`SLCircuit.java:116-147, 221-238, 243-288`)

Lumiya keeps **two queues** plus a pending-ACK list:

- `outgoingQueue` — packets waiting to go on the wire
- `unackedQueue` — reliable packets that have been sent and are awaiting ACK
- `pendingAcks` — list of inbound seqnums we need to ACK back

Lifecycle:

1. `SendMessage` → assigns `seqNum`, `sentTimeMillis`, pushes to
   `outgoingQueue`.
2. `ProcessTransmit()` pops one packet, `Pack()`s it, **piggybacks pending
   ACKs at the tail** (`AppendPendingAcks`), writes to the socket. If the
   packet was reliable, it moves to `unackedQueue`.
3. If `outgoingQueue` is empty but `pendingAcks` is non-empty, send a
   standalone `PacketAck` (up to ~1018-byte payload).
4. `ProcessResends()` runs on every wakeup:
   - For each entry in `unackedQueue`, if `now >= sentTimeMillis +
     PING_INTERVAL` (5 s), increment `retries`, set `isResent = true`, and
     re-queue. After 3 retries, `handleMessageTimeout()` fires — and
     listeners (e.g., teleport request) can surface the failure to the user.
5. `ProcessReceivedAck(seqNum)` removes from both `unackedQueue` and
   `outgoingQueue` (in case the ACK arrives before transmit), then fires the
   `onMessageAcknowledged` listener.

### Linkpoint state today

`UDPConnectionFixed.kt:463` has `handlePacketAck`, and
`UDPConnectionFixed.kt:1205` has `checkMessageTimeouts` with
`MESSAGE_TIMEOUT_MS = 5000` and `MAX_RETRIES = 3` — so the structural pieces
exist. The bug is **upstream**: nothing is being marked reliable, so nothing
ever enters the unacked queue, so the resend logic has no work to do, and
ACKs from the sim are ignored because we never expect them.

---

## 5. Selector-driven I/O loop (`SLCircuit` is single-threaded)

`SLCircuit.java:42-103` keeps **one** `DatagramChannel` registered with one
`Selector` (owned by `SLGridConnection`). Wakeups come from:

- Inbound packet readable (`OP_READ`)
- Outbound packet enqueued (`SendMessage` calls `selector.wakeup()`)
- Idle timer (1 s default, 100 ms when packets pending)

Lumiya recently collapsed Linkpoint's UDP layer to this same single-threaded
pattern (commit `cf194ed7 Collapse UDP layer to Lumiya's single-threaded I/O
pattern`). Verify the selector wakeup timer is **always** running — even
when no Activity is in foreground — so the watchdog's 10 s receive-idle
detection still fires.

The interest-op switch is:

```java
if (outgoingQueue.isEmpty() && pendingAcks.isEmpty())
    selectionKey.interestOps(OP_READ);          // 1
else
    selectionKey.interestOps(OP_READ|OP_WRITE); // 5
```

This must NOT clear `OP_READ` under any condition — otherwise inbound packets
are dropped at the kernel.

---

## 6. Reconnect ladder (`SLGridConnection.java:121-213`)

```java
private synchronized boolean Reconnect() {
    if (!userWantsConnected || !hadConnected
        || !GlobalOptions.getInstance().getAutoReconnect()
        || reconnectAttempts >= GlobalOptions.getInstance().getMaxReconnectAttempts()) {
        isReconnecting = false;
        return false;
    }
    if (connectionState == ConnectionState.Idle && authParams != null) {
        reconnectAttempts++;
        isReconnecting = true;
        eventBus.publish(new SLReconnectingEvent(reconnectAttempts));
        startConnecting(true, "last");   // true ⇒ pre-sleep 3s before attempt
    }
    return true;
}

private void startConnecting(final boolean preSleep, final String startLocation) {
    loginThread = new Thread(() -> {
        if (preSleep) Thread.sleep(3000);   // line 202 — backoff
        DoConnect(authParams, startLocation);
    });
    setConnectionState(ConnectionState.Connecting);
    loginThread.start();
}
```

Notes:

- **Three gating preconditions**: user hasn't logged out, we ever connected
  successfully, and auto-reconnect is enabled in settings. Linkpoint exposes
  `Always Reconnect: true` in the report — this corresponds to
  `getAutoReconnect`.
- **`reconnectAttempts` resets to 0** in three places: explicit `Connect()`
  (line 226), `notifyLoginSuccess()` (line 355), and `addTempCircuit` flow.
  Linkpoint's `Reconnect Count: 0` in the live report is consistent with
  *the watchdog never having fired*, not with a successful reset.
- **3-second `Thread.sleep` before retry.** No exponential backoff. A
  cellular NAT pinhole takes ~30–60 s to time out on the carrier; 3 s is fine
  because the *new* socket gets a fresh ephemeral port and a fresh NAT
  binding. (See segment 02 for the cellular details.)
- **Max attempts caps the loop** — Lumiya pulls the limit from
  `GlobalOptions.getMaxReconnectAttempts()`. Linkpoint should expose the same
  setting.

`reconnectOrDrop(boolean isLogin, boolean wasLogout, String reason)`
(line 142) is the single funnel: if `Reconnect()` returns true, it does
nothing further; otherwise it publishes `SLLoginResultEvent(false, …)` or
`SLDisconnectEvent(wasLogout, reason)`. Linkpoint's equivalent should not
have multiple disconnect paths competing.

---

## 7. End-to-end fail flow (the path Linkpoint missed)

```
sim stops responding (NAT pinhole closes)
  └─ 10 s elapse with no inbound packet
      └─ SLCircuit.TryProcessIdle() sends StartPingCheck #1
          └─ pingSentCount = 1
  └─ +5 s, still nothing
      └─ TryProcessIdle() sends StartPingCheck #2 (pingSentCount = 2)
  └─ +5 s, still nothing
      └─ TryProcessIdle() sends StartPingCheck #3 (pingSentCount = 3)
          └─ next call: pingSentCount >= UNANSWERED_PINGS
              └─ ProcessTimeout()
                  └─ SLAgentCircuit.ProcessTimeout()
                      └─ avatarControl.setEnableAgentUpdates(false)
                      └─ gridConn.processDisconnect(false, "Connection has timed out.")
                          └─ closeConnectionObjects()
                          └─ reconnectOrDrop(false, false, "Network connection lost.")
                              └─ Reconnect() ⇒ startConnecting(preSleep=true, "last")
                                  └─ Thread.sleep(3000)
                                  └─ SLAuth.Login(authParams.withLocation("last"))
                                  └─ startCircuit(authReply, null) — NEW socket, NEW NAT binding
```

Total: **~25 seconds from carrier-NAT death to fresh login**. Linkpoint's
debug report sat dead at 1.6 minutes. That gap is the bug.

---

## 8. Concrete Linkpoint work items

| ID | Item | Lumiya ref | Linkpoint ref | Status |
|---|---|---|---|---|
| L01-A | Time-based receive-idle watchdog (10 s no inbound → ping) | `SLCircuit.java:311-338` | `UDPConnectionFixed.kt:1118-1121` | ✅ already in place |
| L01-C | Reset `pingSentCount`/`unansweredPings` on every inbound packet | `SLCircuit.ProcessReceive()` | `UDPConnectionFixed.kt:847-848` | ✅ already in place |
| L01-D | Mark all auth/teleport/inventory/object-edit/money messages reliable | grep `isReliable = true` in `slproto/` | sweep call sites | open |
| L01-E | After N unanswered pings → reconnect → publish `RECONNECTING`/`CONNECTED`/`FAULTED` | `SLAgentCircuit.java:1503-1512`, `SLGridConnection.java:121-213` | `UDPConnectionFixed.NetworkStateTransition` + `LinkpointApp.setNetworkStateListener` | ✅ landed (this branch) |
| L01-F | UI subscriber that shows "Reconnecting…" pill; reads `protocol.stateManager.connectionStatus` | `SLGridConnection.java:129` | new Compose subscriber | open |
| L01-G | `getMaxReconnectAttempts` setting in preferences | `GlobalOptions.getMaxReconnectAttempts` | add to settings + read | open |
| L01-H | Verify selector keeps `OP_READ` set at all times the circuit is open | `SLCircuit.java:347-359` | review write-only paths | open |
| L01-I | Single disconnect funnel: all paths go through one `processDisconnect` → reconnect ladder | `SLGridConnection.java:142-154,295-313` | audit `LinkpointApp.kt:605` + `GridConnection.kt:279` (latter is dead code) | open |
| L01-J | Lower `UNANSWERED_PINGS_DISCONNECT` 5 → 3 to match Lumiya | `SLCircuit.UNANSWERED_PINGS = 3` | `LinkpointConstants.UNANSWERED_PINGS_DISCONNECT` | ✅ landed (this branch) |
| L01-K | Delete dead `GridConnection`/`GridConnectionManager` reconnect path or wire it to runtime | `SLGridConnection.java:121` | `network/core/GridConnection.kt:279`, `GridConnectionManager.kt` | open |

See segment 02 for the cellular-specific reasoning behind why these
particular numbers (10 s, 5 s, 3 retries, 3 s pre-sleep) work on mobile.
