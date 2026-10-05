import type { PbrMaterialAttributes, SceneEntity, SkinningMetadata, TextureTransform } from "../../viewer-types/src/index";

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

interface EntityRecord {
  entity: SceneEntity;
  rootNode: any;
  boxFallbackMesh?: any;
  importedMeshes: any[];
  importedSkeletons: any[];
  material?: any;
  textures: any[];
  lastMeshUri?: string;
}

/** Babylon is lazy-loaded so chat, inventory, and login never pay the 3D cost. */
export async function createBabylonRenderer(canvas: HTMLCanvasElement): Promise<ViewerRenderer> {
  const [
    engineModule,
    nullEngineModule,
    sceneModule,
    cameraModule,
    mathModule,
    lightModule,
    meshModule,
    nodeModule,
    standardMaterialModule,
    pbrMaterialModule,
    textureModule,
    loaderModule,
    colorModule,
  ] = await Promise.all([
    import("@babylonjs/core/Engines/engine"),
    import("@babylonjs/core/Engines/nullEngine"),
    import("@babylonjs/core/scene"),
    import("@babylonjs/core/Cameras/arcRotateCamera"),
    import("@babylonjs/core/Maths/math.vector"),
    import("@babylonjs/core/Lights/hemisphericLight"),
    import("@babylonjs/core/Meshes/meshBuilder"),
    import("@babylonjs/core/Meshes/transformNode"),
    import("@babylonjs/core/Materials/standardMaterial"),
    import("@babylonjs/core/Materials/PBR/pbrMetallicRoughnessMaterial"),
    import("@babylonjs/core/Materials/Textures/texture"),
    import("@babylonjs/core/Loading/sceneLoader"),
    import("@babylonjs/core/Maths/math.color"),
    import("@babylonjs/loaders/glTF"),
  ]);

  const { Engine } = engineModule;
  const { NullEngine } = nullEngineModule;
  const { Scene } = sceneModule;
  const { ArcRotateCamera } = cameraModule;
  const { Vector3, Quaternion } = mathModule;
  const { HemisphericLight } = lightModule;
  const { MeshBuilder } = meshModule;
  const { TransformNode } = nodeModule;
  const { StandardMaterial } = standardMaterialModule;
  const { PBRMetallicRoughnessMaterial } = pbrMaterialModule;
  const { Texture } = textureModule;
  const { SceneLoader } = loaderModule;
  const { Color3, Color4 } = colorModule;

  const isEngineSupported = typeof Engine.isSupported === "function" ? Engine.isSupported() : false;
  const engine = isEngineSupported
    ? new Engine(canvas, true, { preserveDrawingBuffer: false, stencil: true, adaptToDeviceRatio: true })
    : new NullEngine();

  const scene = new Scene(engine);
  scene.clearColor = new Color4(0.025, 0.045, 0.075, 1);
  scene.ambientColor = new Color3(0.18, 0.22, 0.3);

  const camera = new ArcRotateCamera("viewer-camera", -Math.PI / 2.5, Math.PI / 3.2, 48, new Vector3(128, 128, 0), scene);
  camera.lowerRadiusLimit = 2;
  camera.upperRadiusLimit = 1024;
  camera.wheelPrecision = 12;
  camera.panningSensibility = 70;
  if (canvas && typeof canvas.addEventListener === "function") {
    camera.attachControl(canvas, true);
  }

  const skyLight = new HemisphericLight("region-sky", new Vector3(0.2, 0.4, 1), scene);
  skyLight.intensity = 0.95;
  skyLight.groundColor = new Color3(0.08, 0.11, 0.14);

  const ground = MeshBuilder.CreateGround("region-ground", { width: 256, height: 256, subdivisions: 2 }, scene);
  ground.position.set(128, 128, -0.52);
  const groundMaterial = new StandardMaterial("region-ground-material", scene);
  groundMaterial.diffuseColor = new Color3(0.06, 0.16, 0.13);
  groundMaterial.specularColor = new Color3(0.04, 0.08, 0.07);
  ground.material = groundMaterial;

  const defaultEntityMaterial = new StandardMaterial("scene-entity-material", scene);
  defaultEntityMaterial.diffuseColor = new Color3(0.25, 0.72, 0.62);
  defaultEntityMaterial.specularColor = new Color3(0.18, 0.35, 0.32);

  const entityRecords = new Map<string, EntityRecord>();
  let entities: readonly SceneEntity[] = [];

  const cleanupRecord = (record: EntityRecord) => {
    record.textures.forEach((t) => t.dispose());
    record.textures = [];
    if (record.material && record.material !== defaultEntityMaterial) {
      record.material.dispose();
      record.material = undefined;
    }
    record.importedMeshes.forEach((m) => m.dispose());
    record.importedMeshes = [];
    record.importedSkeletons.forEach((s) => s.dispose());
    record.importedSkeletons = [];
    if (record.boxFallbackMesh) {
      record.boxFallbackMesh.dispose();
      record.boxFallbackMesh = undefined;
    }
    if (record.rootNode) {
      record.rootNode.dispose();
    }
  };

  const remove = (id: string) => {
    const record = entityRecords.get(id);
    if (record) {
      cleanupRecord(record);
      entityRecords.delete(id);
    }
  };

  const applyTextureTransform = (texture: InstanceType<typeof Texture>, transform?: TextureTransform) => {
    if (!transform) return;
    if (transform.offset) {
      texture.uOffset = transform.offset[0];
      texture.vOffset = transform.offset[1];
    }
    if (transform.scale) {
      texture.uScale = transform.scale[0];
      texture.vScale = transform.scale[1];
    }
    if (transform.rotation !== undefined) {
      texture.wAng = transform.rotation;
    }
  };

  const applyMaterialToRecord = (record: EntityRecord, materialAttr?: PbrMaterialAttributes) => {
    // Dispose old textures/material if changed
    record.textures.forEach((t) => t.dispose());
    record.textures = [];
    if (record.material && record.material !== defaultEntityMaterial) {
      record.material.dispose();
      record.material = undefined;
    }

    if (!materialAttr) {
      record.material = defaultEntityMaterial;
    } else {
      const pbr = new PBRMetallicRoughnessMaterial(`pbr-${record.entity.id}`, scene);
      pbr.metallic = materialAttr.metallic ?? 0.0;
      pbr.roughness = materialAttr.roughness ?? 0.5;

      if (materialAttr.baseColor) {
        pbr.baseColor = new Color3(materialAttr.baseColor[0], materialAttr.baseColor[1], materialAttr.baseColor[2]);
        pbr.alpha = materialAttr.baseColor[3] ?? 1.0;
      }
      if (materialAttr.emissiveColor) {
        pbr.emissiveColor = new Color3(materialAttr.emissiveColor[0], materialAttr.emissiveColor[1], materialAttr.emissiveColor[2]);
      }

      if (materialAttr.baseColorTextureUri) {
        const tex = new Texture(materialAttr.baseColorTextureUri, scene);
        applyTextureTransform(tex, materialAttr.textureTransform);
        pbr.baseTexture = tex;
        record.textures.push(tex);
      }
      if (materialAttr.normalMapUri) {
        const tex = new Texture(materialAttr.normalMapUri, scene);
        applyTextureTransform(tex, materialAttr.textureTransform);
        pbr.normalTexture = tex;
        record.textures.push(tex);
      }
      if (materialAttr.metallicRoughnessTextureUri) {
        const tex = new Texture(materialAttr.metallicRoughnessTextureUri, scene);
        applyTextureTransform(tex, materialAttr.textureTransform);
        pbr.metallicRoughnessTexture = tex;
        record.textures.push(tex);
      }
      if (materialAttr.emissiveTextureUri) {
        const tex = new Texture(materialAttr.emissiveTextureUri, scene);
        applyTextureTransform(tex, materialAttr.textureTransform);
        pbr.emissiveTexture = tex;
        record.textures.push(tex);
      }

      record.material = pbr;
    }

    const mat = record.material;
    if (record.boxFallbackMesh) {
      record.boxFallbackMesh.material = mat;
    }
    record.importedMeshes.forEach((m) => {
      m.material = mat;
    });
  };

  const applySkinningToRecord = (record: EntityRecord, skinning?: SkinningMetadata) => {
    if (!skinning?.joints) return;
    for (const joint of skinning.joints) {
      for (const skeleton of record.importedSkeletons) {
        const boneIndex = skeleton.getBoneIndexByName(joint.jointName);
        if (boneIndex !== -1) {
          const bone = skeleton.bones[boneIndex];
          if (bone) {
            bone.setScale(new Vector3(joint.weight, joint.weight, joint.weight));
          }
        }
      }
    }
  };

  const upsert = (entity: SceneEntity) => {
    let record = entityRecords.get(entity.id);

    if (record && record.lastMeshUri !== entity.meshUri) {
      cleanupRecord(record);
      entityRecords.delete(entity.id);
      record = undefined;
    }

    if (!record) {
      const rootNode = new TransformNode(`root-${entity.id}`, scene);
      rootNode.metadata = { viewerEntityId: entity.id };

      record = {
        entity,
        rootNode,
        importedMeshes: [],
        importedSkeletons: [],
        textures: [],
        lastMeshUri: entity.meshUri,
      };
      entityRecords.set(entity.id, record);

      if (entity.meshUri) {
        // Create box fallback while loading external mesh asset
        const fallbackBox = MeshBuilder.CreateBox(`entity-fallback-${entity.id}`, { size: 1 }, scene);
        fallbackBox.parent = rootNode;
        fallbackBox.metadata = { viewerEntityId: entity.id };
        record.boxFallbackMesh = fallbackBox;

        applyMaterialToRecord(record, entity.material);

        // Async load mesh
        let rootUrl = "";
        let fileName = entity.meshUri;
        const lastSlash = entity.meshUri.lastIndexOf("/");
        if (lastSlash !== -1) {
          rootUrl = entity.meshUri.substring(0, lastSlash + 1);
          fileName = entity.meshUri.substring(lastSlash + 1);
        }

        SceneLoader.ImportMeshAsync("", rootUrl, fileName, scene)
          .then((result) => {
            if (!entityRecords.has(entity.id)) {
              // Entity was removed while loading
              result.meshes.forEach((m) => m.dispose());
              result.skeletons.forEach((s) => s.dispose());
              return;
            }
            if (record!.boxFallbackMesh) {
              record!.boxFallbackMesh.dispose();
              record!.boxFallbackMesh = undefined;
            }
            record!.importedMeshes = result.meshes;
            record!.importedSkeletons = result.skeletons;
            result.meshes.forEach((m) => {
              if (!m.parent) m.parent = record!.rootNode;
              m.metadata = { viewerEntityId: entity.id };
              if (record!.material) m.material = record!.material;
            });
            applySkinningToRecord(record!, record!.entity.skinning);
          })
          .catch(() => {
            // Retain box fallback primitive if mesh streaming failed
          });
      } else {
        const boxMesh = MeshBuilder.CreateBox(`entity-${entity.id}`, { size: 1 }, scene);
        boxMesh.parent = rootNode;
        boxMesh.metadata = { viewerEntityId: entity.id };
        record.boxFallbackMesh = boxMesh;
        applyMaterialToRecord(record, entity.material);
      }
    } else {
      record.entity = entity;
      applyMaterialToRecord(record, entity.material);
      applySkinningToRecord(record, entity.skinning);
    }

    // Update root transforms
    record.rootNode.position.set(...entity.position);
    record.rootNode.scaling.set(...(entity.scale ?? [1, 1, 1]));
    if (entity.rotation) {
      record.rootNode.rotationQuaternion = new Quaternion(...entity.rotation);
    } else {
      record.rootNode.rotationQuaternion = Quaternion.Identity();
    }
  };

  const focusAll = () => {
    const bounds = getSceneBounds(entities);
    camera.setTarget(new Vector3(...bounds.center));
    camera.radius = Math.min(camera.upperRadiusLimit ?? 1024, Math.max(16, bounds.radius * 2.8));
  };

  engine.runRenderLoop(() => scene.render());
  const resize = () => engine.resize();
  const resizeObserver = typeof ResizeObserver === "undefined" ? null : new ResizeObserver(resize);
  if (resizeObserver && canvas) {
    resizeObserver.observe(canvas);
  }
  if (typeof window !== "undefined") {
    window.addEventListener("resize", resize);
  }
  resize();

  return {
    applySnapshot(nextEntities) {
      entities = nextEntities;
      const ids = new Set(nextEntities.map(({ id }) => id));
      for (const [id] of entityRecords) {
        if (!ids.has(id)) {
          remove(id);
        }
      }
      nextEntities.forEach(upsert);
    },
    upsert,
    remove,
    focusAll,
    dispose() {
      resizeObserver?.disconnect();
      if (typeof window !== "undefined") {
        window.removeEventListener("resize", resize);
      }
      for (const id of Array.from(entityRecords.keys())) {
        remove(id);
      }
      defaultEntityMaterial.dispose();
      groundMaterial.dispose();
      scene.dispose();
      engine.dispose();
    },
  };
}
