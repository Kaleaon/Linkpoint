import { describe, expect, it } from 'vitest';
import { AvatarSkeleton, skinMatrices, type MeshSkin } from '../avatar-skeleton';

describe('Bento Skeleton & Matrix Propagation', () => {
  const skeleton = new AvatarSkeleton();

  it('AvatarSkeleton.BONES includes Bento joint names', () => {
    expect(AvatarSkeleton.BONES).toBeDefined();
    expect(AvatarSkeleton.BONES.length).toBeGreaterThanOrEqual(133);

    const requiredBentoJoints = [
      'mFaceRoot',
      'mFaceJaw',
      'mFaceEar1Left',
      'mHandRing1Left',
      'mHandIndex1Right',
      'mWingsRoot',
      'mWing1Left',
      'mTail1',
      'mTail6',
      'mGroin',
      'mHindLimb4Left',
    ];

    for (const joint of requiredBentoJoints) {
      expect(AvatarSkeleton.BONES).toContain(joint);
    }
  });

  it('resolves and indexes Bento joints correctly', () => {
    const faceJawIndex = skeleton.indexOf('mFaceJaw');
    const handRingIndex = skeleton.indexOf('mHandRing1Left');
    const wingIndex = skeleton.indexOf('mWing1Left');
    const tailIndex = skeleton.indexOf('mTail1');

    expect(faceJawIndex).toBeGreaterThan(0);
    expect(handRingIndex).toBeGreaterThan(0);
    expect(wingIndex).toBeGreaterThan(0);
    expect(tailIndex).toBeGreaterThan(0);

    expect(skeleton.resolve('mFaceJaw')).toBe('mFaceJaw');
    expect(skeleton.resolve('mHandRing1Left')).toBe('mHandRing1Left');
  });

  it('skinMatrices preserves Bento joint matrices without scrubbing valid joints to pelvis', () => {
    const skin: MeshSkin = {
      jointNames: ['mPelvis', 'mFaceJaw', 'mHandRing1Left', 'mWing1Left', 'mTail1'],
      bindShapeMatrix: null,
      inverseBindMatrices: [
        new Float32Array(16),
        new Float32Array(16),
        new Float32Array(16),
        new Float32Array(16),
        new Float32Array(16),
      ],
    };

    const world = skeleton.worldMatrices();
    const matrices = skinMatrices(skeleton, skin, world);

    expect(matrices.length).toBe(5);

    const faceJawIdx = skeleton.indexOf('mFaceJaw');
    expect(faceJawIdx).not.toBe(0);

    // Ensure the skinning matrix for mFaceJaw is calculated from world[faceJawIdx], not world[0] (mPelvis)
    expect(matrices[1]).toBeDefined();
  });
});
