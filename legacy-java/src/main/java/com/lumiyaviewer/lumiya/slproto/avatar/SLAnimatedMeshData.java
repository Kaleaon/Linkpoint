package com.lumiyaviewer.lumiya.slproto.avatar;

import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;

/* loaded from: classes.dex */
public class SLAnimatedMeshData extends SLMeshData {
    private static final int BUF_INDEX = 1;
    private static final int BUF_TEXCOORD = 2;
    private static final int BUF_VERTEX = 0;
    private static final int BUF_WEIGHTS = 3;
    private boolean VBOLoaded;
    private final boolean animated;
    private final DirectByteBuffer animatedVertexData;
    private GLLoadableBuffer[] glBuffers;
    private boolean texCoordsDirty;
    private boolean verticesDirty;

    public SLAnimatedMeshData(SLPolyMesh sLPolyMesh, boolean z) {
        super(sLPolyMesh);
        this.VBOLoaded = false;
        this.glBuffers = new GLLoadableBuffer[4];
        this.texCoordsDirty = false;
        this.verticesDirty = false;
        this.animated = sLPolyMesh.hasWeights ? !z : false;
        if (!this.animated) {
            this.animatedVertexData = null;
        } else {
            this.animatedVertexData = new DirectByteBuffer(this.vertexBuffer);
            this.verticesDirty = true;
        }
    }

    private void setupVBOs(RenderContext renderContext) {
        if (!this.VBOLoaded || this.texCoordsDirty || this.verticesDirty) {
            if (!this.VBOLoaded || this.verticesDirty) {
                DirectByteBuffer directByteBuffer = this.animated ? this.animatedVertexData : this.vertexBuffer;
                if (this.glBuffers[0] == null) {
                    this.glBuffers[0] = new GLLoadableBuffer(directByteBuffer);
                } else if (this.animated) {
                    this.glBuffers[0].Reload(renderContext);
                }
            }
            if (!this.VBOLoaded) {
                if (renderContext.hasGL20 && this.referenceData.hasWeights) {
                    this.glBuffers[3] = new GLLoadableBuffer(this.referenceData.weightsBuffer);
                }
                this.glBuffers[1] = new GLLoadableBuffer(this.indexBuffer);
            }
            if (!this.VBOLoaded || this.texCoordsDirty) {
                if (this.glBuffers[2] == null) {
                    this.glBuffers[2] = new GLLoadableBuffer(this.texCoordsBuffer);
                } else {
                    this.glBuffers[2].Reload(renderContext);
                }
            }
            this.VBOLoaded = true;
            this.verticesDirty = false;
            this.texCoordsDirty = false;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x0025  */
    /* JADX WARN: Removed duplicated region for block: B:33:0x00e2  */
    /* JADX WARN: Removed duplicated region for block: B:40:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:41:0x0116  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public final void GLDraw(com.lumiyaviewer.lumiya.render.RenderContext r9, com.lumiyaviewer.lumiya.render.drawable.DrawableFaceTexture r10) {
        /*
            Method dump skipped, instructions count: 412
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.avatar.SLAnimatedMeshData.GLDraw(com.lumiyaviewer.lumiya.render.RenderContext, com.lumiyaviewer.lumiya.render.drawable.DrawableFaceTexture):void");
    }

    public DirectByteBuffer getAnimatedVertexData() {
        return this.animatedVertexData;
    }

    public void setVerticesDirty() {
        this.verticesDirty = true;
    }
}
