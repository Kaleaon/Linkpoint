package com.linkpoint.linden.llrender

data class LLShaderFeatures(
    val atmosphericHelpers: Boolean = false,
    val hasLighting: Boolean = false,
    val hasShadows: Boolean = false,
    val hasSpecular: Boolean = false,
    val hasNormalMap: Boolean = false,
    val hasReflectionProbes: Boolean = false,
    val hasSkinning: Boolean = false,
    val hasMaterials: Boolean = false,
    val isDeferred: Boolean = false,
    val isFullbright: Boolean = false,
    val hasAlpha: Boolean = false,
    val hasWater: Boolean = false,
)

object LLShaderMgr {

    enum class ShaderLevel { NONE, LOW, MEDIUM, HIGH, ULTRA }

    private val shaderCache: MutableMap<String, LLGLSLShader> = mutableMapOf()
    private val featureMap: MutableMap<String, LLShaderFeatures> = mutableMapOf()

    var shaderLevel: ShaderLevel = ShaderLevel.HIGH
    var maxVertexAttribs: Int = 16

    var currentShader: LLGLSLShader? = null
        private set

    fun loadShader(name: String, vertSrc: String, fragSrc: String, features: LLShaderFeatures = LLShaderFeatures()): LLGLSLShader? {
        val existing = shaderCache[name]
        if (existing != null && existing.isLinked) return existing

        val shader = LLGLSLShader(name)
        if (!shader.compile(vertSrc, fragSrc)) {
            return null
        }
        shaderCache[name] = shader
        featureMap[name] = features
        return shader
    }

    fun getShader(name: String): LLGLSLShader? = shaderCache[name]

    fun getFeaturesForShader(name: String): LLShaderFeatures? = featureMap[name]

    fun bindShader(shader: LLGLSLShader?) {
        currentShader?.unbind()
        currentShader = shader
        shader?.bind()
    }

    fun unbindShader() {
        currentShader?.unbind()
        currentShader = null
    }

    fun releaseShader(name: String) {
        val shader = shaderCache.remove(name)
        featureMap.remove(name)
        if (shader == currentShader) currentShader = null
        shader?.destroy()
    }

    fun releaseAll() {
        currentShader = null
        shaderCache.values.forEach { it.destroy() }
        shaderCache.clear()
        featureMap.clear()
    }

    fun getLoadedShaderCount(): Int = shaderCache.size

    fun isSupportedVersion(major: Int, minor: Int): Boolean =
        (major > 3) || (major == 3 && minor >= 3)
}
