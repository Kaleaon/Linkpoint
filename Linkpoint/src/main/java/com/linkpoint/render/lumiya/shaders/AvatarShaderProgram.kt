package com.linkpoint.render.lumiya.shaders

import android.opengl.GLES32

/**
 * Shader program for rigged avatar meshes.
 *
 * Design lineage: Lumiya `AvatarProgram.java`, rewritten for GL ES 3.20.
 *
 * Uses a UBO (binding point 1) to hold joint matrices so that the full
 * 133-joint SL skeleton fits comfortably within uniform buffer limits.
 */
class AvatarShaderProgram : BaseShaderProgram() {

    companion object {
        /** Maximum skeleton joints supported (SL skeleton uses up to 133). */
        const val MAX_JOINTS = 134
    }

    private var uModelMatrix = -1
    private var uColor = -1
    private var uTextureSampler = -1
    private var uUseTexture = -1
    private var uLightDir = -1
    private var uLightDiffuse = -1
    private var uLightAmbient = -1
    private var uJointCount = -1

    override val vertexSource = """
        #version 320 es
        precision highp float;

        layout(std140, binding = 0) uniform GlobalData {
            mat4 uProjection;
            mat4 uView;
            mat4 _pad_model;
            vec4 uCameraPos;
            vec4 uSunDir;
        };

        layout(std140, binding = 1) uniform JointData {
            mat4 uJointMatrices[$MAX_JOINTS];
        };

        layout(location = 0) in vec3 aPosition;
        layout(location = 1) in vec3 aNormal;
        layout(location = 2) in vec2 aTexCoord;
        layout(location = 3) in vec4 aWeights;   // bone blend weights
        // Stored as floats so glVertexAttribPointer (not glVertexAttribIPointer)
        // is correct. Converted to int inside the shader via floor+0.5 round.
        layout(location = 4) in vec4 aJoints;

        uniform mat4 uModelMatrix;
        uniform int uJointCount;

        out vec3 vWorldPos;
        out vec3 vNormal;
        out vec2 vTexCoord;

        void main() {
            mat4 skin = mat4(0.0);
            if (uJointCount > 0) {
                ivec4 ji = ivec4(aJoints + vec4(0.5));
                skin += aWeights.x * uJointMatrices[ji.x];
                skin += aWeights.y * uJointMatrices[ji.y];
                skin += aWeights.z * uJointMatrices[ji.z];
                skin += aWeights.w * uJointMatrices[ji.w];
            } else {
                skin = mat4(1.0);
            }

            vec4 skinnedPos = skin * vec4(aPosition, 1.0);
            vec4 worldPos = uModelMatrix * skinnedPos;
            vWorldPos = worldPos.xyz;
            vNormal = normalize(mat3(uModelMatrix) * mat3(skin) * aNormal);
            vTexCoord = aTexCoord;
            gl_Position = uProjection * uView * worldPos;
        }
    """.trimIndent()

    override val fragmentSource = """
        #version 320 es
        precision mediump float;

        in vec3 vWorldPos;
        in vec3 vNormal;
        in vec2 vTexCoord;

        uniform sampler2D uTexture;
        uniform vec4 uColor;
        uniform int uUseTexture;
        uniform vec3 uLightDir;
        uniform vec3 uLightDiffuse;
        uniform vec3 uLightAmbient;

        out vec4 fragColor;

        void main() {
            vec4 baseColor;
            if (uUseTexture != 0) {
                baseColor = texture(uTexture, vTexCoord) * uColor;
            } else {
                baseColor = uColor;
            }
            if (baseColor.a < 0.004) discard;

            float NdotL = max(dot(vNormal, normalize(uLightDir)), 0.0);
            vec3 lit = baseColor.rgb * (uLightAmbient + uLightDiffuse * NdotL);
            fragColor = vec4(lit, baseColor.a);
        }
    """.trimIndent()

    override fun onBind() {
        uModelMatrix = loc("uModelMatrix")
        uColor = loc("uColor")
        uTextureSampler = loc("uTexture")
        uUseTexture = loc("uUseTexture")
        uLightDir = loc("uLightDir")
        uLightDiffuse = loc("uLightDiffuse")
        uLightAmbient = loc("uLightAmbient")
        uJointCount = loc("uJointCount")

        val globalIdx = uboIndex("GlobalData")
        if (globalIdx != GLES32.GL_INVALID_INDEX) GLES32.glUniformBlockBinding(handle, globalIdx, 0)
        val jointIdx = uboIndex("JointData")
        if (jointIdx != GLES32.GL_INVALID_INDEX) GLES32.glUniformBlockBinding(handle, jointIdx, 1)
    }

    fun setModelMatrix(m: FloatArray) = GLES32.glUniformMatrix4fv(uModelMatrix, 1, false, m, 0)
    fun setColor(r: Float, g: Float, b: Float, a: Float) = GLES32.glUniform4f(uColor, r, g, b, a)
    fun setUseTexture(use: Boolean) = GLES32.glUniform1i(uUseTexture, if (use) 1 else 0)
    fun setTextureSampler(unit: Int) = GLES32.glUniform1i(uTextureSampler, unit)
    fun setJointCount(count: Int) = GLES32.glUniform1i(uJointCount, count)

    fun setLighting(
        dirX: Float, dirY: Float, dirZ: Float,
        diffR: Float, diffG: Float, diffB: Float,
        ambR: Float, ambG: Float, ambB: Float
    ) {
        GLES32.glUniform3f(uLightDir, dirX, dirY, dirZ)
        GLES32.glUniform3f(uLightDiffuse, diffR, diffG, diffB)
        GLES32.glUniform3f(uLightAmbient, ambR, ambG, ambB)
    }
}
