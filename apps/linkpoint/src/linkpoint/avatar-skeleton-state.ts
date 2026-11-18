/**
 * AvatarSkeletonState & SkinningPaletteUBO
 *
 * Manages Bento skeleton joint dual quaternion palettes and Uniform Buffer Objects (UBO)
 * for GPU Dual Quaternion Skinning (DQS). Supports up to 128 joint dual quaternion pairs per palette.
 */

import { DualQuaternion } from './dual-quaternion';

export const BENTO_MAX_PALETTE_JOINTS = 128;
export const DQ_FLOATS_PER_JOINT = 8;
export const MATRIX_4X4_FLOATS = 16;
export const UBO_PALETTE_SIZE_FLOATS = BENTO_MAX_PALETTE_JOINTS * DQ_FLOATS_PER_JOINT; // 1024 floats (128 real vec4 + 128 dual vec4)
export const UBO_PALETTE_SIZE_BYTES = UBO_PALETTE_SIZE_FLOATS * 4; // 4096 bytes

const REAL_OFFSET = 0; // floats 0..511 for u_joint_dq_real[128]
const DUAL_OFFSET = 512; // floats 512..1023 for u_joint_dq_dual[128]

/**
 * Uniform Buffer Object manager for Bento joint dual quaternion palettes.
 * Maintains std140 layout Float32Array data for GPU upload.
 */
export class SkinningPaletteUBO {
  readonly buffer: Float32Array;
  private isDirty = true;

  constructor(maxJoints: number = BENTO_MAX_PALETTE_JOINTS) {
    this.buffer = new Float32Array(maxJoints * DQ_FLOATS_PER_JOINT);
    this.resetToIdentity();
  }

  /** Reset all joint slots in the palette to identity dual quaternions. */
  resetToIdentity(): void {
    const totalJoints = Math.floor(this.buffer.length / DQ_FLOATS_PER_JOINT);
    const dualStart = totalJoints * 4;

    for (let i = 0; i < totalJoints; i++) {
      const realOffset = i * 4;
      const dualOffset = dualStart + i * 4;

      this.buffer[realOffset] = 0;
      this.buffer[realOffset + 1] = 0;
      this.buffer[realOffset + 2] = 0;
      this.buffer[realOffset + 3] = 1.0;

      this.buffer[dualOffset] = 0;
      this.buffer[dualOffset + 1] = 0;
      this.buffer[dualOffset + 2] = 0;
      this.buffer[dualOffset + 3] = 0;
    }
    this.isDirty = true;
  }

  /** Set a single joint transformation at jointIndex (converts matrix to dual quaternion). */
  updateJointMatrix(jointIndex: number, matrix: ArrayLike<number>): void {
    const totalJoints = Math.floor(this.buffer.length / DQ_FLOATS_PER_JOINT);
    if (jointIndex < 0 || jointIndex >= totalJoints) return;

    const dq = matrix && matrix.length >= 16 ? DualQuaternion.fromMatrix(matrix) : DualQuaternion.identity();
    const realOffset = jointIndex * 4;
    const dualOffset = totalJoints * 4 + jointIndex * 4;

    this.buffer[realOffset] = dq.real[0];
    this.buffer[realOffset + 1] = dq.real[1];
    this.buffer[realOffset + 2] = dq.real[2];
    this.buffer[realOffset + 3] = dq.real[3];

    this.buffer[dualOffset] = dq.dual[0];
    this.buffer[dualOffset + 1] = dq.dual[1];
    this.buffer[dualOffset + 2] = dq.dual[2];
    this.buffer[dualOffset + 3] = dq.dual[3];

    this.isDirty = true;
  }

  /** Bulk update joint transformation matrices for the palette. */
  setJointMatrices(matrices: ArrayLike<number>[]): void {
    const maxJoints = Math.floor(this.buffer.length / DQ_FLOATS_PER_JOINT);
    const count = Math.min(matrices.length, maxJoints);

    for (let i = 0; i < count; i++) {
      this.updateJointMatrix(i, matrices[i]);
    }
    for (let i = count; i < maxJoints; i++) {
      this.updateJointMatrix(i, []);
    }
    this.isDirty = true;
  }

  /** Get joint dual quaternion components at jointIndex. */
  getJointDualQuaternion(jointIndex: number): DualQuaternion {
    const totalJoints = Math.floor(this.buffer.length / DQ_FLOATS_PER_JOINT);
    const realOffset = jointIndex * 4;
    const dualOffset = totalJoints * 4 + jointIndex * 4;

    const real = this.buffer.subarray(realOffset, realOffset + 4);
    const dual = this.buffer.subarray(dualOffset, dualOffset + 4);
    return new DualQuaternion(real, dual);
  }

  /** Get joint matrix at jointIndex reconstructed from dual quaternion. */
  getJointMatrix(jointIndex: number): Float32Array {
    const dq = this.getJointDualQuaternion(jointIndex);
    return dq.toMatrix();
  }

  /** Return the Float32Array buffer backing the UBO. */
  getUboBuffer(): Float32Array {
    return this.buffer;
  }

  /** Upload palette matrix data to WebGL2 Uniform Buffer Object. */
  uploadToGpu(gl: WebGL2RenderingContext, uboBuffer: WebGLBuffer): void {
    gl.bindBuffer(gl.UNIFORM_BUFFER, uboBuffer);
    gl.bufferData(gl.UNIFORM_BUFFER, this.buffer, gl.DYNAMIC_DRAW);
    gl.bindBuffer(gl.UNIFORM_BUFFER, null);
    this.isDirty = false;
  }

  /** Update dirty portion or full UBO subdata on GPU. */
  updateGpuSubData(gl: WebGL2RenderingContext, uboBuffer: WebGLBuffer): void {
    if (!this.isDirty) return;
    gl.bindBuffer(gl.UNIFORM_BUFFER, uboBuffer);
    gl.bufferSubData(gl.UNIFORM_BUFFER, 0, this.buffer);
    gl.bindBuffer(gl.UNIFORM_BUFFER, null);
    this.isDirty = false;
  }

  /** Bind UBO to specific uniform block binding point. */
  bindToBindingPoint(gl: WebGL2RenderingContext, uboBuffer: WebGLBuffer, bindingPoint: number = 0): void {
    gl.bindBufferBase(gl.UNIFORM_BUFFER, bindingPoint, uboBuffer);
  }

  get dirty(): boolean {
    return this.isDirty;
  }
}

/**
 * AvatarSkeletonState
 * Maintains joint dual quaternion palettes for Bento avatar skeletons and handles palette splitting
 * when skeleton joint count exceeds 128 joints.
 */
export class AvatarSkeletonState {
  readonly palettes: SkinningPaletteUBO[] = [];
  private jointToPaletteMap: Map<number, { paletteIndex: number; localJointIndex: number }> = new Map();

  constructor(totalJointCount: number = BENTO_MAX_PALETTE_JOINTS) {
    this.configurePalettes(totalJointCount);
  }

  /** Configure palette count based on total skeleton joints. */
  configurePalettes(totalJointCount: number): void {
    const paletteCount = Math.max(1, Math.ceil(totalJointCount / BENTO_MAX_PALETTE_JOINTS));
    this.palettes.length = 0;
    this.jointToPaletteMap.clear();

    for (let p = 0; p < paletteCount; p++) {
      this.palettes.push(new SkinningPaletteUBO(BENTO_MAX_PALETTE_JOINTS));
    }

    for (let j = 0; j < totalJointCount; j++) {
      const paletteIndex = Math.floor(j / BENTO_MAX_PALETTE_JOINTS);
      const localJointIndex = j % BENTO_MAX_PALETTE_JOINTS;
      this.jointToPaletteMap.set(j, { paletteIndex, localJointIndex });
    }
  }

  /** Update pose with array of joint world transformation matrices. */
  updatePose(matrices: ArrayLike<number>[]): void {
    if (matrices.length > this.palettes.length * BENTO_MAX_PALETTE_JOINTS) {
      this.configurePalettes(matrices.length);
    }

    for (let j = 0; j < matrices.length; j++) {
      const mapping = this.jointToPaletteMap.get(j);
      if (mapping) {
        this.palettes[mapping.paletteIndex].updateJointMatrix(mapping.localJointIndex, matrices[j]);
      }
    }
  }

  /** Get Palette UBO by index. */
  getPaletteUbo(paletteIndex: number = 0): SkinningPaletteUBO {
    return this.palettes[paletteIndex] || this.palettes[0];
  }

  /** Get number of active palettes. */
  getPaletteCount(): number {
    return this.palettes.length;
  }
}
