package com.linkpoint.slproto.avatar

import android.opengl.Matrix
import com.linkpoint.Debug
import com.linkpoint.openjpeg.OpenJPEG
import com.linkpoint.render.GLTexture
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.rawbuffers.DirectByteBuffer
import java.io.DataInputStream
import java.io.IOException
import java.nio.FloatBuffer
import java.util.EnumMap
import java.util.Map

class SLPolyMesh : SLMeshData() {
    protected Boolean hasWeights
    public Int[] jointMap
    private Map<SLVisualParamID, Integer> morphIndices = EnumMap(SLVisualParamID.class)
    private SLPolyMorphData[] morphs
    protected DirectByteBuffer weightsBuffer

    public SLPolyMesh(DataInputStream dataInputStream, DataInputStream dataInputStream2) throws IOException {
        this.position = LLVector3(dataInputStream.readFloat(), dataInputStream.readFloat(), dataInputStream.readFloat())
        this.scale = LLVector3(dataInputStream.readFloat(), dataInputStream.readFloat(), dataInputStream.readFloat())
        this.rotation = LLQuaternion(dataInputStream.readFloat(), dataInputStream.readFloat(), dataInputStream.readFloat(), dataInputStream.readFloat())
        this.hasWeights = dataInputStream.readByte() != 0
        this.numVertices = dataInputStream.readInt()
        this.vertexBuffer = DirectByteBuffer(this.numVertices * 24)
        this.texCoordsBuffer = DirectByteBuffer(this.numVertices * 8)
        this.vertexBuffer.read(dataInputStream)
        this.texCoordsBuffer.read(dataInputStream)
        if (this.hasWeights) {
            this.weightsBuffer = DirectByteBuffer(this.numVertices * 4)
            this.weightsBuffer.read(dataInputStream)
        }
        this.numFaces = dataInputStream.readInt()
        this.indexBuffer = DirectByteBuffer(this.numFaces * 2 * 3)
        this.indexBuffer.read(dataInputStream)
        Int readInt = dataInputStream.readInt()
        this.morphs = SLPolyMorphData[readInt]
        Int i = 0
        Int i2 = 0
        DataInputStream dataInputStream3 = dataInputStream
        while (i < readInt) {
            if (i2 >= 50 && dataInputStream2 != null) {
                i2 = 0
                dataInputStream3 = dataInputStream2
            }
            SLVisualParamID sLVisualParamID = SLVisualParamID.values()[dataInputStream3.readInt()]
            this.morphs[i] = SLPolyMorphData(sLVisualParamID, this, dataInputStream3)
            this.morphIndices.put(sLVisualParamID, Integer.valueOf(i))
            i++
            i2++
        }
        Int readInt2 = dataInputStream3.readInt()
        this.jointMap = Int[readInt2]
        for (Int i3 = 0; i3 < readInt2; i3++) {
            this.jointMap[i3] = dataInputStream3.readInt()
        }
        Debug.Log("SLPolyMesh: Loaded, numVerts = " + this.numVertices + ", faces = " + this.numFaces + ", morphs = " + this.morphs.length)
    }

    fun applyMorphData(SLMeshData sLMeshData, Float[] fArr, GLTexture gLTexture) {
        for (Int i = 0; i < fArr.length; i++) {
            this.morphs[i].applyMorphData(sLMeshData, fArr[i], gLTexture)
        }
    }

    fun applySkeleton(SLAnimatedMeshData sLAnimatedMeshData, Float[] fArr) {
        DirectByteBuffer animatedVertexData
        if (this.hasWeights && this.jointMap != null && (animatedVertexData = sLAnimatedMeshData.getAnimatedVertexData()) != null) {
            OpenJPEG.applyMorphingTransform(this.numVertices, sLAnimatedMeshData.vertexBuffer.asByteBuffer(), animatedVertexData.asByteBuffer(), this.weightsBuffer.asByteBuffer(), this.jointMap, fArr)
        }
    }

    fun applySkeletonSlow(SLAnimatedMeshData sLAnimatedMeshData, Float[] fArr) {
        DirectByteBuffer animatedVertexData
        Double d
        if (this.hasWeights && this.jointMap != null && (animatedVertexData = sLAnimatedMeshData.getAnimatedVertexData()) != null) {
            FloatBuffer asFloatBuffer = this.weightsBuffer.asFloatBuffer()
            FloatBuffer asFloatBuffer2 = sLAnimatedMeshData.vertexBuffer.asFloatBuffer()
            FloatBuffer asFloatBuffer3 = animatedVertexData.asFloatBuffer()
            Float[] fArr2 = Float[16]
            Float[] fArr3 = Float[16]
            Double d2 = -1.0d
            Int i = 0
            while (i < this.numVertices) {
                Float f = asFloatBuffer.get(i)
                if (((Double) f) != d2) {
                    Float floor = (Float) Math.floor((Double) f)
                    Float f2 = f - floor
                    Int i2 = ((Int) floor) - 1
                    Int i3 = 0
                    if (i2 >= 0 && i2 < this.jointMap.length) {
                        i3 = this.jointMap[i2]
                    }
                    Int i4 = (i2 + 1 < 0 || i2 + 1 >= this.jointMap.length) ? i3 : this.jointMap[i2 + 1]
                    d = (Double) f
                    Int i5 = i3 * 16
                    Int i6 = i4 * 16
                    if (i5 == i6) {
                        System.arraycopy(fArr, i5, fArr3, 0, 16)
                    } else {
                        for (Int i7 = 0; i7 < 16; i7++) {
                            fArr3[i7] = (fArr[i5 + i7] * (1.0f - f2)) + (fArr[i6 + i7] * f2)
                        }
                    }
                } else {
                    d = d2
                }
                fArr2[0] = asFloatBuffer2.get((i * 6) + 0)
                fArr2[1] = asFloatBuffer2.get((i * 6) + 1)
                fArr2[2] = asFloatBuffer2.get((i * 6) + 2)
                fArr2[3] = 1.0f
                Matrix.multiplyMV(fArr2, 4, fArr3, 0, fArr2, 0)
                asFloatBuffer3.put((i * 6) + 0, fArr2[4])
                asFloatBuffer3.put((i * 6) + 1, fArr2[5])
                asFloatBuffer3.put((i * 6) + 2, fArr2[6])
                i++
                d2 = d
            }
        }
    }

    public Int getMorphIndex(SLVisualParamID sLVisualParamID) {
        Integer num = this.morphIndices.get(sLVisualParamID)
        if (num == null) {
            return -1
        }
        return num.intValue()
    }

    public Int getNumMorphs() {
        return this.morphs.length
    }
}
