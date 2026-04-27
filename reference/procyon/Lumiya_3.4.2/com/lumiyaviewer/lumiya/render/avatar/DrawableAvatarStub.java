// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.avatar;

import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.Debug;
import com.google.common.base.Objects;
import java.util.concurrent.Executor;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import java.util.UUID;
import com.lumiyaviewer.lumiya.render.DrawableStore;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;

public class DrawableAvatarStub implements OnChatterNameUpdated
{
    final SLObjectAvatarInfo avatarObject;
    private final ChatterNameRetriever chatterNameRetriever;
    volatile DrawableHoverText drawableNameTag;
    protected final DrawableStore drawableStore;
    private volatile String nameTag;
    
    DrawableAvatarStub(final DrawableStore drawableStore, final UUID uuid, final SLObjectAvatarInfo avatarObject) {
        this.drawableStore = drawableStore;
        this.avatarObject = avatarObject;
        this.chatterNameRetriever = new ChatterNameRetriever(ChatterID.getUserChatterID(uuid, avatarObject.getId()), (ChatterNameRetriever.OnChatterNameUpdated)this, null);
    }
    
    private void setNameTag(final String nameTag) {
        if (!Objects.equal(this.nameTag, nameTag)) {
            String s;
            if ((this.nameTag = nameTag) != null) {
                s = nameTag;
            }
            else {
                s = "null";
            }
            Debug.Printf("DrawableAvatar: setting: nameTag = %s", s);
            if (nameTag != null) {
                this.drawableNameTag = new DrawableHoverText(this.drawableStore.textTextureCache, nameTag, Integer.MIN_VALUE);
            }
        }
    }
    
    public void DrawNameTag(final RenderContext renderContext) {
        final DrawableHoverText drawableNameTag = this.drawableNameTag;
        final float[] worldMatrix = this.getWorldMatrix(renderContext);
        if (drawableNameTag != null && worldMatrix != null) {
            drawableNameTag.DrawAtWorld(renderContext, worldMatrix[12], worldMatrix[13], 0.75f + worldMatrix[14], 0.5f, renderContext.projectionMatrix, false, 0);
        }
    }
    
    float[] getWorldMatrix(final RenderContext renderContext) {
        if (this.avatarObject.isMyAvatar() && this.avatarObject.parentID == 0) {
            final float[] array = new float[32];
            final LLQuaternion rotation = this.avatarObject.getRotation();
            if (rotation != null) {
                Matrix.setIdentityM(array, 16);
                Matrix.translateM(array, 16, renderContext.myAviPosition.x, renderContext.myAviPosition.y, renderContext.myAviPosition.z);
                Matrix.multiplyMM(array, 0, array, 16, rotation.getInverseMatrix(), 0);
            }
            return array;
        }
        return this.avatarObject.worldMatrix;
    }
    
    @Override
    public void onChatterNameUpdated(final ChatterNameRetriever chatterNameRetriever) {
        this.setNameTag(chatterNameRetriever.getResolvedName());
    }
}
