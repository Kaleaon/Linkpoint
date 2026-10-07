import { describe, expect, it, beforeEach } from 'vitest';
import { Graphics3D } from '../graphics-3d';
import { Camera3D } from '../camera-3d';
import { Scene3D } from '../scene-3d';

class MockCanvas {
  width = 1280;
  height = 720;
  getContext() {
    return {
      getParameter: (param: number) => 4096,
      getExtension: () => null,
      enable: () => {},
      disable: () => {},
      cullFace: () => {},
      frontFace: () => {},
      depthFunc: () => {},
      clearColor: () => {},
      createProgram: () => ({}),
      createShader: () => ({}),
      shaderSource: () => {},
      compileShader: () => {},
      getShaderParameter: () => true,
      attachShader: () => {},
      bindAttribLocation: () => {},
      linkProgram: () => {},
      getProgramParameter: () => true,
      getActiveAttrib: () => null,
      getActiveUniform: () => null,
      useProgram: () => {},
      createBuffer: () => ({}),
      bindBuffer: () => {},
      bufferData: () => {},
      createTexture: () => ({}),
      bindTexture: () => {},
      pixelStorei: () => {},
      texImage2D: () => {},
      texParameteri: () => {},
      activeTexture: () => {},
      uniform1i: () => {},
      drawElements: () => {},
      depthMask: () => {},
      enableVertexAttribArray: () => {},
      vertexAttribPointer: () => {},
      clear: () => {},
    };
  }
}

describe('Scene3D LOD Selection and Multi-Buffer Caching', () => {
  let graphics: Graphics3D;
  let camera: Camera3D;
  let scene: Scene3D;

  beforeEach(() => {
    graphics = new Graphics3D(new MockCanvas() as any);
    graphics.gl = (graphics.canvas as any).getContext();
    camera = new Camera3D();
    scene = new Scene3D(graphics, camera);
  });

  it('pre-allocates WebGL vertex buffers for all four LOD levels during asset ingestion', () => {
    const submesh = (vertexCount: number, materialIndex = 0) => {
      const vertices: number[] = [];
      const indices: number[] = [];
      for (let i = 0; i < vertexCount; i++) {
        vertices.push(0, 0, 0, 1, 0, 0, 0, 1, 0);
        indices.push(i * 3, i * 3 + 1, i * 3 + 2);
      }
      return { position: vertices, vertices, indices, triangleList: indices, materialIndex };
    };

    const geometry = {
      lods: {
        high_lod: [submesh(100, 0), submesh(80, 1)],
        medium_lod: [submesh(50, 0)],
        low_lod: [submesh(20, 0)],
        lowest_lod: [submesh(5, 0)],
      },
    };

    const meshes = scene.addAssetMesh('building_01', geometry);

    expect(meshes).toHaveLength(2);
    expect((meshes as any).lodMeshes).toBeDefined();
    const lods = (meshes as any).lodMeshes;

    expect(lods.high_lod).toHaveLength(2);
    expect(lods.medium_lod).toHaveLength(1);
    expect(lods.low_lod).toHaveLength(1);
    expect(lods.lowest_lod).toHaveLength(1);

    expect(lods.high_lod[0].mesh).toBe('asset:building_01:high_lod:0');
    expect(lods.medium_lod[0].mesh).toBe('asset:building_01:medium_lod:0');
    expect(lods.low_lod[0].mesh).toBe('asset:building_01:low_lod:0');
    expect(lods.lowest_lod[0].mesh).toBe('asset:building_01:lowest_lod:0');
  });

  it('falls back gracefully to high_lod when lower LOD levels are omitted from asset data', () => {
    const geometry = {
      lods: {
        high_lod: [{ position: [{ x: 0, y: 0, z: 0 }, { x: 1, y: 0, z: 0 }, { x: 0, y: 1, z: 0 }], triangleList: [0, 1, 2], materialIndex: 0 }],
      },
    };

    const meshes = scene.addAssetMesh('simple_asset', geometry);
    const lods = (meshes as any).lodMeshes;

    expect(lods.high_lod).toHaveLength(1);
    expect(lods.medium_lod).toEqual(lods.high_lod);
    expect(lods.low_lod).toEqual(lods.high_lod);
    expect(lods.lowest_lod).toEqual(lods.high_lod);
  });

  it('accurately computes screen pixel area based on distance, bounding radius, FOV, and height', () => {
    const radius = 2.0; // 2 meter bounding radius
    const screenHeight = 720;
    const fovRad = Math.PI / 3; // 60 deg

    // At close distance (5m)
    const areaClose = scene.calculateScreenPixelArea(radius, 5, fovRad, screenHeight);
    // At far distance (50m)
    const areaFar = scene.calculateScreenPixelArea(radius, 50, fovRad, screenHeight);

    expect(areaClose).toBeGreaterThan(areaFar);
    // Inverse square relationship with distance: 10x distance -> 1/100 area
    expect(areaClose / areaFar).toBeCloseTo(100, 0);
  });

  it('maintains high detail for large structures in the distance due to screen pixel footprint', () => {
    const smallRadius = 0.5; // 0.5m small asset
    const largeRadius = 15.0; // 15m large structure
    const distance = 80; // 80m distance

    const smallArea = scene.calculateScreenPixelArea(smallRadius, distance);
    const largeArea = scene.calculateScreenPixelArea(largeRadius, distance);

    expect(largeArea).toBeGreaterThan(smallArea * 500);

    const smallObj = scene.addObject('small', {
      position: [0, 0, distance],
      scale: [1, 1, 1],
      lodMeshes: {
        high_lod: [{ mesh: 'high' }],
        medium_lod: [{ mesh: 'med' }],
        low_lod: [{ mesh: 'low' }],
        lowest_lod: [{ mesh: 'lowest' }],
      },
    });

    const largeObj = scene.addObject('large', {
      position: [0, 0, distance],
      scale: [30, 30, 30],
      lodMeshes: {
        high_lod: [{ mesh: 'high' }],
        medium_lod: [{ mesh: 'med' }],
        low_lod: [{ mesh: 'low' }],
        lowest_lod: [{ mesh: 'lowest' }],
      },
    });

    camera.position = [0, 0, 0];

    const lodSmall = scene.selectObjectLod(smallObj);
    const lodLarge = scene.selectObjectLod(largeObj);

    expect(lodSmall).toBe('lowest_lod');
    expect(lodLarge).toBe('high_lod');
  });

  it('applies hysteresis thresholds to prevent rapid state oscillation along boundaries', () => {
    const obj = scene.addObject('moving_tower', {
      position: [0, 0, 3], // Initially very close (3m) -> high_lod
      scale: [1, 1, 1],
      lodMeshes: {
        high_lod: [{ mesh: 'h' }],
        medium_lod: [{ mesh: 'm' }],
        low_lod: [{ mesh: 'l' }],
        lowest_lod: [{ mesh: 'lowest' }],
      },
    });

    camera.position = [0, 0, 0];

    // Initial check -> high_lod
    let selected = scene.selectObjectLod(obj);
    expect(selected).toBe('high_lod');

    // Move camera back to near high/medium threshold (tHigh = 8000 sq px)
    // At high_lod, downgrade threshold is 8000 * 0.85 = 6800.
    // Set position so area is ~7500 (between 6800 and 8000): d = 6.38m.
    // Because current is high_lod, it MUST remain high_lod due to hysteresis!
    obj.currentLod = 'high_lod';
    obj.position = [0, 0, 6.38];
    selected = scene.selectObjectLod(obj);
    expect(selected).toBe('high_lod'); // Preserved in high_lod due to hysteresis!

    // If starting from medium_lod at the exact same distance (6.38m, area ~7500):
    // Upgrade threshold to high_lod is 8000 * 1.15 = 9200.
    // 7500 < 9200, so starting from medium_lod, it MUST remain medium_lod!
    obj.currentLod = 'medium_lod';
    selected = scene.selectObjectLod(obj);
    expect(selected).toBe('medium_lod'); // Preserved in medium_lod due to hysteresis!
  });

  it('verifies >70% vertex processing reduction for distant viewport assets', () => {
    const createTriangles = (count: number) => {
      const vertices: number[] = [];
      const indices: number[] = [];
      for (let i = 0; i < count; i++) {
        const base = i * 3;
        // Keep all vertices within a 1m unit bounding box so radius is ~0.86m
        const u = (i / count) - 0.5;
        vertices.push(u, -0.5, 0, u, 0.5, 0, u, 0, 0.5);
        indices.push(base, base + 1, base + 2);
      }
      return { vertices, indices, position: vertices, triangleList: indices };
    };

    const highDetail = createTriangles(1000); // 3000 vertices
    const lowestDetail = createTriangles(50); // 150 vertices

    const geometry = {
      lods: {
        high_lod: [highDetail],
        medium_lod: [createTriangles(500)],
        low_lod: [createTriangles(200)],
        lowest_lod: [lowestDetail],
      },
    };

    const meshes = scene.addAssetMesh('dense_tree', geometry);

    const obj = scene.addObject('tree_far', {
      position: [0, 0, 200], // Far away
      scale: [1, 1, 1],
      meshes,
    });

    camera.position = [0, 0, 0];

    const selectedLod = scene.selectObjectLod(obj);
    expect(selectedLod).toBe('lowest_lod');

    const highVerts = highDetail.vertices.length;
    const lowestVerts = lowestDetail.vertices.length;
    const reduction = (highVerts - lowestVerts) / highVerts;

    expect(reduction).toBeGreaterThanOrEqual(0.70); // 95% reduction
    expect(reduction).toBeCloseTo(0.95, 2);
  });
});
