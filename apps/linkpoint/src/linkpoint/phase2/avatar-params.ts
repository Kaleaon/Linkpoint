/**
 * Visual parameter morphing definitions and bone scale/offset calculations.
 * Converts visual parameter sliders into bone scale and position offset vectors in AvatarSkeleton.
 */
import type { Vec3 } from '../avatar-animation';
import type { AvatarSkeleton } from '../avatar-skeleton';

export enum VisualParamId {
  BODY_FAT = 11,
  TORSO_LENGTH = 32,
  HEIGHT = 33,
  HEAD_SIZE = 682,
  BREAST_SIZE = 683,
  LEG_LENGTH = 692,
  ARM_LENGTH = 693,
  SHOULDER_WIDTH = 750,
  HIP_WIDTH = 756,
}

export const PARAM_NAMES: Record<string, number> = {
  bodyFat: VisualParamId.BODY_FAT,
  torsoLength: VisualParamId.TORSO_LENGTH,
  height: VisualParamId.HEIGHT,
  headSize: VisualParamId.HEAD_SIZE,
  breastSize: VisualParamId.BREAST_SIZE,
  legLength: VisualParamId.LEG_LENGTH,
  armLength: VisualParamId.ARM_LENGTH,
  shoulderWidth: VisualParamId.SHOULDER_WIDTH,
  hipWidth: VisualParamId.HIP_WIDTH,
};

export class AvatarParamsManager {
  private params = new Map<number | string, number>();

  constructor(initialParams?: Record<number | string, number>) {
    if (initialParams) {
      for (const [key, value] of Object.entries(initialParams)) {
        const id = typeof key === 'string' && PARAM_NAMES[key] ? PARAM_NAMES[key] : isNaN(Number(key)) ? key : Number(key);
        this.setParam(id, value);
      }
    }
  }

  setParam(param: number | string, value: number): void {
    const clamped = Math.max(0, Math.min(1, Number.isFinite(value) ? value : 0.5));
    const paramId = typeof param === 'string' && PARAM_NAMES[param] !== undefined ? PARAM_NAMES[param] : param;
    this.params.set(paramId, clamped);
  }

  getParam(param: number | string): number {
    const paramId = typeof param === 'string' && PARAM_NAMES[param] !== undefined ? PARAM_NAMES[param] : param;
    return this.params.get(paramId) ?? 0.5;
  }

  getAllParams(): Map<number | string, number> {
    return new Map(this.params);
  }

  reset(): void {
    this.params.clear();
  }

  /**
   * Computes bone scale and position offset vectors for AvatarSkeleton based on active visual parameters.
   */
  computeBoneTransforms(_skeleton?: AvatarSkeleton): {
    scaleOverrides: Map<string, Vec3>;
    offsetOverrides: Map<string, Vec3>;
  } {
    const scaleOverrides = new Map<string, Vec3>();
    const offsetOverrides = new Map<string, Vec3>();

    const getScaleFactor = (paramId: number | string, minScale = 0.7, maxScale = 1.3): number => {
      const val = this.getParam(paramId);
      return minScale + (maxScale - minScale) * val;
    };

    // 1. HEIGHT (33): Scales overall height across spine and leg joints
    const heightScale = getScaleFactor(VisualParamId.HEIGHT, 0.8, 1.2);
    if (Math.abs(heightScale - 1.0) > 0.001) {
      const heightBones = ['mPelvis', 'mTorso', 'mChest', 'mHipLeft', 'mHipRight', 'mKneeLeft', 'mKneeRight'];
      for (const bone of heightBones) {
        scaleOverrides.set(bone, [heightScale, heightScale, heightScale]);
      }
    }

    // 2. TORSO_LENGTH (32): Scale Z axis for torso/chest and offset neck/head/shoulders
    const torsoScale = getScaleFactor(VisualParamId.TORSO_LENGTH, 0.7, 1.3);
    if (Math.abs(torsoScale - 1.0) > 0.001) {
      for (const bone of ['mTorso', 'mChest']) {
        const current = scaleOverrides.get(bone) || [1, 1, 1];
        scaleOverrides.set(bone, [current[0], current[1], current[2] * torsoScale]);
      }
      const zOffset = (torsoScale - 1.0) * 0.15;
      offsetOverrides.set('mChest', [0, 0, zOffset * 0.5]);
      offsetOverrides.set('mNeck', [0, 0, zOffset]);
      offsetOverrides.set('mHead', [0, 0, zOffset * 1.2]);
      offsetOverrides.set('mCollarLeft', [0, 0, zOffset]);
      offsetOverrides.set('mCollarRight', [0, 0, zOffset]);
    }

    // 3. LEG_LENGTH (692): Scale Z axis for hips, knees, ankles
    const legScale = getScaleFactor(VisualParamId.LEG_LENGTH, 0.7, 1.3);
    if (Math.abs(legScale - 1.0) > 0.001) {
      for (const bone of ['mHipLeft', 'mHipRight', 'mKneeLeft', 'mKneeRight']) {
        const current = scaleOverrides.get(bone) || [1, 1, 1];
        scaleOverrides.set(bone, [current[0], current[1], current[2] * legScale]);
      }
      const legOffset = (legScale - 1.0) * 0.15;
      offsetOverrides.set('mKneeLeft', [0, 0, -legOffset]);
      offsetOverrides.set('mKneeRight', [0, 0, -legOffset]);
      offsetOverrides.set('mAnkleLeft', [0, 0, -legOffset * 2.0]);
      offsetOverrides.set('mAnkleRight', [0, 0, -legOffset * 2.0]);
    }

    // 4. SHOULDER_WIDTH (750): Scale X axis for collars/shoulders
    const shoulderScale = getScaleFactor(VisualParamId.SHOULDER_WIDTH, 0.7, 1.3);
    if (Math.abs(shoulderScale - 1.0) > 0.001) {
      for (const bone of ['mCollarLeft', 'mCollarRight']) {
        const current = scaleOverrides.get(bone) || [1, 1, 1];
        scaleOverrides.set(bone, [current[0] * shoulderScale, current[1], current[2]]);
      }
      const shoulderXOffset = (shoulderScale - 1.0) * 0.1;
      offsetOverrides.set('mShoulderLeft', [shoulderXOffset, 0, 0]);
      offsetOverrides.set('mShoulderRight', [-shoulderXOffset, 0, 0]);
    }

    // 5. HIP_WIDTH (756): Scale X/Y axis for pelvis and hips
    const hipScale = getScaleFactor(VisualParamId.HIP_WIDTH, 0.7, 1.3);
    if (Math.abs(hipScale - 1.0) > 0.001) {
      for (const bone of ['mPelvis', 'mHipLeft', 'mHipRight']) {
        const current = scaleOverrides.get(bone) || [1, 1, 1];
        scaleOverrides.set(bone, [current[0] * hipScale, current[1] * hipScale, current[2]]);
      }
    }

    // 6. HEAD_SIZE (682): Scale X/Y/Z for head and neck
    const headScale = getScaleFactor(VisualParamId.HEAD_SIZE, 0.7, 1.3);
    if (Math.abs(headScale - 1.0) > 0.001) {
      for (const bone of ['mHead', 'mNeck']) {
        const current = scaleOverrides.get(bone) || [1, 1, 1];
        scaleOverrides.set(bone, [current[0] * headScale, current[1] * headScale, current[2] * headScale]);
      }
    }

    // 7. BREAST_SIZE (683): Scale Y/Z for chest
    const breastScale = getScaleFactor(VisualParamId.BREAST_SIZE, 0.7, 1.3);
    if (Math.abs(breastScale - 1.0) > 0.001) {
      const current = scaleOverrides.get('mChest') || [1, 1, 1];
      scaleOverrides.set('mChest', [current[0], current[1] * breastScale, current[2] * breastScale]);
    }

    // 8. ARM_LENGTH (693): Scale X/Z for arm joints
    const armScale = getScaleFactor(VisualParamId.ARM_LENGTH, 0.7, 1.3);
    if (Math.abs(armScale - 1.0) > 0.001) {
      for (const bone of ['mShoulderLeft', 'mShoulderRight', 'mElbowLeft', 'mElbowRight']) {
        const current = scaleOverrides.get(bone) || [1, 1, 1];
        scaleOverrides.set(bone, [current[0] * armScale, current[1], current[2] * armScale]);
      }
    }

    // 9. BODY_FAT (11): Scale X/Y across torso, chest, hips
    const fatScale = getScaleFactor(VisualParamId.BODY_FAT, 0.8, 1.2);
    if (Math.abs(fatScale - 1.0) > 0.001) {
      for (const bone of ['mPelvis', 'mTorso', 'mChest', 'mHipLeft', 'mHipRight']) {
        const current = scaleOverrides.get(bone) || [1, 1, 1];
        scaleOverrides.set(bone, [current[0] * fatScale, current[1] * fatScale, current[2]]);
      }
    }

    return { scaleOverrides, offsetOverrides };
  }
}
