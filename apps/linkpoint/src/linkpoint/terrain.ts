/**
 * Terrain texturing the way the official viewer does it (LLVLComposition::generateHeights):
 * every terrain texel gets a composition value 0..3 from its height plus Perlin noise,
 * measured against a start height and height range that are interpolated between the four
 * region corners. The value then blends the region's four detail textures (0 = lowest,
 * usually dirt, 3 = highest, usually rock).
 */

export const TERRAIN_LAYERS = 4;

// ---- Perlin noise (classic gradient noise, range about -1..1) -------------------------------

const PERM = (() => {
  const base = [
    151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23,
    190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20,
    125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133, 230, 220,
    105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200, 196,
    135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250, 124, 123, 5, 202, 38, 147, 118, 126, 255,
    82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221,
    153, 101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228,
    251, 34, 242, 193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192, 214, 31, 181, 199, 106,
    157, 184, 84, 204, 176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243, 141, 128, 195, 78,
    66, 215, 61, 156, 180,
  ];
  return [...base, ...base];
})();

const fade = (t: number) => t * t * t * (t * (t * 6 - 15) + 10);
const lerp = (t: number, a: number, b: number) => a + t * (b - a);
function grad(hash: number, x: number, y: number) {
  const h = hash & 7;
  const u = h < 4 ? x : y, v = h < 4 ? y : x;
  return ((h & 1) === 0 ? u : -u) + ((h & 2) === 0 ? v : -v);
}

/** 2D Perlin noise, deterministic, roughly in -1..1. */
export function noise2(x: number, y: number): number {
  const X = Math.floor(x) & 255, Y = Math.floor(y) & 255;
  x -= Math.floor(x); y -= Math.floor(y);
  const u = fade(x), v = fade(y);
  const a = PERM[X] + Y, b = PERM[X + 1] + Y;
  return lerp(v,
    lerp(u, grad(PERM[a], x, y), grad(PERM[b], x - 1, y)),
    lerp(u, grad(PERM[a + 1], x, y - 1), grad(PERM[b + 1], x - 1, y - 1)));
}

/** Sum of noise octaves (absolute value), as the viewer's turbulence2. */
export function turbulence2(x: number, y: number, octaves: number): number {
  let t = 0, f = 1;
  for (let i = 0; i < octaves; i++) {
    t += Math.abs(noise2(x * f, y * f)) / f;
    f *= 2;
  }
  return t;
}

// ---- composition -------------------------------------------------------------------------------

export interface TerrainParams {
  /** Start height at each region corner, in the RegionHandshake order 00, 01, 10, 11 = SW, SE, NW, NE. */
  startHeights: number[];
  heightRanges: number[];
  /** Global metres of the region's south-west corner, so noise lines up across region borders. */
  origin: [number, number];
}

const bilinear = (sw: number, se: number, nw: number, ne: number, fx: number, fy: number) =>
  lerp(fy, lerp(fx, sw, se), lerp(fx, nw, ne));

/**
 * Composition value 0..3 for every height sample. `heights` is size*size, row = y (south to north).
 * Corner order in params is SW, SE, NW, NE.
 */
export function terrainComposition(heights: ArrayLike<number>, size: number, params: TerrainParams): Float32Array {
  const out = new Float32Array(size * size);
  const slopeSquared = 1.5 * 1.5;
  const xyScaleInv = 1 / 4.9215, noiseMagnitude = 2;
  const [sw, se, nw, ne] = params.startHeights, [rsw, rse, rnw, rne] = params.heightRanges;
  const scale = 256 / size; // metres per sample
  for (let j = 0; j < size; j++) {
    for (let i = 0; i < size; i++) {
      const fx = i / size, fy = j / size;
      const start = bilinear(sw, se, nw, ne, fx, fy);
      const range = Math.max(bilinear(rsw, rse, rnw, rne, fx, fy), 0.001);
      const height = Number(heights[j * size + i]) || 0;
      const x = (params.origin[0] + i * scale) * xyScaleInv;
      const y = (params.origin[1] + j * scale) * xyScaleInv;
      // low frequency component for large divisions, plus a high frequency turbulence term
      // (the viewer's 2D noise ignores the height coordinate it computes)
      let twiddle = noise2(x * 0.2222222222, y * 0.2222222222) * 6.5;
      twiddle += turbulence2(x, y, 2) * slopeSquared;
      twiddle *= noiseMagnitude;
      const value = ((height + twiddle - start) * TERRAIN_LAYERS) / range;
      out[j * size + i] = Math.min(3, Math.max(0, value));
    }
  }
  return out;
}

/** Pack composition values into RGBA bytes (R = value/3) for upload as a texture; rows are written top-down for a flipped upload. */
export function compositionTexture(values: Float32Array, size: number): Uint8Array {
  const rgba = new Uint8Array(size * size * 4);
  for (let j = 0; j < size; j++) {
    const row = size - 1 - j; // UNPACK_FLIP_Y: memory row 0 is the top (north) of the texture
    for (let i = 0; i < size; i++) {
      const o = (row * size + i) * 4;
      rgba[o] = Math.round((values[j * size + i] / 3) * 255);
      rgba[o + 3] = 255;
    }
  }
  return rgba;
}

/** Layer blend weights for a composition value: a tent between neighbouring layers. */
export function layerWeights(value: number): [number, number, number, number] {
  const v = Math.min(3, Math.max(0, value));
  const base = Math.min(2, Math.floor(v));
  const f = v - base;
  const w: [number, number, number, number] = [0, 0, 0, 0];
  w[base] = 1 - f;
  w[base + 1] = f;
  return w;
}

/** Colours shown for a layer until its detail texture arrives (dirt, grass, mountain, rock). */
export const FALLBACK_LAYER_COLORS: Array<[number, number, number]> = [
  [0.45, 0.36, 0.26], [0.33, 0.45, 0.22], [0.5, 0.45, 0.4], [0.58, 0.56, 0.54],
];

/** Metres covered by one repeat of a detail texture. */
export const DETAIL_TILE_METRES = 16;

export const TERRAIN_VERTEX_SHADER = `
  attribute vec3 aPosition;
  attribute vec3 aNormal;
  attribute vec2 aTexCoord;
  uniform mat4 uModelMatrix;
  uniform mat4 uViewMatrix;
  uniform mat4 uProjectionMatrix;
  uniform mat3 uNormalMatrix;
  varying vec3 vNormal;
  varying vec2 vTexCoord;
  varying vec3 vPosition;
  void main() {
    vec4 worldPos = uModelMatrix * vec4(aPosition, 1.0);
    vPosition = worldPos.xyz;
    vNormal = normalize(uNormalMatrix * aNormal);
    vTexCoord = aTexCoord;
    gl_Position = uProjectionMatrix * uViewMatrix * worldPos;
  }
`;

export const TERRAIN_FRAGMENT_SHADER = `
  #ifdef GL_FRAGMENT_PRECISION_HIGH
  precision highp float;
  #else
  precision mediump float;
  #endif
  varying vec3 vNormal;
  varying vec2 vTexCoord;
  varying vec3 vPosition;
  uniform vec3 uLightPos;
  uniform vec3 uLightColor;
  uniform vec3 uAmbientColor;
  uniform sampler2D uComposition;
  uniform sampler2D uDetail0;
  uniform sampler2D uDetail1;
  uniform sampler2D uDetail2;
  uniform sampler2D uDetail3;
  uniform vec4 uDetailUse;      // 1.0 when the detail texture is loaded
  uniform vec3 uFallback0;
  uniform vec3 uFallback1;
  uniform vec3 uFallback2;
  uniform vec3 uFallback3;
  uniform float uTileScale;     // detail repeats across the whole region
  void main() {
    float value = clamp(texture2D(uComposition, vTexCoord).r * 3.0, 0.0, 3.0);
    float base = min(floor(value), 2.0);
    float f = value - base;
    vec2 tiled = vTexCoord * uTileScale;
    vec3 c0 = mix(uFallback0, texture2D(uDetail0, tiled).rgb, uDetailUse.x);
    vec3 c1 = mix(uFallback1, texture2D(uDetail1, tiled).rgb, uDetailUse.y);
    vec3 c2 = mix(uFallback2, texture2D(uDetail2, tiled).rgb, uDetailUse.z);
    vec3 c3 = mix(uFallback3, texture2D(uDetail3, tiled).rgb, uDetailUse.w);
    vec3 lower = base < 0.5 ? c0 : (base < 1.5 ? c1 : c2);
    vec3 upper = base < 0.5 ? c1 : (base < 1.5 ? c2 : c3);
    vec3 albedo = mix(lower, upper, f);
    vec3 normal = normalize(vNormal);
    vec3 lightDir = normalize(uLightPos - vPosition);
    float diff = max(dot(normal, lightDir), 0.0);
    vec3 light = pow(min(uAmbientColor + diff * uLightColor, vec3(1.0)), vec3(1.0 / 2.2));
    gl_FragColor = vec4(albedo * light, 1.0);
  }
`;
