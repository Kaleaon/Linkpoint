package com.babylonkt.core

import com.babylonkt.math.Vector3
import com.babylonkt.math.Color3
import com.babylonkt.math.Color4
import kotlinx.coroutines.*

/**
 * Represents a 3D scene containing meshes, cameras, lights, and other objects.
 */
class Scene(val engine: Engine) {
    
    // ==================== Scene Graph ====================
    
    private val _meshes = mutableListOf<Mesh>()
    private val _cameras = mutableListOf<Camera>()
    private val _lights = mutableListOf<Light>()
    private val _materials = mutableListOf<Material>()
    private val _textures = mutableListOf<Texture>()
    
    val meshes: List<Mesh> get() = _meshes
    val cameras: List<Camera> get() = _cameras
    val lights: List<Light> get() = _lights
    val materials: List<Material> get() = _materials
    val textures: List<Texture> get() = _textures
    
    // ==================== Scene Properties ====================
    
    var activeCamera: Camera? = null
    var clearColor = Color4(0.2f, 0.2f, 0.3f, 1f)
    var ambientColor = Color3(0.2f, 0.2f, 0.2f)
    var fogEnabled = false
    var fogMode = FogMode.NONE
    var fogColor = Color3(0.5f, 0.5f, 0.5f)
    var fogDensity = 0.1f
    var fogStart = 0f
    var fogEnd = 100f
    
    // ==================== State ====================
    
    var isDisposed = false
        private set
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // ==================== Scene Management ====================
    
    /**
     * Adds a mesh to the scene.
     */
    fun addMesh(mesh: Mesh) {
        if (!_meshes.contains(mesh)) {
            _meshes.add(mesh)
            mesh.scene = this
        }
    }
    
    /**
     * Removes a mesh from the scene.
     */
    fun removeMesh(mesh: Mesh) {
        _meshes.remove(mesh)
    }
    
    /**
     * Adds a camera to the scene.
     */
    fun addCamera(camera: Camera) {
        if (!_cameras.contains(camera)) {
            _cameras.add(camera)
            camera.scene = this
            if (activeCamera == null) {
                activeCamera = camera
            }
        }
    }
    
    /**
     * Removes a camera from the scene.
     */
    fun removeCamera(camera: Camera) {
        _cameras.remove(camera)
        if (activeCamera == camera) {
            activeCamera = _cameras.firstOrNull()
        }
    }
    
    /**
     * Adds a light to the scene.
     */
    fun addLight(light: Light) {
        if (!_lights.contains(light)) {
            _lights.add(light)
            light.scene = this
        }
    }
    
    /**
     * Removes a light from the scene.
     */
    fun removeLight(light: Light) {
        _lights.remove(light)
    }
    
    /**
     * Adds a material to the scene.
     */
    fun addMaterial(material: Material) {
        if (!_materials.contains(material)) {
            _materials.add(material)
        }
    }
    
    /**
     * Adds a texture to the scene.
     */
    fun addTexture(texture: Texture) {
        if (!_textures.contains(texture)) {
            _textures.add(texture)
        }
    }
    
    // ==================== Rendering ====================
    
    /**
     * Renders the scene.
     */
    fun render() {
        if (isDisposed) {
            println("Warning: Cannot render disposed scene")
            return
        }
        
        val camera = activeCamera
        if (camera == null) {
            println("Warning: No active camera in scene")
            return
        }
        
        // Clear the screen
        engine.clear(clearColor.r, clearColor.g, clearColor.b, clearColor.a)
        
        // Update camera matrices
        camera.update()
        
        // Frustum culling
        val visibleMeshes = _meshes.filter { mesh ->
            mesh.isVisible && isInFrustum(mesh, camera)
        }
        
        // Sort meshes by rendering order
        val sortedMeshes = visibleMeshes.sortedWith(
            compareBy<Mesh> { it.renderingGroupId }
                .thenBy { it.material?.isTransparent ?: false }
                .thenBy { -it.position.distanceTo(camera.position) }
        )
        
        // Render each mesh
        sortedMeshes.forEach { mesh ->
            renderMesh(mesh, camera)
        }
    }
    
    private fun renderMesh(mesh: Mesh, camera: Camera) {
        val material = mesh.material ?: return
        
        // Bind material
        material.bind()
        
        // Set transformation matrices
        material.setMatrix("worldMatrix", mesh.getWorldMatrix())
        material.setMatrix("viewMatrix", camera.getViewMatrix())
        material.setMatrix("projectionMatrix", camera.getProjectionMatrix())
        
        // Set lighting uniforms
        setLightingUniforms(material)
        
        // Draw the mesh
        mesh.draw()
    }
    
    private fun setLightingUniforms(material: Material) {
        // Set ambient color
        material.setColor3("ambientColor", ambientColor)
        
        // Set light data (simplified - would be more complex in real implementation)
        _lights.forEachIndexed { index, light ->
            if (index < 4) { // Limit to 4 lights for simplicity
                material.setVector3("lightPosition$index", light.position)
                material.setColor3("lightColor$index", light.diffuse)
                material.setFloat("lightIntensity$index", light.intensity)
            }
        }
        material.setInt("lightCount", _lights.size.coerceAtMost(4))
    }
    
    private fun isInFrustum(mesh: Mesh, camera: Camera): Boolean {
        // Simplified frustum culling - would use proper frustum planes in real implementation
        val distance = mesh.position.distanceTo(camera.position)
        return distance < 1000f // Simple distance check
    }
    
    // ==================== Picking ====================
    
    /**
     * Performs ray picking at the specified screen coordinates.
     */
    fun pick(x: Int, y: Int): PickingInfo? {
        val camera = activeCamera ?: return null
        
        // Convert screen coordinates to world ray
        val ray = camera.getPickingRay(x, y, engine.width, engine.height)
        
        // Test ray against all meshes
        var closestHit: PickingInfo? = null
        var closestDistance = Float.MAX_VALUE
        
        _meshes.forEach { mesh ->
            if (mesh.isPickable) {
                val hit = mesh.intersects(ray)
                if (hit != null && hit.distance < closestDistance) {
                    closestDistance = hit.distance
                    closestHit = hit
                }
            }
        }
        
        return closestHit
    }
    
    // ==================== Asset Loading ====================
    
    /**
     * Loads a 3D model asynchronously.
     */
    suspend fun loadModelAsync(url: String): Mesh = withContext(Dispatchers.IO) {
        println("Loading model from: $url")
        
        // Download and parse the model file
        // This is a placeholder - real implementation would use proper loaders
        delay(100) // Simulate loading time
        
        val mesh = Mesh("loadedModel", this@Scene)
        addMesh(mesh)
        
        println("Model loaded successfully")
        mesh
    }
    
    /**
     * Loads a texture asynchronously.
     */
    suspend fun loadTextureAsync(url: String): Texture = withContext(Dispatchers.IO) {
        println("Loading texture from: $url")
        
        // Download and create texture
        delay(50) // Simulate loading time
        
        val texture = Texture(url, this@Scene)
        addTexture(texture)
        
        println("Texture loaded successfully")
        texture
    }
    
    // ==================== Scene Queries ====================
    
    /**
     * Finds a mesh by name.
     */
    fun getMeshByName(name: String): Mesh? {
        return _meshes.find { it.name == name }
    }
    
    /**
     * Finds all meshes with a specific tag.
     */
    fun getMeshesByTag(tag: String): List<Mesh> {
        return _meshes.filter { it.tags.contains(tag) }
    }
    
    /**
     * Finds a camera by name.
     */
    fun getCameraByName(name: String): Camera? {
        return _cameras.find { it.name == name }
    }
    
    /**
     * Finds a light by name.
     */
    fun getLightByName(name: String): Light? {
        return _lights.find { it.name == name }
    }
    
    // ==================== Disposal ====================
    
    /**
     * Disposes of the scene and all its resources.
     */
    fun dispose() {
        if (isDisposed) return
        
        println("Disposing scene...")
        
        // Dispose all meshes
        _meshes.forEach { it.dispose() }
        _meshes.clear()
        
        // Dispose all materials
        _materials.forEach { it.dispose() }
        _materials.clear()
        
        // Dispose all textures
        _textures.forEach { it.dispose() }
        _textures.clear()
        
        _cameras.clear()
        _lights.clear()
        
        scope.cancel()
        
        isDisposed = true
        println("Scene disposed")
    }
    
    override fun toString(): String {
        return "Scene(meshes=${_meshes.size}, cameras=${_cameras.size}, lights=${_lights.size})"
    }
}

// ==================== Supporting Classes ====================

/**
 * Information about a ray-mesh intersection.
 */
data class PickingInfo(
    val hit: Boolean,
    val distance: Float,
    val pickedMesh: Mesh?,
    val pickedPoint: Vector3?,
    val ray: Ray
)

/**
 * Represents a ray for picking and collision detection.
 */
data class Ray(
    val origin: Vector3,
    val direction: Vector3
) {
    fun getPoint(distance: Float): Vector3 {
        return origin + direction * distance
    }
}

/**
 * Fog modes for the scene.
 */
enum class FogMode {
    NONE,
    LINEAR,
    EXPONENTIAL,
    EXPONENTIAL_SQUARED
}

// ==================== DSL Extensions ====================

/**
 * DSL function for creating a scene.
 */
fun scene(engine: Engine, block: Scene.() -> Unit): Scene {
    return Scene(engine).apply(block)
}

/**
 * DSL function for adding a mesh to a scene.
 */
fun Scene.mesh(name: String, block: Mesh.() -> Unit): Mesh {
    val mesh = Mesh(name, this)
    addMesh(mesh)
    return mesh.apply(block)
}

/**
 * DSL function for adding a camera to a scene.
 */
fun Scene.freeCamera(name: String, position: Vector3, block: FreeCamera.() -> Unit = {}): FreeCamera {
    val camera = FreeCamera(name, position, this)
    addCamera(camera)
    return camera.apply(block)
}

/**
 * DSL function for adding a light to a scene.
 */
fun Scene.hemisphericLight(name: String, direction: Vector3, block: HemisphericLight.() -> Unit = {}): HemisphericLight {
    val light = HemisphericLight(name, direction, this)
    addLight(light)
    return light.apply(block)
}