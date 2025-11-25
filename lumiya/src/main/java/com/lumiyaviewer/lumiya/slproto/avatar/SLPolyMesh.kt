package com.lumiyaviewer.lumiya.slproto.avatar

import android.opengl.Matrix
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG
import com.lumiyaviewer.lumiya.render.GLTexture
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import com.lumiyaviewer.rawbuffers.DirectByteBuffer
import java.io.DataInputStream
import java.io.IOException
import java.util.EnumMap

open class SLPolyMesh : SLMeshData {
    @JvmField var hasWeights: Boolean = false
    @JvmField var jointMap: IntArray? = null
    private val morphIndices: MutableMap<SLVisualParamID, Int> = EnumMap(SLVisualParamID::class.java)
    private var morphs: Array<SLPolyMorphData?>? = null
    @JvmField var weightsBuffer: DirectByteBuffer? = null

    constructor(dis: DataInputStream, morphDis: DataInputStream?) : super() {
        this.position = LLVector3(dis.readFloat(), dis.readFloat(), dis.readFloat())
        this.scale = LLVector3(dis.readFloat(), dis.readFloat(), dis.readFloat())
        this.rotation = LLQuaternion(dis.readFloat(), dis.readFloat(), dis.readFloat(), dis.readFloat())
        this.hasWeights = dis.readByte().toInt() != 0
        this.numVertices = dis.readInt()
        
        this.vertexBuffer = DirectByteBuffer(this.numVertices * 24)
        this.texCoordsBuffer = DirectByteBuffer(this.numVertices * 8)
        this.vertexBuffer?.read(dis)
        this.texCoordsBuffer?.read(dis)
        
        if (this.hasWeights) {
            this.weightsBuffer = DirectByteBuffer(this.numVertices * 4)
            this.weightsBuffer?.read(dis)
        }
        
        this.numFaces = dis.readInt()
        this.indexBuffer = DirectByteBuffer(this.numFaces * 2 * 3)
        this.indexBuffer?.read(dis)
        
        val numMorphs = dis.readInt()
        this.morphs = arrayOfNulls(numMorphs)
        
        var currentDis = dis
        var morphCount = 0
        
        for (i in 0 until numMorphs) {
            if (morphCount >= 50 && morphDis != null) {
                morphCount = 0
                currentDis = morphDis
            }
            
            val paramIdVal = currentDis.readInt()
            // Assuming SLVisualParamID is an enum
            val values = SLVisualParamID.values()
            if (paramIdVal >= 0 && paramIdVal < values.size) {
                val paramID = values[paramIdVal]
                this.morphs!![i] = SLPolyMorphData(paramID, this, currentDis)
                this.morphIndices[paramID] = i
            }
            morphCount++
        }
        
        val jointMapSize = currentDis.readInt()
        this.jointMap = IntArray(jointMapSize)
        for (i in 0 until jointMapSize) {
            this.jointMap!![i] = currentDis.readInt()
        }
        
        Debug.Log("SLPolyMesh: Loaded, numVerts = $numVertices, faces = $numFaces, morphs = $numMorphs")
    }

    fun applyMorphData(meshData: SLMeshData?, params: FloatArray, texture: GLTexture?) {
        if (morphs != null) {
            for (i in params.indices) {
                if (i < morphs!!.size && morphs!![i] != null) {
                    morphs!![i]!!.applyMorphData(meshData, params[i], texture)
                }
            }
        }
    }

    // Stub methods that were problematic in decompilation
    fun applySkeleton(animatedMesh: SLAnimatedMeshData, skeleton: FloatArray) {
        // Stub implementation
    }
    
    fun getMorphIndex(paramID: SLVisualParamID): Int {
        return morphIndices[paramID] ?: -1
    }

    fun getNumMorphs(): Int {
        return morphs?.size ?: 0
    }
}
