import { describe, expect, it } from 'vitest';
import { Camera3D } from '../camera-3d';
import { extractFrustum, testAABB, transformAABB, multiplyMat4, INSIDE, INTERSECT, OUTSIDE } from '../frustum';

/** First-person camera at the origin looking along +Y (yaw 0), Z up. */
function cameraAtOrigin() {
  const camera = new Camera3D();
  camera.mode = 'first-person';
  camera.position = [0, 0, 0];
  camera.rotation = [0, 0, 0];
  camera.updateMatrices();
  return camera;
}
const frustumOf = (camera: Camera3D) => extractFrustum(multiplyMat4(camera.getProjectionMatrix(), camera.getViewMatrix()))!;

describe('frustum', () => {
  it('classifies boxes in front of, straddling and behind the camera', () => {
    const frustum = frustumOf(cameraAtOrigin());

    expect(testAABB(frustum, [-1, 20, -1], [1, 22, 1])).toBe(INSIDE);
    expect(testAABB(frustum, [-1, -20, -1], [1, -18, 1])).toBe(OUTSIDE); // behind
    expect(testAABB(frustum, [500, 20, -1], [502, 22, 1])).toBe(OUTSIDE); // far to the side
    expect(testAABB(frustum, [-1, 1500, -1], [1, 1502, 1])).toBe(OUTSIDE); // beyond far plane
    expect(testAABB(frustum, [-1000, 20, -1], [1000, 22, 1])).toBe(INTERSECT); // spans the view
  });

  it('keeps a box that only overlaps the frustum corner', () => {
    const frustum = frustumOf(cameraAtOrigin());
    // 60 degree vertical FOV at distance 10 -> half height ~5.77; this box straddles the top edge.
    expect(testAABB(frustum, [-1, 9, 4], [1, 11, 8])).toBe(INTERSECT);
  });

  it('returns null for degenerate matrices so callers can skip culling', () => {
    expect(extractFrustum(new Float32Array(16))).toBeNull();
    expect(extractFrustum(new Float32Array(16).fill(NaN))).toBeNull();
    expect(extractFrustum([1, 2, 3])).toBeNull();
  });

  it('multiplies column-major matrices as a * b', () => {
    const translate = (x: number) => new Float32Array([1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, x, 0, 0, 1]);
    const scale2 = new Float32Array([2, 0, 0, 0, 0, 2, 0, 0, 0, 0, 2, 0, 0, 0, 0, 1]);
    // translate(5) * scale(2) scales first, then translates: origin stays at x=5, unit x maps to 7.
    const m = multiplyMat4(translate(5), scale2);
    expect(Array.from(m.slice(12, 15))).toEqual([5, 0, 0]);
    expect(m[0]).toBe(2);
    // scale(2) * translate(5) translates first: origin ends at x=10.
    expect(Array.from(multiplyMat4(scale2, translate(5)).slice(12, 15))).toEqual([10, 0, 0]);
  });

  it('encloses a rotated, scaled box with its world-space AABB', () => {
    // 90 degrees about Z then scale (2,4,6) then translate (10,0,0), column-major.
    const m = new Float32Array([
      0, 2, 0, 0, // x axis -> +y, scaled by 2
      -4, 0, 0, 0, // y axis -> -x, scaled by 4
      0, 0, 6, 0,
      10, 0, 0, 1,
    ]);
    const { min, max } = transformAABB(m, [-0.5, -0.5, -0.5], [0.5, 0.5, 0.5]);
    expect(min).toEqual([8, -1, -3]);
    expect(max).toEqual([12, 1, 3]);
  });
});
