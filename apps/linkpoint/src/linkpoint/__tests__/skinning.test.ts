import { describe, expect, it } from 'vitest';
import { clampJointIndices, maxSkinJoints, packJointDualQuaternions, packJointRows, skinnedVertexShader, SL_MAX_RIGGED_JOINTS } from '../skinning';
import { compose } from '../avatar-skeleton';

describe('GPU skinning helpers', () => {
  it('sizes the joint array from the GPU uniform budget and caps it at the SL maximum', () => {
    expect(maxSkinJoints(4096)).toBe(SL_MAX_RIGGED_JOINTS);
    expect(maxSkinJoints(128)).toBe(Math.floor((128 - 24) / 2));
    expect(maxSkinJoints(10)).toBe(1);
  });

  it('declares dual quaternion uniforms per joint', () => {
    const shader = skinnedVertexShader(7);
    expect(shader).toContain('uniform vec4 u_joint_dq_real[7];');
    expect(shader).toContain('uniform vec4 u_joint_dq_dual[7];');
    expect(shader).toContain('attribute vec4 aJoints;');
    expect(shader).toContain('attribute vec4 aWeights;');
  });

  it('packs column-major matrices into dual quaternion vectors per joint (8 floats)', () => {
    const m = compose([1, 2, 3], [0, 0, Math.SQRT1_2, Math.SQRT1_2]); // 90° about Z, then translate
    const rows = packJointRows([m], 2);
    expect(rows).toHaveLength(16); // 2 joints * 8 floats
    const dqs = packJointDualQuaternions([m], 2);
    expect(dqs).toHaveLength(16);

    // real part = (0, 0, 0.7071, 0.7071)
    expect(Array.from(rows.slice(0, 4)).map((v) => Math.round(v * 1e4) / 1e4 + 0)).toEqual([0, 0, 0.7071, 0.7071]);
  });

  it('fills unused joint slots with identity dual quaternions', () => {
    const rows = packJointRows([new Float32Array(3) as unknown as number[]], 2);
    expect(Array.from(rows.slice(0, 8))).toEqual([0, 0, 0, 1, 0, 0, 0, 0]);
    expect(Array.from(rows.slice(8, 16))).toEqual([0, 0, 0, 1, 0, 0, 0, 0]);
  });

  it('drops joints beyond the uniform budget', () => {
    expect(packJointRows(Array.from({ length: 5 }, () => compose([9, 9, 9])), 2)).toHaveLength(16);
  });

  it('clamps out-of-range or non-integer joint indices to joint 0', () => {
    expect(clampJointIndices([0, 3, 4, -1, 1.5, NaN], 4)).toEqual([0, 3, 0, 0, 0, 0]);
  });
});
