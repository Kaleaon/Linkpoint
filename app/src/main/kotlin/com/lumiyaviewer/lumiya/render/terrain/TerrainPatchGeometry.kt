package com.lumiyaviewer.lumiya.render.terrain

import android.opengl.GLES10
import android.opengl.GLES11
import android.opengl.GLES20
import com.lumiyaviewer.lumiya.render.RenderContext
import com.lumiyaviewer.lumiya.render.glres.buffers.GLLoadableBuffer
import com.lumiyaviewer.lumiya.render.glres.textures.GLLoadedTexture
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchHeightMap
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import com.lumiyaviewer.lumiya.utils.IdentityMatrix
import com.lumiyaviewer.rawbuffers.DirectByteBuffer
import kotlin.math.min

class TerrainPatchGeometry(terrainPatchHeightMap: TerrainPatchHeightMap) {
    
    private val vertexBuffer: GLLoadableBuffer
    private val indexBuffer: GLLoadableBuffer
    private val waterVertexBuffer: GLLoadableBuffer
    private val waterIndexBuffer: GLLoadableBuffer
    
    private var indexCount = 0
    private var waterIndexCount = 0

    init {
        val vertexData = DirectByteBuffer(VERTEX_SIZE_BYTES)
        val waterVertexData = DirectByteBuffer(WATER_VERTEX_SIZE_BYTES)
        val indexData = DirectByteBuffer(INDEX_SIZE_BYTES)
        val waterIndexData = DirectByteBuffer(INDEX_SIZE_BYTES)
        
        vertexData.position(0)
        waterVertexData.position(0)
        indexCount = 0
        waterIndexCount = 0
        
        val normal = LLVector3()
        val waterHeight = terrainPatchHeightMap.waterHeight
        val heightArray = terrainPatchHeightMap.heightArray
        val normalArray = terrainPatchHeightMap.normalArray
        
        // Build vertex data
        var heightIndex = 0
        for (y in 0..16) {
            for (x in 0..16) {
                // Terrain vertex
                vertexData.putFloat(x.toFloat())
                vertexData.putFloat(y.toFloat())
                vertexData.putFloat(heightArray[heightIndex + x])
                
                // Calculate normal
                normal.set(
                    -normalArray[(heightIndex + x) * 2],
                    normalArray[(heightIndex + x) * 2 + 1],
                    2.0f
                )
                normal.normVec()
                
                vertexData.putFloat(normal.x)
                vertexData.putFloat(normal.y)
                vertexData.putFloat(normal.z)
                vertexData.putFloat(x.toFloat() / 16.0f)
                vertexData.putFloat(y.toFloat() / 16.0f)
                
                // Water vertex
                waterVertexData.putFloat(x.toFloat())
                waterVertexData.putFloat(y.toFloat())
                waterVertexData.putFloat(waterHeight)
            }
            heightIndex += 17
        }
        
        // Build index data
        indexData.position(0)
        waterIndexData.position(0)
        
        var vertexIndex = 0
        for (y in 0..15) {
            for (x in 0..15) {
                val v0 = vertexIndex + x
                val v1 = v0 + 1
                val v2 = v0 + 17
                val v3 = v2 + 1
                
                // Terrain indices
                indexData.putShort(v0.toShort())
                indexData.putShort(v1.toShort())
                indexData.putShort(v2.toShort())
                indexData.putShort(v1.toShort())
                indexData.putShort(v3.toShort())
                indexData.putShort(v2.toShort())
                indexCount += 6
                
                // Water indices (only if below water height)
                val minHeight = min(
                    min(
                        min(
                            vertexData.getFloat(v0 * 8 + 2),
                            vertexData.getFloat(v1 * 8 + 2)
                        ),
                        vertexData.getFloat(v2 * 8 + 2)
                    ),
                    vertexData.getFloat(v3 * 8 + 2)
                )
                
                if (minHeight <= waterHeight) {
                    waterIndexData.putShort(v0.toShort())
                    waterIndexData.putShort(v1.toShort())
                    waterIndexData.putShort(v2.toShort())
                    waterIndexData.putShort(v1.toShort())
                    waterIndexData.putShort(v3.toShort())
                    waterIndexData.putShort(v2.toShort())
                    waterIndexCount += 6
                }
            }
            vertexIndex += 17
        }
        
        vertexBuffer = GLLoadableBuffer(vertexData)
        indexBuffer = GLLoadableBuffer(indexData)
        waterVertexBuffer = GLLoadableBuffer(waterVertexData)
        waterIndexBuffer = GLLoadableBuffer(waterIndexData)
    }

    fun GLDraw(renderContext: RenderContext, worldMatrix: FloatArray, texture: GLLoadedTexture?) {
        if (indexCount == 0) {
            return
        }
        
        if (!renderContext.hasGL20) {
            renderContext.glObjWorldPushAndMultMatrixf(worldMatrix, 0)
        }
        
        // Draw terrain
        if (renderContext.hasGL20) {
            GLES20.glUseProgram(renderContext.primProgram.handle)
            vertexBuffer.Bind20(renderContext, renderContext.primProgram.vPosition, 3, 5126, 32, 0)
            vertexBuffer.Bind20(renderContext, renderContext.primProgram.vNormal, 3, 5126, 32, 12)
            GLES20.glUniformMatrix4fv(renderContext.primProgram.uObjWorldMatrix, 1, false, worldMatrix, 0)
            
            if (texture != null) {
                texture.GLDraw()
                vertexBuffer.Bind20(renderContext, renderContext.primProgram.vTexCoord, 2, 5126, 32, 24)
                GLES20.glUniform1i(renderContext.primProgram.sTexture, 0)
                GLES20.glUniform4f(renderContext.primProgram.vColor, 1.0f, 1.0f, 1.0f, 1.0f)
                renderContext.primProgram.setTextureEnabled(true)
            } else {
                GLES20.glBindTexture(3553, 0)
                GLES20.glDisableVertexAttribArray(renderContext.primProgram.vTexCoord)
                GLES20.glUniform1i(renderContext.primProgram.sTexture, 0)
                GLES20.glUniform4f(renderContext.primProgram.vColor, 0.1f, 0.5f, 0.1f, 1.0f)
                renderContext.primProgram.setTextureEnabled(false)
            }
            
            indexBuffer.BindElements20(renderContext)
            indexBuffer.DrawElements20(4, indexCount, 5123, 0)
        } else {
            GLES10.glDisableClientState(32885)
            
            if (texture != null) {
                GLES10.glEnable(3553)
                texture.GLDraw()
                GLES10.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                vertexBuffer.Bind(renderContext, 32888, 2, 5126, 32, 24)
            } else {
                GLES10.glDisableClientState(32888)
                GLES10.glDisable(3553)
                GLES10.glColor4f(0.1f, 0.5f, 0.1f, 1.0f)
            }
            
            vertexBuffer.Bind(renderContext, 32884, 3, 5126, 32, 0)
            indexBuffer.BindElements(renderContext)
            indexBuffer.DrawElements(renderContext, 4, indexCount, 5123, 0)
        }
        
        // Draw water
        if (waterIndexCount != 0) {
            if (renderContext.hasGL20) {
                GLES20.glDisable(2884)
                GLES20.glUseProgram(renderContext.waterProgram.handle)
                GLES20.glUniformMatrix4fv(renderContext.waterProgram.uObjWorldMatrix, 1, false, worldMatrix, 0)
                waterVertexBuffer.Bind20(renderContext, renderContext.waterProgram.vPosition, 3, 5126, 0, 0)
                waterIndexBuffer.BindElements20(renderContext)
                waterIndexBuffer.DrawElements20(4, waterIndexCount, 5123, 0)
                GLES20.glEnable(2884)
            } else {
                GLES10.glDisable(2884)
                GLES10.glDisableClientState(32888)
                GLES10.glDisable(3553)
                GLES10.glColor4f(0.4f, 0.4f, 0.6f, 1.0f)
                waterVertexBuffer.Bind(renderContext, 32884, 3, 5126, 0, 0)
                waterIndexBuffer.BindElements(renderContext)
                waterIndexBuffer.DrawElements(renderContext, 4, waterIndexCount, 5123, 0)
                GLES10.glEnable(2884)
            }
        }
        
        if (!renderContext.hasGL20) {
            renderContext.glObjWorldPopMatrix()
        }
    }

    companion object {
        const val DrawPatchSize = 16
        
        private const val INDEX_SIZE_BYTES = 3072
        private const val VERTEX_SIZE_BYTES = 9248
        private const val WATER_VERTEX_SIZE_BYTES = 3468
        
        private val waterAmplitude = floatArrayOf(0.5f, 0.5f, 0.3f, 0.4f)
        private val waterDirection = floatArrayOf(1.0f, 0.3f, 0.4f, 0.75f, -0.5f, 0.7f, 0.63f, -0.3f)
        private val waterFrequency = floatArrayOf(17.951958f, 12.566371f, 8.975979f, 15.707963f)
        private val waterPhase = floatArrayOf(1.73f, 0.64f, 1.27f, 0.9f)

        @JvmStatic
        fun GLPrepare(renderContext: RenderContext) {
            if (renderContext.hasGL20) {
                GLES20.glUseProgram(renderContext.primProgram.handle)
                renderContext.glModelApplyMatrix(renderContext.primProgram.uMVPMatrix)
                renderContext.primProgram.SetupLighting(renderContext, renderContext.windlightPreset)
                GLES20.glUniform4f(renderContext.primProgram.uObjCoordScale, 1.0f, 1.0f, 1.0f, 1.0f)
                GLES20.glUniformMatrix4fv(
                    renderContext.primProgram.uTexMatrix,
                    1,
                    false,
                    IdentityMatrix.getMatrix(),
                    0
                )
                
                GLES20.glUseProgram(renderContext.waterProgram.handle)
                GLES20.glUniform4f(renderContext.waterProgram.vColor, 0.4f, 0.4f, 0.6f, 1.0f)
                renderContext.glModelApplyMatrix(renderContext.waterProgram.uMVPMatrix)
                GLES20.glUniform1f(renderContext.waterProgram.uTime, renderContext.waterTime)
                GLES20.glUniform1fv(renderContext.waterProgram.uFrequency, 4, waterFrequency, 0)
                GLES20.glUniform1fv(renderContext.waterProgram.uPhase, 4, waterPhase, 0)
                GLES20.glUniform1fv(renderContext.waterProgram.uAmplitude, 4, waterAmplitude, 0)
                GLES20.glUniform2fv(renderContext.waterProgram.uDirection, 4, waterDirection, 0)
            } else {
                GLES11.glMatrixMode(5890)
                GLES11.glLoadMatrixf(IdentityMatrix.getMatrix(), 0)
                GLES11.glMatrixMode(5888)
            }
        }
    }
}
