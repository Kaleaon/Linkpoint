package com.lumiyaviewer.lumiya.slproto.mesh

import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import com.lumiyaviewer.lumiya.slproto.types.LLVector2
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import com.lumiyaviewer.rawbuffers.DirectByteBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.ShortBuffer

class MeshFace(llsdNode: LLSDNode) {
    private var indexBuffer: DirectByteBuffer? = null
    private var numIndices: Int = 0
    private var numVertices: Int = 0
    private var texCoordsBuffer: DirectByteBuffer? = null
    private var vertexBuffer: DirectByteBuffer? = null
    private var weightBuffer: DirectByteBuffer? = null

    init {
        if (!llsdNode.keyExists("NoGeometry") && llsdNode.keyExists("Position") && llsdNode.keyExists("TriangleList")) {
            val positionBytes = llsdNode.byKey("Position").asBinary()
            val normalBytes = if (llsdNode.keyExists("Normal")) llsdNode.byKey("Normal").asBinary() else null
            val texCoord0Bytes = if (llsdNode.keyExists("TexCoord0")) llsdNode.byKey("TexCoord0").asBinary() else null
            
            this.numVertices = positionBytes.size / 6
            this.vertexBuffer = DirectByteBuffer(this.numVertices * 6 * 4)
            
            val posMin = LLVector3(-0.5f, -0.5f, -0.5f)
            val posMax = LLVector3(0.5f, 0.5f, 0.5f)
            
            if (llsdNode.keyExists("PositionDomain")) {
                val posDomain = llsdNode.byKey("PositionDomain")
                if (posDomain.keyExists("Min")) {
                    val minNode = posDomain.byKey("Min")
                    posMin.set(minNode.byIndex(0).asDouble().toFloat(), minNode.byIndex(1).asDouble().toFloat(), minNode.byIndex(2).asDouble().toFloat())
                }
                if (posDomain.keyExists("Max")) {
                    val maxNode = posDomain.byKey("Max")
                    posMax.set(maxNode.byIndex(0).asDouble().toFloat(), maxNode.byIndex(1).asDouble().toFloat(), maxNode.byIndex(2).asDouble().toFloat())
                }
            }
            
            var texMin: LLVector2? = null
            var texMax: LLVector2? = null
            
            if (texCoord0Bytes != null) {
                texMin = LLVector2(0.0f, 0.0f)
                texMax = LLVector2(0.0f, 0.0f)
                
                if (llsdNode.keyExists("TexCoord0Domain")) {
                    val texDomain = llsdNode.byKey("TexCoord0Domain")
                    if (texDomain.keyExists("Min")) {
                        val minNode = texDomain.byKey("Min")
                        texMin.set(minNode.byIndex(0).asDouble().toFloat(), minNode.byIndex(1).asDouble().toFloat())
                    }
                    if (texDomain.keyExists("Max")) {
                        val maxNode = texDomain.byKey("Max")
                        texMax.set(maxNode.byIndex(0).asDouble().toFloat(), maxNode.byIndex(1).asDouble().toFloat())
                    }
                }
            }

            val posBuffer = ByteBuffer.wrap(positionBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()
            val normBuffer = if (normalBytes != null) ByteBuffer.wrap(normalBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer() else null
            val texBuffer = if (texCoord0Bytes != null) ByteBuffer.wrap(texCoord0Bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer() else null
            
            this.vertexBuffer!!.position(0)
            for (i in 0 until this.numVertices) {
                val x = (((posBuffer.get().toInt() and 0xFFFF).toFloat() * (posMax.x - posMin.x)) / 65535.0f) + posMin.x
                val y = (((posBuffer.get().toInt() and 0xFFFF).toFloat() * (posMax.y - posMin.y)) / 65535.0f) + posMin.y
                val z = (((posBuffer.get().toInt() and 0xFFFF).toFloat() * (posMax.z - posMin.z)) / 65535.0f) + posMin.z
                
                this.vertexBuffer!!.putFloat(x)
                this.vertexBuffer!!.putFloat(y)
                this.vertexBuffer!!.putFloat(z)
                
                if (normBuffer != null) {
                    this.vertexBuffer!!.putFloat((((normBuffer.get().toInt() and 0xFFFF).toFloat() * 2.0f) / 65535.0f) - 1.0f)
                    this.vertexBuffer!!.putFloat((((normBuffer.get().toInt() and 0xFFFF).toFloat() * 2.0f) / 65535.0f) - 1.0f)
                    this.vertexBuffer!!.putFloat((((normBuffer.get().toInt() and 0xFFFF).toFloat() * 2.0f) / 65535.0f) - 1.0f)
                } else {
                    this.vertexBuffer!!.putFloat(0.0f)
                    this.vertexBuffer!!.putFloat(0.0f)
                    this.vertexBuffer!!.putFloat(0.0f)
                }
            }
            
            if (texBuffer != null && texMin != null && texMax != null) {
                this.texCoordsBuffer = DirectByteBuffer(this.numVertices * 2 * 4)
                this.texCoordsBuffer!!.position(0)
                for (i in 0 until this.numVertices) {
                    val u = (((texBuffer.get().toInt() and 0xFFFF).toFloat() * (texMax.x - texMin.x)) / 65535.0f) + texMin.x
                    val v = (((texBuffer.get().toInt() and 0xFFFF).toFloat() * (texMax.y - texMin.y)) / 65535.0f) + texMin.y
                    this.texCoordsBuffer!!.putFloat(u)
                    this.texCoordsBuffer!!.putFloat(v)
                }
            } else {
                this.texCoordsBuffer = null
            }
            
            val triangleBytes = llsdNode.byKey("TriangleList").asBinary()
            this.numIndices = triangleBytes.size / 2
            this.indexBuffer = DirectByteBuffer(this.numIndices * 2)
            this.indexBuffer!!.loadFromByteArray(0, triangleBytes, 0, this.numIndices * 2)
            
            if (llsdNode.keyExists("Weights")) {
                val weightBytes = llsdNode.byKey("Weights").asBinary()
                this.weightBuffer = DirectByteBuffer(weightBytes.size)
                this.weightBuffer!!.loadFromByteArray(0, weightBytes, 0, weightBytes.size)
            } else {
                this.weightBuffer = null
            }
        } else {
            this.vertexBuffer = null
            this.indexBuffer = null
            this.weightBuffer = null
            this.texCoordsBuffer = null
            this.numIndices = 0
            this.numVertices = 0
        }
    }

    internal fun PrepareInfluenceBuffer(meshWeightsBuffer: MeshWeightsBuffer, offset: Int) {
        if (weightBuffer != null) {
             OpenJPEG.meshPrepareSeparateInfluenceBuffer(
                this.weightBuffer!!.asByteBuffer(), 
                this.numVertices, 
                meshWeightsBuffer.jointIndexBuffer.asByteBuffer(), 
                meshWeightsBuffer.weightsBuffer.asByteBuffer(), 
                offset
            )
        }
    }

    internal fun PrepareInfluenceBuffer(buffer: DirectByteBuffer, offset: Int) {
        if (this.weightBuffer != null) {
            OpenJPEG.meshPrepareInfluenceBuffer(
                this.weightBuffer!!.asByteBuffer(),
                this.numVertices,
                buffer.asByteBuffer(),
                offset
            )
        }
    }

    internal fun UpdateRigged(buffer: DirectByteBuffer, offset: Int, matrix1: FloatArray, matrix2: FloatArray) {
        if (this.weightBuffer != null && this.vertexBuffer != null) {
            OpenJPEG.applyRiggedMeshMorph(
                buffer.asByteBuffer(),
                offset,
                matrix1,
                matrix2,
                this.vertexBuffer!!.asByteBuffer(),
                this.weightBuffer!!.asByteBuffer(),
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
