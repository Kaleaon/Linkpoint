package com.babylonkt.graphics

import com.babylonkt.core.*
import com.babylonkt.math.Vector3
import com.babylonkt.math.Matrix4x4
import com.babylonkt.math.Color3
import com.babylonkt.math.Color4

/**
 * Platform-agnostic graphics device interface.
 * Implementations exist for OpenGL ES, Metal, Vulkan, etc.
 */
interface GraphicsDevice {
    
    // ==================== Initialization ====================
    
    /**
     * Initializes the graphics device with the given configuration.
     */
    fun initialize(config: EngineConfiguration)
    
    /**
     * Resizes the rendering viewport.
     */
    fun resize(width: Int, height: Int)
    
    // ==================== Rendering ====================
    
    /**
     * Clears the screen with the specified color.
     */
    fun clear(r: Float, g: Float, b: Float, a: Float)
    
    /**
     * Begins a new frame.
     */
    fun beginFrame()
    
    /**
     * Ends the current frame and presents it.
     */
    fun endFrame()
    
    // ==================== Buffer Management ====================
    
    /**
     * Creates a vertex buffer.
     */
    fun createVertexBuffer(data: FloatArray, updatable: Boolean = false): VertexBuffer
    
    /**
     * Creates an index buffer.
     */
    fun createIndexBuffer(data: IntArray, updatable: Boolean = false): IndexBuffer
    
    /**
     * Updates a vertex buffer with new data.
     */
    fun updateVertexBuffer(buffer: VertexBuffer, data: FloatArray)
    
    /**
     * Updates an index buffer with new data.
     */
    fun updateIndexBuffer(buffer: IndexBuffer, data: IntArray)
    
    // ==================== Shader Management ====================
    
    /**
     * Creates a shader program from vertex and fragment shader source code.
     */
    fun createShader(vertexSource: String, fragmentSource: String): Shader
    
    // ==================== Texture Management ====================
    
    /**
     * Creates a texture from image data.
     */
    fun createTexture(
        width: Int,
        height: Int,
        data: ByteArray?,
        format: TextureFormat = TextureFormat.RGBA,
        generateMipmaps: Boolean = true
    ): TextureHandle
    
    /**
     * Updates a texture with new data.
     */
    fun updateTexture(handle: TextureHandle, data: ByteArray)
    
    /**
     * Binds a texture to a texture unit.
     */
    fun bindTexture(handle: TextureHandle, unit: Int)
    
    // ==================== State Management ====================
    
    /**
     * Sets the depth test state.
     */
    fun setDepthTest(enabled: Boolean)
    
    /**
     * Sets the depth write state.
     */
    fun setDepthWrite(enabled: Boolean)
    
    /**
     * Sets the blend mode.
     */
    fun setBlendMode(mode: BlendMode)
    
    /**
     * Sets the cull mode.
     */
    fun setCullMode(mode: CullMode)
    
    /**
     * Sets the viewport.
     */
    fun setViewport(x: Int, y: Int, width: Int, height: Int)
    
    // ==================== Drawing ====================
    
    /**
     * Draws indexed geometry.
     */
    fun drawIndexed(indexCount: Int, indexOffset: Int = 0)
    
    /**
     * Draws non-indexed geometry.
     */
    fun draw(vertexCount: Int, vertexOffset: Int = 0)
    
    /**
     * Draws instanced geometry.
     */
    fun drawInstanced(indexCount: Int, instanceCount: Int)
    
    // ==================== Performance Monitoring ====================
    
    /**
     * Returns the number of draw calls in the current frame.
     */
    fun getDrawCallCount(): Int
    
    /**
     * Returns the number of triangles rendered in the current frame.
     */
    fun getTriangleCount(): Int
    
    /**
     * Resets performance counters.
     */
    fun resetPerformanceCounters()
    
    // ==================== Utilities ====================
    
    /**
     * Captures a screenshot of the current frame.
     */
    fun captureScreenshot(): ByteArray
    
    /**
     * Disposes of the graphics device and all its resources.
     */
    fun dispose()
}

// ==================== Supporting Interfaces ====================

/**
 * Represents a vertex buffer on the GPU.
 */
interface VertexBuffer {
    val size: Int
    fun bind()
    fun unbind()
    fun dispose()
}

/**
 * Represents an index buffer on the GPU.
 */
interface IndexBuffer {
    val size: Int
    fun bind()
    fun unbind()
    fun dispose()
}

/**
 * Handle to a texture on the GPU.
 */
@JvmInline
value class TextureHandle(val id: Int)

// ==================== Enums ====================

/**
 * Texture formats.
 */
enum class TextureFormat {
    RGB,
    RGBA,
    DEPTH,
    DEPTH_STENCIL
}

/**
 * Blend modes for alpha blending.
 */
enum class BlendMode {
    DISABLED,
    ALPHA_BLEND,      // SrcAlpha, OneMinusSrcAlpha
    ADDITIVE,         // One, One
    MULTIPLY,         // DstColor, Zero
    PREMULTIPLIED     // One, OneMinusSrcAlpha
}

/**
 * Face culling modes.
 */
enum class CullMode {
    NONE,
    FRONT,
    BACK,
    FRONT_AND_BACK
}

// ==================== OpenGL ES Implementation Example ====================

/**
 * OpenGL ES implementation of GraphicsDevice.
 * This would use GLES30 bindings on Android.
 */
class OpenGLESGraphicsDevice : GraphicsDevice {
    
    private var drawCallCount = 0
    private var triangleCount = 0
    
    override fun initialize(config: EngineConfiguration) {
        // Initialize OpenGL ES context
        println("Initializing OpenGL ES Graphics Device")
        
        // Enable depth testing by default
        setDepthTest(true)
        setDepthWrite(true)
        
        // Set default blend mode
        setBlendMode(BlendMode.ALPHA_BLEND)
        
        // Set default cull mode
        setCullMode(CullMode.BACK)
    }
    
    override fun resize(width: Int, height: Int) {
        setViewport(0, 0, width, height)
    }
    
    override fun clear(r: Float, g: Float, b: Float, a: Float) {
        // glClearColor(r, g, b, a)
        // glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT)
    }
    
    override fun beginFrame() {
        resetPerformanceCounters()
    }
    
    override fun endFrame() {
        // Swap buffers (handled by platform)
    }
    
    override fun createVertexBuffer(data: FloatArray, updatable: Boolean): VertexBuffer {
        return OpenGLVertexBuffer(data, updatable)
    }
    
    override fun createIndexBuffer(data: IntArray, updatable: Boolean): IndexBuffer {
        return OpenGLIndexBuffer(data, updatable)
    }
    
    override fun updateVertexBuffer(buffer: VertexBuffer, data: FloatArray) {
        (buffer as? OpenGLVertexBuffer)?.update(data)
    }
    
    override fun updateIndexBuffer(buffer: IndexBuffer, data: IntArray) {
        (buffer as? OpenGLIndexBuffer)?.update(data)
    }
    
    override fun createShader(vertexSource: String, fragmentSource: String): Shader {
        return OpenGLShader(vertexSource, fragmentSource)
    }
    
    override fun createTexture(
        width: Int,
        height: Int,
        data: ByteArray?,
        format: TextureFormat,
        generateMipmaps: Boolean
    ): TextureHandle {
        // Create OpenGL texture
        // val textureId = glGenTextures()
        // glBindTexture(GL_TEXTURE_2D, textureId)
        // ... upload data
        return TextureHandle(0) // Placeholder
    }
    
    override fun updateTexture(handle: TextureHandle, data: ByteArray) {
        // glBindTexture(GL_TEXTURE_2D, handle.id)
        // glTexSubImage2D(...)
    }
    
    override fun bindTexture(handle: TextureHandle, unit: Int) {
        // glActiveTexture(GL_TEXTURE0 + unit)
        // glBindTexture(GL_TEXTURE_2D, handle.id)
    }
    
    override fun setDepthTest(enabled: Boolean) {
        // if (enabled) glEnable(GL_DEPTH_TEST) else glDisable(GL_DEPTH_TEST)
    }
    
    override fun setDepthWrite(enabled: Boolean) {
        // glDepthMask(enabled)
    }
    
    override fun setBlendMode(mode: BlendMode) {
        when (mode) {
            BlendMode.DISABLED -> {
                // glDisable(GL_BLEND)
            }
            BlendMode.ALPHA_BLEND -> {
                // glEnable(GL_BLEND)
                // glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
            }
            BlendMode.ADDITIVE -> {
                // glEnable(GL_BLEND)
                // glBlendFunc(GL_ONE, GL_ONE)
            }
            BlendMode.MULTIPLY -> {
                // glEnable(GL_BLEND)
                // glBlendFunc(GL_DST_COLOR, GL_ZERO)
            }
            BlendMode.PREMULTIPLIED -> {
                // glEnable(GL_BLEND)
                // glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA)
            }
        }
    }
    
    override fun setCullMode(mode: CullMode) {
        when (mode) {
            CullMode.NONE -> {
                // glDisable(GL_CULL_FACE)
            }
            CullMode.FRONT -> {
                // glEnable(GL_CULL_FACE)
                // glCullFace(GL_FRONT)
            }
            CullMode.BACK -> {
                // glEnable(GL_CULL_FACE)
                // glCullFace(GL_BACK)
            }
            CullMode.FRONT_AND_BACK -> {
                // glEnable(GL_CULL_FACE)
                // glCullFace(GL_FRONT_AND_BACK)
            }
        }
    }
    
    override fun setViewport(x: Int, y: Int, width: Int, height: Int) {
        // glViewport(x, y, width, height)
    }
    
    override fun drawIndexed(indexCount: Int, indexOffset: Int) {
        // glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, indexOffset)
        drawCallCount++
        triangleCount += indexCount / 3
    }
    
    override fun draw(vertexCount: Int, vertexOffset: Int) {
        // glDrawArrays(GL_TRIANGLES, vertexOffset, vertexCount)
        drawCallCount++
        triangleCount += vertexCount / 3
    }
    
    override fun drawInstanced(indexCount: Int, instanceCount: Int) {
        // glDrawElementsInstanced(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0, instanceCount)
        drawCallCount++
        triangleCount += (indexCount / 3) * instanceCount
    }
    
    override fun getDrawCallCount(): Int = drawCallCount
    
    override fun getTriangleCount(): Int = triangleCount
    
    override fun resetPerformanceCounters() {
        drawCallCount = 0
        triangleCount = 0
    }
    
    override fun captureScreenshot(): ByteArray {
        // glReadPixels(...)
        return ByteArray(0) // Placeholder
    }
    
    override fun dispose() {
        println("Disposing OpenGL ES Graphics Device")
    }
}

// ==================== OpenGL Buffer Implementations ====================

class OpenGLVertexBuffer(data: FloatArray, private val updatable: Boolean) : VertexBuffer {
    private val bufferId: Int = 0 // glGenBuffers()
    override val size: Int = data.size
    
    init {
        // glBindBuffer(GL_ARRAY_BUFFER, bufferId)
        // glBufferData(GL_ARRAY_BUFFER, data, if (updatable) GL_DYNAMIC_DRAW else GL_STATIC_DRAW)
    }
    
    fun update(data: FloatArray) {
        if (!updatable) {
            println("Warning: Attempting to update non-updatable buffer")
            return
        }
        // glBindBuffer(GL_ARRAY_BUFFER, bufferId)
        // glBufferSubData(GL_ARRAY_BUFFER, 0, data)
    }
    
    override fun bind() {
        // glBindBuffer(GL_ARRAY_BUFFER, bufferId)
    }
    
    override fun unbind() {
        // glBindBuffer(GL_ARRAY_BUFFER, 0)
    }
    
    override fun dispose() {
        // glDeleteBuffers(bufferId)
    }
}

class OpenGLIndexBuffer(data: IntArray, private val updatable: Boolean) : IndexBuffer {
    private val bufferId: Int = 0 // glGenBuffers()
    override val size: Int = data.size
    
    init {
        // glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferId)
        // glBufferData(GL_ELEMENT_ARRAY_BUFFER, data, if (updatable) GL_DYNAMIC_DRAW else GL_STATIC_DRAW)
    }
    
    fun update(data: IntArray) {
        if (!updatable) {
            println("Warning: Attempting to update non-updatable buffer")
            return
        }
        // glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferId)
        // glBufferSubData(GL_ELEMENT_ARRAY_BUFFER, 0, data)
    }
    
    override fun bind() {
        // glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, bufferId)
    }
    
    override fun unbind() {
        // glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0)
    }
    
    override fun dispose() {
        // glDeleteBuffers(bufferId)
    }
}

// ==================== OpenGL Shader Implementation ====================

class OpenGLShader(
    private val vertexSource: String,
    private val fragmentSource: String
) : Shader {
    
    private val programId: Int = 0 // glCreateProgram()
    private val uniformLocations = mutableMapOf<String, Int>()
    
    init {
        // Compile vertex shader
        // val vertexShader = glCreateShader(GL_VERTEX_SHADER)
        // glShaderSource(vertexShader, vertexSource)
        // glCompileShader(vertexShader)
        
        // Compile fragment shader
        // val fragmentShader = glCreateShader(GL_FRAGMENT_SHADER)
        // glShaderSource(fragmentShader, fragmentSource)
        // glCompileShader(fragmentShader)
        
        // Link program
        // glAttachShader(programId, vertexShader)
        // glAttachShader(programId, fragmentShader)
        // glLinkProgram(programId)
        
        // Clean up shaders
        // glDeleteShader(vertexShader)
        // glDeleteShader(fragmentShader)
    }
    
    private fun getUniformLocation(name: String): Int {
        return uniformLocations.getOrPut(name) {
            // glGetUniformLocation(programId, name)
            0 // Placeholder
        }
    }
    
    override fun bind() {
        // glUseProgram(programId)
    }
    
    override fun unbind() {
        // glUseProgram(0)
    }
    
    override fun setFloat(name: String, value: Float) {
        val location = getUniformLocation(name)
        // glUniform1f(location, value)
    }
    
    override fun setInt(name: String, value: Int) {
        val location = getUniformLocation(name)
        // glUniform1i(location, value)
    }
    
    override fun setVector3(name: String, value: Vector3) {
        val location = getUniformLocation(name)
        // glUniform3f(location, value.x, value.y, value.z)
    }
    
    override fun setColor3(name: String, value: Color3) {
        val location = getUniformLocation(name)
        // glUniform3f(location, value.r, value.g, value.b)
    }
    
    override fun setColor4(name: String, value: Color4) {
        val location = getUniformLocation(name)
        // glUniform4f(location, value.r, value.g, value.b, value.a)
    }
    
    override fun setMatrix(name: String, value: Matrix4x4) {
        val location = getUniformLocation(name)
        // glUniformMatrix4fv(location, 1, false, value.toFloatArray())
    }
    
    override fun setTexture(name: String, texture: Texture) {
        // Bind texture to a texture unit and set the uniform
        val location = getUniformLocation(name)
        // glUniform1i(location, textureUnit)
    }
    
    override fun dispose() {
        // glDeleteProgram(programId)
    }
}