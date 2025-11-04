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

/* loaded from: classes.dex */
public class DrawableAvatarStub implements ChatterNameRetriever.OnChatterNameUpdated {
    final SLObjectAvatarInfo avatarObject;
    private final ChatterNameRetriever chatterNameRetriever;
    volatile DrawableHoverText drawableNameTag;
    protected final DrawableStore drawableStore;
    private volatile String nameTag;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DrawableAvatarStub(DrawableStore drawableStore, UUID uuid, SLObjectAvatarInfo sLObjectAvatarInfo) {
        this.drawableStore = drawableStore;
        this.avatarObject = sLObjectAvatarInfo;
        this.chatterNameRetriever = new ChatterNameRetriever(ChatterID.getUserChatterID(uuid, sLObjectAvatarInfo.getId()), this, null);
    }

    private void setNameTag(String str) {
        if (Objects.equal(this.nameTag, str)) {
            return;
        }
        this.nameTag = str;
        Object[] objArr = new Object[1];
        objArr[0] = str != null ? str : "null";
        Debug.Printf("DrawableAvatar: setting: nameTag = %s", objArr);
        if (str != null) {
            this.drawableNameTag = new DrawableHoverText(this.drawableStore.textTextureCache, str, Integer.MIN_VALUE);
        }
    }

    public void DrawNameTag(RenderContext renderContext) {
        DrawableHoverText drawableHoverText = this.drawableNameTag;
        float[] worldMatrix = getWorldMatrix(renderContext);
        if (drawableHoverText == null || worldMatrix == null) {
            return;
        }
        drawableHoverText.DrawAtWorld(renderContext, worldMatrix[12], worldMatrix[13], 0.75f + worldMatrix[14], 0.5f, renderContext.projectionMatrix, false, 0);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
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

    @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated
    public void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
        setNameTag(chatterNameRetriever.getResolvedName());
    }
}
