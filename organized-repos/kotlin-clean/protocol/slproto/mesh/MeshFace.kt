package com.linkpoint.slproto.mesh

import com.linkpoint.openjpeg.OpenJPEG
import com.linkpoint.slproto.llsd.LLSDException
import com.linkpoint.slproto.llsd.LLSDNode
import com.linkpoint.slproto.types.LLVector2
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.rawbuffers.DirectByteBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.ShortBuffer

/**
 * MeshFace - Single face of a rigged mesh
 * Handles vertex positions, normals, texture coordinates, and skinning weights
 * Based on Firestorm's mesh face handling
 */
class MeshFace {
    
    private val indexBuffer: DirectByteBuffer?
    private val numIndices: Int
    private val numVertices: Int
    private val texCoordsBuffer: DirectByteBuffer?
    private val vertexBuffer: DirectByteBuffer?
    private val weightBuffer: DirectByteBuffer?

    @Throws(LLSDException::class)
    constructor(faceNode: LLSDNode) {
        // Check for empty geometry
        if (faceNode.keyExists("NoGeometry") ||
            !faceNode.keyExists("Position") ||
            !faceNode.keyExists("TriangleList")) {
            this.vertexBuffer = null
            this.indexBuffer = null
            this.weightBuffer = null
            this.texCoordsBuffer = null
            this.numIndices = 0
            this.numVertices = 0
            return
        }
        
        // Read position data
        val positionData = faceNode.byKey("Position").asBinary()
        
        // Read normal data (optional)
        val normalData = if (faceNode.keyExists("Normal")) {
            faceNode.byKey("Normal").asBinary()
        } else null
        
        // Read texture coordinate data (optional)
        val texCoordData = if (faceNode.keyExists("TexCoord0")) {
            faceNode.byKey("TexCoord0").asBinary()
        } else null
        
        // Calculate number of vertices
        this.numVertices = positionData.size / 6  // 3 shorts per vertex (x, y, z)
        
        // Initialize vertex buffer (position + normal = 6 floats)
        this.vertexBuffer = DirectByteBuffer(this.numVertices * 6 * 4)
        
        // Parse position domain (bounding box for decompression)
        var posMin = LLVector3(-0.5f, -0.5f, -0.5f)
        var posMax = LLVector3(0.5f, 0.5f, 0.5f)
        
        if (faceNode.keyExists("PositionDomain")) {
            val posDomain = faceNode.byKey("PositionDomain")
            if (posDomain.keyExists("Min")) {
                val minNode = posDomain.byKey("Min")
                posMin.set(
                    minNode.byIndex(0).asDouble().toFloat(),
                    minNode.byIndex(1).asDouble().toFloat(),
                    minNode.byIndex(2).asDouble().toFloat()
                )
            }
            if (posDomain.keyExists("Max")) {
                val maxNode = posDomain.byKey("Max")
                posMax.set(
                    maxNode.byIndex(0).asDouble().toFloat(),
                    maxNode.byIndex(1).asDouble().toFloat(),
                    maxNode.byIndex(2).asDouble().toFloat()
                )
            }
        }
        
        // Parse texture coordinate domain (for decompression)
        var texMin: LLVector2? = null
        var texMax: LLVector2? = null
        
        if (texCoordData != null) {
            texMin = LLVector2(0.0f, 0.0f)
            texMax = LLVector2(1.0f, 1.0f)
            
            if (faceNode.keyExists("TexCoord0Domain")) {
                val texDomain = faceNode.byKey("TexCoord0Domain")
                if (texDomain.keyExists("Min")) {
                    val minNode = texDomain.byKey("Min")
                    texMin.set(
                        minNode.byIndex(0).asDouble().toFloat(),
                        minNode.byIndex(1).asDouble().toFloat()
                    )
                }
                if (texDomain.keyExists("Max")) {
                    val maxNode = texDomain.byKey("Max")
                    texMax.set(
                        maxNode.byIndex(0).asDouble().toFloat(),
                        maxNode.byIndex(1).asDouble().toFloat()
                    )
                }
            }
        }
        
        // Decompress position data
        val positionShortBuffer = ByteBuffer.wrap(positionData)
            .order(ByteOrder.LITTLE_ENDIAN)
            .asShortBuffer()
        
        // Decompress normal data
        val normalShortBuffer = if (normalData != null) {
            ByteBuffer.wrap(normalData)
                .order(ByteOrder.LITTLE_ENDIAN)
                .asShortBuffer()
        } else null
        
        // Decompress texture coordinates
        val texCoordShortBuffer = if (texCoordData != null) {
            ByteBuffer.wrap(texCoordData)
                .order(ByteOrder.LITTLE_ENDIAN)
                .asShortBuffer()
        } else null
        
        // Decompress and store vertex data
        this.vertexBuffer.position(0)
        
        for (i in 0 until this.numVertices) {
            // Decompress position (from u16 to float)
            val x = ((positionShortBuffer.get() and 0xFFFF).toFloat() * (posMax.x - posMin.x)) / 65535.0f + posMin.x
            val y = ((positionShortBuffer.get() and 0xFFFF).toFloat() * (posMax.y - posMin.y)) / 65535.0f + posMin.y
            val z = ((positionShortBuffer.get() and 0xFFFF).toFloat() * (posMax.z - posMin.z)) / 65535.0f + posMin.z
            
            this.vertexBuffer.putFloat(x)
            this.vertexBuffer.putFloat(y)
            this.vertexBuffer.putFloat(z)
            
            // Decompress normal (from u16 to float in range [-1, 1])
            if (normalShortBuffer != null) {
                val nx = ((normalShortBuffer.get() and 0xFFFF).toFloat() * 2.0f) / 65535.0f - 1.0f
                val ny = ((normalShortBuffer.get() and 0xFFFF).toFloat() * 2.0f) / 65535.0f - 1.0f
                val nz = ((normalShortBuffer.get() and 0xFFFF).toFloat() * 2.0f) / 65535.0f - 1.0f
                
                this.vertexBuffer.putFloat(nx)
                this.vertexBuffer.putFloat(ny)
                this.vertexBuffer.putFloat(nz)
            } else {
                // Default normal pointing up
                this.vertexBuffer.putFloat(0.0f)
                this.vertexBuffer.putFloat(0.0f)
                this.vertexBuffer.putFloat(1.0f)
            }
        }
        
        // Decompress texture coordinates
        if (texCoordShortBuffer != null && texMin != null && texMax != null) {
            this.texCoordsBuffer = DirectByteBuffer(this.numVertices * 2 * 4)
            this.texCoordsBuffer.position(0)
            
            for (i2 in 0 until this.numVertices) {
                val u = ((texCoordShortBuffer.get() and 0xFFFF).toFloat() * (texMax.x - texMin.x)) / 65535.0f + texMin.x
                val v = ((texCoordShortBuffer.get() and 0xFFFF).toFloat() * (texMax.y - texMin.y)) / 65535.0f + texMin.y
                
                this.texCoordsBuffer.putFloat(u)
                this.texCoordsBuffer.putFloat(v)
            }
        } else {
            this.texCoordsBuffer = null
        }
        
        // Read triangle indices
        val triangleData = faceNode.byKey("TriangleList").asBinary()
        this.numIndices = triangleData.size / 2  // u16 indices
        this.indexBuffer = DirectByteBuffer(this.numIndices * 2)
        this.indexBuffer.loadFromByteArray(0, triangleData, 0, this.numIndices * 2)
        
        // Read skinning weights (for rigged meshes)
        if (faceNode.keyExists("Weights")) {
            val weightsData = faceNode.byKey("Weights").asBinary()
            this.weightBuffer = DirectByteBuffer(weightsData.size)
            this.weightBuffer.loadFromByteArray(0, weightsData, 0, weightsData.size)
        } else {
            this.weightBuffer = null
        }
    }

    /**
     * Prepare influence (skinning) buffer for separate joint indices and weights
     */
    internal fun PrepareInfluenceBuffer(meshWeightsBuffer: MeshWeightsBuffer, vertexOffset: Int) {
        if (weightBuffer != null) {
            OpenJPEG.meshPrepareSeparateInfluenceBuffer(
                weightBuffer.asByteBuffer(),
                numVertices,
                meshWeightsBuffer.jointIndexBuffer.asByteBuffer(),
                meshWeightsBuffer.weightsBuffer.asByteBuffer(),
                vertexOffset
            )
        }
    }

    /**
     * Prepare influence buffer (combined version)
     */
    internal fun PrepareInfluenceBuffer(targetBuffer: DirectByteBuffer, vertexOffset: Int) {
        if (weightBuffer != null) {
            OpenJPEG.meshPrepareInfluenceBuffer(
                weightBuffer.asByteBuffer(),
                numVertices,
                targetBuffer.asByteBuffer(),
                vertexOffset
            )
        }
    }

    /**
     * Apply rigged mesh morphing
     */
    internal fun UpdateRigged(
        targetBuffer: DirectByteBuffer,
        bufferOffset: Int,
        bindShapeMatrix: FloatArray?,
        boneMatrices: FloatArray?
    ) {
        if (weightBuffer != null && vertexBuffer != null && bindShapeMatrix != null && boneMatrices != null) {
            OpenJPEG.applyRiggedMeshMorph(
                targetBuffer.asByteBuffer(),
                bufferOffset,
                bindShapeMatrix,
                boneMatrices,
                vertexBuffer.asByteBuffer(),
                weightBuffer.asByteBuffer(),
                numVertices
            )
        }
    }

    val indices: DirectByteBuffer?
        get() = indexBuffer

    val numIndices: Int
        get() = this.numIndices

    val numVertices: Int
        get() = this.numVertices

    val texCoords: DirectByteBuffer?
        get() = texCoordsBuffer

    val vertices: DirectByteBuffer?
        get() = vertexBuffer
}
