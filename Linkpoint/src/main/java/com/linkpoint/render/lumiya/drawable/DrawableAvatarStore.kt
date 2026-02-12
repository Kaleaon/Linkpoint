package com.linkpoint.render.lumiya.drawable

import android.opengl.GLES32
import android.opengl.Matrix
import com.linkpoint.render.lumiya.core.LumiyaRenderContext
import com.linkpoint.render.lumiya.glres.GLBufferManager
import com.linkpoint.render.lumiya.shaders.AvatarShaderProgram
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Manages avatar rendering with skeletal animation.
 *
 * Design lineage: Lumiya `DrawableAvatar.java` + `DrawableAvatarPart.java` +
 * `AvatarSkeleton.java`, modernised with UBO-based joint upload.
 *
 * Each avatar stores its world transform and a UBO for joint matrices.
 * The SL skeleton uses up to 133 joints; the UBO is sized for 134.
 */
class DrawableAvatarStore {

    /** Per-avatar instance data. */
    class AvatarInstance(
        val id: UUID,
        val modelMatrix: FloatArray = FloatArray(16).also { Matrix.setIdentityM(it, 0) },
        var jointUBO: Int = 0,
        var jointCount: Int = 0,
        var textureHandle: Int = 0,
        val jointMatrices: FloatArray = FloatArray(AvatarShaderProgram.MAX_JOINTS * 16)
    ) {
        init {
            // Default: identity for all joints
            for (i in 0 until AvatarShaderProgram.MAX_JOINTS) {
                Matrix.setIdentityM(jointMatrices, i * 16)
            }
        }
    }

    private val avatars = ConcurrentHashMap<UUID, AvatarInstance>()
    private var placeholderMesh: GLBufferManager.MeshVAO? = null
    private var bufferManager: GLBufferManager? = null

    // ── Mutation ─────────────────────────────────────────────────────────

    fun addAvatar(id: UUID, posX: Float, posY: Float, posZ: Float) {
        val instance = AvatarInstance(id = id)
        Matrix.setIdentityM(instance.modelMatrix, 0)
        Matrix.translateM(instance.modelMatrix, 0, posX, posY, posZ)
        avatars[id] = instance
    }

    fun removeAvatar(id: UUID) {
        avatars.remove(id)?.let { destroyAvatarResources(it) }
    }

    fun clear() {
        avatars.values.forEach { destroyAvatarResources(it) }
        avatars.clear()
    }

    fun destroy() {
        clear()
        placeholderMesh?.let { bufferManager?.destroyVAO(it) }
        placeholderMesh = null
    }

    // ── Draw ─────────────────────────────────────────────────────────────

    fun draw(ctx: LumiyaRenderContext) {
        if (avatars.isEmpty()) return
        ensurePlaceholder(ctx)
        val program = ctx.avatarProgram ?: return
        val mesh = placeholderMesh ?: return

        program.use()
        program.setLighting(
            ctx.sunDirectionX, ctx.sunDirectionY, ctx.sunDirectionZ,
            ctx.sunColorR, ctx.sunColorG, ctx.sunColorB,
            ctx.ambientColorR, ctx.ambientColorG, ctx.ambientColorB
        )

        GLES32.glBindVertexArray(mesh.vao)
        for (avatar in avatars.values) {
            program.setModelMatrix(avatar.modelMatrix)
            program.setColor(0.85f, 0.72f, 0.62f, 1.0f) // skin tone
            program.setUseTexture(avatar.textureHandle != 0)
            program.setJointCount(avatar.jointCount)

            // Bind joint UBO
            if (avatar.jointUBO != 0) {
                GLES32.glBindBufferBase(GLES32.GL_UNIFORM_BUFFER, 1, avatar.jointUBO)
            }

            GLES32.glDrawElements(GLES32.GL_TRIANGLES, mesh.indexCount, GLES32.GL_UNSIGNED_SHORT, 0)
        }
        GLES32.glBindVertexArray(0)
    }

    /**
     * Upload joint matrices for an avatar's skeleton.
     */
    fun updateJoints(id: UUID, matrices: FloatArray, count: Int) {
        val avatar = avatars[id] ?: return
        System.arraycopy(matrices, 0, avatar.jointMatrices, 0, minOf(matrices.size, avatar.jointMatrices.size))
        avatar.jointCount = count

        // Ensure UBO exists
        if (avatar.jointUBO == 0) {
            val buf = IntArray(1)
            GLES32.glGenBuffers(1, buf, 0)
            avatar.jointUBO = buf[0]
        }

        val byteSize = AvatarShaderProgram.MAX_JOINTS * 16 * 4
        val fb = ByteBuffer.allocateDirect(byteSize)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(avatar.jointMatrices, 0, AvatarShaderProgram.MAX_JOINTS * 16)
        fb.flip()

        GLES32.glBindBuffer(GLES32.GL_UNIFORM_BUFFER, avatar.jointUBO)
        GLES32.glBufferData(GLES32.GL_UNIFORM_BUFFER, byteSize, fb, GLES32.GL_DYNAMIC_DRAW)
        GLES32.glBindBuffer(GLES32.GL_UNIFORM_BUFFER, 0)
    }

    // ── Helpers ──────────────────────────────────────────────────────────

    private fun ensurePlaceholder(ctx: LumiyaRenderContext) {
        if (placeholderMesh != null) return
        // Build a simple capsule-like stand-in for avatars
        val bm = GLBufferManager(ctx.resourceManager)
        bufferManager = bm
        placeholderMesh = buildAvatarPlaceholder(bm)
    }

    /**
     * Simple capsule-like shape as placeholder until real avatar meshes load.
     */
    private fun buildAvatarPlaceholder(bm: GLBufferManager): GLBufferManager.MeshVAO {
        // Use a UV sphere scaled to approximate an avatar (roughly 0.3m wide, 1.8m tall)
        val segments = 12
        val rings = 8
        val radiusX = 0.3f
        val radiusY = 0.3f
        val radiusZ = 0.9f

        val verts = mutableListOf<Float>()
        val indices = mutableListOf<Short>()

        for (r in 0..rings) {
            val phi = Math.PI * r / rings
            val sinPhi = Math.sin(phi).toFloat()
            val cosPhi = Math.cos(phi).toFloat()
            for (s in 0..segments) {
                val theta = 2.0 * Math.PI * s / segments
                val sinTheta = Math.sin(theta).toFloat()
                val cosTheta = Math.cos(theta).toFloat()
                val x = cosTheta * sinPhi * radiusX
                val y = sinTheta * sinPhi * radiusY
                val z = cosPhi * radiusZ + radiusZ  // offset so bottom is at 0
                // pos(3) + normal(3) + uv(2) + weights(4) + joints(4) = 16 floats
                // For placeholder, weights = (1,0,0,0), joint = (0,0,0,0)
                verts.addAll(listOf(
                    x, y, z,
                    cosTheta * sinPhi, sinTheta * sinPhi, cosPhi,
                    s.toFloat() / segments, r.toFloat() / rings,
                    1f, 0f, 0f, 0f,   // weights
                    0f, 0f, 0f, 0f    // joints (float, cast in shader)
                ))
            }
        }
        for (r in 0 until rings) {
            for (s in 0 until segments) {
                val cur = r * (segments + 1) + s
                val next = cur + segments + 1
                indices.add(cur.toShort()); indices.add(next.toShort()); indices.add((cur + 1).toShort())
                indices.add((cur + 1).toShort()); indices.add(next.toShort()); indices.add((next + 1).toShort())
            }
        }

        return bm.buildVAO(
            verts.toFloatArray(), indices.toShortArray(),
            listOf(
                0 to 3,  // position
                1 to 3,  // normal
                2 to 2,  // texcoord
                3 to 4,  // weights
                4 to 4   // joints
            )
        )
    }

    private fun destroyAvatarResources(avatar: AvatarInstance) {
        if (avatar.jointUBO != 0) {
            GLES32.glDeleteBuffers(1, intArrayOf(avatar.jointUBO), 0)
            avatar.jointUBO = 0
        }
    }
}
