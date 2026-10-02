import { createRequire } from 'node:module';
import { describe, expect, it } from 'vitest';

const require = createRequire(import.meta.url);
const sharp = require('sharp');
const { computeNormals, decodeJPEG2000, decodeSculpt, normalizeLLMesh, normalizeGLTFMaterial } = require('../../../core/sl-asset-decoder.cjs');

describe('Second Life glTF PBR materials', () => {
  it('normalizes metallic-roughness effects and SL texture asset references', () => {
    const material = normalizeGLTFMaterial({
      images: [{ uri: 'asset://base-id' }, { uri: 'uuid:normal-id' }, { uri: 'sl://orm-id' }],
      textures: [{ source: 0 }, { source: 1 }, { source: 2 }],
      materials: [{
        alphaMode: 'MASK', alphaCutoff: .35, doubleSided: true, emissiveFactor: [.1, .2, .3],
        pbrMetallicRoughness: {
          baseColorFactor: [.8, .7, .6, .5], metallicFactor: .9, roughnessFactor: .25,
          baseColorTexture: { index: 0, extensions: { KHR_texture_transform: { offset: [.1, .2], scale: [2, 3], rotation: .4 } } },
          metallicRoughnessTexture: { index: 2 },
        },
        normalTexture: { index: 1 },
      }],
    });

    expect(material).toMatchObject({ metallic: .9, roughness: .25, alphaMode: 'MASK', alphaCutoff: .35, doubleSided: true });
    expect(material.textures.baseColor).toMatchObject({ textureId: 'base-id', offset: [.1, .2], scale: [2, 3], rotation: .4 });
    expect(material.textures.normal.textureId).toBe('normal-id');
    expect(material.textures.metallicRoughness.textureId).toBe('orm-id');
  });
});

describe('UDP-referenced LLMesh asset decoding', () => {
  it('preserves every material, LOD, skin joint, and normalized vertex influence', () => {
    const point = (x: number, y: number, z = 0) => ({ x, y, z });
    const triangle = (materialIndex: number, withWeights = false) => ({
      position: [point(0, 0), point(1, 0), point(0, 1)],
      normal: [point(0, 0, 1), point(0, 0, 1), point(0, 0, 1)],
      texCoord0: [point(0, 0), point(1, 0), point(0, 1)],
      triangleList: [0, 1, 2], materialIndex,
      weights: withWeights ? [{ 2: 32768, 4: 32767 }, { 2: 65535 }, { 4: 65535 }] : undefined,
    });
    const mesh = normalizeLLMesh({
      version: 1,
      lodLevels: { high_lod: [triangle(0, true), triangle(1)], low_lod: [triangle(0)] },
      skin: {
        jointNames: ['mPelvis', 'mTorso'], bindShapeMatrix: Array.from({ length: 16 }, (_, i) => i),
        inverseBindMatrix: [Array.from({ length: 16 }, (_, i) => i + 16)],
      },
    });

    expect(mesh.selectedLod).toBe('high_lod');
    expect(mesh.parts).toHaveLength(2);
    expect(mesh.lods.low_lod).toHaveLength(1);
    expect(mesh.parts[0].jointWeights.slice(0, 4).reduce((sum: number, weight: number) => sum + weight, 0)).toBeCloseTo(1);
    expect(mesh.skin.jointNames).toEqual(['mPelvis', 'mTorso']);
    expect(mesh.skin.inverseBindMatrices[0]).toHaveLength(16);
  });

  it('rejects corrupt triangle indices instead of sending unsafe geometry to WebGL', () => {
    expect(() => normalizeLLMesh({
      lodLevels: { high_lod: [{ position: [{ x: 0, y: 0, z: 0 }], triangleList: [0, 1, 0] }] },
    })).toThrow(/invalid vertex index/);
  });
});

describe('simulator image asset decoding', () => {
  it('normalizes decoded image assets to browser-ready RGBA', async () => {
    const source = await sharp({
      create: { width: 2, height: 1, channels: 3, background: { r: 10, g: 20, b: 30 } },
    }).png().toBuffer();
    const result = await decodeJPEG2000(source);

    expect(result).toMatchObject({ width: 2, height: 1 });
    expect(Buffer.from(result.rgba, 'base64')).toEqual(Buffer.from([10, 20, 30, 255, 10, 20, 30, 255]));
  });

  it('turns decoded sculpt-map pixels into indexed geometry', async () => {
    const source = await sharp({
      create: { width: 8, height: 8, channels: 3, background: { r: 128, g: 64, b: 255 } },
    }).png().toBuffer();
    const geometry = await decodeSculpt(source, 3);

    expect(geometry.vertices).toHaveLength(8 * 8 * 3);
    expect(geometry.normals).toHaveLength(geometry.vertices.length);
    expect(geometry.texCoords).toHaveLength(8 * 8 * 2);
    expect(geometry.indices.length).toBeGreaterThan(0);
  });

  // A sphere sculpt map the way the default map is laid out: top of the image is the north pole,
  // columns run counter-clockwise around Z, and the last column repeats the first.
  const sphereMap = async (size = 32) => {
    const pixels = Buffer.alloc(size * size * 3);
    for (let y = 0; y < size; y++) for (let x = 0; x < size; x++) {
      const u = (x / (size - 1)) * Math.PI * 2, v = (y / (size - 1)) * Math.PI;
      const o = (y * size + x) * 3;
      pixels[o] = Math.round((0.5 + 0.5 * Math.cos(u) * Math.sin(v)) * 255);
      pixels[o + 1] = Math.round((0.5 + 0.5 * Math.sin(u) * Math.sin(v)) * 255);
      pixels[o + 2] = Math.round((0.5 + 0.5 * Math.cos(v)) * 255);
    }
    return sharp(pixels, { raw: { width: size, height: size, channels: 3 } }).png().toBuffer();
  };
  const outward = (g: any) => {
    let good = 0, total = 0;
    for (let i = 0; i < g.vertices.length; i += 3) {
      const len = Math.hypot(g.vertices[i], g.vertices[i + 1], g.vertices[i + 2]);
      if (len < 1e-6 || Math.hypot(g.normals[i], g.normals[i + 1], g.normals[i + 2]) < 1e-6) continue;
      total++;
      if (g.vertices[i] * g.normals[i] + g.vertices[i + 1] * g.normals[i + 1] + g.vertices[i + 2] * g.normals[i + 2] > 0) good++;
    }
    return good / total;
  };

  it('faces a standard sphere sculpt outward, with unit smooth normals', async () => {
    const geometry = await decodeSculpt(await sphereMap(), 1);
    expect(outward(geometry)).toBeGreaterThan(0.97);
    for (let i = 0; i < geometry.normals.length; i += 3) {
      expect(Math.hypot(geometry.normals[i], geometry.normals[i + 1], geometry.normals[i + 2])).toBeCloseTo(1, 3);
    }
    expect(new Set(geometry.normals.map((n: number) => n.toFixed(2))).size).toBeGreaterThan(10);
  });

  it('turns the shape inside out for the invert flag and back again when mirrored as well', async () => {
    const source = await sphereMap();
    expect(outward(await decodeSculpt(source, 1 | 0x40))).toBeLessThan(0.03);
    // Mirror alone keeps the front outward (the viewer reverses sampling to compensate); both flags invert again.
    expect(outward(await decodeSculpt(source, 1 | 0x80))).toBeGreaterThan(0.97);
    expect(outward(await decodeSculpt(source, 1 | 0x80 | 0x40))).toBeLessThan(0.03);
  });

  it('mirrors a sculpt across X by negating the coordinate', async () => {
    const source = await sphereMap();
    const plain = await decodeSculpt(source, 1), mirrored = await decodeSculpt(source, 1 | 0x80);
    const range = (g: any, k: number) => { const v = g.vertices.filter((_: number, i: number) => i % 3 === k); return [Math.min(...v), Math.max(...v)]; };
    expect(range(mirrored, 0)[0]).toBeCloseTo(-range(plain, 0)[1], 1);
    expect(range(mirrored, 2)[1]).toBeCloseTo(range(plain, 2)[1], 1);
    // The sphere is symmetric, so the north pole is still on top and still a single point.
    const top = mirrored.vertices.slice((32 - 1) * 32 * 3, (32 - 1) * 32 * 3 + 3);
    expect(top[2]).toBeGreaterThan(0.45);
  });

  it('reads rows from the bottom of the image up, as the viewer does', async () => {
    // Bottom half of the map is z = 0, top half z = 1.
    const size = 8, pixels = Buffer.alloc(size * size * 3);
    for (let y = 0; y < size; y++) for (let x = 0; x < size; x++) pixels.set([x * 30, 128, y < size / 2 ? 255 : 0], (y * size + x) * 3);
    const source = await sharp(pixels, { raw: { width: size, height: size, channels: 3 } }).png().toBuffer();
    const geometry = await decodeSculpt(source, 3);
    expect(geometry.vertices[2]).toBeLessThan(0);          // first row = bottom of the image = low blue
    expect(geometry.vertices[(8 * 7) * 3 + 2]).toBeGreaterThan(0); // last row = top of the image
  });

  it('closes a torus sculpt around both axes and leaves a plane open', async () => {
    const size = 8, pixels = Buffer.alloc(size * size * 3);
    for (let y = 0; y < size; y++) for (let x = 0; x < size; x++) pixels.set([x * 30, y * 30, (x + y) * 10], (y * size + x) * 3);
    const source = await sharp(pixels, { raw: { width: size, height: size, channels: 3 } }).png().toBuffer();
    const torus = await decodeSculpt(source, 2), plane = await decodeSculpt(source, 3), cylinder = await decodeSculpt(source, 4);
    expect(torus.indices.length).toBe(8 * 8 * 6);
    expect(cylinder.indices.length).toBe(8 * 7 * 6);
    expect(plane.indices.length).toBe(7 * 7 * 6);
  });

  it('does not add a degenerate wrap face when the last column repeats the first', async () => {
    const closed = await decodeSculpt(await sphereMap(), 1);
    expect(closed.indices.length).toBe(31 * 31 * 6);
  });
});

describe('computeNormals', () => {
  it('points counter-clockwise triangles along their right-hand normal and survives degenerate faces', () => {
    expect(computeNormals([0, 0, 0, 1, 0, 0, 0, 1, 0], [0, 1, 2])).toEqual([0, 0, 1, 0, 0, 1, 0, 0, 1]);
    expect(computeNormals([0, 0, 0, 0, 0, 0, 0, 0, 0], [0, 1, 2])).toEqual([0, 0, 1, 0, 0, 1, 0, 0, 1]);
  });
});
