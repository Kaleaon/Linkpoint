package com.linkpoint.render.terrain
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
const val D: IntrawPatchSize = 16
    private const val Int index_size_bytes = 3072
    private const val Int vertex_size_bytes = 9248
    @JvmStatic
private FloatArray waterAmplitude = {0.5f, 0.5f, 0.3f, 0.4f}
    @JvmStatic
private FloatArray waterDirection = {1.0f, 0.3f, 0.4f, 0.75f, -0.5f, 0.7f, 0.63f, -0.3f}
    @JvmStatic
private FloatArray waterFrequency = {17.951958f, 12.566371f, 8.975979f, 15.707963f}
    @JvmStatic
private FloatArray waterPhase = {1.73f, 0.64f, 1.27f, 0.9f}
    private const val Int water_vertex_size_bytes = 3468
    private val GLLoadableBuffer indexBuffer
    private Int index_count = 0
    private val GLLoadableBuffer vertexBuffer
    private val GLLoadableBuffer waterIndexBuffer
    private val GLLoadableBuffer waterVertexBuffer
    private Int water_index_count = 0

    public TerrainPatchGeometry(TerrainPatchHeightMap terrainPatchHeightMap) {
        val directByteBuffer: DirectByteBuffer = DirectByteBuffer((Int) vertex_size_bytes)
        val directByteBuffer2: DirectByteBuffer = DirectByteBuffer((Int) water_vertex_size_bytes)
        val directByteBuffer3: DirectByteBuffer = DirectByteBuffer((Int) index_size_bytes)
        val directByteBuffer4: DirectByteBuffer = DirectByteBuffer((Int) index_size_bytes)
        directByteBuffer.position(0)
        directByteBuffer2.position(0)
        this.index_count = 0
        this.water_index_count = 0
        val lLVector3: LLVector3 = LLVector3()
        val waterHeight: Float = terrainPatchHeightMap.getWaterHeight()
        val heightArray: FloatArray = terrainPatchHeightMap.getHeightArray()
        val normalArray: FloatArray = terrainPatchHeightMap.getNormalArray()
        val i: Int = 0
        val i2: Int = 0
        while (true) {
            val i3: Int = i
            if (i3 >= 17) {
                break
            }
            for (Int i4 = 0; i4 < 17; i4++) {
                directByteBuffer.putFloat((Float) i4)
                directByteBuffer.putFloat((Float) i3)
                directByteBuffer.putFloat(heightArray[i2 + i4])
                lLVector3.set(-normalArray[(i2 + i4) * 2], normalArray[((i2 + i4) * 2) + 1], 2.0f)
                lLVector3.normVec()
                directByteBuffer.putFloat(lLVector3.x)
                directByteBuffer.putFloat(lLVector3.y)
                directByteBuffer.putFloat(lLVector3.z)
                directByteBuffer.putFloat(((Float) i4) / 16.0f)
                directByteBuffer.putFloat(((Float) i3) / 16.0f)
                directByteBuffer2.putFloat((Float) i4)
                directByteBuffer2.putFloat((Float) i3)
                directByteBuffer2.putFloat(waterHeight)
            }
            i2 += 17
            i = i3 + 1
        }
        directByteBuffer3.position(0)
        directByteBuffer4.position(0)
        val i5: Int = 0
        val i6: Int = 0
        while (true) {
            val i7: Int = i5
            if (i7 < 16) {
                for (Int i8 = 0; i8 < 16; i8++) {
                    val i9: Int = i6 + i8
                    val i10: Int = i9 + 1
                    val i11: Int = i9 + 17
                    val i12: Int = i11 + 1
                    directByteBuffer3.putShort((Short) i9)
                    directByteBuffer3.putShort((Short) i10)
                    directByteBuffer3.putShort((Short) i11)
                    directByteBuffer3.putShort((Short) i10)
                    directByteBuffer3.putShort((Short) i12)
                    directByteBuffer3.putShort((Short) i11)
                    this.index_count += 6
                    if (Math.min(Math.min(Math.min(directByteBuffer.getFloat((i9 * 8) + 2), directByteBuffer.getFloat((i10 * 8) + 2)), directByteBuffer.getFloat((i11 * 8) + 2)), directByteBuffer.getFloat((i12 * 8) + 2)) <= waterHeight) {
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

    @JvmStatic
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

    val Unit GLDraw(RenderContext renderContext, FloatArray fArr, GLLoadedTexture gLLoadedTexture) {
        if (this.index_count != 0) {
            if (!renderContext.hasGL20) {
                renderContext.glObjWorldPushAndMultMatrixf(fArr, 0)
            }
            if (renderContext.hasGL20) {
                GLES20.glUseProgram(renderContext.primProgram.getHandle())
                this.vertexBuffer.Bind20(renderContext, renderContext.primProgram.vPosition, 3, 5126, 32, 0)
                this.vertexBuffer.Bind20(renderContext, renderContext.primProgram.vNormal, 3, 5126, 32, 12)
                GLES20.glUniformMatrix4fv(renderContext.primProgram.uObjWorldMatrix, 1, false, fArr, 0)
                if (gLLoadedTexture != null) {
                    gLLoadedTexture.GLDraw()
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
