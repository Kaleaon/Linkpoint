package com.lumiyaviewer.lumiya.slproto.avatar

import android.opengl.GLES20
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.render.drawable.DrawableFaceTexture
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer
import com.lumiyaviewer.rawbuffers.DirectByteBuffer

class SLAnimatedMeshData(mesh: SLPolyMesh, invert: Boolean) : SLMeshData(mesh) {
    
    private val BUF_INDEX = 1
    private val BUF_TEXCOORD = 2
    private val BUF_VERTEX = 0
    private val BUF_WEIGHTS = 3
    
    private var VBOLoaded = false
    private var animated = false
    private var animatedVertexData: DirectByteBuffer? = null
    private val glBuffers = arrayOfNulls<GLLoadableBuffer>(4)
    private var texCoordsDirty = false
    private var verticesDirty = false

    init {
        this.animated = if (mesh.hasWeights) !invert else false
        if (this.animated) {
            // Copy constructor for DirectByteBuffer?
            // Assuming DirectByteBuffer has a constructor that takes another DirectByteBuffer or a method to clone
            // Or creating new and copying. 
            // Decompiled code: this.animatedVertexData = new DirectByteBuffer(this.vertexBuffer);
             this.animatedVertexData = DirectByteBuffer(this.vertexBuffer!!)
             this.verticesDirty = true
        } else {
            this.animatedVertexData = null
        }
    }

    private fun setupVBOs(renderContext: RenderContext) {
        if (!VBOLoaded || texCoordsDirty || verticesDirty) {
            if (!VBOLoaded || verticesDirty) {
                val buffer = if (animated) animatedVertexData else vertexBuffer
                if (glBuffers[0] == null) {
                    if (buffer != null) glBuffers[0] = GLLoadableBuffer(buffer)
                } else if (animated) {
                    glBuffers[0]?.Reload(renderContext)
                }
            }
            
            if (!VBOLoaded) {
                if (renderContext.hasGL20 && referenceData != null && referenceData!!.hasWeights) {
                    if (referenceData!!.weightsBuffer != null) {
                        glBuffers[3] = GLLoadableBuffer(referenceData!!.weightsBuffer!!)
                    }
                }
                if (indexBuffer != null) {
                    glBuffers[1] = GLLoadableBuffer(indexBuffer!!)
                }
            }
            
            if (!VBOLoaded || texCoordsDirty) {
                if (glBuffers[2] == null) {
                    if (texCoordsBuffer != null) glBuffers[2] = GLLoadableBuffer(texCoordsBuffer!!)
                } else {
                    glBuffers[2]?.Reload(renderContext)
                }
            }
            
            VBOLoaded = true
            verticesDirty = false
            texCoordsDirty = false
        }
    }

    fun GLDraw(renderContext: RenderContext, texture: DrawableFaceTexture?) {
        setupVBOs(renderContext)
        
        if (texture != null) {
            if (!renderContext.hasGL20) {
                GLES20.glEnable(GLES20.GL_TEXTURE_2D)
            }
            
            if (!texture.GLDraw(renderContext)) {
                 if (!renderContext.hasGL20) {
                    GLES20.glDisable(GLES20.GL_TEXTURE_2D)
                 }
                 // Continue drawing without texture or use default?
            }
        }

        // Drawing logic stubbed/simplified from decompiled code
        if (renderContext.hasGL20) {
            // Modern GL drawing
            if (VBOLoaded) {
                 // Bind buffers and draw
            }
        } else {
             // Legacy GL drawing
             if (glBuffers[0] != null && glBuffers[1] != null) {
                 // Draw elements
             }
        }
    }

    fun getAnimatedVertexData(): DirectByteBuffer? {
        return animatedVertexData
    }

    fun setVerticesDirty() {
        verticesDirty = true
    }
}
