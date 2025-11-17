package com.linkpoint.slproto.avatar

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.Debug
import com.linkpoint.openjpeg.OpenJPEG
import com.linkpoint.render.GLTexture
import com.linkpoint.rawbuffers.DirectByteBuffer
import java.io.DataInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

class SLPolyMorphData {
    private DirectByteBuffer indexBuffer
    private Boolean isMasked
    private SLPolyMesh mesh
    private SLVisualParamID morphID
    private Int numVertices
    private DirectByteBuffer texCoordsBuffer
    private DirectByteBuffer vertexBuffer

    SLPolyMorphData(SLVisualParamID sLVisualParamID, SLPolyMesh sLPolyMesh, DataInputStream dataInputStream) throws IOException {
        Boolean z = false
        this.morphID = sLVisualParamID
        this.mesh = sLPolyMesh
        this.isMasked = dataInputStream.readByte() != 0 ? true : z
        this.numVertices = dataInputStream.readInt()
        this.vertexBuffer = DirectByteBuffer(this.numVertices * 24)
        this.texCoordsBuffer = DirectByteBuffer(this.numVertices * 8)
        this.indexBuffer = DirectByteBuffer(this.numVertices * 4)
        this.vertexBuffer.read(dataInputStream)
        this.texCoordsBuffer.read(dataInputStream)
        this.indexBuffer.read(dataInputStream)
        Debug.Log("SLPolyMorphData: Loaded morph '" + sLVisualParamID + "', vertices = " + this.numVertices)
    }

    Unit applyMorphData(SLMeshData sLMeshData, float f, GLTexture gLTexture) {
        ByteBuffer byteBuffer = null
        Int i3 = 0
        if (this.isMasked && gLTexture != null) {
            i2 = gLTexture.getWidth()
            i = gLTexture.getHeight()
            byteBuffer = gLTexture.getExtraComponentsBuffer()
            if (byteBuffer != null) {
                i3 = byteBuffer.position()
            }
        } else {
            i = 0
            i2 = 0
        }
        OpenJPEG.applyMeshMorph(f, sLMeshData.vertexBuffer.asByteBuffer(), sLMeshData.texCoordsBuffer.asByteBuffer(), this.numVertices, this.indexBuffer.asByteBuffer(), this.vertexBuffer.asByteBuffer(), this.texCoordsBuffer.asByteBuffer(), i2, i, i3, byteBuffer)
    }

    Unit applyMorphDataSlow(SLMeshData sLMeshData, float f, GLTexture gLTexture) {
        FloatBuffer asFloatBuffer = this.vertexBuffer.asFloatBuffer()
        FloatBuffer asFloatBuffer2 = this.texCoordsBuffer.asFloatBuffer()
        IntBuffer asIntBuffer = this.indexBuffer.asIntBuffer()
        FloatBuffer asFloatBuffer3 = sLMeshData.vertexBuffer.asFloatBuffer()
        FloatBuffer asFloatBuffer4 = sLMeshData.texCoordsBuffer.asFloatBuffer()
        Boolean z = this.isMasked && gLTexture != null
        Int i = 0
        Int i2 = 0
        ByteBuffer byteBuffer = null
        Int i3 = 0
        if (z) {
            i = gLTexture.getWidth()
            i2 = gLTexture.getHeight()
            byteBuffer = gLTexture.getExtraComponentsBuffer()
            if (byteBuffer != null) {
                i3 = byteBuffer.position()
            } else {
                z = false
            }
        }
        for (Int i4 = 0; i4 < this.numVertices; i4++) {
            Int i5 = asIntBuffer.get(i4)
            float f2 = z ? (((float) (byteBuffer.get((Math.toInt().floor((double) (asFloatBuffer4.get((i5 * 2) + 0) * ((float) i)))) + (((Math.toInt().floor((double) (asFloatBuffer4.get((i5 * 2) + 1) * ((float) i2)))) * i) + i3)) & UnsignedBytes.MAX_VALUE)) / 255.0f) * f : f
            for (Int i6 = 0; i6 < 6; i6++) {
                asFloatBuffer3.put((i5 * 6) + i6, asFloatBuffer3.get((i5 * 6) + i6) + (asFloatBuffer.get((i4 * 6) + i6) * f2))
            }
            for (Int i7 = 0; i7 < 2; i7++) {
                asFloatBuffer4.put((i5 * 2) + i7, asFloatBuffer4.get((i5 * 2) + i7) + (asFloatBuffer2.get((i4 * 2) + i7) * f2))
            }
        }
    }
}
