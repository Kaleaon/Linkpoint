package com.linkpoint.slproto.avatar

import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.rawbuffers.DirectByteBuffer

/**
 * SLMeshData - Base class for avatar mesh data
 * Contains vertex positions, normals, texture coordinates, and face indices
 * Based on Firestorm's mesh data structures
 */
open class SLMeshData {
    
    var indexBuffer: DirectByteBuffer? = null
    var numFaces: Int = 0
    var numVertices: Int = 0
    var position: LLVector3? = null
    var referenceData: SLPolyMesh? = null
    var rotation: LLQuaternion? = null
    var scale: LLVector3? = null
    var texCoordsBuffer: DirectByteBuffer? = null
    var vertexBuffer: DirectByteBuffer? = null

    /**
     * Default constructor for empty mesh
     */
    constructor() {
        // Empty mesh
    }

    /**
     * Construct from reference poly mesh
     */
    constructor(referenceMesh: SLPolyMesh) {
        this.referenceData = referenceMesh
        this.position = LLVector3(referenceMesh.position!!)
        this.scale = LLVector3(referenceMesh.scale!!)
        this.rotation = LLQuaternion(referenceMesh.rotation!!)
        this.numVertices = referenceMesh.numVertices
        this.vertexBuffer = DirectByteBuffer(referenceMesh.vertexBuffer!!)
        this.texCoordsBuffer = DirectByteBuffer(referenceMesh.texCoordsBuffer!!)
        this.numFaces = referenceMesh.numFaces
        this.indexBuffer = DirectByteBuffer(referenceMesh.indexBuffer!!)
    }

    /**
     * Initialize mesh data from reference mesh (copy vertex data)
     */
    fun initFromReference() {
        val refData = referenceData ?: return
        
        if (refData.vertexBuffer != null && vertexBuffer != null) {
            val capacity = refData.vertexBuffer!!.asByteBuffer().capacity()
            vertexBuffer!!.copyFrom(0, refData.vertexBuffer!!, 0, capacity)
        }
        
        if (refData.texCoordsBuffer != null && texCoordsBuffer != null) {
            val capacity = refData.texCoordsBuffer!!.asByteBuffer().capacity()
            texCoordsBuffer!!.copyFrom(0, refData.texCoordsBuffer!!, 0, capacity)
        }
        
        if (refData.indexBuffer != null && indexBuffer != null) {
            val capacity = refData.indexBuffer!!.asByteBuffer().capacity()
            indexBuffer!!.copyFrom(0, refData.indexBuffer!!, 0, capacity)
        }
    }
}
