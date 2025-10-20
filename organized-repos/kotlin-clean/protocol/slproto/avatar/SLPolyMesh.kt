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

/**
 * SLPolyMesh - Avatar mesh with morphing and skeleton support
 * Based on Firestorm's LLPolyMesh implementation
 */
class SLPolyMesh : SLMeshData {
    
    protected var hasWeights: Boolean = false
    var jointMap: IntArray? = null
    private val morphIndices: MutableMap<SLVisualParamID, Int> = EnumMap(SLVisualParamID::class.java)
    private lateinit var morphs: Array<SLPolyMorphData>
    protected var weightsBuffer: DirectByteBuffer? = null

    @Throws(IOException::class)
    constructor(dataInputStream: DataInputStream, dataInputStream2: DataInputStream?) {
        // Load position, scale, rotation (matching LLPolyMeshSharedData)
        this.position = LLVector3(
            dataInputStream.readFloat(),
            dataInputStream.readFloat(),
            dataInputStream.readFloat()
        )
        this.scale = LLVector3(
            dataInputStream.readFloat(),
            dataInputStream.readFloat(),
            dataInputStream.readFloat()
        )
        this.rotation = LLQuaternion(
            dataInputStream.readFloat(),
            dataInputStream.readFloat(),
            dataInputStream.readFloat(),
            dataInputStream.readFloat()
        )
        
        // Read weights flag
        this.hasWeights = dataInputStream.readByte() != 0.toByte()
        
        // Read vertex data
        this.numVertices = dataInputStream.readInt()
        this.vertexBuffer = DirectByteBuffer(this.numVertices * 24) // 6 floats per vertex (pos + normal)
        this.texCoordsBuffer = DirectByteBuffer(this.numVertices * 8) // 2 floats per vertex
        this.vertexBuffer!!.read(dataInputStream)
        this.texCoordsBuffer!!.read(dataInputStream)
        
        // Read weights if present (matching LLPolyMeshSharedData::mWeights)
        if (this.hasWeights) {
            this.weightsBuffer = DirectByteBuffer(this.numVertices * 4)
            this.weightsBuffer!!.read(dataInputStream)
        }
        
        // Read face data
        this.numFaces = dataInputStream.readInt()
        this.indexBuffer = DirectByteBuffer(this.numFaces * 2 * 3) // 3 indices per face
        this.indexBuffer!!.read(dataInputStream)
        
        // Read morph targets (matching LLPolyMeshSharedData::mMorphData)
        val numMorphs = dataInputStream.readInt()
        this.morphs = Array(numMorphs) { SLPolyMorphData.EMPTY }
        
        var i = 0
        var consecutiveReads = 0
        var currentStream = dataInputStream
        
        while (i < numMorphs) {
            // Switch streams to avoid blocking on single stream
            if (consecutiveReads >= 50 && dataInputStream2 != null) {
                consecutiveReads = 0
                currentStream = dataInputStream2
            }
            
            val paramID = SLVisualParamID.values()[currentStream.readInt()]
            this.morphs[i] = SLPolyMorphData(paramID, this, currentStream)
            this.morphIndices[paramID] = i
            
            i++
            consecutiveReads++
        }
        
        // Read joint map (for rigging - matching LLPolyMeshSharedData::mJointNames)
        val numJoints = currentStream.readInt()
        this.jointMap = IntArray(numJoints)
        for (i3 in 0 until numJoints) {
            this.jointMap!![i3] = currentStream.readInt()
        }
        
        Debug.Log("SLPolyMesh: Loaded, numVerts = $numVertices, faces = $numFaces, morphs = ${morphs.size}")
    }

    /**
     * Apply morph data to mesh (visual parameters like body shape, face shape)
     * Matches LLPolyMorphTarget::apply()
     */
    fun applyMorphData(targetMesh: SLMeshData, morphWeights: FloatArray, texture: GLTexture?) {
        for (i in morphWeights.indices) {
            if (i < morphs.size) {
                morphs[i].applyMorphData(targetMesh, morphWeights[i], texture)
            }
        }
    }

    /**
     * Apply skeleton transforms to rigged mesh
     * Matches LLPolySkeletalDistortion behavior
     */
    fun applySkeleton(animatedMesh: SLAnimatedMeshData, jointTransforms: FloatArray) {
        if (!hasWeights || jointMap == null) {
            return
        }
        
        val animatedVertexData = animatedMesh.getAnimatedVertexData() ?: return
        
        // Use native OpenJPEG function for performance
        OpenJPEG.applyMorphingTransform(
            numVertices,
            animatedMesh.vertexBuffer!!.asByteBuffer(),
            animatedVertexData.asByteBuffer(),
            weightsBuffer!!.asByteBuffer(),
            jointMap!!,
            jointTransforms
        )
    }

    /**
     * Slower Kotlin version of skeleton application (for debugging)
     * Matches LLPolySkeletalDistortion::apply() logic
     */
    fun applySkeletonSlow(animatedMesh: SLAnimatedMeshData, jointTransforms: FloatArray) {
        if (!hasWeights || jointMap == null) {
            return
        }
        
        val animatedVertexData = animatedMesh.getAnimatedVertexData() ?: return
        
        val weightsFloatBuffer = weightsBuffer!!.asFloatBuffer()
        val sourceVertexBuffer = animatedMesh.vertexBuffer!!.asFloatBuffer()
        val targetVertexBuffer = animatedVertexData.asFloatBuffer()
        
        val tempMatrix = FloatArray(16)
        val resultMatrix = FloatArray(16)
        val vertexPos = FloatArray(4)
        val transformedPos = FloatArray(4)
        
        var lastWeight = -1.0
        
        for (i in 0 until numVertices) {
            val weight = weightsFloatBuffer.get(i)
            
            if (weight.toDouble() != lastWeight) {
                // Weight encodes joint indices
                val floor = Math.floor(weight.toDouble()).toFloat()
                val frac = weight - floor
                val jointIndex1 = (floor.toInt()) - 1
                val jointIndex2 = jointIndex1 + 1
                
                // Get actual joint indices from map
                var joint1 = 0
                if (jointIndex1 in jointMap!!.indices) {
                    joint1 = jointMap!![jointIndex1]
                }
                
                var joint2 = joint1
                if (jointIndex2 in jointMap!!.indices) {
                    joint2 = jointMap!![jointIndex2]
                }
                
                val matrix1Offset = joint1 * 16
                val matrix2Offset = joint2 * 16
                
                // Interpolate between joint transforms
                if (matrix1Offset == matrix2Offset) {
                    System.arraycopy(jointTransforms, matrix1Offset, resultMatrix, 0, 16)
                } else {
                    for (i7 in 0 until 16) {
                        resultMatrix[i7] = jointTransforms[matrix1Offset + i7] * (1.0f - frac) +
                                          jointTransforms[matrix2Offset + i7] * frac
                    }
                }
                
                lastWeight = weight.toDouble()
            }
            
            // Transform vertex position
            vertexPos[0] = sourceVertexBuffer.get(i * 6 + 0)
            vertexPos[1] = sourceVertexBuffer.get(i * 6 + 1)
            vertexPos[2] = sourceVertexBuffer.get(i * 6 + 2)
            vertexPos[3] = 1.0f
            
            Matrix.multiplyMV(transformedPos, 0, resultMatrix, 0, vertexPos, 0)
            
            // Store transformed position
            targetVertexBuffer.put(i * 6 + 0, transformedPos[0])
            targetVertexBuffer.put(i * 6 + 1, transformedPos[1])
            targetVertexBuffer.put(i * 6 + 2, transformedPos[2])
        }
    }

    /**
     * Get morph index by visual parameter ID
     * Matches LLPolyMeshSharedData morph lookup
     */
    fun getMorphIndex(paramID: SLVisualParamID): Int {
        return morphIndices[paramID] ?: -1
    }

    /**
     * Get number of morph targets
     */
    fun getNumMorphs(): Int {
        return morphs.size
    }
}
