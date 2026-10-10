import { describe, expect, it } from 'vitest';
import { clampJointIndices, maxSkinJoints, packJointRows, skinnedVertexShader, SL_MAX_RIGGED_JOINTS } from '../skinning';
import { compose } from '../avatar-skeleton';

describe('GPU skinning helpers', () => {
  it('sizes the joint array from the GPU uniform budget and caps it at the SL maximum', () => {
    expect(maxSkinJoints(4096)).toBe(SL_MAX_RIGGED_JOINTS);
    expect(maxSkinJoints(254)).toBe(Math.floor((254 - 24) / 3));
    expect(maxSkinJoints(10)).toBe(1);
  });

  it('declares one shader row triple per joint', () => {
    const shader = skinnedVertexShader(7);
    expect(shader).toContain('uniform vec4 uJointRows[21];');
    expect(shader).toContain('attribute vec4 aJoints;');
    expect(shader).toContain('attribute vec4 aWeights;');
  });

  it('packs column-major matrices into three row vectors per joint', () => {
    const m = compose([1, 2, 3], [0, 0, Math.SQRT1_2, Math.SQRT1_2]); // 90° about Z, then translate
    const rows = packJointRows([m], 2);
    expect(rows).toHaveLength(24);
    // row 0 = (m00, m01, m02, tx): rotating +X to +Y means row0 = (0, -1, 0, 1)
    expect(Array.from(rows.slice(0, 4)).map((v) => Math.round(v * 1e4) / 1e4 + 0)).toEqual([0, -1, 0, 1]);
    expect(Array.from(rows.slice(4, 8)).map((v) => Math.round(v * 1e4) / 1e4 + 0)).toEqual([1, 0, 0, 2]);
    expect(Array.from(rows.slice(8, 12)).map((v) => Math.round(v * 1e4) / 1e4 + 0)).toEqual([0, 0, 1, 3]);
  });

  it('fills unused joint slots with identity and ignores malformed matrices', () => {
    const rows = packJointRows([new Float32Array(3) as unknown as number[]], 2);
    expect(Array.from(rows.slice(0, 12))).toEqual([1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0]);
    expect(Array.from(rows.slice(12, 24))).toEqual([1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0]);
  });

  it('drops joints beyond the uniform budget', () => {
    expect(packJointRows(Array.from({ length: 5 }, () => compose([9, 9, 9])), 2)).toHaveLength(24);
  });

  it('clamps out-of-range or non-integer joint indices to joint 0', () => {
    expect(clampJointIndices([0, 3, 4, -1, 1.5, NaN], 4)).toEqual([0, 3, 0, 0, 0, 0]);
  });

  it('writes into provided target buffer when capacity is sufficient', () => {
    const target = new Float32Array(24);
    target.fill(99);
    const m = compose([1, 2, 3]);
    const res = packJointRows([m], 2, target);
    expect(res).toBe(target);
    expect(res[0]).not.toBe(99);
  });

  it('handles identity fallbacks on reused buffers by overwriting lingering values', () => {
    const target = new Float32Array(24);
    target.fill(77);
    const rows = packJointRows([], 2, target);
    expect(rows).toBe(target);
    expect(Array.from(rows.slice(0, 12))).toEqual([1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0]);
    expect(Array.from(rows.slice(12, 24))).toEqual([1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0]);
  });

  it('allocates a new buffer when target parameter is omitted or undersized', () => {
    const undersized = new Float32Array(10);
    const res1 = packJointRows([], 2, undersized);
    expect(res1).not.toBe(undersized);
    expect(res1).toHaveLength(24);

    const res2 = packJointRows([], 2);
    expect(res2).toHaveLength(24);
  });
});
