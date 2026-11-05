/**
 * GPU skinning support: the skinned vertex shader and joint-matrix packing.
 *
 * Joint matrices are uploaded as three vec4 rows each (an affine 3x4), so a
 * 110-joint SL rig needs 330 uniform vectors instead of 440. WebGL 1 vertex
 * shaders may index uniform arrays dynamically, so no joint texture is needed.
 */

export { AVATAR_SKINNING_VERT_SHADER, AVATAR_SKINNING_VERT_SHADER_LEGACY } from './avatar_skinning.vert';
export { SkinningPaletteUBO, AvatarSkeletonState, BENTO_MAX_PALETTE_JOINTS } from './avatar-skeleton-state';
export { AvatarMeshRenderer } from './avatar-mesh-renderer';
export type { SkinnedMeshData, RenderSubmeshPartition } from './avatar-mesh-renderer';

/** Joints in the largest rigs Second Life accepts. */
export const SL_MAX_RIGGED_JOINTS = 110;
/** Uniform vectors left for the matrices/camera/etc. of the vertex shader. */
const RESERVED_VERTEX_VECTORS = 24;

/** How many joints fit in a vertex shader given `MAX_VERTEX_UNIFORM_VECTORS`. */
export function maxSkinJoints(maxVertexUniformVectors: number): number {
  const fit = Math.floor((maxVertexUniformVectors - RESERVED_VERTEX_VECTORS) / 3);
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
    uniform vec4 uJointRows[${maxJoints * 3}];

    varying vec3 vNormal;
    varying vec3 vTangent;
    varying vec2 vTexCoord;
    varying vec3 vPosition;

    void skinInfluence(float joint, float weight, vec4 p, vec3 n, vec3 t, inout vec3 outP, inout vec3 outN, inout vec3 outT, inout float total) {
      if (weight <= 0.0) return;
      int base = int(joint + 0.5) * 3;
      vec4 r0 = uJointRows[base];
      vec4 r1 = uJointRows[base + 1];
      vec4 r2 = uJointRows[base + 2];
      outP += weight * vec3(dot(r0, p), dot(r1, p), dot(r2, p));
      outN += weight * vec3(dot(r0.xyz, n), dot(r1.xyz, n), dot(r2.xyz, n));
      outT += weight * vec3(dot(r0.xyz, t), dot(r1.xyz, t), dot(r2.xyz, t));
      total += weight;
    }

    void main() {
      vec4 p = vec4(aPosition, 1.0);
      vec3 skinnedPos = vec3(0.0);
      vec3 skinnedNormal = vec3(0.0);
      vec3 skinnedTangent = vec3(0.0);
      float total = 0.0;
      skinInfluence(aJoints.x, aWeights.x, p, aNormal, aTangent, skinnedPos, skinnedNormal, skinnedTangent, total);
      skinInfluence(aJoints.y, aWeights.y, p, aNormal, aTangent, skinnedPos, skinnedNormal, skinnedTangent, total);
      skinInfluence(aJoints.z, aWeights.z, p, aNormal, aTangent, skinnedPos, skinnedNormal, skinnedTangent, total);
      skinInfluence(aJoints.w, aWeights.w, p, aNormal, aTangent, skinnedPos, skinnedNormal, skinnedTangent, total);
      // Vertices with no influence stay in bind position instead of collapsing to the origin.
      if (total <= 0.0) { skinnedPos = aPosition; skinnedNormal = aNormal; skinnedTangent = aTangent; }
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
 * Pack column-major 4x4 joint matrices into the shader's row layout
 * (`uJointRows[3 * joint + row] = (m[row], m[4 + row], m[8 + row], m[12 + row])`).
 * Joints beyond `maxJoints` are dropped; unused slots stay identity.
 */
export function packJointRows(matrices: ArrayLike<number>[], maxJoints: number, target?: Float32Array): Float32Array {
  const neededLength = maxJoints * 12;
  const out = target && target.length >= neededLength ? target : new Float32Array(neededLength);
  for (let j = 0; j < maxJoints; j++) {
    const m = matrices[j];
    for (let row = 0; row < 3; row++) {
      const o = j * 12 + row * 4;
      if (m && m.length === 16) {
        out[o] = m[row];
        out[o + 1] = m[4 + row];
        out[o + 2] = m[8 + row];
        out[o + 3] = m[12 + row];
      } else {
        out[o] = row === 0 ? 1 : 0;
        out[o + 1] = row === 1 ? 1 : 0;
        out[o + 2] = row === 2 ? 1 : 0;
        out[o + 3] = 0;
      }
    }
  }
  return out;
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
