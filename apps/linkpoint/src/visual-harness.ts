import { createBabylonRenderer } from '@linkpoint/renderer';
import { Graphics3D } from './linkpoint/graphics-3d';
import { packJointRows } from './linkpoint/skinning';
import type { SceneEntity } from '@linkpoint/viewer-types';

declare global {
  interface Window {
    isRenderComplete?: boolean;
    renderError?: string;
  }
}

// Generate procedural 2x2 data URL image for textures in Babylon renderer
function createProceduralDataUrl(r: number, g: number, b: number, a = 255): string {
  const canvas = document.createElement('canvas');
  canvas.width = 16;
  canvas.height = 16;
  const ctx = canvas.getContext('2d');
  if (ctx) {
    ctx.fillStyle = `rgba(${r},${g},${b},${a / 255})`;
    ctx.fillRect(0, 0, 16, 16);
    // Add grid lines for texture transform verification
    ctx.fillStyle = `rgba(${255 - r},${255 - g},${255 - b},0.8)`;
    ctx.fillRect(0, 0, 8, 8);
    ctx.fillRect(8, 8, 8, 8);
  }
  return canvas.toDataURL('image/png');
}

// Procedural normal map data URL (flat normal with diagonal cross pattern)
function createNormalMapDataUrl(): string {
  const canvas = document.createElement('canvas');
  canvas.width = 16;
  canvas.height = 16;
  const ctx = canvas.getContext('2d');
  if (ctx) {
    ctx.fillStyle = 'rgb(128,128,255)'; // flat normal pointing (0,0,1)
    ctx.fillRect(0, 0, 16, 16);
    ctx.fillStyle = 'rgb(200,100,255)';
    ctx.fillRect(4, 4, 8, 8);
  }
  return canvas.toDataURL('image/png');
}

// Matrix helper utilities for Graphics3D WebGL camera & transformations
function mat4Identity(): Float32Array {
  const m = new Float32Array(16);
  m[0] = 1; m[5] = 1; m[10] = 1; m[15] = 1;
  return m;
}

function mat4Perspective(fovyRad: number, aspect: number, near: number, far: number): Float32Array {
  const f = 1.0 / Math.tan(fovyRad / 2.0);
  const m = new Float32Array(16);
  m[0] = f / aspect;
  m[5] = f;
  m[10] = (far + near) / (near - far);
  m[11] = -1.0;
  m[14] = (2.0 * far * near) / (near - far);
  return m;
}

function mat4LookAt(eye: [number, number, number], center: [number, number, number], up: [number, number, number]): Float32Array {
  let zx = eye[0] - center[0];
  let zy = eye[1] - center[1];
  let zz = eye[2] - center[2];
  let len = Math.hypot(zx, zy, zz) || 1;
  zx /= len; zy /= len; zz /= len;

  let xx = up[1] * zz - up[2] * zy;
  let xy = up[2] * zx - up[0] * zz;
  let xz = up[0] * zy - up[1] * zx;
  len = Math.hypot(xx, xy, xz) || 1;
  xx /= len; xy /= len; xz /= len;

  let yx = zy * xz - zz * xy;
  let yy = zz * xx - zx * xz;
  let yz = zx * xy - zy * xx;

  const m = new Float32Array(16);
  m[0] = xx; m[1] = yx; m[2] = zx; m[3] = 0;
  m[4] = xy; m[5] = yy; m[6] = zy; m[7] = 0;
  m[8] = xz; m[9] = yz; m[10] = zz; m[11] = 0;
  m[12] = -(xx * eye[0] + xy * eye[1] + xz * eye[2]);
  m[13] = -(yx * eye[0] + yy * eye[1] + yz * eye[2]);
  m[14] = -(zx * eye[0] + zy * eye[1] + zz * eye[2]);
  m[15] = 1.0;
  return m;
}

function mat4Multiply(a: Float32Array, b: Float32Array): Float32Array {
  const out = new Float32Array(16);
  for (let i = 0; i < 4; i++) {
    for (let j = 0; j < 4; j++) {
      out[j * 4 + i] =
        a[i] * b[j * 4] +
        a[i + 4] * b[j * 4 + 1] +
        a[i + 8] * b[j * 4 + 2] +
        a[i + 12] * b[j * 4 + 3];
    }
  }
  return out;
}

function mat4Translation(x: number, y: number, z: number): Float32Array {
  const m = mat4Identity();
  m[12] = x; m[13] = y; m[14] = z;
  return m;
}

function mat3NormalFromMat4(m: Float32Array): Float32Array {
  const out = new Float32Array(9);
  out[0] = m[0]; out[1] = m[1]; out[2] = m[2];
  out[3] = m[4]; out[4] = m[5]; out[5] = m[6];
  out[6] = m[8]; out[7] = m[9]; out[8] = m[10];
  return out;
}

// Procedural 3D Mesh Builders
function createSphereMesh(latBands = 24, longBands = 24, radius = 1.5) {
  const vertices: number[] = [];
  const normals: number[] = [];
  const texCoords: number[] = [];
  const tangents: number[] = [];
  const indices: number[] = [];

  for (let lat = 0; lat <= latBands; lat++) {
    const theta = (lat * Math.PI) / latBands;
    const sinTheta = Math.sin(theta);
    const cosTheta = Math.cos(theta);

    for (let long = 0; long <= longBands; long++) {
      const phi = (long * 2 * Math.PI) / longBands;
      const sinPhi = Math.sin(phi);
      const cosPhi = Math.cos(phi);

      const x = cosPhi * sinTheta;
      const y = cosTheta;
      const z = sinPhi * sinTheta;
      const u = 1 - long / longBands;
      const v = 1 - lat / latBands;

      normals.push(x, y, z);
      texCoords.push(u, v);
      vertices.push(radius * x, radius * y, radius * z);
      tangents.push(-sinPhi, 0, cosPhi);
    }
  }

  for (let lat = 0; lat < latBands; lat++) {
    for (let long = 0; long < longBands; long++) {
      const first = lat * (longBands + 1) + long;
      const second = first + longBands + 1;
      indices.push(first, second, first + 1);
      indices.push(second, second + 1, first + 1);
    }
  }

  return { vertices, indices, normals, texCoords, tangents };
}

function createSegmentedCylinderMesh(rings = 12, radialSlices = 16, radius = 0.8, height = 3.0) {
  const vertices: number[] = [];
  const normals: number[] = [];
  const texCoords: number[] = [];
  const tangents: number[] = [];
  const joints: number[] = [];
  const weights: number[] = [];
  const indices: number[] = [];

  for (let r = 0; r <= rings; r++) {
    const yFactor = r / rings;
    const y = (yFactor - 0.5) * height;

    for (let s = 0; s <= radialSlices; s++) {
      const phi = (s * 2 * Math.PI) / radialSlices;
      const nx = Math.cos(phi);
      const nz = Math.sin(phi);

      vertices.push(radius * nx, y, radius * nz);
      normals.push(nx, 0, nz);
      texCoords.push(s / radialSlices, yFactor);
      tangents.push(-nz, 0, nx);

      // Skinning influences: Joint 0 (base) vs Joint 1 (top half)
      const w1 = Math.max(0, Math.min(1, yFactor));
      const w0 = 1.0 - w1;
      joints.push(0, 1, 0, 0);
      weights.push(w0, w1, 0, 0);
    }
  }

  for (let r = 0; r < rings; r++) {
    for (let s = 0; s < radialSlices; s++) {
      const i0 = r * (radialSlices + 1) + s;
      const i1 = i0 + radialSlices + 1;
      indices.push(i0, i1, i0 + 1);
      indices.push(i1, i1 + 1, i0 + 1);
    }
  }

  return { vertices, indices, normals, texCoords, tangents, skin: { joints, weights } };
}

function createTerrainGridMesh(gridSize = 32, scale = 16.0) {
  const vertices: number[] = [];
  const normals: number[] = [];
  const texCoords: number[] = [];
  const tangents: number[] = [];
  const indices: number[] = [];

  for (let z = 0; z <= gridSize; z++) {
    const tz = z / gridSize;
    const vz = (tz - 0.5) * scale;
    for (let x = 0; x <= gridSize; x++) {
      const tx = x / gridSize;
      const vx = (tx - 0.5) * scale;
      const vy = 1.2 * Math.sin(vx * 0.4) * Math.cos(vz * 0.4);

      vertices.push(vx, vy, vz);
      texCoords.push(tx * 4, tz * 4);

      // Approximate terrain surface normal
      const nx = -0.48 * Math.cos(vx * 0.4) * Math.cos(vz * 0.4);
      const nz = 0.48 * Math.sin(vx * 0.4) * Math.sin(vz * 0.4);
      const len = Math.hypot(nx, 1.0, nz);
      normals.push(nx / len, 1.0 / len, nz / len);
      tangents.push(1.0, 0.0, 0.0);
    }
  }

  for (let z = 0; z < gridSize; z++) {
    for (let x = 0; x < gridSize; x++) {
      const i0 = z * (gridSize + 1) + x;
      const i1 = i0 + gridSize + 1;
      indices.push(i0, i1, i0 + 1);
      indices.push(i1, i1 + 1, i0 + 1);
    }
  }

  return { vertices, indices, normals, texCoords, tangents };
}

// Main Harness Execution Function
async function renderFixture(fixtureName: string) {
  const canvas = document.getElementById('render-canvas') as HTMLCanvasElement;
  if (!canvas) throw new Error('Canvas element not found');

  window.isRenderComplete = false;

  switch (fixtureName) {
    case 'renderer-pbr': {
      const renderer = await createBabylonRenderer(canvas);
      const entities: SceneEntity[] = [
        {
          id: 'pbr-sphere-metallic',
          position: [126.5, 128, 1.5],
          scale: [2.5, 2.5, 2.5],
          material: {
            metallic: 0.9,
            roughness: 0.15,
            baseColor: [0.85, 0.25, 0.2, 1.0],
            baseColorTextureUri: createProceduralDataUrl(220, 60, 50),
            normalMapUri: createNormalMapDataUrl(),
          },
        },
        {
          id: 'pbr-box-dielectric',
          position: [129.5, 128, 1.5],
          scale: [2.5, 2.5, 2.5],
          material: {
            metallic: 0.05,
            roughness: 0.75,
            baseColor: [0.2, 0.8, 0.4, 1.0],
            emissiveColor: [0.05, 0.15, 0.05],
            baseColorTextureUri: createProceduralDataUrl(50, 200, 100),
          },
        },
      ];
      renderer.applySnapshot(entities);
      renderer.focusAll();
      // Allow frame rendering to settle
      await new Promise((resolve) => setTimeout(resolve, 300));
      window.isRenderComplete = true;
      break;
    }

    case 'renderer-skinning': {
      const renderer = await createBabylonRenderer(canvas);
      const entities: SceneEntity[] = [
        {
          id: 'skinned-avatar-mesh',
          position: [128, 128, 1.0],
          scale: [2.0, 4.0, 2.0],
          material: {
            metallic: 0.2,
            roughness: 0.4,
            baseColor: [0.95, 0.65, 0.25, 1.0],
            baseColorTextureUri: createProceduralDataUrl(240, 160, 60),
          },
          skinning: {
            skeletonRootId: 'skel-root',
            joints: [
              { jointName: 'mPelvis', weight: 1.0 },
              { jointName: 'mTorso', weight: 0.8 },
              { jointName: 'mChest', weight: 0.6 },
            ],
          },
        },
      ];
      renderer.applySnapshot(entities);
      renderer.focusAll();
      await new Promise((resolve) => setTimeout(resolve, 300));
      window.isRenderComplete = true;
      break;
    }

    case 'renderer-terrain-sky': {
      const renderer = await createBabylonRenderer(canvas);
      const entities: SceneEntity[] = [
        {
          id: 'landscape-element-1',
          position: [124, 124, 0.5],
          scale: [3, 3, 3],
          material: {
            metallic: 0.1,
            roughness: 0.9,
            baseColor: [0.4, 0.3, 0.2, 1.0],
          },
        },
        {
          id: 'landscape-element-2',
          position: [132, 130, 1.0],
          scale: [4, 4, 4],
          material: {
            metallic: 0.3,
            roughness: 0.5,
            baseColor: [0.2, 0.5, 0.7, 1.0],
          },
        },
      ];
      renderer.applySnapshot(entities);
      renderer.focusAll();
      await new Promise((resolve) => setTimeout(resolve, 300));
      window.isRenderComplete = true;
      break;
    }

    case 'graphics3d-pbr': {
      const g3d = new Graphics3D(canvas);
      await g3d.init();

      const sphere = createSphereMesh(32, 32, 1.5);
      g3d.createMesh('pbrSphere', sphere.vertices, sphere.indices, sphere.normals, sphere.texCoords, sphere.tangents);

      // Create textures
      const texData = new Uint8Array(64 * 64 * 4);
      const normData = new Uint8Array(64 * 64 * 4);
      for (let i = 0; i < 64 * 64; i++) {
        texData[i * 4] = 60 + (i % 64) * 3;
        texData[i * 4 + 1] = 160;
        texData[i * 4 + 2] = 220 - (i % 64) * 2;
        texData[i * 4 + 3] = 255;

        normData[i * 4] = 128 + Math.floor(Math.sin(i / 10) * 40);
        normData[i * 4 + 1] = 128 + Math.floor(Math.cos(i / 10) * 40);
        normData[i * 4 + 2] = 240;
        normData[i * 4 + 3] = 255;
      }
      g3d.createTexture('pbrColorTex', 64, 64, texData);
      g3d.createTexture('pbrNormalTex', 64, 64, normData);

      const proj = mat4Perspective((45 * Math.PI) / 180, 800 / 600, 0.1, 100.0);
      const view = mat4LookAt([0, 2, 5], [0, 0, 0], [0, 1, 0]);
      const model = mat4Identity();
      const normMat = mat3NormalFromMat4(model);

      g3d.clear([0.05, 0.08, 0.12, 1.0]);
      g3d.drawMesh('pbrSphere', 'basic', {
        uProjectionMatrix: proj,
        uViewMatrix: view,
        uModelMatrix: model,
        uNormalMatrix: normMat,
        uLightPos: [4.0, 6.0, 5.0],
        uLightColor: [1.0, 0.95, 0.85],
        uAmbientColor: [0.2, 0.25, 0.35],
        uCameraPos: [0, 2, 5],
        uColor: [0.9, 0.9, 0.95, 1.0],
        uMetallic: 0.85,
        uRoughness: 0.2,
        uEmissive: [0.0, 0.0, 0.0],
        uOcclusionFactor: 1.0,
        uUseTexture: true,
        uTextureName: 'pbrColorTex',
        uUseNormalTexture: true,
        uNormalTextureName: 'pbrNormalTex',
        uTexTransform: [1, 1, 0, 0],
        uTexRotation: 0,
        uAlphaMode: 0,
      });

      window.isRenderComplete = true;
      break;
    }

    case 'graphics3d-skinned': {
      const g3d = new Graphics3D(canvas);
      await g3d.init();

      const cylinder = createSegmentedCylinderMesh(16, 20, 0.8, 3.2);
      g3d.createMesh(
        'skinnedCylinder',
        cylinder.vertices,
        cylinder.indices,
        cylinder.normals,
        cylinder.texCoords,
        cylinder.tangents,
        cylinder.skin
      );

      // Create joint matrices: Joint 0 identity, Joint 1 rotated 45 degrees around Z
      const j0 = mat4Identity();
      const j1 = mat4Identity();
      const angle = Math.PI / 4;
      const cosA = Math.cos(angle);
      const sinA = Math.sin(angle);
      j1[0] = cosA; j1[1] = sinA;
      j1[4] = -sinA; j1[5] = cosA;
      j1[13] = 0.5; // translate slightly along Y

      const jointRows = packJointRows([j0, j1], g3d.maxJoints || 32);

      const proj = mat4Perspective((45 * Math.PI) / 180, 800 / 600, 0.1, 100.0);
      const view = mat4LookAt([0, 1, 5.5], [0, 0, 0], [0, 1, 0]);
      const model = mat4Identity();
      const normMat = mat3NormalFromMat4(model);

      g3d.clear([0.05, 0.08, 0.12, 1.0]);
      g3d.drawMesh('skinnedCylinder', 'skinned', {
        uProjectionMatrix: proj,
        uViewMatrix: view,
        uModelMatrix: model,
        uNormalMatrix: normMat,
        uJointRows: jointRows,
        uLightPos: [3.0, 5.0, 4.0],
        uLightColor: [1.0, 1.0, 0.9],
        uAmbientColor: [0.25, 0.25, 0.3],
        uCameraPos: [0, 1, 5.5],
        uColor: [0.95, 0.55, 0.2, 1.0],
        uMetallic: 0.3,
        uRoughness: 0.4,
        uEmissive: [0.0, 0.0, 0.0],
        uOcclusionFactor: 1.0,
        uUseTexture: false,
        uTexTransform: [1, 1, 0, 0],
        uTexRotation: 0,
        uAlphaMode: 0,
      });

      window.isRenderComplete = true;
      break;
    }

    case 'graphics3d-terrain': {
      const g3d = new Graphics3D(canvas);
      await g3d.init();

      const terrain = createTerrainGridMesh(32, 16.0);
      g3d.createMesh('terrainGrid', terrain.vertices, terrain.indices, terrain.normals, terrain.texCoords, terrain.tangents);

      // Create procedural detail texture
      const detailTex = new Uint8Array(32 * 32 * 4);
      for (let i = 0; i < 32 * 32; i++) {
        detailTex[i * 4] = 40 + (i % 8) * 10;
        detailTex[i * 4 + 1] = 140 + (i % 5) * 15;
        detailTex[i * 4 + 2] = 50;
        detailTex[i * 4 + 3] = 255;
      }
      g3d.createTexture('terrainGrassTex', 32, 32, detailTex);

      const proj = mat4Perspective((45 * Math.PI) / 180, 800 / 600, 0.1, 100.0);
      const view = mat4LookAt([0, 8, 12], [0, 0, 0], [0, 1, 0]);
      const model = mat4Identity();
      const normMat = mat3NormalFromMat4(model);

      g3d.clear([0.1, 0.15, 0.25, 1.0]);
      g3d.drawMesh('terrainGrid', 'terrain', {
        uProjectionMatrix: proj,
        uViewMatrix: view,
        uModelMatrix: model,
        uNormalMatrix: normMat,
        uSunDir: [0.4, 0.8, 0.4],
        uSunColor: [1.0, 0.95, 0.85],
        uAmbientColor: [0.2, 0.25, 0.35],
        uDetail0Name: 'terrainGrassTex',
      });

      window.isRenderComplete = true;
      break;
    }

    case 'graphics3d-sky': {
      const g3d = new Graphics3D(canvas);
      await g3d.init();

      // Inverted box or dome for sky dome pass
      const skyDome = createSphereMesh(16, 16, 50.0);
      g3d.createMesh('skyDomeMesh', skyDome.vertices, skyDome.indices, skyDome.normals, skyDome.texCoords, skyDome.tangents);

      const proj = mat4Perspective((50 * Math.PI) / 180, 800 / 600, 0.1, 200.0);
      const view = mat4LookAt([0, 1, 0], [0, 2, -10], [0, 1, 0]);
      const model = mat4Identity();

      g3d.clear([0.02, 0.03, 0.06, 1.0]);

      // Draw Sky Pass
      g3d.drawMesh('skyDomeMesh', 'sky', {
        uProjectionMatrix: proj,
        uViewMatrix: view,
        uModelMatrix: model,
        uSunDir: [0.3, 0.5, -0.8],
        uSunColor: [1.0, 0.9, 0.7],
        uSkyColor: [0.2, 0.45, 0.85],
        uHorizonColor: [0.7, 0.8, 0.95],
      }, { cullFace: false, depthWrite: false });

      // Draw Water Plane Pass
      const waterPlane = createTerrainGridMesh(16, 20.0);
      g3d.createMesh('waterPlaneMesh', waterPlane.vertices, waterPlane.indices, waterPlane.normals, waterPlane.texCoords, waterPlane.tangents);
      const waterModel = mat4Translation(0, -1.0, 0);

      g3d.drawMesh('waterPlaneMesh', 'water', {
        uProjectionMatrix: proj,
        uViewMatrix: view,
        uModelMatrix: waterModel,
        uNormalMatrix: mat3NormalFromMat4(waterModel),
        uTime: 1.5,
        uSunDir: [0.3, 0.5, -0.8],
        uWaterColor: [0.05, 0.25, 0.45],
      }, { blend: true });

      window.isRenderComplete = true;
      break;
    }

    default:
      throw new Error(`Unknown fixture name: ${fixtureName}`);
  }
}

// Automatically execute on page load based on URL query parameter ?fixture=...
const urlParams = new URLSearchParams(window.location.search);
const fixture = urlParams.get('fixture') || 'renderer-pbr';

renderFixture(fixture).catch((err) => {
  console.error('Visual test fixture error:', err);
  window.renderError = err.message || String(err);
});
