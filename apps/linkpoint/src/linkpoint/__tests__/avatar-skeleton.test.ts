import { describe, expect, it } from 'vitest';
import { AvatarSkeleton, compose, hasJointOverrides, identity, jointPositionOverrides, multiply, skinMatrices, skinPoint } from '../avatar-skeleton';
import type { JointPose } from '../avatar-animation';

const skeleton = new AvatarSkeleton();
const close = (a: ArrayLike<number>, b: number[], digits = 4) => b.forEach((v, i) => expect(a[i]).toBeCloseTo(v, digits));

describe('avatar skeleton', () => {
  it('contains the full Lumiya hierarchy rooted at the pelvis, parents before children', () => {
    expect(skeleton.bones).toHaveLength(159);
    expect(skeleton.bones[0].name).toBe('mPelvis');
    for (const bone of skeleton.bones) expect(bone.parentIndex).toBeLessThan(bone.index);
    expect(skeleton.bones.filter((b) => b.joint && !skeleton.bones[b.index].name.startsWith('mFace')).length).toBeGreaterThan(50);
  });

  it('resolves legacy animation names to joints', () => {
    expect(skeleton.resolve('hip')).toBe('mPelvis');
    expect(skeleton.resolve('lShldr')).toBe('mShoulderLeft');
    expect(skeleton.resolve('avatar_mHead')).toBe('mHead');
    expect(skeleton.resolve('WISDOM_SWORD_1')).toBeNull();
  });

  it('puts the head above the pelvis at rest', () => {
    const world = skeleton.worldMatrices();
    const head = world[skeleton.indexOf('mHead')];
    const pelvis = world[0];
    expect(head[14]).toBeGreaterThan(pelvis[14] + 0.5);
    close(pelvis, [1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 1.067, 1], 2);
  });

  it('rotates children with their parent, and replaces joint positions other than the pelvis', () => {
    const rest = skeleton.worldMatrices();
    const turn: [number, number, number, number] = [0, 0, Math.SQRT1_2, Math.SQRT1_2]; // 90° about Z
    const posed = skeleton.worldMatrices(new Map<string, JointPose>([['mTorso', { rotation: turn, position: null }]]));
    const head = skeleton.indexOf('mHead');
    // a 90° turn about Z at the torso moves the head's offset (dx, dy) to (-dy, dx) and keeps its height
    const dx = rest[head][12] - rest[skeleton.indexOf('mTorso')][12], dy = rest[head][13] - rest[skeleton.indexOf('mTorso')][13];
    expect(posed[head][12] - rest[skeleton.indexOf('mTorso')][12]).toBeCloseTo(-dy, 4);
    expect(posed[head][13] - rest[skeleton.indexOf('mTorso')][13]).toBeCloseTo(dx, 4);
    expect(posed[head][14]).toBeCloseTo(rest[head][14], 4);
    // an arm hangs off the chest: it swings 90° about Z
    const hand = skeleton.indexOf('mWristLeft');
    const restHand = [rest[hand][12] - rest[0][12], rest[hand][13] - rest[0][13]];
    expect(posed[hand][12]).toBeCloseTo(-restHand[1], 3);
    expect(posed[hand][13]).toBeCloseTo(restHand[0], 3);
    const moved = skeleton.worldMatrices(new Map<string, JointPose>([['hip', { rotation: [0, 0, 0, 1], position: [0, 0, 0.5] }]]));
    expect(moved[0][14]).toBeCloseTo(rest[0][14] + 0.5, 4);
    const replaced = skeleton.worldMatrices(new Map<string, JointPose>([['mTorso', { rotation: [0, 0, 0, 1], position: [0, 0, 1] }]]));
    expect(replaced[skeleton.indexOf('mTorso')][14]).toBeCloseTo(rest[0][14] + 1, 4);
  });

  it('composes and multiplies column-major matrices', () => {
    const t = compose([1, 2, 3]);
    close(multiply(t, identity()), Array.from(t));
    close(multiply(compose([1, 0, 0]), compose([0, 2, 0])), [1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 1, 2, 0, 1]);
  });

  it('skins in bind pose to the original vertex and follows a posed joint', () => {
    const jointName = 'mWristLeft';
    const world = skeleton.worldMatrices();
    const at = world[skeleton.indexOf(jointName)];
    // inverse bind = inverse of the joint's rest transform (pure translation here), stored row-major
    const inverseBind = [1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, -at[12], -at[13], -at[14], 1];
    const skin = { jointNames: [jointName], bindShapeMatrix: null, inverseBindMatrices: [inverseBind] };
    const vertex: [number, number, number] = [at[12] + 0.1, at[13], at[14]];
    close(skinPoint(vertex, [0, 0, 0, 0], [1, 0, 0, 0], skinMatrices(skeleton, skin, world)), vertex);
    const turn: [number, number, number, number] = [0, 0, Math.SQRT1_2, Math.SQRT1_2];
    const posed = skeleton.worldMatrices(new Map<string, JointPose>([[jointName, { rotation: turn, position: null }]]));
    const out = skinPoint(vertex, [0, 0, 0, 0], [1, 0, 0, 0], skinMatrices(skeleton, skin, posed));
    // 0.1 m along +X from the wrist becomes 0.1 m along +Y after a 90° turn about Z
    close(out, [at[12], at[13] + 0.1, at[14]], 3);
  });

  it('applies Bento-style joint position overrides and the pelvis offset', () => {
    const rest = skeleton.worldMatrices();
    const skin = {
      jointNames: ['mNeck'], bindShapeMatrix: null, inverseBindMatrices: [identity()],
      alternateInverseBindMatrices: [[1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0.5, 1]],
    };
    const overrides = jointPositionOverrides(skeleton, skin);
    expect(overrides.get('mNeck')).toEqual([0, 0, 0.5]);
    const moved = skeleton.worldMatrices(new Map(), overrides, [0, 0, 0.2]);
    const neck = skeleton.indexOf('mNeck'), chest = skeleton.indexOf('mChest');
    expect(moved[neck][14] - moved[chest][14]).toBeCloseTo(0.5, 4);
    // the head follows its (moved) parent, and the whole body is raised by the pelvis offset
    expect(moved[skeleton.indexOf('mHead')][14] - rest[skeleton.indexOf('mHead')][14]).toBeGreaterThan(0.2);
    expect(moved[0][14]).toBeCloseTo(rest[0][14] + 0.2, 4);
  });

  it('follows the viewer\'s override rules: counts must match, defaults and unknown joints are ignored', () => {
    const at = (pos: number[]) => [1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, pos[0], pos[1], pos[2], 1];
    const headRest = skeleton.bones[skeleton.indexOf('mHead')].rest;
    // a position equal to the joint default is not an override (within 0.1 mm)
    const same = { jointNames: ['mHead'], bindShapeMatrix: null, inverseBindMatrices: [], alternateInverseBindMatrices: [at([headRest[0] + 0.00005, headRest[1], headRest[2]])] };
    expect(jointPositionOverrides(skeleton, same).size).toBe(0);
    // a further move is
    const moved = { ...same, alternateInverseBindMatrices: [at([headRest[0], headRest[1], headRest[2] + 0.01])] };
    expect(jointPositionOverrides(skeleton, moved).get('mHead')).toEqual([headRest[0], headRest[1], headRest[2] + 0.01]);
    // unknown joint names are ignored
    expect(jointPositionOverrides(skeleton, { ...moved, jointNames: ['nope'] }).size).toBe(0);
    // mismatched counts disable all overrides
    expect(jointPositionOverrides(skeleton, { ...moved, jointNames: ['mHead', 'mNeck'] }).size).toBe(0);
    expect(hasJointOverrides({ ...moved, jointNames: ['mHead', 'mNeck'] })).toBe(false);
    expect(hasJointOverrides(moved)).toBe(true);
  });

  it('skins joints it does not know as mPelvis, like the viewer', () => {
    const world = skeleton.worldMatrices();
    const unknown = skinMatrices(skeleton, { jointNames: ['nope'], bindShapeMatrix: null, inverseBindMatrices: [identity()] }, world)[0];
    const pelvis = skinMatrices(skeleton, { jointNames: ['mPelvis'], bindShapeMatrix: null, inverseBindMatrices: [identity()] }, world)[0];
    expect(Array.from(unknown)).toEqual(Array.from(pelvis));
  });

  it('leaves vertices alone when no joint influences them', () => {
    expect(skinPoint([1, 2, 3], [0, 0, 0, 0], [0, 0, 0, 0], [identity()])).toEqual([1, 2, 3]);
  });
});
