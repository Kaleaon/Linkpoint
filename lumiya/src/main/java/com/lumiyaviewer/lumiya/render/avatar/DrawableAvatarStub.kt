package com.lumiyaviewer.lumiya.render.avatar

import android.opengl.Matrix
import com.google.common.base.Objects
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.render.DrawableStore
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion
import com.lumiyaviewer.lumiya.slproto.users.ChatterID
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated
import java.util.UUID

class DrawableAvatarStub(
    protected val drawableStore: DrawableStore,
    agentUUID: UUID,
    val avatarObject: SLObjectAvatarInfo
) : OnChatterNameUpdated {
    
    private val chatterNameRetriever: ChatterNameRetriever
    
    @Volatile
    var drawableNameTag: DrawableHoverText? = null
    
    @Volatile
    private var nameTag: String? = null

    init {
        // Using getUserChatterID with avatar ID as per decompiled code
        this.chatterNameRetriever = ChatterNameRetriever(
            ChatterID.getUserChatterID(agentUUID, avatarObject.getId()), 
            this, 
            null
        )
    }

    private fun setNameTag(str: String?) {
        if (!Objects.equal(this.nameTag, str)) {
            this.nameTag = str
            Debug.Printf("DrawableAvatar: setting: nameTag = %s", str ?: "null")
            if (str != null) {
                // Stubbing textTextureCache access if not available
                // this.drawableNameTag = DrawableHoverText(this.drawableStore.textTextureCache, str, Int.MIN_VALUE)
            }
        }
    }

    fun DrawNameTag(renderContext: RenderContext) {
        val nameTag = this.drawableNameTag
        val worldMatrix = getWorldMatrix(renderContext)
        if (nameTag != null && worldMatrix != null) {
            nameTag.DrawAtWorld(
                renderContext, 
                worldMatrix[12], 
                worldMatrix[13], 
                0.75f + worldMatrix[14], 
                0.5f, 
                renderContext.projectionMatrix, 
                false, 
                0
            )
        }
    }

    fun getWorldMatrix(renderContext: RenderContext): FloatArray? {
        if (!this.avatarObject.isMyAvatar() || this.avatarObject.parentID != 0L) { // Note: parentID is long in decompiled code logic (check SLObjectAvatarInfo)
            return this.avatarObject.worldMatrix
        }
        val matrix = FloatArray(32)
        val rotation = this.avatarObject.getRotation()
        if (rotation != null) {
            Matrix.setIdentityM(matrix, 16)
            // Using myAviPosition from renderContext, likely needs accessor or public field
            // Matrix.translateM(matrix, 16, renderContext.myAviPosition.x, renderContext.myAviPosition.y, renderContext.myAviPosition.z)
            // Matrix.multiplyMM(matrix, 0, matrix, 16, rotation.getInverseMatrix(), 0)
        }
        return matrix
    }

    override fun onChatterNameUpdated(chatterNameRetriever: ChatterNameRetriever) {
        setNameTag(chatterNameRetriever.getResolvedName())
    }
}
