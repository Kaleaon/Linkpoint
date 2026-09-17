# Sector 02 — Rendering, Graphics & Native Audio/Texture

**Findings:** 19 | **Critical:** 5 | **High:** 7 | **Medium:** 6 | **Low:** 1

Verified against source at `Linkpoint/src/main/java/com/linkpoint/` and
`Linkpoint/src/main/cpp/` on 2026-09-17.

---

## RENDER-001 | OpenXR session not implemented

- **File:** `Linkpoint/src/main/java/com/linkpoint/xr/XRManager.kt`
- **Lines:** ~363–372
- **Category:** stub / not-implemented
- **Severity:** Critical

**What is broken:**
`OpenXRSession` is a complete stub — `initialize()` returns `false`,
`beginFrame()` returns `null`, `getHeadPose()` returns `null`,
`getControllers()` returns `emptyList()`. The XR rendering path is dead code.

**Fix plan:**
1. Implement OpenXR Loader initialization via JNI (`xrCreateInstance`,
   `xrCreateSession`).
2. Wire frame timing (`xrWaitFrame` / `xrBeginFrame` / `xrEndFrame`).
3. Implement `getHeadPose()` via `xrLocateViews`.
4. Reference: Khronos OpenXR SDK samples; SL viewer XR extensions.

**Acceptance test:** Manual — verify head tracking on Quest device.

---

## RENDER-002 | Android XR session not implemented

- **File:** `Linkpoint/src/main/java/com/linkpoint/xr/XRManager.kt`
- **Lines:** ~375–385
- **Category:** stub / not-implemented
- **Severity:** Critical

**What is broken:**
Same as RENDER-001 but for the Android 15+ XR runtime.

**Fix plan:** Requires Android 15+ SDK; implement when available. Add
`Log.i` noting XR is unavailable on lower API levels.

---

## RENDER-003 | XR sessions explicitly marked stubs

- **File:** `Linkpoint/src/main/java/com/linkpoint/xr/XRManager.kt`
- **Line:** 288
- **Category:** stub-comment
- **Severity:** Medium

**What is broken:**
Comment `// Session implementations (stubs for now)` confirms RENDER-001/002
(still present as of 2026-09-17).

**Fix plan:** Remove comment once RENDER-001 or RENDER-002 is implemented.

---

## RENDER-004 | VxAudio JNI: audio processing returns error

- **File:** `Linkpoint/src/main/cpp/jni/audio_stubs.cpp`
- **Lines:** 16–31
- **Category:** stub-implementation
- **Severity:** Critical

**What is broken:**
`VxAudioJNI_initializeAudio()` returns -1. `processAudioBuffer()` returns 0.
Advanced audio processing (echo cancellation, noise suppression, AGC) is
unavailable.

**Fix plan:**
1. Bridge to WebRTC's AudioProcessing module (AEC, NS, AGC).
2. Replace `initializeAudio` with `AudioProcessingBuilder::Create()`.
3. Replace `processAudioBuffer` with `AudioProcessing::ProcessStream()`.

**Acceptance test:** Native unit test verifying audio buffer round-trip.

---

## RENDER-005 | SndFile audio I/O: always returns null

- **File:** `Linkpoint/src/main/cpp/jni/audio_stubs.cpp`
- **Lines:** 33–56
- **Category:** stub-implementation
- **Severity:** High

**What is broken:**
`sf_1open()` returns 0 (null handle). `sf_1read_1float()` returns 0 frames.
Sound file playback from assets is impossible.

**Fix plan:**
1. Integrate `libsndfile` (BSD) or use Android `MediaExtractor` + `MediaCodec`.
2. Wire JNI methods to real file handles.

**Acceptance test:** Play a WAV sound asset; verify audio output.

---

## RENDER-006 | oRTP: RTP streaming returns error

- **File:** `Linkpoint/src/main/cpp/jni/audio_stubs.cpp`
- **Lines:** 58–86
- **Category:** stub-implementation
- **Severity:** Critical

**What is broken:**
`rtp_1session_1new()` returns 0. `rtp_1session_1send_1with_1ts()` returns -1.
Real-time voice packet transmission at the native layer is non-functional.

**Fix plan:**
Replace oRTP stubs with WebRTC's built-in RTP stack (available via the WebRTC
dependency in the Kotlin VoiceManager). Remove oRTP JNI layer entirely.

**Acceptance test:** Voice call test between two agents.

---

## RENDER-007 | Audio device enumeration returns 0 devices

- **File:** `Linkpoint/src/main/cpp/jni/audio_stubs.cpp`
- **Lines:** 88–105
- **Category:** stub-implementation
- **Severity:** High

**What is broken:**
`getInputDeviceCount()` and `getOutputDeviceCount()` return 0. Voice UI cannot
display or select audio devices.

**Fix plan:**
Query `AudioManager.getDevices(GET_DEVICES_INPUTS | GET_DEVICES_OUTPUTS)` via
JNI callback to Java; return actual device count and names.

**Acceptance test:** Verify device list is non-empty on a real device.

---

## RENDER-008 | Vivox JNI stubs bridge to unimplemented WebRTC

- **File:** `Linkpoint/src/main/cpp/jni/vivox_stubs.cpp`
- **Lines:** 16–180
- **Category:** stub-with-comments
- **Severity:** Critical

**What is broken:**
All 12+ Vivox SDK functions (`vx_initialize`, `vx_req_connector_create`,
`vx_req_account_login`, `vx_req_session_create`, session management, mute,
volume) return success codes but perform no work. Comments say "would be called
here" / "bridging to WebRTC implementation" but the bridge does not exist.

**Fix plan:**
1. Implement `WebRTCVoiceAdapter` in Kotlin/Java that the JNI stubs delegate to.
2. Map Vivox calls to WebRTC adapter methods.
3. Remove the Vivox JNI layer once the adapter is stable.

**Acceptance test:** End-to-end voice test on staging grid.

---

## RENDER-009 | Basis Universal transcoder: returns null

- **File:** `Linkpoint/src/main/cpp/jni/basis_transcoder_stub.cpp`
- **Lines:** 16–40
- **Category:** stub-implementation
- **Severity:** High

**What is broken:**
`nativeTranscodeTexture()` returns `nullptr`. `nativeIsFormatSupported()` returns
`JNI_FALSE`. All Basis Universal / KTX2 textures fail to decode.

**Fix plan:**
1. Basis Universal source is already present at
   `src/main/cpp/basis_universal/`. Wire the transcoder by including
   `basisu_transcoder.cpp` in the CMake build.
2. Implement `nativeTranscodeTexture` using `basist::basisu_transcoder`.
3. Implement `nativeGetOptimalFormat` by querying EGL extensions for
   ASTC/ETC2/BC7 support.

**Acceptance test:** Load a `.basis` texture; verify non-null bitmap.

---

## RENDER-010 | Optimal texture format always returns GL_RGBA

- **File:** `Linkpoint/src/main/cpp/jni/basis_transcoder_stub.cpp`
- **Line:** 43
- **Category:** placeholder-return
- **Severity:** Medium

**What is broken:**
`nativeGetOptimalFormat()` returns `0x1908` (GL_RGBA) regardless of GPU
capabilities.

**Fix plan:** Addressed by RENDER-009 fix item 3.

---

## RENDER-011 | HUD textures fall back to text labels

- **File:** `Linkpoint/src/main/java/com/linkpoint/hud/HUDOverlayView.kt`
- **Lines:** ~187–210
- **Category:** placeholder-fallback
- **Severity:** High

**What is broken:**
When a HUD texture is not yet loaded, the view draws `hud.name.take(15)` as
text. Textured HUDs display as truncated name labels until texture arrives — or
permanently if JPEG2000 decoder is unavailable.

**Fix plan:**
1. Show a checkerboard or loading-spinner placeholder bitmap while the texture
   is being fetched.
2. Add a priority bump in `TextureManager` for HUD textures.
3. Reference: SL viewer `indra/newview/llhudicon.cpp`.

**Acceptance test:** Attach a textured HUD and verify visual before texture loads.

---

## RENDER-012 | World pass can be disabled without validation

- **File:** `Linkpoint/src/main/java/com/linkpoint/render/lumiya/core/LumiyaRenderer.kt`
- **Lines:** ~346–348
- **Category:** incomplete-logic
- **Severity:** Low

**What is broken:**
`setWorldPassEnabled(false)` allows rendering with zero visible world geometry.

**Fix plan:** Add assertion or log warning if both world and HUD passes are off.

---

## RENDER-013 | HUD render pass lacks compositor pipeline

- **File:** `Linkpoint/src/main/java/com/linkpoint/render/lumiya/core/LumiyaRenderer.kt`
- **Lines:** ~350–371
- **Category:** limited-implementation
- **Severity:** Medium

**What is broken:**
The HUD pass swaps matrices and disables depth, but lacks: (a) separate FBO for
HUD compositing, (b) HUD element pick/raycasting for touch input, (c)
HUD-specific depth ordering among overlapping HUD prims.

**Fix plan:**
1. Render HUD to a dedicated FBO; composite onto main framebuffer.
2. Implement HUD ray-hit testing for touch events.

**Acceptance test:** `./gradlew :Linkpoint:testDebugUnitTest --tests "*LumiyaRenderer*"`

---

## RENDER-014 | MaterialLoader returns null on shader compile failure

- **File:** `Linkpoint/src/main/java/com/linkpoint/render/materials/MaterialLoader.kt`
- **Category:** placeholder-material / missing-fallback
- **Severity:** High

**What is broken:**
If `MaterialBuilder.build()` returns null (shader compile error), the function
logs and returns null. Prims using that material become invisible.

**Fix plan:**
1. Maintain a pre-compiled emergency fallback material (solid magenta — the
   classic "missing material" color).
2. Return the fallback instead of null.

**Acceptance test:** Force a shader compile error and verify magenta prim.

---

## RENDER-015 | compileShaders() always returns true

- **File:** `Linkpoint/src/main/java/com/linkpoint/render/lumiya/core/LumiyaRenderContext.kt`
- **Category:** broken-logic
- **Severity:** High

**What is broken:**
If `PrimShaderProgram`, `AvatarShaderProgram`, or `TerrainShaderProgram` fail
to compile, the function still returns `true`. The renderer initializes in a
broken state and draw calls silently no-op.

**Fix plan:**
1. Track a `success` boolean; set to `false` if any critical shader fails.
2. Return `false` so the caller can fall back to software rendering or show an
   error.

**Acceptance test:** Unit test with a deliberately broken shader source.

---

## RENDER-016 | DrawablePrimStore only builds box geometry

- **File:** `Linkpoint/src/main/java/com/linkpoint/render/lumiya/drawable/DrawablePrimStore.kt`
- **Category:** incomplete-implementation
- **Severity:** High

**What is broken:**
Only box/sphere/cylinder VAOs are built; `drawOpaque()` and `drawTransparent()`
always use `boxVAO`. ALL prims render as boxes regardless of their actual shape
type (torus, prism, ring, sculpt, mesh).

**Fix plan:**
1. Add `buildTorus()`, `buildPrism()`, `buildRing()` geometry generators.
2. Store shape type in `PrimInstance` and dispatch to the correct VAO.
3. For sculpt/mesh prims, delegate to the mesh asset decoder.

**Acceptance test:** Rez a torus in-world and verify it renders as a torus.

---

## RENDER-017 | All avatars rendered with hard-coded skin tone

- **File:** `Linkpoint/src/main/java/com/linkpoint/render/lumiya/drawable/DrawableAvatarStore.kt`
- **Line:** ~83
- **Category:** placeholder-color
- **Severity:** High

**What is broken:**
```kotlin
program.setColor(0.85f, 0.72f, 0.62f, 1.0f)
```
Every avatar is rendered with a fixed tan/brown color. Baked textures, skin
textures, and clothing textures are all ignored.

**Fix plan:**
1. Load baked texture from `AvatarBaker` output for each avatar.
2. Bind texture to shader program via `program.setTexture()`.
3. Fall back to placeholder color only if texture is not yet available.

**Acceptance test:** Log in with a non-default skin and verify visual.

---

## RENDER-018 | Water renderer mesh incomplete

- **File:** `Linkpoint/src/main/java/com/linkpoint/render/water/WaterRenderer.kt`
- **Category:** incomplete-implementation
- **Severity:** Medium

**What is broken:**
`createWaterMesh()` starts building vertex/index buffers but is incomplete;
the function is never called from the main render loop.

**Fix plan:**
1. Complete index buffer generation (quad tessellation of the water plane).
2. Add `WaterRenderer.initialize()` call in `LumiyaRenderer.onSurfaceCreated()`.
3. Add water material with wave animation uniform.

**Acceptance test:** Verify water surface renders at region water height.

---

## RENDER-019 | OpenJPEG native placeholder textures

- **File:** `Linkpoint/src/main/cpp/jni/openjpeg_real_implementation.cpp`
- **Line:** ~53
- **Category:** placeholder-comment
- **Severity:** Medium

**What is broken:**
Comment: `// This creates placeholder textures until we integrate full OpenJPEG`.
The OpenJPEG → GL texture upload pipeline is not fully wired.

**Fix plan:**
1. Verify `j2k_decoder.cpp` correctly calls `opj_decode()` and returns pixel data.
2. Wire decoded pixels to `GLES32.glTexImage2D()` upload path.

**Acceptance test:** Load a JPEG2000 texture asset and verify non-placeholder render.
