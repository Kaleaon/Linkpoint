# Segment 02 — Cellular Networks & Background Survival

**Priority:** Critical. The 2026-04-25 debug report captured the exact
failure mode this segment addresses: `Network Type: CELLULAR_LTE`,
`Average Latency: 6652 ms`, `Last Packet Received: 29.8 s ago`,
`Surface Ready: ✗`, `Time Since Last Frame: 30.8 s`. Connection looks alive
to the OS but is functionally dead end-to-end.

The core insight: **Lumiya does not detect "cellular" or "wifi" anywhere in
the codebase** (verified — no `ConnectivityManager`, `TYPE_MOBILE`, or
`NetworkCallback` references in the protocol layer). It survives mobile
networks by being aggressive about three things instead: keepalives, fast
disconnect detection, and fast reconnect. We should follow the same model.

---

## 1. Why cellular kills SL UDP circuits

### 1.1 Carrier NAT UDP timeout

Mobile carriers run large CGNAT pools and time out idle UDP mappings
aggressively. Empirical numbers:

| Carrier behavior | Typical timeout |
|---|---|
| LTE/5G UDP idle timeout | 30–60 s (some as low as 20 s) |
| Wi-Fi home router UDP idle | 60–300 s |
| Wi-Fi enterprise / captive portals | varies, often 30 s |
| Doze mode network suspension | network sleeps until next maintenance window |

Once the NAT mapping is gone, the sim's reply packets are dropped at the
carrier edge. Our socket *looks* fine — `socket.isConnected() == true`,
`channel.isOpen() == true` — because UDP has no connection state in the
kernel. The only signal is "no packets are coming back", which is exactly
what the receive-idle watchdog in segment 01 detects.

### 1.2 Why `AgentUpdate` keepalives aren't enough on their own

Lumiya sends `AgentUpdate` continuously (the avatar controller drives them
when modules.avatarControl.setEnableAgentUpdates(true)). These keep the
upstream NAT binding alive **as long as they're actually being transmitted**.
But:

- `AgentUpdate` is unreliable, so a dropped one isn't retransmitted.
- If the renderer/UI thread stops driving the controller (Activity paused,
  surface lost), Lumiya's `setEnableAgentUpdates(false)` may stop sending.
- `StartPingCheck` is the canonical 5–10 s keepalive that **does not depend
  on UI state**.

This is why the ping watchdog (segment 01) is the load-bearing piece — it's
the keepalive of last resort.

### 1.3 Doze and App Standby (Android 6+)

When the screen is off and the device unplugged, Android can:

- Defer network access entirely until the next maintenance window
  (~9 minutes initially, doubling)
- Defer wakelocks (CPU sleeps between selector wakeups)
- Disable the WiFi multicast lock

A **foreground service with an ongoing notification** is the documented
escape hatch — it's what Lumiya uses (`GridConnectionService` extends
`Service`, runs `startForeground()` from very early in the lifecycle).
Linkpoint already has `LinkpointConnectionService` doing the same; the gap
is in *what runs* inside it.

### 1.4 The "Surface Ready: ✗" trap

The debug report shows `SwapChain: ✗`, `Surface Ready: ✗`,
`Time Since Last Frame: 30.8 s` — i.e., the WorldView Activity went to the
background or was paused. **The UDP circuit must be completely independent
of the renderer.** The selector loop, ping watchdog, ACK processing, and
reconnect ladder must all keep running with zero dependencies on
`GLSurfaceView`, `Filament`, or the Activity.

Lumiya enforces this structurally: the circuit lives on a thread owned by
`SLGridConnection`, which is owned by `GridConnectionService`. The renderer
in `WorldViewActivity` is a *consumer* of the circuit's published events,
never a driver of its lifecycle.

---

## 2. Lumiya's mobile-survival mechanisms (audit results)

Verified with `grep` across `lumiya_decompiled_source/com/lumiyaviewer/`:

| Mechanism | Lumiya implementation | Status |
|---|---|---|
| Foreground service | `GridConnectionService extends Service`, `startForeground(notificationId, notif)` | ✅ used |
| `WAKE_LOCK` permission | declared in manifest | ✅ used |
| Cellular detection (`ConnectivityManager`, `TYPE_MOBILE`) | **not used** in slproto | ⛔ deliberate — no special-casing |
| `NetworkCallback` for connectivity changes | **not used** in slproto | ⛔ relies on watchdog instead |
| Adaptive bandwidth / network-type-aware throttle | `AgentThrottle` is sent once at circuit ready (no runtime adaptation) | partial |
| WiFi lock | not used | ⛔ |
| Per-app data-saver awareness | not used | ⛔ |
| Heartbeat | `StartPingCheck` every 5 s once `NEED_PING_TIMEOUT` (10 s of silence) elapses | ✅ load-bearing |
| Fast reconnect | 3 s pre-sleep, then full XMLRPC re-login on `"last"` location | ✅ |

### 2.1 The bandwidth budget Lumiya advertises

`AgentThrottle` (`messages/AgentThrottle.java`) is a 7-channel byte/sec
budget. Lumiya sends it once after circuit-ready and never adjusts it. The
seven channels are (per LL message template):

| Channel | Purpose | Typical budget |
|---|---|---|
| 0 — Resend | Reliable retransmits | ~150 kbps |
| 1 — Land | Terrain (`LayerData`) | ~150 kbps |
| 2 — Wind | Wind data | ~10 kbps |
| 3 — Cloud | Cloud data | ~10 kbps |
| 4 — Task | Object updates (`ObjectUpdate*`) | ~500 kbps |
| 5 — Texture | Texture pipeline | ~750 kbps |
| 6 — Asset | Other assets (mesh, sound) | ~500 kbps |

Total ~2 Mbps. On cellular this is plausible, but Linkpoint should consider
**adapting** based on observed average latency / loss, not advertising the
desktop default. The `Quality Level: POOR` in the debug report came from a
6.6 s average latency — at that latency, anything other than agent updates
and pings is essentially useless and the throttle should be cut.

---

## 3. Mobile-specific work items for Linkpoint

### 3.1 Independence from UI (must)

| ID | Item | Where |
|---|---|---|
| L02-A | Audit the circuit thread for any `Handler` posted to a `Looper` from an Activity, any `View` reference, any `GLSurfaceView` callback. There must be zero. | sweep `network/`, `services/` for `Activity`, `View`, `Surface*` imports |
| L02-B | Selector wakeup timer must run inside the foreground service, not inside the `WorldViewActivity` lifecycle | `LinkpointConnectionService.kt`, ensure idle ticker is started in `onStartCommand` and not after surface is created |
| L02-C | `AgentUpdate` scheduler must not stop on `onPause()`/`onStop()` of any Activity — only on logout / disconnect | grep `setEnableAgentUpdates`, `agentUpdateJob` for cancellation paths |
| L02-D | Explicit unit test: pause Activity, verify pings still being sent and ACKs still being processed | new instrumentation test |

### 3.2 Wakelocks and foreground type

| ID | Item | Where |
|---|---|---|
| L02-E | Hold a `PARTIAL_WAKE_LOCK` while the circuit is connected | service `onCreate`/`onDestroy` |
| L02-F | Use `FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE` (Android 14+ requires explicit type) | `AndroidManifest.xml` and `startForeground(id, notif, type)` |
| L02-G | Notification channel for the persistent "Connected to <Region>" entry | `NotificationManager` in service |
| L02-H | Optional: `WifiManager.WifiLock` while on Wi-Fi (high-perf if user opts in) | new setting |

### 3.3 Receive-side detection (already in segment 01, restated for cellular)

| ID | Item | Why it matters on cellular |
|---|---|---|
| L02-I | Watchdog keys off `lastPacketReceivedAt`, not `unansweredPings` | When NAT dies, send may keep "succeeding" at the kernel but nothing comes back. Counting unanswered pings is fine; counting time-since-receive is necessary. |
| L02-J | 10 s receive-idle threshold | Tight enough to detect carrier NAT death within 25 s; loose enough not to false-fire on momentary radio drops |
| L02-K | 3 s pre-sleep before reconnect | Enough for the radio to renegotiate after a mode change (LTE↔5G); short enough that the user sees recovery, not abandonment |

### 3.4 Adaptive throttle

| ID | Item | Lumiya ref | Linkpoint ref |
|---|---|---|---|
| L02-L | When Network Quality is `POOR`, send a reduced `AgentThrottle` (e.g. cap textures at 100 kbps, keep land/task at minimal) | `messages/AgentThrottle.java`, sent once in `SLAgentCircuit` post-handshake | new logic in throttle setter |
| L02-M | Re-send `AgentThrottle` when latency moves between bands (e.g. <500 ms vs 500–2000 ms vs >2000 ms) | hysteresis / debounce required | `NetworkQualityMonitor` |
| L02-N | Pause non-critical fetches (texture queue, mesh queue) when latency >2 s | new gate in `TextureFetcher`/`MeshFetcher` | `TextureManager`, `MeshManager` |

### 3.5 ConnectivityManager — yes or no?

Lumiya does not register a `NetworkCallback`. Reasoning: Android can take
several seconds to publish a connectivity-change event after the radio
actually switches, and during that gap the watchdog is already detecting
silence. Adding a `NetworkCallback` does not speed up detection in practice.

**Recommendation for Linkpoint**: do not depend on `ConnectivityManager` for
the **disconnect** path. The watchdog handles it. *Do* use it for the
**recovery** path — when the system reports we just regained connectivity,
fast-fail any in-flight reconnect backoff and try immediately.

| ID | Item |
|---|---|
| L02-O | `ConnectivityManager.NetworkCallback` registered in service. On `onAvailable()` while in `Reconnecting` state with backoff pending, cancel the sleep and try now |
| L02-P | On `onLost()` log it and bump the network-quality flag, but **do not** force-disconnect — that's the watchdog's job. Premature disconnect on transient `onLost` causes drop-storms during cell handoffs |
| L02-Q | Track `NetworkInfo.getType()` only for telemetry / debug-report rendering, never for protocol decisions |

### 3.6 Doze and idle-bucket compatibility

| ID | Item |
|---|---|
| L02-R | Service must remain `startForeground()` for the entire connected lifetime — not "promoted only when an activity is visible" |
| L02-S | The selector idle wakeup (1 s default, 100 ms fast) must not be replaced with `AlarmManager.setExactAndAllowWhileIdle()` — that fires too rarely. Foreground service exempts us from job-scheduler restrictions |
| L02-T | User-visible explanation in settings: "Linkpoint must run a persistent notification to stay logged into Second Life. Disabling this notification will disconnect you when the screen turns off." |

### 3.7 Reconnect resilience for cellular

| ID | Item |
|---|---|
| L02-U | On reconnect, fetch fresh capabilities — do not reuse stale event-queue URLs. The seed cap survives a fresh login but the per-region caps may not |
| L02-V | The XMLRPC login may race the radio coming back. Wrap `SLAuth.Login` in a small retry (e.g. 2 attempts with 5 s spacing) before declaring login failure |
| L02-W | Fall back from `start = "last"` to `start = "home"` on second reconnect attempt if first failed (sim may be down, not just our connection) |
| L02-X | Surface a UI "Reconnecting…" pill in the WorldView that's distinct from "Connecting…" so users understand transient cellular drops |

---

## 4. Empirical numbers we should target

Working baseline (matches Lumiya defaults; numbers in seconds):

| Symptom | Detection | Recovery |
|---|---|---|
| Receive silence | 10 s | first ping sent |
| Confirmed dead | +15 s (3 pings × 5 s) | `processDisconnect` + 3 s pre-sleep + login retry |
| Total worst-case dead time | ~25–35 s | back online |
| Reasonable cellular handoff (transient) | <10 s silence | watchdog never fires |

The current Linkpoint dead time was *at minimum 1.6 minutes* per the debug
report — i.e. **3–4× the Lumiya target** with no recovery. Closing that gap
is the single biggest user-facing improvement available.

---

## 5. Cross-references

- Segment 01 — Connection Lifecycle (constants, watchdog code, ACK lifecycle)
- Segment 03 — Message Parity (which messages must be `reliable`)
- Segment 11 — Services & Lifecycle (foreground service, notification
  channel, plugin model)
- Segment 12 — Persistence & Eventing (where `SLReconnectingEvent` /
  `SLDisconnectEvent` belong on the bus)
