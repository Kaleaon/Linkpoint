import type { SceneEntity } from "../../viewer-types/src/index";

export interface ViewerRenderer {
  applySnapshot(entities: readonly SceneEntity[]): void;
  upsert(entity: SceneEntity): void;
  remove(id: string): void;
  focusAll(): void;
  dispose(): void;
}

export interface SceneBounds {
  readonly center: readonly [number, number, number];
  readonly radius: number;
}

/** Pure scene framing shared by the renderer and its tests. */
export function getSceneBounds(entities: readonly SceneEntity[]): SceneBounds {
  if (!entities.length) return { center: [128, 128, 0], radius: 32 };
  const axes = [0, 1, 2] as const;
  const minimum = axes.map((axis) => Math.min(...entities.map((entity) => entity.position[axis] - (entity.scale?.[axis] ?? 1) / 2)));
  const maximum = axes.map((axis) => Math.max(...entities.map((entity) => entity.position[axis] + (entity.scale?.[axis] ?? 1) / 2)));
  const center = axes.map((axis) => (minimum[axis] + maximum[axis]) / 2) as [number, number, number];
  const radius = Math.max(8, Math.hypot(...axes.map((axis) => maximum[axis] - minimum[axis])) / 2);
  return { center, radius };
}

/** Babylon is lazy-loaded so chat, inventory, and login never pay the 3D cost. */
export async function createBabylonRenderer(canvas: HTMLCanvasElement): Promise<ViewerRenderer> {
  const [engineModule, sceneModule, cameraModule, mathModule, lightModule, meshModule, materialModule, colorModule] = await Promise.all([
    import("@babylonjs/core/Engines/engine"), import("@babylonjs/core/scene"), import("@babylonjs/core/Cameras/arcRotateCamera"),
    import("@babylonjs/core/Maths/math.vector"), import("@babylonjs/core/Lights/hemisphericLight"), import("@babylonjs/core/Meshes/meshBuilder"),
    import("@babylonjs/core/Materials/standardMaterial"), import("@babylonjs/core/Maths/math.color"),
  ]);
  const { Engine } = engineModule;
  const { Scene } = sceneModule;
  const { ArcRotateCamera } = cameraModule;
  const { Vector3, Quaternion } = mathModule;
  const { HemisphericLight } = lightModule;
  const { MeshBuilder } = meshModule;
  const { StandardMaterial } = materialModule;
  const { Color3, Color4 } = colorModule;

  const engine = new Engine(canvas, true, { preserveDrawingBuffer: false, stencil: true, adaptToDeviceRatio: true });
  const scene = new Scene(engine);
  scene.clearColor = new Color4(0.025, 0.045, 0.075, 1);
  scene.ambientColor = new Color3(0.18, 0.22, 0.3);

  const camera = new ArcRotateCamera("viewer-camera", -Math.PI / 2.5, Math.PI / 3.2, 48, new Vector3(128, 128, 0), scene);
  camera.lowerRadiusLimit = 2;
  camera.upperRadiusLimit = 1024;
  camera.wheelPrecision = 12;
  camera.panningSensibility = 70;
  camera.attachControl(canvas, true);

  const skyLight = new HemisphericLight("region-sky", new Vector3(0.2, 0.4, 1), scene);
  skyLight.intensity = 0.95;
  skyLight.groundColor = new Color3(0.08, 0.11, 0.14);

  const ground = MeshBuilder.CreateGround("region-ground", { width: 256, height: 256, subdivisions: 2 }, scene);
  ground.position.set(128, 128, -0.52);
  const groundMaterial = new StandardMaterial("region-ground-material", scene);
  groundMaterial.diffuseColor = new Color3(0.06, 0.16, 0.13);
  groundMaterial.specularColor = new Color3(0.04, 0.08, 0.07);
  ground.material = groundMaterial;

  const entityMaterial = new StandardMaterial("scene-entity-material", scene);
  entityMaterial.diffuseColor = new Color3(0.25, 0.72, 0.62);
  entityMaterial.specularColor = new Color3(0.18, 0.35, 0.32);
  const meshes = new Map<string, ReturnType<typeof MeshBuilder.CreateBox>>();
  let entities: readonly SceneEntity[] = [];

  const upsert = (entity: SceneEntity) => {
    const mesh = meshes.get(entity.id) ?? MeshBuilder.CreateBox(`entity-${entity.id}`, { size: 1 }, scene);
    mesh.metadata = { viewerEntityId: entity.id };
    mesh.material = entityMaterial;
    mesh.position.set(...entity.position);
    mesh.scaling.set(...(entity.scale ?? [1, 1, 1]));
    if (entity.rotation) mesh.rotationQuaternion = new Quaternion(...entity.rotation);
    meshes.set(entity.id, mesh);
  };
  const focusAll = () => {
    const bounds = getSceneBounds(entities);
    camera.setTarget(new Vector3(...bounds.center));
    camera.radius = Math.min(camera.upperRadiusLimit ?? 1024, Math.max(16, bounds.radius * 2.8));
  };

  engine.runRenderLoop(() => scene.render());
  const resize = () => engine.resize();
  const resizeObserver = typeof ResizeObserver === "undefined" ? null : new ResizeObserver(resize);
  resizeObserver?.observe(canvas);
  window.addEventListener("resize", resize);
  resize();

  return {
    applySnapshot(nextEntities) {
      entities = nextEntities;
      const ids = new Set(nextEntities.map(({ id }) => id));
      for (const [id, mesh] of meshes) if (!ids.has(id)) { mesh.dispose(); meshes.delete(id); }
      nextEntities.forEach(upsert);
    },
    upsert,
    remove(id) { meshes.get(id)?.dispose(); meshes.delete(id); },
    focusAll,
    dispose() {
      resizeObserver?.disconnect();
      window.removeEventListener("resize", resize);
      scene.dispose();
      engine.dispose();
    },
  };
}
