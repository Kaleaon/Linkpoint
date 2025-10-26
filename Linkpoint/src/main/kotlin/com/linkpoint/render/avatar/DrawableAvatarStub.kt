package com.linkpoint.render.avatar

import android.opengl.Matrix
import com.google.common.base.Objects
import com.linkpoint.Debug
import com.linkpoint.render.DrawableStore
import com.linkpoint.render.RenderContext
import com.linkpoint.slproto.objects.SLObjectAvatarInfo
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.ChatterNameRetriever
import com.linkpoint.slproto.users.ChatterNameRetriever.OnChatterNameUpdated
import java.util.UUID

class DrawableAvatarStub : OnChatterNameUpdated {
    final SLObjectAvatarInfo avatarObject
    private val ChatterNameRetriever chatterNameRetriever
    volatile DrawableHoverText drawableNameTag
    protected val DrawableStore drawableStore
    private volatile String nameTag

    DrawableAvatarStub(DrawableStore drawableStore, UUID uuid, SLObjectAvatarInfo sLObjectAvatarInfo) {
        this.drawableStore = drawableStore
        this.avatarObject = sLObjectAvatarInfo
        this.chatterNameRetriever = ChatterNameRetriever(ChatterID.getUserChatterID(uuid, sLObjectAvatarInfo.getId()), this, null)
    }

     private fun setNameTag(str: String) {
        if (!Objects.equal(this.nameTag, str)) {
            this.nameTag = str
            val str2: String = "DrawableAvatar: setting: nameTag = %s"
            val objArr: Array<Any> = Object[1]
            objArr[0] = str != null ? str : "null"
            Debug.Printf(str2, objArr)
            if (str != null) {
                this.drawableNameTag = DrawableHoverText(this.drawableStore.textTextureCache, str, Integer.MIN_VALUE)
            }
        }
    }

    fun DrawNameTag(renderContext: RenderContext) {
        val drawableHoverText: DrawableHoverText = this.drawableNameTag
        val worldMatrix: FloatArray = getWorldMatrix(renderContext)
        if (drawableHoverText != null && worldMatrix != null) {
            drawableHoverText.DrawAtWorld(renderContext, worldMatrix[12], worldMatrix[13], 0.75f + worldMatrix[14], 0.5f, renderContext.projectionMatrix, false, 0)
        }
    }

     fun getWorldMatrix(renderContext: RenderContext): FloatArray {
        if (!this.avatarObject.isMyAvatar() || this.avatarObject.parentID != 0) {
            return this.avatarObject.worldMatrix
        }
        val fArr: FloatArray = Float[32]
        val rotation: LLQuaternion = this.avatarObject.getRotation()
        if (rotation != null) {
            Matrix.setIdentityM(fArr, 16)
            Matrix.translateM(fArr, 16, renderContext.myAviPosition.x, renderContext.myAviPosition.y, renderContext.myAviPosition.z)
            Matrix.multiplyMM(fArr, 0, fArr, 16, rotation.getInverseMatrix(), 0)
        }
        return fArr
    }

    fun onChatterNameUpdated(chatterNameRetriever: ChatterNameRetriever) {
        setNameTag(chatterNameRetriever.getResolvedName())
    }
}
