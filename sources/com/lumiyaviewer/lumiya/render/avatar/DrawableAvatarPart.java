package com.lumiyaviewer.lumiya.render.avatar;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.drawable.DrawableFaceTexture;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor;
import com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex;
import com.lumiyaviewer.lumiya.slproto.avatar.SLAnimatedMeshData;
import com.lumiyaviewer.lumiya.slproto.avatar.SLPolyMesh;
import java.util.Arrays;
import java.util.UUID;

public class DrawableAvatarPart implements ResourceConsumer {
    public static final UUID DEFAULT_AVATAR_TEXTURE = UUID.fromString("c228d1cf-4b5d-4ba8-84f4-899a0796aa97");
    private final UUID avatarUUID;
    /* access modifiers changed from: private */
    public final AvatarTextureFaceIndex faceIndex;
    /* access modifiers changed from: private */
    public final boolean hasGL20;
    /* access modifiers changed from: private */
    public volatile SLAnimatedMeshData meshData;
    /* access modifiers changed from: private */
    public volatile boolean meshDataUpdated;
    private final Runnable meshUpdate = new Runnable() {
        public void run() {
            OpenJPEG r0;
            float[] r2;
            Debug.Printf("Avatar: meshUpdate entered for part %s", DrawableAvatarPart.this.faceIndex.toString());
            synchronized (DrawableAvatarPart.this.updateLock) {
                r0 = DrawableAvatarPart.this.rawTexture;
                r2 = DrawableAvatarPart.this.partMorphParams;
            }
            if (r2 != null && r0 != null) {
                Debug.Printf("Avatar: meshUpdate: part %s params %s", DrawableAvatarPart.this.faceIndex.toString(), Arrays.toString(r2));
                SLAnimatedMeshData sLAnimatedMeshData = new SLAnimatedMeshData(DrawableAvatarPart.this.referenceMeshData, DrawableAvatarPart.this.hasGL20);
                DrawableAvatarPart.this.referenceMeshData.applyMorphData(sLAnimatedMeshData, r2, r0);
                synchronized (DrawableAvatarPart.this.updateLock) {
                    SLAnimatedMeshData unused = DrawableAvatarPart.this.meshData = sLAnimatedMeshData;
                    boolean unused2 = DrawableAvatarPart.this.meshDataUpdated = true;
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public volatile float[] partMorphParams;
    /* access modifiers changed from: private */
    public volatile OpenJPEG rawTexture;
    /* access modifiers changed from: private */
    public final SLPolyMesh referenceMeshData;
    private volatile DrawableFaceTexture texture;
    private volatile UUID textureUUID;
    /* access modifiers changed from: private */
    public final Object updateLock = new Object();

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

    /* access modifiers changed from: package-private */
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

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0065, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setTexture(com.lumiyaviewer.lumiya.render.glres.textures.GLTextureCache r7, java.util.UUID r8) {
        /*
            r6 = this;
            r0 = 0
            java.lang.Object r2 = r6.updateLock
            monitor-enter(r2)
            java.lang.String r3 = "Avatar: face %s texture %s"
            r1 = 2
            java.lang.Object[] r4 = new java.lang.Object[r1]     // Catch:{ all -> 0x0070 }
            com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex r1 = r6.faceIndex     // Catch:{ all -> 0x0070 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0070 }
            r5 = 0
            r4[r5] = r1     // Catch:{ all -> 0x0070 }
            if (r8 == 0) goto L_0x0038
            java.lang.String r1 = r8.toString()     // Catch:{ all -> 0x0070 }
        L_0x0019:
            r5 = 1
            r4[r5] = r1     // Catch:{ all -> 0x0070 }
            com.lumiyaviewer.lumiya.Debug.Printf(r3, r4)     // Catch:{ all -> 0x0070 }
            if (r8 == 0) goto L_0x002a
            java.util.UUID r1 = com.lumiyaviewer.lumiya.utils.UUIDPool.ZeroUUID     // Catch:{ all -> 0x0070 }
            boolean r1 = r8.equals(r1)     // Catch:{ all -> 0x0070 }
            if (r1 == 0) goto L_0x003c
            r8 = r0
        L_0x002a:
            java.util.UUID r0 = r6.textureUUID     // Catch:{ all -> 0x0070 }
            if (r0 == 0) goto L_0x0046
            if (r8 == 0) goto L_0x004a
            boolean r0 = r0.equals(r8)     // Catch:{ all -> 0x0070 }
            if (r0 == 0) goto L_0x004a
            monitor-exit(r2)
            return
        L_0x0038:
            java.lang.String r1 = "null"
            goto L_0x0019
        L_0x003c:
            java.util.UUID r1 = DEFAULT_AVATAR_TEXTURE     // Catch:{ all -> 0x0070 }
            boolean r1 = r8.equals(r1)     // Catch:{ all -> 0x0070 }
            if (r1 == 0) goto L_0x002a
            r8 = r0
            goto L_0x002a
        L_0x0046:
            if (r8 != 0) goto L_0x004a
            monitor-exit(r2)
            return
        L_0x004a:
            r6.textureUUID = r8     // Catch:{ all -> 0x0070 }
            if (r8 == 0) goto L_0x0066
            com.lumiyaviewer.lumiya.slproto.avatar.AvatarTextureFaceIndex r0 = r6.faceIndex     // Catch:{ all -> 0x0070 }
            java.util.UUID r1 = r6.avatarUUID     // Catch:{ all -> 0x0070 }
            com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams r0 = com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams.create(r8, r0, r1)     // Catch:{ all -> 0x0070 }
            com.lumiyaviewer.lumiya.res.textures.TextureCache r1 = com.lumiyaviewer.lumiya.res.textures.TextureCache.getInstance()     // Catch:{ all -> 0x0070 }
            r1.RequestResource(r0, r6)     // Catch:{ all -> 0x0070 }
            com.lumiyaviewer.lumiya.render.drawable.DrawableFaceTexture r1 = new com.lumiyaviewer.lumiya.render.drawable.DrawableFaceTexture     // Catch:{ all -> 0x0070 }
            r1.<init>(r0)     // Catch:{ all -> 0x0070 }
            r6.texture = r1     // Catch:{ all -> 0x0070 }
        L_0x0064:
            monitor-exit(r2)
            return
        L_0x0066:
            r0 = 0
            r6.texture = r0     // Catch:{ all -> 0x0070 }
            r0 = 0
            r6.meshData = r0     // Catch:{ all -> 0x0070 }
            r0 = 0
            r6.rawTexture = r0     // Catch:{ all -> 0x0070 }
            goto L_0x0064
        L_0x0070:
            r0 = move-exception
            monitor-exit(r2)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarPart.setTexture(com.lumiyaviewer.lumiya.render.glres.textures.GLTextureCache, java.util.UUID):void");
    }
}
