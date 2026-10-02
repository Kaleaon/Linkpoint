/**
 * Windlight day cycle: the eight default sky presets Lumiya ships (text LLSD
 * XML, copied from its assets) plus the same loading and interpolation rules.
 *
 * Rules checked against Lumiya's WindlightPreset / WindlightDay:
 *  - ambient and sunlight are divided by 3, blue density/horizon by 2, haze
 *    density/horizon by 5, cloud position/density by 3;
 *  - ambient and sunlight are then gamma-encoded (pow 1/2.2) and scaled by 1.25;
 *  - the day is eight presets at 3-hour steps (hour fractions 0, 1/8 ... 7/8),
 *    linearly interpolated, with the last preset wrapping to the first.
 * Sun elevation/azimuth are derived from `sun_angle` and `east_angle`; Lumiya
 * uses the preset's own `lightnorm` vector instead, so that is kept too.
 *
 * These presets are only a fallback for when the simulator has not sent its
 * own environment. They are not a live reading of the region's sky.
 */

import { LLSD } from './llsd';
import a12am from '../assets/windlight/A-12AM.xml?raw';
import a3am from '../assets/windlight/A-3AM.xml?raw';
import a6am from '../assets/windlight/A-6AM.xml?raw';
import a9am from '../assets/windlight/A-9AM.xml?raw';
import a12pm from '../assets/windlight/A-12PM.xml?raw';
import a3pm from '../assets/windlight/A-3PM.xml?raw';
import a6pm from '../assets/windlight/A-6PM.xml?raw';
import a9pm from '../assets/windlight/A-9PM.xml?raw';

export type Vec4 = [number, number, number, number];

export const WINDLIGHT_GAMMA = 2.2;
/** Hour-of-day fractions of the eight default presets (midnight, 3 AM ... 9 PM). */
export const WINDLIGHT_HOUR_TABLE = [0, 0.125, 0.25, 0.375, 0.5, 0.625, 0.75, 0.875] as const;
/** A Second Life region day lasts four real hours. */
export const SL_DAY_SECONDS = 4 * 60 * 60;

export interface WindlightPreset {
  ambient: Vec4;
  sunlightColor: Vec4;
  lightnorm: Vec4;
  blueDensity: Vec4;
  blueHorizon: Vec4;
  hazeDensity: Vec4;
  hazeHorizon: Vec4;
  cloudColor: Vec4;
  cloudPosDensity1: Vec4;
  cloudPosDensity2: Vec4;
  cloudShadow: Vec4;
  starBrightness: number;
  sunAngle: number;
  eastAngle: number;
}

/** Sky frame in the shape `Scene3D.setEnvironment` and `computeSkyUniforms` already accept. */
export interface WindlightSkyFrame {
  blueHorizon: Vec4;
  blueDensity: Vec4;
  sunlightColor: Vec4;
  ambient: Vec4;
  hazeDensity: number;
  hazeHorizon: number;
  starBrightness: number;
  /** Unit vector towards the sun in Z-up world space. */
  sunDirection: [number, number, number];
  cloudColor: Vec4;
}

function vec4(source: any, key: string, divisor: number): Vec4 {
  const values = source?.[key];
  if (!Array.isArray(values) || values.length < 4) throw new Error(`Windlight preset is missing "${key}"`);
  const out = values.slice(0, 4).map((value) => Number(value) / divisor);
  if (!out.every(Number.isFinite)) throw new Error(`Windlight preset has a non-numeric "${key}"`);
  return out as Vec4;
}

function scalar(source: any, key: string): number {
  const value = Number(source?.[key]);
  if (!Number.isFinite(value)) throw new Error(`Windlight preset is missing "${key}"`);
  return value;
}

const gamma = (v: Vec4): Vec4 => v.map((x) => Math.pow(x, 1 / WINDLIGHT_GAMMA) * 1.25) as Vec4;

/** Parse one preset from LLSD XML, applying Lumiya's scale factors and gamma. Throws if a field is missing. */
export function parseWindlightPreset(xml: string): WindlightPreset {
  const source = LLSD.parseXML(xml);
  return {
    ambient: gamma(vec4(source, 'ambient', 3)),
    sunlightColor: gamma(vec4(source, 'sunlight_color', 3)),
    lightnorm: vec4(source, 'lightnorm', 1),
    blueDensity: vec4(source, 'blue_density', 2),
    blueHorizon: vec4(source, 'blue_horizon', 2),
    hazeDensity: vec4(source, 'haze_density', 5),
    hazeHorizon: vec4(source, 'haze_horizon', 5),
    cloudColor: vec4(source, 'cloud_color', 1),
    cloudPosDensity1: vec4(source, 'cloud_pos_density1', 3),
    cloudPosDensity2: vec4(source, 'cloud_pos_density2', 3),
    cloudShadow: vec4(source, 'cloud_shadow', 1),
    starBrightness: scalar(source, 'star_brightness'),
    sunAngle: scalar(source, 'sun_angle'),
    eastAngle: scalar(source, 'east_angle'),
  };
}

const lerp = (a: number, b: number, t: number) => a * (1 - t) + b * t;
const lerpVec = (a: Vec4, b: Vec4, t: number): Vec4 => a.map((x, i) => lerp(x, b[i], t)) as Vec4;

/** Interpolate along a circular angle the short way forward (a → b, wrapping once if b is behind a). */
function lerpAngleForward(a: number, b: number, t: number) {
  const end = b < a ? b + Math.PI * 2 : b;
  return lerp(a, end, t) % (Math.PI * 2);
}

export function interpolateWindlight(a: WindlightPreset, b: WindlightPreset, t: number): WindlightPreset {
  return {
    ambient: lerpVec(a.ambient, b.ambient, t),
    sunlightColor: lerpVec(a.sunlightColor, b.sunlightColor, t),
    lightnorm: lerpVec(a.lightnorm, b.lightnorm, t),
    blueDensity: lerpVec(a.blueDensity, b.blueDensity, t),
    blueHorizon: lerpVec(a.blueHorizon, b.blueHorizon, t),
    hazeDensity: lerpVec(a.hazeDensity, b.hazeDensity, t),
    hazeHorizon: lerpVec(a.hazeHorizon, b.hazeHorizon, t),
    cloudColor: lerpVec(a.cloudColor, b.cloudColor, t),
    cloudPosDensity1: lerpVec(a.cloudPosDensity1, b.cloudPosDensity1, t),
    cloudPosDensity2: lerpVec(a.cloudPosDensity2, b.cloudPosDensity2, t),
    cloudShadow: lerpVec(a.cloudShadow, b.cloudShadow, t),
    starBrightness: lerp(a.starBrightness, b.starBrightness, t),
    sunAngle: lerpAngleForward(a.sunAngle, b.sunAngle, t),
    eastAngle: lerp(a.eastAngle, b.eastAngle, t),
  };
}

/** A day of eight presets. */
export class WindlightDay {
  constructor(readonly presets: WindlightPreset[]) {
    if (presets.length !== WINDLIGHT_HOUR_TABLE.length) throw new Error(`A Windlight day needs ${WINDLIGHT_HOUR_TABLE.length} presets`);
  }

  /** Sky at `hour`, a fraction of the day in [0, 1) where 0 is midnight and 0.5 noon. */
  at(hour: number): WindlightPreset {
    const f = ((hour % 1) + 1) % 1;
    let i = 0;
    for (let k = WINDLIGHT_HOUR_TABLE.length - 1; k >= 0; k--) {
      if (f >= WINDLIGHT_HOUR_TABLE[k]) { i = k; break; }
    }
    const next = (i + 1) % WINDLIGHT_HOUR_TABLE.length;
    const start = WINDLIGHT_HOUR_TABLE[i];
    let end = WINDLIGHT_HOUR_TABLE[next];
    if (end <= start) end += 1;
    return interpolateWindlight(this.presets[i], this.presets[next], (f - start) / (end - start));
  }
}

let defaultDay: WindlightDay | null = null;

/** The eight bundled presets, parsed once. */
export function getDefaultWindlightDay(): WindlightDay {
  if (!defaultDay) {
    defaultDay = new WindlightDay([a12am, a3am, a6am, a9am, a12pm, a3pm, a6pm, a9pm].map(parseWindlightPreset));
  }
  return defaultDay;
}

/**
 * Sun direction in Z-up world space. At `sun_angle` 0 the sun is on the eastern
 * horizon, at π/2 overhead; `east_angle` rotates the compass.
 */
export function sunDirection(sunAngle: number, eastAngle = 0): [number, number, number] {
  const horizontal = Math.cos(sunAngle);
  return [horizontal * Math.cos(eastAngle), horizontal * Math.sin(eastAngle), Math.sin(sunAngle)];
}

export function presetToSkyFrame(preset: WindlightPreset): WindlightSkyFrame {
  return {
    blueHorizon: preset.blueHorizon,
    blueDensity: preset.blueDensity,
    sunlightColor: preset.sunlightColor,
    ambient: preset.ambient,
    hazeDensity: preset.hazeDensity[0],
    hazeHorizon: preset.hazeHorizon[0],
    starBrightness: preset.starBrightness,
    sunDirection: sunDirection(preset.sunAngle, preset.eastAngle),
    cloudColor: preset.cloudColor,
  };
}

/**
 * Time of day as a fraction of a four-hour Second Life day, from a Unix time in
 * milliseconds. This is an estimate: the simulator's own sun phase is not
 * exposed by the client library, and the cycle's alignment to real time is
 * assumed (day starts at the Unix epoch, so the fraction is time mod 4 hours).
 */
export function estimatedSunHour(nowMs: number): number {
  const seconds = nowMs / 1000;
  return (((seconds % SL_DAY_SECONDS) + SL_DAY_SECONDS) % SL_DAY_SECONDS) / SL_DAY_SECONDS;
}

/** Environment object for the scene built from the bundled day at `hour`. */
export function windlightEnvironment(hour: number) {
  return { currentSky: presetToSkyFrame(getDefaultWindlightDay().at(hour)), source: 'default-windlight' as const };
}
