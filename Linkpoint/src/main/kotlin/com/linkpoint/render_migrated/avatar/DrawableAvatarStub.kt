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

    private Unit setNameTag(String str) {
        if (!Objects.equal(this.nameTag, str)) {
            this.nameTag = str
            String str2 = "DrawableAvatar: setting: nameTag = %s"
            Object[] objArr = Object[1]
            objArr[0] = str != null ? str : "null"
            Debug.Printf(str2, objArr)
            if (str != null) {
                this.drawableNameTag = DrawableHoverText(this.drawableStore.textTextureCache, str, Integer.MIN_VALUE)
            }
        }
    }

    public Unit DrawNameTag(RenderContext renderContext) {
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

    public Unit onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
        setNameTag(chatterNameRetriever.getResolvedName())
    }
}
