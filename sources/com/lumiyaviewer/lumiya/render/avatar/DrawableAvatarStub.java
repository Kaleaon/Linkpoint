package com.lumiyaviewer.lumiya.render.avatar;

import android.opengl.Matrix;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.DrawableStore;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import java.util.UUID;
import java.util.concurrent.Executor;

public class DrawableAvatarStub implements ChatterNameRetriever.OnChatterNameUpdated {
    final SLObjectAvatarInfo avatarObject;
    private final ChatterNameRetriever chatterNameRetriever;
    volatile DrawableHoverText drawableNameTag;
    protected final DrawableStore drawableStore;
    private volatile String nameTag;

    DrawableAvatarStub(DrawableStore drawableStore2, UUID uuid, SLObjectAvatarInfo sLObjectAvatarInfo) {
        this.drawableStore = drawableStore2;
        this.avatarObject = sLObjectAvatarInfo;
        this.chatterNameRetriever = new ChatterNameRetriever(ChatterID.getUserChatterID(uuid, sLObjectAvatarInfo.getId()), this, (Executor) null);
    }

    private void setNameTag(String str) {
        if (!Objects.equal(this.nameTag, str)) {
            this.nameTag = str;
            Object[] objArr = new Object[1];
            objArr[0] = str != null ? str : "null";
            Debug.Printf("DrawableAvatar: setting: nameTag = %s", objArr);
            if (str != null) {
                this.drawableNameTag = new DrawableHoverText(this.drawableStore.textTextureCache, str, Integer.MIN_VALUE);
            }
        }
    }

    public void DrawNameTag(RenderContext renderContext) {
        DrawableHoverText drawableHoverText = this.drawableNameTag;
        float[] worldMatrix = getWorldMatrix(renderContext);
        if (drawableHoverText != null && worldMatrix != null) {
            drawableHoverText.DrawAtWorld(renderContext, worldMatrix[12], worldMatrix[13], 0.75f + worldMatrix[14], 0.5f, renderContext.projectionMatrix, false, 0);
        }
    }

    /* access modifiers changed from: package-private */
    public float[] getWorldMatrix(RenderContext renderContext) {
        if (!this.avatarObject.isMyAvatar() || this.avatarObject.parentID != 0) {
            return this.avatarObject.worldMatrix;
        }
        float[] fArr = new float[32];
        LLQuaternion rotation = this.avatarObject.getRotation();
        if (rotation != null) {
            Matrix.setIdentityM(fArr, 16);
            Matrix.translateM(fArr, 16, renderContext.myAviPosition.x, renderContext.myAviPosition.y, renderContext.myAviPosition.z);
            Matrix.multiplyMM(fArr, 0, fArr, 16, rotation.getInverseMatrix(), 0);
        }
        return fArr;
    }

    public void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever2) {
        setNameTag(chatterNameRetriever2.getResolvedName());
    }
}
