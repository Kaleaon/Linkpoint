/**
 * Ray construction and ray/box intersection for object picking.
 * All matrices are column-major (WebGL layout).
 */

export interface Ray {
  origin: number[];
  /** Unit length, so `t` values are world-space distances. */
  direction: number[];
}

/** General 4x4 inverse. Returns null when the matrix is singular. */
export function invertMat4(m: ArrayLike<number>): Float64Array | null {
  const a00 = m[0], a01 = m[1], a02 = m[2], a03 = m[3];
  const a10 = m[4], a11 = m[5], a12 = m[6], a13 = m[7];
  const a20 = m[8], a21 = m[9], a22 = m[10], a23 = m[11];
  const a30 = m[12], a31 = m[13], a32 = m[14], a33 = m[15];

  const b00 = a00 * a11 - a01 * a10, b01 = a00 * a12 - a02 * a10;
  const b02 = a00 * a13 - a03 * a10, b03 = a01 * a12 - a02 * a11;
  const b04 = a01 * a13 - a03 * a11, b05 = a02 * a13 - a03 * a12;
  const b06 = a20 * a31 - a21 * a30, b07 = a20 * a32 - a22 * a30;
  const b08 = a20 * a33 - a23 * a30, b09 = a21 * a32 - a22 * a31;
  const b10 = a21 * a33 - a23 * a31, b11 = a22 * a33 - a23 * a32;

  const determinant = b00 * b11 - b01 * b10 + b02 * b09 + b03 * b08 - b04 * b07 + b05 * b06;
  if (!determinant || !Number.isFinite(determinant)) return null;
  const inv = 1 / determinant;

  return Float64Array.from([
    (a11 * b11 - a12 * b10 + a13 * b09) * inv,
    (a02 * b10 - a01 * b11 - a03 * b09) * inv,
    (a31 * b05 - a32 * b04 + a33 * b03) * inv,
    (a22 * b04 - a21 * b05 - a23 * b03) * inv,
    (a12 * b08 - a10 * b11 - a13 * b07) * inv,
    (a00 * b11 - a02 * b08 + a03 * b07) * inv,
    (a32 * b02 - a30 * b05 - a33 * b01) * inv,
    (a20 * b05 - a22 * b02 + a23 * b01) * inv,
    (a10 * b10 - a11 * b08 + a13 * b06) * inv,
    (a01 * b08 - a00 * b10 - a03 * b06) * inv,
    (a30 * b04 - a31 * b02 + a33 * b00) * inv,
    (a21 * b02 - a20 * b04 - a23 * b00) * inv,
    (a11 * b07 - a10 * b09 - a12 * b06) * inv,
    (a00 * b09 - a01 * b07 + a02 * b06) * inv,
    (a31 * b01 - a30 * b03 - a32 * b00) * inv,
    (a20 * b03 - a21 * b01 + a22 * b00) * inv,
  ]);
}

function transformPoint(m: ArrayLike<number>, x: number, y: number, z: number): number[] {
  const w = m[3] * x + m[7] * y + m[11] * z + m[15];
  const inv = w ? 1 / w : 1;
  return [
    (m[0] * x + m[4] * y + m[8] * z + m[12]) * inv,
    (m[1] * x + m[5] * y + m[9] * z + m[13]) * inv,
    (m[2] * x + m[6] * y + m[10] * z + m[14]) * inv,
  ];
}

/**
 * Unproject normalized device coordinates through the inverse
 * view-projection matrix. The origin is the point on the near plane, which is
 * also correct for orthographic projections.
 */
export function rayFromNDC(inverseViewProjection: ArrayLike<number>, ndcX: number, ndcY: number): Ray | null {
  const near = transformPoint(inverseViewProjection, ndcX, ndcY, -1);
  const far = transformPoint(inverseViewProjection, ndcX, ndcY, 1);
  const direction = [far[0] - near[0], far[1] - near[1], far[2] - near[2]];
  const length = Math.hypot(direction[0], direction[1], direction[2]);
  if (!Number.isFinite(length) || length === 0) return null;
  return { origin: near, direction: direction.map((value) => value / length) };
}

/**
 * Slab test of a ray against an oriented box defined by a local-space AABB and
 * the model matrix that places it in the world. Because the ray is moved into
 * local space without renormalizing, the returned `t` is a world-space distance
 * along the original ray. Returns null on a miss or when the box is entirely
 * behind the origin. A ray starting inside the box reports t = 0.
 */
export function intersectRayOrientedBox(
  ray: Ray,
  modelMatrix: ArrayLike<number>,
  localMin: ArrayLike<number>,
  localMax: ArrayLike<number>,
): number | null {
  const inverse = invertMat4(modelMatrix);
  if (!inverse) return null;
  const o = transformPoint(inverse, ray.origin[0], ray.origin[1], ray.origin[2]);
  // Direction is a vector, so ignore translation.
  const dx = ray.direction[0], dy = ray.direction[1], dz = ray.direction[2];
  const d = [
    inverse[0] * dx + inverse[4] * dy + inverse[8] * dz,
    inverse[1] * dx + inverse[5] * dy + inverse[9] * dz,
    inverse[2] * dx + inverse[6] * dy + inverse[10] * dz,
  ];

  let tMin = 0;
  let tMax = Infinity;
  for (let axis = 0; axis < 3; axis++) {
    if (Math.abs(d[axis]) < 1e-12) {
      if (o[axis] < localMin[axis] || o[axis] > localMax[axis]) return null;
      continue;
    }
    let t1 = (localMin[axis] - o[axis]) / d[axis];
    let t2 = (localMax[axis] - o[axis]) / d[axis];
    if (t1 > t2) [t1, t2] = [t2, t1];
    tMin = Math.max(tMin, t1);
    tMax = Math.min(tMax, t2);
    if (tMin > tMax) return null;
  }
  return tMin;
}

export interface RayTriangleHit {
  /** Distance along the ray (`t`). */
  t: number;
  /** Barycentric coordinate `u` (weight for vertex v1). */
  u: number;
  /** Barycentric coordinate `v` (weight for vertex v2). */
  v: number;
  /** Barycentric coordinate `w` (weight for vertex v0, `1 - u - v`). */
  w: number;
  /** Intersection point in ray space (origin + direction * t). */
  point: number[];
  /** Interpolated texture coordinates `[u, v]` if provided at vertices. */
  uv?: [number, number];
}

/**
 * Möller–Trumbore ray-triangle intersection.
 * Computes triangle intersection distance `t`, barycentric coordinates `(u, v, w)`,
 * and interpolated surface texture coordinates (`uv`).
 *
 * Supports double-sided intersections for HUD prims while skipping parallel or
 * degenerate zero-area triangles.
 */
export function intersectRayTriangle(
  ray: Ray,
  v0: ArrayLike<number>,
  v1: ArrayLike<number>,
  v2: ArrayLike<number>,
  uv0?: ArrayLike<number>,
  uv1?: ArrayLike<number>,
  uv2?: ArrayLike<number>,
): RayTriangleHit | null {
  const e1x = v1[0] - v0[0];
  const e1y = v1[1] - v0[1];
  const e1z = v1[2] - v0[2];

  const e2x = v2[0] - v0[0];
  const e2y = v2[1] - v0[1];
  const e2z = v2[2] - v0[2];

  const px = ray.direction[1] * e2z - ray.direction[2] * e2y;
  const py = ray.direction[2] * e2x - ray.direction[0] * e2z;
  const pz = ray.direction[0] * e2y - ray.direction[1] * e2x;

  const det = e1x * px + e1y * py + e1z * pz;
  if (Math.abs(det) < 1e-9) return null; // Parallel or degenerate triangle

  const invDet = 1 / det;

  const tx = ray.origin[0] - v0[0];
  const ty = ray.origin[1] - v0[1];
  const tz = ray.origin[2] - v0[2];

  const u = (tx * px + ty * py + tz * pz) * invDet;
  if (u < 0 || u > 1) return null;

  const qx = ty * e1z - tz * e1y;
  const qy = tz * e1x - tx * e1z;
  const qz = tx * e1y - ty * e1x;

  const v = (ray.direction[0] * qx + ray.direction[1] * qy + ray.direction[2] * qz) * invDet;
  if (v < 0 || u + v > 1) return null;

  const t = (e2x * qx + e2y * qy + e2z * qz) * invDet;
  if (t <= 1e-7) return null; // Behind or at ray origin

  const w = 1 - u - v;
  const point = [
    ray.origin[0] + ray.direction[0] * t,
    ray.origin[1] + ray.direction[1] * t,
    ray.origin[2] + ray.direction[2] * t,
  ];

  let interpolatedUV: [number, number] | undefined;
  if (uv0 && uv1 && uv2) {
    interpolatedUV = [
      w * uv0[0] + u * uv1[0] + v * uv2[0],
      w * uv0[1] + u * uv1[1] + v * uv2[1],
    ];
  }

  return {
    t,
    u,
    v,
    w,
    point,
    ...(interpolatedUV ? { uv: interpolatedUV } : {}),
  };
}
