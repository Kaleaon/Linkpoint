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
| `slproto/SLCircuit`, `SLConnection`, `SLAgentCircuit` | `Linkpoint/src/main/java/com/linkpoint/protocol/messages/UDPConnectionFixed.kt` (`startAgentUpdates`, `sendAgentUpdate`) + `network/core/AgentCircuit.kt` | ✅ functional after PR #455 |
| `message_template.msg` parser → 477 message classes | `protocol/messages/MessageIds.kt` (e.g. `AGENT_UPDATE = 4`) + parsers under `protocol/messages/` | ✅ comprehensive |
| HTTP capabilities + event queue (`slproto/caps/`) | `network/CapabilityManager.kt` + event queue handlers | ✅ functional |
| greenDAO ORM for asset metadata | Room (or equivalent) under `assets/` | ✅ functional |
| `inventory-skeleton` parsing on login | `protocol/auth/LoginResponseParser.kt` + `addFolderFromLogin` | ✅ + warm-fetch added in PR #455 |

## What is missing — the actual port work

Ordered by impact-per-effort. Each item is a separate commit (or PR) on this branch.

### 1. Unified `LinkpointTexture` class à la Lumiya's `OpenJPEG`

**Lumiya source:** `lumiya_decompiled_source/com/lumiyaviewer/lumiya/openjpeg/OpenJPEG.java`

The existing pattern in Linkpoint is decode-only:
  - `Linkpoint/src/main/java/com/linkpoint/assets/JPEG2000Decoder.kt` returns an Android `Bitmap` (via `nativeDecode` → `liblinkpoint-j2k.so`, with a JP2ForAndroid reflection fallback)
  - `assets/TextureManager.kt` and `render/lumiya/glres/GLTextureCache.kt` upload it to Filament / GL
  - Memory accounting is split across `assets/AssetCache.kt` and `assets/CacheManager.kt`; there is no `TextureMemoryTracker` yet (the Lumiya equivalent), so we are inventing it here

Lumiya's `OpenJPEG` class **owns the lifecycle from disk to GPU** in one object:
  - Multiple constructors for J2K / raw / TGA inputs
  - `ByteBuffer rawBuffer` is the canonical pixel store (mmap-able)
  - `CompressETC1()` converts decoded RGBA → ETC1 in-place — see item 2
  - `SetAsImmutableTexture()` uses `glTexStorage2D` + `glTexSubImage2D` for the ES 3.0 fast path
  - `TextureMemoryTracker.allocOpenJpegMemory(...)` is called from inside the class on every allocation/free, including a flag for whether the buffer is mmapped
  - `finalize()` is the single point that releases the native buffer — **we will not copy this part**, see Action below

**Action:** Introduce `assets/LinkpointTexture.kt` (or `render/LinkpointTexture.kt`) that:
  - Wraps the J2K decode (calls into `JPEG2000Decoder` for now, eventually folds it in)
  - Holds the `ByteBuffer` at native heap (or mmapped file, see item 4)
  - Exposes `compressETC2()` (Filament-friendly successor to ETC1 — see item 2)
  - Exposes `uploadToFilament(engine: Engine): Texture` using compressed-texture upload path
  - Owns its memory accounting (introduce `TextureMemoryTracker` under `assets/` — does not exist yet)
  - **Implements `AutoCloseable`** for deterministic release of the native buffer / mmap region / Filament `Texture`. `finalize()` is non-deterministic and runs on the finalizer thread (often after VRAM has already been exhausted) — never copy that pattern from Lumiya. Back the close path with a `java.lang.ref.Cleaner` registration as a safety net for callers that drop the handle without calling `close()`, but treat that purely as a leak-detection log path, not the primary cleanup mechanism.

**Risk:** Filament's texture API differs from raw OpenGL — investigate whether Filament accepts pre-compressed ETC2 buffers via `Texture.PixelBufferDescriptor` (it does; format `ETC2_RGBA8` is supported). Verify before committing.

### 2. ETC2 GPU compression for prim textures

**Lumiya method:** `OpenJPEG.CompressETC1()` (lines 202–218 of the decompile)
  - Uses Android's built-in `android.opengl.ETC1.encodeImage(...)` on the CPU
  - Only runs when `num_components == 3 && num_extra_components == 0` and bytes-per-pixel is 2 or 3
  - Replaces the raw RGBA buffer with the ETC1-compressed buffer in place
  - 4 bpp (RGBA8888) → 0.5 bpp (ETC1) = **~8× VRAM reduction**

**Why this is *the* perf win:** Lumiya could fit ~8× more textures resident in GPU memory than Linkpoint can today. On a busy SL region (200+ unique textures) Linkpoint thrashes between disk decode and GPU upload, which is exactly what "feels slow" looks like.

**Action:** Pick a CPU-side encoder. Options:
  - **ETC2/EAC** via `etcpak` (https://github.com/wolfpld/etcpak) compiled into the native lib — fast, supports RGBA, MIT/BSD-3 license, `RGBA8 → ETC2_EAC` is the single format we need. **This is the choice.**
  - **`android.opengl.ETC1`** — already in AOSP, no new dependency, but only opaque RGB. Rejected as primary because SL leans heavily on alpha (foliage, hair, particles, glass, signs); shipping an opaque-only fast path means we'd immediately need to write a second path for alpha textures, doubling the surface area.
  - **`org.etc2comp` / `androidx.graphics`** — `androidx.graphics` does not expose a CPU encoder, and `etc2comp` is unmaintained and slower than `etcpak` on the benchmarks the etcpak author publishes. Skipping.

Decision: implement ETC2/EAC directly via `etcpak` in the native lib (item 3), exposed as a single `compressEtc2Rgba(...)` JNI entry point. `minSdk` for this codebase is already ES 3.0+ so ETC2 is universally supported on target devices — there is no fallback story we need to keep alive.

For the very first commit we can stub `compressETC2()` to call `android.opengl.ETC1.encodeImage` on opaque inputs to prove the upload path end-to-end, but the `etcpak` integration must land before this is enabled by default — we never want to ship the half-pipeline that drops alpha textures.

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

**Action:** Extend `Linkpoint/src/main/cpp/j2k_decoder.cpp` (or rename to `linkpoint_native.cpp`) and `Linkpoint/src/main/cpp/CMakeLists.txt` (project name `linkpoint-j2k`, target `linkpoint-j2k`) to add, in priority order:
  1. `applyMeshMorph` / `applyRiggedMeshMorph` — biggest win, called per-skinned-avatar per frame
  2. `bakeTerrainRaw` — once per region load, but currently slow if done in Kotlin
  3. ETC1/ETC2 compression entry points if not using `android.opengl.ETC1`
  4. Frustum occlusion check — only if Filament's culling proves insufficient

Native-side: consider porting Lumiya's actual C source for these (the decompile only shows the Java JNI declarations, not the C bodies — the C source is gone with Alina's laptop). Best reference is probably Firestorm's `indra/llrender/` and `indra/llmath/` for the equivalents.

### 4. Memory-mapped texture cache backing

**Lumiya pattern:** `OpenJPEG.mmapped`, `mmappedAddr`, `mmappedSize` fields hint at an mmap path for cached textures. Decoded RGBA was written to a backing file once, then subsequent loads `mmap`'d the file into a `ByteBuffer` instead of re-decoding.

**Action:**
  - Cache decoded+ETC2-compressed textures by UUID under `CacheManager.getCacheDirectory(TEXTURES)` (existing API — do not introduce a parallel `getCacheDir()/textures/` directory)
  - On second hit, mmap the file and wrap it as a `DirectByteBuffer` via `FileChannel.map(MapMode.READ_ONLY, ...)`
  - Pass the mmapped buffer straight to Filament's `PixelBufferDescriptor`
  - Skip J2K decode entirely on cache hit

**Safety constraints — these are not optional:**
  - **Files in the texture cache are immutable once written.** Write to `<uuid>.etc2.tmp`, fsync, atomic rename to `<uuid>.etc2`. Never truncate, never overwrite, never delete a file that any in-flight `LinkpointTexture` still has mapped — eviction must wait until the last reference drops. A `MappedByteBuffer` whose backing file is truncated or unlinked will SIGBUS on next access; on Android that's an unrecoverable native crash.
  - **Eviction goes through a refcount on `LinkpointTexture`,** not through `File.delete()` from the cache manager's thread. The cache manager marks a UUID as "evictable", and the actual unlink happens once the refcount reaches zero (driven by `LinkpointTexture.close()` from item 1).
  - **Bound the mapped set.** Each `MappedByteBuffer` consumes virtual address space; on a 32-bit ABI you exhaust it fast. Cap concurrent maps (start at 256) and LRU-evict (which means: drop the `MappedByteBuffer` reference, run `System.gc()` only as a last resort — Android does not give us `sun.misc.Unmap`). On 64-bit ABIs the cap can be much higher.
  - **Validate file size against the header before mapping.** A truncated or partially-written cache file should be deleted and re-decoded, not mapped.
  - If these constraints prove too fiddly in practice, fall back to pread-into-direct-buffer on cache hit — slower than mmap but no SIGBUS / VA-exhaustion class of bug. mmap is an optimisation, not a correctness requirement.

This is what makes Lumiya feel instant on previously-visited regions.

### 5. AgentUpdate cadence

**Open question:** What rate did Lumiya send AgentUpdate? Linkpoint currently sends ~10 Hz (every ~110 ms based on the debug capture). The SL viewer reference is 10 Hz when the avatar is moving and back-off when idle. Worth verifying Lumiya did the idle back-off — `lumiya_decompiled_source/com/lumiyaviewer/lumiya/slproto/SLAgentCircuit.java` is the place to look. Linkpoint's send loop lives in `Linkpoint/src/main/java/com/linkpoint/protocol/messages/UDPConnectionFixed.kt::startAgentUpdates`.

Minor compared to items 1–3, but a worthwhile pass.

### 6. Object-update batching / spatial partitioning

**Lumiya pattern (from `ARCHITECTURE.md` of Lumiya-Redux):** `render/spatial/DrawList` + `DrawListEntry` hierarchy with octree-style culling.

**Action:** Out of scope until items 1–3 are done. Filament has its own scene graph and culling, so the win here is much smaller than the texture-compression win.

## What is explicitly *not* on this list

- **glTF/PBR support** — confirmed via Firestorm and SL wiki research that glTF material assets still resolve every texture slot to ordinary J2C UUIDs. There is no new wire format. Once the texture path is fast for J2C, PBR materials are mostly a shader concern.
- **Basis Universal / KTX2** — confirmed neither Lumiya nor Firestorm uses it on the wire. The headers under `src/main/cpp/basis_universal/` were already removed in PR #455.
- **Replacing OpenJPEG with `keiji/jp2k-decoder-android`** — that's a WASM-in-JS-engine fallback. Not the right primary, even less the right target after this work goes in.

## Wiring follow-ups (carry-overs from the scaffold commit)

The first commit on this branch lands the skeleton classes (`LinkpointTexture`, `TextureMemoryTracker`, `Etc2Compressor`, `MmappedTextureCache`) but does not yet plumb them into the existing asset/cache/debug surfaces. The next commit needs to close these loops, otherwise the scaffold will silently undercount memory and bypass existing safety nets:

- **`MmappedTextureCache` directory selection** must come from `CacheManager.getPublicAssetDirectory(CacheableAssetType.TEXTURES)` (or a new `ETC2_TEXTURES` subdir under it). The `.etc2` extension already disambiguates from raw J2K, but eviction signals need to flow both ways: when `AssetCache.pruneIfNeeded()` evicts a UUID it must also call `MmappedTextureCache.markEvictable(uuid)`.
- **`TextureMemoryTracker.snapshot()` must feed `DebugReportService`.** Today the debug report prints `assetCacheStats.memorySizeBytes` only — once `LinkpointTexture` is on the hot path that number will undercount native + mmap + GPU bytes by an order of magnitude.
- **`LinkpointTexture.fromJ2k` must route through `TextureManager.decodeTexture`** (or extract its memory-budget check + retry loop into a free function `LinkpointTexture` calls). The current scaffold has only a coarse `MAX_DECODED_PIXELS` guard; the existing `MAX_DECODE_MEMORY_BYTES` budget and per-texture error-state recording in `TextureManager.kt:478` are richer and should not be bypassed.
- **`Closeable` vs `AutoCloseable` consistency.** New code uses `AutoCloseable`; the protocol layer (`CircuitTaskQueue`, `CircuitThread`) uses `java.io.Closeable`. They interoperate but pick one repo-wide.

## Order of execution (suggested)

1. **First commit:** Land the unified `LinkpointTexture` skeleton (item 1) without changing decode behaviour. Pure refactor — moves existing decode + upload paths behind one class.
2. **Second commit:** Add `compressETC1()` for opaque prim textures (item 2, simple form). Measure VRAM use before/after on a known region.
3. **Third commit:** mmap'd texture cache (item 4). Now repeat-visit regions are instant.
4. **Fourth commit:** Move `applyMeshMorph` to native (item 3, narrow first cut). Profile per-frame mesh skinning.
5. Items 3 (broader) / 5 / 6 as follow-ups.

## Pre-requisite: confirm `linkpoint-j2k.so` actually ships

The last debug capture showed `JPEG2000 Decoding: Backend: none`. PR #455 didn't fix this because it's a build/packaging issue, not a code issue. Before any of the above work pays off, **a fresh debug-build APK off this branch needs to be inspected to confirm `liblinkpoint-j2k.so` is in `lib/<abi>/`**. If it isn't, the CMake / Prefab / OpenJPEG resolution chain needs investigation first.

**Likely root cause (addressed in this branch):** the project did not pin `ndkVersion` in `Linkpoint/build.gradle.kts`, so the NDK chosen by AGP could drift away from the r26 NDK that `com.viliussutkus89.ndk.thirdparty:openjpeg-ndk26-static:2.5.0-beta-4` was built against. A mismatched NDK produces a `liblinkpoint-j2k.so` that fails to link `libopenjp2.a` symbols at load time — `System.loadLibrary` then throws `UnsatisfiedLinkError` and `JPEG2000Decoder` falls through to `Backend: none`. We now pin `ndkVersion = "26.3.11579264"` so this can no longer drift. A clean build off this branch should produce a populated `lib/<abi>/liblinkpoint-j2k.so`.

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
