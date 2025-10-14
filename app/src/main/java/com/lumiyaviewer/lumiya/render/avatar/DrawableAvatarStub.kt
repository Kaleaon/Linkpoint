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

class DrawableAvatarStub : OnChatterNameUpdated {
    SLObjectAvatarInfo avatarObject
    private ChatterNameRetriever chatterNameRetriever
    volatile DrawableHoverText drawableNameTag
    protected DrawableStore drawableStore
    private volatile String nameTag

    DrawableAvatarStub(DrawableStore drawableStore, UUID uuid, SLObjectAvatarInfo sLObjectAvatarInfo) {
        this.drawableStore = drawableStore
        this.avatarObject = sLObjectAvatarInfo
        this.chatterNameRetriever = ChatterNameRetriever(ChatterID.getUserChatterID(uuid, sLObjectAvatarInfo.getId()), this, null)
    }

    private fun setNameTag(str: String): Unit {
        if (!Objects.equal(this.nameTag, str)) {
            this.nameTag = str
            String str2 = "DrawableAvatar: setting: nameTag = %s";
            Any[] objArr = Any[1]
            objArr[0] = str != null ? str : "null";
            Debug.Printf(str2, objArr)
            if (str != null) {
                this.drawableNameTag = DrawableHoverText(this.drawableStore.textTextureCache, str, Int.MIN_VALUE)
            }
        }
    }

    fun DrawNameTag(renderContext: RenderContext): Unit {
        DrawableHoverText drawableHoverText = this.drawableNameTag
        Float[] worldMatrix = getWorldMatrix(renderContext)
        if (drawableHoverText != null && worldMatrix != null) {
            drawableHoverText.DrawAtWorld(renderContext, worldMatrix[12], worldMatrix[13], 0.75f + worldMatrix[14], 0.5f, renderContext.projectionMatrix, false, 0)
        }
    }

    Float[] getWorldMatrix(RenderContext renderContext) {
        if (!this.avatarObject.isMyAvatar() || this.avatarObject.parentID != 0) {
            return this.avatarObject.worldMatrix
        }
        Float[] fArr = Float[32]
        LLQuaternion rotation = this.avatarObject.getRotation()
        if (rotation != null) {
            Matrix.setIdentityM(fArr, 16)
            Matrix.translateM(fArr, 16, renderContext.myAviPosition.x, renderContext.myAviPosition.y, renderContext.myAviPosition.z)
            Matrix.multiplyMM(fArr, 0, fArr, 16, rotation.getInverseMatrix(), 0)
        }
        return fArr
    }

    fun onChatterNameUpdated(chatterNameRetriever: ChatterNameRetriever): Unit {
        setNameTag(chatterNameRetriever.getResolvedName())
    }
}
