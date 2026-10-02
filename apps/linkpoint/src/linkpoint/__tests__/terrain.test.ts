import { describe, expect, it } from 'vitest';
import { compositionTexture, layerWeights, noise2, terrainComposition, turbulence2 } from '../terrain';

const flat = (size: number, height: number) => new Array(size * size).fill(height);
const params = { startHeights: [20, 20, 20, 20], heightRanges: [60, 60, 60, 60], origin: [256000, 256000] as [number, number] };

describe('terrain composition', () => {
  it('has deterministic noise within a sensible range and zero at lattice points', () => {
    expect(noise2(3, 4)).toBe(0);
    expect(noise2(1.37, 2.91)).toBe(noise2(1.37, 2.91));
    let min = 1, max = -1;
    for (let i = 0; i < 4000; i++) { const v = noise2(i * 0.173, i * 0.0917); min = Math.min(min, v); max = Math.max(max, v); }
    expect(min).toBeGreaterThanOrEqual(-1.01);
    expect(max).toBeLessThanOrEqual(1.01);
    expect(max - min).toBeGreaterThan(0.8);
    expect(turbulence2(1.37, 2.91, 2)).toBeGreaterThanOrEqual(0);
  });

  it('puts low land on the first layer and high land on the last, rising with height', () => {
    const low = terrainComposition(flat(16, -50), 16, params);
    const high = terrainComposition(flat(16, 400), 16, params);
    expect(Math.max(...low)).toBe(0);   // far below the start height: clamped to layer 0
    expect(Math.min(...high)).toBe(3);  // far above start + range: clamped to layer 3
    const mean = (h: number) => { const v = terrainComposition(flat(16, h), 16, params); return v.reduce((a, b) => a + b, 0) / v.length; };
    expect(mean(10)).toBeLessThan(mean(40));
    expect(mean(40)).toBeLessThan(mean(70));
    expect(mean(70)).toBeLessThan(mean(120));
  });

  it('interpolates start heights and ranges between the four corners (SW, SE, NW, NE)', () => {
    const size = 16;
    const heights = flat(size, 30);
    // raise the start height only in the east: east texels read lower on the layer scale than west
    const values = terrainComposition(heights, size, { ...params, startHeights: [0, 100, 0, 100], heightRanges: [60, 60, 60, 60] });
    const west = values[8 * size + 0], east = values[8 * size + size - 1];
    expect(west).toBeGreaterThan(east);
    // and north vs south
    const ns = terrainComposition(heights, size, { ...params, startHeights: [0, 0, 100, 100], heightRanges: [60, 60, 60, 60] });
    expect(ns[0 * size + 8]).toBeGreaterThan(ns[(size - 1) * size + 8]);
  });

  it('blends neighbouring layers with a tent and sums to one', () => {
    expect(layerWeights(0)).toEqual([1, 0, 0, 0]);
    expect(layerWeights(1)).toEqual([0, 1, 0, 0]);
    expect(layerWeights(3)).toEqual([0, 0, 0, 1]);
    const w = layerWeights(1.25);
    expect(w[1]).toBeCloseTo(0.75, 5);
    expect(w[2]).toBeCloseTo(0.25, 5);
    for (const v of [0, 0.3, 1.7, 2.99, 3, 5, -2]) expect(layerWeights(v).reduce((a, b) => a + b, 0)).toBeCloseTo(1, 5);
  });

  it('packs composition into a texture with north at the top of the flipped upload', () => {
    const size = 4;
    const values = new Float32Array(size * size);
    for (let j = 0; j < size; j++) for (let i = 0; i < size; i++) values[j * size + i] = j; // value grows to the north
    const rgba = compositionTexture(values, size);
    expect(rgba).toHaveLength(size * size * 4);
    // memory row 0 is the north edge after UNPACK_FLIP_Y
    expect(rgba[0]).toBe(Math.round((3 / 3) * 255));
    expect(rgba[(size - 1) * size * 4]).toBe(0);
    expect(rgba[3]).toBe(255);
  });
});
