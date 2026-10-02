/**
 * Atmospheric scattering for the sky dome and for water reflections, ported from the official
 * viewer's shaders (class1/deferred/skyF.glsl and class1/windlight/atmosphericsFuncs.glsl) to
 * GLSL ES 1.00 with Z up. Cloud textures, rainbows and halos are not reproduced.
 */
import type { SkySettings, SkyState, Vec3 } from './eep';

/** Uniform declarations plus `atmosphereColor(direction)`: the sky colour seen along a world direction. */
export const ATMOSPHERE_GLSL = `
  uniform vec3 uBlueHorizon;
  uniform vec3 uBlueDensity;
  uniform vec3 uAmbient;
  uniform vec3 uSunlight;       // sunlight colour (the moon shares it)
  uniform vec3 uGlow;
  uniform vec3 uLightNorm;      // direction of whichever of sun / moon lights the world
  uniform float uHazeHorizon;
  uniform float uHazeDensity;
  uniform float uDensityMultiplier;
  uniform float uMaxY;
  uniform float uCloudShadow;
  uniform float uSunUp;         // 1.0 when the sun is up
  uniform float uSunMoonGlow;

  vec3 atmosphereColor(vec3 dir) {
    // Position on the (notional) dome, lifted 50 m so the horizon is not degenerate, then
    // clamped to the maximum atmosphere height as the viewer does.
    vec3 rel = dir * 15000.0 + vec3(0.0, 0.0, 50.0);
    if (rel.z > 0.0) rel *= uMaxY / rel.z;
    if (rel.z < 0.0) rel *= -32000.0 / rel.z;
    vec3 relNorm = normalize(rel);
    float relLen = length(rel);
    float lightDot = dot(relNorm, uLightNorm);

    vec3 sunlight = uSunUp > 0.5 ? uSunlight : uSunlight * 0.7;

    // sunlight attenuation (hue and brightness) through the atmosphere
    vec3 lightAtten = (uBlueDensity + vec3(uHazeDensity * 0.25)) * (uDensityMultiplier * uMaxY);
    vec3 combinedHaze = max(abs(uBlueDensity) + vec3(abs(uHazeDensity)), vec3(1e-6));
    vec3 blueWeight = uBlueDensity / combinedHaze;
    vec3 hazeWeight = vec3(uHazeDensity) / combinedHaze;

    float offAxis = 1.0 / max(1e-6, max(0.0, relNorm.z) + uLightNorm.z);
    sunlight *= exp(-lightAtten * offAxis);

    float densityDist = relLen * uDensityMultiplier;
    combinedHaze = exp(-combinedHaze * densityDist);

    // haze glow around the light, brightest toward it
    float hazeGlow = 1.0 - lightDot;
    hazeGlow = max(hazeGlow, 0.001);
    hazeGlow *= uGlow.x;
    hazeGlow = pow(hazeGlow, uGlow.z);
    hazeGlow = (uSunMoonGlow < 1.0) ? 0.0 : (uSunMoonGlow * (hazeGlow + 0.25));

    vec3 color = (uBlueHorizon * blueWeight * (sunlight + uAmbient)
                 + (uHazeHorizon * hazeWeight) * (sunlight * hazeGlow + uAmbient));
    color *= (1.0 - combinedHaze);

    // more ambient under cloud, less sun
    vec3 ambient = uAmbient + max(vec3(0.0), (1.0 - uAmbient)) * uCloudShadow * 0.5;
    sunlight *= max(0.0, 1.0 - uCloudShadow);
    vec3 belowCloud = (uBlueHorizon * blueWeight * (sunlight + ambient)
                      + (uHazeHorizon * hazeWeight) * (sunlight * hazeGlow + ambient));
    combinedHaze = sqrt(combinedHaze);
    color += (belowCloud - color) * (1.0 - sqrt(combinedHaze));

    color *= 2.0;
    return clamp(color, vec3(0.0), vec3(5.0));
  }

  // The sky shader outputs linear light; the viewer tone maps and converts to sRGB in a later
  // pass. This soft clip plus the sRGB curve reproduces that for a bright sky in display range.
  vec3 toneMapSky(vec3 c) {
    return pow(vec3(1.0) - exp(-c * 1.2), vec3(1.0 / 2.2));
  }
`;

/** Angular radius of the sun and moon discs at scale 1, in radians (about half a degree). */
export const HEAVENLY_BODY_RADIUS = 0.0095;

export interface AtmosphereUniforms {
  uBlueHorizon: Float32Array;
  uBlueDensity: Float32Array;
  uAmbient: Float32Array;
  uSunlight: Float32Array;
  uGlow: Float32Array;
  uLightNorm: Float32Array;
  uHazeHorizon: number;
  uHazeDensity: number;
  uDensityMultiplier: number;
  uMaxY: number;
  uCloudShadow: number;
  uSunUp: number;
  uSunMoonGlow: number;
}

/** Shader inputs for the sky and water from a sampled sky and its derived state. */
export function atmosphereUniforms(sky: SkySettings, state: SkyState): AtmosphereUniforms {
  const f3 = (v: Vec3) => new Float32Array(v);
  return {
    uBlueHorizon: f3(sky.blueHorizon), uBlueDensity: f3(sky.blueDensity), uAmbient: f3(sky.ambient),
    uSunlight: f3(sky.sunlightColor), uGlow: f3(sky.glow), uLightNorm: f3(state.lightDirection),
    uHazeHorizon: sky.hazeHorizon, uHazeDensity: sky.hazeDensity, uDensityMultiplier: sky.densityMultiplier,
    uMaxY: sky.maxY, uCloudShadow: sky.cloudShadow, uSunUp: state.sunUp ? 1 : 0, uSunMoonGlow: state.sunMoonGlowFactor,
  };
}

/**
 * The sky colour looking along `dir`, computed on the CPU with the same math as `atmosphereColor`
 * (used for clear colour, tests and fog tint). Returned before tone mapping.
 */
export function atmosphereColor(sky: SkySettings, state: SkyState, dir: Vec3): Vec3 {
  const n = Math.hypot(...dir) || 1;
  const d: Vec3 = [dir[0] / n, dir[1] / n, dir[2] / n];
  let rel: Vec3 = [d[0] * 15000, d[1] * 15000, d[2] * 15000 + 50];
  if (rel[2] > 0) rel = rel.map((v) => v * (sky.maxY / rel[2])) as Vec3;
  else if (rel[2] < 0) rel = rel.map((v) => v * (-32000 / rel[2])) as Vec3;
  const relLen = Math.hypot(...rel);
  const relNorm: Vec3 = [rel[0] / relLen, rel[1] / relLen, rel[2] / relLen];
  const light = state.lightDirection;
  const lightDot = relNorm[0] * light[0] + relNorm[1] * light[1] + relNorm[2] * light[2];

  const sunScale = state.sunUp ? 1 : 0.7;
  const blue = sky.blueDensity, haze = sky.hazeDensity;
  const combined = blue.map((b) => Math.max(Math.abs(b) + Math.abs(haze), 1e-6));
  const blueWeight = blue.map((b, i) => b / combined[i]);
  const hazeWeight = combined.map((c) => haze / c);
  const offAxis = 1 / Math.max(1e-6, Math.max(0, relNorm[2]) + light[2]);
  const sunlight = sky.sunlightColor.map((s, i) => s * sunScale * Math.exp(-((blue[i] + haze * 0.25) * sky.densityMultiplier * sky.maxY) * offAxis));
  const transmittance = combined.map((c) => Math.exp(-c * relLen * sky.densityMultiplier));

  let glow = Math.max(1 - lightDot, 0.001) * sky.glow[0];
  glow = Math.pow(glow, sky.glow[2]);
  glow = state.sunMoonGlowFactor < 1 ? 0 : state.sunMoonGlowFactor * (glow + 0.25);

  const above = [0, 1, 2].map((i) => (sky.blueHorizon[i] * blueWeight[i] * (sunlight[i] + sky.ambient[i])
    + sky.hazeHorizon * hazeWeight[i] * (sunlight[i] * glow + sky.ambient[i])) * (1 - transmittance[i]));
  const ambient = sky.ambient.map((a) => a + Math.max(0, 1 - a) * sky.cloudShadow * 0.5);
  const dimmed = sunlight.map((s) => s * Math.max(0, 1 - sky.cloudShadow));
  const below = [0, 1, 2].map((i) => sky.blueHorizon[i] * blueWeight[i] * (dimmed[i] + ambient[i])
    + sky.hazeHorizon * hazeWeight[i] * (dimmed[i] * glow + ambient[i]));
  // combinedHaze = sqrt(T); the below-cloud blend uses 1 - sqrt(combinedHaze) = 1 - T^(1/4), per channel
  return [0, 1, 2].map((i) => {
    const blend = 1 - Math.pow(transmittance[i], 0.25);
    return Math.min(5, Math.max(0, (above[i] + (below[i] - above[i]) * blend) * 2));
  }) as Vec3;
}
