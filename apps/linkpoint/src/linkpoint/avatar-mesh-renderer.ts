/**
 * AvatarMeshRenderer
 *
 * Manages vertex buffer layouts with skinning attributes (`a_bone_indices`, `a_bone_weights`),
 * WebGL2 Uniform Buffer Objects (UBO) for joint palette uploads, palette splitting, and
 * GPU linear blend skinning execution.
 */

import { BENTO_MAX_PALETTE_JOINTS, SkinningPaletteUBO, VisualParamUBO, AvatarSkeletonState } from './avatar-skeleton-state';
import { AVATAR_SKINNING_VERT_SHADER, AVATAR_SKINNING_VERT_SHADER_LEGACY } from './avatar_skinning.vert';

export interface VertexAttributeLayout {
  name: string;
  size: number;
  type: number; // gl.FLOAT, gl.UNSIGNED_BYTE, etc.
  normalized: boolean;
  stride: number;
  offset: number;
}

export interface SkinnedMeshData {
  positions: Float32Array;
  normals: Float32Array;
  texCoords: Float32Array;
  boneIndices: Float32Array; // a_bone_indices (4 indices per vertex)
  boneWeights: Float32Array; // a_bone_weights (4 weights per vertex)
  indices: Uint16Array | Uint32Array;
}

export interface RenderSubmeshPartition {
  paletteIndex: number;
  indices: Uint16Array | Uint32Array;
  remappedBoneIndices: Float32Array;
}

export interface MorphTargetData {
  name: string;
  deltas: Float32Array; // 3 floats per vertex (dx, dy, dz)
}

/**
 * Package morph target delta arrays into 2D floating-point WebGL2 Data Textures for avatar body parts.
 *
 * Texture layout:
 * Width: vertexCount
 * Height: maxMorphs (up to 32 active morph target deltas per vertex)
 * Format: RGBA32F (4 floats per texel: dx, dy, dz, 0.0)
 */
export function createMorphTargetDataTexture(
  gl: WebGL2RenderingContext | WebGLRenderingContext,
  vertexCount: number,
  morphTargets: MorphTargetData[],
  maxMorphs: number = 32
): WebGLTexture | null {
  if (typeof WebGL2RenderingContext === 'undefined' || !(gl instanceof WebGL2RenderingContext)) {
    return null;
  }
  const gl2 = gl as WebGL2RenderingContext;
  const activeCount = Math.min(morphTargets.length, maxMorphs);
  const height = Math.max(1, activeCount);
  const width = vertexCount;

  const dataArray = new Float32Array(width * height * 4);

  for (let m = 0; m < activeCount; m++) {
    const deltas = morphTargets[m].deltas;
    const rowOffset = m * width * 4;
    for (let v = 0; v < width; v++) {
      const texelOffset = rowOffset + v * 4;
      const v3 = v * 3;
      if (v3 + 2 < deltas.length) {
        dataArray[texelOffset] = deltas[v3];
        dataArray[texelOffset + 1] = deltas[v3 + 1];
        dataArray[texelOffset + 2] = deltas[v3 + 2];
        dataArray[texelOffset + 3] = 0.0;
      }
    }
  }

  const texture = gl2.createTexture();
  if (!texture) return null;

  gl2.bindTexture(gl2.TEXTURE_2D, texture);
  gl2.texImage2D(
    gl2.TEXTURE_2D,
    0,
    gl2.RGBA32F,
    width,
    height,
    0,
    gl2.RGBA,
    gl2.FLOAT,
    dataArray
  );
  gl2.texParameteri(gl2.TEXTURE_2D, gl2.TEXTURE_MIN_FILTER, gl2.NEAREST);
  gl2.texParameteri(gl2.TEXTURE_2D, gl2.TEXTURE_MAG_FILTER, gl2.NEAREST);
  gl2.texParameteri(gl2.TEXTURE_2D, gl2.TEXTURE_WRAP_S, gl2.CLAMP_TO_EDGE);
  gl2.texParameteri(gl2.TEXTURE_2D, gl2.TEXTURE_WRAP_T, gl2.CLAMP_TO_EDGE);
  gl2.bindTexture(gl2.TEXTURE_2D, null);

  return texture;
}

export class AvatarMeshRenderer {
  private gl: WebGLRenderingContext | WebGL2RenderingContext;
  private isUboSupported: boolean = false;
  private uboBuffer: WebGLBuffer | null = null;
  private visualParamUboBuffer: WebGLBuffer | null = null;
  private shaderProgram: WebGLProgram | null = null;
  private uboBlockBinding: number = 0;
  private visualParamBlockBinding: number = 1;

  constructor(gl: WebGLRenderingContext | WebGL2RenderingContext) {
    this.gl = gl;
    this.checkUboCapability();
  }

  /** Capability check for WebGL2 and UBO support. */
  checkUboCapability(): boolean {
    if (typeof WebGL2RenderingContext !== 'undefined' && this.gl instanceof WebGL2RenderingContext) {
      const gl2 = this.gl as WebGL2RenderingContext;
      const maxUniformBlocks = gl2.getParameter(gl2.MAX_VERTEX_UNIFORM_BLOCKS);
      this.isUboSupported = maxUniformBlocks > 0;
    } else {
      this.isUboSupported = false;
    }
    return this.isUboSupported;
  }

  get uboSupported(): boolean {
    return this.isUboSupported;
  }

  /** Initialize GPU UBO buffer for joint palette uploads. */
  initUbo(): WebGLBuffer | null {
    if (!this.isUboSupported) return null;
    const gl2 = this.gl as WebGL2RenderingContext;
    this.uboBuffer = gl2.createBuffer();
    gl2.bindBuffer(gl2.UNIFORM_BUFFER, this.uboBuffer);
    gl2.bufferData(gl2.UNIFORM_BUFFER, 128 * 16 * 4, gl2.DYNAMIC_DRAW);
    gl2.bindBuffer(gl2.UNIFORM_BUFFER, null);
    return this.uboBuffer;
  }

  /** Initialize GPU UBO buffer for visual parameter weight uploads. */
  initVisualParamUbo(): WebGLBuffer | null {
    if (!this.isUboSupported) return null;
    const gl2 = this.gl as WebGL2RenderingContext;
    this.visualParamUboBuffer = gl2.createBuffer();
    gl2.bindBuffer(gl2.UNIFORM_BUFFER, this.visualParamUboBuffer);
    gl2.bufferData(gl2.UNIFORM_BUFFER, 144, gl2.DYNAMIC_DRAW);
    gl2.bindBuffer(gl2.UNIFORM_BUFFER, null);
    return this.visualParamUboBuffer;
  }

  /** Attach UBO block binding point to program for joint palettes. */
  bindProgramUbo(program: WebGLProgram, blockName: string = 'SkinningPaletteBlock', bindingPoint: number = 0): void {
    if (!this.isUboSupported) return;
    const gl2 = this.gl as WebGL2RenderingContext;
    const blockIndex = gl2.getUniformBlockIndex(program, blockName);
    if (blockIndex !== gl2.INVALID_INDEX) {
      gl2.uniformBlockBinding(program, blockIndex, bindingPoint);
      this.uboBlockBinding = bindingPoint;
    }
  }

  /** Attach UBO block binding point to program for visual parameters. */
  bindProgramVisualParamUbo(program: WebGLProgram, blockName: string = 'VisualParamBlock', bindingPoint: number = 1): void {
    if (!this.isUboSupported) return;
    const gl2 = this.gl as WebGL2RenderingContext;
    const blockIndex = gl2.getUniformBlockIndex(program, blockName);
    if (blockIndex !== gl2.INVALID_INDEX) {
      gl2.uniformBlockBinding(program, blockIndex, bindingPoint);
      this.visualParamBlockBinding = bindingPoint;
    }
  }

  /**
   * Return standard vertex buffer attribute locations and layout for avatar skinning.
   * Explicitly defines `a_bone_indices` and `a_bone_weights`.
   */
  getSkinningAttributeLayouts(): Record<string, { size: number; offsetName: string }> {
    return {
      aPosition: { size: 3, offsetName: 'positions' },
      aNormal: { size: 3, offsetName: 'normals' },
      aTexCoord: { size: 2, offsetName: 'texCoords' },
      a_bone_indices: { size: 4, offsetName: 'boneIndices' },
      a_bone_weights: { size: 4, offsetName: 'boneWeights' },
    };
  }

  /**
   * Partition mesh into separate submesh draw calls if joint indices exceed single palette capacity (128 joints).
   */
  partitionSubmeshesByPalette(mesh: SkinnedMeshData, maxPaletteJoints: number = BENTO_MAX_PALETTE_JOINTS): RenderSubmeshPartition[] {
    const vertexCount = Math.floor(mesh.positions.length / 3);
    const triangleCount = Math.floor(mesh.indices.length / 3);

    // Find highest joint index referenced in mesh
    let maxJointIndex = 0;
    for (let i = 0; i < mesh.boneIndices.length; i++) {
      if (mesh.boneIndices[i] > maxJointIndex) {
        maxJointIndex = mesh.boneIndices[i];
      }
    }

    // If max joint fits within 1 palette, return single partition
    if (maxJointIndex < maxPaletteJoints) {
      return [{
        paletteIndex: 0,
        indices: mesh.indices,
        remappedBoneIndices: mesh.boneIndices,
      }];
    }

    // Split mesh indices into submeshes by palette
    const partitionsMap = new Map<number, number[]>();
    for (let t = 0; t < triangleCount; t++) {
      const i0 = mesh.indices[t * 3];
      const i1 = mesh.indices[t * 3 + 1];
      const i2 = mesh.indices[t * 3 + 2];

      // Determine palette based on max joint referenced by triangle's vertices
      let triMaxJoint = 0;
      for (let v of [i0, i1, i2]) {
        for (let b = 0; b < 4; b++) {
          const jointIdx = mesh.boneIndices[v * 4 + b];
          const weight = mesh.boneWeights[v * 4 + b];
          if (weight > 0 && jointIdx > triMaxJoint) {
            triMaxJoint = jointIdx;
          }
        }
      }

      const paletteIndex = Math.floor(triMaxJoint / maxPaletteJoints);
      let list = partitionsMap.get(paletteIndex);
      if (!list) {
        list = [];
        partitionsMap.set(paletteIndex, list);
      }
      list.push(i0, i1, i2);
    }

    const result: RenderSubmeshPartition[] = [];
    for (const [paletteIndex, indicesList] of partitionsMap.entries()) {
      const remappedBones = new Float32Array(mesh.boneIndices.length);

      for (let i = 0; i < mesh.boneIndices.length; i++) {
        const rawJoint = mesh.boneIndices[i];
        remappedBones[i] = Math.floor(rawJoint % maxPaletteJoints);
      }

      const IndexArrayType = mesh.indices instanceof Uint32Array ? Uint32Array : Uint16Array;
      result.push({
        paletteIndex,
        indices: new IndexArrayType(indicesList),
        remappedBoneIndices: remappedBones,
      });
    }

    return result;
  }

  /** Render skinned avatar mesh using UBO palette uploads, visual parameter UBOs, and morph textures or fallback path. */
  renderAvatarMesh(
    skeletonState: AvatarSkeletonState,
    submesh: RenderSubmeshPartition,
    drawCallback: (paletteIndex: number) => void,
    morphTexture?: WebGLTexture | null,
    textureUnit: number = 1
  ): void {
    const palette = skeletonState.getPaletteUbo(submesh.paletteIndex);
    const visualParam = skeletonState.getVisualParamUbo();

    if (this.isUboSupported) {
      const gl2 = this.gl as WebGL2RenderingContext;

      if (this.uboBuffer) {
        palette.uploadToGpu(gl2, this.uboBuffer);
        palette.bindToBindingPoint(gl2, this.uboBuffer, this.uboBlockBinding);
      }

      if (this.visualParamUboBuffer && visualParam) {
        visualParam.uploadToGpu(gl2, this.visualParamUboBuffer);
        visualParam.bindToBindingPoint(gl2, this.visualParamUboBuffer, this.visualParamBlockBinding);
      }

      if (morphTexture) {
        gl2.activeTexture(gl2.TEXTURE0 + textureUnit);
        gl2.bindTexture(gl2.TEXTURE_2D, morphTexture);
      }
    }

    drawCallback(submesh.paletteIndex);
  }
}
