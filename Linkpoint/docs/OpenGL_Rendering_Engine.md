# OpenGL ES 3 Rendering Engine

> Status: **Primary** as of branch `claude/opengl-rendering-engine-LCKVz`.
> Filament has been demoted to an opt-in fallback after persistent
> driver-level crashes on Adreno/Mali devices and friction with the
> Second Life / OpenSim asset pipeline.

This document covers the architecture of Linkpoint's hand-rolled OpenGL
ES 3.0+ renderer, its lineage from existing Second Life viewers, the
producer plumbing that feeds it, and the migration path off Filament.

---

## 1. Reference lineage

The renderer is descended from four lines of Second Life viewer
implementation, in approximate order of how directly we copied them:

| Reference | What we took |
|---|---|
| **Lumiya Viewer** (Android, GL ES 2 originally) | Surface view + render loop pattern, drawable store layout, shader naming, asset-bundled GLSL, FXAA post-process, sky-dome depth trick. Linkpoint's `render/lumiya/` subtree is a near-isomorphic Kotlin port. |
| **Singularity Viewer** (desktop LL fork) | LLPipeline-style pass orchestration, LLDrawPool material grouping, LLSpatialPartition octree concepts. Reflected in `LumiyaRenderer`'s pass list and `spatial/FrustumCuller`. |
| **Firestorm + official LL viewer** | Render-target chain, ALM lighting hints, Windlight environment params (sun direction, ambient/diffuse split, haze, water height). Reflected in `LumiyaRenderContext`'s windlight block. |
| **Lumiya's `WorldViewRenderer.java`** specifically | The 12-stage frame plan: prep → FXAA FBO → clear → opaque world → avatars → sky → transparent → water → particles → HUD → FXAA resolve → cleanup. |

---

## 2. Class map

```
render/lumiya/
├── core/
│   ├── LumiyaGLSurfaceView   → Android GLSurfaceView, owns the EGL ctx
│   ├── LumiyaRenderer         → GLSurfaceView.Renderer + RenderEngineProvider
│   ├── LumiyaRenderContext    → "engine" state (shaders, UBOs, FBOs, windlight)
│   ├── LumiyaFramePlanner     → enumerates which passes run this frame
│   ├── RenderEngineProvider   → backend-agnostic interface
│   ├── RenderEngineSwitcher   → runtime engine swap (defaults to LUMIYA)
│   └── GlExtHelper            → JNI bridge for GL_EXT_buffer_storage
├── glres/
│   ├── GLBufferManager        → VBO/IBO/VAO allocation
│   ├── GLTextureCache         → UUID-keyed LRU on top of immutable textures
│   └── GLResourceManager      → deferred-delete queues + phantom-ref tracking
├── shaders/
│   ├── BaseShaderProgram      → compile/link harness, uniform cache
│   ├── PrimShaderProgram      → opaque + transparent prims
│   ├── AvatarShaderProgram    → system avatar mesh
│   ├── RiggedMeshShaderProgram→ skinned mesh attachments
│   ├── TerrainShaderProgram   → 4-layer splatmap terrain
│   ├── WaterShaderProgram     → animated water plane
│   ├── SkyShaderProgram       → sky dome
│   ├── StarsShaderProgram     → star billboards
│   ├── ParticleShaderProgram  → particle system
│   ├── FXAAShaderProgram      → post-process AA resolve
│   └── ShaderCompiler         → shared compile + log scrub
├── drawable/
│   ├── DrawableTerrain        → 16×16-m heightfield patches
│   ├── DrawableWater          → animated quad + waterTime advance
│   ├── DrawableSky            → dome + stars
│   ├── DrawablePrimStore      → opaque/alpha prim collection
│   ├── DrawableAvatarStore    → tracked avatars + per-avatar joint UBOs
│   ├── DrawableHudStore       → HUD attachment overlay (orthographic pass)
│   ├── DrawableParticleManager→ billboard particles
│   └── AvatarMeshAssetLoader  → bundled default avatar mesh
├── spatial/
│   ├── FrustumCuller          → 6-plane frustum / AABB tests
│   └── SpatialIndex           → octree partition (scaffolding)
└── picking/
    └── GLRayTrace             → object pick via ray-vs-AABB
```

The companion `linden/llrender/` subtree contains thinner wrappers
(`LLGL`, `LLGLSLShader`, `LLDrawPool`, `LLRenderTarget`) that match the
LL viewer naming for code reading directly against Singularity /
Firestorm sources.

---

## 3. GL ES 3.0 minimum target

We target **OpenGL ES 3.0** as the floor. The only feature we
hard-require beyond ES 2 is uniform buffer objects (used for the global
matrix block and per-avatar joint palettes); everything else degrades
when absent.

| Feature | Required | Used for |
|---|---|---|
| UBO (`glBindBufferBase`) | yes | Global view/proj/camera/sun block; per-avatar joint palette (max 64 joints) |
| VAO | yes | Per-shape vertex layout cached once at startup |
| Immutable textures (`glTexStorage2D`) | yes | sRGB albedo + linear non-color |
| Mipmaps + trilinear | yes | All sampled textures |
| sRGB framebuffer (`GL_SRGB8_ALPHA8`) | yes | Albedo gamma correctness on write |
| MRT | optional | Future deferred path |
| Instanced draws | optional | Terrain patches, particles |
| Transform feedback | optional | GPU particle update |
| ETC2 / ASTC | optional | Compressed texture upload (GPU bandwidth) |
| Compute shaders (ES 3.1) | optional | Future GPU-driven culling |
| `GL_EXT_buffer_storage` | optional | Persistent-mapped global UBO (auto-detected) |

Capability detection runs in `LumiyaRenderContext.queryGPUCapabilities`
and reports vendor/renderer/version into `RenderDiagnostics`. The
renderer's behavior degrades silently when an optional path is missing.

---

## 4. Frame plan

Driven by `LumiyaFramePlanner.createPlan` and executed in
`LumiyaRenderer.renderFrame`:

```
1. beginFrame()              → frameNumber++, deltaTime, waterTime
2. onBeforeFrame(dt)         → producer hook (avatar pose tick, etc.)
3. updateCamera()            → look-at + perspective + frustum extract
4. uploadGlobalUBO()         → proj + view + model + camPos + sunDir
5. Bind FXAA FBO if enabled
6. Clear color + depth + stencil

7. WORLD_OPAQUE              → terrain, opaque prims (front-to-back)
8. WORLD_AVATAR              → tracked avatars (skinned)
9. WORLD_SKY                 → dome behind everything via depth trick
10. WORLD_TRANSPARENT        → alpha prims (back-to-front)
11. WORLD_WATER              → animated water plane
12. WORLD_PARTICLES          → billboards
13. HUD                      → orthographic 2D pass

14. Resolve FXAA back to default FBO
15. resourceManager.cleanup() → drain deferred deletes
```

The HUD pass swaps in an orthographic projection then restores the
3D matrices, mirroring Lumiya's `WorldViewRenderer` HUD path.

---

## 5. Producer pipeline

Protocol → `RenderCommandStream` → consumers. This is the canonical
data path; both engines subscribe to the same stream.

```
ObjectUpdate / TerrainData / etc.
              │
              ▼
   LinkpointApp.publishRenderCommand
              │
              ▼
        RenderCommandStream
            (SharedFlow)
              │
       ┌──────┴──────┐
       ▼             ▼
 FilamentRender   Gles3Render
 CommandConsumer  CommandConsumer
       │             │
       ▼             ▼
 Filament thread   GL thread
 (RenderManager)   (LumiyaGLSurfaceView)
```

`Gles3RenderCommandConsumer` handles:

- `UpsertPrim` — `engine.addObject` (or `LumiyaRenderer.upsertAvatar`
  for `pcode == 47`); kicks off a default-face texture fetch.
- `UpsertMesh` — best-effort: still binds the texture entry's default
  face. Geometry compilation is a follow-up; the consumer logs a
  placeholder rather than dropping the asset.
- `UpdateMaterial` — re-binds the default face texture; if a fallback
  ObjectUpdate is attached, also re-upserts the prim (matches Filament
  semantics so material updates can repair a missed prim).
- `RemoveEntity` — `engine.removeObject` + `removeAvatar` if a full UUID
  was supplied.
- `SetCamera` — forwards to `RenderEngineProvider.setCameraPosition/Target`.
- `SetTerrainPatch` — accumulates 16×16 patches into a 256×256 region
  heightmap and re-uploads as a 257×257 vertex grid.

### Texture binding flow

```
UpsertPrim arrives
  │
  ▼
TextureEntryParser.extractTextureIds(entry)
  │
  ▼
TextureFetcher.fetch(uuid, onResolved)
  │  (suspend in applicationScope; calls TextureManager.getTexture)
  ▼
Bitmap delivered to onResolved
  │
  ▼
glThreadExecutor { LumiyaRenderer.uploadTextureForPrim(...) }
  │
  ▼
GLTextureCache.put(uuid, bitmap, ALBEDO)  →  GL handle
  │
  ▼
DrawablePrimStore.setPrimTexture(primId, handle)
```

### Avatar pose flow

`wireGlesDataPipelines` installs `LumiyaRenderer.onBeforeFrame`. On every
frame, on the GL thread, before any draws:

1. For each tracked avatar:
   - `animator.update(deltaTime)` — advance keyframes
   - `skeleton.updateBoneMatrices()` — recompute world joints
   - `skeleton.getSkinningMatrices()` — flatten to `FloatArray`
   - `LumiyaRenderer.updateAvatarJoints(id, matrices, count)` — upload
     to the per-avatar joint UBO

This replaces the Filament-only `RenderManager.avatarPoseProvider`
hook, which never fired when the GL backend was active because
`RenderManager`'s own draw loop wasn't scheduled.

---

## 6. Backend selection

User-visible preference: `renderer_backend` (string).

| Value | Backend | Notes |
|---|---|---|
| `opengl` *(default)* | Lumiya GL ES 3 | Primary path |
| `gles` | Lumiya GL ES 3 | Alias |
| `lumiya` | Lumiya GL ES 3 | Alias |
| `filament` | Google Filament | Opt-in fallback |

For backwards compatibility with the Filament-default era, the legacy
`enable_secondary_renderer` boolean is honored if `renderer_backend`
is unset, and its default has been flipped to `true` so unset
installs land on OpenGL.

The runtime engine swap is orchestrated by `RendererHandoffManager`:
pause → flush → dispose → attach new + replay snapshot. Either
direction is supported with no app restart.

---

## 7. Filament migration path

Filament code remains in-tree; nothing was deleted. To strip Filament
in a future cleanup pass, the surface area is:

| Area | File | Action |
|---|---|---|
| Backend | `render/backend/FilamentBackend.kt` | Remove + `RenderEngineSwitcher` enum entry |
| Renderers | `render/RenderManager.kt`, `prims/`, `terrain/`, `water/`, `environment/`, `particles/` | Filament types (`Engine`, `Renderer`, `Scene`, `View`, `Camera`, `RenderableManager`) need to be replaced with calls into the GL drawables, OR these renderers can be deleted entirely once the GL drawable equivalents are at parity. |
| Materials | `render/materials/MaterialLoader.kt`, `FilamentMaterialTranslator.kt` | Already paired with `GlesMaterialTranslator.kt`; the loader can be deleted once nothing reads `.mat` files. |
| Texture upload | `render/RenderManager.uploadBitmapAsTexture` | Replace callers with `LumiyaRenderer.uploadTextureForPrim`. |
| Build | `Linkpoint/build.gradle.kts` (filament-android, gltfio-android, filamat-android, filament-utils-android) | Remove the four `1.66.0` dependencies. |
| Activities | `ui/render/FilamentTestActivity.kt`, `FilamentWorldViewActivity.kt` | Delete or redirect to the GL surface. |
| Command consumer | `render/scene/commands/FilamentRenderCommandConsumer.kt`, `LinkpointApp.filamentCommandConsumer` | Remove the consumer; the stream itself stays. |

We deliberately did **not** do the full strip in this branch — keeping
Filament available means we can A/B test on devices where the GL path
regresses, and roll back the default if needed.

---

## 8. Known gaps vs. Singularity / Firestorm

The GL pipeline is structurally complete (initialises, draws all pass
types, handles surface lifecycle, swaps engines without restart) but
the following items are deliberately out of scope for the
flip-default-to-GL milestone:

- **Mesh asset compilation in the GL backend.** `UpsertMesh` currently
  binds only the texture entry's default face. The mesh geometry
  itself isn't uploaded to a VAO. To be done by porting the mesh
  upload path from `MeshPrimRenderer.kt` into a new
  `DrawableMeshStore`.
- **Per-face material binding.** `DrawablePrimStore` binds one
  texture per prim; SL prims have up to 32 faces. Needs a per-face
  draw split that consults `TextureEntryParser.parseFull`.
- **Real prim shapes.** `DrawablePrimStore.drawOpaque` always draws
  the box VAO. Sphere / cylinder VAOs exist but the dispatcher
  doesn't choose between them yet.
- **Picking integration.** `picking/GLRayTrace` exists but isn't
  wired into `WorldViewportHost` touch handling.
- **Hover text.** Lumiya renders 3D text above objects;
  `DrawableHudStore` doesn't yet.
- **Occlusion queries.** ES 3 supports `glBeginQuery` /
  `GL_ANY_SAMPLES_PASSED_CONSERVATIVE`; we don't use them.
- **Responsive throttle.** Lumiya pauses background compute during
  touch flings; not implemented.

All of these are unblocked by this branch's plumbing — they just
need follow-up work in their respective drawable / shader files.

---

## 9. Testing

To force a backend at runtime without changing prefs, use:

```kotlin
PreferenceManager.getDefaultSharedPreferences(context)
    .edit().putString("renderer_backend", "filament").apply()
// Then re-launch WorldViewActivity, or rely on onResume's
// preferredRendererBackend() check to trigger a hot swap.
```

`RenderDiagnostics` exposes per-frame counters that the debug floater
button can capture; check for `glContextInfo` lines in the session
log to confirm the GL backend actually came up.
