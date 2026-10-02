import { describe, expect, it } from 'vitest';
import { Camera3D } from '../camera-3d';
import { invertMat4, intersectRayOrientedBox, rayFromNDC } from '../ray-pick';
import { multiplyMat4 } from '../frustum';

const translate = (x: number, y: number, z: number) => new Float32Array([1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, x, y, z, 1]);
const scale = (x: number, y: number, z: number) => new Float32Array([x, 0, 0, 0, 0, y, 0, 0, 0, 0, z, 0, 0, 0, 0, 1]);
const rotateZ = (a: number) => new Float32Array([Math.cos(a), Math.sin(a), 0, 0, -Math.sin(a), Math.cos(a), 0, 0, 0, 0, 1, 0, 0, 0, 0, 1]);
const unitCube = { min: [-0.5, -0.5, -0.5], max: [0.5, 0.5, 0.5] };

describe('invertMat4', () => {
  it('inverts a general transform', () => {
    const m = multiplyMat4(translate(3, -2, 7), multiplyMat4(rotateZ(0.7), scale(2, 3, 4)));
    const product = multiplyMat4(m, invertMat4(m)!);
    for (let i = 0; i < 16; i++) expect(product[i]).toBeCloseTo(i % 5 === 0 ? 1 : 0, 5);
  });

  it('returns null for singular matrices', () => {
    expect(invertMat4(new Float32Array(16))).toBeNull();
  });
});

describe('intersectRayOrientedBox', () => {
  const ray = { origin: [0, 0, 0], direction: [0, 1, 0] };

  it('reports world-space distance to the near face', () => {
    const t = intersectRayOrientedBox(ray, translate(0, 10, 0), unitCube.min, unitCube.max);
    expect(t).toBeCloseTo(9.5, 6);
  });

  it('accounts for non-uniform scale', () => {
    const t = intersectRayOrientedBox(ray, multiplyMat4(translate(0, 10, 0), scale(1, 4, 1)), unitCube.min, unitCube.max);
    expect(t).toBeCloseTo(8, 6);
  });

  it('accounts for rotation', () => {
    // A 4 x 1 slab rotated 90 degrees about Z spans 4 along Y, so the near face moves from 9.5 to 8.
    const model = multiplyMat4(translate(0, 10, 0), multiplyMat4(rotateZ(Math.PI / 2), scale(4, 1, 1)));
    expect(intersectRayOrientedBox(ray, model, unitCube.min, unitCube.max)).toBeCloseTo(8, 5);
  });

  it('misses boxes off the ray, and boxes behind the origin', () => {
    expect(intersectRayOrientedBox(ray, translate(5, 10, 0), unitCube.min, unitCube.max)).toBeNull();
    expect(intersectRayOrientedBox(ray, translate(0, -10, 0), unitCube.min, unitCube.max)).toBeNull();
  });

  it('misses a box that a diagonal ray only passes beside', () => {
    const diagonal = { origin: [0, 0, 0], direction: [Math.SQRT1_2, Math.SQRT1_2, 0] };
    expect(intersectRayOrientedBox(diagonal, translate(0, 10, 0), unitCube.min, unitCube.max)).toBeNull();
    expect(intersectRayOrientedBox(diagonal, translate(0, 1.2, 0), unitCube.min, unitCube.max)).toBeNull(); // near miss
    expect(intersectRayOrientedBox(diagonal, translate(10, 10, 0), unitCube.min, unitCube.max)).toBeCloseTo(Math.SQRT2 * 9.5, 5);
  });

  it('reports 0 when the origin is inside the box and handles axis-parallel rays outside the slab', () => {
    expect(intersectRayOrientedBox(ray, translate(0, 0, 0), unitCube.min, unitCube.max)).toBe(0);
    expect(intersectRayOrientedBox({ origin: [0, 0, 5], direction: [0, 1, 0] }, translate(0, 10, 0), unitCube.min, unitCube.max)).toBeNull();
  });
});

describe('Camera3D.screenToWorldRay', () => {
  function orbitCamera() {
    const camera = new Camera3D();
    camera.setOrbitTarget(100, 100, 30);
    camera.setRotation(0.4, 2.1); // pitch, yaw
    camera.updateMatrices();
    return camera;
  }

  it('fires the centre pixel straight at the orbit target', () => {
    const camera = orbitCamera();
    const { origin, direction } = camera.screenToWorldRay(400, 300, 800, 600);
    const toTarget = [100 - origin[0], 100 - origin[1], 30 - origin[2]];
    const length = Math.hypot(...toTarget);
    for (let i = 0; i < 3; i++) expect(direction[i]).toBeCloseTo(toTarget[i] / length, 4);
  });

  it('honours aspect ratio and field of view at the screen edges', () => {
    const camera = new Camera3D();
    camera.mode = 'first-person';
    camera.position = [0, 0, 0];
    camera.rotation = [0, 0, 0]; // looking along +Y
    camera.aspect = 2;
    camera.fov = 60;
    camera.updateMatrices();

    const right = camera.screenToWorldRay(800, 300, 800, 600).direction; // right edge, vertical centre
    const expected = Math.atan(Math.tan((60 * Math.PI) / 360) * 2); // horizontal half-angle = atan(tan(v/2) * aspect)
    expect(Math.atan2(right[0], right[1])).toBeCloseTo(expected, 4);
    expect(right[2]).toBeCloseTo(0, 5);

    const top = camera.screenToWorldRay(400, 0, 800, 600).direction;
    expect(Math.atan2(top[2], top[1])).toBeCloseTo((60 * Math.PI) / 360, 4);
  });

  it('falls back safely for a zero-sized viewport', () => {
    const { direction } = orbitCamera().screenToWorldRay(0, 0, 0, 0);
    expect(direction.every(Number.isFinite)).toBe(true);
  });

  it('rayFromNDC returns null for a singular matrix input', () => {
    expect(rayFromNDC(new Float32Array(16), 0, 0)).toBeNull();
  });
});
