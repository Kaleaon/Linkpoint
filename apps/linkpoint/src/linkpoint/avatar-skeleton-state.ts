/**
 * AvatarSkeletonState & SkinningPaletteUBO & VisualParamUBO
 *
 * Manages Bento skeleton joint matrix palettes, visual parameters, and Uniform Buffer Objects (UBO)
 * for GPU linear blend skinning and morph target shape deformation. Supports up to 128 4x4
 * transformation matrices per palette and up to 32 active morph target deltas.
 */

export const BENTO_MAX_PALETTE_JOINTS = 128;
export const MATRIX_4X4_FLOATS = 16;
export const MATRIX_4X4_BYTES = MATRIX_4X4_FLOATS * 4; // 64 bytes per 4x4 matrix in std140
export const UBO_PALETTE_SIZE_FLOATS = BENTO_MAX_PALETTE_JOINTS * MATRIX_4X4_FLOATS; // 2048 floats
export const UBO_PALETTE_SIZE_BYTES = UBO_PALETTE_SIZE_FLOATS * 4; // 8192 bytes

export const MAX_ACTIVE_MORPH_TARGETS = 32;
export const VISUAL_PARAM_UBO_SIZE_BYTES = 144; // std140 layout: 32 floats (128B) + 1 int32 (4B) + 3 padding int32s (12B)

const IDENTITY_MATRIX = new Float32Array([
  1, 0, 0, 0,
  0, 1, 0, 0,
  0, 0, 1, 0,
  0, 0, 0, 1,
]);

/**
 * Uniform Buffer Object manager for visual parameter weights and active morph target counts.
 * Maintains std140 layout memory for GPU upload.
 */
export class VisualParamUBO {
  readonly arrayBuffer: ArrayBuffer;
  readonly floatView: Float32Array;
  readonly intView: Int32Array;
  private isDirty = true;

  constructor(maxMorphTargets: number = MAX_ACTIVE_MORPH_TARGETS) {
    this.arrayBuffer = new ArrayBuffer(VISUAL_PARAM_UBO_SIZE_BYTES);
    this.floatView = new Float32Array(this.arrayBuffer);
    this.intView = new Int32Array(this.arrayBuffer);
    this.resetToZero();
  }

  /** Reset all morph weights and active morph count to zero. */
  resetToZero(): void {
    this.floatView.fill(0);
    this.isDirty = true;
  }

  /** Set single morph target weight at index. */
  setMorphWeight(index: number, weight: number): void {
    if (index >= 0 && index < MAX_ACTIVE_MORPH_TARGETS) {
      this.floatView[index] = weight;
      this.isDirty = true;
    }
  }

  /** Set array of morph target weights up to 32. Automatically updates active morph count if not set. */
  setMorphWeights(weights: ArrayLike<number>): void {
    const count = Math.min(weights.length, MAX_ACTIVE_MORPH_TARGETS);
    for (let i = 0; i < count; i++) {
      this.floatView[i] = weights[i];
    }
    for (let i = count; i < MAX_ACTIVE_MORPH_TARGETS; i++) {
      this.floatView[i] = 0;
    }
    this.intView[32] = count;
    this.isDirty = true;
  }

  /** Set active morph target count. */
  setActiveMorphCount(count: number): void {
    this.intView[32] = Math.min(Math.max(0, count), MAX_ACTIVE_MORPH_TARGETS);
    this.isDirty = true;
  }

  /** Get active morph target count. */
  getActiveMorphCount(): number {
    return this.intView[32];
  }

  /** Get slice of morph target weights. */
  getMorphWeights(): Float32Array {
    return this.floatView.slice(0, MAX_ACTIVE_MORPH_TARGETS);
  }

  /** Return backing Float32Array for UBO buffer. */
  getUboBuffer(): Float32Array {
    return this.floatView;
  }

  /** Return backing ArrayBuffer for GPU upload. */
  getArrayBuffer(): ArrayBuffer {
    return this.arrayBuffer;
  }

  /** Upload visual parameter data to WebGL2 Uniform Buffer Object. */
  uploadToGpu(gl: WebGL2RenderingContext, uboBuffer: WebGLBuffer): void {
    gl.bindBuffer(gl.UNIFORM_BUFFER, uboBuffer);
    gl.bufferData(gl.UNIFORM_BUFFER, this.arrayBuffer, gl.DYNAMIC_DRAW);
    gl.bindBuffer(gl.UNIFORM_BUFFER, null);
    this.isDirty = false;
  }

  /** Update dirty visual parameter subdata on GPU. */
  updateGpuSubData(gl: WebGL2RenderingContext, uboBuffer: WebGLBuffer): void {
    if (!this.isDirty) return;
    gl.bindBuffer(gl.UNIFORM_BUFFER, uboBuffer);
    gl.bufferSubData(gl.UNIFORM_BUFFER, 0, this.arrayBuffer);
    gl.bindBuffer(gl.UNIFORM_BUFFER, null);
    this.isDirty = false;
  }

  /** Bind UBO to specific uniform block binding point. */
  bindToBindingPoint(gl: WebGL2RenderingContext, uboBuffer: WebGLBuffer, bindingPoint: number = 1): void {
    gl.bindBufferBase(gl.UNIFORM_BUFFER, bindingPoint, uboBuffer);
  }

  get dirty(): boolean {
    return this.isDirty;
  }
}

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
  readonly visualParamUbo: VisualParamUBO = new VisualParamUBO();
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

  /** Get Visual Parameter UBO instance. */
  getVisualParamUbo(): VisualParamUBO {
    return this.visualParamUbo;
  }

  /** Update visual parameter weights array. */
  setVisualParameters(weights: ArrayLike<number>): void {
    this.visualParamUbo.setMorphWeights(weights);
  }
}
