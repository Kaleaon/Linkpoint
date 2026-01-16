# 3D Rendering Pipeline Standards for Second Life Viewers

## Overview

This document establishes comprehensive standards for implementing 3D rendering pipelines in Second Life mobile viewers, derived from analysis of the official Second Life Android viewer, Lumiya, and Firestorm viewer implementations.

## Minimum Requirements

### OpenGL ES Requirements

```kotlin>
/**
 * Standard OpenGL ES version requirements
 * Based on Second Life Android Viewer 2025.12.1075
 */
object OpenGLRequirements {
    
    // Minimum version requirement
    const val MIN_OPENGL_ES_VERSION = 0x00030000 // OpenGL ES 3.0
    
    // Preferred version for optimal performance
    const val PREFERRED_OPENGL_ES_VERSION = 0x00030001 // OpenGL ES 3.1
    
    // Required extensions
    val REQUIRED_EXTENSIONS = listOf(
        "GL_OES_EGL_image",           // EGL image support
        "GL_OES_vertex_array_object",  // VAO support
        "GL_EXT_texture_filter_anisotropic" // Anisotropic filtering
    )
    
    // Optional but recommended extensions
    val RECOMMENDED_EXTENSIONS = listOf(
        "GL_EXT_disjoint_timer_query",  // GPU timing queries
        "GL_KHR_debug",                   // Debug output
        "GL_EXT_texture_compression_s3tc" // S3TC texture compression
    )
    
    fun checkCapabilities(context: Context): RendererCapabilities {
        val configInfo = EGL14.eglChooseConfig(
            EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY),
            intArrayOf(
                EGL14.EGL_RENDERABLE_TYPE, EGLExt.EGL_OPENGL_ES3_BIT_KHR,
                EGL14.EGL_RED_SIZE, 8,
                EGL14.EGL_GREEN_SIZE, 8,
                EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_ALPHA_SIZE, 8,
                EGL14.EGL_DEPTH_SIZE, 24,
                EGL14.EGL_STENCIL_SIZE, 8,
                EGL14.EGL_NONE
            ),
            intArrayOf(1),
            intArrayOf(1),
            IntArray(1)
        )
        
        // Check OpenGL ES version
        val version = GLES30.glGetString(GLES30.GL_VERSION)
        val supportsES30 = version.contains("3.0")
        val supportsES31 = version.contains("3.1")
        
        // Check extensions
        val extensions = GLES30.glGetString(GLES30.GL_EXTENSIONS)?.split(" ") ?: emptyList()
        val hasRequiredExtensions = REQUIRED_EXTENSIONS.all { it in extensions }
        
        return RendererCapabilities(
            minVersion = "3.0",
            currentVersion = version,
            supportsES30 = supportsES30,
            supportsES31 = supportsES31,
            hasRequiredExtensions = hasRequiredExtensions,
            availableExtensions = extensions
        )
    }
}

data class RendererCapabilities(
    val minVersion: String,
    val currentVersion: String,
    val supportsES30: Boolean,
    val supportsES31: Boolean,
    val hasRequiredExtensions: Boolean,
    val availableExtensions: List<String>
)
```

## Rendering Pipeline Architecture

### Standard Pipeline Stages

```kotlin>
/**
 * Standard rendering pipeline stages
 * Based on Firestorm viewer and official Second Life implementation
 */
enum class RenderStage {
    PRE_RENDER,           // Pre-render processing
    CULLING,              // Frustum and occlusion culling
    SORTING,              // Depth sorting
    GEOMETRY_PASS,        // Geometry rendering
    LIGHTING_PASS,        // Lighting calculations
    SHADOW_PASS,          // Shadow map generation
    POST_PROCESSING,      // Post-processing effects
    PRESENTATION          // Final presentation
}

/**
 * Standard rendering pipeline implementation
 */
class RenderingPipeline(
    private val context: Context,
    private val width: Int,
    private val height: Int
) {
    
    private val capabilities = OpenGLRequirements.checkCapabilities(context)
    private val renderQueue = PriorityQueue<RenderCommand>(
        compareBy { it.priority }
    )
    
    private val framebuffers = mutableMapOf<String, Framebuffer>()
    private val shaders = mutableMapOf<String, ShaderProgram>()
    
    fun render(scene: Scene) {
        executeStage(RenderStage.PRE_RENDER) {
            preprocessScene(scene)
        }
        
        executeStage(RenderStage.CULLING) {
            cullObjects(scene)
        }
        
        executeStage(RenderStage.SORTING) {
            sortRenderQueue()
        }
        
        executeStage(RenderStage.GEOMETRY_PASS) {
            renderGeometry(scene)
        }
        
        executeStage(RenderStage.LIGHTING_PASS) {
            applyLighting(scene)
        }
        
        executeStage(RenderStage.SHADOW_PASS) {
            renderShadows(scene)
        }
        
        executeStage(RenderStage.POST_PROCESSING) {
            applyPostProcessing()
        }
        
        executeStage(RenderStage.PRESENTATION) {
            presentFrame()
        }
    }
    
    private inline fun executeStage(stage: RenderStage, action: () -> Unit) {
        val startTime = System.nanoTime()
        action()
        val duration = System.nanoTime() - startTime
        // Log timing for performance monitoring
    }
}
```

## Shader Management Standards

### Standard Shader Structure

```kotlin>
/**
 * Standard vertex shader template
 * Used for most objects in the scene
 */
const val STANDARD_VERTEX_SHADER = """
#version 300 es
precision highp float;

// Input attributes
layout(location = 0) in vec3 a_position;
layout(location = 1) in vec3 a_normal;
layout(location = 2) in vec2 a_texCoord;
layout(location = 3) in vec4 a_tangent;

// Output to fragment shader
out vec3 v_position;
out vec3 v_normal;
out vec2 v_texCoord;
out vec4 v_tangent;

// Uniform matrices
uniform mat4 u_modelMatrix;
uniform mat4 u_viewMatrix;
uniform mat4 u_projectionMatrix;
uniform mat3 u_normalMatrix;

void main() {
    vec4 worldPosition = u_modelMatrix * vec4(a_position, 1.0);
    v_position = worldPosition.xyz;
    v_normal = normalize(u_normalMatrix * a_normal);
    v_texCoord = a_texCoord;
    v_tangent = a_tangent;
    
    gl_Position = u_projectionMatrix * u_viewMatrix * worldPosition;
}
"""

/**
 * Standard fragment shader template
 * Supports basic lighting and texturing
 */
const val STANDARD_FRAGMENT_SHADER = """
#version 300 es
precision highp float;

// Input from vertex shader
in vec3 v_position;
in vec3 v_normal;
in vec2 v_texCoord;
in vec4 v_tangent;

// Output color
layout(location = 0) out vec4 fragColor;

// Material properties
uniform vec4 u_baseColor;
uniform sampler2D u_texture;
uniform float u_metallic;
uniform float u_roughness;
uniform vec3 u_emissive;

// Lighting
uniform vec3 u_lightPositions[4];
uniform vec3 u_lightColors[4];
uniform vec3 u_cameraPosition;

void main() {
    // Sample texture
    vec4 textureColor = texture(u_texture, v_texCoord);
    
    // Calculate lighting
    vec3 N = normalize(v_normal);
    vec3 V = normalize(u_cameraPosition - v_position);
    
    vec3 Lo = vec3(0.0);
    for (int i = 0; i < 4; i++) {
        vec3 L = normalize(u_lightPositions[i] - v_position);
        vec3 H = normalize(L + V);
        
        // Diffuse
        float NdotL = max(dot(N, L), 0.0);
        vec3 diffuse = u_lightColors[i] * NdotL;
        
        // Specular (simplified)
        float NdotH = max(dot(N, H), 0.0);
        vec3 specular = u_lightColors[i] * pow(NdotH, (1.0 - u_roughness) * 32.0);
        
        Lo += diffuse + specular;
    }
    
    // Combine with material
    vec3 color = u_baseColor.rgb * textureColor.rgb + u_emissive;
    color = color * Lo;
    
    fragColor = vec4(color, u_baseColor.a * textureColor.a);
}
"""

/**
 * Standard shader compiler
 */
class ShaderCompiler {
    
    fun compileVertexShader(source: String): Int {
        return GLES30.glCreateShader(GLES30.GL_VERTEX_SHADER).also { shader ->
            GLES30.glShaderSource(shader, source)
            GLES30.glCompileShader(shader)
            
            val compileStatus = IntArray(1)
            GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compileStatus, 0)
            
            if (compileStatus[0] == GLES30.GL_FALSE) {
                val errorLog = GLES30.glGetShaderInfoLog(shader)
                throw ShaderCompilationException("Vertex shader compilation failed: $errorLog")
            }
        }
    }
    
    fun compileFragmentShader(source: String): Int {
        return GLES30.glCreateShader(GLES30.GL_FRAGMENT_SHADER).also { shader ->
            GLES30.glShaderSource(shader, source)
            GLES30.glCompileShader(shader)
            
            val compileStatus = IntArray(1)
            GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compileStatus, 0)
            
            if (compileStatus[0] == GLES30.GL_FALSE) {
                val errorLog = GLES30.glGetShaderInfoLog(shader)
                throw ShaderCompilationException("Fragment shader compilation failed: $errorLog")
            }
        }
    }
    
    fun createProgram(vertexShader: Int, fragmentShader: Int): Int {
        val program = GLES30.glCreateProgram().also {
            GLES30.glAttachShader(it, vertexShader)
            GLES30.glAttachShader(it, fragmentShader)
            GLES30.glLinkProgram(it)
            
            val linkStatus = IntArray(1)
            GLES30.glGetProgramiv(it, GLES30.GL_LINK_STATUS, linkStatus, 0)
            
            if (linkStatus[0] == GLES30.GL_FALSE) {
                val errorLog = GLES30.glGetProgramInfoLog(it)
                GLES30.glDeleteProgram(it)
                throw ShaderCompilationException("Shader program linking failed: $errorLog")
            }
        }
        
        return program
    }
}

class ShaderCompilationException(message: String) : Exception(message)
```

## Camera Standards

### Camera Configuration

```kotlin>
/**
 * Standard camera configuration
 * Based on Second Life viewer camera system
 */
class Camera(
    private val fov: Float = 60f,
    private val nearPlane: Float = 0.1f,
    private val farPlane: Float = 1000f
) {
    
    var position = Vector3f(0f, 0f, 2f)
    var target = Vector3f(0f, 0f, 0f)
    var up = Vector3f(0f, 1f, 0f)
    
    private val projectionMatrix = Matrix4f()
    private val viewMatrix = Matrix4f()
    
    fun updateProjection(aspectRatio: Float) {
        projectionMatrix.setPerspective(
            Math.toRadians(fov.toDouble()).toFloat(),
            aspectRatio,
            nearPlane,
            farPlane
        )
    }
    
    fun updateView() {
        viewMatrix.setLookAt(
            position,
            target,
            up
        )
    }
    
    fun getProjectionMatrix(): Matrix4f = Matrix4f(projectionMatrix)
    fun getViewMatrix(): Matrix4f = Matrix4f(viewMatrix)
    
    /**
     * Camera control modes
     */
    enum class ControlMode {
        ORBIT,       // Orbit around target
        FLY,         // Fly freely
        TRACKING,    // Track an object
        FIRST_PERSON // First person view
    }
    
    fun setMode(mode: ControlMode) {
        // Implement camera mode switching
    }
}
```

### Camera Controls

```kotlin>
/**
 * Standard camera controls
 * Mobile-optimized gestures
 */
class CameraController(
    private val camera: Camera
) {
    
    private val sensitivity = 0.5f
    private val zoomSpeed = 0.1f
    
    /**
     * Handle touch events for camera control
     */
    fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                // Start tracking touch
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                // Update camera based on touch movement
                return true
            }
            MotionEvent.ACTION_UP -> {
                // Stop tracking
                return true
            }
        }
        return false
    }
    
    /**
     * Zoom controls
     */
    fun zoom(delta: Float) {
        val direction = camera.target.sub(camera.position).normalize()
        camera.position.add(direction.mul(delta * zoomSpeed))
    }
    
    /**
     * Pan controls
     */
    fun pan(deltaX: Float, deltaY: Float) {
        val right = camera.getDirection().cross(camera.up).normalize()
        val up = camera.up
        camera.position.add(right.mul(deltaX * sensitivity))
        camera.position.add(up.mul(deltaY * sensitivity))
        camera.target.add(right.mul(deltaX * sensitivity))
        camera.target.add(up.mul(deltaY * sensitivity))
    }
    
    /**
     * Rotate controls
     */
    fun rotate(deltaX: Float, deltaY: Float) {
        val rotationMatrix = Matrix4f().rotateXYZ(
            Vector3f(deltaY * sensitivity, deltaX * sensitivity, 0f)
        )
        
        val offset = camera.position.sub(camera.target)
        rotationMatrix.transform(offset)
        camera.position = camera.target.add(offset)
    }
}
```

## Scene Graph Standards

### Scene Graph Architecture

```kotlin>
/**
 * Standard scene graph node
 */
abstract class SceneNode {
    val children = mutableListOf<SceneNode>()
    var parent: SceneNode? = null
    var transform = Transform()
    
    val localMatrix: Matrix4f
        get() = transform.toMatrix()
    
    val worldMatrix: Matrix4f
        get() = parent?.worldMatrix?.mul(localMatrix) ?: localMatrix
    
    abstract fun render(renderer: Renderer)
    abstract fun update(deltaTime: Float)
    
    fun addChild(child: SceneNode) {
        child.parent = this
        children.add(child)
    }
    
    fun removeChild(child: SceneNode) {
        child.parent = null
        children.remove(child)
    }
}

/**
 * Standard mesh node
 */
class MeshNode(
    private val mesh: Mesh,
    private val material: Material
) : SceneNode() {
    
    override fun render(renderer: Renderer) {
        renderer.renderMesh(mesh, material, worldMatrix)
    }
    
    override fun update(deltaTime: Float) {
        // Update animations if any
    }
}

/**
 * Standard transform
 */
data class Transform(
    var position: Vector3f = Vector3f(),
    var rotation: Quaternionf = Quaternionf(),
    var scale: Vector3f = Vector3f(1f, 1f, 1f)
) {
    fun toMatrix(): Matrix4f {
        val matrix = Matrix4f()
        matrix.translationRotateScale(position, rotation, scale)
        return matrix
    }
}
```

## Lighting Standards

### Standard Light Types

```kotlin>
/**
 * Standard light configuration
 */
sealed class Light {
    abstract var color: Vector3f
    abstract var intensity: Float
    
    /**
     * Directional light (sun/moon)
     */
    class DirectionalLight(
        var direction: Vector3f = Vector3f(0f, -1f, 0f)
    ) : Light() {
        override var color = Vector3f(1f, 1f, 1f)
        override var intensity = 1.0f
    }
    
    /**
     * Point light (local light source)
     */
    class PointLight(
        var position: Vector3f = Vector3f(),
        var range: Float = 10f
    ) : Light() {
        override var color = Vector3f(1f, 1f, 1f)
        override var intensity = 1.0f
        
        fun getAttenuation(distance: Float): Float {
            return 1.0f / (1.0f + 0.1f * distance + 0.01f * distance * distance)
        }
    }
    
    /**
     * Spot light (focused light)
     */
    class SpotLight(
        var position: Vector3f = Vector3f(),
        var direction: Vector3f = Vector3f(0f, -1f, 0f),
        var cutOff: Float = 12.5f,
        var outerCutOff: Float = 17.5f
    ) : Light() {
        override var color = Vector3f(1f, 1f, 1f)
        override var intensity = 1.0f
        
        fun getAttenuation(distance: Float): Float {
            return 1.0f / (1.0f + 0.1f * distance + 0.01f * distance * distance)
        }
    }
}

/**
 * Standard lighting manager
 */
class LightingManager {
    
    private val directionalLights = mutableListOf<Light.DirectionalLight>()
    private val pointLights = mutableListOf<Light.PointLight>()
    private val spotLights = mutableListOf<Light.SpotLight>()
    
    fun addLight(light: Light) {
        when (light) {
            is Light.DirectionalLight -> directionalLights.add(light)
            is Light.PointLight -> pointLights.add(light)
            is Light.SpotLight -> spotLights.add(light)
        }
    }
    
    fun removeLight(light: Light) {
        when (light) {
            is Light.DirectionalLight -> directionalLights.remove(light)
            is Light.PointLight -> pointLights.remove(light)
            is Light.SpotLight -> spotLights.remove(light)
        }
    }
    
    fun setupLighting(shader: ShaderProgram) {
        // Setup lighting uniforms
        directionalLights.forEachIndexed { index, light ->
            shader.setUniform("u_lightPositions[$index]", light.direction)
            shader.setUniform("u_lightColors[$index]", light.color * light.intensity)
        }
    }
}
```

## Performance Optimization Standards

### Frustum Culling

```kotlin>
/**
 * Standard frustum culling
 */
class FrustumCuller {
    
    private val planes = Array(6) { Plane() }
    
    fun updateFrustum(viewProjectionMatrix: Matrix4f) {
        // Extract planes from view-projection matrix
        // Left, Right, Top, Bottom, Near, Far
    }
    
    fun isCullable(boundingBox: BoundingBox): Boolean {
        // Check if bounding box is outside frustum
        planes.forEach { plane ->
            if (boundingBox.isOutsidePlane(plane)) {
                return true
            }
        }
        return false
    }
}
```

### Level of Detail (LOD)

```kotlin>
/**
 * Standard LOD system
 * Based on Second Life LOD system
 */
class LODSystem {
    
    data class LODLevel(
        val distance: Float,
        val mesh: Mesh,
        val priority: Int
    )
    
    fun selectLOD(
        lods: List<LODLevel>,
        distance: Float
    ): Mesh {
        return lods.firstOrNull { distance < it.distance }?.mesh ?: lods.last().mesh
    }
}
```

## Conclusion

This document establishes comprehensive standards for 3D rendering in Second Life mobile viewers. Key takeaways:

1. **OpenGL ES 3.0 minimum** - Ensures compatibility with modern mobile devices
2. **Modular pipeline** - Clear separation of rendering stages
3. **Standardized shaders** - Reusable shader templates
4. **Mobile-optimized controls** - Touch-based camera controls
5. **Performance optimizations** - Frustum culling, LOD, efficient resource management

These standards ensure:
- Consistent rendering quality across devices
- Optimal performance on mobile hardware
- Maintainable and extensible code
- Compatibility with Second Life asset formats

---

**Document Version**: 1.0  
**Last Updated**: January 16, 2025  
**Status**: ✅ Complete