/**
 * Linkpoint PWA - 3D Scene Manager
 */

import { Utils } from './utils';
import { Graphics3D } from './graphics-3d';
import { Camera3D } from './camera-3d';
import { Primitives3D } from './primitives-3d';
import { extractFrustum, multiplyMat4, testAABB, transformAABB, OUTSIDE, type Frustum } from './frustum';
import { TieredSimdCuller } from './simd-culler';
import { intersectRayOrientedBox, intersectRayTriangle, invertMat4, type Ray } from './ray-pick';
import { HEAVENLY_BODY_RADIUS, atmosphereColor, atmosphereUniforms } from './atmosphere';

export function computeST(uv: ArrayLike<number>, faceConfig?: any): [number, number, number] {
  const u = Number(uv[0]) || 0;
  const v = Number(uv[1]) || 0;
  const repeat = faceConfig?.repeat || [1, 1];
  const offset = faceConfig?.offset || [0, 0];
  const rotation = faceConfig?.rotation || 0;

  const centeredU = u - 0.5;
  const centeredV = v - 0.5;
  const cos = Math.cos(rotation);
  const sin = Math.sin(rotation);

  const rotatedU = cos * centeredU - sin * centeredV + 0.5;
  const rotatedV = sin * centeredU + cos * centeredV + 0.5;

  const s = rotatedU * repeat[0] + offset[0];
  const t = rotatedV * repeat[1] + offset[1];

  return [s, t, 0];
}
import { DEFAULT_SKY, DEFAULT_WATER, dayFraction, normalizeSky, normalizeWater, skyAt, skyState, waterAt, type SkySettings, type SkyState, type WaterSettings } from './eep';
import { DETAIL_TILE_METRES, FALLBACK_LAYER_COLORS, TERRAIN_LAYERS, compositionTexture, terrainComposition, type TerrainParams } from './terrain';
import { fitHud, hudExtents, hudProjection, HUD_SIZE, type HudFit } from './hud';
import {
  WATER_WAVES, WATER_NORMAL_SCALE, DEFAULT_WATER_HEIGHT, computeSkyUniforms, computeWaterUniforms, isUnderWater, readVec3,
  createSkyDome, createStarField, createWaterPlane,
  type SkyUniforms, type WaterUniforms,
} from './sky';

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
  public culler = new TieredSimdCuller();
  private frameCount = 0;
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
    this.graphics.createMesh('cube', cube.vertices, cube.indices, cube.normals, cube.texCoords);

    // Sphere
    const sphere = Primitives3D.createSphere(0.5, 32, 16);
    this.graphics.createMesh('sphere', sphere.vertices, sphere.indices, sphere.normals, sphere.texCoords);

    // Plane
    const plane = Primitives3D.createPlane(10, 10, 10, 10);
    this.graphics.createMesh('plane', plane.vertices, plane.indices, plane.normals, plane.texCoords);
    const particleSprite = Primitives3D.createPlane(1, 1);
    this.graphics.createMesh('particle-sprite', particleSprite.vertices, particleSprite.indices, particleSprite.normals, particleSprite.texCoords);

    // Cylinder
    const cylinder = Primitives3D.createCylinder(0.5, 0.5, 1, 32);
    this.graphics.createMesh('cylinder', cylinder.vertices, cylinder.indices, cylinder.normals, cylinder.texCoords);

    const prism = Primitives3D.createPrism();
    this.graphics.createMesh('prism', prism.vertices, prism.indices, prism.normals, prism.texCoords);

    const torus = Primitives3D.createTorus();
    this.graphics.createMesh('torus', torus.vertices, torus.indices, torus.normals, torus.texCoords);

    // Until LLMesh/JP2 decoding is available in WebGL, uploaded mesh and sculpt
    // assets get an unmistakable non-cube proxy rather than silently vanishing.
    const assetProxy = Primitives3D.createTorus(0.28, 0.22, 16, 8);
    this.graphics.createMesh('asset-proxy', assetProxy.vertices, assetProxy.indices, assetProxy.normals, assetProxy.texCoords);
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
    this.graphics.createMesh('grid', grid.vertices, grid.indices, grid.normals, grid.texCoords);
  }

  addAssetMesh(assetId: string, geometry: { vertices: number[]; indices: number[]; normals?: number[]; texCoords?: number[]; parts?: any[] }) {
    const parts = geometry.parts?.length ? geometry.parts : [geometry];
    return parts.map((part, index) => {
      const name = `asset:${assetId}:${index}`;
      const skin = Array.isArray(part.joints) && Array.isArray(part.jointWeights) ? { joints: part.joints, weights: part.jointWeights } : undefined;
      this.graphics.createMesh(name, part.vertices, part.indices, part.normals, part.texCoords, undefined, skin);
      return { mesh: name, materialIndex: Number(part.materialIndex ?? index) };
    });
  }

  /** Register the faces of a generated prim volume; returns one draw per face (material index = texture-entry face). */
  addVolumeMeshes(key: string, faces: Array<{ faceIndex: number; vertices: number[]; indices: number[]; normals?: number[]; texCoords?: number[] }>) {
    return faces.map((face) => {
      const name = `volume:${key}:${face.faceIndex}`;
      this.graphics.createMesh(name, face.vertices, face.indices, face.normals, face.texCoords);
      return { mesh: name, materialIndex: face.faceIndex };
    });
  }

  /** Register a skinned mesh (joint indices + weights per vertex) under `name`. */
  addSkinnedMesh(name: string, geometry: { vertices: number[]; indices: number[]; normals?: number[]; texCoords?: number[] }, skin: { joints: number[]; weights: number[] }) {
    this.graphics.createMesh(name, geometry.vertices, geometry.indices, geometry.normals, geometry.texCoords, undefined, skin);
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

  /** Replace the flat helper grid with the simulator's height field. */
  setTerrain(heights: number[], size = 256) {
    if (!Array.isArray(heights) || size < 2 || heights.length < size * size) return false;
    const cells = Math.min(128, size - 1);
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
    const object = {
      id,
      mesh: config.mesh || 'cube',
      meshes: config.meshes || null,
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
    this.culler.upsertObject(
      id,
      object.position,
      object.rotation,
      object.scale,
      this.objectLocalBounds(object),
      (p, r, s) => this.calculateModelMatrix(p, r, s)
    );
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
      this.culler.removeObject(id);
      this.emit('object_removed', object);
    }
  }

  /**
   * Update object
   */
  updateObject(id: string, updates: any) {
    const object = this.objects.get(id);
    if (object) {
      Object.assign(object, updates);
      this.culler.upsertObject(
        id,
        object.position,
        object.rotation,
        object.scale,
        this.objectLocalBounds(object),
        (p, r, s) => this.calculateModelMatrix(p, r, s)
      );
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

    this.frameCount++;
    if (this.cullingEnabled && frustum) {
      for (const object of this.objects.values()) {
        if (!object.hud && object.visible !== false) {
          const local = this.objectLocalBounds(object);
          this.culler.upsertObject(
            object.id,
            object.position,
            object.rotation,
            object.scale,
            local,
            (p, r, s) => this.calculateModelMatrix(p, r, s)
          );
        }
      }
      this.culler.cull(
        this.camera.position as [number, number, number],
        frustum,
        this.frameCount,
        (p, r, s) => this.calculateModelMatrix(p, r, s)
      );
    }

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
    let best: { id: string; distance: number; point: number[]; face?: number; uv?: number[]; st?: number[] } | null = null;
    for (const { object, local } of setup.prims) {
      const bounds = this.objectLocalBounds(object) || UNIT_CUBE_BOUNDS;
      const model = multiplyMat4(setup.fit.matrix, local);
      const hit = this.pickObjectRay(ray, object, model, bounds.min, bounds.max);
      if (hit === null || (best && hit.distance >= best.distance)) continue;
      best = { id: object.id, distance: hit.distance, point: hit.point, face: hit.face, uv: hit.uv, st: hit.st };
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

  /** Meshes drawn for an object, falling back to its single mesh. */
  private objectDraws(object: any) {
    return object.meshes?.length ? object.meshes : [{ mesh: object.mesh, materialIndex: 0 }];
  }

  /** Union of the local-space bounds of every mesh the object draws, or null if any is unknown. */
  private objectLocalBounds(object: any): { min: number[]; max: number[] } | null {
    if (typeof this.graphics.getMeshBounds !== 'function') return null;
    // A posed rig can extend beyond its bind-pose bounds, so never cull or box-pick it by them.
    if (object.skin) return null;
    const min = [Infinity, Infinity, Infinity];
    const max = [-Infinity, -Infinity, -Infinity];
    for (const draw of this.objectDraws(object)) {
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
    if (!frustum || !this.cullingEnabled) return false;
    const local = this.objectLocalBounds(object);
    if (!local) return false;

    const cullerObj = this.culler.getObject(object.id);
    if (cullerObj) {
      return !cullerObj.visible;
    }

    const model = this.calculateModelMatrix(object.position, object.rotation, object.scale);
    const world = transformAABB(model, local.min, local.max);
    return testAABB(frustum, world.min, world.max) === OUTSIDE;
  }

  /**
   * Two-stage ray pick test against an object given its model matrix and bounding box.
   * Performs broad-phase box culling first, then narrow-phase triangle intersection
   * if vertex buffers are available.
   */
  private pickObjectRay(
    ray: Ray,
    object: any,
    modelMatrix: ArrayLike<number>,
    localMin: ArrayLike<number>,
    localMax: ArrayLike<number>,
  ): { distance: number; point: number[]; face: number; uv: [number, number, number]; st: [number, number, number] } | null {
    const boxDist = intersectRayOrientedBox(ray, modelMatrix, localMin, localMax);
    if (boxDist === null) return null;

    const draws = this.objectDraws(object);
    let hasGeometry = false;

    if (typeof (this.graphics as any).getMeshGeometry === 'function') {
      for (const draw of draws) {
        if ((this.graphics as any).getMeshGeometry(draw.mesh)) {
          hasGeometry = true;
          break;
        }
      }
    }

    // Fallback to bounding box intersection when CPU vertex buffers are absent.
    if (!hasGeometry) {
      const point = [
        ray.origin[0] + ray.direction[0] * boxDist,
        ray.origin[1] + ray.direction[1] * boxDist,
        ray.origin[2] + ray.direction[2] * boxDist,
      ];
      return {
        distance: boxDist,
        point,
        face: 0,
        uv: [0, 0, 0],
        st: [0, 0, 0],
      };
    }

    // Narrow phase: transform ray into object local space and test mesh triangles.
    const inverse = invertMat4(modelMatrix);
    if (!inverse) return null;

    const ox = ray.origin[0], oy = ray.origin[1], oz = ray.origin[2];
    const w = inverse[3] * ox + inverse[7] * oy + inverse[11] * oz + inverse[15];
    const invW = w ? 1 / w : 1;
    const localOrigin = [
      (inverse[0] * ox + inverse[4] * oy + inverse[8] * oz + inverse[12]) * invW,
      (inverse[1] * ox + inverse[5] * oy + inverse[9] * oz + inverse[13]) * invW,
      (inverse[2] * ox + inverse[6] * oy + inverse[10] * oz + inverse[14]) * invW,
    ];

    const dx = ray.direction[0], dy = ray.direction[1], dz = ray.direction[2];
    const localDirection = [
      inverse[0] * dx + inverse[4] * dy + inverse[8] * dz,
      inverse[1] * dx + inverse[5] * dy + inverse[9] * dz,
      inverse[2] * dx + inverse[6] * dy + inverse[10] * dz,
    ];

    const localRay: Ray = { origin: localOrigin, direction: localDirection };

    let bestHit: { distance: number; face: number; uv: [number, number]; point: number[] } | null = null;

    for (const draw of draws) {
      const geom = (this.graphics as any).getMeshGeometry(draw.mesh);
      if (!geom || !geom.vertices || !geom.indices) continue;

      const vertices = geom.vertices;
      const indices = geom.indices;
      const texCoords = geom.texCoords;
      const faceIndex = Number(draw.materialIndex ?? 0);

      for (let i = 0; i + 2 < indices.length; i += 3) {
        const i0 = indices[i];
        const i1 = indices[i + 1];
        const i2 = indices[i + 2];

        const v0 = [vertices[3 * i0], vertices[3 * i0 + 1], vertices[3 * i0 + 2]];
        const v1 = [vertices[3 * i1], vertices[3 * i1 + 1], vertices[3 * i1 + 2]];
        const v2 = [vertices[3 * i2], vertices[3 * i2 + 1], vertices[3 * i2 + 2]];

        const uv0 = texCoords ? [texCoords[2 * i0], texCoords[2 * i0 + 1]] : undefined;
        const uv1 = texCoords ? [texCoords[2 * i1], texCoords[2 * i1 + 1]] : undefined;
        const uv2 = texCoords ? [texCoords[2 * i2], texCoords[2 * i2 + 1]] : undefined;

        const hit = intersectRayTriangle(localRay, v0, v1, v2, uv0, uv1, uv2);
        if (hit && hit.t > 0 && (!bestHit || hit.t < bestHit.distance)) {
          const worldPoint = [
            ray.origin[0] + ray.direction[0] * hit.t,
            ray.origin[1] + ray.direction[1] * hit.t,
            ray.origin[2] + ray.direction[2] * hit.t,
          ];
          bestHit = {
            distance: hit.t,
            face: faceIndex,
            uv: hit.uv || [0, 0],
            point: worldPoint,
          };
        }
      }
    }

    if (!bestHit) return null;

    const faceConfig = object.faces?.[bestHit.face];
    const rawUV: [number, number, number] = [bestHit.uv[0], bestHit.uv[1], 0];
    const st = computeST(rawUV, faceConfig);

    return {
      distance: bestHit.distance,
      point: bestHit.point,
      face: bestHit.face,
      uv: rawUV,
      st,
    };
  }

  /**
   * Find the nearest visible object under a screen point using two-stage picking
   * (broad-phase box culling followed by narrow-phase ray-triangle intersection).
   */
  pick(screenX: number, screenY: number, width: number, height: number) {
    if (typeof this.camera.screenToWorldRay !== 'function') return null;
    const ray = this.camera.screenToWorldRay(screenX, screenY, width, height);
    let best: { id: string; distance: number; point: number[]; face?: number; uv?: number[]; st?: number[] } | null = null;
    for (const object of this.objects.values()) {
      if (!object.visible || object.hud) continue;
      // Meshes without known bounds are treated as the unit cube prims are scaled from.
      const local = this.objectLocalBounds(object) || UNIT_CUBE_BOUNDS;
      const model = this.calculateModelMatrix(object.position, object.rotation, object.scale);
      const hit = this.pickObjectRay(ray, object, model, local.min, local.max);
      if (hit === null || (best && hit.distance >= best.distance)) continue;
      best = { id: object.id, distance: hit.distance, point: hit.point, face: hit.face, uv: hit.uv, st: hit.st };
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
    // Rigged meshes are authored in avatar space: the viewer ignores the object's prim scale for them.
    const modelMatrix = options.model || this.calculateModelMatrix(
      object.position,
      object.rotation,
      skinned ? [1, 1, 1] : object.scale
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

    // Translate
    this.mat4Translate(matrix, position);

    // Rotate
    if (rotation[0] !== 0) this.mat4RotateX(matrix, rotation[0]);
    if (rotation[1] !== 0) this.mat4RotateY(matrix, rotation[1]);
    if (rotation[2] !== 0) this.mat4RotateZ(matrix, rotation[2]);

    // Scale
    this.mat4Scale(matrix, scale);

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
    const m1 = m[1], m2 = m[2];
    const m5 = m[5], m6 = m[6];
    const m9 = m[9], m10 = m[10];

    m[1] = m1 * c + m2 * s;
    m[2] = m2 * c - m1 * s;
    m[5] = m5 * c + m6 * s;
    m[6] = m6 * c - m5 * s;
    m[9] = m9 * c + m10 * s;
    m[10] = m10 * c - m9 * s;
  }

  private mat4RotateY(m: Float32Array, angle: number) {
    const c = Math.cos(angle);
    const s = Math.sin(angle);
    const m0 = m[0], m2 = m[2];
    const m4 = m[4], m6 = m[6];
    const m8 = m[8], m10 = m[10];

    m[0] = m0 * c - m2 * s;
    m[2] = m0 * s + m2 * c;
    m[4] = m4 * c - m6 * s;
    m[6] = m4 * s + m6 * c;
    m[8] = m8 * c - m10 * s;
    m[10] = m8 * s + m10 * c;
  }

  private mat4RotateZ(m: Float32Array, angle: number) {
    const c = Math.cos(angle);
    const s = Math.sin(angle);
    const m0 = m[0], m1 = m[1];
    const m4 = m[4], m5 = m[5];
    const m8 = m[8], m9 = m[9];

    m[0] = m0 * c + m1 * s;
    m[1] = m1 * c - m0 * s;
    m[4] = m4 * c + m5 * s;
    m[5] = m5 * c - m4 * s;
    m[8] = m8 * c + m9 * s;
    m[9] = m9 * c - m8 * s;
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
