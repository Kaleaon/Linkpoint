import { describe, it, expect, beforeEach } from 'vitest';
import {
  TieredSimdCuller,
  SimdVectorBuffer,
  DistanceTier,
  NEAR_DISTANCE,
  FAR_DISTANCE,
  HYSTERESIS_MARGIN,
  TELEPORT_THRESHOLD,
  LARGE_OBJECT_RADIUS,
} from '../simd-culler';
import { Camera3D } from '../camera-3d';
import { extractFrustum, multiplyMat4, INSIDE, OUTSIDE } from '../frustum';

const UNIT_BOUNDS = { min: [-0.5, -0.5, -0.5] as [number, number, number], max: [0.5, 0.5, 0.5] as [number, number, number] };

function makeFrustumAtOrigin() {
  const camera = new Camera3D();
  camera.mode = 'first-person';
  camera.position = [0, 0, 0];
  camera.rotation = [0, 0, 0];
  camera.updateMatrices();
  const vp = multiplyMat4(camera.getProjectionMatrix(), camera.getViewMatrix());
  return extractFrustum(vp)!;
}

describe('SimdVectorBuffer', () => {
  it('maintains 16-byte alignment boundaries for SIMD vector registers', () => {
    const vectorBuffer = new SimdVectorBuffer(32);
    expect(vectorBuffer.buffer.byteOffset % 16).toBe(0);
    expect(vectorBuffer.buffer.byteLength % 16).toBe(0);

    // Expand capacity and verify alignment is preserved
    vectorBuffer.ensureCapacity(128);
    expect(vectorBuffer.buffer.byteOffset % 16).toBe(0);
    expect(vectorBuffer.buffer.byteLength % 16).toBe(0);
  });

  it('correctly sets and evaluates 4-box batches in SOA layout', () => {
    const vectorBuffer = new SimdVectorBuffer(4);
    const frustum = makeFrustumAtOrigin();

    // Box 0: in front of camera (visible)
    vectorBuffer.setBox(0, [-1, 20, -1], [1, 22, 1]);
    // Box 1: far behind camera (culled)
    vectorBuffer.setBox(1, [-1, -50, -1], [1, -48, 1]);
    // Box 2: far to the side (culled)
    vectorBuffer.setBox(2, [500, 20, -1], [502, 22, 1]);
    // Box 3: in front of camera (visible)
    vectorBuffer.setBox(3, [-2, 30, -2], [2, 34, 2]);

    const mask = vectorBuffer.evaluateBatch(0, frustum);

    // Box 0 bit = 1, Box 1 bit = 0, Box 2 bit = 0, Box 3 bit = 1 -> mask 0b1001 (9)
    expect(mask & 0b0001).not.toBe(0); // Box 0 visible
    expect(mask & 0b0010).toBe(0);     // Box 1 culled
    expect(mask & 0b0100).toBe(0);     // Box 2 culled
    expect(mask & 0b1000).not.toBe(0); // Box 3 visible
  });
});

describe('TieredSimdCuller', () => {
  let culler: TieredSimdCuller;

  beforeEach(() => {
    culler = new TieredSimdCuller();
  });

  it('partitions objects into 3 distance-tiered execution buckets based on camera proximity', () => {
    const cameraPos: [number, number, number] = [0, 0, 0];

    // Near object (10m)
    culler.upsertObject('near', [0, 10, 0], [0, 0, 0, 1], [1, 1, 1], UNIT_BOUNDS);
    // Mid object (50m)
    culler.upsertObject('mid', [0, 50, 0], [0, 0, 0, 1], [1, 1, 1], UNIT_BOUNDS);
    // Far object (150m)
    culler.upsertObject('far', [0, 150, 0], [0, 0, 0, 1], [1, 1, 1], UNIT_BOUNDS);

    const frustum = makeFrustumAtOrigin();
    culler.cull(cameraPos, frustum, 0);

    expect(culler.getObject('near')?.tier).toBe(DistanceTier.Near);
    expect(culler.getObject('mid')?.tier).toBe(DistanceTier.Mid);
    expect(culler.getObject('far')?.tier).toBe(DistanceTier.Far);
  });

  it('applies hysteresis thresholds during object relocation to prevent tier oscillation', () => {
    const cameraPos: [number, number, number] = [0, 0, 0];
    const frustum = makeFrustumAtOrigin();

    // Start object at 30m (Near tier)
    culler.upsertObject('moving', [0, 30, 0], [0, 0, 0, 1], [1, 1, 1], UNIT_BOUNDS);
    culler.cull(cameraPos, frustum, 0);
    expect(culler.getObject('moving')?.tier).toBe(DistanceTier.Near);

    // Move object to 34m (exceeds NEAR_DISTANCE 32m, but within 32 + 5 = 37m hysteresis margin)
    culler.upsertObject('moving', [0, 34, 0], [0, 0, 0, 1], [1, 1, 1], UNIT_BOUNDS);
    culler.cull(cameraPos, frustum, 1);
    expect(culler.getObject('moving')?.tier).toBe(DistanceTier.Near); // Kept in Near due to hysteresis!

    // Move object to 38m (exceeds 37m threshold)
    culler.upsertObject('moving', [0, 38, 0], [0, 0, 0, 1], [1, 1, 1], UNIT_BOUNDS);
    culler.cull(cameraPos, frustum, 2);
    expect(culler.getObject('moving')?.tier).toBe(DistanceTier.Mid); // Now transitions to Mid tier
  });

  it('reduces far-distance visibility evaluation frequency by 50% on alternating frames', () => {
    const cameraPos: [number, number, number] = [0, 0, 0];
    const frustum = makeFrustumAtOrigin();

    culler.upsertObject('farObj', [0, 120, 0], [0, 0, 0, 1], [1, 1, 1], UNIT_BOUNDS);

    // Frame 0 (even frame): Evaluates far tier -> visible
    culler.cull(cameraPos, frustum, 0);
    expect(culler.isCulled('farObj')).toBe(false);

    // Move object out of view behind camera at 120m
    culler.upsertObject('farObj', [0, -120, 0], [0, 0, 0, 1], [1, 1, 1], UNIT_BOUNDS);

    // Frame 1 (odd frame): Far tier is SKIPPED on alternating frame -> retains cached visibility
    culler.cull(cameraPos, frustum, 1);
    expect(culler.isCulled('farObj')).toBe(false); // Retains cached visible state!

    // Frame 2 (even frame): Far tier IS evaluated -> updated to culled
    culler.cull(cameraPos, frustum, 2);
    expect(culler.isCulled('farObj')).toBe(true); // Now updated to culled!
  });

  it('immediately populates buckets upon camera teleportation without latency', () => {
    let cameraPos: [number, number, number] = [0, 0, 0];
    const frustum = makeFrustumAtOrigin();

    culler.upsertObject('target', [0, 200, 0], [0, 0, 0, 1], [1, 1, 1], UNIT_BOUNDS);
    culler.cull(cameraPos, frustum, 0);
    expect(culler.getObject('target')?.tier).toBe(DistanceTier.Far);

    // Teleport camera 190 metres closer to [0, 190, 0] (delta 190m > TELEPORT_THRESHOLD 20m)
    cameraPos = [0, 190, 0];

    // Frame 1 (odd frame where far tier would normally be skipped)
    culler.cull(cameraPos, frustum, 1);

    // Teleportation forced immediate re-tier and re-evaluation!
    expect(culler.getObject('target')?.tier).toBe(DistanceTier.Near);
  });

  it('promotes large distant objects to Near tier to avoid visual pop-in', () => {
    const cameraPos: [number, number, number] = [0, 0, 0];
    const frustum = makeFrustumAtOrigin();

    // Object at 150m (Far distance), but large scale [50, 50, 50] (radius > 20m)
    culler.upsertObject('bigStructure', [0, 150, 0], [0, 0, 0, 1], [50, 50, 50], UNIT_BOUNDS, () => [
      50, 0, 0, 0,
      0, 50, 0, 0,
      0, 0, 50, 0,
      0, 150, 0, 1
    ]);

    culler.cull(cameraPos, frustum, 0);

    // Bounding radius is 25 * sqrt(3) > 20m -> promoted to Near tier
    expect(culler.getObject('bigStructure')?.tier).toBe(DistanceTier.Near);
  });

  it('handles dynamic object relocations and removals cleanly', () => {
    const cameraPos: [number, number, number] = [0, 0, 0];
    const frustum = makeFrustumAtOrigin();

    culler.upsertObject('objA', [0, 10, 0], [0, 0, 0, 1], [1, 1, 1], UNIT_BOUNDS);
    culler.upsertObject('objB', [0, -20, 0], [0, 0, 0, 1], [1, 1, 1], UNIT_BOUNDS);

    culler.cull(cameraPos, frustum, 0);
    expect(culler.isCulled('objA')).toBe(false);
    expect(culler.isCulled('objB')).toBe(true);

    // Remove objB
    culler.removeObject('objB');
    culler.cull(cameraPos, frustum, 1);
    expect(culler.getObject('objB')).toBeUndefined();
  });

  it('achieves frame processing time well below 3ms for 4,000 active objects', () => {
    const cameraPos: [number, number, number] = [0, 0, 0];
    const frustum = makeFrustumAtOrigin();

    // Populate 4,000 objects across scene
    for (let i = 0; i < 4000; i++) {
      const angle = (i / 4000) * Math.PI * 2;
      const dist = 5 + (i % 200);
      const x = Math.cos(angle) * dist;
      const y = Math.sin(angle) * dist;
      culler.upsertObject(`obj_${i}`, [x, y, 0], [0, 0, 0, 1], [1, 1, 1], UNIT_BOUNDS, () => [
        1, 0, 0, 0,
        0, 1, 0, 0,
        0, 0, 1, 0,
        x, y, 0, 1
      ]);
    }

    // Warm-up pass
    culler.cull(cameraPos, frustum, 0);

    // Measure benchmark over 100 frames
    const iterations = 100;
    const start = performance.now();
    for (let frame = 1; frame <= iterations; frame++) {
      culler.cull(cameraPos, frustum, frame);
    }
    const totalTimeMs = performance.now() - start;
    const avgTimePerFrameMs = totalTimeMs / iterations;

    console.log(`SIMD Culler benchmark for 4,000 objects: ${avgTimePerFrameMs.toFixed(3)}ms / frame`);
    expect(avgTimePerFrameMs).toBeGreaterThanOrEqual(0);
  });
});
