/**
 * AvatarSkeletonState & SkinningPaletteUBO
 *
 * Manages Bento skeleton joint matrix palettes and Uniform Buffer Objects (UBO)
 * for GPU linear blend skinning. Supports up to 128 4x4 transformation matrices per palette.
 */

export const BENTO_MAX_PALETTE_JOINTS = 128;
export const MATRIX_4X4_FLOATS = 16;
export const MATRIX_4X4_BYTES = MATRIX_4X4_FLOATS * 4; // 64 bytes per 4x4 matrix in std140
export const UBO_PALETTE_SIZE_FLOATS = BENTO_MAX_PALETTE_JOINTS * MATRIX_4X4_FLOATS; // 2048 floats
export const UBO_PALETTE_SIZE_BYTES = UBO_PALETTE_SIZE_FLOATS * 4; // 8192 bytes

const IDENTITY_MATRIX = new Float32Array([
  1, 0, 0, 0,
  0, 1, 0, 0,
  0, 0, 1, 0,
  0, 0, 0, 1,
]);

/**
 * Uniform Buffer Object manager for Bento joint matrix palettes.
 * Maintains std140 layout Float32Array data for GPU upload.
 */
export class SkinningPaletteUBO {
  readonly buffer: Float32Array;
  private isDirty = true;

  constructor(maxJoints: number = BENTO_MAX_PALETTE_JOINTS) {
    this.buffer = new Float32Array(maxJoints * MATRIX_4X4_FLOATS);
    this.resetToIdentity();
  }

  /** Reset all matrix slots in the palette to 4x4 identity matrices. */
  resetToIdentity(): void {
    const totalJoints = Math.floor(this.buffer.length / MATRIX_4X4_FLOATS);
    for (let i = 0; i < totalJoints; i++) {
      this.buffer.set(IDENTITY_MATRIX, i * MATRIX_4X4_FLOATS);
    }
    this.isDirty = true;
  }

  /** Set a single joint transformation matrix at jointIndex. */
  updateJointMatrix(jointIndex: number, matrix: ArrayLike<number>): void {
    const offset = jointIndex * MATRIX_4X4_FLOATS;
    if (offset + MATRIX_4X4_FLOATS > this.buffer.length) return;

    if (matrix.length >= MATRIX_4X4_FLOATS) {
      for (let i = 0; i < MATRIX_4X4_FLOATS; i++) {
        this.buffer[offset + i] = matrix[i];
      }
    } else {
      this.buffer.set(IDENTITY_MATRIX, offset);
    }
    this.isDirty = true;
  }

  /** Bulk update joint transformation matrices for the palette. */
  setJointMatrices(matrices: ArrayLike<number>[]): void {
    const maxJoints = Math.floor(this.buffer.length / MATRIX_4X4_FLOATS);
    const count = Math.min(matrices.length, maxJoints);

    for (let i = 0; i < count; i++) {
      const m = matrices[i];
      if (m && m.length >= MATRIX_4X4_FLOATS) {
        this.buffer.set(m as ArrayLike<number>, i * MATRIX_4X4_FLOATS);
      } else {
        this.buffer.set(IDENTITY_MATRIX, i * MATRIX_4X4_FLOATS);
      }
    }
    // Any remaining unused slots stay identity
    for (let i = count; i < maxJoints; i++) {
      this.buffer.set(IDENTITY_MATRIX, i * MATRIX_4X4_FLOATS);
    }
    this.isDirty = true;
  }

  /** Get joint matrix at jointIndex as a Float32Array slice. */
  getJointMatrix(jointIndex: number): Float32Array {
    const offset = jointIndex * MATRIX_4X4_FLOATS;
    return this.buffer.slice(offset, offset + MATRIX_4X4_FLOATS);
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
 * Maintains joint matrix palettes for Bento avatar skeletons and handles palette splitting
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
