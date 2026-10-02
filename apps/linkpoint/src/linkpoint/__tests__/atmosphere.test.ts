import { describe, expect, it } from 'vitest';
import { ATMOSPHERE_GLSL, atmosphereColor, atmosphereUniforms } from '../atmosphere';
import { DEFAULT_SKY, skyState, type SkySettings, type Vec3 } from '../eep';

const norm = (v: Vec3): Vec3 => { const n = Math.hypot(...v); return [v[0] / n, v[1] / n, v[2] / n]; };
const sunAt = (elevationDegrees: number, over: Partial<SkySettings> = {}): SkySettings => {
  const half = (elevationDegrees * Math.PI) / 360;
  return { ...DEFAULT_SKY, sunRotation: [0, -Math.sin(half), 0, Math.cos(half)], ...over }; // rotation about -Y lifts +X
};
const luminance = (c: Vec3) => 0.2126 * c[0] + 0.7152 * c[1] + 0.0722 * c[2];

describe('atmosphere', () => {
  it('colours the sky bluer overhead than at the horizon, which is hazier and brighter', () => {
    const sky = sunAt(50);
    const state = skyState(sky);
    const zenith = atmosphereColor(sky, state, [0, 0, 1]);
    const horizon = atmosphereColor(sky, state, norm([0, 1, 0.01]));
    expect(zenith.every((v) => v >= 0 && v <= 5)).toBe(true);
    expect(zenith[2]).toBeGreaterThan(zenith[0]); // blue
    expect(luminance(horizon)).toBeGreaterThan(luminance(zenith)); // the horizon is paler / brighter
    expect(horizon[0] / horizon[2]).toBeGreaterThan(zenith[0] / zenith[2]); // and less saturated
  });

  it('brightens the haze toward the sun (glow) and fades it away from the sun', () => {
    const sky = sunAt(8);
    const state = skyState(sky);
    const towardSun = atmosphereColor(sky, state, norm([state.sunDirection[0], state.sunDirection[1], state.sunDirection[2] + 0.02]));
    const awayFromSun = atmosphereColor(sky, state, norm([-state.sunDirection[0], -state.sunDirection[1], 0.2]));
    expect(luminance(towardSun)).toBeGreaterThan(luminance(awayFromSun));
  });

  it('dims the whole sky when the sun is down and the moon is not up', () => {
    const day = sunAt(50);
    const dark = { ...sunAt(-30), moonRotation: sunAt(-30).sunRotation };
    const dayColor = atmosphereColor(day, skyState(day), norm([0, 1, 0.3]));
    const darkColor = atmosphereColor(dark, skyState(dark), norm([0, 1, 0.3]));
    expect(luminance(darkColor)).toBeLessThan(luminance(dayColor));
  });

  it('is denser (hazier) with a bigger density multiplier', () => {
    const thin = sunAt(50, { densityMultiplier: 0.00005 });
    const thick = sunAt(50, { densityMultiplier: 0.0006 });
    const dir = norm([0, 1, 0.5]);
    expect(luminance(atmosphereColor(thick, skyState(thick), dir))).toBeGreaterThan(luminance(atmosphereColor(thin, skyState(thin), dir)));
  });

  it('always yields finite, clamped colours, including straight down and for odd settings', () => {
    const sky = sunAt(50, { hazeDensity: 0, blueDensity: [0, 0, 0], maxY: 10 });
    for (const dir of [[0, 0, -1], [0, 0, 1], [1, 0, 0], [0, 0.0001, -0.0001]] as Vec3[]) {
      const color = atmosphereColor(sky, skyState(sky), norm(dir));
      expect(color.every((v) => Number.isFinite(v) && v >= 0 && v <= 5)).toBe(true);
    }
  });

  it('passes the sky settings to the shader unchanged, with the light direction and sun flag', () => {
    const sky = sunAt(50);
    const state = skyState(sky);
    const u = atmosphereUniforms(sky, state);
    expect(Array.from(u.uBlueHorizon)).toEqual(sky.blueHorizon.map(Math.fround));
    expect(u.uHazeHorizon).toBe(sky.hazeHorizon);
    expect(u.uMaxY).toBe(sky.maxY);
    expect(u.uSunUp).toBe(1);
    expect(Array.from(u.uLightNorm)).toEqual(state.lightDirection.map(Math.fround));
    expect(atmosphereUniforms(sunAt(-30), skyState(sunAt(-30))).uSunUp).toBe(0);
  });

  it('declares every uniform the uniform builder provides, and the entry point', () => {
    for (const name of ['uBlueHorizon', 'uBlueDensity', 'uAmbient', 'uSunlight', 'uGlow', 'uLightNorm', 'uHazeHorizon', 'uHazeDensity', 'uDensityMultiplier', 'uMaxY', 'uCloudShadow', 'uSunUp', 'uSunMoonGlow']) {
      expect(ATMOSPHERE_GLSL, name).toContain(name);
    }
    expect(ATMOSPHERE_GLSL).toContain('vec3 atmosphereColor(vec3 dir)');
    expect(ATMOSPHERE_GLSL).toContain('vec3 toneMapSky(vec3 c)');
  });
});
