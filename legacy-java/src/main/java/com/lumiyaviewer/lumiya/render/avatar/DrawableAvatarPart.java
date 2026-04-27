package com.lumiyaviewer.lumiya.render.avatar;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.drawable.DrawableFaceTexture;
import com.lumiyaviewer.lumiya.render.glres.textures.GLTextureCache;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAnimatedMeshData;
import com.lumiyaviewer.lumiya.slproto.avatar.SLPolyMesh;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.Arrays;
import java.util.UUID;

/* loaded from: classes.dex */
public class DrawableAvatarPart implements ResourceConsumer {
    public static final UUID DEFAULT_AVATAR_TEXTURE = UUID.fromString("c228d1cf-4b5d-4ba8-84f4-899a0796aa97");
    private final UUID avatarUUID;
    private final AvatarTextureFaceIndex faceIndex;
    private final boolean hasGL20;
    private volatile SLAnimatedMeshData meshData;
    private volatile boolean meshDataUpdated;
    private volatile float[] partMorphParams;
    private volatile OpenJPEG rawTexture;
    private final SLPolyMesh referenceMeshData;
    private volatile DrawableFaceTexture texture;
    private volatile UUID textureUUID;
    private final Object updateLock = new Object();
    private final Runnable meshUpdate = new Runnable() { // from class: com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarPart.1
        @Override // java.lang.Runnable
        public void run() {
            OpenJPEG openJPEG;
            float[] fArr;
            Debug.Printf("Avatar: meshUpdate entered for part %s", DrawableAvatarPart.this.faceIndex.toString());
            synchronized (DrawableAvatarPart.this.updateLock) {
                openJPEG = DrawableAvatarPart.this.rawTexture;
                fArr = DrawableAvatarPart.this.partMorphParams;
            }
            if (fArr == null || openJPEG == null) {
                return;
            }
            Debug.Printf("Avatar: meshUpdate: part %s params %s", DrawableAvatarPart.this.faceIndex.toString(), Arrays.toString(fArr));
            SLAnimatedMeshData sLAnimatedMeshData = new SLAnimatedMeshData(DrawableAvatarPart.this.referenceMeshData, DrawableAvatarPart.this.hasGL20);
            DrawableAvatarPart.this.referenceMeshData.applyMorphData(sLAnimatedMeshData, fArr, openJPEG);
            synchronized (DrawableAvatarPart.this.updateLock) {
                DrawableAvatarPart.this.meshData = sLAnimatedMeshData;
                DrawableAvatarPart.this.meshDataUpdated = true;
            }
        }
    };

    /* JADX INFO: Access modifiers changed from: package-private */
    public DrawableAvatarPart(UUID uuid, AvatarTextureFaceIndex avatarTextureFaceIndex, SLPolyMesh sLPolyMesh, boolean z) {
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
            if (sLAnimatedMeshData != null && this.meshDataUpdated && fArr != null) {
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

    @Override // com.lumiyaviewer.lumiya.res.ResourceConsumer
    public void OnResourceReady(Object obj, boolean z) {
        Object[] objArr = new Object[2];
        objArr[0] = this.faceIndex.toString();
        objArr[1] = obj != null ? obj.toString() : "null";
        Debug.Printf("Avatar: (requesting meshUpdate) face %s texture %s", objArr);
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

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPartMorphParams(float[] fArr) {
        boolean z;
        synchronized (this.updateLock) {
            z = !Arrays.equals(this.partMorphParams, fArr);
            if (z) {
                this.partMorphParams = fArr;
            }
        }
        if (z) {
            Debug.Printf("Avatar: (requesting meshUpdate) new morphParams for part %s", this.faceIndex.toString());
            RequestMeshUpdate();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setTexture(GLTextureCache gLTextureCache, UUID uuid) {
        synchronized (this.updateLock) {
            Object[] objArr = new Object[2];
            objArr[0] = this.faceIndex.toString();
            objArr[1] = uuid != null ? uuid.toString() : "null";
            Debug.Printf("Avatar: face %s texture %s", objArr);
            if (uuid != null) {
                if (uuid.equals(UUIDPool.ZeroUUID)) {
                    uuid = null;
                } else if (uuid.equals(DEFAULT_AVATAR_TEXTURE)) {
                    uuid = null;
                }
            }
            UUID uuid2 = this.textureUUID;
            if (uuid2 != null) {
                if (uuid != null && uuid2.equals(uuid)) {
                    return;
                }
            } else if (uuid == null) {
                return;
            }
            this.textureUUID = uuid;
            if (uuid != null) {
                DrawableTextureParams create = DrawableTextureParams.create(uuid, this.faceIndex, this.avatarUUID);
                TextureCache.getInstance().RequestResource(create, this);
                this.texture = new DrawableFaceTexture(create);
            } else {
                this.texture = null;
                this.meshData = null;
                this.rawTexture = null;
            }
        }
    }
}
