package com.linkpoint.slproto.avatar

import android.opengl.GLES20
import com.linkpoint.render.RenderContext
import com.linkpoint.render.drawable.DrawableFaceTexture
import com.linkpoint.render.glres.buffers.GLLoadableBuffer
import com.linkpoint.rawbuffers.DirectByteBuffer

/**
 * SLAnimatedMeshData - Animated avatar mesh with GPU buffers
 * Handles vertex animation and rendering with VBOs
 * Based on Firestorm's avatar rendering pipeline
 */
class SLAnimatedMeshData : SLMeshData {
    
    companion object {
        private const val BUF_VERTEX = 0
        private const val BUF_INDEX = 1
        private const val BUF_TEXCOORD = 2
        private const val BUF_WEIGHTS = 3
    }
    
    private var vboLoaded = false
    private val animated: Boolean
    private val animatedVertexData: DirectByteBuffer?
    private val glBuffers = arrayOfNulls<GLLoadableBuffer>(4)
    private var texCoordsDirty = false
    private var verticesDirty = false

    /**
     * Create animated mesh from poly mesh
     * @param sourceMesh The source poly mesh
     * @param skipAnimation If true, skip animation even if mesh has weights
     */
    constructor(sourceMesh: SLPolyMesh, skipAnimation: Boolean = false) : super(sourceMesh) {
        // Determine if this mesh should be animated
        this.animated = sourceMesh.hasWeights && !skipAnimation
        
        if (this.animated) {
            // Create separate buffer for animated vertices
            this.animatedVertexData = DirectByteBuffer(this.vertexBuffer!!)
            this.verticesDirty = true
        } else {
            this.animatedVertexData = null
        }
    }

    /**
     * Setup VBOs (Vertex Buffer Objects) for GPU rendering
     */
    private fun setupVBOs(renderContext: RenderContext) {
        if (!vboLoaded || texCoordsDirty || verticesDirty) {
            
            // Setup vertex buffer
            if (!vboLoaded || verticesDirty) {
                val vertexData = if (animated) animatedVertexData else vertexBuffer
                
                if (glBuffers[BUF_VERTEX] == null) {
                    glBuffers[BUF_VERTEX] = GLLoadableBuffer(vertexData)
                } else if (animated) {
                    // Reload animated vertices each frame
                    glBuffers[BUF_VERTEX]!!.Reload(renderContext)
                }
            }
            
            // Setup index and weights buffers (only first time)
            if (!vboLoaded) {
                // Setup weights buffer if rigged
                if (renderContext.hasGL20 && referenceData?.hasWeights == true) {
                    glBuffers[BUF_WEIGHTS] = GLLoadableBuffer(referenceData!!.weightsBuffer)
                }
                
                // Setup index buffer
                glBuffers[BUF_INDEX] = GLLoadableBuffer(indexBuffer)
            }
            
            // Setup texture coordinate buffer
            if (!vboLoaded || texCoordsDirty) {
                if (glBuffers[BUF_TEXCOORD] == null) {
                    glBuffers[BUF_TEXCOORD] = GLLoadableBuffer(texCoordsBuffer)
                } else {
                    glBuffers[BUF_TEXCOORD]!!.Reload(renderContext)
                }
            }
            
            vboLoaded = true
            verticesDirty = false
            texCoordsDirty = false
        }
    }

    /**
     * Draw mesh with OpenGL
     * @param renderContext Rendering context
     * @param faceTexture Optional texture to apply
     */
    fun GLDraw(renderContext: RenderContext, faceTexture: DrawableFaceTexture?) {
        setupVBOs(renderContext)
        
        // Determine if we're rendering with texture
        var hasTexture = false
        
        if (faceTexture != null) {
            if (!renderContext.hasGL20) {
                GLES20.glEnable(GLES20.GL_TEXTURE_2D)
            }
            hasTexture = faceTexture.GLDraw(renderContext)
            if (!hasTexture && !renderContext.hasGL20) {
                GLES20.glDisable(GLES20.GL_TEXTURE_2D)
            }
        }
        
        if (!vboLoaded) {
            return
        }
        
        // Render with appropriate path
        if (renderContext.hasGL20) {
            drawGL20(renderContext, hasTexture)
        } else {
            drawGL11(renderContext, hasTexture)
        }
        
        // Cleanup texture binding
        if (hasTexture) {
            if (renderContext.hasGL20) {
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
            } else {
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
                GLES20.glDisable(GLES20.GL_TEXTURE_2D)
            }
        }
    }

    /**
     * Render using OpenGL ES 2.0+ (shader-based)
     */
    private fun drawGL20(renderContext: RenderContext, hasTexture: Boolean) {
        val program = renderContext.avatarProgram
        
        // Set texture enabled state
        program.setTextureEnabled(hasTexture)
        if (!hasTexture) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        }
        
        // Apply object world matrix
        renderContext.glObjWorldApplyMatrix(program.uObjWorldMatrix)
        
        // Set vertex color (white for textured, gray for untextured)
        if (hasTexture) {
            GLES20.glUniform4f(program.vColor, 1.0f, 1.0f, 1.0f, 1.0f)
        } else {
            GLES20.glUniform4f(program.vColor, 0.5f, 0.5f, 0.5f, 1.0f)
        }
        
        // Bind vertex positions and normals
        glBuffers[BUF_VERTEX]!!.Bind20(
            renderContext,
            program.vPosition,
            3,  // 3 floats per position
            GLES20.GL_FLOAT,
            24,  // stride (6 floats = 24 bytes)
            0    // offset for position
        )
        
        glBuffers[BUF_VERTEX]!!.Bind20(
            renderContext,
            program.vNormal,
            3,  // 3 floats per normal
            GLES20.GL_FLOAT,
            24,  // stride
            12   // offset for normal (after 3 position floats = 12 bytes)
        )
        
        // Bind indices
        glBuffers[BUF_INDEX]!!.BindElements20(renderContext)
        
        // Bind texture coordinates
        if (hasTexture) {
            glBuffers[BUF_TEXCOORD]!!.Bind20(
                renderContext,
                program.vTexCoord,
                2,  // 2 floats per texcoord
                GLES20.GL_FLOAT,
                8,   // stride (2 floats = 8 bytes)
                0    // offset
            )
        } else {
            GLES20.glDisableVertexAttribArray(program.vTexCoord)
        }
        
        // Bind weights for rigged mesh
        if (glBuffers[BUF_WEIGHTS] != null) {
            glBuffers[BUF_WEIGHTS]!!.Bind20(
                renderContext,
                program.vWeight,
                1,  // 1 float per weight
                GLES20.GL_FLOAT,
                4,   // stride (1 float = 4 bytes)
                0    // offset
            )
            
            // Upload joint map
            val jointMap = referenceData?.jointMap
            if (jointMap != null) {
                GLES20.glUniform1iv(program.uJointMap, jointMap.size, jointMap, 0)
                GLES20.glUniform1i(program.uJointMapLength, jointMap.size)
            }
            
            GLES20.glUniform1i(program.uUseWeight, 1)
        } else {
            GLES20.glDisableVertexAttribArray(program.vWeight)
            GLES20.glUniform1i(program.uUseWeight, 0)
        }
        
        // Draw triangles
        glBuffers[BUF_INDEX]!!.DrawElements20(
            GLES20.GL_TRIANGLES,
            numFaces * 3,
            GLES20.GL_UNSIGNED_SHORT,
            0
        )
    }

    /**
     * Render using OpenGL ES 1.1 (fixed function pipeline - deprecated)
     */
    private fun drawGL11(renderContext: RenderContext, hasTexture: Boolean) {
        // Use animated or static vertices
        val vertexData = if (referenceData?.hasWeights == true) {
            animatedVertexData
        } else {
            vertexBuffer
        }
        
        if (vertexData == null) {
            return
        }
        
        // Set color
        GLES20.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
        
        // Bind vertex positions
        glBuffers[BUF_VERTEX]!!.Bind(
            renderContext,
            GLES20.GL_VERTEX_ARRAY,
            3,
            GLES20.GL_FLOAT,
            24,
            0
        )
        
        // Bind normals
        glBuffers[BUF_VERTEX]!!.Bind(
            renderContext,
            GLES20.GL_NORMAL_ARRAY,
            3,
            GLES20.GL_FLOAT,
            24,
            12
        )
        
        // Bind texture coordinates
        glBuffers[BUF_TEXCOORD]!!.Bind(
            renderContext,
            GLES20.GL_TEXTURE_COORD_ARRAY,
            2,
            GLES20.GL_FLOAT,
            8,
            0
        )
        
        // Bind indices
        glBuffers[BUF_INDEX]!!.BindElements(renderContext)
        
        // Draw triangles
        glBuffers[BUF_INDEX]!!.DrawElements(
            renderContext,
            GLES20.GL_TRIANGLES,
            numFaces * 3,
            GLES20.GL_UNSIGNED_SHORT,
            0
        )
        
        // Disable client states
        GLES20.glDisableClientState(GLES20.GL_NORMAL_ARRAY)
        GLES20.glDisableClientState(GLES20.GL_TEXTURE_COORD_ARRAY)
    }

    /**
     * Get animated vertex data buffer
     */
    fun getAnimatedVertexData(): DirectByteBuffer? {
        return animatedVertexData
    }

    /**
     * Mark vertices as dirty (need to be re-uploaded to GPU)
     */
    fun setVerticesDirty() {
        verticesDirty = true
    }
    
    /**
     * Mark texture coordinates as dirty
     */
    fun setTexCoordsDirty() {
        texCoordsDirty = true
    }
}
