package com.linkpoint.slproto.avatar

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.Debug
import com.linkpoint.openjpeg.OpenJPEG
import com.linkpoint.render.GLTexture
import com.linkpoint.rawbuffers.DirectByteBuffer
import java.io.DataInputStream
import java.io.IOException
import java.nio.ByteBuffer

class SLPolyMorphData {
    private var indexBuffer: DirectByteBuffer? = null
    private var isMasked: Boolean = false
    private var mesh: SLPolyMesh? = null
    private var morphID: SLVisualParamID? = null
    private var numVertices: Int = 0
    private var texCoordsBuffer: DirectByteBuffer? = null
    private var vertexBuffer: DirectByteBuffer? = null

    companion object {
        val EMPTY = SLPolyMorphData()
    }

    // Empty constructor for EMPTY companion object
    private constructor()

    @Throws(IOException::class)
    constructor(sLVisualParamID: SLVisualParamID, sLPolyMesh: SLPolyMesh, dataInputStream: DataInputStream) {
        this.morphID = sLVisualParamID
        this.mesh = sLPolyMesh
        this.isMasked = dataInputStream.readByte().toInt() != 0
        this.numVertices = dataInputStream.readInt()
        this.vertexBuffer = DirectByteBuffer(this.numVertices * 24)
        this.texCoordsBuffer = DirectByteBuffer(this.numVertices * 8)
        this.indexBuffer = DirectByteBuffer(this.numVertices * 4)
        this.vertexBuffer?.read(dataInputStream)
        this.texCoordsBuffer?.read(dataInputStream)
        this.indexBuffer?.read(dataInputStream)
        Debug.Log("SLPolyMorphData: Loaded morph '$sLVisualParamID', vertices = ${this.numVertices}")
    }

    fun applyMorphData(sLMeshData: SLMeshData, f: Float, gLTexture: GLTexture?) {
        val vertBuf = this.vertexBuffer ?: return
        val texCoordBuf = this.texCoordsBuffer ?: return
        val idxBuf = this.indexBuffer ?: return
        val meshVertBuf = sLMeshData.vertexBuffer ?: return
        val meshTexCoordBuf = sLMeshData.texCoordsBuffer ?: return
        
        var byteBuffer: ByteBuffer? = null
        var i3 = 0
        var i = 0
        var i2 = 0
        
        if (this.isMasked && gLTexture != null) {
            i2 = gLTexture.getWidth()
            i = gLTexture.getHeight()
            byteBuffer = gLTexture.getExtraComponentsBuffer()
            if (byteBuffer != null) {
                i3 = byteBuffer.position()
            }
        }
        
        OpenJPEG.applyMeshMorph(
            f,
            meshVertBuf.asByteBuffer(),
            meshTexCoordBuf.asByteBuffer(),
            this.numVertices,
            idxBuf.asByteBuffer(),
            vertBuf.asByteBuffer(),
            texCoordBuf.asByteBuffer(),
            i2,
            i,
            i3,
            byteBuffer
        )
    }

    fun applyMorphDataSlow(sLMeshData: SLMeshData, f: Float, gLTexture: GLTexture?) {
        val vertBuf = this.vertexBuffer ?: return
        val texCoordBuf = this.texCoordsBuffer ?: return
        val idxBuf = this.indexBuffer ?: return
        val meshVertBuf = sLMeshData.vertexBuffer ?: return
        val meshTexCoordBuf = sLMeshData.texCoordsBuffer ?: return
        
        val asFloatBuffer = vertBuf.asFloatBuffer()
        val asFloatBuffer2 = texCoordBuf.asFloatBuffer()
        val asIntBuffer = idxBuf.asIntBuffer()
        val asFloatBuffer3 = meshVertBuf.asFloatBuffer()
        val asFloatBuffer4 = meshTexCoordBuf.asFloatBuffer()
        var z = this.isMasked && gLTexture != null
        var i = 0
        var i2 = 0
        var byteBuffer: ByteBuffer? = null
        var i3 = 0
        
        if (z) {
            i = gLTexture!!.getWidth()
            i2 = gLTexture.getHeight()
            byteBuffer = gLTexture.getExtraComponentsBuffer()
            if (byteBuffer != null) {
                i3 = byteBuffer.position()
            } else {
                z = false
            }
        }
        
        for (i4 in 0 until this.numVertices) {
            val i5 = asIntBuffer.get(i4)
            val f2 = if (z && byteBuffer != null) {
                val texU = asFloatBuffer4.get((i5 * 2) + 0)
                val texV = asFloatBuffer4.get((i5 * 2) + 1)
                val pixelX = Math.floor((texU * i.toFloat()).toDouble()).toInt()
                val pixelY = Math.floor((texV * i2.toFloat()).toDouble()).toInt()
                val bufferIndex = pixelX + (pixelY * i) + i3
                ((byteBuffer.get(bufferIndex).toInt() and UnsignedBytes.MAX_VALUE.toInt()).toFloat() / 255.0f) * f
            } else {
                f
            }
            for (i6 in 0 until 6) {
                asFloatBuffer3.put((i5 * 6) + i6, asFloatBuffer3.get((i5 * 6) + i6) + (asFloatBuffer.get((i4 * 6) + i6) * f2))
            }
            for (i7 in 0 until 2) {
                asFloatBuffer4.put((i5 * 2) + i7, asFloatBuffer4.get((i5 * 2) + i7) + (asFloatBuffer2.get((i4 * 2) + i7) * f2))
            }
        }
    }
}
