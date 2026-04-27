package com.linkpoint.render.lumiya.glres

import android.opengl.GLES32
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer

/**
 * Utility for creating and uploading GL buffer objects and VAOs.
 *
 * Design lineage: Lumiya `GLBuffer.java` / `DrawableGeometry.java`,
 * modernised with VAO-first workflow and direct byte-buffer allocation.
 */
class GLBufferManager(private val resourceManager: GLResourceManager) {

    // ── Float buffer helpers ─────────────────────────────────────────────

    fun createFloatBuffer(data: FloatArray): FloatBuffer {
        return ByteBuffer.allocateDirect(data.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .apply { put(data); flip() }
    }

    fun createShortBuffer(data: ShortArray): ShortBuffer {
        return ByteBuffer.allocateDirect(data.size * 2)
            .order(ByteOrder.nativeOrder())
            .asShortBuffer()
            .apply { put(data); flip() }
    }

    fun createIntBuffer(data: IntArray): IntBuffer {
        return ByteBuffer.allocateDirect(data.size * 4)
            .order(ByteOrder.nativeOrder())
            .asIntBuffer()
            .apply { put(data); flip() }
    }

    // ── VBO creation ─────────────────────────────────────────────────────

    /**
     * Create a VBO from a float array.
     * @return GL buffer handle.
     */
    fun uploadVertexBuffer(data: FloatArray, usage: Int = GLES32.GL_STATIC_DRAW): Int {
        resourceManager.assertGlThread("GLBufferManager.uploadVertexBuffer")
        val handle = resourceManager.createBuffer()
        val fb = createFloatBuffer(data)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, handle)
        GLES32.glBufferData(GLES32.GL_ARRAY_BUFFER, data.size * 4, fb, usage)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, 0)
        resourceManager.addMemory(data.size * 4L)
        return handle
    }

    /**
     * Create an index buffer from a short array.
     * @return GL buffer handle.
     */
    fun uploadIndexBuffer(data: ShortArray, usage: Int = GLES32.GL_STATIC_DRAW): Int {
        resourceManager.assertGlThread("GLBufferManager.uploadIndexBuffer(short)")
        val handle = resourceManager.createBuffer()
        val sb = createShortBuffer(data)
        GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, handle)
        GLES32.glBufferData(GLES32.GL_ELEMENT_ARRAY_BUFFER, data.size * 2, sb, usage)
        GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, 0)
        resourceManager.addMemory(data.size * 2L)
        return handle
    }

    fun uploadIndexBuffer(data: IntArray, usage: Int = GLES32.GL_STATIC_DRAW): Int {
        resourceManager.assertGlThread("GLBufferManager.uploadIndexBuffer(int)")
        val handle = resourceManager.createBuffer()
        val ib = createIntBuffer(data)
        GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, handle)
        GLES32.glBufferData(GLES32.GL_ELEMENT_ARRAY_BUFFER, data.size * 4, ib, usage)
        GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, 0)
        resourceManager.addMemory(data.size * 4L)
        return handle
    }

    // ── UBO creation ─────────────────────────────────────────────────────

    fun createUniformBuffer(sizeBytes: Int, usage: Int = GLES32.GL_DYNAMIC_DRAW): Int {
        resourceManager.assertGlThread("GLBufferManager.createUniformBuffer")
        val handle = resourceManager.createBuffer()
        GLES32.glBindBuffer(GLES32.GL_UNIFORM_BUFFER, handle)
        GLES32.glBufferData(GLES32.GL_UNIFORM_BUFFER, sizeBytes, null, usage)
        GLES32.glBindBuffer(GLES32.GL_UNIFORM_BUFFER, 0)
        return handle
    }

    // ── VAO builder ──────────────────────────────────────────────────────

    /**
     * Build a complete VAO for a mesh.
     *
     * @param vertexData   Interleaved vertex data (pos, normal, texcoord, ...)
     * @param indexData     Index data (short)
     * @param attributes    List of (location, componentCount) pairs describing the
     *                      interleaved layout.  Example: [(0,3), (1,3), (2,2)]
     *                      for position(3), normal(3), texcoord(2).
     * @return [MeshVAO] holding all handles needed for drawing.
     */
    fun buildVAO(
        vertexData: FloatArray,
        indexData: ShortArray,
        attributes: List<Pair<Int, Int>>  // (location, size)
    ): MeshVAO {
        resourceManager.assertGlThread("GLBufferManager.buildVAO")
        val vao = resourceManager.createVAO()
        val vbo = uploadVertexBuffer(vertexData)
        val ebo = uploadIndexBuffer(indexData)

        GLES32.glBindVertexArray(vao)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, vbo)

        val stride = attributes.sumOf { it.second } * 4  // bytes
        var offset = 0
        for ((location, size) in attributes) {
            GLES32.glEnableVertexAttribArray(location)
            GLES32.glVertexAttribPointer(location, size, GLES32.GL_FLOAT, false, stride, offset)
            offset += size * 4
        }

        GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, ebo)
        GLES32.glBindVertexArray(0)

        return MeshVAO(vao, vbo, ebo, indexData.size)
    }

    fun buildVAOInt(
        vertexData: FloatArray,
        indexData: IntArray,
        attributes: List<Pair<Int, Int>>
    ): MeshVAO {
        resourceManager.assertGlThread("GLBufferManager.buildVAOInt")
        val vao = resourceManager.createVAO()
        val vbo = uploadVertexBuffer(vertexData)
        val ebo = uploadIndexBuffer(indexData)

        GLES32.glBindVertexArray(vao)
        GLES32.glBindBuffer(GLES32.GL_ARRAY_BUFFER, vbo)

        val stride = attributes.sumOf { it.second } * 4
        var offset = 0
        for ((location, size) in attributes) {
            GLES32.glEnableVertexAttribArray(location)
            GLES32.glVertexAttribPointer(location, size, GLES32.GL_FLOAT, false, stride, offset)
            offset += size * 4
        }

        GLES32.glBindBuffer(GLES32.GL_ELEMENT_ARRAY_BUFFER, ebo)
        GLES32.glBindVertexArray(0)

        return MeshVAO(vao, vbo, ebo, indexData.size, useIntIndices = true)
    }

    // ── Cleanup ──────────────────────────────────────────────────────────

    fun destroyVAO(mesh: MeshVAO) {
        resourceManager.assertGlThread("GLBufferManager.destroyVAO")
        resourceManager.deleteVAO(mesh.vao)
        resourceManager.deleteBuffer(mesh.vbo)
        resourceManager.deleteBuffer(mesh.ebo)
    }

    data class MeshVAO(
        val vao: Int,
        val vbo: Int,
        val ebo: Int,
        val indexCount: Int,
        val useIntIndices: Boolean = false
    )
}
