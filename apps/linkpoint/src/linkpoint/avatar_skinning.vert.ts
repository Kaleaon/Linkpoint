/**
 * Avatar Vertex Shader for GPU Linear Blend Skinning using Uniform Buffer Objects (UBO)
 * and Data Texture Morph Target Blending.
 *
 * Supports up to 128 joint matrices per palette stored in the SkinningPaletteBlock UBO,
 * and up to 32 active morph target deltas per vertex sampled from 2D Data Textures
 * weighted by the VisualParamBlock UBO.
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
    mat4 u_joint_matrices[128];
};

layout(std140) uniform VisualParamBlock {
    vec4 u_morph_weights[8];
    int u_active_morph_count;
};

uniform sampler2D u_morph_delta_texture;

uniform mat4 uModelMatrix;
uniform mat4 uViewMatrix;
uniform mat4 uProjectionMatrix;
uniform mat3 uNormalMatrix;

out vec3 vNormal;
out vec2 vTexCoord;
out vec3 vPosition;

void main() {
    vec3 morphedPosition = aPosition;

    if (u_active_morph_count > 0) {
        vec3 morphOffset = vec3(0.0);
        int maxMorphs = u_active_morph_count;
        if (maxMorphs > 32) { maxMorphs = 32; }

        for (int i = 0; i < 32; i++) {
            if (i >= maxMorphs) break;
            int vecIdx = i / 4;
            int compIdx = i % 4;
            float weight = (compIdx == 0) ? u_morph_weights[vecIdx].x :
                          ((compIdx == 1) ? u_morph_weights[vecIdx].y :
                          ((compIdx == 2) ? u_morph_weights[vecIdx].z : u_morph_weights[vecIdx].w));

            if (abs(weight) > 0.0001) {
                vec3 delta = texelFetch(u_morph_delta_texture, ivec2(gl_VertexID, i), 0).xyz;
                morphOffset += delta * weight;
            }
        }
        morphedPosition += morphOffset;
    }

    ivec4 indices = ivec4(a_bone_indices + 0.5);
    vec4 weights = a_bone_weights;

    mat4 skinMatrix = mat4(0.0);
    float totalWeight = weights.x + weights.y + weights.z + weights.w;

    if (totalWeight > 0.0) {
        skinMatrix += weights.x * u_joint_matrices[indices.x];
        skinMatrix += weights.y * u_joint_matrices[indices.y];
        skinMatrix += weights.z * u_joint_matrices[indices.z];
        skinMatrix += weights.w * u_joint_matrices[indices.w];
    } else {
        skinMatrix = mat4(1.0);
    }

    vec4 skinnedPosition = skinMatrix * vec4(morphedPosition, 1.0);
    mat3 skinNormalMatrix = mat3(skinMatrix);
    vec3 skinnedNormal = skinNormalMatrix * aNormal;

    vec4 worldPos = uModelMatrix * skinnedPosition;
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
uniform mat4 u_joint_matrices[110];

varying vec3 vNormal;
varying vec2 vTexCoord;
varying vec3 vPosition;

void main() {
    vec4 weights = a_bone_weights;
    ivec4 indices = ivec4(a_bone_indices + 0.5);

    mat4 skinMatrix = mat4(0.0);
    float totalWeight = weights.x + weights.y + weights.z + weights.w;

    if (totalWeight > 0.0) {
        skinMatrix += weights.x * u_joint_matrices[indices.x];
        skinMatrix += weights.y * u_joint_matrices[indices.y];
        skinMatrix += weights.z * u_joint_matrices[indices.z];
        skinMatrix += weights.w * u_joint_matrices[indices.w];
    } else {
        skinMatrix = mat4(1.0);
    }

    vec4 skinnedPos = skinMatrix * vec4(aPosition, 1.0);
    vec3 skinnedNormal = mat3(skinMatrix) * aNormal;

    vec4 worldPos = uModelMatrix * skinnedPos;
    vPosition = worldPos.xyz;
    vNormal = normalize(uNormalMatrix * skinnedNormal);
    vTexCoord = aTexCoord;

    gl_Position = uProjectionMatrix * uViewMatrix * worldPos;
}
`;
