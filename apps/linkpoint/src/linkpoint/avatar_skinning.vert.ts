/**
 * Avatar Vertex Shader for GPU Dual Quaternion Skinning (DQS) using Uniform Buffer Objects (UBO).
 *
 * Supports up to 128 joint dual quaternion pairs (u_joint_dq_real and u_joint_dq_dual)
 * stored in the SkinningPaletteBlock UBO.
 * Transforms vertex position and normal on GPU using a_bone_indices and a_bone_weights attributes
 * with Dual Linear Blending (DLB) and antipodal alignment check.
 */
export const AVATAR_SKINNING_VERT_SHADER = `#version 300 es
precision highp float;

layout(location = 0) in vec3 aPosition;
layout(location = 1) in vec3 aNormal;
layout(location = 2) in vec2 aTexCoord;
layout(location = 3) in vec3 aTangent;
layout(location = 4) in vec4 a_bone_indices;
layout(location = 5) in vec4 a_bone_weights;

layout(std140) uniform SkinningPaletteBlock {
    vec4 u_joint_dq_real[128];
    vec4 u_joint_dq_dual[128];
};

uniform mat4 uModelMatrix;
uniform mat4 uViewMatrix;
uniform mat4 uProjectionMatrix;
uniform mat3 uNormalMatrix;

out vec3 vNormal;
out vec2 vTexCoord;
out vec3 vPosition;

void blendJoint(int index, float weight, vec4 refReal, inout vec4 bReal, inout vec4 bDual) {
    if (weight <= 0.0) return;
    vec4 q0 = u_joint_dq_real[index];
    vec4 qe = u_joint_dq_dual[index];
    // Antipodal sign alignment check
    float sign = dot(refReal, q0) < 0.0 ? -1.0 : 1.0;
    bReal += weight * sign * q0;
    bDual += weight * sign * qe;
}

void main() {
    ivec4 indices = ivec4(a_bone_indices + 0.5);
    vec4 weights = a_bone_weights;

    vec4 dqReal = vec4(0.0);
    vec4 dqDual = vec4(0.0);
    float totalWeight = weights.x + weights.y + weights.z + weights.w;

    if (totalWeight > 0.0) {
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

    vec3 p = aPosition;
    vec3 r_xyz = dqReal.xyz;
    float r_w = dqReal.w;
    vec3 d_xyz = dqDual.xyz;
    float d_w = dqDual.w;

    vec3 rotPos = p + 2.0 * cross(r_xyz, cross(r_xyz, p) + r_w * p);
    vec3 trans = 2.0 * (r_w * d_xyz - d_w * r_xyz + cross(r_xyz, d_xyz));
    vec3 skinnedPosition = rotPos + trans;

    vec3 skinnedNormal = aNormal + 2.0 * cross(r_xyz, cross(r_xyz, aNormal) + r_w * aNormal);

    vec4 worldPos = uModelMatrix * vec4(skinnedPosition, 1.0);
    vPosition = worldPos.xyz;
    vNormal = normalize(uNormalMatrix * skinnedNormal);
    vTexCoord = aTexCoord;

    gl_Position = uProjectionMatrix * uViewMatrix * worldPos;
}
`;

/** Alternative GLSL ES 1.0 fallback vertex shader when WebGL2/UBO is not available. */
export const AVATAR_SKINNING_VERT_SHADER_LEGACY = `
attribute vec3 aPosition;
attribute vec3 aNormal;
attribute vec2 aTexCoord;
attribute vec4 a_bone_indices;
attribute vec4 a_bone_weights;

uniform mat4 uModelMatrix;
uniform mat4 uViewMatrix;
uniform mat4 uProjectionMatrix;
uniform mat3 uNormalMatrix;
uniform vec4 u_joint_dq_real[110];
uniform vec4 u_joint_dq_dual[110];

varying vec3 vNormal;
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
    vec4 weights = a_bone_weights;
    ivec4 indices = ivec4(a_bone_indices + 0.5);

    vec4 dqReal = vec4(0.0);
    vec4 dqDual = vec4(0.0);
    float totalWeight = weights.x + weights.y + weights.z + weights.w;

    if (totalWeight > 0.0) {
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

    vec3 p = aPosition;
    vec3 r_xyz = dqReal.xyz;
    float r_w = dqReal.w;
    vec3 d_xyz = dqDual.xyz;
    float d_w = dqDual.w;

    vec3 rotPos = p + 2.0 * cross(r_xyz, cross(r_xyz, p) + r_w * p);
    vec3 trans = 2.0 * (r_w * d_xyz - d_w * r_xyz + cross(r_xyz, d_xyz));
    vec3 skinnedPos = rotPos + trans;

    vec3 skinnedNormal = aNormal + 2.0 * cross(r_xyz, cross(r_xyz, aNormal) + r_w * aNormal);

    vec4 worldPos = uModelMatrix * vec4(skinnedPos, 1.0);
    vPosition = worldPos.xyz;
    vNormal = normalize(uNormalMatrix * skinnedNormal);
    vTexCoord = aTexCoord;

    gl_Position = uProjectionMatrix * uViewMatrix * worldPos;
}
`;
