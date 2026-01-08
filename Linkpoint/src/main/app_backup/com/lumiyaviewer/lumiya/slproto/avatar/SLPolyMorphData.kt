package com.lumiyaviewer.lumiya.slproto.avatar

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG
import com.lumiyaviewer.lumiya.render.GLTexture
import com.lumiyaviewer.rawbuffers.DirectByteBuffer
import java.io.DataInputStream
import java.io.IOException
import java.nio.ByteBuffer
import kotlin.math.floor

class SLPolyMorphData(
    private val morphID: SLVisualParamID,
    private val mesh: SLPolyMesh,
    dataInputStream: DataInputStream
) {
    private val isMasked: Boolean
    private val numVertices: Int
    private val vertexBuffer: DirectByteBuffer
    private val texCoordsBuffer: DirectByteBuffer
    private val indexBuffer: DirectByteBuffer

    init {
        isMasked = dataInputStream.readByte().toInt() != 0
        numVertices = dataInputStream.readInt()
        vertexBuffer = DirectByteBuffer(numVertices * 24)
        texCoordsBuffer = DirectByteBuffer(numVertices * 8)
        indexBuffer = DirectByteBuffer(numVertices * 4)
        
        vertexBuffer.read(dataInputStream)
        texCoordsBuffer.read(dataInputStream)
        indexBuffer.read(dataInputStream)
        
        Debug.Log("SLPolyMorphData: Loaded morph '$morphID', vertices = $numVertices")
    }

    fun applyMorphData(sLMeshData: SLMeshData, f: Float, gLTexture: GLTexture?) {
        var byteBuffer: ByteBuffer? = null
        var width = 0
        var height = 0
        var offset = 0
        
        if (isMasked && gLTexture != null) {
            width = gLTexture.width
            height = gLTexture.height
            byteBuffer = gLTexture.extraComponentsBuffer
            if (byteBuffer != null) {
                offset = byteBuffer.position()
            }
        }
        
        OpenJPEG.applyMeshMorph(
            f,
            sLMeshData.vertexBuffer.asByteBuffer(),
            sLMeshData.texCoordsBuffer.asByteBuffer(),
            numVertices,
            indexBuffer.asByteBuffer(),
            vertexBuffer.asByteBuffer(),
            texCoordsBuffer.asByteBuffer(),
            width,
            height,
            offset,
            byteBuffer
        )
    }

    fun applyMorphDataSlow(sLMeshData: SLMeshData, f: Float, gLTexture: GLTexture?) {
        val asFloatBuffer = vertexBuffer.asFloatBuffer()
        val asFloatBuffer2 = texCoordsBuffer.asFloatBuffer()
        val asIntBuffer = indexBuffer.asIntBuffer()
        val asFloatBuffer3 = sLMeshData.vertexBuffer.asFloatBuffer()
        val asFloatBuffer4 = sLMeshData.texCoordsBuffer.asFloatBuffer()
        
        var useMask = isMasked && gLTexture != null
        var width = 0
        var height = 0
        var maskBuffer: ByteBuffer? = null
        var maskOffset = 0
        
        if (useMask) {
            width = gLTexture!!.width
            height = gLTexture.height
            maskBuffer = gLTexture.extraComponentsBuffer
            if (maskBuffer != null) {
                maskOffset = maskBuffer.position()
            } else {
                useMask = false
            }
        }
        
        for (i in 0 until numVertices) {
            val index = asIntBuffer.get(i)
            var factor = f
            
            if (useMask && maskBuffer != null) {
                val u = asFloatBuffer4.get(index * 2 + 0)
                val v = asFloatBuffer4.get(index * 2 + 1)
                val x = floor(u * width).toInt()
                val y = floor(v * height).toInt()
                val maskVal = maskBuffer.get(maskOffset + y * width + x).toInt() and 0xFF
                factor = (maskVal / 255.0f) * f
            }
            
            for (j in 0 until 6) {
                val original = asFloatBuffer3.get(index * 6 + j)
                val morph = asFloatBuffer.get(i * 6 + j)
                asFloatBuffer3.put(index * 6 + j, original + morph * factor)
            }
            
            for (j in 0 until 2) {
                val original = asFloatBuffer4.get(index * 2 + j)
                val morph = asFloatBuffer2.get(i * 2 + j)
                asFloatBuffer4.put(index * 2 + j, original + morph * factor)
            }
        }
    }
}
