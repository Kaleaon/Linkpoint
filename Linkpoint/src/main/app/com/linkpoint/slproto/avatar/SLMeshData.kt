package com.linkpoint.slproto.avatar

import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.rawbuffers.DirectByteBuffer

open class SLMeshData {
    var indexBuffer: DirectByteBuffer? = null
        protected set
    var numFaces: Int = 0
        protected set
    var numVertices: Int = 0
        protected set
    var position: LLVector3? = null
        protected set
    var referenceData: SLPolyMesh? = null
        protected set
    var rotation: LLQuaternion? = null
        protected set
    var scale: LLVector3? = null
        protected set
    var texCoordsBuffer: DirectByteBuffer? = null
        protected set
    var vertexBuffer: DirectByteBuffer? = null
        protected set

    constructor()

    constructor(sLPolyMesh: SLPolyMesh) {
        this.referenceData = sLPolyMesh
        this.position = sLPolyMesh.position?.let { LLVector3(it) }
        this.scale = sLPolyMesh.scale?.let { LLVector3(it) }
        this.rotation = sLPolyMesh.rotation?.let { LLQuaternion(it) }
        this.numVertices = sLPolyMesh.numVertices
        val vb = sLPolyMesh.vertexBuffer
        if (vb != null) {
            this.vertexBuffer = DirectByteBuffer(vb)
        }
        val tcb = sLPolyMesh.texCoordsBuffer
        if (tcb != null) {
            this.texCoordsBuffer = DirectByteBuffer(tcb)
        }
        this.numFaces = sLPolyMesh.numFaces
        val ib = sLPolyMesh.indexBuffer
        if (ib != null) {
            this.indexBuffer = DirectByteBuffer(ib)
        }
    }

    fun initFromReference() {
        val refData = this.referenceData ?: return
        val refVertBuf = refData.vertexBuffer
        val refTexCoordBuf = refData.texCoordsBuffer
        val refIdxBuf = refData.indexBuffer
        
        if (refVertBuf != null) {
            this.vertexBuffer?.copyFrom(0, refVertBuf, 0, refVertBuf.asByteBuffer().capacity())
        }
        if (refTexCoordBuf != null) {
            this.texCoordsBuffer?.copyFrom(0, refTexCoordBuf, 0, refTexCoordBuf.asByteBuffer().capacity())
        }
        if (refIdxBuf != null) {
            this.indexBuffer?.copyFrom(0, refIdxBuf, 0, refIdxBuf.asByteBuffer().capacity())
        }
    }
}
