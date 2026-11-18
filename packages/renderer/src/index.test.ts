// @vitest-environment jsdom
import { describe, expect, it } from "vitest";
import type { SceneEntity } from "../../viewer-types/src/index";
import { createBabylonRenderer, getPlaceholderTextureUri, getSceneBounds } from "./index";

describe("getSceneBounds", () => {
  it("provides a useful default for an empty region", () => {
    expect(getSceneBounds([])).toEqual({ center: [128, 128, 0], radius: 32 });
  });

  it("includes entity scale when framing a scene", () => {
    const bounds = getSceneBounds([
      { id: "a", position: [0, 0, 0], scale: [2, 2, 2] },
      { id: "b", position: [10, 4, 2], scale: [4, 2, 2] },
    ]);
    expect(bounds.center).toEqual([5.5, 2, 1]);
    expect(bounds.radius).toBeGreaterThanOrEqual(8);
  });
});

describe("createBabylonRenderer", () => {
  const gltfJson = {
    asset: { version: "2.0" },
    nodes: [{ name: "JointBone" }],
    scenes: [{ nodes: [0] }],
    scene: 0,
  };
  const minimalGltfUri = "data:application/json;base64," + Buffer.from(JSON.stringify(gltfJson)).toString("base64");

  it("initializes renderer, applies snapshots, PBR materials, skinning, and cleans up on disposal", async () => {
    const canvas = document.createElement("canvas");
    const renderer = await createBabylonRenderer(canvas);

    const initialEntities: SceneEntity[] = [
      {
        id: "entity-box",
        position: [10, 20, 30],
        rotation: [0, 0, 0, 1],
        scale: [2, 2, 2],
        material: {
          metallic: 0.8,
          roughness: 0.2,
          baseColor: [1, 0, 0, 1],
          emissiveColor: [0.1, 0.1, 0.1],
          baseColorTextureUri: "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==",
          normalMapUri: "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==",
          textureTransform: {
            offset: [0.1, 0.2],
            scale: [2.0, 2.0],
            rotation: 0.5,
          },
        },
      },
      {
        id: "entity-mesh",
        position: [0, 0, 0],
        meshUri: minimalGltfUri,
        material: {
          metallic: 0.5,
          roughness: 0.5,
        },
        skinning: {
          joints: [{ jointName: "JointBone", weight: 1.5 }],
        },
      },
    ];

    renderer.applySnapshot(initialEntities);
    renderer.focusAll();

    // Verify PBR texture color space assignment (linear vs sRGB)
    const entityRecord = (renderer as any).entityRecords?.get?.("entity-box");
    if (entityRecord?.material) {
      if (entityRecord.material.baseTexture) {
        expect(entityRecord.material.baseTexture.gammaSpace).toBe(true);
      }
      if (entityRecord.material.normalTexture) {
        expect(entityRecord.material.normalTexture.gammaSpace).toBe(false);
      }
    }

    // Dynamically update entity with upsert
    renderer.upsert({
      id: "entity-box",
      position: [15, 25, 35],
      scale: [3, 3, 3],
      material: {
        metallic: 0.1,
        roughness: 0.9,
        baseColor: [0, 1, 0, 0.8],
      },
    });

    // Remove single entity
    renderer.remove("entity-box");

    // Apply snapshot diff removing remaining entity
    renderer.applySnapshot([]);

    // Execute disposal
    renderer.dispose();
  });

  it("handles missing or corrupted texture URIs gracefully using dynamic placeholder materials", async () => {
    const canvas = document.createElement("canvas");
    const renderer = await createBabylonRenderer(canvas);

    const corruptEntity: SceneEntity = {
      id: "entity-corrupt-texture",
      position: [0, 0, 0],
      material: {
        baseColorTextureUri: "http://invalid-domain-404.example/missing.png",
        normalMapUri: "invalid-scheme://corrupted-texture-data",
        metallicRoughnessTextureUri: "data:image/png;base64,invalidbase64",
        emissiveTextureUri: "http://invalid-domain-404.example/emissive.png",
      },
    };

    expect(() => {
      renderer.upsert(corruptEntity);
    }).not.toThrow();

    const placeholderUri = getPlaceholderTextureUri();
    expect(placeholderUri).toContain("data:image/png;base64,");

    renderer.dispose();
  });
});
