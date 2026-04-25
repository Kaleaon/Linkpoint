# Lumiya Comparison Worklist

This worklist captures parity gaps between Linkpoint and the decompiled
Lumiya 3.4.2 reference (`lumiya_decompiled_source/`). Heavy detail lives in
segmented docs under [`docs/lumiya-parity/`](docs/lumiya-parity/) so the
top-level index stays small and individual segments stay focused.

Updated 2026-04-25 after the live-session debug capture from Athanasia (LTE,
1.6 m of receive silence with no recovery) and a fresh sweep of Lumiya's
`SLCircuit` / `SLAgentCircuit` / `SLGridConnection` and the Lumiya-Redux
modernization repo (`github.com/Kaleaon/Lumiya-Redux`).

---

## Segments

| # | Segment | Priority | Status |
|---|---|---|---|
| [01](docs/lumiya-parity/01-connection-lifecycle.md) | UDP circuit & connection lifecycle | **Critical** | spec'd |
| [02](docs/lumiya-parity/02-cellular-and-background.md) | Cellular networks & background survival | **Critical** | spec'd |
| [03](docs/lumiya-parity/03-message-parity.md) | UDP message parity | High | spec'd |
| [04](docs/lumiya-parity/04-protocol-correctness.md) | Protocol correctness (identity blocks, LLSD) | **Critical** | spec'd |
| [05](docs/lumiya-parity/05-rendering.md) | Rendering & scene management | Medium-High | spec'd |
| [06](docs/lumiya-parity/06-textures-and-assets.md) | Textures, assets, JPEG2000 | High | spec'd |
| [07](docs/lumiya-parity/07-inventory.md) | Inventory dual-path | High | spec'd |
| [08](docs/lumiya-parity/08-chat-and-events.md) | Chat & event hierarchy | Medium-High | spec'd |
| [09](docs/lumiya-parity/09-voice-and-plugins.md) | Voice (Vivox) & sidecar plugin model | Medium | spec'd |
| [10](docs/lumiya-parity/10-rlv.md) | RLV (Restrained Love) | Medium | spec'd |
| [11](docs/lumiya-parity/11-ui-and-settings.md) | UI surface & settings | Medium | spec'd |
| [12](docs/lumiya-parity/12-persistence-and-eventing.md) | Persistence (DAO/ORM) & eventing | Medium | spec'd |

---

## Top-priority items (current sprint)

These come from segments 01, 02, and 04 — they are the live-failure
recovery work and the silently-broken-mutation work.

1. **L01-A / L01-B / L01-C / L01-E / L01-F** — receive-idle watchdog,
   `StartPingCheck` reliable, reset on inbound, three-strike disconnect
   into `Reconnect()` ladder, `SLReconnectingEvent` analogue published.
2. **L02-A / L02-B / L02-C** — circuit thread fully independent of
   Activity / surface; selector loop runs inside foreground service;
   `AgentUpdate` scheduler does not stop on `onPause()`/`onStop()`.
3. **L02-E / L02-F** — `PARTIAL_WAKE_LOCK` while connected;
   `FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE` for Android 14+.
4. **L02-O** — `ConnectivityManager.NetworkCallback`: speed up *recovery*
   only (not disconnect detection).
5. **L04-A / L04-B / L04-C / L04-D** — sweep for placeholder
   `AgentID`/`SessionID`; pull from `circuitInfo`, never a global; lint
   test enforces it.
6. **L01-D** — reliability matrix sweep across all sent message sites
   (per segment 03 §3).

---

## Suggested execution order

1. **Connectivity recovery** (segments 01 + 02): the user-facing failure
   is "the viewer goes silent and never comes back". This is the single
   largest improvement available.
2. **Protocol correctness** (segment 04): silent-failure mutation bugs are
   second-priority because they're harder to notice but corrupt user
   trust.
3. **Message parity** (segment 03): close the gap-list and the reliability
   matrix.
4. **Inventory dual-path** (segment 07): unblocks every wearable /
   landmark / notecard flow. The "5479 folders / 0 items" symptom from
   the live debug report.
5. **Texture pipeline + memory tracker** (segments 05 + 06): visual
   parity, stops accidental OOMs.
6. **Chat event hierarchy** (segment 08): unblocks correct rendering of
   item offers, friendship offers, dialogs.
7. **UI / settings panels** (segment 11): expose the cellular tuning
   knobs from segment 02.
8. **RLV, Voice, multi-account** (segments 10, 09, 11): feature-completion
   tier.
9. **Persistence migration to Room** (segment 12): ongoing, not blocking.

---

## Conformance harness (Lumiya-Redux convention)

Lumiya-Redux pins the upstream message template at
`recovered/reference/message_template.msg` and gates `slproto/**` changes
with `tools/protocol/run_conformance.sh`. Linkpoint should adopt the same
harness:

| Tool | Purpose |
|---|---|
| `tools/protocol/verify_message_template_conformance.py` | message field-order conformance |
| `tools/protocol/run_conformance.sh` | runs the verifier in CI on `slproto/**`, `orm/**` PRs |
| `tools/protocol/message_template_mismatches.txt` | record of intentional deviations |

Mirror Lumiya-Redux LLSD test fixtures under
`src/test/resources/protocol/llsd/` and add round-trip tests for each
LLSD format.

---

## Repro commands for the original parity sweep

```bash
python - <<'PY'
import re, pathlib
root = pathlib.Path('/home/user/Linkpoint')
msgs = list((root/'lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/messages').glob('*.java'))
print('lumiya_messages', len(msgs))
mid = (root/'Linkpoint/src/main/java/com/linkpoint/protocol/messages/MessageIds.kt').read_text(errors='ignore')
print('linkpoint_message_ids', len(re.findall(r'const val\s+([A-Z0-9_]+)\s*=\s*', mid)))
app = (root/'Linkpoint/src/main/java/com/linkpoint/LinkpointApp.kt').read_text(errors='ignore')
ids = re.findall(r'register(?:Parsed)?Handler\(\s*com\.linkpoint\.protocol\.messages\.MessageIds\.([A-Z0-9_]+)', app)
print('registered_message_ids', len(set(ids)))
PY
```
