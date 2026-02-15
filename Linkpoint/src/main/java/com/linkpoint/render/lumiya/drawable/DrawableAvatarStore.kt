package com.linkpoint.render.lumiya.drawable

import android.opengl.GLES32
import android.opengl.Matrix
import android.util.Log
import com.linkpoint.diagnostics.ScenePopulationDiagnostics
import com.linkpoint.render.lumiya.core.LumiyaRenderContext
import com.linkpoint.render.lumiya.glres.GLBufferManager
import com.linkpoint.render.lumiya.shaders.AvatarShaderProgram
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class DrawableAvatarStore(
    private val avatarMeshProvider: (() -> AvatarMeshAsset?)? = null
) {
    companion object {
        private const val TAG = "DrawableAvatarStore"
    }

    class AvatarInstance(
        val id: UUID,
        val modelMatrix: FloatArray = FloatArray(16).also { Matrix.setIdentityM(it, 0) },
        var jointUBO: Int = 0,
        var jointCount: Int = 0,
        var textureHandle: Int = 0,
        val jointMatrices: FloatArray = FloatArray(AvatarShaderProgram.MAX_JOINTS * 16)
    ) {
        init {
            for (i in 0 until AvatarShaderProgram.MAX_JOINTS) {
                Matrix.setIdentityM(jointMatrices, i * 16)
            }
        }
    }

    private val avatars = ConcurrentHashMap<UUID, AvatarInstance>()
    private var avatarMesh: GLBufferManager.MeshVAO? = null
    private var bufferManager: GLBufferManager? = null

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
        avatarMesh?.let { bufferManager?.destroyVAO(it) }
        avatarMesh = null
    }

    fun draw(ctx: LumiyaRenderContext) {
        if (avatars.isEmpty()) return
        ensureAvatarMesh(ctx)
        val program = ctx.avatarProgram ?: return
        val mesh = avatarMesh
        if (mesh == null) {
            ScenePopulationDiagnostics.markSceneInserted(ScenePopulationDiagnostics.EntityType.AVATAR, false)
            return
        }

        program.use()
        program.setLighting(
            ctx.sunDirectionX, ctx.sunDirectionY, ctx.sunDirectionZ,
            ctx.sunColorR, ctx.sunColorG, ctx.sunColorB,
            ctx.ambientColorR, ctx.ambientColorG, ctx.ambientColorB
        )

        GLES32.glBindVertexArray(mesh.vao)
        for (avatar in avatars.values) {
            program.setModelMatrix(avatar.modelMatrix)
            program.setColor(0.85f, 0.72f, 0.62f, 1.0f)
            program.setUseTexture(avatar.textureHandle != 0)
            program.setJointCount(avatar.jointCount)

            if (avatar.jointUBO != 0) {
                GLES32.glBindBufferBase(GLES32.GL_UNIFORM_BUFFER, 1, avatar.jointUBO)
            }
            GLES32.glDrawElements(GLES32.GL_TRIANGLES, mesh.indexCount, GLES32.GL_UNSIGNED_SHORT, 0)
        }
        GLES32.glBindVertexArray(0)
    }

    fun updateJoints(id: UUID, matrices: FloatArray, count: Int) {
        val avatar = avatars[id] ?: return
        System.arraycopy(matrices, 0, avatar.jointMatrices, 0, minOf(matrices.size, avatar.jointMatrices.size))
        avatar.jointCount = count

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

    private fun ensureAvatarMesh(ctx: LumiyaRenderContext) {
        if (avatarMesh != null) return
        val asset = avatarMeshProvider?.invoke()
        if (asset == null) {
            Log.w(TAG, "Avatar mesh asset unavailable; avatars will be skipped")
            return
        }
        val bm = GLBufferManager(ctx.resourceManager)
        bufferManager = bm
        avatarMesh = bm.buildVAO(
            asset.vertices,
            asset.indices,
            listOf(0 to 3, 1 to 3, 2 to 2, 3 to 4, 4 to 4)
        )
    }

    private fun destroyAvatarResources(avatar: AvatarInstance) {
        if (avatar.jointUBO != 0) {
            GLES32.glDeleteBuffers(1, intArrayOf(avatar.jointUBO), 0)
            avatar.jointUBO = 0
        }
    }
}
