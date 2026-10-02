/**
 * Environment (EEP) sky and water settings: sampling a day cycle by time of day, and the derived
 * sun / moon / light values, following the official viewer (LLSettingsSky, LLSettingsWater,
 * LLSettingsDay). All vectors are in Second Life world space (Z up).
 */

export type Vec3 = [number, number, number];
export type Quat = [number, number, number, number];

export interface SkySettings {
  sunlightColor: Vec3;
  ambient: Vec3;
  blueHorizon: Vec3;
  blueDensity: Vec3;
  hazeHorizon: number;
  hazeDensity: number;
  densityMultiplier: number;
  distanceMultiplier: number;
  maxY: number;
  glow: Vec3;
  cloudShadow: number;
  gamma: number;
  sunRotation: Quat;
  moonRotation: Quat;
  moonBrightness: number;
  starBrightness: number;
  sunScale: number;
  moonScale: number;
  cloudColor: Vec3;
}

export interface WaterSettings {
  fogColor: Vec3;
  fogDensity: number;
  fogMod: number;
  fresnelOffset: number;
  fresnelScale: number;
  blurMultiplier: number;
  normalScale: Vec3;
  scaleAbove: number;
  scaleBelow: number;
  wave1Direction: [number, number];
  wave2Direction: [number, number];
  normalMapId: string | null;
}

/** Viewer defaults (LLSettingsSky::defaults / legacy haze defaults). */
export const DEFAULT_SKY: SkySettings = {
  sunlightColor: [0.7342, 0.7815, 0.8999],
  ambient: [0.25, 0.25, 0.25],
  blueHorizon: [0.4954, 0.4954, 0.6399],
  blueDensity: [0.2447, 0.4487, 0.7599],
  hazeHorizon: 0.19,
  hazeDensity: 0.7,
  densityMultiplier: 0.0001,
  distanceMultiplier: 0.8,
  maxY: 1605,
  glow: [5, 0.001, -0.4799],
  cloudShadow: 0.2699,
  gamma: 1,
  sunRotation: [0, -0.7071, 0, 0.7071], // sun overhead
  moonRotation: [0, 0.7071, 0, 0.7071],
  moonBrightness: 0.5,
  starBrightness: 250,
  sunScale: 1,
  moonScale: 1,
  cloudColor: [0.4099, 0.4099, 0.4099],
};

/** Viewer defaults (LLSettingsWater::defaults). */
export const DEFAULT_WATER: WaterSettings = {
  fogColor: [0.0156, 0.149, 0.2509],
  fogDensity: 2,
  fogMod: 0.25,
  fresnelOffset: 0.5,
  fresnelScale: 0.3999,
  blurMultiplier: 0.04,
  normalScale: [2, 2, 2],
  scaleAbove: 0.0299,
  scaleBelow: 0.2,
  wave1Direction: [1.04999, -0.42],
  wave2Direction: [1.10999, -1.16],
  normalMapId: null,
};

// ---- reading settings -------------------------------------------------------------------------

type Raw = Record<string, any>;
const finite = (v: unknown): v is number => typeof v === 'number' && Number.isFinite(v);

/** First defined value among alternative key spellings (camelCase from node-metaverse, snake_case from LLSD). */
function pick(frame: Raw | null | undefined, ...keys: string[]): any {
  for (const key of keys) {
    const value = frame?.[key];
    if (value !== undefined && value !== null) return value;
  }
  return undefined;
}

function vec3(value: any, fallback: Vec3): Vec3 {
  if (Array.isArray(value) && value.length >= 3 && value.slice(0, 3).every((v) => finite(Number(v)))) return [Number(value[0]), Number(value[1]), Number(value[2])];
  if (value && typeof value === 'object' && 'x' in value) {
    const v: Vec3 = [Number(value.x), Number(value.y), Number(value.z)];
    if (v.every(finite)) return v;
  }
  return [...fallback] as Vec3;
}
function num(value: any, fallback: number): number {
  const n = Array.isArray(value) ? Number(value[0]) : Number(value);
  return value === undefined || value === null || value === '' || !Number.isFinite(n) ? fallback : n;
}
function quat(value: any, fallback: Quat): Quat {
  let q: number[] | null = null;
  if (Array.isArray(value) && value.length >= 4) q = value.slice(0, 4).map(Number);
  else if (value && typeof value === 'object' && 'w' in value) q = [Number(value.x), Number(value.y), Number(value.z), Number(value.w)];
  if (!q || !q.every(Number.isFinite)) return [...fallback] as Quat;
  const n = Math.hypot(...q);
  return n > 1e-9 ? [q[0] / n, q[1] / n, q[2] / n, q[3] / n] : [...fallback] as Quat;
}

/** Normalise a raw sky frame (any key spelling, haze values nested or not) to a full set of settings. */
export function normalizeSky(frame: Raw | null | undefined): SkySettings {
  const d = DEFAULT_SKY;
  const haze = pick(frame, 'legacyHaze', 'legacy_haze') || {};
  const hz = (...keys: string[]) => pick(haze, ...keys) ?? pick(frame, ...keys);
  return {
    sunlightColor: vec3(pick(frame, 'sunlightColor', 'sunlight_color'), d.sunlightColor),
    ambient: vec3(hz('ambient', 'ambientColor', 'ambient_color'), d.ambient),
    blueHorizon: vec3(hz('blueHorizon', 'blue_horizon'), d.blueHorizon),
    blueDensity: vec3(hz('blueDensity', 'blue_density'), d.blueDensity),
    hazeHorizon: num(hz('hazeHorizon', 'haze_horizon'), d.hazeHorizon),
    hazeDensity: num(hz('hazeDensity', 'haze_density'), d.hazeDensity),
    densityMultiplier: num(hz('densityMultiplier', 'density_multiplier'), d.densityMultiplier),
    distanceMultiplier: num(hz('distanceMultiplier', 'distance_multiplier'), d.distanceMultiplier),
    maxY: num(pick(frame, 'maxY', 'max_y'), d.maxY),
    glow: vec3(pick(frame, 'glow'), d.glow),
    cloudShadow: num(pick(frame, 'cloudShadow', 'cloud_shadow'), d.cloudShadow),
    gamma: num(pick(frame, 'gamma'), d.gamma),
    sunRotation: quat(pick(frame, 'sunRotation', 'sun_rotation'), d.sunRotation),
    moonRotation: quat(pick(frame, 'moonRotation', 'moon_rotation'), d.moonRotation),
    moonBrightness: num(pick(frame, 'moonBrightness', 'moon_brightness'), d.moonBrightness),
    starBrightness: num(pick(frame, 'starBrightness', 'star_brightness'), d.starBrightness),
    sunScale: num(pick(frame, 'sunScale', 'sun_scale'), d.sunScale),
    moonScale: num(pick(frame, 'moonScale', 'moon_scale'), d.moonScale),
    cloudColor: vec3(pick(frame, 'cloudColor', 'cloud_color'), d.cloudColor),
  };
}

export function normalizeWater(frame: Raw | null | undefined): WaterSettings {
  const d = DEFAULT_WATER;
  const dir = (value: any, fallback: [number, number]): [number, number] =>
    Array.isArray(value) && value.length >= 2 && value.slice(0, 2).every((v) => finite(Number(v))) ? [Number(value[0]), Number(value[1])] : [...fallback] as [number, number];
  const id = pick(frame, 'normalMap', 'normal_map');
  // node-metaverse UUIDs serialise as { mUUID: '...' }
  const raw = id && typeof id === 'object' && 'mUUID' in id ? id.mUUID : id;
  const idText = raw === undefined || raw === null || typeof raw === 'object' ? null : String(raw);
  return {
    fogColor: vec3(pick(frame, 'waterFogColor', 'water_fog_color'), d.fogColor),
    fogDensity: num(pick(frame, 'waterFogDensity', 'water_fog_density'), d.fogDensity),
    fogMod: num(pick(frame, 'underwaterFogMod', 'underwater_fog_mod'), d.fogMod),
    fresnelOffset: num(pick(frame, 'fresnelOffset', 'fresnel_offset'), d.fresnelOffset),
    fresnelScale: num(pick(frame, 'fresnelScale', 'fresnel_scale'), d.fresnelScale),
    blurMultiplier: num(pick(frame, 'blurMultiplier', 'blur_multiplier'), d.blurMultiplier),
    normalScale: vec3(pick(frame, 'normalScale', 'normal_scale'), d.normalScale),
    scaleAbove: num(pick(frame, 'scaleAbove', 'scale_above'), d.scaleAbove),
    scaleBelow: num(pick(frame, 'scaleBelow', 'scale_below'), d.scaleBelow),
    wave1Direction: dir(pick(frame, 'wave1Direction', 'wave1_direction'), d.wave1Direction),
    wave2Direction: dir(pick(frame, 'wave2Direction', 'wave2_direction'), d.wave2Direction),
    normalMapId: idText && !/^0{8}-0{4}-0{4}-0{4}-0{12}$/.test(idText) ? idText : null,
  };
}

// ---- blending ---------------------------------------------------------------------------------------

const lerp = (a: number, b: number, t: number) => a + (b - a) * t;
const lerp3 = (a: Vec3, b: Vec3, t: number): Vec3 => [lerp(a[0], b[0], t), lerp(a[1], b[1], t), lerp(a[2], b[2], t)];

export function slerp(a: Quat, b: Quat, t: number): Quat {
  let dot = a[0] * b[0] + a[1] * b[1] + a[2] * b[2] + a[3] * b[3];
  const bb: Quat = dot < 0 ? [-b[0], -b[1], -b[2], -b[3]] : b;
  dot = Math.abs(dot);
  if (dot > 0.9995) {
    const q: Quat = [lerp(a[0], bb[0], t), lerp(a[1], bb[1], t), lerp(a[2], bb[2], t), lerp(a[3], bb[3], t)];
    const n = Math.hypot(...q);
    return [q[0] / n, q[1] / n, q[2] / n, q[3] / n];
  }
  const theta = Math.acos(Math.min(1, dot));
  const s = Math.sin(theta);
  const wa = Math.sin((1 - t) * theta) / s, wb = Math.sin(t * theta) / s;
  return [a[0] * wa + bb[0] * wb, a[1] * wa + bb[1] * wb, a[2] * wa + bb[2] * wb, a[3] * wa + bb[3] * wb];
}

export function blendSky(a: SkySettings, b: SkySettings, t: number): SkySettings {
  return {
    sunlightColor: lerp3(a.sunlightColor, b.sunlightColor, t), ambient: lerp3(a.ambient, b.ambient, t),
    blueHorizon: lerp3(a.blueHorizon, b.blueHorizon, t), blueDensity: lerp3(a.blueDensity, b.blueDensity, t),
    hazeHorizon: lerp(a.hazeHorizon, b.hazeHorizon, t), hazeDensity: lerp(a.hazeDensity, b.hazeDensity, t),
    densityMultiplier: lerp(a.densityMultiplier, b.densityMultiplier, t), distanceMultiplier: lerp(a.distanceMultiplier, b.distanceMultiplier, t),
    maxY: lerp(a.maxY, b.maxY, t), glow: lerp3(a.glow, b.glow, t), cloudShadow: lerp(a.cloudShadow, b.cloudShadow, t), gamma: lerp(a.gamma, b.gamma, t),
    sunRotation: slerp(a.sunRotation, b.sunRotation, t), moonRotation: slerp(a.moonRotation, b.moonRotation, t),
    moonBrightness: lerp(a.moonBrightness, b.moonBrightness, t), starBrightness: lerp(a.starBrightness, b.starBrightness, t),
    sunScale: lerp(a.sunScale, b.sunScale, t), moonScale: lerp(a.moonScale, b.moonScale, t), cloudColor: lerp3(a.cloudColor, b.cloudColor, t),
  };
}

export function blendWater(a: WaterSettings, b: WaterSettings, t: number): WaterSettings {
  const lerp2 = (x: [number, number], y: [number, number]): [number, number] => [lerp(x[0], y[0], t), lerp(x[1], y[1], t)];
  return {
    fogColor: lerp3(a.fogColor, b.fogColor, t), fogDensity: lerp(a.fogDensity, b.fogDensity, t), fogMod: lerp(a.fogMod, b.fogMod, t),
    fresnelOffset: lerp(a.fresnelOffset, b.fresnelOffset, t), fresnelScale: lerp(a.fresnelScale, b.fresnelScale, t),
    blurMultiplier: lerp(a.blurMultiplier, b.blurMultiplier, t), normalScale: lerp3(a.normalScale, b.normalScale, t),
    scaleAbove: lerp(a.scaleAbove, b.scaleAbove, t), scaleBelow: lerp(a.scaleBelow, b.scaleBelow, t),
    wave1Direction: lerp2(a.wave1Direction, b.wave1Direction), wave2Direction: lerp2(a.wave2Direction, b.wave2Direction),
    normalMapId: t < 0.5 ? a.normalMapId : b.normalMapId,
  };
}

// ---- day cycle ---------------------------------------------------------------------------------------------

/** Where in the cycle we are (0..1) at `nowSeconds` (Unix time), given the region's day length and offset in seconds. */
export function dayFraction(nowSeconds: number, dayLength: number, dayOffset = 0): number {
  const length = finite(dayLength) && dayLength > 0 ? dayLength : 14400;
  const t = (((nowSeconds + (finite(dayOffset) ? dayOffset : 0)) % length) + length) % length;
  return t / length;
}

interface Keyframe { time: number; frame: Raw }

function trackKeyframes(cycle: Raw, trackIndex: number): Keyframe[] {
  const track = cycle?.tracks?.[trackIndex];
  const frames = cycle?.frames instanceof Map ? Object.fromEntries(cycle.frames) : cycle?.frames;
  if (!Array.isArray(track) || !frames) return [];
  const result: Keyframe[] = [];
  for (const entry of track) {
    const time = Number(pick(entry, 'keyKeyframe', 'key_keyframe'));
    const name = pick(entry, 'keyName', 'key_name');
    const frame = frames[name];
    if (Number.isFinite(time) && frame) result.push({ time, frame });
  }
  return result.sort((a, b) => a.time - b.time);
}

/**
 * Raw frames either side of `fraction` on a track, and how far between them (0..1), wrapping around the
 * day. Returns null when the track has no keyframes.
 */
export function bracket(cycle: Raw, trackIndex: number, fraction: number): { a: Raw; b: Raw; t: number } | null {
  const keys = trackKeyframes(cycle, trackIndex);
  if (!keys.length) return null;
  if (keys.length === 1) return { a: keys[0].frame, b: keys[0].frame, t: 0 };
  let next = keys.findIndex((k) => k.time > fraction);
  let prev: Keyframe, after: Keyframe, span: number, into: number;
  if (next === -1) { prev = keys[keys.length - 1]; after = keys[0]; span = 1 - prev.time + after.time; into = fraction - prev.time; }
  else if (next === 0) { prev = keys[keys.length - 1]; after = keys[0]; span = 1 - prev.time + after.time; into = fraction + 1 - prev.time; }
  else { prev = keys[next - 1]; after = keys[next]; span = after.time - prev.time; into = fraction - prev.time; }
  void next;
  return { a: prev.frame, b: after.frame, t: span > 1e-9 ? Math.min(1, Math.max(0, into / span)) : 0 };
}

/** Sky for the ground-level sky track (track 1) at a point in the day. Falls back to the first sky frame, then defaults. */
export function skyAt(cycle: Raw | null | undefined, fraction: number): SkySettings {
  if (!cycle) return { ...DEFAULT_SKY };
  for (const track of [1, 2, 3, 4]) {
    const span = bracket(cycle, track, fraction);
    if (span) return blendSky(normalizeSky(span.a), normalizeSky(span.b), span.t);
  }
  const frames = cycle.frames instanceof Map ? [...cycle.frames.values()] : Object.values(cycle.frames || {});
  const sky = frames.find((frame: any) => pick(frame, 'type') === 'sky' || pick(frame, 'sunlightColor', 'sunlight_color'));
  return sky ? normalizeSky(sky as Raw) : { ...DEFAULT_SKY };
}

/** Water for the water track (track 0) at a point in the day. */
export function waterAt(cycle: Raw | null | undefined, fraction: number): WaterSettings {
  if (cycle) {
    const span = bracket(cycle, 0, fraction);
    if (span) return blendWater(normalizeWater(span.a), normalizeWater(span.b), span.t);
    const frames = cycle.frames instanceof Map ? [...cycle.frames.values()] : Object.values(cycle.frames || {});
    const water = frames.find((frame: any) => pick(frame, 'type') === 'water' || pick(frame, 'waterFogColor', 'water_fog_color'));
    if (water) return normalizeWater(water as Raw);
  }
  return { ...DEFAULT_WATER };
}

// ---- derived values ---------------------------------------------------------------------------------------------

/** World direction of the sun/moon: the +X axis rotated by their rotation quaternion. */
export function directionFrom(q: Quat): Vec3 {
  const [x, y, z, w] = q;
  const v: Vec3 = [1 - 2 * (y * y + z * z), 2 * (x * y + w * z), 2 * (x * z - w * y)];
  const n = Math.hypot(...v);
  return n > 1e-9 ? [v[0] / n, v[1] / n, v[2] / n] : [0, 0, 1];
}

export interface SkyState {
  sunDirection: Vec3;
  moonDirection: Vec3;
  sunUp: boolean;
  moonUp: boolean;
  /** Direction toward whichever of sun / moon is up (the sun when both are, as the viewer does). */
  lightDirection: Vec3;
  /** 1 by day, scaled moon brightness at night, 0 when neither is up. */
  sunMoonGlowFactor: number;
  /** Sunlight on surfaces after atmospheric attenuation. */
  sunDiffuse: Vec3;
  /** Ambient light on surfaces (raised under clouds). */
  sunAmbient: Vec3;
  moonDiffuse: Vec3;
  moonAmbient: Vec3;
}

const LIGHT_LIMIT = 1.1920929e-7 * 8;

export function skyState(sky: SkySettings, directions: { sun?: Vec3; moon?: Vec3 } = {}): SkyState {
  const unit = (v: Vec3 | undefined): Vec3 | null => {
    if (!v || !v.every(finite)) return null;
    const n = Math.hypot(...v);
    return n > 1e-9 ? [v[0] / n, v[1] / n, v[2] / n] : null;
  };
  const sunDirection = unit(directions.sun) ?? directionFrom(sky.sunRotation);
  const moonDirection = unit(directions.moon) ?? directionFrom(sky.moonRotation);
  const sunUp = sunDirection[2] >= 0, moonUp = moonDirection[2] >= 0;
  const lightDirection = sunUp ? sunDirection : moonDirection; // the viewer takes the moon when the sun is down
  const sunMoonGlowFactor = sunUp ? 1 : moonUp ? sky.moonBrightness * 0.25 : 0;

  const lightAtten: Vec3 = sky.blueDensity.map((b) => (b + sky.hazeDensity * 0.25) * sky.densityMultiplier * sky.maxY) as Vec3;
  const transmittance: Vec3 = sky.blueDensity.map((b) => Math.exp(-(b + sky.hazeDensity) * sky.densityMultiplier * sky.maxY)) as Vec3;
  let lighty = Math.abs(lightDirection[2]);
  if (lighty >= LIGHT_LIMIT) lighty = 1 / lighty;
  lighty = Math.max(LIGHT_LIMIT, lighty);

  const attenuate = (color: Vec3): Vec3 => color.map((c, i) => c * Math.exp(-lightAtten[i] * lighty) * transmittance[i]) as Vec3;
  const sunDiffuse = attenuate(sky.sunlightColor);
  const sunAmbient = sky.ambient.map((a) => a + (1 - a) * sky.cloudShadow * 0.5) as Vec3;
  const moonBrightness = moonUp ? sky.moonBrightness : 0.001;
  const moonDiffuse = attenuate(sky.sunlightColor).map((c) => c * moonBrightness) as Vec3; // the moon shares the sunlight colour
  const moonAmbient: Vec3 = [0.66 * 0.0125, 0.66 * 0.0125, 1.2 * 0.0125];
  return { sunDirection, moonDirection, sunUp, moonUp, lightDirection, sunMoonGlowFactor, sunDiffuse, sunAmbient, moonDiffuse, moonAmbient };
}
