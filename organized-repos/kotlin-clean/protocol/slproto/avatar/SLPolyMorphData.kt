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

/**
 * SLPolyMorphData - Morph target data for avatar customization
 * Handles visual parameter morphs (body shape, face shape, etc.)
 * Based on Firestorm's LLPolyMorphData implementation
 */
class SLPolyMorphData {
    
    companion object {
        // Empty placeholder for missing morph data
        val EMPTY = SLPolyMorphData()
    }
    
    private var indexBuffer: DirectByteBuffer? = null
    private var isMasked: Boolean = false
    private var mesh: SLPolyMesh? = null
    private var morphID: SLVisualParamID? = null
    private var numVertices: Int = 0
    private var texCoordsBuffer: DirectByteBuffer? = null
    private var vertexBuffer: DirectByteBuffer? = null

    /**
     * Empty constructor for EMPTY placeholder
     */
    private constructor() {
        // Empty morph
    }

    /**
     * Load morph data from stream
     */
    @Throws(IOException::class)
    constructor(paramID: SLVisualParamID, parentMesh: SLPolyMesh, dataInputStream: DataInputStream) {
        this.morphID = paramID
        this.mesh = parentMesh
        
        // Read mask flag (whether morph is masked by alpha channel)
        this.isMasked = dataInputStream.readByte() != 0.toByte()
        
        // Read number of affected vertices
        this.numVertices = dataInputStream.readInt()
        
        // Allocate buffers
        this.vertexBuffer = DirectByteBuffer(this.numVertices * 24)  // 6 floats per vertex
        this.texCoordsBuffer = DirectByteBuffer(this.numVertices * 8)  // 2 floats per vertex
        this.indexBuffer = DirectByteBuffer(this.numVertices * 4)  // 1 int per vertex (index into main mesh)
        
        // Read morph data
        this.vertexBuffer!!.read(dataInputStream)
        this.texCoordsBuffer!!.read(dataInputStream)
        this.indexBuffer!!.read(dataInputStream)
        
        Debug.Log("SLPolyMorphData: Loaded morph '$paramID', vertices = $numVertices")
    }

    /**
     * Apply morph to mesh with given weight (fast native version)
     * @param targetMesh The mesh to morph
     * @param weight Morph strength (0.0 to 1.0)
     * @param alphaMask Optional texture for masked morphs
     */
    fun applyMorphData(targetMesh: SLMeshData, weight: Float, alphaMask: GLTexture?) {
        if (vertexBuffer == null || indexBuffer == null || targetMesh.vertexBuffer == null) {
            return
        }
        
        var maskBuffer: ByteBuffer? = null
        var maskWidth = 0
        var maskHeight = 0
        var maskOffset = 0
        
        // Handle masked morphs (e.g., makeup that only applies to face texture regions)
        if (isMasked && alphaMask != null) {
            maskWidth = alphaMask.width
            maskHeight = alphaMask.height
            maskBuffer = alphaMask.extraComponentsBuffer
            if (maskBuffer != null) {
                maskOffset = maskBuffer.position()
            }
        }
        
        // Use native OpenJPEG function for performance
        OpenJPEG.applyMeshMorph(
            weight,
            targetMesh.vertexBuffer!!.asByteBuffer(),
            targetMesh.texCoordsBuffer!!.asByteBuffer(),
            numVertices,
            indexBuffer!!.asByteBuffer(),
            vertexBuffer!!.asByteBuffer(),
            texCoordsBuffer!!.asByteBuffer(),
            maskWidth,
            maskHeight,
            maskOffset,
            maskBuffer
        )
    }

    /**
     * Slow Kotlin version of morph application (for debugging/testing)
     * Matches LLPolyMorphTarget::apply() logic
     */
    fun applyMorphDataSlow(targetMesh: SLMeshData, weight: Float, alphaMask: GLTexture?) {
        if (vertexBuffer == null || indexBuffer == null ||
            targetMesh.vertexBuffer == null || targetMesh.texCoordsBuffer == null) {
            return
        }
        
        val morphVertices = vertexBuffer!!.asFloatBuffer()
        val morphTexCoords = texCoordsBuffer!!.asFloatBuffer()
        val morphIndices = indexBuffer!!.asIntBuffer()
        val targetVertices = targetMesh.vertexBuffer!!.asFloatBuffer()
        val targetTexCoords = targetMesh.texCoordsBuffer!!.asFloatBuffer()
        
        // Setup masking if needed
        var useMask = isMasked && alphaMask != null
        var maskWidth = 0
        var maskHeight = 0
        var maskBuffer: ByteBuffer? = null
        var maskOffset = 0
        
        if (useMask) {
            maskWidth = alphaMask!!.width
            maskHeight = alphaMask.height
            maskBuffer = alphaMask.extraComponentsBuffer
            if (maskBuffer != null) {
                maskOffset = maskBuffer.position()
            } else {
                useMask = false
            }
        }
        
        // Apply morph to each affected vertex
        for (i in 0 until numVertices) {
            val vertexIndex = morphIndices.get(i)
            
            // Calculate mask value if needed (sample alpha from texture coordinates)
            var effectiveWeight = weight
            if (useMask && maskBuffer != null) {
                val u = targetTexCoords.get(vertexIndex * 2 + 0)
                val v = targetTexCoords.get(vertexIndex * 2 + 1)
                
                val texX = Math.floor((u * maskWidth).toDouble()).toInt()
                val texY = Math.floor((v * maskHeight).toDouble()).toInt()
                val maskIndex = texY * maskWidth + texX + maskOffset
                
                if (maskIndex >= 0 && maskIndex < maskBuffer.limit()) {
                    val maskValue = (maskBuffer.get(maskIndex).toInt() and 0xFF).toFloat() / 255.0f
                    effectiveWeight = maskValue * weight
                }
            }
            
            // Apply morph to vertex position and normal (6 floats)
            for (component in 0 until 6) {
                val originalValue = targetVertices.get(vertexIndex * 6 + component)
                val morphDelta = morphVertices.get(i * 6 + component)
                targetVertices.put(vertexIndex * 6 + component, originalValue + morphDelta * effectiveWeight)
            }
            
            // Apply morph to texture coordinates (2 floats)
            for (component in 0 until 2) {
                val originalValue = targetTexCoords.get(vertexIndex * 2 + component)
                val morphDelta = morphTexCoords.get(i * 2 + component)
                targetTexCoords.put(vertexIndex * 2 + component, originalValue + morphDelta * effectiveWeight)
            }
        }
    }
}
