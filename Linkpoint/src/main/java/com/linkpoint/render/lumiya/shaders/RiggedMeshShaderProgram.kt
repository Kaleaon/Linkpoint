package com.linkpoint.render.lumiya.shaders

import android.opengl.GLES32

/**
 * Shader program for rigged mesh attachments (fitted mesh, animesh).
 *
 * Design lineage: Lumiya `RiggedMeshProgram30.java`, modernised.
 *
 * Uses Uniform Buffer Objects for joint data (binding 1) and supports
 * a bind-shape matrix for mesh-to-skeleton space conversion.
 */
class RiggedMeshShaderProgram : BaseShaderProgram() {

    companion object {
        const val MAX_JOINTS = 134
    }

    private var uModelMatrix = -1
    private var uBindShapeMatrix = -1
    private var uColor = -1
    private var uTextureSampler = -1
    private var uUseTexture = -1
    private var uTexMatrix = -1
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
        layout(location = 3) in vec4 aWeights;
        layout(location = 4) in ivec4 aJoints;

        uniform mat4 uModelMatrix;
        uniform mat4 uBindShapeMatrix;
        uniform mat4 uTexMatrix;
        uniform int uJointCount;

        out vec3 vWorldPos;
        out vec3 vNormal;
        out vec2 vTexCoord;

        void main() {
            vec4 bindPos = uBindShapeMatrix * vec4(aPosition, 1.0);

            mat4 skin = mat4(0.0);
            if (uJointCount > 0) {
                skin += aWeights.x * uJointMatrices[aJoints.x];
                skin += aWeights.y * uJointMatrices[aJoints.y];
                skin += aWeights.z * uJointMatrices[aJoints.z];
                skin += aWeights.w * uJointMatrices[aJoints.w];
            } else {
                skin = mat4(1.0);
            }

            vec4 skinnedPos = skin * bindPos;
            vec4 worldPos = uModelMatrix * skinnedPos;
            vWorldPos = worldPos.xyz;

            vec3 bindNorm = mat3(uBindShapeMatrix) * aNormal;
            vNormal = normalize(mat3(uModelMatrix) * mat3(skin) * bindNorm);

            vTexCoord = (uTexMatrix * vec4(aTexCoord, 0.0, 1.0)).xy;
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
        uBindShapeMatrix = loc("uBindShapeMatrix")
        uColor = loc("uColor")
        uTextureSampler = loc("uTexture")
        uUseTexture = loc("uUseTexture")
        uTexMatrix = loc("uTexMatrix")
        uLightDir = loc("uLightDir")
        uLightDiffuse = loc("uLightDiffuse")
        uLightAmbient = loc("uLightAmbient")
        uJointCount = loc("uJointCount")

        val g = uboIndex("GlobalData")
        if (g != GLES32.GL_INVALID_INDEX) GLES32.glUniformBlockBinding(handle, g, 0)
        val j = uboIndex("JointData")
        if (j != GLES32.GL_INVALID_INDEX) GLES32.glUniformBlockBinding(handle, j, 1)
    }

    fun setModelMatrix(m: FloatArray) = GLES32.glUniformMatrix4fv(uModelMatrix, 1, false, m, 0)
    fun setBindShapeMatrix(m: FloatArray) = GLES32.glUniformMatrix4fv(uBindShapeMatrix, 1, false, m, 0)
    fun setTexMatrix(m: FloatArray) = GLES32.glUniformMatrix4fv(uTexMatrix, 1, false, m, 0)
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
