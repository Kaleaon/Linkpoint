# Cellular Networking Plan

Consolidates the research from five sources into a concrete plan for
Linkpoint's cellular behaviour, and tracks which items are implemented
versus pending.

## Sources

1. **Power Monitors — _UDP vs TCP on a Cellular Network_**
   ([library.powermonitors.com](https://library.powermonitors.com/udp-vs-tcp-on-a-cellular-network)).
   Cellular characteristics undermine TCP's reliability heuristics. When
   the application protocol does its own ACK/retry (DNP3, SL viewer
   protocol), TCP's redundancy hurts more than it helps.
2. **Brown & Singh — _M-UDP: UDP for Mobile Cellular Networks_** (ACM
   SIGCOMM CCR 26(5), 1996,
   [dl.acm.org/doi/abs/10.1145/242896.242901](https://dl.acm.org/doi/abs/10.1145/242896.242901)).
   Edge-proxy ("supervisor host") buffers datagrams across cellular
   fades to bound loss without giving up UDP semantics.
3. **Balakrishnan, Padmanabhan, Seshan & Katz — _A Comparison of
   Mechanisms for Improving TCP Performance over Wireless Links_**
   (IEEE/ACM ToN, Dec 1997 / SIGCOMM '96,
   [Princeton mirror](https://www.cs.princeton.edu/courses/archive/spring17/cos598A/papers/balakrishnan-ton.pdf)).
   TCP-aware link layer + SACK + ELN beats split-connection by 10-30%.
   ELN lets fast-retransmit work without congestion collapse.
4. **IEEE 6733597 — _Mitigating Egregious ACK Delays in Cellular Data
   Networks by Eliminating TCP ACK Clocking_** (TCP-RRE,
   [IEEE Xplore](https://ieeexplore.ieee.org/document/6733597/)). Saturated
   cellular uplink delays return ACKs and stalls unrelated downloads.
   Pacing-based congestion control (BBR / TCP-RRE) yields 2–4× downlink
   throughput.
5. **Hologram / Salesforce / Second Life Wiki / IMC 2016
   bufferbloat measurements**. UDP is right on cellular when the app does
   its own reliability; NAT keepalive is the operational risk; carrier
   UDP idle timeouts cluster around 60–65s.

## Architectural lessons (single picture)

| Lesson | Source | Action in Linkpoint |
|---|---|---|
| App-layer reliability + UDP > TCP on cellular | Power Monitors, M-UDP | SL UDP circuit (existing) |
| Don't go split-connection (worst of three families) | Balakrishnan et al. | Never tunnel SL UDP through TCP/proxy |
| Fast retransmit needs ELN; otherwise loss = false-congestion-collapse | Balakrishnan et al. | Inbound seq-gap → fast-NAK |
| RTT-driven RTO with Karn backoff, not fixed timer | RFC 6298 / Karn | `RttEstimator` |
| Uplink ACK-clocking poisons downlink | TCP-RRE | Cap concurrent HTTPS on metered |
| Short gaps = wireless loss, not failure | Balakrishnan et al. | Handoff grace window |
| Carrier UDP NAT idle ~60–65s | IMC 2016 | 20s `CELLULAR_KEEPALIVE_INTERVAL_MS` (existing) |
| Edge-proxy buffering for fade recovery | M-UDP | **N/A — no base station available** |

## Implementation status

### Already in tree (pre-existing)

- ✅ UDP circuit with selective `LL_RELIABLE_FLAG` reliability
- ✅ `CELLULAR_KEEPALIVE_INTERVAL_MS = 20_000` actually emits a
  `StartPingCheck` packet on cellular every 20s
- ✅ `CELLULAR_UNANSWERED_PINGS_DISCONNECT = 12` (vs 3 for Wi-Fi) tolerates
  cellular CGNAT idle timeouts
- ✅ Cellular-aware inbound stall threshold (90s) before forced rebind
- ✅ IPv4-forced UDP socket (Lumiya cellular fix)
- ✅ Cronet engine with HTTP/2, QUIC enabled, brotli, QUIC hints
  pre-seeded for `asset-cdn.glb.{agni,aditi}.lindenlab.com`
- ✅ ConnectivityManager `NetworkCallback` driving network-type StateFlow
- ✅ Foreground service keepalive wired to `sendStartPingCheck`

### Added by this plan

- ✅ **`RttEstimator`** (`protocol/circuit/RttEstimator.kt`):
  Karn/Jacobson SRTT, RTTVAR, RTO with `[300ms, 6s]` clamp. Karn-clean
  sampling (only retries == 0 ACKs) and Karn backoff (RTO doubles per
  retransmit, resets on next clean ACK). Wired into
  `UDPConnectionFixed.processReceivedAck` and
  `UDPConnectionFixed.checkMessageTimeouts`.
- ✅ **`isMetered` StateFlow** on `ConnectionQualityManager`. Sourced
  from `NetworkCapabilities.NET_CAPABILITY_NOT_METERED` and seeded from
  `NetworkDiagnostics` on init.
- ✅ **`MeteredAssetGate`** (`network/MeteredAssetGate.kt`): semaphore
  with 2 permits on metered, 8 on unmetered. Wired into
  `CronetHttpClient.execute`. The 2-permit cellular cap matches the SL
  viewer's own UDP texture-transfer cap and the IMC 2016 measurement
  that ≤2 streams keeps median uplink ACK delay <200ms.
- ✅ **Handoff grace window** (`ConnectionQualityManager.HANDOFF_GRACE_MS
  = 8_000`): when `onLost` fires, `isInHandoff` flips true for up to 8s.
  `UDPConnectionFixed.checkMessageTimeouts` returns immediately during
  the window so we don't burn retries into a black hole. Wired through
  `setHandoffProvider`.
- ✅ **Inbound seq-gap detector (ELN approximation)**: when the
  simulator's outbound seq number skips one or more, we set
  `fastTimeoutCheckRequested = true` and the next idle iteration runs
  the resend watchdog immediately rather than waiting up to 1s. Karn
  backoff still bounds resend volume.

### Out of scope (intentional)

- ❌ **M-UDP edge proxy**. The supervisor-host buffering model in Brown
  & Singh requires control of the wireless boundary (base station /
  carrier-side proxy). Linkpoint is a client, so this is unimplementable
  here. The closest end-to-end approximation — QUIC's connection
  migration — is already enabled in Cronet but blocked by SL hosts not
  advertising `Alt-Svc`. Nothing to do until LL flips the switch.
- ❌ **Tunnel SL UDP over TCP**. Balakrishnan et al. ranked
  split-connection as the worst of the three TCP-over-wireless
  families; for UDP it would also defeat the SL ACK semantics. Don't.
- ❌ **TCP-RRE / BBR client integration**. Cronet uses Chromium's
  default congestion control (currently BBRv2 for QUIC, CUBIC for TCP),
  which is the right default. We don't need to override it; we just
  need to stop fighting it with concurrent-stream contention on the
  uplink — that's what `MeteredAssetGate` does.

## File map

| Change | File | Status |
|---|---|---|
| `RttEstimator` class | `Linkpoint/src/main/java/com/linkpoint/protocol/circuit/RttEstimator.kt` | Added |
| Adaptive RTO + Karn-clean sampling + handoff guard | `…/protocol/messages/UDPConnectionFixed.kt` | Modified |
| Inbound seq-gap fast-NAK | `…/protocol/messages/UDPConnectionFixed.kt` | Modified |
| `isMetered` + `isInHandoff` StateFlows + grace API | `…/network/core/ConnectionQualityManager.kt` | Modified |
| `MeteredAssetGate` | `…/network/MeteredAssetGate.kt` | Added |
| `CronetHttpClient.execute` acquires gate permit | `…/network/CronetHttpClient.kt` | Modified |
| `setHandoffProvider` + metered collector wiring | `…/LinkpointApp.kt` | Modified |

## Verification

- Unit test for `RttEstimator` (Karn-clean, backoff, clamps).
- The cellular-aware existing watchdogs continue to fire at the same
  cadence as before for users on Wi-Fi (estimator falls back to the
  5-second default until the first ACK sample lands).
