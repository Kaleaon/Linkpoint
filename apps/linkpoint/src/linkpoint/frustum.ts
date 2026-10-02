/**
 * Frustum extraction and bounding-box classification.
 *
 * Matrices are column-major (WebGL layout). Planes are stored as
 * [nx, ny, nz, d] with the normal pointing into the frustum, so a point is
 * inside a plane when `n·p + d >= 0`.
 */

export const OUTSIDE = -1;
export const INTERSECT = 0;
export const INSIDE = 1;
export type FrustumResult = typeof OUTSIDE | typeof INTERSECT | typeof INSIDE;

/** Six normalized planes: left, right, bottom, top, near, far. */
export type Frustum = Float64Array;

const PLANE_COUNT = 6;

/**
 * Extract the six clip planes from a view-projection matrix.
 * Returns null for degenerate matrices (all-zero, NaN) so callers can skip
 * culling instead of discarding the whole scene.
 */
export function extractFrustum(viewProjection: ArrayLike<number>): Frustum | null {
  if (!viewProjection || viewProjection.length < 16) return null;
  const m = viewProjection;
  const planes = new Float64Array(PLANE_COUNT * 4);
  for (let plane = 0; plane < PLANE_COUNT; plane++) {
    const row = plane >> 1; // 0: x, 1: y, 2: z
    const sign = plane & 1 ? -1 : 1;
    const base = plane * 4;
    for (let column = 0; column < 4; column++) {
      planes[base + column] = m[column * 4 + 3] + sign * m[column * 4 + row];
    }
    const length = Math.hypot(planes[base], planes[base + 1], planes[base + 2]);
    if (!Number.isFinite(length) || length < 1e-9) return null;
    for (let component = 0; component < 4; component++) planes[base + component] /= length;
  }
  return planes;
}

/** Classify an axis-aligned box against the frustum. */
export function testAABB(frustum: Frustum, min: ArrayLike<number>, max: ArrayLike<number>): FrustumResult {
  let result: FrustumResult = INSIDE;
  for (let plane = 0; plane < PLANE_COUNT; plane++) {
    const base = plane * 4;
    const nx = frustum[base], ny = frustum[base + 1], nz = frustum[base + 2], d = frustum[base + 3];
    // Positive vertex: the corner furthest along the plane normal.
    const px = nx >= 0 ? max[0] : min[0];
    const py = ny >= 0 ? max[1] : min[1];
    const pz = nz >= 0 ? max[2] : min[2];
    if (nx * px + ny * py + nz * pz + d < 0) return OUTSIDE;
    // Negative vertex: the corner furthest against the normal.
    const qx = nx >= 0 ? min[0] : max[0];
    const qy = ny >= 0 ? min[1] : max[1];
    const qz = nz >= 0 ? min[2] : max[2];
    if (nx * qx + ny * qy + nz * qz + d < 0) result = INTERSECT;
  }
  return result;
}

/**
 * Transform a local-space box by an affine column-major matrix and return the
 * world-space axis-aligned box that encloses it (Arvo's method).
 */
export function transformAABB(matrix: ArrayLike<number>, min: ArrayLike<number>, max: ArrayLike<number>) {
  const outMin = [matrix[12], matrix[13], matrix[14]];
  const outMax = [matrix[12], matrix[13], matrix[14]];
  for (let row = 0; row < 3; row++) {
    for (let column = 0; column < 3; column++) {
      const element = matrix[column * 4 + row];
      const a = element * min[column];
      const b = element * max[column];
      if (a < b) { outMin[row] += a; outMax[row] += b; } else { outMin[row] += b; outMax[row] += a; }
    }
  }
  return { min: outMin, max: outMax };
}

/** Column-major `a * b`. */
export function multiplyMat4(a: ArrayLike<number>, b: ArrayLike<number>): Float32Array {
  const out = new Float32Array(16);
  for (let column = 0; column < 4; column++) {
    for (let row = 0; row < 4; row++) {
      let sum = 0;
      for (let k = 0; k < 4; k++) sum += a[k * 4 + row] * b[column * 4 + k];
      out[column * 4 + row] = sum;
    }
  }
  return out;
}
