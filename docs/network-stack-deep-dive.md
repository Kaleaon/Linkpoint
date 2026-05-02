# Linkpoint network stack ↔ libremetaverse: full comparison

Per-component diff of every UDP / capability / event-queue / login code path
between Linkpoint and the canonical reference implementation in
[`cinderblocks/libremetaverse`](https://github.com/cinderblocks/libremetaverse)
(C# .NET 6/7, BSD-3, v2.6.6 / 2026-04-12, 5 154 commits, 102 releases).

This is the working notes from the Apr/May 2026 audit. The shorter doc
in `docs/circuit-cross-check-libremetaverse.md` covers the original
ping/ACK/resend cross-check; this one extends it to the full stack
(send/receive paths, throttle, ping protocol, capability HTTP layer,
EventQueueGet long-poll, login, threading) and folds in the binary-
reader audit triggered by the Apr-28 `EndianAwareBinaryReader` validation
fix in libremetaverse.

Sources fetched 2026-05-01 from
`https://raw.githubusercontent.com/cinderblocks/libremetaverse/master/`.

## File-size context

| | libremetaverse (C#) | Linkpoint (Kotlin) |
|---|---|---|
| Per-region UDP circuit | `Simulator.cs` 1916 lines | `UDPConnectionFixed.kt` 3997 lines |
| Connection lifecycle / login | `NetworkManager.cs` 1545 | `LinkpointApp.kt` (subset) + `SecondLifeProtocol.kt` |
| Raw UDP socket | `UDPBase.cs` 316 | (inline in `UDPConnectionFixed.kt`) |
| Settings / constants | `Settings.cs` 388 | `LinkpointConstants.kt` 202 |
| Caps base | `Capabilities/CapsBase.cs` 346, `CapsClient.cs` 351, `HttpCapsClient.cs` 798 | `protocol/capabilities/CapabilityManager.kt` |
| EventQueue | `Capabilities/EventQueueClient.cs` 514 | `protocol/eventqueue/EventQueueDispatcher.kt` |
| Packet codec | `Packet.cs` 254, `PacketDecoder.cs` 1777 | `protocol/messages/MessageParser.kt` 1963 |
| Per-message handlers | `NetworkManager` callback registration | `protocol/messages/MessageRouter.kt` 192 |

Roughly equivalent surface area. Linkpoint's UDP class is larger
because it inlines what libremetaverse splits across `UDPBase` +
`Simulator` + `NetworkManager`, and includes Lumiya-parity diagnostics
(packet history ring, enhanced packet logger, session-recorder hooks)
that libremetaverse keeps in separate classes.

## Send path

| Concern | libremetaverse | Linkpoint | Verdict |
|---|---|---|---|
| Sequence number assignment | `Interlocked.Increment(ref Sequence)` after FLAG_RESENT check (`Simulator.cs:1325`) | `sequenceNumber.incrementAndGet()` after FLAG_RESENT check | **Match** |
| FLAG_RESENT handling | OR `0x20` into byte 0; do NOT reassign sequence (`Simulator.cs:1294`) | OR `0x20` into byte 0; reuse original sequence | **Match** |
| FLAG_RELIABLE → NeedAck | `lock (NeedAck) NeedAck[seq] = packet` (`Simulator.cs:1335`) | `inflightReliablePackets[seq] = inflight` | **Match** (concurrent map vs locked dictionary, equivalent) |
| ACK piggyback | Drain `PendingAcks` until packet would exceed MTU; trailing count byte; set FLAG_ACK (`Simulator.cs:1305`) | `maybeAppendAcksToOutgoing` with `MAX_ACKS_PER_PACKET` cap and a "skip-piggyback-on-fat-packets" heuristic | **Linkpoint stricter** — fine; the cap is an over-conservatism, not a bug |
| MTU bound | `Packet.MTU` constant (typically 1500) | `MAX_PAYLOAD_SIZE = 1018` (close to LL's 1024) | **Slight tightness on Linkpoint** — matches LL's documented cap |
| Send call | `AsyncBeginSend(buffer)` — `BeginSendTo` async API | `channel.write(ByteBuffer.wrap(wireBytes))` from a dedicated I/O thread | **Different model, equivalent semantics** (libremetaverse leans on .NET's async sockets; Linkpoint owns its blocking-IO thread) |
| Stats tracking | `Stats.IncrementSentPackets()` per send | `packetsSent.incrementAndGet()` plus per-message-type counters | **Linkpoint has more granular counters** |

## Receive path

| Concern | libremetaverse | Linkpoint | Verdict |
|---|---|---|---|
| Source address validation | `if (remoteEndPoint.Address != buffer.RemoteEndPoint.Address) reject` (`Simulator.cs:1446`) | None — accepts any inbound on the bound socket | **Linkpoint gap** — see "Open issues" below |
| Zerocode decoding | `Packet.BuildPacket(...)` allocates an 8 KB scratch when `MSG_ZEROCODED` is set | Inline zero-decode in receive loop, allocates `ByteArray` per packet | **Equivalent** (Linkpoint allocates more per packet but on a single thread) |
| Trailing ACK extraction | Reads `AckList` from header BEFORE handler dispatch | Extracts `appendedAcks` from raw bytes BEFORE zero-decode (correct — the appendix is on the wire-bytes, not the post-decode bytes) | **Linkpoint correct** (this was a previous Linkpoint bug fixed via the wire-bytes path; libremetaverse doesn't have the bug because it uses pre-built `Packet` objects) |
| `PacketAck` packet handling | Parse acked-IDs, remove from `NeedAck`, signal `GotUseCircuitCodeAck` (`Simulator.cs:1505`) | Parse acked-IDs, remove from `inflightReliablePackets`, set `completeAgentMovementSent` | **Match** |
| Inbound dedup | `PacketArchive.TryEnqueue(seq)` — 1024-entry ring buffer + hash set (`Simulator.cs:1539`) | `InboundSequenceArchive` — direct port, same shape, same 1024 capacity | **Match** (added in the cross-check PR; was missing prior) |
| Resent-packet logging | DEBUG for `Header.Resent` duplicates, WARN for non-resent duplicates (`Simulator.cs:1542`) | Same WARN/DEBUG split | **Match** |
| Inbox dispatch | `Network.EnqueueIncoming(packet)` — into a worker thread queue | `dispatchMessageDirect(messageId, data)` synchronously on I/O thread | **Different model**: libremetaverse decouples receive from handler dispatch via a queue; Linkpoint dispatches inline (Lumiya parity). Linkpoint's model is lower latency for the handler but blocks the I/O thread if a handler is slow. **Recommendation:** any handler that touches disk / network / WebRTC SDK should `launch` onto an application coroutine, not block. (Most already do.) |

## Resend logic

| Concern | libremetaverse | Linkpoint |
|---|---|---|
| Trigger | Polled in `AckTimer_Elapsed`, also explicit `ResendUnacked()` called from `SendAcks` flow | Polled in receive-loop idle pass, every ~1 s (`checkMessageTimeouts`) |
| Per-packet age check | `Environment.TickCount - outgoing.TickCount > Settings.RESEND_TIMEOUT` (default 4000) | Same predicate; constant `MESSAGE_TIMEOUT_MS = 5000` |
| Retry counter | `outgoing.ResendCount < Settings.MAX_RESEND_COUNT` (default 3) | `inflight.retries < MESSAGE_MAX_RETRIES` (3) |
| Resend bit | `outgoing.Buffer.Data[0] |= MSG_RESENT` | `data[0] = (data[0].toInt() or 0x20).toByte()` |
| Re-arm | `TickCount = 0` so it'll be set on next `SendPacketFinal` | `lastSentTime = now` immediately |
| Drop | After max retries, remove from `NeedAck`, no further action | Same, plus optional listener callback |
| Stats | `Stats.IncrementResentPackets()` | `packetsResentCount.addAndGet(resends)` |

**Match.** Linkpoint's 5000ms vs libremetaverse's 4000ms is a tunable
tradeoff (Linkpoint slightly more tolerant of high-latency cellular).

## Ping protocol

| Concern | libremetaverse | Linkpoint | Verdict |
|---|---|---|---|
| Cadence | `PingTimer_Elapsed` every `Settings.PING_INTERVAL = 2200` ms | `checkPingHealth` every ~1 s, fires when `timeSinceReceive > NEED_PING_TIMEOUT_MS = 10000` AND `timeSincePing > PING_INTERVAL_MS = 5000` | **Different**: libremetaverse pings on a strict 2.2s timer; Linkpoint pings only when inbound is quiet. On cellular Linkpoint also has a 20s NAT-keepalive forced cadence. |
| Wire format | `StartPingCheckPacket { PingID = id, OldestUnacked = lowest_key(NeedAck) }` | `payload = [pingId(1), oldestUnacked(4 BE)]` — was always 0, now computed from `inflightReliablePackets.keys.minOrNull()` | **Now match** (this commit) |
| Reply | Sim returns `CompletePingCheckPacket { PingID }` | Sim returns `CompletePingCheck` (msg ID 0x02), bumps `unansweredPings.set(0)` | **Match** |
| Disconnect threshold | None — pings forever | `UNANSWERED_PINGS_DISCONNECT = 3` (Wi-Fi) / `12` (cellular) | **Linkpoint stricter, intentional** — see `circuit-cross-check-libremetaverse.md` |

**One real bug fixed in this PR:** Linkpoint's `OldestUnacked` was
hardcoded `0`. Now computed from `inflightReliablePackets.keys.minOrNull()`,
matching `Simulator.SendPing` line 1347.

## Throttle (AgentThrottle)

| Band | libremetaverse default split (% of total bps) | Linkpoint |
|---|---|---|
| Resend | 10% | (need to verify against `applyAdaptiveAgentThrottle`) |
| Land | 17.3% (`0.52 / 3`) | |
| Wind | 5% | |
| Cloud | 5% | |
| Task | 23.5% (`0.704 / 3`) | |
| Texture | 23.5% (`0.704 / 3`) | |
| Asset | 16.1% (`0.484 / 3`) | |
| Total default | 1 536 000 bps (1.5 Mbps) | (band split lives in `applyAdaptiveAgentThrottle`) |

libremetaverse `AgentThrottle.cs:122` derives all 7 bands from a single
`Total` bps via fixed multipliers. Linkpoint already has cellular-
adaptive logic on top of this. **Not changing** the split — Linkpoint's
network-adaptive multipliers go beyond what libremetaverse does.

## Capability HTTP layer

| Concern | libremetaverse | Linkpoint |
|---|---|---|
| Underlying client | `HttpClient` (System.Net.Http) | OkHttp 4.12 + Cronet 119 + Conscrypt 2.5.2 |
| Per-request timeout | `Settings.CAPS_TIMEOUT` (default 60s) | `HttpRequestOptions` per-cap with retry policy |
| HTTP/2 | Negotiated by .NET HTTP stack (BoringSSL on .NET 6+) | Explicit `.protocols(HTTP_2, HTTP_1_1)` + Conscrypt SSLSocketFactory injection (PR `claude/linkpoint-protocol-stack-fixes`) |
| Retry-After | Not in `CapsClient.cs` (relies on caller) | `parseRetryAfterHeader` + `RETRYABLE_HTTP_CODES = {503, 429, 500, 502, 504}` |
| LLSD content-type negotiation | Sends `Accept: application/llsd+xml,application/llsd+binary`; `HttpCapsClient.cs` autodetects | Same Accept header; `LLSDContentTypeDetector` picks parser by Content-Type response |
| Connection pooling | .NET default (1 connection / host) | `ConnectionPool(maxIdleConnections=4, keepAliveDuration=30s)` — looser per cap |
| HTTP/3 | Not enabled (.NET 7+ supports but libremetaverse doesn't opt in) | Cronet has it enabled but no LL endpoint advertises Alt-Svc |

**Linkpoint is more featureful here.** No changes needed.

## EventQueueGet long-poll

| Concern | libremetaverse | Linkpoint |
|---|---|---|
| File | `Capabilities/EventQueueClient.cs` 514 lines | `protocol/eventqueue/EventQueueDispatcher.kt` |
| Long-poll request shape | POST LLSD `{ack: <last_ack_id>, done: false}`, response `{events: [...], id: <new_ack_id>}` | Same |
| Ack semantics | Carry the `id` from the previous response back as `ack` in the next request — server uses this to advance its event cursor | Same |
| Error recovery | 502 / 504 / network error → retry with same `ack`; explicit "DONE" message ends the loop | Same |
| Threading | Dedicated `Task.Run` loop per simulator | Dedicated coroutine on `applicationScope` |

**Match.** Both implementations follow LL's long-poll spec correctly.

## Login

| Concern | libremetaverse | Linkpoint |
|---|---|---|
| Transport | XML-RPC over HTTPS POST to `cgi-bin/login.cgi` | Same |
| Body | `login_to_simulator` method with positional struct of credentials | Same |
| MFA | Honoured via `mfa_hash` parameter on retry | Same |
| Response shape | `LoginResponseData.cs` decodes ~80 fields | `LoginResponseParser.kt` decodes the same fields |
| Connection reuse | Disabled — login uses HTTP/1.1 with `Connection: close` | Same (`GrpcChannelFactory.kt:328` — explicit HTTP/1.1 for LOGIN policy class) |

**Match.**

## Threading model

| Concern | libremetaverse | Linkpoint |
|---|---|---|
| Receive | One thread per simulator (`UDPBase` async receive callbacks) | One dedicated `SLCircuitIO` thread per circuit (Lumiya parity) |
| Send | Caller thread enqueues; ACK timer drains | Caller enqueues to `outgoingQueue`; SLCircuitIO drains via selector wakeup |
| Inbound dispatch | Decoupled — `EnqueueIncoming` puts on a worker queue | Synchronous on I/O thread (Lumiya parity) |
| Cap HTTP | .NET thread pool | OkHttp dispatcher thread pool |
| Voice | Separate `Sdl3Audio` thread for capture | Separate `VoiceThread` for VoiceManager |
| Coroutines | Async/await | Kotlin coroutines on `applicationScope` (`Dispatchers.Default`) |

**Different but equivalent.**

## Binary-reader audit (the "e reader" review)

Triggered by libremetaverse Apr-28 commit `533cc85` "All six numeric
read methods in `EndianAwareBinaryReader` now validate the byte count
before calling `BitConverter`. Any premature end-of-stream will throw
a clear `EndOfStreamException`."

That fix is for `EndianAwareBinaryReader.cs` — used by mesh / animation /
TAR archive / inventory cache binary parsers. The bug pattern was:
`BitConverter.ToInt32(m_a32, 0)` was called on a possibly-truncated
`m_a32` array, producing garbage instead of throwing.

### Linkpoint equivalent

Linkpoint has no `EndianAwareBinaryReader`. Three reader paths instead:

1. **`java.nio.ByteBuffer`** — used in `MessageParser.kt`, `LLMeshLoader.kt`,
   `MeshManager.kt`, `AnimationManager.kt`, etc. **Safe** —
   `ByteBuffer.getInt()` / `getShort()` / `getLong()` / `getFloat()` /
   `getDouble()` all throw `BufferUnderflowException` on truncation. No
   silent partial-read possible.

2. **`LLSDParser`** — uses `readByte`, `readExact(n)`, `readLength`
   (`LLSDParser.kt:303-345`). All bounded:
   - `readByte` returns `-1` on EOF (caller checks).
   - `readExact(n)` throws `TruncatedBinaryPayloadException(n)` if
     fewer than `n` bytes are available.
   - `readLength` is bounded by `maxStringBytes` / `maxBinaryBytes`
     before any allocation.

3. **`LLSDNotationParser`** (this PR) — bounded by `MAX_STRING_BYTES`
   and `MAX_BINARY_BYTES`; throws `IllegalStateException` on truncation
   or limit overflow, caller catches and returns `LLSDUndefined`.

### `MessageParser.kt` length-prefix scan

10 sites that allocate `ByteArray(someLength)` from a wire-supplied
length:

| Site | Length source | Max | Verdict |
|---|---|---|---|
| `objectData = ByteArray(dataLen)` line 185 | U8 (`buffer.get()`) | 255 | Safe |
| `textureEntry = ByteArray(textureEntryLen)` line 234 | U16 (`buffer.short`) | 65 535 | Safe |
| `textureAnim = ByteArray(textureAnimLen)` line 239 | U8 | 255 | Safe |
| `nameValue = ByteArray(nameValueLen)` line 244 | U16 | 65 535 | Safe |
| `textBytes = ByteArray(textLen)` line 256 | U8 | 255 | Safe |
| `urlBytes = ByteArray(mediaUrlLen)` line 269 | U8 | 255 | Safe |
| `psBlock = ByteArray(psBlockLen)` line 276 | U8 | 255 | Safe |
| `extraParams = ByteArray(extraParamsLen)` line 281 | U8 | 255 | Safe |
| `compressedData = ByteArray(dataLen)` line 360 | U16 | 65 535 | Safe |
| `simNameBytes = ByteArray(simNameLen)` line 712 | U16 | 65 535 | Safe |

Every length is hard-bounded by the prefix size. No DoS allocation
possible from a single message — even a worst-case fully-65kB-blob
packet is fine.

### XML parser numeric safety

`LLSDParser.parseXML` uses Kotlin's `String.toIntOrNull()` /
`toDoubleOrNull()` (return null on parse failure rather than throw).
**Safe.**

### `ByteBufferExtensions.getUUID/putUUID`

Fixed 16 bytes via two `getLong()` / `putLong()`. Underflow throws.
**Safe.**

### Conclusion

**No reader bugs found.** The Apr-28 libremetaverse fix doesn't apply
to Linkpoint because the Java `ByteBuffer` API throws on truncation
by design (the `BitConverter` API libremetaverse used did not).

## Open issues worth tracking

These are differences that didn't surface a concrete bug in the
captures we have, but are worth filing for the next time someone
touches this code.

### 1. Linkpoint accepts inbound packets from any source on the bound socket

`UDPConnectionFixed.receiveLoopBlocking` doesn't validate the inbound
`SocketAddress` against the connected simulator's IP. libremetaverse
`Simulator.PacketReceived` (line 1446) drops anything from an
unrecognized source. Low-impact in practice (we use a connected
DatagramChannel which Java filters by remote endpoint anyway), but
worth confirming the connect-then-bind invariant holds across the
reconnect path. **Action**: assert in `receiveLoopBlocking` that the
read came from the expected remote, log + drop otherwise.

### 2. Per-message frequency-band stats

libremetaverse `Stats` exposes per-frequency-band counters
(high / medium / low / fixed). Linkpoint tracks total + per-message
type but not per-band. Useful for spotting throttle-band saturation
(e.g. "we're hitting the texture-band cap"). **Action**: add
band-aware counters to `EnhancedPacketLogger`.

### 3. `FLAG_ZEROCODED` on outbound is opportunistic, not message-template-driven

libremetaverse picks zerocoding per outgoing message based on the
`Trusted` / `Encoded` flags in `message_template.msg`. Linkpoint zero-
codes opportunistically based on compressibility. Wrong for
`AgentSetAppearance` (must NOT be zerocoded — LL bug we'd inherit).
**Action**: when we touch the next appearance-baking refactor, audit
which messages need zerocoding and route them through a single
"is-zerocoded?" predicate driven by the template.

### 4. AISv3 / inventory action coverage

The Apr 2026 push in libremetaverse landed several `InventoryAISClient`
methods we don't have (`SlamFolder`, `PutCategoryChildren`,
`CopyCategoryLinks`, `DeleteCategoryChildren`, etc.). The May
follow-ups branch ports the most-used ones (`patchCategory`,
`slamFolder`, `purgeDescendents`, `copyCategory`, `moveCategory`,
`postInventory`); the rest are listed in the followups commit message.

### 5. EventQueue resilience to capability rotation

When the `EventQueueGet` cap URL changes (region cross or
re-establish-agent flow), libremetaverse's `EventQueueClient` exits
the long-poll loop and the higher-level code spins up a new client
against the new URL. Linkpoint should do the same — verify
`EventQueueDispatcher` handles this cleanly across the
`EstablishAgentCommunication` event.

## Concrete fixes landed in this audit

1. `WebRtcVoiceSession.kt` — added handlers for moderator-pushed
   mute/gain maps and per-peer position updates (`PeerEvent.MuteMap` /
   `GainMap` / `Position`). Mirrors libremetaverse `WebRtcTest`
   reference handlers.
2. `AppearanceManager.kt` — `encodeVisualParamWireBytes()` now walks
   the wire-order group-0+3 list from `VisualParamLoader` instead of
   shipping 218 × 0x7F default bytes. Mirrors libremetaverse
   Apr-30 `Simulator.AppearanceManager.cs:2326` fix.
3. `VisualParamLoader.kt` — parses `group` attribute, builds a
   `wireOrderParams()` accessor matching libremetaverse's
   `VisualParams.Group0ParamIds`. Re-stamps kind-filtered `byteIndex`
   values to wire offsets so render-side `bytes[p.byteIndex]` works.
4. `LLSDParser.parseBinary` — strips the optional `<?llsd/binary?>\n`
   magic header (libremetaverse + python-llsd both emit it; we were
   silently dropping every prefixed binary payload).
5. `LLSDNotationParser` — new parser for the third LLSD form, with
   spec-mapped tokens and bounded parser limits.
6. `AisClient.kt` — extended to cover the libremetaverse AISv3 surface
   (patchCategory / slamFolder / purgeDescendents / copyCategory /
   moveCategory / postInventory) plus headers + custom HTTP verbs.
7. `UDPConnectionFixed.sendStartPingCheck` — `OldestUnacked` now
   computed from the inflight queue instead of hardcoded 0. Costless
   server-side state cleanup. Matches libremetaverse `Simulator.SendPing`.

All on branch `claude/linkpoint-libremetaverse-followups`.
