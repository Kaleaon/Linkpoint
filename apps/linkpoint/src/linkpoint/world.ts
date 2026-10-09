/**
 * Linkpoint PWA - World Viewer Module
 */

import { Utils } from './utils';
import { Graphics3D } from './graphics-3d';
import { Camera3D } from './camera-3d';
import { Scene3D } from './scene-3d';
import { slBridge } from './sl-bridge';
import { CameraControls } from './camera-controls';
import { estimatedSunHour, windlightEnvironment } from './windlight';
import { AvatarSkeleton, jointPositionOverrides, skinMatrices, type MeshSkin } from './avatar-skeleton';
import { parseAnimation, type JointPose } from './avatar-animation';
import { packJointRows } from './skinning';
import { generateVolume, initVolumeWasm, volumeKey, volumeParamsFrom, type VolumeFace } from './sl-volume';
import { AvatarAnimator, bundledAnimationLoader } from './avatar-animator';
import { BODY_PARTS, bodyPartRows, bodyPartSkin, bodyPartVertexSkin, loadBodyParts, type BodyPartGeometry } from './avatar-body';
import { HUD_POINTS, HUD_SIZE, isHudPoint, type HudInfo } from './hud';
import { ParticleEngine } from './particles';
import { CoordinateNormalizer } from './coordinate-normalizer';
import { TextureCache } from './texture-cache';

export class WorldViewer extends Utils.EventEmitter {
  /**
   * Live simulator scene stream is active when protocol or bridge is connected.
   */
  public get liveSceneSupported() {
    return this.protocol.connected;
  }
  public protocol: any;
  public canvas: HTMLCanvasElement | null = null;
  public graphics3d: Graphics3D | null = null;
  public camera3d: Camera3D | null = null;
  public scene3d: Scene3D | null = null;
  private animationId: number | null = null;
  private cameraControls: CameraControls | null = null;
  private lastMovement = '';
  private resizeAttached = false;
  private readonly handleResize = () => this.resizeCanvas();
  public use3D: boolean = true;
  public interactionMode: 'navigate' | 'interact' = 'navigate';
  private dragVelocityThreshold = 0.05;
  private dragDisplacementThreshold = 3;

  public region: any = null;
  public objects: any[] = [];
  public nearbyUsers: any[] = [];
  public avatarPosition: [number, number, number] | null = null;
  public environment: any = null;
  public simSunHour: number | null = null;
  public terrain: { size: number; heights: number[] } | null = null;
  public selectedObject: any = null;
  /** The worn HUD shown over the view, if any. Its size is a fraction of the view height. */
  public displayedHud: { id: string; size: number } | null = null;
  private hudSignature = '';
  private sceneObjects = new Map<string, any>();
  private localObjectIds = new Map<number, string>();
  private decodedAssets = new Map<string, any>();
  private decodedTextures = new TextureCache<any>({
    capacity: 100,
    onEvict: (asset, key) => this.handleTextureEviction(asset, key),
  });
  private decodedMaterials = new Map<string, any>();
  private particles = new ParticleEngine();
  private renderedParticles = new Set<string>();

  public getDataStatus() {
    if (!this.protocol.connected) return 'Disconnected';
    return this.objects.length > 0
      ? `Live simulator scene: ${this.objects.length} objects loaded`
      : 'Live simulator scene: streaming from grid…';
  }

  constructor(protocolManager: any) {
    super();
    this.protocol = protocolManager;
    this.protocol.on('connected', (reply: any) => {
      if (reply?.world_data) this.applyWorldData(reply.world_data);
      this.region = {
        ...this.region,
        name: reply.sim_name || reply.region_name || null,
        x: CoordinateNormalizer.normalizeRegionTileCoordinate(reply.region_x),
        y: CoordinateNormalizer.normalizeRegionTileCoordinate(reply.region_y),
      };
      this.emit('region_changed', this.region);
      void this.loadScene();
    });
    this.protocol.on('disconnected', () => {
      this.region = null;
      this.avatarPosition = null;
      this.nearbyUsers = [];
      this.environment = null;
      this.simSunHour = null;
      this.terrain = null;
      this.terrainMaterials = null;
      this.selectedObject = null;
      this.displayedHud = null;
      this.hudSignature = '';
      this.sceneObjects.clear();
      this.particles.clear();
      this.renderedParticles.clear();
      this.avatarBakes.clear();
      this.localObjectIds.clear();
      this.objects = [];
      this.emit('region_changed', null);
      this.emit('nearby_changed', []);
      this.emit('objects_changed', []);
      this.emit('huds_changed', []);
      this.emit('hud_display_changed', null);
      this.emit('selection_changed', null);
    });
    this.protocol.on('RegionHandshake', (data: any) => {
      this.region = {
        id: data.regionID || data.region_id,
        name: data.regionName || data.region_name || data.name || 'Unknown region',
        x: CoordinateNormalizer.normalizeRegionTileCoordinate(data.regionX ?? data.region_x ?? 0),
        y: CoordinateNormalizer.normalizeRegionTileCoordinate(data.regionY ?? data.region_y ?? 0),
      };
      this.emit('region_changed', { ...this.region });
      this.updateLocationDisplay();
    });
    this.protocol.on('ObjectUpdate', (data: any) => {
      const updates = Array.isArray(data) ? data : data?.objects || [data];
      for (const update of updates.filter(Boolean)) {
        const id = update.id || update.objectId || update.full_id;
        if (id) this.upsertSceneObject({ ...update, id });
      }
    });
    this.protocol.on('CoarseLocationUpdate', (data: any) => this.updateCoarseLocations(data));
    this.protocol.on('CoarseAvatarUpdate', (data: any) => {
      if (!data?.id || data.id === this.protocol.agentId) return;
      const position = Array.isArray(data.position) ? data.position.map(Number) : null;
      const distance = position && this.avatarPosition
        ? Math.hypot(position[0] - this.avatarPosition[0], position[1] - this.avatarPosition[1], position[2] - this.avatarPosition[2])
        : null;
      const next = { ...data, position, distance };
      const index = this.nearbyUsers.findIndex((user) => user.id === data.id);
      if (index >= 0) this.nearbyUsers = this.nearbyUsers.map((user, itemIndex) => itemIndex === index ? next : user);
      else this.nearbyUsers = [...this.nearbyUsers, next];
      this.emit('nearby_changed', this.nearbyUsers.map((user) => ({ ...user })));
    });
    this.protocol.on('ParcelProperties', (data: any) => {
      const raw = data?.ParcelData || data?.parcelData || data;
      const parcel = Array.isArray(raw) ? raw[0] : raw;
      if (!parcel || typeof parcel !== 'object') return;
      const ownerId = parcel.ownerId || parcel.OwnerID?.toString?.() || parcel.ownerID?.toString?.() || null;
      const groupId = parcel.groupId || parcel.GroupID?.toString?.() || parcel.groupID?.toString?.() || null;
      const normalizedParcel = {
        id: parcel.id ?? parcel.LocalID ?? parcel.LocalId ?? null,
        name: parcel.name || parcel.Name || '',
        description: parcel.description || parcel.Desc || parcel.desc || '',
        area: Number(parcel.area ?? parcel.Area ?? 0),
        ownerId: ownerId && ownerId !== '00000000-0000-0000-0000-000000000000' ? ownerId : null,
        groupId: groupId && groupId !== '00000000-0000-0000-0000-000000000000' ? groupId : null,
        maxPrims: Number(parcel.maxPrims ?? parcel.MaxPrims ?? 0),
        totalPrims: Number(parcel.totalPrims ?? parcel.TotalPrims ?? 0),
        musicUrl: parcel.musicUrl || parcel.MusicURL || '',
        mediaUrl: parcel.mediaUrl || parcel.MediaURL || '',
        parcelFlags: Number(parcel.parcelFlags ?? parcel.ParcelFlags ?? parcel.Flags ?? parcel.flags ?? 0),
        ...parcel,
      };
      this.region = { ...this.region, parcel: normalizedParcel };
      this.emit('parcel_changed', { ...normalizedParcel });
      this.emit('region_changed', { ...this.region });
    });
    this.protocol.on('scene:object-add', (object: any) => {
      this.upsertSceneObject(object);
      this.followAvatar(object);
    });
    this.protocol.on('scene:object-update', (object: any) => {
      this.upsertSceneObject(object);
      this.followAvatar(object);
    });
    this.protocol.on('scene:object-remove', (object: any) => this.removeSceneObject(object));
    this.protocol.on('scene:asset-ready', (asset: any) => this.applyAsset(asset));
    this.protocol.on('scene:animations', (data: any) => {
      if (data?.id && Array.isArray(data.animations)) this.animator.setAnimations(String(data.id), data.animations);
    });
    this.protocol.on('scene:texture-ready', (asset: any) => this.applyTexture(asset));
    this.protocol.on('scene:texture-progress', (data: any) => {
      const progress = this.scene3d?.reportTextureProgress(data) || data;
      this.emit('scene:texture-progress', progress);
    });
    this.protocol.on('scene:material-ready', (asset: any) => this.applyMaterial(asset));
    this.protocol.on('scene:world-data', (data: any) => this.applyWorldData(data));
    this.protocol.on('scene:environment', (data: any) => this.applyWorldData({ environment: data }));
    this.protocol.on('scene:terrain', (data: any) => this.applyWorldData({ terrain: data }));
    const handleSunHour = (data: any) => {
      const hour = typeof data === 'number' ? data : data?.sunHour;
      if (typeof hour === 'number' && Number.isFinite(hour)) {
        this.simSunHour = ((hour % 1.0) + 1.0) % 1.0;
        this.applyEnvironment();
      }
    };
    this.protocol.on('scene:sun-hour-update', handleSunHour);
    this.protocol.on('sun-hour-update', handleSunHour);
    slBridge.on('sun-hour-update', (data: any) => {
      if (!this.protocol?.connected) {
        handleSunHour(data);
      }
    });
    this.protocol.on('avatar_presence', (data: any) => this.handleAvatarPresence(data));
    this.protocol.on('AgentMovementComplete', (data: any) => this.handleAgentMovement(data));
    slBridge.on('avatar_presence', (data: any) => {
      // Only process bridge event directly if protocol is not connected
      if (!this.protocol?.connected) {
        this.handleAvatarPresence(data);
      }
    });
  }

  private applyWorldData(data: any) {
    if (!data) return;
    if (data.region) {
      this.region = { ...(this.region || {}), ...data.region };
      this.emit('region_changed', { ...this.region });
    }
    if (data.environment) {
      this.environment = data.environment;
      this.applyEnvironment();
      this.emit('environment_changed', data.environment);
    }
    if (data.terrainMaterials) {
      this.terrainMaterials = data.terrainMaterials;
      this.applyTerrainMaterials();
    }
    if (data.terrain?.heights && Number(data.terrain.size) > 1) {
      this.terrain = { size: Number(data.terrain.size), heights: Array.from(data.terrain.heights, Number) };
      this.scene3d?.setTerrain(this.terrain.heights, this.terrain.size);
      this.emit('terrain_changed', this.terrain);
    }
  }

  private terrainMaterials: any = null;

  /** Hand the region's terrain textures, blend ranges and water height to the scene. */
  private applyTerrainMaterials() {
    const m = this.terrainMaterials;
    if (!m || !this.scene3d) return;
    if (Number.isFinite(m.waterHeight)) this.scene3d.setWaterHeight(m.waterHeight);
    if (Array.isArray(m.startHeights) && Array.isArray(m.heightRanges)) {
      this.scene3d.setTerrainMaterials({
        textureNames: (m.textureIds || []).map((id: string | null) => (id ? `texture:${id}` : '')),
        startHeights: m.startHeights.map(Number), heightRanges: m.heightRanges.map(Number),
        origin: [Number(m.origin?.[0]) || 0, Number(m.origin?.[1]) || 0],
      });
    }
  }

  /** Generated prim geometry by shape key, and which scene it has been uploaded to. */
  private volumeFaces = new Map<string, VolumeFace[]>();
  private volumeDraws = new Map<string, Array<{ mesh: string; materialIndex: number }>>();
  private volumeScene: unknown = null;

  /**
   * Real Second Life prim geometry (profile swept along a path, with cut, hollow, twist, taper,
   * shear, skew...) for an ordinary prim, one mesh per texture-entry face. Null for meshes, sculpts
   * and shapes the generator cannot build (those keep the closest basic shape).
   */
  private volumeMeshesFor(object: any): Array<{ mesh: string; materialIndex: number }> | null {
    if (!this.scene3d || object.avatar || object.assetId || object.assetKind) return null;
    const params = volumeParamsFrom(object.shapeParams);
    if (!params) return null;
    const key = volumeKey(params);
    if (this.volumeScene !== this.scene3d) { this.volumeDraws.clear(); this.volumeScene = this.scene3d; }
    let draws = this.volumeDraws.get(key);
    if (draws) return draws;
    let faces = this.volumeFaces.get(key);
    if (!faces) {
      try { faces = generateVolume(params); } catch (error) { console.warn('[WorldViewer] prim geometry failed:', error); faces = []; }
      this.volumeFaces.set(key, faces);
    }
    if (!faces.length || typeof (this.scene3d as any).addVolumeMeshes !== 'function') return null;
    draws = (this.scene3d as any).addVolumeMeshes(key, faces) as Array<{ mesh: string; materialIndex: number }>;
    this.volumeDraws.set(key, draws);
    return draws;
  }

  private skeleton: AvatarSkeleton | null = null;
  private animator = new AvatarAnimator(this.animationLoader());
  /** Bundled animations first, then (for custom/uploaded ones) the simulator's asset service. */
  private animationLoader() {
    const bundled = bundledAnimationLoader();
    return async (id: string) => {
      const local = await bundled(id);
      if (local) return local;
      if (typeof this.protocol?.fetchAnimation !== 'function') return null;
      try {
        return parseAnimation(await this.protocol.fetchAnimation(id));
      } catch (error) {
        console.warn(`[WorldViewer] animation ${id} unavailable:`, error);
        return null;
      }
    };
  }

  /** Avatars without any announced animation stand (the viewer's default idle). */
  private static readonly STAND_ANIMATION = '2408fe9e-df1d-1d7d-f4ff-1384fa7b350f';
  private bodyParts: Map<string, BodyPartGeometry> | null = null;
  private bodyLoad: Promise<void> | null = null;
  private bodyMeshesReadyFor: unknown = null;
  private bodySkins = new Map<string, ReturnType<typeof bodyPartSkin>>();
  /** Official-viewer style last-known-good baked texture per avatar/body layer. */
  private avatarBakes = new Map<string, string>();
  /** Objects whose skin currently holds an animated pose (restored to rest when their animation ends). */
  private posedObjects = new Set<string>();

  /**
   * Joint matrices for a rigged mesh asset, including the joint position overrides (Bento /
   * alternate bind) requested by every mesh worn on the same avatar.
   * Null for unrigged meshes.
   */
  private skinRowsFor(assetId: string, pose?: Map<string, JointPose>, subject?: string): Float32Array | null {
    const key = String(assetId);
    const skin = (this.decodedAssets.get(key) || this.decodedAssets.get(key.toLowerCase()))?.skin as (MeshSkin & { pelvisOffset?: number | number[] | null }) | null | undefined;
    let rows: Float32Array | null = null;
    if (skin?.jointNames?.length) {
      this.skeleton ||= new AvatarSkeleton();
      const deformation = subject ? this.avatarDeformation(subject) : {
        overrides: jointPositionOverrides(this.skeleton, skin),
        pelvisOffset: typeof skin.pelvisOffset === 'number' ? skin.pelvisOffset : 0,
      };
      const world = this.skeleton.worldMatrices(pose, deformation.overrides, [0, 0, deformation.pelvisOffset]);
      const maxJoints = (this.scene3d as any)?.graphics?.maxJoints || 110;
      rows = packJointRows(skinMatrices(this.skeleton, { ...skin, pelvisOffset: undefined }, world), maxJoints);
    }
    return rows;
  }

  /**
   * Lumiya rebuilds one avatar skeleton from every worn rigged mesh. Joint translations therefore
   * reshape the body and all attachments together; applying each mesh's alternate bind only to
   * itself makes separately-authored clothes diverge and appear stretched or exploded.
   */
  private avatarDeformation(subject: string): { overrides: Map<string, [number, number, number]>; pelvisOffset: number } {
    this.skeleton ||= new AvatarSkeleton();
    const overrides = new Map<string, [number, number, number]>();
    let pelvisOffset = 0;
    for (const object of this.sceneObjects.values()) {
      if (!object.assetId || this.animationSubject(object) !== subject) continue;
      const key = String(object.assetId);
      const skin = (this.decodedAssets.get(key) || this.decodedAssets.get(key.toLowerCase()))?.skin as MeshSkin | undefined;
      if (!skin) continue;
      // MeshJointTranslations.ApplyJointTranslations sums the pelvis offsets and lets the later
      // attachment win when two meshes override the same joint.
      if (typeof skin.pelvisOffset === 'number' && Number.isFinite(skin.pelvisOffset)) pelvisOffset += skin.pelvisOffset;
      for (const [joint, position] of jointPositionOverrides(this.skeleton, skin)) overrides.set(joint, position);
    }
    return { overrides, pelvisOffset };
  }

  private avatarWorldMatrices(subject: string, pose = this.animator.pose(subject)) {
    this.skeleton ||= new AvatarSkeleton();
    const deformation = this.avatarDeformation(subject);
    return this.skeleton.worldMatrices(pose, deformation.overrides, [0, 0, deformation.pelvisOffset]);
  }

  /** The avatar a rigged attachment is worn by, or the object itself (an animated object is its own subject). */
  private animationSubject(object: any): string {
    let current = object;
    for (let depth = 0; depth < 16 && current; depth++) {
      if (current.avatar) return current.id;
      const parentId = this.localObjectIds.get(Number(current.parentId));
      current = parentId ? this.sceneObjects.get(parentId) : null;
    }
    return object.id;
  }

  private reapplyAvatarSubject(subject: string) {
    for (const object of this.sceneObjects.values()) {
      const objectSubject = object.avatar ? object.id : this.animationSubject(object);
      if (objectSubject === subject) this.applySceneObject(object);
    }
  }

  /** Re-pose every rigged mesh whose avatar / animated object is running animations. Called once per frame. */
  private updateAnimatedSkins() {
    if (!this.scene3d) return;
    for (const object of this.sceneObjects.values()) {
      const assetKey = String(object.assetId || '');
      if (object.avatar || !assetKey || !(this.decodedAssets.get(assetKey) || this.decodedAssets.get(assetKey.toLowerCase()))?.skin) continue;
      const subject = this.animationSubject(object);
      const animating = this.animator.isAnimating(subject);
      if (!animating && !this.posedObjects.has(object.id)) continue;
      const rows = this.skinRowsFor(object.assetId, animating ? this.animator.pose(subject) : undefined, subject);
      if (animating) this.posedObjects.add(object.id); else this.posedObjects.delete(object.id);
      if (rows) this.scene3d.updateObject(object.id, { skin: rows });
    }
  }

  /** Anchor transform calculations to the avatar's feet origin [x, y, z - height / 2]. */
  private avatarRootTransform(avatar: any): { position: number[]; rotation: number[] } {
    const transform = this.worldTransform(avatar);
    const scale = Array.isArray(avatar?.scale) ? avatar.scale : [1, 1, 1];
    const height = Array.isArray(scale) && scale[2] > 0.5 ? scale[2] : 1.9;
    return {
      position: [transform.position[0], transform.position[1], transform.position[2] - height / 2],
      rotation: transform.rotation,
    };
  }

  /** Rigged meshes move with their avatar (not with the attachment offset the simulator reports). */
  private riggedTransform(object: any): { position: number[]; rotation: number[] } {
    let current = object;
    for (let depth = 0; depth < 16 && current; depth++) {
      if (current.avatar) return this.avatarRootTransform(current);
      const parentId = this.localObjectIds.get(Number(current.parentId));
      current = parentId ? this.sceneObjects.get(parentId) : null;
    }
    return this.worldTransform(object);
  }

  private sameId(a: any, b: any): boolean {
    return Boolean(a && b && String(a).toLowerCase() === String(b).toLowerCase());
  }

  private applyAsset(asset: any) {
    if (!asset?.assetId || !asset.geometry) return;
    const assetIdStr = String(asset.assetId);
    this.decodedAssets.set(assetIdStr, asset.geometry);
    this.decodedAssets.set(assetIdStr.toLowerCase(), asset.geometry);
    const meshes = this.scene3d?.addAssetMesh(assetIdStr, asset.geometry);
    const fallbackMeshes = asset.geometry.parts?.length
      ? asset.geometry.parts.map((part: any, index: number) => ({ mesh: `asset:${assetIdStr}:${index}`, materialIndex: part.materialIndex ?? index }))
      : [{ mesh: `asset:${assetIdStr}:0`, materialIndex: 0 }];
    const resolvedMeshes = meshes || fallbackMeshes;
    const affectedSubjects = new Set<string>();
    for (const object of this.sceneObjects.values()) {
      if (!this.sameId(object.assetId, assetIdStr)) continue;
      object.decodedMeshes = resolvedMeshes;
      affectedSubjects.add(this.animationSubject(object));
    }
    // One newly decoded attachment can alter the shared skeleton used by all clothing and the body.
    // Reapply the complete avatar, matching Lumiya's shapeParamsUpdate skeleton rebuild.
    for (const subject of affectedSubjects) this.reapplyAvatarSubject(subject);
  }

  private handleTextureEviction(asset: any, key: string) {
    if (!key) return;
    const keyStr = String(key);
    if (this.scene3d) {
      this.scene3d.removeTexture(keyStr);
    }
    for (const object of this.sceneObjects.values()) {
      let modified = false;
      if (this.sameId(object.textureId, keyStr)) {
        object.decodedTexture = undefined;
        modified = true;
      }
      const usesEvicted = object.faceTextures?.some((face: any) =>
        [face.textureId, ...this.overrideTextureIds(face), ...this.materialTextureIds(face.materialId)]
          .some((id: any) => this.sameId(id, keyStr))
      );
      if (usesEvicted) {
        object.decodedFaceTextures = (object.faceTextures || []).map((face: any) => this.resolveFace(face));
        modified = true;
      }
      if (modified) {
        this.applySceneObject(object);
      }
    }
  }

  private applyTexture(asset: any) {
    if (!asset?.assetId || !asset.rgba) return;
    const assetIdStr = String(asset.assetId);
    this.decodedTextures.set(assetIdStr, asset);
    this.decodedTextures.set(assetIdStr.toLowerCase(), asset);
    if (!this.scene3d) return;
    const binary = atob(asset.rgba);
    const rgba = Uint8Array.from(binary, character => character.charCodeAt(0));
    const texture = this.scene3d.addAssetTexture(assetIdStr, asset.width, asset.height, rgba);
    for (const object of this.sceneObjects.values()) {
      const usedByFace = object.faceTextures?.some((face: any) =>
        [face.textureId, ...this.overrideTextureIds(face), ...this.materialTextureIds(face.materialId)]
          .some((id: any) => this.sameId(id, assetIdStr)));
      if (!this.sameId(object.textureId, assetIdStr) && !usedByFace) continue;
      if (this.sameId(object.textureId, assetIdStr)) object.decodedTexture = texture;
      object.decodedFaceTextures = (object.faceTextures || []).map((face: any) => this.resolveFace(face));
      this.applySceneObject(object);
    }
  }

  private materialTextureIds(materialId: string | null) {
    const materialIdStr = materialId ? String(materialId) : '';
    const material = materialIdStr && (this.decodedMaterials.get(materialIdStr) || this.decodedMaterials.get(materialIdStr.toLowerCase()));
    return material ? Object.values(material.textures || {}).map((texture: any) => texture?.textureId).filter(Boolean) : [];
  }

  private overrideTextureIds(face: any): string[] {
    const textures = face?.materialOverride?.textures;
    const values = Array.isArray(textures) ? textures : Object.values(textures || {});
    return values
      .map((texture: any) => texture?.textureId || texture?.id || texture)
      .filter(Boolean)
      .map(String);
  }

  private applyMaterial(asset: any) {
    if (!asset?.assetId || !asset.material) return;
    const assetIdStr = String(asset.assetId);
    this.decodedMaterials.set(assetIdStr, asset.material);
    this.decodedMaterials.set(assetIdStr.toLowerCase(), asset.material);
    for (const object of this.sceneObjects.values()) {
      if (!object.faceTextures?.some((face: any) => this.sameId(face.materialId, assetIdStr))) continue;
      this.applySceneObject(object);
    }
  }

  private resolveFace(face: any) {
    const materialIdStr = face.materialId ? String(face.materialId) : '';
    const base = materialIdStr ? (this.decodedMaterials.get(materialIdStr) || this.decodedMaterials.get(materialIdStr.toLowerCase())) : undefined;
    const findTexture = (id?: string) => {
      if (!id) return undefined;
      const str = String(id);
      return (this.decodedTextures.has(str) || this.decodedTextures.has(str.toLowerCase())) ? `texture:${str}` : undefined;
    };
    const override = face.materialOverride || {};
    const overrideTextures = Array.isArray(override.textures) ? override.textures : Object.values(override.textures || {});
    const overrideTextureId = (index: number) => {
      const texture = overrideTextures[index];
      return texture?.textureId || texture?.id || texture;
    };
    const hasPbrOverride = override.metallicFactor !== undefined || override.roughnessFactor !== undefined || override.alphaMode !== undefined || override.baseColor || override.textureTransforms || overrideTextures.length > 0;
    if (!base && !hasPbrOverride) {
      return { ...face, texture: findTexture(overrideTextureId(0)) || findTexture(face.textureId) || face.texture };
    }
    const baseTransform = override.textureTransforms?.[0] || base?.textures?.baseColor || {};
    const texture = (role: string, index: number) => {
      const textureId = overrideTextureId(index) || base?.textures?.[role]?.textureId;
      return findTexture(textureId);
    };
    const metallic = override.metallicFactor ?? base?.metallic;
    const roughness = override.roughnessFactor ?? base?.roughness;
    const emissive = override.emissiveFactor || base?.emissive;
    const alphaMode = override.alphaMode ?? base?.alphaMode;
    const alphaCutoff = override.alphaCutoff ?? base?.alphaCutoff;
    const doubleSided = override.doubleSided ?? base?.doubleSided;
    const baseColorTex = texture('baseColor', 0);
    const normalTex = texture('normal', 1);
    const metallicRoughnessTex = texture('metallicRoughness', 2);
    const emissiveTex = texture('emissive', 3);
    const pbr = {
      metallic,
      roughness,
      emissive,
      alphaMode,
      alphaCutoff,
      doubleSided,
      baseColorTexture: baseColorTex,
      normalTexture: normalTex,
      metallicRoughnessTexture: metallicRoughnessTex,
      emissiveTexture: emissiveTex,
    };
    return {
      ...face,
      color: override.baseColor || base?.baseColor || face.color,
      repeat: baseTransform.scale || face.repeat,
      offset: baseTransform.offset || face.offset,
      rotation: baseTransform.rotation ?? face.rotation,
      pbr,
      texture: baseColorTex || findTexture(face.textureId) || face.texture,
    };
  }

  public async loadScene() {
    if (slBridge.connected) {
      try {
        const snapshot = typeof (slBridge as any).fetchSceneSnapshot === 'function'
          ? await (slBridge as any).fetchSceneSnapshot()
          : null;
        if (snapshot?.assets && Array.isArray(snapshot.assets)) {
          for (const item of snapshot.assets) {
            if (item.type === 'asset-ready') this.applyAsset(item.data);
            else if (item.type === 'texture-ready') this.applyTexture(item.data);
            else if (item.type === 'material-ready') this.applyMaterial(item.data);
          }
        }
        const objects = snapshot?.objects || (await slBridge.fetchScene());
        if (Array.isArray(objects) && objects.length > 0) {
          for (const obj of objects) {
            this.upsertSceneObject(obj);
          }
        }
      } catch (err) {
        console.warn('[WorldViewer] loadScene warning:', err);
      }
    }
  }

  private resizeObserver: any = null;
  /** Bumped whenever the renderer is destroyed, so an in-flight init() can tell it has been superseded. */
  private renderToken = 0;

  async init(targetCanvas?: HTMLCanvasElement | null) {
    this.use3D = true;
    const canvas = targetCanvas || (typeof document !== 'undefined' ? (document.getElementById('world-canvas') as HTMLCanvasElement) : null);
    if (!canvas) return;
    if (this.graphics3d && this.canvas === canvas) {
      this.resizeCanvas();
      const reuseToken = this.renderToken;
      await this.loadScene();
      if (reuseToken === this.renderToken) this.startRendering();
      return;
    }
    this.destroyRenderer();
    this.canvas = canvas;
    // The view can go away (or be replaced) while this async setup is awaiting.
    // destroyRenderer() bumps the token, and every await below re-checks it so a
    // stale init stops instead of touching state that now belongs to someone else.
    const token = this.renderToken;
    const stale = () => token !== this.renderToken;

    try {
      const graphics = new Graphics3D(canvas);
      this.graphics3d = graphics;
      await graphics.init();
      if (stale()) return;

      this.camera3d = new Camera3D();
      this.camera3d.setPosition(128, 138, 35);
      this.camera3d.setRotation(-0.3, -Math.PI / 2, 0);
      this.camera3d.setMode('orbit');
      this.camera3d.setOrbitTarget(128, 128, 25);
      this.camera3d.setPreset('rear');
      this.cameraControls = new CameraControls(canvas, this.camera3d, () => {
        this.updateLocationDisplay();
        this.emit('camera_changed', this.getCameraState());
      }, (x, y) => this.pickObject(x, y), (motion, run) => this.controlAvatar(motion, run));
      this.cameraControls.setVelocityThreshold(this.dragVelocityThreshold);
      this.cameraControls.setDisplacementThreshold(this.dragDisplacementThreshold);

      const scene = new Scene3D(graphics, this.camera3d);
      if (typeof (scene as any)?.on === 'function') {
        (scene as any).on('scene:texture-progress', (data: any) => this.emit('scene:texture-progress', data));
      }
      this.scene3d = scene;
      await scene.init();
      if (stale()) return;
      this.applyEnvironment();
      this.applyTerrainMaterials();
      if (this.terrain) scene.setTerrain(this.terrain.heights, this.terrain.size);
      if (this.displayedHud) scene.setDisplayedHud(this.displayedHud.id, this.displayedHud.size);
      await this.loadBody();
      if (stale()) return;
      this.installBodyMeshes(scene);
      for (const [assetId, geometry] of this.decodedAssets) {
        const meshes = scene.addAssetMesh(assetId, geometry);
        for (const object of this.sceneObjects.values()) {
          if (object.assetId === assetId) object.decodedMeshes = meshes;
        }
      }
      for (const texture of this.decodedTextures.values()) this.applyTexture(texture);
      try {
        await initVolumeWasm();
      } catch (_err) {
        // Fall back to pure JS volume generator if WASM module initialization fails
      }
      if (stale()) return;
      await this.loadScene();
      if (stale()) return;
      for (const object of this.sceneObjects.values()) {
        if (object.assetId && !object.decodedMeshes && this.decodedAssets.has(object.assetId)) {
          const geometry = this.decodedAssets.get(object.assetId);
          object.decodedMeshes = scene.addAssetMesh(object.assetId, geometry);
        }
        this.applySceneObject(object);
      }
      for (const object of this.sceneObjects.values()) if (this.followAvatar(object)) break;

      this.startRendering();
      this.updateLocationDisplay();

      window.addEventListener('resize', this.handleResize);
      this.resizeAttached = true;
      if (typeof ResizeObserver !== 'undefined' && canvas.parentElement) {
        this.resizeObserver = new ResizeObserver(() => this.resizeCanvas());
        this.resizeObserver.observe(canvas.parentElement);
      }
      this.resizeCanvas();
    } catch (error) {
      // A failure after the view was torn down or replaced is not this renderer's problem,
      // and destroying here would tear down whatever replaced it.
      if (stale()) return;
      console.error('3D initialization failed:', error);
      this.use3D = false;
      this.destroyRenderer();
      throw error;
    }
  }

  private resizeCanvas() {
    if (!this.canvas) return;
    const parent = this.canvas.parentElement;
    if (parent) {
      const cssWidth = Math.max(1, parent.clientWidth);
      const cssHeight = Math.max(1, parent.clientHeight);
      const pixelRatio = Math.min(2, window.devicePixelRatio || 1);
      const renderWidth = Math.round(cssWidth * pixelRatio);
      const renderHeight = Math.round(cssHeight * pixelRatio);
      if (this.graphics3d) this.graphics3d.resize(renderWidth, renderHeight);
      if (this.camera3d) this.camera3d.setAspect(cssWidth / cssHeight);
    }
  }

  /** Interval between refreshes of the fallback sky, ms. The sun moves slowly (a four-hour day). */
  private static readonly FALLBACK_SKY_REFRESH_MS = 30000;
  private fallbackSkyAt = 0;

  /** Use the simulator's environment when there is one, else the bundled Windlight day at the estimated hour. */
  private applyEnvironment(now = Date.now()) {
    if (!this.scene3d) return;
    if (this.environment) {
      this.scene3d.setEnvironment(this.environment);
      return;
    }
    this.fallbackSkyAt = now;
    const hour = this.simSunHour ?? estimatedSunHour(now);
    this.scene3d.setEnvironment(windlightEnvironment(hour));
  }

  public startRendering() {
    if (this.animationId !== null) return;
    const render = (time: number) => {
      if (this.use3D && this.scene3d && this.camera3d) {
        if (!this.environment && Date.now() - this.fallbackSkyAt > WorldViewer.FALLBACK_SKY_REFRESH_MS) this.applyEnvironment();
        this.camera3d.updateMatrices();
        this.updateAnimatedSkins();
        this.updateAnimatedAvatars();
        this.updateParticles(time / 1000);
        this.scene3d.render();
      }
      this.animationId = requestAnimationFrame(render);
    };
    this.animationId = requestAnimationFrame(render);
  }

  public stopRendering() {
    if (this.animationId !== null) cancelAnimationFrame(this.animationId);
    this.animationId = null;
  }

  public destroyRenderer(forCanvas?: HTMLCanvasElement | null) {
    if (forCanvas && this.canvas && this.canvas !== forCanvas) {
      return;
    }
    this.renderToken++;
    this.stopRendering();
    if (this.resizeAttached) window.removeEventListener('resize', this.handleResize);
    this.resizeAttached = false;
    this.resizeObserver?.disconnect();
    this.resizeObserver = null;
    this.cameraControls?.destroy();
    this.cameraControls = null;
    if (this.lastMovement && this.protocol.connected) {
      this.lastMovement = '';
      void this.protocol.setMovement({ forward: 0, right: 0, up: 0, turn: 0, run: false }).catch(() => undefined);
    }
    this.graphics3d?.destroy();
    this.graphics3d = null;
    this.camera3d = null;
    this.scene3d = null;
    this.renderedParticles.clear();
    this.canvas = null;
  }

  public updateLocationDisplay() {
    const regionName = document.getElementById('region-name');
    const coordinates = document.getElementById('coordinates');
    if (regionName) regionName.textContent = this.region?.name || '';
    if (coordinates && this.camera3d) {
      const [x, y, z] = this.camera3d.position;
      coordinates.textContent = `${Math.floor(x)}, ${Math.floor(y)}, ${Math.floor(z)}`;
    }
  }

  public moveCamera(dx: number, dy: number, dz: number) {
    if (this.camera3d?.preset !== 'free' && this.protocol.connected) {
      void this.pulseAvatar({ forward: dy, right: dx, up: dz });
      return;
    }
    if (this.camera3d) {
      this.camera3d.move(dy, dx, dz);
      this.updateLocationDisplay();
      this.emit('camera_changed', this.getCameraState());
    }
  }

  /** Keep third-person views centred behind the logged-in avatar as simulator updates arrive. */
  private followAvatar(object: any) {
    if (!object?.avatar || !Array.isArray(object.position)) return false;
    const myName = this.protocol.authReply?.first_name;
    if (object.id !== this.protocol.agentId && !(myName && object.name?.includes(myName))) return false;
    this.avatarPosition = object.position;
    if (!this.camera3d || this.camera3d.preset === 'free') return true;
    this.camera3d.setOrbitTarget(object.position[0], object.position[1], object.position[2] + 1.2);
    if (this.camera3d.preset === 'rear' && Array.isArray(object.rotation)) {
      const [x = 0, y = 0, z = 0, w = 1] = object.rotation;
      const heading = Math.atan2(2 * (w * z + x * y), 1 - 2 * (y * y + z * z));
      this.camera3d.setRotation(-0.28, -Math.PI / 2 - heading, 0);
    }
    return true;
  }

  private controlAvatar(motion: { forward: number; right: number; up: number; turn: number }, run: boolean) {
    if (this.camera3d?.preset === 'free' || !this.protocol.connected) return false;
    const signature = `${motion.forward}:${motion.right}:${motion.up}:${motion.turn}:${run}`;
    if (signature !== this.lastMovement) {
      this.lastMovement = signature;
      void this.protocol.setMovement({ ...motion, run }).catch((error: unknown) => {
        console.warn('[WorldViewer] avatar movement unavailable:', error);
      });
    }
    return true;
  }

  private async pulseAvatar(motion: { forward: number; right: number; up: number }) {
    await this.protocol.setMovement({ ...motion, turn: 0, run: false });
    window.setTimeout(() => {
      void this.protocol
        .setMovement({ forward: 0, right: 0, up: 0, turn: 0, run: false })
        .catch((error: unknown) => console.warn('[WorldViewer] avatar movement unavailable:', error));
    }, 180);
  }

  /** Rotate the view by the given pitch/yaw deltas in degrees (positive yaw turns right). */
  public rotateCamera(pitchDegrees: number, yawDegrees: number) {
    if (!this.camera3d) return;
    const toRadians = Math.PI / 180;
    if (pitchDegrees) this.camera3d.rotate(this.camera3d.mode === 'orbit' ? -pitchDegrees * toRadians : pitchDegrees * toRadians, 0);
    if (yawDegrees) this.camera3d.turn(yawDegrees * toRadians);
    this.updateLocationDisplay();
    this.emit('camera_changed', this.getCameraState());
  }

  /** Switch between the third-person orbit camera and first-person view. */
  public toggleCameraMode() {
    if (!this.camera3d) return;
    this.setCameraPreset(this.camera3d.mode === 'orbit' ? 'first-person' : 'rear');
  }

  public setCameraPreset(preset: 'rear' | 'front' | 'first-person' | 'free') {
    this.camera3d?.setPreset(preset);
    this.updateLocationDisplay();
    this.emit('camera_changed', this.getCameraState());
  }

  public getCameraState() {
    if (!this.camera3d) return null;
    const { heading, pitch } = this.camera3d.viewAngles();
    return { position: [...this.camera3d.position], preset: this.camera3d.preset, mode: this.camera3d.mode, heading, pitch };
  }

  public getInteractionMode(): 'navigate' | 'interact' {
    return this.interactionMode;
  }

  public setInteractionMode(mode: 'navigate' | 'interact') {
    const nextMode = mode === 'interact' ? 'interact' : 'navigate';
    if (this.interactionMode !== nextMode) {
      this.interactionMode = nextMode;
      this.emit('interaction_mode_changed', this.interactionMode);
    }
  }

  public toggleInteractionMode(): 'navigate' | 'interact' {
    const next = this.interactionMode === 'navigate' ? 'interact' : 'navigate';
    this.setInteractionMode(next);
    return next;
  }

  public getDragVelocityThreshold(): number {
    return this.cameraControls ? this.cameraControls.getVelocityThreshold() : this.dragVelocityThreshold;
  }

  public setDragVelocityThreshold(threshold: number): void {
    this.dragVelocityThreshold = Math.max(0, threshold);
    if (this.cameraControls) this.cameraControls.setVelocityThreshold(this.dragVelocityThreshold);
  }

  public getDragDisplacementThreshold(): number {
    return this.cameraControls ? this.cameraControls.getDisplacementThreshold() : this.dragDisplacementThreshold;
  }

  public setDragDisplacementThreshold(threshold: number): void {
    this.dragDisplacementThreshold = Math.max(0, threshold);
    if (this.cameraControls) this.cameraControls.setDisplacementThreshold(this.dragDisplacementThreshold);
  }

  public getPanMode(): boolean {
    return this.cameraControls ? this.cameraControls.getPanMode() : false;
  }

  public setPanMode(enabled: boolean): void {
    if (this.cameraControls) this.cameraControls.setPanMode(enabled);
    this.emit('pan_mode_changed', enabled);
  }

  public togglePanMode(): boolean {
    const next = !this.getPanMode();
    this.setPanMode(next);
    return next;
  }

  public resetCamera(): void {
    if (!this.camera3d) return;
    const target = this.avatarPosition || (this.protocol?.agentId && this.sceneObjects.get(this.protocol.agentId)?.position) || [128, 128, 25];
    this.camera3d.reset(target);
    this.updateLocationDisplay();
    this.emit('camera_changed', this.getCameraState());
  }

  public pickObject(x: number, y: number) {
    if (this.interactionMode === 'navigate') return null;
    if (!this.canvas || !this.scene3d) return null;
    // A displayed HUD sits over the world, so a tap on it is a touch on the HUD, not a world selection.
    if (this.touchHudAt(x, y)) return null;
    const bounds = this.canvas.getBoundingClientRect();
    const hit = this.scene3d.pick(x, y, bounds.width, bounds.height);
    const objectId = hit?.id.replace(/:(body|head|legs)$/, '') || null;
    this.selectedObject = objectId ? this.sceneObjects.get(objectId) || null : null;
    const selection = this.selectedObject ? { ...this.selectedObject, hitPoint: hit?.point, distance: hit?.distance } : null;
    this.emit('selection_changed', selection);
    return selection;
  }

  /** Zoom the orbit camera onto an avatar or object by id (used by the radar list). */
  public focusObjectById(id: string) {
    if (!this.camera3d) return false;
    const target = this.sceneObjects.get(id) || this.objects.find((o) => o.id === id) || this.nearbyUsers.find((u) => u.id === id);
    const position = target?.position;
    if (!Array.isArray(position)) return false;
    this.camera3d.setOrbitTarget(position[0], position[1], position[2] + (target.avatar || target.kind === 'person' ? 1.2 : 0));
    this.camera3d.setMode('orbit');
    this.camera3d.orbitDistance = target.avatar ? 4 : Math.max(2.5, Math.hypot(...(target.scale || [1, 1, 1])) * 2.5);
    this.camera3d.updateMatrices();
    this.emit('camera_changed', this.getCameraState());
    return true;
  }

  public focusSelectedObject() {
    if (!this.selectedObject || !this.camera3d) return false;
    const { position } = this.worldTransform(this.selectedObject);
    this.camera3d.setOrbitTarget(position[0], position[1], position[2]);
    this.camera3d.setMode('orbit');
    this.camera3d.orbitDistance = Math.max(2.5, Math.hypot(...(this.selectedObject.scale || [1, 1, 1])) * 2.5);
    this.camera3d.updateMatrices();
    this.emit('camera_changed', this.getCameraState());
    return true;
  }

  private quaternionToEuler([x, y, z, w]: number[]) {
    const sinX = 2 * (w * x + y * z);
    const cosX = 1 - 2 * (x * x + y * y);
    const sinY = Math.max(-1, Math.min(1, 2 * (w * y - z * x)));
    const sinZ = 2 * (w * z + x * y);
    const cosZ = 1 - 2 * (y * y + z * z);
    return [Math.atan2(sinX, cosX), Math.asin(sinY), Math.atan2(sinZ, cosZ)];
  }

  private upsertSceneObject(object: any, emitChanged = true) {
    if (!object?.id) return;
    // Terse simulator updates only carry motion fields.  Keep the shape,
    // material and link metadata learned from the full ObjectUpdate packet.
    const previous = this.sceneObjects.get(object.id);
    const merged = previous ? { ...previous, ...object } : object;
    this.sceneObjects.set(object.id, merged);
    this.particles.setEmitter(object.id, merged.particles || null, performance.now() / 1000);
    if (object.localId) this.localObjectIds.set(object.localId, object.id);
    if (merged.assetId && !merged.decodedMeshes) {
      const assetKey = String(merged.assetId);
      const geometry = this.decodedAssets.get(assetKey) || this.decodedAssets.get(assetKey.toLowerCase());
      if (geometry) {
        const meshes = this.scene3d?.addAssetMesh(assetKey, geometry);
        merged.decodedMeshes = meshes || (geometry?.parts?.length
          ? geometry.parts.map((p: any, i: number) => ({ mesh: `asset:${assetKey}:${i}`, materialIndex: p.materialIndex ?? i }))
          : [{ mesh: `asset:${assetKey}:0`, materialIndex: 0 }]);
      }
    }
    if (merged.textureId && !merged.decodedTexture) {
      const texKey = String(merged.textureId);
      if (this.decodedTextures.has(texKey) || this.decodedTextures.has(texKey.toLowerCase())) {
        merged.decodedTexture = `texture:${texKey}`;
      }
    }
    this.objects = Array.from(this.sceneObjects.values());
    this.applySceneObject(merged);
    if (!previous && merged.assetId) {
      const key = String(merged.assetId);
      if ((this.decodedAssets.get(key) || this.decodedAssets.get(key.toLowerCase()))?.skin) this.reapplyAvatarSubject(this.animationSubject(merged));
    }
    // A root prim moving changes every child prim's world transform even when
    // the simulator quite correctly sends no update for those children.
    if (merged.localId) this.reapplyChildren(merged.localId);
    this.syncHuds();
    if (emitChanged) {
      this.emit('objects_changed', this.objects);
    }
  }

  private reapplyChildren(parentLocalId: number, visited = new Set<number>()) {
    if (visited.has(parentLocalId)) return;
    visited.add(parentLocalId);
    for (const child of this.sceneObjects.values()) {
      if (Number(child.parentId) !== Number(parentLocalId)) continue;
      this.applySceneObject(child);
      if (child.localId) this.reapplyChildren(child.localId, visited);
    }
  }

  private multiplyQuaternion(a: number[], b: number[]) {
    return [
      a[3] * b[0] + a[0] * b[3] + a[1] * b[2] - a[2] * b[1],
      a[3] * b[1] - a[0] * b[2] + a[1] * b[3] + a[2] * b[0],
      a[3] * b[2] + a[0] * b[1] - a[1] * b[0] + a[2] * b[3],
      a[3] * b[3] - a[0] * b[0] - a[1] * b[1] - a[2] * b[2],
    ];
  }

  private rotateVector(vector: number[], quaternion: number[]) {
    const [x, y, z] = vector;
    const [qx, qy, qz, qw] = quaternion;
    const ix = qw * x + qy * z - qz * y;
    const iy = qw * y + qz * x - qx * z;
    const iz = qw * z + qx * y - qy * x;
    const iw = -qx * x - qy * y - qz * z;
    return [
      ix * qw + iw * -qx + iy * -qz - iz * -qy,
      iy * qw + iw * -qy + iz * -qx - ix * -qz,
      iz * qw + iw * -qz + ix * -qy - iy * -qx,
    ];
  }

  /** Skeleton joint used as the origin of each in-world attachment point. */
  private static readonly ATTACHMENT_JOINTS: Readonly<Record<number, string>> = {
    1: 'mChest', 2: 'mSkull', 3: 'mCollarLeft', 4: 'mCollarRight', 5: 'mWristLeft', 6: 'mWristRight',
    7: 'mAnkleLeft', 8: 'mAnkleRight', 9: 'mTorso', 10: 'mPelvis', 11: 'mHead', 12: 'mHead',
    13: 'mHead', 14: 'mHead', 15: 'mEyeLeft', 16: 'mEyeRight', 17: 'mHead',
    18: 'mShoulderRight', 19: 'mElbowRight', 20: 'mShoulderLeft', 21: 'mElbowLeft',
    22: 'mHipRight', 23: 'mHipRight', 24: 'mKneeRight', 25: 'mHipLeft', 26: 'mHipLeft',
    27: 'mKneeLeft', 28: 'mTorso', 29: 'mChest', 30: 'mChest', 39: 'mNeck', 40: 'mPelvis',
    41: 'mHandRing1Left', 42: 'mHandRing1Right', 43: 'mTail1', 44: 'mTail6',
    45: 'mWing1Left', 46: 'mWing1Right', 47: 'mFaceJaw', 48: 'mFaceEar1Left', 49: 'mFaceEar1Right',
    50: 'mFaceEyeAltLeft', 51: 'mFaceEyeAltRight', 52: 'mFaceTongueBase', 53: 'mGroin',
    54: 'mHindLimb4Left', 55: 'mHindLimb4Right',
  };

  private quaternionFromMatrix(matrix: ArrayLike<number>): number[] {
    const trace = matrix[0] + matrix[5] + matrix[10];
    let x: number, y: number, z: number, w: number;
    if (trace > 0) {
      const s = Math.sqrt(trace + 1) * 2;
      w = s / 4; x = (matrix[6] - matrix[9]) / s; y = (matrix[8] - matrix[2]) / s; z = (matrix[1] - matrix[4]) / s;
    } else if (matrix[0] > matrix[5] && matrix[0] > matrix[10]) {
      const s = Math.sqrt(1 + matrix[0] - matrix[5] - matrix[10]) * 2;
      w = (matrix[6] - matrix[9]) / s; x = s / 4; y = (matrix[4] + matrix[1]) / s; z = (matrix[8] + matrix[2]) / s;
    } else if (matrix[5] > matrix[10]) {
      const s = Math.sqrt(1 + matrix[5] - matrix[0] - matrix[10]) * 2;
      w = (matrix[8] - matrix[2]) / s; x = (matrix[4] + matrix[1]) / s; y = s / 4; z = (matrix[9] + matrix[6]) / s;
    } else {
      const s = Math.sqrt(1 + matrix[10] - matrix[0] - matrix[5]) * 2;
      w = (matrix[1] - matrix[4]) / s; x = (matrix[8] + matrix[2]) / s; y = (matrix[9] + matrix[6]) / s; z = s / 4;
    }
    return [x, y, z, w];
  }

  /** Transform an attachment root from its joint-local coordinates into region coordinates. */
  private attachmentTransform(object: any, avatar: any): { position: number[]; rotation: number[] } | null {
    if (isHudPoint(Number(object.attachmentPoint))) return null;
    const jointName = WorldViewer.ATTACHMENT_JOINTS[Number(object.attachmentPoint)];
    if (!jointName) return null;
    this.skeleton ||= new AvatarSkeleton();
    const jointIndex = this.skeleton.indexOf(jointName);
    if (jointIndex < 0) return null;
    const matrix = this.avatarWorldMatrices(avatar.id)[jointIndex];
    const jointPosition = [matrix[12], matrix[13], matrix[14]];
    const jointRotation = this.quaternionFromMatrix(matrix);
    const localPosition = Array.isArray(object.position) ? object.position : [0, 0, 0];
    const localRotation = Array.isArray(object.rotation) && object.rotation.length === 4 ? object.rotation : [0, 0, 0, 1];
    const avatarTransform = this.avatarRootTransform(avatar);
    const jointOffset = jointPosition.map((value, axis) => value + this.rotateVector(localPosition, jointRotation)[axis]);
    return {
      position: avatarTransform.position.map((value, axis) => value + this.rotateVector(jointOffset, avatarTransform.rotation)[axis]),
      rotation: this.multiplyQuaternion(avatarTransform.rotation, this.multiplyQuaternion(jointRotation, localRotation)),
    };
  }

  private worldTransform(object: any, visited = new Set<string>()): { position: number[]; rotation: number[] } {
    const position = Array.isArray(object.position) ? object.position : [0, 0, 0];
    const rotation = Array.isArray(object.rotation) && object.rotation.length === 4 ? object.rotation : [0, 0, 0, 1];
    if (!object.parentId || visited.has(object.id)) return { position, rotation };
    // A HUD root is placed relative to its HUD attachment point, not to the avatar's world position.
    if (this.isHudRoot(object)) return { position, rotation };
    const parentId = this.localObjectIds.get(Number(object.parentId));
    const parent = parentId && this.sceneObjects.get(parentId);
    if (!parent) return { position, rotation };
    if (parent.avatar && Number(object.attachmentPoint) > 0) {
      const attached = this.attachmentTransform(object, parent);
      if (attached) return attached;
    }
    visited.add(object.id);
    const parentTransform = this.worldTransform(parent, visited);
    const offset = this.rotateVector(position, parentTransform.rotation);
    return {
      position: parentTransform.position.map((value, index) => value + offset[index]),
      rotation: this.multiplyQuaternion(parentTransform.rotation, rotation),
    };
  }

  // ---- worn HUDs ----------------------------------------------------------

  /** A HUD root is an attachment on one of the HUD points (31-38) whose parent is the avatar. */
  private isHudRoot(object: any) {
    return !object.avatar && Number(object.parentId) > 0 && isHudPoint(Number(object.attachmentPoint));
  }

  /** The HUD root this object belongs to (itself, or the root of its link set), or null. */
  private hudRootOf(object: any): any | null {
    let current = object;
    for (let depth = 0; depth < 16 && current; depth++) {
      if (this.isHudRoot(current)) return current;
      const parentId = this.localObjectIds.get(Number(current.parentId));
      current = parentId ? this.sceneObjects.get(parentId) : null;
    }
    return null;
  }

  /** The worn HUDs the simulator has sent, each with all its linked prims. */
  public getHuds(): HudInfo[] {
    const roots = [...this.sceneObjects.values()].filter((object) => this.isHudRoot(object));
    return roots.map((root) => ({
      id: root.id,
      name: root.name || '',
      attachmentPoint: Number(root.attachmentPoint),
      pointName: HUD_POINTS[Number(root.attachmentPoint)] || '',
      memberIds: [...this.sceneObjects.values()].filter((object) => this.hudRootOf(object)?.id === root.id).map((object) => object.id),
    }));
  }

  /** Show one HUD over the view (or hide it). Unknown ids are ignored. */
  public setDisplayedHud(id: string | null, size: number = HUD_SIZE.initial) {
    const known = id ? this.getHuds().some((hud) => hud.id === id) : true;
    if (!known) return false;
    this.displayedHud = id ? { id, size: Math.max(HUD_SIZE.min, Math.min(HUD_SIZE.max, size)) } : null;
    this.scene3d?.setDisplayedHud(this.displayedHud ? this.displayedHud.id : null, this.displayedHud?.size);
    this.emit('hud_display_changed', this.displayedHud ? { ...this.displayedHud } : null);
    return true;
  }

  /** Make the displayed HUD larger or smaller. */
  public zoomHud(direction: 1 | -1) {
    if (!this.displayedHud) return;
    this.setDisplayedHud(this.displayedHud.id, this.displayedHud.size + direction * HUD_SIZE.step);
  }

  /** Re-announce the HUD list when it changed, and drop the displayed HUD if it is gone. */
  private syncHuds() {
    const huds = this.getHuds();
    const signature = huds.map((hud) => `${hud.id}:${hud.name}:${hud.memberIds.length}`).join('|');
    if (this.displayedHud && !huds.some((hud) => hud.id === this.displayedHud!.id)) this.setDisplayedHud(null);
    if (signature === this.hudSignature) return;
    this.hudSignature = signature;
    this.emit('huds_changed', huds);
  }

  /**
   * Tap on the displayed HUD: send a touch to the object under the finger.
   * Returns the touched prim, or null when the tap was not on the HUD.
   * Which face or texture coordinate was hit is not resolved yet (picking uses
   * bounding boxes), so scripts that read the touched face get the defaults.
   */
  public touchHudAt(x: number, y: number) {
    if (!this.displayedHud || !this.canvas || !this.scene3d) return null;
    const bounds = this.canvas.getBoundingClientRect();
    const hit = this.scene3d.pickHud(x, y, bounds.width, bounds.height);
    if (!hit) return null;
    const object = this.sceneObjects.get(hit.id);
    void this.touchObject(hit.id);
    this.emit('hud_touched', { id: hit.id, name: object?.name || '' });
    return { id: hit.id, name: object?.name || '' };
  }

  /** Touch an object by id through the connection; failures are reported, not hidden. */
  public async touchObject(id: string) {
    try {
      await this.protocol.touchObject({ id });
      return true;
    } catch (error) {
      this.emit('action_failed', { action: 'touch', message: error instanceof Error ? error.message : 'Touch failed' });
      return false;
    }
  }

  /** Touch the object selected in the world view. */
  public async touchSelected() {
    return this.selectedObject ? this.touchObject(this.selectedObject.id) : false;
  }

  private applySceneObject(object: any) {
    if (!this.scene3d) return;
    const subject = !object.avatar ? this.animationSubject(object) : object.id;
    const skin = object.assetId && !object.avatar ? this.skinRowsFor(object.assetId, undefined, subject) : null;
    const { position, rotation } = skin ? this.riggedTransform(object) : this.worldTransform(object);
    const hudRoot = object.avatar ? null : this.hudRootOf(object);
    // Worn rigged attachments are authored in avatar space and ignore their prim scale. Animesh is
    // different: it is a world object and Second Life applies the object's scale after skinning.
    // Treating both alike made scaled Animesh appear compressed into its unit-sized bind shape.
    const objectScale = Array.isArray(object.scale) ? object.scale : [1, 1, 1];
    const scale = skin && !object.animatedMesh ? [1, 1, 1] : objectScale;
    object.decodedFaceTextures = (object.faceTextures || []).map((face: any) => this.resolveFace(face));
    const config = {
      mesh: object.avatar ? 'sphere' : ['cube', 'cylinder', 'sphere', 'prism', 'torus', 'asset-proxy'].includes(object.shape) ? object.shape : 'cube',
      meshes: object.decodedMeshes || this.volumeMeshesFor(object),
      position,
      rotation: this.quaternionToEuler(rotation),
      scale,
      color: object.avatar ? [0.3, 0.65, 1, 1] : object.color || [0.8, 0.8, 0.8, 1],
      texture: object.decodedTexture || (object.textureId && (this.decodedTextures.has(String(object.textureId)) || this.decodedTextures.has(String(object.textureId).toLowerCase())) ? `texture:${object.textureId}` : undefined),
      faces: object.decodedFaceTextures,
      reflectionProbe: object.reflectionProbe,
      skin,
      // HUD prims belong to the HUD pass, never the world.
      hud: Boolean(hudRoot),
      hudRoot: hudRoot ? hudRoot.id : null,
    };
    if (this.scene3d.objects.has(object.id)) this.scene3d.updateObject(object.id, config);
    else this.scene3d.addObject(object.id, config);
    if (object.avatar) this.applyAvatarParts(object.id, config, object);
  }

  /** Advance simulator particle sources and draw camera-facing, textured translucent sprites. */
  private updateParticles(now: number) {
    if (!this.scene3d || !this.camera3d) return;
    const positions = new Map<string, number[]>();
    for (const object of this.sceneObjects.values()) positions.set(object.id, this.worldTransform(object).position);
    const frames = this.particles.update(now, positions);
    const live = new Set<string>();
    // A plane starts in XY. Match its normal to the view direction with the camera pitch/yaw.
    const rotation = [Math.PI / 2 + this.camera3d.rotation[0], 0, -this.camera3d.rotation[1]];
    for (const particle of frames) {
      const id = `particle:${particle.id}`;
      live.add(id);
      const texture = particle.textureId && this.decodedTextures.has(particle.textureId) ? `texture:${particle.textureId}` : undefined;
      const config = {
        mesh: 'particle-sprite', position: particle.position, rotation, scale: [particle.scale[0], particle.scale[1], 1],
        color: particle.color, texture, faces: [{ texture, color: particle.color, fullBright: particle.emissive, pbr: { alphaMode: 'BLEND' } }],
      };
      if (this.scene3d.objects.has(id)) this.scene3d.updateObject(id, config); else this.scene3d.addObject(id, config);
    }
    for (const id of this.renderedParticles) if (!live.has(id)) this.scene3d.removeObject(id);
    this.renderedParticles = live;
  }

  /** Load the base avatar meshes once; on failure avatars keep the placeholder shapes. */
  private loadBody(): Promise<void> {
    if (this.bodyParts) return Promise.resolve();
    this.bodyLoad ||= loadBodyParts()
      .then((parts) => { this.bodyParts = parts; })
      .catch((error) => {
        console.warn('[WorldViewer] avatar body meshes unavailable, using placeholder avatars:', error);
        this.bodyLoad = null;
        this.scheduleBodyRetry();
      });
    return this.bodyLoad;
  }

  private bodyRetries = 0;
  private bodyRetryTimer: ReturnType<typeof setTimeout> | null = null;

  /** A transient fetch failure must not leave blocks and spheres for the whole session: retry, then swap in the real bodies. */
  private scheduleBodyRetry() {
    if (this.bodyRetryTimer || this.bodyRetries >= 4) return;
    this.bodyRetries++;
    this.bodyRetryTimer = setTimeout(async () => {
      this.bodyRetryTimer = null;
      await this.loadBody();
      const scene = this.scene3d;
      if (!scene || !this.bodyParts) return;
      this.installBodyMeshes(scene);
      for (const object of this.sceneObjects.values()) if (object.avatar) this.applySceneObject(object);
    }, 3000 * this.bodyRetries);
  }

  private installBodyMeshes(scene: Scene3D) {
    if (!this.bodyParts || this.bodyMeshesReadyFor === scene) return;
    for (const [part, geometry] of this.bodyParts) {
      scene.addSkinnedMesh(`avatar-body:${part}`, geometry, bodyPartVertexSkin(geometry));
    }
    this.bodyMeshesReadyFor = scene;
  }

  /** Joint matrices for every body part of an avatar in its current animated pose. */
  private avatarBodyRows(avatarId: string): Map<string, Float32Array> {
    this.skeleton ||= new AvatarSkeleton();
    const world = this.avatarWorldMatrices(avatarId);
    const maxJoints = (this.scene3d as any)?.graphics?.maxJoints || 110;
    const rows = new Map<string, Float32Array>();
    for (const { part, instance, rigidJoint } of BODY_PARTS) {
      const geometry = this.bodyParts?.get(part);
      if (!geometry) continue;
      let skin = this.bodySkins.get(instance);
      if (!skin) { skin = bodyPartSkin(this.skeleton, geometry, rigidJoint); this.bodySkins.set(instance, skin); }
      rows.set(instance, bodyPartRows(this.skeleton, skin, world, maxJoints));
    }
    return rows;
  }

  /** Draw an avatar as the skinned base body, standing with its feet at the ground under the reported position. */
  /** Texture-entry faces that hold an avatar's baked textures. */
  private static readonly BAKED_FACE: Record<string, number> = { head: 8, upper: 9, lower: 10, eyes: 11, skirt: 19, hair: 20 };
  /** Placeholder textures the simulator uses before a bake exists. */
  private static readonly UNBAKED_TEXTURES = new Set([
    '00000000-0000-0000-0000-000000000000', 'c228d1cf-4b5d-4ba8-84f4-899a0796aa97', '5748decc-f629-461c-9a36-a35a221fe21f',
  ]);

  /** Name of the decoded baked texture for a body slot, or null while it is missing or not yet downloaded. */
  private bakedTexture(object: any, bake: string): string | null {
    const index = WorldViewer.BAKED_FACE[bake];
    const textureId: string | null | undefined = object?.faceTextures?.[index]?.textureId;
    const key = `${object?.id || ''}:${bake}`;
    if (textureId && !WorldViewer.UNBAKED_TEXTURES.has(textureId) && this.decodedTextures.has(textureId)) {
      this.avatarBakes.set(key, textureId);
      return `texture:${textureId}`;
    }
    const previous = this.avatarBakes.get(key);
    return previous && this.decodedTextures.has(previous) ? `texture:${previous}` : null;
  }

  private applyAvatarBody(id: string, config: any, object?: any) {
    if (!this.scene3d) return;
    if (!this.animator.subjects().includes(id)) this.animator.setAnimations(id, [{ id: WorldViewer.STAND_ANIMATION, seq: 0 }]);
    const [x, y, z] = config.position;
    // The simulator reports the avatar's bounding box centre and its height in scale.z.
    const height = Array.isArray(config.scale) && config.scale[2] > 0.5 ? config.scale[2] : 1.9;
    const rows = this.avatarBodyRows(id);
    for (const { part, instance, color, bake } of BODY_PARTS) {
      if (!rows.has(instance)) continue;
      const texture = this.bakedTexture(object, bake);
      const partId = `${id}:body:${instance}`;
      const part3d = {
        mesh: 'cube', meshes: [{ mesh: `avatar-body:${part}`, materialIndex: 0 }],
        position: [x, y, z - height / 2], rotation: config.rotation, scale: [1, 1, 1],
        // A baked texture replaces the flat fallback colour; hair blends, skin and eyes are cut out.
        color: texture ? [1, 1, 1, 1] : color,
        faces: texture ? [{ texture, color: [1, 1, 1, 1], repeat: [1, 1], offset: [0, 0], rotation: 0, pbr: { alphaMode: bake === 'hair' ? 'BLEND' : 'MASK', alphaCutoff: 0.5 } }] : [],
        skin: rows.get(instance), visible: true,
      };
      if (this.scene3d.objects.has(partId)) this.scene3d.updateObject(partId, part3d);
      else this.scene3d.addObject(partId, part3d);
    }
    // The skirt is a separate optional system layer. Do not show an opaque
    // fallback skirt, but do render it whenever the skirt bake is available.
    const skirtTexture = this.bakedTexture(object, 'skirt');
    const skirtId = `${id}:body:skirt`;
    const skirtGeometry = this.bodyParts?.get('skirt');
    if (skirtTexture && skirtGeometry) {
      let skirtSkin = this.bodySkins.get('skirt');
      if (!skirtSkin) { skirtSkin = bodyPartSkin(this.skeleton, skirtGeometry); this.bodySkins.set('skirt', skirtSkin); }
      const skirt = {
        mesh: 'cube', meshes: [{ mesh: 'avatar-body:skirt', materialIndex: 0 }],
        position: [x, y, z - height / 2], rotation: config.rotation, scale: [1, 1, 1], color: [1, 1, 1, 1],
        faces: [{ texture: skirtTexture, color: [1, 1, 1, 1], repeat: [1, 1], offset: [0, 0], rotation: 0, pbr: { alphaMode: 'MASK', alphaCutoff: 0.5, doubleSided: true } }],
        skin: bodyPartRows(this.skeleton, skirtSkin, this.avatarWorldMatrices(id), (this.scene3d as any).graphics?.maxJoints || 110), visible: true,
      };
      if (this.scene3d.objects.has(skirtId)) this.scene3d.updateObject(skirtId, skirt);
      else this.scene3d.addObject(skirtId, skirt);
    } else this.scene3d.removeObject(skirtId);
    for (const suffix of [':body', ':head', ':legs']) this.scene3d.removeObject(`${id}${suffix}`);
    this.scene3d.updateObject(id, { visible: false });
  }

  /** Re-pose avatars every frame while they animate. */
  private updateAnimatedAvatars() {
    if (!this.scene3d || !this.bodyParts) return;
    for (const object of this.sceneObjects.values()) {
      if (!object.avatar || !this.scene3d.objects.has(`${object.id}:body:${BODY_PARTS[0].instance}`)) continue;
      if (!this.animator.isAnimating(object.id)) continue;
      const rows = this.avatarBodyRows(object.id);
      for (const [instance, skin] of rows) this.scene3d.updateObject(`${object.id}:body:${instance}`, { skin });
      const skirt = this.scene3d.objects.get(`${object.id}:body:skirt`);
      const skirtGeometry = skirt && this.bodyParts.get('skirt');
      if (skirtGeometry) {
        let skirtSkin = this.bodySkins.get('skirt');
        if (!skirtSkin) { skirtSkin = bodyPartSkin(this.skeleton, skirtGeometry); this.bodySkins.set('skirt', skirtSkin); }
        const world = this.avatarWorldMatrices(object.id);
        this.scene3d.updateObject(`${object.id}:body:skirt`, { skin: bodyPartRows(this.skeleton, skirtSkin, world, (this.scene3d as any).graphics?.maxJoints || 110) });
      }
    }
  }

  private applyAvatarParts(id: string, config: any, object?: any) {
    if (!this.scene3d) return;
    if (this.bodyParts && this.bodyMeshesReadyFor === this.scene3d) return this.applyAvatarBody(id, config, object);
    // Do not regress avatars to block/cylinder stand-ins during renderer startup. Load the real
    // skinned body lazily and replace the hidden simulator marker as soon as its meshes are ready.
    this.scene3d.updateObject(id, { visible: false });
    const scene = this.scene3d;
    void this.loadBody().then(() => {
      if (this.scene3d !== scene || !this.bodyParts) return;
      this.installBodyMeshes(scene);
      for (const avatar of this.sceneObjects.values()) if (avatar.avatar) this.applySceneObject(avatar);
    });
  }

  private removeSceneObject(object: any, emitChanged = true) {
    const id = object.id && this.sceneObjects.has(object.id)
      ? object.id
      : this.localObjectIds.get(object.localId);
    if (!id) return;
    const removed = this.sceneObjects.get(id);
    const affectedSubject = removed && !removed.avatar ? this.animationSubject(removed) : null;
    this.sceneObjects.delete(id);
    this.localObjectIds.delete(object.localId);
    this.animator.remove(id);
    this.particles.removeEmitter(id);
    for (const key of this.avatarBakes.keys()) if (key.startsWith(`${id}:`)) this.avatarBakes.delete(key);
    this.posedObjects.delete(id);
    this.scene3d?.removeObject(id);
    for (const suffix of [':body', ':head', ':legs', ...BODY_PARTS.map((p) => `:body:${p.instance}`)]) this.scene3d?.removeObject(`${id}${suffix}`);
    this.objects = Array.from(this.sceneObjects.values());
    if (affectedSubject && affectedSubject !== id) this.reapplyAvatarSubject(affectedSubject);
    if (this.selectedObject?.id === id) {
      this.selectedObject = null;
      this.emit('selection_changed', null);
    }
    this.syncHuds();
    if (emitChanged) {
      this.emit('objects_changed', this.objects);
    }
  }

  private updateCoarseLocations(data: any) {
    const locations = data?.Location_Fields || data?.locations || [];
    const agents = data?.AgentData_Fields || data?.agents || [];
    const you = Number(data?.Index_Field?.You ?? data?.youIndex ?? -1);
    const next: any[] = [];
    for (let index = 0; index < locations.length; index++) {
      const location = locations[index];
      const rawPos = [
        Number(location.X ?? location.x ?? 0),
        Number(location.Y ?? location.y ?? 0),
        Number(location.Z ?? location.z ?? 0) * 4,
      ];
      const position: [number, number, number] = CoordinateNormalizer.globalToRegionLocal(rawPos, this.region);
      if (index === you) {
        this.avatarPosition = position;
        continue;
      }
      const agent = agents[index] || {};
      const id = agent.AgentID || agent.agentId || agent.id;
      if (!id || /^0{8}-0{4}-0{4}-0{4}-0{12}$/.test(String(id))) continue;
      const distance = this.avatarPosition
        ? Math.hypot(position[0] - this.avatarPosition[0], position[1] - this.avatarPosition[1], position[2] - this.avatarPosition[2])
        : null;
      next.push({ id: String(id), position, distance });
    }
    this.nearbyUsers = next;
    this.emit('nearby_changed', next.map(user => ({ ...user })));
  }

  public parseCoordinates(item: any): [number, number, number] | null {
    if (!item) return null;
    const parsed = CoordinateNormalizer.parseVector(item) || CoordinateNormalizer.parseVector(
      item.coordinates ??
      item.Coordinates ??
      item.position ??
      item.Position ??
      item.pos ??
      item.Pos ??
      item.coarsePosition ??
      item.location ??
      item.Location
    );
    if (!parsed) return null;
    return CoordinateNormalizer.globalToRegionLocal(parsed, this.region);
  }

  public handleAvatarPresence(data: any) {
    if (!data) return;
    const body = data.body ?? data.data ?? data;
    const items = Array.isArray(body)
      ? body
      : Array.isArray(body.AgentData)
      ? body.AgentData
      : Array.isArray(body.agents)
      ? body.agents
      : Array.isArray(body.avatars)
      ? body.avatars
      : [body];

    let changed = false;
    let objectsChanged = false;

    for (const item of items) {
      if (!item) continue;
      const rawId = item.id || item.agentId || item.AgentID || item.agent_id || item.avatar_id || item.avatarId || item.uuid || '';
      const id = String(typeof rawId === 'object' && rawId?.toString ? rawId.toString() : rawId).trim();
      if (!id || /^0{8}-0{4}-0{4}-0{4}-0{12}$/.test(id)) continue;

      const isLeft = Boolean(
        item.left === true ||
        item.Left === true ||
        item.presence === 'left' ||
        item.presence === 'departed' ||
        item.presence === 'offline' ||
        item.online === false ||
        item.Online === false
      );

      if (isLeft) {
        if (id === this.protocol.agentId) {
          this.avatarPosition = null;
        } else {
          const prevCount = this.nearbyUsers.length;
          this.nearbyUsers = this.nearbyUsers.filter((u) => u.id !== id);
          if (this.nearbyUsers.length !== prevCount) {
            changed = true;
          }
          this.removeSceneObject({ id }, false);
          objectsChanged = true;
        }
        continue;
      }

      const position = this.parseCoordinates(item);
      const firstName = item.firstName || item.FirstName || '';
      const lastName = item.lastName || item.LastName || '';
      const fullName = [firstName, lastName].filter(Boolean).join(' ');
      const name = item.name || item.Name || fullName || item.username;

      if (id === this.protocol.agentId) {
        if (position) {
          this.avatarPosition = position;
          if (this.camera3d) {
            this.camera3d.setOrbitTarget(position[0], position[1], position[2]);
          }
          if (this.sceneObjects.has(id)) {
            this.upsertSceneObject({
              id,
              position,
              avatar: true,
              name: name || this.protocol.authReply?.first_name || 'Me',
              shape: 'sphere',
            }, false);
            objectsChanged = true;
          }
          // Recalculate distance and bearing for all nearby users
          this.nearbyUsers = this.nearbyUsers.map((u) => {
            if (u.position) {
              const distance = Math.hypot(
                u.position[0] - position[0],
                u.position[1] - position[1],
                u.position[2] - position[2]
              );
              const dx = u.position[0] - position[0];
              const dy = u.position[1] - position[1];
              let bearing = Math.atan2(dx, dy) * (180 / Math.PI);
              if (bearing < 0) bearing += 360;
              const roundedBearing = Math.round(bearing);
              if (u.distance !== distance || u.bearing !== roundedBearing) {
                changed = true;
                return { ...u, distance, bearing: roundedBearing };
              }
            }
            return u;
          });
        }
        continue;
      }

      // Another avatar in region
      let distance: number | null = null;
      let bearing: number | null = null;
      if (position && this.avatarPosition) {
        distance = Math.hypot(
          position[0] - this.avatarPosition[0],
          position[1] - this.avatarPosition[1],
          position[2] - this.avatarPosition[2]
        );
        const dx = position[0] - this.avatarPosition[0];
        const dy = position[1] - this.avatarPosition[1];
        let brg = Math.atan2(dx, dy) * (180 / Math.PI);
        if (brg < 0) brg += 360;
        bearing = Math.round(brg);
      }

      const existingIndex = this.nearbyUsers.findIndex((u) => u.id === id);
      const existing = existingIndex >= 0 ? this.nearbyUsers[existingIndex] : null;

      const nextUser = {
        ...(existing || {}),
        ...item,
        id,
        name: name || existing?.name || `Resident ${id.slice(0, 8)}`,
        position: position ?? existing?.position ?? null,
        distance: distance ?? existing?.distance ?? null,
        bearing: bearing ?? existing?.bearing ?? null,
        online: true,
        presence: item.presence || 'online',
      };

      if (existingIndex >= 0) {
        if (
          existing.position?.[0] !== nextUser.position?.[0] ||
          existing.position?.[1] !== nextUser.position?.[1] ||
          existing.position?.[2] !== nextUser.position?.[2] ||
          existing.distance !== nextUser.distance ||
          existing.bearing !== nextUser.bearing ||
          existing.name !== nextUser.name
        ) {
          this.nearbyUsers = this.nearbyUsers.map((u, idx) => (idx === existingIndex ? nextUser : u));
          changed = true;
        }
      } else {
        this.nearbyUsers = [...this.nearbyUsers, nextUser];
        changed = true;
      }

      // If position is known, upsert into 3D scene so avatars appear in the 3D world view!
      if (position) {
        this.upsertSceneObject({
          id,
          position,
          avatar: true,
          name: nextUser.name,
          shape: 'sphere',
        }, false);
        objectsChanged = true;
      }
    }

    if (objectsChanged) {
      this.emit('objects_changed', this.objects);
    }

    if (changed) {
      this.emit('nearby_changed', this.nearbyUsers.map((user) => ({ ...user })));
    }
  }

  public getNearbyUsers() {
    return this.nearbyUsers.map((user) => ({ ...user }));
  }

  public handleAgentMovement(data: any) {
    if (!data) return;
    const raw = data?.Data?.Position ?? data?.Position ?? data?.position ?? data;
    const pos = this.parseCoordinates(raw);
    if (pos) {
      this.avatarPosition = pos;
      if (this.camera3d) {
        this.camera3d.setOrbitTarget(pos[0], pos[1], pos[2]);
      }
      if (this.protocol.agentId && this.sceneObjects.has(this.protocol.agentId)) {
        this.upsertSceneObject({
          id: this.protocol.agentId,
          position: pos,
          avatar: true,
          name: this.protocol.authReply?.first_name || 'Me',
          shape: 'sphere',
        });
      }
      this.updateLocationDisplay();
    }
  }
}
