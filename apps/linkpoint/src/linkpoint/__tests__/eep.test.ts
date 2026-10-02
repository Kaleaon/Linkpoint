import { describe, expect, it } from 'vitest';
import { DEFAULT_SKY, DEFAULT_WATER, bracket, dayFraction, directionFrom, normalizeSky, normalizeWater, skyAt, skyState, slerp, waterAt } from '../eep';

const frame = (over: Record<string, any>) => ({ type: 'sky', ...over });
const cycle = (frames: Record<string, any>, track: Array<[number, string]>, waterTrack: Array<[number, string]> = []) => ({
  frames,
  tracks: [waterTrack.map(([keyKeyframe, keyName]) => ({ keyKeyframe, keyName })), track.map(([keyKeyframe, keyName]) => ({ keyKeyframe, keyName }))],
});

describe('EEP day cycle', () => {
  it('maps clock time to a point in the cycle using the region\'s length and offset', () => {
    expect(dayFraction(0, 14400, 0)).toBe(0);
    expect(dayFraction(7200, 14400, 0)).toBeCloseTo(0.5, 6);
    expect(dayFraction(7200, 14400, 3600)).toBeCloseTo(0.75, 6);
    expect(dayFraction(100, 14400, -200)).toBeCloseTo((14400 - 100) / 14400, 6); // negative wraps
    expect(dayFraction(5, 0)).toBeGreaterThanOrEqual(0); // invalid length falls back to the 4 h day
  });

  it('finds the keyframes either side of a time, wrapping around midnight', () => {
    const c = cycle({ a: {}, b: {}, c: {} }, [[0.25, 'a'], [0.5, 'b'], [0.75, 'c']]);
    expect(bracket(c, 1, 0.375)).toMatchObject({ t: 0.5 });
    expect(bracket(c, 1, 0.375)!.a).toBe(c.frames.a);
    expect(bracket(c, 1, 0.375)!.b).toBe(c.frames.b);
    // after the last key: between c (0.75) and a (1.25)
    const late = bracket(c, 1, 0.875)!;
    expect(late.a).toBe(c.frames.c);
    expect(late.b).toBe(c.frames.a);
    expect(late.t).toBeCloseTo(0.25, 6);
    // before the first key: wraps back to c
    const early = bracket(c, 1, 0.125)!;
    expect(early.a).toBe(c.frames.c);
    expect(early.t).toBeCloseTo(0.75, 6);
    expect(bracket(c, 3, 0.5)).toBeNull();
  });

  it('blends sky settings between keyframes and slerps the sun rotation', () => {
    const c = cycle({
      noon: frame({ sunlightColor: [1, 1, 1], sunRotation: [0, -0.7071068, 0, 0.7071068], hazeDensity: 0.4, legacyHaze: { blueHorizon: [0, 0, 1] } }),
      dusk: frame({ sunlightColor: [1, 0.5, 0], sunRotation: [0, 0, 0, 1], hazeDensity: 1.0, legacyHaze: { blueHorizon: [1, 0, 0] } }),
    }, [[0, 'noon'], [0.5, 'dusk']]);
    const mid = skyAt(c, 0.25);
    expect(mid.sunlightColor[1]).toBeCloseTo(0.75, 5);
    expect(mid.hazeDensity).toBeCloseTo(0.7, 5);
    expect(mid.blueHorizon).toEqual([0.5, 0, 0.5]);
    // halfway between "sun up" and "sun on the horizon"
    expect(directionFrom(mid.sunRotation)[2]).toBeCloseTo(Math.sin(Math.PI / 4), 3);
    expect(Math.hypot(...mid.sunRotation)).toBeCloseTo(1, 5);
  });

  it('falls back to a lone sky frame, then to viewer defaults', () => {
    expect(skyAt({ frames: { only: frame({ sunlightColor: [0.1, 0.2, 0.3] }) }, tracks: [] }, 0.3).sunlightColor).toEqual([0.1, 0.2, 0.3]);
    expect(skyAt(null, 0.5)).toEqual(DEFAULT_SKY);
    expect(skyAt({ frames: {}, tracks: [] }, 0.5)).toEqual(DEFAULT_SKY);
    expect(waterAt(null, 0)).toEqual(DEFAULT_WATER);
  });

  it('accepts snake_case LLSD keys, nested legacy haze, and quaternion objects', () => {
    const sky = normalizeSky({
      sunlight_color: [0.5, 0.4, 0.3, 0], legacy_haze: { blue_density: [0.1, 0.2, 0.3], haze_horizon: 0.3, density_multiplier: 0.0002 },
      sun_rotation: { x: 0, y: 0, z: 0, w: 2 }, max_y: 1000, glow: [4, 0.002, -0.5],
    });
    expect(sky.sunlightColor).toEqual([0.5, 0.4, 0.3]);
    expect(sky.blueDensity).toEqual([0.1, 0.2, 0.3]);
    expect(sky.hazeHorizon).toBe(0.3);
    expect(sky.densityMultiplier).toBe(0.0002);
    expect(sky.sunRotation).toEqual([0, 0, 0, 1]);
    expect(sky.maxY).toBe(1000);
    expect(sky.hazeDensity).toBe(DEFAULT_SKY.hazeDensity); // missing -> default
    expect(normalizeSky({ sunRotation: [0, 0, 0, 0] }).sunRotation).toEqual(DEFAULT_SKY.sunRotation); // zero quaternion rejected
  });

  it('reads water settings, ignoring a null texture id', () => {
    const water = normalizeWater({ waterFogColor: [1, 0, 0], water_fog_density: 5, fresnel_scale: 0.9, wave1Direction: [2, 3], normalMap: '822ded49-9a6c-f61c-cb89-6df54f42cdf4' });
    expect(water.fogColor).toEqual([1, 0, 0]);
    expect(water.fogDensity).toBe(5);
    expect(water.fresnelScale).toBe(0.9);
    expect(water.wave1Direction).toEqual([2, 3]);
    expect(water.normalMapId).toBe('822ded49-9a6c-f61c-cb89-6df54f42cdf4');
    expect(normalizeWater({ normalMap: '00000000-0000-0000-0000-000000000000' }).normalMapId).toBeNull();
    expect(normalizeWater({ normalMap: { mUUID: '822ded49-9a6c-f61c-cb89-6df54f42cdf4' } }).normalMapId).toBe('822ded49-9a6c-f61c-cb89-6df54f42cdf4');
    expect(normalizeWater({ normalMap: { somethingElse: 1 } }).normalMapId).toBeNull();
    expect(normalizeWater({}).fresnelOffset).toBe(0.5);
  });

  it('derives sun and moon directions from their rotations', () => {
    expect(directionFrom([0, 0, 0, 1])).toEqual([1, 0, 0]);
    const up = directionFrom([0, -Math.SQRT1_2, 0, Math.SQRT1_2]);
    expect(up[2]).toBeCloseTo(1, 5);
    const slerped = slerp([0, 0, 0, 1], [0, 0, 1, 0], 0.5);
    expect(Math.hypot(...slerped)).toBeCloseTo(1, 6);
  });

  it('lights surfaces with attenuated sunlight: dimmer and redder near the horizon, off at night', () => {
    const overhead = skyState({ ...DEFAULT_SKY, sunRotation: [0, -Math.SQRT1_2, 0, Math.SQRT1_2] });
    expect(overhead.sunUp).toBe(true);
    expect(overhead.sunMoonGlowFactor).toBe(1);
    const lowAngle = Math.asin(0.1); // 5.7 degrees above the horizon
    const half = lowAngle / 2;
    const low = skyState({ ...DEFAULT_SKY, sunRotation: [0, -Math.sin(half), 0, Math.cos(half)] });
    expect(low.sunUp).toBe(true);
    expect(low.sunDiffuse[0]).toBeLessThan(overhead.sunDiffuse[0]);
    // blue is absorbed most, so the low sun is redder than the overhead sun
    expect(low.sunDiffuse[2] / low.sunDiffuse[0]).toBeLessThan(overhead.sunDiffuse[2] / overhead.sunDiffuse[0]);
    // sun below the horizon: the moon takes over the light direction
    const night = skyState({ ...DEFAULT_SKY, sunRotation: [0, Math.SQRT1_2, 0, Math.SQRT1_2], moonRotation: [0, -Math.SQRT1_2, 0, Math.SQRT1_2] });
    expect(night.sunUp).toBe(false);
    expect(night.moonUp).toBe(true);
    expect(night.lightDirection[2]).toBeCloseTo(1, 5);
    expect(night.sunMoonGlowFactor).toBeCloseTo(DEFAULT_SKY.moonBrightness * 0.25, 6);
    // clouds raise ambient light toward white
    expect(skyState({ ...DEFAULT_SKY, cloudShadow: 1 }).sunAmbient[0]).toBeGreaterThan(skyState({ ...DEFAULT_SKY, cloudShadow: 0 }).sunAmbient[0]);
    // nothing in the sky: no glow
    const dark = skyState({ ...DEFAULT_SKY, sunRotation: [0, Math.SQRT1_2, 0, Math.SQRT1_2], moonRotation: [0, Math.SQRT1_2, 0, Math.SQRT1_2] });
    expect(dark.sunMoonGlowFactor).toBe(0);
  });
});
