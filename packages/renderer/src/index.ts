import type { SceneEntity } from "../../viewer-types/src/index";

export interface ViewerRenderer {
  applySnapshot(entities: readonly SceneEntity[]): void;
  upsert(entity: SceneEntity): void;
  remove(id: string): void;
  dispose(): void;
}

/** Babylon is loaded only when the world screen requests a renderer. */
export async function createBabylonRenderer(canvas: HTMLCanvasElement): Promise<ViewerRenderer> {
  const [{ Engine }, { Scene }, { FreeCamera }, { Vector3 }, { HemisphericLight }, { MeshBuilder }] = await Promise.all([
    import("@babylonjs/core/Engines/engine"), import("@babylonjs/core/scene"), import("@babylonjs/core/Cameras/freeCamera"),
    import("@babylonjs/core/Maths/math.vector"), import("@babylonjs/core/Lights/hemisphericLight"), import("@babylonjs/core/Meshes/meshBuilder"),
  ]);
  const engine = new Engine(canvas, true, { preserveDrawingBuffer: false, stencil: false });
  const scene = new Scene(engine);
  const camera = new FreeCamera("camera", new Vector3(128, 128, 24), scene);
  camera.attachControl(canvas, true);
  new HemisphericLight("ambient", new Vector3(0, 0, 1), scene);
  const meshes = new Map<string, ReturnType<typeof MeshBuilder.CreateBox>>();
  const upsert = (entity: SceneEntity) => {
    const mesh = meshes.get(entity.id) ?? MeshBuilder.CreateBox(entity.id, { size: 1 }, scene);
    mesh.position.set(...entity.position);
    if (entity.scale) mesh.scaling.set(...entity.scale);
    meshes.set(entity.id, mesh);
  };
  engine.runRenderLoop(() => scene.render());
  const resize = () => engine.resize(); window.addEventListener("resize", resize);
  return {
    applySnapshot(entities) { const ids = new Set(entities.map(({ id }) => id)); for (const [id, mesh] of meshes) if (!ids.has(id)) { mesh.dispose(); meshes.delete(id); } entities.forEach(upsert); },
    upsert,
    remove(id) { meshes.get(id)?.dispose(); meshes.delete(id); },
    dispose() { window.removeEventListener("resize", resize); scene.dispose(); engine.dispose(); },
  };
}
