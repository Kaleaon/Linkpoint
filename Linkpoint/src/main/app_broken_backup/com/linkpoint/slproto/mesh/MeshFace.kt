package com.linkpoint.slproto.mesh

import com.linkpoint.openjpeg.OpenJPEG
import com.linkpoint.slproto.llsd.LLSDException
import com.linkpoint.slproto.llsd.LLSDNode
import com.linkpoint.slproto.types.LLVector2
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.rawbuffers.DirectByteBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

class MeshFace {
    private var indexBuffer: DirectByteBuffer? = null
    private var numIndices: Int = 0
    private var numVertices: Int = 0
    private var texCoordsBuffer: DirectByteBuffer? = null
    private var vertexBuffer: DirectByteBuffer? = null
    private var weightBuffer: DirectByteBuffer? = null

    @Throws(LLSDException::class)
    constructor(lLSDNode: LLSDNode) {
        if (lLSDNode.keyExists("NoGeometry") || (!lLSDNode.keyExists("Position")) || (!lLSDNode.keyExists("TriangleList"))) {
            this.vertexBuffer = null
            this.indexBuffer = null
            this.weightBuffer = null
            this.texCoordsBuffer = null
            this.numIndices = 0
            this.numVertices = 0
            return
        }
        val asBinary = lLSDNode.byKey("Position").asBinary()
        val asBinary2 = if (lLSDNode.keyExists("Normal")) lLSDNode.byKey("Normal").asBinary() else null
        val asBinary3 = if (lLSDNode.keyExists("TexCoord0")) lLSDNode.byKey("TexCoord0").asBinary() else null
        this.numVertices = asBinary.size / 6
        this.vertexBuffer = DirectByteBuffer(this.numVertices * 6 * 4)
        val lLVector3 = LLVector3(-0.5f, -0.5f, -0.5f)
        val lLVector32 = LLVector3(0.5f, 0.5f, 0.5f)
        if (lLSDNode.keyExists("PositionDomain")) {
            if (lLSDNode.byKey("PositionDomain").keyExists("Min")) {
                val byKey = lLSDNode.byKey("PositionDomain").byKey("Min")
                lLVector3.set(byKey.byIndex(0).asDouble().toFloat(), byKey.byIndex(1).asDouble().toFloat(), byKey.byIndex(2).asDouble().toFloat())
            }
            if (lLSDNode.byKey("PositionDomain").keyExists("Max")) {
                val byKey2 = lLSDNode.byKey("PositionDomain").byKey("Max")
                lLVector32.set(byKey2.byIndex(0).asDouble().toFloat(), byKey2.byIndex(1).asDouble().toFloat(), byKey2.byIndex(2).asDouble().toFloat())
            }
        }
        var lLVector2: LLVector2? = null
        var lLVector22: LLVector2? = null
        if (asBinary3 != null) {
            lLVector2 = LLVector2(0.0f, 0.0f)
            lLVector22 = LLVector2(0.0f, 0.0f)
            if (lLSDNode.keyExists("TexCoord0Domain")) {
                if (lLSDNode.byKey("TexCoord0Domain").keyExists("Min")) {
                    val byKey3 = lLSDNode.byKey("TexCoord0Domain").byKey("Min")
                    lLVector2.set(byKey3.byIndex(0).asDouble().toFloat(), byKey3.byIndex(1).asDouble().toFloat())
                }
                if (lLSDNode.byKey("TexCoord0Domain").keyExists("Max")) {
                    val byKey4 = lLSDNode.byKey("TexCoord0Domain").byKey("Max")
                    lLVector22.set(byKey4.byIndex(0).asDouble().toFloat(), byKey4.byIndex(1).asDouble().toFloat())
                }
            }
        }
        val asShortBuffer = ByteBuffer.wrap(asBinary).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()
        val asShortBuffer2 = if (asBinary2 != null) ByteBuffer.wrap(asBinary2).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer() else null
        val asShortBuffer3 = if (asBinary3 != null) ByteBuffer.wrap(asBinary3).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer() else null
        val vertBuf = this.vertexBuffer!!
        vertBuf.position(0)
        for (i in 0 until this.numVertices) {
            val f = (((asShortBuffer.get().toInt() and 0xFFFF).toFloat() * (lLVector32.x - lLVector3.x)) / 65535.0f) + lLVector3.x
            val f2 = (((asShortBuffer.get().toInt() and 0xFFFF).toFloat() * (lLVector32.y - lLVector3.y)) / 65535.0f) + lLVector3.y
            val f3 = (((asShortBuffer.get().toInt() and 0xFFFF).toFloat() * (lLVector32.z - lLVector3.z)) / 65535.0f) + lLVector3.z
            vertBuf.putFloat(f)
            vertBuf.putFloat(f2)
            vertBuf.putFloat(f3)
            if (asShortBuffer2 != null) {
                vertBuf.putFloat((((asShortBuffer2.get().toInt() and 0xFFFF).toFloat() * 2.0f) / 65535.0f) - 1.0f)
                vertBuf.putFloat((((asShortBuffer2.get().toInt() and 0xFFFF).toFloat() * 2.0f) / 65535.0f) - 1.0f)
                vertBuf.putFloat((((asShortBuffer2.get().toInt() and 0xFFFF).toFloat() * 2.0f) / 65535.0f) - 1.0f)
            } else {
                vertBuf.putFloat(0.0f)
                vertBuf.putFloat(0.0f)
                vertBuf.putFloat(0.0f)
            }
        }
        if (asShortBuffer3 != null && lLVector2 != null && lLVector22 != null) {
            this.texCoordsBuffer = DirectByteBuffer(this.numVertices * 2 * 4)
            val texBuf = this.texCoordsBuffer!!
            texBuf.position(0)
            for (i2 in 0 until this.numVertices) {
                val f4 = (((asShortBuffer3.get().toInt() and 0xFFFF).toFloat() * (lLVector22.x - lLVector2.x)) / 65535.0f) + lLVector2.x
                val f5 = (((asShortBuffer3.get().toInt() and 0xFFFF).toFloat() * (lLVector22.y - lLVector2.y)) / 65535.0f) + lLVector2.y
                texBuf.putFloat(f4)
                texBuf.putFloat(f5)
            }
        } else {
            this.texCoordsBuffer = null
        }
        val asBinary4 = lLSDNode.byKey("TriangleList").asBinary()
        this.numIndices = asBinary4.size / 2
        this.indexBuffer = DirectByteBuffer(this.numIndices * 2)
        this.indexBuffer?.loadFromByteArray(0, asBinary4, 0, this.numIndices * 2)
        if (lLSDNode.keyExists("Weights")) {
            val asBinary5 = lLSDNode.byKey("Weights").asBinary()
            this.weightBuffer = DirectByteBuffer(asBinary5.size)
            this.weightBuffer?.loadFromByteArray(0, asBinary5, 0, asBinary5.size)
        } else {
            this.weightBuffer = null
        }
    }

    fun PrepareInfluenceBuffer(meshWeightsBuffer: MeshWeightsBuffer, i: Int) {
        val weightBuf = this.weightBuffer ?: return
        OpenJPEG.meshPrepareSeparateInfluenceBuffer(
            weightBuf.asByteBuffer(), 
            this.numVertices, 
            meshWeightsBuffer.jointIndexBuffer.asByteBuffer(), 
            meshWeightsBuffer.weightsBuffer.asByteBuffer(), 
            i
        )
    }

    fun PrepareInfluenceBuffer(directByteBuffer: DirectByteBuffer, i: Int) {
        val weightBuf = this.weightBuffer ?: return
        OpenJPEG.meshPrepareInfluenceBuffer(weightBuf.asByteBuffer(), this.numVertices, directByteBuffer.asByteBuffer(), i)
    }

    fun UpdateRigged(directByteBuffer: DirectByteBuffer, i: Int, fArr: FloatArray, fArr2: FloatArray) {
        val weightBuf = this.weightBuffer
        val vertBuf = this.vertexBuffer
        if (weightBuf != null && vertBuf != null) {
            OpenJPEG.applyRiggedMeshMorph(
                directByteBuffer.asByteBuffer(), 
                i, 
                fArr, 
                fArr2, 
                vertBuf.asByteBuffer(), 
                weightBuf.asByteBuffer(), 
                this.numVertices
            )
        }
    }

    fun getIndices(): DirectByteBuffer? {
        return this.indexBuffer
    }

    fun getNumIndices(): Int {
        return this.numIndices
    }

    fun getNumVertices(): Int {
        return this.numVertices
    }

    fun getTexCoords(): DirectByteBuffer? {
        return this.texCoordsBuffer
    }

    fun getVertices(): DirectByteBuffer? {
        return this.vertexBuffer
    }
}
