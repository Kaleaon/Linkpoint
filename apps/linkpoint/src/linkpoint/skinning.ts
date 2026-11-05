/**
 * GPU skinning support: Dual Quaternion Skinning (DQS) shaders and joint packing.
 *
 * Joint matrices are converted to dual quaternions and uploaded as uniform pairs
 * (`u_joint_dq_real` and `u_joint_dq_dual`), requiring 8 floats (2 vec4 vectors)
 * per joint instead of 16 floats (mat4) or 12 floats (affine 3x4).
 */

import { DualQuaternion } from './dual-quaternion';

export { AVATAR_SKINNING_VERT_SHADER, AVATAR_SKINNING_VERT_SHADER_LEGACY } from './avatar_skinning.vert';
export { SkinningPaletteUBO, AvatarSkeletonState, BENTO_MAX_PALETTE_JOINTS } from './avatar-skeleton-state';
export { AvatarMeshRenderer } from './avatar-mesh-renderer';
export type { SkinnedMeshData, RenderSubmeshPartition } from './avatar-mesh-renderer';
export { DualQuaternion } from './dual-quaternion';

/** Joints in the largest rigs Second Life accepts. */
export const SL_MAX_RIGGED_JOINTS = 110;
/** Uniform vectors left for camera/matrices in vertex shader. */
const RESERVED_VERTEX_VECTORS = 24;

/** How many joints fit in a vertex shader given `MAX_VERTEX_UNIFORM_VECTORS` using DQS (2 vec4s per joint). */
export function maxSkinJoints(maxVertexUniformVectors: number): number {
  const fit = Math.floor((maxVertexUniformVectors - RESERVED_VERTEX_VECTORS) / 2);
  return Math.max(1, Math.min(SL_MAX_RIGGED_JOINTS, fit));
}

export function skinnedVertexShader(maxJoints: number): string {
  return `
    attribute vec3 aPosition;
    attribute vec3 aNormal;
    attribute vec2 aTexCoord;
    attribute vec3 aTangent;
    attribute vec4 aJoints;
    attribute vec4 aWeights;

    uniform mat4 uModelMatrix;
    uniform mat4 uViewMatrix;
    uniform mat4 uProjectionMatrix;
    uniform mat3 uNormalMatrix;
    uniform vec4 u_joint_dq_real[${maxJoints}];
    uniform vec4 u_joint_dq_dual[${maxJoints}];

    varying vec3 vNormal;
    varying vec3 vTangent;
    varying vec2 vTexCoord;
    varying vec3 vPosition;

    void blendJoint(int index, float weight, vec4 refReal, inout vec4 bReal, inout vec4 bDual) {
      if (weight <= 0.0) return;
      vec4 q0 = u_joint_dq_real[index];
      vec4 qe = u_joint_dq_dual[index];
      float sign = dot(refReal, q0) < 0.0 ? -1.0 : 1.0;
      bReal += weight * sign * q0;
      bDual += weight * sign * qe;
    }

    void main() {
      ivec4 indices = ivec4(aJoints + 0.5);
      vec4 weights = aWeights;

      vec4 dqReal = vec4(0.0);
      vec4 dqDual = vec4(0.0);
      float total = weights.x + weights.y + weights.z + weights.w;

      if (total > 0.0) {
        vec4 refReal = u_joint_dq_real[indices.x];
        blendJoint(indices.x, weights.x, refReal, dqReal, dqDual);
        blendJoint(indices.y, weights.y, refReal, dqReal, dqDual);
        blendJoint(indices.z, weights.z, refReal, dqReal, dqDual);
        blendJoint(indices.w, weights.w, refReal, dqReal, dqDual);

        float len = length(dqReal);
        if (len > 0.00001) {
          dqReal /= len;
          dqDual /= len;
        } else {
          dqReal = vec4(0.0, 0.0, 0.0, 1.0);
          dqDual = vec4(0.0);
        }
      } else {
        dqReal = vec4(0.0, 0.0, 0.0, 1.0);
        dqDual = vec4(0.0);
      }

      vec3 pos = aPosition;
      vec3 r_xyz = dqReal.xyz;
      float r_w = dqReal.w;
      vec3 d_xyz = dqDual.xyz;
      float d_w = dqDual.w;

      vec3 rotPos = pos + 2.0 * cross(r_xyz, cross(r_xyz, pos) + r_w * pos);
      vec3 trans = 2.0 * (r_w * d_xyz - d_w * r_xyz + cross(r_xyz, d_xyz));
      vec3 skinnedPos = rotPos + trans;

      vec3 skinnedNormal = aNormal + 2.0 * cross(r_xyz, cross(r_xyz, aNormal) + r_w * aNormal);
      vec3 skinnedTangent = aTangent + 2.0 * cross(r_xyz, cross(r_xyz, aTangent) + r_w * aTangent);

      if (total <= 0.0) {
        skinnedPos = aPosition;
        skinnedNormal = aNormal;
        skinnedTangent = aTangent;
      }

      vec4 worldPos = uModelMatrix * vec4(skinnedPos, 1.0);
      vPosition = worldPos.xyz;
      vNormal = normalize(uNormalMatrix * skinnedNormal);
      vTangent = normalize(uNormalMatrix * skinnedTangent);
      vTexCoord = aTexCoord;
      gl_Position = uProjectionMatrix * uViewMatrix * worldPos;
    }
  `;
}

/**
 * Pack column-major 4x4 joint matrices into dual quaternions (8 floats per joint: 4 real + 4 dual).
 */
export function packJointDualQuaternions(matrices: ArrayLike<number>[], maxJoints: number): Float32Array {
  const out = new Float32Array(maxJoints * 8);
  for (let j = 0; j < maxJoints; j++) {
    const m = matrices[j];
    const dq = m && m.length >= 16 ? DualQuaternion.fromMatrix(m) : DualQuaternion.identity();
    const offset = j * 8;
    out[offset] = dq.real[0];
    out[offset + 1] = dq.real[1];
    out[offset + 2] = dq.real[2];
    out[offset + 3] = dq.real[3];

    out[offset + 4] = dq.dual[0];
    out[offset + 5] = dq.dual[1];
    out[offset + 6] = dq.dual[2];
    out[offset + 7] = dq.dual[3];
  }
  return out;
}

/**
 * Packs 8 float values per joint as normalized dual quaternions into uniform buffers.
 */
export function packJointRows(matrices: ArrayLike<number>[], maxJoints: number): Float32Array {
  return packJointDualQuaternions(matrices, maxJoints);
}

/** Flatten per-vertex joint indices to what the shader can address, clamping to the uploaded joint count. */
export function clampJointIndices(joints: ArrayLike<number>, maxJoints: number): number[] {
  const out = new Array<number>(joints.length);
  for (let i = 0; i < joints.length; i++) {
    const v = joints[i];
    out[i] = Number.isInteger(v) && v >= 0 && v < maxJoints ? v : 0;
  }
  return out;
}
