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
    void setTexture(com.lumiyaviewer.lumiya.render.glres.textures.GLTextureCache r7, java.util.UUID r8) {
        /*
        r6 = this;
        r0 = 0;
        r2 = r6.updateLock;
        monitor-enter(r2);
        r3 = "Avatar: face %s texture %s";
        r1 = 2;
        r4 = new java.lang.Object[r1];	 Catch:{ all -> 0x0070 }
        r1 = r6.faceIndex;	 Catch:{ all -> 0x0070 }
        r1 = r1.toString();	 Catch:{ all -> 0x0070 }
        r5 = 0;
        r4[r5] = r1;	 Catch:{ all -> 0x0070 }
        if (r8 == 0) goto L_0x0038;
    L_0x0015:
        r1 = r8.toString();	 Catch:{ all -> 0x0070 }
    L_0x0019:
        r5 = 1;
        r4[r5] = r1;	 Catch:{ all -> 0x0070 }
        com.lumiyaviewer.lumiya.Debug.Printf(r3, r4);	 Catch:{ all -> 0x0070 }
        if (r8 == 0) goto L_0x002a;
    L_0x0021:
        r1 = com.lumiyaviewer.lumiya.utils.UUIDPool.ZeroUUID;	 Catch:{ all -> 0x0070 }
        r1 = r8.equals(r1);	 Catch:{ all -> 0x0070 }
        if (r1 == 0) goto L_0x003c;
    L_0x0029:
        r8 = r0;
    L_0x002a:
        r0 = r6.textureUUID;	 Catch:{ all -> 0x0070 }
        if (r0 == 0) goto L_0x0046;
    L_0x002e:
        if (r8 == 0) goto L_0x004a;
    L_0x0030:
        r0 = r0.equals(r8);	 Catch:{ all -> 0x0070 }
        if (r0 == 0) goto L_0x004a;
    L_0x0036:
        monitor-exit(r2);
        return;
    L_0x0038:
        r1 = "null";
        goto L_0x0019;
    L_0x003c:
        r1 = DEFAULT_AVATAR_TEXTURE;	 Catch:{ all -> 0x0070 }
        r1 = r8.equals(r1);	 Catch:{ all -> 0x0070 }
        if (r1 == 0) goto L_0x002a;
    L_0x0044:
        r8 = r0;
        goto L_0x002a;
    L_0x0046:
        if (r8 != 0) goto L_0x004a;
    L_0x0048:
        monitor-exit(r2);
        return;
    L_0x004a:
        r6.textureUUID = r8;	 Catch:{ all -> 0x0070 }
        if (r8 == 0) goto L_0x0066;
    L_0x004e:
        r0 = r6.faceIndex;	 Catch:{ all -> 0x0070 }
        r1 = r6.avatarUUID;	 Catch:{ all -> 0x0070 }
        r0 = com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams.create(r8, r0, r1);	 Catch:{ all -> 0x0070 }
        r1 = com.lumiyaviewer.lumiya.res.textures.TextureCache.getInstance();	 Catch:{ all -> 0x0070 }
        r1.RequestResource(r0, r6);	 Catch:{ all -> 0x0070 }
        r1 = new com.lumiyaviewer.lumiya.render.drawable.DrawableFaceTexture;	 Catch:{ all -> 0x0070 }
        r1.<init>(r0);	 Catch:{ all -> 0x0070 }
        r6.texture = r1;	 Catch:{ all -> 0x0070 }
    L_0x0064:
        monitor-exit(r2);
        return;
    L_0x0066:
        r0 = 0;
        r6.texture = r0;	 Catch:{ all -> 0x0070 }
        r0 = 0;
        r6.meshData = r0;	 Catch:{ all -> 0x0070 }
        r0 = 0;
        r6.rawTexture = r0;	 Catch:{ all -> 0x0070 }
        goto L_0x0064;
    L_0x0070:
        r0 = move-exception;
        monitor-exit(r2);
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarPart.setTexture(com.lumiyaviewer.lumiya.render.glres.textures.GLTextureCache, java.util.UUID):void");
    }
}
