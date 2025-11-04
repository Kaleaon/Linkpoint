// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.avatar;

import android.opengl.GLES10;
import android.opengl.GLES20;
import com.lumiyaviewer.lumiya.render.drawable.DrawableFaceTexture;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer;
import com.lumiyaviewer.rawbuffers.DirectByteBuffer;

public class SLAnimatedMeshData extends SLMeshData
{
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
    
    public SLAnimatedMeshData(final SLPolyMesh slPolyMesh, final boolean b) {
        boolean animated = false;
        super(slPolyMesh);
        this.VBOLoaded = false;
        this.glBuffers = new GLLoadableBuffer[4];
        this.texCoordsDirty = false;
        this.verticesDirty = false;
        if (slPolyMesh.hasWeights) {
            animated = (b ^ true);
        }
        this.animated = animated;
        if (this.animated) {
            this.animatedVertexData = new DirectByteBuffer(this.vertexBuffer);
            this.verticesDirty = true;
        }
        else {
            this.animatedVertexData = null;
        }
    }
    
    private void setupVBOs(final RenderContext renderContext) {
        if (!this.VBOLoaded || this.texCoordsDirty || this.verticesDirty) {
            if (!this.VBOLoaded || this.verticesDirty) {
                DirectByteBuffer directByteBuffer;
                if (this.animated) {
                    directByteBuffer = this.animatedVertexData;
                }
                else {
                    directByteBuffer = this.vertexBuffer;
                }
                if (this.glBuffers[0] == null) {
                    this.glBuffers[0] = new GLLoadableBuffer(directByteBuffer);
                }
                else if (this.animated) {
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
                }
                else {
                    this.glBuffers[2].Reload(renderContext);
                }
            }
            this.VBOLoaded = true;
            this.verticesDirty = false;
            this.texCoordsDirty = false;
        }
    }
    
    public final void GLDraw(final RenderContext renderContext, final DrawableFaceTexture drawableFaceTexture) {
        boolean textureEnabled = false;
        this.setupVBOs(renderContext);
        if (drawableFaceTexture != null) {
            if (!renderContext.hasGL20) {
                GLES20.glEnable(3553);
            }
            textureEnabled = true;
            if (!drawableFaceTexture.GLDraw(renderContext)) {
                if (!renderContext.hasGL20) {
                    GLES20.glDisable(3553);
                }
                textureEnabled = false;
            }
        }
        while (true) {
            if (renderContext.hasGL20) {
                if (this.VBOLoaded) {
                    renderContext.avatarProgram.setTextureEnabled(textureEnabled);
                    if (!textureEnabled) {
                        GLES20.glBindTexture(3553, 0);
                    }
                    renderContext.glObjWorldApplyMatrix(renderContext.avatarProgram.uObjWorldMatrix);
                    if (textureEnabled) {
                        GLES20.glUniform4f(renderContext.avatarProgram.vColor, 1.0f, 1.0f, 1.0f, 1.0f);
                    }
                    else {
                        GLES20.glUniform4f(renderContext.avatarProgram.vColor, 0.5f, 0.5f, 0.5f, 1.0f);
                    }
                    this.glBuffers[0].Bind20(renderContext, renderContext.avatarProgram.vPosition, 3, 5126, 24, 0);
                    this.glBuffers[0].Bind20(renderContext, renderContext.avatarProgram.vNormal, 3, 5126, 24, 12);
                    this.glBuffers[1].BindElements20(renderContext);
                    if (textureEnabled) {
                        this.glBuffers[2].Bind20(renderContext, renderContext.avatarProgram.vTexCoord, 2, 5126, 8, 0);
                    }
                    else {
                        GLES20.glDisableVertexAttribArray(renderContext.avatarProgram.vTexCoord);
                    }
                    if (this.glBuffers[3] != null) {
                        this.glBuffers[3].Bind20(renderContext, renderContext.avatarProgram.vWeight, 1, 5126, 4, 0);
                        GLES20.glUniform1iv(renderContext.avatarProgram.uJointMap, this.referenceData.jointMap.length, this.referenceData.jointMap, 0);
                        GLES20.glUniform1i(renderContext.avatarProgram.uJointMapLength, this.referenceData.jointMap.length);
                        GLES20.glUniform1i(renderContext.avatarProgram.uUseWeight, 1);
                    }
                    else {
                        GLES20.glDisableVertexAttribArray(renderContext.avatarProgram.vWeight);
                        GLES20.glUniform1i(renderContext.avatarProgram.uUseWeight, 0);
                    }
                    this.glBuffers[1].DrawElements20(4, this.numFaces * 3, 5123, 0);
                }
            }
            else {
                DirectByteBuffer directByteBuffer;
                if (this.referenceData.hasWeights) {
                    directByteBuffer = this.animatedVertexData;
                }
                else {
                    directByteBuffer = this.vertexBuffer;
                }
                if (directByteBuffer != null) {
                    GLES10.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    this.glBuffers[0].Bind(renderContext, 32884, 3, 5126, 24, 0);
                    this.glBuffers[0].Bind(renderContext, 32885, 3, 5126, 24, 12);
                    this.glBuffers[2].Bind(renderContext, 32888, 2, 5126, 8, 0);
                    this.glBuffers[1].BindElements(renderContext);
                    this.glBuffers[1].DrawElements(renderContext, 4, this.numFaces * 3, 5123, 0);
                    GLES10.glDisableClientState(32885);
                    GLES10.glDisableClientState(32888);
                }
            }
            if (textureEnabled) {
                if (renderContext.hasGL20) {
                    GLES20.glBindTexture(3553, 0);
                }
                else {
                    GLES10.glBindTexture(3553, 0);
                    GLES10.glDisable(3553);
                }
            }
            return;
            continue;
        }
    }
    
    public DirectByteBuffer getAnimatedVertexData() {
        return this.animatedVertexData;
    }
    
    public void setVerticesDirty() {
        this.verticesDirty = true;
    }
}
