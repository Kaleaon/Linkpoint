package com.linkpoint.render.terrain

import kotlin.math.*
import java.util.*

import android.opengl.GLES10
import android.opengl.GLES11
import android.opengl.GLES20
import com.linkpoint.render.RenderContext
import com.linkpoint.render.glres.buffers.GLLoadableBuffer
import com.linkpoint.render.glres.textures.GLLoadedTexture
import com.linkpoint.slproto.terrain.TerrainPatchHeightMap
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.utils.IdentityMatrix
import com.linkpoint.rawbuffers.DirectByteBuffer

class TerrainPatchGeometry {
    var DrawPatchSize: Int = 16
    private Int index_size_bytes = 3072
    private Int vertex_size_bytes = 9248
    private val waterAmplitude: FloatArray = {0.5f, 0.5f, 0.3f, 0.4f}
    private val waterDirection: FloatArray = {1.0f, 0.3f, 0.4f, 0.75f, -0.5f, 0.7f, 0.63f, -0.3f}
    private val waterFrequency: FloatArray = {17.951958f, 12.566371f, 8.975979f, 15.707963f}
    private val waterPhase: FloatArray = {1.73f, 0.64f, 1.27f, 0.9f}
    private Int water_vertex_size_bytes = 3468
    private GLLoadableBuffer indexBuffer
    private var index_count: Int = 0
    private GLLoadableBuffer vertexBuffer
    private GLLoadableBuffer waterIndexBuffer
    private GLLoadableBuffer waterVertexBuffer
    private var water_index_count: Int = 0

    constructor(terrainPatchHeightMap: TerrainPatchHeightMap) {
        DirectByteBuffer directByteBuffer = DirectByteBuffer(vertex_size_bytes.toInt())
        DirectByteBuffer directByteBuffer2 = DirectByteBuffer(water_vertex_size_bytes.toInt())
        DirectByteBuffer directByteBuffer3 = DirectByteBuffer(index_size_bytes.toInt())
        DirectByteBuffer directByteBuffer4 = DirectByteBuffer(index_size_bytes.toInt())
        directByteBuffer.position(0)
        directByteBuffer2.position(0)
        this.index_count = 0
        this.water_index_count = 0
        LLVector3 lLVector3 = LLVector3()
        var waterHeight: Float = terrainPatchHeightMap.getWaterHeight()
        FloatArray heightArray = terrainPatchHeightMap.getHeightArray()
        FloatArray normalArray = terrainPatchHeightMap.getNormalArray()
        var i: Int = 0
        var i2: Int = 0
        while (true) {
            var i3: Int = i
            if (i3 >= 17) {
                break
            }
            for (i4 in 0 until 17) {
                directByteBuffer.putFloat(i4.toFloat())
                directByteBuffer.putFloat(i3.toFloat())
                directByteBuffer.putFloat(heightArray[i2 + i4])
                lLVector3.set(-normalArray[(i2 + i4) * 2], normalArray[((i2 + i4) * 2) + 1], 2.0f)
                lLVector3.normVec()
                directByteBuffer.putFloat(lLVector3.x)
                directByteBuffer.putFloat(lLVector3.y)
                directByteBuffer.putFloat(lLVector3.z)
                directByteBuffer.putFloat((i4.toFloat()) / 16.0f)
                directByteBuffer.putFloat((i3.toFloat()) / 16.0f)
                directByteBuffer2.putFloat(i4.toFloat())
                directByteBuffer2.putFloat(i3.toFloat())
                directByteBuffer2.putFloat(waterHeight)
            }
            i2 += 17
            i = i3 + 1
        }
        directByteBuffer3.position(0)
        directByteBuffer4.position(0)
        var i5: Int = 0
        var i6: Int = 0
        while (true) {
            var i7: Int = i5
            if (i7 < 16) {
                for (i8 in 0 until 16) {
                    var i9: Int = i6 + i8
                    var i10: Int = i9 + 1
                    var i11: Int = i9 + 17
                    var i12: Int = i11 + 1
                    directByteBuffer3.putShort((Short) i9)
                    directByteBuffer3.putShort((Short) i10)
                    directByteBuffer3.putShort((Short) i11)
                    directByteBuffer3.putShort((Short) i10)
                    directByteBuffer3.putShort((Short) i12)
                    directByteBuffer3.putShort((Short) i11)
                    this.index_count += 6
                    if (min(min(min(directByteBuffer.getFloat((i9 * 8) + 2), directByteBuffer.getFloat((i10 * 8) + 2)), directByteBuffer.getFloat((i11 * 8) + 2)), directByteBuffer.getFloat((i12 * 8) + 2)) <= waterHeight) {
                        directByteBuffer4.putShort((Short) i9)
                        directByteBuffer4.putShort((Short) i10)
                        directByteBuffer4.putShort((Short) i11)
                        directByteBuffer4.putShort((Short) i10)
                        directByteBuffer4.putShort((Short) i12)
                        directByteBuffer4.putShort((Short) i11)
                        this.water_index_count += 6
                    }
                }
                i6 += 17
                i5 = i7 + 1
            } else {
                this.vertexBuffer = GLLoadableBuffer(directByteBuffer)
                this.indexBuffer = GLLoadableBuffer(directByteBuffer3)
                this.waterVertexBuffer = GLLoadableBuffer(directByteBuffer2)
                this.waterIndexBuffer = GLLoadableBuffer(directByteBuffer4)
                return
            }
        }
    }

    fun GLPrepare(renderContext: RenderContext)  {
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

    fun GLDraw(RenderContext renderContext, FloatArray fArr, GLLoadedTexture gLLoadedTexture)  {
        if (this.index_count != 0) {
            if (!renderContext.hasGL20) {
                renderContext.glObjWorldPushAndMultMatrixf(fArr, 0)
            }
            if (renderContext.hasGL20) {
                GLES20.glUseProgram(renderContext.primProgram.getHandle())
                this.vertexBuffer.Bind20(renderContext, renderContext.primProgram.vPosition, 3, 5126, 32, 0)
                this.vertexBuffer.Bind20(renderContext, renderContext.primProgram.vNormal, 3, 5126, 32, 12)
                GLES20.glUniformMatrix4fv(renderContext.primProgram.uObjWorldMatrix, 1, false, fArr, 0)
                gLLoadedTexture?.GLDraw()
                    this.vertexBuffer.Bind20(renderContext, renderContext.primProgram.vTexCoord, 2, 5126, 32, 24)
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
                this.indexBuffer.BindElements20(renderContext)
                this.indexBuffer.DrawElements20(4, this.index_count, 5123, 0)
            } else {
                GLES10.glDisableClientState(32885)
                if (gLLoadedTexture != null) {
                    GLES10.glEnable(3553)
                    gLLoadedTexture.GLDraw()
                    GLES10.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
                    this.vertexBuffer.Bind(renderContext, 32888, 2, 5126, 32, 24)
                } else {
                    GLES10.glDisableClientState(32888)
                    GLES10.glDisable(3553)
                    GLES10.glColor4f(0.1f, 0.5f, 0.1f, 1.0f)
                }
                this.vertexBuffer.Bind(renderContext, 32884, 3, 5126, 32, 0)
                this.indexBuffer.BindElements(renderContext)
                this.indexBuffer.DrawElements(renderContext, 4, this.index_count, 5123, 0)
            }
            if (this.water_index_count != 0) {
                if (renderContext.hasGL20) {
                    GLES20.glDisable(2884)
                    GLES20.glUseProgram(renderContext.waterProgram.getHandle())
                    GLES20.glUniformMatrix4fv(renderContext.waterProgram.uObjWorldMatrix, 1, false, fArr, 0)
                    this.waterVertexBuffer.Bind20(renderContext, renderContext.waterProgram.vPosition, 3, 5126, 0, 0)
                    this.waterIndexBuffer.BindElements20(renderContext)
                    this.waterIndexBuffer.DrawElements20(4, this.water_index_count, 5123, 0)
                    GLES20.glEnable(2884)
                } else {
                    GLES10.glDisable(2884)
                    GLES10.glDisableClientState(32888)
                    GLES10.glDisable(3553)
                    GLES10.glColor4f(0.4f, 0.4f, 0.6f, 1.0f)
                    this.waterVertexBuffer.Bind(renderContext, 32884, 3, 5126, 0, 0)
                    this.waterIndexBuffer.BindElements(renderContext)
                    this.waterIndexBuffer.DrawElements(renderContext, 4, this.water_index_count, 5123, 0)
                    GLES10.glEnable(2884)
                }
            }
            if (!renderContext.hasGL20) {
                renderContext.glObjWorldPopMatrix()
            }
        }
    }
}
