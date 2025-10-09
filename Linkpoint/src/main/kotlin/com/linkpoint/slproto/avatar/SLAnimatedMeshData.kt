package com.linkpoint.slproto.avatar
import java.util.*

import com.linkpoint.render.RenderContext
import com.linkpoint.render.glres.buffers.GLLoadableBuffer
import com.linkpoint.rawbuffers.DirectByteBuffer

class SLAnimatedMeshData : SLMeshData() {
    private const val BUF_INDEX: Int = 1
    private const val BUF_TEXCOORD: Int = 2
    private const val BUF_VERTEX: Int = 0
    private const val BUF_WEIGHTS: Int = 3
    private Boolean VBOLoaded = false
    private val Boolean animated
    private val DirectByteBuffer animatedVertexData
    private GLLoadableBuffer[] glBuffers = GLLoadableBuffer[4]
    private Boolean texCoordsDirty = false
    private Boolean verticesDirty = false

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public SLAnimatedMeshData(SLPolyMesh sLPolyMesh, Boolean z) {
        super(sLPolyMesh)
        Boolean z2 = false
        this.animated = sLPolyMesh.hasWeights ? !z : z2
        if (this.animated) {
            this.animatedVertexData = DirectByteBuffer(this.vertexBuffer)
            this.verticesDirty = true
            return
        }
        this.animatedVertexData = null
    }

    private Unit setupVBOs(RenderContext renderContext) {
        if (!this.VBOLoaded || this.texCoordsDirty || this.verticesDirty) {
            if (!this.VBOLoaded || this.verticesDirty) {
                DirectByteBuffer directByteBuffer = this.animated ? this.animatedVertexData : this.vertexBuffer
                if (this.glBuffers[0] == null) {
                    this.glBuffers[0] = GLLoadableBuffer(directByteBuffer)
                } else if (this.animated) {
                    this.glBuffers[0].Reload(renderContext)
                }
            }
            if (!this.VBOLoaded) {
                if (renderContext.hasGL20 && this.referenceData.hasWeights) {
                    this.glBuffers[3] = GLLoadableBuffer(this.referenceData.weightsBuffer)
                }
                this.glBuffers[1] = GLLoadableBuffer(this.indexBuffer)
            }
            if (!this.VBOLoaded || this.texCoordsDirty) {
                if (this.glBuffers[2] == null) {
                    this.glBuffers[2] = GLLoadableBuffer(this.texCoordsBuffer)
                } else {
                    this.glBuffers[2].Reload(renderContext)
                }
            }
            this.VBOLoaded = true
            this.verticesDirty = false
            this.texCoordsDirty = false
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x0025  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00e2  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0116  */
    /* JADX WARNING: Removed duplicated region for block: B:44:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    val Unit GLDraw(com.lumiyaviewer.lumiya.render.RenderContext r9, com.lumiyaviewer.lumiya.render.drawable.DrawableFaceTexture r10) {
        /*
            r8 = this
            r0 = 0
            r8.setupVBOs(r9)
            if (r10 == 0) goto L_0x0199
            Boolean r0 = r9.hasGL20
            if (r0 != 0) goto L_0x000f
            r0 = 3553(0xde1, Float:4.979E-42)
            android.opengl.GLES20.glEnable(r0)
        L_0x000f:
            r0 = 1
            Boolean r1 = r10.GLDraw(r9)
            if (r1 != 0) goto L_0x0199
            Boolean r0 = r9.hasGL20
            if (r0 != 0) goto L_0x001f
            r0 = 3553(0xde1, Float:4.979E-42)
            android.opengl.GLES20.glDisable(r0)
        L_0x001f:
            r0 = 0
            r7 = r0
        L_0x0021:
            Boolean r0 = r9.hasGL20
            if (r0 == 0) goto L_0x0116
            Boolean r0 = r8.VBOLoaded
            if (r0 == 0) goto L_0x00e0
            com.lumiyaviewer.lumiya.render.shaders.AvatarProgram r0 = r9.avatarProgram
            r0.setTextureEnabled(r7)
            if (r7 != 0) goto L_0x0036
            r0 = 3553(0xde1, Float:4.979E-42)
            r1 = 0
            android.opengl.GLES20.glBindTexture(r0, r1)
        L_0x0036:
            com.lumiyaviewer.lumiya.render.shaders.AvatarProgram r0 = r9.avatarProgram
            Int r0 = r0.uObjWorldMatrix
            r9.glObjWorldApplyMatrix(r0)
            if (r7 == 0) goto L_0x00ed
            com.lumiyaviewer.lumiya.render.shaders.AvatarProgram r0 = r9.avatarProgram
            Int r0 = r0.vColor
            r1 = 1065353216(0x3f800000, Float:1.0)
            r2 = 1065353216(0x3f800000, Float:1.0)
            r3 = 1065353216(0x3f800000, Float:1.0)
            r4 = 1065353216(0x3f800000, Float:1.0)
            android.opengl.GLES20.glUniform4f(r0, r1, r2, r3, r4)
        L_0x004e:
            com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer[] r0 = r8.glBuffers
            r1 = 0
            r0 = r0[r1]
            com.lumiyaviewer.lumiya.render.shaders.AvatarProgram r1 = r9.avatarProgram
            Int r2 = r1.vPosition
            r3 = 3
            r4 = 5126(0x1406, Float:7.183E-42)
            r5 = 24
            r6 = 0
            r1 = r9
            r0.Bind20(r1, r2, r3, r4, r5, r6)
            com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer[] r0 = r8.glBuffers
            r1 = 0
            r0 = r0[r1]
            com.lumiyaviewer.lumiya.render.shaders.AvatarProgram r1 = r9.avatarProgram
            Int r2 = r1.vNormal
            r3 = 3
            r4 = 5126(0x1406, Float:7.183E-42)
            r5 = 24
            r6 = 12
            r1 = r9
            r0.Bind20(r1, r2, r3, r4, r5, r6)
            com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer[] r0 = r8.glBuffers
            r1 = 1
            r0 = r0[r1]
            r0.BindElements20(r9)
            if (r7 == 0) goto L_0x00fe
            com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer[] r0 = r8.glBuffers
            r1 = 2
            r0 = r0[r1]
            com.lumiyaviewer.lumiya.render.shaders.AvatarProgram r1 = r9.avatarProgram
            Int r2 = r1.vTexCoord
            r3 = 2
            r4 = 5126(0x1406, Float:7.183E-42)
            r5 = 8
            r6 = 0
            r1 = r9
            r0.Bind20(r1, r2, r3, r4, r5, r6)
        L_0x0092:
            com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer[] r0 = r8.glBuffers
            r1 = 3
            r0 = r0[r1]
            if (r0 == 0) goto L_0x0106
            com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer[] r0 = r8.glBuffers
            r1 = 3
            r0 = r0[r1]
            com.lumiyaviewer.lumiya.render.shaders.AvatarProgram r1 = r9.avatarProgram
            Int r2 = r1.vWeight
            r3 = 1
            r4 = 5126(0x1406, Float:7.183E-42)
            r5 = 4
            r6 = 0
            r1 = r9
            r0.Bind20(r1, r2, r3, r4, r5, r6)
            com.lumiyaviewer.lumiya.render.shaders.AvatarProgram r0 = r9.avatarProgram
            Int r0 = r0.uJointMap
            com.lumiyaviewer.lumiya.slproto.avatar.SLPolyMesh r1 = r8.referenceData
            Int[] r1 = r1.jointMap
            Int r1 = r1.length
            com.lumiyaviewer.lumiya.slproto.avatar.SLPolyMesh r2 = r8.referenceData
            Int[] r2 = r2.jointMap
            r3 = 0
            android.opengl.GLES20.glUniform1iv(r0, r1, r2, r3)
            com.lumiyaviewer.lumiya.render.shaders.AvatarProgram r0 = r9.avatarProgram
            Int r0 = r0.uJointMapLength
            com.lumiyaviewer.lumiya.slproto.avatar.SLPolyMesh r1 = r8.referenceData
            Int[] r1 = r1.jointMap
            Int r1 = r1.length
            android.opengl.GLES20.glUniform1i(r0, r1)
            com.lumiyaviewer.lumiya.render.shaders.AvatarProgram r0 = r9.avatarProgram
            Int r0 = r0.uUseWeight
            r1 = 1
            android.opengl.GLES20.glUniform1i(r0, r1)
        L_0x00d0:
            com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer[] r0 = r8.glBuffers
            r1 = 1
            r0 = r0[r1]
            Int r1 = r8.numFaces
            Int r1 = r1 * 3
            r2 = 4
            r3 = 5123(0x1403, Float:7.179E-42)
            r4 = 0
            r0.DrawElements20(r2, r1, r3, r4)
        L_0x00e0:
            if (r7 == 0) goto L_0x00ec
            Boolean r0 = r9.hasGL20
            if (r0 == 0) goto L_0x018c
            r0 = 3553(0xde1, Float:4.979E-42)
            r1 = 0
            android.opengl.GLES20.glBindTexture(r0, r1)
        L_0x00ec:
            return
        L_0x00ed:
            com.lumiyaviewer.lumiya.render.shaders.AvatarProgram r0 = r9.avatarProgram
            Int r0 = r0.vColor
            r1 = 1056964608(0x3f000000, Float:0.5)
            r2 = 1056964608(0x3f000000, Float:0.5)
            r3 = 1056964608(0x3f000000, Float:0.5)
            r4 = 1065353216(0x3f800000, Float:1.0)
            android.opengl.GLES20.glUniform4f(r0, r1, r2, r3, r4)
            goto L_0x004e
        L_0x00fe:
            com.lumiyaviewer.lumiya.render.shaders.AvatarProgram r0 = r9.avatarProgram
            Int r0 = r0.vTexCoord
            android.opengl.GLES20.glDisableVertexAttribArray(r0)
            goto L_0x0092
        L_0x0106:
            com.lumiyaviewer.lumiya.render.shaders.AvatarProgram r0 = r9.avatarProgram
            Int r0 = r0.vWeight
            android.opengl.GLES20.glDisableVertexAttribArray(r0)
            com.lumiyaviewer.lumiya.render.shaders.AvatarProgram r0 = r9.avatarProgram
            Int r0 = r0.uUseWeight
            r1 = 0
            android.opengl.GLES20.glUniform1i(r0, r1)
            goto L_0x00d0
        L_0x0116:
            com.lumiyaviewer.lumiya.slproto.avatar.SLPolyMesh r0 = r8.referenceData
            Boolean r0 = r0.hasWeights
            if (r0 == 0) goto L_0x0189
            com.lumiyaviewer.rawbuffers.DirectByteBuffer r0 = r8.animatedVertexData
        L_0x011e:
            if (r0 == 0) goto L_0x00e0
            r0 = 1065353216(0x3f800000, Float:1.0)
            r1 = 1065353216(0x3f800000, Float:1.0)
            r2 = 1065353216(0x3f800000, Float:1.0)
            r3 = 1065353216(0x3f800000, Float:1.0)
            android.opengl.GLES10.glColor4f(r0, r1, r2, r3)
            com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer[] r0 = r8.glBuffers
            r1 = 0
            r0 = r0[r1]
            r2 = 32884(0x8074, Float:4.608E-41)
            r3 = 3
            r4 = 5126(0x1406, Float:7.183E-42)
            r5 = 24
            r6 = 0
            r1 = r9
            r0.Bind(r1, r2, r3, r4, r5, r6)
            com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer[] r0 = r8.glBuffers
            r1 = 0
            r0 = r0[r1]
            r2 = 32885(0x8075, Float:4.6082E-41)
            r3 = 3
            r4 = 5126(0x1406, Float:7.183E-42)
            r5 = 24
            r6 = 12
            r1 = r9
            r0.Bind(r1, r2, r3, r4, r5, r6)
            com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer[] r0 = r8.glBuffers
            r1 = 2
            r0 = r0[r1]
            r2 = 32888(0x8078, Float:4.6086E-41)
            r3 = 2
            r4 = 5126(0x1406, Float:7.183E-42)
            r5 = 8
            r6 = 0
            r1 = r9
            r0.Bind(r1, r2, r3, r4, r5, r6)
            com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer[] r0 = r8.glBuffers
            r1 = 1
            r0 = r0[r1]
            r0.BindElements(r9)
            com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer[] r0 = r8.glBuffers
            r1 = 1
            r0 = r0[r1]
            Int r1 = r8.numFaces
            Int r3 = r1 * 3
            r2 = 4
            r4 = 5123(0x1403, Float:7.179E-42)
            r5 = 0
            r1 = r9
            r0.DrawElements(r1, r2, r3, r4, r5)
            r0 = 32885(0x8075, Float:4.6082E-41)
            android.opengl.GLES10.glDisableClientState(r0)
            r0 = 32888(0x8078, Float:4.6086E-41)
            android.opengl.GLES10.glDisableClientState(r0)
            goto L_0x00e0
        L_0x0189:
            com.lumiyaviewer.rawbuffers.DirectByteBuffer r0 = r8.vertexBuffer
            goto L_0x011e
        L_0x018c:
            r0 = 3553(0xde1, Float:4.979E-42)
            r1 = 0
            android.opengl.GLES10.glBindTexture(r0, r1)
            r0 = 3553(0xde1, Float:4.979E-42)
            android.opengl.GLES10.glDisable(r0)
            goto L_0x00ec
        L_0x0199:
            r7 = r0
            goto L_0x0021
        */
        throw UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.avatar.SLAnimatedMeshData.GLDraw(com.lumiyaviewer.lumiya.render.RenderContext, com.lumiyaviewer.lumiya.render.drawable.DrawableFaceTexture):Unit")
    }

    public DirectByteBuffer getAnimatedVertexData() {
        return this.animatedVertexData
    }

    fun setVerticesDirty() {
        this.verticesDirty = true
    }
}
