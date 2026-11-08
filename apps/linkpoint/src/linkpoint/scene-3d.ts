/**
 * Linkpoint PWA - 3D Scene Manager
 */

import { Utils } from './utils';
import { Graphics3D } from './graphics-3d';
import { Camera3D } from './camera-3d';
import { Primitives3D } from './primitives-3d';
import { extractFrustum, multiplyMat4, testAABB, transformAABB, OUTSIDE, type Frustum } from './frustum';
import { intersectRayOrientedBox } from './ray-pick';
import { HEAVENLY_BODY_RADIUS, atmosphereColor, atmosphereUniforms } from './atmosphere';
import { DEFAULT_SKY, DEFAULT_WATER, dayFraction, normalizeSky, normalizeWater, skyAt, skyState, waterAt, type SkySettings, type SkyState, type WaterSettings } from './eep';
import { DETAIL_TILE_METRES, FALLBACK_LAYER_COLORS, TERRAIN_LAYERS, compositionTexture, terrainComposition, type TerrainParams } from './terrain';
import { fitHud, hudExtents, hudProjection, HUD_SIZE, type HudFit } from './hud';
import {
  WATER_WAVES, WATER_NORMAL_SCALE, DEFAULT_WATER_HEIGHT, computeSkyUniforms, computeWaterUniforms, isUnderWater, readVec3,
  createSkyDome, createStarField, createWaterPlane,
  type SkyUniforms, type WaterUniforms,
} from './sky';

function tangentsFor(shape: { vertices: number[]; indices: number[]; normals?: number[]; texCoords?: number[]; tangents?: number[] }) {
  if (shape.tangents) return shape.tangents;
  if (shape.normals && shape.texCoords) {
    return Primitives3D.calculateTangents(shape.vertices, shape.normals, shape.texCoords, shape.indices);
  }
  return undefined;
}

const UNIT_CUBE_BOUNDS = { min: [-0.5, -0.5, -0.5], max: [0.5, 0.5, 0.5] };

/** Distance of the sun light from the origin, in metres; far enough to behave as a directional light. */
const SUN_DISTANCE = 10000;

export class Scene3D extends Utils.EventEmitter {
  public graphics: Graphics3D;
  public camera: Camera3D;

  // Scene objects
  public objects: Map<string, any> = new Map();
  public lights: any[] = [];

  // Grid
  public showGrid: boolean = true;
  public gridSize: number = 256;
  public gridDivisions: number = 16;
  public environment: any = null;
  private terrainLoaded = false;
  private terrainHeights: number[] | null = null;
  private terrainSize = 0;
  private terrainMaterials: (TerrainParams & { textureNames: string[] }) | null = null;
  private terrainCompositionReady = false;

  // Sky, water and culling. Sky/water resources are created in init().
  public showSky = true;
  public showWater = true;
  public cullingEnabled = true;
  public waterHeight = DEFAULT_WATER_HEIGHT;
  public underWater = false;
  /** Objects drawn / skipped by frustum culling in the most recent frame. */
  public frameStats = { drawn: 0, culled: 0 };
  private environmentMeshesReady = false;

  private decodedTextureCount = 0;
  private pendingTextureCount = 0;
  private textureProgressState = { pending: 0, decoded: 0, total: 0, activeProgress: 100, stage: 'Idle' };

  public getTextureProgress() {
    return { ...this.textureProgressState };
  }

  public reportTextureProgress(progressData: { pending?: number; decoded?: number; total?: number; activeProgress?: number; stage?: string; textureId?: string }) {
    if (typeof progressData.decoded === 'number') this.decodedTextureCount = progressData.decoded;
    if (typeof progressData.pending === 'number') this.pendingTextureCount = progressData.pending;

    const decoded = this.decodedTextureCount;
    const pending = this.pendingTextureCount;
    const total = progressData.total ?? (decoded + pending);
    const stage = progressData.stage || (pending > 0 ? 'Decompressing' : 'Complete');
    const activeProgress = progressData.activeProgress ?? (total > 0 ? Math.round((decoded / total) * 100) : 100);

    this.textureProgressState = { pending, decoded, total, activeProgress, stage };
    this.emit('scene:texture-progress', this.textureProgressState);
    return this.textureProgressState;
  }

  /** The HUD currently overlaid on the view, if any. Its prims are objects flagged `hud` with `hudRoot` set to this id. */
  public displayedHud: { rootId: string; size: number; pan: [number, number] } | null = null;
  private skyUniforms: SkyUniforms = computeSkyUniforms(null);
  private waterUniforms: WaterUniforms = computeWaterUniforms(null);
  private skyClearColor: number[] = [0.53, 0.81, 0.92, 1];
  /** Sky, water and light values for the current time of day (EEP day cycle, or the fallback sky frame). */
  public atmosphere: { sky: SkySettings; state: SkyState; water: WaterSettings } = { sky: DEFAULT_SKY, state: skyState(DEFAULT_SKY), water: DEFAULT_WATER };
  private atmosphereBucket = -1;
  /** Wall-clock seconds, used to find the point in the region's day. Replaceable for tests. */
  public wallClock = () => Date.now() / 1000;
  /** Ambient light on objects: from the environment's ambient term when there is one. */
  private ambientColor: number[] = [0.2, 0.2, 0.2];
  private now = () => (typeof performance !== 'undefined' ? performance.now() : Date.now()) / 1000;

  constructor(graphics: Graphics3D, camera: Camera3D) {
    super();
    this.graphics = graphics;
    this.camera = camera;
  }

  /**
   * Initialize scene
   */
  async init() {
    // Create default primitives
    this.createDefaultPrimitives();
    this.createEnvironmentMeshes();

    // Create grid
    if (this.showGrid) {
      this.createGrid();
    }

    // Add default light
    this.addLight({
      type: 'directional',
      position: [100, 100, 200],
      color: [1, 1, 1],
      intensity: 1.0
    });

    this.emit('initialized');
  }

  /**
   * Create default primitive meshes
   */
  createDefaultPrimitives() {
    // Cube
    const cube = Primitives3D.createCube(1);
    this.graphics.createMesh('cube', cube.vertices, cube.indices, cube.normals, cube.texCoords, tangentsFor(cube));

    // Sphere
    const sphere = Primitives3D.createSphere(0.5, 32, 16);
    this.graphics.createMesh('sphere', sphere.vertices, sphere.indices, sphere.normals, sphere.texCoords, tangentsFor(sphere));

    // Plane
    const plane = Primitives3D.createPlane(10, 10, 10, 10);
    this.graphics.createMesh('plane', plane.vertices, plane.indices, plane.normals, plane.texCoords, tangentsFor(plane));
    const particleSprite = Primitives3D.createPlane(1, 1);
    this.graphics.createMesh('particle-sprite', particleSprite.vertices, particleSprite.indices, particleSprite.normals, particleSprite.texCoords, tangentsFor(particleSprite));

    // Cylinder
    const cylinder = Primitives3D.createCylinder(0.5, 0.5, 1, 32);
    this.graphics.createMesh('cylinder', cylinder.vertices, cylinder.indices, cylinder.normals, cylinder.texCoords, tangentsFor(cylinder));

    const prism = Primitives3D.createPrism();
    this.graphics.createMesh('prism', prism.vertices, prism.indices, prism.normals, prism.texCoords, tangentsFor(prism));

    const torus = Primitives3D.createTorus();
    this.graphics.createMesh('torus', torus.vertices, torus.indices, torus.normals, torus.texCoords, tangentsFor(torus));

    // Until LLMesh/JP2 decoding is available in WebGL, uploaded mesh and sculpt
    // assets get an unmistakable non-cube proxy rather than silently vanishing.
    const assetProxy = Primitives3D.createTorus(0.28, 0.22, 16, 8);
    this.graphics.createMesh('asset-proxy', assetProxy.vertices, assetProxy.indices, assetProxy.normals, assetProxy.texCoords, tangentsFor(assetProxy));
  }

  /** Sky dome, star field and water plane (shaders live in sky.ts). */
  createEnvironmentMeshes() {
    const dome = createSkyDome(2);
    this.graphics.createMesh('sky-dome', dome.vertices, dome.indices);
    const stars = createStarField(500);
    this.graphics.createMesh('sky-stars', stars.vertices, stars.indices);
    const water = createWaterPlane();
    this.graphics.createMesh('water-plane', water.vertices, water.indices);
    this.environmentMeshesReady = true;
  }

  /**
   * Create grid
   */
  createGrid() {
    const grid = Primitives3D.createGrid(this.gridSize, this.gridDivisions);
    this.graphics.createMesh('grid', grid.vertices, grid.indices, grid.normals, grid.texCoords, tangentsFor(grid));
  }

  addAssetMesh(assetId: string, geometry: { vertices?: number[]; indices?: number[]; normals?: number[]; texCoords?: number[]; tangents?: number[]; parts?: any[]; lods?: Record<string, any[]>; metadata?: any }) {
    const ALL_LOD_NAMES = ['high_lod', 'medium_lod', 'low_lod', 'lowest_lod'] as const;
    const lodsInput = geometry.lods && Object.keys(geometry.lods).length > 0 ? geometry.lods : null;
    const lodMeshes: Record<string, Array<{ mesh: string; materialIndex: number }>> = {};

    const registerSubmeshParts = (partsList: any[], lodPrefix: string) => {
      return partsList.map((part, index) => {
        const name = `asset:${assetId}:${lodPrefix}:${index}`;
        const skin = Array.isArray(part.joints) && Array.isArray(part.jointWeights) ? { joints: part.joints, weights: part.jointWeights } : undefined;
        const tangents = tangentsFor(part);
        try {
          this.graphics.createMesh(name, part.vertices, part.indices, part.normals, part.texCoords, tangents, skin);
          if (lodPrefix === 'high_lod' || lodPrefix === 'default') {
            try {
              this.graphics.createMesh(`asset:${assetId}:${index}`, part.vertices, part.indices, part.normals, part.texCoords, tangents, skin);
            } catch {
              // Ignore alias errors
            }
          }
          return { mesh: name, materialIndex: Number(part.materialIndex ?? index) };
        } catch (err) {
          console.warn(`[Scene3D] Failed to register LOD mesh ${name}, falling back to asset proxy:`, err);
          return { mesh: 'asset-proxy', materialIndex: Number(part.materialIndex ?? index) };
        }
      });
    };

    if (lodsInput) {
      for (const lodName of ALL_LOD_NAMES) {
        if (Array.isArray(lodsInput[lodName]) && lodsInput[lodName].length > 0) {
          lodMeshes[lodName] = registerSubmeshParts(lodsInput[lodName], lodName);
        }
      }
      if (!lodMeshes.high_lod && geometry.parts?.length) {
        lodMeshes.high_lod = registerSubmeshParts(geometry.parts, 'high_lod');
      }
      if (!lodMeshes.medium_lod) {
        lodMeshes.medium_lod = lodMeshes.high_lod || [];
      }
      if (!lodMeshes.low_lod) {
        lodMeshes.low_lod = lodMeshes.medium_lod || lodMeshes.high_lod || [];
      }
      if (!lodMeshes.lowest_lod) {
        lodMeshes.lowest_lod = lodMeshes.low_lod || lodMeshes.medium_lod || lodMeshes.high_lod || [];
      }
    } else {
      const parts = geometry.parts?.length ? geometry.parts : [geometry.vertices ? geometry : { vertices: [], indices: [] }];
      const defaultDraws = registerSubmeshParts(parts, 'high_lod');
      lodMeshes.high_lod = defaultDraws;
      lodMeshes.medium_lod = defaultDraws;
      lodMeshes.low_lod = defaultDraws;
      lodMeshes.lowest_lod = defaultDraws;
    }

    const primaryMeshes = lodMeshes.high_lod || [];
    (primaryMeshes as any).lodMeshes = lodMeshes;
    (primaryMeshes as any).lodThresholds = geometry.metadata?.lodPixelAngles || null;
    return primaryMeshes;
  }

  /** Register the faces of a generated prim volume; returns one draw per face (material index = texture-entry face). */
  addVolumeMeshes(key: string, faces: Array<{ faceIndex: number; vertices: number[]; indices: number[]; normals?: number[]; texCoords?: number[]; tangents?: number[] }>) {
    return faces.map((face) => {
      const name = `volume:${key}:${face.faceIndex}`;
      this.graphics.createMesh(name, face.vertices, face.indices, face.normals, face.texCoords, tangentsFor(face));
      return { mesh: name, materialIndex: face.faceIndex };
    });
  }

  /** Register a skinned mesh (joint indices + weights per vertex) under `name`. */
  addSkinnedMesh(name: string, geometry: { vertices: number[]; indices: number[]; normals?: number[]; texCoords?: number[]; tangents?: number[] }, skin: { joints: number[]; weights: number[] }) {
    this.graphics.createMesh(name, geometry.vertices, geometry.indices, geometry.normals, geometry.texCoords, tangentsFor(geometry), skin);
    return name;
  }

  addAssetTexture(assetId: string, width: number, height: number, rgba: Uint8Array) {
    this.decodedTextureCount++;
    if (this.pendingTextureCount > 0) this.pendingTextureCount--;
    const texName = this.graphics.createTexture(`texture:${assetId}`, width, height, rgba);
    this.reportTextureProgress({
      decoded: this.decodedTextureCount,
      pending: this.pendingTextureCount,
      activeProgress: 100,
      stage: 'Decoded',
      textureId: assetId
    });
    return texName;
  }

  removeTexture(textureId: string): boolean {
    if (!textureId) return false;
    return this.graphics.deleteTexture(textureId);
  }

  /** Replace the flat helper grid with the simulator's height field. */
  setTerrain(heights: number[], size = 256) {
    if (!Array.isArray(heights) || size < 2 || heights.length < size * size) return false;
    // A simulator region supplies one height sample per metre.  Keep every sample for the
    // normal 256x256 height field: reducing it to 128 cells moves the intervening vertices onto
    // rounded sample positions and makes the rendered ground cut through (or sit below) prims
    // whose placement was calculated from the original terrain.  255 cells still fit exactly in
    // WebGL's unsigned-short index range (256 * 256 vertices, last index 65535).
    const cells = Math.min(255, size - 1);
    const vertices: number[] = [], normals: number[] = [], texCoords: number[] = [], indices: number[] = [];
    const sample = (x: number, y: number) => Number(heights[Math.min(size - 1, y) * size + Math.min(size - 1, x)]) || 0;
    for (let y = 0; y <= cells; y++) {
      const sy = Math.round(y * (size - 1) / cells);
      for (let x = 0; x <= cells; x++) {
        const sx = Math.round(x * (size - 1) / cells);
        vertices.push(sx, sy, sample(sx, sy));
        const dx = sample(Math.min(size - 1, sx + 1), sy) - sample(Math.max(0, sx - 1), sy);
        const dy = sample(sx, Math.min(size - 1, sy + 1)) - sample(sx, Math.max(0, sy - 1));
        const length = Math.hypot(dx, dy, 2) || 1;
        normals.push(-dx / length, -dy / length, 2 / length);
        texCoords.push(sx / (size - 1), sy / (size - 1));
      }
    }
    for (let y = 0; y < cells; y++) for (let x = 0; x < cells; x++) {
      const a = y * (cells + 1) + x, b = a + 1, c = a + cells + 1, d = c + 1;
      indices.push(a, b, c, b, d, c);
    }
    this.graphics.createMesh('terrain', vertices, indices, normals, texCoords);
    this.terrainLoaded = true;
    this.terrainHeights = Array.from(heights, Number);
    this.terrainSize = size;
    this.buildTerrainComposition();
    return true;
  }

  /**
   * Texture the terrain like the official viewer: four detail textures blended by height + noise,
   * measured against per-corner start heights and ranges (SW, SE, NW, NE). `textureNames` are the
   * graphics texture names of the four layers; any not loaded yet show a fallback colour.
   */
  setTerrainMaterials(materials: TerrainParams & { textureNames: string[] }) {
    if (!materials || materials.startHeights?.length < 4 || materials.heightRanges?.length < 4) return false;
    this.terrainMaterials = materials;
    this.buildTerrainComposition();
    return true;
  }

  private buildTerrainComposition() {
    this.terrainCompositionReady = false;
    const materials = this.terrainMaterials;
    if (!materials || !this.terrainHeights || !this.terrainSize) return;
    const values = terrainComposition(this.terrainHeights, this.terrainSize, materials);
    this.graphics.createTexture('terrain:composition', this.terrainSize, this.terrainSize, compositionTexture(values, this.terrainSize));
    this.terrainCompositionReady = true;
  }

  /** True when the terrain is drawn with height-blended detail textures. */
  get terrainTextured() {
    return this.terrainLoaded && this.terrainCompositionReady;
  }

  setEnvironment(environment: any) {
    this.environment = environment || null;
    const sky = environment?.sky || environment?.currentSky || {};
    this.skyUniforms = computeSkyUniforms(sky);
    this.atmosphereBucket = -1;
    this.updateAtmosphere();
  }

  /**
   * Recompute sky, water and lighting for the current time of day. With a region day cycle the
   * sun, moon and colours follow the clock (as the official viewer does); otherwise the single
   * sky frame the environment carries is used. Cheap enough to call every frame: it only works
   * when the two-second time bucket changes.
   */
  private updateAtmosphere() {
    const environment = this.environment;
    const bucket = Math.floor(this.wallClock() / 2);
    if (bucket === this.atmosphereBucket) return;
    this.atmosphereBucket = bucket;

    let sky: SkySettings, water: WaterSettings, state: SkyState;
    const cycle = environment?.dayCycle;
    const hasCycle = cycle && (cycle.tracks?.length || Object.keys(cycle.frames || {}).length);
    if (hasCycle) {
      const fraction = dayFraction(this.wallClock(), Number(environment.dayLength), Number(environment.dayOffset) || 0);
      sky = skyAt(cycle, fraction);
      water = waterAt(cycle, fraction);
      state = skyState(sky);
    } else {
      const frame = environment?.sky || environment?.currentSky;
      sky = frame ? normalizeSky(frame) : DEFAULT_SKY;
      water = normalizeWater(environment?.water);
      const sun = Array.isArray(frame?.sunDirection) ? (frame.sunDirection.slice(0, 3).map(Number) as [number, number, number]) : undefined;
      state = skyState(sky, sun ? { sun, moon: sun.map((v: number) => -v) as [number, number, number] } : {});
    }
    this.atmosphere = { sky, state, water };

    // Light on objects: sunlight (or moonlight) after atmospheric attenuation, plus ambient.
    const light = this.lights[0];
    const direction = state.lightDirection;
    const diffuse = state.sunUp ? state.sunDiffuse : state.moonDiffuse;
    const ambient = state.sunUp ? state.sunAmbient.map((v) => Math.pow(Math.max(0, v), 0.9) * 0.57) : state.moonAmbient;
    if (light) {
      light.position = direction.map((v) => v * SUN_DISTANCE);
      light.color = diffuse.map((v) => Math.max(0, Math.min(1, v)));
    }
    this.ambientColor = ambient.map((v) => Math.max(0, Math.min(1, v)));

    // Clear colour: the sky near the zenith, tone mapped (the dome covers it, but it shows through gaps).
    const zenith = atmosphereColor(sky, state, [0, 0, 1]).map((v) => Math.pow(1 - Math.exp(-v * 1.2), 1 / 2.2));
    this.skyClearColor = [...zenith, 1];
    if (!this.underWater) this.graphics.setClearColor(this.skyClearColor);
    this.waterUniforms = computeWaterUniforms({ waterFogColor: water.fogColor }, this.waterHeight);
  }

  /** Region water level in metres (RegionHandshake WaterHeight). */
  setWaterHeight(height: number) {
    if (!Number.isFinite(height)) return false;
    this.waterHeight = height;
    this.waterUniforms = { ...this.waterUniforms, height };
    return true;
  }

  /**
   * Add object to scene
   */
  /** Show one worn HUD over the world (or none). `size` is the fraction of the view height its largest side fills. */
  setDisplayedHud(rootId: string | null, size: number = HUD_SIZE.initial, pan: [number, number] = [0, 0]) {
    this.displayedHud = rootId ? { rootId, size: Math.max(HUD_SIZE.min, Math.min(HUD_SIZE.max, size)), pan } : null;
  }

  addObject(id: string, config: any) {
    const lodMeshes = config.lodMeshes || config.meshes?.lodMeshes || null;
    const lodThresholds = config.lodThresholds || config.meshes?.lodThresholds || null;
    const object = {
      id,
      mesh: config.mesh || 'cube',
      meshes: config.meshes || null,
      lodMeshes,
      lodThresholds,
      currentLod: config.currentLod || 'high_lod',
      position: config.position || [0, 0, 0],
      rotation: config.rotation || [0, 0, 0],
      scale: config.scale || [1, 1, 1],
      color: config.color || [1, 1, 1, 1],
      material: config.material || 'basic',
      texture: config.texture,
      faces: config.faces || [],
      reflectionProbe: config.reflectionProbe || null,
      visible: config.visible !== false,
      // HUD prims are kept out of the world and drawn only by the HUD pass.
      hud: Boolean(config.hud),
      hudRoot: config.hudRoot ?? null,
      // Packed joint matrices (see skinning.ts packJointRows) for rigged meshes.
      skin: config.skin || null,
    };

    this.objects.set(id, object);
    this.emit('object_added', object);
    return object;
  }

  /**
   * Remove object from scene
   */
  removeObject(id: string) {
    const object = this.objects.get(id);
    if (object) {
      this.objects.delete(id);
      this.emit('object_removed', object);
    }
  }

  /**
   * Update object
   */
  updateObject(id: string, updates: any) {
    const object = this.objects.get(id);
    if (object) {
      if (updates.meshes) {
        updates.lodMeshes = updates.lodMeshes || updates.meshes.lodMeshes || object.lodMeshes;
        updates.lodThresholds = updates.lodThresholds || updates.meshes.lodThresholds || object.lodThresholds;
      }
      Object.assign(object, updates);
      this.emit('object_updated', object);
    }
  }

  /**
   * Add light
   */
  addLight(config: any) {
    const light = {
      id: config.id || Utils.generateUUID(),
      type: config.type || 'point',
      position: config.position || [0, 0, 0],
      color: config.color || [1, 1, 1],
      intensity: config.intensity || 1.0
    };

    this.lights.push(light);
    this.emit('light_added', light);
    return light;
  }

  /**
   * Render scene
   */
  render() {
    this.updateAtmosphere();
    this.renderMirrors();
    // Clear
    this.graphics.clear();

    // Get matrices
    const viewMatrix = this.camera.getViewMatrix();
    const projectionMatrix = this.camera.getProjectionMatrix();
    const frustum = this.cullingEnabled ? extractFrustum(multiplyMat4(projectionMatrix, viewMatrix)) : null;

    // Below the surface the sky is not visible; show the water tint instead.
    const waterActive = this.showWater && this.terrainLoaded && this.environmentMeshesReady;
    const underWater = waterActive && isUnderWater(this.camera.position[2], this.waterHeight);
    if (underWater !== this.underWater) {
      this.underWater = underWater;
      const tint = this.waterUniforms.color.map((value) => value * 0.6);
      this.graphics.setClearColor(underWater ? [...tint, 1] : this.skyClearColor);
      this.graphics.clear();
    }

    // Render grid first
    if (this.showGrid || this.terrainLoaded) {
      this.renderGrid(viewMatrix, projectionMatrix);
    }

    // Opaque geometry writes depth first. Alpha-blended faces are rendered
    // back-to-front afterwards so trees, windows and hair do not disappear as
    // insertion order changes while simulator updates stream in.
    let culled = 0;
    const visible = [...this.objects.values()].filter(object => {
      if (!object.visible || object.hud) return false;
      if (this.isCulled(object, frustum)) { culled++; return false; }
      return true;
    });
    this.frameStats = { drawn: visible.length, culled };
    const transparent = (object: any) => this.isTransparent(object);
    const distanceSquared = (object: any) => object.position.reduce((sum: number, value: number, index: number) => sum + (value - this.camera.position[index]) ** 2, 0);
    visible.filter(object => !transparent(object)).forEach(object => this.renderObject(object, viewMatrix, projectionMatrix));

    // The sky is drawn after opaque geometry at the far plane, so it only
    // shades pixels nothing else covered instead of overdrawing the screen.
    if (this.showSky && this.environmentMeshesReady && !underWater) this.renderSky(viewMatrix, projectionMatrix);
    if (waterActive && !underWater) this.renderWater(viewMatrix, projectionMatrix);

    visible.filter(transparent).sort((a, b) => distanceSquared(b) - distanceSquared(a)).forEach(object => this.renderObject(object, viewMatrix, projectionMatrix));

    // The HUD goes last, over everything, in its own orthographic view.
    this.renderHud();
  }

  /**
   * Whether a face is drawn alpha-blended: an explicit material mode wins; with
   * none, a translucent colour or a texture with transparent pixels blends, as
   * viewers do for legacy faces.
   */
  private faceBlendMode(object: any, face: any): 0 | 1 | 2 {
    const mode = face?.pbr?.alphaMode;
    if (mode === 'MASK' || mode === 1) return 1;
    if (mode === 'BLEND' || mode === 2) return 2;
    if (mode !== undefined && mode !== null) return 0;
    const colourAlpha = Number((face?.color ?? object.color)?.[3] ?? 1);
    const texture = object.mirrorTexture || face?.texture || object.texture;
    return colourAlpha < 0.99 || this.graphics.textureHasAlpha?.(texture) ? 2 : 0;
  }

  /** True when any face of an object is drawn blended, so it belongs in the back-to-front pass. */
  private isTransparent(object: any) {
    const faces = object.faces?.length ? object.faces : [undefined];
    return faces.some((face: any) => this.faceBlendMode(object, face) === 2);
  }

  /** Placement of the displayed HUD: the fit over its prims' extents and the prims themselves. */
  private hudSetup(): { fit: HudFit; prims: Array<{ object: any; local: Float32Array }> } | null {
    const displayed = this.displayedHud;
    if (!displayed) return null;
    const prims = [...this.objects.values()]
      .filter((object) => object.hud && object.hudRoot === displayed.rootId && object.visible !== false)
      .map((object) => ({ object, local: this.calculateModelMatrix(object.position, object.rotation, object.scale) }));
    if (!prims.length) return null;
    // Measure each prim's real mesh bounds (the unit cube when unknown) through the same matrix it is drawn with.
    const boxes = prims.map(({ object, local }) => {
      const bounds = this.objectLocalBounds(object) || UNIT_CUBE_BOUNDS;
      return { local, bounds };
    });
    const extents = hudExtents(boxes.map(({ local, bounds }) => {
      // Express the transformed bounds as a prim so the shared extents code can measure it.
      const world = transformAABB(local, bounds.min, bounds.max);
      return {
        position: [(world.min[0] + world.max[0]) / 2, (world.min[1] + world.max[1]) / 2, (world.min[2] + world.max[2]) / 2],
        rotation: [0, 0, 0, 1],
        scale: [world.max[0] - world.min[0], world.max[1] - world.min[1], world.max[2] - world.min[2]],
      };
    }));
    return { fit: fitHud(extents, displayed.size, displayed.pan), prims };
  }

  private hudAspect() {
    return Number((this.camera as any).aspect) || 1;
  }

  /** Draw the displayed HUD over the finished world frame. */
  renderHud() {
    const setup = this.hudSetup();
    if (!setup) return;
    const { fit, prims } = setup;
    const identity = new Float32Array([1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1]);
    const projection = hudProjection(this.hudAspect());
    this.graphics.clearDepth?.();
    // Opaque prims first, then blended ones from far to near (screen -z is away from the viewer).
    const depthOf = ({ local }: { local: Float32Array }) => fit.matrix[2] * local[12] + fit.matrix[6] * local[13] + fit.matrix[10] * local[14] + fit.matrix[14];
    const ordered = [
      ...prims.filter(({ object }) => !this.isTransparent(object)),
      ...prims.filter(({ object }) => this.isTransparent(object)).sort((a, b) => depthOf(a) - depthOf(b)),
    ];
    for (const { object, local } of ordered) {
      // HUDs are not lit by the sun: they are drawn full-bright, as Lumiya does (no Windlight lighting).
      this.renderObject(object, identity, projection, { model: multiplyMat4(fit.matrix, local), fullBright: true });
    }
  }

  /** Find the HUD prim under a screen point, nearest the viewer first. Distance is in view units. */
  pickHud(screenX: number, screenY: number, width: number, height: number) {
    const setup = this.hudSetup();
    if (!setup || !(width > 0) || !(height > 0)) return null;
    const x = ((2 * screenX) / width - 1) * this.hudAspect();
    const y = 1 - (2 * screenY) / height;
    const ray = { origin: [x, y, 50], direction: [0, 0, -1] };
    let best: { id: string; distance: number; point: number[] } | null = null;
    for (const { object, local } of setup.prims) {
      const bounds = this.objectLocalBounds(object) || UNIT_CUBE_BOUNDS;
      const distance = intersectRayOrientedBox(ray, multiplyMat4(setup.fit.matrix, local), bounds.min, bounds.max);
      if (distance === null || (best && distance >= best.distance)) continue;
      best = { id: object.id, distance, point: [x, y, 50 - distance] };
    }
    return best;
  }

  /** Atmospheric sky dome (sun, moon, haze glow) plus optional stars, pinned to the far plane. */
  private renderSky(viewMatrix: Float32Array, projectionMatrix: Float32Array) {
    const skyView = new Float32Array(viewMatrix);
    skyView[12] = 0; skyView[13] = 0; skyView[14] = 0;
    const { sky, state } = this.atmosphere;
    this.graphics.drawMesh('sky-dome', 'sky', {
      uSkyViewMatrix: skyView,
      uProjectionMatrix: projectionMatrix,
      ...atmosphereUniforms(sky, state),
      uSunDir: new Float32Array(state.sunDirection),
      uMoonDir: new Float32Array(state.moonDirection),
      uSunRadius: HEAVENLY_BODY_RADIUS * Math.max(0.2, Math.min(sky.sunScale, 4)),
      uMoonRadius: HEAVENLY_BODY_RADIUS * Math.max(0.2, Math.min(sky.moonScale, 4)),
      uMoonBrightness: Math.max(0, sky.moonBrightness),
      uMoonUp: state.moonUp ? 1 : 0,
    }, { depthWrite: false, cullFace: false });
    // EEP star brightness is 0..250 (0 by day); the fallback frames carry 0..1.
    const stars = this.environment?.dayCycle ? Math.max(0, Math.min(1, sky.starBrightness / 250)) : this.skyUniforms.starBrightness;
    if (stars > 0) {
      this.graphics.drawMesh('sky-stars', 'stars', {
        uSkyViewMatrix: skyView,
        uProjectionMatrix: projectionMatrix,
        uStarColor: new Float32Array([1, 1, 1, stars]),
      }, { mode: 'points', depthWrite: false, blend: true, cullFace: false });
    }
  }

  /** Animated four-wave water surface at the region water level. */
  private renderWater(viewMatrix: Float32Array, projectionMatrix: Float32Array) {
    const light = this.lights[0] || { position: [100, 100, 200], color: [1, 1, 1] };
    this.graphics.drawMesh('water-plane', 'water', {
      uViewMatrix: viewMatrix,
      uProjectionMatrix: projectionMatrix,
      uWaterHeight: this.waterHeight,
      uCameraPos: new Float32Array(this.camera.position),
      ...atmosphereUniforms(this.atmosphere.sky, this.atmosphere.state),
      uFogColor: new Float32Array(this.atmosphere.water.fogColor),
      uFogDensity: this.atmosphere.water.fogDensity,
      uFresnelScale: this.atmosphere.water.fresnelScale,
      uFresnelOffset: this.atmosphere.water.fresnelOffset,
      uLightDir: new Float32Array(this.atmosphere.state.lightDirection),
      uLightColor: new Float32Array(light.color),
      uSurfaceAmbient: new Float32Array(this.ambientColor),
      // Wrap so float precision does not degrade the phase over long sessions.
      uTime: this.now() % 1000,
      uPixelAngle: this.pixelAngle(),
      uNormalScale: WATER_NORMAL_SCALE,
      uFrequency: new Float32Array(WATER_WAVES.frequency),
      uPhase: new Float32Array(WATER_WAVES.phase),
      uAmplitude: new Float32Array(WATER_WAVES.amplitude),
      uDirection: new Float32Array(WATER_WAVES.direction),
    }, { depthWrite: false, blend: true, cullFace: false });
  }

  /** Approximate angle subtended by one screen pixel, used to filter sub-pixel water ripples. */
  private pixelAngle() {
    const height = (this.graphics as any).canvas?.height || 720;
    const fov = Number(this.camera.fov) || 60;
    return (2 * Math.tan((fov * Math.PI) / 360)) / height;
  }

  /**
   * Compute apparent screen pixel area (in sq px) for an object's bounding sphere footprint.
   *
   * Formula:
   * radiusPx = (boundingRadius * screenHeightPx) / (2 * distance * tan(fov / 2))
   * areaPx = PI * radiusPx^2
   */
  public calculateScreenPixelArea(
    boundingRadius: number,
    distanceMeters: number,
    fovRad: number = Math.PI / 3,
    screenHeightPx: number = 720
  ): number {
    const safeRadius = boundingRadius > 0 ? boundingRadius : 0.5;
    const safeDistance = distanceMeters > 0.001 ? distanceMeters : 0.001;
    const safeFov = Math.max(0.01, Math.min(Math.PI - 0.01, fovRad));
    const tanHalfFov = Math.tan(safeFov / 2);
    const safeTan = tanHalfFov > 0.0001 ? tanHalfFov : 0.57735;

    const radiusPx = (safeRadius * screenHeightPx) / (2 * safeDistance * safeTan);
    return Math.PI * radiusPx * radiusPx;
  }

  /**
   * Select active LOD level for an object based on camera distance, bounding sphere, screen pixel area, and hysteresis.
   */
  public selectObjectLod(object: any): 'high_lod' | 'medium_lod' | 'low_lod' | 'lowest_lod' {
    const lodMeshes = object.lodMeshes || object.meshes?.lodMeshes;
    if (!lodMeshes) return 'high_lod';

    const dx = object.position[0] - this.camera.position[0];
    const dy = object.position[1] - this.camera.position[1];
    const dz = object.position[2] - this.camera.position[2];
    const distance = Math.hypot(dx, dy, dz);

    const bounds = this.objectLocalBounds(object);
    let localRadius = 0.5;
    if (bounds) {
      const bx = bounds.max[0] - bounds.min[0];
      const by = bounds.max[1] - bounds.min[1];
      const bz = bounds.max[2] - bounds.min[2];
      localRadius = 0.5 * Math.hypot(bx, by, bz);
    }
    const scale = Array.isArray(object.scale) ? object.scale : [1, 1, 1];
    const maxScale = Math.max(Math.abs(scale[0]), Math.abs(scale[1]), Math.abs(scale[2])) || 1;
    const worldRadius = localRadius * maxScale;

    const canvasHeight = (this.graphics as any).canvas?.height || 720;
    const fovRad = ((Number(this.camera.fov) || 60) * Math.PI) / 180;

    const pixelArea = this.calculateScreenPixelArea(worldRadius, distance, fovRad, canvasHeight);

    let tHigh = 8000;
    let tMed = 2000;
    let tLow = 500;

    const customAngles = object.lodThresholds || object.meshes?.lodThresholds;
    if (customAngles) {
      if (typeof customAngles.high_lod === 'number') {
        const hD = customAngles.high_lod;
        const mD = customAngles.medium_lod ?? hD / 2;
        const lD = customAngles.low_lod ?? mD / 2;
        tHigh = hD <= 2000 ? Math.PI * (hD / 2) ** 2 : hD;
        tMed = mD <= 2000 ? Math.PI * (mD / 2) ** 2 : mD;
        tLow = lD <= 2000 ? Math.PI * (lD / 2) ** 2 : lD;
      }
    }

    const HYSTERESIS_MARGIN = 0.15;
    const currentLod: 'high_lod' | 'medium_lod' | 'low_lod' | 'lowest_lod' = object.currentLod || 'high_lod';

    let selected: 'high_lod' | 'medium_lod' | 'low_lod' | 'lowest_lod' = 'lowest_lod';

    const highUpgrade = tHigh * (1 + HYSTERESIS_MARGIN);
    const highDowngrade = tHigh * (1 - HYSTERESIS_MARGIN);

    if (currentLod === 'high_lod') {
      if (pixelArea >= highDowngrade) selected = 'high_lod';
    } else {
      if (pixelArea >= highUpgrade) selected = 'high_lod';
    }

    if (selected === 'lowest_lod' && pixelArea >= tMed * (1 - HYSTERESIS_MARGIN)) {
      const medUpgrade = tMed * (1 + HYSTERESIS_MARGIN);
      const medDowngrade = tMed * (1 - HYSTERESIS_MARGIN);

      if (currentLod === 'medium_lod') {
        if (pixelArea >= medDowngrade) selected = 'medium_lod';
      } else if (currentLod === 'high_lod') {
        if (pixelArea < highDowngrade && pixelArea >= medDowngrade) selected = 'medium_lod';
      } else {
        if (pixelArea >= medUpgrade) selected = 'medium_lod';
      }
    }

    if (selected === 'lowest_lod' && pixelArea >= tLow * (1 - HYSTERESIS_MARGIN)) {
      const lowUpgrade = tLow * (1 + HYSTERESIS_MARGIN);
      const lowDowngrade = tLow * (1 - HYSTERESIS_MARGIN);

      if (currentLod === 'low_lod') {
        if (pixelArea >= lowDowngrade) selected = 'low_lod';
      } else if (currentLod === 'high_lod' || currentLod === 'medium_lod') {
        if (pixelArea >= lowDowngrade) selected = 'low_lod';
      } else {
        if (pixelArea >= lowUpgrade) selected = 'low_lod';
      }
    }

    object.currentLod = selected;
    return selected;
  }

  /** Meshes drawn for an object, selecting active LOD level when multi-LOD buffers are available. */
  private objectDraws(object: any) {
    const lodMeshes = object.lodMeshes || object.meshes?.lodMeshes;
    if (lodMeshes) {
      const selectedLod = this.selectObjectLod(object);
      return lodMeshes[selectedLod] || lodMeshes.high_lod || object.meshes;
    }
    return object.meshes?.length ? object.meshes : [{ mesh: object.mesh, materialIndex: 0 }];
  }

  /** Union of the local-space bounds of every mesh the object draws, or null if any is unknown. */
  private objectLocalBounds(object: any): { min: number[]; max: number[] } | null {
    if (typeof this.graphics.getMeshBounds !== 'function') return null;
    // A posed rig can extend beyond its bind-pose bounds, so never cull or box-pick it by them.
    if (object.skin) return null;
    const min = [Infinity, Infinity, Infinity];
    const max = [-Infinity, -Infinity, -Infinity];
    const lodMeshes = object.lodMeshes || object.meshes?.lodMeshes;
    const draws = lodMeshes?.high_lod || (object.meshes?.length ? object.meshes : [{ mesh: object.mesh, materialIndex: 0 }]);
    for (const draw of draws) {
      const bounds = this.graphics.getMeshBounds(draw.mesh);
      if (!bounds) return null;
      for (let axis = 0; axis < 3; axis++) {
        min[axis] = Math.min(min[axis], bounds.min[axis]);
        max[axis] = Math.max(max[axis], bounds.max[axis]);
      }
    }
    return min.every(Number.isFinite) ? { min, max } : null;
  }

  /** True when the object's world bounds are entirely outside the frustum. Unknown bounds are never culled. */
  private isCulled(object: any, frustum: Frustum | null) {
    if (!frustum) return false;
    const local = this.objectLocalBounds(object);
    if (!local) return false;
    const model = this.calculateModelMatrix(object.position, object.rotation, object.scale);
    const world = transformAABB(model, local.min, local.max);
    return testAABB(frustum, world.min, world.max) === OUTSIDE;
  }

  /**
   * Find the nearest visible object under a screen point using each object's
   * oriented bounding box. Terrain and water are not pickable yet. Distance is
   * in world metres from the camera.
   */
  pick(screenX: number, screenY: number, width: number, height: number) {
    if (typeof this.camera.screenToWorldRay !== 'function') return null;
    const ray = this.camera.screenToWorldRay(screenX, screenY, width, height);
    let best: { id: string; distance: number; point: number[] } | null = null;
    for (const object of this.objects.values()) {
      if (!object.visible || object.hud) continue;
      // Meshes without known bounds are treated as the unit cube prims are scaled from.
      const local = this.objectLocalBounds(object) || UNIT_CUBE_BOUNDS;
      const model = this.calculateModelMatrix(object.position, object.rotation, object.scale);
      const distance = intersectRayOrientedBox(ray, model, local.min, local.max);
      if (distance === null || (best && distance >= best.distance)) continue;
      best = { id: object.id, distance, point: ray.origin.map((value: number, axis: number) => value + ray.direction[axis] * distance) };
    }
    return best;
  }

  private renderMirrors() {
    const mirrors = [...this.objects.values()].filter(object => object.visible && object.reflectionProbe?.mirror);
    for (const mirror of mirrors.slice(0, 2)) {
      const targetName = this.graphics.createRenderTarget(`mirror:${mirror.id}`, 256, 256);
      if (!this.graphics.beginRenderTarget(targetName)) continue;
      const [rx, ry, rz] = mirror.rotation;
      const sx = Math.sin(rx), cx = Math.cos(rx), sy = Math.sin(ry), cy = Math.cos(ry), sz = Math.sin(rz), cz = Math.cos(rz);
      const normal = [cz * sy * cx + sz * sx, sz * sy * cx - cz * sx, cy * cx];
      const reflect = (point: number[]) => {
        const offset = point.map((value, index) => value - mirror.position[index]);
        const distance = offset[0] * normal[0] + offset[1] * normal[1] + offset[2] * normal[2];
        return point.map((value, index) => value - 2 * distance * normal[index]);
      };
      const eye = reflect(this.camera.position);
      const focus = reflect(this.camera.mode === 'orbit' ? this.camera.orbitTarget : this.camera.target);
      const view = this.camera.mat4LookAt(eye, focus, [0, 0, 1]);
      if (this.showGrid) this.renderGrid(view, this.camera.getProjectionMatrix());
      const mirrorFrustum = this.cullingEnabled ? extractFrustum(multiplyMat4(this.camera.getProjectionMatrix(), view)) : null;
      for (const object of this.objects.values()) {
        if (object.visible && !object.hud && object !== mirror && !object.reflectionProbe?.mirror && !this.isCulled(object, mirrorFrustum)) this.renderObject(object, view, this.camera.getProjectionMatrix());
      }
      this.graphics.endRenderTarget();
      mirror.mirrorTexture = targetName;
    }
  }

  /**
   * Render grid
   */
  renderGrid(viewMatrix: Float32Array, projectionMatrix: Float32Array) {
    const modelMatrix = this.mat4Identity();
    const normalMatrix = this.mat3FromMat4(modelMatrix);

    const light = this.lights[0] || { position: [100, 100, 200], color: [1, 1, 1] };

    if (this.terrainTextured && this.terrainMaterials) {
      const names = this.terrainMaterials.textureNames;
      const use = [0, 1, 2, 3].map((i) => (this.graphics.hasTexture(names[i]) ? 1 : 0));
      const detail: Record<string, any> = {};
      for (let i = 0; i < TERRAIN_LAYERS; i++) {
        detail[`uDetail${i}Name`] = names[i];
        detail[`uFallback${i}`] = new Float32Array(FALLBACK_LAYER_COLORS[i]);
      }
      this.graphics.drawMesh('terrain', 'terrain', {
        uModelMatrix: modelMatrix, uViewMatrix: viewMatrix, uProjectionMatrix: projectionMatrix, uNormalMatrix: normalMatrix,
        uLightPos: new Float32Array(light.position), uLightColor: new Float32Array(light.color), uAmbientColor: new Float32Array(this.ambientColor),
        uCompositionName: 'terrain:composition', uDetailUse: new Float32Array(use),
        uTileScale: this.terrainSize > 1 ? 256 / DETAIL_TILE_METRES : 16,
        ...detail,
      });
      return;
    }
    this.graphics.drawMesh(this.terrainLoaded ? 'terrain' : 'grid', 'basic', {
      uModelMatrix: modelMatrix,
      uViewMatrix: viewMatrix,
      uProjectionMatrix: projectionMatrix,
      uNormalMatrix: normalMatrix,
      uLightPos: new Float32Array(light.position),
      uLightColor: new Float32Array(light.color),
      uAmbientColor: new Float32Array(this.ambientColor),
      uColor: new Float32Array([0.5, 0.5, 0.5, 0.3]),
      uUseTexture: false,
      // Uniform values persist between WebGL draws. Reset every shader option
      // used by objects so the grid/terrain cannot inherit the previous
      // object's alpha mask, full-bright, or PBR material on the next frame.
      uTexTransform: new Float32Array([1, 1, 0, 0]),
      uTexRotation: 0,
      uFullBright: false,
      uCameraPos: new Float32Array(this.camera.position),
      uMetallic: 0,
      uRoughness: 1,
      uEmissive: new Float32Array([0, 0, 0]),
      uOcclusionFactor: 1.0,
      uUseMetallicRoughnessTexture: false,
      uUseNormalTexture: false,
      uUseEmissiveTexture: false,
      uAlphaMode: 0,
      uAlphaCutoff: 0.5,
      uDoubleSided: false,
    });
  }

  /**
   * Render object
   */
  renderObject(object: any, viewMatrix: Float32Array, projectionMatrix: Float32Array, options: { model?: Float32Array; fullBright?: boolean } = {}) {
    const skinned = Boolean(object.skin) && typeof this.graphics.isSkinnedMesh === 'function';
    // WorldViewer has already resolved the correct scale: worn rigged attachments use unit scale,
    // while Animesh keeps its simulator scale. Do not discard that distinction here.
    const modelMatrix = options.model || this.calculateModelMatrix(
      object.position,
      object.rotation,
      object.scale
    );

    const normalMatrix = this.mat3FromMat4(modelMatrix);
    const light = this.lights[0] || { position: [100, 100, 200], color: [1, 1, 1] };

    const draws = object.meshes?.length ? object.meshes : [{ mesh: object.mesh, materialIndex: 0 }];
    for (const draw of draws) {
      const face = object.faces?.[draw.materialIndex];
      const pbr = face?.pbr || {};
      const alphaMode = this.faceBlendMode(object, face);
      const drawSkinned = skinned && this.graphics.isSkinnedMesh(draw.mesh);
      this.graphics.drawMesh(draw.mesh, drawSkinned ? 'skinned' : object.material, {
        ...(drawSkinned ? { uJointRows: object.skin } : null),
        uModelMatrix: modelMatrix,
        uViewMatrix: viewMatrix,
        uProjectionMatrix: projectionMatrix,
        uNormalMatrix: normalMatrix,
        uLightPos: new Float32Array(light.position),
        uLightColor: new Float32Array(light.color),
        uAmbientColor: new Float32Array(this.ambientColor),
        uColor: new Float32Array(face?.color || object.color),
        uUseTexture: Boolean(object.mirrorTexture || face?.texture || object.texture),
        uTextureName: object.mirrorTexture || face?.texture || object.texture,
        uTexTransform: new Float32Array([...(face?.repeat || [1, 1]), ...(face?.offset || [0, 0])]),
        uTexRotation: face?.rotation || 0,
        uFullBright: Boolean(options.fullBright || face?.fullBright),
        uCameraPos: new Float32Array(this.camera.position),
        uMetallic: pbr.metallic ?? 0,
        uRoughness: pbr.roughness ?? 1,
        uEmissive: new Float32Array(pbr.emissive || [0, 0, 0]),
        uOcclusionFactor: pbr.occlusionFactor ?? pbr.occlusion ?? 1.0,
        uMetallicRoughnessTextureName: pbr.metallicRoughnessTexture,
        uNormalTextureName: pbr.normalTexture,
        uEmissiveTextureName: pbr.emissiveTexture,
        uUseMetallicRoughnessTexture: Boolean(pbr.metallicRoughnessTexture),
        uUseNormalTexture: Boolean(pbr.normalTexture),
        uUseEmissiveTexture: Boolean(pbr.emissiveTexture),
        uAlphaMode: alphaMode,
        uAlphaCutoff: pbr.alphaCutoff ?? 0.5,
        uDoubleSided: Boolean(pbr.doubleSided),
      });
    }
  }

  /**
   * Calculate model matrix from transform
   */
  private calculateModelMatrix(position: number[], rotation: number[], scale: number[]) {
    const matrix = this.mat4Identity();

    // The rotation helpers pre-multiply, so build S, then R, then T. Applying
    // translation first would rotate the object's position around the origin.
    this.mat4Scale(matrix, scale);
    if (rotation[0] !== 0) this.mat4RotateX(matrix, rotation[0]);
    if (rotation[1] !== 0) this.mat4RotateY(matrix, rotation[1]);
    if (rotation[2] !== 0) this.mat4RotateZ(matrix, rotation[2]);
    this.mat4Translate(matrix, position);

    return matrix;
  }

  /**
   * Matrix operations
   */
  private mat4Identity(): Float32Array {
    return new Float32Array([
      1, 0, 0, 0,
      0, 1, 0, 0,
      0, 0, 1, 0,
      0, 0, 0, 1
    ]);
  }

  private mat4Translate(m: Float32Array, v: number[]) {
    m[12] += v[0];
    m[13] += v[1];
    m[14] += v[2];
  }

  private mat4RotateX(m: Float32Array, angle: number) {
    const c = Math.cos(angle);
    const s = Math.sin(angle);
    for (let column = 0; column < 4; column++) {
      const offset = column * 4;
      const y = m[offset + 1], z = m[offset + 2];
      m[offset + 1] = y * c - z * s;
      m[offset + 2] = y * s + z * c;
    }
  }

  private mat4RotateY(m: Float32Array, angle: number) {
    const c = Math.cos(angle);
    const s = Math.sin(angle);
    for (let column = 0; column < 4; column++) {
      const offset = column * 4;
      const x = m[offset], z = m[offset + 2];
      m[offset] = x * c + z * s;
      m[offset + 2] = z * c - x * s;
    }
  }

  private mat4RotateZ(m: Float32Array, angle: number) {
    const c = Math.cos(angle);
    const s = Math.sin(angle);
    for (let column = 0; column < 4; column++) {
      const offset = column * 4;
      const x = m[offset], y = m[offset + 1];
      m[offset] = x * c - y * s;
      m[offset + 1] = x * s + y * c;
    }
  }

  private mat4Scale(m: Float32Array, v: number[]) {
    m[0] *= v[0];
    m[1] *= v[0];
    m[2] *= v[0];
    m[3] *= v[0];
    m[4] *= v[1];
    m[5] *= v[1];
    m[6] *= v[1];
    m[7] *= v[1];
    m[8] *= v[2];
    m[9] *= v[2];
    m[10] *= v[2];
    m[11] *= v[2];
  }

  private mat3FromMat4(m4: Float32Array): Float32Array {
    // Normals transform by the inverse transpose. Using the model matrix
    // directly visibly breaks lighting on the heavily non-uniform scales used
    // by prims and avatar parts.
    const a00 = m4[0], a01 = m4[1], a02 = m4[2];
    const a10 = m4[4], a11 = m4[5], a12 = m4[6];
    const a20 = m4[8], a21 = m4[9], a22 = m4[10];
    const b01 = a22 * a11 - a12 * a21;
    const b11 = -a22 * a10 + a12 * a20;
    const b21 = a21 * a10 - a11 * a20;
    const determinant = a00 * b01 + a01 * b11 + a02 * b21;
    if (!determinant) return new Float32Array([1, 0, 0, 0, 1, 0, 0, 0, 1]);
    const inverse = 1 / determinant;
    return new Float32Array([
      b01 * inverse, (-a22 * a01 + a02 * a21) * inverse, (a12 * a01 - a02 * a11) * inverse,
      b11 * inverse, (a22 * a00 - a02 * a20) * inverse, (-a12 * a00 + a02 * a10) * inverse,
      b21 * inverse, (-a21 * a00 + a01 * a20) * inverse, (a11 * a00 - a01 * a10) * inverse,
    ]);
  }
}
