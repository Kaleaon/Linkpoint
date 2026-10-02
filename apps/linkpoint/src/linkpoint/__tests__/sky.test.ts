import { describe, expect, it } from 'vitest';
import {
  WATER_WAVES, DEFAULT_WATER_HEIGHT, computeSkyUniforms, computeWaterUniforms, createSkyDome, createStarField,
  createWaterPlane, isUnderWater, readScalar, readVec3,
} from '../sky';

describe('computeSkyUniforms', () => {
  it('applies (blueHorizon + sunlight + ambient) * blueDensity and hazeDensity * ambient', () => {
    const uniforms = computeSkyUniforms({
      blueHorizon: [0.1, 0.2, 0.3],
      sunlightColor: [0.2, 0.2, 0.2],
      ambient: [0.1, 0.1, 0.2],
      blueDensity: [0.5, 1, 0.25],
      hazeDensity: 0.5,
      hazeHorizon: 0.3,
      starBrightness: 0.4,
    });
    expect(uniforms.skyColor[0]).toBeCloseTo(0.2, 6); // (0.1+0.2+0.1)*0.5
    expect(uniforms.skyColor[1]).toBeCloseTo(0.5, 6); // (0.2+0.2+0.1)*1
    expect(uniforms.skyColor[2]).toBeCloseTo(0.175, 6); // (0.3+0.2+0.2)*0.25
    expect(uniforms.hazeColor).toEqual([0.05, 0.05, 0.1]);
    expect(uniforms.hazeHorizon).toBe(0.3);
    expect(uniforms.starBrightness).toBeCloseTo(0.4, 6);
  });

  it('accepts snake_case keys, scalar-in-array values and object vectors, and clamps to 0..1', () => {
    const uniforms = computeSkyUniforms({
      blue_horizon: { x: 1, y: 1, z: 1 },
      sunlight_color: [1, 1, 1, 1],
      ambient: { r: 1, g: 1, b: 1 },
      blue_density: [1, 1, 1, 1],
      haze_density: [2, 0, 0, 1],
      haze_horizon: [0.19, 0, 0, 1],
      star_brightness: 9,
    });
    expect(uniforms.skyColor).toEqual([1, 1, 1]);
    expect(uniforms.hazeColor).toEqual([1, 1, 1]);
    expect(uniforms.hazeHorizon).toBeCloseTo(0.19, 6);
    expect(uniforms.starBrightness).toBe(1);
  });

  it('keeps a usable sky when the simulator sends nothing or partial data', () => {
    for (const input of [null, undefined, {}, { sunlightColor: [1, 1, 1] }, 'bad']) {
      const uniforms = computeSkyUniforms(input);
      expect(uniforms.skyColor.every((v) => v >= 0 && v <= 1)).toBe(true);
      expect(uniforms.hazeHorizon).toBeGreaterThan(0);
    }
  });

  it('rejects non-numeric vectors', () => {
    expect(readVec3(['a', 1, 2], [9, 9, 9])).toEqual([9, 9, 9]);
    expect(readVec3([1, 2], [9, 9, 9])).toEqual([9, 9, 9]);
    expect(readScalar(null, 4)).toBe(4);
    expect(readScalar('', 4)).toBe(4);
    expect(readScalar([0.5], 4)).toBe(0.5);
  });
});

describe('water', () => {
  it('keeps the four-wave tables consistent and matches the recovered Lumiya values', () => {
    expect(WATER_WAVES.frequency.length).toBe(4);
    expect(WATER_WAVES.phase.length).toBe(4);
    expect(WATER_WAVES.amplitude.length).toBe(4);
    expect(WATER_WAVES.direction.length).toBe(8);
    // Values read from the TerrainPatchGeometry smali (array-data blocks).
    expect(Array.from(WATER_WAVES.frequency)).toEqual([17.951958, 12.566371, 8.975979, 15.707963]);
    expect(Array.from(WATER_WAVES.phase)).toEqual([1.73, 0.64, 1.27, 0.9]);
    expect(Array.from(WATER_WAVES.amplitude)).toEqual([0.5, 0.5, 0.3, 0.4]);
    expect(Array.from(WATER_WAVES.direction)).toEqual([1, 0.3, 0.4, 0.75, -0.5, 0.7, 0.63, -0.3]);
  });

  it('lightens dark EEP fog colours and falls back to the default tint', () => {
    const dark = computeWaterUniforms({ waterFogColor: [0, 0, 0] }, 17);
    expect(dark.height).toBe(17);
    expect(dark.color).toEqual([0.2, 0.2, 0.3]);
    expect(computeWaterUniforms(null).color).toEqual([0.4, 0.4, 0.6]);
    expect(computeWaterUniforms(undefined, Number.NaN).height).toBe(DEFAULT_WATER_HEIGHT);
  });

  it('detects the camera being below the surface', () => {
    expect(isUnderWater(19.9, 20)).toBe(true);
    expect(isUnderWater(20, 20)).toBe(false);
    expect(isUnderWater(Number.NaN, 20)).toBe(false);
  });

  it('builds a horizontal plane covering the region', () => {
    const { vertices, indices } = createWaterPlane();
    expect(indices.length).toBe(6);
    const xs = vertices.filter((_, i) => i % 3 === 0), ys = vertices.filter((_, i) => i % 3 === 1);
    expect(Math.min(...xs)).toBeLessThan(0);
    expect(Math.max(...xs)).toBeGreaterThan(256);
    expect(Math.min(...ys)).toBeLessThan(0);
    expect(Math.max(...ys)).toBeGreaterThan(256);
  });
});

describe('sky geometry', () => {
  it('builds a closed unit icosphere with inward-facing triangles', () => {
    const { vertices, indices } = createSkyDome(2);
    expect(indices.length / 3).toBe(20 * 4 * 4);
    for (let i = 0; i < vertices.length; i += 3) expect(Math.hypot(vertices[i], vertices[i + 1], vertices[i + 2])).toBeCloseTo(1, 6);

    const edges = new Map<string, number>();
    let outward = 0;
    for (let i = 0; i < indices.length; i += 3) {
      const tri = [indices[i], indices[i + 1], indices[i + 2]];
      for (let k = 0; k < 3; k++) {
        const a = tri[k], b = tri[(k + 1) % 3];
        const key = a < b ? `${a}:${b}` : `${b}:${a}`;
        edges.set(key, (edges.get(key) || 0) + 1);
      }
      const p = tri.map((index) => vertices.slice(index * 3, index * 3 + 3));
      const u = p[1].map((v, axis) => v - p[0][axis]), w = p[2].map((v, axis) => v - p[0][axis]);
      const normal = [u[1] * w[2] - u[2] * w[1], u[2] * w[0] - u[0] * w[2], u[0] * w[1] - u[1] * w[0]];
      const centroid = [0, 1, 2].map((axis) => p[0][axis] + p[1][axis] + p[2][axis]);
      if (normal[0] * centroid[0] + normal[1] * centroid[1] + normal[2] * centroid[2] > 0) outward++;
    }
    expect([...edges.values()].every((count) => count === 2)).toBe(true); // watertight
    expect(outward).toBe(0);
  });

  it('subdivision 0 is the 12-vertex icosahedron', () => {
    const { vertices, indices } = createSkyDome(0);
    expect(vertices.length / 3).toBe(12);
    expect(indices.length / 3).toBe(20);
  });

  it('places a deterministic star field on the unit sphere, covering both hemispheres', () => {
    const a = createStarField(500), b = createStarField(500);
    expect(a.vertices).toEqual(b.vertices);
    expect(a.indices).toEqual(Array.from({ length: 500 }, (_, i) => i));
    let up = 0;
    for (let i = 0; i < a.vertices.length; i += 3) {
      expect(Math.hypot(a.vertices[i], a.vertices[i + 1], a.vertices[i + 2])).toBeCloseTo(1, 6);
      if (a.vertices[i + 2] > 0) up++;
    }
    expect(up).toBeGreaterThan(200);
    expect(up).toBeLessThan(300);
    expect(createStarField(10, 1).vertices).not.toEqual(createStarField(10, 2).vertices);
  });
});
