package com.linkpoint.slproto.avatar

import kotlin.math.*

import android.opengl.Matrix
import com.linkpoint.Debug
import com.linkpoint.openjpeg.OpenJPEG
import com.linkpoint.render.GLTexture
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.rawbuffers.DirectByteBuffer
import java.io.DataInputStream
import java.io.IOException
import java.util.EnumMap

class SLPolyMesh : SLMeshData {
    protected var hasWeights: Boolean = false
    var jointMap: IntArray? = null
    private val morphIndices: MutableMap<SLVisualParamID, Int> = EnumMap(SLVisualParamID::class.java)
    private lateinit var morphs: Array<SLPolyMorphData>
    protected var weightsBuffer: DirectByteBuffer? = null

    @Throws(IOException::class)
    constructor(dataInputStream: DataInputStream, dataInputStream2: DataInputStream?) {
        this.position = LLVector3(dataInputStream.readFloat(), dataInputStream.readFloat(), dataInputStream.readFloat())
        this.scale = LLVector3(dataInputStream.readFloat(), dataInputStream.readFloat(), dataInputStream.readFloat())
        this.rotation = LLQuaternion(dataInputStream.readFloat(), dataInputStream.readFloat(), dataInputStream.readFloat(), dataInputStream.readFloat())
        this.hasWeights = dataInputStream.readByte().toInt() != 0
        this.numVertices = dataInputStream.readInt()
        this.vertexBuffer = DirectByteBuffer(this.numVertices * 24)
        this.texCoordsBuffer = DirectByteBuffer(this.numVertices * 8)
        this.vertexBuffer?.read(dataInputStream)
        this.texCoordsBuffer?.read(dataInputStream)
        if (this.hasWeights) {
            this.weightsBuffer = DirectByteBuffer(this.numVertices * 4)
            this.weightsBuffer?.read(dataInputStream)
        }
        this.numFaces = dataInputStream.readInt()
        this.indexBuffer = DirectByteBuffer(this.numFaces * 2 * 3)
        this.indexBuffer?.read(dataInputStream)
        val readInt = dataInputStream.readInt()
        this.morphs = Array(readInt) { SLPolyMorphData.EMPTY }
        var i2 = 0
        var currentStream = dataInputStream
        for (i in 0 until readInt) {
            if (i2 >= 50 && dataInputStream2 != null) {
                i2 = 0
                currentStream = dataInputStream2
            }
            val sLVisualParamID = SLVisualParamID.values()[currentStream.readInt()]
            this.morphs[i] = SLPolyMorphData(sLVisualParamID, this, currentStream)
            this.morphIndices[sLVisualParamID] = i
            i2++
        }
        val readInt2 = currentStream.readInt()
        this.jointMap = IntArray(readInt2)
        for (i3 in 0 until readInt2) {
            this.jointMap!![i3] = currentStream.readInt()
        }
        Debug.Log("SLPolyMesh: Loaded, numVerts = " + this.numVertices + ", faces = " + this.numFaces + ", morphs = " + this.morphs.size)
    }

    fun applyMorphData(sLMeshData: SLMeshData, fArr: FloatArray, gLTexture: GLTexture?) {
        for (i in fArr.indices) {
            this.morphs[i].applyMorphData(sLMeshData, fArr[i], gLTexture)
        }
    }

    fun applySkeleton(sLAnimatedMeshData: SLAnimatedMeshData, fArr: FloatArray) {
        val jMap = this.jointMap
        val wBuffer = this.weightsBuffer
        if (this.hasWeights && jMap != null && wBuffer != null) {
            val animatedVertexData = sLAnimatedMeshData.getAnimatedVertexData()
            val vertBuf = sLAnimatedMeshData.vertexBuffer
            if (animatedVertexData != null && vertBuf != null) {
                OpenJPEG.applyMorphingTransform(
                    this.numVertices,
                    vertBuf.asByteBuffer(),
                    animatedVertexData.asByteBuffer(),
                    wBuffer.asByteBuffer(),
                    jMap,
                    fArr
                )
            }
        }
    }

    fun applySkeletonSlow(sLAnimatedMeshData: SLAnimatedMeshData, fArr: FloatArray) {
        val jMap = this.jointMap
        val wBuffer = this.weightsBuffer
        if (!this.hasWeights || jMap == null || wBuffer == null) return
        
        val animatedVertexData = sLAnimatedMeshData.getAnimatedVertexData()
        val vertBuf = sLAnimatedMeshData.vertexBuffer
        if (animatedVertexData == null || vertBuf == null) return
        
        val asFloatBuffer = wBuffer.asFloatBuffer()
        val asFloatBuffer2 = vertBuf.asFloatBuffer()
        val asFloatBuffer3 = animatedVertexData.asFloatBuffer()
        val fArr2 = FloatArray(16)
        val fArr3 = FloatArray(16)
        var d2 = -1.0
        
        for (i in 0 until this.numVertices) {
            val f = asFloatBuffer.get(i)
            val d = f.toDouble()
            if (d != d2) {
                val floor = floor(f.toDouble()).toFloat()
                val f2 = f - floor
                val i2 = floor.toInt() - 1
                var i3 = 0
                if (i2 >= 0 && i2 < jMap.size) {
                    i3 = jMap[i2]
                }
                val i4 = if (i2 + 1 < 0 || i2 + 1 >= jMap.size) i3 else jMap[i2 + 1]
                val i5 = i3 * 16
                val i6 = i4 * 16
                if (i5 == i6) {
                    System.arraycopy(fArr, i5, fArr3, 0, 16)
                } else {
                    for (i7 in 0 until 16) {
                        fArr3[i7] = (fArr[i5 + i7] * (1.0f - f2)) + (fArr[i6 + i7] * f2)
                    }
                }
            }
            d2 = d
            fArr2[0] = asFloatBuffer2.get((i * 6) + 0)
            fArr2[1] = asFloatBuffer2.get((i * 6) + 1)
            fArr2[2] = asFloatBuffer2.get((i * 6) + 2)
            fArr2[3] = 1.0f
            Matrix.multiplyMV(fArr2, 4, fArr3, 0, fArr2, 0)
            asFloatBuffer3.put((i * 6) + 0, fArr2[4])
            asFloatBuffer3.put((i * 6) + 1, fArr2[5])
            asFloatBuffer3.put((i * 6) + 2, fArr2[6])
        }
    }

    fun getMorphIndex(sLVisualParamID: SLVisualParamID): Int {
        return this.morphIndices[sLVisualParamID] ?: -1
    }

    fun getNumMorphs(): Int {
        return this.morphs.size
    }
}
