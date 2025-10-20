package com.babylonkt.core

import com.babylonkt.math.Vector3
import com.babylonkt.math.Matrix4x4
import com.babylonkt.math.Color3
import com.babylonkt.math.Color4

/**
 * Base class for all materials.
 */
abstract class Material(val name: String) {
    
    // ==================== Material Properties ====================
    
    var alpha = 1f
    var backFaceCulling = true
    var wireframe = false
    var isTransparent = false
    var alphaMode = AlphaMode.COMBINE
    
    // Rendering state
    var depthWrite = true
    var depthTest = true
    
    // ==================== Shader Management ====================
    
    protected var shader: Shader? = null
    protected val uniforms = mutableMapOf<String, Any>()
    
    /**
     * Binds this material for rendering.
     */
    abstract fun bind()
    
    /**
     * Unbinds this material after rendering.
     */
    open fun unbind() {
        // Override in subclasses if needed
    }
    
    /**
     * Sets a uniform value in the shader.
     */
    fun setFloat(name: String, value: Float) {
        uniforms[name] = value
        shader?.setFloat(name, value)
    }
    
    fun setInt(name: String, value: Int) {
        uniforms[name] = value
        shader?.setInt(name, value)
    }
    
    fun setVector3(name: String, value: Vector3) {
        uniforms[name] = value
        shader?.setVector3(name, value)
    }
    
    fun setColor3(name: String, value: Color3) {
        uniforms[name] = value
        shader?.setColor3(name, value)
    }
    
    fun setColor4(name: String, value: Color4) {
        uniforms[name] = value
        shader?.setColor4(name, value)
    }
    
    fun setMatrix(name: String, value: Matrix4x4) {
        uniforms[name] = value
        shader?.setMatrix(name, value)
    }
    
    fun setTexture(name: String, texture: Texture) {
        uniforms[name] = texture
        shader?.setTexture(name, texture)
    }
    
    /**
     * Disposes of this material and its resources.
     */
    open fun dispose() {
        shader?.dispose()
    }
    
    override fun toString(): String {
        return "Material(name='$name', alpha=$alpha)"
    }
}

// ==================== Standard Material ====================

/**
 * Standard material using Phong/Blinn-Phong lighting model.
 */
class StandardMaterial(name: String, val scene: Scene) : Material(name) {
    
    // ==================== Color Properties ====================
    
    var diffuseColor = Color3(1f, 1f, 1f)
    var specularColor = Color3(1f, 1f, 1f)
    var emissiveColor = Color3(0f, 0f, 0f)
    var ambientColor = Color3(0f, 0f, 0f)
    
    var specularPower = 64f
    
    // ==================== Texture Properties ====================
    
    var diffuseTexture: Texture? = null
    var specularTexture: Texture? = null
    var emissiveTexture: Texture? = null
    var ambientTexture: Texture? = null
    var bumpTexture: Texture? = null
    var opacityTexture: Texture? = null
    
    // ==================== Rendering ====================
    
    override fun bind() {
        // Set material properties
        setColor3("diffuseColor", diffuseColor)
        setColor3("specularColor", specularColor)
        setColor3("emissiveColor", emissiveColor)
        setColor3("ambientColor", ambientColor)
        setFloat("specularPower", specularPower)
        setFloat("alpha", alpha)
        
        // Bind textures
        diffuseTexture?.let { setTexture("diffuseTexture", it) }
        specularTexture?.let { setTexture("specularTexture", it) }
        emissiveTexture?.let { setTexture("emissiveTexture", it) }
        ambientTexture?.let { setTexture("ambientTexture", it) }
        bumpTexture?.let { setTexture("bumpTexture", it) }
        opacityTexture?.let { setTexture("opacityTexture", it) }
    }
}

// ==================== PBR Material ====================

/**
 * Physically Based Rendering (PBR) material.
 */
class PBRMaterial(name: String, val scene: Scene) : Material(name) {
    
    // ==================== PBR Properties ====================
    
    var albedoColor = Color3(1f, 1f, 1f)
    var metallic = 0f
    var roughness = 1f
    var emissiveColor = Color3(0f, 0f, 0f)
    var emissiveIntensity = 1f
    
    // Index of refraction
    var indexOfRefraction = 1.5f
    
    // ==================== Texture Properties ====================
    
    var albedoTexture: Texture? = null
    var metallicTexture: Texture? = null
    var roughnessTexture: Texture? = null
    var metallicRoughnessTexture: Texture? = null // Combined metallic (B) and roughness (G)
    var normalTexture: Texture? = null
    var emissiveTexture: Texture? = null
    var ambientOcclusionTexture: Texture? = null
    var environmentTexture: Texture? = null
    
    // ==================== Advanced Properties ====================
    
    var useMetallicRoughnessWorkflow = true
    var useSpecularGlossinessWorkflow = false
    
    // Specular-Glossiness workflow (alternative to metallic-roughness)
    var specularColor = Color3(1f, 1f, 1f)
    var glossiness = 1f
    var specularTexture: Texture? = null
    var glossinessTexture: Texture? = null
    
    // Clear coat
    var clearCoat = 0f
    var clearCoatRoughness = 0f
    var clearCoatTexture: Texture? = null
    
    // Sheen (fabric-like appearance)
    var sheen = 0f
    var sheenColor = Color3(1f, 1f, 1f)
    var sheenTexture: Texture? = null
    
    // Subsurface scattering
    var subsurface = 0f
    var subsurfaceColor = Color3(1f, 1f, 1f)
    
    // Anisotropy
    var anisotropy = 0f
    var anisotropyAngle = 0f
    
    override fun bind() {
        // Set PBR properties
        setColor3("albedoColor", albedoColor)
        setFloat("metallic", metallic)
        setFloat("roughness", roughness)
        setColor3("emissiveColor", emissiveColor)
        setFloat("emissiveIntensity", emissiveIntensity)
        setFloat("alpha", alpha)
        
        // Bind textures
        albedoTexture?.let { setTexture("albedoTexture", it) }
        metallicTexture?.let { setTexture("metallicTexture", it) }
        roughnessTexture?.let { setTexture("roughnessTexture", it) }
        metallicRoughnessTexture?.let { setTexture("metallicRoughnessTexture", it) }
        normalTexture?.let { setTexture("normalTexture", it) }
        emissiveTexture?.let { setTexture("emissiveTexture", it) }
        ambientOcclusionTexture?.let { setTexture("ambientOcclusionTexture", it) }
        environmentTexture?.let { setTexture("environmentTexture", it) }
        
        // Advanced features
        if (clearCoat > 0f) {
            setFloat("clearCoat", clearCoat)
            setFloat("clearCoatRoughness", clearCoatRoughness)
            clearCoatTexture?.let { setTexture("clearCoatTexture", it) }
        }
        
        if (sheen > 0f) {
            setFloat("sheen", sheen)
            setColor3("sheenColor", sheenColor)
            sheenTexture?.let { setTexture("sheenTexture", it) }
        }
    }
}

// ==================== Shader Material ====================

/**
 * Material with custom shaders.
 */
class ShaderMaterial(
    name: String,
    val scene: Scene,
    val vertexShaderCode: String,
    val fragmentShaderCode: String
) : Material(name) {
    
    init {
        // Compile shaders
        shader = scene.engine.getGraphicsDevice().createShader(vertexShaderCode, fragmentShaderCode)
    }
    
    override fun bind() {
        shader?.bind()
        
        // Set all uniforms
        uniforms.forEach { (name, value) ->
            when (value) {
                is Float -> shader?.setFloat(name, value)
                is Int -> shader?.setInt(name, value)
                is Vector3 -> shader?.setVector3(name, value)
                is Color3 -> shader?.setColor3(name, value)
                is Color4 -> shader?.setColor4(name, value)
                is Matrix4x4 -> shader?.setMatrix(name, value)
                is Texture -> shader?.setTexture(name, value)
            }
        }
    }
}

// ==================== Node Material ====================

/**
 * Material created using a visual node editor.
 */
class NodeMaterial(name: String, val scene: Scene) : Material(name) {
    
    private val nodes = mutableListOf<MaterialNode>()
    private val connections = mutableListOf<NodeConnection>()
    
    /**
     * Adds a node to the material graph.
     */
    fun addNode(node: MaterialNode) {
        nodes.add(node)
    }
    
    /**
     * Connects two nodes together.
     */
    fun connect(
        sourceNode: MaterialNode,
        sourceOutput: String,
        targetNode: MaterialNode,
        targetInput: String
    ) {
        connections.add(NodeConnection(sourceNode, sourceOutput, targetNode, targetInput))
    }
    
    /**
     * Builds the shader from the node graph.
     */
    fun build() {
        // Generate shader code from node graph
        val vertexCode = generateVertexShader()
        val fragmentCode = generateFragmentShader()
        
        shader = scene.engine.getGraphicsDevice().createShader(vertexCode, fragmentCode)
    }
    
    private fun generateVertexShader(): String {
        // Simplified - real implementation would traverse node graph
        return """
            #version 330 core
            layout(location = 0) in vec3 position;
            layout(location = 1) in vec3 normal;
            layout(location = 2) in vec2 uv;
            
            uniform mat4 worldMatrix;
            uniform mat4 viewMatrix;
            uniform mat4 projectionMatrix;
            
            out vec3 vPosition;
            out vec3 vNormal;
            out vec2 vUV;
            
            void main() {
                vec4 worldPos = worldMatrix * vec4(position, 1.0);
                vPosition = worldPos.xyz;
                vNormal = mat3(worldMatrix) * normal;
                vUV = uv;
                gl_Position = projectionMatrix * viewMatrix * worldPos;
            }
        """.trimIndent()
    }
    
    private fun generateFragmentShader(): String {
        // Simplified - real implementation would traverse node graph
        return """
            #version 330 core
            in vec3 vPosition;
            in vec3 vNormal;
            in vec2 vUV;
            
            out vec4 fragColor;
            
            void main() {
                fragColor = vec4(vNormal * 0.5 + 0.5, 1.0);
            }
        """.trimIndent()
    }
    
    override fun bind() {
        shader?.bind()
    }
}

// ==================== Supporting Classes ====================

/**
 * Alpha blending modes.
 */
enum class AlphaMode {
    DISABLE,
    ADD,
    COMBINE,
    SUBTRACT,
    MULTIPLY,
    MAXIMIZED,
    ONEONE,
    PREMULTIPLIED
}

/**
 * Base class for material nodes.
 */
abstract class MaterialNode(val name: String) {
    abstract val inputs: List<String>
    abstract val outputs: List<String>
}

/**
 * Connection between two material nodes.
 */
data class NodeConnection(
    val sourceNode: MaterialNode,
    val sourceOutput: String,
    val targetNode: MaterialNode,
    val targetInput: String
)

// ==================== Shader Interface ====================

/**
 * Interface for shader programs.
 */
interface Shader {
    fun bind()
    fun unbind()
    fun setFloat(name: String, value: Float)
    fun setInt(name: String, value: Int)
    fun setVector3(name: String, value: Vector3)
    fun setColor3(name: String, value: Color3)
    fun setColor4(name: String, value: Color4)
    fun setMatrix(name: String, value: Matrix4x4)
    fun setTexture(name: String, texture: Texture)
    fun dispose()
}

// ==================== Texture Class ====================

/**
 * Represents a texture.
 */
class Texture(val url: String, val scene: Scene) {
    var isReady = false
    var width = 0
    var height = 0
    
    // Texture properties
    var wrapU = TextureWrapMode.REPEAT
    var wrapV = TextureWrapMode.REPEAT
    var minFilter = TextureFilterMode.LINEAR_MIPMAP_LINEAR
    var magFilter = TextureFilterMode.LINEAR
    var generateMipmaps = true
    
    fun dispose() {
        // Release GPU resources
    }
}

enum class TextureWrapMode {
    REPEAT,
    CLAMP_TO_EDGE,
    MIRRORED_REPEAT
}

enum class TextureFilterMode {
    NEAREST,
    LINEAR,
    NEAREST_MIPMAP_NEAREST,
    LINEAR_MIPMAP_NEAREST,
    NEAREST_MIPMAP_LINEAR,
    LINEAR_MIPMAP_LINEAR
}

// ==================== Light Classes ====================

abstract class Light(val name: String, var scene: Scene? = null) {
    var position = Vector3.Zero()
    var direction = Vector3.Down()
    var diffuse = Color3(1f, 1f, 1f)
    var specular = Color3(1f, 1f, 1f)
    var intensity = 1f
    var range = Float.MAX_VALUE
}

class HemisphericLight(name: String, direction: Vector3, scene: Scene? = null) : Light(name, scene) {
    init {
        this.direction = direction
    }
    
    var groundColor = Color3(0f, 0f, 0f)
}

class PointLight(name: String, position: Vector3, scene: Scene? = null) : Light(name, scene) {
    init {
        this.position = position
    }
}

class DirectionalLight(name: String, direction: Vector3, scene: Scene? = null) : Light(name, scene) {
    init {
        this.direction = direction
    }
}

class SpotLight(
    name: String,
    position: Vector3,
    direction: Vector3,
    var angle: Float,
    var exponent: Float,
    scene: Scene? = null
) : Light(name, scene) {
    init {
        this.position = position
        this.direction = direction
    }
}