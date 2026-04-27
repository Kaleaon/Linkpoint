package com.linkpoint.render.lumiya.shaders

import android.opengl.GLES32

/**
 * Gerstner-wave water surface shader.
 *
 * Design lineage: Lumiya `WaterProgram.java` + `water.vsh/fsh`, modernised.
 *
 * 4-layer wave superposition with per-layer frequency, amplitude, phase, and
 * direction.  The fragment shader adds simple specular highlights and
 * colour modulation.
 */
class WaterShaderProgram : BaseShaderProgram() {

    private var uModelMatrix = -1
    private var uColor = -1
    private var uTime = -1
    private var uFrequency = -1
    private var uAmplitude = -1
    private var uPhase = -1
    private var uDirection = -1
    private var uSunDir = -1
    private var uCameraPos = -1

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

        layout(location = 0) in vec3 aPosition;

        uniform mat4 uModelMatrix;
        uniform float uTime;
        uniform float uFrequency[4];
        uniform float uAmplitude[4];
        uniform float uPhase[4];
        uniform vec2 uDirection[4];

        out vec3 vWorldPos;
        out vec3 vNormal;

        void main() {
            vec3 pos = aPosition;
            // Gerstner wave superposition
            float dy = 0.0;
            vec3 normal = vec3(0.0, 0.0, 1.0);  // SL Z-up
            for (int i = 0; i < 4; i++) {
                float d = dot(uDirection[i], pos.xy);
                float w = uFrequency[i];
                float p = uPhase[i];
                float a = uAmplitude[i];
                dy += a * sin(w * d + uTime * p);
                float deriv = a * w * cos(w * d + uTime * p);
                normal.x -= deriv * uDirection[i].x;
                normal.y -= deriv * uDirection[i].y;
            }
            pos.z += dy;
            normal = normalize(normal);

            vec4 worldPos = uModelMatrix * vec4(pos, 1.0);
            vWorldPos = worldPos.xyz;
            vNormal = normalize(mat3(uModelMatrix) * normal);
            gl_Position = uProjection * uView * worldPos;
        }
    """.trimIndent()

    override val fragmentSource = """
        #version 320 es
        precision mediump float;

        in vec3 vWorldPos;
        in vec3 vNormal;

        uniform vec4 uColor;
        uniform vec3 uSunDirF;
        uniform vec3 uCameraPosF;

        out vec4 fragColor;

        void main() {
            vec3 N = normalize(vNormal);
            vec3 L = normalize(uSunDirF);
            vec3 V = normalize(uCameraPosF - vWorldPos);
            vec3 H = normalize(L + V);

            float NdotL = max(dot(N, L), 0.0);
            float NdotH = pow(max(dot(N, H), 0.0), 64.0);

            vec3 waterBase = uColor.rgb;
            vec3 diffuse = waterBase * (0.3 + 0.7 * NdotL);
            vec3 specular = vec3(1.0) * NdotH * 0.6;

            // Fresnel-like rim
            float fresnel = pow(1.0 - max(dot(N, V), 0.0), 3.0) * 0.4;

            vec3 color = diffuse + specular + vec3(fresnel);
            fragColor = vec4(color, uColor.a);
        }
    """.trimIndent()

    override fun onBind() {
        uModelMatrix = loc("uModelMatrix")
        uColor = loc("uColor")
        uTime = loc("uTime")
        uFrequency = loc("uFrequency")
        uAmplitude = loc("uAmplitude")
        uPhase = loc("uPhase")
        uDirection = loc("uDirection")
        uSunDir = loc("uSunDirF")
        uCameraPos = loc("uCameraPosF")

        val g = uboIndex("GlobalData")
        if (g != GLES32.GL_INVALID_INDEX) GLES32.glUniformBlockBinding(handle, g, 0)
    }

    fun setModelMatrix(m: FloatArray) = GLES32.glUniformMatrix4fv(uModelMatrix, 1, false, m, 0)
    fun setColor(r: Float, g: Float, b: Float, a: Float) = GLES32.glUniform4f(uColor, r, g, b, a)
    fun setTime(t: Float) = GLES32.glUniform1f(uTime, t)
    fun setSunDirection(x: Float, y: Float, z: Float) = GLES32.glUniform3f(uSunDir, x, y, z)
    fun setCameraPosition(x: Float, y: Float, z: Float) = GLES32.glUniform3f(uCameraPos, x, y, z)

    fun setWaveParams(
        frequencies: FloatArray,
        amplitudes: FloatArray,
        phases: FloatArray,
        directions: FloatArray   // [dx0,dy0, dx1,dy1, dx2,dy2, dx3,dy3]
    ) {
        GLES32.glUniform1fv(uFrequency, 4, frequencies, 0)
        GLES32.glUniform1fv(uAmplitude, 4, amplitudes, 0)
        GLES32.glUniform1fv(uPhase, 4, phases, 0)
        GLES32.glUniform2fv(uDirection, 4, directions, 0)
    }
}
