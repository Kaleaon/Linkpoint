import { describe, expect, it } from 'vitest';
import { AvatarSkeleton } from '../avatar-skeleton';
import { AvatarParamsManager, VisualParamId } from '../phase2/avatar-params';
import { createProceduralHumanoidParts } from '../avatar-body';

describe('Avatar visual parameters and morphing', () => {
  it('manages visual parameters with clamping and default fallback', () => {
    const mgr = new AvatarParamsManager();
    expect(mgr.getParam(VisualParamId.HEIGHT)).toBe(0.5);

    mgr.setParam(VisualParamId.HEIGHT, 0.8);
    expect(mgr.getParam(VisualParamId.HEIGHT)).toBe(0.8);

    mgr.setParam('height', 1.5); // Clamped to 1.0
    expect(mgr.getParam(VisualParamId.HEIGHT)).toBe(1.0);

    mgr.setParam('height', -0.5); // Clamped to 0.0
    expect(mgr.getParam(VisualParamId.HEIGHT)).toBe(0.0);
  });

  it('translates visual parameters into bone scale and position offset vectors', () => {
    const mgr = new AvatarParamsManager();
    mgr.setParam(VisualParamId.HEIGHT, 1.0);
    mgr.setParam(VisualParamId.SHOULDER_WIDTH, 1.0);
    mgr.setParam(VisualParamId.HEAD_SIZE, 0.0);

    const skeleton = new AvatarSkeleton();
    const { scaleOverrides, offsetOverrides } = mgr.computeBoneTransforms(skeleton);

    expect(scaleOverrides.has('mPelvis')).toBe(true);
    expect(scaleOverrides.get('mPelvis')![0]).toBeGreaterThan(1.0);

    expect(scaleOverrides.has('mHead')).toBe(true);
    expect(scaleOverrides.get('mHead')![0]).toBeLessThan(1.0);

    expect(offsetOverrides.has('mShoulderLeft')).toBe(true);
  });

  it('applies bone scale and offset overrides into skeleton world matrices', () => {
    const skeleton = new AvatarSkeleton();
    const restWorld = skeleton.worldMatrices();

    const scaleOverrides = new Map<string, [number, number, number]>([['mHead', [2, 2, 2]]]);
    const offsetOverrides = new Map<string, [number, number, number]>([['mHead', [0, 0, 0.5]]]);

    const modifiedWorld = skeleton.worldMatrices(new Map(), new Map(), [0, 0, 0], scaleOverrides, offsetOverrides);

    const headIdx = skeleton.indexOf('mHead');
    expect(modifiedWorld[headIdx][14]).toBeGreaterThan(restWorld[headIdx][14]);
    expect(modifiedWorld[headIdx][0]).toBeCloseTo(2, 4); // X scale in column-major [0]
  });

  it('generates low-poly procedural humanoid body parts for fallback rendering', () => {
    const skeleton = new AvatarSkeleton();
    const parts = createProceduralHumanoidParts(skeleton);

    expect(parts.has('upperBody')).toBe(true);
    expect(parts.has('lowerBody')).toBe(true);
    expect(parts.has('head')).toBe(true);
    expect(parts.has('eyelashes')).toBe(true);
    expect(parts.has('eye')).toBe(true);
    expect(parts.has('hair')).toBe(true);

    const upper = parts.get('upperBody')!;
    expect(upper.vertices.length).toBeGreaterThan(0);
    expect(upper.joints.length).toBe(upper.vertices.length / 3 * 4);
    expect(upper.jointWeights.length).toBe(upper.vertices.length / 3 * 4);
    expect(upper.jointNames).toContain('mChest');
  });
});
