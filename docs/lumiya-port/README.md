# Lumiya → Kotlin Port Plan

**Status:** Planning. No code changes yet — this branch starts as just this document.
**Predecessor:** PR #455 (`claude/debug-linkpoint-7MSH7`) closed out the latest debug-report fixes (UDP receive-loop stability, inventory warm-fetch, viewer themes, debug-report formatting, basis_universal cleanup). The investigation that fed *this* document was done in that same session.
**Reference tree (read-only):** `lumiya_decompiled_source/` already in this repo, plus `https://github.com/Kaleaon/Lumiya-Redux` for fuller decompile + smali.

## Why this work exists

Lumiya 3.4.2 still renders Second Life faster and more completely on a 2014-era phone than Linkpoint does on a Pixel 10 Pro XL. The gap is not in the protocol layer (Linkpoint mirrors `slproto/` cleanly) and not in the service lifecycle (`GridConnectionService` is already in place). The gap is in the **per-frame hot path**: texture decoding, GPU upload, mesh skinning, terrain bake, and frustum culling. Lumiya pushed almost all of those across one fat JNI boundary into `libopenjpeg.so`; Linkpoint splits them into many narrow Kotlin files.

Alina Lyvette's original source tree was lost. The decompiled Java + smali in `lumiya_decompiled_source/` and `Kaleaon/Lumiya-Redux` is what we have left, and it's enough to mirror the architecture.

## What is *already* mirrored — do NOT redo

| Lumiya | Linkpoint counterpart | Status |
|---|---|---|
| `GridConnectionService.java` (foreground service owning the UDP circuit) | `services/GridConnectionService.kt` + `service/LinkpointConnectionService.kt` | ✅ in place |
| `slproto/SLCircuit`, `SLConnection`, `SLAgentCircuit` | `protocol/messages/UDPConnectionFixed.kt` (+ siblings) | ✅ functional after PR #455 |
| `message_template.msg` parser → 477 message classes | `protocol/messages/MessageIds.kt` + parsers under `protocol/messages/` | ✅ comprehensive |
| HTTP capabilities + event queue (`slproto/caps/`) | `network/CapabilityManager.kt` + event queue handlers | ✅ functional |
| greenDAO ORM for asset metadata | Room (or equivalent) under `assets/` | ✅ functional |
| `inventory-skeleton` parsing on login | `protocol/auth/LoginResponseParser.kt` + `addFolderFromLogin` | ✅ + warm-fetch added in PR #455 |

## What is missing — the actual port work

Ordered by impact-per-effort. Each item is a separate commit (or PR) on this branch.

### 1. Unified `LinkpointTexture` class à la Lumiya's `OpenJPEG`

**Lumiya source:** `lumiya_decompiled_source/com/lumiyaviewer/lumiya/openjpeg/OpenJPEG.java`

The existing pattern in Linkpoint is decode-only:
  - `JPEG2000Decoder.kt` returns an Android `Bitmap`
  - Some other manager uploads it to Filament
  - Memory accounting is split across `AssetCache`, `TextureMemoryTracker` etc.

Lumiya's `OpenJPEG` class **owns the lifecycle from disk to GPU** in one object:
  - Multiple constructors for J2K / raw / TGA inputs
  - `ByteBuffer rawBuffer` is the canonical pixel store (mmap-able)
  - `CompressETC1()` converts decoded RGBA → ETC1 in-place — see item 2
  - `SetAsImmutableTexture()` uses `glTexStorage2D` + `glTexSubImage2D` for the ES 3.0 fast path
  - `TextureMemoryTracker.allocOpenJpegMemory(...)` is called from inside the class on every allocation/free, including a flag for whether the buffer is mmapped
  - `finalize()` is the single point that releases the native buffer

**Action:** Introduce `assets/LinkpointTexture.kt` (or `render/LinkpointTexture.kt`) that:
  - Wraps the J2K decode (calls into `JPEG2000Decoder` for now, eventually folds it in)
  - Holds the `ByteBuffer` at native heap (or mmapped file, see item 4)
  - Exposes `compressETC2()` (Filament-friendly successor to ETC1 — see item 2)
  - Exposes `uploadToFilament(engine: Engine): Texture` using compressed-texture upload path
  - Owns its memory accounting (replace `TextureMemoryTracker` with a Kotlin equivalent under `assets/`)

**Risk:** Filament's texture API differs from raw OpenGL — investigate whether Filament accepts pre-compressed ETC2 buffers via `Texture.PixelBufferDescriptor` (it does; format `ETC2_RGBA8` is supported). Verify before committing.

### 2. ETC2 GPU compression for prim textures

**Lumiya method:** `OpenJPEG.CompressETC1()` (lines 202–218 of the decompile)
  - Uses Android's built-in `android.opengl.ETC1.encodeImage(...)` on the CPU
  - Only runs when `num_components == 3 && num_extra_components == 0` and bytes-per-pixel is 2 or 3
  - Replaces the raw RGBA buffer with the ETC1-compressed buffer in place
  - 4 bpp (RGBA8888) → 0.5 bpp (ETC1) = **~8× VRAM reduction**

**Why this is *the* perf win:** Lumiya could fit ~8× more textures resident in GPU memory than Linkpoint can today. On a busy SL region (200+ unique textures) Linkpoint thrashes between disk decode and GPU upload, which is exactly what "feels slow" looks like.

**Action:** Pick a CPU-side encoder. Options, in order of preference:
  - **`android.opengl.ETC1`** — already in AOSP, no new dependency, but only ETC1 (no alpha). Use for the opaque-prim fast path only.
  - **ETC2/EAC** via `etcpak` (https://github.com/wolfpld/etcpak) compiled into the native lib — fast, supports RGBA, MIT license. Best long-term.
  - **`org.etc2comp` / `androidx.graphics`** — investigate.

Decision: start with `android.opengl.ETC1` on opaque RGB textures (matching Lumiya's gating) to validate the pipeline; later add ETC2/EAC for RGBA via native `etcpak`.

### 3. One fat native lib for all hot-path math

**Lumiya pattern:** `libopenjpeg.so` exports — visible from `OpenJPEG.java` JNI declarations:
  - `decompress`, `decompressTGA`, `readRaw`, `writeJPEG2K`, `writeRaw` — codec
  - `applyMeshMorph`, `applyRiggedMeshMorph`, `applyMorphingTransform`, `applyFlexibleMorph` — mesh skinning
  - `bakeTerrainRaw` — terrain texture bake
  - `checkFrustrumOcclusion` — culling
  - `calcFlexiSections` — flexi-prim physics
  - `meshPrepareInfluenceBuffer`, `meshPrepareSeparateInfluenceBuffer` — rigged mesh prep
  - `drawBuf` — RGBA blit/blend with alpha and bump options

Everything that runs every frame lives behind the same JNI boundary. **Kotlin → JNI call overhead is not free** (boxing, marshalling, array pinning) — Linkpoint pays it many times per frame because operations are split across files and managers.

**Action:** Extend `src/main/cpp/j2k_decoder.cpp` (or rename to `linkpoint_native.cpp`) and `CMakeLists.txt` to add, in priority order:
  1. `applyMeshMorph` / `applyRiggedMeshMorph` — biggest win, called per-skinned-avatar per frame
  2. `bakeTerrainRaw` — once per region load, but currently slow if done in Kotlin
  3. ETC1/ETC2 compression entry points if not using `android.opengl.ETC1`
  4. Frustum occlusion check — only if Filament's culling proves insufficient

Native-side: consider porting Lumiya's actual C source for these (the decompile only shows the Java JNI declarations, not the C bodies — the C source is gone with Alina's laptop). Best reference is probably Firestorm's `indra/llrender/` and `indra/llmath/` for the equivalents.

### 4. Memory-mapped texture cache backing

**Lumiya pattern:** `OpenJPEG.mmapped`, `mmappedAddr`, `mmappedSize` fields hint at an mmap path for cached textures. Decoded RGBA was written to a backing file once, then subsequent loads `mmap`'d the file into a `ByteBuffer` instead of re-decoding.

**Action:** 
  - Cache decoded+ETC2-compressed textures by UUID under `getCacheDir()/textures/`
  - On second hit, mmap the file and wrap it as a `DirectByteBuffer` via `FileChannel.map(MapMode.READ_ONLY, ...)`
  - Pass the mmapped buffer straight to Filament's `PixelBufferDescriptor`
  - Skip J2K decode entirely on cache hit

This is what makes Lumiya feel instant on previously-visited regions.

### 5. AgentUpdate cadence

**Open question:** What rate did Lumiya send AgentUpdate? Linkpoint currently sends ~10 Hz (every ~110 ms based on the debug capture). The SL viewer reference is 10 Hz when the avatar is moving and back-off when idle. Worth verifying Lumiya did the idle back-off — `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/SLAgentCircuit.java` is the place to look.

Minor compared to items 1–3, but a worthwhile pass.

### 6. Object-update batching / spatial partitioning

**Lumiya pattern (from `ARCHITECTURE.md` of Lumiya-Redux):** `render/spatial/DrawList` + `DrawListEntry` hierarchy with octree-style culling.

**Action:** Out of scope until items 1–3 are done. Filament has its own scene graph and culling, so the win here is much smaller than the texture-compression win.

## What is explicitly *not* on this list

- **glTF/PBR support** — confirmed via Firestorm and SL wiki research that glTF material assets still resolve every texture slot to ordinary J2C UUIDs. There is no new wire format. Once the texture path is fast for J2C, PBR materials are mostly a shader concern.
- **Basis Universal / KTX2** — confirmed neither Lumiya nor Firestorm uses it on the wire. The headers under `src/main/cpp/basis_universal/` were already removed in PR #455.
- **Replacing OpenJPEG with `keiji/jp2k-decoder-android`** — that's a WASM-in-JS-engine fallback. Not the right primary, even less the right target after this work goes in.

## Order of execution (suggested)

1. **First commit:** Land the unified `LinkpointTexture` skeleton (item 1) without changing decode behaviour. Pure refactor — moves existing decode + upload paths behind one class.
2. **Second commit:** Add `compressETC1()` for opaque prim textures (item 2, simple form). Measure VRAM use before/after on a known region.
3. **Third commit:** mmap'd texture cache (item 4). Now repeat-visit regions are instant.
4. **Fourth commit:** Move `applyMeshMorph` to native (item 3, narrow first cut). Profile per-frame mesh skinning.
5. Items 3 (broader) / 5 / 6 as follow-ups.

## Pre-requisite: confirm `linkpoint-j2k.so` actually ships

The last debug capture showed `JPEG2000 Decoding: Backend: none`. PR #455 didn't fix this because it's a build/packaging issue, not a code issue. Before any of the above work pays off, **a fresh debug-build APK off this branch needs to be inspected to confirm `liblinkpoint-j2k.so` is in `lib/<abi>/`**. If it isn't, the CMake / Prefab / OpenJPEG resolution chain needs investigation first.

Quick check command:
```bash
unzip -l app-debug.apk | grep -i 'linkpoint-j2k\|openjp2'
```

## References

- `lumiya_decompiled_source/com/lumiyaviewer/lumiya/openjpeg/OpenJPEG.java` — the unified texture class
- `lumiya_decompiled_source/com/lumiyaviewer/lumiya/GridConnectionService.java` — service lifecycle (already mirrored)
- `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/` — protocol layer (already mirrored)
- `https://github.com/Kaleaon/Lumiya-Redux` — fuller decompile, smali, AndroidX-migrated sources
- `https://github.com/FirestormViewer/phoenix-firestorm` — `indra/llimage/`, `indra/llimagej2coj/`, `indra/llrender/` for canonical reference implementations
- `https://wiki.secondlife.com/wiki/Protocol` — message frequency classes, packet layout
- `https://wiki.secondlife.com/wiki/PBR_Materials` — confirms texture UUIDs still resolve to J2C
- PR #455 (Linkpoint) — debug-report fixes that closed out the previous session
