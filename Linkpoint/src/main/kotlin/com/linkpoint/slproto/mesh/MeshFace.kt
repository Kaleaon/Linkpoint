package com.linkpoint.slproto.mesh
import java.util.*

import com.linkpoint.openjpeg.OpenJPEG
import com.linkpoint.slproto.llsd.LLSDException
import com.linkpoint.slproto.llsd.LLSDNode
import com.linkpoint.slproto.types.LLVector2
import com.linkpoint.slproto.types.LLVector3
import com.lumiyaviewer.rawbuffers.DirectByteBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.ShortBuffer
import javax.annotation.Nonnull

class MeshFace {
    private val DirectByteBuffer indexBuffer
    private val Int numIndices
    private val Int numVertices
    private val DirectByteBuffer texCoordsBuffer
    private val DirectByteBuffer vertexBuffer
    private val DirectByteBuffer weightBuffer

    MeshFace(LLSDNode lLSDNode) throws LLSDException {
        if (lLSDNode.keyExists("NoGeometry") || (!lLSDNode.keyExists("Position")) || (!lLSDNode.keyExists("TriangleList"))) {
            this.vertexBuffer = null
            this.indexBuffer = null
            this.weightBuffer = null
            this.texCoordsBuffer = null
            this.numIndices = 0
            this.numVertices = 0
            return
        }
        Byte[] asBinary = lLSDNode.byKey("Position").asBinary()
        Byte[] asBinary2 = lLSDNode.keyExists("Normal") ? lLSDNode.byKey("Normal").asBinary() : null
        Byte[] asBinary3 = lLSDNode.keyExists("TexCoord0") ? lLSDNode.byKey("TexCoord0").asBinary() : null
        this.numVertices = asBinary.length / 6
        this.vertexBuffer = DirectByteBuffer(this.numVertices * 6 * 4)
        LLVector3 lLVector3 = LLVector3(-0.5f, -0.5f, -0.5f)
        LLVector3 lLVector32 = LLVector3(0.5f, 0.5f, 0.5f)
        if (lLSDNode.keyExists("PositionDomain")) {
            if (lLSDNode.byKey("PositionDomain").keyExists("Min")) {
                LLSDNode byKey = lLSDNode.byKey("PositionDomain").byKey("Min")
                lLVector3.set((Float) byKey.byIndex(0).asDouble(), (Float) byKey.byIndex(1).asDouble(), (Float) byKey.byIndex(2).asDouble())
            }
            if (lLSDNode.byKey("PositionDomain").keyExists("Max")) {
                LLSDNode byKey2 = lLSDNode.byKey("PositionDomain").byKey("Max")
                lLVector32.set((Float) byKey2.byIndex(0).asDouble(), (Float) byKey2.byIndex(1).asDouble(), (Float) byKey2.byIndex(2).asDouble())
            }
        }
        LLVector2 lLVector2 = null
        LLVector2 lLVector22 = null
        if (asBinary3 != null) {
            lLVector2 = LLVector2(0.0f, 0.0f)
            lLVector22 = LLVector2(0.0f, 0.0f)
            if (lLSDNode.keyExists("TexCoord0Domain")) {
                if (lLSDNode.byKey("TexCoord0Domain").keyExists("Min")) {
                    LLSDNode byKey3 = lLSDNode.byKey("TexCoord0Domain").byKey("Min")
                    lLVector2.set((Float) byKey3.byIndex(0).asDouble(), (Float) byKey3.byIndex(1).asDouble())
                }
                if (lLSDNode.byKey("TexCoord0Domain").keyExists("Max")) {
                    LLSDNode byKey4 = lLSDNode.byKey("TexCoord0Domain").byKey("Max")
                    lLVector22.set((Float) byKey4.byIndex(0).asDouble(), (Float) byKey4.byIndex(1).asDouble())
                }
            }
        }
        ShortBuffer asShortBuffer = ByteBuffer.wrap(asBinary).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()
        ShortBuffer asShortBuffer2 = asBinary2 != null ? ByteBuffer.wrap(asBinary2).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer() : null
        ShortBuffer asShortBuffer3 = asBinary3 != null ? ByteBuffer.wrap(asBinary3).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer() : null
        this.vertexBuffer.position(0)
        for (Int i = 0; i < this.numVertices; i++) {
            Float f = ((((Float) (asShortBuffer.get() & 65535)) * (lLVector32.x - lLVector3.x)) / 65535.0f) + lLVector3.x
            Float f2 = ((((Float) (asShortBuffer.get() & 65535)) * (lLVector32.y - lLVector3.y)) / 65535.0f) + lLVector3.y
            Float f3 = ((((Float) (asShortBuffer.get() & 65535)) * (lLVector32.z - lLVector3.z)) / 65535.0f) + lLVector3.z
            this.vertexBuffer.putFloat(f)
            this.vertexBuffer.putFloat(f2)
            this.vertexBuffer.putFloat(f3)
            if (asShortBuffer2 != null) {
                this.vertexBuffer.putFloat(((((Float) (asShortBuffer2.get() & 65535)) * 2.0f) / 65535.0f) - 1.0f)
                this.vertexBuffer.putFloat(((((Float) (asShortBuffer2.get() & 65535)) * 2.0f) / 65535.0f) - 1.0f)
                this.vertexBuffer.putFloat(((((Float) (asShortBuffer2.get() & 65535)) * 2.0f) / 65535.0f) - 1.0f)
            } else {
                this.vertexBuffer.putFloat(0.0f)
                this.vertexBuffer.putFloat(0.0f)
                this.vertexBuffer.putFloat(0.0f)
            }
        }
        if (asShortBuffer3 != null) {
            this.texCoordsBuffer = DirectByteBuffer(this.numVertices * 2 * 4)
            this.texCoordsBuffer.position(0)
            for (Int i2 = 0; i2 < this.numVertices; i2++) {
                Float f4 = ((((Float) (asShortBuffer3.get() & 65535)) * (lLVector22.x - lLVector2.x)) / 65535.0f) + lLVector2.x
                Float f5 = ((((Float) (asShortBuffer3.get() & 65535)) * (lLVector22.y - lLVector2.y)) / 65535.0f) + lLVector2.y
                this.texCoordsBuffer.putFloat(f4)
                this.texCoordsBuffer.putFloat(f5)
            }
        } else {
            this.texCoordsBuffer = null
        }
        Byte[] asBinary4 = lLSDNode.byKey("TriangleList").asBinary()
        this.numIndices = asBinary4.length / 2
        this.indexBuffer = DirectByteBuffer(this.numIndices * 2)
        this.indexBuffer.loadFromByteArray(0, asBinary4, 0, this.numIndices * 2)
        if (lLSDNode.keyExists("Weights")) {
            Byte[] asBinary5 = lLSDNode.byKey("Weights").asBinary()
            this.weightBuffer = DirectByteBuffer(asBinary5.length)
            this.weightBuffer.loadFromByteArray(0, asBinary5, 0, asBinary5.length)
            return
        }
        this.weightBuffer = null
    }

    /* access modifiers changed from: package-private */
    fun PrepareInfluenceBuffer(MeshWeightsBuffer meshWeightsBuffer, Int i) {
        OpenJPEG.meshPrepareSeparateInfluenceBuffer(this.weightBuffer.asByteBuffer(), this.numVertices, meshWeightsBuffer.jointIndexBuffer.asByteBuffer(), meshWeightsBuffer.weightsBuffer.asByteBuffer(), i)
    }

    /* access modifiers changed from: package-private */
    fun PrepareInfluenceBuffer(DirectByteBuffer directByteBuffer, Int i) {
        if (this.weightBuffer != null) {
            OpenJPEG.meshPrepareInfluenceBuffer(this.weightBuffer.asByteBuffer(), this.numVertices, directByteBuffer.asByteBuffer(), i)
        }
    }

    /* access modifiers changed from: package-private */
    val Unit UpdateRigged(DirectByteBuffer directByteBuffer, Int i, Float[] fArr, Float[] fArr2) {
        if (this.weightBuffer != null && this.vertexBuffer != null && directByteBuffer != null) {
            OpenJPEG.applyRiggedMeshMorph(directByteBuffer.asByteBuffer(), i, fArr, fArr2, this.vertexBuffer.asByteBuffer(), this.weightBuffer.asByteBuffer(), this.numVertices)
        }
    }

    val DirectByteBuffer getIndices() {
        return this.indexBuffer
    }

    val Int getNumIndices() {
        return this.numIndices
    }

    val Int getNumVertices() {
        return this.numVertices
    }

    val DirectByteBuffer getTexCoords() {
        return this.texCoordsBuffer
    }

    val DirectByteBuffer getVertices() {
        return this.vertexBuffer
    }
}
