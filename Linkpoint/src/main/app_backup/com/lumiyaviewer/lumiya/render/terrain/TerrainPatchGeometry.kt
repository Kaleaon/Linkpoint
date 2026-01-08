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

class TerrainPatchGeometry {
    val DrawPatchSize = 16
    private val index_size_bytes = 3072
    private val vertex_size_bytes = 9248
    private val waterAmplitude = floatArrayOf(0.5f, 0.5f, 0.3f, 0.4f)
    private val waterDirection = floatArrayOf(1.0f, 0.3f, 0.4f, 0.75f, -0.5f, 0.7f, 0.63f, -0.3f)
    private val waterFrequency = floatArrayOf(17.951958f, 12.566371f, 8.975979f, 15.707963f)
    private val waterPhase = floatArrayOf(1.73f, 0.64f, 1.27f, 0.9f)
    private val water_vertex_size_bytes = 3468
    private var indexBuffer: GLLoadableBuffer? = null
    private var index_count: Int = 0
    private var vertexBuffer: GLLoadableBuffer? = null
    private var waterIndexBuffer: GLLoadableBuffer? = null
    private var waterVertexBuffer: GLLoadableBuffer? = null
    private var water_index_count: Int = 0

    constructor(terrainPatchHeightMap: TerrainPatchHeightMap) {
        val directByteBuffer = DirectByteBuffer(vertex_size_bytes)
        val directByteBuffer2 = DirectByteBuffer(water_vertex_size_bytes)
        val directByteBuffer3 = DirectByteBuffer(index_size_bytes)
        val directByteBuffer4 = DirectByteBuffer(index_size_bytes)
        directByteBuffer.position(0)
        directByteBuffer2.position(0)
        this.index_count = 0
        this.water_index_count = 0
        val lLVector3 = LLVector3()
        val waterHeight = terrainPatchHeightMap.getWaterHeight()
        val heightArray = terrainPatchHeightMap.getHeightArray()
        val normalArray = terrainPatchHeightMap.getNormalArray()
        var i = 0
        var i2 = 0
        while (i < 17) {
            for (i4 in 0 until 17) {
                directByteBuffer.putFloat(i4.toFloat())
                directByteBuffer.putFloat(i.toFloat())
                directByteBuffer.putFloat(heightArray[i2 + i4])
                lLVector3.set(-normalArray[(i2 + i4) * 2], normalArray[((i2 + i4) * 2) + 1], 2.0f)
                lLVector3.normVec()
                directByteBuffer.putFloat(lLVector3.x)
                directByteBuffer.putFloat(lLVector3.y)
                directByteBuffer.putFloat(lLVector3.z)
                directByteBuffer.putFloat(i4.toFloat() / 16.0f)
                directByteBuffer.putFloat(i.toFloat() / 16.0f)
                directByteBuffer2.putFloat(i4.toFloat())
                directByteBuffer2.putFloat(i.toFloat())
                directByteBuffer2.putFloat(waterHeight)
            }
            i2 += 17
            i++
        }
        directByteBuffer3.position(0)
        directByteBuffer4.position(0)
        var i5 = 0
        var i6 = 0
        while (i5 < 16) {
            for (i8 in 0 until 16) {
                val i9 = i6 + i8
                val i10 = i9 + 1
                val i11 = i9 + 17
                val i12 = i11 + 1
                directByteBuffer3.putShort(i9.toShort())
                directByteBuffer3.putShort(i10.toShort())
                directByteBuffer3.putShort(i11.toShort())
                directByteBuffer3.putShort(i10.toShort())
                directByteBuffer3.putShort(i12.toShort())
                directByteBuffer3.putShort(i11.toShort())
                this.index_count += 6
                
                val f1: Float = directByteBuffer.getFloat((i9 * 8) + 2)
                val f2: Float = directByteBuffer.getFloat((i10 * 8) + 2)
                val f3: Float = directByteBuffer.getFloat((i11 * 8) + 2)
                val f4: Float = directByteBuffer.getFloat((i12 * 8) + 2)
                
                // Using if/else with explicit casts to Float to resolve ambiguity
                val min1 = if ((f1 as Float) < (f2 as Float)) f1 else f2
                val min2 = if ((f3 as Float) < (f4 as Float)) f3 else f4
                val minH = if ((min1 as Float) < (min2 as Float)) min1 else min2
                
                if ((minH as Float) <= (waterHeight as Float)) {
                    directByteBuffer4.putShort(i9.toShort())
                    directByteBuffer4.putShort(i10.toShort())
                    directByteBuffer4.putShort(i11.toShort())
                    directByteBuffer4.putShort(i10.toShort())
                    directByteBuffer4.putShort(i12.toShort())
                    directByteBuffer4.putShort(i11.toShort())
                    this.water_index_count += 6
                }
            }
            i6 += 17
            i5++
        }
        
        this.vertexBuffer = GLLoadableBuffer(directByteBuffer)
        this.indexBuffer = GLLoadableBuffer(directByteBuffer3)
        this.waterVertexBuffer = GLLoadableBuffer(directByteBuffer2)
        this.waterIndexBuffer = GLLoadableBuffer(directByteBuffer4)
    }

    fun GLPrepare(renderContext: RenderContext) {
        if (renderContext.hasGL20) {
            GLES20.glUseProgram(renderContext.primProgram.getHandle())
            renderContext.glModelApplyMatrix(renderContext.primProgram.uMVPMatrix)
            renderContext.primProgram.SetupLighting(renderContext, renderContext.windlightPreset)
            GLES20.glUniform4f(renderContext.primProgram.uObjCoordScale, 1.0f, 1.0f, 1.0f, 1.0f)
            GLES20.glUniformMatrix4fv(renderContext.primProgram.uTexMatrix, 1, false, IdentityMatrix.getMatrix(), 0)
            GLES20.glUseProgram(renderContext.waterProgram.getHandle())
            GLES20.glUniform4f(renderContext.waterProgram.vColor, 0.4f, 0.4f, 0.6f, 1.0f)
            renderContext.glModelApplyMatrix(renderContext.waterProgram.uMVPMatrix)
            GLES20.glUniform1f(renderContext.waterProgram.uTime, renderContext.waterTime)
            GLES20.glUniform1fv(renderContext.waterProgram.uFrequency, 4, waterFrequency, 0)
            GLES20.glUniform1fv(renderContext.waterProgram.uPhase, 4, waterPhase, 0)
            GLES20.glUniform1fv(renderContext.waterProgram.uAmplitude, 4, waterAmplitude, 0)
            GLES20.glUniform2fv(renderContext.waterProgram.uDirection, 4, waterDirection, 0)
            return
        }
        GLES11.glMatrixMode(5890)
        GLES11.glLoadMatrixf(IdentityMatrix.getMatrix(), 0)
        GLES11.glMatrixMode(5888)
    }

    fun GLDraw(renderContext: RenderContext, fArr: FloatArray, gLLoadedTexture: GLLoadedTexture?) {
        if (this.index_count != 0) {
            if (!renderContext.hasGL20) {
                renderContext.glObjWorldPushAndMultMatrixf(fArr, 0)
            }
            if (renderContext.hasGL20) {
                GLES20.glUseProgram(renderContext.primProgram.getHandle())
                this.vertexBuffer!!.Bind20(renderContext, renderContext.primProgram.vPosition, 3, 5126, 32, 0)
                this.vertexBuffer!!.Bind20(renderContext, renderContext.primProgram.vNormal, 3, 5126, 32, 12)
                GLES20.glUniformMatrix4fv(renderContext.primProgram.uObjWorldMatrix, 1, false, fArr, 0)
                
                if (gLLoadedTexture != null) {
                    gLLoadedTexture.GLDraw()
                    this.vertexBuffer!!.Bind20(renderContext, renderContext.primProgram.vTexCoord, 2, 5126, 32, 24)
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
                this.indexBuffer!!.BindElements20(renderContext)
                this.indexBuffer!!.DrawElements20(4, this.index_count, 5123, 0)
            } else {
                GLES10.glDisableClientState(32885)
                if (gLLoadedTexture != null) {
                    GLES10.glEnable(3553)
                    gLLoadedTexture.GLDraw()
                    GLES10.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                    this.vertexBuffer!!.Bind(renderContext, 32888, 2, 5126, 32, 24)
                } else {
                    GLES10.glDisableClientState(32888)
                    GLES10.glDisable(3553)
                    GLES10.glColor4f(0.1f, 0.5f, 0.1f, 1.0f)
                }
                this.vertexBuffer!!.Bind(renderContext, 32884, 3, 5126, 32, 0)
                this.indexBuffer!!.BindElements(renderContext)
                this.indexBuffer!!.DrawElements(renderContext, 4, this.index_count, 5123, 0)
            }
            if (this.water_index_count != 0) {
                if (renderContext.hasGL20) {
                    GLES20.glDisable(2884)
                    GLES20.glUseProgram(renderContext.waterProgram.getHandle())
                    GLES20.glUniformMatrix4fv(renderContext.waterProgram.uObjWorldMatrix, 1, false, fArr, 0)
                    this.waterVertexBuffer!!.Bind20(renderContext, renderContext.waterProgram.vPosition, 3, 5126, 0, 0)
                    this.waterIndexBuffer!!.BindElements20(renderContext)
                    this.waterIndexBuffer!!.DrawElements20(4, this.water_index_count, 5123, 0)
                    GLES20.glEnable(2884)
                } else {
                    GLES10.glDisable(2884)
                    GLES10.glDisableClientState(32888)
                    GLES10.glDisable(3553)
                    GLES10.glColor4f(0.4f, 0.4f, 0.6f, 1.0f)
                    this.waterVertexBuffer!!.Bind(renderContext, 32884, 3, 5126, 0, 0)
                    this.waterIndexBuffer!!.BindElements(renderContext)
                    this.waterIndexBuffer!!.DrawElements(renderContext, 4, this.water_index_count, 5123, 0)
                    GLES10.glEnable(2884)
                }
            }
            if (!renderContext.hasGL20) {
                renderContext.glObjWorldPopMatrix()
            }
        }
    }
}
