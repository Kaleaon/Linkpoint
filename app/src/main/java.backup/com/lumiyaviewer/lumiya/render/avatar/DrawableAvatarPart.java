package com.lumiyaviewer.lumiya.render.avatar;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.GLTexture;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.drawable.DrawableFaceTexture;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor;
import com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAnimatedMeshData;
import com.lumiyaviewer.lumiya.slproto.avatar.SLMeshData;
import com.lumiyaviewer.lumiya.slproto.avatar.SLPolyMesh;
import java.util.Arrays;
import java.util.UUID;

public class DrawableAvatarPart implements ResourceConsumer {
    public static final UUID DEFAULT_AVATAR_TEXTURE = UUID.fromString("c228d1cf-4b5d-4ba8-84f4-899a0796aa97");
    private final UUID avatarUUID;
    private final AvatarTextureFaceIndex faceIndex;
    private final boolean hasGL20;
    private volatile SLAnimatedMeshData meshData;
    private volatile boolean meshDataUpdated;
    private final Runnable meshUpdate = new Runnable() {
        public void run() {
            GLTexture -get3;
            float[] -get2;
            Debug.Printf("Avatar: meshUpdate entered for part %s", DrawableAvatarPart.this.faceIndex.toString());
            synchronized (DrawableAvatarPart.this.updateLock) {
                -get3 = DrawableAvatarPart.this.rawTexture;
                -get2 = DrawableAvatarPart.this.partMorphParams;
            }
            if (-get2 != null && -get3 != null) {
                Debug.Printf("Avatar: meshUpdate: part %s params %s", DrawableAvatarPart.this.faceIndex.toString(), Arrays.toString(-get2));
                SLMeshData sLAnimatedMeshData = new SLAnimatedMeshData(DrawableAvatarPart.this.referenceMeshData, DrawableAvatarPart.this.hasGL20);
                DrawableAvatarPart.this.referenceMeshData.applyMorphData(sLAnimatedMeshData, -get2, -get3);
                synchronized (DrawableAvatarPart.this.updateLock) {
                    DrawableAvatarPart.this.meshData = sLAnimatedMeshData;
                    DrawableAvatarPart.this.meshDataUpdated = true;
                }
            }
        }
    };
    private volatile float[] partMorphParams;
    private volatile OpenJPEG rawTexture;
    private final SLPolyMesh referenceMeshData;
    private volatile DrawableFaceTexture texture;
    private volatile UUID textureUUID;
    private final Object updateLock = new Object();

    DrawableAvatarPart(UUID uuid, AvatarTextureFaceIndex avatarTextureFaceIndex, SLPolyMesh sLPolyMesh, boolean z) {
        this.avatarUUID = uuid;
        this.faceIndex = avatarTextureFaceIndex;
        this.referenceMeshData = sLPolyMesh;
        this.hasGL20 = z;
    }

    private void RequestMeshUpdate() {
        PrimComputeExecutor.getInstance().execute(this.meshUpdate);
    }

    public final void GLDraw(RenderContext renderContext, float[] fArr, boolean z) {
        SLAnimatedMeshData sLAnimatedMeshData;
        DrawableFaceTexture drawableFaceTexture;
        if (renderContext.hasGL20) {
            z = false;
        }
        synchronized (this.updateLock) {
            sLAnimatedMeshData = this.meshData;
            drawableFaceTexture = this.texture;
            if (!(sLAnimatedMeshData == null || !this.meshDataUpdated || fArr == null)) {
                this.meshDataUpdated = false;
                z = true;
            }
        }
        if (sLAnimatedMeshData != null) {
            if (z) {
                this.referenceMeshData.applySkeleton(sLAnimatedMeshData, fArr);
                sLAnimatedMeshData.setVerticesDirty();
            }
            sLAnimatedMeshData.GLDraw(renderContext, drawableFaceTexture);
        }
    }

    public void OnResourceReady(Object obj, boolean z) {
        String str = "Avatar: (requesting meshUpdate) face %s texture %s";
        Object[] objArr = new Object[2];
        objArr[0] = this.faceIndex.toString();
        objArr[1] = obj != null ? obj.toString() : "null";
        Debug.Printf(str, objArr);
        if (obj instanceof OpenJPEG) {
            synchronized (this.updateLock) {
                this.rawTexture = (OpenJPEG) obj;
            }
            RequestMeshUpdate();
        }
    }

    public AvatarTextureFaceIndex getFaceIndex() {
        return this.faceIndex;
    }

    void setPartMorphParams(float[] fArr) {
        int equals;
        synchronized (this.updateLock) {
            equals = Arrays.equals(this.partMorphParams, fArr) ^ 1;
            if (equals != 0) {
                this.partMorphParams = fArr;
            }
        }
        if (equals != 0) {
            Debug.Printf("Avatar: (requesting meshUpdate) new morphParams for part %s", this.faceIndex.toString());
            RequestMeshUpdate();
        }
    }

    /* DevToolsApp WARNING: Missing block: B:31:0x0065, code:
            return;
     */
    void setTexture(com.lumiyaviewer.lumiya.render.glres.textures.GLTextureCache glTextureCache, UUID textureUUID) {
        synchronized (this.updateLock) {
            // Log texture change for debugging
            Debug.Printf("Avatar: face %s texture %s", 
                this.faceIndex.toString(), 
                textureUUID != null ? textureUUID.toString() : "null");
            
            // Handle null or zero UUID by setting to null or default avatar texture
            if (textureUUID != null && com.lumiyaviewer.lumiya.utils.UUIDPool.ZeroUUID.equals(textureUUID)) {
                textureUUID = null;
            }
            
            // If texture hasn't changed, return early
            if (this.textureUUID != null ? this.textureUUID.equals(textureUUID) : textureUUID == null) {
                return;
            }
            
            // If setting null texture, clear all texture-related data
            if (textureUUID == null) {
                this.textureUUID = null;
                this.texture = null;
                this.meshData = null;
                this.rawTexture = null;
                return;
            }
            
            // Update texture UUID and request new texture
            this.textureUUID = textureUUID;
            
            // Create texture parameters for this avatar part
            com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams textureParams = 
                com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams.create(
                    textureUUID, this.faceIndex, this.avatarUUID);
            
            // Request the texture from cache
            com.lumiyaviewer.lumiya.res.textures.TextureCache.getInstance()
                .RequestResource(textureParams, this);
            
            // Create the drawable face texture
            this.texture = new DrawableFaceTexture(textureParams);
        }
    }
}
