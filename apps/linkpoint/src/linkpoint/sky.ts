/**
 * Sky dome, star field and animated water for the WebGL viewer.
 *
 * Behaviour is modelled on the Lumiya Android viewer's Windlight renderer:
 *  - sky colour  = (blueHorizon + sunlight + ambient) * blueDensity, per channel
 *  - haze colour = hazeDensity * ambient, blended in below `hazeHorizon`
 *    (haze factor = clamp((hazeHorizon - elevation) * 2, 0, 1))
 *  - water       = four summed travelling sine waves driving the surface normal
 * The wave tables below were checked against the smali for Lumiya's
 * TerrainPatchGeometry (see docs/LUMIYA_RENDERING_ANALYSIS.md). Geometry and
 * shaders are written for this renderer; no Lumiya source is reused.
 *
 * World space is Z-up, like Second Life.
 */

import { ATMOSPHERE_GLSL } from './atmosphere';

export type Vec3 = [number, number, number];

export interface SkyUniforms {
  skyColor: Vec3;
  hazeHorizon: number;
  hazeColor: Vec3;
  starBrightness: number;
}

export interface WaterUniforms {
  color: Vec3;
  height: number;
}

/** Default region water level in Second Life, in metres. */
export const DEFAULT_WATER_HEIGHT = 20;

/**
 * Wave slope multiplier. The recovered amplitudes and frequencies give
 * slopes of up to ~9, which tilts the normal almost flat and sparkles; this
 * keeps ripples readable. Aesthetic choice, not taken from Lumiya.
 */
export const WATER_NORMAL_SCALE = 0.06;

/** Per-wave tables: frequency (rad/m), phase speed (rad/s), amplitude, direction. */
export const WATER_WAVES = {
  frequency: [17.951958, 12.566371, 8.975979, 15.707963],
  phase: [1.73, 0.64, 1.27, 0.9],
  amplitude: [0.5, 0.5, 0.3, 0.4],
  direction: [1, 0.3, 0.4, 0.75, -0.5, 0.7, 0.63, -0.3],
} as const;

/** Neutral clear-sky fallback used until the simulator sends an environment. */
const FALLBACK_SKY: SkyUniforms = {
  skyColor: [0.45, 0.68, 0.92],
  hazeHorizon: 0.19,
  hazeColor: [0.42, 0.5, 0.58],
  starBrightness: 0,
};
const FALLBACK_WATER_COLOR: Vec3 = [0.4, 0.4, 0.6];

const clamp01 = (value: number) => Math.max(0, Math.min(1, value));

/** Read an RGB triple from `[r,g,b,...]`, `{x,y,z}` or `{r,g,b}`. */
export function readVec3(value: unknown, fallback: Vec3): Vec3 {
  let channels: unknown[] | null = null;
  if (Array.isArray(value)) channels = value;
  else if (ArrayBuffer.isView(value)) channels = Array.from(value as unknown as ArrayLike<number>);
  else if (value && typeof value === 'object') {
    const o = value as Record<string, unknown>;
    if ('x' in o) channels = [o.x, o.y, o.z];
    else if ('r' in o) channels = [o.r, o.g, o.b];
  }
  if (!channels || channels.length < 3) return [...fallback] as Vec3;
  const numbers = channels.slice(0, 3).map(Number);
  return numbers.every(Number.isFinite) ? (numbers as Vec3) : ([...fallback] as Vec3);
}

/** Read a scalar from a number or the first element of a vector. */
export function readScalar(value: unknown, fallback: number): number {
  const candidate = Array.isArray(value) ? value[0] : value;
  const number = Number(candidate);
  return candidate !== null && candidate !== undefined && candidate !== '' && Number.isFinite(number) ? number : fallback;
}

/** Pick the first defined property, since EEP/Windlight exports use both camelCase and snake_case. */
function pick(source: any, ...names: string[]) {
  for (const name of names) if (source && source[name] !== undefined && source[name] !== null) return source[name];
  return undefined;
}

/** Derive sky-shader inputs from a simulator sky settings frame. */
export function computeSkyUniforms(sky: any): SkyUniforms {
  if (!sky || typeof sky !== 'object') return { ...FALLBACK_SKY, skyColor: [...FALLBACK_SKY.skyColor], hazeColor: [...FALLBACK_SKY.hazeColor] };
  const blueHorizon = pick(sky, 'blueHorizon', 'blue_horizon');
  const blueDensity = pick(sky, 'blueDensity', 'blue_density');
  const sunlight = pick(sky, 'sunlightColor', 'sunlight_color');
  const ambient = pick(sky, 'ambient', 'ambientColor', 'ambient_color');
  const hazeDensity = pick(sky, 'hazeDensity', 'haze_density');
  // Without the colour terms the formula is meaningless; keep the fallback sky.
  if (blueHorizon === undefined || blueDensity === undefined) {
    return { ...FALLBACK_SKY, skyColor: [...FALLBACK_SKY.skyColor], hazeColor: [...FALLBACK_SKY.hazeColor], starBrightness: clamp01(readScalar(pick(sky, 'starBrightness', 'star_brightness'), 0)) };
  }
  const horizon = readVec3(blueHorizon, FALLBACK_SKY.skyColor);
  const density = readVec3(blueDensity, [1, 1, 1]);
  const sun = readVec3(sunlight, [0, 0, 0]);
  const amb = readVec3(ambient, [0, 0, 0]);
  const haze = readScalar(hazeDensity, 0);
  return {
    skyColor: [0, 1, 2].map((i) => clamp01((horizon[i] + sun[i] + amb[i]) * density[i])) as Vec3,
    hazeHorizon: readScalar(pick(sky, 'hazeHorizon', 'haze_horizon'), FALLBACK_SKY.hazeHorizon),
    hazeColor: [0, 1, 2].map((i) => clamp01(haze * amb[i])) as Vec3,
    starBrightness: clamp01(readScalar(pick(sky, 'starBrightness', 'star_brightness'), 0)),
  };
}

/** Derive the water tint. EEP fog colours are very dark, so they are lightened toward the default. */
export function computeWaterUniforms(water: any, height = DEFAULT_WATER_HEIGHT): WaterUniforms {
  const fog = water && typeof water === 'object' ? pick(water, 'waterFogColor', 'water_fog_color') : undefined;
  const base = readVec3(fog, FALLBACK_WATER_COLOR);
  const color = base.map((value, i) => clamp01(value * 0.5 + FALLBACK_WATER_COLOR[i] * 0.5)) as Vec3;
  return { color, height: Number.isFinite(height) ? height : DEFAULT_WATER_HEIGHT };
}

/** True when the camera is below the water surface. */
export function isUnderWater(cameraZ: number, waterHeight: number) {
  return Number.isFinite(cameraZ) && cameraZ < waterHeight;
}

export interface RawGeometry {
  vertices: number[];
  indices: number[];
}

/** Unit-radius icosphere with `subdivisions` midpoint subdivisions (20 * 4^n faces), triangles wound inward. */
export function createSkyDome(subdivisions = 2): RawGeometry {
  const t = (1 + Math.sqrt(5)) / 2;
  const seed: Vec3[] = [
    [-1, t, 0], [1, t, 0], [-1, -t, 0], [1, -t, 0],
    [0, -1, t], [0, 1, t], [0, -1, -t], [0, 1, -t],
    [t, 0, -1], [t, 0, 1], [-t, 0, -1], [-t, 0, 1],
  ];
  const normalize = (v: Vec3): Vec3 => {
    const length = Math.hypot(v[0], v[1], v[2]);
    return [v[0] / length, v[1] / length, v[2] / length];
  };
  const vertices = seed.map(normalize);
  let faces: number[][] = [
    [0, 11, 5], [0, 5, 1], [0, 1, 7], [0, 7, 10], [0, 10, 11],
    [1, 5, 9], [5, 11, 4], [11, 10, 2], [10, 7, 6], [7, 1, 8],
    [3, 9, 4], [3, 4, 2], [3, 2, 6], [3, 6, 8], [3, 8, 9],
    [4, 9, 5], [2, 4, 11], [6, 2, 10], [8, 6, 7], [9, 8, 1],
  ];
  for (let level = 0; level < Math.max(0, Math.floor(subdivisions)); level++) {
    const cache = new Map<string, number>();
    const midpoint = (a: number, b: number) => {
      const key = a < b ? `${a}:${b}` : `${b}:${a}`;
      const cached = cache.get(key);
      if (cached !== undefined) return cached;
      const index = vertices.push(normalize([
        (vertices[a][0] + vertices[b][0]) / 2,
        (vertices[a][1] + vertices[b][1]) / 2,
        (vertices[a][2] + vertices[b][2]) / 2,
      ])) - 1;
      cache.set(key, index);
      return index;
    };
    const next: number[][] = [];
    for (const [a, b, c] of faces) {
      const ab = midpoint(a, b), bc = midpoint(b, c), ca = midpoint(c, a);
      next.push([a, ab, ca], [b, bc, ab], [c, ca, bc], [ab, bc, ca]);
    }
    faces = next;
  }
  // The seed faces wind counter-clockwise from outside. The camera sits inside
  // the dome, so flip them to stay front-facing if culling is ever enabled.
  return { vertices: vertices.flat(), indices: faces.flatMap(([a, b, c]) => [a, c, b]) };
}

/** Deterministic PRNG so the star field is stable between sessions. */
function mulberry32(seed: number) {
  let state = seed >>> 0;
  return () => {
    state = (state + 0x6d2b79f5) >>> 0;
    let t = state;
    t = Math.imul(t ^ (t >>> 15), t | 1);
    t ^= t + Math.imul(t ^ (t >>> 7), t | 61);
    return ((t ^ (t >>> 14)) >>> 0) / 4294967296;
  };
}

/** Uniformly distributed unit-sphere points, drawn as GL points. */
export function createStarField(count = 500, seed = 0x5eed): RawGeometry {
  const random = mulberry32(seed);
  const vertices: number[] = [];
  const indices: number[] = [];
  for (let i = 0; i < count; i++) {
    const z = random() * 2 - 1;
    const angle = random() * Math.PI * 2;
    const radius = Math.sqrt(1 - z * z);
    vertices.push(radius * Math.cos(angle), radius * Math.sin(angle), z);
    indices.push(i);
  }
  return { vertices, indices };
}

/** Large horizontal quad (z = 0) centred on a region; the shader supplies the real height. */
export function createWaterPlane(centre = 128, halfExtent = 1536): RawGeometry {
  const lo = centre - halfExtent, hi = centre + halfExtent;
  return { vertices: [lo, lo, 0, hi, lo, 0, hi, hi, 0, lo, hi, 0], indices: [0, 1, 2, 0, 2, 3] };
}

export const SKY_VERTEX_SHADER = `
  attribute vec3 aPosition;
  uniform mat4 uSkyViewMatrix; // view matrix with translation removed
  uniform mat4 uProjectionMatrix;
  varying vec3 vDirection;
  void main() {
    vDirection = aPosition;
    vec4 clip = uProjectionMatrix * uSkyViewMatrix * vec4(aPosition, 1.0);
    // z = w pins the dome to the far plane so it only fills untouched pixels.
    gl_Position = clip.xyww;
  }
`;

export const SKY_FRAGMENT_SHADER = `
  #ifdef GL_FRAGMENT_PRECISION_HIGH
  precision highp float;
  #else
  precision mediump float;
  #endif
  varying vec3 vDirection;
  uniform vec3 uSunDir;
  uniform vec3 uMoonDir;
  uniform float uSunRadius;      // angular radius, radians
  uniform float uMoonRadius;
  uniform float uMoonBrightness;
  uniform float uMoonUp;
  ${ATMOSPHERE_GLSL}
  void main() {
    vec3 dir = normalize(vDirection);
    vec3 color = atmosphereColor(dir);
    // sun disc with a soft edge and a tight bright core, dimmed as it sinks into the haze
    float sd = dot(dir, uSunDir);
    float sunDisc = smoothstep(cos(uSunRadius * 1.25), cos(uSunRadius), sd);
    float sunBloom = pow(max(sd, 0.0), 900.0) * 0.6;
    float sunVisible = uSunUp * smoothstep(-0.02, 0.06, uSunDir.z);
    vec3 sunColor = uSunlight * 8.0;
    color += sunColor * (sunDisc + sunBloom) * sunVisible;
    // moon disc
    float md = dot(dir, uMoonDir);
    float moonDisc = smoothstep(cos(uMoonRadius * 1.15), cos(uMoonRadius), md);
    color += vec3(0.82, 0.88, 1.0) * moonDisc * uMoonBrightness * 3.0 * uMoonUp * smoothstep(-0.02, 0.06, uMoonDir.z);
    gl_FragColor = vec4(toneMapSky(color), 1.0);
  }
`;

export const STARS_VERTEX_SHADER = `
  attribute vec3 aPosition;
  uniform mat4 uSkyViewMatrix;
  uniform mat4 uProjectionMatrix;
  void main() {
    vec4 clip = uProjectionMatrix * uSkyViewMatrix * vec4(aPosition, 1.0);
    gl_Position = clip.xyww;
    gl_PointSize = 2.0;
  }
`;

export const STARS_FRAGMENT_SHADER = `
  precision mediump float;
  uniform vec4 uStarColor;
  void main() {
    gl_FragColor = uStarColor;
  }
`;

export const WATER_VERTEX_SHADER = `
  attribute vec3 aPosition;
  uniform mat4 uViewMatrix;
  uniform mat4 uProjectionMatrix;
  uniform float uWaterHeight;
  varying vec3 vWorld;
  void main() {
    vWorld = vec3(aPosition.xy, uWaterHeight);
    gl_Position = uProjectionMatrix * uViewMatrix * vec4(vWorld, 1.0);
  }
`;

export const WATER_FRAGMENT_SHADER = `
  #ifdef GL_FRAGMENT_PRECISION_HIGH
  precision highp float;
  #else
  precision mediump float;
  #endif
  uniform vec3 uCameraPos;
  uniform vec3 uFogColor;      // refracted (under-surface) colour from the water settings
  uniform float uFogDensity;
  uniform float uFresnelScale;
  uniform float uFresnelOffset;
  uniform vec3 uLightDir;      // direction towards the light
  uniform vec3 uLightColor;    // surface light colour (sun or moon)
  uniform vec3 uSurfaceAmbient;
  uniform float uTime;
  uniform float uPixelAngle;   // approx. radians covered by one screen pixel
  uniform float uNormalScale;  // scales wave slope into a usable normal tilt
  uniform float uFrequency[4];
  uniform float uPhase[4];
  uniform float uAmplitude[4];
  uniform vec2 uDirection[4];
  varying vec3 vWorld;
  ${ATMOSPHERE_GLSL}
  void main() {
    vec3 toCamera = uCameraPos - vWorld;
    float distanceToCamera = length(toCamera);
    vec3 viewDir = toCamera / max(distanceToCamera, 0.0001);
    // Ground footprint of one pixel, stretched at grazing angles. A wave whose
    // wavelength is shorter than two footprints would alias into speckle, so
    // each wave fades out once it drops below that limit.
    float footprint = distanceToCamera * uPixelAngle / max(viewDir.z, 0.1);
    vec2 slope = vec2(0.0);
    for (int i = 0; i < 4; i++) {
      float weight = clamp(1.0 - footprint * uFrequency[i] / 3.14159, 0.0, 1.0);
      float arg = dot(uDirection[i], vWorld.xy) * uFrequency[i] + uTime * uPhase[i];
      slope += uDirection[i] * uAmplitude[i] * uFrequency[i] * cos(arg) * weight;
    }
    vec3 normal = normalize(vec3(-slope * uNormalScale, 1.0));
    // Fresnel from the water settings: more reflective at grazing angles.
    float cosV = max(dot(normal, viewDir), 0.0);
    float f1 = clamp(uFresnelOffset - uFresnelScale * cosV, 0.0, 1.0);
    float reflectance = clamp(f1 * f1 * 4.0, 0.0, 1.0);
    // reflect the actual sky (the same atmosphere the dome shows)
    vec3 reflectDir = reflect(-viewDir, normal);
    reflectDir.z = abs(reflectDir.z);
    vec3 skyReflection = toneMapSky(atmosphereColor(reflectDir));
    // refracted colour: the water's fog tint, lit by what reaches the surface
    vec3 refracted = pow(clamp(uFogColor * (uSurfaceAmbient + uLightColor * 0.6) * 1.6, 0.0, 1.0), vec3(1.0 / 2.2));
    // sun / moon glint
    float glint = pow(max(dot(reflect(-normalize(uLightDir), normal), viewDir), 0.0), 220.0);
    vec3 color = mix(refracted, skyReflection, reflectance) + uLightColor * glint * 3.0;
    // opacity from the fog density along the view path; reflections stay opaque
    float facing = max(viewDir.z, 0.12);
    float opacity = clamp(1.0 - exp(-uFogDensity * 0.6 / facing), 0.45, 0.97);
    gl_FragColor = vec4(color, max(opacity, reflectance));
  }
`;
