package com.linkpoint.slproto.avatar

import android.opengl.GLES10
import android.opengl.GLES20
import com.linkpoint.render.RenderContext
import com.linkpoint.render.drawable.DrawableFaceTexture
import com.linkpoint.render.glres.buffers.GLLoadableBuffer
import com.linkpoint.rawbuffers.DirectByteBuffer

class SLAnimatedMeshData : SLMeshData {
    companion object {
        private const val BUF_VERTEX = 0
        private const val BUF_INDEX = 1
        private const val BUF_TEXCOORD = 2
        private const val BUF_WEIGHTS = 3
    }
    
    private var VBOLoaded: Boolean = false
    private val animated: Boolean
    private var animatedVertexData: DirectByteBuffer? = null
    private val glBuffers: Array<GLLoadableBuffer?> = arrayOfNulls(4)
    private var texCoordsDirty: Boolean = false
    private var verticesDirty: Boolean = false

    constructor(sLPolyMesh: SLPolyMesh, z: Boolean) : super(sLPolyMesh) {
        this.animated = if (sLPolyMesh.hasWeights) !z else false
        if (this.animated) {
            val vb = this.vertexBuffer
            if (vb != null) {
                this.animatedVertexData = DirectByteBuffer(vb)
            }
            this.verticesDirty = true
        } else {
            this.animatedVertexData = null
        }
    }

    private fun setupVBOs(renderContext: RenderContext) {
        if (!this.VBOLoaded || this.texCoordsDirty || this.verticesDirty) {
            if (!this.VBOLoaded || this.verticesDirty) {
                val directByteBuffer = if (this.animated) this.animatedVertexData else this.vertexBuffer
                if (this.glBuffers[BUF_VERTEX] == null && directByteBuffer != null) {
                    this.glBuffers[BUF_VERTEX] = GLLoadableBuffer(directByteBuffer)
                } else if (this.animated) {
                    this.glBuffers[BUF_VERTEX]?.Reload(renderContext)
                }
            }
            if (!this.VBOLoaded) {
                val refData = this.referenceData
                if (renderContext.hasGL20 && refData != null && refData.hasWeights) {
                    val wb = refData.weightsBuffer
                    if (wb != null) {
                        this.glBuffers[BUF_WEIGHTS] = GLLoadableBuffer(wb)
                    }
                }
                val ib = this.indexBuffer
                if (ib != null) {
                    this.glBuffers[BUF_INDEX] = GLLoadableBuffer(ib)
                }
            }
            if (!this.VBOLoaded || this.texCoordsDirty) {
                val tcb = this.texCoordsBuffer
                if (this.glBuffers[BUF_TEXCOORD] == null && tcb != null) {
                    this.glBuffers[BUF_TEXCOORD] = GLLoadableBuffer(tcb)
                } else {
                    this.glBuffers[BUF_TEXCOORD]?.Reload(renderContext)
                }
            }
            this.VBOLoaded = true
            this.verticesDirty = false
            this.texCoordsDirty = false
        }
    }

    fun GLDraw(renderContext: RenderContext, faceTexture: DrawableFaceTexture?) {
        setupVBOs(renderContext)
        
        var hasTexture = false
        if (faceTexture != null) {
            if (!renderContext.hasGL20) {
                GLES20.glEnable(GLES20.GL_TEXTURE_2D)
            }
            hasTexture = !faceTexture.GLDraw(renderContext)
            if (!hasTexture) {
                if (!renderContext.hasGL20) {
                    GLES20.glDisable(GLES20.GL_TEXTURE_2D)
                }
            }
        }
        
        if (renderContext.hasGL20) {
            drawGL20(renderContext, hasTexture)
        } else {
            drawGL11(renderContext, hasTexture)
        }
        
        if (hasTexture) {
            if (renderContext.hasGL20) {
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
            } else {
                GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, 0)
                GLES10.glDisable(GLES10.GL_TEXTURE_2D)
            }
        }
    }
    
    private fun drawGL20(renderContext: RenderContext, hasTexture: Boolean) {
        if (!this.VBOLoaded) return
        
        val avatarProgram = renderContext.avatarProgram
        avatarProgram.setTextureEnabled(hasTexture)
        
        if (!hasTexture) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        }
        
        renderContext.glObjWorldApplyMatrix(avatarProgram.uObjWorldMatrix)
        
        if (hasTexture) {
            GLES20.glUniform4f(avatarProgram.vColor, 1.0f, 1.0f, 1.0f, 1.0f)
        } else {
            GLES20.glUniform4f(avatarProgram.vColor, 0.5f, 0.5f, 0.5f, 1.0f)
        }
        
        // Bind vertex position
        glBuffers[BUF_VERTEX]?.Bind20(renderContext, avatarProgram.vPosition, 3, 5126, 24, 0)
        
        // Bind vertex normal
        glBuffers[BUF_VERTEX]?.Bind20(renderContext, avatarProgram.vNormal, 3, 5126, 24, 12)
        
        // Bind index buffer
        glBuffers[BUF_INDEX]?.BindElements20(renderContext)
        
        // Bind texture coords
        if (hasTexture) {
            glBuffers[BUF_TEXCOORD]?.Bind20(renderContext, avatarProgram.vTexCoord, 2, 5126, 8, 0)
        } else {
            GLES20.glDisableVertexAttribArray(avatarProgram.vTexCoord)
        }
        
        // Bind weights if available
        val weightsBuffer = glBuffers[BUF_WEIGHTS]
        val refData = this.referenceData
        if (weightsBuffer != null && refData != null) {
            weightsBuffer.Bind20(renderContext, avatarProgram.vWeight, 1, 5126, 4, 0)
            val jointMap = refData.jointMap
            if (jointMap != null) {
                GLES20.glUniform1iv(avatarProgram.uJointMap, jointMap.size, jointMap, 0)
                GLES20.glUniform1i(avatarProgram.uJointMapLength, jointMap.size)
            }
            GLES20.glUniform1i(avatarProgram.uUseWeight, 1)
        } else {
            GLES20.glDisableVertexAttribArray(avatarProgram.vWeight)
            GLES20.glUniform1i(avatarProgram.uUseWeight, 0)
        }
        
        // Draw elements
        glBuffers[BUF_INDEX]?.DrawElements20(4, this.numFaces * 3, 5123, 0)
    }
    
    private fun drawGL11(renderContext: RenderContext, hasTexture: Boolean) {
        val refData = this.referenceData
        val vertexData = if (refData?.hasWeights == true) this.animatedVertexData else this.vertexBuffer
        if (vertexData == null) return
        
        GLES10.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        
        // Bind vertex position
        glBuffers[BUF_VERTEX]?.Bind(renderContext, GLES10.GL_VERTEX_ARRAY, 3, 5126, 24, 0)
        
        // Bind vertex normal
        glBuffers[BUF_VERTEX]?.Bind(renderContext, GLES10.GL_NORMAL_ARRAY, 3, 5126, 24, 12)
        
        // Bind texture coords
        glBuffers[BUF_TEXCOORD]?.Bind(renderContext, GLES10.GL_TEXTURE_COORD_ARRAY, 2, 5126, 8, 0)
        
        // Bind and draw elements
        glBuffers[BUF_INDEX]?.BindElements(renderContext)
        glBuffers[BUF_INDEX]?.DrawElements(renderContext, 4, this.numFaces * 3, 5123, 0)
        
        GLES10.glDisableClientState(GLES10.GL_NORMAL_ARRAY)
        GLES10.glDisableClientState(GLES10.GL_TEXTURE_COORD_ARRAY)
    }

    fun getAnimatedVertexData(): DirectByteBuffer? {
        return this.animatedVertexData
    }

    fun setVerticesDirty() {
        this.verticesDirty = true
    }
}
