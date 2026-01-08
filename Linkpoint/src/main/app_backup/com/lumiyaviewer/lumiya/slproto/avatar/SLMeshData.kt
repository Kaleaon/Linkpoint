package com.lumiyaviewer.lumiya.slproto.avatar

import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import com.lumiyaviewer.rawbuffers.DirectByteBuffer

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

    constructor()

    constructor(mesh: SLPolyMesh) {
        this.referenceData = mesh
        this.position = if (mesh.position != null) LLVector3(mesh.position!!) else LLVector3()
        this.scale = if (mesh.scale != null) LLVector3(mesh.scale!!) else LLVector3()
        this.rotation = if (mesh.rotation != null) LLQuaternion(mesh.rotation!!) else LLQuaternion()
        this.numVertices = mesh.numVertices
        
        if (mesh.vertexBuffer != null) {
             this.vertexBuffer = DirectByteBuffer(mesh.vertexBuffer!!)
        }
        if (mesh.texCoordsBuffer != null) {
             this.texCoordsBuffer = DirectByteBuffer(mesh.texCoordsBuffer!!)
        }
        
        this.numFaces = mesh.numFaces
        if (mesh.indexBuffer != null) {
             this.indexBuffer = DirectByteBuffer(mesh.indexBuffer!!)
        }
    }

    fun initFromReference() {
        if (referenceData == null) return
        
        vertexBuffer?.copyFrom(0, referenceData!!.vertexBuffer!!, 0, referenceData!!.vertexBuffer!!.asByteBuffer().capacity())
        texCoordsBuffer?.copyFrom(0, referenceData!!.texCoordsBuffer!!, 0, referenceData!!.texCoordsBuffer!!.asByteBuffer().capacity())
        indexBuffer?.copyFrom(0, referenceData!!.indexBuffer!!, 0, referenceData!!.indexBuffer!!.asByteBuffer().capacity())
    }
}
