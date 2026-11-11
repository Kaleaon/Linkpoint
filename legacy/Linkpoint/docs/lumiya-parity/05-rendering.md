# Segment 05 — Rendering & Scene Management

**Priority:** Medium-High. Lumiya runs OpenGL ES 2.0/3.0 with octree-style
spatial culling; Linkpoint targets Filament. The protocol/scene model
should be backend-agnostic — only the draw layer differs.

Reference: `lumiya_decompiled_source/com/lumiyaviewer/lumiya/render/`.

---

## 1. Pipeline shape Lumiya uses

| Subpackage | Role | Linkpoint analogue |
|---|---|---|
| `render/spatial/` | `SpatialIndex` (octree), `DrawList`, `FrustrumPlanes` | `render/scene/` (verify spatial index exists) |
| `render/avatar/` | `DrawableAvatar` (rigged-mesh skinning), `DrawableAvatarStub` (loading), `DrawableHUD` | `avatar/`, `render/` |
| `render/drawable/` | `DrawableObject`, `DrawableStore` (pool) | `render/scene/` |
| `render/prims/`, `render/mesh/` | prim profiles/paths/flexibles/sculpts; rigged-mesh draw | `render/prims/` |
| `render/terrain/` | `DrawableTerrainPatch` consuming `LayerData` heightmaps | `render/terrain/` |
| `render/windlight/` | `WindlightSky` HDR atmosphere | `render/environment/` |
| `render/glres/` | GLTexture, VBO buffers, `TextureMemoryTracker` (64 MB default) | TextureManager / asset loader |
| `render/shaders/` | GLSL | `assets/shaders/` |
| `render/picking/` | ray-triangle, HUD picking | TBD |
| `render/cardboard/` | stereo path | `xr/` (Linkpoint already has this dir) |

---

## 2. The HUD pass (worklist item #5)

Lumiya's `DrawableHUD` is a separate draw pass that runs after the main
world pass with a dedicated projection matrix:

- HUD attachments are positioned in a 1×1 normalized space anchored to
  attachment points (top-left, top-right, …).
- They render with depth-test off and depth-write off.
- Picking against HUDs uses a separate ray (screen-space), not the world
  ray.

Linkpoint's renderer flag this as "not implemented." Implementation order:

1. Identify HUD attachments in `AvatarManager` — items at attachment
   points 31–38 (`HUD_CENTER_2`, `HUD_TOP_RIGHT`, etc.).
2. Build a per-HUD draw list keyed off `attachmentPoint`.
3. Render after the main world pass with overridden state.
4. Wire `render/picking/` to do screen-space picking against HUD draw list
   first, fall back to world ray.

---

## 3. Spatial index

Without spatial culling, every visible-prim test is O(scene size). The
debug report shows only 6 objects in the test sim — which masks the issue.
On a 1000-prim sim, lack of culling tanks the frame rate.

| ID | Item |
|---|---|
| L05-A | Verify Linkpoint has an octree or grid-based `SpatialIndex` and that the renderer queries it per frame |
| L05-B | If not, port Lumiya's structure: `SpatialIndex` with `DrawList` + `FrustrumPlanes` |
| L05-C | Frustum culling: build per-frame from camera projection × view matrix |
| L05-D | Distance culling: respect `drawDistance` from settings (Lumiya has it in `GlobalOptions`) |

---

## 4. Texture memory tracking

`render/glres/TextureMemoryTracker` enforces a 64 MB default budget,
configurable in settings. The debug report shows:

```
Texture Memory Tracker:
  Live Textures: 0
  Native Heap: 0 B   Mmapped: 0 B   GPU: 0 B   Total: 0 B
```

…despite 24 textures decoded successfully. Either the tracker isn't
plumbed to the renderer, or Filament's texture lifecycle doesn't notify
back. This is segment 06's territory, but the rendering side of the fix
is:

| ID | Item |
|---|---|
| L05-E | When uploading a texture to Filament, call `TextureMemoryTracker.add(uuid, sizeBytes)` |
| L05-F | When Filament releases a texture, call `TextureMemoryTracker.remove(uuid)` |
| L05-G | When the budget is exceeded, evict the LRU texture (downgrade to its lower-discard-level cache copy if available) |
| L05-H | Surface the live tracker numbers to the debug report |

---

## 5. Avatar baking and BOM

| ID | Item | Notes |
|---|---|---|
| L05-I | Verify `avatar/baking/` produces composite head/upper/lower/skirt/hair/eyes textures from wearable layers | matches Lumiya `slproto/avatar/baker/` |
| L05-J | Bake on appearance change, push via `AgentSetAppearance` (reliable) | uses cached-texture cap if available |
| L05-K | Bakes-On-Mesh (BOM) support — the `bom/` package exists in Linkpoint; verify it's wired | newer than 3.4.2 |

---

## 6. Concrete work items

| ID | Item |
|---|---|
| L05-A through L05-K above | |
| L05-L | Implement HUD pass (worklist item #5) |
| L05-M | Add a render-side test that verifies frustum culling on a synthetic 1000-prim scene |
| L05-N | Add a debug overlay (toggle in settings) showing draw-call count, prim count, texture memory, FPS |

---

## 7. Cross-references

- Segment 06 — Textures & Assets (JPEG2000 stability, the live-tracker bug)
- Segment 02 — Cellular & Background (texture/mesh fetch should pause when
  network quality is poor)
