# Segment 06 — Textures, Assets, and JPEG2000

**Priority:** High. Texture stability is table-stakes for in-world
navigation. Worklist item #3 in the original doc.

References: `lumiya_decompiled_source/com/lumiyaviewer/lumiya/openjpeg/`,
`slproto/modules/texfetcher/`, `slproto/modules/texuploader/`,
`slproto/modules/transfer/`.

---

## 1. Asset transport paths

Two parallel paths, just like inventory. Lumiya implements both:

| Path | When used | Lumiya owner |
|---|---|---|
| **HTTP cap** (`GetTexture`, `GetMesh`, `GetMesh2`) | preferred whenever cap is present | `transferManager`, `textureFetcher` |
| **UDP** (`RequestImage` → `ImageData` + `ImagePacket`) | fallback for older sims / textures not on CDN | `slproto/messages/RequestImage.java` etc. |

The debug report shows Linkpoint has both `GetTexture` and `GetMesh`
capabilities active, and that 24 textures decoded successfully via the cap
path. Good. **HTTP/2 is not being used** (`HTTP/1.1: 25 / HTTP/2: 0`) —
Lumiya is OkHttp 3.x and gets HTTP/2 automatically when the server
advertises it. Investigate why Linkpoint's client is negotiating down.

---

## 2. JPEG2000 (worklist item #3)

`libopenjpeg.so` is the load-bearing native lib. Linkpoint already has the
JNI bridge (commit `17b0a30c Fix JPEG2000 nativeHealthCheck always returning
false` is in this branch's history). The remaining work:

| ID | Item |
|---|---|
| L06-A | Decode pipeline must handle the discard-level system: decode at the lowest level the network has delivered so far, then refine when more bytes arrive |
| L06-B | Decode failures must fall through to a placeholder, not crash. Lumiya's pattern: `JP2ForAndroid` Java fallback — Linkpoint has `JP2ForAndroid Fallback: ✗` per debug report; verify either path works |
| L06-C | Decode stats: `Attempts: 24, Successes: 24, Error States: 0` is the healthy state from the debug report. Keep the counters and surface them in diagnostics |
| L06-D | Decoded texture must register with `TextureMemoryTracker` (segment 05, L05-E) |
| L06-E | Pending-downloads queue must be bounded; if quality is POOR (segment 02), pause the queue |

---

## 3. Texture memory tracking gap

Debug report:

```
Live Textures: 0           ← BUG
Native Heap: 0 B           ← BUG
GPU: 0 B                   ← BUG (24 actually live)
```

vs.

```
JPEG2000 Decoding:
  Successes: 24
```

The decoder works but the tracker doesn't see the uploads. Per segment 05
the fix lives at the renderer — every Filament texture upload must
`tracker.add(uuid, bytes)` and every release must `tracker.remove(uuid)`.

| ID | Item |
|---|---|
| L06-F | Wire tracker callbacks at texture-upload site in renderer |
| L06-G | Wire release callback at every texture-disposal site |
| L06-H | Add a regression test: upload N synthetic textures, verify tracker sums correctly; release all, verify tracker zeroes |

---

## 4. Mesh

| ID | Item |
|---|---|
| L06-I | Mesh fetch via `GetMesh2` cap (LOD0/1/2/3 sub-streams) |
| L06-J | Mesh decoder: zlib-compressed LLSD blobs per LOD |
| L06-K | Skin info / bone bindings from mesh asset payload |
| L06-L | LOD switching tied to camera distance (segment 05's spatial index drives this) |
| L06-M | Mesh memory tracker analogous to `TextureMemoryTracker` |

---

## 5. Other asset types Lumiya handles

| Type | Loader | Notes |
|---|---|---|
| Sounds | `slproto/modules/sound/` | OGG/Vorbis. `AttachedSound`, `SoundTrigger`, `PreloadSound` are the UDP triggers; the bytes come via cap or Xfer |
| Animations | bundled assets + asset fetch | the 46 animations in Lumiya's APK are UUID-named files |
| Notecards | inventory fetch + asset cap | `NotecardEditActivity` |
| Scripts (read-only viewing) | inventory fetch + asset cap | LSL text |
| Landmarks | inventory; teleport target | `TeleportLandmarkRequest` |
| Gestures | inventory + `ActivateGestures`/`DeactivateGestures` | UDP messages |
| Wearables | inventory; layered into bake | segment 05 / avatar baking |
| Sculpt textures | texture pipeline; consumed by sculpt prim renderer | normal texture flow + sculpt-specific tessellation |

---

## 6. Asset cache

Debug report shows:

```
Total Cache Size: 0.0 KB / 4.0 GB
Memory Cache: 5.21 MB / 512.00 MB
  Hit Count: 0     Miss Count: 42     Hit Rate: 0.0%
Disk Cache: 0 B    Asset Count: 0
```

…on a session that's been alive 1.7 minutes. Two issues:

1. **Disk cache is empty** — every relaunch refetches every texture. Lumiya
   has `CachedAsset` / `CachedResponse` DAOs in `dao/` for exactly this.
2. **Memory cache hit rate is 0%** — 42 distinct misses, not a single
   re-request hit. Either nothing is sticking in the cache, or every UUID
   really was unique (plausible for the first 1.7 min of a session, but
   should not stay that way).

| ID | Item |
|---|---|
| L06-N | Audit asset cache write paths — is `put` ever called? |
| L06-O | Disk cache tier with 4 GB budget (matches the report's stated capacity); LRU eviction |
| L06-P | Disk cache survives app restart |
| L06-Q | Cache hit/miss counters are accurate (write-through vs write-around) |

---

## 7. HTTP/2

| ID | Item |
|---|---|
| L06-R | Confirm Linkpoint's HTTP client (OkHttp?) is configured to negotiate HTTP/2 (ALPN). The server side (`simhost-…agni.secondlife.com`) should advertise it |
| L06-S | If HTTP/2 negotiation is failing, log the protocol version and downgrade reason; surface in debug report |
| L06-T | Once HTTP/2 is up, parallelism shifts from "many sockets" to "many streams on one socket" — review the request scheduler |

---

## 8. Cross-references

- Segment 02 — pause non-critical fetches during POOR network
- Segment 05 — texture memory tracker is rendered at upload site
- Segment 07 — inventory dual-path is the same pattern applied to inventory
