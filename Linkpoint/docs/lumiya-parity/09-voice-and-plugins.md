# Segment 09 — Voice (Vivox) and the Sidecar-APK Plugin Model

**Priority:** Medium. Voice is non-trivial, and Lumiya's architectural
decision to ship voice as a **separate APK** is worth deliberate
consideration before Linkpoint commits to bundling it.

References:
`lumiya_decompiled_source/com/lumiyaviewer/lumiya/voice/`,
`com/lumiyaviewer/lumiya/voiceintf/`.

---

## 1. The plugin model

Lumiya separates the main viewer from voice and cloud-sync features:

| APK | Package | Role |
|---|---|---|
| Main viewer | `com.lumiyaviewer.lumiya` | UDP circuit, render, chat, inventory |
| Voice plugin | `com.lumiyaviewer.lumiya.voice` | Vivox SDK + RTP |
| Cloud plugin | `com.lumiyaviewer.lumiya.cloud` | Google Drive sync of chat history |

Main app binds to plugins via `VoicePluginServiceConnection` /
`CloudSyncServiceConnection`. If a plugin isn't installed, the main app
shows an install offer (registers a `BroadcastReceiver` for
`ACTION_PACKAGE_ADDED` filtered to the plugin package).

### 1.1 Why Lumiya did this

Speculation (consistent with the artifact):

1. **Vivox SDK licensing** — separating it lets the main APK ship without
   the SDK and credentials.
2. **APK size** — voice native libs are ~5–10 MB.
3. **Permission ladder** — a user who never wants voice can decline to
   install the plugin entirely; main app doesn't need to declare audio
   permissions.
4. **Crash isolation** — voice plugin crashes don't bring down the world
   view.

### 1.2 Decision for Linkpoint

Recommendation: **single APK** for now. Reasoning:

- Modern Vivox alternatives (LiveKit, mediasoup, custom WebRTC) have
  permissive licenses.
- Sidecar APKs have to be re-signed per Play Store account; complicates
  releases.
- Crash isolation can be achieved with an in-process worker process
  (`android:process=":voice"` in the manifest).

If we ever do split, follow Lumiya's IPC shape (next section).

---

## 2. Voice IPC protocol (`voice/common/messages/`)

Even within a single APK, modeling voice as a service with these messages
is a clean boundary:

| Message | Direction | Purpose |
|---|---|---|
| `VoiceInitialize` | viewer → voice | hand over Vivox creds |
| `VoiceLogin` | viewer → voice | authenticate with Vivox server |
| `VoiceConnectChannel` | viewer → voice | join an audio channel |
| `VoiceSet3DPosition` | viewer → voice | spatial audio update (per-frame) |
| `VoiceEnableMic` | viewer → voice | mute/unmute |
| `VoiceAcceptCall` | viewer → voice | accept incoming |
| `VoiceRejectCall` | viewer → voice | decline |
| `VoiceTerminateCall` | viewer → voice | hang up |
| `VoiceChannelStatus` | voice → viewer | joining / joined / failed |
| `VoiceChannelClosed` | voice → viewer | session ended |
| `VoiceRinging` | voice → viewer | inbound call notification |

Models: `VoiceLoginInfo`, `VoiceChannelInfo`, `Voice3DPosition`,
`Voice3DVector`, `VoiceAudioDevice`, `VoiceBluetoothState`.

### 2.1 The Vivox connect flow

1. Sim provides `ProvisionVoiceAccountRequest` cap; client posts to it.
2. Cap returns `username`, `password`, `voice_account_server_name` (the
   Vivox SIP server), `voice_sip_uri_hostname`.
3. Client sends `VoiceLogin` to Vivox server with those creds.
4. Per-region: client requests channel by sim's voice channel name
   (`"sip:Wij2…@bhr.vivox.com"`), gets back a Vivox channel handle.
5. Position-update loop: every avatar frame, send the avatar's world
   position + orientation to Vivox.
6. Vivox mixes positional audio and streams.

### 2.2 Spatial audio

Position updates are 3D vectors plus orientation quaternion. Frame rate
matters: too slow and audio "jumps" when moving; too fast and we waste
bandwidth (cellular!). Lumiya updates at the avatar-update cadence (~10 Hz
when in-world).

---

## 3. UI integration

`ui/voice/voice_status.xml`: CardView with a SeekBar (volume), connected
indicator, mute toggle. Surfaced contextually in IM windows and the world
view top bar.

| ID | Item |
|---|---|
| L09-A | Voice status indicator in WorldView top bar (mute / connected / disconnected states) |
| L09-B | Per-IM voice button to start a 1:1 voice call |
| L09-C | Group-IM voice button to join the group's voice channel |
| L09-D | Settings: Voice on/off, prefer Bluetooth, push-to-talk vs voice-activated |

---

## 4. Bluetooth audio

`VoiceBluetoothState` tracks whether a BT headset is connected. Important
for cellular: BT SCO routing has to be requested via `AudioManager`, and
behavior differs per Android version.

| ID | Item |
|---|---|
| L09-E | Detect BT headset; when present and voice active, route via SCO |
| L09-F | Handle "becoming noisy" intent (headset unplugged) — auto-mute |
| L09-G | Audio focus management: lower or pause parcel-stream music when voice is active |

---

## 5. Concrete work items

Voice is a meaningful chunk of work. Order:

1. **L09-H** Decision recorded: single-APK, with `:voice` process if
   isolation is needed later (see §1.2).
2. **L09-I** Replace Vivox with a modern alternative or ship Vivox SDK
   (license decision required).
3. **L09-J** Implement the IPC contract from §2.0 even within a single
   APK — gives clean abstraction.
4. **L09-K** Wire `ProvisionVoiceAccountRequest` cap on circuit-ready.
5. **L09-L** Spatial audio frame loop tied to AvatarManager, not the
   renderer surface (so it survives backgrounding — but voice has its own
   decision to make about whether to keep streaming when surface is gone).

---

## 6. Cross-references

- Segment 02 — when surface is gone, voice may legitimately want to keep
  going (audio-only mode), unlike rendering
- Segment 08 — `SLMissedVoiceCallEvent`, `SLVoiceUpgradeEvent`
- Segment 11 — settings UI for voice
