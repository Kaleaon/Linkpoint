package com.linkpoint.render.lumiya.shaders

import android.opengl.GLES32

/**
 * Shader program for rendering Second Life primitives (prims) and glTF 2.0 PBR materials.
 *
 * Supports:
 *  - 5-channel glTF 2.0 PBR material inputs:
 *      0: Base Color Map (uBaseColorMap / uTexture)
 *      1: Normal Map (uNormalMap)
 *      2: Metallic-Roughness Map (uMetallicRoughnessMap)
 *      3: Emissive Map (uEmissiveMap)
 *      4: Occlusion Map (uOcclusionMap)
 *  - Cook-Torrance microfacet BRDF specularity (GGX D, Schlick F, Smith-GGX G)
 *  - Automatic tangent-space normal mapping via screen-space derivatives
 *  - Default vector fallbacks for missing material channels
 *  - OpenGL ES 2.0 diffuse fallback execution path
 *  - Shared global UBO (binding 0) for projection/view/camera
 */
class PrimShaderProgram : BaseShaderProgram() {

    // Uniform locations
    private var uModelMatrix = -1
    private var uTexMatrix = -1
    private var uColor = -1
    private var uUseTexture = -1
    private var uLightDir = -1
    private var uLightDiffuse = -1
    private var uLightAmbient = -1

    // 5-channel PBR sampler uniforms
    private var uBaseColorMapSampler = -1
    private var uNormalMapSampler = -1
    private var uMetallicRoughnessMapSampler = -1
    private var uEmissiveMapSampler = -1
    private var uOcclusionMapSampler = -1

    // Channel availability flags
    private var uHasBaseColorMap = -1
    private var uHasNormalMap = -1
    private var uHasMetallicRoughnessMap = -1
    private var uHasEmissiveMap = -1
    private var uHasOcclusionMap = -1
    private var uIsGles20Fallback = -1

    // Factor uniforms
    private var uMetallicFactor = -1
    private var uRoughnessFactor = -1
    private var uEmissiveFactor = -1
    private var uOcclusionFactor = -1
    private var uNormalScale = -1

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
        layout(location = 1) in vec3 aNormal;
        layout(location = 2) in vec2 aTexCoord;

        uniform mat4 uModelMatrix;
        uniform mat4 uTexMatrix;

        out vec3 vWorldPos;
        out vec3 vNormal;
        out vec2 vTexCoord;
        out float vFogFactor;

        void main() {
            vec4 worldPos = uModelMatrix * vec4(aPosition, 1.0);
            vWorldPos = worldPos.xyz;
            vNormal = normalize(mat3(uModelMatrix) * aNormal);
            vTexCoord = (uTexMatrix * vec4(aTexCoord, 0.0, 1.0)).xy;

            float dist = length(uCameraPos.xyz - worldPos.xyz);
            vFogFactor = clamp(dist / 256.0, 0.0, 1.0);

            gl_Position = uProjection * uView * worldPos;
        }
    """.trimIndent()

    override val fragmentSource = """
        #version 320 es
        precision mediump float;

        layout(std140, binding = 0) uniform GlobalData {
            mat4 uProjection;
            mat4 uView;
            mat4 _pad_model;
            vec4 uCameraPos;
            vec4 uSunDir;
        };

        in vec3 vWorldPos;
        in vec3 vNormal;
        in vec2 vTexCoord;
        in float vFogFactor;

        // 5-channel PBR Samplers
        uniform sampler2D uBaseColorMap;
        uniform sampler2D uNormalMap;
        uniform sampler2D uMetallicRoughnessMap;
        uniform sampler2D uEmissiveMap;
        uniform sampler2D uOcclusionMap;

        // Active channel flags
        uniform int uHasBaseColorMap;
        uniform int uHasNormalMap;
        uniform int uHasMetallicRoughnessMap;
        uniform int uHasEmissiveMap;
        uniform int uHasOcclusionMap;
        uniform int uIsGles20Fallback;

        // Uniform factors & tints
        uniform vec4 uColor;
        uniform vec3 uLightDir;
        uniform vec3 uLightDiffuse;
        uniform vec3 uLightAmbient;
        uniform float uMetallicFactor;
        uniform float uRoughnessFactor;
        uniform vec3 uEmissiveFactor;
        uniform float uOcclusionFactor;
        uniform float uNormalScale;

        out vec4 fragColor;

        const float PI = 3.14159265359;

        // Construct Tangent-Bitangent-Normal frame using screen space derivatives
        mat3 getTBN(vec3 N, vec3 p, vec2 uv) {
            vec3 dp1 = dFdx(p);
            vec3 dp2 = dFdy(p);
            vec2 duv1 = dFdx(uv);
            vec2 duv2 = dFdy(uv);

            vec3 dp2perp = cross(dp2, N);
            vec3 dp1perp = cross(N, dp1);
            vec3 T = dp2perp * duv1.x + dp1perp * duv2.x;
            vec3 B = dp2perp * duv1.y + dp1perp * duv2.y;

            float invmax = inversesqrt(max(dot(T,T), dot(B,B)));
            if (isinf(invmax) || isnan(invmax)) {
                T = vec3(1.0, 0.0, 0.0);
                B = vec3(0.0, 1.0, 0.0);
                return mat3(T, B, N);
            }
            return mat3(T * invmax, B * invmax, N);
        }

        // Cook-Torrance BRDF GGX Distribution
        float distributionGGX(vec3 N, vec3 H, float roughness) {
            float a = roughness * roughness;
            float a2 = a * a;
            float NdotH = max(dot(N, H), 0.0);
            float NdotH2 = NdotH * NdotH;

            float num = a2;
            float denom = (NdotH2 * (a2 - 1.0) + 1.0);
            denom = PI * denom * denom;

            return num / max(denom, 0.0001);
        }

        // Schlick-GGX Geometry function
        float geometrySchlickGGX(float NdotV, float roughness) {
            float r = (roughness + 1.0);
            float k = (r * r) / 8.0;

            float num = NdotV;
            float denom = NdotV * (1.0 - k) + k;

            return num / max(denom, 0.0001);
        }

        float geometrySmith(vec3 N, vec3 V, vec3 L, float roughness) {
            float NdotV = max(dot(N, V), 0.0);
            float NdotL = max(dot(N, L), 0.0);
            float ggx2 = geometrySchlickGGX(NdotV, roughness);
            float ggx1 = geometrySchlickGGX(NdotL, roughness);

            return ggx1 * ggx2;
        }

        // Schlick Fresnel approximation
        vec3 fresnelSchlick(float cosTheta, vec3 F0) {
            return F0 + (1.0 - F0) * pow(clamp(1.0 - cosTheta, 0.0, 1.0), 5.0);
        }

        void main() {
            // Channel 0: Base Color
            vec4 baseColor = uColor;
            if (uHasBaseColorMap != 0) {
                baseColor *= texture(uBaseColorMap, vTexCoord);
            }

            // Alpha discard
            if (baseColor.a < 0.004) discard;

            // Compute geometric normal or normal map
            vec3 N = normalize(vNormal);
            if (uHasNormalMap != 0) {
                vec3 mapNormal = texture(uNormalMap, vTexCoord).xyz * 2.0 - 1.0;
                mapNormal.xy *= uNormalScale;
                mat3 TBN = getTBN(N, vWorldPos, vTexCoord);
                N = normalize(TBN * mapNormal);
            }

            vec3 L = normalize(uLightDir);
            float NdotL = max(dot(N, L), 0.0);

            // Legacy ES 2.0 Fallback path: simple Lambertian shading
            if (uIsGles20Fallback != 0) {
                vec3 lit = baseColor.rgb * (uLightAmbient + uLightDiffuse * NdotL);
                vec3 fogColor = vec3(0.24, 0.44, 0.76);
                lit = mix(lit, fogColor, vFogFactor * vFogFactor);
                fragColor = vec4(lit, baseColor.a);
                return;
            }

            // Channel 2: Metallic & Roughness (glTF 2.0: G = Roughness, B = Metallic)
            float metallic = uMetallicFactor;
            float roughness = uRoughnessFactor;
            if (uHasMetallicRoughnessMap != 0) {
                vec4 mrSample = texture(uMetallicRoughnessMap, vTexCoord);
                roughness *= mrSample.g;
                metallic *= mrSample.b;
            }
            roughness = clamp(roughness, 0.04, 1.0);
            metallic = clamp(metallic, 0.0, 1.0);

            // Channel 3: Emissive
            vec3 emissive = uEmissiveFactor;
            if (uHasEmissiveMap != 0) {
                emissive *= texture(uEmissiveMap, vTexCoord).rgb;
            }

            // Channel 4: Occlusion
            float occlusion = 1.0;
            if (uHasOcclusionMap != 0) {
                float occSample = texture(uOcclusionMap, vTexCoord).r;
                occlusion = mix(1.0, occSample, clamp(uOcclusionFactor, 0.0, 1.0));
            }

            vec3 V = normalize(uCameraPos.xyz - vWorldPos);
            vec3 H = normalize(V + L);

            vec3 F0 = vec3(0.04);
            F0 = mix(F0, baseColor.rgb, metallic);

            // Cook-Torrance Specular BRDF
            float NDF = distributionGGX(N, H, roughness);
            float G = geometrySmith(N, V, L, roughness);
            vec3 F = fresnelSchlick(max(dot(H, V), 0.0), F0);

            vec3 numerator = NDF * G * F;
            float denominator = 4.0 * max(dot(N, V), 0.0) * NdotL + 0.0001;
            vec3 specular = numerator / denominator;

            vec3 kS = F;
            vec3 kD = vec3(1.0) - kS;
            kD *= (1.0 - metallic);

            vec3 diffuse = kD * baseColor.rgb / PI;
            vec3 directLighting = (diffuse + specular) * uLightDiffuse * NdotL * PI;
            vec3 ambientLighting = baseColor.rgb * uLightAmbient * occlusion;

            vec3 finalColor = directLighting + ambientLighting + emissive;

            // Fog blending
            vec3 fogColor = vec3(0.24, 0.44, 0.76);
            finalColor = mix(finalColor, fogColor, vFogFactor * vFogFactor);

            fragColor = vec4(finalColor, baseColor.a);
        }
    """.trimIndent()

    override fun onBind() {
        uModelMatrix = loc("uModelMatrix")
        uTexMatrix = loc("uTexMatrix")
        uColor = loc("uColor")
        uUseTexture = loc("uHasBaseColorMap")
        uLightDir = loc("uLightDir")
        uLightDiffuse = loc("uLightDiffuse")
        uLightAmbient = loc("uLightAmbient")

        // 5-channel samplers
        uBaseColorMapSampler = loc("uBaseColorMap")
        uNormalMapSampler = loc("uNormalMap")
        uMetallicRoughnessMapSampler = loc("uMetallicRoughnessMap")
        uEmissiveMapSampler = loc("uEmissiveMap")
        uOcclusionMapSampler = loc("uOcclusionMap")

        // Channel flags
        uHasBaseColorMap = loc("uHasBaseColorMap")
        uHasNormalMap = loc("uHasNormalMap")
        uHasMetallicRoughnessMap = loc("uHasMetallicRoughnessMap")
        uHasEmissiveMap = loc("uHasEmissiveMap")
        uHasOcclusionMap = loc("uHasOcclusionMap")
        uIsGles20Fallback = loc("uIsGles20Fallback")

        // Factors
        uMetallicFactor = loc("uMetallicFactor")
        uRoughnessFactor = loc("uRoughnessFactor")
        uEmissiveFactor = loc("uEmissiveFactor")
        uOcclusionFactor = loc("uOcclusionFactor")
        uNormalScale = loc("uNormalScale")

        // Bind the GlobalData UBO to binding point 0
        val idx = uboIndex("GlobalData")
        if (idx != GLES32.GL_INVALID_INDEX) {
            GLES32.glUniformBlockBinding(handle, idx, 0)
        }
    }

    // ── Uniform setters ──────────────────────────────────────────────────

    fun setModelMatrix(m: FloatArray) = GLES32.glUniformMatrix4fv(uModelMatrix, 1, false, m, 0)
    fun setTexMatrix(m: FloatArray)   = GLES32.glUniformMatrix4fv(uTexMatrix, 1, false, m, 0)
    fun setColor(r: Float, g: Float, b: Float, a: Float) = GLES32.glUniform4f(uColor, r, g, b, a)

    fun setSpecularColor(r: Float, g: Float, b: Float) {}
    fun setSpecularExponent(exp: Float) {}

    fun setUseTexture(use: Boolean) {
        setHasBaseColorMap(use)
    }

    fun setUseNormalMap(use: Boolean) {
        setHasNormalMap(use)
    }

    fun setTextureSampler(unit: Int) {
        setBaseColorMapSampler(unit)
    }

    fun setBaseColorMapSampler(unit: Int) = GLES32.glUniform1i(uBaseColorMapSampler, unit)
    fun setNormalMapSampler(unit: Int) = GLES32.glUniform1i(uNormalMapSampler, unit)
    fun setMetallicRoughnessMapSampler(unit: Int) = GLES32.glUniform1i(uMetallicRoughnessMapSampler, unit)
    fun setEmissiveMapSampler(unit: Int) = GLES32.glUniform1i(uEmissiveMapSampler, unit)
    fun setOcclusionMapSampler(unit: Int) = GLES32.glUniform1i(uOcclusionMapSampler, unit)

    fun setHasBaseColorMap(has: Boolean) = GLES32.glUniform1i(uHasBaseColorMap, if (has) 1 else 0)
    fun setHasNormalMap(has: Boolean) = GLES32.glUniform1i(uHasNormalMap, if (has) 1 else 0)
    fun setHasMetallicRoughnessMap(has: Boolean) = GLES32.glUniform1i(uHasMetallicRoughnessMap, if (has) 1 else 0)
    fun setHasEmissiveMap(has: Boolean) = GLES32.glUniform1i(uHasEmissiveMap, if (has) 1 else 0)
    fun setHasOcclusionMap(has: Boolean) = GLES32.glUniform1i(uHasOcclusionMap, if (has) 1 else 0)
    fun setIsGles20Fallback(fallback: Boolean) = GLES32.glUniform1i(uIsGles20Fallback, if (fallback) 1 else 0)

    fun setMetallicFactor(factor: Float) = GLES32.glUniform1f(uMetallicFactor, factor)
    fun setRoughnessFactor(factor: Float) = GLES32.glUniform1f(uRoughnessFactor, factor)
    fun setEmissiveFactor(r: Float, g: Float, b: Float) = GLES32.glUniform3f(uEmissiveFactor, r, g, b)
    fun setOcclusionFactor(factor: Float) = GLES32.glUniform1f(uOcclusionFactor, factor)
    fun setNormalScale(scale: Float) = GLES32.glUniform1f(uNormalScale, scale)

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
