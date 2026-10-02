import { describe, expect, it } from 'vitest';
import { HUD_POINTS, HUD_SIZE, fitHud, hudExtents, hudProjection, hudToScreenMatrix, isHudPoint } from '../hud';

const apply = (m: ArrayLike<number>, v: number[]) => [0, 1, 2].map((r) => m[r] * v[0] + m[4 + r] * v[1] + m[8 + r] * v[2] + m[12 + r]);
const close = (a: number[], b: number[]) => a.forEach((value, i) => expect(value).toBeCloseTo(b[i], 5));
const identity = [0, 0, 0, 1];

describe('HUD attachment points (Lumiya SLAttachmentPoint)', () => {
  it('are exactly points 31 to 38', () => {
    for (let id = 0; id <= 60; id++) expect(isHudPoint(id)).toBe(id >= 31 && id <= 38);
    expect(Object.keys(HUD_POINTS)).toHaveLength(8);
    expect(isHudPoint('35')).toBe(false);
    expect(isHudPoint(undefined)).toBe(false);
  });
});

describe('HUD space to screen (Lumiya: rotate 90 about Y, then -90 about X)', () => {
  it('makes SL x the depth axis, y screen-left and z up', () => {
    const m = hudToScreenMatrix();
    close(apply(m, [1, 0, 0]), [0, 0, -1]); // forward in HUD space goes away from the viewer
    close(apply(m, [0, 1, 0]), [-1, 0, 0]); // SL +y is to the left
    close(apply(m, [0, 0, 1]), [0, 1, 0]); // up is up
  });
  it('is a proper rotation, so shapes are not mirrored', () => {
    const m = hudToScreenMatrix();
    const det = m[0] * (m[5] * m[10] - m[6] * m[9]) - m[4] * (m[1] * m[10] - m[2] * m[9]) + m[8] * (m[1] * m[6] - m[2] * m[5]);
    expect(det).toBeCloseTo(1, 6);
  });
});

describe('hudExtents', () => {
  it('measures a single box, and uses all eight corners of a rotated one', () => {
    expect(hudExtents([{ position: [0, 0, 0], rotation: identity, scale: [0.1, 2, 4] }])).toEqual({ min: [-0.05, -1, -2], max: [0.05, 1, 2] });
    const s = Math.SQRT1_2; // 90 degrees about x swaps the y and z extents
    const rotated = hudExtents([{ position: [0, 0, 0], rotation: [s, 0, 0, s], scale: [1, 2, 4] }])!;
    close(rotated.min, [-0.5, -2, -1]);
    close(rotated.max, [0.5, 2, 1]);
  });
  it('covers every prim and respects offsets', () => {
    const box = hudExtents([
      { position: [0, 0, 0], rotation: identity, scale: [0.1, 1, 1] },
      { position: [0, 3, -2], rotation: identity, scale: [0.1, 1, 1] },
    ])!;
    expect(box.min).toEqual([-0.05, -0.5, -2.5]);
    expect(box.max).toEqual([0.05, 3.5, 0.5]);
  });
  it('returns null for no prims and skips non-finite ones', () => {
    expect(hudExtents([])).toBeNull();
    expect(hudExtents([{ position: [Number.NaN, 0, 0], rotation: identity, scale: [1, 1, 1] }])).toBeNull();
  });
});

describe('fitHud', () => {
  const extents = hudExtents([{ position: [0.3, 0.5, -1], rotation: identity, scale: [0.05, 2, 1] }])!; // 2 wide (y), 1 tall (z)

  it('centres the HUD on screen and scales its largest side to the requested size', () => {
    const { matrix, scale } = fitHud(extents, 1);
    expect(scale).toBeCloseTo(0.5, 6); // largest side is 2 -> 1 view unit
    close(apply(matrix, [0.3, 0.5, -1]), [0, 0, apply(matrix, [0.3, 0.5, -1])[2]]); // its centre maps to the screen centre
    // Left and right edges (SL y = -0.5 and 1.5) land at +-0.5 on screen: y is mirrored because SL y is left.
    expect(apply(matrix, [0.3, -0.5, -1])[0]).toBeCloseTo(0.5, 6);
    expect(apply(matrix, [0.3, 1.5, -1])[0]).toBeCloseTo(-0.5, 6);
  });
  it('puts the nearest face at depth zero and keeps proportions', () => {
    const { matrix } = fitHud(extents, 1);
    expect(apply(matrix, [0.3 - 0.025, 0.5, -1])[2]).toBeCloseTo(0, 6);
    const w = Math.abs(apply(matrix, [0.3, -0.5, -1])[0] - apply(matrix, [0.3, 1.5, -1])[0]);
    const h = Math.abs(apply(matrix, [0.3, 0.5, -0.5])[1] - apply(matrix, [0.3, 0.5, -1.5])[1]);
    expect(w / h).toBeCloseTo(2, 6);
  });
  it('honours the size and pan, and leaves a tiny or empty HUD unscaled', () => {
    const big = fitHud(extents, 1.8).scale;
    expect(big).toBeCloseTo(0.9, 6);
    const panned = fitHud(extents, 1, [0.25, -0.1]).matrix;
    expect(apply(panned, [0.3, 0.5, -1]).slice(0, 2)).toEqual([expect.closeTo(0.25, 5), expect.closeTo(-0.1, 5)]);
    expect(fitHud(null).scale).toBe(1);
    const dot = hudExtents([{ position: [0, 0, 0], rotation: identity, scale: [0.0001, 0.0001, 0.0001] }]);
    expect(fitHud(dot, 1).scale).toBe(1); // Lumiya's 0.001 threshold
  });
  it('keeps the zoom range sensible', () => {
    expect(HUD_SIZE.min).toBeLessThan(HUD_SIZE.initial);
    expect(HUD_SIZE.initial).toBeLessThan(HUD_SIZE.max);
  });
});

describe('hudProjection', () => {
  it('maps x in [-aspect, aspect] and y in [-1, 1] to clip space', () => {
    const p = hudProjection(2);
    expect(apply(p, [2, 1, 0]).slice(0, 2)).toEqual([1, 1]);
    expect(apply(p, [-2, -1, 0]).slice(0, 2)).toEqual([-1, -1]);
  });
  it('keeps content within the depth range and tolerates bad aspect ratios', () => {
    const p = hudProjection(1.5, 50);
    expect(Math.abs(apply(p, [0, 0, -49])[2])).toBeLessThan(1);
    expect(hudProjection(0)[0]).toBe(1);
    expect(hudProjection(Number.NaN)[0]).toBe(1);
  });
});
