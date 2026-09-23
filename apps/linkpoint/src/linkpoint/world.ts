/**
 * Linkpoint PWA - World Viewer Module
 */

import { Utils } from './utils';
import { Graphics3D } from './graphics-3d';
import { Camera3D } from './camera-3d';
import { Scene3D } from './scene-3d';

export class WorldViewer extends Utils.EventEmitter {
  /**
   * Desktop sessions establish a native simulator circuit and stream decoded
   * object transforms. Browser sessions consume only login/capability LLSD.
   * Keep the distinction explicit so metadata login cannot be mistaken for a
   * live scene stream.
   */
  public get liveSceneSupported() {
    return this.protocol.connected && this.protocol.authReply?.native_scene === true;
  }
  public protocol: any;
  public canvas: HTMLCanvasElement | null = null;
  public graphics3d: Graphics3D | null = null;
  public camera3d: Camera3D | null = null;
  public scene3d: Scene3D | null = null;
  private animationId: number | null = null;
  private resizeAttached = false;
  private readonly handleResize = () => this.resizeCanvas();
  public use3D: boolean = true;

  public region: any = { name: 'Region unavailable', x: 0, y: 0 };
  public objects: any[] = [];
  public nearbyUsers: any[] = [];
  public avatarPosition: [number, number, number] | null = null;
  private sceneObjects = new Map<string, any>();
  private localObjectIds = new Map<number, string>();

  public getDataStatus() {
    if (!this.protocol.connected) return 'Disconnected';
    return this.liveSceneSupported
      ? 'Live simulator scene stream'
      : 'Connected: region metadata only (scene streaming unavailable)';
  }

  constructor(protocolManager: any) {
    super();
    this.protocol = protocolManager;
    this.protocol.on('connected', (reply: any) => {
      this.region = { name: reply.sim_name || reply.region_name || 'Unknown region', x: Number(reply.region_x) || 0, y: Number(reply.region_y) || 0 };
      this.emit('region_changed', this.region);
    });
    this.protocol.on('RegionHandshake', (data: any) => {
      this.region = {
        id: data.regionID || data.region_id,
        name: data.regionName || data.region_name || data.name || 'Unknown region',
        x: data.regionX ?? data.region_x ?? 0,
        y: data.regionY ?? data.region_y ?? 0,
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
    this.protocol.on('ParcelProperties', (data: any) => {
      const parcels = data?.ParcelData || data?.parcelData || [];
      const parcel = Array.isArray(parcels) ? parcels[0] : parcels;
      if (!parcel) return;
      this.region = { ...this.region, parcel: { ...parcel } };
      this.emit('parcel_changed', { ...parcel });
      this.emit('region_changed', { ...this.region });
    });
    this.protocol.on('scene:object-add', (object: any) => this.upsertSceneObject(object));
    this.protocol.on('scene:object-update', (object: any) => this.upsertSceneObject(object));
    this.protocol.on('scene:object-remove', (object: any) => this.removeSceneObject(object));
  }

  async init() {
    const canvas = document.getElementById('world-canvas') as HTMLCanvasElement;
    if (!canvas) return;
    if (this.graphics3d && this.canvas === canvas) {
      this.resizeCanvas();
      this.startRendering();
      return;
    }
    this.destroyRenderer();
    this.canvas = canvas;

    try {
      this.graphics3d = new Graphics3D(this.canvas);
      await this.graphics3d.init();

      this.camera3d = new Camera3D();
      this.camera3d.setPosition(128, 138, 35);
      this.camera3d.setRotation(-0.3, -Math.PI / 2, 0);
      this.camera3d.setMode('orbit');
      this.camera3d.setOrbitTarget(128, 128, 25);

      this.scene3d = new Scene3D(this.graphics3d, this.camera3d);
      await this.scene3d.init();
      for (const object of this.sceneObjects.values()) this.applySceneObject(object);

      this.startRendering();
      this.updateLocationDisplay();
    } catch (error) {
      console.error('3D initialization failed:', error);
      this.use3D = false;
    }

    window.addEventListener('resize', this.handleResize);
    this.resizeAttached = true;
    this.resizeCanvas();
  }

  private resizeCanvas() {
    if (!this.canvas) return;
    const parent = this.canvas.parentElement;
    if (parent) {
      this.canvas.width = parent.clientWidth;
      this.canvas.height = parent.clientHeight;
      if (this.graphics3d) this.graphics3d.resize(this.canvas.width, this.canvas.height);
      if (this.camera3d && this.canvas.height > 0) this.camera3d.setAspect(this.canvas.width / this.canvas.height);
    }
  }

  public startRendering() {
    if (this.animationId !== null) return;
    const render = (time: number) => {
      if (this.use3D && this.scene3d && this.camera3d) {
        this.camera3d.updateMatrices();
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

  public destroyRenderer() {
    this.stopRendering();
    if (this.resizeAttached) window.removeEventListener('resize', this.handleResize);
    this.resizeAttached = false;
    this.graphics3d?.destroy();
    this.graphics3d = null;
    this.camera3d = null;
    this.scene3d = null;
    this.canvas = null;
  }

  public updateLocationDisplay() {
    const regionName = document.getElementById('region-name');
    const coordinates = document.getElementById('coordinates');
    if (regionName) regionName.textContent = this.region.name;
    if (coordinates && this.camera3d) {
      const [x, y, z] = this.camera3d.position;
      coordinates.textContent = `${Math.floor(x)}, ${Math.floor(y)}, ${Math.floor(z)}`;
    }
  }

  public moveCamera(dx: number, dy: number, dz: number) {
    if (this.camera3d) {
      this.camera3d.move(dy, dx, dz);
      this.updateLocationDisplay();
    }
  }

  private quaternionToEuler([x, y, z, w]: number[]) {
    const sinX = 2 * (w * x + y * z);
    const cosX = 1 - 2 * (x * x + y * y);
    const sinY = Math.max(-1, Math.min(1, 2 * (w * y - z * x)));
    const sinZ = 2 * (w * z + x * y);
    const cosZ = 1 - 2 * (y * y + z * z);
    return [Math.atan2(sinX, cosX), Math.asin(sinY), Math.atan2(sinZ, cosZ)];
  }

  private upsertSceneObject(object: any) {
    if (!object?.id) return;
    this.sceneObjects.set(object.id, object);
    if (object.localId) this.localObjectIds.set(object.localId, object.id);
    this.objects = Array.from(this.sceneObjects.values());
    this.applySceneObject(object);
    this.emit('objects_changed', this.objects);
  }

  private applySceneObject(object: any) {
    if (!this.scene3d) return;
    const position = Array.isArray(object.position) ? object.position : [0, 0, 0];
    const rotation = Array.isArray(object.rotation) && object.rotation.length === 4 ? object.rotation : [0, 0, 0, 1];
    const scale = Array.isArray(object.scale) ? object.scale : [1, 1, 1];
    const config = {
      mesh: object.avatar ? 'sphere' : 'cube',
      position,
      rotation: this.quaternionToEuler(rotation),
      scale,
      color: object.avatar ? [0.3, 0.65, 1, 1] : [0.8, 0.8, 0.8, 1],
    };
    if (this.scene3d.objects.has(object.id)) this.scene3d.updateObject(object.id, config);
    else this.scene3d.addObject(object.id, config);
  }

  private removeSceneObject(object: any) {
    const id = object.id && this.sceneObjects.has(object.id)
      ? object.id
      : this.localObjectIds.get(object.localId);
    if (!id) return;
    this.sceneObjects.delete(id);
    this.localObjectIds.delete(object.localId);
    this.scene3d?.removeObject(id);
    this.objects = Array.from(this.sceneObjects.values());
    this.emit('objects_changed', this.objects);
  }

  private updateCoarseLocations(data: any) {
    const locations = data?.Location_Fields || data?.locations || [];
    const agents = data?.AgentData_Fields || data?.agents || [];
    const you = Number(data?.Index_Field?.You ?? data?.youIndex ?? -1);
    const next: any[] = [];
    for (let index = 0; index < locations.length; index++) {
      const location = locations[index];
      const position: [number, number, number] = [
        Number(location.X ?? location.x ?? 0),
        Number(location.Y ?? location.y ?? 0),
        Number(location.Z ?? location.z ?? 0) * 4,
      ];
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
}
